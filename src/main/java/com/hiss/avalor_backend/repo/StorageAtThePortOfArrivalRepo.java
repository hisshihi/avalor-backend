package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.StorageAtThePortOfArrivalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageAtThePortOfArrivalRepo extends JpaRepository<StorageAtThePortOfArrivalEntity, Long> {

    Optional<StorageAtThePortOfArrivalEntity> findByName(String name);

}
