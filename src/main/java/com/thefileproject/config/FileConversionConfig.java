package com.thefileproject.config;

import com.thefileproject.converter.FileConverter;
import com.thefileproject.converter.ImageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
/**
 * Configuration class for file conversion settings and beans
 */
@Configuration
@Slf4j
public class FileConversionConfig implements WebMvcConfigurer {

    /**
     * Tüm file converter'ları Spring Container'a kayıt eder
     * Bu liste otomatik olarak FileConversionServiceImpl'e inject edilir
     */
    @Bean
    public List<FileConverter> fileConverters() {
        List<FileConverter> converters = List.of(
                new ImageConverter()
                // Gelecekte eklenecekler:
                // new PdfConverter(),
                // new OfficeConverter()
        );

        log.info("Registered {} file converters:", converters.size());
        converters.forEach(converter ->
                log.info("  - {} (priority: {})",
                        converter.getClass().getSimpleName(),
                        converter.getPriority())
        );

        return converters;
    }

    /**
     * Uygulama genelinde kullanılacak ayarlar
     */
    @Bean
    public FileConversionProperties fileConversionProperties() {
        FileConversionProperties properties = FileConversionProperties.builder()
                // Dosya boyutu limitleri
                .maxFileSize(50 * 1024 * 1024L) // 50MB
                .maxImageDimension(4096) // Max 4096x4096 pixel

                // Desteklenen format listeleri
                .supportedImageFormats(List.of("jpg", "jpeg", "png", "bmp", "gif", "webp"))
                .supportedPdfFormats(List.of("pdf"))
                .supportedOfficeFormats(List.of("docx", "xlsx", "pptx", "doc", "xls", "ppt"))

                // Kalite ayarları
                .defaultImageQuality(0.85f)
                .minImageQuality(0.1f)
                .maxImageQuality(1.0f)

                // İşlem timeout'ları (millisaniye)
                .conversionTimeoutMs(30000L) // 30 saniye

                // Temporary dosya ayarları
                .tempFilePrefix("fileconvert_")
                .cleanupTempFiles(true)
                .build();


        log.info("FileConversionProperties configured:");
        log.info("  - Max file size: {} MB", properties.getMaxFileSize() / 1024 / 1024);
        log.info("  - Supported image formats: {}", properties.getSupportedImageFormats());
        log.info("  - Default image quality: {}", properties.getDefaultImageQuality());
        log.info("  - Max image dimension: {}px", properties.getMaxImageDimension());

        return properties;
    }

    /**
     * CORS konfigürasyonu - Frontend'ten API çağrıları için
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*") // Production'da specific domain'ler belirt
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 1 saat cache

        log.info("CORS configured for /api/** endpoints");
    }
}