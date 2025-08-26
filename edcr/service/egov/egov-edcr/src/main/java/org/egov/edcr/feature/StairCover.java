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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.COMMON_MUMTY;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class StairCover extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(StairCover.class);

    @Autowired
	MDMSCacheManager cache;

    /**
     * Validates the building plan for stair cover requirements.
     * Currently performs no validation and returns the plan as-is.
     *
     * @param pl The building plan to validate
     * @return The unmodified plan
     */
    // Placeholder validate method (not performing any logic here)
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    /**
     * Processes stair cover (mumty) requirements for all blocks in the building plan.
     * Initializes scrutiny details, fetches permissible height values from MDMS,
     * and validates each block's stair covers against the requirements.
     *
     * @param pl The building plan to process
     * @return The processed plan with scrutiny details added
     */
    @Override
    public Plan process(Plan pl) {
        ScrutinyDetail scrutinyDetail = initializeScrutinyDetail();
        BigDecimal stairCoverValue = getStairCoverPermissibleValue(pl);

        for (Block block : pl.getBlocks()) {
            processBlockStairCovers(block, stairCoverValue, scrutinyDetail, pl);
        }

        return pl;
    }

    /**
     * Initializes the scrutiny detail object for stair cover validation reporting.
     * Sets up column headings and key for the mumty (stair cover) scrutiny report.
     *
     * @return Configured ScrutinyDetail object with appropriate headings and key
     */
    private ScrutinyDetail initializeScrutinyDetail() {
        ScrutinyDetail detail = new ScrutinyDetail();
        detail.setKey(COMMON_MUMTY);
        detail.addColumnHeading(1, RULE_NO);
        detail.addColumnHeading(2, DESCRIPTION);
        detail.addColumnHeading(3, VERIFIED);
        detail.addColumnHeading(4, ACTION);
        detail.addColumnHeading(5, STATUS);
        return detail;
    }

    /**
     * Retrieves the permissible stair cover height value from MDMS cache.
     * Fetches stair cover requirements based on plan configuration and returns
     * the permissible height limit.
     *
     * @param pl The building plan containing configuration details
     * @return The permissible stair cover height, or BigDecimal.ZERO if not found
     */
    private BigDecimal getStairCoverPermissibleValue(Plan pl) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.STAIR_COVER.getValue(), false);
        Optional<StairCoverRequirement> matchedRule = rules.stream()
            .filter(StairCoverRequirement.class::isInstance)
            .map(StairCoverRequirement.class::cast)
            .findFirst();

        return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
    }

    /**
     * Processes stair covers for a specific building block and generates scrutiny results.
     * Compares minimum stair cover height against permissible limits and determines
     * whether the height should be included in building height calculations.
     *
     * @param block The building block containing stair covers
     * @param permissibleHeight The maximum allowed stair cover height
     * @param scrutinyDetail The scrutiny detail object to add results to
     * @param plan The building plan for adding scrutiny details to report
     */
    private void processBlockStairCovers(Block block, BigDecimal permissibleHeight, ScrutinyDetail scrutinyDetail, Plan plan) {
        if (block.getStairCovers() != null && !block.getStairCovers().isEmpty()) {
            BigDecimal minHeight = block.getStairCovers().stream().reduce(BigDecimal::min).get();
            ReportScrutinyDetail detail = new ReportScrutinyDetail();
            detail.setRuleNo(RULE_44_C);
            detail.setDescription(STAIRCOVER_DESCRIPTION);
            detail.setVerified(STAIRCOVER_HEIGHT_DESC + permissibleHeight + MTS);

            if (minHeight.compareTo(permissibleHeight) <= 0) {
                detail.setAction(NOT_INCLUDED_STAIR_COVER_HEIGHT + minHeight + TO_BUILDING_HEIGHT);
                detail.setStatus(Result.Accepted.getResultVal());
            } else {
                detail.setAction(INCLUDED_STAIR_COVER_HEIGHT + minHeight + TO_BUILDING_HEIGHT);
                detail.setStatus(Result.Verify.getResultVal());
            }
            Map<String, String> details = mapReportDetails(detail);
            addScrutinyDetailtoPlan(scrutinyDetail, plan, details);
        }
    }

    /**
     * Returns amendment dates for stair cover rules.
     * Currently returns an empty map as no amendments are defined.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    // No amendments implemented currently
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}

