package com.file.converter.constants;

import java.util.HashSet;
import java.util.Set;

public class Constants {
    public static  final Integer MAX_RETRY_CONSUMER = 5;
    public static final String ZIP_TYPE = "application/zip";
    public  static  final Integer MAX_ZIP_DEPTH = 3;
    public static final String PDF_CONTENT_TYPE = "application/pdf";
    public static final Set<String> SUPPORTED_IMAGE = new HashSet<>(Set.of("image/jpg", "image/png"));
    public static final String SUPPORTED_TEXT_FILE_TYPE = "text/plain";

}
