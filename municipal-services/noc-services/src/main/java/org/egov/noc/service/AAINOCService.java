package org.egov.noc.service;

import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.util.NOCConstants;
import org.egov.noc.web.model.BpaApplication;
import org.egov.noc.web.model.Document;
import org.egov.noc.web.model.Noc;
import org.egov.noc.web.model.RequestInfoWrapper;
import org.egov.noc.web.model.SiteCoordinate;
import org.egov.noc.web.model.bpa.Address;
import org.egov.noc.web.model.bpa.BPA;
import org.egov.noc.web.model.bpa.LandInfo;
import org.egov.noc.web.model.bpa.OwnerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import lombok.extern.slf4j.Slf4j;

import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.STANDALONE;

@Service
@Slf4j
public class AAINOCService {

	@Autowired
	private NOCConfiguration config;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	@Autowired
	private NOCService nocService;

	@Autowired
	private EDCRService edcrService;

	@Autowired
	private FileStoreService fileStoreService;

	@Autowired
	private AAINOCValidationService aaiNOCValidationService;

	/**
	 * Generates NOCAS XML response by fetching newly created NOC applications
	 * and their corresponding BPA details. Maps data to AAI NOCAS XML format.
	 * 
	 * @param requestInfo Authenticated request information
	 * @param tenantId Tenant ID (optional, defaults to 'as')
	 * @return XML string containing BPA application details
	 */
	public String generateNocasXml(RequestInfo requestInfo, String tenantId) {

		try {
			List<Noc> nocList = nocService.fetchNewAAINOCs(tenantId);

			if (CollectionUtils.isEmpty(nocList)) {
				log.info("No new AAI NOC applications found");
				return createEmptyResponse(config.getAuthorityName());
			}

			// Validate NOCs before fetching BPA details to avoid unnecessary service calls
			nocList = aaiNOCValidationService.validateNOCs(nocList);
			
			if (CollectionUtils.isEmpty(nocList)) {
				log.info("No valid AAI NOC applications found after validation");
				return createEmptyResponse(config.getAuthorityName());
			}
			log.info("No. of valid NOC applications after validation: " + nocList.size());
			RequestInfoWrapper requestInfoWrapper =	new RequestInfoWrapper();
			requestInfoWrapper.setRequestInfo(requestInfo);

			// Map NOC by sourceRefId (which equals BPA applicationNo) for lookup
			Map<String, Noc> nocMap = new HashMap<>();
			for (Noc noc : nocList) {
				if (noc != null && noc.getSourceRefId() != null) {
					nocMap.put(noc.getSourceRefId(), noc);
				}
			}

			// Fetch BPA details for the VALIDATED NOC applications only
			List<BPA> bpaDetails = nocService.getBPADetails(nocList, requestInfoWrapper);
			
			Map<String, BPA> bpaMap = new HashMap<>();
			for (BPA bpa : bpaDetails) {
				if (bpa != null && bpa.getApplicationNo() != null) {
					bpaMap.put(bpa.getApplicationNo(), bpa);
				}
			}
			
			List<BpaApplication> applications = new ArrayList<>();
			for (BPA bpa : bpaDetails) {
				try {
					BpaApplication obj = mapBPAToBpaApplication(bpa);
					if (obj != null) {
						// Set corresponding NOC for document mapping (sourceRefId = applicationNo)
						Noc correspondingNoc = nocMap.get(bpa.getApplicationNo());
						obj.setNoc(correspondingNoc);
						applications.add(obj);
					}
				} catch (Exception e) {
					log.error("Error mapping BPA {}: {}", 
							bpa != null ? bpa.getApplicationNo() : "null", e.getMessage());
				}
			}
			
			log.info("No. of new NOC applications sent: " + applications.size());

			// Create XML document
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);

			// Root element
			Element rootElement = doc.createElement(config.getAuthorityName() + "DETAILS");
			doc.appendChild(rootElement);

			for (BpaApplication app : applications) {
				Element toAAI = doc.createElement("TOAAI");
				rootElement.appendChild(toAAI);

				// Application Data
				Element appData = doc.createElement("ApplicationData");
				toAAI.appendChild(appData);

				addElement(doc, appData, "AUTHORITY", config.getAuthorityName());
				addElement(doc, appData, "UNIQUEID", app.getUniqueId());
				addElement(doc, appData, "APPLICATIONDATE", app.getApplicationDate());
				addElement(doc, appData, "APPLICANTNAME", app.getApplicantName());
				addElement(doc, appData, "APPLICANTADDRESS", app.getApplicantAddress());
				addElement(doc, appData, "APPLICANTNO", app.getApplicantContact());
				addElement(doc, appData, "APPLICANTEMAIL", app.getApplicantEmail());
				addElement(doc, appData, "APPLICATIONNO", app.getApplicationNo());
				addElement(doc, appData, "OWNERNAME", app.getOwnerName());
				addElement(doc, appData, "OWNERADDRESS", app.getOwnerAddress());
				addElement(doc, appData, "STRUCTURETYPE", app.getStructureType());
				addElement(doc, appData, "STRUCTUREPURPOSE", app.getStructurePurpose());
				addElement(doc, appData, "SITEADDRESS", app.getSiteAddress());
				addElement(doc, appData, "SITECITY", app.getSiteCity());
				addElement(doc, appData, "SITESTATE", app.getSiteState());
				addElement(doc, appData, "PLOTSIZE", String.valueOf(app.getPlotSize()));
				addElement(doc, appData, "ISINAIRPORTPREMISES", app.getIsInAirportPremises());
				addElement(doc, appData, "PERMISSIONTAKEN", app.getPermissionTaken());

				BPA correspondingBPA = bpaMap.get(app.getApplicationNo());
				List<SiteCoordinate> coordinates = extractCoordinatesFromNOC(app.getNoc(), correspondingBPA, requestInfo);

				Element siteDetails = doc.createElement("SiteDetails");
				toAAI.appendChild(siteDetails);

				if (!coordinates.isEmpty()) {
					for (SiteCoordinate coord : coordinates) {
						Element coordElement = doc.createElement("Coordinates");
						siteDetails.appendChild(coordElement);

						addElement(doc, coordElement, "LATITUDE", 
								coord.getLatitude() != null ? coord.getLatitude() : "");
						addElement(doc, coordElement, "LONGITUDE", 
								coord.getLongitude() != null ? coord.getLongitude() : "");
						addElement(doc, coordElement, "SITEELEVATION", 
								coord.getSiteElevation() != null ? String.valueOf(coord.getSiteElevation()) : "");
						addElement(doc, coordElement, "BUILDINGHEIGHT", 
								coord.getBuildingHeight() != null ? String.valueOf(coord.getBuildingHeight()) : "");
						addElement(doc, coordElement, "STRUCTURENO", 
								coord.getStructureNo() != null ? String.valueOf(coord.getStructureNo()) : "");
					}
				}

				// Files (Document URLs from NOC documents)
				Element files = doc.createElement("FILES");
				toAAI.appendChild(files);

				Map<String, String> documentUrls = getDocumentUrls(app.getNoc());
				
				addElement(doc, files, "UNDERTAKING1A", 
						documentUrls.getOrDefault(NOCConstants.DOC_TYPE_UNDERTAKING1A, ""));
				addElement(doc, files, "SITEELEVATION", 
						documentUrls.getOrDefault(NOCConstants.DOC_TYPE_SITEELEVATION, ""));
				addElement(doc, files, "SITECORDINATES", 
						documentUrls.getOrDefault(NOCConstants.DOC_TYPE_SITECORDINATES, ""));
				addElement(doc, files, "AUTHORIZATION", 
						documentUrls.getOrDefault(NOCConstants.DOC_TYPE_AUTHORIZATION, ""));
				if ("Yes".equalsIgnoreCase(app.getIsInAirportPremises())) {
					addElement(doc, files, "PERMISSION", 
							documentUrls.getOrDefault(NOCConstants.DOC_TYPE_PERMISSION, ""));
				}
			}

			// Convert Document to String with XML declaration
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(STANDALONE, "yes");
			transformer.setOutputProperty(INDENT, "no");
			
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));

			return writer.toString();

		} catch (Exception e) {
			throw new RuntimeException("Error generating NOCAS XML", e);
		}
	}

	/**
	 * Adds XML element with tag and value to parent element
	 * 
	 * @param doc XML document
	 * @param parent Parent element
	 * @param tagName Element tag name
	 * @param value Element value
	 */
	private void addElement(org.w3c.dom.Document doc, Element parent, String tagName, String value) {
		Element element = doc.createElement(tagName);
		element.appendChild(doc.createTextNode(value != null ? value : ""));
		parent.appendChild(element);
	}

//	/**
//	 * Fetches NOC applications in CREATED status and maps to BpaApplication objects
//	 *
//	 * @return List of BpaApplication objects
//	 */
//	public List<BpaApplication> getCreatedApplications() {
//
//		List<BpaApplication> result = new ArrayList<>();
//		List<Noc> nocList = nocService.fetchNewAAINOCs(null);
//
//		if (CollectionUtils.isEmpty(nocList)) {
//			log.info("No new AAI NOC applications found");
//			return result;
//		}
//
//		List<BPA> bpaDetails = nocService.getBPADetails(nocList, nocUtil.createDefaultRequestInfo());
//
//		for (BPA bpa : bpaDetails) {
//			try {
//				BpaApplication obj = mapBPAToBpaApplication(bpa);
//				if (obj != null) {
//					result.add(obj);
//				}
//			} catch (Exception e) {
//				log.error("Error mapping BPA {} to BpaApplication: {}",
//						bpa.getApplicationNo(), e.getMessage(), e);
//				// Continue processing other applications even if one fails
//			}
//		}
//
//		return result;
//
//	}

	/**
	 * Maps BPA to BpaApplication with null safety and data formatting
	 * 
	 * @param bpa BPA object
	 * @return BpaApplication object or null if data missing
	 */
	private BpaApplication mapBPAToBpaApplication(BPA bpa) {
		if (bpa == null || bpa.getApplicationNo() == null) {
			return null;
		}

		BpaApplication obj = new BpaApplication();
		obj.setUniqueId(bpa.getApplicationNo());
		obj.setApplicationNo(bpa.getApplicationNo());

		Long appDate = bpa.getApplicationDate();
		if (appDate != null && appDate > 0) {
			LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(appDate), ZoneId.systemDefault());
			obj.setApplicationDate(dateTime.format(DATE_FORMATTER));
		} else {
			obj.setApplicationDate(LocalDateTime.now().format(DATE_FORMATTER));
		}

		LandInfo landInfo = bpa.getLandInfo();
		if (landInfo != null && !CollectionUtils.isEmpty(landInfo.getOwners())) {
			OwnerInfo owner = landInfo.getOwners().get(0);
			obj.setApplicantName(owner.getName() != null ? owner.getName() : "");
			obj.setOwnerName(owner.getName() != null ? owner.getName() : "");
			obj.setApplicantContact(owner.getMobileNumber() != null ? owner.getMobileNumber() : "");
			obj.setApplicantEmail(owner.getEmailId() != null ? owner.getEmailId() : "");

			Address permanentAddr = owner.getPermanentAddress();
			String formattedAddr = permanentAddr != null ? formatAddress(permanentAddr) : "";
			obj.setApplicantAddress(formattedAddr);
			obj.setOwnerAddress(formattedAddr);
		}

		if (landInfo != null) {
			Address landAddress = landInfo.getAddress();
			if (landAddress != null) {
				obj.setSiteAddress(formatAddress(landAddress));
				obj.setSiteCity(landAddress.getCity() != null ? landAddress.getCity() : "");
				obj.setSiteState(landAddress.getState() != null ? landAddress.getState() : "");
			}

			obj.setPlotSize(landInfo.getTotalPlotArea() != null ? landInfo.getTotalPlotArea().doubleValue() : 0.0);
		}

		obj.setStructureType(extractFromAdditionalDetails(bpa, "structureType", "Building"));
        String OccupancyType = landInfo.getUnits().get(0).getOccupancyType();
		obj.setStructurePurpose(OccupancyType != null ? OccupancyType : "Residential");
		obj.setIsInAirportPremises(extractFromAdditionalDetails(bpa, "isInAirportPremises", "No"));
		obj.setPermissionTaken(extractFromAdditionalDetails(bpa, "permissionTaken", "No"));

		return obj;
	}

	/**
	 * Formats Address object to readable string
	 * 
	 * @param address Address object
	 * @return Formatted address
	 */
	private String formatAddress(Address address) {
		if (address == null) {
			return "";
		}

		StringBuilder addr = new StringBuilder();
		
		if (!StringUtils.isEmpty(address.getDoorNo())) {
			addr.append(address.getDoorNo()).append(", ");
		}
		if (!StringUtils.isEmpty(address.getPlotNo())) {
			addr.append("Plot No. ").append(address.getPlotNo()).append(", ");
		}
		if (!StringUtils.isEmpty(address.getStreet())) {
			addr.append(address.getStreet()).append(", ");
		}
		if (!StringUtils.isEmpty(address.getBuildingName())) {
			addr.append(address.getBuildingName()).append(", ");
		}
		if (address.getLocality() != null && !StringUtils.isEmpty(address.getLocality().getName())) {
			addr.append(address.getLocality().getName()).append(", ");
		}
		if (!StringUtils.isEmpty(address.getCity())) {
			addr.append(address.getCity()).append(", ");
		}
		if (!StringUtils.isEmpty(address.getDistrict())) {
			addr.append(address.getDistrict()).append(", ");
		}
		if (!StringUtils.isEmpty(address.getState())) {
			addr.append(address.getState());
		}
		if (!StringUtils.isEmpty(address.getPincode())) {
			addr.append(" - ").append(address.getPincode());
		}

		return addr.toString().replaceAll(", $", "").trim();
	}

	/**
	 * Extracts value from BPA additionalDetails
	 * 
	 * @param bpa BPA object
	 * @param key Key to extract
	 * @param defaultValue Default if not found
	 * @return Extracted value or default
	 */
	@SuppressWarnings("unchecked")
	private String extractFromAdditionalDetails(BPA bpa, String key, String defaultValue) {
		if (bpa == null || bpa.getAdditionalDetails() == null) {
			return defaultValue;
		}

		try {
			if (bpa.getAdditionalDetails() instanceof Map) {
				Map<String, Object> additionalDetails = (Map<String, Object>) bpa.getAdditionalDetails();
				Object value = additionalDetails.get(key);
				return value != null ? String.valueOf(value) : defaultValue;
			}
		} catch (Exception e) {
			log.warn("Error extracting {} from additionalDetails: {}", key, e.getMessage());
		}

		return defaultValue;
	}

	/**
	 * Creates empty XML response
	 * 
	 * @param authorityName Authority name
	 * @return Empty XML
	 */
	private String createEmptyResponse(String authorityName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			
			Element rootElement = doc.createElement(authorityName + "DETAILS");
			doc.appendChild(rootElement);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(STANDALONE, "yes");
			transformer.setOutputProperty(INDENT, "no");
			
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			
			return writer.toString();
		} catch (Exception e) {
			log.error("Error creating empty response", e);
			return "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?><" + authorityName + "DETAILS></" + authorityName + "DETAILS>";
		}
	}

	/**
	 * Extracts coordinates from NOC additionalDetails
	 * For plot > 300 sq m: Extracts 4 corner coordinates (EAST, WEST, NORTH, SOUTH)
	 * For plot <= 300 sq m: Extracts center coordinate (CENTER)
	 */
	private List<SiteCoordinate> extractCoordinatesFromNOC(Noc noc, BPA bpa, RequestInfo requestInfo) {
		List<SiteCoordinate> coordinates = new ArrayList<>();
		if (noc == null || noc.getAdditionalDetails() == null) {
			return coordinates;
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> additionalDetails = (Map<String, Object>) noc.getAdditionalDetails();

		Object siteElevationObj = additionalDetails.get("siteElevation");
		Double siteElevation = null;
		if (siteElevationObj != null) {
			try {
				siteElevation = siteElevationObj instanceof Number 
						? ((Number) siteElevationObj).doubleValue()
						: Double.parseDouble(String.valueOf(siteElevationObj).trim());
			} catch (Exception e) {
				log.warn("Invalid siteElevation: {}", siteElevationObj);
			}
		}

		Double buildingHeight = null;
		if (bpa != null && bpa.getEdcrNumber() != null && !bpa.getEdcrNumber().isEmpty()) {
			buildingHeight = edcrService.fetchBuildingHeight(bpa.getEdcrNumber(), requestInfo);
		}

		String[] directions = {"EAST", "WEST", "NORTH", "SOUTH"};
		for (String direction : directions) {
			Object dirObj = additionalDetails.get(direction);
			if (dirObj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> dirMap = (Map<String, Object>) dirObj;
				Object lat = dirMap.get("latitude");
				Object lon = dirMap.get("longitude");
				if (lat != null && lon != null) {
					coordinates.add(SiteCoordinate.builder()
							.latitude(String.valueOf(lat))
							.longitude(String.valueOf(lon))
							.siteElevation(siteElevation)
							.buildingHeight(buildingHeight)
							.structureNo(1)
							.build());
				}
			}
		}

		if (coordinates.isEmpty()) {
			Object centerObj = additionalDetails.get("CENTER");
			if (centerObj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> centerMap = (Map<String, Object>) centerObj;
				Object lat = centerMap.get("latitude");
				Object lon = centerMap.get("longitude");
				if (lat != null && lon != null) {
					coordinates.add(SiteCoordinate.builder()
							.latitude(String.valueOf(lat))
							.longitude(String.valueOf(lon))
							.siteElevation(siteElevation)
							.buildingHeight(buildingHeight)
							.structureNo(1)
							.build());
				}
			}
		}

		return coordinates;
	}

	/**
	 * Gets document URLs from NOC documents mapped by document type
	 * 
	 * @param noc NOC object with documents
	 * @return Map of document type to file URL
	 */
	private Map<String, String> getDocumentUrls(Noc noc) {
		Map<String, String> documentUrls = new HashMap<>();
		
		if (noc == null || CollectionUtils.isEmpty(noc.getDocuments())) {
			return documentUrls;
		}

		for (Document doc : noc.getDocuments()) {
			if (doc == null || doc.getDocumentType() == null || doc.getFileStoreId() == null) {
				continue;
			}

			String documentType = doc.getDocumentType().toUpperCase();
			if (isValidDocumentType(documentType)) {
				String fileUrl = fileStoreService.getFileUrl(doc.getFileStoreId(), noc.getTenantId());
				if (fileUrl != null && !fileUrl.isEmpty()) {
					documentUrls.put(documentType, fileUrl);
				}
			}
		}

		return documentUrls;
	}

	/**
	 * Validates if document type is a valid AAI document type
	 * 
	 * @param documentType Document type to validate
	 * @return true if valid
	 */
	private boolean isValidDocumentType(String documentType) {
		return NOCConstants.DOC_TYPE_UNDERTAKING1A.equals(documentType) ||
				NOCConstants.DOC_TYPE_SITEELEVATION.equals(documentType) ||
				NOCConstants.DOC_TYPE_SITECORDINATES.equals(documentType) ||
				NOCConstants.DOC_TYPE_AUTHORIZATION.equals(documentType) ||
				NOCConstants.DOC_TYPE_PERMISSION.equals(documentType);
	}

}
