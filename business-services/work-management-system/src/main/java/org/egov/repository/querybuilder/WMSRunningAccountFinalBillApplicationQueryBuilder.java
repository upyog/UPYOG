package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSRunningAccountFinalBillApplicationQueryBuilder {

	
	private static final String BASE_RAFB_QUERY = " SELECT rafb.running_account_id as bRunningAccountId, rafb.project_name as bProjectName, rafb.work_name as bworkName, rafb.mb_no as bMbNo, rafb.mb_date as bMbDate, rafb.mb_amount as bMbAmount, rafb.estimated_cost as bEstimatedCost, rafb.tender_type as bTenderType, rafb.percentage_type as bPercentageType, rafb.award_amount as bAwardAmount, rafb.bill_date as bBillDate, rafb.bill_no as bBillNo, rafb.bill_amount as bBillAmount, rafb.deduction_amount as bDeductionAmount,rafb.remark as bRemark,rafb.sr_no as bSrNo,rafb.deduction_description as bDeductionDescription,rafb.addition_deduction as bAdditionDeduction,rafb.calculation_method as bCalculationMethod,rafb.percentage as bPercentage, rafb.createdby as bCreatedBy, rafb.lastmodifiedby as bLastmodifiedby, rafb.createdtime as bCreatedtime, rafb.lastmodifiedtime as bLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM running_account_final_bill rafb";

    private final String ORDERBY_CREATEDTIME = " ORDER BY rafb.bill_date DESC ";

    public String getRunningAccountFinalBillApplicationSearchQuery(WMSRunningAccountFinalBillApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_RAFB_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getRunningAccountId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" rafb.running_account_id IN ( ").append(createQuery(criteria.getRunningAccountId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getRunningAccountId());
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
