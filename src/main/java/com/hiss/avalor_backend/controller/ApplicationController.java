package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.dto.SaveApplicationDto;
import com.hiss.avalor_backend.dto.UpdateApplicationDto;
import com.hiss.avalor_backend.entity.Application;
import com.hiss.avalor_backend.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/application")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveApplication(
            @ModelAttribute @Valid SaveApplicationDto saveApplicationDto,
            Principal principal
    ) {
        applicationService.saveApplication(saveApplicationDto, principal);
        return ResponseEntity.ok("Application saved successfully");
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public ResponseEntity<?> findUserApplications(
            Principal principal, Pageable pageable, PagedResourcesAssembler<Application> assembler) {
        Page<Application> applications = applicationService.findAllByUser(principal, pageable);

        // Преобразуем Page<Application> в PagedModel
        PagedModel<EntityModel<Application>> pagedModel = assembler.toModel(applications);

        return ResponseEntity.ok(pagedModel);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/admin")
    public ResponseEntity<?> findAdminApplications(Pageable pageable, PagedResourcesAssembler<Application> assembler) {
        Page<Application> applications = applicationService.findAll(pageable);

        PagedModel<EntityModel<Application>> pagedModel = assembler.toModel(applications);

        return ResponseEntity.ok(pagedModel);
    }

    // TODO: Проверить, правильно ли работает обновление свзянных данных
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<?> updateApplication(
            @PathVariable Long id,
            @ModelAttribute UpdateApplicationDto updateApplicationDto
    ) {
        applicationService.updateApplication(id, updateApplicationDto);
        return ResponseEntity.ok("Application updated successfully");
    }

    // Обновление для пользователя
    @PatchMapping("/user/{id}")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<?> updateApplicationUser(
            @PathVariable Long id,
            @ModelAttribute UpdateApplicationDto updateApplicationDto,
            Principal principal
    ) {
        applicationService.updateApplicationWithUser(id, updateApplicationDto, principal);
        return ResponseEntity.ok("Application updated successfully");
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<?> deleteApplicationUser(
            @PathVariable Long id,
            Principal principal
    ) {
        applicationService.deleteApplicationWithUser(id, principal);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }


}
