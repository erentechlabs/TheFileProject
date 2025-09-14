package com.thefileproject.converter;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Set;

@Component
public class ImageConverter implements FileConverter {

    private static final Set<String> SUPPORTED_FORMATS = Set.of(
            "jpg", "jpeg", "png", "bmp", "gif", "webp"
    );

    @Override
    public byte[] convert(InputStream inputStream, String sourceFormat, String targetFormat) throws Exception {
        if (!isSupported(sourceFormat, targetFormat)) {
            throw new IllegalArgumentException(
                    String.format("Conversion from %s to %s is not supported by ImageConverter",
                            sourceFormat, targetFormat));
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Use Thumbnailator to convert between image formats
            // Scale to 1.0 means no resizing, just format conversion
            Thumbnails.of(inputStream)
                    .scale(1.0)
                    .outputFormat(targetFormat.toLowerCase())
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }

    @Override
    public boolean isSupported(String sourceFormat, String targetFormat) {
        return SUPPORTED_FORMATS.contains(sourceFormat.toLowerCase()) &&
                SUPPORTED_FORMATS.contains(targetFormat.toLowerCase()) &&
                !sourceFormat.equalsIgnoreCase(targetFormat);
    }

    @Override
    public int getPriority() {
        return 10; // High priority for image conversions
    }

    /**
     * Convert with custom quality (for JPEG)
     */
    public byte[] convertWithQuality(InputStream inputStream, String sourceFormat,
                                     String targetFormat, float quality) throws Exception {
        if (!isSupported(sourceFormat, targetFormat)) {
            throw new IllegalArgumentException("Conversion not supported");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            var builder = Thumbnails.of(inputStream).scale(1.0);

            if ("jpg".equalsIgnoreCase(targetFormat) || "jpeg".equalsIgnoreCase(targetFormat)) {
                builder.outputQuality(quality);
            }

            builder.outputFormat(targetFormat.toLowerCase())
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }

    /**
     * Convert with resizing
     */
    public byte[] convertWithResize(InputStream inputStream, String sourceFormat,
                                    String targetFormat, int width, int height) throws Exception {
        if (!isSupported(sourceFormat, targetFormat)) {
            throw new IllegalArgumentException("Conversion not supported");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputFormat(targetFormat.toLowerCase())
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }
}