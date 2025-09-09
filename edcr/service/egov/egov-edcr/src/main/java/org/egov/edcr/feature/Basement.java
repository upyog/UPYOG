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
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class Basement extends FeatureProcess {


	private static final Logger LOG = LogManager.getLogger(Basement.class);

	@Autowired
	private MDMSCacheManager cache;

	@Override
	public Plan validate(Plan pl) {
	    return pl;
	}

	/**
	 * Processes the basement floor of each block in the plan to validate height-related rules.
	 * <p>
	 * This method performs the following steps:
	 * <ul>
	 *     <li>Retrieves applicable MDMS rules for basement floors.</li>
	 *     <li>Iterates over each block and validates the basement (floor number -1) heights:</li>
	 *     <li> - Floor to ceiling height against {@code permissibleOne}</li>
	 *     <li> - Ceiling height of upper basement between {@code permissibleTwo} and {@code permissibleThree}</li>
	 *     <li>Adds the validation results to the scrutiny report.</li>
	 * </ul>
	 *
	 * @param pl the plan to process
	 * @return the modified plan with scrutiny details added
	 */
	@Override
	public Plan process(Plan pl) {
	    if (pl.getBlocks() == null) return pl;

	    ScrutinyDetail scrutinyDetail = createScrutinyDetail();
	    Optional<BasementRequirement> matchedRule = fetchBasementRule(pl);

	    if (!matchedRule.isPresent()) return pl;

	    BasementRequirement rule = matchedRule.get();
	    BigDecimal basementValue = rule.getPermissibleOne();
	    BigDecimal basementValuetwo = rule.getPermissibleTwo();
	    BigDecimal basementValuethree = rule.getPermissibleThree();

	    for (Block block : pl.getBlocks()) {
	        if (!hasValidFloors(block)) continue;

	        for (Floor floor : block.getBuilding().getFloors()) {
	            if (floor.getNumber() != -1) continue; // Only basement floor

	            validateHeightFromFloorToCeiling(floor, basementValue, scrutinyDetail);
	            validateUpperBasementCeilingHeight(floor, basementValuetwo, basementValuethree, scrutinyDetail);
	        }
	    }

	    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	    return pl;
	}

	/**
	 * Checks whether the given block has a valid building with at least one floor.
	 *
	 * @param block the block to check
	 * @return {@code true} if the building and its floors are non-null and non-empty, {@code false} otherwise
	 */
	private boolean hasValidFloors(Block block) {
	    return block.getBuilding() != null
	            && block.getBuilding().getFloors() != null
	            && !block.getBuilding().getFloors().isEmpty();
	}

	/**
	 * Fetches the first applicable MDMS rule for the basement feature from the cache.
	 *
	 * @param pl the plan for which the rules are to be fetched
	 * @return an {@code Optional<MdmsFeatureRule>} containing the basement rule, if available
	 */
	private Optional<BasementRequirement> fetchBasementRule(Plan pl) {
	    List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.BASEMENT.getValue(), false);

	    return rules.stream()
	        .filter(BasementRequirement.class::isInstance)
	        .map(BasementRequirement.class::cast)
	        .findFirst();
	}
	
	/**
	 * Creates and initializes a new {@link ScrutinyDetail} for basement rule validations.
	 *
	 * @return a {@link ScrutinyDetail} instance with appropriate column headings and key
	 */
	private ScrutinyDetail createScrutinyDetail() {
	    ScrutinyDetail detail = new ScrutinyDetail();
	    detail.setKey(Common_Basement);
	    detail.addColumnHeading(1, RULE_NO);
	    detail.addColumnHeading(2, DESCRIPTION);
	    detail.addColumnHeading(3, REQUIRED);
	    detail.addColumnHeading(4, PROVIDED);
	    detail.addColumnHeading(5, STATUS);
	    return detail;
	}
	
	/**
	 * Validates that the minimum height from the basement floor to the ceiling meets the required value.
	 *
	 * @param floor          the basement floor to validate
	 * @param basementValue  the minimum required floor-to-ceiling height
	 * @param detail         the scrutiny detail where results will be added
	 */

	private void validateHeightFromFloorToCeiling(Floor floor, BigDecimal basementValue, ScrutinyDetail detail) {
	    List<BigDecimal> heights = floor.getHeightFromTheFloorToCeiling();
	    if (heights == null || heights.isEmpty()) return;

	    BigDecimal minHeight = heights.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
	    boolean accepted = minHeight.compareTo(basementValue) >= 0;

	    detail.getDetail().add(createResultRow(
	            RULE_46_6A,
	            BASEMENT_DESCRIPTION_ONE,
	            GREATER_THAN_EQUAL + basementValue,
	            minHeight,
	            accepted
	    ));
	}

	/**
	 * Validates that the minimum ceiling height of the upper basement lies within the specified range.
	 *
	 * @param floor      the basement floor to validate
	 * @param minValue   the minimum allowable ceiling height
	 * @param maxValue   the maximum allowable ceiling height (exclusive)
	 * @param detail     the scrutiny detail where results will be added
	 */
	private void validateUpperBasementCeilingHeight(Floor floor, BigDecimal minValue, BigDecimal maxValue, ScrutinyDetail detail) {
	    List<BigDecimal> ceilingHeights = floor.getHeightOfTheCeilingOfUpperBasement();
	    if (ceilingHeights == null || ceilingHeights.isEmpty()) return;

	    BigDecimal minCeilingHeight = ceilingHeights.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
	    boolean accepted = minCeilingHeight.compareTo(minValue) >= 0 && minCeilingHeight.compareTo(maxValue) < 0;

	    detail.getDetail().add(createResultRow(
	            RULE_46_6C,
	            BASEMENT_DESCRIPTION_TWO,
	            BETWEEN + minValue + TO + maxValue,
	            minCeilingHeight,
	            accepted
	    ));
	}

	/**
	 * Creates a result row for the scrutiny report with the specified parameters.
	 *
	 * @param ruleNo      the rule number being validated
	 * @param description a brief description of the rule
	 * @param required    the required/permissible value as a string
	 * @param provided    the actual provided value
	 * @param accepted    whether the validation passed or failed
	 * @return a map representing a row in the scrutiny report
	 */
	private Map<String, String> createResultRow(String ruleNo, String description, String required, BigDecimal provided, boolean accepted) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(ruleNo);
		detail.setDescription(description);
		detail.setRequired(required);
		detail.setProvided(provided.toString());
		detail.setStatus(accepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

		Map<String, String> details = mapReportDetails(detail);
		return details;
	}

	@Override
	public Map<String, Date> getAmendments() {
	    return new LinkedHashMap<>();
	}


}
