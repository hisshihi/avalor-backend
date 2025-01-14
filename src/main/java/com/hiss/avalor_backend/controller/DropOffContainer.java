package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.DropOffEntity;
import com.hiss.avalor_backend.repo.DropOffRepository;
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
@RequestMapping("/api/drop-off")
@RequiredArgsConstructor
public class DropOffContainer {

    private final DropOffRepository dropOffRepository;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping
    public PagedModel<DropOffEntity> getAll(Pageable pageable) {
        Page<DropOffEntity> dropOffEntities = dropOffRepository.findAll(pageable);
        return new PagedModel<>(dropOffEntities);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/{id}")
    public DropOffEntity getOne(@PathVariable Long id) {
        Optional<DropOffEntity> dropOffEntityOptional = dropOffRepository.findById(id);
        return dropOffEntityOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public DropOffEntity create(@RequestBody DropOffEntity dropOffEntity) {
        return dropOffRepository.save(dropOffEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public DropOffEntity patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        DropOffEntity dropOffEntity = dropOffRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(dropOffEntity).readValue(patchNode);

        return dropOffRepository.save(dropOffEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public DropOffEntity delete(@PathVariable Long id) {
        DropOffEntity dropOffEntity = dropOffRepository.findById(id).orElse(null);
        if (dropOffEntity != null) {
            dropOffRepository.delete(dropOffEntity);
        }
        return dropOffEntity;
    }

}
