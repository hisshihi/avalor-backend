package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.Route;

import java.util.List;

public interface RouteService {

    List<Route> findShortestPath(String from, String to);

    double calculateCost(List<Route> path);

}
