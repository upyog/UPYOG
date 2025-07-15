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
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.ElectricLine;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class OverheadElectricalLineService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(OverheadElectricalLineService.class);
    private static final String SUB_RULE_31 = "31";

    private static BigDecimal overheadVerticalDistance_11000 = BigDecimal.ZERO;
    private static BigDecimal overheadVerticalDistance_33000 = BigDecimal.ZERO;
    private static BigDecimal overheadHorizontalDistance_11000 = BigDecimal.ZERO;
    private static BigDecimal overheadHorizontalDistance_33000 = BigDecimal.ZERO;
    private static BigDecimal overheadVoltage_11000 = BigDecimal.ZERO;
    private static BigDecimal overheadVoltage_33000 = BigDecimal.ZERO;
    private static final String REMARKS = "Remarks";
    private static final String VOLTAGE = "Voltage";

   
	@Autowired
	CacheManagerMdms cache;

	/**
	 * Validates the presence of required attributes in electric lines.
	 *
	 * @param pl The plan object containing electric line data.
	 * @return The updated plan object with validation errors (if any) added.
	 */
	@Override
	public Plan validate(Plan pl) {
	    HashMap<String, String> errors = new HashMap<>();
	    for (ElectricLine el : pl.getElectricLine()) {
	        if (el.getPresentInDxf()) {
	            if (el.getVoltage() == null) {
	                errors.put(DcrConstants.VOLTAGE, edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
	                        new String[]{DcrConstants.VOLTAGE}, LocaleContextHolder.getLocale()));
	            } else if (el.getHorizontalDistance() == null && el.getVerticalDistance() == null) {
	                errors.put(DcrConstants.ELECTRICLINE_DISTANCE, edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
	                        new String[]{DcrConstants.ELECTRICLINE_DISTANCE}, LocaleContextHolder.getLocale()));
	            }
	        }
	    }
	    pl.addErrors(errors);
	    return pl;
	}
	
	/**
	 * Processes overhead electric line scrutiny by validating clearances and setting report details.
	 *
	 * @param pl The plan object to be processed.
	 * @return The updated plan object with scrutiny results added.
	 */

	@Override
	public Plan process(Plan pl) {
	    validate(pl);
	    setupScrutinyDetail();

	    loadOverheadRules(pl);

	    for (ElectricLine el : pl.getElectricLine()) {
	        if (el.getPresentInDxf() && el.getVoltage() != null && el.getVoltage().compareTo(BigDecimal.ZERO) > 0 &&
	                (el.getHorizontalDistance() != null || el.getVerticalDistance() != null)) {

	            boolean horizontalPassed = false;
	            String expected = "", actual = "";

	            if (el.getHorizontalDistance() != null) {
	                actual = el.getHorizontalDistance() + DcrConstants.IN_METER;
	                expected = getExpectedHorizontalDistance(el.getVoltage());

	                BigDecimal expectedVal = getHorizontalThreshold(el.getVoltage());
	                if (el.getHorizontalDistance().compareTo(expectedVal) >= 0) {
	                    horizontalPassed = true;
	                }
	            }

	            if (horizontalPassed) {
	                setReportOutputDetails(pl, SUB_RULE_31,
	                        DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE + el.getNumber(),
	                        expected, actual,
	                        Result.Accepted.getResultVal(), "", el.getVoltage() + DcrConstants.IN_KV);
	            } else {
//	                boolean verticalPassed = processVerticalDistance(el, pl, "", "", overheadVoltage_11000,
//	                        overheadVerticalDistance_11000, overheadVoltage_33000);

//	                String result = verticalPassed ? Result.Verify.getResultVal() : Result.Not_Accepted.getResultVal();
//	                String remarks = verticalPassed
//	                        ? String.format(DcrConstants.HORIZONTAL_ELINE_DISTANCE_NOC, el.getNumber())
//	                        : "";
//
//	                setReportOutputDetails(pl, SUB_RULE_31,
//	                        DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE + el.getNumber(),
//	                        expected, actual, result, remarks, el.getVoltage() + DcrConstants.IN_KV);
//
//	                if (verticalPassed) {
//	                    HashMap<String, String> noc = new HashMap<>();
//	                    noc.put(DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE + el.getNumber(),
//	                            DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE_NOC);
//	                    pl.addNocs(noc);
//	                }
	            }
	        }
	    }
	    return pl;
	}


/**
 * Initializes the scrutiny detail section for overhead electric line processing.
 */
	private void setupScrutinyDetail() {
	    scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.setKey(Common_OverHead_Electric_Line);
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	    scrutinyDetail.addColumnHeading(3, REQUIRED);
	    scrutinyDetail.addColumnHeading(4, PROVIDED);
	    scrutinyDetail.addColumnHeading(6, VOLTAGE);
	    scrutinyDetail.addColumnHeading(7, REMARKS);
	    scrutinyDetail.addColumnHeading(8, STATUS);
	}

	/**
	 * Loads the permissible threshold values for electric line voltage and distance from rule cache.
	 *
	 * @param pl The plan object used to fetch the rules.
	 */
	private void loadOverheadRules(Plan pl) {
	    List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.OVERHEAD_ELECTRICAL_LINE_SERVICE, false);
	    Optional<MdmsFeatureRule> matched = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

	    if (matched.isPresent()) {
	        MdmsFeatureRule rule = matched.get();
	        overheadVerticalDistance_11000 = rule.getOverheadVerticalDistance_11000();
	        overheadVerticalDistance_33000 = rule.getOverheadVerticalDistance_33000();
	        overheadHorizontalDistance_11000 = rule.getOverheadHorizontalDistance_11000();
	        overheadHorizontalDistance_33000 = rule.getOverheadHorizontalDistance_33000();
	        overheadVoltage_11000 = rule.getOverheadVoltage_11000();
	        overheadVoltage_33000 = rule.getOverheadVoltage_33000();
	    }
	}

	/**
	 * Computes the expected horizontal clearance for a given voltage as a formatted string.
	 *
	 * @param voltage The voltage of the electric line.
	 * @return A string representing the required clearance in meters.
	 */
	private String getExpectedHorizontalDistance(BigDecimal voltage) {
	    return getHorizontalThreshold(voltage).toString() + DcrConstants.IN_METER;
	}

	/**
	 * Returns the horizontal clearance threshold based on the given voltage.
	 *
	 * @param voltage The voltage of the electric line.
	 * @return The minimum permissible horizontal clearance.
	 */
	private BigDecimal getHorizontalThreshold(BigDecimal voltage) {
	    if (voltage.compareTo(overheadVoltage_11000) < 0) {
	        return overheadHorizontalDistance_11000;
	    } else if (voltage.compareTo(overheadVoltage_33000) <= 0) {
	        return overheadHorizontalDistance_33000;
	    } else {
	        double increment = 0.3 * Math.ceil(voltage.subtract(overheadVoltage_33000)
	                .divide(overheadVoltage_33000, 2, RoundingMode.HALF_UP).doubleValue());
	        return BigDecimal.valueOf(overheadHorizontalDistance_33000.doubleValue() + increment);
	    }
	}
	

	/**
	 * Adds the scrutiny result for an electric line check to the report output.
	 *
	 * @param pl       The plan object to add the report output to.
	 * @param ruleNo   Rule number under which the check falls.
	 * @param ruleDesc Description of the rule/check performed.
	 * @param expected The expected (permissible) value.
	 * @param actual   The actual provided value from the plan.
	 * @param status   Status of the check (e.g., Accepted, Not Accepted).
	 * @param remarks  Any additional remarks or required action.
	 * @param voltage  Voltage level of the electric line.
	 */
	
  private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual, String status, String remarks, String voltage) {
  Map<String, String> details = new HashMap<>();
  details.put(RULE_NO, ruleNo);
  details.put(DESCRIPTION, ruleDesc);
  details.put(REQUIRED, expected);
  details.put(PROVIDED, actual);
  details.put(REMARKS, remarks);
  details.put(VOLTAGE, voltage);
  details.put(STATUS, status);
  scrutinyDetail.getDetail().add(details);
  pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
}



    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}
