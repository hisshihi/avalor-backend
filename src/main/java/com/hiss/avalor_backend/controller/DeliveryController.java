package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.dto.RouteDto;
import com.hiss.avalor_backend.dto.RouteSaveDto;
import com.hiss.avalor_backend.dto.RouteSegmentDto;
import com.hiss.avalor_backend.entity.Application;
import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.entity.RouteWithCost;
import com.hiss.avalor_backend.repo.ApplicationRepo;
import com.hiss.avalor_backend.repo.RouteRepo;
import com.hiss.avalor_backend.service.CacheService;
import com.hiss.avalor_backend.service.RouteExcelParserService;
import com.hiss.avalor_backend.service.RouteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    private final ApplicationRepo applicationRepo;

    private final RouteExcelParserService routeExcelParserService;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/by-ids")
    public ResponseEntity<List<Route>> getMany(@RequestParam List<Long> ids) {
        List<Route> routes = routeService.findMany(ids);
        return ResponseEntity.ok(routes);
    }

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
                                    r.getArrivalDate(),
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
    @Transactional
    public ResponseEntity<?> delete(@PathVariable Long id) {
        clearCache();

        // Найти маршрут
        Route route = routeRepo.findById(id).orElse(null);
        if (route == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Route not found");
        }

        // Найти заявки, связанные с маршрутом
        List<Application> applications = applicationRepo.findByRoutesId(route.getId());

        // Удалить связь маршрута с заявками
        for (Application application : applications) {
            application.getRoutes().remove(route);
            applicationRepo.save(application); // Сохранить изменения
        }

        // Удалить маршрут
        routeRepo.delete(route);

        return ResponseEntity.ok("Route and related applications have been deleted");
    }

    // Сохранение маршрутов в формате xlsx
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/excel")
    public ResponseEntity<?> uploadRouteExcel(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) { // Create workbook here
            List<String> errors = new ArrayList<>();
            try {
                routeExcelParserService.saveRoutesFromExcel(workbook, errors); // Pass workbook and errors
                return ResponseEntity.ok("Routes uploaded successfully");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }

    private void clearCache() {
        cacheService.refreshCacheRoute();
    }

}
