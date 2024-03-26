package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSMeasurementBookApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSMeasurementBookApplicationQueryBuilder {

	
	private static final String BASE_MB_QUERY = " SELECT mb.measurement_book_id as mMeasurementBookId, mb.work_order_no as mWorkOrderNo, mb.contractor_name as mContractorName, mb.work_name as mWorkName, mb.measurement_book_no as mMeasurementBookNo, mb.status as mStatus, mb.agreement_no as mAgreementNo, mb.project_name as mProjectName, mb.work_order_amount as mWorkOrderAmount, mb.work_order_date as mWorkOrderDate, mb.measurement_date as mMeasurementDate, mb.description_of_mb as mDescriptionOfMb, mb.je_name as mJeName, mb.chapter as mChapter,mb.item_no as mItemNo,mb.description_of_the_item as mDescriptionOfTheItem,mb.estimated_quantity as mEstimatedQuantity,mb.cumulative_quantity as mCumulativeQuantity,mb.unit as mUnit,mb.rate as mRate,mb.consumed_quantity as mConsumedQuantity,mb.amount as mAmount,mb.add_mb as mAddMb,mb.item_description as mItemDescription,mb.nos as mNos,mb.l as mL,mb.bw as mBw,mb.dh as mDh,mb.upload_images as mUploadImages,mb.item_code as mItemCode,mb.description as mDescription,mb.commulative_quantity as mCommulativeQuantity,mb.remark as mRemark,mb.overhead_description as mOverheadDescription,mb.value_type as mValueType,mb.estimated_amount as mEstimatedAmount,mb.actual_amount as mActualAmount,mb.document_description as mDocumentDescription,mb.upload_document as mUploadDocument, mb.createdby as mCreatedBy, mb.lastmodifiedby as mLastmodifiedby, mb.createdtime as mCreatedtime, mb.lastmodifiedtime as mLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM measurement_book mb";

    private final String ORDERBY_CREATEDTIME = " ORDER BY mb.measurement_date DESC ";

    public String getMeasurementBookApplicationSearchQuery(WMSMeasurementBookApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_MB_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getMeasurementBookId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" mb.measurement_book_id IN ( ").append(createQuery(criteria.getMeasurementBookId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getMeasurementBookId());
      
        // order birth registration applications based on their createdtime in latest first manner
        query.append(ORDERBY_CREATEDTIME);

        
    }
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
