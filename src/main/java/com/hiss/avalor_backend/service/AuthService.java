package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.dto.AuthResponseDto;
import com.hiss.avalor_backend.dto.UserRegistrationDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response);

    Object getAccessTokenUsingRefreshToken(String authorizationHeader);

    AuthResponseDto registerUser(UserRegistrationDto userRegistrationDto, HttpServletResponse response);

}
