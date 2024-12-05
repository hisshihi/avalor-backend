package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.dto.SaveApplicationDto;
import com.hiss.avalor_backend.dto.UpdateApplicationDto;
import com.hiss.avalor_backend.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface ApplicationService {

    void saveApplication(SaveApplicationDto dto, Principal principal);

    Page<Application> findAllByUser(Principal principal, Pageable pageable);

    Page<Application> findAll(Pageable pageable);

    void updateApplication(Long id, UpdateApplicationDto updateApplicationDto);

    void updateApplicationWithUser(Long id, UpdateApplicationDto updateApplicationDto, Principal principal);

    void deleteApplicationWithUser(Long id, Principal principal);

    void deleteApplication(Long id);
}
