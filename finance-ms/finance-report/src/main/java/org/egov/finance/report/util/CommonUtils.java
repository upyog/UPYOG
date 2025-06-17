/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.report.util;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;

import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.model.MasterDetail;
import org.egov.finance.report.model.MdmsCriteria;
import org.egov.finance.report.model.MdmsCriteriaReq;
import org.egov.finance.report.model.ModuleDetail;
import org.egov.finance.report.model.RequestInfo;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@Component
public class CommonUtils {

	@Value("${egov.mdms.host}")
	private String mdmshost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearch;

	private RestTemplate restTemplate;

	@Autowired
	public CommonUtils(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/*********************** MDMS Utitlity Methods *****************************/

	/**
	 * Fetches all the values of particular attribute as map of fieldname to list
	 *
	 * @param tenantId    tenantId of properties in PropertyRequest
	 * @param names       List of String containing the names of all masterdata
	 *                    whose code has to be extracted
	 * @param requestInfo RequestInfo of the received PropertyRequest
	 * @return Map of MasterData name to the list of code in the MasterData
	 *
	 */
	public Map<String, List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names,
			String filter, String jsonpath) {

		RequestInfo requestInfo = new RequestInfo();
		Object response = null;
		Map<String, String> errormap = new HashMap<>();
		StringBuilder uri = new StringBuilder(mdmshost).append(mdmsSearch);
		MdmsCriteriaReq criteriaReq = prepareMdMsRequest(tenantId, moduleName, names, filter, requestInfo);
		response = restTemplate.postForObject(uri.toString(), criteriaReq, Map.class);
		Optional<Object> mdmsresponse = Optional.ofNullable(response);

		try {
			if (mdmsresponse.isPresent()) {
				return JsonPath.read(mdmsresponse.get(), jsonpath);
			}
		} catch (Exception e) {
			errormap.put("MDMS_ERROR", e.getLocalizedMessage());
		}

		if (!CollectionUtils.isEmpty(errormap))
			throw new ReportServiceException(errormap);

		return Collections.emptyMap();
	}

	public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter,
			RequestInfo requestInfo) {

		List<MasterDetail> masterDetails = new ArrayList<>();

		names.forEach(name -> masterDetails.add(MasterDetail.builder().name(name).filter(filter).build()));

		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}

	/**
	 * Removes Null Values between source and target
	 *
	 * @param source is the request object
	 * @param target is the object wich will be compared with source
	 * 
	 * @author bpattanayak
	 */
	public List<String> applyNonNullFields(Object source, Object target) {
	    BeanWrapper srcWrapper = new BeanWrapperImpl(source);
	    BeanWrapper trgWrapper = new BeanWrapperImpl(target);
	    List<String> updatedFields = new ArrayList<>();

	    for (PropertyDescriptor propertyDescriptor : srcWrapper.getPropertyDescriptors()) {
	        String propertyName = propertyDescriptor.getName();

	        if (!trgWrapper.isWritableProperty(propertyName)) {
	            continue;
	        }

	        Object sourceValue = srcWrapper.getPropertyValue(propertyName);
	        Object targetValue = trgWrapper.getPropertyValue(propertyName);

	        if (!ObjectUtils.isEmpty(sourceValue) && !Objects.equals(sourceValue, targetValue)) {
	            trgWrapper.setPropertyValue(propertyName, sourceValue);
	            updatedFields.add(propertyName);
	        }
	    }

	    return updatedFields;
	}
	
	/**
     * Safely converts a list of unknown objects (e.g., LinkedHashMap) to a list of the specified target class.
     *
     * @param sourceList  the original list (possibly from cache or deserialized JSON)
     * @param targetClass the class to convert each element to
     * @param <T>         the type of the target class
     * @return a list of converted objects
     * @author bpattanayak
     */
	public <T> List<T> convertListIfNeeded(Object sourceList, Class<T> targetClass) {
		final ObjectMapper objectMapper = new ObjectMapper();
	        if (sourceList instanceof List<?> list && !list.isEmpty()) {
	            Object first = list.get(0);
	            if (targetClass.isInstance(first)) {
	                return (List<T>) list;
	            } else if (first instanceof LinkedHashMap) {
	                return objectMapper.convertValue(
	                    list,
	                    objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass)
	                );
	            }
	        }
	        return List.of(); 
	    }

	
	public  String getFormattedDate(Date date, String pattern) {
	    if (date == null || pattern == null) {
	        return "";
	    }
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	    return formatter.format(localDateTime);
	}
	
	public String removeSpecialCharacters(final String str) {
	    if (str == null || str.isEmpty()) {
	        return "";
	    }
	    String sanitized = str.replaceAll("\\s{2,}|\\r?\\n", "<br/>");
	    sanitized = sanitized.replaceAll("'", Matcher.quoteReplacement("\\'"));

	    return sanitized;
	}

}
