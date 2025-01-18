package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.entity.ScheduleEntity;
import com.hiss.avalor_backend.repo.ScheduleRepo;
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
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleRepo scheduleRepo;

    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public PagedModel<ScheduleEntity> getAll(Pageable pageable) {
        Page<ScheduleEntity> scheduleEntities = scheduleRepo.findAll(pageable);
        return new PagedModel<>(scheduleEntities);
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public ScheduleEntity getOne(@PathVariable Long id) {
        Optional<ScheduleEntity> scheduleEntityOptional = scheduleRepo.findById(id);
        return scheduleEntityOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/pol-pod-carrier")
    public List<ScheduleEntity> polPodCarrier(@RequestParam String pol, @RequestParam String pod, @RequestParam String carrier) {
        return scheduleRepo.findByPolAndPodAndCarrier(pol, pod, carrier);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping
    public ScheduleEntity create(@RequestBody ScheduleEntity scheduleEntity) {
        return scheduleRepo.save(scheduleEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PatchMapping("/{id}")
    public ScheduleEntity patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        ScheduleEntity scheduleEntity = scheduleRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(scheduleEntity).readValue(patchNode);

        return scheduleRepo.save(scheduleEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public ScheduleEntity delete(@PathVariable Long id) {
        ScheduleEntity scheduleEntity = scheduleRepo.findById(id).orElse(null);
        if (scheduleEntity != null) {
            scheduleRepo.delete(scheduleEntity);
        }
        return scheduleEntity;
    }

}
