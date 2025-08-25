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
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static org.egov.edcr.constants.CommonFeatureConstants.GREATER_THAN;
import static org.egov.edcr.constants.CommonFeatureConstants.LESS_THAN_EQUAL_TO;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;


@Service
public class RiverDistance extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(RiverDistance.class);

    @Autowired
  	MDMSCacheManager cache;
  	
    // Validation method (currently returns the plan as is)
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    /**
     * Processes the given {@link Plan} object to evaluate minimum required distances
     * from river-related features (e.g., protection wall, embankment, river edges),
     * and adds scrutiny details accordingly.
     *
     * <p>The method uses river proximity information from the plan and checks compliance
     * with applicable MDMS rules. Scrutiny results are added to the report output.
     *
     * @param pl the plan to be processed
     * @return the processed {@link Plan} object with river distance scrutiny results added
     */
    @Override
    public Plan process(Plan pl) {
        ScrutinyDetail scrutinyDetail = createRiverScrutinyDetail();

        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_22);

        BigDecimal minDistanceFromProtectionWall = BigDecimal.ZERO;
        BigDecimal minDistanceFromEmbankment = BigDecimal.ZERO;
        BigDecimal minDistanceFromMainRiverEdge = BigDecimal.ZERO;
        BigDecimal minDistanceFromSubRiver = BigDecimal.ZERO;

        BigDecimal rDminDistanceFromProtectionWall = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromEmbankment = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromMainRiverEdge = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromSubRiver = BigDecimal.ZERO;

        List<River> rivers = pl.getDistanceToExternalEntity().getRivers();
        List<River> mainRiver = new ArrayList<>();
        List<River> subRiver = new ArrayList<>();

        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.RIVER_DISTANCE.getValue(), false);
        Optional<RiverDistanceRequirement> matchedRule = rules.stream()
            .filter(RiverDistanceRequirement.class::isInstance)
            .map(RiverDistanceRequirement.class::cast)
            .findFirst();
        if (matchedRule.isPresent()) {
        	RiverDistanceRequirement rule = matchedRule.get();
            rDminDistanceFromProtectionWall = rule.getrDminDistanceFromProtectionWall();
            rDminDistanceFromEmbankment = rule.getrDminDistanceFromEmbankment();
            rDminDistanceFromMainRiverEdge = rule.getrDminDistanceFromMainRiverEdge();
            rDminDistanceFromSubRiver = rule.getrDminDistanceFromSubRiver();
        }

        if (!rivers.isEmpty()) {
            mainRiver = rivers.stream()
                .filter(river -> MAIN_RIVER.equals(river.getColorCode()))
                .collect(Collectors.toList());
            subRiver = rivers.stream()
                .filter(river -> SUB_RIVER.equals(river.getColorCode()))
                .collect(Collectors.toList());
        }

        List<BigDecimal> distancesFromRiverProtectionWall = mainRiver.isEmpty() ? new ArrayList<>() : mainRiver.get(0).getDistancesFromProtectionWall();
        List<BigDecimal> distancesFromEmbankment = mainRiver.isEmpty() ? new ArrayList<>() : mainRiver.get(0).getDistancesFromEmbankment();
        List<BigDecimal> distancesFromMainRiverEdge = mainRiver.isEmpty() ? new ArrayList<>() : mainRiver.get(0).getDistancesFromRiverEdge();
        List<BigDecimal> distancesFromSubRiver = subRiver.isEmpty() ? new ArrayList<>() : subRiver.get(0).getDistancesFromProtectionWall();

        if (StringUtils.hasText(pl.getPlanInformation().getBuildingNearToRiver()) &&
                DcrConstants.YES.equalsIgnoreCase(pl.getPlanInformation().getBuildingNearToRiver())) {

            if (!distancesFromRiverProtectionWall.isEmpty()) {
                minDistanceFromProtectionWall = getMin(distancesFromRiverProtectionWall);
                scrutinyDetail.getDetail().add(buildDetails(RULE_22, MAIN_RIVER_PROTECTION_WALL_DESCRIPTION, rDminDistanceFromProtectionWall, minDistanceFromProtectionWall));
            }

            if (!distancesFromEmbankment.isEmpty()) {
                minDistanceFromEmbankment = getMin(distancesFromEmbankment);
                scrutinyDetail.getDetail().add(buildDetails(RULE_22, MAIN_RIVER_EMBANKMENT_DESCRIPTION, rDminDistanceFromEmbankment, minDistanceFromEmbankment));
            }

            if (!distancesFromMainRiverEdge.isEmpty()) {
                minDistanceFromMainRiverEdge = getMin(distancesFromMainRiverEdge);
                scrutinyDetail.getDetail().add(buildDetails(RULE_22, MAIN_RIVER_DESCRIPTION, rDminDistanceFromMainRiverEdge, minDistanceFromMainRiverEdge));
            }

            if (!distancesFromSubRiver.isEmpty()) {
                minDistanceFromSubRiver = getMin(distancesFromSubRiver);
                scrutinyDetail.getDetail().add(buildDetails(RULE_22, SUB_RIVER_DESCRIPTION, rDminDistanceFromSubRiver, minDistanceFromSubRiver));
            }

            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }

        return pl;
    }

  

    /**
     * Creates and initializes a {@link ScrutinyDetail} object specifically for river distance scrutiny.
     * Adds standard column headings such as rule number, description, permitted, provided, and status.
     *
     * @return the initialized {@link ScrutinyDetail} object for river distance reporting
     */
    private ScrutinyDetail createRiverScrutinyDetail() {
        ScrutinyDetail detail = new ScrutinyDetail();
        detail.setKey(Common_River_Distance);
        detail.addColumnHeading(1, RULE_NO);
        detail.addColumnHeading(2, DESCRIPTION);
        detail.addColumnHeading(3, PERMITTED);
        detail.addColumnHeading(4, PROVIDED);
        detail.addColumnHeading(5, STATUS);
        return detail;
    }

    /**
     * Returns the minimum value from a list of {@link BigDecimal} values.
     * If the list is empty, returns {@link BigDecimal#ZERO}.
     *
     * @param values a list of BigDecimal distances
     * @return the minimum distance or BigDecimal.ZERO if list is empty
     */
    private BigDecimal getMin(List<BigDecimal> values) {
        return values.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
    }


	/**
	 * Builds and returns a details map representing the result of a river distance check.
	 * Includes rule number, description, permitted value (with comparison symbol), provided value, and status.
	 *
	 * @param ruleNo the rule number being applied
	 * @param description the description of the check
	 * @param permitted the permitted distance as per rules
	 * @param provided the actual distance provided in the plan
	 * @return a map of result details for scrutiny
	 */
    private Map<String, String> buildDetails(String ruleNo, String description, BigDecimal permitted, BigDecimal provided) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(ruleNo);
        detail.setDescription(description);
        detail.setPermitted((provided.compareTo(permitted) > 0 ? GREATER_THAN : LESS_THAN_EQUAL_TO) + permitted);
        detail.setProvided(provided.toString());
        detail.setStatus(provided.compareTo(permitted) > 0 ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
        return mapReportDetails(detail);
    }


    // No amendments configured for this feature
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}