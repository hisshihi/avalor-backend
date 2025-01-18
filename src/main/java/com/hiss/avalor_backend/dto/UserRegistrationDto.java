package com.hiss.avalor_backend.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserRegistrationDto (
        @NotEmpty(message = "User Name must not be empty")
        String username,
//        String userMobileNo,
//        @NotEmpty(message = "User email must not be empty") //Neither null nor 0 size
//        @Email(message = "Invalid email format")
//        String userEmail,

        @NotEmpty(message = "User password must not be empty")
        String userPassword,
//        @NotEmpty(message = "User role must not be empty")
//        String userRole

        @NotEmpty(message = "Full name must not be empty")
        String fullName,
        @NotEmpty(message = "Phone number must not be empty")
        String phoneNumber
){ }
