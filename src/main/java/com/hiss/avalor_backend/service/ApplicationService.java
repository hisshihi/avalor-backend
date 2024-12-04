package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.dto.SaveApplicationDto;
import com.hiss.avalor_backend.entity.Application;

import java.security.Principal;
import java.util.List;

public interface ApplicationService {

    void saveApplication(SaveApplicationDto dto, Principal principal);

    List<Application> findAllByUser(Principal principal);
}
