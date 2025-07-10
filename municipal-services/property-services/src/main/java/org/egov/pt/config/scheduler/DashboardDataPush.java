package org.egov.pt.config.scheduler;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.dashboardservice.DashboardService;
import org.egov.pt.models.AssessedProperties;
import org.egov.pt.models.Bucket;
import org.egov.pt.models.BucketGroup;
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
import org.springframework.util.CollectionUtils;
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
		List<Data> propertyTaxPayloads = new ArrayList<>();
		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

		Map<String, String> parentMap = new HashMap<>();
		parentMap.putAll(dashboardService.wardWithTanentList());
		parentMap.putAll(dashboardService.wardWithAssessment());
		parentMap.putAll(dashboardService.wardWithClosedCount());
		parentMap.putAll(dashboardService.wardWithPaidCount());
		parentMap.putAll(dashboardService.wardWithApprovedCount());
		parentMap.putAll(dashboardService.wardWithMovedCount());
		parentMap.putAll(dashboardService.wardWithPropertyRegistered());
		parentMap.putAll(dashboardService.wardWithPropertyAssessed());
		parentMap.putAll(dashboardService.wardWithTransactionCount());
		parentMap.putAll(dashboardService.wardWithTodaysCollection());
		parentMap.putAll(dashboardService.wardWithPropertyCount());
		parentMap.putAll(dashboardService.wardWithRebateGiven());
		parentMap.putAll(dashboardService.wardWithPenaltyCollected());
		parentMap.putAll(dashboardService.wardWithInterestCollected());

		if (parentMap.isEmpty())
			return propertyTaxPayloads;

		for (Map.Entry<String, String> entry : parentMap.entrySet()) {
			String key = entry.getKey();
			String[] keyParts = key.split("-");

			Data propertyTaxPayload = new Data();
			propertyTaxPayload.setDate(formattedDate);
			propertyTaxPayload.setModule(PTConstants.DASHBOARD_MODULENAME);
			propertyTaxPayload.setState(PTConstants.DASHBOARD_STATE);
			propertyTaxPayload.setWard(keyParts[0]);
			propertyTaxPayload.setUlb(keyParts[1]);

			RequestInfo requestInfo = new RequestInfo();
			List<String> masterNames = Collections.singletonList("tenants");
			Map<String, List<String>> regionName = propertyutil.getAttributeValues(config.getStateLevelTenantId(),
					"tenant", masterNames,
					"[?(@.city.districtTenantCode== '" + propertyTaxPayload.getUlb() + "')].city.name",
					"$.MdmsRes.tenant", requestInfo);
			propertyTaxPayload.setRegion(regionName.get("tenants").get(0));

			Metrics metrics = new Metrics();
			populateSimpleMetrics(metrics, key);
			populateBucketMetrics(metrics, key);

			propertyTaxPayload.setMetrics(metrics);
			propertyTaxPayloads.add(propertyTaxPayload);
		}

		return propertyTaxPayloads;
	}

	private void populateSimpleMetrics(Metrics metrics, String key) {
		if (dashboardService.wardWithAssessment().containsKey(key))
			metrics.setAssessments(new BigInteger(dashboardService.wardWithAssessment().get(key)));
		if (dashboardService.wardWithTanentList().containsKey(key))
			metrics.setTodaysTotalApplications(new BigInteger(dashboardService.wardWithTanentList().get(key)));
		if (dashboardService.wardWithClosedCount().containsKey(key))
			metrics.setTodaysClosedApplications(new BigInteger(dashboardService.wardWithClosedCount().get(key)));
		if (dashboardService.wardWithPaidCount().containsKey(key))
			metrics.setNoOfPropertiesPaidToday(new BigInteger(dashboardService.wardWithPaidCount().get(key)));
		if (dashboardService.wardWithApprovedCount().containsKey(key))
			metrics.setTodaysApprovedApplications(new BigInteger(dashboardService.wardWithApprovedCount().get(key)));
	}

	private void populateBucketMetrics(Metrics metrics, String key) {
		addBucketData(metrics::setTodaysMovedApplications, dashboardService.wardWithMovedCount(), key,
				PTConstants.DASHBOARD_APPLICATION_STATUS, TodaysMovedApplications::new);
		addBucketData(metrics::setPropertiesRegistered, dashboardService.wardWithPropertyRegistered(), key,
				PTConstants.DASHBOARD_FINANCIAL_YEAR, PropertiesRegistered::new);
		addBucketData(metrics::setAssessedProperties, dashboardService.wardWithPropertyAssessed(), key, PTConstants.DASHBOARD_USAGE_CATEGORY,
				AssessedProperties::new);
		addBucketData(metrics::setTransactions, dashboardService.wardWithTransactionCount(), key, PTConstants.DASHBOARD_USAGE_CATEGORY,
				Transactions::new);
		addBucketData(metrics::setTodaysCollection, dashboardService.wardWithTodaysCollection(), key, PTConstants.DASHBOARD_USAGE_CATEGORY,
				TodaysCollection::new);
		addBucketData(metrics::setPropertyTax, dashboardService.wardWithPropertyCount(), key,PTConstants.DASHBOARD_USAGE_CATEGORY,
				PropertyTax::new);
		addBucketData(metrics::setRebate, dashboardService.wardWithRebateGiven(), key, PTConstants.DASHBOARD_USAGE_CATEGORY, Rebate::new);
		addBucketData(metrics::setPenalty, dashboardService.wardWithPenaltyCollected(), key, PTConstants.DASHBOARD_USAGE_CATEGORY,
				Penalty::new);
		addBucketData(metrics::setInterest, dashboardService.wardWithInterestCollected(), key, PTConstants.DASHBOARD_USAGE_CATEGORY,
				Interest::new);
	}

	private <T extends BucketGroup> void addBucketData(Consumer<List<T>> setter, Map<String, String> sourceMap,
			String key, String groupBy, Supplier<T> constructor) {
		if (sourceMap.containsKey(key)) {
			List<Bucket> buckets = Arrays.stream(sourceMap.get(key).split(",")).map(s -> {
				String[] parts = s.split(":");
				Bucket bucket = new Bucket();
				bucket.setName(parts[0]);
				bucket.setValue(new BigInteger(parts[1]));
				return bucket;
			}).collect(Collectors.toList());

			T group = constructor.get();
			group.setGroupBy(groupBy);
			group.setBuckets(buckets);

			setter.accept(Collections.singletonList(group));
		}
	}

	private void authenticationdetails(RequestInfo requestInfo) {

		HttpHeaders headers = new HttpHeaders();
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
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

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		Map<String, Object> response = restTemplate
				.postForEntity(config.getDashbordUserHost() + "/user/oauth/token", request, Map.class).getBody();
		requestInfo.setApiId("asset-services");
		requestInfo.setMsgId("search with from and to values");
		requestInfo.setAuthToken(objectMapper.convertValue(response.get("access_token"), String.class));
		requestInfo.setUserInfo(objectMapper.convertValue(response.get("UserRequest"), User.class));
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<Data> datas = dataPush();
		RequestInfo requestInfo = new RequestInfo();
		authenticationdetails(requestInfo);
		DashboardDataRequest dashboardDataRequest = DashboardDataRequest.builder().requestInfo(requestInfo).datas(datas)
				.build();
		Object response = "No Response";
		try {
			if (!CollectionUtils.isEmpty(datas)) {
				response = restTemplate
						.postForEntity(config.getDashbordUserHost() + "/national-dashboard/metric/_ingest",
								dashboardDataRequest, Map.class)
						.getBody();
			}
			propertyRepository.savedashbordDatalog(dashboardDataRequest, response, null);
		} catch (Exception e) {
			propertyRepository.savedashbordDatalog(dashboardDataRequest, response, e.getLocalizedMessage());
		}
	}

}
