package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalServiceRepo extends JpaRepository<AdditionalService, Long> {
}
