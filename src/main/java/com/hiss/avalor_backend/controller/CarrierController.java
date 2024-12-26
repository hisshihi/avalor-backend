package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.Carrier;
import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.repo.CarrierRepo;
import com.hiss.avalor_backend.repo.RouteRepo;
import com.hiss.avalor_backend.service.CacheService;
import com.hiss.avalor_backend.service.CarrierService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/carrier")
@RequiredArgsConstructor
public class CarrierController {

    private final CarrierRepo carrierRepo;

    private final ObjectMapper objectMapper;

    private final CacheService cacheService;

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public Carrier create(@RequestBody Carrier carrier) {
        clearCache();
        carrier.setActive(true);
        return carrierRepo.save(carrier);
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public List<Carrier> getList() {
        return carrierRepo.findAll();
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public Carrier patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        clearCache();
        Carrier carrier = carrierRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(carrier).readValue(patchNode);

        return carrierRepo.save(carrier);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clearCache();
        Carrier carrier = carrierRepo.findById(id).orElse(null);
        if (carrier != null && carrier.isActive()) { // Проверяем активность
            carrier.setActive(false); // Деактивируем перевозчика
            carrierRepo.save(carrier); // Сохраняем изменения
        }
    }

    private void clearCache() {
        cacheService.refreshCacheRoute();
    }

}
