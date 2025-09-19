package com.thefileproject.controller;

import com.thefileproject.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/convert/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdfBytes, String originalName) {
        String outputFileName = pdfService.buildOutputFileName(originalName, "pdf");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + outputFileName + "\"")
                .body(pdfBytes);
    }


    @PostMapping(value = "/txt-to-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> txtToPdf(@RequestParam("file") MultipartFile file) {
        byte[] pdf = pdfService.convertTxtToPdf(file);
        return buildPdfResponse(pdf, file.getOriginalFilename());
    }


    @PostMapping(value = "/docx-to-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> docxToPdf(@RequestParam("file") MultipartFile file) {
        byte[] pdf = pdfService.convertDocxToPdf(file);
        return buildPdfResponse(pdf, file.getOriginalFilename());
    }


    @PostMapping(value = "/xlsx-to-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> xlsxToPdf(@RequestParam("file") MultipartFile file) {
        byte[] pdf = pdfService.convertXlsxToPdf(file);
        return buildPdfResponse(pdf, file.getOriginalFilename());
    }


    @PostMapping(value = "/xls-to-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> xlsToPdf(@RequestParam("file") MultipartFile file) {
        byte[] pdf = pdfService.convertXlsToPdf(file);
        return buildPdfResponse(pdf, file.getOriginalFilename());
    }


    @PostMapping(value = "/pdf-to-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> pdfToPdf(@RequestParam("file") MultipartFile file) {
        byte[] pdf = pdfService.convertPdfToPdf(file);
        return buildPdfResponse(pdf, file.getOriginalFilename());
    }
}
