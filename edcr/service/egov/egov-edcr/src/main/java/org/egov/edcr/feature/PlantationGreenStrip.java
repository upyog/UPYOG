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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.BLOCK;
import static org.egov.edcr.constants.EdcrReportConstants.RULE_37_6;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class PlantationGreenStrip extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(PlantationGreenStrip.class);

    @Autowired
  	MDMSCacheManager cache;
    
    /**
     * Validates the given plan.
     * 
     * @param pl The plan to be validated.
     * @return Currently returns null as the validation logic is not implemented.
     */
    @Override
    public Plan validate(Plan pl) {
        return null; // Validation logic is not implemented
    }

    /**
     * Processes the plantation green strip rules for the given plan.
     * Checks if plantation green strip width conditions are met for each block,
     * based on the permissible width and area threshold defined in the rule.
     *
     * @param pl The plan to be processed.
     * @return The updated plan after plantation green strip scrutiny.
     */

    @Override
    public Plan process(Plan pl) {
        Optional<PlantationGreenStripRequirement> ruleOpt = getPlantationGreenStripRule(pl);

        BigDecimal plantationGreenStripPlanValue = ruleOpt.map(PlantationGreenStripRequirement::getPlantationGreenStripPlanValue)
                                                           .orElse(BigDecimal.ZERO);
        BigDecimal plantationGreenStripMinWidth = ruleOpt.map(PlantationGreenStripRequirement::getPlantationGreenStripMinWidth)
                                                          .orElse(BigDecimal.ZERO);

        if (isPlotAreaGreaterThanPermissible(pl, plantationGreenStripPlanValue)) {
            for (Block block : pl.getBlocks()) {
                processBlock(pl, block, plantationGreenStripMinWidth);
            }
        }

        return pl;
    }

    /**
     * Retrieves the plantation green strip rule from MDMS for the given plan.
     *
     * @param pl The plan containing feature and configuration context.
     * @return An Optional containing the first matched {@link MdmsFeatureRule}, if available.
     */
    private Optional<PlantationGreenStripRequirement> getPlantationGreenStripRule(Plan pl) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.PLANTATION_GREEN_STRIP.getValue(), false);
        return rules.stream()
            .filter(PlantationGreenStripRequirement.class::isInstance)
            .map(PlantationGreenStripRequirement.class::cast)
            .findFirst();
    }


    /**
     * Checks whether the plot area in the plan is greater than the given permissible value.
     *
     * @param pl               The plan containing the plot information.
     * @param permissibleValue The threshold area value to compare against.
     * @return true if plot area is greater than permissible value, false otherwise.
     */
    private boolean isPlotAreaGreaterThanPermissible(Plan pl, BigDecimal permissibleValue) {
        return pl.getPlot() != null && pl.getPlot().getArea().compareTo(permissibleValue) > 0;
    }

    /**
     * Processes a single block in the plan to evaluate plantation green strip widths.
     * Builds and adds scrutiny details based on the minimum green strip width found in the block.
     *
     * @param pl                          The plan being processed.
     * @param block                       The block to be evaluated.
     * @param plantationGreenStripMinWidth The minimum required width for plantation green strips.
     */
    private void processBlock(Plan pl, Block block, BigDecimal plantationGreenStripMinWidth) {
        ScrutinyDetail scrutinyDetail = createScrutinyDetailForBlock(block);

        boolean isWidthAccepted = false;
        BigDecimal minWidth = BigDecimal.ZERO;

        List<BigDecimal> widths = block.getPlantationGreenStripes().stream()
                .map(greenStrip -> greenStrip.getWidth())
                .collect(Collectors.toList());

        if (!widths.isEmpty()) {
            minWidth = widths.stream().reduce(BigDecimal::min).get();

            if (minWidth.compareTo(plantationGreenStripMinWidth) >= 0) {
                isWidthAccepted = true;
            }

            buildResult(pl, scrutinyDetail, isWidthAccepted,
                    WIDTH_OF_CONTINUOUS_PLANTATION_GREEN_STRIP,
                    GREATER_THAN_EQUAL + plantationGreenStripMinWidth.toString(),
                    minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
                            .toString());
        }
    }

    /**
     * Creates a {@link ScrutinyDetail} object for the specified block for plantation green strip width check.
     *
     * @param block The block for which scrutiny details are to be created.
     * @return A configured {@link ScrutinyDetail} object with column headings.
     */
    private ScrutinyDetail createScrutinyDetailForBlock(Block block) {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        scrutinyDetail.setKey(BLOCK + block.getNumber() + CONTINUOUS_GREEN_PLANTING_STRIP);
        return scrutinyDetail;
    }

    /**
     * Builds a result entry and appends it to the plan's scrutiny report output.
     *
     * @param pl         The plan to which the scrutiny detail will be added.
     * @param scrutinyDetail The scrutiny detail being populated.
     * @param valid      Whether the validation passed.
     * @param description Description of the validation being checked.
     * @param permited   Permissible value as per rule.
     * @param provided   Provided value in the plan.
     */
    private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, boolean valid, String description, String permited,
                             String provided) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_37_6);
        detail.setDescription(description);
        detail.setPermissible(permited);
        detail.setProvided(provided);
        detail.setStatus(valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

        Map<String, String> details = mapReportDetails(detail);
        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
    }

    
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>(); // Return an empty map for amendments
    }
}
