package com.thefileproject.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class FileConversionProperties {

    // Dosya boyutu limitleri
    private Long maxFileSize;
    private Integer maxImageDimension;

    // Desteklenen format listeleri
    private List<String> supportedImageFormats;
    private List<String> supportedPdfFormats;
    private List<String> supportedOfficeFormats;

    // Kalite ayarları
    private Float defaultImageQuality;
    private Float minImageQuality;
    private Float maxImageQuality;

    // Timeout ve performans ayarları
    private Long conversionTimeoutMs;

    // Temporary dosya ayarları
    private String tempFilePrefix;
    private Boolean cleanupTempFiles;

    /**
     * Format destek kontrolü metodları
     */
    public boolean isImageFormatSupported(String format) {
        return supportedImageFormats.contains(format.toLowerCase().trim());
    }

    public boolean isPdfFormatSupported(String format) {
        return supportedPdfFormats.contains(format.toLowerCase().trim());
    }

    public boolean isOfficeFormatSupported(String format) {
        return supportedOfficeFormats.contains(format.toLowerCase().trim());
    }

    public boolean isFormatSupported(String format) {
        if (format == null || format.trim().isEmpty()) {
            return false;
        }

        return isImageFormatSupported(format) ||
                isPdfFormatSupported(format) ||
                isOfficeFormatSupported(format);
    }

    /**
     * Dosya boyutu validasyonu
     */
    public boolean isFileSizeValid(long sizeInBytes) {
        return sizeInBytes > 0 && sizeInBytes <= maxFileSize;
    }

    /**
     * Kalite değeri validasyonu
     */
    public boolean isQualityValid(Float quality) {
        return quality != null &&
                quality >= minImageQuality &&
                quality <= maxImageQuality;
    }

    /**
     * Görüntü boyutu validasyonu
     */
    public boolean isImageDimensionValid(Integer width, Integer height) {
        return width != null && height != null &&
                width > 0 && height > 0 &&
                width <= maxImageDimension && height <= maxImageDimension;
    }

    /**
     * Tüm desteklenen formatları döner
     */
    public List<String> getAllSupportedFormats() {
        return List.of(
                        supportedImageFormats,
                        supportedPdfFormats,
                        supportedOfficeFormats
                ).stream()
                .flatMap(List::stream)
                .distinct()
                .sorted()
                .toList();
    }
}