package com.thefileproject.controller;

import com.thefileproject.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/convert/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/to-png")
    public ResponseEntity<Resource> convertToPng(@RequestParam("file") MultipartFile file) {
        log.info("Received request to convert {} to PNG", file.getOriginalFilename());

        byte[] convertedImage = imageService.convertToPng(file);
        String outputFilename = getFileNameWithoutExtension(file.getOriginalFilename()) + ".png";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + outputFilename + "\"")
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(convertedImage.length)
                .body(new ByteArrayResource(convertedImage));
    }

    @PostMapping("/to-jpg")
    public ResponseEntity<Resource> convertToJpg(@RequestParam("file") MultipartFile file) {
        log.info("Received request to convert {} to JPG", file.getOriginalFilename());

        byte[] convertedImage = imageService.convertToJpg(file);
        String outputFilename = getFileNameWithoutExtension(file.getOriginalFilename()) + ".jpg";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + outputFilename + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(convertedImage.length)
                .body(new ByteArrayResource(convertedImage));
    }

    @PostMapping("/to-webp")
    public ResponseEntity<Resource> convertToWebp(@RequestParam("file") MultipartFile file) {
        log.info("Received request to convert {} to WEBP", file.getOriginalFilename());

        byte[] convertedImage = imageService.convertToWebp(file);
        String outputFilename = getFileNameWithoutExtension(file.getOriginalFilename()) + ".webp";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + outputFilename + "\"")
                .contentType(MediaType.parseMediaType("image/webp"))
                .contentLength(convertedImage.length)
                .body(new ByteArrayResource(convertedImage));
    }

    @PostMapping("/resize")
    public ResponseEntity<Resource> resizeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam(value = "keepAspectRatio", defaultValue = "true") boolean keepAspectRatio) {

        log.info("Received request to resize {} to {}x{}", file.getOriginalFilename(), width, height);

        byte[] resizedImage = imageService.resizeImage(file, width, height, keepAspectRatio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resized_" + file.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(resizedImage.length)
                .body(new ByteArrayResource(resizedImage));
    }

    @PostMapping("/compress")
    public ResponseEntity<Resource> compressImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "quality", defaultValue = "0.8") float quality) {

        log.info("Received request to compress {} with quality {}", file.getOriginalFilename(), quality);

        if (quality < 0.1 || quality > 1.0) {
            throw new IllegalArgumentException("Quality must be between 0.1 and 1.0");
        }

        byte[] compressedImage = imageService.compressImage(file, quality);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"compressed_" + file.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(compressedImage.length)
                .body(new ByteArrayResource(compressedImage));
    }

    private String getFileNameWithoutExtension(String filename) {
        if (filename == null) return "converted";
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? filename : filename.substring(0, lastDotIndex);
    }
}