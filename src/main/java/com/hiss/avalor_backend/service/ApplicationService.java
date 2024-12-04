package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.dto.SaveApplicationDto;
import com.hiss.avalor_backend.entity.Application;

import java.security.Principal;

public interface ApplicationService {

    Application saveApplication(SaveApplicationDto dto, Principal principal);

}
