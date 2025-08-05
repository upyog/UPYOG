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
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.GREATER_THAN_EQUAL;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class SegregatedToilet extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(SegregatedToilet.class);

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	MDMSCacheManager cache;

    @Override
    public Plan validate(Plan pl) {
        return pl; // No specific validation logic implemented
    }

    
    /**
     * Processes the segregated toilet rules for a given plan.
     * Initializes scrutiny details, retrieves rule values, evaluates whether rules are applicable,
     * and applies validations for required segregated toilets and minimum dimension.
     *
     * @param pl the {@link Plan} object to be processed
     * @return the updated {@link Plan} object with scrutiny results
     */
    @Override
    public Plan process(Plan pl) {

        ScrutinyDetail scrutinyDetail = initializeScrutinyDetail();
        ReportScrutinyDetail details = initializeDetails();

        SegregatedToiletRuleValues ruleValues = getSegregatedToiletRuleValues(pl);
        BigDecimal minDimension = findMinimumDistanceToEntrance(pl);
        BigDecimal maxHeightOfBuilding = getMaxBuildingHeight(pl);
        BigDecimal maxNumOfFloors = getMaxFloorsAboveGround(pl);

        if (isRuleApplicable(pl, ruleValues, maxHeightOfBuilding, maxNumOfFloors)) {
            processSegregatedToilet(pl, scrutinyDetail, details, ruleValues);
            processMinimumDimension(pl, scrutinyDetail, details, ruleValues, minDimension);
        }

        return pl;
    }

    /**
     * Initializes the scrutiny detail with headers specific to segregated toilet checks.
     *
     * @return an initialized {@link ScrutinyDetail} object
     */
    private ScrutinyDetail initializeScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(Common_Segregated_Toilet);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
    }

    /**
     * Initializes and returns rule-related details including the rule number.
     *
     * @return a map containing rule details
     */
    private ReportScrutinyDetail initializeDetails() {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_59_10);
        return detail;
    }

    /**
     * Fetches segregated toilet rule values from the feature rules for the given plan.
     *
     * @param pl the plan object
     * @return an object containing segregated toilet rule thresholds and requirements
     */
    private SegregatedToiletRuleValues getSegregatedToiletRuleValues(Plan pl) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.SEGREGATED_TOILET.getValue(), false);
        Optional<SegregatedToiletRequirement> matchedRule = rules.stream()
            .filter(SegregatedToiletRequirement.class::isInstance)
            .map(SegregatedToiletRequirement.class::cast)
            .findFirst();

        SegregatedToiletRuleValues values = new SegregatedToiletRuleValues();
        if (matchedRule.isPresent()) {
        	SegregatedToiletRequirement rule = matchedRule.get();
            values.sTValueOne = rule.getsTValueOne();
            values.sTValueTwo = rule.getsTValueTwo();
            values.sTValueThree = rule.getsTValueThree();
            values.sTValueFour = rule.getsTValueFour();
            values.sTSegregatedToiletRequired = rule.getsTSegregatedToiletRequired();
            values.sTSegregatedToiletProvided = rule.getsTSegregatedToiletProvided();
            values.sTminDimensionRequired = rule.getsTminDimensionRequired();
        }
        return values;
    }

    /**
     * Finds the minimum distance from any segregated toilet to the main entrance in the plan.
     *
     * @param pl the plan object
     * @return the minimum distance as {@link BigDecimal}; returns zero if not applicable
     */
    private BigDecimal findMinimumDistanceToEntrance(Plan pl) {
        if (pl.getSegregatedToilet() != null && !pl.getSegregatedToilet().getDistancesToMainEntrance().isEmpty()) {
            return pl.getSegregatedToilet().getDistancesToMainEntrance().stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Determines the maximum building height among all blocks in the plan.
     *
     * @param pl the plan object
     * @return the maximum building height
     */
    private BigDecimal getMaxBuildingHeight(Plan pl) {
        BigDecimal max = BigDecimal.ZERO;
        for (Block b : pl.getBlocks()) {
            if (b.getBuilding().getBuildingHeight() != null && b.getBuilding().getBuildingHeight().compareTo(max) > 0) {
                max = b.getBuilding().getBuildingHeight();
            }
        }
        return max;
    }

    /**
     * Determines the maximum number of floors above ground among all blocks in the plan.
     *
     * @param pl the plan object
     * @return the maximum number of floors above ground
     */
    private BigDecimal getMaxFloorsAboveGround(Plan pl) {
        BigDecimal max = BigDecimal.ZERO;
        for (Block b : pl.getBlocks()) {
            if (b.getBuilding().getFloorsAboveGround() != null && b.getBuilding().getFloorsAboveGround().compareTo(max) > 0) {
                max = b.getBuilding().getFloorsAboveGround();
            }
        }
        return max;
    }

    /**
     * Evaluates whether the segregated toilet rule is applicable based on building type, height, built-up area, and floors.
     *
     * @param pl the plan object
     * @param vals the rule values to compare against
     * @param maxHeight the maximum height of the building
     * @param maxFloors the maximum number of floors above ground
     * @return true if the rule is applicable, false otherwise
     */
    private boolean isRuleApplicable(Plan pl, SegregatedToiletRuleValues vals, BigDecimal maxHeight, BigDecimal maxFloors) {
        if (pl.getVirtualBuilding() == null || pl.getVirtualBuilding().getMostRestrictiveFarHelper() == null
                || pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType() == null)
            return false;

        String typeCode = pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode();
        BigDecimal totalBuiltUp = pl.getVirtualBuilding().getTotalBuitUpArea();

        return (DxfFileConstants.A.equals(typeCode) && maxHeight.compareTo(vals.sTValueOne) >= 0)
                || ((DxfFileConstants.I.equals(typeCode) || DxfFileConstants.A.equals(typeCode) || DxfFileConstants.E.equals(typeCode))
                    && totalBuiltUp != null && totalBuiltUp.compareTo(vals.sTValueTwo) >= 0
                    && maxFloors.compareTo(vals.sTValueThree) >= 0)
                || (DxfFileConstants.C.equals(typeCode)
                    && totalBuiltUp != null && totalBuiltUp.compareTo(vals.sTValueFour) >= 0);
    }

    /**
     * Validates and adds scrutiny details for the segregated toilet requirement based on availability in the plan.
     *
     * @param pl the plan object
     * @param scrutinyDetail the scrutiny detail to update
     * @param detail the rule metadata details
     * @param vals the rule values
     */
    private void processSegregatedToilet(Plan pl, ScrutinyDetail scrutinyDetail, ReportScrutinyDetail detail, SegregatedToiletRuleValues vals) {
        detail.setDescription(SEGREGATEDTOILET_DESCRIPTION);
        detail.setRequired(vals.sTSegregatedToiletRequired.toString());

        if (pl.getSegregatedToilet() != null && pl.getSegregatedToilet().getSegregatedToilets() != null
                && !pl.getSegregatedToilet().getSegregatedToilets().isEmpty()) {
            detail.setProvided(String.valueOf(pl.getSegregatedToilet().getSegregatedToilets().size()));
            detail.setStatus(Result.Accepted.getResultVal());
        } else {
            detail.setProvided(vals.sTSegregatedToiletProvided.toString());
            detail.setStatus(Result.Not_Accepted.getResultVal());
        }

        Map<String, String> details = mapReportDetails(detail);
        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
    }

    /**
     * Validates and adds scrutiny details for the minimum dimension requirement for segregated toilets.
     *
     * @param pl the plan object
     * @param scrutinyDetail the scrutiny detail to update
     * @param detail the rule metadata details
     * @param vals the rule values
     * @param minDimension the minimum measured dimension
     */
    private void processMinimumDimension(Plan pl, ScrutinyDetail scrutinyDetail, ReportScrutinyDetail detail,
                                         SegregatedToiletRuleValues vals, BigDecimal minDimension) {

        detail.setDescription(SEGREGATEDTOILET_DIMENSION_DESCRIPTION);
        detail.setRequired(GREATER_THAN_EQUAL + vals.sTminDimensionRequired.toString());
        detail.setProvided(minDimension.toString());
        if (minDimension != null && minDimension.compareTo(vals.sTminDimensionRequired) >= 0) {
            detail.setStatus(Result.Accepted.getResultVal());
        } else {
            detail.setStatus(Result.Not_Accepted.getResultVal());
        }

        Map<String, String> details = mapReportDetails(detail);
        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
    }


/**
 * Helper class to encapsulate segregated toilet rule values fetched from MDMS.
 */
    private static class SegregatedToiletRuleValues {
        BigDecimal sTValueOne = BigDecimal.ZERO;
        BigDecimal sTValueTwo = BigDecimal.ZERO;
        BigDecimal sTValueThree = BigDecimal.ZERO;
        BigDecimal sTValueFour = BigDecimal.ZERO;
        BigDecimal sTSegregatedToiletProvided = BigDecimal.ZERO;
        BigDecimal sTSegregatedToiletRequired = BigDecimal.ZERO;
        BigDecimal sTminDimensionRequired = BigDecimal.ZERO;
    }


    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>(); // No amendments
    }
}

