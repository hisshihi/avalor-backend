package com.hiss.avalor_backend.controller;

import com.hiss.avalor_backend.dto.DocumentContactDto;
import com.hiss.avalor_backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

//    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
//    @GetMapping
//    public ResponseEntity<?> listAllDocuments(@RequestBody DocumentContactDto contactDto) {
//        try {
//            XWPFDocument document = documentService.fillTemplateAndSendToFont(contactDto);
//
//            File tempFile = File.createTempFile("generated_document", ".docx");
//            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                document.write(fos);
//            }
//
//            document.close();
//
//            Resource resource = new FileSystemResource(tempFile);
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Заполненный_договор.docx")
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(resource);
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при отправке файла: " + e.getMessage());
//        }
//    }

//    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
//    @GetMapping
//    public ResponseEntity<Resource> listAllDocuments(@RequestBody DocumentContactDto contactDto) {
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {  // Для отправки клиенту
//            XWPFDocument document = documentService.fillTemplateAndSendToFont(contactDto);
//
//            document.write(baos); // Записываем в ByteArrayOutputStream для клиента
//            ByteArrayResource byteArrayResource = new ByteArrayResource(baos.toByteArray());
//
//            // Сохраняем в resources/templates
//            String filename = "Заполненный_договор_" + System.currentTimeMillis() + ".docx"; // Уникальное имя файла
//            String filePath = "src/main/resources/templates/" + filename; // Указываем resources/templates
//            try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                document.write(fos); // Записываем в файл в resources/templates
//            }
//            document.close(); // Закрываем документ
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + UriUtils.encode(filename, StandardCharsets.UTF_8))
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(byteArrayResource); // Отправляем клиенту данные из ByteArrayResource
//
//        } catch (IOException e) {
//            log.error("Ошибка при обработке документа", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    // TODO: убрать локальное сохранение
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping
    public ResponseEntity<Resource> generateDocument(@RequestBody DocumentContactDto contactDto) {
        log.info(contactDto.toString());
        try {
            byte[] documentData = documentService.fillTemplateAndSendToFont(contactDto);
            String uploadDir = "src/main/resources/templates/"; // Настроить путь для сохранения файлов
            String filename = "Заполненный_договор_" + System.currentTimeMillis() + ".docx";
            File file = new File(uploadDir, filename);
            Files.write(file.toPath(), documentData);

            Resource fileResource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + UriUtils.encode(filename, StandardCharsets.UTF_8))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileResource);
        } catch (IOException e) {
            log.error("Ошибка при генерации документа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
//    @GetMapping("/save")
//    public ResponseEntity<Resource> listAllDocumentsSave(@RequestBody DocumentContactDto contactDto) {
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            XWPFDocument document = documentService.fillTemplateAndSendToFont(contactDto);
//            document.write(baos);
//            document.close(); // Обязательно закрывайте документ после записи
//
//            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
//
//            String encodedFilename = "UTF-8''" + URLEncoder.encode("Заполненный_договор.docx", StandardCharsets.UTF_8);
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"") // Изменено здесь
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(resource);
//        } catch (IOException e) {
//            log.error("Ошибка при создании документа", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

}
