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
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.OBJECTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.RULE109;
import static org.egov.edcr.utility.DcrConstants.SOLAR_SYSTEM;

import java.math.BigDecimal;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class Solar extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Solar.class);

    @Autowired
	MDMSCacheManager cache;

    /**
     * Validates solar system requirements based on building occupancy type and built-up area.
     * Checks if solar systems are mandatory for buildings exceeding specified area thresholds
     * and adds validation errors if requirements are not met.
     *
     * @param pl The building plan to validate
     * @return The validated plan with any errors added
     */
    @Override
    public Plan validate(Plan pl) {
        Map<String, String> errors = new HashMap<>();

        Map<String, BigDecimal> solarValues = fetchSolarValues(pl);
        BigDecimal solarValueOne = solarValues.get(SOLAR_VALUE_ONE);
        BigDecimal solarValueTwo = solarValues.get(SOLAR_VALUE_TWO);

        if (pl != null && pl.getUtility() != null && pl.getVirtualBuilding() != null && !pl.getVirtualBuilding().getOccupancies().isEmpty()) {
            for (OccupancyType occupancyType : pl.getVirtualBuilding().getOccupancies()) {
                BigDecimal builtUpArea = pl.getVirtualBuilding().getTotalBuitUpArea();

                if (occupancyType.equals(OccupancyType.OCCUPANCY_A1)
                        && builtUpArea != null
                        && builtUpArea.compareTo(solarValueOne) > 0
                        && pl.getUtility().getSolar().isEmpty()) {

                    errors.put(SOLAR_SYSTEM, edcrMessageSource.getMessage(OBJECTNOTDEFINED,
                            new String[]{OBJECTNOTDEFINED}, LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                    break;

                } else if (isOtherOccupancy(occupancyType)
                        && builtUpArea != null
                        && builtUpArea.compareTo(solarValueTwo) > 0
                        && pl.getUtility().getSolar().isEmpty()) {

                    errors.put(SOLAR_SYSTEM, edcrMessageSource.getMessage(OBJECTNOTDEFINED,
                            new String[]{SOLAR_SYSTEM}, LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                    break;
                }
            }
        }

        return pl;
    }

    /**
     * Processes solar system requirements and generates scrutiny details for the report.
     * Validates the plan first, then checks occupancy types and built-up areas to determine
     * if solar system processing is required.
     *
     * @param pl The building plan to process
     * @return The processed plan with scrutiny details added
     */
    @Override
    public Plan process(Plan pl) {
        validate(pl); // Ensure rules are validated before processing
        initializeScrutinyDetail();

        String rule = RULE109;
        String subRule = SUB_RULE_109_C;
        String subRuleDesc = SUB_RULE_109_C_DESCRIPTION;

        Map<String, BigDecimal> solarValues = fetchSolarValues(pl);
        BigDecimal solarValueOne = solarValues.get(SOLAR_VALUE_ONE);
        BigDecimal solarValueTwo = solarValues.get(SOLAR_VALUE_TWO);

        if (pl.getVirtualBuilding() != null && !pl.getVirtualBuilding().getOccupancies().isEmpty()) {
            for (OccupancyType occupancyType : pl.getVirtualBuilding().getOccupancies()) {
                BigDecimal builtUpArea = pl.getVirtualBuilding().getTotalBuitUpArea();

                if (occupancyType.equals(OccupancyType.OCCUPANCY_A1)
                        && builtUpArea != null
                        && builtUpArea.compareTo(solarValueOne) > 0) {
                    processSolar(pl, rule, subRule, subRuleDesc);
                    break;

                } else if (isOtherOccupancy(occupancyType)
                        && builtUpArea != null
                        && builtUpArea.compareTo(solarValueTwo) > 0) {
                    processSolar(pl, rule, subRule, subRuleDesc);
                    break;
                }
            }
        }

        return pl;
    }

    /**
     * Fetches solar requirement values from MDMS cache based on plan configuration.
     * Retrieves threshold values for different occupancy types that determine when
     * solar systems become mandatory.
     *
     * @param pl The building plan containing configuration details
     * @return Map containing solarValueOne and solarValueTwo threshold values
     */
    private Map<String, BigDecimal> fetchSolarValues(Plan pl) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.SOLAR.getValue(), false);
        Optional<SolarRequirement> matchedRule = rules.stream()
            .filter(SolarRequirement.class::isInstance)
            .map(SolarRequirement.class::cast)
            .findFirst();

        BigDecimal solarValueOne = BigDecimal.ZERO;
        BigDecimal solarValueTwo = BigDecimal.ZERO;

        if (matchedRule.isPresent()) {
        	SolarRequirement rule = matchedRule.get();
            solarValueOne = rule.getSolarValueOne();
            solarValueTwo = rule.getSolarValueTwo();
        }

        Map<String, BigDecimal> solarValues = new HashMap<>();
        solarValues.put(SOLAR_VALUE_ONE, solarValueOne);
        solarValues.put(SOLAR_VALUE_TWO, solarValueTwo);
        return solarValues;
    }

    /**
     * Initializes the scrutiny detail object with column headings for solar system reporting.
     * Sets up the report structure with rule number, description, required, provided, and status columns.
     */
    private void initializeScrutinyDetail() {
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        scrutinyDetail.setKey(Common_Solar);
    }

    /**
     * Checks if the given occupancy type falls under "other occupancies" category.
     * Returns true for occupancy types A2, A3, A4, C, C1, C2, C3, D, D1, D2.
     *
     * @param occupancyType The occupancy type to check
     * @return true if occupancy type is in the other occupancies list, false otherwise
     */
    private boolean isOtherOccupancy(OccupancyType occupancyType) {
        return Arrays.asList(
                OccupancyType.OCCUPANCY_A2, OccupancyType.OCCUPANCY_A3, OccupancyType.OCCUPANCY_A4,
                OccupancyType.OCCUPANCY_C, OccupancyType.OCCUPANCY_C1, OccupancyType.OCCUPANCY_C2,
                OccupancyType.OCCUPANCY_C3, OccupancyType.OCCUPANCY_D, OccupancyType.OCCUPANCY_D1,
                OccupancyType.OCCUPANCY_D2
        ).contains(occupancyType);
    }

    /**
     * Processes solar system requirements and adds results to scrutiny details.
     * Determines compliance status based on whether solar systems are defined in the plan.
     *
     * @param pl The building plan being processed
     * @param rule The main rule identifier
     * @param subRule The specific sub-rule identifier
     * @param subRuleDesc The description of the sub-rule
     */
    private void processSolar(Plan pl, String rule, String subRule, String subRuleDesc) {
        String status = pl.getUtility().getSolar().isEmpty()
                ? Result.Not_Accepted.getResultVal()
                : Result.Accepted.getResultVal();
        String provided = pl.getUtility().getSolar().isEmpty()
                ? OBJECTNOTDEFINED_DESC
                : OBJECTDEFINED_DESC;

        setReportOutputDetailsWithoutOccupancy(pl, subRule, subRuleDesc, EMPTY_STRING, provided, status);
    }

    /**
     * Adds solar system validation results to the scrutiny report without occupancy details.
     * Creates a detailed report entry with rule information, requirements, and compliance status.
     *
     * @param pl The building plan
     * @param ruleNo The rule number
     * @param ruleDesc The rule description
     * @param expected The expected requirement (empty for solar)
     * @param actual The actual provision status
     * @param status The compliance status (Accepted/Not_Accepted)
     */
    private void setReportOutputDetailsWithoutOccupancy(Plan pl, String ruleNo, String ruleDesc, String expected,
                                                        String actual, String status) {
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
     * Returns amendment dates for solar system rules.
     * Currently returns an empty map as no amendments are defined.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    @Override
    public Map<String, Date> getAmendments() {
        // No amendments defined
        return new LinkedHashMap<>();
    }
}

