package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RouteSea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteSeaRepository extends JpaRepository<RouteSea, Integer> {
    RouteSea findByCityTo(String city);

    RouteSea findByCityFrom(String city);

    List<RouteSea> findAllByCityFromContainingIgnoreCase(String city);

    List<RouteSea> findAllByCityToContainingIgnoreCase(String city);

    Optional<RouteSea> findById(Long id);
}
