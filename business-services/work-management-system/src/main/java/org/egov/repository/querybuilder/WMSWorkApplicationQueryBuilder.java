package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSWorkApplicationQueryBuilder {

	
	private static final String BASE_BTR_QUERY = " SELECT work.work_id as wWorkId, work.project_id as wProjectId, work.work_no as wWorkNo, work.work_name as wWorkName, work.project_name as wProjectName, work.department_name as wDepartmentName, work.work_type as wWorkType, work.work_category as wWorkCategory, work.work_subtype as wWorkSubtype, work.project_phase as wProjectPhase, work.deviation_percent as wDeviationPercent, work.start_location as wStartLocation, work.end_location as wEndLocation, work.financial_year as wFinancialYear, work.budget_head as wBudgetHead, work.createdby as wcreatedBy, work.lastmodifiedby as wlastmodifiedby, work.createdtime as wcreatedtime, work.lastmodifiedtime as wlastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM work work ";

    private final String ORDERBY_CREATEDTIME = " ORDER BY work.end_location DESC ";

    public String getWorkApplicationSearchQuery(WMSWorkApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_BTR_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getWorkNo())){
        	addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.work_no = ? ");
            preparedStmtList.add(criteria.getWorkNo());
       }
        
        if(!ObjectUtils.isEmpty(criteria.getWorkId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" work.work_id IN ( ").append(createQuery(criteria.getWorkId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getWorkId());
        }

        // order birth registration applications based on their createdtime in latest first manner
        query.append(ORDERBY_CREATEDTIME);

        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList){
        if(preparedStmtList.isEmpty()){
            query.append(" WHERE ");
        }else{
            query.append(" AND ");
        }
    }

    private String createQuery(List<String> list) {
        StringBuilder builder = new StringBuilder();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> list) {
        list.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}
