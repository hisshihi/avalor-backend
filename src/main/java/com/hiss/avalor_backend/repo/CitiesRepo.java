package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Cities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitiesRepo extends JpaRepository<Cities, Long> {

    boolean existsByCity(String city);


}
