package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.entity.Tariff;
import com.hiss.avalor_backend.repo.TariffRepository;
import com.hiss.avalor_backend.service.TariffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;

    @Override
    @Cacheable(value = "tariffs", key = "#transportType")
    public Tariff findByTransportType(String transportType) {
        log.info("Loading tariff for transport type: {}", transportType);
        return tariffRepository.findByTransportType(transportType);
    }

    @Override
    @CacheEvict(value = "tariffs", allEntries = true)
    public void refreshTariffsCache() {
        log.info("Tariffs cache has been refreshed");
    }

}
