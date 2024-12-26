package com.hiss.avalor_backend.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteWithCost {

    private List<Route> route;
    private int totalCost;
//    private int segmentCost;

    public RouteWithCost(List<Route> route, int totalCost) {
        this.route = route;
        this.totalCost = totalCost;
    }

}
