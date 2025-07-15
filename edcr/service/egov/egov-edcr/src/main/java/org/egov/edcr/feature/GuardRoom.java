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
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuardRoom extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOGGER = LogManager.getLogger(GuardRoom.class);

    // Rule identifier and descriptions for guard room scrutiny
    private static final String RULE_48_A = "48-A";
    public static final String GUARD_ROOM_DIMENSION_DESCRIPTION = "Guard Room Dimension";
    public static final String GUARD_ROOM_AREA_DESCRIPTION = "Guard Room Area";
    public static final String GUARD_ROOM_HEIGHT_DESCRIPTION = "Guard Room Height";
    public static final String HEIGHT = "Height";
    public static final String AREA = "Area";
    public static final String DIMENSION = "Dimension";

   
    @Autowired
	CacheManagerMdms cache;

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan pl) {
        return null;
    }

	/**
	 * Processes the given plan to validate guard room dimensions, area, and height.
	 * Fetches permissible values for guard room properties and validates them
	 * against the plan details.
	 *
	 * @param pl The plan object to process.
	 * @return The processed plan object with scrutiny details added.
	 */

    
    @Override
    public Plan process(Plan pl) {
    	validate(pl);
    	initializeScrutinyDetail();

    	Map<String, String> details = new HashMap<>();
    	HashMap<String, String> errors = new HashMap<>();

    	BigDecimal minHeight = BigDecimal.ZERO;
    	BigDecimal minWidth = BigDecimal.ZERO;
    	BigDecimal minArea = BigDecimal.ZERO;
    	BigDecimal minCabinHeight = BigDecimal.ZERO;

    	BigDecimal GuardRoomMinHeight = BigDecimal.ZERO;
    	BigDecimal GuardRoomMinWidth = BigDecimal.ZERO;
    	BigDecimal GuardRoomMinArea = BigDecimal.ZERO;
    	BigDecimal GuardRoomMinCabinHeightOne = BigDecimal.ZERO;
    	BigDecimal GuardRoomMinCabinHeightTwo = BigDecimal.ZERO;

    	List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.GUARD_ROOM, false);
    	Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

    	if (matchedRule.isPresent()) {
    		MdmsFeatureRule rule = matchedRule.get();
    		GuardRoomMinHeight = rule.getGuardRoomMinHeight();
    		GuardRoomMinWidth = rule.getGuardRoomMinWidth();
    		GuardRoomMinArea = rule.getGuardRoomMinArea();
    		GuardRoomMinCabinHeightOne = rule.getGuardRoomMinCabinHeightOne();
    		GuardRoomMinCabinHeightTwo = rule.getGuardRoomMinCabinHeightTwo();
    	}

    	if (pl.getGuardRoom() != null && !pl.getGuardRoom().getGuardRooms().isEmpty()) {
    		List<BigDecimal> heightList = pl.getGuardRoom().getGuardRooms().stream().map(Measurement::getHeight).collect(Collectors.toList());
    		List<BigDecimal> widthList = pl.getGuardRoom().getGuardRooms().stream().map(Measurement::getWidth).collect(Collectors.toList());
    		List<BigDecimal> areaList = pl.getGuardRoom().getGuardRooms().stream().map(Measurement::getArea).collect(Collectors.toList());
    		List<BigDecimal> cabinHeightList = pl.getGuardRoom().getCabinHeights();

    		if (cabinHeightList != null && !cabinHeightList.isEmpty()) {
    			minHeight = heightList.stream().reduce(BigDecimal::min).get();
    			minWidth = widthList.stream().reduce(BigDecimal::min).get();
    			minArea = areaList.stream().reduce(BigDecimal::min).get();
    			minCabinHeight = cabinHeightList.stream().reduce(BigDecimal::min).get();

    			validateDimensions(minHeight, minWidth, GuardRoomMinHeight, GuardRoomMinWidth);
    			validateArea(minArea, GuardRoomMinArea);
    			validateCabinHeight(minCabinHeight, GuardRoomMinCabinHeightOne, GuardRoomMinCabinHeightTwo);

    			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    		} else {
    			errors.put("Distance_Guard Room", "Cabin heights are not provided in layer GUARD_ROOM");
    			pl.addErrors(errors);
    		}
    	}

    	return pl;
    }

    /**
     * Initializes the {@link ScrutinyDetail} object with predefined key and column headings
     * specific to guard room scrutiny.
     * This method sets up the structure used to store and report the results of rule validations.
     */
    private void initializeScrutinyDetail() {
    	scrutinyDetail = new ScrutinyDetail();
    	scrutinyDetail.setKey(COMMON_Guard_Room);
    	scrutinyDetail.addColumnHeading(1, RULE_NO);
    	scrutinyDetail.addColumnHeading(2, DESCRIPTION);
    	scrutinyDetail.addColumnHeading(3, REQUIRED);
    	scrutinyDetail.addColumnHeading(4, PROVIDED);
    	scrutinyDetail.addColumnHeading(5, STATUS);
    }

    /**
     * Validates the dimensions (height and width) of a guard room against the required minimum dimensions.
     *
     * @param minHeight       The minimum height provided in the plan.
     * @param minWidth        The minimum width provided in the plan.
     * @param requiredHeight  The required minimum height as per rule.
     * @param requiredWidth   The required minimum width as per rule.
     */
    private void validateDimensions(BigDecimal minHeight, BigDecimal minWidth, BigDecimal requiredHeight, BigDecimal requiredWidth) {
    	Map<String, String> details = new HashMap<>();
    	details.put(RULE_NO, RULE_48_A);
    	details.put(DESCRIPTION, GUARD_ROOM_DIMENSION_DESCRIPTION);
    	details.put(REQUIRED, DIMENSION + " > " + requiredHeight.toString() + "x" + requiredWidth.toString());
    	details.put(PROVIDED, DIMENSION + ": " + minWidth + "x" + minHeight);

    	if (minHeight.compareTo(requiredHeight) >= 0 && minWidth.compareTo(requiredWidth) >= 0) {
    		details.put(STATUS, Result.Accepted.getResultVal());
    	} else {
    		details.put(STATUS, Result.Not_Accepted.getResultVal());
    	}

    	scrutinyDetail.getDetail().add(details);
    }

    /**
     * Validates the area of the guard room against the maximum permissible area.
     *
     * @param minArea        The area provided in the plan.
     * @param requiredArea   The maximum allowed area as per the rule.
     */
    private void validateArea(BigDecimal minArea, BigDecimal requiredArea) {
    	Map<String, String> details = new HashMap<>();
    	details.put(RULE_NO, RULE_48_A);
    	details.put(DESCRIPTION, GUARD_ROOM_AREA_DESCRIPTION);
    	details.put(REQUIRED, AREA + " <= " + requiredArea.toString());
    	details.put(PROVIDED, AREA + ": " + minArea);

    	if (minArea.compareTo(requiredArea) <= 0) {
    		details.put(STATUS, Result.Accepted.getResultVal());
    	} else {
    		details.put(STATUS, Result.Not_Accepted.getResultVal());
    	}

    	scrutinyDetail.getDetail().add(details);
    }

    /**
     * Validates the height of the guard cabin against the specified minimum and maximum allowed heights.
     *
     * @param minCabinHeight     The height of the cabin as provided in the plan.
     * @param minHeightAllowed   The minimum height allowed as per rule.
     * @param maxHeightAllowed   The maximum height allowed as per rule.
     */
    private void validateCabinHeight(BigDecimal minCabinHeight, BigDecimal minHeightAllowed, BigDecimal maxHeightAllowed) {
    	Map<String, String> details = new HashMap<>();
    	details.put(RULE_NO, RULE_48_A);
    	details.put(DESCRIPTION, GUARD_ROOM_HEIGHT_DESCRIPTION);
    	details.put(REQUIRED, HEIGHT + " >= " + minHeightAllowed.toString() + " and <= " + maxHeightAllowed.toString());
    	details.put(PROVIDED, HEIGHT + ": " + minCabinHeight + "m");

    	if (minCabinHeight.compareTo(minHeightAllowed) >= 0 && minCabinHeight.compareTo(maxHeightAllowed) <= 0) {
    		details.put(STATUS, Result.Accepted.getResultVal());
    	} else {
    		details.put(STATUS, Result.Not_Accepted.getResultVal());
    	}

    	scrutinyDetail.getDetail().add(details);
    }


    /**
     * Returns an empty map as no amendments are defined for this feature.
     *
     * @return An empty map of amendments.
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
