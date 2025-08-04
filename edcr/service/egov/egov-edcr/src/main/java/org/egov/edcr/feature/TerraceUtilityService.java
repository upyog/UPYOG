/*
 * UPYOG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency
,
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonKeyConstants.BLOCK;
import static org.egov.edcr.constants.CommonKeyConstants.TERRACE_UTILITY_SUFFIX;
import static org.egov.edcr.constants.EdcrReportConstants.RULE_43_1;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class TerraceUtilityService extends FeatureProcess {

    // Logger to log important info for debugging or monitoring
    private static final Logger LOG = LogManager.getLogger(TerraceUtilityService.class);

    // Autowired service to fetch rules from MDMS
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	MDMSCacheManager cache;

    /**
     * Returns amendment dates for terrace utility service rules.
     * Currently returns null as no amendments are defined.
     *
     * @return null indicating no amendments are available
     */
    // No amendments defined for this rule
    @Override
    public Map<String, Date> getAmendments() {
        return null;
    }

    /**
     * Validates the building plan for terrace utility service requirements.
     * Currently performs no validation and returns the plan as-is.
     *
     * @param pl The building plan to validate
     * @return The unmodified plan
     */
    // No pre-validation logic implemented
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    /**
     * Processes terrace utility service requirements for all blocks in the building plan.
     * Fetches permissible distance values from MDMS and validates each block's
     * terrace utilities against the minimum distance requirements.
     *
     * @param pl The building plan to process
     * @return The processed plan with scrutiny details added
     */
    @Override
    public Plan process(Plan pl) {

        BigDecimal terraceUtilityValue = getTerraceUtilityPermissibleValue(pl);

        if (pl.getBlocks() != null) {
            for (Block block : pl.getBlocks()) {
                ScrutinyDetail scrutinyDetail = createScrutinyDetailForBlock(block);
                processTerraceUtilitiesForBlock(block, terraceUtilityValue, scrutinyDetail, pl);
            }
        }

        return pl;
    }

    /**
     * Retrieves the permissible terrace utility distance value from MDMS cache.
     * Fetches terrace utility service requirements based on plan configuration
     * and returns the minimum required distance.
     *
     * @param pl The building plan containing configuration details
     * @return The permissible terrace utility distance, or BigDecimal.ZERO if not found
     */
    private BigDecimal getTerraceUtilityPermissibleValue(Plan pl) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.TERRACE_UTILITY_SERVICE.getValue(), false);
        Optional<TerraceUtilityServiceRequirement> matchedRule = rules.stream()
            .filter(TerraceUtilityServiceRequirement.class::isInstance)
            .map(TerraceUtilityServiceRequirement.class::cast)
            .findFirst();

        return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
    }

    /**
     * Creates and initializes a scrutiny detail object for a specific building block.
     * Sets up column headings and key for terrace utility validation reporting.
     *
     * @param block The building block for which scrutiny detail is being created
     * @return Configured ScrutinyDetail object with appropriate headings and key
     */
    private ScrutinyDetail createScrutinyDetailForBlock(Block block) {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(BLOCK + block.getNumber() + TERRACE_UTILITY_SUFFIX);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, PERMITTED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
    }

    /**
     * Processes all terrace utilities within a building block and generates scrutiny results.
     * Validates minimum distances against permissible limits and determines compliance status
     * for each terrace utility in the block.
     *
     * @param block The building block containing terrace utilities
     * @param permissibleDistance The minimum required distance for terrace utilities
     * @param scrutinyDetail The scrutiny detail object to add results to
     * @param pl The building plan for adding scrutiny details to report
     */
    private void processTerraceUtilitiesForBlock(Block block, BigDecimal permissibleDistance,
                                                 ScrutinyDetail scrutinyDetail, Plan pl) {
        for (TerraceUtility terraceUtility : block.getTerraceUtilities()) {
            BigDecimal minDistance = terraceUtility.getDistances().stream().reduce(BigDecimal::min).get();
            BigDecimal roundedDistance = Util.roundOffTwoDecimal(minDistance);

            ReportScrutinyDetail detail = new ReportScrutinyDetail();
            detail.setRuleNo(RULE_43_1);
            detail.setDescription(terraceUtility.getName());
            detail.setPermitted(permissibleDistance + DcrConstants.IN_METER);
            detail.setProvided(roundedDistance + DcrConstants.IN_METER);
            if (roundedDistance.compareTo(permissibleDistance) >= 0) {
                detail.setStatus(Result.Accepted.getResultVal());
            } else {
                detail.setStatus(Result.Not_Accepted.getResultVal());
            }
            Map<String, String> details = mapReportDetails(detail);
            addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
        }
    }

}

