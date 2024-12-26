package com.hiss.avalor_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteDto {

    List<RouteSegmentDto> segments;
    int totalCost;

    public RouteDto(List<RouteSegmentDto> segments, int totalCost) {
        this.segments = segments;
        this.totalCost = totalCost;
    }
}
