package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByTokenAndUserId(String token, Long userId);

}
