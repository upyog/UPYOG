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
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.ParapetRequirement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonKeyConstants.COMMON_PARAPET;
import static org.egov.edcr.constants.EdcrReportConstants.*;

@Service
public class Parapet extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Parapet.class);

	@Autowired
	MDMSCacheManager cache;

	/**
	 * Validates the parapet-related details in the given Plan.
	 *
	 * @param pl the plan object containing all architectural data.
	 * @return the same plan object, optionally enriched with validation errors (currently no validation logic here).
	 */
	@Override
	public Plan validate(Plan pl) {

		return pl;
	}

	/**
	 * Processes the parapet height for each block in the plan, 
	 * compares it against permissible values from the MDMS rule,
	 * and adds scrutiny details to the plan's report output.
	 *
	 * @param pl the plan containing block-wise parapet details
	 * @return the plan object enriched with parapet scrutiny results
	 */
	public Plan process(Plan pl) {
	    ScrutinyDetail scrutinyDetail = initializeScrutinyDetail();
	    Map<String, String> details = initializeRuleDetails();

	    List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.PARAPET.getValue(), false);
        Optional<ParapetRequirement> matchedRule = rules.stream()
            .filter(ParapetRequirement.class::isInstance)
            .map(ParapetRequirement.class::cast)
            .findFirst();

	    BigDecimal parapetValueOne = BigDecimal.ZERO;
	    BigDecimal parapetValueTwo = BigDecimal.ZERO;

	    if (matchedRule.isPresent()) {
	        parapetValueOne = matchedRule.get().getParapetValueOne();
	        parapetValueTwo = matchedRule.get().getParapetValueTwo();
	    }

	    for (Block b : pl.getBlocks()) {
	        if (b.getParapets() != null && !b.getParapets().isEmpty()) {
	            BigDecimal minHeight = getMinimumParapetHeight(b);
	            validateParapetHeight(minHeight, parapetValueOne, parapetValueTwo, details, scrutinyDetail, pl);
	        }
	    }

	    return pl;
	}

	/**
	 * Initializes a {@link ScrutinyDetail} object with predefined headings 
	 * for parapet height scrutiny.
	 *
	 * @return the initialized scrutiny detail object
	 */
	private ScrutinyDetail initializeScrutinyDetail() {
	    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.setKey(COMMON_PARAPET);
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	    scrutinyDetail.addColumnHeading(3, REQUIRED);
	    scrutinyDetail.addColumnHeading(4, PROVIDED);
	    scrutinyDetail.addColumnHeading(5, STATUS);
	    return scrutinyDetail;
	}
	/**
	 * Initializes rule metadata such as rule number and description
	 * for parapet validation reporting.
	 *
	 * @return a map containing initial rule details
	 */

	private Map<String, String> initializeRuleDetails() {
	    Map<String, String> details = new HashMap<>();
	    details.put(RULE_NO, RULE_41_V);
	    details.put(DESCRIPTION, PARAPET_DESCRIPTION);
	    return details;
	}

	/**
	 * Computes the minimum parapet height from the list of parapets in a block.
	 *
	 * @param block the block containing parapet height values
	 * @return the minimum parapet height value in the block
	 */
	private BigDecimal getMinimumParapetHeight(Block block) {
	    return block.getParapets().stream().reduce(BigDecimal::min).get();
	}

	/**
	 * Validates whether the given minimum parapet height falls within the permissible range.
	 * Appends the result (accepted/rejected) to the plan's report output.
	 *
	 * @param minHeight the minimum parapet height found in a block
	 * @param parapetValueOne the lower bound of permissible height
	 * @param parapetValueTwo the upper bound of permissible height
	 * @param details the map containing rule metadata and validation outcome
	 * @param scrutinyDetail the scrutiny detail object to which results are appended
	 * @param pl the plan to which scrutiny details are added
	 */
	private void validateParapetHeight(BigDecimal minHeight, BigDecimal parapetValueOne, BigDecimal parapetValueTwo,
	                                   Map<String, String> details, ScrutinyDetail scrutinyDetail, Plan pl) {
	    String required = HEIGHT + parapetValueOne + AND_HEIGHT + parapetValueTwo;
	    String provided = HEIGHT + minHeight + AND_HEIGHT + minHeight;

	    details.put(REQUIRED, required);
	    details.put(PROVIDED, provided);

	    if (minHeight.compareTo(parapetValueOne) >= 0 && minHeight.compareTo(parapetValueTwo) <= 0) {
	        details.put(STATUS, Result.Accepted.getResultVal());
	    } else {
	        details.put(STATUS, Result.Not_Accepted.getResultVal());
	    }

	    scrutinyDetail.getDetail().add(details);
	    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
