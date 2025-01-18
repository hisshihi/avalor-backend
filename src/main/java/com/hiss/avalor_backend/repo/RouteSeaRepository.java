package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RouteSea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteSeaRepository extends JpaRepository<RouteSea, Long> {
    List<RouteSea> findByCityTo(String city);

    RouteSea findByCityFrom(String city);

    List<RouteSea> findAllByCityFromContainingIgnoreCase(String city);

    List<RouteSea> findAllByCityToContainingIgnoreCase(String city);

    RouteSea findByPolAndPodAndEqptAndCarrier(String pol, String pod, String eqpt, String carrier);

//    Optional<RouteSea> findById(Long id);
}
