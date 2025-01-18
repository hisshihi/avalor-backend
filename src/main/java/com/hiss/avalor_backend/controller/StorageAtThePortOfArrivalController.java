package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.StorageAtThePortOfArrivalEntity;
import com.hiss.avalor_backend.repo.StorageAtThePortOfArrivalRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/storage-at-the-port-of-arrival")
@RequiredArgsConstructor
public class StorageAtThePortOfArrivalController {

    private final StorageAtThePortOfArrivalRepo storageAtThePortOfArrivalRepo;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public List<StorageAtThePortOfArrivalEntity> getList() {
        return storageAtThePortOfArrivalRepo.findAll();
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/port")
    public List<StorageAtThePortOfArrivalEntity> getPort(@RequestParam String port) {
        return storageAtThePortOfArrivalRepo.findByPort(port);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public StorageAtThePortOfArrivalEntity create(@RequestBody StorageAtThePortOfArrivalEntity storageAtThePortOfArrivalEntity) {
        return storageAtThePortOfArrivalRepo.save(storageAtThePortOfArrivalEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public StorageAtThePortOfArrivalEntity patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        StorageAtThePortOfArrivalEntity storageAtThePortOfArrivalEntity = storageAtThePortOfArrivalRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(storageAtThePortOfArrivalEntity).readValue(patchNode);

        return storageAtThePortOfArrivalRepo.save(storageAtThePortOfArrivalEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public StorageAtThePortOfArrivalEntity delete(@PathVariable Long id) {
        StorageAtThePortOfArrivalEntity storageAtThePortOfArrivalEntity = storageAtThePortOfArrivalRepo.findById(id).orElse(null);
        if (storageAtThePortOfArrivalEntity != null) {
            storageAtThePortOfArrivalRepo.delete(storageAtThePortOfArrivalEntity);
        }
        return storageAtThePortOfArrivalEntity;
    }

}
