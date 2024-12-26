package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.entity.Cities;
import com.hiss.avalor_backend.repo.CitiesRepo;
import com.hiss.avalor_backend.service.CitiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitiesServiceImpl implements CitiesService {

    private final CitiesRepo citiesRepo;

    @Override
    public List<Cities> findAll() {
        return citiesRepo.findAll();
    }

    @Override
    public void save(Cities cities) {
        citiesRepo.save(cities);
        log.info("Cities saved: {}", cities);
    }

    @Override
    public boolean existsByCity(String city) {
        return citiesRepo.existsByCity(city);
    }

}
