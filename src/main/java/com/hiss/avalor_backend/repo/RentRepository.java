package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentRepository extends JpaRepository<RentEntity, Long> {

    RentEntity findByPolAndPod(String pol, String pod);

}
