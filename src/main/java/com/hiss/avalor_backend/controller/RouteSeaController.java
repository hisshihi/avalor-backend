package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.RouteSea;
import com.hiss.avalor_backend.repo.RouteSeaRepository;
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

@RequestMapping("/api/route-sea")
@RestController
@RequiredArgsConstructor
public class RouteSeaController {

    private final RouteSeaRepository routeSeaRepository;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping
    public PagedModel<RouteSea> getAll(Pageable pageable) {
        Page<RouteSea> routeSeas = routeSeaRepository.findAll(pageable);
        return new PagedModel<>(routeSeas);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/{id}")
    public RouteSea getOne(@PathVariable Long id) {
        Optional<RouteSea> routeSeaOptional = routeSeaRepository.findById(id);
        return routeSeaOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public RouteSea create(@RequestBody RouteSea routeSea) {
        return routeSeaRepository.save(routeSea);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public RouteSea patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        RouteSea routeSea = routeSeaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(routeSea).readValue(patchNode);

        return routeSeaRepository.save(routeSea);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public RouteSea delete(@PathVariable Long id) {
        RouteSea routeSea = routeSeaRepository.findById(id).orElse(null);
        if (routeSea != null) {
            routeSeaRepository.delete(routeSea);
        }
        return routeSea;
    }

}
