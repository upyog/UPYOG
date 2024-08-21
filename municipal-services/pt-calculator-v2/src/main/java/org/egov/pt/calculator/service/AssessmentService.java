package org.egov.pt.calculator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.pt.calculator.repository.AssessmentRepository;
import org.egov.pt.calculator.repository.PTCalculatorRepository;
import org.egov.pt.calculator.repository.Repository;
import org.egov.pt.calculator.util.CalculatorConstants;
import org.egov.pt.calculator.util.CalculatorUtils;
import org.egov.pt.calculator.util.Configurations;
import org.egov.pt.calculator.web.models.Assessment;
import org.egov.pt.calculator.web.models.AssessmentRequest;
import org.egov.pt.calculator.web.models.AssessmentResponse;
import org.egov.pt.calculator.web.models.CalculationReq;
import org.egov.pt.calculator.web.models.CreateAssessmentRequest;
import org.egov.pt.calculator.web.models.DefaultersInfo;
import org.egov.pt.calculator.web.models.demand.Demand;
import org.egov.pt.calculator.web.models.property.AuditDetails;
import org.egov.pt.calculator.web.models.property.Channel;
import org.egov.pt.calculator.web.models.property.Property;
import org.egov.pt.calculator.web.models.property.PropertyResponse;
import org.egov.pt.calculator.web.models.property.RequestInfoWrapper;
import org.egov.pt.calculator.web.models.propertyV2.AssessmentV2.Source;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

/**
 * AssesmentService
 * 
 * Serves for the purpose of saving and retrieving of assessments
 * 
 * @author kavi elrey
 */
@Service
@Slf4j
public class AssessmentService {

	@Autowired
	private CalculatorUtils utils;

	@Autowired
	private AssessmentRepository repository;

	@Autowired
	private PTCalculatorRepository ptCalculatorRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private UserService userService;

	@Autowired
	private MasterDataService mdmsService;

	@Autowired
	private Configurations configs;

	@Autowired
	private Repository mdmsRepository;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * persists the assessments
	 * <p>
	 * adds the data to the respective kafka topic
	 *
	 * @param demands
	 * @param info
	 */
	public List<Assessment> saveAssessments(List<Demand> demands, Map<String, String> consumerCodeFinYearMap,
			RequestInfo info) {

		List<Assessment> assessments = new ArrayList<>();

		AuditDetails details = utils.getAuditDetails(info.getUserInfo().getId().toString(), true);
		demands.forEach(demand -> {

			String[] consumerCodeSplitArray = demand.getConsumerCode()
					.split(CalculatorConstants.PT_CONSUMER_CODE_SEPARATOR);
			assessments.add(Assessment.builder().propertyId(consumerCodeSplitArray[0])
					.assessmentYear(consumerCodeFinYearMap.get(demand.getConsumerCode()))
					.uuid(UUID.randomUUID().toString()).assessmentNumber(consumerCodeSplitArray[1])
					.tenantId(demand.getTenantId()).demandId(demand.getId()).auditDetails(details).build());
		});
		return repository.saveAssessments(assessments, info);
	}

	/**
	 * Returns the latest assessment for the given criteria
	 *
	 * @param assessment
	 * @return
	 */
	public List<Assessment> getMaxAssessment(Assessment assessment) {

		List<Object> preparedStatementList = new ArrayList<>();
		String query = utils.getMaxAssessmentQuery(assessment, preparedStatementList);
		return repository.getAssessments(query, preparedStatementList.toArray());
	}

	/**
	 * Returns list of assessments for the given criteria
	 *
	 * @param assessment
	 * @return
	 */
	public List<Assessment> getAssessments(Assessment assessment) {

		List<Object> preparedStatementList = new ArrayList<>();
		String query = utils.getAssessmentQuery(assessment, preparedStatementList);
		return repository.getAssessments(query, preparedStatementList.toArray());
	}

	/**
	 * Enriches the property object from assessment numnber
	 *
	 * @param calculationReq The input calculation requests
	 */
	public void enrichAssessment(CalculationReq calculationReq) {

		Map<String, Property> propertyMap = getPropertyMap(calculationReq);

		Map<String, String> errorMap = new HashMap<>();

		calculationReq.getCalculationCriteria().forEach(calculationCriteria -> {
			if (propertyMap.containsKey(calculationCriteria.getAssessmentNumber()))
				calculationCriteria.setProperty(propertyMap.get(calculationCriteria.getAssessmentNumber()));
			else
				errorMap.put("INVALID_CRITERIA", "Property for the assessment number : "
						+ calculationCriteria.getAssessmentNumber() + " is not found ");
		});

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

	}

	/**
	 * Searches property based on assessment number and returns a map
	 *
	 * @param calculationReq The CalculationReq for which demand is to be generated
	 * @return Map of assessmentNumber to property
	 */
	private Map<String, Property> getPropertyMap(CalculationReq calculationReq) {

		String tenantId = calculationReq.getCalculationCriteria().get(0).getTenantId();
		RequestInfo requestInfo = calculationReq.getRequestInfo();
		List<String> assessmentNumbers = new LinkedList<>();

		calculationReq.getCalculationCriteria().forEach(calculationCriteria -> {
			assessmentNumbers.add(calculationCriteria.getAssessmentNumber());
		});

		StringBuilder url = utils.getPTSearchQuery(tenantId, assessmentNumbers);

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		Object res = ptCalculatorRepository.fetchResult(url, requestInfoWrapper);

		PropertyResponse response = mapper.convertValue(res, PropertyResponse.class);

		Map<String, Property> propertyMap = new HashMap<>();

		response.getProperties().forEach(property -> {
			propertyMap.put(property.getPropertyDetails().get(0).getAssessmentNumber(), property);
		});

		return propertyMap;
	}

	@SuppressWarnings("unchecked")
	public List<Assessment> createAssessmentsForFY(CreateAssessmentRequest assessmentRequest) {
		Map<String, Map<String, Object>> scheduledTenants = fetchScheduledTenants(assessmentRequest.getRequestInfo());

		User user = userService.fetchPTAsseessmentUser();
		RequestInfo requestInfo = assessmentRequest.getRequestInfo();
		requestInfo.setUserInfo(user);
		List<Assessment> assessedProperties = new ArrayList<Assessment>();

		for (Entry<String, Map<String, Object>> tenantConfig : scheduledTenants.entrySet()) {
			Map<String, Object> configData = tenantConfig.getValue();
			List<String> locality = (List<String>) configData.get(CalculatorConstants.LOCALITY_KEY);
			List<String> propertyType = (List<String>) configData.get(CalculatorConstants.PROPERTYTYPE_KEY);
			assessmentRequest.setTenantId(tenantConfig.getKey());
			assessmentRequest.setAssessmentYear(configData.get(CalculatorConstants.FINANCIALYEAR_KEY).toString());
			assessmentRequest.setLocality(locality);
			assessmentRequest.setPropertyType(propertyType);
			assessmentRequest.setIsRented(configData.get(CalculatorConstants.IS_RENTED) == null ? true
					: (Boolean) configData.get(CalculatorConstants.IS_RENTED));

			int count = repository.getActivePropertyCount(assessmentRequest);
			Long limit = configs.getMaxSearchLimit();
			Long offset = configs.getDefaultOffset();
			while (limit < count) {

				assessmentRequest.setOffset(offset);
				assessmentRequest.setLimit(configs.getDefaultLimit());

				List<Property> properties = repository.fetchAllActivePropertieswithLimit(assessmentRequest);
				for (Property property : properties) {
					boolean isExists = repository.isAssessmentExists(property.getPropertyId(),
							assessmentRequest.getAssessmentYear(), property.getTenantId());
					if (!isExists) {

						Assessment assessment = Assessment.builder()
								.financialYear(assessmentRequest.getAssessmentYear())
								.propertyId(property.getPropertyId()).source(Source.MUNICIPAL_RECORDS)
								.channel(Channel.CFC_COUNTER).assessmentDate(System.currentTimeMillis())
								.tenantId(assessmentRequest.getTenantId()).build();
						AssessmentRequest assessmentReq = AssessmentRequest.builder().assessment(assessment)
								.requestInfo(requestInfo).build();
						String url = new StringBuilder().append(configs.getAssessmentServiceHost())
								.append(configs.getAssessmentCreateEndpoint()).toString();
						AssessmentResponse response = null;
						try {
							response = restTemplate.postForObject(url, assessmentReq, AssessmentResponse.class);
							Assessment createdAsessment = response.getAssessments().get(0);
							repository.saveAssessmentGenerationDetails(createdAsessment, "SUCCESS", "Assessment", null);
							assessedProperties.add(createdAsessment);
						} catch (HttpClientErrorException e) {
							repository.saveAssessmentGenerationDetails(assessment, "FAILED", "Assessment",
									e.toString());
						} catch (Exception e) {
							repository.saveAssessmentGenerationDetails(assessment, "FAILED", "Assessment",
									e.toString());
						}

					}

				}
				offset = limit;
				limit = limit + configs.getMaxSearchLimit();
			}
		}
		return assessedProperties;
	}

	@SuppressWarnings("unchecked")
	public void createReAssessmentsForFY(CreateAssessmentRequest assessmentRequest) {

		Map<String, Object> config = fetchReAssessmentConfig(assessmentRequest.getRequestInfo());
		List<String> tenants = (List<String>) config.get(CalculatorConstants.TENANT_KEY);
		User user = userService.fetchPTAsseessmentUser();
		RequestInfo requestInfo = assessmentRequest.getRequestInfo();
		requestInfo.setUserInfo(user);
		Map<String, Long> finYearDates = new HashMap<>();
		String finYear = null;
		if (StringUtils.isNotBlank(((String) config.get(CalculatorConstants.FINANCIALYEAR_KEY)))) {
			finYear = (String) config.get(CalculatorConstants.FINANCIALYEAR_KEY);
			finYearDates = getFinancialYearDates(requestInfo, finYear, configs.getStateLevelTenantId());
		}
		for (String tenant : tenants) {
			List<DefaultersInfo> dueproperties = repository.fetchAllPropertiesForReAssess(
					finYearDates.get(CalculatorConstants.FINANCIAL_YEAR_STARTING_DATE),
					finYearDates.get(CalculatorConstants.FINANCIAL_YEAR_ENDING_DATE), tenant);
			log.info("Total properites with due: " + dueproperties.size());

			for (DefaultersInfo property : dueproperties) {
				final List<Assessment> assessments = repository.fetchAssessments(property.getPropertyId(), finYear,
						tenant);
				if (assessments.isEmpty()) {

					log.info("No assessments");
				} else {
					Assessment assessment = assessments.get(0);
					AssessmentRequest assessmentReq = AssessmentRequest.builder().assessment(assessment)
							.requestInfo(requestInfo).build();
					String url = new StringBuilder().append(configs.getAssessmentServiceHost())
							.append(configs.getAssessmentUpdateEndpoint()).toString();
					AssessmentResponse response = null;
					log.info("re-assess request:" + assessmentReq);
					try {
						response = restTemplate.postForObject(url, assessmentReq, AssessmentResponse.class);
						log.info("re-assess response:" + response);
						Assessment createdAsessment = response.getAssessments().get(0);

						repository.saveAssessmentGenerationDetails(createdAsessment, "SUCCESS", "Re-Assess", null);
					} catch (HttpClientErrorException e) {
						repository.saveAssessmentGenerationDetails(assessment, "FAILED", "Re-Assess", e.toString());
					} catch (Exception e) {
						repository.saveAssessmentGenerationDetails(assessment, "FAILED", "Re-Assess", e.toString());
					}

				}

			}
		}

	}

	@SuppressWarnings("unchecked")
	public void cancelAssessmentsForFY(CreateAssessmentRequest assessmentRequest) {
		Map<String, Map<String, Object>> scheduledTenants = fetchCancelAssessmentScheduledTenants(
				assessmentRequest.getRequestInfo());

		User user = userService.fetchPTAsseessmentUser();
		RequestInfo requestInfo = assessmentRequest.getRequestInfo();
		requestInfo.setUserInfo(user);

		for (Entry<String, Map<String, Object>> tenantConfig : scheduledTenants.entrySet()) {
			Map<String, Object> configData = tenantConfig.getValue();
			List<String> locality = (List<String>) configData.get(CalculatorConstants.LOCALITY_KEY);
			List<String> propertyType = (List<String>) configData.get(CalculatorConstants.PROPERTYTYPE_KEY);
			assessmentRequest.setTenantId(tenantConfig.getKey());
			assessmentRequest.setAssessmentYear(configData.get(CalculatorConstants.FINANCIALYEAR_KEY).toString());
			assessmentRequest.setLocality(locality);
			assessmentRequest.setPropertyType(propertyType);
			assessmentRequest.setIsRented(configData.get(CalculatorConstants.IS_RENTED) == null ? true
					: (Boolean) configData.get(CalculatorConstants.IS_RENTED));
			List<Property> properties = repository.fetchAllActiveProperties(assessmentRequest);
			for (Property property : properties) {
				boolean isExists = repository.isAssessmentExistsForCancellation(property.getPropertyId(),
						assessmentRequest.getAssessmentYear(), property.getTenantId());
				if (isExists) // if assessment exist for configured FY
				{
					// {"RequestInfo":{"apiId":"Rainmaker","ver":".01","ts":"","action":"_cancel","did":"1","key":"","msgId":"20170310130900|en_IN","authToken":"22db262d-6902-4295-a0de-b810c5c73389"},"Assessment":{"id":"74f77f7d-3d36-4fb0-82d5-64dbb748d1d9","tenantId":"pb.testing","assessmentNumber":"AS-2022-08-17-5781704","financialYear":"2021-22","propertyId":"PT-1012-1243333","assessmentDate":1660714845244,"status":"ACTIVE","source":"MUNICIPAL_RECORDS","unitUsageList":[],"documents":[],"additionalDetails":{},"channel":null,"auditDetails":{"createdBy":"9b05ad7f-0152-40ca-9938-f328cc41ea88","lastModifiedBy":"9b05ad7f-0152-40ca-9938-f328cc41ea88","createdTime":1660714904989,"lastModifiedTime":1660714904989},"workflow":null}}
					// https://mseva.lgpunjab.gov.in/property-services/assessment/_cancel?tenantId=pb.testing
					// SEARCH ASSESSMENT URL:
					// https://mseva-uat.lgpunjab.gov.in/property-services/assessment/_search?propertyIds=PT-1012-1036156&tenantId=pb.testing&financialYear=2021-22

					String url = new StringBuilder().append(configs.getAssessmentServiceHost())
							.append(configs.getAssessmentSearchEndpoint()).toString();
					url = url.concat(
							"?propertyIds=" + property.getPropertyId() + "&tenantId=" + assessmentRequest.getTenantId()
									+ "&financialYear=" + assessmentRequest.getAssessmentYear());

					/*
					 * Assessment assessment =
					 * Assessment.builder().financialYear(assessmentRequest.getAssessmentYear())
					 * .propertyId(property.getPropertyId()).source(Source.MUNICIPAL_RECORDS)
					 * .channel(Channel.CFC_COUNTER).assessmentDate(System.currentTimeMillis())
					 * .tenantId(assessmentRequest.getTenantId()).build();
					 * 
					 * 
					 * 
					 * String url = new StringBuilder().append(configs.getAssessmentServiceHost())
					 * .append(configs.getAssessmentCancelEndpoint()).toString();
					 * url=url.concat("?tenantId="+assessmentRequest.getTenantId());
					 */

					// BELOW to be used to create new assessment for insert query in database
					/*
					 * Assessment assessment =
					 * Assessment.builder().financialYear(assessmentRequest.getAssessmentYear())
					 * .propertyId(property.getPropertyId()).source(Source.MUNICIPAL_RECORDS)
					 * .channel(Channel.CFC_COUNTER).assessmentDate(System.currentTimeMillis())
					 * .tenantId(assessmentRequest.getTenantId()).build();
					 */
					Assessment assessment = null;

					AssessmentRequest assessmentReq = AssessmentRequest.builder().assessment(assessment)
							.requestInfo(requestInfo).build();
					AssessmentResponse response = null;
					String urll;
					Assessment asmt1 = null;// needed for catch block

					try {
						response = restTemplate.postForObject(url, assessmentReq, AssessmentResponse.class);
						// Assessment assesssment = response.getAssessments().get(0);

						for (Assessment asmt : response.getAssessments()) {
							asmt1 = asmt; // needed for catch block
							// INSERT CODE TO CANCEL THE ASSESSMENT HERE
							urll = new StringBuilder().append(configs.getAssessmentServiceHost())
									.append(configs.getAssessmentCancelEndpoint()).toString();
							assessmentReq = AssessmentRequest.builder().assessment(asmt).requestInfo(requestInfo)
									.build();
							response = restTemplate.postForObject(urll, assessmentReq, AssessmentResponse.class);

							repository.saveAssessmentGenerationDetails(asmt, "CANCELLED", "Assessment", null);

						}

					} catch (HttpClientErrorException e) {
						repository.saveAssessmentGenerationDetails(asmt1, "CANCELFAILED", "Assessment", e.toString());
					} catch (Exception e) {
						repository.saveAssessmentGenerationDetails(asmt1, "CANCELFAILED", "Assessment", e.toString());
					}

				}

			}
		}

	}

	private Map<String, Long> getFinancialYearDates(RequestInfo requestInfo, String finYear, String tenantId) {
		Map<String, Long> finDates = new HashMap<>();
		Map<String, Map<String, Object>> finYearMap = mdmsService.getFinancialYear(tenantId, requestInfo,
				new HashSet<>(Arrays.asList(finYear)));
		finDates.put(CalculatorConstants.FINANCIAL_YEAR_STARTING_DATE,
				Long.valueOf(finYearMap.get(finYear).get(CalculatorConstants.FINANCIAL_YEAR_STARTING_DATE).toString()));
		finDates.put(CalculatorConstants.FINANCIAL_YEAR_ENDING_DATE,
				Long.valueOf(finYearMap.get(finYear).get(CalculatorConstants.FINANCIAL_YEAR_ENDING_DATE).toString()));
		return finDates;

	}

	private Map<String, Map<String, Object>> fetchScheduledTenants(RequestInfo request) {

		StringBuilder mdmsURL = utils.getMdmsSearchUrl();
		MdmsCriteriaReq mdmsConfig = utils.getAssessmentConfigRequest(request, configs.getStateLevelTenantId());

		try {
			Object response = mdmsRepository.fetchResult(mdmsURL, mdmsConfig);
			List<Map<String, Object>> jsonOutput = JsonPath.read(response,
					CalculatorConstants.MDMS_ASSESSMENT_JOB_CONFIG_PATH);
			Map<String, Map<String, Object>> scheduledTenants = new HashMap<>();

			for (Map<String, Object> config : jsonOutput) {
				Map<String, Object> tenantConfig = new HashMap<>();
				tenantConfig.put(CalculatorConstants.LOCALITY_KEY, config.get(CalculatorConstants.LOCALITY_KEY));
				tenantConfig.put(CalculatorConstants.PROPERTYTYPE_KEY,
						config.get(CalculatorConstants.PROPERTYTYPE_KEY));
				tenantConfig.put(CalculatorConstants.FINANCIALYEAR_KEY,
						config.get(CalculatorConstants.FINANCIALYEAR_KEY));
				tenantConfig.put(CalculatorConstants.IS_RENTED, config.get(CalculatorConstants.IS_RENTED));
				scheduledTenants.put(config.get(CalculatorConstants.TENANT_KEY).toString(), tenantConfig);
			}
			return scheduledTenants;
		} catch (Exception e) {
			throw new CustomException(CalculatorConstants.ASSESSMENT_JOB_MDMS_ERROR,
					CalculatorConstants.ASSESSMENT_JOB_MDMS_ERROR_MSG);
		}
	}

	private Map<String, Object> fetchReAssessmentConfig(RequestInfo request) {

		StringBuilder mdmsURL = utils.getMdmsSearchUrl();

		MdmsCriteriaReq mdmsConfig = utils.getReAssessmentConfigRequest(request, configs.getStateLevelTenantId());
		try {
			Object response = mdmsRepository.fetchResult(mdmsURL, mdmsConfig);
			List<Map<String, Object>> jsonOutput = JsonPath.read(response,
					CalculatorConstants.MDMS_RE_ASSESSMENT_JOB_CONFIG_PATH);
			Map<String, Object> tenantConfig = new HashMap<>();
			for (Map<String, Object> config : jsonOutput) {

				tenantConfig.put(CalculatorConstants.FINANCIALYEAR_KEY,
						config.get(CalculatorConstants.FINANCIALYEAR_KEY));
				tenantConfig.put(CalculatorConstants.TENANT_KEY, config.get(CalculatorConstants.TENANT_KEY));
			}
			return tenantConfig;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private Map<String, Map<String, Object>> fetchCancelAssessmentScheduledTenants(RequestInfo request) {

		StringBuilder mdmsURL = utils.getMdmsSearchUrl();
		MdmsCriteriaReq mdmsConfig = utils.getCancelAssessmentConfigRequest(request, configs.getStateLevelTenantId());

		try {
			Object response = mdmsRepository.fetchResult(mdmsURL, mdmsConfig);
			List<Map<String, Object>> jsonOutput = JsonPath.read(response,
					CalculatorConstants.MDMS_CANCEL_ASSESSMENT_JOB_CONFIG_PATH);
			Map<String, Map<String, Object>> scheduledTenants = new HashMap<>();

			for (Map<String, Object> config : jsonOutput) {
				Map<String, Object> tenantConfig = new HashMap<>();
				tenantConfig.put(CalculatorConstants.LOCALITY_KEY, config.get(CalculatorConstants.LOCALITY_KEY));
				tenantConfig.put(CalculatorConstants.PROPERTYTYPE_KEY,
						config.get(CalculatorConstants.PROPERTYTYPE_KEY));
				tenantConfig.put(CalculatorConstants.FINANCIALYEAR_KEY,
						config.get(CalculatorConstants.FINANCIALYEAR_KEY));
				tenantConfig.put(CalculatorConstants.IS_RENTED, config.get(CalculatorConstants.IS_RENTED));
				scheduledTenants.put(config.get(CalculatorConstants.TENANT_KEY).toString(), tenantConfig);
			}
			return scheduledTenants;
		} catch (Exception e) {
			throw new CustomException(CalculatorConstants.ASSESSMENT_JOB_MDMS_ERROR,
					CalculatorConstants.ASSESSMENT_JOB_MDMS_ERROR_MSG);
		}
	}

}