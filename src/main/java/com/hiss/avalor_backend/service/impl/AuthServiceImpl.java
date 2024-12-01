package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.dto.AuthResponseDto;
import com.hiss.avalor_backend.entity.TokenType;
import com.hiss.avalor_backend.repo.UserRepo;
import com.hiss.avalor_backend.service.AuthService;
import com.hiss.avalor_backend.service.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication) {
        try {
            var userEntity = userRepo.findByUsername(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND ");
                    });


            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", userEntity.getUsername());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(15 * 60)
                    .userName(userEntity.getUsername())
                    .tokenType(TokenType.Bearer)
                    .build();


        } catch (Exception e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }

}
