package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarrierRepo extends JpaRepository<Carrier, Long> {

    Carrier findByName(String name);

    @Query("select c from Carrier c where c.active = true")
    List<Carrier> findAll();

}
