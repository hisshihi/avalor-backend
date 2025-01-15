package com.hiss.avalor_backend.dto;

import com.hiss.avalor_backend.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteDto {

    private List<RouteSegmentDto> segments;
    private int totalCost;
    private RentEntity rent;
    private DropOffEntity dropOffEntity;
//    private List<DropOffEntity> dropOff;
    private List<Long> railways;
    private List<Long> seas;
    private List<Long> autos;

    public RouteDto(List<RouteSegmentDto> segments, int totalCost, RentEntity rent, DropOffEntity dropOffEntity, List<Long> railways, List<Long> seas, List<Long> autos) {
        this.segments = segments;
        this.totalCost = totalCost;
        this.rent = rent;
        this.dropOffEntity = dropOffEntity;
        this.railways = railways;
        this.seas = seas;
        this.autos = autos;
    }
}
