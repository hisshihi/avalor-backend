package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.util.AmoCRMLead;
import com.hiss.avalor_backend.util.AmoCrmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final AmoCrmService amoCrmService;

    @PostMapping
    public ResponseEntity<?> createLead(@RequestBody AmoCRMLead amoCRMLead) {
        // Пример заполнения дополнительных полей:
        amoCRMLead.setResponsibleUserId(12345); // ID ответственного пользователя
        amoCRMLead.setStatusId(67890);          // ID статуса сделки
        amoCRMLead.setPipelineId(13579);        // ID воронки


        // Пример добавления пользовательского поля:
        AmoCRMLead.CustomField customField = new AmoCRMLead.CustomField();
        customField.setFieldId(112233L); // Замените на ID вашего поля

        AmoCRMLead.CustomField.Value value1 = new AmoCRMLead.CustomField.Value();
        value1.setValue("Значение 1");

        customField.setValues(List.of(value1));

        amoCRMLead.setCustomFieldsValues(List.of(customField));

        return amoCrmService.createLead(amoCRMLead);
    }

}
