package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.DropOffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DropOffRepository extends JpaRepository<DropOffEntity, Long> {

    List<DropOffEntity> findByPolAndPodAndSize(String pol, String pod, String size);

    List<DropOffEntity> findByPolAndPodAndSizeAndCarrier(String pol, String pod, String size, String carrier);

}
