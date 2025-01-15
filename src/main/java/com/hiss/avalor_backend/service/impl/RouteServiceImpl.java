package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.dto.RouteSaveDto;
import com.hiss.avalor_backend.entity.*;
import com.hiss.avalor_backend.repo.*;
import com.hiss.avalor_backend.service.CacheService;
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
    private final RouteRailwayRepository routeRailwayRepository;
    private final RouteSeaRepository routeSeaRepository;
    private final CacheService cacheService;
    private final CitiesService citiesService;
    private final DropOffRepository dropOffRepository;
    private final RentRepository rentRepository;
    private final RouteAutoRepository routeAutoRepository;

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
        List<Route> allRoutes = new ArrayList<>();

        for (RouteRailway railway : routeRailwayRepository.findAll()) {
            log.info("Рельсовый маршрут: " + railway);
            allRoutes.add(new Route(
                    railway.getCityFrom(),
                    railway.getCityTo(),
                    railway.getPol(),
                    railway.getPod(),
                    railway.getCarrier(),
                    railway.getValidTo(),
                    railway.getTransportType(),
                    railway.getContainerTypeSize(),
                    railway.getExclusive(),
                    railway.getFilo20(),
                    railway.getFilo20HC(),
                    railway.getFilo40()
            ));
        }


        for (RouteSea sea : routeSeaRepository.findAll()) {
            log.info("Морской маршрут: " + sea);
            allRoutes.add(new Route(
                    sea.getCityFrom(),
                    sea.getCityTo(),
                    sea.getPol(),
                    sea.getPod(),
                    sea.getCarrier(),
                    sea.getValidTo(),
                    sea.getTransportType(),
                    sea.getContainerTypeSize(),
                    sea.getEqpt(),
                    sea.getFilo(),
                    sea.getExclusive()
            ));
        }

        for (RouteAuto auto : routeAutoRepository.findAll()) {
            log.info("Автомобильный маршрут: " + auto);
            allRoutes.add(new Route(
                    auto.getCityFrom(),
                    auto.getCityTo(),
                    auto.getPol(),
                    auto.getPod(),
                    auto.getCarrier(),
                    auto.getValidTo(),
                    auto.getTransportType(),
                    auto.getContainerTypeSize(),
                    auto.getExclusive(),
                    auto.getFilo20(),
                    auto.getFilo20HC(),
                    auto.getFilo40()
            ));
        }

        log.info("Найдено {} маршрутов в базе данных.", allRoutes.size());
        for (Route route : allRoutes) {
            log.info("Маршрут - {}", route);
        }

        LocalDate targetDate = parseDate(time);

        // Обработка onlyThisCarrier
        List<Route> filteredRoutes = filterRoutes(allRoutes, targetDate, weight);

        log.info("После фильтрации осталось {} маршрутов.", filteredRoutes.size());

        // Список для хранения всех возможных маршрутов.
        List<List<RouteWithCost>> results = new ArrayList<>();

        // Поиск всех возможных маршрутов.
        findRoutes(filteredRoutes, cityFrom, cityTo, targetDate, results);

        log.info("Найдено {} маршрутов из '{}' в '{}'.", results.size(), cityFrom, cityTo);

        for (List<RouteWithCost> allRoutest : results) {
            for (RouteWithCost routeWithCosts : allRoutest) {
                for (Route route : routeWithCosts.getRoute()) {
                    RouteSaveDto routeSaveDto = new RouteSaveDto();
                    routeSaveDto.setCityFrom(route.getCityFrom());
                    routeSaveDto.setCityTo(route.getCityTo());
                    routeSaveDto.setPol(route.getPol());
                    routeSaveDto.setPod(route.getPod());
                    routeSaveDto.setEqpt(route.getEqpt());
                    routeSaveDto.setCarrier(route.getCarrier());
                    routeSaveDto.setValidTo(route.getValidTo());
                    routeSaveDto.setTransportType(route.getTransportType());
                    routeSaveDto.setContainerTypeSize(route.getContainerTypeSize());
                    routeSaveDto.setExclusive(route.getExclusive());
                    if (route.getTransportType().equals("Море")) {
                        routeSaveDto.setFilo(route.getFilo());
                    }
                    if (route.getTransportType().equals("ЖД") || route.getTransportType().equals("Авто")) {
                        routeSaveDto.setFilo20(route.getFilo20());
                        routeSaveDto.setFilo20HC(route.getFilo20HC());
                        routeSaveDto.setFilo40(route.getFilo40());
                    }

                    if (!routeRepo.existsByPolAndPodAndEqpt(route.getPol(), route.getPod(), route.getEqpt())) {
                        create(routeSaveDto);
                    }
                }
            }
        }

        // Сортировка маршрутов по стоимости.
        return sortRoutesByCost(results);
    }

    private List<Route> filterRoutes(List<Route> allRoutes, LocalDate targetDate, String weight) {
        return allRoutes.stream()
                .filter(route -> isValidForDateRange(route.getValidTo(), targetDate))
                .filter(route -> isValidForWeight(route, weight))
                .toList();
    }

    private boolean isValidForWeight(Route route, String weight) {
        if (weight == null) {
            return true; //  If weight is null, don't filter by weight
        }

        String transportType = route.getTransportType();
        if ("ЖД".equals(transportType)) {
            if ("20".equals(weight) || "20t".equals(weight)) {
                return !Objects.equals(route.getFilo20(), 0) || !Objects.equals(route.getFilo20HC(), 0); // 20 or 20t matches either filo20 or filo20HC
            } else if ("40".equals(weight)) {
                return !Objects.equals(route.getFilo40(), 0);
            }
        } else if ("Море".equals(transportType)) {
            return weight.equals(route.getEqpt());
        } else if ("Авто".equals(transportType)) {
            if ("20".equals(weight) || "20t".equals(weight)) {
                return !Objects.equals(route.getFilo20(), 0) || !Objects.equals(route.getFilo20HC(), 0); // 20 or 20t matches either filo20 or filo20HC
            } else if ("40".equals(weight)) {
                return !Objects.equals(route.getFilo40(), 0);
            }
        }
        return false; // No match found
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
        Route route = Route.builder()
                .cityFrom(routeSaveDto.getCityFrom())
                .cityTo(routeSaveDto.getCityTo())
                .validTo(routeSaveDto.getValidTo())
                .transportType(routeSaveDto.getTransportType())
                .carrier(routeSaveDto.getCarrier())
                .filo(routeSaveDto.getFilo())
                .containerTypeSize(routeSaveDto.getContainerTypeSize())
                .exclusive(routeSaveDto.getExclusive())
                .filo20(routeSaveDto.getFilo20())
                .filo20HC(routeSaveDto.getFilo20HC())
                .filo40(routeSaveDto.getFilo40())
                .pod(routeSaveDto.getPod())
                .pol(routeSaveDto.getPol())
                .eqpt(routeSaveDto.getEqpt())
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

        // Ограничение глубины рекурсии (максимальное количество пересадок)
        if (currentPath.size() > 2) {
            log.debug("Достигнуто максимальное количество промежуточных городов: {}", currentPath);
            return;
        }

        for (Route route : allRoutes) {
            // Проверяем, подходит ли маршрут по городу отправления и не был ли он уже посещен
            if (!route.getCityFrom().equalsIgnoreCase(currentCity) || visited.contains(route)) {
                log.trace("Пропуск маршрута из '{}' в '{}' (либо не подходит, либо уже посещен).", route.getCityFrom(), route.getCityTo());
                continue;
            }

            // Проверка даты маршрута
            if (!isValidForDateRange(route.getValidTo(), targetDate)) {
                log.info("Пропуск маршрута из-за даты {}, {}", route.getCityFrom(), route.getCityTo());
                continue;
            }

            // Проверка согласованности портов (POL/POD)
            if (isValidPortSequence(currentPath, route)) {
                log.info("Добавление маршрута: {} -> {}", route.getCityFrom(), route.getCityTo());
                currentPath.add(route); // Добавляем текущий маршрут к пути
                visited.add(route);      // Помечаем маршрут как посещенный


                if (route.getCityTo().equalsIgnoreCase(destinationCity)) {
                    // Если достигли пункта назначения, добавляем найденный маршрут в результаты
                    log.info("Найден маршрут до конечного города '{}'.", destinationCity);
                    results.add(convertToRouteWithCosts(new ArrayList<>(currentPath))); // Создаем копию пути для добавления в результаты
                } else {
                    // Рекурсивный вызов для поиска следующего сегмента маршрута
                    findRoutesRecursive(allRoutes, route.getCityTo(), destinationCity, currentPath, visited, results, targetDate);
                }

                // Удаляем текущий маршрут из пути и списка посещенных, чтобы вернуться к предыдущему состоянию и продолжить поиск других вариантов
                currentPath.remove(currentPath.size() - 1);
                visited.remove(route);
                log.trace("Возврат к предыдущему маршруту: удален маршрут {} -> {}", route.getCityFrom(), route.getCityTo());

            } else {
                // Логируем несогласованность портов
                log.warn("Маршрут {} -> {} не подходит по последовательности портов.", route.getCityFrom(), route.getCityTo());
            }
        }
    }

    private boolean isValidPortSequence(List<Route> currentPath, Route nextRoute) {
        if (currentPath.isEmpty()) {
            return true; // Первый маршрут всегда валиден.
        }

        for (int i = 0; i < currentPath.size(); i++) {
            Route currentRoute = currentPath.get(i);
            Route subsequentRoute = (i < currentPath.size() - 1) ? currentPath.get(i + 1) : nextRoute;

            if (!currentRoute.getPod().equals(subsequentRoute.getPol())) {
                log.warn("Несогласованность портов: POD={} не совпадает с POL={}.", currentRoute.getPod(), subsequentRoute.getPol());
                return false;
            }
        }
        return true;
    }

    /**
     * Конвертация списка маршрутов в список маршрутов с указанием стоимости.
     */
    private List<RouteWithCost> convertToRouteWithCosts(List<Route> path) {
        RentEntity rentEntity = null;
        DropOffEntity dropOffEntity = null;

        boolean hasSea = path.stream().anyMatch(route -> "Море".equals(route.getTransportType()));

        if (hasSea) {
            Optional<Route> seaRoute = path.stream()
                    .filter(route -> "Море".equals(route.getTransportType()))
                    .findFirst();

            if (seaRoute.isPresent()) {
                if ("SOC".equals(seaRoute.get().getContainerTypeSize())) {
                    rentEntity = findRentForRoute(path);
                } else if ("COC".equals(seaRoute.get().getContainerTypeSize())) {
                    String seaCarrier = seaRoute.get().getCarrier(); // Получаем перевозчика морского маршрута

                    String combinedPol = null;  // Для хранения POL комбинированного маршрута
                    String combinedPod = null; // Для хранения POD комбинированного маршрута
                    String eqpt = null;


                    // 1. Проверяем наличие комбинированного маршрута Авто + ЖД
                    Optional<Route> autoRoute = path.stream()
                            .filter(r -> "Авто".equals(r.getTransportType()))
                            .findFirst();
                    Optional<Route> railwayRoute = path.stream()
                            .filter(r -> "ЖД".equals(r.getTransportType()))
                            .findFirst();

                    if (autoRoute.isPresent() && railwayRoute.isPresent()) {
                        combinedPol = autoRoute.get().getPol();
                        combinedPod = railwayRoute.get().getPod();
                        eqpt = determineEqptFromFilo(railwayRoute.get());

                    } else if (railwayRoute.isPresent()) { // Если только жд
                        combinedPol = railwayRoute.get().getPol();
                        combinedPod = railwayRoute.get().getPod();
                        eqpt = determineEqptFromFilo(railwayRoute.get());

                    }

                    if (combinedPol != null && combinedPod != null && eqpt != null) {
                        // Ищем drop off с учётом перевозчика
                        dropOffEntity = dropOffRepository.findByPolAndPodAndSizeAndCarrier(combinedPol, combinedPod, eqpt, seaCarrier);

//                        dropOffEntity = dropOffRepository.findByPolAndPodAndSize(combinedPol, combinedPod, eqpt);

                        if (dropOffEntity == null) { // Если не нашлось с учетом перевозчика, ищем по ЖД без учета перевозчика
                            dropOffEntity = dropOffRepository.findByPolAndPodAndSize(
                                    railwayRoute.get().getPol(), railwayRoute.get().getPod(), eqpt
                            );
                            if (dropOffEntity == null) { // Если и так не нашлось, ищем по морю без учета перевозчика
                                dropOffEntity = dropOffRepository.findByPolAndPodAndSize(
                                        seaRoute.get().getPol(), seaRoute.get().getPod(), seaRoute.get().getEqpt()
                                );
                            }
                        }

                    } else {
                        // Если нет ни комбинированного маршрута, ни только ЖД, используем данные морского маршрута
                        dropOffEntity = dropOffRepository.findByPolAndPodAndSizeAndCarrier(
                                seaRoute.get().getPol(), seaRoute.get().getPod(), seaRoute.get().getEqpt(), seaCarrier
                        );

                        if (dropOffEntity == null) { // Если и так не нашлось, ищем по морю без учета перевозчика
                            dropOffEntity = dropOffRepository.findByPolAndPodAndSize(
                                    seaRoute.get().getPol(), seaRoute.get().getPod(), seaRoute.get().getEqpt()
                            );
                        }
                    }
                }
            }
        }

        final RentEntity finalRentEntity = rentEntity;
        final DropOffEntity finalDropOffEntity = dropOffEntity;

        int totalCost = path.stream().mapToInt(route -> calculateSegmentCost(route, finalRentEntity, finalDropOffEntity)).sum();

        return List.of(new RouteWithCost(path, totalCost, rentEntity, dropOffEntity));
    }

    private String determineEqptFromFilo(Route route) {
        if (route.getFilo20() != null && route.getFilo20() != 0) {
            return "20";
        } else if (route.getFilo20HC() != null && route.getFilo20HC() != 0) {
            return "20t";
        } else if (route.getFilo40() != null && route.getFilo40() != 0) {
            return "40";
        } else {
            return null; // Или другое значение по умолчанию/обработка ошибки, если нужно
        }
    }


    private RentEntity findRentForRoute(List<Route> path) {
        Optional<Route> socSeaRoute = path.stream()
                .filter(route -> "Море".equals(route.getTransportType()) && "SOC".equals(route.getContainerTypeSize()))
                .findFirst();

        if (socSeaRoute.isEmpty()) {
            log.warn("SOC sea route not found in path.");
            return null;
        }

        Route seaRoute = socSeaRoute.get();

        // Начальный город всего маршрута
        String startCity = path.get(0).getCityFrom();

        // Конечный город всего маршрута
        String endCity = path.get(path.size() - 1).getCityTo(); // Последний город в пути

        return rentRepository.findByPolAndPodAndSize(startCity, endCity, seaRoute.getEqpt());
    }

    // Метод для поиска аренды по городам (с логикой из determinePolForCity и determinePodForCity)
    private RentEntity findRentForRouteByCity(String startCity, String endCity, String eqpt) {
        RentEntity rent = rentRepository.findByPolAndPodAndSize(startCity, endCity, eqpt);
        if (rent != null) return rent;


        String startPol = determinePolForCity(startCity);
        String endPod = determinePodForCity(endCity);

        if (startPol != null && endPod != null) {
            rent = rentRepository.findByPolAndPodAndSize(startPol, endPod, eqpt);
            if (rent != null) return rent;
        }


        return null;

    }

    private DropOffEntity getDropOffEntity(List<Route> routes) {
        for (Route route : routes) {
            log.info("route with getDropOffEntity: {}", route);
            if ("Море".equals(route.getTransportType()) && "COC".equals(route.getContainerTypeSize())) {
                log.info("Найден морской маршрут с COC: {} -> {}, eqpt: {}", route.getCityFrom(), route.getCityTo(), route.getEqpt());
                DropOffEntity dropOffEntity = dropOffRepository.findByPolAndPodAndSize(route.getPol(), route.getPod(), route.getEqpt());
                log.info("Drop off entity: {}", dropOffEntity);

                if (dropOffEntity != null) {
                    log.info("Найден dropOffEntity: {}", dropOffEntity);
                    return dropOffEntity;
                } else {
                    log.warn("dropOffEntity НЕ НАЙДЕН для pol: {}, pod: {}, size: {}", route.getPol(), route.getPod(), route.getEqpt());
                    return null;
                }
            }
        }
        log.warn("Морской маршрут с COC не найден в этом пути.");
        return null;
    }

    private String determinePolForCity(String city) {
        // 1. Попробуем найти прямой маршрут, начинающийся в этом городе:
        RouteSea route = routeSeaRepository.findByCityFrom(city); //  Нужен репозиторий для Route
        if (route != null) {
            return route.getPol(); //  Если нашли маршрут, возвращаем его POL
        }


        // 2. Если прямой маршрут не найден, попробуем найти по частичному совпадению в названии города
        List<RouteSea> routes = routeSeaRepository.findAllByCityFromContainingIgnoreCase(city);
        if (!routes.isEmpty()) {
            return routes.get(0).getPol(); //  Возвращаем POL первого найденного маршрута
        }

        // 3. Если ничего не найдено, можно попробовать другие варианты (например, поиск по таблице городов/портов)

        return null; // Если POL не найден
    }

    private String determinePodForCity(String city) {
        // Аналогичная логика для POD (с использованием cityTo и pod)

        List<RouteSea> routes = routeSeaRepository.findByCityTo(city); //  Нужен репозиторий для Route
        if (!routes.isEmpty()) {
            return routes.get(0).getPod();
        }

        List<RouteSea> routesIgnoreCase = routeSeaRepository.findAllByCityToContainingIgnoreCase(city);
        if (!routesIgnoreCase.isEmpty()) {
            return routesIgnoreCase.get(0).getPod();
        }


        return null;  //  Если POD не найден

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
    private int calculateSegmentCost(Route route, final RentEntity rentEntity, final DropOffEntity dropOffEntity) {

        int routeCost = 0;
        log.trace("Начальная стоимость маршрута: {} -> {} = {}.",
                route.getCityFrom(), route.getCityTo(), routeCost);

        if ("SOC".equals(route.getContainerTypeSize()) && rentEntity != null) {
            routeCost += rentEntity.getFilo();
            log.info("Добавлена стоимость аренды контейнера: {}.", rentEntity.getFilo());

        } else if ("COC".equals(route.getContainerTypeSize()) && "Море".equals(route.getTransportType())  && dropOffEntity != null) {
            routeCost += dropOffEntity.getFilo();
            log.info("Добавлена стоимость drop off: {}.", dropOffEntity.getFilo());
        }

//        int handlingCost = getHandlingCost(route);
        routeCost += getHandlingCost(route);
        log.info("Добавлена стоимость обработки: {}.", routeCost);

        log.info("Итоговая стоимость сегмента {} -> {} = {}.", route.getCityFrom(), route.getCityTo(), routeCost);
        return routeCost;
    }

    /**
     * Расчет стоимости аренды контейнера.
     */
    private int getContainerRentCost(Route route) {
        if (rentRepository.findByPolAndPodAndSize(route.getPol(), route.getPod(), route.getEqpt()) != null) {
            RentEntity rentCost = rentRepository.findByPolAndPodAndSize(route.getPol(), route.getPod(), route.getEqpt()); // Константа для аренды контейнера.
            log.info("Стоимость аренды контейнера для маршрута {} -> {}: {}.",
                    route.getPol(), route.getPod(), rentCost.getFilo());
            return rentCost.getFilo();
        }
        return 0;
    }

    /*
    * Расчёт стоимости drop off
    * */
    private int getContainerDropOffCost(Route route) {
        if (dropOffRepository.findByPolAndPodAndSize(route.getPol(), route.getPod(), route.getEqpt()) != null) {
            DropOffEntity dropOffEntityCost = dropOffRepository.findByPolAndPodAndSize(route.getPol(), route.getPod(), route.getEqpt());
            log.info("Стоимость drop off для маршрута {} -> {}: {}",
                    route.getPol(), route.getPod(), dropOffEntityCost.getFilo());
            return dropOffEntityCost.getFilo();
        }
        return 0;
    }

    /**
     * Расчет стоимости обработки груза.
     */
    private int getHandlingCost(Route route) {
        int handlingCost = 0;
        if (route.getFilo() != null) {
            handlingCost = route.getFilo();
        }
        if (route.getFilo20() != null && !route.getFilo20().equals(0)) {
            handlingCost += route.getFilo20();
            if (route.getTransportType().equals("ЖД") || route.getTransportType().equals("Авто") && !route.getFilo20().equals(0)) {
                route.setEqpt("20");
                route.setFilo(route.getFilo20());
            }
        } else if (route.getFilo20HC() != null && !route.getFilo20HC().equals(0)) {
            handlingCost += route.getFilo20HC();
            if (route.getTransportType().equals("ЖД") || route.getTransportType().equals("Авто") && !route.getFilo20HC().equals(0)) {
                route.setEqpt("20t");
                route.setFilo(route.getFilo20HC());
            }
        } else if (route.getFilo40() != null && !route.getFilo40().equals(0)) {
            handlingCost += route.getFilo40();
            if (route.getTransportType().equals("ЖД") || route.getTransportType().equals("Авто") && !route.getFilo40().equals(0)) {
                route.setEqpt("40");
                route.setFilo(route.getFilo40());
            }
        }
        log.info("Стоимость обработки для маршрута {} -> {}: {}.",
                route.getCityFrom(), route.getCityTo(), handlingCost);
        return handlingCost;
    }

}