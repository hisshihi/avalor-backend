package com.hiss.avalor_backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.service.CityToAndFromService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CityToAndFromServiceImpl implements CityToAndFromService {

    public static final String API_URL = "https://rest.tramis.ru/index.php?_url=/vedexx/";
    RestTemplate restTemplate = new RestTemplate();

    @Override
    @SneakyThrows
    public Map<String, Object> findCityTo() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
            {
                "method": "getReferenceData",
                "reference_name": "lgst_place_to_wg"
            }
            """;

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        // Десериализуем основной JSON-объект
        Map<String, Object> mainResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

        // Извлекаем и парсим поле "message"
        String message = (String) mainResponse.get("message");
        List<Map<String, Object>> messageData = objectMapper.readValue(message, new TypeReference<>() {});

        // Добавляем разобранное поле обратно в результат
        mainResponse.put("message", messageData);

        return mainResponse;
    }

    @Override
    @SneakyThrows
    public Map<String, Object> findCityFrom() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
            {
                "method": "getReferenceData",
                "reference_name": "lgst_place_from_wg"
            }
            """;

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        // Десериализуем основной JSON-объект
        Map<String, Object> mainResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

        // Извлекаем и парсим поле "message"
        String message = (String) mainResponse.get("message");
        List<Map<String, Object>> messageData = objectMapper.readValue(message, new TypeReference<>() {});

        // Добавляем разобранное поле обратно в результат
        mainResponse.put("message", messageData);

        return mainResponse;
    }

}
