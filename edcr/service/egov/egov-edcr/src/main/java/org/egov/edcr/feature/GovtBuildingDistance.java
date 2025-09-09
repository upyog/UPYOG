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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.GovtBuildingDistanceRequirement;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;

@Service
public class GovtBuildingDistance extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(GovtBuildingDistance.class);

    // Rule identifier and description for government building distance scrutiny
    private static final String RULE_21 = "21";
    public static final String GOVTBUILDING_DESCRIPTION = "Distance from Government Building";
    public static final String BUILDING_HEIGHT = "Building Height: ";
    public static final String MT = "mt";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	MDMSCacheManager cache;

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
     * Processes the given plan to validate the distance from nearby government buildings
     * and the corresponding permissible building height.
     *
     * @param plan the building plan to be validated
     * @return the updated plan with validation results and errors (if any)
     */
    @Override
    public Plan process(Plan plan) {
        if (!isGovtBuildingNearby(plan)) {
            return plan;
        }

        ScrutinyDetail scrutinyDetail = createScrutinyDetail();
        Map<String, String> details = createInitialRuleDetails();
        Map<String, String> errors = new HashMap<>();

        List<BigDecimal> distances = plan.getDistanceToExternalEntity().getGovtBuildings();
        if (distances == null || distances.isEmpty()) {
            errors.put(DISTANCE_FROM_GOVT_BUILDING, NO_DISTANCE_PROVIDED);
            plan.addErrors(errors);
            return plan;
        }

        BigDecimal minDistance = getMinimumDistance(distances);
        BigDecimal maxHeight = getMaxBuildingHeight(plan.getBlocks());

        GovtBuildingDistanceRequirement rule = getApplicableRule(plan);
        if (rule == null) {
            errors.put(RULE_FETCH, NO_APPLICABLE_RULE);
            plan.addErrors(errors);
            return plan;
        }

        validateDistanceAndHeight(minDistance, maxHeight, rule, details, scrutinyDetail);
        plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        return plan;
    }
    
    /**
     * Checks whether the plan indicates proximity to a government building.
     *
     * @param plan the plan to check
     * @return true if the building is near a government building; false otherwise
     */
    private boolean isGovtBuildingNearby(Plan plan) {
        String nearGovtBuilding = plan.getPlanInformation().getBuildingNearGovtBuilding();
        return StringUtils.isNotBlank(nearGovtBuilding) && DcrConstants.YES.equalsIgnoreCase(nearGovtBuilding);
    }

    /**
     * Creates and initializes a {@link ScrutinyDetail} object for reporting.
     *
     * @return a populated ScrutinyDetail instance
     */
    private ScrutinyDetail createScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(COMMON_GOVT_BUILDING_DISTANCE);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, DISTANCE);
        scrutinyDetail.addColumnHeading(4, PERMITTED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        return scrutinyDetail;
    }

    /**
     * Creates a map with the initial rule metadata (e.g., rule number and description).
     *
     * @return a map of rule metadata
     */
    private Map<String, String> createInitialRuleDetails() {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_21);
        details.put(DESCRIPTION, GOVTBUILDING_DESCRIPTION);
        return details;
    }

    
    /**
     * Returns the minimum distance from the list of provided distances.
     *
     * @param distances list of distances from government buildings
     * @return the minimum distance, or 0 if list is empty
     */
    private BigDecimal getMinimumDistance(List<BigDecimal> distances) {
        return distances.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
    }

    /**
     * Calculates the maximum building height from all blocks in the plan.
     *
     * @param blocks list of blocks in the plan
     * @return the highest building height among the blocks
     */
    private BigDecimal getMaxBuildingHeight(List<Block> blocks) {
        return blocks.stream()
                     .filter(b -> b.getBuilding() != null)
                     .map(b -> b.getBuilding().getBuildingHeight())
                     .reduce(BigDecimal.ZERO, BigDecimal::max);
    }

    /**
     * Retrieves the applicable rule from the MDMS cache for validating government building distance.
     *
     * @param plan the plan for which the rule needs to be fetched
     * @return the matching {@link MdmsFeatureRule} or null if not found
     */
    private GovtBuildingDistanceRequirement getApplicableRule(Plan plan) {
    	List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.GOVT_BUILDING_DISTANCE.getValue(), false);
        rules.stream()
            .filter(GovtBuildingDistanceRequirement.class::isInstance)
            .map(GovtBuildingDistanceRequirement.class::cast)
            .findFirst();
            return rules.stream()
                    .map(obj -> (GovtBuildingDistanceRequirement) obj)
                    .findFirst()
                    .orElse(null);
    }

    
    /**
     * Validates the distance from the government building and compares it with the maximum allowed building height.
     * Adds the result into the scrutiny details.
     *
     * @param minDistance the minimum distance from a government building
     * @param maxHeight the maximum height of the building in the plan
     * @param rule the applicable MDMS 
     * */
    private void validateDistanceAndHeight(BigDecimal minDistance, BigDecimal maxHeight,
    		GovtBuildingDistanceRequirement rule, Map<String, String> details,
                                           ScrutinyDetail scrutinyDetail) {

        BigDecimal allowedDistance = rule.getGovtBuildingDistanceValue();
        BigDecimal maxAllowedHeight = rule.getGovtBuildingDistanceMaxHeight();

        if (minDistance.compareTo(allowedDistance) > 0) {
            details.put(DISTANCE, GREATER_THAN + allowedDistance);
            details.put(PERMITTED, ALL);
            details.put(PROVIDED, minDistance.toString());
            details.put(STATUS, Result.Accepted.getResultVal());
        } else {
            details.put(DISTANCE, LESS_THAN_EQUAL_TO + allowedDistance);
            details.put(PERMITTED, BUILDING_HEIGHT + maxAllowedHeight + MT);
            details.put(PROVIDED, BUILDING_HEIGHT + maxHeight + MT);
            details.put(STATUS, maxHeight.compareTo(maxAllowedHeight) <= 0
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal());
        }

        scrutinyDetail.getDetail().add(details);
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
