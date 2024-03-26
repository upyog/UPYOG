package org.egov.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.web.models.AuditDetails;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSMeasurementBookApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSMeasurementBookApplicationRowMapper implements ResultSetExtractor<List<WMSMeasurementBookApplication>> {
    public List<WMSMeasurementBookApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSMeasurementBookApplication> wmsMeasurementBookApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String measurementBookId = rs.getString("mMeasurementBookId");
            WMSMeasurementBookApplication wmsMeasurementBookApplication = wmsMeasurementBookApplicationMap.get(measurementBookId);

            if(wmsMeasurementBookApplication == null) {

            	Long lastModifiedTime = rs.getLong("mLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("mCreatedBy"))
                        .createdTime(rs.getLong("mCreatedtime"))
                        .lastModifiedBy(rs.getString("mLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsMeasurementBookApplication = WMSMeasurementBookApplication.builder()
                        .measurementBookId(rs.getString("mMeasurementBookId"))
                        .workOrderNo(rs.getInt("mWorkOrderNo"))
                        .contractorName(rs.getString("mContractorName"))
                        .workName(rs.getString("mWorkName"))
                        .measurementBookNo(rs.getString("mMeasurementBookNo"))
                        .status(rs.getString("mStatus"))
                        .agreementNo(rs.getInt("mAgreementNo"))
                        .projectName(rs.getString("mProjectName"))
                        .workOrderAmount(rs.getInt("mWorkOrderAmount"))
                        .workOrderDate(rs.getString("mWorkOrderDate"))
                        .measurementDate(rs.getString("mMeasurementDate"))
                        .descriptionOfMb(rs.getString("mDescriptionOfMb"))
                        .jeName(rs.getString("mJeName"))
                        .chapter(rs.getString("mChapter"))
                        .itemNo(rs.getString("mItemNo"))
                        .descriptionOfTheItem(rs.getString("mDescriptionOfTheItem"))
                        .estimatedQuantity(rs.getInt("mEstimatedQuantity"))
                        .cumulativeQuantity(rs.getInt("mCumulativeQuantity"))
                        .unit(rs.getInt("mUnit"))
                        .rate(rs.getLong("mRate"))
                        .consumedQuantity(rs.getInt("mConsumedQuantity"))
                        .amount(rs.getInt("mAmount"))
                        .addMb(rs.getString("mAddMb"))
                        .itemDescription(rs.getString("mItemDescription"))
                        .nos(rs.getString("mNos"))
                        .l(rs.getString("mL"))
                        .bw(rs.getString("mBw"))
                        .dh(rs.getString("mDh"))
                        .uploadImages(rs.getString("mUploadImages"))
                        .itemCode(rs.getString("mItemCode"))
                        .description(rs.getString("mDescription"))
                        .commulativeQuantity(rs.getInt("mCommulativeQuantity"))
                        .remark(rs.getString("mRemark"))
                        .overheadDescription(rs.getString("mOverheadDescription"))
                        .valueType(rs.getString("mValueType"))
                        .estimatedAmount(rs.getInt("mEstimatedAmount"))
                        .actualAmount(rs.getInt("mActualAmount"))
                        .documentDescription(rs.getString("mDocumentDescription"))
                        .uploadDocument(rs.getString("mUploadDocument"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsMeasurementBookApplicationMap.put(measurementBookId, wmsMeasurementBookApplication);
        }
        return new ArrayList<>(wmsMeasurementBookApplicationMap.values());
    }

}
