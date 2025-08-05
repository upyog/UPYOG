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
import org.egov.common.entity.edcr.FeatureRuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.COMMON_ROOF_TANKS;
import static org.egov.edcr.constants.EdcrReportConstants.*;

@Service
public class RoofTank extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(RoofTank.class);

	@Autowired
	MDMSCacheManager cache;
	
	/**
	 * Validates the provided Plan object.
	 * Currently, no validation logic is applied for roof tank height feature.
	 *
	 * @param pl the Plan object to validate
	 * @return the same Plan object without modifications
	 */
	@Override
	public Plan validate(Plan pl) {
		return pl; // No validation logic defined for this feature
	}

	/**
	 * Processes the Plan object to evaluate roof tank height compliance.
	 * It adds scrutiny details to the report output for each block in the plan.
	 *
	 * @param pl the Plan object to process
	 * @return the modified Plan object with scrutiny results
	 */
	@Override
	public Plan process(Plan pl) {
	    ScrutinyDetail scrutinyDetail = createScrutinyDetail();
	    Map<String, String> details = initializeResultDetails();

	    BigDecimal roofTankValue = getPermissibleRoofTankValue(pl);

	    for (Block block : pl.getBlocks()) {
	        processBlockForRoofTank(pl, block, roofTankValue, scrutinyDetail, new HashMap<>(details));
	    }

	    return pl;
	}

	/**
	 * Creates and initializes a ScrutinyDetail object with appropriate column headings
	 * for the roof tank feature.
	 *
	 * @return a new ScrutinyDetail object
	 */
	private ScrutinyDetail createScrutinyDetail() {
	    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.setKey(COMMON_ROOF_TANKS);
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	    scrutinyDetail.addColumnHeading(3, VERIFIED);
	    scrutinyDetail.addColumnHeading(4, ACTION);
	    scrutinyDetail.addColumnHeading(5, STATUS);
	    return scrutinyDetail;
	}

	/**
	 * Initializes the result details map with rule number for roof tank validation.
	 *
	 * @return a map containing initial result details
	 */
	private Map<String, String> initializeResultDetails() {
	    Map<String, String> details = new HashMap<>();
	    details.put(RULE_NO, RULE_44_A);
	    return details;
	}

	/**
	 * Fetches the permissible roof tank height value from the MDMS rules for the plan.
	 *
	 * @param pl the Plan object
	 * @return the permissible roof tank height as BigDecimal, or BigDecimal.ZERO if not found
	 */
	private BigDecimal getPermissibleRoofTankValue(Plan pl) {
	    List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.ROOF_TANK, false);
	    Optional<MdmsFeatureRule> matchedRule = rules.stream()
	        .map(obj -> (MdmsFeatureRule) obj)
	        .findFirst();

	    return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
	}

	/**
	 * Processes a single block in the plan to evaluate roof tank height against the permissible value.
	 * Adds appropriate scrutiny detail entries based on compliance.
	 *
	 * @param pl              the Plan object
	 * @param block           the Block to process
	 * @param roofTankValue   the permissible height for roof tanks
	 * @param scrutinyDetail  the ScrutinyDetail object to which results are added
	 * @param details         a map of result details used for reporting
	 */
	private void processBlockForRoofTank(Plan pl, Block block, BigDecimal roofTankValue,
	                                     ScrutinyDetail scrutinyDetail, Map<String, String> details) {
	    BigDecimal minHeight = BigDecimal.ZERO;

	    if (block.getRoofTanks() != null && !block.getRoofTanks().isEmpty()) {
	        minHeight = block.getRoofTanks().stream().reduce(BigDecimal::min).get();

	        if (minHeight.compareTo(roofTankValue) <= 0) {
	            details.put(DESCRIPTION, ROOFTANK_DESCRIPTION);
	            details.put(VERIFIED, ROOFTANK_HEIGHT_DESC + roofTankValue + MTS);
	            details.put(ACTION, NOT_INCLUDED_ROOF_TANK_HEIGHT + minHeight + TO_BUILDING_HEIGHT);
	            details.put(STATUS, Result.Accepted.getResultVal());
	        } else {
	            details.put(DESCRIPTION, ROOFTANK_DESCRIPTION);
	            details.put(VERIFIED, ROOFTANK_HEIGHT_DESC + roofTankValue + MTS);
	            details.put(ACTION, INCLUDED_ROOF_TANK_HEIGHT + minHeight + TO_BUILDING_HEIGHT);
	            details.put(STATUS, Result.Verify.getResultVal());
	        }

	        scrutinyDetail.getDetail().add(details);
	        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	    }
	}


	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>(); // No amendments defined
	}
}
