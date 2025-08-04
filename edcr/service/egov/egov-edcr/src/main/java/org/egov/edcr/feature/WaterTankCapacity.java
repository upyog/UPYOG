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

import static org.egov.edcr.constants.CommonFeatureConstants.EMPTY_STRING;
import static org.egov.edcr.constants.CommonKeyConstants.COMMON_WATER_TANK_CAPACITY;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
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
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WaterTankCapacity extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(WaterTankCapacity.class);
    /**
     * Validates the building plan for water tank capacity requirements.
     * Currently performs no validation and returns the plan as-is.
     *
     * @param pl The building plan to validate
     * @return The unmodified plan
     */
    @Override
    public Plan validate(Plan pl) {
        // No specific validation logic added; returning the input plan as-is.
        return pl;
    }

    @Autowired
	MDMSCacheManager cache;

    /**
     * Processes water tank capacity requirements for the building plan.
     * Initializes scrutiny details, fetches water tank rules from MDMS,
     * calculates expected tank capacity based on built-up area, and validates
     * against provided capacity.
     *
     * @param pl The building plan to process
     * @return The processed plan with scrutiny details added
     */
    @Override
    public Plan process(Plan pl) {
        initializeScrutinyDetail();

        Optional<WaterTankCapacityRequirement> matchedRule = getWaterTankRule(pl);
        if (!matchedRule.isPresent()) return pl;

        WaterTankCapacityRequirement rule = matchedRule.get();

        if (pl.getUtility() != null && pl.getVirtualBuilding() != null
                && pl.getUtility().getWaterTankCapacity() != null) {

            BigDecimal expectedWaterTankCapacity = calculateExpectedTankCapacity(pl, rule);
            BigDecimal providedTankCapacity = pl.getUtility().getWaterTankCapacity()
                    .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

            boolean isValid = providedTankCapacity.compareTo(expectedWaterTankCapacity) >= 0;

            processWaterTankCapacity(pl, EMPTY_STRING, RULE_59_10_vii, RULE_59_10_vii_DESCRIPTION,
                    expectedWaterTankCapacity, isValid);
        }

        return pl;
    }

    /**
     * Initializes the scrutiny detail object for water tank capacity validation reporting.
     * Sets up column headings and key for the water tank capacity scrutiny report.
     */
    private void initializeScrutinyDetail() {
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        scrutinyDetail.setKey(COMMON_WATER_TANK_CAPACITY);
    }

    /**
     * Retrieves water tank capacity requirement rules from MDMS cache.
     * Fetches the first matching water tank capacity requirement rule based on plan configuration.
     *
     * @param pl The building plan containing configuration details
     * @return Optional containing WaterTankCapacityRequirement rule if found, empty otherwise
     */
    private Optional<WaterTankCapacityRequirement> getWaterTankRule(Plan pl) {
    	 List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.WATER_TANK_CAPACITY.toString(), false);
        return rules.stream()
             .filter(WaterTankCapacityRequirement.class::isInstance)
             .map(WaterTankCapacityRequirement.class::cast)
             .findFirst();
    }

    /**
     * Calculates the expected water tank capacity based on built-up area and occupancy.
     * Uses the formula: (built-up area / area per person) * capacity per person
     * to determine minimum required tank capacity.
     *
     * @param pl The building plan containing built-up area information
     * @param rule The water tank capacity requirement rule containing calculation parameters
     * @return Expected minimum water tank capacity in liters
     */
    private BigDecimal calculateExpectedTankCapacity(Plan pl, WaterTankCapacityRequirement rule) {
        BigDecimal builtUpArea = pl.getVirtualBuilding().getTotalBuitUpArea();
        BigDecimal waterTankCapacityArea = rule.getWaterTankCapacityArea();
        BigDecimal waterTankCapacityExpected = rule.getWaterTankCapacityExpected();

        BigDecimal noOfPersons = builtUpArea.divide(
                waterTankCapacityArea, DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

        BigDecimal expectedCapacity = waterTankCapacityExpected
                .multiply(noOfPersons.setScale(0, BigDecimal.ROUND_HALF_UP));

        return expectedCapacity.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
    }

    /**
     * Processes water tank capacity validation and generates scrutiny details.
     * Compares expected capacity with provided capacity and creates appropriate
     * scrutiny report entries with compliance status.
     *
     * @param plan The building plan
     * @param rule The main rule identifier (currently unused)
     * @param subRule The specific sub-rule identifier
     * @param subRuleDesc The sub-rule description
     * @param expectedWaterTankCapacity The calculated minimum required capacity
     * @param valid Whether the provided capacity meets requirements
     */
    private void processWaterTankCapacity(Plan plan, String rule, String subRule, String subRuleDesc,
                                          BigDecimal expectedWaterTankCapacity, Boolean valid) {
        if (expectedWaterTankCapacity.compareTo(BigDecimal.ZERO) > 0) {
            String expected = expectedWaterTankCapacity.toString() + IN_LITRE;
            String actual = plan.getUtility().getWaterTankCapacity().toString() + IN_LITRE;
            String status = valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal();
            setReportOutputDetails(plan, subRule, WATER_TANK_CAPACITY, expected, actual, status);
        }
    }

    /**
     * Adds water tank capacity validation results to the scrutiny report.
     * Creates a detailed report entry with rule information, requirements,
     * and compliance status.
     *
     * @param pl The building plan
     * @param ruleNo The rule number being validated
     * @param ruleDesc The rule description
     * @param expected The expected/required capacity value
     * @param actual The actual/provided capacity value
     * @param status The compliance status (Accepted/Not_Accepted)
     */
    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
                                         String status) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(ruleNo);
        detail.setDescription(ruleDesc);
        detail.setRequired(expected);
        detail.setProvided(actual);
        detail.setStatus(status);

        Map<String, String> details = mapReportDetails(detail);
        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
    }

    /**
     * Returns amendment dates for water tank capacity rules.
     * Currently returns an empty map as no amendments are defined.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
