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
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadRoom extends FeatureProcess {

	// Logger for logging information and errors
	private static final Logger LOG = LogManager.getLogger(HeadRoom.class);

	// Constants for rule identifiers and descriptions
	private static final String RULE42_5_ii = "42-5-ii";
	private static final String RULE_42_5_ii_DESCRIPTION = "Minimum clear of stair head-room";

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;

	@Autowired
	CacheManagerMdms cache;

	/**
	 * Validates the given plan object. Currently, no specific validation logic is
	 * implemented.
	 *
	 * @param plan The plan object to validate.
	 * @return The same plan object without any modifications.
	 */
	@Override
	public Plan validate(Plan plan) {
		return plan;
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

	/**
	 * Processes the headroom validation for each block in the plan.
	 * It checks whether the provided headroom dimensions meet the permissible values defined in the feature rules.
	 *
	 * @param plan The plan object containing building blocks and headroom details.
	 * @return The modified plan object with scrutiny results added.
	 */
	@Override
	public Plan process(Plan plan) {
		BigDecimal permissibleHeadroom = fetchPermissibleHeadroom(plan);

		for (Block block : plan.getBlocks()) {
			if (block.getBuilding() != null) {
				processHeadroomForBlock(plan, block, permissibleHeadroom);
			}
		}

		return plan;
	}

	/**
	 * Fetches the permissible headroom value defined under the HEAD_ROOM feature rules.
	 *
	 * @param plan The plan from which occupancy and context are used to filter applicable feature rules.
	 * @return The permissible headroom value, or {@code BigDecimal.ZERO} if not found.
	 */
	private BigDecimal fetchPermissibleHeadroom(Plan plan) {
		List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.HEAD_ROOM, false);

		return rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst().map(MdmsFeatureRule::getPermissible)
				.orElse(BigDecimal.ZERO);
	}

	/**
	 * Validates the minimum headroom provided in a block against the permissible limit.
	 * If headroom dimensions are found, the result is added to the scrutiny detail of the plan.
	 *
	 * @param plan                The plan object to which the scrutiny results will be added.
	 * @param block               The block being validated for headroom dimensions.
	 * @param permissibleHeadroom The minimum permissible headroom as per rules.
	 */
	private void processHeadroomForBlock(Plan plan, Block block, BigDecimal permissibleHeadroom) {
		org.egov.common.entity.edcr.HeadRoom headRoom = block.getBuilding().getHeadRoom();
		if (headRoom == null || headRoom.getHeadRoomDimensions() == null
				|| headRoom.getHeadRoomDimensions().isEmpty()) {
			return;
		}

		BigDecimal minHeadRoom = headRoom.getHeadRoomDimensions().stream().reduce(BigDecimal::min)
				.orElse(BigDecimal.ZERO);
		BigDecimal minWidth = Util.roundOffTwoDecimal(minHeadRoom);

		ScrutinyDetail scrutinyDetail = createScrutinyDetail("Block_" + block.getNumber() + "_Stair Headroom");

		String resultStatus = minWidth.compareTo(permissibleHeadroom) >= 0 ? Result.Accepted.getResultVal()
				: Result.Not_Accepted.getResultVal();

		addScrutinyResult(plan, scrutinyDetail, RULE42_5_ii, RULE_42_5_ii_DESCRIPTION, permissibleHeadroom.toString(),
				minWidth.toString(), resultStatus);
	}

	/**
	 * Creates a {@link ScrutinyDetail} object with predefined column headings for headroom validation.
	 *
	 * @param key The key identifying the scrutiny section (e.g., for a particular block).
	 * @return A {@link ScrutinyDetail} object initialized with standard columns.
	 */
	private ScrutinyDetail createScrutinyDetail(String key) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey(key);
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		return scrutinyDetail;
	}

	/**
	 * Adds a result row to the given scrutiny detail and appends it to the plan’s report output.
	 *
	 * @param plan         The plan object to which scrutiny details are appended.
	 * @param scrutinyDetail The detail object to store rule evaluation results.
	 * @param ruleNo       The rule number being evaluated.
	 * @param ruleDesc     A short description of the rule.
	 * @param expected     The expected value as per the rule.
	 * @param actual       The actual value found in the plan.
	 * @param status       The result of the evaluation (e.g., Accepted or Not Accepted).
	 */
	private void addScrutinyResult(Plan plan, ScrutinyDetail scrutinyDetail, String ruleNo, String ruleDesc,
			String expected, String actual, String status) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

}
