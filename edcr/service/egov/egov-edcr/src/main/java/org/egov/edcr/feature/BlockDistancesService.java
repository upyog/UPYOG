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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class BlockDistancesService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(BlockDistancesService.class);

    @Autowired
	MDMSCacheManager cache;
	

    /**
     * Validates the given Plan object.
     * Currently acts as a pass-through returning the same Plan without any modification.
     *
     * @param pl The Plan to validate.
     * @return The validated Plan (unchanged in this implementation).
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    /**
     * Processes the given Plan object by validating and evaluating distances between blocks.
     *
     * @param pl The Plan to process.
     * @return The processed Plan with scrutiny details added.
     */
    @Override
    public Plan process(Plan pl) {
        processDistanceBetweenBlocks(pl);
        return pl;
    }

    /**
     * Validates whether distances between each pair of blocks are defined in the Plan.
     * Also ensures that required building attributes (height and occupancies) are present.
     *
     * @param pl The Plan to validate.
     */
    public void validateDistanceBetweenBlocks(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        List<String> sourceBlockNumbers = new ArrayList<>();

        for (Block sourceBlock : pl.getBlocks()) {
            validateSourceBlock(pl, sourceBlock, errors);
            sourceBlockNumbers.add(sourceBlock.getNumber());
            validateDistancesBetweenBlockPairs(pl, sourceBlock, sourceBlockNumbers, errors);
        }
    }


	/**
	 * Validates required fields (building height and occupancies) for a given source block.
	 *
	 * @param pl     The Plan being validated.
	 * @param sourceBlock The block to validate.
	 * @param errors A map of validation errors to be added to.
	 */
    private void validateSourceBlock(Plan pl, Block sourceBlock, HashMap<String, String> errors) {
        if (sourceBlock.getBuilding() != null) {
            if (sourceBlock.getBuilding().getBuildingHeight().compareTo(BigDecimal.ZERO) == 0) {
                errors.put(String.format(DcrConstants.BLOCK_BUILDING_HEIGHT, sourceBlock.getNumber()),
                        edcrMessageSource.getMessage(
                                DcrConstants.OBJECTNOTDEFINED,
                                new String[]{String.format(DcrConstants.BLOCK_BUILDING_HEIGHT, sourceBlock.getNumber())},
                                LocaleContextHolder.getLocale()));
                pl.addErrors(errors);
            }
            if (sourceBlock.getBuilding().getOccupancies().isEmpty()) {
                errors.put(String.format(DcrConstants.BLOCK_BUILDING_OCCUPANCY, sourceBlock.getNumber()),
                        edcrMessageSource.getMessage(
                                DcrConstants.OBJECTNOTDEFINED,
                                new String[]{String.format(DcrConstants.BLOCK_BUILDING_OCCUPANCY, sourceBlock.getNumber())},
                                LocaleContextHolder.getLocale()));
                pl.addErrors(errors);
            }
        }
    }

    /**
     * Checks that distances are defined between a source block and all other destination blocks.
     * Adds an error if the distance is missing in both directions.
     *
     * @param pl                 The Plan being validated.
     * @param sourceBlock        The block used as source for checking distance.
     * @param sourceBlockNumbers List of block numbers already validated.
     * @param errors             A map of validation errors to be added to.
     */
    private void validateDistancesBetweenBlockPairs(Plan pl, Block sourceBlock, List<String> sourceBlockNumbers,
                                                    HashMap<String, String> errors) {
        for (Block destinationBlock : pl.getBlocks()) {
            if (!sourceBlockNumbers.contains(destinationBlock.getNumber())) {
                List<BigDecimal> distanceBetBlocks = getDistancesBetween(sourceBlock, destinationBlock.getNumber());
                List<BigDecimal> distanceBtwBlocks = getDistancesBetween(destinationBlock, sourceBlock.getNumber());

                if (distanceBetBlocks.isEmpty() && distanceBtwBlocks.isEmpty()) {
                    errors.put(String.format(DcrConstants.BLOCKS_DISTANCE, sourceBlock.getNumber(), destinationBlock.getNumber()),
                            edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                    new String[]{String.format(DcrConstants.BLOCKS_DISTANCE,
                                            sourceBlock.getNumber(), destinationBlock.getNumber())},
                                    LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                }
            }
        }
    }

    /**
     * Retrieves the list of distances from the given block to the target block number.
     *
     * @param block            The source block.
     * @param targetBlockNumber The number of the target block.
     * @return A list of BigDecimal distances.
     */
    private List<BigDecimal> getDistancesBetween(Block block, String targetBlockNumber) {
        List<BigDecimal> distances = new ArrayList<>();
        for (BlockDistances distance : block.getDistanceBetweenBlocks()) {
            if (distance.getBlockNumber().equals(targetBlockNumber)) {
                distances = distance.getDistances();
            }
        }
        return distances;
    }

    /**
     * Validates and processes the minimum distances between all block pairs in the Plan.
     * Populates the scrutiny details for reporting.
     *
     * @param pl The Plan object containing blocks and distances.
     */
    public void processDistanceBetweenBlocks(Plan pl) {
        if (pl.getBlocks().isEmpty()) return;

        validateDistanceBetweenBlocks(pl);

        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(Common_Distance_Between_Blocks);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        for (Block b : pl.getBlocks()) {
            for (Block block : pl.getBlocks()) {
                if (b.getNumber() != block.getNumber()) {
                    processBlockPairDistance(pl, b, block);
                }
            }
        }
    }


	/**
	 * Processes a pair of blocks to calculate the minimum distance between them and validate it.
	 *
	 * @param pl    The Plan containing the blocks.
	 * @param b     The first block.
	 * @param block The second block.
	 */
    private void processBlockPairDistance(Plan pl, Block b, Block block) {
        for (BlockDistances distanceBetweenBlock : b.getDistanceBetweenBlocks()) {
            if (distanceBetweenBlock.getBlockNumber().equals(block.getNumber())) {
                if (!distanceBetweenBlock.getDistances().isEmpty()) {
                    BigDecimal minimumDistance = distanceBetweenBlock.getDistances().get(0);
                    for (BigDecimal distance : distanceBetweenBlock.getDistances()) {
                        if (distance.compareTo(minimumDistance) < 0) {
                            minimumDistance = distance;
                        }
                    }
                    validateMinimumDistance(pl, minimumDistance, b, block, false, false);
                }
            }
        }
    }

    /**
     * Validates the actual minimum distance between two blocks against:
     * 1. The derived minimum required distance based on building height.
     * 2. The maximum of the relevant setback heights.
     * Adds scrutiny report entries based on validation results.
     *
     * @param pl             The Plan under validation.
     * @param actualDistance The actual minimum distance measured between the blocks.
     * @param b              The first block.
     * @param block          The second block.
     * @param valid1         Flag indicating whether height-based requirement is met.
     * @param valid2         Flag indicating whether setback-based requirement is met.
     */
    private void validateMinimumDistance(Plan pl, BigDecimal actualDistance, Block b, Block block, Boolean valid1, Boolean valid2) {
        BigDecimal bHeight = b.getBuilding().getBuildingHeight();
        BigDecimal blockHeight = block.getBuilding().getBuildingHeight();
        HashMap<BigDecimal, Block> blockMap = new HashMap<>();
        blockMap.put(bHeight, b);
        blockMap.put(blockHeight, block);

        List<BigDecimal> blkHeights = Arrays.asList(bHeight, blockHeight);
        BigDecimal maxHeight = blkHeights.stream().reduce(BigDecimal::max).get();

        BigDecimal blockDistanceServiceValue = getServiceBlockDistance(pl);
        ArrayList<BigDecimal> setBacksValues = collectSetBackValues(block, blockDistanceServiceValue);

        BigDecimal dividedHeight = maxHeight.divide(blockDistanceServiceValue, DcrConstants.DECIMALDIGITS_MEASUREMENTS,
                DcrConstants.ROUNDMODE_MEASUREMENTS);
        BigDecimal minHeight = Arrays.asList(dividedHeight, BigDecimal.valueOf(18)).stream().reduce(BigDecimal::min).get();

        if (actualDistance.compareTo(minHeight) >= 0) {
            valid1 = true;
        }

        BigDecimal maxSetBack = setBacksValues.stream().reduce(BigDecimal::max).get();
        if (actualDistance.compareTo(maxSetBack) >= 0) {
            valid2 = true;
        }

        setReportOutputDetails(pl, SUBRULE_37_1,
                String.format(SUB_RULE_DES, b.getNumber(), block.getNumber()),
                StringUtils.EMPTY, MINIMUM_DISTANCE_BUILDING,
                actualDistance.toString() + DcrConstants.IN_METER,
                valid1 ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

        setReportOutputDetails(pl, SUBRULE_37_1,
                String.format(SUB_RULE_DES, b.getNumber(), block.getNumber()),
                StringUtils.EMPTY, MINIMUM_DISTANCE_SETBACK,
                actualDistance.toString() + DcrConstants.IN_METER,
                valid2 ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
    }

    /**
     * Fetches the minimum permissible service distance for blocks from the feature rule cache.
     *
     * @param pl The Plan for which service block distance is needed.
     * @return The permissible block distance value.
     */
    private BigDecimal getServiceBlockDistance(Plan pl) {
    	  List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.BLOCK_DISTANCES_SERVICE.getValue(), false);
          Optional<BlockDistancesServiceRequirement> matchedRule = rules.stream()
              .filter(BlockDistancesServiceRequirement.class::isInstance)
              .map(BlockDistancesServiceRequirement.class::cast)
              .findFirst();

        return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
    }

    /**
     * Collects all setback height values from a block including side yards and rear yard.
     * Defaults to service distance value if specific setbacks are not defined.
     *
     * @param block         The Block to collect setbacks from.
     * @param defaultValue  Default value to include if setbacks are not present.
     * @return A list of BigDecimal setback values.
     */
    private ArrayList<BigDecimal> collectSetBackValues(Block block, BigDecimal defaultValue) {
        ArrayList<BigDecimal> setBacksValues = new ArrayList<>();
        setBacksValues.add(defaultValue);
        List<SetBack> setBacks = block.getSetBacks();
        for (SetBack setback : setBacks) {
            if (setback.getRearYard() != null)
                setBacksValues.add(setback.getRearYard().getHeight());
            if (setback.getSideYard1() != null)
                setBacksValues.add(setback.getSideYard1().getHeight());
            if (setback.getSideYard2() != null)
                setBacksValues.add(setback.getSideYard2().getHeight());
        }
        return setBacksValues;
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
     * Returns an empty map as no amendments are defined for this feature.
     *
     * @return An empty map of amendments.
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
