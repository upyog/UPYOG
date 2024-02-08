package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSPhysicalFinancialMilestoneApplicationQueryBuilder {

	
	//private static final String BASE_PFMILE_QUERY = " SELECT pfmile.milestone_id as mMilestoneId, pfmile.project_name as mProjectName, pfmile.work_name as mWorkName, pfmile.milestone_name as mMilestoneName, pfmile.sr_no as mSrNo, pfmile.activity_description as mActivityDescription, pfmile.percentage_weightage as mPercentageWeightage, pfmile.planned_start_date as mPlannedStartDate, pfmile.planned_end_date as mPlannedEndDate, pfmile.total_weightage as mTotalWeightage, pfmile.milestone_description as mMilestoneDescription, pfmile.actual_start_date as mActualStartDate, pfmile.actual_end_date as mActualEndDate, pfmile.progress_update_date as mProgressUpdateDate, pfmile.completed_percentage as mCompletedPercentage ";

	private static final String BASE_PFMILE_QUERY = " SELECT pfmile.milestone_id as mMilestoneId, pfmile.project_name as mProjectName, pfmile.work_name as mWorkName, pfmile.milestone_name as mMilestoneName, pfmile.percentage_weightage as mPercentageWeightage,pfmile.description_of_the_item as mDescriptionOfTheItem,pfmile.start_date as mStartDate,pfmile.end_date as mEndDate, pfmile.createdby as mCreatedBy, pfmile.lastmodifiedby as mLastmodifiedby, pfmile.createdtime as mCreatedtime, pfmile.lastmodifiedtime as mLastmodifiedtime ";
    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM physical_financial_milestone pfmile";

    private final String ORDERBY_CREATEDTIME = " ORDER BY pfmile.milestone_name DESC ";

    public String getPhysicalFinancialMilestoneApplicationSearchQuery(WMSPhysicalFinancialMilestoneApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_PFMILE_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getMilestoneId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" pfmile.milestone_id IN ( ").append(createQuery(criteria.getMilestoneId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getMilestoneId());
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
