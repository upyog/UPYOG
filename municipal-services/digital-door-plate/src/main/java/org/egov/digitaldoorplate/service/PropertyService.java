package org.egov.digitaldoorplate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.digitaldoorplate.model.DoorPlateQrSnapshot;
import org.egov.digitaldoorplate.model.RemoteProperty;
import org.egov.digitaldoorplate.repository.PropertyOwnerRepository;
import org.egov.digitaldoorplate.repository.ServiceRequestRepository;
import org.egov.digitaldoorplate.util.DdpConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PropertyService {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private PropertyOwnerRepository propertyOwnerRepository;

	@Autowired
	private DdpConstants ddpConfig;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Searches property-services by the property id printed on the door plate
	 * QR code (the "PropertyID" field). Returns the matching property, or
	 * throws if property-services has no record for it.
	 */
	@SuppressWarnings("unchecked")
	public RemoteProperty searchPropertyByPropertyId(RequestInfo requestInfo, String tenantId, String propertyId) {

		StringBuilder uri = new StringBuilder(ddpConfig.getPropertyServiceHostUrl())
				.append(ddpConfig.getPropertySearchEndpoint())
				.append("?tenantId=").append(tenantId)
				.append("&propertyIds=").append(propertyId);

		Map<String, Object> request = new HashMap<>();
		request.put("RequestInfo", requestInfo);

		Optional<Object> response = serviceRequestRepository.fetchResult(uri, request);

		if (!response.isPresent()) {
			throw new CustomException("PROPERTY_SEARCH_FAILED",
					"No response received from property-service while searching the property.");
		}

		Object rawProperties = ((Map<String, Object>) response.get()).get("Properties");
		List<RemoteProperty> properties = null == rawProperties ? null
				: objectMapper.convertValue(rawProperties, new TypeReference<List<RemoteProperty>>() {
				});

		if (CollectionUtils.isEmpty(properties)) {
			throw new CustomException("PROPERTY_NOT_FOUND", "No property found for PropertyID: " + propertyId);
		}

		return properties.stream().filter(property -> StringUtils.equalsIgnoreCase(propertyId, property.getPropertyId()))
				.findFirst().orElse(properties.get(0));
	}

	/**
	 * Builds the {OwnerName, MobileNo, PropertyID, id, ulbName, Ward, Address}
	 * snapshot for a property, in the same shape as a door plate QR payload:
	 * the primary owner's name, the owner's mobile number read straight from
	 * eg_pt_owner (property-services' own /property/_search overwrites it with
	 * egov-user's stored number), and the address fields kept under the
	 * address's additionalDetails. {@code id} is echoed back as given, not
	 * read from the property. Throws if property-services has no record for
	 * propertyId.
	 */
	public DoorPlateQrSnapshot buildQrSnapshot(RequestInfo requestInfo, String tenantId, String propertyId, String id) {

		RemoteProperty property = searchPropertyByPropertyId(requestInfo, tenantId, propertyId);

		RemoteProperty.Owner owner = findPrimaryOwner(property.getOwners());
		JsonNode addressDetails = null == property.getAddress() ? null : property.getAddress().getAdditionalDetails();

		String mobileNo = propertyOwnerRepository.getOwnerMobileNumber(property.getPropertyId(),
				null == property.getTenantId() ? tenantId : property.getTenantId());
		if (StringUtils.isEmpty(mobileNo) && null != owner) {
			mobileNo = owner.getMobileNumber();
		}

		return DoorPlateQrSnapshot.builder()
				.ownerName(null == owner ? null : owner.getPropertyOwnerName())
				.mobileNo(mobileNo)
				.propertyId(property.getPropertyId())
				.id(id)
				.ulbName(readText(addressDetails, "ulbName"))
				.ward(readText(addressDetails, "wardNumber"))
				.address(readText(addressDetails, "propertyAddress"))
				.build();
	}

	/**
	 * Best-effort variant of {@link #buildQrSnapshot} for enrichment scenarios
	 * (e.g. the garbage-collection scan API) where a missing/failed
	 * property-service lookup should not block the primary operation; returns
	 * null (and logs a warning) instead of throwing.
	 */
	public DoorPlateQrSnapshot buildQrSnapshotQuietly(RequestInfo requestInfo, String tenantId, String propertyId,
			String id) {

		if (StringUtils.isEmpty(propertyId)) {
			return null;
		}
		try {
			return buildQrSnapshot(requestInfo, tenantId, propertyId, id);
		} catch (Exception e) {
			log.warn("Unable to build property snapshot for propertyId {}: {}", propertyId, e.getMessage());
			return null;
		}
	}

	private RemoteProperty.Owner findPrimaryOwner(List<RemoteProperty.Owner> owners) {
		if (CollectionUtils.isEmpty(owners)) {
			return null;
		}
		return owners.stream().filter(owner -> Boolean.TRUE.equals(owner.getIsPrimaryOwner())).findFirst()
				.orElse(owners.get(0));
	}

	private String readText(JsonNode node, String fieldName) {
		if (null == node) {
			return null;
		}
		JsonNode value = node.path(fieldName);
		return value.isMissingNode() || value.isNull() ? null : value.asText();
	}
}
