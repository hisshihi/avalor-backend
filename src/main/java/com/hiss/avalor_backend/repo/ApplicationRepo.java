package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long>, PagingAndSortingRepository<Application, Long> {

    Page<Application> findByCreatedById(Long id,
                                        Pageable pageable);

    Optional<Application> findByCreatedByIdAndId(Long id, Long id1);
}
