package com.hiss.avalor_backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiss.avalor_backend.service.CalculateTransferService;
import com.hiss.avalor_backend.service.CityToAndFromService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final CityToAndFromService cityToAndFromService;
    private final CalculateTransferService calculateTransferService;

    public static final String API_URL = "https://rest.tramis.ru/index.php?_url=/vedexx/";

    // Поиск городов получения
    @PreAuthorize("permitAll()")
    @GetMapping("/city-to")
    @SneakyThrows
    public ResponseEntity<?> cityTo() {
        return new ResponseEntity<>(cityToAndFromService.findCityTo(), HttpStatus.OK);
    }

    // Поиск городов отправления
    @PreAuthorize("permitAll()")
    @GetMapping("/city-from")
    @SneakyThrows
    public ResponseEntity<?> cityFrom() {
        return new ResponseEntity<>(cityToAndFromService.findCityFrom(), HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/calculate")
    @SneakyThrows
    public ResponseEntity<?> calculate(@RequestParam Long placeTo, @RequestParam Long placeFrom) {
        return new ResponseEntity<>(calculateTransferService.findTransfer(placeFrom, placeTo), HttpStatus.OK);
    }


}
