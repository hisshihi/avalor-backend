package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.dto.RouteSaveDto;
import com.hiss.avalor_backend.entity.*;
import com.hiss.avalor_backend.repo.RouteRepo;
import com.hiss.avalor_backend.repo.StorageAtThePortOfArrivalRepo;
import com.hiss.avalor_backend.service.CacheService;
import com.hiss.avalor_backend.service.CarrierService;
import com.hiss.avalor_backend.service.CitiesService;
import com.hiss.avalor_backend.service.RouteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteRepo routeRepo;
    private final CarrierService carrierService;
    private final CacheService cacheService;
    private final CitiesService citiesService;
    private final StorageAtThePortOfArrivalRepo storageAtThePortOfArrivalRepo;

    /**
     * Основной метод для расчета маршрутов между двумя городами.
     * Использует кеширование для оптимизации повторных запросов.
     */
    @Override
    @Cacheable(value = "shortestPaths", key = "#cityFrom + '->' + #cityTo + ':' + #time + ':' + #weight")
    public List<List<RouteWithCost>> calculateRoutes(String cityFrom, String cityTo, String time, String weight) {
        validateInputs(cityFrom, cityTo);

        log.info("Запуск расчета маршрутов из '{}' в '{}'.", cityFrom, cityTo);

        // Получение всех доступных маршрутов из базы данных.
        List<Route> allRoutes = routeRepo.findAll();
        log.info("Найдено {} маршрутов в базе данных.", allRoutes.size());

        // Парсинг времени с учётом формата
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate filterTime = LocalDate.parse(time, formatter);

        LocalDate targetDate = parseDate(time);

        // Фильтрация маршрутов по дате
        List<Route> filteredRoutes = allRoutes.stream()
                .filter(route -> {
                    boolean dateMatches = isValidForDateRange(route.getValidTo(), targetDate);
                    log.info("Маршрут ID={} прошел фильтрацию по конечной дате: {}", route.getId(), dateMatches);
                    return dateMatches;
                })
                .filter(route -> {
                    boolean equipmentMatches = route.getEqpt().equals(weight);
                    log.info("Маршрут ID={} прошел фильтрацию по оборудованию: {} {} {}", route.getId(), equipmentMatches, route.getEqpt(), weight);
                    return equipmentMatches;
                })
                .filter(route -> route.getCarrier().isActive())
                .toList();

        log.info("После фильтрации осталось {} маршрутов.", filteredRoutes.size());

        // Список для хранения всех возможных маршрутов.
        List<List<RouteWithCost>> results = new ArrayList<>();

        // Поиск всех возможных маршрутов.
        findRoutes(filteredRoutes, cityFrom, cityTo, targetDate, results);

        log.info("Найдено {} маршрутов из '{}' в '{}'.", results.size(), cityFrom, cityTo);

        // Сортировка маршрутов по стоимости.
        return sortRoutesByCost(results);
    }

    private boolean isValidForDateRange(String validTo, LocalDate targetDate) {
        try {
            // Разбиваем диапазон на начало и конец
            String[] dateRange = validTo.split("-");
            if (dateRange.length != 2) {
                log.warn("Неверный формат диапазона дат: {}", validTo);
                return false;
            }

            // Берем только конечную дату
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            LocalDate startDate = LocalDate.parse(dateRange[0].trim(), formatter);
            LocalDate endDate = LocalDate.parse(dateRange[1].trim(), formatter);

            log.info("Start date: {}", startDate);
            log.info("End date: {}", endDate);
            log.info("Search: start date - {}, end date - {}", !targetDate.isBefore(startDate), !targetDate.isAfter(endDate));

            // Проверяем, что целевая дата не позже конечной
            return !targetDate.isBefore(startDate) && !targetDate.isAfter(endDate);

        } catch (DateTimeParseException e) {
            log.error("Ошибка парсинга конечной даты: validTo={}, targetDate={}, error={}", validTo, targetDate, e);
            return false;
        }
    }

    private LocalDate parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(date.trim(), formatter);
        } catch (DateTimeParseException e) {
            log.error("Ошибка парсинга даты: date={}, error={}", date, e);
            throw new IllegalArgumentException("Неверный формат даты. Ожидается 'dd.MM.yyyy'.");
        }
    }

    @Override
    public Optional<Route> findById(Long id) {
        return routeRepo.findById(id);
    }

    @Override
    public Route getRouteById(Long id) {
        return routeRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Route with ID " + id + " not found"));
    }

    @Override
    public void create(RouteSaveDto routeSaveDto) {

//        Carrier carrier = carrierService.findById(routeSaveDto.getCarrierId())
//                .orElseThrow(
//                        () -> new EntityNotFoundException("Carrier with ID " + routeSaveDto.getCarrierId() + " not found")
//                );
        Carrier carrier = carrierService.findByName(routeSaveDto.getCarrier())
                .orElseThrow(
                        () -> new EntityNotFoundException("Carrier with name " + routeSaveDto.getCarrier() + " not found")
                );

        StorageAtThePortOfArrivalEntity storageAtThePortOfArrivalEntity = storageAtThePortOfArrivalRepo.
                findById(routeSaveDto.getStorageAtThePortOfArrivalEntity()).orElseThrow(
                        () -> new EntityNotFoundException("Route port not found " + routeSaveDto.getStorageAtThePortOfArrivalEntity())
                );

        StorageAtThePortOfArrivalEntity storageAtTheRailwayOfArrivalEntity = storageAtThePortOfArrivalRepo.
                findById(routeSaveDto.getStorageAtTheRailwayOfArrivalEntity()).orElseThrow(
                        () -> new EntityNotFoundException("Route railway not found " + routeSaveDto.getStorageAtTheRailwayOfArrivalEntity())
                );

        Route route = Route.builder()
                .cityFrom(routeSaveDto.getCityFrom())
                .cityTo(routeSaveDto.getCityTo())
                .transportType(routeSaveDto.getTransportType())
                .polCountry(routeSaveDto.getPolCountry())
                .carrier(carrier)
                .pol(routeSaveDto.getPol())
                .pod(routeSaveDto.getPod())
                .eqpt(routeSaveDto.getEqpt())
                .containerTypeSize(routeSaveDto.getContainerTypeSize())
                .validTo(routeSaveDto.getValidTo())
                .filo(routeSaveDto.getFilo())
                .notes(routeSaveDto.getNotes())
                .comments(routeSaveDto.getComments())
                .carrierShortName(carrier.getName())
                .totalTravelDays(routeSaveDto.getTotalTravelDays())
                .arrangementForRailwayDays(routeSaveDto.getArrangementForRailwayDays())
                .totalTotalTimeDays(routeSaveDto.getTotalTotalTimeDays())
                .transitTimeByTrainDays(routeSaveDto.getTransitTimeByTrainDays())
                .totalWithoutMovementDays(routeSaveDto.getTotalWithoutMovementDays())
                .arrivalDate(addArrivalDate(routeSaveDto.getValidTo()))
                .storageAtTheRailwayOfArrivalEntity(storageAtTheRailwayOfArrivalEntity)
                .storageAtThePortOfArrivalEntity(storageAtThePortOfArrivalEntity)
                .build();

        routeRepo.save(route);

        clearCache();

        // Проверка и сохранение города
        saveUniqueCities(routeSaveDto.getCityFrom(), routeSaveDto.getCityTo());

    }

    private String addArrivalDate(String validTo) {
        String[] dateRange = validTo.split("-");
        String endDate = dateRange[1];
        return endDate;
    }

    private void saveUniqueCities(String cityFrom, String cityTo) {
        // Проверяем, существуют ли города
        boolean cityFromExists = citiesService.existsByCity(cityFrom);
        boolean cityToExists = citiesService.existsByCity(cityTo);

        // Сохраняем только те города, которые ещё не существуют
        if (!cityFromExists) {
            Cities newCityFrom = new Cities();
            newCityFrom.setCity(cityFrom);
            citiesService.save(newCityFrom);
        }
        if (!cityToExists) {
            Cities newCityTo = new Cities();
            newCityTo.setCity(cityTo);
            citiesService.save(newCityTo);
        }
    }

    @Override
    @Transactional
    public List<Route> findMany(List<Long> ids) {
        return routeRepo.findAllById(ids);
    }

    private void clearCache() {
        cacheService.refreshCacheRoute();
    }

    /**
     * Проверка входных данных.
     */
    private void validateInputs(String cityFrom, String cityTo) {
        if (cityFrom == null || cityTo == null || cityFrom.isBlank() || cityTo.isBlank()) {
            throw new IllegalArgumentException("Города отправления и назначения не могут быть пустыми.");
        }
        log.info("Входные данные валидны: город отправления = '{}', город назначения = '{}'.", cityFrom, cityTo);
    }

    /**
     * Инициализация поиска маршрутов.
     */
    private void findRoutes(List<Route> allRoutes, String cityFrom, String cityTo, LocalDate targetDate,
                            List<List<RouteWithCost>> results) {
        log.info("Инициализация поиска маршрутов из '{}' в '{}'.", cityFrom, cityTo);
        findRoutesRecursive(allRoutes, cityFrom, cityTo, new ArrayList<>(), new HashSet<>(), results, targetDate);
    }

    /**
     * Рекурсивный метод для поиска всех возможных маршрутов.
     */
    private void findRoutesRecursive(List<Route> allRoutes, String currentCity, String destinationCity,
                                     List<Route> currentPath, Set<Route> visited, List<List<RouteWithCost>> results, LocalDate targetDate) {
        log.info("Поиск маршрутов: текущий город = '{}', конечный город = '{}'.", currentCity, destinationCity);

        // Ограничение длины маршрута: не более 1 промежуточных пунктов.
        if (currentPath.size() > 1) {
            log.debug("Достигнуто максимальное количество промежуточных городов: {}", currentPath);
            return;
        }

        for (Route route : allRoutes) {
            // Проверяем, подходит ли маршрут.
            if (!route.getCityFrom().equalsIgnoreCase(currentCity) || visited.contains(route)) {
                log.trace("Пропуск маршрута из '{}' в '{}' (либо не подходит, либо уже посещен).",
                        route.getCityFrom(), route.getCityTo());
                continue;
            }

            // Дополнительная проверка даты для каждого сегмента состовного маршрута
            if (!isValidForDateRange(route.getValidTo(), targetDate)) {
                log.info("Пропуск маршрута из-за даты {}, {}", route.getCityFrom(), route.getCityTo());
                continue;
            }

            log.info("Добавление маршрута: {} -> {}", route.getCityFrom(), route.getCityTo());
            currentPath.add(route);
            visited.add(route);

            // Если маршрут достигает конечного города.
            if (route.getCityTo().equalsIgnoreCase(destinationCity)) {
                log.info("Найден маршрут до конечного города '{}'.", destinationCity);
                results.add(convertToRouteWithCosts(new ArrayList<>(currentPath)));
            } else {
                // Продолжаем поиск рекурсивно.
                findRoutesRecursive(allRoutes, route.getCityTo(), destinationCity, currentPath, visited, results, targetDate);
            }

            // Возврат к предыдущему состоянию (удаление последнего маршрута).
            currentPath.remove(currentPath.size() - 1);
            visited.remove(route);
            log.trace("Возврат к предыдущему маршруту: удален маршрут {} -> {}", route.getCityFrom(), route.getCityTo());
        }
    }

    private boolean isValidPortSequence(List<Route> currentPath, Route nextRoute) {
        if (currentPath.isEmpty()) {
            return true; // Первый маршрут всегда валиден.
        }

        Route lastRoute = currentPath.get(currentPath.size() - 1);

        // Проверяем, чтобы тип транспорта был согласован.
        if ("Море".equals(nextRoute.getTransportType()) && !"Port".equals(lastRoute.getTransportType())) {
            log.warn("Маршрут {} -> {} требует порта для перехода на корабль.", lastRoute.getCityTo(), nextRoute.getCityFrom());
            return false;
        }
        if ("ЖД".equals(nextRoute.getTransportType()) && "Море".equals(lastRoute.getTransportType())) {
            log.warn("Маршрут {} -> {} не может перейти с корабля на поезд.", lastRoute.getCityTo(), nextRoute.getCityFrom());
            return false;
        }

        // Проверяем соответствие портов для морских маршрутов.
        if ("Море".equals(nextRoute.getTransportType()) && !lastRoute.getPod().equals(nextRoute.getPol())) {
            log.warn("Несогласованность портов: POD={} не совпадает с POL={}.", lastRoute.getPod(), nextRoute.getPol());
            return false;
        }

        return true;
    }


    /**
     * Конвертация списка маршрутов в список маршрутов с указанием стоимости.
     */
    private List<RouteWithCost> convertToRouteWithCosts(List<Route> path) {
        // Расчет общей стоимости всего маршрута.
        int totalCost = path.stream().mapToInt(this::calculateSegmentCost).sum();
        log.debug("Расчет общей стоимости маршрута: {}.", totalCost);

        // Create a single RouteWithCost for the entire path.
        return List.of(new RouteWithCost(path, totalCost));
    }

    /**
     * Сортировка маршрутов по общей стоимости.
     */
    private List<List<RouteWithCost>> sortRoutesByCost(List<List<RouteWithCost>> routes) {
        routes.sort(Comparator.comparingInt(routeList -> routeList.get(0).getTotalCost())); // Access total cost from the first element
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
            log.info("Добавлена стоимость аренды контейнера: {}.", rentCost);
        }

        if ("Liner Out".equals(route.getFilo())) {
            int handlingCost = getHandlingCost(route);
            routeCost += handlingCost;
            log.info("Добавлена стоимость обработки: {}.", handlingCost);
        } else if (!"Free In".equals(route.getFilo())) {
            log.warn("Неизвестный тип FILO: {}", route.getFilo());
        }

        log.info("Итоговая стоимость сегмента {} -> {} = {}.", route.getCityFrom(), route.getCityTo(), routeCost);
        return routeCost;
    }

    /**
     * Расчет стоимости аренды контейнера.
     */
    private int getContainerRentCost(Route route) {
        int rentCost = route.getCarrier().getContainerRentalPrice(); // Константа для аренды контейнера.
        log.info("Стоимость аренды контейнера для маршрута {} -> {}: {}.",
                route.getCityFrom(), route.getCityTo(), rentCost);
        return rentCost;
    }

    /**
     * Расчет стоимости обработки груза.
     */
    private int getHandlingCost(Route route) {
        int handlingCost = 0; // Константа для обработки.
        log.info("Стоимость обработки для маршрута {} -> {}: {}.",
                route.getCityFrom(), route.getCityTo(), handlingCost);
        return handlingCost;
    }
}




