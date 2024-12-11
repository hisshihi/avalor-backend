package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.dto.RouteDto;
import com.hiss.avalor_backend.dto.RouteSaveDto;
import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.entity.RouteWithCost;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    List<List<RouteWithCost>> calculateRoutes(String cityFrom, String cityTo, String time, String weight);

    Optional<Route> findById(Long id);

    Route getRouteById(Long aLong);

    void create(RouteSaveDto routeSaveDto);

    List<Route> findMany(List<Long> ids);
}
