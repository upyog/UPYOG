package org.egov.filestore.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.filestore.config.FileStoreConfig;
import org.egov.filestore.config.FileStoreConstants;
import org.egov.filestore.domain.model.Artifact;
import org.egov.filestore.domain.model.FileLocation;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Unit tests for {@link StorageValidator#validateMediaFile}.
 */
class StorageValidatorMediaTest {

    private FileStoreConfig fileStoreConfig;
    private StorageValidator storageValidator;

    private static final List<String> ALLOWED_AV_FORMATS =
            Arrays.asList("mp4", "mp3", "wav", "ogg", "webm", "avi", "mov", "aac", "flac", "mkv", "m4a", "m4v");

    @BeforeEach
    void setUp() {
        fileStoreConfig = new FileStoreConfig();
        fileStoreConfig.setAllowedAudioVideoFormats(ALLOWED_AV_FORMATS);
        fileStoreConfig.setMediaUploadMaxSizeBytes(1024L);

        Map<String, List<String>> formatsMap = new HashMap<>();
        formatsMap.put("mp3", Arrays.asList("audio/mpeg", "audio/mp3"));
        formatsMap.put("mp4", Arrays.asList("video/mp4"));
        fileStoreConfig.setAllowedFormatsMap(formatsMap);

        storageValidator = new StorageValidator(fileStoreConfig);
    }

    @Test
    void testValidateMediaFile_EmptyFile_ThrowsCustomException() {
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[0]);
        Artifact artifact = buildArtifact(file);

        CustomException ex = assertThrows(CustomException.class, () -> storageValidator.validateMediaFile(artifact));
        assertEquals(FileStoreConstants.EG_FILESTORE_INVALID_INPUT, ex.getCode());
    }

    @Test
    void testValidateMediaFile_InvalidExtension_ThrowsCustomException() {
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf",
                "content".getBytes());
        Artifact artifact = buildArtifact(file);

        CustomException ex = assertThrows(CustomException.class, () -> storageValidator.validateMediaFile(artifact));
        assertEquals(FileStoreConstants.EG_FILESTORE_INVALID_MEDIA_TYPE, ex.getCode());
    }

    @Test
    void testValidateMediaFile_InvalidContentType_ThrowsCustomException() {
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "text/plain", "content".getBytes());
        Artifact artifact = buildArtifact(file);

        CustomException ex = assertThrows(CustomException.class, () -> storageValidator.validateMediaFile(artifact));
        assertEquals(FileStoreConstants.EG_FILESTORE_INVALID_MEDIA_TYPE, ex.getCode());
    }

    @Test
    void testValidateMediaFile_FileTooLarge_ThrowsCustomException() {
        byte[] largeContent = new byte[2048];
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", largeContent);
        Artifact artifact = buildArtifact(file);

        CustomException ex = assertThrows(CustomException.class, () -> storageValidator.validateMediaFile(artifact));
        assertEquals(FileStoreConstants.EG_FILESTORE_MEDIA_TOO_LARGE, ex.getCode());
    }

    @Test
    void testValidateMediaFile_ValidMp3_DoesNotThrow() {
        // Minimal bytes with MP3 frame sync prefix — sufficient for Tika audio/mpeg detection
        byte[] mp3Bytes = new byte[] {(byte) 0xFF, (byte) 0xFB, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00};
        MockMultipartFile file = new MockMultipartFile("file", "audio.mp3", "audio/mpeg", mp3Bytes);
        Artifact artifact = buildArtifact(file);

        assertDoesNotThrow(() -> storageValidator.validateMediaFile(artifact));
    }

    @Test
    void testValidateFileSize_PdfExceedsLimit_ThrowsCustomException() {
        fileStoreConfig.setPdfUploadMaxSizeBytes(500L);
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf", new byte[1000]);

        CustomException ex = assertThrows(CustomException.class, () -> storageValidator.validateFileSize(file, "pdf"));
        assertEquals(FileStoreConstants.EG_FILESTORE_FILE_TOO_LARGE, ex.getCode());
    }

    @Test
    void testValidateFileSize_ImageExceedsLimit_ThrowsCustomException() {
        fileStoreConfig.setImageUploadMaxSizeBytes(500L);
        fileStoreConfig.setImageFormats(Arrays.asList("jpg", "jpeg", "png"));
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[1000]);

        CustomException ex = assertThrows(CustomException.class, () -> storageValidator.validateFileSize(file, "jpg"));
        assertEquals(FileStoreConstants.EG_FILESTORE_FILE_TOO_LARGE, ex.getCode());
    }

    @Test
    void testValidateFileSize_PdfWithinLimit_DoesNotThrow() {
        fileStoreConfig.setPdfUploadMaxSizeBytes(2000L);
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf", new byte[1000]);

        assertDoesNotThrow(() -> storageValidator.validateFileSize(file, "pdf"));
    }


    private Artifact buildArtifact(MockMultipartFile file) {
        FileLocation location = new FileLocation("id", "PGR", "tag", "pb.amritsar", "bucket/path/file.mp4", null);
        return Artifact.builder().multipartFile(file).fileLocation(location).build();
    }
}
