package org.egov.noc.service;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.ServiceRequestRepository;
import org.egov.noc.util.NOCConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to fetch file URLs from fileStore service and upload files
 */
@Slf4j
@Service
public class FileStoreService {

	@Autowired
	private NOCConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Gets file URL from fileStoreId and tenantId
	 * 
	 * @param fileStoreId File store ID
	 * @param tenantId Tenant ID
	 * @return File URL or null if not found
	 */
	public String getFileUrl(String fileStoreId, String tenantId) {
		if (fileStoreId == null || fileStoreId.isEmpty()) {
			return null;
		}
		try {
			StringBuilder uri = new StringBuilder(config.getFileStoreHost());
			uri.append(config.getFileStorePath());
			uri.append("?tenantId=").append(tenantId);
			uri.append("&fileStoreIds=").append(fileStoreId);

			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) 
					serviceRequestRepository.fetchResultUsingGet(uri);

			if (response != null) {
				Object directUrl = response.get(fileStoreId);
				if (directUrl != null) {
					String urlStr = directUrl.toString();
					if (urlStr.contains(",")) {
						urlStr = urlStr.substring(0, urlStr.indexOf(","));
					}
					return urlStr;
				}

				// Fallback to fileStoreIds array
				Object fileStoreIds = response.get("fileStoreIds");
				if (fileStoreIds != null && fileStoreIds instanceof List) {
					@SuppressWarnings("unchecked")
					List<Object> fileList = (List<Object>) fileStoreIds;
					if (!CollectionUtils.isEmpty(fileList)) {
						for (Object fileObj : fileList) {
							if (fileObj instanceof Map) {
								@SuppressWarnings("unchecked")
								Map<String, Object> fileMap = (Map<String, Object>) fileObj;
								Object id = fileMap.get("id");
								if (fileStoreId.equals(id)) {
									Object url = fileMap.get("url");
									if (url != null) {
										String urlStr = url.toString();
										if (urlStr.contains(",")) {
											urlStr = urlStr.substring(0, urlStr.indexOf(","));
										}
										return urlStr;
									}
								}
							}
						}
					}
				}
			}

			return null;
		} catch (Exception e) {
			log.error("Error fetching file URL for fileStoreId {}", fileStoreId, e);
			return null;
		}
	}

	/**
	 * Downloads file from external URL and uploads to FileStore service
	 * 
	 * @param fileUrl External file URL to download
	 * @param tenantId Tenant ID
	 * @param module Module name (e.g., "NOC")
	 * @return FileStore ID if successful, null otherwise
	 */
	public String uploadFileFromUrlToFileStore(String fileUrl, String tenantId, String module) {
		if (fileUrl == null || fileUrl.trim().isEmpty() || NOCConstants.AAI_STATUS_INPROCESS.equalsIgnoreCase(fileUrl.trim())) {

			return null;
		}
		File tempFile = null;
		try {
			URL url = new URL(fileUrl);
			String extractedFileName = FilenameUtils.getName(fileUrl.split("\\?")[0]);
			final String fileName = (extractedFileName == null || extractedFileName.isEmpty()) 
					? "aai_noc_document_" + System.currentTimeMillis() + ".pdf" 
					: extractedFileName;

			tempFile = File.createTempFile("aai_doc_", "_" + fileName);
			FileUtils.copyURLToFile(url, tempFile);
			log.info("Downloaded file from URL: {} to temp file: {}", fileUrl, tempFile.getAbsolutePath());

			byte[] fileBytes = FileUtils.readFileToByteArray(tempFile);
			ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
				@Override
				public String getFilename() {
					return fileName;
				}
			};

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
			formData.add("tenantId", tenantId);
			formData.add("module", module);
			formData.add("file", fileResource);

			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(formData, headers);
			StringBuilder uri = new StringBuilder(config.getFileStoreHost());
			uri.append(config.getFileStoreUploadPath());

			@SuppressWarnings("unchecked")
			Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(
					uri.toString(), request, Map.class);

			if (response != null && response.get("files") != null) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> files = (List<Map<String, Object>>) response.get("files");
				if (!CollectionUtils.isEmpty(files) && files.get(0).get("fileStoreId") != null) {
					String fileStoreId = files.get(0).get("fileStoreId").toString();
					log.info("Successfully uploaded file to FileStore, fileStoreId: {}", fileStoreId);
					return fileStoreId;
				}
			}

			log.error("Invalid response from FileStore service: {}", response);
			return null;

		} catch (Exception e) {
			log.error("Error downloading and uploading file from URL: {}", fileUrl, e);
			return null;
		} finally {
			if (tempFile != null && tempFile.exists()) {
				try {
					tempFile.delete();
				} catch (Exception e) {
					log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath(), e);
				}
			}
		}
	}
}

