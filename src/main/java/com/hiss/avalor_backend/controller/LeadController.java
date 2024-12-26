package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.util.AmoCRMLeadRequest;
import com.hiss.avalor_backend.util.AmoCrmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final AmoCrmService amoCrmService;

    /**
     * Эндпоинт для создания сделки.
     *
     * @param lead объект сделки.
     * @return Ответ от amoCRM.
     */
    @PostMapping
    public ResponseEntity<String> createLead(@RequestBody AmoCRMLeadRequest lead) {
        return amoCrmService.createLead(lead);
    }
}
