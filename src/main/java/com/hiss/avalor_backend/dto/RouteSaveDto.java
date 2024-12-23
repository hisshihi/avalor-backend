package com.hiss.avalor_backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteSaveDto {

    private String cityFrom;
    private String cityTo;
    private String transportType;

    private String polCountry; // Страна порта погрузки
    private String pol;        // Порт погрузки
    private String pod;        // Порт разгрузки

    //    private String carrierShortName; // Короткое название перевозчика
    private String eqpt;       // Тип оборудования
    private String containerTypeSize; // Тип и размер контейнера
    private String validTo; // Дата действия
    private String filo;       // Free In, Liner Out
    private String notes;      // Примечания
    private String comments;   // Комментарии

    //    private Long carrierId;
    private String carrier;

    private String arrangementForRailwayDays;
    private String transitTimeByTrainDays;
    private String totalWithoutMovementDays;
    private String totalTravelDays;
    private String totalTotalTimeDays;

    private Long storageAtThePortOfArrivalEntity;
    private Long storageAtTheRailwayOfArrivalEntity;

    private String arrivalDate;


}
