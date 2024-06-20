package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkAwardApprovalApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSWorkAwardApprovalApplicationQueryBuilder {

	
	private static final String BASE_WAWARD_QUERY = " SELECT waward.work_award_id as wWorkAwardId,waward.work_no as wWorkNo, waward.work_name as wWorkName, waward.percentage_type as wPercentageType, waward.quoted_percentage as wQuotedPercentage,waward.accepted_work_cost as wAcceptedWorkCost, waward.contractor_name as wContractorName, waward.no_of_days_for_agreement as wNoOfDaysForAgreement, waward.loa_generation as wLoaGeneration, waward.award_date as wAwardDate, waward.document_upload as wDocumentUpload, waward.award_status as wAwardStatus, waward.createdby as wCreatedBy, waward.lastmodifiedby as wLastmodifiedby, waward.createdtime as wCreatedtime, waward.lastmodifiedtime as wLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM work_award_approval waward";

    private final String ORDERBY_CREATEDTIME = " ORDER BY waward.award_date DESC ";

    public String getWorkAwardApprovalApplicationSearchQuery(WMSWorkAwardApprovalApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_WAWARD_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getWorkAwardId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" waward.work_award_id IN ( ").append(createQuery(criteria.getWorkAwardId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getWorkAwardId());
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
