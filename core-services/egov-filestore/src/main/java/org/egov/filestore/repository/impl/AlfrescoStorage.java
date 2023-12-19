package org.egov.filestore.repository.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.egov.filestore.domain.model.Artifact;
import org.egov.filestore.persistence.repository.FileStoreJpaRepository;
import org.egov.filestore.repository.AlfrescoClientFacade;
import org.egov.filestore.repository.CloudFilesManager;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(value = "isAlfrescoEnabled", havingValue = "true")
public class AlfrescoStorage implements CloudFilesManager {

	private Session alfrscoSession;

	@Autowired
	private AlfrescoClientFacade alfrescoFacade;

	@Autowired
	private CloudFileMgrUtils util;

	@Value("${is.container.fixed}")
	private Boolean isContainerFixed;

	@Value("${fixed.container.name}")
	private String fixedContainerName;

	@Value("${filestore.path}")
	private String filestorePath;

	@Value("${filestore.url}")
	private String filestoreUrl;

	@Value("${image.small}")
	private String _small;

	@Value("${image.medium}")
	private String _medium;

	@Value("${image.large}")
	private String _large;

	private FileStoreJpaRepository fileStoreJpaRepository;

	private static final String UPYOG_UPLOAD_FOLDER = "/UPYOG";

	@Autowired
	AlfrescoStorage(FileStoreJpaRepository fileStoreJpaRepository) {
		this.fileStoreJpaRepository = fileStoreJpaRepository;
	}

	@Override
	public void saveFiles(List<Artifact> artifacts) {
		if (null == alfrscoSession)
			alfrscoSession = alfrescoFacade.getAlfrescoClient();

		Set<String> listOfUUids = new HashSet<>();
		for (Artifact a : artifacts) {
			listOfUUids.add(a.getFileLocation().getFileStoreId());
		}

		artifacts.forEach(artifact -> {

			String completeName = artifact.getFileLocation().getFileName();
			try {
				if (null != alfrscoSession) {
					upload(artifact.getMultipartFile(), alfrscoSession, artifact.getFileLocation().getFileStoreId(),
							artifact.getFileLocation().getTenantId());
				}

			} catch (Exception e) {
				fileStoreJpaRepository.deleteFileStoreMap(artifacts.get(0).getFileLocation().getTenantId(),
						listOfUUids);
				log.error("Exceptione while creating the container: ", e);
			}

		});
	}

	/**
	 * There's a problem with this implementation: In case of images, we are trying
	 * to retrieve 4 different versions of the same file namely - small, medium,
	 * large and the original. The path stored in the db is the path of the original
	 * file only, we are making suitable changes to that file path by appending some
	 * extensions to obtain file paths of the different versions. TODO: This has to
	 * be fixed, we need to keep track of all these versions by storing their paths
	 * in the db separately instead of deriving them.
	 * 
	 * Secondly, once these paths are obtained, their SAS urls are being returned as
	 * comma separated values in a single string, this has to change to list of
	 * strings. We aren't taking this up because this will cause high impact on UI.
	 * TODO: Change comma separated string to list of strings and test it with UI
	 * once their changes are done.
	 */

	public Map<String, String> getFiles(Map<String, String> mapOfIdAndFilePath) {
		if (null == alfrscoSession)
			alfrscoSession = alfrescoFacade.getAlfrescoClient();
		Map<String, String> mapOfIdAndSASUrls = new HashMap<>();
		mapOfIdAndFilePath.keySet().forEach(id -> {
			if (util.isFileAnImage(mapOfIdAndFilePath.get(id))) {

				StringBuilder url = new StringBuilder();
				/*
				 * Don't change the order of images within this if, it is index-based and UI
				 * will break.
				 */
				String[] imageFormats = { _large, _medium, _small };
				// url.append(getSASURL(mapOfIdAndFilePath.get(id),
				// util.generateSASToken(azureBlobClient, mapOfIdAndFilePath.get(id))));
				String replaceString = mapOfIdAndFilePath.get(id).substring(mapOfIdAndFilePath.get(id).lastIndexOf('.'),
						mapOfIdAndFilePath.get(id).length());
				for (String format : Arrays.asList(imageFormats)) {
					url.append(",");
					String path = mapOfIdAndFilePath.get(id);
					path = path.replaceAll(replaceString, format + replaceString);
					// url.append(getSASURL(path, util.generateSASToken(azureBlobClient, path)));
				}
				mapOfIdAndSASUrls.put(id, url.toString());
			} else {
				// mapOfIdAndSASUrls.put(id, getSASURL(mapOfIdAndFilePath.get(id),
				// util.generateSASToken(azureBlobClient, mapOfIdAndFilePath.get(id))));
			}
		});
		return mapOfIdAndSASUrls;
	}

	/**
	 * Prepares the SASUrls for the resource on azure
	 * 
	 * @param path
	 * @param sasToken
	 * @return
	 */
	private String getSASURL(String path, String sasToken) {
		StringBuilder sasURL = new StringBuilder();
		// String host = azureBlobStorageHost.replace("$accountName", azureAccountName);
		// sasURL.append(host).append("/").append(path).append("?").append(sasToken);
		return sasURL.toString();
	}

	/**
	 * Uploads the file to Azure Blob Storage
	 * 
	 * @param container
	 * @param completePath
	 * @param file
	 * @param image
	 * @param extension
	 */
	public void upload(CloudBlobContainer container, String completePath, InputStream inputStream, Long contentLength,
			BufferedImage image, String extension) {
		try {
			if (null == inputStream && null != image) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, extension, os);
				CloudBlockBlob blob = container.getBlockBlobReference(completePath);
				blob.upload(new ByteArrayInputStream(os.toByteArray()), 8 * 1024 * 1024);
			} else {
				CloudBlockBlob blob = container.getBlockBlobReference(completePath);
				blob.upload(inputStream, contentLength);
			}

		} catch (Exception e) {
			throw new CustomException("WG_WF_UPLOAD_ERROR", e.getMessage());
		}
	}

	public Map<String, String> getFiles(List<org.egov.filestore.persistence.entity.Artifact> artifacts) {
		Map<String, String> mapOfIdAndSASUrls = new HashMap<>();
		for (org.egov.filestore.persistence.entity.Artifact artifact : artifacts) {

			String fileLocation = artifact.getFileLocation().getFileName();
			String fileName = fileLocation.substring(fileLocation.indexOf('/') + 1, fileLocation.length());
			String finalFilePath = filestorePath + filestoreUrl + "?" + "tenantId=" + artifact.getTenantId() + "&"
					+ "fileStoreId=" + artifact.getFileStoreId();
			mapOfIdAndSASUrls.put(artifact.getFileStoreId(), finalFilePath);

		}
		return mapOfIdAndSASUrls;
	}

	public void upload(MultipartFile multipartFile, Session alfrscoSession, String uuid, String tenantId) {
		String fileName = null;
		Folder folder = null;
		Folder parent = null;
		InputStream targetStream = null;
		Map<String, Object> properties = null;
		byte[] content = null;
		InputStream stream = null;
		ContentStream contentStream = null;
		Document newDoc = null;
		try {
			parent = (Folder) alfrscoSession.getObjectByPath(UPYOG_UPLOAD_FOLDER);
			properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			properties.put(PropertyIds.NAME, uuid);
			try {
				folder = parent.createFolder(properties);
				parent = (Folder) alfrscoSession.getObjectByPath(UPYOG_UPLOAD_FOLDER + "/" + uuid);
			} catch (Exception e) {
				System.out.println("Folder already exists");
			}
			if (folder == null) {
				parent = (Folder) alfrscoSession.getObjectByPath(UPYOG_UPLOAD_FOLDER + "/" + uuid);
			}
			targetStream = multipartFile.getInputStream();
			System.out.println(multipartFile);
			String fileObj[] = (multipartFile.getOriginalFilename()).split("[.]");
			fileName = fileObj[0] + "_" + tenantId + "_" + uuid + "." + fileObj[1];
			content = multipartFile.getBytes();
			properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties.put(PropertyIds.NAME, fileName);
			properties.put(PropertyIds.CONTENT_STREAM_LENGTH,BigInteger.valueOf(content.length));
			properties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE,"application/"+fileObj[1]);
			
			targetStream.read(content);
			targetStream.close();
			stream = new ByteArrayInputStream(content);
			Integer s = content.length;
			contentStream = new ContentStreamImpl(fileName, new BigInteger(s.toString()), "application/"+fileObj[1], stream);
			newDoc = parent.createDocument(properties, contentStream, VersioningState.MAJOR);
			updateFileStoreData(newDoc.getId().split(";")[0], uuid, tenantId);
		} catch (Exception e) {
			throw new CustomException("FAILED_UPLOAD", "Failed to Upload to Alfresco");
		}

	}

	private void updateFileStoreData(String alfresccoDocId, String uuid, String tenantId) {

		fileStoreJpaRepository.updateFileStoreWithAlfrescoIds(alfresccoDocId, tenantId, uuid);

	}

	public Map<String,Object> getFileFromAlfresco(String id) {
		if (null == alfrscoSession)
			alfrscoSession = alfrescoFacade.getAlfrescoClient();
		Document doc = null;
		ContentStream cs = null;
		BufferedInputStream bis = null;
		Resource resource = null;
		
		 Map<String,Object> ret = new HashMap<>();
		try {
			doc = (Document) alfrscoSession.getObject(id);
			cs = doc.getContentStream(null);
			bis = new BufferedInputStream(cs.getStream());
			resource = new InputStreamResource(bis);
			ret.put("fileBytes", cs.getBigLength());
			ret.put("resource",resource );
		} catch (Exception e) {
			throw new CustomException("INVALID_FILE_ID", "File not Found in Alfresco");
		}
		return ret;
	}

}
