package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.config.jwtAuth.JwtTokenGenerator;
import com.hiss.avalor_backend.dto.AuthResponseDto;
import com.hiss.avalor_backend.dto.UserRegistrationDto;
import com.hiss.avalor_backend.entity.RefreshTokenEntity;
import com.hiss.avalor_backend.entity.TokenType;
import com.hiss.avalor_backend.entity.UserEntity;
import com.hiss.avalor_backend.entity.VerificationToken;
import com.hiss.avalor_backend.mapper.UserMapper;
import com.hiss.avalor_backend.repo.RefreshTokenRepo;
import com.hiss.avalor_backend.repo.UserRepo;
import com.hiss.avalor_backend.repo.VerificationTokenRepo;
import com.hiss.avalor_backend.service.AuthService;
import com.hiss.avalor_backend.service.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserMapper userMapper;
    private final VerificationTokenRepo verificationTokenRepo;
    private final EmailService emailService;

    private static Authentication createAuthenticationObject(UserEntity userInfoEntity) {
        // Extract user details from UserDetailsEntity
        String username = userInfoEntity.getUsername();
        String password = userInfoEntity.getPassword();
        String roles = userInfoEntity.getRoles();

        // Extract authorities from roles (comma-separated)
        String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
    }

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            var userEntity = userRepo.findByUsername(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND ");
                    });


            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            saveUserRefreshToken(userEntity, refreshToken);
            creatRefreshTokenCookie(response, refreshToken);

            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", userEntity.getUsername());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(60)
                    .userName(userEntity.getUsername())
                    .tokenType(TokenType.Bearer)
                    .build();


        } catch (Exception e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }

    @Override
    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {
        if (!authorizationHeader.startsWith(TokenType.Bearer.name())) {
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please verify your token type");
        }

        final String refreshToken = authorizationHeader.substring(7);

        //Find refreshToken from database and should not be revoked : Same thing can be done through filter.
        var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
                .filter(tokens -> !tokens.isRevoked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));

        UserEntity userInfoEntity = refreshTokenEntity.getUser();

        //Now create the Authentication object
        Authentication authentication = createAuthenticationObject(userInfoEntity);

        //Use the authentication object to generate new accessToken as the Authentication object that we will have may not contain correct role.
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .userName(userInfoEntity.getUsername())
                .tokenType(TokenType.Bearer)
                .build();
    }

    @Override
    public AuthResponseDto registerUser(UserRegistrationDto userRegistrationDto, HttpServletResponse response) {
        try {
            log.info("[AuthService:registerUser]User Registration Started with :::{}", userRegistrationDto);

            Optional<UserEntity> user = userRepo.findByUsername(userRegistrationDto.username());
            if (user.isPresent()) {
                throw new Exception("User Already Exist");
            }

            UserEntity userDetailsEntity = userMapper.convertToEntity(userRegistrationDto);
            userDetailsEntity.setRoles("ROLE_UNVERIFIED");
            userDetailsEntity.setEmailIsVerification(false);
            userDetailsEntity.setFullName(userRegistrationDto.fullName());
            userDetailsEntity.setPhoneNumber(userRegistrationDto.phoneNumber());
            UserEntity savedUserDetails = userRepo.save(userDetailsEntity);

            Authentication authentication = createAuthenticationObject(userDetailsEntity);

            // TODO: поменять токен на рандомные 4-6 цифр
            // Generate validate token
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(savedUserDetails);
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
            verificationTokenRepo.save(verificationToken);

            // Отправка письма
            String verificationLink = token;
            emailService.sendVerificationEmail(userRegistrationDto.username(), verificationLink);

            // Generate a JWT token
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            saveUserRefreshToken(userDetailsEntity, refreshToken);

            creatRefreshTokenCookie(response, refreshToken);

            log.info("[AuthService:registerUser] User:{} Successfully registered", savedUserDetails.getUsername());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5 * 60)
                    .userName(savedUserDetails.getUsername())
                    .tokenType(TokenType.Bearer)
                    .verificationLink(verificationLink)
                    .build();


        } catch (Exception e) {
            log.error("[AuthService:registerUser]Exception while registering the user due to :" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void saveUserRefreshToken(UserEntity userEntity, String refreshToken) {
        var refreshTokenEntity = RefreshTokenEntity.builder()
                .user(userEntity)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();
        refreshTokenRepo.save(refreshTokenEntity);
    }

    private Cookie creatRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60); // in seconds
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }

}
