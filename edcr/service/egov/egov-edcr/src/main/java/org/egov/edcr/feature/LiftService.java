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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Lift;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LiftService extends FeatureProcess {


	@Autowired
	CacheManagerMdms cache;

	
	private static final String SUBRULE_48 = "48";	
	private static final String SUBRULE_48_DESCRIPTION = "Minimum number of lifts";
	private static final String SUBRULE_118 = "118";
	private static final String SUBRULE_118_DESCRIPTION = "Dimension Of lift";
	

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

	@Override
	public Plan process(Plan plan) {
	    if (plan == null || plan.getBlocks().isEmpty()) return plan;

	    for (Block block : plan.getBlocks()) {
	        if (!isLiftValidationRequired(block)) continue;

	        ScrutinyDetail liftCountDetail = createScrutinyDetail("Block_" + block.getNumber() + "_Lift - Minimum Required");
	        ScrutinyDetail liftDimDetail = createScrutinyDetail("Block_" + block.getNumber() + "_Lift Dimension");

	        validateLiftCount(plan, block, liftCountDetail);
	        validateLiftDimensions(plan, block, liftDimDetail);
	    }

	    return plan;
	}

	

	private boolean hasBuildingWithFloors(Block block) {
	    return block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty();
	}

	private List<Lift> getLifts(Floor floor) {
	    return floor.getLifts() != null ? floor.getLifts() : Collections.emptyList();
	}

	private boolean isLiftValidationRequired(Block block) {
	    return block.getBuilding() != null &&
	           !block.getBuilding().getOccupancies().isEmpty() &&
	           block.getBuilding().getFloors().stream().anyMatch(floor -> !getLifts(floor).isEmpty());
	}

	private void validateLiftCount(Plan plan, Block block, ScrutinyDetail scrutinyDetail) {
	    BigDecimal requiredLifts = getRequiredLiftCount(plan);
	    if (!isHighRiseLiftRequired(plan)) return;

	    boolean valid = BigDecimal.valueOf(Double.parseDouble(block.getNumberOfLifts())).compareTo(requiredLifts) >= 0;
	    setReportOutputDetails(plan, SUBRULE_48, SUBRULE_48_DESCRIPTION,
	            requiredLifts.toString(), block.getNumberOfLifts(),
	            valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal(),
	            scrutinyDetail);
	}

	private boolean isHighRiseLiftRequired(Plan plan) {
	    String farCode = plan.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode();
	    String subTypeCode = plan.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode();
	    return DxfFileConstants.A.equals(farCode) ||
	           DxfFileConstants.B.equals(subTypeCode) ||
	           DxfFileConstants.E.equals(subTypeCode) ||
	           DxfFileConstants.F.equals(subTypeCode);
	}

	private BigDecimal getRequiredLiftCount(Plan plan) {
	    List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.LIFT, false);
	    return rules.stream()
	                .map(r -> (MdmsFeatureRule) r)
	                .findFirst()
	                .map(MdmsFeatureRule::getPermissible)
	                .orElse(BigDecimal.ZERO);
	}

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
	                setReportOutputDetails(plan, SUBRULE_118, String.format(SUBRULE_118_DESCRIPTION, "", ""), "",
	                        provided, Result.Accepted.getResultVal(), scrutinyDetail);
	                return;
	            }
	        }
	    }

	    // No valid lift dimensions found (if required, handle this case too)
	    // setReportOutputDetails(..., Result.Not_Accepted.getResultVal(), ...);
	}

	private boolean hasValidBuildingHeight(Block block) {
	    return block.getBuilding() != null &&
	           block.getBuilding().getBuildingHeight() != null &&
	           block.getBuilding().getBuildingHeight().intValue() > 0;
	}

	private BigDecimal scale(BigDecimal value) {
	    return value.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

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

	private void validateDimensions(Plan plan, String blockNo, int floorNo, String liftNo, List<Measurement> liftPolylines) {
	    long invalidCount = liftPolylines.stream()
	    		.filter(m -> m.getInvalidReason() != null && !m.getInvalidReason().toString().isEmpty())
	            .count();

	    if (invalidCount > 0) {
	        plan.addError(
	                String.format(DxfFileConstants.LAYER_LIFT_WITH_NO, blockNo, floorNo, liftNo),
	                invalidCount + " number of lift polyline not having only 4 points in layer "
	                        + String.format(DxfFileConstants.LAYER_LIFT_WITH_NO, blockNo, floorNo, liftNo));
	    }
	}

	private void setReportOutputDetails(Plan plan, String ruleNo, String ruleDesc, String expected, String actual,
	                                     String status, ScrutinyDetail scrutinyDetail) {
	    Map<String, String> details = new HashMap<>();
	    details.put(RULE_NO, ruleNo);
	    details.put(DESCRIPTION, ruleDesc);
	    details.put(REQUIRED, expected);
	    details.put(PROVIDED, actual);
	    details.put(STATUS, status);
	    scrutinyDetail.getDetail().add(details);
	    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}


	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
