package com.thefileproject.exception.custom_exception_classes;

public class FileTypeNotSupportedException extends RuntimeException {
    public FileTypeNotSupportedException(String message) {
        super(message);
    }
}
