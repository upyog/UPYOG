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

import static org.egov.edcr.utility.DcrConstants.IN_LITRE;

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
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WaterTankCapacity extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(WaterTankCapacity.class);

    private static final String RULE_59_10_vii = "59-10-vii";
    private static final String RULE_59_10_vii_DESCRIPTION = "Water tank capacity";
    private static final String WATER_TANK_CAPACITY = "Minimum capacity of Water tank";

    @Override
    public Plan validate(Plan pl) {
        // No specific validation logic added; returning the input plan as-is.
        return pl;
    }

    @Autowired
	CacheManagerMdms cache;
//
//	@Override
//	public Plan process(Plan pl) {
//		// Initialize ScrutinyDetail for reporting the rule check
//		scrutinyDetail = new ScrutinyDetail();
//		scrutinyDetail.addColumnHeading(1, RULE_NO);
//		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//		scrutinyDetail.addColumnHeading(3, REQUIRED);
//		scrutinyDetail.addColumnHeading(4, PROVIDED);
//		scrutinyDetail.addColumnHeading(5, STATUS);
//		scrutinyDetail.setKey("Common_Water tank capacity");
//
//		// Rule metadata
//		String subRule = RULE_59_10_vii;
//		String subRuleDesc = RULE_59_10_vii_DESCRIPTION;
//
//		// Default expected values
//		BigDecimal expectedWaterTankCapacity = BigDecimal.ZERO;
//		BigDecimal waterTankCapacityArea = BigDecimal.ZERO;
//		BigDecimal waterTankCapacityExpected = BigDecimal.ZERO;
//
//		List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.WATER_TANK_CAPACITY, false);
//
//		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
//
//		if (matchedRule.isPresent()) {
//			MdmsFeatureRule rule = matchedRule.get();
//			waterTankCapacityArea = rule.getWaterTankCapacityArea();
//			waterTankCapacityExpected = rule.getWaterTankCapacityExpected();
//		}
//
//		// Proceed if water tank capacity is available in plan
//		if (pl.getUtility() != null && pl.getVirtualBuilding() != null
//				&& pl.getUtility().getWaterTankCapacity() != null) {
//
//			Boolean valid = false;
//
//			// Calculate number of persons = total built-up area / permissible area per
//			// person
//			BigDecimal totalBuitUpArea = pl.getVirtualBuilding().getTotalBuitUpArea();
//			BigDecimal noOfPersons = totalBuitUpArea.divide(waterTankCapacityArea,
//					DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
//
//			// Required tank capacity = expected liters * number of persons
//			expectedWaterTankCapacity = waterTankCapacityExpected
//					.multiply(noOfPersons.setScale(0, BigDecimal.ROUND_HALF_UP));
//			expectedWaterTankCapacity = expectedWaterTankCapacity.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
//					DcrConstants.ROUNDMODE_MEASUREMENTS);
//
//			// Get provided tank capacity from plan
//			BigDecimal providedWaterTankCapacity = pl.getUtility().getWaterTankCapacity();
//			providedWaterTankCapacity = providedWaterTankCapacity.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
//					DcrConstants.ROUNDMODE_MEASUREMENTS);
//
//			// Validate whether provided capacity meets the requirement
//			if (providedWaterTankCapacity.compareTo(expectedWaterTankCapacity) >= 0) {
//				valid = true;
//			}
//
//			// Process result and update scrutiny report
//			processWaterTankCapacity(pl, "", subRule, subRuleDesc, expectedWaterTankCapacity, valid);
//		}
//
//		return pl;
//	}
//
//    /**
//     * Add the rule check result to scrutiny report based on validation
//     */
//    private void processWaterTankCapacity(Plan plan, String rule, String subRule, String subRuleDesc,
//            BigDecimal expectedWaterTankCapacity, Boolean valid) {
//        if (expectedWaterTankCapacity.compareTo(BigDecimal.valueOf(0)) > 0) {
//            if (valid) {
//                setReportOutputDetails(plan, subRule, WATER_TANK_CAPACITY,
//                        expectedWaterTankCapacity.toString(),
//                        plan.getUtility().getWaterTankCapacity().toString(),
//                        Result.Accepted.getResultVal());
//            } else {
//                setReportOutputDetails(plan, subRule, WATER_TANK_CAPACITY,
//                        expectedWaterTankCapacity.toString() + IN_LITRE,
//                        plan.getUtility().getWaterTankCapacity().toString() + IN_LITRE,
//                        Result.Not_Accepted.getResultVal());
//            }
//        }
//    }
//
//    /**
//     * Helper method to populate scrutiny report
//     */
//    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
//            String status) {
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
    public Plan process(Plan pl) {
        initializeScrutinyDetail();

        Optional<MdmsFeatureRule> matchedRule = getWaterTankRule(pl);
        if (!matchedRule.isPresent()) return pl;

        MdmsFeatureRule rule = matchedRule.get();

        if (pl.getUtility() != null && pl.getVirtualBuilding() != null
                && pl.getUtility().getWaterTankCapacity() != null) {

            BigDecimal expectedWaterTankCapacity = calculateExpectedTankCapacity(pl, rule);
            BigDecimal providedTankCapacity = pl.getUtility().getWaterTankCapacity()
                    .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

            boolean isValid = providedTankCapacity.compareTo(expectedWaterTankCapacity) >= 0;

            processWaterTankCapacity(pl, "", RULE_59_10_vii, RULE_59_10_vii_DESCRIPTION,
                    expectedWaterTankCapacity, isValid);
        }

        return pl;
    }

    private void initializeScrutinyDetail() {
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        scrutinyDetail.setKey("Common_Water tank capacity");
    }

    private Optional<MdmsFeatureRule> getWaterTankRule(Plan pl) {
        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.WATER_TANK_CAPACITY, false);
        return rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
    }

    private BigDecimal calculateExpectedTankCapacity(Plan pl, MdmsFeatureRule rule) {
        BigDecimal builtUpArea = pl.getVirtualBuilding().getTotalBuitUpArea();
        BigDecimal waterTankCapacityArea = rule.getWaterTankCapacityArea();
        BigDecimal waterTankCapacityExpected = rule.getWaterTankCapacityExpected();

        BigDecimal noOfPersons = builtUpArea.divide(
                waterTankCapacityArea, DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

        BigDecimal expectedCapacity = waterTankCapacityExpected
                .multiply(noOfPersons.setScale(0, BigDecimal.ROUND_HALF_UP));

        return expectedCapacity.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
    }

    private void processWaterTankCapacity(Plan plan, String rule, String subRule, String subRuleDesc,
                                          BigDecimal expectedWaterTankCapacity, Boolean valid) {
        if (expectedWaterTankCapacity.compareTo(BigDecimal.ZERO) > 0) {
            String expected = expectedWaterTankCapacity.toString() + IN_LITRE;
            String actual = plan.getUtility().getWaterTankCapacity().toString() + IN_LITRE;
            String status = valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal();
            setReportOutputDetails(plan, subRule, WATER_TANK_CAPACITY, expected, actual, status);
        }
    }

    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
                                         String status) {
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
