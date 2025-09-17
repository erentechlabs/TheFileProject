package com.thefileproject.service;
import com.thefileproject.exception.custom_exception_classes.FileConversionException;
import com.thefileproject.exception.custom_exception_classes.FileTypeNotSupportedException;
import com.thefileproject.exception.custom_exception_classes.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.io.InputStream;


@Slf4j
@Service
public class ImageService {

    private static final List<String> SUPPORTED_IMAGE_FORMATS =
            Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    public byte[] convertToPng(MultipartFile file) {
        validateImageFile(file);

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                throw new InvalidFileException("Cannot read image from file: " + file.getOriginalFilename());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);

            log.info("Successfully converted {} to PNG", file.getOriginalFilename());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Failed to convert image to PNG: {}", e.getMessage());
            throw new FileConversionException("Failed to convert image to PNG", e);
        }
    }

    public byte[] convertToJpg(MultipartFile file) {
        validateImageFile(file);

        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (originalImage == null) {
                throw new InvalidFileException("Cannot read image from file: " + file.getOriginalFilename());
            }

            // Create a new BufferedImage with RGB type for JPG (no transparency)
            BufferedImage rgbImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // Draw the original image onto the RGB image
            rgbImage.createGraphics().drawImage(originalImage, 0, 0, null);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(rgbImage, "jpg", outputStream);

            log.info("Successfully converted {} to JPG", file.getOriginalFilename());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Failed to convert image to JPG: {}", e.getMessage());
            throw new FileConversionException("Failed to convert image to JPG", e);
        }
    }

    public byte[] convertToWebp(MultipartFile file) {
        validateImageFile(file);

        try {
            // Note: Java doesn't have native WebP support, using workaround
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                throw new InvalidFileException("Cannot read image from file: " + file.getOriginalFilename());
            }

            // For now, we'll convert to PNG as WebP requires additional libraries
            // In production, you'd want to add a webp-imageio library
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);

            log.info("Successfully converted {} to WebP format (as PNG)", file.getOriginalFilename());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Failed to convert image to WebP: {}", e.getMessage());
            throw new FileConversionException("Failed to convert image to WebP", e);
        }
    }

    public byte[] resizeImage(MultipartFile file, int width, int height, boolean keepAspectRatio) {
        validateImageFile(file);

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive numbers");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (InputStream in = file.getInputStream()) {
                var builder = Thumbnails.of(in);

                if (keepAspectRatio) {
                    builder.size(width, height);
                } else {
                    builder.forceSize(width, height);
                }

                // Determine an output format from input
                String format = getFileExtension(file.getOriginalFilename());
                if (format.equals("jpg") || format.equals("jpeg")) {
                    builder.outputFormat("jpg");
                } else if (format.equals("png")) {
                    builder.outputFormat("png");
                } else {
                    builder.outputFormat("png"); // Default to PNG for other formats
                }

                builder.toOutputStream(outputStream);
            }

            log.info("Successfully resized {} to {}x{}", file.getOriginalFilename(), width, height);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Failed to resize image: {}", e.getMessage());
            throw new FileConversionException("Failed to resize image", e);
        }
    }

    public byte[] compressImage(MultipartFile file, float quality) {
        validateImageFile(file);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(new ByteArrayInputStream(file.getBytes()))
                    .scale(1.0)  // Keep the original size
                    .outputQuality(quality)
                    .toOutputStream(outputStream);

            long originalSize = file.getSize();
            long compressedSize = outputStream.size();
            double compressionRatio = ((double)(originalSize - compressedSize) / originalSize) * 100;

            log.info("Successfully compressed {} from {} bytes to {} bytes ({}% reduction)",
                    file.getOriginalFilename(), originalSize, compressedSize,
                    String.format("%.2f", compressionRatio));

            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Failed to compress image: {}", e.getMessage());
            throw new FileConversionException("Failed to compress image", e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty or null");
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        if (!SUPPORTED_IMAGE_FORMATS.contains(fileExtension.toLowerCase())) {
            throw new FileTypeNotSupportedException(
                    "File format ." + fileExtension + " is not supported. Supported formats: " +
                            String.join(", ", SUPPORTED_IMAGE_FORMATS)
            );
        }

        // Validate file size (max 50MB as configured)
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new InvalidFileException("File size exceeds maximum limit of 50MB");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex + 1);
    }
}