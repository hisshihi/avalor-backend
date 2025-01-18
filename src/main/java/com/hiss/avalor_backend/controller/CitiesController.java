package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.entity.Cities;
import com.hiss.avalor_backend.repo.CitiesRepo;
import com.hiss.avalor_backend.service.CitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@RequiredArgsConstructor
public class CitiesController {

    private final CitiesService citiesService;

    private final CitiesRepo citiesRepo;

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(citiesService.findAll());
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public Cities delete(@PathVariable Long id) {
        Cities cities = citiesRepo.findById(id).orElse(null);
        if (cities != null) {
            citiesRepo.delete(cities);
        }
        return cities;
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        citiesRepo.deleteAllById(ids);
    }

}
