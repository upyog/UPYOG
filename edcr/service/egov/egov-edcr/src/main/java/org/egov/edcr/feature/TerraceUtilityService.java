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
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.FeatureRuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.StairCoverRequirement;
import org.egov.common.entity.edcr.TerraceUtility;
import org.egov.common.entity.edcr.TerraceUtilityServiceRequirement;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TerraceUtilityService extends FeatureProcess {

    // Logger to log important info for debugging or monitoring
    private static final Logger LOG = LogManager.getLogger(TerraceUtilityService.class);

    // Rule identifier for terrace utility check
    private static final String RULE_34 = "43-1";

    // Feature key used in MDMS and for internal processing
    public static final String TERRACEUTILITIESDISTANCE = "TerraceUtilitiesDistance";

    // Error message key (not used in this version but defined for standardization)
    public static final String ERROR_MSG = "Minimum_distance";

    // Autowired service to fetch rules from MDMS
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	MDMSCacheManager cache;

    // No amendments defined for this rule
    @Override
    public Map<String, Date> getAmendments() {
        return null;
    }

    // No pre-validation logic implemented
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

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

    private BigDecimal getTerraceUtilityPermissibleValue(Plan pl) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.TERRACE_UTILITY_SERVICE.getValue(), false);
        Optional<TerraceUtilityServiceRequirement> matchedRule = rules.stream()
            .filter(TerraceUtilityServiceRequirement.class::isInstance)
            .map(TerraceUtilityServiceRequirement.class::cast)
            .findFirst();

        return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
    }

    private ScrutinyDetail createScrutinyDetailForBlock(Block block) {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Block_" + block.getNumber() + "_Terrace Utility");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, PERMITTED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
    }

    private void processTerraceUtilitiesForBlock(Block block, BigDecimal permissibleDistance,
                                                 ScrutinyDetail scrutinyDetail, Plan pl) {
        for (TerraceUtility terraceUtility : block.getTerraceUtilities()) {

            Map<String, String> details = new HashMap<>();
            details.put(RULE_NO, RULE_34);
            details.put(DESCRIPTION, terraceUtility.getName());

            BigDecimal minDistance = terraceUtility.getDistances().stream().reduce(BigDecimal::min).get();
            BigDecimal roundedDistance = Util.roundOffTwoDecimal(minDistance);

            details.put(PERMITTED, permissibleDistance + DcrConstants.IN_METER);
            details.put(PROVIDED, roundedDistance + DcrConstants.IN_METER);

            if (roundedDistance.compareTo(permissibleDistance) >= 0) {
                details.put(STATUS, Result.Accepted.getResultVal());
            } else {
                details.put(STATUS, Result.Not_Accepted.getResultVal());
            }

            scrutinyDetail.getDetail().add(details);
            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }
    }

}

