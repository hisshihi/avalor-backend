package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.DollarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DollarRepo extends JpaRepository<DollarEntity, Long> {

    Optional<DollarEntity> findTopByOrderByIdDesc();
}
