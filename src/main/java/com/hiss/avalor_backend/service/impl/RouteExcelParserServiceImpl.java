package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.dto.RouteSaveDto;
import com.hiss.avalor_backend.entity.Carrier;
import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.entity.StorageAtThePortOfArrivalEntity;
import com.hiss.avalor_backend.repo.StorageAtThePortOfArrivalRepo;
import com.hiss.avalor_backend.service.CacheService;
import com.hiss.avalor_backend.service.CarrierService;
import com.hiss.avalor_backend.service.RouteExcelParserService;
import com.hiss.avalor_backend.service.RouteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteExcelParserServiceImpl implements RouteExcelParserService {

    private final CarrierService carrierService;
    private final StorageAtThePortOfArrivalRepo storageAtThePortOfArrivalRepository;
    private final RouteService routeService;
    private final CacheService cacheService;

    @Override
    public List<Route> parseRoutes(XSSFWorkbook workbook, List<String> errors) throws Exception {
        List<Route> routes = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        boolean isHeader = true;

        for (Row row : sheet) {
            if (isHeader) {
                isHeader = false;
                continue;
            }

            try {
                Route route = new Route();

                // Преобразуем данные из ячеек
                route.setCityFrom(getCellValue(row.getCell(0)));
                route.setCityTo(getCellValue(row.getCell(1)));
                route.setTransportType(getCellValue(row.getCell(2)));
                route.setPol(getCellValue(row.getCell(4)));
                route.setPod(getCellValue(row.getCell(5)));
                route.setEqpt(getCellValue(row.getCell(6)));
                route.setContainerTypeSize(getCellValue(row.getCell(7)));
                route.setValidTo(getCellValue(row.getCell(8)));
                route.setFilo(Integer.valueOf(getCellValue(row.getCell(9))));

//                TODO: полностью переделать парсинг excel

//                try {
//                    route.setStorageAtThePortOfArrivalEntity(findStorageAtPort(row.getCell(18)));
//                } catch (RuntimeException e) {
//                    errors.add(String.format("Error in row %d: %s", row.getRowNum() + 1, e.getMessage()));
//                    continue; // Skip to the next row if an entity is not found
//                }
//
//                try {
//                    route.setStorageAtTheRailwayOfArrivalEntity(findStorageAtRailway(row.getCell(19)));
//                } catch (RuntimeException e) {
//                    errors.add(String.format("Error in row %d: %s", row.getRowNum() + 1, e.getMessage()));
//                    continue;
//                }
//
//                try {
//                    route.setCarrier(findCarrier(row.getCell(20)));
//                } catch (EntityNotFoundException e) {
//                    errors.add(String.format("Error in row %d: %s", row.getRowNum() + 1, e.getMessage()));
//                    continue;
//                }

                routes.add(route);
                log.info("Parsed Route: {}", route);
            } catch (Exception e) {
                log.error("Failed to parse row: {}", row.getRowNum(), e);
                // Можно пропустить строку или выбросить исключение в зависимости от ситуации
            }
        }


        log.info("Total routes parsed: {}", routes.size());
        return routes;
    }

    @Override
    @SneakyThrows
    public void saveRoutesFromExcel(XSSFWorkbook workbook, List<String> errors) {
        List<Route> routes = parseRoutes(workbook, errors);

        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            try {
                RouteSaveDto routeSaveDto = convertToDto(route);
                routeService.create(routeSaveDto);
            } catch (Exception e) {
                String errorMessage = String.format("Error saving route from row %d: %s", i + 2, e.getMessage()); // +2 because of header and 0-based indexing
                errors.add(errorMessage);
                log.error(errorMessage, e);
            }
        }

        clearCache();

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

    }

    private RouteSaveDto convertToDto(Route route) {
        return RouteSaveDto.builder().cityFrom(route.getCityFrom()).cityTo(route.getCityTo()).transportType(route.getTransportType()).pol(route.getPol()).pod(route.getPod()).eqpt(route.getEqpt()).containerTypeSize(route.getContainerTypeSize()).validTo(route.getValidTo()).filo(route.getFilo()).build();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        CellType cellType = cell.getCellType();

        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType(); // Получаем тип результата формулы
        }

        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {  // Проверка на формат даты
                    LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // Ваш формат
                    return date.format(formatter);
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula(); // Или результат формулы, если нужно значение
            default:
                return null;
        }
    }

    private StorageAtThePortOfArrivalEntity findStorageAtPort(Cell cell) {
        try {
            if (cell == null || getCellValue(cell).isEmpty()) {
                return null;
            }
            long storagePortId = (long) Double.parseDouble(getCellValue(cell));
            return storageAtThePortOfArrivalRepository.findById(storagePortId).orElseThrow(() -> new RuntimeException("Storage at port not found: " + storagePortId));
        } catch (NumberFormatException e) {
            log.error("Invalid storage port ID format: {}", getCellValue(cell));
            throw e; // Или вернуть null, если требуется пропустить строку
        }
    }

    private StorageAtThePortOfArrivalEntity findStorageAtRailway(Cell cell) {
        try {
            if (cell == null || getCellValue(cell).isEmpty()) {
                return null;
            }
            long storageRailwayId = (long) Double.parseDouble(getCellValue(cell));
            return storageAtThePortOfArrivalRepository.findById(storageRailwayId).orElseThrow(() -> new RuntimeException("Storage at railway not found: " + storageRailwayId));
        } catch (NumberFormatException e) {
            log.error("Invalid storage railway ID format: {}", getCellValue(cell));
            throw e; // Или вернуть null, если требуется пропустить строку
        }
    }

//    private Carrier findCarrier(Cell cell) {
//        try {
//            if (cell == null || getCellValue(cell).isEmpty()) {
//                return null;
//            }
//            long carrierId = (long) Double.parseDouble(getCellValue(cell));
//            return carrierService
//                    .findById(carrierId)
//                    .orElseThrow(() -> new RuntimeException("Carrier not found: " + carrierId));
//        } catch (NumberFormatException e) {
//            log.error("Invalid carrier ID format: {}", getCellValue(cell));
//            throw e; // Или вернуть null, если требуется пропустить строку
//        }
//    }

    private Carrier findCarrier(Cell cell) {
        try {
            if (cell == null || getCellValue(cell).isEmpty()) {
                return null;
            }
            String carrier = getCellValue(cell);
            return carrierService.findByName(carrier).orElseThrow(() -> new EntityNotFoundException("Carrier not found: " + carrier));
        } catch (Exception exception) {
            log.error("Failed to find carrier by name: {}", getCellValue(cell));
            throw exception;
        }
    }

    private void clearCache() {
        cacheService.refreshCacheRoute();
    }

}
