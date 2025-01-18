package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.StorageAtThePortOfArrivalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageAtThePortOfArrivalRepo extends JpaRepository<StorageAtThePortOfArrivalEntity, Long> {

    List<StorageAtThePortOfArrivalEntity> findByPort(String port);

}
