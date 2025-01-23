package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.dto.UserResponseDto;
import com.hiss.avalor_backend.entity.UserEntity;
import com.hiss.avalor_backend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepo userRepo;

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping("/admin")
    public ResponseEntity<?> getAdminUser(@RequestParam String username) {
        Optional<UserEntity> findUser = userRepo.findByUsername(username);
        if (findUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(findUser);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public ResponseEntity<?> getUser(Principal principal) {
        Optional<UserEntity> findUser = userRepo.findByUsername(principal.getName());
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .username(findUser.get().getUsername())
                .fullName(findUser.get().getFullName())
                .phoneNumber(findUser.get().getPhoneNumber())
                .build();
        return ResponseEntity.ok(userResponseDto);
    }

}
