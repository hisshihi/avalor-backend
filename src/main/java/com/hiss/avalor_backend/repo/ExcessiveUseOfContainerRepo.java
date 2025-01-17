package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.ExcessiveUseOfContainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcessiveUseOfContainerRepo extends JpaRepository<ExcessiveUseOfContainerEntity, Long> {
    List<ExcessiveUseOfContainerEntity> findByCarrierName(String carrier);
}
