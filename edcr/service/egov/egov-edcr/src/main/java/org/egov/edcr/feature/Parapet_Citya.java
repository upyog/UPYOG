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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Parapet_Citya extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Parapet_Citya.class);
	private static final String RULE_41_V = "41-v";
	public static final String PARAPET_DESCRIPTION = "Parapet";

	@Override
	public Plan validate(Plan pl) {

		return pl;
	}
	
	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;

@Override
public Plan process(Plan pl) {

    // Initialize scrutiny details for the report
    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
    scrutinyDetail.setKey("Common_Parapet"); // Key for the scrutiny detail
    scrutinyDetail.addColumnHeading(1, RULE_NO); // Column for rule number
    scrutinyDetail.addColumnHeading(2, DESCRIPTION); // Column for description
    scrutinyDetail.addColumnHeading(3, REQUIRED); // Column for required values
    scrutinyDetail.addColumnHeading(4, PROVIDED); // Column for provided values
    scrutinyDetail.addColumnHeading(5, STATUS); // Column for status (Accepted/Not Accepted)

    // Initialize a map to store rule details
    Map<String, String> details = new HashMap<>();
    details.put(RULE_NO, RULE_41_V); // Rule number for parapet
    details.put(DESCRIPTION, PARAPET_DESCRIPTION); // Description of the rule

    // Initialize variables to store minimum height and permissible parapet values
    BigDecimal minHeight = BigDecimal.ZERO;
    BigDecimal parapetValueOne = BigDecimal.ZERO;
    BigDecimal parapetValueTwo = BigDecimal.ZERO;

    // Determine the occupancy type
    String occupancyName = null;
    String feature = MdmsFeatureConstants.PARAPET; // Feature name for parapet

    Map<String, Object> params = new HashMap<>();
    if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
        occupancyName = "Residential"; // Set occupancy type to Residential if condition matches
    }

    // Add feature and occupancy to the parameters map
    params.put("feature", feature);
    params.put("occupancy", occupancyName);

    // Fetch the list of rules from the plan object
    Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

    // Specify the columns to fetch from the rules
    ArrayList<String> valueFromColumn = new ArrayList<>();
    valueFromColumn.add(EdcrRulesMdmsConstants.PARAPET_VALUE_ONE); // First permissible parapet value
    valueFromColumn.add(EdcrRulesMdmsConstants.PARAPET_VALUE_TWO); // Second permissible parapet value

    // Initialize a list to store permissible values
    List<Map<String, Object>> permissibleValue = new ArrayList<>();

    // Fetch permissible values from MDMS
    permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
    LOG.info("permissibleValue" + permissibleValue); // Log the fetched permissible values

    // Check if permissible values are available and update the parapet values
    if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PARAPET_VALUE_ONE)) {
        parapetValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PARAPET_VALUE_ONE).toString()));
        parapetValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PARAPET_VALUE_TWO).toString()));
    }

    // Iterate through all blocks in the plan
    for (Block b : pl.getBlocks()) {
        // Check if the block has parapets
        if (b.getParapets() != null && !b.getParapets().isEmpty()) {
            // Find the minimum height among the parapets
            minHeight = b.getParapets().stream().reduce(BigDecimal::min).get();

            // Validate the minimum height against the permissible values
            if (minHeight.compareTo(parapetValueOne) >= 0 && minHeight.compareTo(parapetValueTwo) <= 0) {
                // If the height is within the permissible range, mark as Accepted
                details.put(REQUIRED, "Height >= " + parapetValueOne.toString() + " and height <= " + parapetValueTwo.toString());
                details.put(PROVIDED, "Height >= " + minHeight + " and height <= " + minHeight);
                details.put(STATUS, Result.Accepted.getResultVal());
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            } else {
                // If the height is outside the permissible range, mark as Not Accepted
                details.put(REQUIRED, "Height >= " + parapetValueOne.toString() + " and height <= " + parapetValueTwo.toString());
                details.put(PROVIDED, "Height >= " + minHeight + " and height <= " + minHeight);
                details.put(STATUS, Result.Not_Accepted.getResultVal());
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }
        }
    }

    return pl; // Return the updated plan object
}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
