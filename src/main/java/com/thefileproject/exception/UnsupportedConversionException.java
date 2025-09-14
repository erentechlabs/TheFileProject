package com.thefileproject.exception;

public class UnsupportedConversionException extends RuntimeException {

    private final String sourceFormat;
    private final String targetFormat;

    public UnsupportedConversionException(String sourceFormat, String targetFormat) {
        super(String.format("Conversion from '%s' to '%s' is not supported", sourceFormat, targetFormat));
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
    }

    public UnsupportedConversionException(String sourceFormat, String targetFormat, String message) {
        super(message);
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
    }

    public String getSourceFormat() {
        return sourceFormat;
    }

    public String getTargetFormat() {
        return targetFormat;
    }
}