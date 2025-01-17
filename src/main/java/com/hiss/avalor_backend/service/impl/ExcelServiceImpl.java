package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.entity.*;
import com.hiss.avalor_backend.repo.*;
import com.hiss.avalor_backend.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelServiceImpl implements ExcelService {

    private final RouteRailwayRepository routeRailwayRepository;
    private final RouteSeaRepository routeSeaRepository;
    private final RouteAutoRepository routeAutoRepository;
    private final DropOffRepository dropOffRepository;
    private final RentRepository rentRepository;

    @Override
    @Async("asyncTaskExecutor")
    @SneakyThrows
    public void saveRouteRailwayFromExcel(XSSFWorkbook workbook, List<String> errors) {
        List<RouteRailway> routes = parseRailwayRoutes(workbook, errors);
        for (int i = 0; i < routes.size(); i++) {
            RouteRailway route = routes.get(i);
            try {
                routeRailwayRepository.save(route);
            } catch (Exception e) {
                String errorMessage = String.format("Error saving railway route from row %d: %s", i + 2, e.getMessage());
                errors.add(errorMessage);
                log.error(errorMessage, e);
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }
    }

    @Override
    @Async("asyncTaskExecutor")
    @SneakyThrows
    public void saveRouteSeaFromExcel(XSSFWorkbook workbook, List<String> errors) {
        List<RouteSea> routes = parseSeaRoutes(workbook, errors);

        for (int i = 0; i < routes.size(); i++) {
            RouteSea route = routes.get(i);
            try {
                routeSeaRepository.save(route);
            } catch (Exception e) {
                String errorMessage = String.format("Error saving sea route from row %d: %s", i + 2, e.getMessage());
                errors.add(errorMessage);
                log.error(errorMessage, e);
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }
    }

    @Override
    @Async("asyncTaskExecutor")
    @SneakyThrows
    public void saveRouteAutoFromExcel(XSSFWorkbook workbook, List<String> errors) {
        List<RouteAuto> routes = parseAutoRoutes(workbook, errors);

        for (int i = 0; i < routes.size(); i++) {
            RouteAuto route = routes.get(i);
            try {
                routeAutoRepository.save(route);
            } catch (Exception e) {
                String errorMessage = String.format("Error saving auto route from row %d: %s", i + 2, e.getMessage());
                errors.add(errorMessage);
                log.error(errorMessage, e);
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }
    }

    @Override
    @Async("asyncTaskExecutor")
    @SneakyThrows
    public void saveDropOffFromExcel(XSSFWorkbook workbook, List<String> errors) {
        List<DropOffEntity> dropOffs = parseDropOffs(workbook, errors);
        for (DropOffEntity dropOff : dropOffs) {
            try {
                dropOffRepository.save(dropOff);
            } catch (Exception e) {
                errors.add(String.format("Error saving drop-off: %s", e.getMessage()));
                log.error("Error saving drop-off", e);
            }
        }
    }

    @Override
    @Async("asyncTaskExecutor")
    @SneakyThrows
    public void saveRentFromExcel(XSSFWorkbook workbook, List<String> errors) {
        List<RentEntity> rents = parseRents(workbook, errors);
        for (RentEntity rent : rents) {
            try {
                rentRepository.save(rent);
            } catch (Exception e) {
                errors.add(String.format("Error saving rent: %s", e.getMessage()));
                log.error("Error saving rent", e);
            }
        }
    }

    private List<RouteRailway> parseRailwayRoutes(XSSFWorkbook workbook, List<String> errors) {
        return parseRoutes(workbook, errors, RouteRailway.class);
    }

    private List<RouteSea> parseSeaRoutes(XSSFWorkbook workbook, List<String> errors) {
        return parseRoutes(workbook, errors, RouteSea.class);
    }

    private List<RouteAuto> parseAutoRoutes(XSSFWorkbook workbook, List<String> errors) {
        return parseRoutes(workbook, errors, RouteAuto.class);
    }

    private List<DropOffEntity> parseDropOffs(XSSFWorkbook workbook, List<String> errors) {
        return parseRoutes(workbook, errors, DropOffEntity.class);
    }

    private List<RentEntity> parseRents(XSSFWorkbook workbook, List<String> errors) {
        return parseRoutes(workbook, errors, RentEntity.class);
    }

    private <T> List<T> parseRoutes(XSSFWorkbook workbook, List<String> errors, Class<T> routeClass) {
        List<T> routes = new ArrayList<>();
        XSSFSheet sheet = workbook.getSheetAt(0);

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null) continue;

            try {
                T route = routeClass.getDeclaredConstructor().newInstance();
                if (route instanceof RouteRailway railway) {
                    railway.setCityFrom(getCellValue(row.getCell(0)));
                    railway.setCityTo(getCellValue(row.getCell(1)));
                    railway.setPol(getCellValue(row.getCell(2)));
                    railway.setPod(getCellValue(row.getCell(3)));
                    railway.setCarrier(getCellValue(row.getCell(4)));
                    railway.setValidTo(getCellValue(row.getCell(5)));
                    railway.setTransportType("ЖД");
                    railway.setFilo20(Integer.parseInt(getCellValue(row.getCell(6))));
                    railway.setFilo20HC(Integer.parseInt(getCellValue(row.getCell(7))));
                    railway.setFilo40(Integer.parseInt(getCellValue(row.getCell(8))));
                    railway.setExclusive(Integer.parseInt(getCellValue(row.getCell(9))));
                    railway.setFilo20D(Integer.parseInt(getCellValue(row.getCell(10))));
                    railway.setFilo20HCD(Integer.parseInt(getCellValue(row.getCell(11))));
                    railway.setFilo40D(Integer.parseInt(getCellValue(row.getCell(12))));
                    routes.add(route);
                } else if (route instanceof RouteSea sea) {
                    sea.setCityFrom(getCellValue(row.getCell(0)));
                    sea.setCityTo(getCellValue(row.getCell(1)));
                    sea.setPol(getCellValue(row.getCell(2)));
                    sea.setPod(getCellValue(row.getCell(3)));
                    sea.setCarrier(getCellValue(row.getCell(4)));
                    sea.setValidTo(getCellValue(row.getCell(5)));
                    sea.setTransportType("Море");
                    sea.setEqpt(getCellValue(row.getCell(6)));
                    sea.setContainerTypeSize(getCellValue(row.getCell(7)));
                    sea.setFilo(Integer.parseInt(getCellValue(row.getCell(8))));
                    sea.setExclusive(Integer.parseInt(getCellValue(row.getCell(9))));
                    sea.setFiloD(Integer.parseInt(getCellValue(row.getCell(10))));
                    routes.add(route);
                } else if (route instanceof RouteAuto auto) {
                    auto.setCityFrom(getCellValue(row.getCell(0)));
                    auto.setCityTo(getCellValue(row.getCell(1)));
                    auto.setPol(getCellValue(row.getCell(2)));
                    auto.setPod(getCellValue(row.getCell(3)));
                    auto.setCarrier(getCellValue(row.getCell(4)));
                    auto.setValidTo(getCellValue(row.getCell(5)));
                    auto.setTransportType("ЖД");
                    auto.setFilo20(Integer.parseInt(getCellValue(row.getCell(6))));
                    auto.setFilo20HC(Integer.parseInt(getCellValue(row.getCell(7))));
                    auto.setFilo40(Integer.parseInt(getCellValue(row.getCell(8))));
                    auto.setExclusive(Integer.parseInt(getCellValue(row.getCell(9))));
                    routes.add(route);
                } else if (route instanceof DropOffEntity dropOff) {
                    dropOff.setPol(getCellValue(row.getCell(0))); // Поле POL
                    dropOff.setPod(getCellValue(row.getCell(1))); // Поле POD
                    dropOff.setCarrier(getCellValue(row.getCell(2))); // Перевозчик
                    dropOff.setValidTo(getCellValue(row.getCell(3))); // Дата
                    dropOff.setFilo(Integer.parseInt(getCellValue(row.getCell(4)))); // FILO
                    dropOff.setSize(getCellValue(row.getCell(5))); // Размер
                    dropOff.setFiloD(Integer.parseInt(getCellValue(row.getCell(6)))); // Filo dollar
                    routes.add(route);
                } else if (route instanceof RentEntity rent) {
                    rent.setPol(getCellValue(row.getCell(0))); // Поле POL
                    rent.setPod(getCellValue(row.getCell(1))); // Поле POD
                    rent.setCarrier(getCellValue(row.getCell(2))); // Перевозчик
                    rent.setSize(getCellValue(row.getCell(3))); // Размер
                    rent.setValidTo(getCellValue(row.getCell(4))); // Дата
                    rent.setFilo(Integer.parseInt(getCellValue(row.getCell(5)))); // FILO
                    rent.setFiloD(Integer.parseInt(getCellValue(row.getCell(6)))); // FILO dollar
                    routes.add(route);
                }
            } catch (Exception e) {
                String errorMessage = String.format("Error parsing route from row %d: %s", rowIndex + 1, e.getMessage());
                errors.add(errorMessage);
                log.error(errorMessage, e);
            }
        }
        return routes;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        CellType cellType = cell.getCellType();

        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }

        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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
            default:
                return null;
        }
    }

}
