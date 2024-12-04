package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.ExcessiveUseOfContainerEntity;
import com.hiss.avalor_backend.repo.ExcessiveUseOfContainerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/excessive-use-of-container-entity")
@RequiredArgsConstructor
public class ExcessiveUseOfContainerController {

    private final ExcessiveUseOfContainerRepo excessiveUseOfContainerRepo;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public PagedModel<ExcessiveUseOfContainerEntity> getList(Pageable pageable) {
        Page<ExcessiveUseOfContainerEntity> excessiveUseOfContainerEntities = excessiveUseOfContainerRepo.findAll(pageable);
        return new PagedModel<>(excessiveUseOfContainerEntities);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public ExcessiveUseOfContainerEntity create(@RequestBody ExcessiveUseOfContainerEntity excessiveUseOfContainerEntity) {
        return excessiveUseOfContainerRepo.save(excessiveUseOfContainerEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public ExcessiveUseOfContainerEntity patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        ExcessiveUseOfContainerEntity excessiveUseOfContainerEntity = excessiveUseOfContainerRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(excessiveUseOfContainerEntity).readValue(patchNode);

        return excessiveUseOfContainerRepo.save(excessiveUseOfContainerEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @DeleteMapping("/{id}")
    public ExcessiveUseOfContainerEntity delete(@PathVariable Long id) {
        ExcessiveUseOfContainerEntity excessiveUseOfContainerEntity = excessiveUseOfContainerRepo.findById(id).orElse(null);
        if (excessiveUseOfContainerEntity != null) {
            excessiveUseOfContainerRepo.delete(excessiveUseOfContainerEntity);
        }
        return excessiveUseOfContainerEntity;
    }
}
