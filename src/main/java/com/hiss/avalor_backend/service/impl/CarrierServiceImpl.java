package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.entity.Carrier;
import com.hiss.avalor_backend.repo.CarrierRepo;
import com.hiss.avalor_backend.service.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepo carrierRepo;

    @Override
    public Optional<Carrier> findById(Long id) {
        return carrierRepo.findById(id);
    }

}
