package com.hiss.avalor_backend.dto;

import com.hiss.avalor_backend.entity.DropOffEntity;
import com.hiss.avalor_backend.entity.RentEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteDto {

    private List<RouteSegmentDto> segments;
    private int totalCost;
    private RentEntity rent;
    private DropOffEntity dropOff;

    public RouteDto(List<RouteSegmentDto> segments, int totalCost, RentEntity rent, DropOffEntity dropOff) {
        this.segments = segments;
        this.totalCost = totalCost;
        this.rent = rent;
        this.dropOff = dropOff;
    }
}
