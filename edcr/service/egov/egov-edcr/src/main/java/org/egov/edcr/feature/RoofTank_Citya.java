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
public class RoofTank_Citya extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(RoofTank_Citya.class);
	private static final String RULE_44_A = "44-A";
	public static final String ROOFTANK_DESCRIPTION = "Roof Tanks";

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;
	
	@Override
	public Plan validate(Plan pl) {
		return pl; // No validation logic defined for this feature
	}

	@Override
	public Plan process(Plan pl) {

		// Initialize scrutiny detail object and define its headers
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Roof Tanks");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, VERIFIED);
		scrutinyDetail.addColumnHeading(4, ACTION);
		scrutinyDetail.addColumnHeading(5, STATUS);

		// Create a map to hold the result details for this rule
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, RULE_44_A);

		BigDecimal minHeight = BigDecimal.ZERO;
		BigDecimal roofTankValue = BigDecimal.ZERO;
		String occupancyName = null;

		String feature = MdmsFeatureConstants.ROOF_TANK;

		Map<String, Object> params = new HashMap<>();

		// Determine occupancy type based on building type code
		if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
			occupancyName = "Residential";
		}

		// Prepare parameters to fetch permissible value from MDMS
		params.put("feature", feature);
		params.put("occupancy", occupancyName);

		Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

		ArrayList<String> valueFromColumn = new ArrayList<>();
		valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE);

		List<Map<String, Object>> permissibleValue = new ArrayList<>();

		// Fetch permissible value using MDMS service
		permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
		LOG.info("permissibleValue" + permissibleValue);

		// Extract permissible roof tank height from result
		if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
			roofTankValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
		}

		// Loop through each block to evaluate roof tank height
		for (Block b : pl.getBlocks()) {
			minHeight = BigDecimal.ZERO;

			// If block has roof tanks defined, find the minimum height among them
			if (b.getRoofTanks() != null && !b.getRoofTanks().isEmpty()) {
				minHeight = b.getRoofTanks().stream().reduce(BigDecimal::min).get();

				// Check if the minimum roof tank height is within permissible limit
				if (minHeight.compareTo(roofTankValue) <= 0) {
					details.put(DESCRIPTION, ROOFTANK_DESCRIPTION);
					details.put(VERIFIED, "Verified whether roof tank height is <= " + roofTankValue.toString() + " meters");
					details.put(ACTION, "Not included roof tank height(" + minHeight + ") to building height");
					details.put(STATUS, Result.Accepted.getResultVal());
				} else {
					// If tank height exceeds permissible value, mark for verification
					details.put(DESCRIPTION, ROOFTANK_DESCRIPTION);
					details.put(VERIFIED, "Verified whether roof tank height is <= " + roofTankValue.toString() + " meters");
					details.put(ACTION, "Included roof tank height(" + minHeight + ") to building height");
					details.put(STATUS, Result.Verify.getResultVal());
				}

				// Add result to scrutiny detail and report
				scrutinyDetail.getDetail().add(details);
				pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
			}
		}
		return pl; // Return processed plan
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>(); // No amendments defined
	}
}
