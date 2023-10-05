package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.WMSTenderEntryApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSTenderEntryApplicationQueryBuilder {

	
	//private static final String BASE_TENDER_QUERY = " SELECT tender.tender_id as ttenderId, tender.department_name as tdepartmentName, tender.request_category as trequestCategory, tender.project_name as tprojectName, tender.resolution_no as tresolutionNo, tender.resolution_date as tresolutionDate, tender.prebid_meeting_date as tprebidMeetingDate,tender.prebid_meeting_location as tprebidMeetingLocation, tender.issue_from_date as tissueFromDate, tender.issue_till_date as tissueTillDate, tender.publish_date as tpublishDate, tender.technical_bid_open_date as ttechnicalBidOpenDate, tender.financial_bid_open_date as tfinancialBidOpenDate, tender.validity as tvalidity, tender.upload_document as tuploadDocument, tender.work_no as tworkNo, tender.work_description as tworkDescription,tender.estimated_cost as testimatedCost,tender.tender_type as ttenderType,tender.tender_fee as ttenderFee,tender.emd as temd,tender.vendor_class as tvendorClass,tender.work_duration as tworkDuration, tender.createdby as tCreatedBy, tender.lastmodifiedby as tLastmodifiedby, tender.createdtime as tCreatedtime, tender.lastmodifiedtime as tLastmodifiedtime ";
	private static final String BASE_TENDER_QUERY = " SELECT tender.tender_id as ttenderId, tender.department_name as tdepartmentName, tender.request_category as trequestCategory, tender.project_name as tprojectName, tender.resolution_no as tresolutionNo, tender.resolution_date as tresolutionDate, tender.prebid_meeting_date as tprebidMeetingDate,tender.prebid_meeting_location as tprebidMeetingLocation, tender.issue_from_date as tissueFromDate, tender.issue_till_date as tissueTillDate, tender.publish_date as tpublishDate, tender.technical_bid_open_date as ttechnicalBidOpenDate, tender.financial_bid_open_date as tfinancialBidOpenDate, tender.validity as tvalidity, tender.upload_document as tuploadDocument, tender.createdby as tCreatedBy, tender.lastmodifiedby as tLastmodifiedby, tender.createdtime as tCreatedtime, tender.lastmodifiedtime as tLastmodifiedtime ";
    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM tender_entry tender";

    private final String ORDERBY_CREATEDTIME = " ORDER BY tender.department_name DESC ";

    public String getTenderEntryApplicationSearchQuery(WMSTenderEntryApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_TENDER_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getTenderId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" tender.tender_id IN ( ").append(createQuery(criteria.getTenderId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getTenderId());
        }
        
        if(!ObjectUtils.isEmpty(criteria.getDepartmentName())){
       	 addClauseIfRequired(query, preparedStmtList);
            query.append(" tender.department_name IN ( ").append(createQuery(criteria.getDepartmentName())).append(" ) ");
            addToPreparedStatement(preparedStmtList, criteria.getDepartmentName());
       }
        
        if(!ObjectUtils.isEmpty(criteria.getResolutionNo())){
       	 addClauseIfRequired(query, preparedStmtList);
            query.append(" tender.resolution_no IN ( ").append(createQueryInt(criteria.getResolutionNo())).append(" ) ");
            addToPreparedStatementInt(preparedStmtList, criteria.getResolutionNo());
       }
        
        if(!ObjectUtils.isEmpty(criteria.getPrebidMeetingDate())){
       	 addClauseIfRequired(query, preparedStmtList);
            query.append(" tender.prebid_meeting_date IN ( ").append(createQuery(criteria.getPrebidMeetingDate())).append(" ) ");
            addToPreparedStatement(preparedStmtList, criteria.getPrebidMeetingDate());
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
    
    private String createQueryInt(List<Integer> list) {
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
    
    private void addToPreparedStatementInt(List<Object> preparedStmtList, List<Integer> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}
