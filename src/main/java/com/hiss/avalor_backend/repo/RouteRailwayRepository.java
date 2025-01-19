package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RouteRailway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRailwayRepository extends JpaRepository<RouteRailway, Long> {
    RouteRailway findByPolAndPodAndFilo20AndFilo20HCAndFilo40AndCarrierAndContainerTypeSizeAndValidTo(String pol, String pod, Integer filo20, Integer filo20HC, Integer filo40, String carrier, String containerTypeSize, String validTo);
//    Optional<RouteRailway> findById(Long id);
}
