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
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Verandah extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Verandah.class);
	private static final String RULE_43A = "43A";
	private static final String RULE_43 = "43";

	public static final String VERANDAH_DESCRIPTION = "Verandah";

	@Override
	public Plan validate(Plan pl) {
		// Currently no validation logic required for Verandah feature
		return pl;
	}
	
	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;

	@Override
	public Plan process(Plan pl) {
		for (Block b : pl.getBlocks()) {
			// Create a scrutiny detail to hold verandah rule checks
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Verandah");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, REQUIRED);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);
			
			BigDecimal verandahWidth = BigDecimal.ZERO;
			BigDecimal verandahDepth = BigDecimal.ZERO;
			
			String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
			String feature = MdmsFeatureConstants.VERANDAH;

			// Check occupancy type; only residential is supported for now
			Map<String, Object> params = new HashMap<>();
			

			params.put("feature", feature);
			params.put("occupancy", occupancyName);
			
			// Fetch permissible verandah dimension rules from MDMS
			Map<String,List<Map<String,Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
			
			ArrayList<String> valueFromColumn = new ArrayList<>();
			valueFromColumn.add(EdcrRulesMdmsConstants.VERANDAH_DEPTH);
			valueFromColumn.add(EdcrRulesMdmsConstants.VERANDAH_WIDTH);

			List<Map<String, Object>> permissibleValue = new ArrayList<>();
			permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
			LOG.info("permissibleValue" + permissibleValue);

			// Parse permissible width and depth if values are found
			if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.VERANDAH_WIDTH)) {
				verandahWidth = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.VERANDAH_WIDTH).toString()));
				verandahDepth = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.VERANDAH_DEPTH).toString()));
			}

			// Loop through each floor and perform width and depth checks
			if (b.getBuilding() != null && b.getBuilding().getFloors() != null
					&& !b.getBuilding().getFloors().isEmpty()) {

				for (Floor f : b.getBuilding().getFloors()) {

					// Check if verandah data exists
					if (f.getVerandah() != null && f.getVerandah().getMeasurements() != null
							&& !f.getVerandah().getMeasurements().isEmpty()) {

						// Find minimum verandah width
						BigDecimal minVerandaWidth = f.getVerandah().getMeasurements().stream()
								.map(Measurement::getWidth).reduce(BigDecimal::min).get();

						// Find minimum verandah depth (heightOrDepth list)
						BigDecimal minVerandDepth = f.getVerandah().getHeightOrDepth().stream().reduce(BigDecimal::min)
								.get();

						// Width check against permissible value
						if (minVerandaWidth.compareTo(BigDecimal.ZERO) > 0) {
							Map<String, String> details = new HashMap<>();
							details.put(RULE_NO, RULE_43);
							details.put(DESCRIPTION, VERANDAH_DESCRIPTION);

							if (minVerandaWidth.compareTo(verandahWidth) >= 0) {
								details.put(REQUIRED, "Minimum width " + verandahWidth.toString() + "m   ");
								details.put(PROVIDED, "Width area " + minVerandaWidth + " at floor " + f.getNumber());
								details.put(STATUS, Result.Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							} else {
								details.put(REQUIRED, "Minimum width " + verandahWidth.toString() + "m   ");
								details.put(PROVIDED, "Width area " + minVerandaWidth + " at floor " + f.getNumber());
								details.put(STATUS, Result.Not_Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							}
						}

						// Depth check against permissible value
						if (minVerandDepth.compareTo(BigDecimal.ZERO) > 0) {
							Map<String, String> details = new HashMap<>();
							details.put(RULE_NO, RULE_43A);
							details.put(DESCRIPTION, VERANDAH_DESCRIPTION);

							if (minVerandDepth.compareTo(verandahDepth) <= 0) {
								details.put(REQUIRED, "Minimum depth not more than " + verandahDepth.toString() + " m ");
								details.put(PROVIDED, " Depth area  " + minVerandDepth + " at floor " + f.getNumber());
								details.put(STATUS, Result.Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							} else {
								details.put(REQUIRED, "Minimum depth not more than " + verandahDepth.toString() + " m ");
								details.put(PROVIDED, " Depth area  " + minVerandDepth + " at floor " + f.getNumber());
								details.put(STATUS, Result.Not_Accepted.getResultVal());
								scrutinyDetail.getDetail().add(details);
								pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							}
						}
					}
				}
			}
		}
		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
		// No amendments for this feature as of now
		return new LinkedHashMap<>();
	}


}
