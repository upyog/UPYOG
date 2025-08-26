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
import static org.egov.edcr.constants.DxfFileConstants.COLOUR_CODE_CULDESAC;
import static org.egov.edcr.constants.DxfFileConstants.COLOUR_CODE_LANE;
import static org.egov.edcr.constants.DxfFileConstants.COLOUR_CODE_LEACHPIT_TO_PLOT_BNDRY;
import static org.egov.edcr.constants.DxfFileConstants.COLOUR_CODE_NONNOTIFIEDROAD;
import static org.egov.edcr.constants.DxfFileConstants.COLOUR_CODE_NOTIFIEDROAD;
import static org.egov.edcr.constants.DxfFileConstants.COLOUR_CODE_WELLTOBOUNDARY;
import static org.egov.edcr.constants.DxfFileConstants.COLOUR_CODE_WELLTOLEACHPIT;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.IN_METER;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.WELL_DISTANCE_FROMBOUNDARY;
import static org.egov.edcr.utility.DcrConstants.WELL_ERROR_COLOUR_CODE_DISTANCE_FROMROAD;

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
public class Well extends FeatureProcess {

    /**
     * Validates the building plan for well distance requirements.
     * Currently commented out - would check if wells and waste disposal units are defined
     * and validate required distance measurements between wells, boundaries, roads, and
     * waste treatment facilities based on their types (proposed/existing).
     *
     * @param pl The building plan to validate
     * @return The unmodified plan
     */
    @Override
    public Plan validate(Plan pl) {/*
                                    * HashMap<String, String> errors = new HashMap<>(); if (pl != null && pl.getUtility() != null)
                                    * { if (!pl.getUtility().getWells().isEmpty()) { if
                                    * (!pl.getUtility().getWasteDisposalUnits().isEmpty()) { if
                                    * (pl.getUtility().getWells().get(0).getType() != null &&
                                    * pl.getUtility().getWasteDisposalUnits().get(0).getType() != null) { if
                                    * (pl.getUtility().getWells().get(0).getType() != null &&
                                    * pl.getUtility().getWells().get(0).getType().equals(DcrConstants.PROPOSED) &&
                                    * pl.getUtility().getWasteDisposalUnits().get(0).getType().equals(DcrConstants.PROPOSED)) { if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(7)))) { errors.put(SUB_RULE_104_2_DESCRIPTION,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { SUB_RULE_104_2_DESCRIPTION },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(8)))) { errors.put(SUB_RULE_104_4_DESCRIPTION,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { SUB_RULE_104_4_DESCRIPTION },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(9)))) {
                                    * errors.put(SUB_RULE_104_4_PLOT_DESCRIPTION, edcrMessageSource.getMessage(OBJECTNOTDEFINED,
                                    * new String[] { SUB_RULE_104_4_PLOT_DESCRIPTION }, LocaleContextHolder.getLocale()));
                                    * pl.addErrors(errors); } if (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput
                                    * -> roadOutput.colourCode.equals(String.valueOf(1)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(2)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(5)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(6)))) { errors.put(WELL_DISTANCE_FROM_ROAD,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { WELL_DISTANCE_FROM_ROAD },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } } else if
                                    * (pl.getUtility().getWells().get(0).getType() != null &&
                                    * pl.getUtility().getWells().get(0).getType().equals(DcrConstants.PROPOSED) &&
                                    * pl.getUtility().getWasteDisposalUnits().get(0).getType().equals(DcrConstants.EXISTING)) { if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(7)))) { errors.put(SUB_RULE_104_2_DESCRIPTION,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { SUB_RULE_104_2_DESCRIPTION },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(8)))) { errors.put(SUB_RULE_104_4_DESCRIPTION,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { SUB_RULE_104_4_DESCRIPTION },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(1)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(2)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(5)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(6)))) { errors.put(WELL_DISTANCE_FROM_ROAD,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { WELL_DISTANCE_FROM_ROAD },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } } else if
                                    * (pl.getUtility().getWells().get(0).getType() != null &&
                                    * pl.getUtility().getWells().get(0).getType().equals(DcrConstants.EXISTING) &&
                                    * pl.getUtility().getWasteDisposalUnits().get(0).getType().equals(DcrConstants.PROPOSED)) { if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(8)))) { errors.put(SUB_RULE_104_4_DESCRIPTION,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { SUB_RULE_104_4_DESCRIPTION },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(9)))) {
                                    * errors.put(SUB_RULE_104_4_PLOT_DESCRIPTION, edcrMessageSource.getMessage(OBJECTNOTDEFINED,
                                    * new String[] { SUB_RULE_104_4_PLOT_DESCRIPTION }, LocaleContextHolder.getLocale()));
                                    * pl.addErrors(errors); } } } } else { if (pl.getUtility().getWells().get(0).getType() != null
                                    * && pl.getUtility().getWells().get(0).getType().equals(DcrConstants.PROPOSED)) { if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(7)))) { errors.put(SUB_RULE_104_2_DESCRIPTION,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { SUB_RULE_104_2_DESCRIPTION },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } if
                                    * (pl.getUtility().getWellDistance().stream() .noneMatch(roadOutput ->
                                    * roadOutput.colourCode.equals(String.valueOf(1)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(2)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(5)) ||
                                    * roadOutput.colourCode.equals(String.valueOf(6)))) { errors.put(WELL_DISTANCE_FROM_ROAD,
                                    * edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { WELL_DISTANCE_FROM_ROAD },
                                    * LocaleContextHolder.getLocale())); pl.addErrors(errors); } } } } }
                                    */
        return pl;
    }

    /**
     * Processes well distance requirements for the building plan.
     * Would validate minimum distances from wells to roads, plot boundaries,
     * and waste treatment facilities, generating scrutiny details for compliance verification.
     *
     * @param pl The building plan to process
     * @return The processed plan with scrutiny details added
     */
    @Override
    public Plan process(Plan pl) {/*
                                   * validate(pl); scrutinyDetail = new ScrutinyDetail(); scrutinyDetail.addColumnHeading(1,
                                   * RULE_NO); scrutinyDetail.addColumnHeading(2, DESCRIPTION); scrutinyDetail.addColumnHeading(3,
                                   * REQUIRED); scrutinyDetail.addColumnHeading(4, PROVIDED); scrutinyDetail.addColumnHeading(5,
                                   * STATUS); scrutinyDetail.setKey("Common_Well"); if (!pl.getUtility().getWells().isEmpty()) {
                                   * if (!pl.getUtility().getWasteDisposalUnits().isEmpty()) { if
                                   * (pl.getUtility().getWells().get(0).getType() != null &&
                                   * pl.getUtility().getWasteDisposalUnits().get(0).getType() != null) { if
                                   * (pl.getUtility().getWells().get(0).getType() != null &&
                                   * pl.getUtility().getWells().get(0).getType().equals(DcrConstants.PROPOSED) &&
                                   * pl.getUtility().getWasteDisposalUnits().get(0).getType().equals(DcrConstants.PROPOSED)) {
                                   * printOutputForProposedWellAndProposedWasteDisposal(pl); } else if
                                   * (pl.getUtility().getWells().get(0).getType() != null &&
                                   * pl.getUtility().getWells().get(0).getType().equals(DcrConstants.PROPOSED) &&
                                   * pl.getUtility().getWasteDisposalUnits().get(0).getType().equals(DcrConstants.EXISTING)) {
                                   * printOutputForProposedWellAndExistingWasteDisposal(pl); } else if
                                   * (pl.getUtility().getWells().get(0).getType() != null &&
                                   * pl.getUtility().getWells().get(0).getType().equals(DcrConstants.EXISTING) &&
                                   * pl.getUtility().getWasteDisposalUnits().get(0).getType().equals(DcrConstants.PROPOSED)) {
                                   * printOutputForExistingWellAndProposedWasteDisposal(pl); } } } else { if
                                   * (pl.getUtility().getWells().get(0).getType() != null &&
                                   * pl.getUtility().getWells().get(0).getType().equals(DcrConstants.PROPOSED)) {
                                   * printOutputForProposedWellWithNoWasteDisposalDefined(pl); } } }
                                   */
        return pl;
    }

    /**
     * Processes well distance validation for different scenarios.
     * Validates distances based on color codes representing different measurement types
     * (well to boundary, well to road, well to waste disposal facilities).
     *
     * @param pl The building plan containing well distance information
     */
    private void printOutputForProposedWellWithNoWasteDisposalDefined(Plan pl) {
        String subRule = null;
        String subRuleDesc = null;
        boolean valid = false;
        for (RoadOutput roadOutput : pl.getUtility().getWellDistance()) {

            BigDecimal minimumDistance = BigDecimal.ZERO;
            if (checkConditionForNotifiedNonNotifiedRoad(roadOutput)) {
                minimumDistance = three;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForCuldesacRoad(roadOutput)) {
                minimumDistance = TWO;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForLane(roadOutput)) {
                minimumDistance = ONE_ANDHALF_MTR;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForBoundary(roadOutput)) {
                subRule = SUB_RULE_104_2;
                subRuleDesc = SUB_RULE_104_2_DESCRIPTION;
                minimumDistance = ONE_ANDHALF_MTR;
            } else
                continue;
            printReportOutput(pl, subRule, subRuleDesc, valid, roadOutput, minimumDistance);
        }
    }

    private void printOutputForExistingWellAndProposedWasteDisposal(Plan pl) {
        String subRule = null;
        String subRuleDesc = null;
        boolean valid = false;
        for (RoadOutput roadOutput : pl.getUtility().getWellDistance()) {

            BigDecimal minimumDistance = BigDecimal.ZERO;
            if (checkConditionForWellToLeachPit(roadOutput)) {
                subRule = SUB_RULE_104_4;
                subRuleDesc = SUB_RULE_104_4_DESCRIPTION;
                minimumDistance = BigDecimal.valueOf(7.5);
            } else if (checkConditionForLeachPitToBoundary(roadOutput)) {
                subRule = SUB_RULE_104_4;
                subRuleDesc = SUB_RULE_104_4_PLOT_DESCRIPTION;
                minimumDistance = BigDecimal.valueOf(1.2);
            } else
                continue;
            printReportOutput(pl, subRule, subRuleDesc, valid, roadOutput, minimumDistance);

        }
    }

    private void printOutputForProposedWellAndExistingWasteDisposal(Plan pl) {
        String subRule = null;
        String subRuleDesc = null;
        boolean valid = false;
        for (RoadOutput roadOutput : pl.getUtility().getWellDistance()) {

            BigDecimal minimumDistance = BigDecimal.ZERO;
            if (checkConditionForNotifiedNonNotifiedRoad(roadOutput)) {
                minimumDistance = three;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForCuldesacRoad(roadOutput)) {
                minimumDistance = TWO;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForLane(roadOutput)) {
                minimumDistance = ONE_ANDHALF_MTR;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForBoundary(roadOutput)) {
                subRule = SUB_RULE_104_2;
                subRuleDesc = SUB_RULE_104_2_DESCRIPTION;
                minimumDistance = ONE_ANDHALF_MTR;
            } else if (checkConditionForWellToLeachPit(roadOutput)) {
                subRule = SUB_RULE_104_4;
                subRuleDesc = SUB_RULE_104_4_DESCRIPTION;
                minimumDistance = BigDecimal.valueOf(7.5);
            } else
                continue;
            printReportOutput(pl, subRule, subRuleDesc, valid, roadOutput, minimumDistance);
        }
    }

    private void printOutputForProposedWellAndProposedWasteDisposal(Plan pl) {
        String subRule = null;
        String subRuleDesc = null;
        boolean valid = false;
        for (RoadOutput roadOutput : pl.getUtility().getWellDistance()) {

            BigDecimal minimumDistance = BigDecimal.ZERO;
            if (checkConditionForNotifiedNonNotifiedRoad(roadOutput)) {
                minimumDistance = three;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForCuldesacRoad(roadOutput)) {
                minimumDistance = TWO;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForLane(roadOutput)) {
                minimumDistance = ONE_ANDHALF_MTR;
                subRule = SUB_RULE_104_1;
                subRuleDesc = SUB_RULE_104_1_DESCRIPTION;
            } else if (checkConditionForBoundary(roadOutput)) {
                subRule = SUB_RULE_104_2;
                subRuleDesc = SUB_RULE_104_2_DESCRIPTION;
                minimumDistance = ONE_ANDHALF_MTR;
            } else if (checkConditionForWellToLeachPit(roadOutput)) {
                subRule = SUB_RULE_104_4;
                subRuleDesc = SUB_RULE_104_4_DESCRIPTION;
                minimumDistance = BigDecimal.valueOf(7.5);
            } else if (checkConditionForLeachPitToBoundary(roadOutput)) {
                subRule = SUB_RULE_104_4;
                subRuleDesc = SUB_RULE_104_4_PLOT_DESCRIPTION;
                minimumDistance = BigDecimal.valueOf(1.2);
            } else
                continue;
            printReportOutput(pl, subRule, subRuleDesc, valid, roadOutput, minimumDistance);

        }
    }

    /**
     * Generates scrutiny report output for well distance validation.
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
            errors.put(WELL_DISTANCE_FROMBOUNDARY,
                    getLocaleMessage(WELL_ERROR_COLOUR_CODE_DISTANCE_FROMROAD,
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

    /**
     * Checks if the road output represents well to boundary distance measurement.
     * Validates color code to determine if measurement is for well to plot boundary distance.
     *
     * @param roadOutput The road output containing color code and distance data
     * @return true if represents well to boundary measurement, false otherwise
     */
    private boolean checkConditionForLeachPitToBoundary(RoadOutput roadOutput) {
        return Integer.valueOf(roadOutput.colourCode) == COLOUR_CODE_LEACHPIT_TO_PLOT_BNDRY;
    }

    /**
     * Checks if the road output represents well to waste disposal facility distance measurement.
     * Validates color code to determine if measurement is for well to leach pit/waste facility distance.
     *
     * @param roadOutput The road output containing color code and distance data
     * @return true if represents well to waste facility measurement, false otherwise
     */
    private boolean checkConditionForWellToLeachPit(RoadOutput roadOutput) {
        return Integer.valueOf(roadOutput.colourCode) == COLOUR_CODE_WELLTOLEACHPIT;
    }

    /**
     * Checks if the road output represents well to plot boundary distance measurement.
     * Validates color code to determine if measurement is for well to boundary distance.
     *
     * @param roadOutput The road output containing color code and distance data
     * @return true if color code matches well to boundary measurement, false otherwise
     */
    private boolean checkConditionForBoundary(RoadOutput roadOutput) {
        return Integer.valueOf(roadOutput.colourCode) == COLOUR_CODE_WELLTOBOUNDARY;
    }

    /**
     * Checks if the road output represents well to lane distance measurement.
     * Validates color code to determine if measurement is for well to lane distance.
     *
     * @param roadOutput The road output containing color code and distance data
     * @return true if color code matches lane measurement, false otherwise
     */
    private boolean checkConditionForLane(RoadOutput roadOutput) {
        return Integer.valueOf(roadOutput.colourCode) == COLOUR_CODE_LANE;
    }

    /**
     * Checks if the road output represents well to cul-de-sac road distance measurement.
     * Validates color code to determine if measurement is for well to cul-de-sac distance.
     *
     * @param roadOutput The road output containing color code and distance data
     * @return true if color code matches cul-de-sac road measurement, false otherwise
     */
    private boolean checkConditionForCuldesacRoad(RoadOutput roadOutput) {
        return Integer.valueOf(roadOutput.colourCode) == COLOUR_CODE_CULDESAC;
    }

    /**
     * Checks if the road output represents well to notified or non-notified road distance measurement.
     * Validates color code to determine if measurement is for well to either type of road.
     *
     * @param roadOutput The road output containing color code and distance data
     * @return true if color code matches notified or non-notified road measurement, false otherwise
     */
    private boolean checkConditionForNotifiedNonNotifiedRoad(RoadOutput roadOutput) {
        return Integer.valueOf(roadOutput.colourCode) == COLOUR_CODE_NOTIFIEDROAD ||
                Integer.valueOf(roadOutput.colourCode) == COLOUR_CODE_NONNOTIFIEDROAD;
    }

    /**
     * Adds well distance validation results to the scrutiny report.
     * Creates a detailed report entry with rule information, distance requirements,
     * and compliance status.
     *
     * @param pl The building plan
     * @param ruleNo The rule number being validated
     * @param ruleDesc The rule description
     * @param expected The expected/required distance value
     * @param actual The actual/provided distance value
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
     * Returns amendment dates for well distance rules.
     * Currently returns an empty map as no amendments are defined.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
