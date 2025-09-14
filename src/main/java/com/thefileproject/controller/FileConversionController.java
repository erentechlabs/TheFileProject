package com.thefileproject.controller;


import com.thefileproject.dto.ConversionResponse;
import com.thefileproject.service.FileConversionService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

/**
 * REST Controller for file conversion operations
 */
@RestController
@RequestMapping("/api/convert")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Configure appropriately for production
public class FileConversionController {

    private final FileConversionService fileConversionService;

    /**
     * Convert a file to the specified target format
     */
    @PostMapping("/{targetFormat}")
    public ResponseEntity<?> convertFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable @NotBlank String targetFormat) {

        log.info("Received conversion request: {} -> {}",
                file.getOriginalFilename(), targetFormat);

        // Basic validation
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No file provided"));
        }

        if (targetFormat.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Target format is required"));
        }

        ConversionResponse response = fileConversionService.convertFile(file, targetFormat.trim());

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", response.getErrorMessage(),
                            "sourceFormat", response.getSourceFormat(),
                            "targetFormat", response.getTargetFormat(),
                            "originalFilename", response.getOriginalFilename()
                    ));
        }

        // Return the converted file
        HttpHeaders headers = getHttpHeaders(response);

        return ResponseEntity.ok()
                .headers(headers)
                .body(response.getFileData());
    }

    private static HttpHeaders getHttpHeaders(ConversionResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", response.getConvertedFilename());
        headers.setContentLength(response.getFileSizeBytes());

        // Add custom headers with conversion info
        headers.add("X-Original-Format", response.getSourceFormat());
        headers.add("X-Target-Format", response.getTargetFormat());
        headers.add("X-Original-Filename", response.getOriginalFilename());
        return headers;
    }

    /**
     * Check if a specific conversion is supported
     */
    @GetMapping("/supports/{sourceFormat}/{targetFormat}")
    public ResponseEntity<Map<String, Object>> checkConversionSupport(
            @PathVariable String sourceFormat,
            @PathVariable String targetFormat) {

        boolean supported = fileConversionService.isConversionSupported(sourceFormat, targetFormat);

        return ResponseEntity.ok(Map.of(
                "supported", supported,
                "sourceFormat", sourceFormat,
                "targetFormat", targetFormat
        ));
    }

    /**
     * Get all supported source formats
     */
    @GetMapping("/formats/source")
    public ResponseEntity<Map<String, Set<String>>> getSupportedSourceFormats() {
        Set<String> formats = fileConversionService.getSupportedSourceFormats();
        return ResponseEntity.ok(Map.of("supportedSourceFormats", formats));
    }

    /**
     * Get supported target formats for a specific source format
     */
    @GetMapping("/formats/target/{sourceFormat}")
    public ResponseEntity<Map<String, Object>> getSupportedTargetFormats(
            @PathVariable String sourceFormat) {

        Set<String> formats = fileConversionService.getSupportedTargetFormats(sourceFormat);

        return ResponseEntity.ok(Map.of(
                "sourceFormat", sourceFormat,
                "supportedTargetFormats", formats
        ));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "TheFileProject Conversion API"
        ));
    }

    /**
     * Get API information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        return ResponseEntity.ok(Map.of(
                "name", "TheFileProject Conversion API",
                "version", "1.0.0",
                "description", "REST API for file format conversion",
                "supportedOperations", Map.of(
                        "imageConversion", "PNG, JPG, JPEG, BMP, GIF, WEBP",
                        "pdfConversion", "PDF to various formats (coming soon)",
                        "officeConversion", "Office documents (coming soon)"
                ),
                "endpoints", Map.of(
                        "convert", "POST /api/convert/{targetFormat}",
                        "checkSupport", "GET /api/convert/supports/{sourceFormat}/{targetFormat}",
                        "sourceFormats", "GET /api/convert/formats/source",
                        "targetFormats", "GET /api/convert/formats/target/{sourceFormat}"
                )
        ));
    }

    /**
     * Global exception handler for this controller
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Unexpected error in file conversion", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal server error occurred",
                        "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
                ));
    }
}