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
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.RULE_36_3;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class FireTenderMovement extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(FireTenderMovement.class);

    @Autowired
	MDMSCacheManager cache;

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param plan The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan plan) {
        return plan;
    }

	/**
	 * Processes the given plan to validate fire tender movement. Fetches
	 * permissible values for fire tender movement and validates them against the
	 * plan details.
	 *
	 * @param plan The plan object to process.
	 * @return The processed plan object with scrutiny details added.
	 */

	@Override
	public Plan process(Plan plan) {
		HashMap<String, String> errors = new HashMap<>();

		// Fetch permissible values from rules
		 List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.FIRE_TENDER_MOVEMENT.getValue(), false);
	        Optional<FireTenderMovementRequirement> matchedRule = rules.stream()
	            .filter(FireTenderMovementRequirement.class::isInstance)
	            .map(FireTenderMovementRequirement.class::cast)
	            .findFirst();

		BigDecimal fireTenderValueOne = BigDecimal.ZERO;
		BigDecimal fireTenderValueTwo = BigDecimal.ZERO;

		if (matchedRule.isPresent()) {
			FireTenderMovementRequirement rule = matchedRule.get();
			fireTenderValueOne = rule.getFireTenderMovementValueOne();
			fireTenderValueTwo = rule.getFireTenderMovementValueTwo();
		}

		for (Block block : plan.getBlocks()) {
			processBlockForFireTender(plan, block, fireTenderValueOne, fireTenderValueTwo, errors);
		}

		return plan;
	}
    
	/**
	 * Processes a single block to validate fire tender movement requirements based on the building height.
	 * If the building height exceeds the required threshold, it validates the fire tender movement width
	 * and adds scrutiny results or errors to the plan accordingly.
	 *
	 * @param plan               the building plan being processed
	 * @param block              the specific block within the plan to process
	 * @param minRequiredHeight  the minimum building height above which fire tender movement is mandatory
	 * @param minRequiredWidth   the minimum required width for fire tender movement
	 * @param errors             a map to collect and store error messages if validation fails
	 */
	private void processBlockForFireTender(Plan plan, Block block, BigDecimal minRequiredHeight,
			BigDecimal minRequiredWidth, Map<String, String> errors) {

		if (block.getBuilding() == null)
			return;

		BigDecimal buildingHeight = block.getBuilding().getBuildingHeight()
				.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

		if (buildingHeight.compareTo(minRequiredHeight) <= 0)
			return;

		ScrutinyDetail scrutinyDetail = createScrutinyDetail(block.getNumber(), FIRE_TENDER_MOVEMENT);

		org.egov.common.entity.edcr.FireTenderMovement fireTenderMovement = block.getFireTenderMovement();

		if (fireTenderMovement == null) {
			errors.put(BLK_FTM_ + block.getNumber(),
					FTM_NOT_DEFINED + block.getNumber());
			plan.addErrors(errors);
			return;
		}

		validateFireTenderWidth(plan, block, fireTenderMovement, minRequiredWidth, scrutinyDetail, errors);
	}

	/**
	 * Validates whether the provided width for fire tender movement meets the
	 * minimum required width. If validation passes or fails, a scrutiny detail is
	 * created and added to the plan's report output. Also collects any errors found
	 * in the fire tender movement configuration (e.g., invalid yard locations).
	 *
	 * @param plan               the building plan containing the block and fire
	 *                           tender details
	 * @param block              the block being validated
	 * @param fireTenderMovement the fire tender movement object containing movement
	 *                           paths and widths
	 * @param minRequiredWidth   the minimum width required for fire tender movement
	 * @param scrutinyDetail     the scrutiny detail object used to log validation
	 *                           results
	 * @param errors             a map to collect and store error messages if
	 *                           validation fails
	 */
	private void validateFireTenderWidth(Plan plan, Block block,
			org.egov.common.entity.edcr.FireTenderMovement fireTenderMovement, BigDecimal minRequiredWidth,
			ScrutinyDetail scrutinyDetail, Map<String, String> errors) {

		List<BigDecimal> widths = fireTenderMovement.getFireTenderMovements().stream().map(ftm -> ftm.getWidth())
				.collect(Collectors.toList());

		if (widths.isEmpty())
			return;

		BigDecimal providedWidth = widths.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO)
				.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

		boolean isAccepted = providedWidth.compareTo(minRequiredWidth) >= 0;

		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(RULE_36_3);
		detail.setDescription(WIDTH_DESCRIPTION);
		detail.setPermissible(GREATER_THAN_EQUAL + minRequiredWidth.toPlainString());
		detail.setProvided(providedWidth.toPlainString());
		detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, plan, details);

		if (!fireTenderMovement.getErrors().isEmpty()) {
			String yardNames = String.join(COMMA, fireTenderMovement.getErrors());
			errors.put(FTM_SETBACK,
					FTM_NOT_INSIDE + block.getNumber() + IS_NOT_INSIDE + yardNames + DOT);
			plan.addErrors(errors);
		}
	}

   /**
    * Creates and returns a new {@link ScrutinyDetail} object with column headings pre-set for rule validation.
    * The key is constructed using the block number and the feature being validated (e.g., "Fire Tender Movement").
    *
    * @param string   the block number or identifier
    * @param feature  the feature being scrutinized (e.g., "Fire Tender Movement")
    * @return a {@link ScrutinyDetail} instance with initialized column headings and key
    */
    private ScrutinyDetail createScrutinyDetail(String string, String feature) {
        ScrutinyDetail sd = new ScrutinyDetail();
        sd.setKey(BLOCK + string + UNDERSCORE + feature);
        sd.addColumnHeading(1, RULE_NO);
        sd.addColumnHeading(2, DESCRIPTION);
        sd.addColumnHeading(3, PERMISSIBLE);
        sd.addColumnHeading(4, PROVIDED);
        sd.addColumnHeading(5, STATUS);
        return sd;
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