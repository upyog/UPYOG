package org.egov.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSRunningAccountFinalBillApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSRunningAccountFinalBillApplicationRowMapper implements ResultSetExtractor<List<WMSRunningAccountFinalBillApplication>> {
    public List<WMSRunningAccountFinalBillApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer,WMSRunningAccountFinalBillApplication> wmsRunningAccountFinalBillApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            int runningAccountId = rs.getInt("bRunningAccountId");
            WMSRunningAccountFinalBillApplication wmsRunningAccountFinalBillApplication = wmsRunningAccountFinalBillApplicationMap.get(runningAccountId);

            if(wmsRunningAccountFinalBillApplication == null) {

                Date lastModifiedTime = rs.getDate("bBillDate");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                wmsRunningAccountFinalBillApplication = WMSRunningAccountFinalBillApplication.builder()
                        .runningAccountId(rs.getInt("bRunningAccountId"))
                        .projectName(rs.getString("bProjectName"))
                        .workName(rs.getString("bworkName"))
                        .mbNo(rs.getInt("bMbNo"))
                        .mbDate(rs.getString("bMbDate"))
                        .mbAmount(rs.getInt("bMbAmount"))
                        .estimatedCost(rs.getString("bEstimatedCost"))
                        .tenderType(rs.getString("bTenderType"))
                        .percentageType(rs.getString("bPercentageType"))
                        .awardAmount(rs.getInt("bAwardAmount"))
                        .billDate(rs.getString("bBillDate"))
                        .billNo(rs.getInt("bBillNo"))
                        .billAmount(rs.getInt("bBillAmount"))
                        .deductionAmount(rs.getInt("bDeductionAmount"))
                        .remark(rs.getString("bRemark"))
                        .srNo(rs.getInt("bSrNo"))
                        .deductionDescription(rs.getString("bDeductionDescription"))
                        .additionDeduction(rs.getString("bAdditionDeduction"))
                        .calculationMethod(rs.getString("bCalculationMethod"))
                        .percentage(rs.getString("bPercentage"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsRunningAccountFinalBillApplicationMap.put(runningAccountId, wmsRunningAccountFinalBillApplication);
        }
        return new ArrayList<>(wmsRunningAccountFinalBillApplicationMap.values());
    }

}
