package com.hiss.avalor_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/application")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @PostMapping
    public ResponseEntity<?> createApplication(
            @RequestParam String cityTo,
            @RequestParam String cityFrom,
            @RequestParam String carrierName,
            @RequestParam String transportType,
            Principal principal) {
        log.info("Data: {}, {}, {}, {}, {}", principal.getName(), cityTo, cityFrom, carrierName, transportType);
        return ResponseEntity.ok().build();
    }

}
