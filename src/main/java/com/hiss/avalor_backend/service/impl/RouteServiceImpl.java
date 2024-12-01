package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.entity.Tariff;
import com.hiss.avalor_backend.repo.RouteRepository;
import com.hiss.avalor_backend.repo.TariffRepository;
import com.hiss.avalor_backend.service.RouteGraphService;
import com.hiss.avalor_backend.service.RouteService;
import com.hiss.avalor_backend.service.TariffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final TariffService tariffService;
    private final RouteGraphService routeGraphService;

    // Очищение кеша
    @Override
    @CacheEvict(value = "shortestPaths", allEntries = true)
    public void refreshCache() {
        log.info("Route cache has been cleared");
    }

    @Override
    @Cacheable(value = "shortestPaths", key = "#from + '->' + #to")
    public List<Route> findShortestPath(String from, String to) {
        // Загрузим все маршруты из базы данных
        List<Route> allRoutes = routeRepository.findAll();
        log.info("Loaded {} routes from database", allRoutes.size());

        // Построим граф из маршрутов
        routeGraphService.buildGraph(allRoutes);

        // Логика поиска кратчайшего пути остаётся без изменений
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previousCity = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        distances.put(from, 0.0);
        queue.add(from);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            log.info("Current city: {}", current);

            if (current.equals(to)) {
                log.info("Destination reached: {}", to);
                break;
            }

            for (Route route : routeGraphService.getRoutesFrom(current)) {
                double newDistance = distances.get(current) + route.getDistance();
                log.info("Checking route from {} to {}: current distance = {}, new distance = {}",
                        current, route.getToCity(), distances.getOrDefault(route.getToCity(), Double.MAX_VALUE), newDistance);

                if (newDistance < distances.getOrDefault(route.getToCity(), Double.MAX_VALUE)) {
                    distances.put(route.getToCity(), newDistance);
                    previousCity.put(route.getToCity(), current);
                    queue.add(route.getToCity());
                }
            }
        }
        log.info("Distances map: {}", distances);
        log.info("Previous city map: {}", previousCity);

        List<Route> path = new ArrayList<>();
        for (String at = to; at != null; at = previousCity.get(at)) {
            String prev = previousCity.get(at);
            if (prev != null) {
                String destination = at;
                Route route = routeGraphService.getRoutesFrom(prev).stream()
                        .filter(r -> r.getToCity().equals(destination))
                        .findFirst()
                        .orElseThrow();
                path.add(route);
            }
        }

        Collections.reverse(path);
        return path;
    }

    @Override
    public double calculateCost(List<Route> path) {
        double totalCost = 0;
        for (Route route : path) {
            Tariff tariff = tariffService.findByTransportType(route.getTransportType());
            totalCost += route.getDistance() * tariff.getCostPerUnit();
        }
        return totalCost;
    }
}

