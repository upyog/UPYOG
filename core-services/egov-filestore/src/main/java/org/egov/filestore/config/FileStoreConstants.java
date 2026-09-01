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
    public static final String EG_FILESTORE_PARSING_ERROR = "EG_FILESTORE_PARSING_ERROR";

    // Default Audio / Video Formats
    public static final List<String> DEFAULT_AUDIO_VIDEO_FORMATS = Collections.unmodifiableList(
            Arrays.asList("mp4", "mp3", "wav", "ogg", "webm", "avi", "mov", "aac", "flac", "mkv", "m4a", "m4v")
    );
}

