package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long>, PagingAndSortingRepository<Application, Long> {

    List<Application> findByCreatedById(Long id);

}
