/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
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
public class PassageService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(PassageService.class);
    private static final String RULE41 = "41";
    private static final String RULE39_6 = "39(6)";
    private static final String RULE39_6_DESCRIPTION = "The minimum passage giving access to stair";
    private static final String RULE_41_DESCRIPTION = "The minimum width of corridors/ verandhas";
  
    @Autowired
   	CacheManagerMdms cache;
    
    @Override
    public Plan validate(Plan plan) {
        return plan;
    }

//	@Override
//	public Plan process(Plan plan) {
//		// Initialize variables to store permissible passage dimensions
//		BigDecimal passageStairMinimumWidth = BigDecimal.ZERO;
//		BigDecimal passageMinWidth = BigDecimal.ZERO;
//
//	
//		List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.PASSAGE_SERVICE, false);
//
//		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
//
//		if (matchedRule.isPresent()) {
//			MdmsFeatureRule rule = matchedRule.get();
//			passageStairMinimumWidth = rule.getPassageServiceValueOne();
//			passageMinWidth = rule.getPassageServiceValueTwo();
//		}
//		// Iterate through all blocks in the plan
//		for (Block block : plan.getBlocks()) {
//			if (block.getBuilding() != null) {
//				// Initialize scrutiny details for passage and passage stair
//				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//				scrutinyDetail.addColumnHeading(1, RULE_NO);
//				scrutinyDetail.addColumnHeading(2, REQUIRED);
//				scrutinyDetail.addColumnHeading(3, PROVIDED);
//				scrutinyDetail.addColumnHeading(4, STATUS);
//				scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Passage");
//
//				ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
//				scrutinyDetail1.addColumnHeading(1, RULE_NO);
//				scrutinyDetail1.addColumnHeading(2, REQUIRED);
//				scrutinyDetail1.addColumnHeading(3, PROVIDED);
//				scrutinyDetail1.addColumnHeading(4, STATUS);
//				scrutinyDetail1.setKey("Block_" + block.getNumber() + "_" + "Passage Stair");
//
//				// Get the passage details from the block
//				org.egov.common.entity.edcr.Passage passage = block.getBuilding().getPassage();
//
//				if (passage != null) {
//					// Validate passage dimensions
//					List<BigDecimal> passagePolylines = passage.getPassageDimensions();
//					List<BigDecimal> passageStairPolylines = passage.getPassageStairDimensions();
//
//					if (passagePolylines != null && !passagePolylines.isEmpty()) {
//						// Find the minimum width of the passage
//						BigDecimal minPassagePolyLine = passagePolylines.stream().reduce(BigDecimal::min).get();
//						BigDecimal minWidth = Util.roundOffTwoDecimal(minPassagePolyLine);
//
//						// Compare the minimum width with the permissible width
//						if (minWidth.compareTo(passageMinWidth) >= 0) {
//							// If the width is within permissible limits, mark as Accepted
//							setReportOutputDetails(plan, RULE41, RULE_41_DESCRIPTION, passageMinWidth.toString(),
//									String.valueOf(minWidth), Result.Accepted.getResultVal(), scrutinyDetail);
//						} else {
//							// If the width is not within permissible limits, mark as Not Accepted
//							setReportOutputDetails(plan, RULE41, RULE_41_DESCRIPTION, passageMinWidth.toString(),
//									String.valueOf(minWidth), Result.Not_Accepted.getResultVal(), scrutinyDetail);
//						}
//					}
//
//					if (passageStairPolylines != null && !passageStairPolylines.isEmpty()) {
//						// Find the minimum width of the passage stair
//						BigDecimal minPassageStairPolyLine = passageStairPolylines.stream().reduce(BigDecimal::min)
//								.get();
//						BigDecimal minWidth = Util.roundOffTwoDecimal(minPassageStairPolyLine);
//
//						// Compare the minimum width with the permissible width
//						if (minWidth.compareTo(Util.roundOffTwoDecimal(passageStairMinimumWidth)) >= 0) {
//							// If the width is within permissible limits, mark as Accepted
//							setReportOutputDetails(plan, RULE39_6, RULE39_6_DESCRIPTION,
//									passageStairMinimumWidth.toString(), String.valueOf(minWidth),
//									Result.Accepted.getResultVal(), scrutinyDetail1);
//						} else {
//							// If the width is not within permissible limits, mark as Not Accepted
//							setReportOutputDetails(plan, RULE39_6, RULE39_6_DESCRIPTION,
//									passageStairMinimumWidth.toString(), String.valueOf(minWidth),
//									Result.Not_Accepted.getResultVal(), scrutinyDetail1);
//						}
//					}
//				}
//			}
//		}
//		return plan; // Return the updated plan object
//	}
//
//    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
//            String status, ScrutinyDetail scrutinyDetail) {
//        // Add details to the scrutiny report
//        Map<String, String> details = new HashMap<>();
//        details.put(RULE_NO, ruleNo);
//        details.put(DESCRIPTION, ruleDesc);
//        details.put(REQUIRED, expected);
//        details.put(PROVIDED, actual);
//        details.put(STATUS, status);
//        scrutinyDetail.getDetail().add(details);
//        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//    }
    @Override
    public Plan process(Plan plan) {
        BigDecimal passageStairMinimumWidth = BigDecimal.ZERO;
        BigDecimal passageMinWidth = BigDecimal.ZERO;

        List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.PASSAGE_SERVICE, false);

        for (Object obj : rules) {
            MdmsFeatureRule rule = (MdmsFeatureRule) obj;
            passageStairMinimumWidth = rule.getPassageServiceValueOne();
            passageMinWidth = rule.getPassageServiceValueTwo();
            break; 
        }

        for (Block block : plan.getBlocks()) {
            if (block.getBuilding() == null || block.getBuilding().getPassage() == null) {
                continue;
            }

            ScrutinyDetail passageDetail = createScrutinyDetail("Block_" + block.getNumber() + "_Passage");
            ScrutinyDetail passageStairDetail = createScrutinyDetail("Block_" + block.getNumber() + "_Passage Stair");

            org.egov.common.entity.edcr.Passage passage = block.getBuilding().getPassage();

            validatePassageDimension(
                passage.getPassageDimensions(), 
                passageMinWidth, 
                RULE41, 
                RULE_41_DESCRIPTION, 
                passageDetail, 
                plan
            );

            validatePassageDimension(
                passage.getPassageStairDimensions(), 
                passageStairMinimumWidth, 
                RULE39_6, 
                RULE39_6_DESCRIPTION, 
                passageStairDetail, 
                plan
            );
        }

        return plan;
    }

    private void validatePassageDimension(List<BigDecimal> dimensions, BigDecimal permissibleWidth,
                                          String ruleNo, String ruleDesc, ScrutinyDetail detail, Plan plan) {
        if (dimensions != null && !dimensions.isEmpty()) {
            BigDecimal minWidth = Util.roundOffTwoDecimal(dimensions.stream().reduce(BigDecimal::min).get());
            String result = minWidth.compareTo(permissibleWidth) >= 0
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal();

            setReportOutputDetails(plan, ruleNo, ruleDesc, permissibleWidth.toString(),
                    String.valueOf(minWidth), result, detail);
        }
    }

    private ScrutinyDetail createScrutinyDetail(String key) {
        ScrutinyDetail detail = new ScrutinyDetail();
        detail.addColumnHeading(1, RULE_NO);
        detail.addColumnHeading(2, REQUIRED);
        detail.addColumnHeading(3, PROVIDED);
        detail.addColumnHeading(4, STATUS);
        detail.setKey(key);
        return detail;
    }

    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
                                        String status, ScrutinyDetail scrutinyDetail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }


    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
