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
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Balcony extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Balcony.class);
	private static final String FLOOR = "Floor";
	private static final String RULE45_IV = "4.4.4 (iii)";
	private static final String WIDTH_BALCONY_DESCRIPTION = "Minimum width for balcony %s";

	BigDecimal balconyValue;
	
	@Autowired
	CacheManagerMdms cache;

	@Override
	public Plan validate(Plan plan) {
	    return plan;
	}

	@Override
	public Plan process(Plan plan) {
	    for (Block block : plan.getBlocks()) {
	        if (block.getBuilding() != null) {
	            processBlockBalconies(plan, block);
	        }
	    }
	    return plan;
	}

	// 1. Process balconies block-wise
	private void processBlockBalconies(Plan plan, Block block) {
	    ScrutinyDetail scrutinyDetail = createScrutinyDetail("Block_" + block.getNumber() + "_" + MdmsFeatureConstants.BALCONY,
	            RULE_NO, FLOOR, DESCRIPTION, PERMISSIBLE, PROVIDED, STATUS);

	    for (Floor floor : block.getBuilding().getFloors()) {
	        processFloorBalconies(plan, block, floor, scrutinyDetail);
	    }

	    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	// 2. Process each floor's balconies
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

	// 3. Validate individual balcony and add to scrutiny
	private void validateBalcony(Plan plan, Floor floor, org.egov.common.entity.edcr.Balcony balcony,
	                              Map<String, Object> typicalFloorValues, ScrutinyDetail scrutinyDetail) {

	    List<BigDecimal> widths = balcony.getWidths();
	    BigDecimal minWidth = widths.isEmpty() ? BigDecimal.ZERO
	            : widths.stream().reduce(BigDecimal::min).get();
	    minWidth = minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

	    List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.BALCONY, false);
	    Optional<MdmsFeatureRule> matchedRule = rules.stream()
	            .map(obj -> (MdmsFeatureRule) obj)
	            .findFirst();

	    if (matchedRule.isPresent()) {
	        balconyValue = matchedRule.get().getPermissible();
	    } else {
	        balconyValue = BigDecimal.ZERO;
	    }

	    boolean isAccepted = minWidth.compareTo(balconyValue.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
	            DcrConstants.ROUNDMODE_MEASUREMENTS)) >= 0;

	    String floorLabel = typicalFloorValues.get("typicalFloors") != null
	            ? (String) typicalFloorValues.get("typicalFloors")
	            : "floor " + floor.getNumber();

	    Map<String, String> resultRow = createResultRow(
	            RULE45_IV,
	            String.format(WIDTH_BALCONY_DESCRIPTION, balcony.getNumber()),
	            balconyValue.toString(),
	            minWidth,
	            isAccepted
	    );
	    resultRow.put(FLOOR, floorLabel);

	    scrutinyDetail.getDetail().add(resultRow);
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

	// Method to create result row
	private Map<String, String> createResultRow(String ruleNo, String description, String permissible,
	                                            BigDecimal provided, boolean accepted) {
	    Map<String, String> details = new HashMap<>();
	    details.put(RULE_NO, ruleNo);
	    details.put(DESCRIPTION, description);
	    details.put(PERMISSIBLE, permissible);
	    details.put(PROVIDED, provided.toString());
	    details.put(STATUS, accepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
	    return details;
	}

	@Override
	public Map<String, Date> getAmendments() {
	    return new LinkedHashMap<>();
	}


}