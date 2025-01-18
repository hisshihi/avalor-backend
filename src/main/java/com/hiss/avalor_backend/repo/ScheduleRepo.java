package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepo extends JpaRepository<ScheduleEntity, Long> {
    List<ScheduleEntity> findByPolAndPodAndCarrier(String pol, String pod, String carrier);
}
