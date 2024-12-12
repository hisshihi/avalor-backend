package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.Cities;

import java.util.List;

public interface CitiesService {
    List<Cities> findAll();

    void save(Cities cities);

    boolean existsByCity(String city);
}
