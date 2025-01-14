package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.DropOffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DropOffRepository extends JpaRepository<DropOffEntity, Long> {

    DropOffEntity findByPolAndPodAndSize(String pol, String pod, String size);

}
