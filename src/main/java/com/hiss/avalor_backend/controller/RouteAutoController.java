package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.RouteAuto;
import com.hiss.avalor_backend.repo.RouteAutoRepository;
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

@RequestMapping("/api/route-auto")
@RestController
@RequiredArgsConstructor
public class RouteAutoController {

    private final RouteAutoRepository routeAutoRepository;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping
    public PagedModel<RouteAuto> getAll(Pageable pageable) {
        Page<RouteAuto> routeAutos = routeAutoRepository.findAll(pageable);
        return new PagedModel<>(routeAutos);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/{id}")
    public RouteAuto getOne(@PathVariable Integer id) {
        Optional<RouteAuto> routeAutoOptional = routeAutoRepository.findById(id);
        return routeAutoOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public RouteAuto create(@RequestBody RouteAuto routeAuto) {
        return routeAutoRepository.save(routeAuto);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public RouteAuto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        RouteAuto routeAuto = routeAutoRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(routeAuto).readValue(patchNode);

        return routeAutoRepository.save(routeAuto);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public RouteAuto delete(@PathVariable Long id) {
        RouteAuto routeAuto = routeAutoRepository.findById(id).orElse(null);
        if (routeAuto != null) {
            routeAutoRepository.delete(routeAuto);
        }
        return routeAuto;
    }

}
