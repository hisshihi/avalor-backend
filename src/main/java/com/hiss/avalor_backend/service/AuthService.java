package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.dto.AuthResponseDto;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication);

}
