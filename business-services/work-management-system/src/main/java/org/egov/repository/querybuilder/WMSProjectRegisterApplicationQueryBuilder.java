package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSProjectRegisterApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSProjectRegisterApplicationQueryBuilder {

	
	private static final String BASE_PR_QUERY = " SELECT pr.register_id as pRegisterId, pr.scheme_name as pSchemeName, pr.project_name as pProjectName, pr.work_name as pWorkName, pr.work_type as pWorkType, pr.estimated_number as pEstimatedNumber, pr.estimated_work_cost as pEstimatedWorkCost, pr.sanctioned_tender_amount as pSanctionedTenderAmount, pr.status_name as pStatusName, pr.bill_received_till_date as pBillReceivedTillDate,pr.createdby as pCreatedBy, pr.lastmodifiedby as pLastmodifiedby, pr.createdtime as pCreatedtime, pr.lastmodifiedtime as pLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM project_register pr";

    private final String ORDERBY_CREATEDTIME = " ORDER BY pr.project_name DESC ";

    public String getProjectRegisterApplicationSearchQuery(WMSProjectRegisterApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_PR_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getRegisterId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" pr.register_id IN ( ").append(createQuery(criteria.getRegisterId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getRegisterId());
        }
        
		/*
		 * if(!ObjectUtils.isEmpty(criteria.getVendorName())){
		 * addClauseIfRequired(query, preparedStmtList);
		 * query.append(" contr.vendor_name IN ( ").append(createQueryString(criteria.
		 * getVendorName())).append(" ) ");
		 * addToPreparedStatementString(preparedStmtList, criteria.getVendorName()); }
		 */
        
		/*
		 * if(!ObjectUtils.isEmpty(criteria.getPFMSVendorCode())){
		 * addClauseIfRequired(query, preparedStmtList);
		 * query.append(" contr.pfms_vendor_code IN ( ").append(createQueryString(
		 * criteria.getPFMSVendorCode())).append(" ) ");
		 * addToPreparedStatementString(preparedStmtList, criteria.getPFMSVendorCode());
		 * }
		 */

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
    
    

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
    
    
}
