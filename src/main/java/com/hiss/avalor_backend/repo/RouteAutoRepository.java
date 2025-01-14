package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RouteAuto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteAutoRepository extends JpaRepository<RouteAuto, Integer> {
    Optional<RouteAuto> findById(Long id);
}
