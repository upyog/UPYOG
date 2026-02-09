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

import org.apache.commons.lang3.ObjectUtils;
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
import org.egov.pt.util.PropertyRedisCache;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.web.contracts.DashboardRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.netty.util.internal.ObjectUtil;
import org.egov.pt.util.PTConstants;
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
	
	@Autowired
	PropertyRedisCache propertyRedisCache;
	
	public List<DashboardData> dashboardDatas(DashboardDataSearch dashboardDataSearch,RequestInfo requestInfo)
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
			//RequestInfo requestInfo = new RequestInfo();
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
		services.setPropertiesPendingWithDocVerifier(dashboardDataRepository.getPropertiesPendingWithCount(dashboardDataSearch).getOrDefault(PTConstants.PENDINGWITHDOCVERIFIER, BigInteger.ZERO));
		//services.setPropertiesPendingWithDocVerifierMap(dashboardDataRepository.getPropertiesPendingWithMap(dashboardDataSearch).get("PENDINGWITHDOCVERIFIER"));
		services.setPropertiesPendingWithFieldInspector(dashboardDataRepository.getPropertiesPendingWithCount(dashboardDataSearch).getOrDefault(PTConstants.PENDINGWITHFILEDVERIFIER,BigInteger.ZERO));
		services.setPropertiesPendingWithApprover(dashboardDataRepository.getPropertiesPendingWithCount(dashboardDataSearch).getOrDefault(PTConstants.PENDINGWITHAPPROVER,BigInteger.ZERO));
		services.setPropertiesRejected(dashboardDataRepository.getTotalPropertyRejectedCount(dashboardDataSearch));
		services.setPropertiesApproved(dashboardDataRepository.getTotalPropertyApprovedCount(dashboardDataSearch));
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
		revenue.setTaxCollectedProperties(dashboardDataRepository.getTotalTaxCollectedProperties(dashboardDataSearch));
		
		//Map<String, Property> mapPendingProperty =  getPropertiesWithCache(dashboardDataSearch.getTenantid(),services.getPropertiesPendingWithDocVerifierMap(),requestInfo);
		/*
		 * services.setPropertiesPendingWithDocVerifierList( new
		 * ArrayList<>(mapPendingProperty.values()) );
		 */
		dashboardData.setServices(services);
		dashboardData.setRevenue(revenue);
		dashboardDatas.add(dashboardData);
		return dashboardDatas;
	}
	
	
	
	public DashboardReport dashboardDatasWithProperties(DashboardRequest dashboardRequest)
	{
		
		
		if(ObjectUtils.isEmpty(dashboardRequest.getDashboardDataSearch().getSearchKey())) {
			throw new CustomException("PROVIDE_DATA_SEARCH_KEY","No valid search key provided");
		}
		List<ServiceWithProperties> service = new ArrayList<ServiceWithProperties>();
		List<ServiceWithProperties> revenue = new ArrayList<ServiceWithProperties>();
		DashboardReport dashboardData=new DashboardReport();
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
		
		
		
		
		String key = dashboardRequest.getDashboardDataSearch().getSearchKey();
		List<Property> value;

		switch (key) {
		    case "totalPropertiesRegistered":
		        value = dashboardReportRepository.getTotalPropertyRegisteredCount(dashboardRequest);
		        break;
		    case "propertiesPendingWithDocVerifier":
		        value = dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest, PTConstants.PENDINGWITHDOCVERIFIER);
		        break;
		    case "propertiesPendingWithFieldInspector":
		        value = dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest, PTConstants.PENDINGWITHFILEDVERIFIER);
		        break;
		    case "propertiesPendingWithApprover":
		        value = dashboardReportRepository.getPropertiesPendingWithCount(dashboardRequest, PTConstants.PENDINGWITHAPPROVER);
		        break;
		    case "propertiesApproved":
		        value = dashboardReportRepository.getTotalPropertyApprovedCount(dashboardRequest);
		        break;
		    case "propertiesRejected":
		        value = dashboardReportRepository.getTotalPropertyRejectedCount(dashboardRequest);
		        break;
		    case "propertiesSelfAssessed":
		        value = dashboardReportRepository.getTotalPropertySelfassessedCount(dashboardRequest);
		        break;
		    case "propertiesPendingSelfAssessment":
		        value = dashboardReportRepository.getTotalPropertyPendingselfAssessedCount(dashboardRequest);
		        break;
		    case "propertiesPaid":
		        value = dashboardReportRepository.getTotalPropertyPaidCount(dashboardRequest);
		        break;
		    case "propertiesWithAppealSubmitted":
		        value = dashboardReportRepository.getTotalPropertyAppealSubmitedCount(dashboardRequest);
		        break;
		    case "appealsPending":
		        value = dashboardReportRepository.getTotalPropertyAppealPendingCount(dashboardRequest);
		        break;
		    case "totalTaxCollected":
		    	 value = dashboardReportRepository.getTotalTaxCollectedAmount(dashboardRequest);
			      break;
		    case "propertyTax":
		        value = dashboardReportRepository.getTotalTaxCollectedAmount(dashboardRequest);
		        break;
		    case "penalty":
		        value = dashboardReportRepository.getPenaltyShareAmount(dashboardRequest);
		        break;
		    case "interest":
		        value = dashboardReportRepository.getInterestShareAmount(dashboardRequest);
		        break;
		    case "advance":
		        value =  dashboardReportRepository.getAdvanceShareAmount(dashboardRequest);
		        break;
		    default:
		        throw new IllegalArgumentException("Unknown key: " + key);
		}

		// Now add to service
		service.add(buildService(key, value));

			/*
			 * revenue.add(buildService( "refund",
			 * dashboardReportRepository.getTotalTaxCollectedAmount(dashboardRequest) ));
			 */

			/*
			 * revenue.add(buildService( "arrears",
			 * dashboardReportRepository.getTotalTaxCollectedAmount(dashboardRequest) ));
			 */
		
		dashboardData.setServices(service);
		
		
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
	
	
	 public Map<String, Property> getPropertiesWithCache(
	            String tenantId,
	           Set<String> propertiesPendingWithDocVerifierMap,RequestInfo requestInfo ){
	        List<String> allPropertyIds =
	                new ArrayList<>(propertiesPendingWithDocVerifierMap);

	        // 1️⃣ Fetch from Redis (batch)
	        Map<String, Property> cachedMap =null;
	                //propertyRedisCache.multiGet(tenantId, allPropertyIds);

	        // 2️⃣ Find misses
	        Set<String> missedPropertyIds = allPropertyIds.stream()
	                .filter(id -> !cachedMap.containsKey(id))
	                .collect(Collectors.toSet());

	        // 3️⃣ Call search API ONLY if misses exist
	        if (!missedPropertyIds.isEmpty()) {

	        	PropertyCriteria criteria = PropertyCriteria.builder()
	        			.tenantId(tenantId)
	        			.propertyIds(missedPropertyIds)
	        			.build();
	                    

	        	List<Property> searchResponse =propertyService.searchProperty(criteria,requestInfo);

	            if (!CollectionUtils.isEmpty(searchResponse)) {

	                for (Property property : searchResponse) {

	                    String propertyId = property.getPropertyId();

	                    // cache each property
	                    propertyRedisCache.put(tenantId, propertyId, property);

	                    // add to result
	                    cachedMap.put(propertyId, property);
	                }
	            }
	        }

	        return cachedMap;
	    }
	 
	 private ServiceWithProperties buildService(
		        String type,
		        List<Property> properties) {

		    ServiceWithProperties swp = new ServiceWithProperties();
		    swp.setType(type);
		    swp.setProperties(
		        CollectionUtils.isEmpty(properties) ? 
		        Collections.emptyList() : properties
		    );
		    swp.setTotal(
		        CollectionUtils.isEmpty(properties) ? 0 : properties.size()
		    );
		    return swp;
		}


}
