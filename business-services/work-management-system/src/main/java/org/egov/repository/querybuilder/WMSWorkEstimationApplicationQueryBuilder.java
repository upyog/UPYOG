package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkEstimationApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSWorkEstimationApplicationQueryBuilder {

	
	private static final String BASE_WRKEST_QUERY = " SELECT wrkest.estimate_id as eEstimateId, wrkest.work_estimation_no as eWorkEstimationNo, wrkest.project_name as eProjectName, wrkest.work_name as eWorkName, wrkest.from_date as eFromDate, wrkest.to_date as eToDate, wrkest.estimate_type as eEstimateType, wrkest.sor_name as eSorName, wrkest.download_template as eDownloadTemplate, wrkest.upload_template as eUploadTemplate, wrkest.chapter as eChapter, wrkest.item_no as eItemNo, wrkest.desription_of_the_item as eDescriptionOfTheItem, wrkest.length as eLength, wrkest.bw as eBw,wrkest.dh as eDh,wrkest.nos as eNos,wrkest.quantity as eQuantity,wrkest.unit as eUnit,wrkest.rate as eRate,wrkest.estimate_amount as eEstimateAmount,wrkest.serial_no as eSerialNo,wrkest.particulars_of_item as eParticularsOfItem,wrkest.calculation_type as eCalculationType,wrkest.addition_deduction  as eAdditionDeduction ,wrkest.lf as eLf,wrkest.bwf as eBwf,wrkest.dhf as eDhf,wrkest.sub_total as eSubTotal,wrkest.grand_total as eGrandTotal,wrkest.estimated_quantity as eEstimatedQuantity,wrkest.remarks as eRemarks,wrkest.overhead_code as eOverheadCode,wrkest.overhead_description as eOverheadDescription,wrkest.value_type as eValueType,wrkest.estimated_amount as eEstimatedAmount,wrkest.document_description as eDocumentDescription,wrkest.upload_document as eUploadDocument, wrkest.createdby as eCreatedBy, wrkest.lastmodifiedby as eLastmodifiedby, wrkest.createdtime as eCreatedtime, wrkest.lastmodifiedtime as eLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM work_estimation wrkest";

    private final String ORDERBY_CREATEDTIME = " ORDER BY wrkest.from_date  DESC ";

    public String getWorkEstimationApplicationSearchQuery(WMSWorkEstimationApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_WRKEST_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getEstimateId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" wrkest.estimate_id IN ( ").append(createQuery(criteria.getEstimateId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getEstimateId());
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
