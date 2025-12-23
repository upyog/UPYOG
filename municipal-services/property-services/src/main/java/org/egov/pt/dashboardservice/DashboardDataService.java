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
		services.setPropertiesPaid(dashboardDataRepository.getTotalPropertyPaidCount(dashboardDataSearch));
		
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
		List<Property> emptyProperities =new ArrayList<Property>();
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
		service.add(propertiesRegistered);
		
		Map<String, String> pendingwith=new HashMap<String, String>();
		PropertyCriteria criteria = new PropertyCriteria();
		pendingwith=dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest);
		Set<String> propertyIds = new HashSet<>();
		String propertyIdstring =pendingwith.getOrDefault("PENDINGWITHDOCVERIFIER", null);
		if(!StringUtils.isEmpty(propertyIdstring))
			propertyIds.addAll(
				    Arrays.stream(propertyIdstring.split(","))
				          .map(String::trim)
				          .collect(Collectors.toSet()));
		
		
		criteria.setPropertyIds(propertyIds);
		propertiesRegistered = new ServiceWithProperties();
		if(!CollectionUtils.isEmpty(propertyIds))
		{
			properities=propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			propertiesRegistered.setTotal(properities.size());
			propertiesRegistered.setType("propertiesPendingWithDocVerifier");
			propertiesRegistered.setProperties(properities);
		}
		else
		{
			propertiesRegistered.setTotal(0);
			propertiesRegistered.setType("propertiesPendingWithDocVerifier");
			propertiesRegistered.setProperties(emptyProperities);
		}
		service.add(propertiesRegistered);
		
		pendingwith=dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest);
		propertyIds = new HashSet<>();
		criteria = new PropertyCriteria();
		propertiesRegistered = new ServiceWithProperties();
		propertyIdstring =pendingwith.getOrDefault("PENDINGWITHFILEDVERIFIER", null);
		if(!StringUtils.isEmpty(propertyIdstring))
			propertyIds.addAll(
				    Arrays.stream(propertyIdstring.split(","))
				          .map(String::trim)
				          .collect(Collectors.toSet()));
		
		criteria.setPropertyIds(propertyIds);
		if(!CollectionUtils.isEmpty(propertyIds))
		{
			properities=propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			propertiesRegistered.setTotal(properities.size());
			propertiesRegistered.setType("propertiesPendingWithFieldInspector");
			propertiesRegistered.setProperties(properities);
		}
		else
		{
			propertiesRegistered.setTotal(0);
			propertiesRegistered.setType("propertiesPendingWithFieldInspector");
			propertiesRegistered.setProperties(emptyProperities);
		}
		service.add(propertiesRegistered);
		
		pendingwith=dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest);
		propertyIds = new HashSet<>();
		criteria = new PropertyCriteria();
		propertiesRegistered = new ServiceWithProperties();
		propertyIdstring =pendingwith.getOrDefault("PENDINGWITHAPPROVER", null);
		if(!StringUtils.isEmpty(propertyIdstring))
			propertyIds.addAll(
				    Arrays.stream(propertyIdstring.split(","))
				          .map(String::trim)
				          .collect(Collectors.toSet()));
		
		criteria.setPropertyIds(propertyIds);
		if(!CollectionUtils.isEmpty(propertyIds))
		{
			properities=propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			propertiesRegistered.setTotal(properities.size());
			propertiesRegistered.setType("propertiesPendingWithApprover");
			propertiesRegistered.setProperties(properities);
		}
		else
		{
			propertiesRegistered.setTotal(0);
			propertiesRegistered.setType("propertiesPendingWithApprover");
			propertiesRegistered.setProperties(emptyProperities);
		}
		service.add(propertiesRegistered);
		
		pendingwith=dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest);
		propertyIds = new HashSet<>();
		criteria = new PropertyCriteria();
		propertiesRegistered = new ServiceWithProperties();
		propertyIdstring =pendingwith.getOrDefault("APPROVED", null);
		if(!StringUtils.isEmpty(propertyIdstring))
			propertyIds.addAll(
				    Arrays.stream(propertyIdstring.split(","))
				          .map(String::trim)
				          .collect(Collectors.toSet()));
		
		criteria.setPropertyIds(propertyIds);
		if(!CollectionUtils.isEmpty(propertyIds))
		{
			properities=propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			propertiesRegistered.setTotal(properities.size());
			propertiesRegistered.setType("propertiesApproved");
			propertiesRegistered.setProperties(properities);
		}
		else
		{
			propertiesRegistered.setTotal(0);
			propertiesRegistered.setType("propertiesApproved");
			propertiesRegistered.setProperties(emptyProperities);
		}
		service.add(propertiesRegistered);
		
		pendingwith=dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest);
		propertyIds = new HashSet<>();
		criteria = new PropertyCriteria();
		propertiesRegistered = new ServiceWithProperties();
		propertyIdstring =pendingwith.getOrDefault("REJECTED", null);
		if(!StringUtils.isEmpty(propertyIdstring))
			propertyIds.addAll(
				    Arrays.stream(propertyIdstring.split(","))
				          .map(String::trim)
				          .collect(Collectors.toSet()));
		
		criteria.setPropertyIds(propertyIds);
		if(!CollectionUtils.isEmpty(propertyIds))
		{
			properities=propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			propertiesRegistered.setTotal(properities.size());
			propertiesRegistered.setType("propertiesRejected");
			propertiesRegistered.setProperties(properities);
		}
		else
		{
			propertiesRegistered.setTotal(0);
			propertiesRegistered.setType("propertiesRejected");
			propertiesRegistered.setProperties(emptyProperities);
		}
		service.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalPropertySelfassessedCount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("propertiesSelfAssessed");
		propertiesRegistered.setProperties(properities);
		service.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalPropertyPendingselfAssessedCount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("propertiesPendingSelfAssessment");
		propertiesRegistered.setProperties(properities);
		service.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalPropertyPaidCount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("propertiesPaid");
		propertiesRegistered.setProperties(properities);
		service.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalPropertyAppealSubmitedCount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("propertiesWithAppealSubmitted");
		propertiesRegistered.setProperties(properities);
		service.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalPropertyAppealPendingCount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("appealsPending");
		propertiesRegistered.setProperties(properities);
		service.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalTaxCollectedAmount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("totalTaxCollected");
		propertiesRegistered.setProperties(properities);
		revenue.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalTaxCollectedAmount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("propertyTax");
		propertiesRegistered.setProperties(properities);
		revenue.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalTaxCollectedAmount(dashboardRequest);
		propertiesRegistered.setTotal(emptyProperities.size());
		propertiesRegistered.setType("refund");
		propertiesRegistered.setProperties(emptyProperities);
		revenue.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getPenaltyShareAmount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("penalty");
		propertiesRegistered.setProperties(properities);
		revenue.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getInterestShareAmount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("interest");
		propertiesRegistered.setProperties(properities);
		revenue.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getAdvanceShareAmount(dashboardRequest);
		propertiesRegistered.setTotal(properities.size());
		propertiesRegistered.setType("advance");
		propertiesRegistered.setProperties(properities);
		revenue.add(propertiesRegistered);
		
		propertiesRegistered = new ServiceWithProperties();
		properities  = dashboardReportRepository.getTotalTaxCollectedAmount(dashboardRequest);
		propertiesRegistered.setTotal(emptyProperities.size());
		propertiesRegistered.setType("arrears");
		propertiesRegistered.setProperties(emptyProperities);
		revenue.add(propertiesRegistered);
		
		
		
		dashboardData.setServices(service);
		dashboardData.setRevenue(revenue);
		
		return dashboardData;
	}
	
	/*
	 * public ResponseEntity<Resource> generateExcelResponse(DashboardReport
	 * dashboardData, String filename) throws Exception { ByteArrayOutputStream out
	 * = new ByteArrayOutputStream(); Workbook workbook = new XSSFWorkbook();
	 * 
	 * writeReportToExcel(workbook, dashboardData);
	 * 
	 * workbook.write(out); workbook.close();
	 * 
	 * byte[] excelBytes = out.toByteArray(); ByteArrayResource resource = new
	 * ByteArrayResource(excelBytes); return ResponseEntity.ok()
	 * .header(HttpHeaders.CONTENT_DISPOSITION,
	 * "attachment; filename=\"report.xlsx\"") .contentLength(excelBytes.length)
	 * .contentType(MediaType.parseMediaType(
	 * "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
	 * .body(resource); }
	 */


	/*
	 * private void writeReportToExcel(Workbook workbook, DashboardReport report) {
	 * writeSection(workbook.createSheet("Services"), report.getServices());
	 * writeSection(workbook.createSheet("Revenue"), report.getRevenue()); }
	 */
	/*
	 * private void writeSection(Sheet sheet, List<ServiceWithProperties> items) {
	 * int rowNum = 0;
	 * 
	 * for (ServiceWithProperties item : items) { // Write service type as a merged
	 * header row Row typeRow = sheet.createRow(rowNum++); Cell typeCell =
	 * typeRow.createCell(0); typeCell.setCellValue(item.getType());
	 * 
	 * // You can style this cell as bold if you want (optional)
	 * 
	 * // If no properties, write "No Properties" below the type if
	 * (item.getProperties() == null || item.getProperties().isEmpty()) { Row
	 * noPropRow = sheet.createRow(rowNum++);
	 * noPropRow.createCell(0).setCellValue("No Properties"); continue; // next item
	 * }
	 * 
	 * // Otherwise write properties header row Row headerRow =
	 * sheet.createRow(rowNum++);
	 * headerRow.createCell(0).setCellValue("Property ID");
	 * headerRow.createCell(1).setCellValue("Property Name"); // Add more headers if
	 * you have more fields in Property
	 * 
	 * // Now write each property in its own row for (Property prop :
	 * item.getProperties()) { Row propRow = sheet.createRow(rowNum++);
	 * propRow.createCell(0).setCellValue(prop.getId()); // Adjust getter
	 * propRow.createCell(1).setCellValue(prop.getPropertyId()); // Adjust getter //
	 * Add more cells here if needed }
	 * 
	 * // Blank row between services (optional) rowNum++; }
	 * 
	 * // Autosize columns for readability for (int i = 0; i < 5; i++) {
	 * sheet.autoSizeColumn(i); } }
	 */
	public List<Property> applicationData(DashboardDataSearch dashboardDataSearch,RequestInfo requestInfo)
	{
		return propertyService.searchProperty(dashboardDataRepository.getApplicationData(dashboardDataSearch, requestInfo), requestInfo);
	}

}
