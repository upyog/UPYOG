/**
 * Created on Jun 19, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.egov.finance.report.entity.AppConfigValues;
import org.egov.finance.report.entity.FinancialYear;
import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.repository.AppConfigValuesRepository;
import org.egov.finance.report.service.MasterCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Component
public class BudgetReportQueryHelper {
	
	
	@Autowired
	MasterCommonService masterCommonService;
	
	@Autowired
	AppConfigValuesRepository appConfigRepo;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	public  String prepareQueryForBudget(
	        final String departmentCode, final Long functionid, final Integer functionaryid,
	        final Integer schemeid, final Integer subschemeid, final Integer boundaryid,
	        final Integer fundid, Map<String, Object> params) {
		
		Map<String, String> errorMap  = new HashMap<>();

		List<AppConfigValues>  list = masterCommonService.getConfigValuesByModuleAndKey(ReportConstants.EGF, ReportConstants.BUDGETARY_CHECK_GROUPBY_VALUES);

	    if (list.isEmpty()) {
	    	errorMap.put(ReportConstants.INVALID_CODE, "budgetaryCheck_groupby_values is not defined in AppConfig");
	        throw new ReportServiceException(errorMap);
	    }

	    AppConfigValues configValue = list.get(0);
	    String[] values = StringUtils.split(configValue.getValue(), ",");
	    StringBuilder query = new StringBuilder(" and bd.budget.status.description='Approved' and bd.status.description='Approved'");

	    for (String value : values) {
	        switch (value.trim()) {
	            case "department":
	                validateAndAppend(query, "Department", departmentCode, "bd.executingDepartment", "departmentCode", params, Function.identity());
	                break;
	            case "function":
	                validateAndAppend(query, "Function", functionid, "bd.function", "functionid", params, Function.identity());
	                break;
	            case "functionary":
	                validateAndAppend(query, "Functionary", functionaryid, "bd.functionary", "functionaryid", params, Function.identity());
	                break;
	            case "fund":
	                validateAndAppend(query, "Fund", fundid, "bd.fund", "fundid", params, Function.identity());
	                break;
	            case "scheme":
	                validateAndAppend(query, "Scheme", schemeid, "bd.scheme", "schemeid", params, Function.identity());
	                break;
	            case "subscheme":
	                validateAndAppend(query, "Subscheme", subschemeid, "bd.subScheme", "subschemeid", params, Function.identity());
	                break;
	            case "boundary":
	            	Function<Integer, Long> toLong = v-> v != null ? v.longValue() : null;
	                validateAndAppend(query, "Boundary", boundaryid, "bd.boundary", "boundaryId", params, toLong);
	            default:
	            	//need to update
	            	errorMap.put(ReportConstants.INVALID_CODE, "budgetaryCheck_groupby_values is not defined in AppConfig");
	    	        throw new ReportServiceException(errorMap);
	        }
	    }

	    return query.toString();
	}
	
	
	
	

	public <T, R> void validateAndAppend(StringBuilder query, String fieldName, T value, String queryField,
			String paramKey, Map<String, Object> params, Function<T, R> converter) {
		if (value == null || value.equals(0)) {
			//throw new ValidationException(EMPTY_STRING, fieldName + " is required");
		} else {
			query.append(" and ").append(queryField).append("=:").append(paramKey);
			params.put(paramKey, converter.apply(value));
		}
	}
	
	

}
