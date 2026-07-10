package org.egov.wscalculation.util;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.config.WSCalculationConfiguration;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.web.models.*;
import org.egov.wscalculation.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Slf4j
public class WSCalculationUtil {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private WSCalculationConfiguration configurations;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${egov.property.service.host}")
	private String propertyHost;

	@Value("${egov.property.searchendpoint}")
	private String searchPropertyEndPoint;

	private final String tenantId = "tenantId=";
	private final String mobileNumber = "mobileNumber=";
	private final String propertyIds = "propertyIds=";
	private final String uuids = "uuids=";

	/**
	 * Returns the tax head search Url with tenantId and WS service name parameters
	 * 
	 * @param tenantId Tenant Id
	 * @param serviceFieldValue Service Field Value
	 * @return Returns Tax Period Search URL
	 */
	public StringBuilder getTaxPeriodSearchUrl(String tenantId, String serviceFieldValue) {
		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getTaxPeriodSearchEndpoint()).append(WSCalculationConstant.URL_PARAMS_SEPARATER)
				.append(WSCalculationConstant.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(WSCalculationConstant.SEPARATER).append(WSCalculationConstant.SERVICE_FIELD_FOR_SEARCH_URL)
				.append(serviceFieldValue);
	}

	/**
	 * Returns the tax head search Url with tenantId and WS service name parameters
	 * 
	 * @param tenantId TenantId
	 * @param serviceFieldValue Service Field Value
	 * @return Returns the Tax Head Search URL
	 */
	public StringBuilder getTaxHeadSearchUrl(String tenantId, String serviceFieldValue) {

		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getTaxheadsSearchEndpoint()).append(WSCalculationConstant.URL_PARAMS_SEPARATER)
				.append(WSCalculationConstant.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(WSCalculationConstant.SEPARATER).append(WSCalculationConstant.SERVICE_FIELD_FOR_SEARCH_URL)
				.append(serviceFieldValue);
	}

	/**
	 * method to create demand search url with demand criteria
	 *
	 * @param getBillCriteria Bill Criteria to search
	 * @return - Returns Demand Search URL
	 */
	public StringBuilder getDemandSearchUrl(GetBillCriteria getBillCriteria) {

		StringBuilder url;
		if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes()))
			url = new StringBuilder().append(configurations.getBillingServiceHost())
					.append(configurations.getDemandSearchEndPoint()).append(WSCalculationConstant.URL_PARAMS_SEPARATER)
					.append(WSCalculationConstant.TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
					.append(WSCalculationConstant.SEPARATER)
					.append(WSCalculationConstant.CONSUMER_CODE_SEARCH_FIELD_NAME)
					.append(getBillCriteria.getConnectionId())
					.append(WSCalculationConstant.WS_CONSUMER_CODE_SEPARATOR)
					.append(getBillCriteria.getConnectionNumber())
					.append(WSCalculationConstant.SEPARATER)
					.append(WSCalculationConstant.BUSINESSSERVICE_FIELD_FOR_SEARCH_URL)
					.append("WS")
				
						;

		else {
			 url = new StringBuilder().append(configurations.getBillingServiceHost())
					.append(configurations.getDemandSearchEndPoint()).append(WSCalculationConstant.URL_PARAMS_SEPARATER)
					.append(WSCalculationConstant.TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
					.append(WSCalculationConstant.SEPARATER)
					.append(WSCalculationConstant.CONSUMER_CODE_SEARCH_FIELD_NAME)
					.append(StringUtils.join(getBillCriteria.getConsumerCodes(), ","))
					.append(WSCalculationConstant.SEPARATER)
					.append(WSCalculationConstant.BUSINESSSERVICE_FIELD_FOR_SEARCH_URL)
					.append("WS");

			 if(getBillCriteria.getIsPaymentCompleted() != null)
				 url.append(WSCalculationConstant.SEPARATER)
				 .append(WSCalculationConstant.PAYMENT_COMPLETED_SEARCH_FIELD_NAME)
				 .append(getBillCriteria.getIsPaymentCompleted());
		}

		return url;
	}

	/**
	 * Returns url for demand update Api
	 *
	 * @return Returns UpdateDemandUrl
	 */
	public StringBuilder getUpdateDemandUrl() {
		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getDemandUpdateEndPoint());
	}

	public DemandDetailAndCollection getLatestDemandDetailByTaxHead(String taxHeadCode,
			List<DemandDetail> demandDetails) {
		List<DemandDetail> details = demandDetails.stream()
				.filter(demandDetail -> demandDetail.getTaxHeadMasterCode().equalsIgnoreCase(taxHeadCode))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(details))
			return null;

		BigDecimal taxAmountForTaxHead = BigDecimal.ZERO;
		BigDecimal collectionAmountForTaxHead = BigDecimal.ZERO;
		DemandDetail latestDemandDetail = null;
		long maxCreatedTime = 0L;

		for (DemandDetail detail : details) {
			taxAmountForTaxHead = taxAmountForTaxHead.add(detail.getTaxAmount());
			collectionAmountForTaxHead = collectionAmountForTaxHead.add(detail.getCollectionAmount());
			if (detail.getAuditDetails().getCreatedTime() > maxCreatedTime) {
				maxCreatedTime = detail.getAuditDetails().getCreatedTime();
				latestDemandDetail = detail;
			}
		}

		return DemandDetailAndCollection.builder().taxHeadCode(taxHeadCode).latestDemandDetail(latestDemandDetail)
				.taxAmountForTaxHead(taxAmountForTaxHead).collectionAmountForTaxHead(collectionAmountForTaxHead)
				.build();

	}

	/**
	 * 
	 * @param waterConnectionRequest
	 *            WaterConnectionRequest containing property
	 * @return List of Property
	 */
	public List<Property> propertySearch(WaterConnectionRequest waterConnectionRequest) {
		String propertyId = waterConnectionRequest.getWaterConnection().getPropertyId();
		String tenantId = waterConnectionRequest.getWaterConnection().getTenantId();

		List<Property> propertyList = searchPropertiesFromES(
				waterConnectionRequest.getRequestInfo(),
				Collections.singleton(propertyId),
				null,
				null,
				tenantId,
				1L
		);
		if (CollectionUtils.isEmpty(propertyList)) {
			throw new CustomException("INCORRECT_PROPERTY_ID", "PROPERTY SEARCH ERROR!");
		}
		return propertyList;
	}

	public List<Property> propertySearch(RequestInfo requestInfo, Set<String> propertyIds, String tenantId, Long limit) {
		return searchPropertiesFromES(requestInfo, propertyIds, null, null, tenantId, limit);
	}

	private List<Property> searchPropertiesFromES(RequestInfo requestInfo, Set<String> propertyIds, String mobileNumber, Set<String> uuids, String tenantId, Long limit) {
		List<Map<String, Object>> mustList = new ArrayList<>();

		if (tenantId != null) {
			mustList.add(Collections.singletonMap("term", Collections.singletonMap("Data.tenantId.keyword", tenantId)));
		}
		if (!CollectionUtils.isEmpty(propertyIds)) {
			mustList.add(Collections.singletonMap("terms", Collections.singletonMap("Data.propertyId.keyword", propertyIds)));
		}
		if (mobileNumber != null) {
			mustList.add(Collections.singletonMap("term", Collections.singletonMap("Data.owners.mobileNumber.keyword", mobileNumber)));
		}
		if (!CollectionUtils.isEmpty(uuids)) {
			mustList.add(Collections.singletonMap("terms", Collections.singletonMap("Data.owners.uuid.keyword", uuids)));
		}

		Map<String, Object> queryMap = new HashMap<>();
		if (!mustList.isEmpty()) {
			Map<String, Object> boolMap = new HashMap<>();
			boolMap.put("must", mustList);
			queryMap.put("query", Collections.singletonMap("bool", boolMap));
		} else {
			queryMap.put("query", Collections.singletonMap("match_all", new HashMap<>()));
		}
		queryMap.put("size", limit != null ? limit : 50);

		String host = configurations.getElasticsearchHost();
		String index = configurations.getPropertyEsIndex();
		String endpoint = configurations.getElasticsearchSearchEndpoint();
		if (host == null) {
			host = "http://localhost:9200/";
		}
		if (!host.endsWith("/")) {
			host = host + "/";
		}
		if (index == null) {
			index = "property-services";
		}
		if (endpoint == null) {
			endpoint = "/_search";
		}
		if (endpoint.startsWith("/")) {
			endpoint = endpoint.substring(1);
		}
		String esUrl = host + index + "/" + endpoint;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (configurations.getElasticsearchUsername() != null && !configurations.getElasticsearchUsername().isEmpty() &&
			configurations.getElasticsearchPassword() != null && !configurations.getElasticsearchPassword().isEmpty()) {
			headers.setBasicAuth(configurations.getElasticsearchUsername(), configurations.getElasticsearchPassword());
		}

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(queryMap, headers);
		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(esUrl, requestEntity, Map.class);
			if (response.getBody() != null) {
				Map hitsMap = (Map) response.getBody().get("hits");
				if (hitsMap != null) {
					List hitsList = (List) hitsMap.get("hits");
					if (hitsList != null) {
						List<Property> properties = new ArrayList<>();
						for (Object hitObj : hitsList) {
							Map hit = (Map) hitObj;
							Map source = (Map) hit.get("_source");
							if (source != null && source.get("Data") != null) {
								Property property = objectMapper.convertValue(source.get("Data"), Property.class);
								properties.add(property);
							}
						}
						return properties;
					}
				}
			}
		} catch (Exception e) {
			log.error("Error occurred while fetching property from ElasticSearch, falling back to database REST API", e);
		}

		// Fallback to database REST API
		PropertyCriteria propertyCriteria = PropertyCriteria.builder()
				.propertyIds(propertyIds)
				.tenantId(tenantId)
				.mobileNumber(mobileNumber)
				.uuids(uuids)
				.limit(limit)
				.build();
		try {
			StringBuilder url = getPropertyURL(propertyCriteria);
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
					.requestInfo(requestInfo)
					.build();
			Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
			return getPropertyDetails(result);
		} catch (Exception ex) {
			log.error("Failed to fetch property from database REST API during fallback", ex);
			throw new CustomException("PROPERTY_SEARCH_ERROR", "Failed to fetch property from database REST API: " + ex.getMessage());
		}
	}
	
	/**
	 * 
	 * @param waterConnectionRequest
	 *            WaterConnectionRequest
	 */
	public Property getProperty(WaterConnectionRequest waterConnectionRequest) {
		Optional<Property> propertyList = propertySearch(waterConnectionRequest).stream().findFirst();
		if (!propertyList.isPresent()) {
			throw new CustomException("INVALID_WATER_CONNECTION_PROPERTY",
					"Water connection cannot be enriched without property");
		}
		Property property = propertyList.get();
		if (StringUtils.isEmpty(property.getUsageCategory())) {
			throw new CustomException("INVALID_WATER_CONNECTION_PROPERTY_USAGE_TYPE",
					"Water connection cannot be enriched without property usage type");
		}
		return property;
	}

	/**
	 * 
	 * @param criteria Property Search Criteria
	 * @return property URL
	 */
	private StringBuilder getPropertyURL(PropertyCriteria criteria) {
		StringBuilder url = new StringBuilder(getPropertyURL());
		boolean isanyparametermatch = false;
		url.append("?");
		if (!StringUtils.isEmpty(criteria.getTenantId())) {
			isanyparametermatch = true;
			url.append(tenantId).append(criteria.getTenantId());
		}
		if (!CollectionUtils.isEmpty(criteria.getPropertyIds())) {
			if (isanyparametermatch)
				url.append("&");
			isanyparametermatch = true;
			String propertyIdsString = criteria.getPropertyIds().stream().map(propertyId -> propertyId)
					.collect(Collectors.toSet()).stream().collect(Collectors.joining(","));
			url.append(propertyIds).append(propertyIdsString);
		}
		if (!StringUtils.isEmpty(criteria.getMobileNumber())) {
			if (isanyparametermatch)
				url.append("&");
			isanyparametermatch = true;
			url.append(mobileNumber).append(criteria.getMobileNumber());
		}
		if (!CollectionUtils.isEmpty(criteria.getUuids())) {
			if (isanyparametermatch)
				url.append("&");
			String uuidString = criteria.getUuids().stream().map(uuid -> uuid).collect(Collectors.toSet()).stream()
					.collect(Collectors.joining(","));
			url.append(uuids).append(uuidString);
		}
		if ((criteria.getLimit()) != null) {
			if (isanyparametermatch)
				url.append("&");
			isanyparametermatch = true;
			url.append("limit=").append(criteria.getLimit());
		}
		return url;
	}
	
	
	
//	public StringBuilder getBillSearchUrl(GetBillCriteria getBillCriteria) {
//	    return new StringBuilder().append(configurations.getBillingServiceHost())
//	            .append(configurations.getSearchBillEndPoints())
//	            .append(WSCalculationConstant.URL_PARAMS_SEPARATER)
//	            .append(WSCalculationConstant.TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
//	            .append(WSCalculationConstant.SEPARATER)
//	            .append(WSCalculationConstant.CONSUMER_CODE_SEARCH_FIELD_NAME)
//	            .append(StringUtils.join(getBillCriteria.getConsumerCodes(), ","))
//	            .append(WSCalculationConstant.SEPARATER)
//	            // Change this specific line:
//	            .append("service=") // Hardcoded 'service' to satisfy billing-service requirement
//	            .append("WS");
//	}
	
	public StringBuilder getBillSearchUrl(GetBillCriteria getBillCriteria) {
	    return new StringBuilder().append(configurations.getBillingServiceHost())
	            .append(configurations.getSearchBillEndPoints())
	            .append(WSCalculationConstant.URL_PARAMS_SEPARATER)
	            .append(WSCalculationConstant.TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
	            .append(WSCalculationConstant.SEPARATER)
	            .append(WSCalculationConstant.CONSUMER_CODE_SEARCH_FIELD_NAME)
	            .append(StringUtils.join(getBillCriteria.getConsumerCodes(), ","))
	            .append(WSCalculationConstant.SEPARATER)
	            // Use the constant instead of hardcoding "service=" and "WS"
	            .append(WSCalculationConstant.SERVICE_FIELD_FOR_SEARCH_URL) 
	            .append(WSCalculationConstant.SERVICE_FIELD_VALUE_WS);
	}
	

	/**
	 * 
	 * @param result
	 *            Response object from property service call
	 * @return List of property
	 */
	private List<Property> getPropertyDetails(Object result) {
		try {
			PropertyResponse propertyResponse = objectMapper.convertValue(result, PropertyResponse.class);
			return propertyResponse.getProperties();
		} catch (Exception ex) {
			throw new CustomException("PARSING_ERROR", "The property json cannot be parsed");
		}
	}

	public StringBuilder getPropertyURL() {
		return new StringBuilder().append(propertyHost).append(searchPropertyEndPoint);
	}
}
