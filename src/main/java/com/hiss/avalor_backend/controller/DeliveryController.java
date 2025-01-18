package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.dto.RouteDto;
import com.hiss.avalor_backend.dto.RouteSegmentDto;
import com.hiss.avalor_backend.entity.*;
import com.hiss.avalor_backend.repo.*;
import com.hiss.avalor_backend.service.CacheService;
import com.hiss.avalor_backend.service.RouteExcelParserService;
import com.hiss.avalor_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final RouteService routeService;
    private final CacheService cacheService;
    private final RouteExcelParserService routeExcelParserService;
    private final DropOffRepository dropOffRepository;
    private final RentRepository rentRepository;
    private final RouteRailwayRepository routeRailwayRepository;
    private final RouteSeaRepository routeSeaRepository;
    private final RouteAutoRepository routeAutoRepository;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/by-ids")
    public ResponseEntity<List<Route>> getMany(@RequestParam List<Long> ids) {
        List<Route> routes = routeService.findMany(ids);
        return ResponseEntity.ok(routes);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/calculate")
    public ResponseEntity<List<RouteDto>> calculateRoutes(
            @RequestParam String cityFrom,
            @RequestParam String cityTo,
            @RequestParam String time,
            @RequestParam String weight) {
        List<List<RouteWithCost>> routesWithCosts = routeService.calculateRoutes(cityFrom, cityTo, time, weight);
        List<RouteDto> routesDTO = routesWithCosts.stream()
                .map(this::convertToRouteDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(routesDTO);
    }

    private RouteDto convertToRouteDTO(List<RouteWithCost> routeWithCosts) {
        //Assuming only one RouteWithCost per List
        RouteWithCost route = routeWithCosts.get(0);

//        RentEntity rentEntity = getRentEntity(route.getRoute());
//        List<DropOffEntity> dropOff = getDropOffEntity(route.getRoute());

//        if (dropOff == null) {
//            log.warn("Drop off not found: {}", route.getRoute());
//        } else {
//            log.info("Drop off: {}", dropOff);
//        }

        List<Long> railwayIds = collectRouteIds(routeWithCosts, "ЖД");
        List<Long> autoIds = collectRouteIds(routeWithCosts, "Авто");
        List<Long> seaIds = collectRouteIds(routeWithCosts, "Море");

        return new RouteDto(
                route.getRoute().stream()
                        .map(r -> {
                            return new RouteSegmentDto(
                                    r.getId(),
                                    r.getCityFrom(),
                                    r.getCityTo(),
                                    r.getCarrier(),
                                    r.getValidTo(),
                                    r.getEqpt(),
                                    r.getTransportType(),
                                    r.getContainerTypeSize(),
                                    r.getFilo(),
                                    r.getFiloD(),
                                    r.getPol(),
                                    r.getPod()
                            );

                        })
                        .collect(Collectors.toList()),
                route.getTotalCost(),
                route.getRentEntity(),
                route.getDropOff(),
                railwayIds,
                seaIds,
                autoIds
        );
    }

    List<Long> collectRouteIds(List<RouteWithCost> routeWithCosts, String transportType) {
        List<Long> routeIds = new ArrayList<>();
        for (RouteWithCost routeWithCost : routeWithCosts) {
            for (Route findRoute : routeWithCost.getRoute()) {
                switch (transportType) {
                    case "ЖД":
                        RouteRailway routeRailway = routeRailwayRepository.findByPolAndPodAndFilo20AndFilo20HCAndFilo40AndCarrier(
                                findRoute.getPol(),
                                findRoute.getPod(),
                                findRoute.getFilo20(),
                                findRoute.getFilo20HC(),
                                findRoute.getFilo40(),
                                findRoute.getCarrier()
                        );
                        if (routeRailway != null) {
                            routeIds.add(routeRailway.getId());
                        }
                        break;

                    case "Авто":
                        RouteAuto routeAuto = routeAutoRepository.findByPolAndPodAndFilo20AndFilo20HCAndFilo40AndCarrier(
                                findRoute.getPol(),
                                findRoute.getPod(),
                                findRoute.getFilo20(),
                                findRoute.getFilo20HC(),
                                findRoute.getFilo40(),
                                findRoute.getCarrier()
                        );
                        if (routeAuto != null) {
                            routeIds.add(routeAuto.getId());
                        }
                        break;

                    case "Море":
                        RouteSea routeSea = routeSeaRepository.findByPolAndPodAndEqptAndCarrier(
                                findRoute.getPol(),
                                findRoute.getPod(),
                                findRoute.getEqpt(),
                                findRoute.getCarrier()
                        );
                        if (routeSea != null) {
                            routeIds.add(routeSea.getId());
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported transport type: " + transportType);
                }
            }
        }
        return routeIds;
    }


    //  Вспомогательные методы для получения RentEntity и DropOffEntity (в контроллере)
    private RentEntity getRentEntity(List<Route> routes) {
        for (Route route : routes) {
            if (route.getContainerTypeSize().equals("SOC")) {
                String startPol = route.getPol();
                String endPod = route.getPod();
                return rentRepository.findByPolAndPodAndSize(startPol, endPod, route.getEqpt());
            }
        }
        return null; // Возвращаем null, если RentEntity не применима
    }


    private DropOffEntity getDropOffEntity(List<Route> routes) {
        for (Route route : routes) {
            if ("Море".equals(route.getTransportType()) && "COC".equals(route.getContainerTypeSize())) {
                log.info("Найден морской маршрут с COC: {} -> {}, eqpt: {}", route.getCityFrom(), route.getCityTo(), route.getEqpt());
                DropOffEntity dropOffEntity = dropOffRepository.findByPolAndPodAndSize(route.getPol(), route.getPod(), route.getEqpt());

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

//    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
//    @PostMapping("/calculate")
//    public ResponseEntity<?> saveRoute(@RequestBody RouteSaveDto routeSaveDto) {
//        clearCache();
//        routeService.create(routeSaveDto);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
//
//    // Вывод всех маршрутов для админа
//    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
//    @GetMapping("/all")
//    public ResponseEntity<?> getAll(Pageable pageable, PagedResourcesAssembler<Route> assembler) {
//        Page<Route> routes = routeRepo.findAll(pageable);
//
//        PagedModel<EntityModel<Route>> pagedModel = assembler.toModel(routes);
//
//        return ResponseEntity.ok(pagedModel);
//
//    }

//    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
//    @PatchMapping("/{id}")
//    public Route patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
//        clearCache();
//        Route route = routeRepo.findById(id).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
//
//        // Обновляем перевозчика, если ID передан
//        if (patchNode.has("carrierId")) {
//            Long carrierId = patchNode.get("carrierId").asLong();
//            Carrier newCarrier = carrierService.findById(carrierId)
//                    .orElseThrow(() -> new EntityNotFoundException("Carrier not found"));
//            route.setCarrier(newCarrier);
//        }
//
//        // Обновляем storageAtThePortOfArrivalEntity, если ID передан
//        if (patchNode.has("storageAtThePortOfArrivalEntityId")) {
//            Long portStorageId = patchNode.get("storageAtThePortOfArrivalEntityId").asLong();
//            StorageAtThePortOfArrivalEntity newPortStorage = storageAtThePortOfArrivalRepo.findById(portStorageId)
//                    .orElseThrow(() -> new EntityNotFoundException("Port storage entity not found"));
//            route.setStorageAtThePortOfArrivalEntity(newPortStorage);
//        }
//
//        // Обновляем storageAtTheRailwayOfArrivalEntity, если ID передан
//        if (patchNode.has("storageAtTheRailwayOfArrivalEntityId")) {
//            Long railwayStorageId = patchNode.get("storageAtTheRailwayOfArrivalEntityId").asLong();
//            StorageAtThePortOfArrivalEntity newRailwayStorage = storageAtThePortOfArrivalRepo.findById(railwayStorageId)
//                    .orElseThrow(() -> new EntityNotFoundException("Railway storage entity not found"));
//            route.setStorageAtTheRailwayOfArrivalEntity(newRailwayStorage);
//        }
//
//        // Обновляем остальные поля
//        objectMapper.readerForUpdating(route).readValue(patchNode);
//
//        return routeRepo.save(route);
//    }
//
//    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
//    @DeleteMapping("/{id}")
//    @Transactional
//    public ResponseEntity<?> delete(@PathVariable Long id) {
//        clearCache();
//
//        // Найти маршрут
//        Route route = routeRepo.findById(id).orElse(null);
//        if (route == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Route not found");
//        }
//
//        // Найти заявки, связанные с маршрутом
//        List<Application> applications = applicationRepo.findByRoutesId(route.getId());
//
//        // Удалить связь маршрута с заявками
//        for (Application application : applications) {
//            application.getRoutes().remove(route);
//            applicationRepo.save(application); // Сохранить изменения
//        }
//
//        // Удалить маршрут
//        routeRepo.delete(route);
//
//        return ResponseEntity.ok("Route and related applications have been deleted");
//    }

    // Сохранение маршрутов в формате xlsx
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/excel")
    public ResponseEntity<?> uploadRouteExcel(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) { // Create workbook here
            List<String> errors = new ArrayList<>();
            try {
                routeExcelParserService.saveRoutesFromExcel(workbook, errors); // Pass workbook and errors
                return ResponseEntity.ok("Routes uploaded successfully");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }

    private void clearCache() {
        cacheService.refreshCacheRoute();
    }

}
