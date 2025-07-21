package org.egov.pt.dashboardservice;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.DashboardData;
import org.egov.pt.models.DashboardDataSearch;
import org.egov.pt.models.DashboardReport;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.Revenue;
import org.egov.pt.models.ServiceWithProperties;
import org.egov.pt.models.Services;
import org.egov.pt.repository.DashboardDataRepository;
import org.egov.pt.repository.DashboardReportRepository;
import org.egov.pt.service.PropertyService;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.web.contracts.DashboardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class DashboardDataService {
	
	@Autowired
	DashboardDataRepository dashboardDataRepository;
	
	@Autowired
	DashboardReportRepository dashboardReportRepository;
	
	@Autowired
	PropertyService propertyService;
	
	@Autowired
	PropertyUtil propertyutil;
	
	@Autowired
	PropertyConfiguration config;
	
	public List<DashboardData> dashboardDatas(DashboardDataSearch dashboardDataSearch)
	{
		List<DashboardData> dashboardDatas=new ArrayList<DashboardData>();
		DashboardData dashboardData=new DashboardData();
		Services services=new Services();
		Revenue revenue=new Revenue();
		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		dashboardData.setState("MANIPUR");
		dashboardData.setModule("PT");
		if(!StringUtils.isEmpty(dashboardDataSearch.getFromDate()) && !StringUtils.isEmpty(dashboardDataSearch.getToDate()))
		{
			dashboardData.setFromdate(dashboardDataSearch.getFromDate());
			dashboardData.setTodate(dashboardDataSearch.getToDate());
		}
		else
		{
			dashboardData.setFromdate("01-04-2025");
			dashboardData.setTodate(formattedDate);
		}
		if(!StringUtils.isEmpty(dashboardDataSearch.getTenantid()))
		{
			dashboardData.setUlb(dashboardDataSearch.getTenantid());
			RequestInfo requestInfo = new RequestInfo();
			List<String> masterNames = Collections.singletonList("tenants");
			Map<String, List<String>> regionName = propertyutil.getAttributeValues(config.getStateLevelTenantId(),
					"tenant", masterNames,
					"[?(@.city.districtTenantCode== '" + dashboardData.getUlb() + "')].city.name",
					"$.MdmsRes.tenant", requestInfo);
			dashboardData.setRegion(regionName.get("tenants").get(0));
		}
		else
		{
			dashboardData.setUlb("MN");
			dashboardData.setRegion("MN");
		}
		if(!StringUtils.isEmpty(dashboardDataSearch.getWard()))
			dashboardData.setWard(dashboardDataSearch.getWard());
		else
			dashboardData.setWard("MN");
		
		services.setTotalPropertiesRegistered(dashboardDataRepository.getTotalPropertyRegisteredCount(dashboardDataSearch));
		services.setPropertiesPendingWithDocVerifier(dashboardDataRepository.getPropertiesPendingWithCount(dashboardDataSearch).getOrDefault("PENDINGWITHDOCVERIFIER", BigInteger.ZERO));
		services.setPropertiesPendingWithFieldInspector(dashboardDataRepository.getPropertiesPendingWithCount(dashboardDataSearch).getOrDefault("PENDINGWITHFILEDVERIFIER",BigInteger.ZERO));
		services.setPropertiesPendingWithApprover(dashboardDataRepository.getPropertiesPendingWithCount(dashboardDataSearch).getOrDefault("PENDINGWITHAPPROVER",BigInteger.ZERO));
		services.setPropertiesRejected(dashboardDataRepository.getPropertiesPendingWithCount(dashboardDataSearch).getOrDefault("REJECTED",BigInteger.ZERO));
		services.setPropertiesApproved(dashboardDataRepository.getPropertiesPendingWithCount(dashboardDataSearch).getOrDefault("APPROVED",BigInteger.ZERO));
		services.setPropertiesSelfAssessed(dashboardDataRepository.getTotalPropertySelfassessedCount(dashboardDataSearch));
		services.setPropertiesPendingSelfAssessment(dashboardDataRepository.getTotalPropertyPendingselfAssessedCount(dashboardDataSearch));
		services.setPropertiesWithAppealSubmitted(dashboardDataRepository.getTotalPropertyAppealSubmitedCount(dashboardDataSearch));
		services.setAppealsPending(dashboardDataRepository.getTotalPropertyAppealPendingCount(dashboardDataSearch));
		
		revenue.setTotalTaxCollected(dashboardDataRepository.getTotalTaxCollectedAmount(dashboardDataSearch));
		revenue.setPropertyTaxShare(dashboardDataRepository.getPropertyTaxShareAmount(dashboardDataSearch));
		revenue.setPenaltyShare(dashboardDataRepository.getPenaltyShareAmount(dashboardDataSearch));
		revenue.setInterestShare(dashboardDataRepository.getInterestShareAmount(dashboardDataSearch));
		revenue.setAdvanceShare(dashboardDataRepository.getAdvanceShareAmount(dashboardDataSearch));
		
		dashboardData.setServices(services);
		dashboardData.setRevenue(revenue);
		dashboardDatas.add(dashboardData);
		return dashboardDatas;
	}
	
	
	
	public DashboardReport dashboardDatasWithProperties(DashboardRequest dashboardRequest)
	{
		List<ServiceWithProperties> service = new ArrayList<ServiceWithProperties>();
		List<ServiceWithProperties> revenue = new ArrayList<ServiceWithProperties>();
		DashboardReport dashboardData=new DashboardReport();
		List<Property> properities =new ArrayList<Property>();
		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		dashboardData.setState("MANIPUR");
		dashboardData.setModule("PT");
		if(!StringUtils.isEmpty(dashboardRequest.getDashboardDataSearch().getFromDate()) && !StringUtils.isEmpty(dashboardRequest.getDashboardDataSearch().getToDate()))
		{
			dashboardData.setFromdate(dashboardRequest.getDashboardDataSearch().getFromDate());
			dashboardData.setTodate(dashboardRequest.getDashboardDataSearch().getToDate());
		}
		else
		{
			dashboardData.setFromdate("01-04-2025");
			dashboardData.setTodate(formattedDate);
		}
		if(!StringUtils.isEmpty(dashboardRequest.getDashboardDataSearch().getTenantid()))
		{
			dashboardData.setUlb(dashboardRequest.getDashboardDataSearch().getTenantid());
			RequestInfo requestInfo = new RequestInfo();
			List<String> masterNames = Collections.singletonList("tenants");
			Map<String, List<String>> regionName = propertyutil.getAttributeValues(config.getStateLevelTenantId(),
					"tenant", masterNames,
					"[?(@.city.districtTenantCode== '" + dashboardData.getUlb() + "')].city.name",
					"$.MdmsRes.tenant", requestInfo);
			dashboardData.setRegion(regionName.get("tenants").get(0));
		}
		else
		{
			dashboardData.setUlb("MN");
			dashboardData.setRegion("MN");
		}
		if(!StringUtils.isEmpty(dashboardRequest.getDashboardDataSearch().getWard()))
			dashboardData.setWard(dashboardRequest.getDashboardDataSearch().getWard());
		else
			dashboardData.setWard("MN");
		
		ServiceWithProperties propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalPropertyRegisteredCount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("totalPropertiesRegistered");
		propertiesRegistered.setProperties(properities);
		
		Map<String, String> pendingwithDV=new HashMap<String, String>();
		pendingwithDV=dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest);
		Set<String> propertyIds = new HashSet<>();
		String propertyIdsPDV =pendingwithDV.getOrDefault("PENDINGWITHDOCVERIFIER", null);
		if(!StringUtils.isEmpty(propertyIdsPDV))
			propertyIds.addAll(
				    Arrays.stream(propertyIdsPDV.split(","))
				          .map(String::trim)
				          .collect(Collectors.toSet()));
		
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		ServiceWithProperties propertiesPWDV = new ServiceWithProperties();
		if(!CollectionUtils.isEmpty(propertyIds))
		{
			properities=propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			propertiesPWDV.setTotal(properities.size());
			propertiesPWDV.setType("propertiesPendingWithDocVerifier");
			propertiesPWDV.setProperties(properities);
		}
		else
		{
			propertiesPWDV.setTotal(0);
			propertiesPWDV.setType("propertiesPendingWithDocVerifier");
			propertiesPWDV.setProperties(null);
		}
			
		
		
		service.add(propertiesRegistered);
		service.add(propertiesPWDV);
		
		dashboardData.setServices(service);
		dashboardData.setRevenue(revenue);
	
		return dashboardData;
	}

}
