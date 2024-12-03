package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.Route;

import java.util.List;

public interface RouteService {

    List<List<Route>> calculateRoutes(String cityFrom, String cityTo);

}
