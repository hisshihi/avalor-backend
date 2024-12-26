package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Application;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long>, PagingAndSortingRepository<Application, Long> {

    Page<Application> findByCreatedById(Long id,
                                        Pageable pageable);

    Optional<Application> findByCreatedByIdAndId(Long id, Long id1);

    @Query("SELECT a FROM Application a JOIN a.routes r WHERE r.id = :routeId")
    List<Application> findByRoutesId(@Param("routeId") Long routeId);

}
