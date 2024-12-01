package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.Tariff;

public interface TariffService {

    Tariff findByTransportType(String transportType);

    void refreshTariffsCache();

}
