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
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SolarRequirement;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class Solar extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Solar.class);

	// Constants for rule descriptions and identifiers
    private static final String SUB_RULE_109_C_DESCRIPTION = "Solar Assisted Water Heating / Lighting system ";
    private static final String SUB_RULE_109_C = "109-C";
    
    // Static variables to hold rule values
    private static BigDecimal solarValueOne = BigDecimal.ZERO;
    private static BigDecimal solarValueTwo = BigDecimal.ZERO;
    
    
    @Autowired
	MDMSCacheManager cache;

    @Override
    public Plan validate(Plan pl) {
        Map<String, String> errors = new HashMap<>();

        Map<String, BigDecimal> solarValues = fetchSolarValues(pl);
        BigDecimal solarValueOne = solarValues.get("solarValueOne");
        BigDecimal solarValueTwo = solarValues.get("solarValueTwo");

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

    @Override
    public Plan process(Plan pl) {
        validate(pl); // Ensure rules are validated before processing
        initializeScrutinyDetail();

        String rule = RULE109;
        String subRule = SUB_RULE_109_C;
        String subRuleDesc = SUB_RULE_109_C_DESCRIPTION;

        Map<String, BigDecimal> solarValues = fetchSolarValues(pl);
        BigDecimal solarValueOne = solarValues.get("solarValueOne");
        BigDecimal solarValueTwo = solarValues.get("solarValueTwo");

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
        solarValues.put("solarValueOne", solarValueOne);
        solarValues.put("solarValueTwo", solarValueTwo);
        return solarValues;
    }

    private void initializeScrutinyDetail() {
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        scrutinyDetail.setKey(Common_Solar);
    }

    private boolean isOtherOccupancy(OccupancyType occupancyType) {
        return Arrays.asList(
                OccupancyType.OCCUPANCY_A2, OccupancyType.OCCUPANCY_A3, OccupancyType.OCCUPANCY_A4,
                OccupancyType.OCCUPANCY_C, OccupancyType.OCCUPANCY_C1, OccupancyType.OCCUPANCY_C2,
                OccupancyType.OCCUPANCY_C3, OccupancyType.OCCUPANCY_D, OccupancyType.OCCUPANCY_D1,
                OccupancyType.OCCUPANCY_D2
        ).contains(occupancyType);
    }

    private void processSolar(Plan pl, String rule, String subRule, String subRuleDesc) {
        String status = pl.getUtility().getSolar().isEmpty()
                ? Result.Not_Accepted.getResultVal()
                : Result.Accepted.getResultVal();
        String provided = pl.getUtility().getSolar().isEmpty()
                ? OBJECTNOTDEFINED_DESC
                : OBJECTDEFINED_DESC;

        setReportOutputDetailsWithoutOccupancy(pl, subRule, subRuleDesc, "", provided, status);
    }

    private void setReportOutputDetailsWithoutOccupancy(Plan pl, String ruleNo, String ruleDesc, String expected,
                                                        String actual, String status) {
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
        // No amendments defined
        return new LinkedHashMap<>();
    }
}

