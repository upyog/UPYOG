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
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.GREATER_THAN_EQUAL;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class SepticTank extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(SepticTank.class);

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms; // Service to fetch rules from MDMS
	
	@Autowired
	MDMSCacheManager cache;

	/**
	 * Validates septic tank requirements for the plan.
	 * Currently no validation rules are implemented.
	 *
	 * @param pl The plan object to validate
	 * @return The unchanged plan object
	 */
	@Override
	public Plan validate(Plan pl) {
		// No validation rules implemented here for now
		return pl;
	}

	/**
	 * Processes septic tank distance requirements and generates scrutiny report.
	 * Validates minimum distances from water sources and buildings based on MDMS rules.
	 *
	 * @param pl The plan object containing septic tank details
	 * @return The processed plan with scrutiny details added
	 */
	@Override
	public Plan process(Plan pl) {
	    ScrutinyDetail scrutinyDetail = createScrutinyDetail();
	    List<org.egov.common.entity.edcr.SepticTank> septicTanks = pl.getSepticTanks();
	    if (septicTanks == null || septicTanks.isEmpty()) {
	        return pl;
	    }

	    BigDecimal septicTankMinDisWatersrc = BigDecimal.ZERO;
	    BigDecimal septicTankMinDisBuilding = BigDecimal.ZERO;

	    Optional<SepticTankRequirement> matchedRule = getMatchedSepticTankRule(pl);
	    if (matchedRule.isPresent()) {
	        septicTankMinDisWatersrc = matchedRule.get().getSepticTankMinDisWatersrc();
	        septicTankMinDisBuilding = matchedRule.get().getSepticTankMinDisBuilding();
	    }
	    validateSepticTanks(pl, scrutinyDetail, septicTanks, septicTankMinDisWatersrc, septicTankMinDisBuilding);
	    return pl;
	}

	/**
	 * Creates a new scrutiny detail object with predefined column headings.
	 * Sets up the report structure for septic tank validation results.
	 *
	 * @return A new ScrutinyDetail object with standard column headers
	 */
	private ScrutinyDetail createScrutinyDetail() {
	    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.setKey(Common_Septic_Tank);
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	    scrutinyDetail.addColumnHeading(3, PERMITTED);
	    scrutinyDetail.addColumnHeading(4, PROVIDED);
	    scrutinyDetail.addColumnHeading(5, STATUS);
	    return scrutinyDetail;
	}

	/**
	 * Retrieves septic tank requirements from MDMS cache based on plan details.
	 * Filters and returns the first matching septic tank rule configuration.
	 *
	 * @param pl The plan object containing building details
	 * @return Optional containing matched SepticTankRequirement or empty if not found
	 */
	private Optional<SepticTankRequirement> getMatchedSepticTankRule(Plan pl) {
		List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.SEPTIC_TANK.getValue(), false);
       return rules.stream()
            .filter(SepticTankRequirement.class::isInstance)
            .map(SepticTankRequirement.class::cast)
            .findFirst();
	
	}

	/**
	 * Validates all septic tanks in the plan against distance requirements.
	 * Checks distances from water sources and buildings for each septic tank.
	 *
	 * @param pl The plan object
	 * @param scrutinyDetail The scrutiny detail for reporting results
	 * @param septicTanks List of septic tanks to validate
	 * @param minDisWaterSrc Minimum required distance from water source
	 * @param minDisBuilding Minimum required distance from building
	 */
	private void validateSepticTanks(Plan pl, ScrutinyDetail scrutinyDetail, List<org.egov.common.entity.edcr.SepticTank> septicTanks,
	                                  BigDecimal minDisWaterSrc, BigDecimal minDisBuilding) {
	    for (org.egov.common.entity.edcr.SepticTank septicTank : septicTanks) {
	        validateDistance(pl, scrutinyDetail, septicTank.getDistanceFromWaterSource(), minDisWaterSrc,
	                DISTANCE_FROM_WATERSOURCE);
	        validateDistance(pl, scrutinyDetail, septicTank.getDistanceFromBuilding(), minDisBuilding,
	                DISTANCE_FROM_BUILDING);
	    }
	}

	/**
	 * Validates distance requirements for a specific measurement type.
	 * Compares minimum provided distance against minimum permitted distance.
	 *
	 * @param pl The plan object
	 * @param scrutinyDetail The scrutiny detail for reporting
	 * @param distances List of distance measurements
	 * @param minPermittedDistance Minimum permitted distance requirement
	 * @param description Description of the distance type being validated
	 */
	private void validateDistance(Plan pl, ScrutinyDetail scrutinyDetail, List<BigDecimal> distances, BigDecimal minPermittedDistance,
	                              String description) {
	    if (distances == null || distances.isEmpty()) return;

	    BigDecimal minProvided = distances.stream().reduce(BigDecimal::min).orElse(null);
	    if (minProvided == null) return;

	    boolean isValid = minProvided.compareTo(minPermittedDistance) >= 0;
	    buildResult(pl, scrutinyDetail, isValid, description,
	            GREATER_THAN_EQUAL + minPermittedDistance.toString(), minProvided.toString());
	}

	/**
	 * Builds and adds validation result to scrutiny detail.
	 * Creates a formatted report entry with rule details and validation status.
	 *
	 * @param pl The plan object
	 * @param scrutinyDetail The scrutiny detail to add results to
	 * @param valid Boolean indicating if validation passed
	 * @param description Description of what was validated
	 * @param permited The permitted/required value
	 * @param provided The actual provided value
	 */
	private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, boolean valid, String description, String permited,
	                         String provided) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(RULE_45_E);
		detail.setDescription(description);
		detail.setPermitted(permited);
		detail.setProvided(provided);
		detail.setStatus(valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	}


	@Override
	public Map<String, Date> getAmendments() {
		// No amendments applicable for now
		return new LinkedHashMap<>();
	}
}

