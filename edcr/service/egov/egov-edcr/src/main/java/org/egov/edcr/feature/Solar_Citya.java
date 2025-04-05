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

import static org.egov.edcr.utility.DcrConstants.OBJECTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.RULE109;
import static org.egov.edcr.utility.DcrConstants.SOLAR_SYSTEM;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class Solar_Citya extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Solar_Citya.class);

	// Constants for rule descriptions and identifiers
    private static final String SUB_RULE_109_C_DESCRIPTION = "Solar Assisted Water Heating / Lighting system ";
    private static final String SUB_RULE_109_C = "109-C";
    
    // Static variables to hold rule values
    private static BigDecimal solarValueOne = BigDecimal.ZERO;
    private static BigDecimal solarValueTwo = BigDecimal.ZERO;
    
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    // Fetch permissible solar rule values from MDMS
    private Map<String, BigDecimal> fetchSolarValues(Plan pl) {
        String occupancyName = null;
		String feature = MdmsFeatureConstants.SOLAR;
        Map<String, Object> params = new HashMap<>();
        
        // Check occupancy type (only A i.e., residential considered here)
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // Define rule fields to fetch
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.SOLAR_VALUE_ONE);
        valueFromColumn.add(EdcrRulesMdmsConstants.SOLAR_VALUE_TWO);

		List<Map<String, Object>> permissibleValue = new ArrayList<>();
		
		// Fetch values from MDMS
		permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
		LOG.info("permissibleValue" + permissibleValue);

        // Extract and assign values if present
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.SOLAR_VALUE_ONE)) {
        	solarValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SOLAR_VALUE_ONE).toString()));
        	solarValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SOLAR_VALUE_TWO).toString()));
		}
        
        // Return the values in a map
        Map<String, BigDecimal> solarValues = new HashMap<>();
        solarValues.put("solarValueOne", solarValueOne);
        solarValues.put("solarValueTwo", solarValueTwo);
        return solarValues;
    }

    @Override
    public Plan validate(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        
        // Fetch rule values before validation
        Map<String, BigDecimal> solarValues = fetchSolarValues(pl);
        solarValueOne = solarValues.get("solarValueOne");
        solarValueTwo = solarValues.get("solarValueTwo"); 

        // Validate solar provision based on occupancy and built-up area
        if (pl != null && pl.getUtility() != null) {
            if (pl.getVirtualBuilding() != null && !pl.getVirtualBuilding().getOccupancies().isEmpty()) {
                for (OccupancyType occupancyType : pl.getVirtualBuilding().getOccupancies()) {

                    // For A1 occupancy and built-up area > valueOne, solar must be defined
                    if (occupancyType.equals(OccupancyType.OCCUPANCY_A1)
                            && pl.getVirtualBuilding().getTotalBuitUpArea() != null
                            && pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(solarValueOne) > 0
                            && pl.getUtility().getSolar().isEmpty()) {

                        errors.put(SOLAR_SYSTEM,
                                edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] {
                                        OBJECTNOTDEFINED }, LocaleContextHolder.getLocale()));
                        pl.addErrors(errors);
                        break;

                    // For other specified occupancies and built-up area > valueTwo
                    } else if ((occupancyType.equals(OccupancyType.OCCUPANCY_A4)
                            || occupancyType.equals(OccupancyType.OCCUPANCY_A2) ||
                            occupancyType.equals(OccupancyType.OCCUPANCY_A3) || occupancyType.equals(OccupancyType.OCCUPANCY_C) ||
                            occupancyType.equals(OccupancyType.OCCUPANCY_C1) || occupancyType.equals(OccupancyType.OCCUPANCY_C2)
                            || occupancyType.equals(OccupancyType.OCCUPANCY_C3) || occupancyType.equals(OccupancyType.OCCUPANCY_D) ||
                            occupancyType.equals(OccupancyType.OCCUPANCY_D1) || occupancyType.equals(OccupancyType.OCCUPANCY_D2))
                            && pl.getVirtualBuilding().getTotalBuitUpArea() != null
                            && pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(solarValueTwo) > 0
                            && pl.getUtility().getSolar().isEmpty()) {

                        errors.put(SOLAR_SYSTEM,
                                edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] {
                                        SOLAR_SYSTEM }, LocaleContextHolder.getLocale()));
                        pl.addErrors(errors);
                        break;
                    }
                }
            }
        }

        return pl;
    }

    @Override
    public Plan process(Plan pl) {
        // Run validation first to ensure compliance
        validate(pl);

        // Setup scrutiny detail columns
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        scrutinyDetail.setKey("Common_Solar");

        // Rule details
        String rule = RULE109;
        String subRule = SUB_RULE_109_C;
        String subRuleDesc = SUB_RULE_109_C_DESCRIPTION;        
        
        // Fetch rule values again
        Map<String, BigDecimal> solarValues = fetchSolarValues(pl);
        solarValueOne = solarValues.get("solarValueOne");
        solarValueTwo = solarValues.get("solarValueTwo");  

        // Add scrutiny detail based on occupancy and built-up area
        if (pl.getVirtualBuilding() != null && !pl.getVirtualBuilding().getOccupancies().isEmpty()) {
            for (OccupancyType occupancyType : pl.getVirtualBuilding().getOccupancies()) {

                // For A1 occupancy
                if (occupancyType.equals(OccupancyType.OCCUPANCY_A1)
                        && pl.getVirtualBuilding().getTotalBuitUpArea() != null
                        && pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(solarValueOne) > 0) {
                    processSolar(pl, rule, subRule, subRuleDesc);
                    break;

                // For other occupancy types
                } else if ((occupancyType.equals(OccupancyType.OCCUPANCY_A4) || occupancyType.equals(OccupancyType.OCCUPANCY_A2)
                        || occupancyType.equals(OccupancyType.OCCUPANCY_A3) || occupancyType.equals(OccupancyType.OCCUPANCY_C) ||
                        occupancyType.equals(OccupancyType.OCCUPANCY_C1) || occupancyType.equals(OccupancyType.OCCUPANCY_C2) ||
                        occupancyType.equals(OccupancyType.OCCUPANCY_C3) || occupancyType.equals(OccupancyType.OCCUPANCY_D) ||
                        occupancyType.equals(OccupancyType.OCCUPANCY_D1) || occupancyType.equals(OccupancyType.OCCUPANCY_D2))
                        && pl.getVirtualBuilding().getTotalBuitUpArea() != null
                        && pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(solarValueTwo) > 0) {
                    processSolar(pl, rule, subRule, subRuleDesc);
                    break;
                }
            }
        }

        return pl;
    }

    // Helper method to generate scrutiny result based on solar presence
    private void processSolar(Plan pl, String rule, String subRule, String subRuleDesc) {
        if (!pl.getUtility().getSolar().isEmpty()) {
            setReportOutputDetailsWithoutOccupancy(pl, subRule, subRuleDesc, "", OBJECTDEFINED_DESC,
                    Result.Accepted.getResultVal());
            return;
        } else {
            setReportOutputDetailsWithoutOccupancy(pl, subRule, subRuleDesc, "", OBJECTNOTDEFINED_DESC,
                    Result.Not_Accepted.getResultVal());
            return;
        }
    }

    // Build and add a scrutiny detail row
    private void setReportOutputDetailsWithoutOccupancy(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
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
        // No amendments defined
        return new LinkedHashMap<>();
    }
}

