package com.hiss.avalor_backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.service.CalculateTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateTransferServiceImpl implements CalculateTransferService {

    RestTemplate restTemplate = new RestTemplate();
    public static final String API_URL = "https://rest.tramis.ru/index.php?_url=/vedexx/";

    @Override
    public Map<String, Object> findTransfer(Long placeFrom, Long placeTo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
                {
                    "method": "getRateList",
                    "on_date": "2025-01-01T00:00:00.000Z",
                    "place_to": %s,
                    "place_from": %s,
                    "unit_code": "25",
                    "token": "null",
                    "client_id": "-99"
                }
                """.formatted(placeTo, placeFrom);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();

            // Десериализуем основной JSON-объект
            Map<String, Object> mainResponse = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
            });

            // Проверяем, есть ли поле "message" в ответе
            if (mainResponse.containsKey("message") && mainResponse.get("message") != null) {
                String message = (String) mainResponse.get("message");
                // Парсим строку с сообщением в список данных
                List<Map<String, Object>> messageData = objectMapper.readValue(message, new TypeReference<List<Map<String, Object>>>() {
                });

                // Добавляем разобранное поле обратно в результат
                mainResponse.put("message", messageData);
            } else {
                // Если "message" отсутствует или пустое, можно вернуть ошибку или дефолтное значение
                mainResponse.put("message", new ArrayList<>());
            }

            return mainResponse;

        } catch (Exception e) {
            // Обработка ошибок, например, при запросе или парсинге
            throw new IllegalArgumentException("Ошибка при поиске маршрута");
        }
    }
}
