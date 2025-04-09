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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.BlockDistances;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BlockDistancesService_Citya extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(BlockDistancesService_Citya.class);

    // Constants for subrules and other configurations
    public static final String SUBRULE_54_3 = "54-3";
    public static final String SUBRULE_55_2 = "55-2";
    public static final String SUBRULE_57_4 = "57-4";
    public static final String SUBRULE_58_3_A = "58-3-a";
    public static final String SUBRULE_59_3 = "59-3";
    public static final String SUBRULE_117_3 = "117-3";
    public static final String BLK_NUMBER = "blkNumber";
    public static final String SUBRULE = "subrule";
    public static final String MIN_DISTANCE = "minimumDistance";
    public static final String OCCUPANCY = "occupancy";
    private static final String SUBRULE_37_1 = "37-1";
    private static final String SUB_RULE_DES = "Minimum distance between blocks %s and %s";
    public static final String MINIMUM_DISTANCE_SETBACK = "Minimum distance should not be less than setback of tallest building or 3m";
    public static final String MINIMUM_DISTANCE_BUILDING = "Minimum distance should not be less than 1/3 of height of tallest building or 18m";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    /**
     * Processes the given plan to validate and calculate distances between blocks.
     * Calls the `processDistanceBetweenBlocks` method to handle the logic.
     *
     * @param pl The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */
    @Override
    public Plan process(Plan pl) {
        processDistanceBetweenBlocks(pl);
        return pl;
    }

    /**
     * Validates the distances between blocks in the given plan.
     * Checks if the required distances are defined and throws errors if not.
     *
     * @param pl The plan object to validate.
     */
    public void validateDistanceBetweenBlocks(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        List<String> sourceBlockNumbers = new ArrayList<>();

        // Iterate through all blocks in the plan
        for (Block sourceBlock : pl.getBlocks()) {
            // Validate building height and occupancies for the source block
            if (sourceBlock.getBuilding() != null) {
                if (sourceBlock.getBuilding().getBuildingHeight().compareTo(BigDecimal.ZERO) == 0) {
                    errors.put(String.format(DcrConstants.BLOCK_BUILDING_HEIGHT, sourceBlock.getNumber()),
                            edcrMessageSource.getMessage(
                                    DcrConstants.OBJECTNOTDEFINED, new String[]{String
                                            .format(DcrConstants.BLOCK_BUILDING_HEIGHT, sourceBlock.getNumber())},
                                    LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                }
                if (sourceBlock.getBuilding().getOccupancies().isEmpty()) {
                    errors.put(String.format(DcrConstants.BLOCK_BUILDING_OCCUPANCY, sourceBlock.getNumber()),
                            edcrMessageSource.getMessage(
                                    DcrConstants.OBJECTNOTDEFINED, new String[]{String
                                            .format(DcrConstants.BLOCK_BUILDING_OCCUPANCY, sourceBlock.getNumber())},
                                    LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                }
            }

            // Add the source block to the list of processed blocks
            sourceBlockNumbers.add(sourceBlock.getNumber());

            // Validate distances between the source block and other blocks
            for (Block destinationBlock : pl.getBlocks()) {
                if (!sourceBlockNumbers.contains(destinationBlock.getNumber())) {
                    List<BigDecimal> distanceBetBlocks = new ArrayList<>();
                    List<BigDecimal> distanceBtwBlocks = new ArrayList<>();

                    // Check distances from source to destination block
                    if (!sourceBlock.getDistanceBetweenBlocks().isEmpty()) {
                        for (BlockDistances distanceBetweenBlock : sourceBlock.getDistanceBetweenBlocks()) {
                            if (distanceBetweenBlock.getBlockNumber().equals(destinationBlock.getNumber())) {
                                distanceBetBlocks = distanceBetweenBlock.getDistances();
                            }
                        }
                    }

                    // Check distances from destination to source block
                    if (!destinationBlock.getDistanceBetweenBlocks().isEmpty()) {
                        for (BlockDistances distanceBetweenBlock : destinationBlock.getDistanceBetweenBlocks()) {
                            if (distanceBetweenBlock.getBlockNumber().equals(sourceBlock.getNumber())) {
                                distanceBtwBlocks = distanceBetweenBlock.getDistances();
                            }
                        }
                    }

                    // Throw error if no distances are found between the blocks
                    if (distanceBetBlocks.isEmpty() && distanceBtwBlocks.isEmpty()) {
                        errors.put(
                                String.format(DcrConstants.BLOCKS_DISTANCE, sourceBlock.getNumber(),
                                        destinationBlock.getNumber()),
                                edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                        new String[]{String.format(DcrConstants.BLOCKS_DISTANCE,
                                                sourceBlock.getNumber(), destinationBlock.getNumber())},
                                        LocaleContextHolder.getLocale()));
                        pl.addErrors(errors);
                    }
                }
            }
        }
    }

    /**
     * Processes the distances between blocks in the given plan.
     * Validates the distances and calculates the minimum required distances.
     *
     * @param pl The plan object to process.
     */
    public void processDistanceBetweenBlocks(Plan pl) {
        if (pl.getBlocks().isEmpty())
            return;

        // Validate distances between blocks
        validateDistanceBetweenBlocks(pl);

        // Initialize scrutiny details for reporting
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Distance Between Blocks");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        // Iterate through all blocks to calculate distances
        for (Block b : pl.getBlocks()) {
            for (Block block : pl.getBlocks()) {
                if (b.getNumber() != block.getNumber()) {
                    if (!b.getDistanceBetweenBlocks().isEmpty()) {
                        for (BlockDistances distanceBetweenBlock : b.getDistanceBetweenBlocks()) {
                            if (distanceBetweenBlock.getBlockNumber().equals(block.getNumber())) {
                                BigDecimal minimumDistance;
                                boolean valid1 = false;
                                boolean valid2 = false;

                                // Calculate the minimum distance between blocks
                                if (!distanceBetweenBlock.getDistances().isEmpty()) {
                                    minimumDistance = distanceBetweenBlock.getDistances().get(0);
                                    for (BigDecimal distance : distanceBetweenBlock.getDistances()) {
                                        if (distance.compareTo(minimumDistance) < 0) {
                                            minimumDistance = distance;
                                        }
                                    }
                                    validateMinimumDistance(pl, minimumDistance, b, block, valid1, valid2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Validates the minimum distance between two blocks.
     * Checks if the distance meets the required conditions based on height and setbacks.
     *
     * @param pl             The plan object.
     * @param actualDistance The actual distance between the blocks.
     * @param b              The source block.
     * @param block          The destination block.
     * @param valid1         Validation flag for height-based distance.
     * @param valid2         Validation flag for setback-based distance.
     */
    private void validateMinimumDistance(Plan pl, BigDecimal actualDistance, Block b, Block block, Boolean valid1,
                                         Boolean valid2) {
        BigDecimal bHeight = b.getBuilding().getBuildingHeight();
        BigDecimal blockHeight = block.getBuilding().getBuildingHeight();
        HashMap<BigDecimal, Block> blockMap = new HashMap<>();
        blockMap.put(bHeight, b);
        blockMap.put(blockHeight, block);
        List<BigDecimal> blkHeights = Arrays.asList(bHeight, blockHeight);
        BigDecimal maxHeight = blkHeights.stream().reduce(BigDecimal::max).get();

        BigDecimal blockDistanceServiceValue = BigDecimal.ZERO;

        // Fetch permissible values for block distances
        String occupancyName = null;
        String feature = MdmsFeatureConstants.BLOCK_DISTANCES_SERVICE;
        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE);

        List<Map<String, Object>> permissibleValue = new ArrayList<>();
        try {
            permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
            LOG.info("permissibleValue" + permissibleValue);
        } catch (NullPointerException e) {
            LOG.error("Permissible Value for BlockDistancesService not found--------", e);
        }

        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
            blockDistanceServiceValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
        } else {
            blockDistanceServiceValue = BigDecimal.ZERO;
        }

        ArrayList<BigDecimal> setBacksValues = new ArrayList<>();
        setBacksValues.add(blockDistanceServiceValue);
        List<SetBack> setBacks = block.getSetBacks();
        for (SetBack setback : setBacks) {
            if (setback.getRearYard() != null)
                setBacksValues.add(setback.getRearYard().getHeight());
            if (setback.getSideYard1() != null)
                setBacksValues.add(setback.getSideYard1().getHeight());
            if (setback.getSideYard2() != null)
                setBacksValues.add(setback.getSideYard2().getHeight());
        }

        BigDecimal dividedHeight = maxHeight.divide(blockDistanceServiceValue, DcrConstants.DECIMALDIGITS_MEASUREMENTS,
                DcrConstants.ROUNDMODE_MEASUREMENTS);

        List<BigDecimal> heights = Arrays.asList(dividedHeight, BigDecimal.valueOf(18));
        BigDecimal minHeight = heights.stream().reduce(BigDecimal::min).get();

        if (actualDistance.compareTo(minHeight) >= 0) {
            valid1 = true;
        }

        BigDecimal maxSetBack = setBacksValues.stream().reduce(BigDecimal::max).get();
        if (actualDistance.compareTo(maxSetBack) >= 0) {
            valid2 = true;
        }

        if (valid1) {
            setReportOutputDetails(pl, SUBRULE_37_1, String.format(SUB_RULE_DES, b.getNumber(), block.getNumber()),
                    StringUtils.EMPTY, MINIMUM_DISTANCE_BUILDING, actualDistance.toString() + DcrConstants.IN_METER,
                    Result.Accepted.getResultVal());
        } else {
            setReportOutputDetails(pl, SUBRULE_37_1, String.format(SUB_RULE_DES, b.getNumber(), block.getNumber()),
                    StringUtils.EMPTY, MINIMUM_DISTANCE_BUILDING, actualDistance.toString() + DcrConstants.IN_METER,
                    Result.Not_Accepted.getResultVal());
        }

        if (valid2) {
            setReportOutputDetails(pl, SUBRULE_37_1, String.format(SUB_RULE_DES, b.getNumber(), block.getNumber()),
                    StringUtils.EMPTY, MINIMUM_DISTANCE_SETBACK, actualDistance.toString() + DcrConstants.IN_METER,
                    Result.Accepted.getResultVal());
        } else {
            setReportOutputDetails(pl, SUBRULE_37_1, String.format(SUB_RULE_DES, b.getNumber(), block.getNumber()),
                    StringUtils.EMPTY, MINIMUM_DISTANCE_SETBACK, actualDistance.toString() + DcrConstants.IN_METER,
                    Result.Not_Accepted.getResultVal());
        }
    }

    /**
     * Sets the report output details for the scrutiny process.
     *
     * @param pl       The plan object.
     * @param ruleNo   The rule number.
     * @param ruleDesc The rule description.
     * @param occupancy The occupancy type.
     * @param expected The expected value.
     * @param actual   The actual value.
     * @param status   The status of the validation.
     */
    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String occupancy, String expected,
                                        String actual, String status) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    /**
     * Returns an empty map as no amendments are defined for this feature.
     *
     * @return An empty map of amendments.
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
