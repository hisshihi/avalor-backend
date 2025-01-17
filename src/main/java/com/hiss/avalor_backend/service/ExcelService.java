package com.hiss.avalor_backend.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface ExcelService {
    void saveRouteRailwayFromExcel(XSSFWorkbook workbook, List<String> errors);
    void saveRouteSeaFromExcel(XSSFWorkbook workbook, List<String> errors);
    void saveRouteAutoFromExcel(XSSFWorkbook workbook, List<String> errors);
    void saveDropOffFromExcel(XSSFWorkbook workbook, List<String> errors);
    void saveRentFromExcel(XSSFWorkbook workbook, List<String> errors);
}
