package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.ProjectApplicationSearchCriteria;
import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class ProjectQueryBuilder {

	
	private static final String BASE_PRJ_QUERY = " SELECT prj.project_id as sProjectId, prj.project_number as sProjectNumber, prj.source_of_fund as sSchemeSourceOfFund, prj.scheme_no as sSchemeNo, prj.department as sDepartment, prj.project_name_en as sProjectNameEn, prj.project_name_reg as sProjectNameReg, prj.project_description as sProjectDescription, prj.project_timeline as sProjectTimeline,prj.project_start_date as sProjectStartDate,prj.project_end_date as sProjectEndDate,prj.scheme_name as sSchemeName,prj.approval_number as sApprovalNumber,prj.approval_date as sApprovalDate,prj.status as sStatus, prj.createdby as sCreatedBy, prj.lastmodifiedby as sLastmodifiedby, prj.createdtime as sCreatedtime, prj.lastmodifiedtime as sLastmodifiedtime";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM project_master prj ";

    private final String ORDERBY_CREATEDTIME = " ORDER BY prj.project_end_date DESC ";

    public String getProjectApplicationSearchQuery(ProjectApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_PRJ_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getProjectId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" prj.project_id IN ( ").append(createQuery(criteria.getProjectId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getProjectId());
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
