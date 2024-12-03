package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepo extends JpaRepository<Carrier, Long> {

    Carrier findByName(String name);

}
