package com.hiss.avalor_backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmoCrmService {

    private final RestTemplate restTemplate;

    private String amocrmApiUrl = "https://avalog2024.amocrm.ru";
    private String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjlkOGY4OTk2NGRkZWUzNzY1ODhmMGVkNmZiN2M1NmYyNGVhODU1OGUwYjQ3MjA0OGI2Y2M3NzY2ZmUwYmE2ZGEwMmU5MWQ0NTI2MGVkZTQ1In0.eyJhdWQiOiI4ODliNDUzYi1mMzFkLTQ3NjAtYjAxOS0yNWExNTdlMDhhOGIiLCJqdGkiOiI5ZDhmODk5NjRkZGVlMzc2NTg4ZjBlZDZmYjdjNTZmMjRlYTg1NThlMGI0NzIwNDhiNmNjNzc2NmZlMGJhNmRhMDJlOTFkNDUyNjBlZGU0NSIsImlhdCI6MTczMzU5NTE4NywibmJmIjoxNzMzNTk1MTg3LCJleHAiOjE4NTUwMDgwMDAsInN1YiI6IjEwMjU1NjYyIiwiZ3JhbnRfdHlwZSI6IiIsImFjY291bnRfaWQiOjMxMzY4OTkwLCJiYXNlX2RvbWFpbiI6ImFtb2NybS5ydSIsInZlcnNpb24iOjIsInNjb3BlcyI6WyJjcm0iLCJmaWxlcyIsImZpbGVzX2RlbGV0ZSIsIm5vdGlmaWNhdGlvbnMiLCJwdXNoX25vdGlmaWNhdGlvbnMiXSwiaGFzaF91dWlkIjoiMzQ5ODg5MWItZTdlOC00YzE4LTk5NGQtZTM4ZGUyNDhhNjg4IiwiYXBpX2RvbWFpbiI6ImFwaS1iLmFtb2NybS5ydSJ9.LegP6vbd12LdupQ19B5IX7S4EkwR3SPSi_eAxd6Qi8T5FZ0LFCwdjnqf4tD2zf83TgYQOah4cYMyNw780mBjMfeWK-2jvph2bNTeATm5DrFO4ousH4-m7RTY3HBwg8GeyOZSGzis7fAZkNhwz8pdCqrkD5Aom2PgtSEFwyBKn264a-F0HzT0MwOV4p87ImiNK6Ftl_dXmIwr1wGQyyXOG7_a0O7C-cYpbs8UVre1f1_BKK1sZRk6rxGM5u7D3FPoBYFx96T0gNG6zD2XCTdtMWFc0AwQ9GDJW6EdHCmCgHU8FFXShkJYn7MG63CQQaJYz0wwXUKg7TSADL4a7IP98Q";

    public ResponseEntity<String> createLead(AmoCRMLead lead) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + accessToken);

        // Создаем список для отправки нескольких сделок, если нужно
        List<AmoCRMLead> leadsList = Collections.singletonList(lead);

        // Создаем тело запроса с _embedded.leads
        AmoCRMLeadRequest requestBody = new AmoCRMLeadRequest();
        requestBody.setLeads(leadsList);
        HttpEntity<AmoCRMLeadRequest> requestEntity = new HttpEntity<>(requestBody, headers);


        String addLeadUrl = amocrmApiUrl + "/api/v4/leads";  // Исправленный URL

        ResponseEntity<String> response = restTemplate.exchange(
                addLeadUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return response;
    }

    private static class AmoCRMLeadRequest {
        private List<AmoCRMLead> leads;

        // Геттеры и сеттеры
        public List<AmoCRMLead> getLeads() {
            return leads;
        }

        public void setLeads(List<AmoCRMLead> leads) {
            this.leads = leads;
        }
    }

}

