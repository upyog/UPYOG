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
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.FLOOR;
import static org.egov.edcr.constants.CommonFeatureConstants.UNDERSCORE;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class Balcony extends FeatureProcess {

	private static final Logger log = LogManager.getLogger(Balcony.class);
	
	@Autowired
	MDMSCacheManager cache;

	@Override
	public Plan validate(Plan plan) {
	    return plan;
	}

	/**
	 * Processes the given {@link Plan} by validating balcony widths block-wise.
	 * <p>
	 * For each block in the plan, if the block contains a building, it invokes
	 * {@code processBlockBalconies} to validate balconies floor-wise, gather
	 * scrutiny details, and append them to the plan's report output.
	 * </p>
	 *
	 * @param plan the {@link Plan} object containing building blocks and their details
	 * @return the modified {@link Plan} object with updated scrutiny report
	 */
	
	@Override
	public Plan process(Plan plan) {
	    for (Block block : plan.getBlocks()) {
	        if (block.getBuilding() != null) {
	            processBlockBalconies(plan, block);
	        }
	    }
	    return plan;
	}

	/**
	 * Processes all balconies for a given block and prepares scrutiny details.
	 * <p>
	 * Iterates over each floor of the block's building and delegates balcony validation
	 * to {@code processFloorBalconies}. Appends the collected scrutiny details to
	 * the plan's report output.
	 * </p>
	 *
	 * @param plan  the plan being processed
	 * @param block the block whose balconies are to be processed"Block_"
	 */
	private void processBlockBalconies(Plan plan, Block block) {
	    ScrutinyDetail scrutinyDetail = createScrutinyDetail(BLOCK + block.getNumber() + UNDERSCORE + MdmsFeatureConstants.BALCONY,
	            RULE_NO, FLOOR, DESCRIPTION, PERMISSIBLE, PROVIDED, STATUS);

	    for (Floor floor : block.getBuilding().getFloors()) {
	        processFloorBalconies(plan, block, floor, scrutinyDetail);
	    }

	    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	/**
	 * Processes all balconies on a given floor of a block.
	 * <p>
	 * Retrieves the list of balconies from the floor and validates each one
	 * using {@code validateBalcony}. Also handles typical floor logic.
	 * </p>
	 *
	 * @param plan           the plan being processed
	 * @param block          the block containing the floor
	 * @param floor          the floor to process
	 * @param scrutinyDetail the scrutiny detail object to which validation results are added
	 */
	private void processFloorBalconies(Plan plan, Block block, Floor floor, ScrutinyDetail scrutinyDetail) {
	    boolean isTypicalRepititiveFloor = false;

	    Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);

	    List<org.egov.common.entity.edcr.Balcony> balconies = floor.getBalconies();
	    if (balconies != null && !balconies.isEmpty()) {
	        for (org.egov.common.entity.edcr.Balcony balcony : balconies) {
	            validateBalcony(plan, floor, balcony, typicalFloorValues, scrutinyDetail);
	        }
	    }
	}

	/**
	 * Validates the width of a single balcony against MDMS rules and adds the result to scrutiny.
	 * <p>
	 * Compares the minimum width of the balcony with the permissible value obtained from
	 * MDMS rules. Based on the comparison, a result row is created and added to the scrutiny detail.
	 * </p>
	 *
	 * @param plan              the plan being processed
	 * @param floor             the floor containing the balcony
	 * @param balcony           the balcony to validate
	 * @param typicalFloorValues a map containing details about typical floors, if applicable
	 * @param scrutinyDetail    the scrutiny detail object to which the validation result is added
	 */
	private void validateBalcony(Plan plan, Floor floor, org.egov.common.entity.edcr.Balcony balcony,
	                              Map<String, Object> typicalFloorValues, ScrutinyDetail scrutinyDetail) {
		
		BigDecimal balconyValue;

	    List<BigDecimal> widths = balcony.getWidths();
	    BigDecimal minWidth = widths.isEmpty() ? BigDecimal.ZERO
	            : widths.stream().reduce(BigDecimal::min).get();
	    minWidth = minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
	    
	    List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.BALCONY.getValue(), false);
        Optional<BalconyRequirement> matchedRule = rules.stream()
            .filter(BalconyRequirement.class::isInstance)
            .map(BalconyRequirement.class::cast)
            .findFirst();

	    if (matchedRule.isPresent()) {
	        balconyValue = matchedRule.get().getPermissible();
	    } else {
	        balconyValue = BigDecimal.ZERO;
	    }
	    
//	    List<BalconyRule> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.BALCONY, false, BalconyRule.class);
//
//	    BigDecimal balconyValue = rules.stream()
//	        .findFirst()
//	        .map(BalconyRule::getPermissible)
//	        .orElse(BigDecimal.ZERO);
//


	    boolean isAccepted = minWidth.compareTo(balconyValue.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
	            DcrConstants.ROUNDMODE_MEASUREMENTS)) >= 0;

	    String floorLabel = typicalFloorValues.get(TYPICAL_FLOOR) != null
	            ? (String) typicalFloorValues.get(TYPICAL_FLOOR)
	            : FLOOR_SPACED + floor.getNumber();

		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(RULE45_IV);
		detail.setDescription(String.format(WIDTH_BALCONY_DESCRIPTION, balcony.getNumber()));
		detail.setPermissible(balconyValue.toString());
		detail.setProvided(minWidth.toString());
		detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
		detail.setFloorNo(floorLabel);

		Map<String, String> details = mapReportDetails(detail);
	    scrutinyDetail.getDetail().add(details);
	}

	// Method to create ScrutinyDetail
	private ScrutinyDetail createScrutinyDetail(String key, String... headings) {
	    ScrutinyDetail detail = new ScrutinyDetail();
	    detail.setKey(key);
	    for (int i = 0; i < headings.length; i++) {
	        detail.addColumnHeading(i + 1, headings[i]);
	    }
	    return detail;
	}

	@Override
	public Map<String, Date> getAmendments() {
	    return new LinkedHashMap<>();
	}


}