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
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SepticTank_Citya extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(SepticTank_Citya.class);

	// Constants for rule number and field descriptions
	private static final String RULE_45_E = "45-e";
	public static final String DISTANCE_FROM_WATERSOURCE = "Distance from watersource";
	public static final String DISTANCE_FROM_BUILDING = "Distance from Building";
	public static final String MIN_DISTANCE_FROM_GOVTBUILDING_DESC = "Minimum distance fcrom government building";

	// Default minimum distances (fallback values)
	public static final BigDecimal MIN_DIS_WATERSRC = BigDecimal.valueOf(18);
	public static final BigDecimal MIN_DIS_BUILDING = BigDecimal.valueOf(6);

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms; // Service to fetch rules from MDMS

	@Override
	public Plan validate(Plan pl) {
		// No validation rules implemented here for now
		return pl;
	}

	@Override
	public Plan process(Plan pl) {

		// Setting up scrutiny detail metadata
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Septic Tank ");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, PERMITTED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);

		// Fetch septic tank details from the plan
		List<org.egov.common.entity.edcr.SepticTank> septicTanks = pl.getSepticTanks();

    	BigDecimal septicTankMinDisWatersrc = BigDecimal.ZERO;
    	BigDecimal septicTankMinDisBuilding = BigDecimal.ZERO;

    	String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);

		// Define the feature for MDMS lookup
		String feature = MdmsFeatureConstants.SEPTIC_TANK;

		// Prepare parameters to fetch rules based on occupancy
		Map<String, Object> params = new HashMap<>();
		
		params.put("feature", feature);
		params.put("occupancy", occupancyName);

		// Fetch available rules data from the plan object (populated from MDMS)
		Map<String,List<Map<String,Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

		// Define rule keys to fetch values for
		ArrayList<String> valueFromColumn = new ArrayList<>();
		valueFromColumn.add(EdcrRulesMdmsConstants.SEPTIC_TANK_MIN_DISTANCE_WATERSRC);
		valueFromColumn.add(EdcrRulesMdmsConstants.SEPTIC_TANK_MIN_DISTANCE_BUILDING);

		List<Map<String, Object>> permissibleValue = new ArrayList<>();

		// Fetch permissible values from MDMS
		permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
		LOG.info("permissibleValue" + permissibleValue);

		// If rules exist, override the default minimum distances
		if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.SEPTIC_TANK_MIN_DISTANCE_WATERSRC)) {
			septicTankMinDisWatersrc = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SEPTIC_TANK_MIN_DISTANCE_WATERSRC).toString()));
			septicTankMinDisBuilding = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SEPTIC_TANK_MIN_DISTANCE_BUILDING).toString()));
		}

		// Validate each septic tank’s distance from water source and building
		for (org.egov.common.entity.edcr.SepticTank septicTank : septicTanks) {
			boolean validWaterSrcDistance = false;
			boolean validBuildingDistance = false;

			// Validate distance from water source
			if (!septicTank.getDistanceFromWaterSource().isEmpty()) {
				BigDecimal minDistWaterSrc = septicTank.getDistanceFromWaterSource().stream().reduce(BigDecimal::min).get();
				if (minDistWaterSrc != null && minDistWaterSrc.compareTo(septicTankMinDisWatersrc) >= 0) {
					validWaterSrcDistance = true;
				}
				buildResult(pl, scrutinyDetail, validWaterSrcDistance, DISTANCE_FROM_WATERSOURCE, ">= " + septicTankMinDisWatersrc.toString(), minDistWaterSrc.toString());
			}

			// Validate distance from building
			if (!septicTank.getDistanceFromBuilding().isEmpty()) {
				BigDecimal minDistBuilding = septicTank.getDistanceFromBuilding().stream().reduce(BigDecimal::min).get();
				if (minDistBuilding != null && minDistBuilding.compareTo(septicTankMinDisBuilding) >= 0) {
					validBuildingDistance = true;
				}
				buildResult(pl, scrutinyDetail, validBuildingDistance, DISTANCE_FROM_BUILDING, ">= " + septicTankMinDisBuilding.toString(), minDistBuilding.toString());
			}
		}

		return pl;
	}

	// Helper method to build scrutiny detail report row
	private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, boolean valid, String description, String permited,
			String provided) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, RULE_45_E);
		details.put(DESCRIPTION, description);
		details.put(PERMITTED, permited);
		details.put(PROVIDED, provided);
		details.put(STATUS, valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

		// Add details to the scrutiny detail and attach to the plan report
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	@Override
	public Map<String, Date> getAmendments() {
		// No amendments applicable for now
		return new LinkedHashMap<>();
	}
}

