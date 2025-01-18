package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.AdditionalServiceAtThePortOfArrivalPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdditionalServiceAtThePortOfArrivalPortRepo extends JpaRepository<AdditionalServiceAtThePortOfArrivalPort, Integer> {
    List<AdditionalServiceAtThePortOfArrivalPort> findByPort(String port);

    Optional<AdditionalServiceAtThePortOfArrivalPort> findById(Long id);
}
