package org.egov.filestore.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.egov.filestore.config.FileStoreConfig;
import org.egov.filestore.domain.model.Artifact;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class StorageValidator {

	private FileStoreConfig fileStoreConfig;

	@Autowired
	public StorageValidator(FileStoreConfig fileStoreConfig) {
		super();
		this.fileStoreConfig = fileStoreConfig;
	}

	public void validate(Artifact artifact) {

		String extension = (FilenameUtils.getExtension(artifact.getMultipartFile().getOriginalFilename())).toLowerCase();
		validateFileExtention(extension);
		validateContentType(artifact.getFileContentInString(), extension);
		validateInputContentType(artifact);
	}

	/**
	 * Validates a directly uploaded audio/video file before it is stored in cloud storage.
	 * Checks file presence, extension allow-list, declared content-type, size limit, and
	 * binary content-type via Apache Tika.
	 */
	public void validateMediaFile(Artifact artifact) {
		MultipartFile file = artifact.getMultipartFile();
		if (file == null || file.isEmpty()) {
			throw new CustomException("EG_FILESTORE_INVALID_INPUT", "Media file must not be empty");
		}

		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || originalFilename.isBlank()) {
			throw new CustomException("EG_FILESTORE_INVALID_INPUT", "Media file must have a filename");
		}

		String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
		if (extension.isBlank() || !fileStoreConfig.getAllowedAudioVideoFormats().contains(extension)) {
			throw new CustomException("EG_FILESTORE_INVALID_MEDIA_TYPE",
					"File extension '" + extension + "' is not in the allowed audio/video formats: "
							+ fileStoreConfig.getAllowedAudioVideoFormats());
		}

		if (file.getSize() > fileStoreConfig.getMediaUploadMaxSizeBytes()) {
			throw new CustomException("EG_FILESTORE_MEDIA_TOO_LARGE",
					"Media file exceeds maximum allowed size of "
							+ fileStoreConfig.getMediaUploadMaxSizeBytes() + " bytes");
		}

		String contentType = file.getContentType();
		if (contentType == null
				|| (!contentType.startsWith("audio/") && !contentType.startsWith("video/"))) {
			throw new CustomException("EG_FILESTORE_INVALID_MEDIA_TYPE",
					"Invalid content type for media upload: " + contentType);
		}

		if (fileStoreConfig.getAllowedFormatsMap().containsKey(extension)) {
			List<String> allowedMimes = fileStoreConfig.getAllowedFormatsMap().get(extension);
			if (!allowedMimes.contains(contentType)) {
				throw new CustomException("EG_FILESTORE_INVALID_MEDIA_TYPE", "Invalid Content Type");
			}
		}

		try (InputStream inputStream = file.getInputStream()) {
			String detectedFormat = new Tika().detect(inputStream, originalFilename);
			if (!detectedFormat.startsWith("audio/") && !detectedFormat.startsWith("video/")) {
				throw new CustomException("EG_FILESTORE_INVALID_MEDIA_TYPE",
						"File content does not match an audio or video format. Detected: " + detectedFormat);
			}
		} catch (IOException e) {
			throw new CustomException("EG_FILESTORE_PARSING_ERROR",
					"Unable to read media file: " + e.getMessage());
		}
	}

	private void validateFileExtention(String extension) {
		if (!fileStoreConfig.getAllowedFormatsMap().containsKey(extension)) {
			throw new CustomException("EG_FILESTORE_INVALID_INPUT", "Inalvid input provided for file : " + extension
					+ ", please upload any of the allowed formats : " + fileStoreConfig.getAllowedKeySet());
		}
	}

	private void validateContentType(String inputStreamAsString, String extension) {

		String inputFormat = null;
		Tika tika = new Tika();
		try {

			InputStream ipStreamForValidation = IOUtils.toInputStream(inputStreamAsString,
					fileStoreConfig.getImageCharsetType());
			inputFormat = tika.detect(ipStreamForValidation);
			ipStreamForValidation.close();
		} catch (IOException e) {
			throw new CustomException("EG_FILESTORE_PARSING_ERROR",
					"not able to parse the input please upload a proper file of allowed type : " + e.getMessage());
		}

		if (!fileStoreConfig.getAllowedFormatsMap().get(extension).contains(inputFormat)) {
			throw new CustomException("EG_FILESTORE_INVALID_INPUT",
					"Inalvid input provided for file, the extension does not match the file format. Please upload any of the allowed formats : "
							+ fileStoreConfig.getAllowedKeySet());
		}
	}

	private void validateInputContentType(Artifact artifact) {

		MultipartFile file = artifact.getMultipartFile();
		String contentType = file.getContentType();
		String extension = (FilenameUtils.getExtension(artifact.getMultipartFile().getOriginalFilename())).toLowerCase();

		if (!fileStoreConfig.getAllowedFormatsMap().get(extension).contains(contentType)) {
			throw new CustomException("EG_FILESTORE_INVALID_INPUT", "Invalid Content Type");
		}
	}
}
