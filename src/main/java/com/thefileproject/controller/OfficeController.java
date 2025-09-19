package com.thefileproject.controller;

import com.thefileproject.service.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/convert/office")
@RequiredArgsConstructor
public class OfficeController {

    private final OfficeService officeService;

    @PostMapping("/pdf-to-docx")
    public ResponseEntity<byte[]> convertPdfToDocx(@RequestParam("file") MultipartFile file) {
        byte[] converted = officeService.convertPdfToDocx(file);
        String filename = officeService.buildOutputFileName(file.getOriginalFilename(), "docx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(converted);
    }

    @PostMapping("/docx-to-xlsx")
    public ResponseEntity<byte[]> convertDocxToXlsx(@RequestParam("file") MultipartFile file) {
        byte[] converted = officeService.convertDocxToXlsx(file);
        String filename = officeService.buildOutputFileName(file.getOriginalFilename(), "xlsx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(converted);
    }

    @PostMapping("/xlsx-to-docx")
    public ResponseEntity<byte[]> convertXlsxToDocx(@RequestParam("file") MultipartFile file) {
        byte[] converted = officeService.convertXlsxToDocx(file);
        String filename = officeService.buildOutputFileName(file.getOriginalFilename(), "docx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(converted);
    }
}
