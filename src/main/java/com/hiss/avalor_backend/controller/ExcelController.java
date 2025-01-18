package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/railway")
    public ResponseEntity<?> uploadRailwayExcel(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveRouteRailwayFromExcel);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/sea")
    public ResponseEntity<?> uploadSeaExcel(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveRouteSeaFromExcel);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/auto")
    public ResponseEntity<?> uploadAutoExcel(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveRouteAutoFromExcel);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping("/drop-off")
    public ResponseEntity<?> uploadDropOffExcel(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveDropOffFromExcel);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping("/rent")
    public ResponseEntity<?> uploadRentExcel(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveRentFromExcel);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/excessive")
    public ResponseEntity<?> uploadExcessiveExcel(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveExcessiveFromExcel);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/storage")
    public ResponseEntity<?> uploadStorage(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveStorageFromExcel);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/additional")
    public ResponseEntity<?> uploadAdditionalExcel(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveAdditionalServiceAtThePortFromExcel);
    }

    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @PostMapping("/schedule")
    public ResponseEntity<?> uploadScheduleExcel(@RequestParam("file") MultipartFile file) {
        return handleExcelUpload(file, excelService::saveSchedule);
    }

    private ResponseEntity<?> handleExcelUpload(MultipartFile file, BiConsumer<XSSFWorkbook, List<String>> serviceMethod) {
        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            List<String> errors = new ArrayList<>();
            try {
                serviceMethod.accept(workbook, errors);
                return ResponseEntity.ok("Routes uploaded successfully");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }
}

