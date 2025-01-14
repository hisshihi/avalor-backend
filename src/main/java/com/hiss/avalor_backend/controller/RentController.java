package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.RentEntity;
import com.hiss.avalor_backend.repo.RentRepository;
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
@RequestMapping("/api/rent")
@RequiredArgsConstructor
public class RentController {

    private final RentRepository rentRepository;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping
    public PagedModel<RentEntity> getAll(Pageable pageable) {
        Page<RentEntity> rentEntities = rentRepository.findAll(pageable);
        return new PagedModel<>(rentEntities);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/{id}")
    public RentEntity getOne(@PathVariable Long id) {
        Optional<RentEntity> rentEntityOptional = rentRepository.findById(id);
        return rentEntityOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public RentEntity create(@RequestBody RentEntity rentEntity) {
        return rentRepository.save(rentEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public RentEntity patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        RentEntity rentEntity = rentRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(rentEntity).readValue(patchNode);

        return rentRepository.save(rentEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public RentEntity delete(@PathVariable Long id) {
        RentEntity rentEntity = rentRepository.findById(id).orElse(null);
        if (rentEntity != null) {
            rentRepository.delete(rentEntity);
        }
        return rentEntity;
    }
}
