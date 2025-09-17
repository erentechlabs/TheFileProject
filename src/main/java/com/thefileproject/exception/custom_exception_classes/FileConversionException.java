package com.thefileproject.exception.custom_exception_classes;

public class FileConversionException extends RuntimeException {
    public FileConversionException(String message) {
        super(message);
    }

    public FileConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}