package com.thefileproject.service;


import com.thefileproject.converter.FileConverter;
import com.thefileproject.dto.ConversionResponse;
import com.thefileproject.exception.UnsupportedConversionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileConversionServiceImpl implements FileConversionService {

    private final List<FileConverter> converters;

    @Override
    public ConversionResponse convertFile(MultipartFile file, String targetFormat) {
        if (file.isEmpty()) {
            return ConversionResponse.error("File is empty", file.getOriginalFilename(), "", targetFormat);
        }

        String originalFilename = file.getOriginalFilename();
        String sourceFormat = extractFileExtension(originalFilename);

        if (sourceFormat.isEmpty()) {
            return ConversionResponse.error("Unable to determine source file format",
                    originalFilename, "", targetFormat);
        }

        log.info("Converting file '{}' from {} to {}", originalFilename, sourceFormat, targetFormat);

        try {
            FileConverter converter = findConverter(sourceFormat, targetFormat);

            if (converter == null) {
                throw new UnsupportedConversionException(sourceFormat, targetFormat);
            }

            byte[] convertedData = converter.convert(file.getInputStream(), sourceFormat, targetFormat);

            log.info("Successfully converted file '{}' from {} to {} (size: {} bytes)",
                    originalFilename, sourceFormat, targetFormat, convertedData.length);

            return ConversionResponse.success(convertedData, originalFilename, sourceFormat, targetFormat);

        } catch (UnsupportedConversionException e) {
            log.warn("Unsupported conversion: {}", e.getMessage());
            return ConversionResponse.error(e.getMessage(), originalFilename, sourceFormat, targetFormat);

        } catch (Exception e) {
            log.error("Error converting file '{}': {}", originalFilename, e.getMessage(), e);
            return ConversionResponse.error(
                    "File conversion failed: " + e.getMessage(),
                    originalFilename, sourceFormat, targetFormat);
        }
    }

    @Override
    public boolean isConversionSupported(String sourceFormat, String targetFormat) {
        return findConverter(sourceFormat, targetFormat) != null;
    }

    @Override
    public Set<String> getSupportedSourceFormats() {
        return converters.stream()
                .flatMap(converter -> getAllSupportedFormats(converter).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getSupportedTargetFormats(String sourceFormat) {
        return converters.stream()
                .filter(converter -> supportsAsSource(converter, sourceFormat))
                .flatMap(converter -> getAllSupportedFormats(converter).stream())
                .filter(format -> !format.equalsIgnoreCase(sourceFormat))
                .collect(Collectors.toSet());
    }

    private FileConverter findConverter(String sourceFormat, String targetFormat) {
        return converters.stream()
                .filter(converter -> converter.isSupported(sourceFormat, targetFormat))
                .max(Comparator.comparing(FileConverter::getPriority))
                .orElse(null);
    }

    private String extractFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }

        return "";
    }

    private Set<String> getAllSupportedFormats(FileConverter converter) {
        // This is a simplified approach - in practice, you might want to define
        // supported formats more explicitly in each converter
        Set<String> commonFormats = Set.of("jpg", "jpeg", "png", "bmp", "gif", "pdf", "docx", "xlsx", "pptx");

        return commonFormats.stream()
                .filter(format -> commonFormats.stream()
                        .anyMatch(otherFormat -> converter.isSupported(format, otherFormat) ||
                                converter.isSupported(otherFormat, format)))
                .collect(Collectors.toSet());
    }

    private boolean supportsAsSource(FileConverter converter, String sourceFormat) {
        Set<String> commonFormats = Set.of("jpg", "jpeg", "png", "bmp", "gif", "pdf", "docx", "xlsx", "pptx");

        return commonFormats.stream()
                .anyMatch(targetFormat -> converter.isSupported(sourceFormat, targetFormat));
    }
}