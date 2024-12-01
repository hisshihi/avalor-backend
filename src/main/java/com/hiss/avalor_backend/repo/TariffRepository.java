package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

    Tariff findByTransportType(String transportType);

}
