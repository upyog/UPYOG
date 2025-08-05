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
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonFeatureConstants.AT_FLOOR;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class Ventilation extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Ventilation.class);

	 @Autowired
	 MDMSCacheManager cache;

	/**
	 * Validates the building plan for ventilation requirements.
	 * Currently performs no validation and returns the plan as-is.
	 *
	 * @param pl The building plan to validate
	 * @return The unmodified plan
	 */
	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	/**
	 * Processes ventilation requirements for all blocks and floors in the building plan.
	 * Extracts ventilation rules from MDMS, creates scrutiny details, and validates
	 * general ventilation requirements for each floor.
	 *
	 * @param pl The building plan to process
	 * @return The processed plan with scrutiny details added
	 */
	@Override
	public Plan process(Plan pl) {
	    for (Block b : pl.getBlocks()) {
	        BigDecimal[] ventilationValues = extractVentilationRules(pl);
	        ScrutinyDetail generalScrutiny = createScrutinyDetail(COMMON_VENTILATION);
	        ScrutinyDetail bathScrutiny = createScrutinyDetail(BATH_VENTILATION);

	        if (b.getBuilding() != null && b.getBuilding().getFloors() != null) {
	            for (Floor f : b.getBuilding().getFloors()) {
	                processGeneralVentilation(f, ventilationValues[0], generalScrutiny, pl);
	                // processBathroomVentilation(f, ventilationValues[1], bathScrutiny, pl); // Uncomment if needed
	            }
	        }
	    }
	    return pl;
	}

	/**
	 * Extracts ventilation requirement values from MDMS cache.
	 * Fetches ventilation ratio values used for calculating minimum required ventilation areas.
	 *
	 * @param pl The building plan containing configuration details
	 * @return Array containing ventilationValueOne and ventilationValueTwo
	 */
	private BigDecimal[] extractVentilationRules(Plan pl) {
	    BigDecimal ventilationValueOne = BigDecimal.ZERO;
	    BigDecimal ventilationValueTwo = BigDecimal.ZERO;
        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.VENTILATION.getValue(), false);
        Optional<VentilationRequirement> matchedRule = rules.stream()
            .filter(VentilationRequirement.class::isInstance)
            .map(VentilationRequirement.class::cast)
            .findFirst();

	    if (matchedRule.isPresent()) {
	    	VentilationRequirement rule = matchedRule.get();
	        ventilationValueOne = rule.getVentilationValueOne();
	        ventilationValueTwo = rule.getVentilationValueTwo();
	    }

	    return new BigDecimal[]{ventilationValueOne, ventilationValueTwo};
	}

	/**
	 * Creates and initializes a scrutiny detail object for ventilation reporting.
	 * Sets up column headings and key for the specified ventilation type.
	 *
	 * @param key The key identifier for the scrutiny detail (e.g., COMMON_VENTILATION)
	 * @return Configured ScrutinyDetail object with appropriate headings and key
	 */
	private ScrutinyDetail createScrutinyDetail(String key) {
	    ScrutinyDetail detail = new ScrutinyDetail();
	    detail.setKey(key);
	    detail.addColumnHeading(1, RULE_NO);
	    detail.addColumnHeading(2, DESCRIPTION);
	    detail.addColumnHeading(3, REQUIRED);
	    detail.addColumnHeading(4, PROVIDED);
	    detail.addColumnHeading(5, STATUS);
	    return detail;
	}

	/**
	 * Processes general light and ventilation requirements for a specific floor.
	 * Calculates total ventilation area and carpet area, compares against required ratios,
	 * and generates scrutiny details for compliance verification.
	 *
	 * @param floor The floor containing ventilation measurements
	 * @param ventilationRatio The required ventilation ratio (e.g., 1/10th of floor area)
	 * @param scrutinyDetail The scrutiny detail object to add results to
	 * @param pl The building plan for adding scrutiny details to report
	 */
	private void processGeneralVentilation(Floor floor, BigDecimal ventilationRatio,
	                                       ScrutinyDetail scrutinyDetail, Plan pl) {
	    if (floor.getLightAndVentilation() != null &&
	        floor.getLightAndVentilation().getMeasurements() != null &&
	        !floor.getLightAndVentilation().getMeasurements().isEmpty()) {

	        BigDecimal totalVentilationArea = floor.getLightAndVentilation().getMeasurements().stream()
	            .map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);

	        BigDecimal totalCarpetArea = floor.getOccupancies().stream()
	            .map(Occupancy::getCarpetArea).reduce(BigDecimal.ZERO, BigDecimal::add);

	        if (totalVentilationArea.compareTo(BigDecimal.ZERO) > 0) {
	            BigDecimal requiredVentilation = totalCarpetArea.divide(ventilationRatio, 2, BigDecimal.ROUND_HALF_UP);
				ReportScrutinyDetail detail = new ReportScrutinyDetail();
				detail.setRuleNo(RULE_43);
				detail.setDescription(LIGHT_VENTILATION_DESCRIPTION);
				detail.setRequired(MINIMUM_PREFIX_1 + ventilationRatio + TH_OF_FLOOR_AREA);
				detail.setProvided(VENTILATION_AREA + totalVentilationArea + OF_CARPET_AREA + totalCarpetArea + AT_FLOOR + floor.getNumber());
				detail.setStatus(totalVentilationArea.compareTo(requiredVentilation) >= 0 ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

				Map<String, String> details = mapReportDetails(detail);
				addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	        }
	    }
	}

	/**
	 * Processes bathroom ventilation requirements for a specific floor.
	 * Validates bathroom ventilation area against minimum requirements and generates
	 * scrutiny details with compliance status.
	 *
	 * @param floor The floor containing bathroom ventilation measurements
	 * @param requiredArea The minimum required bathroom ventilation area
	 * @param scrutinyDetail The scrutiny detail object to add results to
	 * @param pl The building plan for adding scrutiny details to report
	 */
	private void processBathroomVentilation(Floor floor, BigDecimal requiredArea,
		            ScrutinyDetail scrutinyDetail, Plan pl) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(RULE_43);
		detail.setDescription(LIGHT_VENTILATION_DESCRIPTION);
		if (floor.getBathVentilaion() != null &&
		floor.getBathVentilaion().getMeasurements() != null &&
		!floor.getBathVentilaion().getMeasurements().isEmpty()) {
			BigDecimal totalVentilationArea = floor.getBathVentilaion().getMeasurements().stream()
			.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
			detail.setRequired(requiredArea.toString());
			detail.setProvided(BATH_VENTILATION_AREA + totalVentilationArea + AT_FLOOR + floor.getNumber());
			detail.setStatus(totalVentilationArea.compareTo(requiredArea) >= 0 ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
		} else {
			detail.setRequired(requiredArea.toString());
			detail.setProvided(BATH_VENTILATION_NOT_DEFINED + floor.getNumber());
			detail.setStatus(Result.Not_Accepted.getResultVal());
		}

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
		}

	/**
	 * Returns amendment dates for ventilation rules.
	 * Currently returns an empty map as no amendments are defined.
	 *
	 * @return Empty LinkedHashMap of amendment dates
	 */
	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
