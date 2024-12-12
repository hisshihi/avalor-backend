package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.service.CitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/city")
@RequiredArgsConstructor
public class CitiesController {

    private final CitiesService citiesService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(citiesService.findAll());
    }

}
