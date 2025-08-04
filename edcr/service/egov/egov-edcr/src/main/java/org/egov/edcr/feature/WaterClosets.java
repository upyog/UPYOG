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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class WaterClosets extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(WaterClosets.class);

	/**
	 * Validates the building plan for water closet requirements.
	 * Currently performs no validation and returns the plan as-is.
	 *
	 * @param pl The building plan to validate
	 * @return The unmodified plan
	 */
	@Override
	public Plan validate(Plan pl) {
		return pl;
	}
	
	@Autowired
	MDMSCacheManager cache;
	/**
	 * Processes water closet requirements for all blocks and floors in the building plan.
	 * Creates scrutiny details for dimensions and ventilation, fetches water closet rules
	 * from MDMS, and validates height, width, area, and ventilation requirements.
	 *
	 * @param pl The building plan to process
	 * @return The processed plan with scrutiny details added
	 */
	@Override
	public Plan process(Plan pl) {
	    ScrutinyDetail dimScrutinyDetail = createScrutinyDetail(COMMON_WATER_CLOSETS);
	    ScrutinyDetail ventScrutinyDetail = createScrutinyDetail(WATER_CLOSETS_VENTILATION);

	    Optional<WaterClosetsRequirement> matchedRule = getWaterClosetsRule(pl);
	    
	    WaterClosetsRequirement wcRule = matchedRule.get();

	    if (wcRule == null) return pl;

	    for (Block block : pl.getBlocks()) {
	        if (block.getBuilding() == null || block.getBuilding().getFloors() == null) continue;

	        for (Floor floor : block.getBuilding().getFloors()) {
	            if (!hasValidWaterClosets(floor)) continue;

	            BigDecimal minHeight = getMinHeight(floor.getWaterClosets().getHeights());
	            BigDecimal minWidth = getMinWidth(floor.getWaterClosets().getRooms());
	            BigDecimal totalArea = getTotalArea(floor.getWaterClosets().getRooms());

	            // Ventilation validation
	            if (floor.getWaterClosetVentilation() != null
	                    && floor.getWaterClosetVentilation().getMeasurements() != null
	                    && !floor.getWaterClosetVentilation().getMeasurements().isEmpty()) {

	                BigDecimal ventArea = floor.getWaterClosetVentilation().getMeasurements().stream()
	                        .map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
	                Map<String, String> ventDetails = buildVentilationDetails(
	                	    wcRule.getWaterClosetsVentilationArea(), ventArea
	                	);
	                	ventScrutinyDetail.getDetail().add(ventDetails);

	            }

	            Map<String, String> dimDetails = buildDimensionDetails(
	            	    wcRule.getWaterClosetsHeight(), wcRule.getWaterClosetsArea(), wcRule.getWaterClosetsWidth(),
	            	    minHeight, totalArea, minWidth
	            	);
	            	dimScrutinyDetail.getDetail().add(dimDetails);

	        }
	    }

	    pl.getReportOutput().getScrutinyDetails().add(dimScrutinyDetail);
	    pl.getReportOutput().getScrutinyDetails().add(ventScrutinyDetail);
	    return pl;
	}
	/**
	 * Creates and initializes a scrutiny detail object for water closet validation reporting.
	 * Sets up column headings and key for the specified water closet scrutiny type.
	 *
	 * @param key The key identifier for the scrutiny detail (e.g., "Common_Water Closets")
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
	 * Retrieves water closet requirement rules from MDMS cache.
	 * Fetches the first matching water closet requirement rule based on plan configuration.
	 *
	 * @param pl The building plan containing configuration details
	 * @return Optional containing WaterClosetsRequirement rule if found, empty otherwise
	 */
	private Optional<WaterClosetsRequirement> getWaterClosetsRule(Plan pl) {
		 List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.WATER_CLOSETS.getValue(), false);
	         return rules.stream()
	            .filter(WaterClosetsRequirement.class::isInstance)
	            .map(WaterClosetsRequirement.class::cast)
	            .findFirst();
	}
	/**
	 * Checks if a floor has valid water closet data for processing.
	 * Validates that water closets object exists with non-empty heights and rooms collections.
	 *
	 * @param floor The floor to check for valid water closet data
	 * @return true if floor has valid water closet data, false otherwise
	 */
	private boolean hasValidWaterClosets(Floor floor) {
	    return floor.getWaterClosets() != null
	            && floor.getWaterClosets().getHeights() != null
	            && !floor.getWaterClosets().getHeights().isEmpty()
	            && floor.getWaterClosets().getRooms() != null
	            && !floor.getWaterClosets().getRooms().isEmpty();
	}
	/**
	 * Finds the minimum height among all water closet room heights.
	 * Returns the smallest height value from the list of room heights.
	 *
	 * @param heights List of room height measurements
	 * @return Minimum height value, or BigDecimal.ZERO if list is empty
	 */
	private BigDecimal getMinHeight(List<RoomHeight> heights) {
	    return heights.stream()
	            .map(RoomHeight::getHeight)
	            .min(Comparator.naturalOrder())
	            .orElse(BigDecimal.ZERO);
	}
	/**
	 * Finds the minimum width among all water closet room measurements.
	 * Returns the smallest width value from the list of room measurements.
	 *
	 * @param rooms List of room measurements
	 * @return Minimum width value, or BigDecimal.ZERO if list is empty
	 */
	private BigDecimal getMinWidth(List<Measurement> rooms) {
	    return rooms.stream()
	            .map(Measurement::getWidth)
	            .min(Comparator.naturalOrder())
	            .orElse(BigDecimal.ZERO);
	}
	/**
	 * Calculates the total area of all water closet rooms.
	 * Sums up the areas of all room measurements.
	 *
	 * @param rooms List of room measurements
	 * @return Total area of all rooms
	 */
	private BigDecimal getTotalArea(List<Measurement> rooms) {
	    return rooms.stream()
	            .map(Measurement::getArea)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	/**
	 * Builds ventilation validation details for scrutiny reporting.
	 * Creates a map with rule information, requirements, and compliance status
	 * for water closet ventilation area validation.
	 *
	 * @param requiredVentArea The minimum required ventilation area
	 * @param providedVentArea The actual provided ventilation area
	 * @return Map containing ventilation validation details
	 */
	private Map<String, String> buildVentilationDetails(BigDecimal requiredVentArea, BigDecimal providedVentArea) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(RULE_41_IV);
		detail.setDescription(WATERCLOSETS_DESCRIPTION);
		detail.setRequired(requiredVentArea.toString());
		detail.setProvided(WATER_CLOSET_VENTILATION_AREA + providedVentArea);
		detail.setStatus(providedVentArea.compareTo(requiredVentArea) >= 0
				? Result.Accepted.getResultVal()
				: Result.Not_Accepted.getResultVal());
		return mapReportDetails(detail);
	}
	/**
	 * Builds dimension validation details for scrutiny reporting.
	 * Creates a map with rule information, requirements, and compliance status
	 * for water closet height, area, and width validation.
	 *
	 * @param requiredHeight The minimum required height
	 * @param requiredArea The minimum required area
	 * @param requiredWidth The minimum required width
	 * @param providedHeight The actual provided height
	 * @param providedArea The actual provided area
	 * @param providedWidth The actual provided width
	 * @return Map containing dimension validation details
	 */
	private Map<String, String> buildDimensionDetails(BigDecimal requiredHeight, BigDecimal requiredArea, BigDecimal requiredWidth,
	                                                  BigDecimal providedHeight, BigDecimal providedArea, BigDecimal providedWidth) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(RULE_41_IV);
		detail.setDescription(WATERCLOSETS_DESCRIPTION);
		detail.setRequired(String.format(HEIGHT_AREA_WIDTH_GREATER_THAN_S,
				requiredHeight, requiredArea, requiredWidth));
		detail.setProvided(String.format(HEIGHT_AREA_WIDTH_EQUAL_TO_S, providedHeight, providedArea, providedWidth));
		detail.setStatus((providedHeight.compareTo(requiredHeight) >= 0
				&& providedArea.compareTo(requiredArea) >= 0
				&& providedWidth.compareTo(requiredWidth) >= 0)
				? Result.Accepted.getResultVal()
				: Result.Not_Accepted.getResultVal());
		return mapReportDetails(detail);
	}

	/**
	 * Returns amendment dates for water closet rules.
	 * Currently returns an empty map as no amendments are defined.
	 *
	 * @return Empty LinkedHashMap of amendment dates
	 */
	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
