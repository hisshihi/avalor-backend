package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.entity.Route;

import java.io.InputStream;
import java.util.List;

public interface RouteExcelParserService {

    List<Route> parseRoutes(InputStream excelInputStream) throws Exception;

    void saveRoutesFromExcel(InputStream excelInputStream);

}
