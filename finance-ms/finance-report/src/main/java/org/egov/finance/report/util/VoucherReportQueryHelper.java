/**
 * Created on Jun 19, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.egov.finance.report.entity.AppConfigValues;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class VoucherReportQueryHelper {

	public  String prepareQueryForBudget(
	        final String departmentCode, final Long functionid, final Integer functionaryid,
	        final Integer schemeid, final Integer subschemeid, final Integer boundaryid,
	        final Integer fundid, Map<String, Object> params,List<AppConfigValues> list) {
		
		Map<String, String> errorMap  = new HashMap<>();

	     // list = appConfigValuesService.getConfigValuesByModuleAndKey(EGF, BUDGETARY_CHECK_GROUPBY_VALUES);

	    if (list.isEmpty()) {
	    //	errorMap.put(ReportConstants.INVALID_ID_PASSED, departmentCode)
	       // throw new ValidationException(EMPTY_STRING, "budgetaryCheck_groupby_values is not defined in AppConfig");
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
	               // throw new ValidationException(EMPTY_STRING, "Unsupported budgetary group by value: " + value);
	        }
	    }

	    return query.toString();
	}

	private <T, R> void validateAndAppend(StringBuilder query, String fieldName, T value, String queryField,
			String paramKey, Map<String, Object> params, Function<T, R> converter) {
		if (value == null || value.equals(0)) {
			//throw new ValidationException(EMPTY_STRING, fieldName + " is required");
		} else {
			query.append(" and ").append(queryField).append("=:").append(paramKey);
			params.put(paramKey, converter.apply(value));
		}
	}

}
