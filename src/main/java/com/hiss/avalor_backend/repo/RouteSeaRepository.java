package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RouteSea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteSeaRepository extends JpaRepository<RouteSea, Integer> {
}
