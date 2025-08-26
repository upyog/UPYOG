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
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class OverheadElectricalLineService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(OverheadElectricalLineService.class);
	

	@Autowired
	MDMSCacheManager cache;

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

        // Load rules into local variables
        OverheadElectricalLineServiceRequirement rule = loadOverheadRules(pl);

        for (ElectricLine el : pl.getElectricLine()) {
            if (el.getPresentInDxf() && el.getVoltage() != null && el.getVoltage().compareTo(BigDecimal.ZERO) > 0 &&
                    (el.getHorizontalDistance() != null || el.getVerticalDistance() != null)) {

                boolean horizontalPassed = false;
                String expected = "", actual = "";

                if (el.getHorizontalDistance() != null) {
                    actual = el.getHorizontalDistance() + DcrConstants.IN_METER;
                    expected = getExpectedHorizontalDistance(el.getVoltage(), rule);

                    BigDecimal expectedVal = getHorizontalThreshold(el.getVoltage(), rule);
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
	 private OverheadElectricalLineServiceRequirement loadOverheadRules(Plan pl) {
	        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.OVERHEAD_ELECTRICAL_LINE_SERVICE.getValue(), false);
	        Optional<OverheadElectricalLineServiceRequirement> matchedRule = rules.stream()
	                .filter(OverheadElectricalLineServiceRequirement.class::isInstance)
	                .map(OverheadElectricalLineServiceRequirement.class::cast)
	                .findFirst();
	        return matchedRule.orElse(new OverheadElectricalLineServiceRequirement());
	    }


	/**
	 * Computes the expected horizontal clearance for a given voltage as a formatted string.
	 *
	 * @param voltage The voltage of the electric line.
	 * @return A string representing the required clearance in meters.
	 */
	private String getExpectedHorizontalDistance(BigDecimal voltage,
            OverheadElectricalLineServiceRequirement rule) {
return getHorizontalThreshold(voltage, rule).toString() + DcrConstants.IN_METER;
}

	/**
	 * Returns the horizontal clearance threshold based on the given voltage.
	 *
	 * @param voltage The voltage of the electric line.
	 * @return The minimum permissible horizontal clearance.
	 */
	private BigDecimal getHorizontalThreshold(BigDecimal voltage,
            OverheadElectricalLineServiceRequirement rule) {
		if (voltage.compareTo(rule.getOverheadVoltage_11000()) < 0) {
		return rule.getOverheadHorizontalDistance_11000();
		} else if (voltage.compareTo(rule.getOverheadVoltage_33000()) <= 0) {
		return rule.getOverheadHorizontalDistance_33000();
		} else {
		double increment = 0.3 * Math.ceil(voltage.subtract(rule.getOverheadVoltage_33000())
		.divide(rule.getOverheadVoltage_33000(), 2, RoundingMode.HALF_UP).doubleValue());
		return BigDecimal.valueOf(rule.getOverheadHorizontalDistance_33000().doubleValue() + increment);
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
	  ReportScrutinyDetail detail = new ReportScrutinyDetail();
	  detail.setRuleNo(ruleNo);
	  detail.setDescription(ruleDesc);
	  detail.setRemarks(remarks);
	  detail.setRequired(expected);
	  detail.setProvided(actual);
	  detail.setStatus(status);
	  detail.setVoltage(voltage);

	  Map<String, String> details = mapReportDetails(detail);
	  addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
}



    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}
