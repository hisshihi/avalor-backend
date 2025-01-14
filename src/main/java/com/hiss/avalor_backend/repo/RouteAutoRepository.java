package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RouteAuto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteAutoRepository extends JpaRepository<RouteAuto, Integer> {
}
