package com.hiss.avalor_backend.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RouteWithCost {

    private List<Route> route;
    private int totalCost;
    private RentEntity rentEntity;
    private DropOffEntity dropOff;
//    private int segmentCost;

    public RouteWithCost(List<Route> route, int totalCost, RentEntity rentEntity, DropOffEntity dropOff) {
        this.route = route;
        this.totalCost = totalCost;
        this.rentEntity = rentEntity;
        this.dropOff = dropOff;
    }

}
