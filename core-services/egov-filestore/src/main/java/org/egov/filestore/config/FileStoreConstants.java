package org.egov.filestore.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileStoreConstants {

    private FileStoreConstants() {}

    // Error Codes
    public static final String EG_FILESTORE_INVALID_INPUT = "EG_FILESTORE_INVALID_INPUT";
    public static final String EG_FILESTORE_INVALID_MEDIA_TYPE = "EG_FILESTORE_INVALID_MEDIA_TYPE";
    public static final String EG_FILESTORE_MEDIA_TOO_LARGE = "EG_FILESTORE_MEDIA_TOO_LARGE";
    public static final String EG_FILESTORE_FILE_TOO_LARGE = "EG_FILESTORE_FILE_TOO_LARGE";
    public static final String EG_FILESTORE_PARSING_ERROR = "EG_FILESTORE_PARSING_ERROR";
    public static final String EG_FILESTORE_INPUT_ERROR = "EG_FILESTORE_INPUT_ERROR";

    // Default Media Formats
    public static final List<String> DEFAULT_AUDIO_VIDEO_FORMATS = Collections.unmodifiableList(
            Arrays.asList("mp4", "mp3", "wav", "ogg", "webm", "avi", "mov", "aac", "flac", "mkv", "m4a", "m4v")
    );

    // Default Image Formats
    public static final List<String> DEFAULT_IMAGE_FORMATS = Collections.unmodifiableList(
            Arrays.asList("png", "jpeg", "jpg")
    );

    // Default Document Formats
    public static final List<String> DEFAULT_DOCUMENT_FORMATS = Collections.unmodifiableList(
            Arrays.asList("doc", "docx", "xls", "xlsx", "txt", "csv", "ods", "odt", "dxf")
    );
}
