/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Lift;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.springframework.stereotype.Service;

@Service
public class LiftService extends FeatureProcess {

    private static final String SUBRULE_48_DESC = "Minimum number of lifts for block %s";
    private static final String SUBRULE_48 = "48";
    private static final String SUBRULE_48_DESCRIPTION = "Minimum number of lifts";
    private static final String SUBRULE_118 = "118";
    private static final String SUBRULE_118_DESCRIPTION = "Dimension Of lift";
    private static final String SUBRULE_118_DESC = "Minimum dimension Of lift";

    @Override
    public Plan validate(Plan plan) {
        for (Block block : plan.getBlocks()) {
            if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
                for (Floor floor : block.getBuilding().getFloors()) {
                    List<Lift> lifts = floor.getLifts();
                    if (lifts != null && !lifts.isEmpty()) {
                        for (Lift lift : lifts) {
                            List<Measurement> liftPolyLines = lift.getLifts();
                            if (liftPolyLines != null && !liftPolyLines.isEmpty()) {
                                validateDimensions(plan, block.getNumber(), floor.getNumber(),
                                        lift.getNumber().toString(), liftPolyLines);
                            }
                        }
                    }
                }
            }
        }
        return plan;
    }

    @Override
    public Plan process(Plan plan) {
        if (plan != null && !plan.getBlocks().isEmpty()) {
            blk: for (Block block : plan.getBlocks()) {
                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                scrutinyDetail.addColumnHeading(3, REQUIRED);
                scrutinyDetail.addColumnHeading(4, PROVIDED);
                scrutinyDetail.addColumnHeading(5, STATUS);
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Lift Requirements");

                if (block.getBuilding() != null && !block.getBuilding().getOccupancies().isEmpty()
                        && block.getBuilding().getFloors().stream()
                                .anyMatch(floor -> floor.getLifts() != null && !floor.getLifts().isEmpty())) {

                    // Process Minimum Number of Lifts
                    BigDecimal noOfLiftsRqrd = BigDecimal.ZERO;
                    if ((DxfFileConstants.A
                            .equals(plan.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
                            || DxfFileConstants.B.equals(
                                    plan.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode())
                            || DxfFileConstants.E.equals(
                                    plan.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode())
                            || DxfFileConstants.F.equals(
                                    plan.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode()))) {
                        noOfLiftsRqrd = BigDecimal.valueOf(1);
                        boolean valid = BigDecimal.valueOf(Double.valueOf(block.getNumberOfLifts()))
                                .compareTo(noOfLiftsRqrd) >= 0;
                        setReportOutputDetails(plan, SUBRULE_48, SUBRULE_48_DESCRIPTION, noOfLiftsRqrd.toString(),
                                block.getNumberOfLifts(), valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal(), scrutinyDetail);
                    }

                    // Process Lift Dimensions
                    BigDecimal liftWidth = BigDecimal.ZERO;
                    BigDecimal liftHeight = BigDecimal.ZERO;

                    if (block.getBuilding() != null && block.getBuilding().getBuildingHeight() != null
                            && block.getBuilding().getBuildingHeight().intValue() > 0) {
                        if (!block.getBuilding().getFloors().isEmpty()) {
                            flr: for (Floor floor : block.getBuilding().getFloors()) {
                                for (Lift lift : floor.getLifts()) {
                                    if (lift.getLiftClosed()) {
                                        for (Measurement measurement : lift.getLifts()) {
                                            liftHeight = measurement.getHeight().setScale(2, BigDecimal.ROUND_HALF_UP);
                                            liftWidth = measurement.getWidth().setScale(2, BigDecimal.ROUND_HALF_UP);
                                            break flr;
                                        }
                                    }
                                }
                            }
                            setReportOutputDetails(plan, SUBRULE_118,
                                    String.format(SUBRULE_118_DESCRIPTION, "",
                                            ""),
                                    "",
                                    liftHeight + " * " + liftWidth,
                                    Result.Accepted.getResultVal(), // Assuming dimensions are always accepted for now
                                    scrutinyDetail);
                        }
                    }
                }
                if (!scrutinyDetail.getDetail().isEmpty()) {
                    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail); // this line was present in below method: setReportOutputDetails()
                }
            }
        }
        return plan;
    }

    private void setReportOutputDetails(Plan plan, String ruleNo, String ruleDesc, String expected, String actual,
                                        String status, ScrutinyDetail scrutinyDetail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
    }

    private void validateDimensions(Plan plan, String blockNo, int floorNo, String liftNo,
                                    List<Measurement> liftPolylines) {
        int count = 0;
        for (Measurement m : liftPolylines) {
            if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0) {
                count++;
            }
        }
        if (count > 0) {
            plan.addError(String.format(DxfFileConstants.LAYER_LIFT_WITH_NO, blockNo, floorNo, liftNo),
                    count + " number of lift polyline not having only 4 points in layer "
                            + String.format(DxfFileConstants.LAYER_LIFT_WITH_NO, blockNo, floorNo, liftNo));
        }
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}