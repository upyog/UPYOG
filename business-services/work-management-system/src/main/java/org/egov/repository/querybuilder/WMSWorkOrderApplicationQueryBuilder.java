package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkOrderApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSWorkOrderApplicationQueryBuilder {

	
	private static final String BASE_WORDER_QUERY = " SELECT worder.work_order_id as oWorkOrderId, worder.work_order_date as oWorkOrderDate, worder.agreement_no as oAgreementNo, worder.contractor_name as oContractorName, worder.work_name as oWorkName, worder.contract_value as oContractValue, worder.stipulated_completion_period as oStipulatedCompletionPeriod, worder.tender_number as oTenderNumber, worder.tender_date as oTenderDate, worder.date_of_commencement as oDateOfCommencement, worder.work_assignee as oWorkAssignee, worder.document_description as oDocumentDescription, worder.terms_and_conditions as oTermsAndConditions, worder.createdby as oCreatedBy, worder.lastmodifiedby as oLastmodifiedby, worder.createdtime as oCreatedtime, worder.lastmodifiedtime as oLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM work_order worder";

    private final String ORDERBY_CREATEDTIME = " ORDER BY worder.work_name DESC ";

    public String getWorkOrderApplicationSearchQuery(WMSWorkOrderApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_WORDER_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getWorkOrderId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" worder.work_order_id IN ( ").append(createQuery(criteria.getWorkOrderId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getWorkOrderId());
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

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}
