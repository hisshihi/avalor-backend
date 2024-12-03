package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final RouteService routeService;

    @GetMapping("/calculate")
    public ResponseEntity<List<List<Route>>> calculateRoutes(
            @RequestParam String cityFrom,
            @RequestParam String cityTo) {
        List<List<Route>> routes = routeService.calculateRoutes(cityFrom, cityTo);
        return ResponseEntity.ok(routes);
    }

}
