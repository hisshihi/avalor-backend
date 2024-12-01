package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.Route;

import java.util.List;

public interface RouteGraphService {

    void buildGraph(List<Route> routes);

    List<Route> getRoutesFrom(String city);

}
