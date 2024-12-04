package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Override
    @CacheEvict(value = "userApplications", allEntries = true)
    public void refreshCacheApplicationUser() {
        log.info("Кэш для userApplications успешно очищен.");
    }
}
