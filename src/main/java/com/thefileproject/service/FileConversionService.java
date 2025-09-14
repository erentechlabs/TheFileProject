package com.thefileproject.service;
import com.thefileproject.dto.ConversionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileConversionService {

    /**
     * Converts a file from source format to target format
     *
     * @param file The input file
     * @param targetFormat The desired output format
     * @return ConversionResponse containing the converted file or error information
     */
    ConversionResponse convertFile(MultipartFile file, String targetFormat);

    /**
     * Checks if conversion between two formats is supported
     *
     * @param sourceFormat Source file format
     * @param targetFormat Target file format
     * @return true if conversion is supported
     */
    boolean isConversionSupported(String sourceFormat, String targetFormat);

    /**
     * Gets all supported source formats
     *
     * @return Set of supported source formats
     */
    java.util.Set<String> getSupportedSourceFormats();

    /**
     * Gets all supported target formats for a given source format
     *
     * @param sourceFormat The source format
     * @return Set of supported target formats
     */
    java.util.Set<String> getSupportedTargetFormats(String sourceFormat);
}
