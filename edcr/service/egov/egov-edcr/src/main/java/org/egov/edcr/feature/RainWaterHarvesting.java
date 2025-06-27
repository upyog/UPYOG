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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RainWaterHarvesting extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(RainWaterHarvesting.class);
    private static final String RULE_51 = "10.3";
    /*
     * private static final String RULE_51_DESCRIPTION = "RainWater Storage Arrangement "; private static final String
     * RAINWATER_HARVESTING_TANK_CAPACITY = "Minimum capacity of Rain Water Harvesting Tank";
     */
    private static final String RULE_51_DESCRIPTION = "Rain Water Harvesting";
    // private static final String RAINWATER_HARVESTING_TANK_CAPACITY = "Minimum capacity of Rain Water Harvesting Tank";
    private static final String RWH_DECLARATION_ERROR = DxfFileConstants.RWH_DECLARED
            + " in PLAN_INFO layer must be declared as YES for plot area greater than 100 sqm.";

    @Override
    public Plan validate(Plan pl) {
        return pl;
    }
    
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
@Override
public Plan process(Plan pl) {
    // Initialize a map to store errors
    HashMap<String, String> errors = new HashMap<>();

    // Initialize scrutiny details for the report
    scrutinyDetail = new ScrutinyDetail();
    scrutinyDetail.addColumnHeading(1, RULE_NO); // Column for rule number
    scrutinyDetail.addColumnHeading(2, DESCRIPTION); // Column for description
    scrutinyDetail.addColumnHeading(4, PROVIDED); // Column for provided values
    scrutinyDetail.addColumnHeading(5, STATUS); // Column for status (Accepted/Not Accepted)
    scrutinyDetail.setKey("Common_Rain Water Harvesting"); // Key for the scrutiny detail

    // Define rule and description for rainwater harvesting
    String subRule = RULE_51;
    String subRuleDesc = RULE_51_DESCRIPTION;

    // Initialize variables for plot area and permissible values
    BigDecimal plotArea = pl.getPlot() != null ? pl.getPlot().getArea() : BigDecimal.ZERO;
    BigDecimal rainWaterHarvestingPermissibleValue = BigDecimal.ZERO;

    // Get the most restrictive FAR helper for the virtual building
    OccupancyTypeHelper mostRestrictiveFarHelper = pl.getVirtualBuilding() != null
            ? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
            : null;

    // Determine the occupancy type
    String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
    String feature = MdmsFeatureConstants.RAIN_WATER_HARVESTING; // Feature name for rainwater harvesting

    // Prepare parameters for fetching MDMS values
    Map<String, Object> params = new HashMap<>();
    
    params.put("feature", feature);
    params.put("occupancy", occupancyName);

    // Fetch the list of rules from the plan object
    Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

    // Specify the columns to fetch from the rules
    ArrayList<String> valueFromColumn = new ArrayList<>();
    valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE); // Permissible value for rainwater harvesting

    // Initialize a list to store permissible values
    List<Map<String, Object>> permissibleValue = new ArrayList<>();

    // Fetch permissible values from MDMS
    permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
    LOG.info("permissibleValue" + permissibleValue); // Log the fetched permissible values

    // Check if permissible values are available and update the permissible value for rainwater harvesting
    if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
        rainWaterHarvestingPermissibleValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
    }

    // Validate the plot area and occupancy type for rainwater harvesting
    if (mostRestrictiveFarHelper != null && mostRestrictiveFarHelper.getType() != null) {
        if (DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveFarHelper.getType().getCode()) &&
                plotArea.compareTo(rainWaterHarvestingPermissibleValue) >= 0) {
            addOutput(pl, errors, subRule, subRuleDesc); // Add output for compliance
        } else if (DxfFileConstants.F.equalsIgnoreCase(mostRestrictiveFarHelper.getType().getCode())) {
            addOutput(pl, errors, subRule, subRuleDesc); // Add output for compliance
        } else if (DxfFileConstants.G.equalsIgnoreCase(mostRestrictiveFarHelper.getType().getCode())) {
            addOutput(pl, errors, subRule, subRuleDesc); // Add output for compliance
        }
    }

    return pl; // Return the updated plan object
}

/**
 * Adds output details for rainwater harvesting compliance.
 *
 * @param pl The plan object
 * @param errors The map of errors
 * @param subRule The rule number
 * @param subRuleDesc The rule description
 */
private void addOutput(Plan pl, HashMap<String, String> errors, String subRule, String subRuleDesc) {
    if (pl.getPlanInformation() != null && pl.getPlanInformation().getRwhDeclared() != null) {
        if (pl.getPlanInformation().getRwhDeclared().equalsIgnoreCase(DcrConstants.NO)
                || pl.getPlanInformation().getRwhDeclared().equalsIgnoreCase(DcrConstants.NA)) {
            // Add error if rainwater harvesting is not declared
            errors.put(DxfFileConstants.RWH_DECLARED, RWH_DECLARATION_ERROR);
            pl.addErrors(errors);
            addReportOutput(pl, subRule, subRuleDesc); // Add report output
        } else {
            addReportOutput(pl, subRule, subRuleDesc); // Add report output
        }
    }
}

/**
 * Adds report output details for rainwater harvesting compliance.
 *
 * @param pl The plan object
 * @param subRule The rule number
 * @param subRuleDesc The rule description
 */
private void addReportOutput(Plan pl, String subRule, String subRuleDesc) {
    if (pl.getUtility() != null) {
        if (pl.getUtility().getRainWaterHarvest() != null && !pl.getUtility().getRainWaterHarvest().isEmpty()) {
            // Add report output if rainwater harvesting is defined
            setReportOutputDetails(pl, subRule, subRuleDesc, null,
                    "Capacity - " + pl.getUtility().getRainWaterHarvestingTankCapacity(),
                    Result.Verify.getResultVal());
        } else {
            // Add report output if rainwater harvesting is not defined
            setReportOutputDetails(pl, subRule, subRuleDesc, null,
                    "Not Defined in the plan",
                    Result.Not_Accepted.getResultVal());
        }
    }
}

/**
 * Sets the report output details for scrutiny.
 *
 * @param pl The plan object
 * @param ruleNo The rule number
 * @param ruleDesc The rule description
 * @param expected The expected value
 * @param actual The actual value
 * @param status The validation status
 */
private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
        String status) {
    Map<String, String> details = new HashMap<>();
    details.put(RULE_NO, ruleNo); // Rule number
    details.put(DESCRIPTION, ruleDesc); // Rule description
    details.put(PROVIDED, actual); // Actual value
    details.put(STATUS, status); // Validation status
    scrutinyDetail.getDetail().add(details); // Add details to scrutiny detail
    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail); // Add scrutiny detail to the plan's report output
}

@Override
public Map<String, Date> getAmendments() {
    return new LinkedHashMap<>(); // Return an empty map for amendments
}
}
