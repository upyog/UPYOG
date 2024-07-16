package org.egov.tl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileSystemUtil {

	private MultipartFile convertResourceToMultipartFile(Resource resource, String newFileName) {

//		MultipartFile multipartFile = convertResourceToMultipartFile(resource, "filename");

		if (resource == null) {
			return null;
		}

		try {
			InputStream inputStream = resource.getInputStream();

			// Read the content of the InputStream into a byte array
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			byte[] content = outputStream.toByteArray();

			MediaType contentType = MediaType.ALL;
			String finalFileName = getFinalFileName(resource.getFilename(), newFileName);

			return new MultipartFile() {
				@Override
				public String getName() {
					return finalFileName;
				}

				@Override
				public String getOriginalFilename() {
					return finalFileName;
				}

				@Override
				public String getContentType() {
					return contentType.toString();
				}

				@Override
				public boolean isEmpty() {
					return content == null || content.length == 0;
				}

				@Override
				public long getSize() {
					return content.length;
				}

				@Override
				public byte[] getBytes() throws IOException {
					return content;
				}

				@Override
				public InputStream getInputStream() throws IOException {
					return new ByteArrayInputStream(content);
				}

				@Override
				public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
					throw new UnsupportedOperationException(
							"MultipartFile.transferTo() is not supported for this implementation");
				}
			};
		} catch (IOException e) {
			log.error("Error occured while converting Resource to MultipartFile", e);
			throw new RuntimeException(
					"Error occured while converting Resource to MultipartFile. Message: " + e.getMessage());
		}
	}
	
	
	
	private static String getFinalFileName(String originalFilename, String fileName) {
		if (originalFilename == null || originalFilename.isEmpty()) {
			throw new IllegalArgumentException("Original filename cannot be null or empty.");
		}
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("New file name cannot be null or empty.");
		}

		// Generate a new file name using NOCUtil
		String newFileName = generateFileName(fileName);

		// Get the file extension from the original filename
		String fileNameExtension = FilenameUtils.getExtension(originalFilename);

		return new StringBuilder(newFileName).append('.').append(fileNameExtension).toString();
	}
	
	
	
	private static String generateFileName(String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException("File name cannot be null.");
		}

		// Replace disallowed symbols with allowed replacements
		return StringUtils.replaceChars(fileName, "/\\:|?*\"<>", "_");
	}
}
