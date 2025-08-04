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

import static org.egov.edcr.constants.CommonFeatureConstants.EMPTY_STRING;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.OBJECTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED_DESC;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.entity.edcr.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WaterTreatmentPlant extends FeatureProcess {
    /**
     * Validates the building plan for water treatment plant requirements.
     * Currently commented out - would check if liquid waste treatment plants are required
     * based on occupancy type and built-up area, adding errors if missing.
     *
     * @param pl The building plan to validate
     * @return The unmodified plan
     */
    @Override
    public Plan validate(Plan pl) {/*
                                    * HashMap<String, String> errors = new HashMap<>(); if (pl != null && pl.getUtility() != null)
                                    * { // liquid waste treatment plant defined or not if (pl.getVirtualBuilding() != null &&
                                    * !pl.getVirtualBuilding().getOccupancies().isEmpty()) { for (OccupancyType occupancyType :
                                    * pl.getVirtualBuilding().getOccupancies()) { if
                                    * (checkOccupancyTypeEqualsToNonConditionalOccupancyTypes(occupancyType) &&
                                    * pl.getUtility().getLiquidWasteTreatementPlant().isEmpty()) {
                                    * errors.put(SUB_RULE_53_5_DESCRIPTION, edcrMessageSource.getMessage(OBJECTNOTDEFINED, new
                                    * String[] { SUB_RULE_53_5_DESCRIPTION }, LocaleContextHolder.getLocale()));
                                    * pl.addErrors(errors); break; } else if
                                    * (checkOccupancyTypeEqualsToConditionalOccupancyTypes(occupancyType) &&
                                    * pl.getVirtualBuilding().getTotalBuitUpArea() != null &&
                                    * pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(TWOTHOUSANDFIVEHUNDER) > 0 &&
                                    * pl.getUtility().getLiquidWasteTreatementPlant().isEmpty()) {
                                    * errors.put(SUB_RULE_53_5_DESCRIPTION, edcrMessageSource.getMessage(OBJECTNOTDEFINED, new
                                    * String[] { SUB_RULE_53_5_DESCRIPTION }, LocaleContextHolder.getLocale()));
                                    * pl.addErrors(errors); break; } } } }
                                    */
        return pl;
    }

    /**
     * Processes water treatment plant requirements for the building plan.
     * Currently commented out - would validate liquid waste treatment requirements
     * based on occupancy types and generate scrutiny details for compliance verification.
     *
     * @param pl The building plan to process
     * @return The unmodified plan
     */
    @Override
    public Plan process(Plan pl) {/*
                                   * validate(pl); scrutinyDetail = new ScrutinyDetail(); scrutinyDetail.addColumnHeading(1,
                                   * RULE_NO); scrutinyDetail.addColumnHeading(2, DESCRIPTION); scrutinyDetail.addColumnHeading(3,
                                   * REQUIRED); scrutinyDetail.addColumnHeading(4, PROVIDED); scrutinyDetail.addColumnHeading(5,
                                   * STATUS); scrutinyDetail.setKey("Common_Water Treatment Plant"); if (pl.getVirtualBuilding()
                                   * != null && !pl.getVirtualBuilding().getOccupancies().isEmpty()) { for (OccupancyType
                                   * occupancyType : pl.getVirtualBuilding().getOccupancies()) { if
                                   * (checkOccupancyTypeEqualsToNonConditionalOccupancyTypes(occupancyType)) {
                                   * processLiquidWasteTreatment(pl); break; } else if
                                   * (checkOccupancyTypeEqualsToConditionalOccupancyTypes(occupancyType) &&
                                   * pl.getVirtualBuilding().getTotalBuitUpArea() != null &&
                                   * pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(TWOTHOUSANDFIVEHUNDER) > 0) {
                                   * processLiquidWasteTreatment(pl); break; } } }
                                   */
        return pl;
    }

    /**
     * Processes liquid waste treatment plant requirements and generates scrutiny details.
     * Checks if liquid waste treatment plant is defined and creates appropriate
     * scrutiny report entries with compliance status.
     *
     * @param pl The building plan containing utility information
     */
    private void processLiquidWasteTreatment(Plan pl) {
        if (!pl.getUtility().getLiquidWasteTreatementPlant().isEmpty()) {
            setReportOutputDetailsWithoutOccupancy(pl, SUB_RULE_53_5, SUB_RULE_53_5_DESCRIPTION, EMPTY_STRING,
                    OBJECTDEFINED_DESC, Result.Accepted.getResultVal());
            return;
        } else {
            setReportOutputDetailsWithoutOccupancy(pl, SUB_RULE_53_5, SUB_RULE_53_5_DESCRIPTION, EMPTY_STRING,
                    OBJECTNOTDEFINED_DESC, Result.Not_Accepted.getResultVal());
            return;
        }
    }

    /**
     * Checks if the occupancy type requires liquid waste treatment plant unconditionally.
     * Returns true for occupancy types B1, B2, B3, C, C1, C2, C3, D, D1, D2, G1, G2, H, I1, I2
     * which always require liquid waste treatment facilities regardless of built-up area.
     *
     * @param occupancyType The occupancy type to check
     * @return true if occupancy type requires treatment plant unconditionally, false otherwise
     */
    private boolean checkOccupancyTypeEqualsToNonConditionalOccupancyTypes(OccupancyType occupancyType) {
        return occupancyType.equals(OccupancyType.OCCUPANCY_B1) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_B2) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_B3) || occupancyType.equals(OccupancyType.OCCUPANCY_C) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_C1) || occupancyType.equals(OccupancyType.OCCUPANCY_C2) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_C3) || occupancyType.equals(OccupancyType.OCCUPANCY_D) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_D1) || occupancyType.equals(OccupancyType.OCCUPANCY_D2) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_G1) || occupancyType.equals(OccupancyType.OCCUPANCY_G2) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_H) || occupancyType.equals(OccupancyType.OCCUPANCY_I1) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_I2);
    }

    /**
     * Checks if the occupancy type requires liquid waste treatment plant conditionally.
     * Returns true for occupancy types A1, A2, A3, A4 which require liquid waste treatment
     * facilities only when built-up area exceeds the specified threshold.
     *
     * @param occupancyType The occupancy type to check
     * @return true if occupancy type requires treatment plant conditionally, false otherwise
     */
    private boolean checkOccupancyTypeEqualsToConditionalOccupancyTypes(OccupancyType occupancyType) {
        return occupancyType.equals(OccupancyType.OCCUPANCY_A1) || occupancyType.equals(OccupancyType.OCCUPANCY_A2) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_A3) || occupancyType.equals(OccupancyType.OCCUPANCY_A4) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_A5) || occupancyType.equals(OccupancyType.OCCUPANCY_E) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_F) || occupancyType.equals(OccupancyType.OCCUPANCY_F1) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_F2) || occupancyType.equals(OccupancyType.OCCUPANCY_F3)
                || occupancyType.equals(OccupancyType.OCCUPANCY_F4);
    }

    /**
     * Adds liquid waste treatment validation results to the scrutiny report.
     * Creates a detailed report entry with rule information, requirements,
     * and compliance status without occupancy-specific details.
     *
     * @param pl The building plan
     * @param ruleNo The rule number being validated
     * @param ruleDesc The rule description
     * @param expected The expected/required value (empty for this validation)
     * @param actual The actual/provided status description
     * @param status The compliance status (Accepted/Not_Accepted)
     */
    private void setReportOutputDetailsWithoutOccupancy(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
            String status) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(ruleNo);
        detail.setDescription(ruleDesc);
        detail.setRequired(expected);
        detail.setProvided(actual);
        detail.setStatus(status);

        Map<String, String> details = mapReportDetails(detail);
        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
    }

    /**
     * Returns amendment dates for water treatment plant rules.
     * Currently returns an empty map as no amendments are defined.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
