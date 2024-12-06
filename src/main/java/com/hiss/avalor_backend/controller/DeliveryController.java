package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.dto.RouteDto;
import com.hiss.avalor_backend.dto.RouteSaveDto;
import com.hiss.avalor_backend.dto.RouteSegmentDto;
import com.hiss.avalor_backend.entity.Application;
import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.entity.RouteWithCost;
import com.hiss.avalor_backend.repo.RouteRepo;
import com.hiss.avalor_backend.service.CacheService;
import com.hiss.avalor_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final ObjectMapper objectMapper;

    private final RouteRepo routeRepo;

    private final RouteService routeService;

    private final CacheService cacheService;

    @PreAuthorize("permitAll()")
    @GetMapping("/calculate")
    public ResponseEntity<List<RouteDto>> calculateRoutes(
            @RequestParam String cityFrom,
            @RequestParam String cityTo,
            @RequestParam String time,
            @RequestParam String weight) {
        List<List<RouteWithCost>> routesWithCosts = routeService.calculateRoutes(cityFrom, cityTo, time, weight);
        List<RouteDto> routesDTO = routesWithCosts.stream()
                .map(this::convertToRouteDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(routesDTO);
    }

    private RouteDto convertToRouteDTO(List<RouteWithCost> routeWithCosts) {
        //Assuming only one RouteWithCost per List
        RouteWithCost route = routeWithCosts.get(0);
        return new RouteDto(
                route.getRoute().stream()
                        .map(r -> {
                            int price;
                            if ("COC".equals(r.getContainerTypeSize())) {
                                price = r.getCarrier().getPrice() + r.getCarrier().getContainerRentalPrice();
                            } else if ("SOC".equals(r.getContainerTypeSize())) {
                                price = r.getCarrier().getPrice();
                            } else {
                                price = 0;
                            }

                            return new RouteSegmentDto(
                                    r.getId(),
                                    r.getCityFrom(),
                                    r.getCityTo(),
                                    r.getCarrier().getName(),
                                    r.getCarrier().getPrice(),
                                    r.getCarrier().getPriceDollars(),
                                    r.getCarrier().getContainerRentalPrice(),
                                    r.getCarrier().getContainerRentalPriceDollars(),
                                    r.getValidTo(),
                                    r.getEqpt(),
                                    price,
                                    r.getTransportType(),
                                    r.getContainerTypeSize(),
                                    r.getFilo(),
                                    r.getPol(),
                                    r.getPod(),
                                    r.getArrangementForRailwayDays(),
                                    r.getTransitTimeByTrainDays(),
                                    r.getTotalWithoutMovementDays(),
                                    r.getTotalTravelDays(),
                                    r.getTotalTotalTimeDays()
                            );

                        })
                        .collect(Collectors.toList()),
                route.getTotalCost()
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/calculate")
    public ResponseEntity<?> saveRoute(@RequestBody RouteSaveDto routeSaveDto) {
        clearCache();
        routeService.create(routeSaveDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Вывод всех маршрутов для админа
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/all")
    public ResponseEntity<?> getAll(Pageable pageable, PagedResourcesAssembler<Route> assembler) {
        Page<Route> routes = routeRepo.findAll(pageable);

        PagedModel<EntityModel<Route>> pagedModel = assembler.toModel(routes);

        return ResponseEntity.ok(pagedModel);

    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public Route patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        clearCache();
        Route route = routeRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(route).readValue(patchNode);

        return routeRepo.save(route);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public Route delete(@PathVariable Long id) {
        clearCache();
        Route route = routeRepo.findById(id).orElse(null);
        if (route != null) {
            routeRepo.delete(route);
        }
        return route;
    }

    private void clearCache() {
        cacheService.refreshCacheRoute();
    }

}
