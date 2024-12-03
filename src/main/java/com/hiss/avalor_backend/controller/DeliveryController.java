package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.dto.RouteDto;
import com.hiss.avalor_backend.dto.RouteSegmentDto;
import com.hiss.avalor_backend.entity.RouteWithCost;
import com.hiss.avalor_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final RouteService routeService;

//    @PreAuthorize("permitAll()")
//    @GetMapping("/calculate")
//    public ResponseEntity<List<List<RouteWithCost>>> calculateRoutes(
//            @RequestParam String cityFrom,
//            @RequestParam String cityTo) {
//        List<List<RouteWithCost>> routes = routeService.calculateRoutes(cityFrom, cityTo);
//        return ResponseEntity.ok(routes);
//    }

    @GetMapping("/calculate")
    public ResponseEntity<List<RouteDto>> calculateRoutes(
            @RequestParam String cityFrom,
            @RequestParam String cityTo) {
        List<List<RouteWithCost>> routesWithCosts = routeService.calculateRoutes(cityFrom, cityTo);
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
                        .map(r -> new RouteSegmentDto(
                                r.getCityFrom(),
                                r.getCityTo(),
                                r.getCarrier().getName(),
                                r.getCarrier().getPrice(),
                                r.getTransportType(),
                                r.getContainerTypeSize(),
                                r.getFilo(),
                                r.getPol(),
                                r.getPod()))
                        .collect(Collectors.toList()),
                route.getTotalCost()
        );
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/calculate")
    public ResponseEntity<?> saveRoute(@RequestBody RouteWithCost routeWithCost) {
        log.info("Data: {}", routeWithCost);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
