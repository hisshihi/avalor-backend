package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.repo.RouteRepo;
import com.hiss.avalor_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteRepo routeRepo;

    // Очищение кеша
    @CacheEvict(value = "shortestPaths", allEntries = true)
    public void refreshCache() {
        log.info("Refresh cache");
    }

    @Override
    @Cacheable(value = "shortestPaths", key = "#cityFrom + '->' + #cityTo")
    public List<List<Route>> calculateRoutes(String cityFrom, String cityTo) {
        List<Route> allRoutes = routeRepo.findAll(); // Получаем все маршруты из БД
        List<List<Route>> results = new ArrayList<>();

        // Рекурсивный поиск маршрутов
        findRoutesRecursive(allRoutes, cityFrom, cityTo, new ArrayList<>(), results);

        // Сортировка маршрутов по убыванию стоимости
        results.sort((route1, route2) -> {
            int cost1 = route1.stream().mapToInt(Route::getCost).sum();
            int cost2 = route2.stream().mapToInt(Route::getCost).sum();
            return Integer.compare(cost1, cost2);
        });

        return results;
    }

    private void findRoutesRecursive(List<Route> allRoutes, String currentCity, String destinationCity,
                                     List<Route> currentPath, List<List<Route>> results) {
        for (Route route : allRoutes) {
            // Если маршрут начинается с текущего города
            if (route.getCityFrom().equalsIgnoreCase(currentCity)) {
                if (currentPath.contains(route)) {
                    // Избегаем циклов
                    continue;
                }

                // Добавляем маршрут в текущий путь
                currentPath.add(route);

                if (route.getCityTo().equalsIgnoreCase(destinationCity)) {
                    // Если достигли конечного города, добавляем путь в результат
                    results.add(new ArrayList<>(currentPath));
                } else {
                    // Рекурсивно продолжаем поиск из нового города
                    findRoutesRecursive(allRoutes, route.getCityTo(), destinationCity, currentPath, results);
                }

                // Убираем маршрут из текущего пути (шаг назад для других ветвей поиска)
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

}
