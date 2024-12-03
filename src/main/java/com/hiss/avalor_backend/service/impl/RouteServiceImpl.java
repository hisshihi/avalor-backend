package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.entity.RouteWithCost;
import com.hiss.avalor_backend.repo.RouteRepo;
import com.hiss.avalor_backend.service.RouteService;
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

    private final RouteRepo routeRepo;

    /**
     * Метод для очистки кеша маршрутов.
     */
    @CacheEvict(value = "shortestPaths", allEntries = true)
    public void refreshCache() {
        log.info("Кэш для shortestPaths успешно очищен.");
    }

    /**
     * Основной метод для расчета маршрутов между двумя городами.
     * Использует кеширование для оптимизации повторных запросов.
     */
    @Override
    @Cacheable(value = "shortestPaths", key = "#cityFrom + '->' + #cityTo")
    public List<List<RouteWithCost>> calculateRoutes(String cityFrom, String cityTo) {
        validateInputs(cityFrom, cityTo);

        log.info("Запуск расчета маршрутов из '{}' в '{}'.", cityFrom, cityTo);

        // Получение всех доступных маршрутов из базы данных.
        List<Route> allRoutes = routeRepo.findAll();
        log.debug("Найдено {} маршрутов в базе данных.", allRoutes.size());

        // Список для хранения всех возможных маршрутов.
        List<List<RouteWithCost>> results = new ArrayList<>();

        // Поиск всех возможных маршрутов.
        findRoutes(allRoutes, cityFrom, cityTo, results);

        log.info("Найдено {} маршрутов из '{}' в '{}'.", results.size(), cityFrom, cityTo);

        // Сортировка маршрутов по стоимости.
        return sortRoutesByCost(results);
    }

    /**
     * Проверка входных данных.
     */
    private void validateInputs(String cityFrom, String cityTo) {
        if (cityFrom == null || cityTo == null || cityFrom.isBlank() || cityTo.isBlank()) {
            throw new IllegalArgumentException("Города отправления и назначения не могут быть пустыми.");
        }
        log.debug("Входные данные валидны: город отправления = '{}', город назначения = '{}'.", cityFrom, cityTo);
    }

    /**
     * Инициализация поиска маршрутов.
     */
    private void findRoutes(List<Route> allRoutes, String cityFrom, String cityTo,
                            List<List<RouteWithCost>> results) {
        log.debug("Инициализация поиска маршрутов из '{}' в '{}'.", cityFrom, cityTo);
        findRoutesRecursive(allRoutes, cityFrom, cityTo, new ArrayList<>(), new HashSet<>(), results);
    }

    /**
     * Рекурсивный метод для поиска всех возможных маршрутов.
     */
    private void findRoutesRecursive(List<Route> allRoutes, String currentCity, String destinationCity,
                                     List<Route> currentPath, Set<Route> visited, List<List<RouteWithCost>> results) {
        log.debug("Поиск маршрутов: текущий город = '{}', конечный город = '{}'.", currentCity, destinationCity);

        for (Route route : allRoutes) {
            if (!route.getCityFrom().equalsIgnoreCase(currentCity) || visited.contains(route)) {
                log.trace("Пропуск маршрута из '{}' в '{}' (либо не подходит, либо уже посещен).",
                        route.getCityFrom(), route.getCityTo());
                continue;
            }

            log.debug("Добавление маршрута: {} -> {}", route.getCityFrom(), route.getCityTo());
            currentPath.add(route);
            visited.add(route);

            // Если маршрут достигает конечного города.
            if (route.getCityTo().equalsIgnoreCase(destinationCity)) {
                log.info("Найден маршрут до конечного города '{}'.", destinationCity);
                results.add(convertToRouteWithCosts(new ArrayList<>(currentPath)));
            } else {
                findRoutesRecursive(allRoutes, route.getCityTo(), destinationCity, currentPath, visited, results);
            }

            // Возврат к предыдущему состоянию (удаление последнего маршрута).
            currentPath.remove(currentPath.size() - 1);
            visited.remove(route);
            log.trace("Возврат к предыдущему маршруту: удален маршрут {} -> {}", route.getCityFrom(), route.getCityTo());
        }
    }

    /**
     * Конвертация списка маршрутов в список маршрутов с указанием стоимости.
     */
    private List<RouteWithCost> convertToRouteWithCosts(List<Route> path) {
        // Расчет общей стоимости всего маршрута.
        int totalCost = path.stream().mapToInt(this::calculateSegmentCost).sum();
        log.debug("Расчет общей стоимости маршрута: {}.", totalCost);

        // Конвертация каждого сегмента маршрута в объект RouteWithCost.
        return path.stream()
                .map(route -> {
                    int segmentCost = calculateSegmentCost(route); // Стоимость сегмента.
                    log.trace("Расчет стоимости сегмента: {} -> {}, стоимость = {}.",
                            route.getCityFrom(), route.getCityTo(), segmentCost);

                    // Создание объекта RouteWithCost с общей стоимостью маршрута.
                    return new RouteWithCost(route, segmentCost, totalCost);
                })
                .toList();
    }

    /**
     * Сортировка маршрутов по общей стоимости.
     */
    private List<List<RouteWithCost>> sortRoutesByCost(List<List<RouteWithCost>> routes) {
        routes.sort(Comparator.comparingInt(routeList ->
                routeList.stream().mapToInt(RouteWithCost::getTotalCost).sum()));
        log.info("Маршруты успешно отсортированы по стоимости.");
        return routes;
    }

    /**
     * Расчет стоимости сегмента маршрута.
     */
    private int calculateSegmentCost(Route route) {
        int routeCost = route.getCarrier().getPrice();
        log.trace("Начальная стоимость маршрута: {} -> {} = {}.",
                route.getCityFrom(), route.getCityTo(), routeCost);

        if ("COC".equals(route.getContainerTypeSize())) {
            int rentCost = getContainerRentCost(route);
            routeCost += rentCost;
            log.trace("Добавлена стоимость аренды контейнера: {}.", rentCost);
        }

        if ("Liner Out".equals(route.getFilo())) {
            int handlingCost = getHandlingCost(route);
            routeCost += handlingCost;
            log.trace("Добавлена стоимость обработки: {}.", handlingCost);
        } else if (!"Free In".equals(route.getFilo())) {
            log.warn("Неизвестный тип FILO: {}", route.getFilo());
        }

        log.debug("Итоговая стоимость сегмента {} -> {} = {}.", route.getCityFrom(), route.getCityTo(), routeCost);
        return routeCost;
    }

    /**
     * Расчет стоимости аренды контейнера.
     */
    private int getContainerRentCost(Route route) {
        int rentCost = 2000; // Константа для аренды контейнера.
        log.debug("Стоимость аренды контейнера для маршрута {} -> {}: {}.",
                route.getCityFrom(), route.getCityTo(), rentCost);
        return rentCost;
    }

    /**
     * Расчет стоимости обработки груза.
     */
    private int getHandlingCost(Route route) {
        int handlingCost = 500; // Константа для обработки.
        log.debug("Стоимость обработки для маршрута {} -> {}: {}.",
                route.getCityFrom(), route.getCityTo(), handlingCost);
        return handlingCost;
    }
}




