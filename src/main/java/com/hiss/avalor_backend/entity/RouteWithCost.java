package com.hiss.avalor_backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteWithCost {

    private Route route;
    private int totalCost;
    private int segmentCost;

    public RouteWithCost(Route route, int totalCost, int segmentCost) {
        this.route = route;
        this.totalCost = totalCost;
        this.segmentCost = segmentCost;
    }

}
