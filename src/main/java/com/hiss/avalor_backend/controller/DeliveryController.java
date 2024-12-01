package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.dto.DeliveryOptionDto;
import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("permitAll()")
    @GetMapping("/calculate")
    public ResponseEntity<?> calculateDelivery(@RequestParam String from,
                                               @RequestParam String to) {
        log.info("Calculating delivery from {} to {}", from, to);
        List<Route> path = routeService.findShortestPath(from, to);
        log.info("Calculated path: {}", path);

        double cost = routeService.calculateCost(path);
        log.info("Total cost: {}", cost);

        int deliveryDays = path.size() * 2; // Условная логика: каждый маршрут = 2 дня
        log.info("Estimated delivery days: {}", deliveryDays);

        return new ResponseEntity<>(new DeliveryOptionDto(path.toString(), cost, deliveryDays), HttpStatus.OK);
    }

}
