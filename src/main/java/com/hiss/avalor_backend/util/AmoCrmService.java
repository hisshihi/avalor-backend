package com.hiss.avalor_backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmoCrmService {

    private final RestTemplate restTemplate;
    private final String amocrmApiUrl = "https://avalog2024.amocrm.ru"; // Замените на ваш URL
    private final String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjM4YmUxZGIyNDJmOThiNjI0OGZmZDYxYjI3ZmNkMjFjNWNkNWY1YzdhODZiYjJjYjE1YzdmNzFkZThhM2EzNmIxYzZmZmZkMGI0ZmY5MTA1In0.eyJhdWQiOiI4ODliNDUzYi1mMzFkLTQ3NjAtYjAxOS0yNWExNTdlMDhhOGIiLCJqdGkiOiIzOGJlMWRiMjQyZjk4YjYyNDhmZmQ2MWIyN2ZjZDIxYzVjZDVmNWM3YTg2YmIyY2IxNWM3ZjcxZGU4YTNhMzZiMWM2ZmZmZDBiNGZmOTEwNSIsImlhdCI6MTczMzY3MzgxNCwibmJmIjoxNzMzNjczODE0LCJleHAiOjE4NTQ3NDg4MDAsInN1YiI6IjEwMjU1NjYyIiwiZ3JhbnRfdHlwZSI6IiIsImFjY291bnRfaWQiOjMxMzY4OTkwLCJiYXNlX2RvbWFpbiI6ImFtb2NybS5ydSIsInZlcnNpb24iOjIsInNjb3BlcyI6WyJjcm0iLCJmaWxlcyIsImZpbGVzX2RlbGV0ZSIsIm5vdGlmaWNhdGlvbnMiLCJwdXNoX25vdGlmaWNhdGlvbnMiXSwiaGFzaF91dWlkIjoiMWJiYWQ2MjAtMGM4My00MWZhLTliMjUtNzNjODIwY2FkN2UwIiwiYXBpX2RvbWFpbiI6ImFwaS1iLmFtb2NybS5ydSJ9.gQnsbrgzyfvzkYhCM5pkwsKGsVGpPLIIYonHEkxQujlACmTlnkkwffoAsypFygpFdeF_qmcp86eHTD-Lnil2AVZMVFGl1hgOvKwcpfWxYhU4C6FKC9kIlDaK72PH2O5ID9OQSA32OTLr7Su25YiGtAp4QeRf5aDFo0jUMPrauJwRr08CPI4NVXLPF0GIdcfiS7lXWAL7k2abeg3KfmKHCC8WC5w5FfiRQumlwc2spfJR1D6i2Y5V6PejZI-xU0WMvOknpzOiW_xjMxQ_2RaWOpaSvbo5hvYVAq4kTdGdKvtsq2O3R8ump6h1g6gQvPAqq4X4X8SNtDCAuwRLrzVrcQ";

    /**
     * Метод для создания сделки в amoCRM.
     *
     * @param lead объект сделки, содержащий необходимые данные.
     * @return Ответ от API amoCRM.
     */
    public ResponseEntity<String> createLead(AmoCRMLeadRequest lead) {
        // Установка заголовков запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        // Формируем тело запроса с необходимыми полями
        Map<String, Object> leadData = new HashMap<>();
        leadData.put("name", lead.getName()); // Название сделки
        leadData.put("price", lead.getPrice()); // Цена сделки

        // Исправляем добавление тегов
        if (lead.getTagsToAdd() != null && !lead.getTagsToAdd().isEmpty()) {
            List<Map<String, Object>> tags = lead.getTagsToAdd().stream()
                    .map(tag -> {
                        Map<String, Object> tagData = new HashMap<>();
                        if (tag.getId() != null) {
                            tagData.put("id", tag.getId());
                        }
                        if (tag.getName() != null) {
                            tagData.put("name", tag.getName());
                        }
                        return tagData;
                    })
                    .collect(Collectors.toList());
            leadData.put("tags", tags); // amoCRM ожидает ключ "tags"
        }

        // Преобразуем объект в JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest;
        try {
            jsonRequest = objectMapper.writeValueAsString(Collections.singletonList(leadData)); // amoCRM ожидает массив
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при преобразовании запроса в JSON", e);
        }

        // Создаем HTTP-запрос
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        // Отправляем запрос
        ResponseEntity<String> response = restTemplate.exchange(
                amocrmApiUrl + "/api/v4/leads", // URL
                HttpMethod.POST,               // Метод POST
                requestEntity,                 // Тело запроса
                String.class                   // Тип ответа
        );

        log.info("Response: {}", response.getBody());

        return response;

    }
}