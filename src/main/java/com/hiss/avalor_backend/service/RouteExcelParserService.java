package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.Route;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface RouteExcelParserService {

    List<Route> parseRoutes(XSSFWorkbook workbook, List<String> errors) throws Exception;

    void saveRoutesFromExcel(XSSFWorkbook workbook, List<String> errors);

}
