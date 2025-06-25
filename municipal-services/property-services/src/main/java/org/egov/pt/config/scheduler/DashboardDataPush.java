package org.egov.pt.config.scheduler;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.dashboardservice.DashboardService;
import org.egov.pt.models.AssessedProperties;
import org.egov.pt.models.Bucket;
import org.egov.pt.models.Data;
import org.egov.pt.models.Interest;
import org.egov.pt.models.Metrics;
import org.egov.pt.models.Penalty;
import org.egov.pt.models.PropertiesRegistered;
import org.egov.pt.models.PropertyTax;
import org.egov.pt.models.Rebate;
import org.egov.pt.models.TodaysCollection;
import org.egov.pt.models.TodaysMovedApplications;
import org.egov.pt.models.Transactions;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.web.contracts.DashboardDataRequest;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DashboardDataPush implements Job {

	@Autowired
	DashboardService dashboardService;
	@Autowired
	PropertyUtil propertyutil;
	@Autowired
	PropertyConfiguration config;
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	PropertyRepository propertyRepository;

	@Autowired
	ObjectMapper objectMapper;
	
	public synchronized List<Data> dataPush() {
		List<Data> propertyTaxPayloads = new ArrayList<Data>();
		Map<String, String> parentMap = new HashMap<String, String>();
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String formattedDate = currentDate.format(formatter);
		Map<String, String> wardwithTanentsMap = dashboardService.wardwithtanentlist();
		Map<String, String> wardwithtenatasmtMap = dashboardService.wardwithAssesment();
		Map<String, String> wardwithtenatClosdMap = dashboardService.wardwithClosedcount();
		Map<String, String> wardwithtenatPaidMap = dashboardService.wardwithPaidcount();
		Map<String, String> wardwithtenatApprovedMap = dashboardService.wardwithApprovedcount();
		Map<String, String> wardwithtenatMovedMap = dashboardService.wardwithMovedcount();
		Map<String, String> wardwithtenatRegisteredMap = dashboardService.wardwithpropertyRegistered();
		Map<String, String> wardwithtenatAssedMap = dashboardService.wardwithpropertyAssed();
		Map<String, String> wardwithtenatTransactionMap = dashboardService.wardwithTransactioncount();
		Map<String, String> wardwithtenattodaysCollectionMap = dashboardService.wardwithtodaysCollection();
		Map<String, String> wardwithtenatpropertyCountMap = dashboardService.wardwithpropertyCount();
		Map<String, String> wardwithtenatrebategivenMap = dashboardService.wardwithrebategiven();
		Map<String, String> wardwithtenatpenaltyCollectedMap = dashboardService.wardwithpenaltyCollected();
		Map<String, String> wardwithtenatinterestCollectedMap = dashboardService.wardwithinterestCollected();
		parentMap.putAll(wardwithTanentsMap);
		parentMap.putAll(wardwithtenatasmtMap);
		parentMap.putAll(wardwithtenatClosdMap);
		parentMap.putAll(wardwithtenatPaidMap);
		parentMap.putAll(wardwithtenatApprovedMap);
		parentMap.putAll(wardwithtenatMovedMap);
		parentMap.putAll(wardwithtenatRegisteredMap);
		parentMap.putAll(wardwithtenatAssedMap);
		parentMap.putAll(wardwithtenatTransactionMap);
		parentMap.putAll(wardwithtenattodaysCollectionMap);
		parentMap.putAll(wardwithtenatpropertyCountMap);
		parentMap.putAll(wardwithtenatrebategivenMap);
		parentMap.putAll(wardwithtenatpenaltyCollectedMap);
		parentMap.putAll(wardwithtenatinterestCollectedMap);
		System.out.println("parentMap is::" + parentMap);
		if (parentMap.size() > 0) {
			for (Map.Entry<String, String> entry : parentMap.entrySet()) {
				Data propertyTaxPayload = new Data();
				Metrics metrics = new Metrics();
				TodaysMovedApplications todaysMovedApplications = new TodaysMovedApplications();
				List<TodaysMovedApplications> movedApplications = new ArrayList<TodaysMovedApplications>();
				todaysMovedApplications.setGroupBy("applicationStatus");
				PropertiesRegistered propertiesRegistered = new PropertiesRegistered();
				List<PropertiesRegistered> propertiesRegistereds = new ArrayList<PropertiesRegistered>();
				propertiesRegistered.setGroupBy("financialYear");
				AssessedProperties assessedProperties = new AssessedProperties();
				List<AssessedProperties> propertiesAssed = new ArrayList<AssessedProperties>();
				assessedProperties.setGroupBy(PTConstants.MDMS_PT_USAGECATEGORY);
				Transactions transactions = new Transactions();
				List<Transactions> transactionslist = new ArrayList<Transactions>();
				transactions.setGroupBy(PTConstants.MDMS_PT_USAGECATEGORY);
				TodaysCollection todaysCollection = new TodaysCollection();
				List<TodaysCollection> todaysCollections = new ArrayList<TodaysCollection>();
				todaysCollection.setGroupBy(PTConstants.MDMS_PT_USAGECATEGORY);
				PropertyTax propertyTax = new PropertyTax();
				List<PropertyTax> propertyTaxs = new ArrayList<PropertyTax>();
				propertyTax.setGroupBy(PTConstants.MDMS_PT_USAGECATEGORY);
				Rebate rebate = new Rebate();
				List<Rebate> rebates = new ArrayList<Rebate>();
				rebate.setGroupBy(PTConstants.MDMS_PT_USAGECATEGORY);
				Penalty penalty = new Penalty();
				List<Penalty> penalties = new ArrayList<Penalty>();
				penalty.setGroupBy(PTConstants.MDMS_PT_USAGECATEGORY);
				Interest interest = new Interest();
				List<Interest> interests = new ArrayList<Interest>();
				interest.setGroupBy(PTConstants.MDMS_PT_USAGECATEGORY);
				propertyTaxPayload.setDate(formattedDate);
				propertyTaxPayload.setModule(PTConstants.ASMT_MODULENAME);
				propertyTaxPayload.setState(config.getStateLevelTenantId());
				String key = entry.getKey();
				propertyTaxPayload.setWard(key.split("-")[0]);
				propertyTaxPayload.setUlb(key.split("-")[1]);
				RequestInfo requestInfo = new RequestInfo();
				List<String> masterNames = new ArrayList<>(Arrays.asList("tenants"));
				Map<String, List<String>> regionName = propertyutil.getAttributeValues(config.getStateLevelTenantId(),
						"tenant", masterNames,
						"[?(@.city.districtTenantCode== '" + propertyTaxPayload.getUlb() + "')].city.districtCode",
						"$.MdmsRes.tenant", requestInfo);
				propertyTaxPayload.setRegion(regionName.get("tenants").get(0));
				if (wardwithtenatasmtMap.containsKey(key))
					metrics.setAssessments(new BigInteger(wardwithtenatasmtMap.get(key)));
				if (wardwithTanentsMap.containsKey(key))
					metrics.setTodaysTotalApplications(new BigInteger(wardwithTanentsMap.get(key)));
				if (wardwithtenatClosdMap.containsKey(key))
					metrics.setTodaysClosedApplications(new BigInteger(wardwithtenatClosdMap.get(key)));
				if (wardwithtenatPaidMap.containsKey(key))
					metrics.setNoOfPropertiesPaidToday(new BigInteger(wardwithtenatPaidMap.get(key)));
				if (wardwithtenatApprovedMap.containsKey(key))
					metrics.setTodaysApprovedApplications(new BigInteger(wardwithtenatApprovedMap.get(key)));
				if (wardwithtenatMovedMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenatMovedMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					todaysMovedApplications.setBuckets(buckets);
					movedApplications.add(todaysMovedApplications);
					metrics.setTodaysMovedApplications(movedApplications);
				}
				if (wardwithtenatRegisteredMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenatRegisteredMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					propertiesRegistered.setBuckets(buckets);
					propertiesRegistereds.add(propertiesRegistered);
					metrics.setPropertiesRegistered(propertiesRegistereds);
				}
				if (wardwithtenatAssedMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenatAssedMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					assessedProperties.setBuckets(buckets);
					propertiesAssed.add(assessedProperties);
					metrics.setAssessedProperties(propertiesAssed);
				}
				if (wardwithtenatTransactionMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenatTransactionMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					transactions.setBuckets(buckets);
					transactionslist.add(transactions);
					metrics.setTransactions(transactionslist);
				}
				if (wardwithtenattodaysCollectionMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenattodaysCollectionMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					todaysCollection.setBuckets(buckets);
					todaysCollections.add(todaysCollection);
					metrics.setTodaysCollection(todaysCollections);
				}
				if (wardwithtenatpropertyCountMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenatpropertyCountMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					propertyTax.setBuckets(buckets);
					propertyTaxs.add(propertyTax);
					metrics.setPropertyTax(propertyTaxs);
				}
				if (wardwithtenatrebategivenMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenatrebategivenMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					rebate.setBuckets(buckets);
					rebates.add(rebate);
					metrics.setRebate(rebates);
				}
				if (wardwithtenatpenaltyCollectedMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenatpenaltyCollectedMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					penalty.setBuckets(buckets);
					penalties.add(penalty);
					metrics.setPenalty(penalties);
				}
				if (wardwithtenatinterestCollectedMap.containsKey(key)) {
					List<Bucket> buckets = new ArrayList<Bucket>();
					List<String> values = Arrays.asList(wardwithtenatinterestCollectedMap.get(key).split(","));
					for (String valueString : values) {
						Bucket bucket = new Bucket();
						bucket.setName(valueString.split(":")[0]);
						bucket.setValue(new BigInteger(valueString.split(":")[1]));
						buckets.add(bucket);
					}
					interest.setBuckets(buckets);
					interests.add(interest);
					metrics.setInterest(interests);
				}

				propertyTaxPayload.setMetrics(metrics);
				propertyTaxPayloads.add(propertyTaxPayload);
			}

		}

		System.out.println("propertyTaxPayloads::" + propertyTaxPayloads);
		return propertyTaxPayloads;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<Data> datas = dataPush();
		RequestInfo requestInfo = new RequestInfo();
		authenticationdetails(requestInfo);
		DashboardDataRequest dashboardDataRequest = DashboardDataRequest.builder().requestInfo(requestInfo).datas(datas)
				.build();
		try {
			propertyRepository.savedashbordDatalog(dashboardDataRequest,null);
		} catch (Exception e) {
			propertyRepository.savedashbordDatalog(dashboardDataRequest,e.getLocalizedMessage());
		}
	}

	private void authenticationdetails(RequestInfo requestInfo) {
		
		HttpHeaders headers = new HttpHeaders();
		List<MediaType> mediaTypes=new ArrayList<MediaType>();
		mediaTypes.add(MediaType.APPLICATION_JSON);
		mediaTypes.add(MediaType.TEXT_PLAIN);
		headers.setAccept(mediaTypes);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDo=");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", "MN_NDA_USER");
		map.add("password", "upyogTest@123");
		map.add("grant_type", "password");
		map.add("scope", "read");
		map.add("tenantId", "pg");
		map.add("userType", "SYSTEM");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				headers);
		Map<String, Object> response= restTemplate.postForEntity(config.getDashbordUserHost() + "/user/oauth/token", request, Map.class).getBody();
		requestInfo.setApiId("asset-services");
		requestInfo.setMsgId("search with from and to values");
		requestInfo.setAuthToken(objectMapper.convertValue(response.get("access_token"),String.class));
		requestInfo.setUserInfo(objectMapper.convertValue(response.get("UserRequest"), User.class));
	}

}
