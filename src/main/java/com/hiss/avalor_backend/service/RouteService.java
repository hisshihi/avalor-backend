package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.RouteWithCost;

import java.util.List;

public interface RouteService {

    List<List<RouteWithCost>> calculateRoutes(String cityFrom, String cityTo);

}
