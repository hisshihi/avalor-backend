package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RouteRailway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRailwayRepository extends JpaRepository<RouteRailway, Integer> {
    Optional<RouteRailway> findById(Long id);
}
