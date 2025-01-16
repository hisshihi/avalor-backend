package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.RouteAuto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteAutoRepository extends JpaRepository<RouteAuto, Long> {
//    Optional<RouteAuto> findById(Long id);

    RouteAuto findByPolAndPodAndFilo20AndFilo20HCAndFilo40(String pol, String pod, Integer filo20, Integer filo20HC, Integer filo40);
}
