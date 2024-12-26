package com.hiss.avalor_backend.mapper;

import com.hiss.avalor_backend.dto.UserRegistrationDto;
import com.hiss.avalor_backend.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    public UserEntity convertToEntity(UserRegistrationDto userRegistrationDto) {
        UserEntity userInfoEntity = new UserEntity();
        userInfoEntity.setUsername(userRegistrationDto.username());
//        userInfoEntity.setEmailId(userRegistrationDto.userEmail());
//        userInfoEntity.setMobileNumber(userRegistrationDto.userMobileNo());
        userInfoEntity.setRoles(userRegistrationDto.userRole());
        userInfoEntity.setPassword(passwordEncoder.encode(userRegistrationDto.userPassword()));
        return userInfoEntity;
    }

}
