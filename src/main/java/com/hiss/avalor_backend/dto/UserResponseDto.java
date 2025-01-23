package com.hiss.avalor_backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {

    String username;
    String fullName;
    String phoneNumber;

}
