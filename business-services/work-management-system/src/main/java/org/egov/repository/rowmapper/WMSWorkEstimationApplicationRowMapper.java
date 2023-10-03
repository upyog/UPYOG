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
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkEstimationApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSWorkEstimationApplicationRowMapper implements ResultSetExtractor<List<WMSWorkEstimationApplication>> {
    public List<WMSWorkEstimationApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSWorkEstimationApplication> wmsWorkEstimationApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String estimateId = rs.getString("eEstimateId");
            WMSWorkEstimationApplication wmsWorkEstimationApplication = wmsWorkEstimationApplicationMap.get(estimateId);

            if(wmsWorkEstimationApplication == null) {

            	Long lastModifiedTime = rs.getLong("eLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("eCreatedBy"))
                        .createdTime(rs.getLong("eCreatedtime"))
                        .lastModifiedBy(rs.getString("eLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsWorkEstimationApplication = WMSWorkEstimationApplication.builder()
                        .estimateId(rs.getString("eEstimateId"))
                        .workEstimationNo(rs.getString("eWorkEstimationNo"))
                        .projectName(rs.getString("eProjectName"))
                        .workName(rs.getString("eWorkName"))
                        .fromDate(rs.getString("eFromDate"))
                        .toDate(rs.getString("eToDate"))
                        .estimateType(rs.getString("eEstimateType"))
                        .sorName(rs.getString("eSorName"))
                        .downloadTemplate(rs.getString("eDownloadTemplate"))
                        .uploadTemplate(rs.getString("eUploadTemplate"))
                        .chapter(rs.getString("eChapter"))
                        .itemNo(rs.getString("eItemNo"))
                        .descriptionOfTheItem(rs.getString("eDescriptionOfTheItem"))
                        .length(rs.getInt("eLength"))
                        .bw(rs.getInt("eBw"))
                        .dh(rs.getInt("eDh"))
                        .noS(rs.getInt("eNos"))
                        .quantity(rs.getInt("eQuantity"))
                        .unit(rs.getString("eUnit"))
                        .rate(rs.getLong("eRate"))
                        .estimateAmount(rs.getInt("eEstimateAmount"))
                        .serialNo(rs.getInt("eSerialNo"))
                        .particularsOfItem(rs.getString("eParticularsOfItem"))
                        .calculationType(rs.getString("eCalculationType"))
                        .additionDeduction(rs.getInt("eAdditionDeduction"))
                        .lf(rs.getInt("eLf"))
                        .bwf(rs.getInt("eBwf"))
                        .dhf(rs.getInt("eDhf"))
                        .subTotal(rs.getInt("eSubTotal"))
                        .grandTotal(rs.getInt("eGrandTotal"))
                        .estimatedQuantity(rs.getInt("eEstimatedQuantity"))
                        .remarks(rs.getString("eRemarks"))
                        .overheadCode(rs.getString("eOverheadCode"))
                        .overheadDescription(rs.getString("eOverheadDescription"))
                        .valueType(rs.getString("eValueType"))
                        .estimatedAmount(rs.getInt("eEstimatedAmount"))
                        .documentDescription(rs.getString("eDocumentDescription"))
                        .uploadDocument(rs.getString("eUploadDocument"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsWorkEstimationApplicationMap.put(estimateId, wmsWorkEstimationApplication);
        }
        return new ArrayList<>(wmsWorkEstimationApplicationMap.values());
    }

}
