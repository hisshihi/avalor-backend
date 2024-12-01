package com.hiss.avalor_backend.config.user;

import com.hiss.avalor_backend.entity.UserEntity;
import com.hiss.avalor_backend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class InitialUser implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("user@example.com");
        user.setPassword(passwordEncoder.encode("123"));
        user.setRoles("ROLE_USER");

//        UserEntity manager = new UserEntity();
//        manager.setUsername("manager@example.com");
//        manager.setPassword(passwordEncoder.encode("123"));
//        manager.setRoles(Role.ROLE_MANAGER.toString());

        UserEntity admin = new UserEntity();
        admin.setUsername("admin@example.com");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setRoles("ROLE_ADMIN");

        if (userRepo.findByUsername(user.getUsername()).isEmpty()) {
            userRepo.save(user);
        }
//        if (userRepository.findByUsername(manager.getUsername()).isEmpty()) {
//            userRepository.save(manager);
//        }
        if (userRepo.findByUsername(admin.getUsername()).isEmpty()) {
            userRepo.save(admin);
        }

        log.info("User and admin created");
    }
}
