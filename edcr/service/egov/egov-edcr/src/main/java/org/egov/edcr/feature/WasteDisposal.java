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
import static org.egov.edcr.constants.DxfFileConstants.COLOUR_CODE_LEACHPIT_TO_PLOT_BNDRY;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.IN_METER;
import static org.egov.edcr.utility.DcrConstants.OBJECTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.WASTEDISPOSAL;
import static org.egov.edcr.utility.DcrConstants.WASTE_DISPOSAL_DISTANCE_FROMBOUNDARY;
import static org.egov.edcr.utility.DcrConstants.WASTE_DISPOSAL_ERROR_COLOUR_CODE_DISTANCE_FROMBOUNDARY;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.entity.edcr.*;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WasteDisposal extends FeatureProcess {

    /**
     * Validates the building plan for waste disposal requirements.
     * Currently commented out - would check if waste disposal units or liquid waste
     * treatment plants are defined and add errors if missing.
     *
     * @param pl The building plan to validate
     * @return The unmodified plan
     */
    @Override
    public Plan validate(Plan pl) {
        /*
         * HashMap<String, String> errors = new HashMap<>(); // waste disposal defined or not if (pl != null && pl.getUtility() !=
         * null) { if (pl.getUtility().getLiquidWasteTreatementPlant().isEmpty()) { if
         * (pl.getUtility().getWasteDisposalUnits().isEmpty()) { errors.put(WASTEDISPOSAL,
         * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { WASTEDISPOSAL }, LocaleContextHolder.getLocale()));
         * pl.addErrors(errors); } } }
         */
        return pl;
    }
    /**
     * Processes waste disposal requirements for the building plan.
     * Currently commented out - would validate waste disposal units, check distances
     * from boundaries, and generate scrutiny details for compliance verification.
     *
     * @param pl The building plan to process
     * @return The unmodified plan
     */
    @Override
    public Plan process(Plan pl) {/*
                                   * validate(pl); scrutinyDetail = new ScrutinyDetail(); scrutinyDetail.addColumnHeading(1,
                                   * RULE_NO); scrutinyDetail.addColumnHeading(2, DESCRIPTION); scrutinyDetail.addColumnHeading(3,
                                   * REQUIRED); scrutinyDetail.addColumnHeading(4, PROVIDED); scrutinyDetail.addColumnHeading(5,
                                   * STATUS); scrutinyDetail.setKey("Common_Waste Disposal"); if
                                   * (pl.getUtility().getLiquidWasteTreatementPlant().isEmpty()) { if
                                   * (!pl.getUtility().getWasteDisposalUnits().isEmpty()) {
                                   * setReportOutputDetailsWithoutOccupancy(pl, SUB_RULE_26A, SUB_RULE_26A_DESCRIPTION, "",
                                   * OBJECTDEFINED_DESC, Result.Accepted.getResultVal()); if
                                   * (pl.getUtility().getWells().isEmpty()) { for (org.egov.common.entity.edcr.WasteDisposal
                                   * wasteDisposal : pl.getUtility().getWasteDisposalUnits()) { if
                                   * (wasteDisposal.getType().equals(DcrConstants.PROPOSED)) {
                                   * printOutputForProposedWasteDisposal(pl); } } } } else {
                                   * setReportOutputDetailsWithoutOccupancy(pl, SUB_RULE_26A, SUB_RULE_26A_DESCRIPTION, "",
                                   * OBJECTNOTDEFINED_DESC, Result.Not_Accepted.getResultVal()); } }
                                   */
        return pl;
    }
    /**
     * Adds waste disposal validation results to the scrutiny report.
     * Creates a detailed report entry with rule information, requirements,
     * and compliance status without occupancy-specific details.
     *
     * @param pl The building plan
     * @param ruleNo The rule number being validated
     * @param ruleDesc The rule description
     * @param expected The expected/required value
     * @param actual The actual/provided value
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
     * Processes proposed waste disposal units and validates distance requirements.
     * Iterates through well distances and validates minimum distance requirements
     * for waste disposal facilities like leach pits from plot boundaries.
     *
     * @param pl The building plan containing waste disposal information
     */
    private void printOutputForProposedWasteDisposal(Plan pl) {
        String subRule;
        String subRuleDesc;
        boolean valid = false;
        for (RoadOutput roadOutput : pl.getUtility().getWellDistance()) {

            BigDecimal minimumDistance;
            if (checkConditionForLeachPitToBoundary(roadOutput)) {
                subRule = SUB_RULE_104_4_WD;
                subRuleDesc = SUB_RULE_104_4_PLOT_DESCRIPTION_WD;
                minimumDistance = BigDecimal.valueOf(1.2);
            } else
                continue;
            printReportOutput(pl, subRule, subRuleDesc, valid, roadOutput, minimumDistance);

        }
    }
    /**
     * Generates scrutiny report output for waste disposal distance validation.
     * Validates actual distances against minimum requirements and creates
     * appropriate scrutiny details or error messages.
     *
     * @param pl The building plan
     * @param subRule The specific sub-rule identifier
     * @param subRuleDesc The sub-rule description
     * @param valid Whether the validation passed
     * @param roadOutput The road/distance output containing measurement data
     * @param minimumDistance The minimum required distance
     */
    private void printReportOutput(Plan pl, String subRule, String subRuleDesc, boolean valid, RoadOutput roadOutput,
            BigDecimal minimumDistance) {
        HashMap<String, String> errors = new HashMap<>();
        if (minimumDistance.compareTo(BigDecimal.ZERO) == 0) {
            errors.put(WASTE_DISPOSAL_DISTANCE_FROMBOUNDARY,
                    getLocaleMessage(WASTE_DISPOSAL_ERROR_COLOUR_CODE_DISTANCE_FROMBOUNDARY,
                            roadOutput.distance != null ? roadOutput.distance.toString()
                                    : EMPTY_STRING));
            pl.addErrors(errors);
        } else {
            if (roadOutput.distance != null &&
                    roadOutput.distance.compareTo(BigDecimal.ZERO) > 0
                    && roadOutput.distance.compareTo(minimumDistance) >= 0)
                valid = true;
            if (valid) {
                setReportOutputDetailsWithoutOccupancy(pl, subRule, subRuleDesc, minimumDistance.toString() + IN_METER,
                        roadOutput.distance + IN_METER, Result.Accepted.getResultVal());
            } else {
                setReportOutputDetailsWithoutOccupancy(pl, subRule, subRuleDesc, minimumDistance.toString() + IN_METER,
                        roadOutput.distance + IN_METER, Result.Not_Accepted.getResultVal());
            }
        }

    }

    private boolean checkConditionForLeachPitToBoundary(RoadOutput roadOutput) {
        return Integer.valueOf(roadOutput.colourCode) == COLOUR_CODE_LEACHPIT_TO_PLOT_BNDRY;
    }
    /**
     * Returns amendment dates for waste disposal rules.
     * Currently returns an empty map as no amendments are defined.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
