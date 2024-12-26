package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Carrier;
import com.hiss.avalor_backend.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepo extends JpaRepository<Route, Long> {
    List<Route> findByCarrierId(Long carrierId);

    void deleteByCarrierId(Long id);

    List<Route> findByCarrier(Carrier carrier);
}
