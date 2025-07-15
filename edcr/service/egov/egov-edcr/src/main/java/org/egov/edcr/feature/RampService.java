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

import static org.egov.edcr.constants.DxfFileConstants.A_R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.DARamp;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Ramp;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RampService extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(RampService.class);
    private static final String SUBRULE_50_C_4_B = " 50-c-4-b";
    private static final String SUBRULE_40 = "40";
    /*
     * private static final String SUBRULE_40_A1 = "40A(1)"; private static final String SUBRULE_40_A_7_DESC =
     * "Minimum number of DA Rooms in block %s "; private static final String SUBRULE_40_A_3_WIDTH_DESC =
     * "Minimum Width of Ramp %s for block %s "; private static final String SUBRULE_40_DESC =
     * "Maximum slope of ramp %s for block %s ";
     */
    private static final String SUBRULE_50_C_4_B_DESCRIPTION = "Maximum slope of ramp %s";

    /*
     * private static final String SUBRULE_40_A_1_DESC = "DA Ramp"; private static final String SUBRULE_40_A_3_SLOPE_DESC =
     * "Maximum Slope of DA Ramp %s for block %s";
     */
    private static final String SUBRULE_50_C_4_B_SLOPE_DESCRIPTION = "Maximum Slope of DA Ramp %s";
    private static final String FLOOR = "Floor";
    // private static final String SUBRULE_40_A_3_WIDTH_DESCRIPTION = "Minimum Width of Ramp %s";
    private static final String SUBRULE_50_C_4_B_SLOPE_MAN_DESC = "Slope of DA Ramp";

    
    
    @Autowired
	CacheManagerMdms cache;
 
    /**
     * Validates the given Plan object for ramp and DA ramp requirements.
     *
     * @param pl the Plan object to validate
     * @return the validated Plan object with errors added if any
     */
    @Override
    public Plan validate(Plan pl) {
        validateRampMeasurements(pl);
        validateDARamps(pl);
        return pl;
    }

    /**
     * Validates ramp measurements for each floor of every block in the plan.
     *
     * @param pl the Plan object containing blocks and floors to validate ramps
     */
    private void validateRampMeasurements(Plan pl) {
        for (Block block : pl.getBlocks()) {
            if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
                for (Floor floor : block.getBuilding().getFloors()) {
                    List<Ramp> ramps = floor.getRamps();
                    if (ramps != null && !ramps.isEmpty()) {
                        for (Ramp ramp : ramps) {
                            List<Measurement> rampPolyLines = ramp.getRamps();
                            if (rampPolyLines != null && !rampPolyLines.isEmpty()) {
                                validateDimensions(pl, block.getNumber(), floor.getNumber(), ramp.getNumber().toString(), rampPolyLines);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Validates the presence and slope of DA (Differently Abled) ramps in each block of the plan.
     *
     * @param pl the Plan object containing blocks and occupancy information
     */

    private void validateDARamps(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper();

        if (pl != null && !pl.getBlocks().isEmpty()) {
            for (Block block : pl.getBlocks()) {
                if (shouldValidateDARamps(pl, block, mostRestrictiveOccupancyType)) {
                    if (!block.getDARamps().isEmpty()) {
                        validateSlopeForDARamps(pl, block, errors);
                    } else {
                        addMissingDARampError(pl, block, errors);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Determines if the DA ramp validation should be performed for a given block based on occupancy and plot conditions.
     *
     * @param pl the Plan object
     * @param block the Block to be checked
     * @param mostRestrictiveOccupancyType the most restrictive occupancy type in the plan
     * @return true if validation should be performed; false otherwise
     */

    private boolean shouldValidateDARamps(Plan pl, Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {
        return pl.getPlot() != null
                && !Util.checkExemptionConditionForSmallPlotAtBlkLevel(pl.getPlot(), block)
                && mostRestrictiveOccupancyType != null
                && mostRestrictiveOccupancyType.getSubtype() != null
                && !A_R.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode());
    }

    /**
     * Validates the slope values for DA ramps in a block and adds errors to the plan if not defined.
     *
     * @param pl the Plan object to add errors to
     * @param block the Block containing DA ramps
     * @param errors map of errors to be added
     */
    private void validateSlopeForDARamps(Plan pl, Block block, Map<String, String> errors) {
        boolean isSlopeDefined = false;
        for (DARamp daRamp : block.getDARamps()) {
            if (daRamp != null && daRamp.getSlope() != null && daRamp.getSlope().compareTo(BigDecimal.valueOf(0)) > 0) {
                isSlopeDefined = true;
            }
        }
        if (!isSlopeDefined) {
            errors.put(String.format(DcrConstants.RAMP_SLOPE, "", block.getNumber()),
                    edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                            new String[]{String.format(DcrConstants.RAMP_SLOPE, "", block.getNumber())},
                            LocaleContextHolder.getLocale()));
            pl.addErrors(errors);
        }
    }

    /**
     * Adds an error to the Plan indicating the absence of a required DA ramp in the block.
     *
     * @param pl the Plan object to add errors to
     * @param block the Block missing the DA ramp
     * @param errors map of errors to be added
     */
    private void addMissingDARampError(Plan pl, Block block, Map<String, String> errors) {
        errors.put(String.format("DA Ramp", block.getNumber()),
                edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                        new String[]{String.format("DA Ramp", block.getNumber())},
                        LocaleContextHolder.getLocale()));
        pl.addErrors(errors);
    }

    /**
     * Checks if a given occupancy type is one for which ramps are required.
     *
     * @param occupancyType the OccupancyType to check
     * @return true if the occupancy type requires ramps; false otherwise
     */

    private boolean getOccupanciesForRamp(OccupancyType occupancyType) {
        return occupancyType.equals(OccupancyType.OCCUPANCY_A2) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_A3) || occupancyType.equals(OccupancyType.OCCUPANCY_A4) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_B1) || occupancyType.equals(OccupancyType.OCCUPANCY_B2) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_B3) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_C) || occupancyType.equals(OccupancyType.OCCUPANCY_C1) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_C2) || occupancyType.equals(OccupancyType.OCCUPANCY_C3) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_D) || occupancyType.equals(OccupancyType.OCCUPANCY_D1) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_D2) || occupancyType.equals(OccupancyType.OCCUPANCY_E) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_F) || occupancyType.equals(OccupancyType.OCCUPANCY_F1) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_F2) || occupancyType.equals(OccupancyType.OCCUPANCY_F3) ||
                occupancyType.equals(OccupancyType.OCCUPANCY_F4);
    }

    
    /**
     * Processes the Plan object for compliance with ramp and DA ramp rules,
     * performing validations and adding scrutiny details.
     *
     * @param pl the Plan object to process
     * @return the processed Plan with scrutiny details
     */
    @Override
    public Plan process(Plan pl) {
        BigDecimal rampServiceValueOne = BigDecimal.ZERO;
        BigDecimal rampServiceExpectedSlopeOne = BigDecimal.ZERO;
        BigDecimal rampServiceDivideExpectedSlope = BigDecimal.ZERO;
        BigDecimal rampServiceSlopValue = BigDecimal.ZERO;
        BigDecimal rampServiceBuildingHeight = BigDecimal.ZERO;
        BigDecimal rampServiceTotalLength = BigDecimal.ZERO;
        BigDecimal rampServiceExpectedSlopeCompare = BigDecimal.ZERO;
        BigDecimal rampServiceExpectedSlopeTwo = BigDecimal.ZERO;
        BigDecimal rampServiceExpectedSlopeCompareTrue = BigDecimal.ZERO;
        BigDecimal rampServiceExpectedSlopeCompareFalse = BigDecimal.ZERO;

        validate(pl);

        if (pl != null && !pl.getBlocks().isEmpty()) {
            for (Block block : pl.getBlocks()) {
                ScrutinyDetail scrutinyDetail = createScrutinyDetail("DA Ramp - Defined or not", block.getNumber(), false);
                ScrutinyDetail scrutinyDetail1 = createScrutinyDetail("DA Ramp - Slope width", block.getNumber(), false);
                ScrutinyDetail scrutinyDetail2 = createScrutinyDetail("DA Ramp - Maximum Slope", block.getNumber(), false);
                ScrutinyDetail scrutinyDetail3 = createScrutinyDetail("DA Room", block.getNumber(), true);
                ScrutinyDetail scrutinyDetail4 = createScrutinyDetail("Ramp - Minimum Width", block.getNumber(), true);
                ScrutinyDetail scrutinyDetail5 = createScrutinyDetail("Ramp - Maximum Slope", block.getNumber(), true);

                List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.RAMP_SERVICE, false);
                Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

                if (matchedRule.isPresent()) {
                    MdmsFeatureRule rule = matchedRule.get();
                    rampServiceValueOne = rule.getRampServiceValueOne();
                    rampServiceExpectedSlopeOne = rule.getRampServiceExpectedSlopeOne();
                    rampServiceDivideExpectedSlope = rule.getRampServiceDivideExpectedSlope();
                    rampServiceSlopValue = rule.getRampServiceSlopValue();
                    rampServiceBuildingHeight = rule.getRampServiceBuildingHeight();
                    rampServiceTotalLength = rule.getRampServiceTotalLength();
                    rampServiceExpectedSlopeTwo = rule.getRampServiceExpectedSlopeTwo();
                    rampServiceExpectedSlopeCompare = rule.getRampServiceExpectedSlopeCompare();
                    rampServiceExpectedSlopeCompareTrue = rule.getRampServiceExpectedSlopeCompareTrue();
                    rampServiceExpectedSlopeCompareFalse = rule.getRampServiceExpectedSlopeCompareFalse();
                }

                processRampSlopeValidation(pl, block, rampServiceValueOne, rampServiceExpectedSlopeOne,
                        rampServiceDivideExpectedSlope, rampServiceSlopValue, scrutinyDetail1, scrutinyDetail2);

                processDARoomValidation(pl, block, rampServiceBuildingHeight, scrutinyDetail3);
            }
        }

        return pl;
    }
    
    /**
     * Creates a ScrutinyDetail object with given parameters.
     *
     * @param keySuffix suffix to be used in the scrutiny detail key
     * @param blockNumber the number of the block being validated
     * @param hasFloorColumn whether floor column should be added
     * @return the constructed ScrutinyDetail object
     */

    private ScrutinyDetail createScrutinyDetail(String keySuffix, String blockNumber, boolean hasFloorColumn) {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        int columnIndex = 3;
        if (hasFloorColumn) {
            scrutinyDetail.addColumnHeading(columnIndex++, FLOOR);
        }
        scrutinyDetail.addColumnHeading(columnIndex++, REQUIRED);
        scrutinyDetail.addColumnHeading(columnIndex++, PROVIDED);
        scrutinyDetail.addColumnHeading(columnIndex, STATUS);
        scrutinyDetail.setKey("Block_" + blockNumber + "_" + keySuffix);
        if (keySuffix.equals("DA Room")) {
            scrutinyDetail.setSubHeading("Minimum number of da rooms");
        }
        return scrutinyDetail;
    }
    
    /**
     * Performs validation of DA ramp slope for the given block using specified slope values and adds scrutiny details.
     *
     * @param pl the Plan object
     * @param block the Block containing the DA ramps
     * @param rampServiceValueOne base value to check if slope is defined
     * @param rampServiceExpectedSlopeOne numerator of expected slope calculation
     * @param rampServiceDivideExpectedSlope denominator of expected slope calculation
     * @param rampServiceSlopValue minimum allowed slope value
     * @param scrutinyDetail1 scrutiny detail for slope definition
     * @param scrutinyDetail2 scrutiny detail for slope compliance
     */

    private void processRampSlopeValidation(Plan pl, Block block, BigDecimal rampServiceValueOne,
            BigDecimal rampServiceExpectedSlopeOne, BigDecimal rampServiceDivideExpectedSlope,
            BigDecimal rampServiceSlopValue, ScrutinyDetail scrutinyDetail1, ScrutinyDetail scrutinyDetail2) {

        OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper();

        if (pl.getPlot() != null
                && !Util.checkExemptionConditionForSmallPlotAtBlkLevel(pl.getPlot(), block)
                && mostRestrictiveOccupancyType != null
                && mostRestrictiveOccupancyType.getSubtype() != null
                && !A_R.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())) {

            if (!block.getDARamps().isEmpty()) {
                boolean isSlopeDefined = isSlopeDefined(block, rampServiceValueOne);

                setReportOutputDetails(pl, SUBRULE_50_C_4_B, SUBRULE_50_C_4_B_SLOPE_MAN_DESC, "",
                        isSlopeDefined ? DcrConstants.OBJECTDEFINED_DESC : DcrConstants.OBJECTNOTDEFINED_DESC,
                        isSlopeDefined ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal(),
                        scrutinyDetail1);

                if (isSlopeDefined) {
                    validateRampSlopes(pl, block, rampServiceExpectedSlopeOne, rampServiceDivideExpectedSlope,
                            rampServiceSlopValue, scrutinyDetail2);
                }
            }
        }
    }

    /**
     * Checks if any DA ramp in the block has a slope defined above a specified threshold.
     *
     * @param block the Block to check
     * @param rampServiceValueOne minimum slope value to consider as defined
     * @return true if a valid slope is defined; false otherwise
     */
    private boolean isSlopeDefined(Block block, BigDecimal rampServiceValueOne) {
        for (DARamp daRamp : block.getDARamps()) {
            if (daRamp != null && daRamp.getSlope() != null
                    && daRamp.getSlope().compareTo(rampServiceValueOne) > 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validates that DA ramp slopes in the block fall within the allowed maximum slope.
     *
     * @param pl the Plan object
     * @param block the Block containing DA ramps
     * @param expectedSlopeOne numerator for slope validation
     * @param divideExpectedSlope denominator for slope calculation
     * @param rampServiceSlopValue maximum allowed slope
     * @param scrutinyDetail2 scrutiny detail to report results
     */

    private void validateRampSlopes(Plan pl, Block block, BigDecimal expectedSlopeOne,
            BigDecimal divideExpectedSlope, BigDecimal rampServiceSlopValue,
            ScrutinyDetail scrutinyDetail2) {

        boolean valid = false;
        Map<String, String> mapOfRampNumberAndSlopeValues = new HashMap<>();
        BigDecimal expectedSlope = expectedSlopeOne.divide(divideExpectedSlope, 2, RoundingMode.HALF_UP);

        for (DARamp daRamp : block.getDARamps()) {
            BigDecimal slope = daRamp.getSlope();
            if (slope != null && slope.compareTo(rampServiceSlopValue) > 0 && expectedSlope != null) {
                if (slope.compareTo(expectedSlope) <= 0) {
                    valid = true;
                    mapOfRampNumberAndSlopeValues.put("daRampNumber", daRamp.getNumber().toString());
                    mapOfRampNumberAndSlopeValues.put("slope", slope.toString());
                    break;
                }
            }
        }

        if (valid) {
            setReportOutputDetails(pl, SUBRULE_50_C_4_B,
                    String.format(SUBRULE_50_C_4_B_SLOPE_DESCRIPTION,
                            mapOfRampNumberAndSlopeValues.get("daRampNumber")),
                    expectedSlope.toString(),
                    mapOfRampNumberAndSlopeValues.get("slope"),
                    Result.Accepted.getResultVal(), scrutinyDetail2);
        } else {
            setReportOutputDetails(pl, SUBRULE_50_C_4_B,
                    String.format(SUBRULE_50_C_4_B_SLOPE_DESCRIPTION, ""),
                    expectedSlope.toString(), "Less than 0.08 for all da ramps",
                    Result.Not_Accepted.getResultVal(), scrutinyDetail2);
        }
    }

    private void processDARoomValidation(Plan pl, Block block, BigDecimal rampServiceBuildingHeight, ScrutinyDetail scrutinyDetail3) {
        
    }

    
    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual, String status,
            ScrutinyDetail scrutinyDetail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    private void setReportOutputDetailsFloorWise(Plan pl, String ruleNo, String floor, String expected, String actual,
            String status, ScrutinyDetail scrutinyDetail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(FLOOR, floor);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    private void setReportOutputDetailsFloorWiseWithDescription(Plan pl, String ruleNo, String ruleDesc, String floor,
            String expected, String actual, String status, ScrutinyDetail scrutinyDetail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(FLOOR, floor);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    private void validateDimensions(Plan plan, String blockNo, int floorNo, String rampNo,
            List<Measurement> rampPolylines) {
        int count = 0;
        for (Measurement m : rampPolylines) {
            if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0) {
                count++;
            }
        }
        if (count > 0) {
            plan.addError(String.format(DxfFileConstants.LAYER_RAMP_WITH_NO, blockNo, floorNo, rampNo),
                    count + " number of ramp polyline not having only 4 points in layer "
                            + String.format(DxfFileConstants.LAYER_RAMP_WITH_NO, blockNo, floorNo, rampNo));

        }
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
