package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.repo.AdditionalServiceRepo;
import com.hiss.avalor_backend.service.AdditionalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    private final AdditionalServiceRepo additionalServiceRepo;

}
