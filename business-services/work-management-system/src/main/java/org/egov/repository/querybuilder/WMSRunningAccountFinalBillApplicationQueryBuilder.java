package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSRunningAccountFinalBillApplicationQueryBuilder {

	
	//private static final String BASE_RAFB_QUERY = " SELECT rafb.running_account_id as bRunningAccountId, rafb.project_name as bProjectName, rafb.work_name as bworkName, rafb.mb_no as bMbNo, rafb.mb_date as bMbDate, rafb.mb_amount as bMbAmount, rafb.estimated_cost as bEstimatedCost, rafb.tender_type as bTenderType,rafb.value as bValue, rafb.percentage_type as bPercentageType, rafb.award_amount as bAwardAmount, rafb.bill_date as bBillDate, rafb.bill_no as bBillNo, rafb.bill_amount as bBillAmount, rafb.deduction_amount as bDeductionAmount,rafb.remark as bRemark,rafb.sr_no as bSrNo,rafb.deduction_description as bDeductionDescription,rafb.addition_deduction as bAdditionDeduction,rafb.calculation_method as bCalculationMethod,rafb.percentage as bPercentage,rafb.percentage_Value as bPercentageValue,rafb.workOrder_No as bWorkOrderNo,rafb.tax_Amount as bTaxAmount,rafb.tax_Category as bTaxCategory, rafb.createdby as bCreatedBy, rafb.lastmodifiedby as bLastmodifiedby, rafb.createdtime as bCreatedtime, rafb.lastmodifiedtime as bLastmodifiedtime ";

	private static final String BASE_RAFB_QUERY = " SELECT prb.prbi_id as bPrbiId,rafb.running_account_id as bRunningAccountId, prb.running_account_bill_date as bRunningAccountBillDate,prb.running_account_bill_no as bRunningAccountBillNo,prb.running_account_bill_amount as bRunningAccountBillAmount,prb.tax_Amount as bTaxAmount,prb.remark as bRemark, rafb.percentage_type as bPercentageType, rafb.award_amount as bAwardAmount, rafb.deduction_amount as bDeductionAmount,rafb.deduction_description as bDeductionDescription,rafb.calculation_method as bCalculationMethod, rafb.createdby as bCreatedBy, rafb.lastmodifiedby as bLastmodifiedby, rafb.createdtime as bCreatedtime, rafb.lastmodifiedtime as bLastmodifiedtime ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM running_account_final_bill rafb INNER JOIN  previousrunningbillinfo prb ON rafb.running_account_id=prb.running_account_id";

    private final String ORDERBY_CREATEDTIME = " ORDER BY rafb.deduction_amount DESC ";

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
