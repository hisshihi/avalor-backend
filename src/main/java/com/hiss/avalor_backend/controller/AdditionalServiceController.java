package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.AdditionalService;
import com.hiss.avalor_backend.repo.AdditionalServiceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/additional-service")
@RequiredArgsConstructor
public class AdditionalServiceController {

    private final AdditionalServiceRepo additionalServiceRepo;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public List<AdditionalService> getList() {
        return additionalServiceRepo.findAll();
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public AdditionalService create(@RequestBody AdditionalService additionalService) {
        return additionalServiceRepo.save(additionalService);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public AdditionalService patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        AdditionalService additionalService = additionalServiceRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(additionalService).readValue(patchNode);

        return additionalServiceRepo.save(additionalService);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public AdditionalService delete(@PathVariable Long id) {
        AdditionalService additionalService = additionalServiceRepo.findById(id).orElse(null);
        if (additionalService != null) {
            additionalServiceRepo.delete(additionalService);
        }
        return additionalService;
    }
}
