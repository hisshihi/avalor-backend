package com.hiss.avalor_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class RouteSegmentDto {

    Long id;
    String cityFrom;
    String cityTo;
    String carrier;
    Integer price;
    Integer priceDollars;
    Integer containerRentalPrice;
    Integer containerRentalPriceDollars;
    LocalDate validTo;
    String eqpt;
    int cost;
    String transportType;
    String containerTypeSize;
    String filo;
    String pol;
    String pod;
    String arrangementForRailwayDays;
    String transitTimeByTrainDays;
    String totalWithoutMovementDays;
    String totalTravelDays;
    String totalTotalTimeDays;


}
