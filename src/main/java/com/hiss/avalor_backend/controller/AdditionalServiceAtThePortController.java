package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.AdditionalServiceAtThePortOfArrivalPort;
import com.hiss.avalor_backend.repo.AdditionalServiceAtThePortOfArrivalPortRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/additional-service-at-the-port")
@RequiredArgsConstructor
public class AdditionalServiceAtThePortController {

    private final AdditionalServiceAtThePortOfArrivalPortRepo additionalServiceAtThePortOfArrivalPortRepo;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public PagedModel<AdditionalServiceAtThePortOfArrivalPort> getAll(Pageable pageable) {
        Page<AdditionalServiceAtThePortOfArrivalPort> additionalServiceAtThePortOfArrivalPorts = additionalServiceAtThePortOfArrivalPortRepo.findAll(pageable);
        return new PagedModel<>(additionalServiceAtThePortOfArrivalPorts);
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public AdditionalServiceAtThePortOfArrivalPort getOne(@PathVariable Integer id) {
        Optional<AdditionalServiceAtThePortOfArrivalPort> additionalServiceAtThePortOfArrivalPortOptional = additionalServiceAtThePortOfArrivalPortRepo.findById(id);
        return additionalServiceAtThePortOfArrivalPortOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/port")
    public List<AdditionalServiceAtThePortOfArrivalPort> getPort(@RequestParam String port) {
        return additionalServiceAtThePortOfArrivalPortRepo.findByPort(port);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public AdditionalServiceAtThePortOfArrivalPort create(@RequestBody AdditionalServiceAtThePortOfArrivalPort additionalServiceAtThePortOfArrivalPort) {
        return additionalServiceAtThePortOfArrivalPortRepo.save(additionalServiceAtThePortOfArrivalPort);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public AdditionalServiceAtThePortOfArrivalPort patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        AdditionalServiceAtThePortOfArrivalPort additionalServiceAtThePortOfArrivalPort = additionalServiceAtThePortOfArrivalPortRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(additionalServiceAtThePortOfArrivalPort).readValue(patchNode);

        return additionalServiceAtThePortOfArrivalPortRepo.save(additionalServiceAtThePortOfArrivalPort);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public AdditionalServiceAtThePortOfArrivalPort delete(@PathVariable Long id) {
        AdditionalServiceAtThePortOfArrivalPort additionalServiceAtThePortOfArrivalPort = additionalServiceAtThePortOfArrivalPortRepo.findById(id).orElse(null);
        if (additionalServiceAtThePortOfArrivalPort != null) {
            additionalServiceAtThePortOfArrivalPortRepo.delete(additionalServiceAtThePortOfArrivalPort);
        }
        return additionalServiceAtThePortOfArrivalPort;
    }
}
