package com.thefileproject.converter;

import java.io.InputStream;

public interface FileConverter {

    /**
     * @param inputStream Input file stream
     * @param sourceFormat Source file format (e.g., "png", "pdf")
     * @param targetFormat Target file format (e.g., "jpg", "docx")
     * @return Converted file as a byte array
     * @throws Exception if conversion fails
     */
    byte[] convert(InputStream inputStream, String sourceFormat, String targetFormat) throws Exception;

    /**
     * Checks if this converter supports the given conversion
     *
     * @param sourceFormat Source file format
     * @param targetFormat Target file format
     * @return true if conversion is supported
     */
    boolean isSupported(String sourceFormat, String targetFormat);

    // Gets the priority of the converter. Lower priority converters are run first.
    default int getPriority() {
        return 0;
    }
}
