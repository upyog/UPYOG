/*
 * UPYOG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
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

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.entity.edcr.*;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.DEF_IN_THE_PLAN;
import static org.egov.edcr.constants.CommonFeatureConstants.NOT_DEF_PLAN_VERIFY_REQ_DEF_BUSINESS;
import static org.egov.edcr.constants.CommonKeyConstants.COM_COL_DISPOSAL_SOLID_LIQUID_WASTE;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class SolidLiquidWasteTreatment extends FeatureProcess {


    /**
     * Validates the building plan for solid and liquid waste treatment requirements.
     * Currently performs no validation and returns the plan as-is.
     *
     * @param pl The building plan to validate
     * @return The unmodified plan
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    /**
     * Processes solid and liquid waste treatment requirements for the building plan.
     * Currently commented out and returns the plan without processing.
     *
     * @param pl The building plan to process
     * @return The unmodified plan
     */
    @Override
    public Plan process(Plan pl) {
        /*
         * validate(pl); processSolidLiquidWasteTreat(pl);
         */
        return pl;
    }

    /**
     * Processes solid and liquid waste treatment requirements for business occupancy buildings.
     * Checks if any floor units have OCCUPANCY_E (business) type and validates whether
     * solid liquid waste treatment facilities are defined in the plan.
     * Generates scrutiny details with verification status based on findings.
     *
     * @param pl The building plan to process for waste treatment requirements
     */
    private void processSolidLiquidWasteTreat(Plan pl) {
        validate(pl);
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, FIELDVERIFIED);
        scrutinyDetail.addColumnHeading(4, STATUS);
        scrutinyDetail.setKey(COM_COL_DISPOSAL_SOLID_LIQUID_WASTE);
        if (pl != null && !pl.getBlocks().isEmpty()) {
            Boolean isFound = false;
            for (Block b : pl.getBlocks()) {
                for (Floor f : b.getBuilding().getFloors()) {
                    for (FloorUnit unit : f.getUnits()) {
                        if (OccupancyType.OCCUPANCY_E.equals(unit.getOccupancy().getType())) {
                            isFound = true;
                        }
                    }
                }
            }
            if (isFound && pl.getUtility().getSolidLiqdWasteTrtmnt().isEmpty()) {
                /*
                 * Marked as verify. As per rule, This rule applicable for wedding hall. There is no colour code specific to
                 * identify business. For other type of business, this might not mandatory.
                 */
                ReportScrutinyDetail detail = new ReportScrutinyDetail();
                detail.setRuleNo(SUBRULE_55_11);
                detail.setDescription(SUBRULE_55_11_DESC);
                detail.setFieldVerified(NOT_DEF_PLAN_VERIFY_REQ_DEF_BUSINESS);
                detail.setStatus(Result.Verify.getResultVal());

                Map<String, String> details = mapReportDetails(detail);
                addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
            } else if (isFound && !pl.getUtility().getSolidLiqdWasteTrtmnt().isEmpty()) {
                ReportScrutinyDetail detail = new ReportScrutinyDetail();
                detail.setRuleNo(SUBRULE_55_11);
                detail.setDescription(SUBRULE_55_11_DESC);
                detail.setFieldVerified(DEF_IN_THE_PLAN);
                detail.setStatus(Result.Accepted.getResultVal());

                Map<String, String> details = mapReportDetails(detail);
                addScrutinyDetailtoPlan(scrutinyDetail, pl, details);

            }
        }
    }

    /**
     * Returns amendment dates for solid and liquid waste treatment rules.
     * Currently returns an empty map as no amendments are defined.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
