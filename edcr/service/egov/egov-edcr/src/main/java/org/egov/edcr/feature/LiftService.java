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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class LiftService extends FeatureProcess {

	@Autowired
	MDMSCacheManager cache;

	/**
	 * Validates the lift dimensions and counts for each block and floor in the plan.
	 * @param plan the Plan object to validate
	 * @return the validated Plan object
	 */
		@Override
		public Plan validate(Plan plan) {
		    for (Block block : plan.getBlocks()) {
		        if (!hasBuildingWithFloors(block)) continue;
	
		        for (Floor floor : block.getBuilding().getFloors()) {
		            for (Lift lift : getLifts(floor)) {
		                validateDimensions(plan, block.getNumber(), floor.getNumber(), lift.getNumber().toString(), lift.getLifts());
		            }
		        }
		    }
		    return plan;
		}
	
		/**
		 * Processes lift-related scrutiny details like count and dimensions per block.
		 * @param plan the Plan object to process
		 * @return the updated Plan object with scrutiny details
		 */
		@Override
		public Plan process(Plan plan) {
		    if (plan == null || plan.getBlocks().isEmpty()) return plan;
	
		    for (Block block : plan.getBlocks()) {
		        if (!isLiftValidationRequired(block)) continue;
	
		        ScrutinyDetail liftCountDetail = createScrutinyDetail(BLOCK + block.getNumber() + LIFT_MINIMUM_REQUIRED);
		        ScrutinyDetail liftDimDetail = createScrutinyDetail(BLOCK + block.getNumber() + LIFT_DIMENSION);
	
		        validateLiftCount(plan, block, liftCountDetail);
		        validateLiftDimensions(plan, block, liftDimDetail);
		    }
	
		    return plan;
		}
	
		
	
		/**
		 * Checks if the given block has a building with floors.
		 * @param block the block to check
		 * @return true if the building has at least one floor; false otherwise
		 */
		private boolean hasBuildingWithFloors(Block block) {
		    return block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty();
		}
	
		/**
		 * Retrieves the list of lifts from a floor.
		 * @param floor the floor object
		 * @return list of Lift objects or empty list if null
		 */
		private List<Lift> getLifts(Floor floor) {
		    return floor.getLifts() != null ? floor.getLifts() : Collections.emptyList();
		}
	
		/**
		 * Checks whether lift validation is needed for a block.
		 * @param block the block to evaluate
		 * @return true if at least one floor has lifts; false otherwise
		 */	
		private boolean isLiftValidationRequired(Block block) {
		    return block.getBuilding() != null &&
		           !block.getBuilding().getOccupancies().isEmpty() &&
		           block.getBuilding().getFloors().stream().anyMatch(floor -> !getLifts(floor).isEmpty());
		}
	
		/**
		 * Validates whether the number of lifts in a block meets the required minimum.
		 * @param plan the plan object
		 * @param block the block being validated
		 * @param scrutinyDetail the scrutiny detail object to populate
		 */
		private void validateLiftCount(Plan plan, Block block, ScrutinyDetail scrutinyDetail) {
		    Optional<LiftRequirement> matchedRule = getRequiredLiftCount(plan);
		    if (matchedRule.isPresent()) {
		    	LiftRequirement rule = matchedRule.get();
		    	BigDecimal requiredLifts = rule.getPermissible();
		    if (!isHighRiseLiftRequired(plan)) return;
	
		    boolean valid = BigDecimal.valueOf(Double.parseDouble(block.getNumberOfLifts())).compareTo(requiredLifts) >= 0;
		    setReportOutputDetails(plan, SUBRULE_48, SUBRULE_48_DESCRIPTION,
		            requiredLifts.toString(), block.getNumberOfLifts(),
		            valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal(),
		            scrutinyDetail);
		}
		}
		/**
		 * Checks if high-rise lift rules apply based on FAR or subtype code.
		 * @param plan the plan object
		 * @return true if high-rise lift validation is required
		 */
		private boolean isHighRiseLiftRequired(Plan plan) {
		    String farCode = plan.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode();
		    String subTypeCode = plan.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode();
		    return DxfFileConstants.A.equals(farCode) ||
		           DxfFileConstants.B.equals(subTypeCode) ||
		           DxfFileConstants.E.equals(subTypeCode) ||
		           DxfFileConstants.F.equals(subTypeCode);
		}
	
		/**
		 * Fetches the required number of lifts from feature rules (MDMS).
		 * @param plan the plan object
		 * @return required number of lifts as BigDecimal
		 */
		private Optional<LiftRequirement> getRequiredLiftCount(Plan plan) {
			List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.LIFT.getValue(), false);
	       return rules.stream()
	            .filter(LiftRequirement.class::isInstance)
	            .map(LiftRequirement.class::cast)
	            .findFirst();
		}
	
		/**
		 * Validates dimensions (height & width) of lifts for a given block.
		 * @param plan the plan object
		 * @param block the block being validated
		 * @param scrutinyDetail the scrutiny detail object to populate
		 */
		private void validateLiftDimensions(Plan plan, Block block, ScrutinyDetail scrutinyDetail) {
		    if (!hasValidBuildingHeight(block)) return;
	
		    for (Floor floor : block.getBuilding().getFloors()) {
		        for (Lift lift : getLifts(floor)) {
		            if (!Boolean.TRUE.equals(lift.getLiftClosed())) continue;
	
		            for (Measurement measurement : lift.getLifts()) {
		                BigDecimal area = scale(measurement.getArea());
		                BigDecimal height = scale(measurement.getHeight());
		                BigDecimal width = scale(measurement.getWidth());
	
		                // You can apply min dimension checks here if needed
		                String provided = height + " * " + width;
		                setReportOutputDetails(plan, SUBRULE_118, String.format(SUBRULE_118_DESCRIPTION, EMPTY_STRING, EMPTY_STRING), EMPTY_STRING,
		                        provided, Result.Accepted.getResultVal(), scrutinyDetail);
		                return;
		            }
		        }
		    }
	
		    // No valid lift dimensions found (if required, handle this case too)
		    // setReportOutputDetails(..., Result.Not_Accepted.getResultVal(), ...);
		}
	
		/**
		 * Checks whether the building has a valid (non-zero) height.
		 * @param block the block being checked
		 * @return true if height is set and > 0
		 */
		private boolean hasValidBuildingHeight(Block block) {
		    return block.getBuilding() != null &&
		           block.getBuilding().getBuildingHeight() != null &&
		           block.getBuilding().getBuildingHeight().intValue() > 0;
		}
	
		/**
		 * Rounds a BigDecimal to two decimal places using HALF_UP rounding mode.
		 * @param value the value to round
		 * @return rounded BigDecimal
		 */
		private BigDecimal scale(BigDecimal value) {
		    return value.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
	
		/**
		 * Creates a ScrutinyDetail object with standard column headings.
		 * @param key the unique scrutiny detail key
		 * @return initialized ScrutinyDetail object
		 */
		private ScrutinyDetail createScrutinyDetail(String key) {
		    ScrutinyDetail detail = new ScrutinyDetail();
		    detail.addColumnHeading(1, RULE_NO);
		    detail.addColumnHeading(2, DESCRIPTION);
		    detail.addColumnHeading(3, REQUIRED);
		    detail.addColumnHeading(4, PROVIDED);
		    detail.addColumnHeading(5, STATUS);
		    detail.setKey(key);
		    return detail;
		}
	
		/**
		 * Validates if lift geometry has correct polyline configuration (i.e., 4 points).
		 * Adds error if lift polyline is invalid.
		 * @param plan the Plan object
		 * @param blockNo block number
		 * @param floorNo floor number
		 * @param liftNo lift number
		 * @param liftPolylines list of Measurement objects (lift shapes)
		 */
		private void validateDimensions(Plan plan, String blockNo, int floorNo, String liftNo, List<Measurement> liftPolylines) {
		    long invalidCount = liftPolylines.stream()
		    		.filter(m -> m.getInvalidReason() != null && !m.getInvalidReason().toString().isEmpty())
		            .count();
	
		    if (invalidCount > 0) {
		        plan.addError(
		                String.format(DxfFileConstants.LAYER_LIFT_WITH_NO, blockNo, floorNo, liftNo),
		                invalidCount + INVALID_LIFT_POLYLINE
		                        + String.format(DxfFileConstants.LAYER_LIFT_WITH_NO, blockNo, floorNo, liftNo));
		    }
		}
	
		/**
		 * Populates a scrutiny detail map with rule metadata and adds it to the plan's report.
		 * @param plan the Plan object
		 * @param ruleNo the rule number
		 * @param ruleDesc the rule description
		 * @param expected the expected value
		 * @param actual the provided value
		 * @param status the validation result
		 * @param scrutinyDetail the scrutiny detail object to update
		 */
		private void setReportOutputDetails(Plan plan, String ruleNo, String ruleDesc, String expected, String actual,
		                                     String status, ScrutinyDetail scrutinyDetail) {
			ReportScrutinyDetail detail = new ReportScrutinyDetail();
			detail.setRuleNo(ruleNo);
			detail.setDescription(ruleDesc);
			detail.setRequired(expected);
			detail.setProvided(actual);
			detail.setStatus(status);

			Map<String, String> details = mapReportDetails(detail);
			addScrutinyDetailtoPlan(scrutinyDetail, plan, details);
		}


	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
