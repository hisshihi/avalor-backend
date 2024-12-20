package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.Carrier;

import java.util.Optional;

public interface CarrierService {

    Optional<Carrier> findById(Long id);

    Optional<Carrier> findByName(String carrierName);
}
