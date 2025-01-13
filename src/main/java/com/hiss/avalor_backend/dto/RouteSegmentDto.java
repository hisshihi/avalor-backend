package com.hiss.avalor_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RouteSegmentDto {

//    Long id;
    String cityFrom;
    String cityTo;
    String carrier;
    String validTo;
    String eqpt;
//    int cost;
    String transportType;
    String containerTypeSize;
    Integer filo;
    String pol;
    String pod;


}
