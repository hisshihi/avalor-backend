package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.RouteRailway;
import com.hiss.avalor_backend.repo.RouteRailwayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/route-railway")
@RequiredArgsConstructor
public class RouteRailwayController {

    private final RouteRailwayRepository routeRailwayRepository;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping
    public PagedModel<RouteRailway> getAll(Pageable pageable) {
        Page<RouteRailway> routeRailways = routeRailwayRepository.findAll(pageable);
        return new PagedModel<>(routeRailways);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/{id}")
    public RouteRailway getOne(@PathVariable Integer id) {
        Optional<RouteRailway> routeRailwayOptional = routeRailwayRepository.findById(id);
        return routeRailwayOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public RouteRailway create(@RequestBody RouteRailway routeRailway) {
        return routeRailwayRepository.save(routeRailway);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public RouteRailway patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        RouteRailway routeRailway = routeRailwayRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(routeRailway).readValue(patchNode);

        return routeRailwayRepository.save(routeRailway);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public RouteRailway delete(@PathVariable Long id) {
        RouteRailway routeRailway = routeRailwayRepository.findById(id).orElse(null);
        if (routeRailway != null) {
            routeRailwayRepository.delete(routeRailway);
        }
        return routeRailway;
    }

}
