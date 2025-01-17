package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.dto.DocumentContactDto;
import com.hiss.avalor_backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private InputStream loadTemplate(String templateName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("templates/" + templateName);
        return classPathResource.getInputStream();
    }

    @Override
    public byte[] fillTemplateAndSendToFont(DocumentContactDto dto) throws IOException {
        String templateName = "Договор_АВАЛОГ_мультимодальный_длябека.docx";

        try (InputStream templateStream = loadTemplate(templateName);
             XWPFDocument document = new XWPFDocument(templateStream)) {

            // Подготовка плейсхолдеров
            Map<String, String> placeholders = preparePlaceholders(dto);

            // Обработка параграфов
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, placeholders);
            }

            // Обработка таблиц
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            replacePlaceholdersInParagraph(paragraph, placeholders);
                        }
                    }
                }
            }

            // Сохранение документа в байтовый массив
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    private Map<String, String> preparePlaceholders(DocumentContactDto dto) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{{rndnumber}}", safeValue(generateNumber()));
        placeholders.put("{{typecompanyandname}}", safeValue(dto.getTypecompanyandname()));
        placeholders.put("{{sname}}", safeValue(dto.getSname()));
        placeholders.put("{{username}}", safeValue(dto.getUsername()));
        placeholders.put("{{condition}}", safeValue(dto.getCondition()));
        placeholders.put("{{INN1}}", safeValue(dto.getINN1()));
        placeholders.put("{{fulladress1}}", safeValue(dto.getFulladress1()));
        placeholders.put("{{INN2}}", safeValue(dto.getINN2()));
        placeholders.put("{{fulladress2}}", safeValue(dto.getFulladress2()));
        placeholders.put("{{INN/KPP}}", safeValue(dto.getInnKpp()));
        placeholders.put("{{OGRN}}", safeValue(dto.getOGRN()));
        placeholders.put("{{R/S}}", safeValue(dto.getRS1()));
        placeholders.put("{{K/S}}", safeValue(dto.getKS2()));
        placeholders.put("{{BIK}}", safeValue(dto.getBIK()));
        placeholders.put("{{fullnamebank}}", safeValue(dto.getFullnamebank()));
        placeholders.put("{{telephone}}", safeValue(dto.getTelephone()));
        placeholders.put("{{email}}", safeValue(dto.getEmail()));
        return placeholders;
    }

    private String safeValue(String value) {
        return value != null ? value : "";
    }

    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        // Сбор текста из всех Run
        StringBuilder paragraphText = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getText(0) != null) {
                paragraphText.append(run.getText(0));
            }
        }

        String originalText = paragraphText.toString();
        String replacedText = originalText;

        // Замена текста
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            replacedText = replacedText.replace(entry.getKey(), entry.getValue());
        }

        if (!originalText.equals(replacedText)) {
            // Удаление старых Runs
            for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // Создание нового Run с замененным текстом
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(replacedText);
        }
    }

    private String generateNumber() {
        String uniqueId = String.format("%08d", System.currentTimeMillis() % 1000000);
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        return uniqueId + "/" + currentMonth;
    }

}


