package com.file.converter.services;

public interface Converter {
    boolean supportsFile(String fileName);
    byte[] convert(byte[] file);
}
