package com.thefileproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversionResponse {
    private byte[] fileData;
    private String originalFilename;
    private String convertedFilename;
    private String sourceFormat;
    private String targetFormat;
    private long fileSizeBytes;
    private boolean success;
    private String errorMessage;

    public static ConversionResponse success(byte[] fileData, String originalFilename,
                                             String sourceFormat, String targetFormat) {
        String convertedFilename = generateConvertedFilename(originalFilename, targetFormat);

        return ConversionResponse.builder()
                .fileData(fileData)
                .originalFilename(originalFilename)
                .convertedFilename(convertedFilename)
                .sourceFormat(sourceFormat.toLowerCase())
                .targetFormat(targetFormat.toLowerCase())
                .fileSizeBytes(fileData.length)
                .success(true)
                .build();
    }

    public static ConversionResponse error(String errorMessage, String originalFilename,
                                           String sourceFormat, String targetFormat) {
        return ConversionResponse.builder()
                .originalFilename(originalFilename)
                .sourceFormat(sourceFormat)
                .targetFormat(targetFormat)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    private static String generateConvertedFilename(String originalFilename, String targetFormat) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "converted." + targetFormat.toLowerCase();
        }

        int lastDotIndex = originalFilename.lastIndexOf('.');
        String nameWithoutExtension = lastDotIndex > 0 ?
                originalFilename.substring(0, lastDotIndex) : originalFilename;

        return nameWithoutExtension + "." + targetFormat.toLowerCase();
    }
}