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
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Portico;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PorticoService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(PorticoService.class);
    private static final String SUBRULE_PORTICO = "PORTICO";
    private static final String SUBRULE_PORTICO_MAX_LENGTHDESCRIPTION = "Maximum Portico length for portico %s ";
    public static final String PORTICO_DISTANCETO_EXTERIORWALL = "Block %s Portico %s Portico distance to exteriorwall";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan validate(Plan plan) {
        HashMap<String, String> errors = new HashMap<>();

        for (Block block : plan.getBlocks()) {
            for (Portico portico : block.getPorticos()) {
                if (portico.getDistanceToExteriorWall().isEmpty()) {
                    errors.put(String.format(PORTICO_DISTANCETO_EXTERIORWALL, block.getNumber(), portico.getName()),
                            edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                    new String[] { String.format(PORTICO_DISTANCETO_EXTERIORWALL, block.getNumber(), portico.getName()) },
                                    LocaleContextHolder.getLocale()));
                    plan.addErrors(errors);
                }
            }
        }
        return plan;
    }

    @Override
    public Plan process(Plan plan) {
        // Initialize permissible value for portico service
        BigDecimal porticoServicePermissibleValue = BigDecimal.ZERO;

        // Validate the plan for portico compliance
        validate(plan);

        // Determine the occupancy type
        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(plan);
        String feature = MdmsFeatureConstants.PORTICO_SERVICE; // Feature name for portico service

        // Prepare parameters for fetching MDMS values
        Map<String, Object> params = new HashMap<>();
        
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch the list of rules from the plan object
        Map<String, List<Map<String, Object>>> edcrRuleList = plan.getEdcrRulesFeatures();

        // Specify the columns to fetch from the rules
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE); // Permissible value for portico length

        // Initialize a list to store permissible values
        List<Map<String, Object>> permissibleValue = new ArrayList<>();

        // Fetch permissible values from MDMS
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue); // Log the fetched permissible values

        // Check if permissible values are available and update the permissible value for portico service
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
            porticoServicePermissibleValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
        }

        // Iterate through all blocks in the plan
        for (Block block : plan.getBlocks()) {
            // Iterate through all porticos in the block
            for (Portico portico : block.getPorticos()) {
                // Initialize scrutiny details for the portico
                scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.addColumnHeading(1, RULE_NO); // Column for rule number
                scrutinyDetail.addColumnHeading(2, DESCRIPTION); // Column for description
                scrutinyDetail.addColumnHeading(3, REQUIRED); // Column for required values
                scrutinyDetail.addColumnHeading(4, PROVIDED); // Column for provided values
                scrutinyDetail.addColumnHeading(5, STATUS); // Column for status (Accepted/Not Accepted)
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Portico"); // Key for the scrutiny detail

                // Check if the portico length is provided
                if (portico.getLength() != null) {
                    // Compare the portico length with the permissible value
                    if (portico.getLength().compareTo(porticoServicePermissibleValue) >= 0) {
                        // If the portico length is within permissible limits, mark as Accepted
                        setReportOutputDetails(plan, SUBRULE_PORTICO,
                                String.format(SUBRULE_PORTICO_MAX_LENGTHDESCRIPTION, portico.getName()),
                                "Max " + porticoServicePermissibleValue.toString() + " Mtr.",
                                portico.getLength() + " Mtr.", Result.Accepted.getResultVal(), scrutinyDetail);
                    } else {
                        // If the portico length is not within permissible limits, mark as Not Accepted
                        setReportOutputDetails(plan, SUBRULE_PORTICO,
                                String.format(SUBRULE_PORTICO_MAX_LENGTHDESCRIPTION, portico.getName()),
                                "Max " + porticoServicePermissibleValue.toString() + " Mtr.",
                                portico.getLength() + " Mtr.", Result.Not_Accepted.getResultVal(), scrutinyDetail);
                    }
                }
            }
        }

        return plan; // Return the updated plan object
    }

    /**
     * Adds the result of the validation to the scrutiny report.
     *
     * @param pl The plan object
     * @param ruleNo The rule number
     * @param ruleDesc The rule description
     * @param expected The expected value
     * @param actual The actual value
     * @param status The validation status (Accepted/Not Accepted)
     * @param scrutinyDetail The scrutiny detail object
     */
    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual, String status,
            ScrutinyDetail scrutinyDetail) {
        // Initialize a map to store rule details
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo); // Rule number
        details.put(DESCRIPTION, ruleDesc); // Rule description
        details.put(REQUIRED, expected); // Expected value
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
