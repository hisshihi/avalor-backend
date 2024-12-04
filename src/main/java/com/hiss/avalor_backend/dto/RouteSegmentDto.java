package com.hiss.avalor_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteSegmentDto {

    Long id;
    String cityFrom;
    String cityTo;
    String carrier;
    int cost;
    String transportType;
    String containerTypeSize;
    String filo;
    String pol;
    String pod;

    public RouteSegmentDto(Long id, String cityFrom, String cityTo, String carrier, int cost, String transportType, String containerTypeSize, String filo, String pol, String pod) {
        this.id = id;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.carrier = carrier;
        this.cost = cost;
        this.transportType = transportType;
        this.containerTypeSize = containerTypeSize;
        this.filo = filo;
        this.pol = pol;
        this.pod = pod;
    }
}
