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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Ventilation_Citya extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Ventilation_Citya.class);
	private static final String RULE_43 = "43";
	public static final String LIGHT_VENTILATION_DESCRIPTION = "Light and Ventilation";

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}
	
	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;

	@Override
	public Plan process(Plan pl) {
	    for (Block b : pl.getBlocks()) {

	        // Create scrutiny detail for common room ventilation
	        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	        scrutinyDetail.setKey("Common_Ventilation");
	        scrutinyDetail.addColumnHeading(1, RULE_NO);
	        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	        scrutinyDetail.addColumnHeading(3, REQUIRED);
	        scrutinyDetail.addColumnHeading(4, PROVIDED);
	        scrutinyDetail.addColumnHeading(5, STATUS);

	        // Create scrutiny detail for bathroom ventilation
	        ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
	        scrutinyDetail1.setKey("Bath_Ventilation");
	        scrutinyDetail1.addColumnHeading(1, RULE_NO);
	        scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
	        scrutinyDetail1.addColumnHeading(3, REQUIRED);
	        scrutinyDetail1.addColumnHeading(4, PROVIDED);
	        scrutinyDetail1.addColumnHeading(5, STATUS);

	        // Initialize ventilation requirements
	        BigDecimal ventilationValueOne = BigDecimal.ZERO;
	        BigDecimal ventilationValueTwo = BigDecimal.ZERO;

	        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
	        String feature = MdmsFeatureConstants.VENTILATION;

	        Map<String, Object> params = new HashMap<>();

	        // Identify occupancy type — currently only checking for Residential
	       
	        params.put("feature", feature);
	        params.put("occupancy", occupancyName);

	        // Get permissible values for the ventilation rules from MDMS config
	        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
	        ArrayList<String> valueFromColumn = new ArrayList<>();
	        valueFromColumn.add(EdcrRulesMdmsConstants.VENTILATION_VALUE_ONE);
	        valueFromColumn.add(EdcrRulesMdmsConstants.VENTILATION_VALUE_TWO);

	        List<Map<String, Object>> permissibleValue = new ArrayList<>();
	        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
	        LOG.info("permissibleValue" + permissibleValue);

	        // Extract values if they exist
	        if (!permissibleValue.isEmpty() &&
	            permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.VENTILATION_VALUE_ONE)) {
	            ventilationValueOne = BigDecimal.valueOf(Double.valueOf(
	                    permissibleValue.get(0).get(EdcrRulesMdmsConstants.VENTILATION_VALUE_ONE).toString()));
	            ventilationValueTwo = BigDecimal.valueOf(Double.valueOf(
	                    permissibleValue.get(0).get(EdcrRulesMdmsConstants.VENTILATION_VALUE_TWO).toString()));
	        }

	        // Process each floor in the building
	        if (b.getBuilding() != null && b.getBuilding().getFloors() != null && !b.getBuilding().getFloors().isEmpty()) {
	            for (Floor f : b.getBuilding().getFloors()) {

	                // Details map for general ventilation
	                Map<String, String> details = new HashMap<>();
	                details.put(RULE_NO, RULE_43);
	                details.put(DESCRIPTION, LIGHT_VENTILATION_DESCRIPTION);

	                // Details map for bath ventilation
	                Map<String, String> details1 = new HashMap<>();
	                details1.put(RULE_NO, RULE_43);
	                details1.put(DESCRIPTION, LIGHT_VENTILATION_DESCRIPTION);

	                // Check general (room) ventilation
	                if (f.getLightAndVentilation() != null && f.getLightAndVentilation().getMeasurements() != null
	                        && !f.getLightAndVentilation().getMeasurements().isEmpty()) {

	                    // Sum ventilation areas and carpet areas
	                    BigDecimal totalVentilationArea = f.getLightAndVentilation().getMeasurements().stream()
	                            .map(Measurement::getArea)
	                            .reduce(BigDecimal.ZERO, BigDecimal::add);

	                    BigDecimal totalCarpetArea = f.getOccupancies().stream()
	                            .map(Occupancy::getCarpetArea)
	                            .reduce(BigDecimal.ZERO, BigDecimal::add);

	                    // Validation: Check if ventilation is at least 1/N of carpet area
	                    if (totalVentilationArea.compareTo(BigDecimal.ZERO) > 0) {
	                        BigDecimal requiredVentilation = totalCarpetArea.divide(ventilationValueOne, 2, BigDecimal.ROUND_HALF_UP);
	                        if (totalVentilationArea.compareTo(requiredVentilation) >= 0) {
	                            details.put(REQUIRED, "Minimum 1/" + ventilationValueOne + "th of the floor area ");
	                            details.put(PROVIDED, "Ventilation area " + totalVentilationArea + " of Carpet Area " + totalCarpetArea + " at floor " + f.getNumber());
	                            details.put(STATUS, Result.Accepted.getResultVal());
	                        } else {
	                            details.put(REQUIRED, "Minimum 1/" + ventilationValueOne + "th of the floor area ");
	                            details.put(PROVIDED, "Ventilation area " + totalVentilationArea + " of Carpet Area " + totalCarpetArea + " at floor " + f.getNumber());
	                            details.put(STATUS, Result.Not_Accepted.getResultVal());
	                        }

	                        scrutinyDetail.getDetail().add(details);
	                        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	                    }
	                }

	                // Check bathroom ventilation
	               /* if (f.getBathVentilaion() != null && f.getBathVentilaion().getMeasurements() != null
	                        && !f.getBathVentilaion().getMeasurements().isEmpty()) {

	                    BigDecimal totalVentilationArea = f.getBathVentilaion().getMeasurements().stream()
	                            .map(Measurement::getArea)
	                            .reduce(BigDecimal.ZERO, BigDecimal::add);

	                    // Validate against required area
	                    if (totalVentilationArea.compareTo(ventilationValueTwo) >= 0) {
	                        details1.put(REQUIRED, ventilationValueTwo.toString());
	                        details1.put(PROVIDED, "Bath Ventilation area " + totalVentilationArea + " at floor " + f.getNumber());
	                        details1.put(STATUS, Result.Accepted.getResultVal());
	                    } else {
	                        details1.put(REQUIRED, ventilationValueTwo.toString());
	                        details1.put(PROVIDED, "Bath Ventilation area " + totalVentilationArea + " at floor " + f.getNumber());
	                        details1.put(STATUS, Result.Not_Accepted.getResultVal());
	                    }

	                    scrutinyDetail1.getDetail().add(details1);
	                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail1);

	                } else {
	                    // If no bathroom ventilation measurements defined
	                    details1.put(REQUIRED, ventilationValueTwo.toString());
	                    details1.put(PROVIDED, "Bath Ventilation area not defined in floor " + f.getNumber());
	                    details1.put(STATUS, Result.Not_Accepted.getResultVal());
	                    scrutinyDetail1.getDetail().add(details1);
	                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail1);
	                } */
	            } 
	        }
	    }

	    return pl;
	}


	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
