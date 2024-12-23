package com.hiss.avalor_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    String validTo;
    String arrivalDate;
    String eqpt;
    int cost;
    String transportType;
    String containerTypeSize;
    Integer filo;
    String pol;
    String pod;
    String arrangementForRailwayDays;
    String transitTimeByTrainDays;
    String totalWithoutMovementDays;
    String totalTravelDays;
    String totalTotalTimeDays;


}
