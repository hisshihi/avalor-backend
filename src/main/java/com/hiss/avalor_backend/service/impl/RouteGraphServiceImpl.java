package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.service.RouteGraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
// Сервис для хранения графа маршрутов
public class RouteGraphServiceImpl implements RouteGraphService {

    private final Map<String, List<Route>> graph = new HashMap<>();

    @Override
    public void buildGraph(List<Route> routes) {
        for (Route route : routes) {
            graph.putIfAbsent(route.getFromCity(), new ArrayList<>());
            graph.get(route.getFromCity()).add(route);
            log.info("Added route from {} to {}, distance: {}", route.getFromCity(), route.getToCity(), route.getDistance());
        }
        log.info("Graph built: {}", graph);
    }

    @Override
    public List<Route> getRoutesFrom(String city) {
        List<Route> routes = graph.getOrDefault(city, new ArrayList<>());
        log.info("Routes from {}: {}", city, routes);
        return routes;
    }

}
