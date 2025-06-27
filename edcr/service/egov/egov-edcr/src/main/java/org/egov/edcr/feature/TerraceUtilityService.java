/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.TerraceUtility;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
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

    // Main logic for processing terrace utility service validations
    @Override
    public Plan process(Plan pl) {

        // Default permissible value set to zero initially
        BigDecimal terraceUtilityValue = BigDecimal.ZERO;

        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);

        // Define the feature name for fetching rules
        String feature = MdmsFeatureConstants.TERRACE_UTILITY_SERVICE;

        // Prepare parameters for fetching MDMS rules
        Map<String, Object> params = new HashMap<>();
        
        // If occupancy is residential, set the corresponding string
       
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Retrieve all EDCR rules from the plan
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // Define the list of columns to retrieve from MDMS
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE);

        // Fetch permissible value from MDMS using occupancy and feature
        List<Map<String, Object>> permissibleValue = new ArrayList<>();
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        // If value is found, extract and convert it to BigDecimal
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
            terraceUtilityValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
        }

        // Iterate through each block in the plan
        if (pl.getBlocks() != null) {
            for (Block block : pl.getBlocks()) {

                // Create scrutiny detail object to hold validation report per block
                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Terrace Utility");

                // Define column headers for scrutiny output
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                scrutinyDetail.addColumnHeading(3, PERMITTED);
                scrutinyDetail.addColumnHeading(4, PROVIDED);
                scrutinyDetail.addColumnHeading(5, STATUS);

                // Iterate through terrace utilities for the block
                for (TerraceUtility terraceUtility : block.getTerraceUtilities()) {

                    // Prepare the result map for each utility
                    Map<String, String> details = new HashMap<>();
                    details.put(RULE_NO, RULE_34);

                    // Get the minimum distance of the terrace utility from the edge
                    BigDecimal minDistance = terraceUtility.getDistances().stream().reduce(BigDecimal::min).get();

                    // Set utility name in description
                    details.put(DESCRIPTION, terraceUtility.getName());

                    // Compare provided distance with required distance and record results
                    if (Util.roundOffTwoDecimal(minDistance).compareTo(terraceUtilityValue) >= 0) {
                        details.put(PERMITTED, terraceUtilityValue + DcrConstants.IN_METER);
                        details.put(PROVIDED, minDistance + DcrConstants.IN_METER);
                        details.put(STATUS, Result.Accepted.getResultVal());
                        scrutinyDetail.getDetail().add(details);
                        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                    } else {
                        details.put(PERMITTED, terraceUtilityValue + DcrConstants.IN_METER);
                        details.put(PROVIDED, minDistance + DcrConstants.IN_METER);
                        details.put(STATUS, Result.Not_Accepted.getResultVal());
                        scrutinyDetail.getDetail().add(details);
                        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                    }

                }

            }

        }

        // Return the updated plan after processing
        return pl;
    }
}

