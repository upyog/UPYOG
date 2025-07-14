/*
 * UPYPG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
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
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.CacheManagerMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Basement extends FeatureProcess {


	private static final Logger LOG = LogManager.getLogger(Basement.class);
	private static final String RULE_46_6A = "46-6a";
	private static final String RULE_46_6C = "46-6c";
	public static final String BASEMENT_DESCRIPTION_ONE = "Height from the floor to the soffit of the roof slab or ceiling";
	public static final String BASEMENT_DESCRIPTION_TWO = "Minimum height of the ceiling of upper basement above ground level";

	@Autowired
	private CacheManagerMdms cache;

	@Override
	public Plan validate(Plan pl) {
	    return pl;
	}

	@Override
	public Plan process(Plan pl) {
	    if (pl.getBlocks() == null) return pl;

	    ScrutinyDetail scrutinyDetail = createScrutinyDetail();
	    Optional<MdmsFeatureRule> matchedRule = fetchBasementRule(pl);

	    if (!matchedRule.isPresent()) return pl;

	    MdmsFeatureRule rule = matchedRule.get();
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

	private boolean hasValidFloors(Block block) {
	    return block.getBuilding() != null
	            && block.getBuilding().getFloors() != null
	            && !block.getBuilding().getFloors().isEmpty();
	}

	private Optional<MdmsFeatureRule> fetchBasementRule(Plan pl) {
	    List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.BASEMENT, false);
	    return rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
	}

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

	private void validateHeightFromFloorToCeiling(Floor floor, BigDecimal basementValue, ScrutinyDetail detail) {
	    List<BigDecimal> heights = floor.getHeightFromTheFloorToCeiling();
	    if (heights == null || heights.isEmpty()) return;

	    BigDecimal minHeight = heights.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
	    boolean accepted = minHeight.compareTo(basementValue) >= 0;

	    detail.getDetail().add(createResultRow(
	            RULE_46_6A,
	            BASEMENT_DESCRIPTION_ONE,
	            ">=" + basementValue,
	            minHeight,
	            accepted
	    ));
	}

	private void validateUpperBasementCeilingHeight(Floor floor, BigDecimal minValue, BigDecimal maxValue, ScrutinyDetail detail) {
	    List<BigDecimal> ceilingHeights = floor.getHeightOfTheCeilingOfUpperBasement();
	    if (ceilingHeights == null || ceilingHeights.isEmpty()) return;

	    BigDecimal minCeilingHeight = ceilingHeights.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
	    boolean accepted = minCeilingHeight.compareTo(minValue) >= 0 && minCeilingHeight.compareTo(maxValue) < 0;

	    detail.getDetail().add(createResultRow(
	            RULE_46_6C,
	            BASEMENT_DESCRIPTION_TWO,
	            "Between " + minValue + " to " + maxValue,
	            minCeilingHeight,
	            accepted
	    ));
	}

	private Map<String, String> createResultRow(String ruleNo, String description, String required, BigDecimal provided, boolean accepted) {
	    Map<String, String> details = new HashMap<>();
	    details.put(RULE_NO, ruleNo);
	    details.put(DESCRIPTION, description);
	    details.put(REQUIRED, required);
	    details.put(PROVIDED, provided.toString());
	    details.put(STATUS, accepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
	    return details;
	}

	@Override
	public Map<String, Date> getAmendments() {
	    return new LinkedHashMap<>();
	}


}
