package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(@NotEmpty(message = "User Name must not be empty") String username);
}
