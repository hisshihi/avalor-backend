package com.hiss.avalor_backend.repo;

import com.hiss.avalor_backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepo extends JpaRepository<Application, Long> {
}
