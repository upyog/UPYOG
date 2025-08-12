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

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_SA;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.G;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;

import java.math.BigDecimal;
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
import org.springframework.stereotype.Service;

@Service
public class Plantation extends FeatureProcess {

    private static final Logger LOGGER = LogManager.getLogger(Plantation.class);

    @Autowired
  	MDMSCacheManager cache;
    
    @Override
    public Plan validate(Plan pl) {
        return null;
    }

    /**
     * Validates and processes plantation area compliance based on occupancy type and subtype.
     *
     * @param pl The plan object containing plantation and plot details.
     * @return The updated plan object after processing plantation scrutiny.
     */
    @Override
    public Plan process(Plan pl) {
    	validate(pl);
    	ScrutinyDetail scrutinyDetail = createScrutinyDetail();
    	ReportScrutinyDetail details = createInitialDetails();

    	BigDecimal totalArea = getTotalPlantationArea(pl);
    	BigDecimal plotArea = getPlotArea(pl);
    	String type = getOccupancyType(pl);
    	String subType = getOccupancySubType(pl);

    	BigDecimal plantationPer = calculatePlantationPercentage(totalArea, plotArea);

    	if (isRelevantOccupancyType(type, subType) && plantationPer.compareTo(BigDecimal.ZERO) > 0) {
    		processPlantationRule(pl, plantationPer, scrutinyDetail, details);
    	}

    	return pl;
    }

    /**
     * Creates and returns an initialized ScrutinyDetail object for plantation scrutiny.
     *
     * @return A ScrutinyDetail object with predefined column headings and key.
     */
    private ScrutinyDetail createScrutinyDetail() {
    	ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
    	scrutinyDetail.setKey(Common_Plantation);
    	scrutinyDetail.addColumnHeading(1, RULE_NO);
    	scrutinyDetail.addColumnHeading(2, DESCRIPTION);
    	scrutinyDetail.addColumnHeading(3, REQUIRED);
    	scrutinyDetail.addColumnHeading(4, PROVIDED);
    	scrutinyDetail.addColumnHeading(5, STATUS);
    	return scrutinyDetail;
    }

    /**
     * Creates and returns a map containing initial rule number and description for plantation.
     *
     * @return A map with rule number and rule description.
     */
    private ReportScrutinyDetail createInitialDetails() {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(RULE_32);
		detail.setDescription(PLANTATION_TREECOVER_DESCRIPTION);
		return detail;
	}

    /**
     * Calculates and returns the total plantation area from the plan.
     *
     * @param pl The plan containing plantation measurements.
     * @return The total area of plantation as BigDecimal.
     */
    private BigDecimal getTotalPlantationArea(Plan pl) {
    	BigDecimal totalArea = BigDecimal.ZERO;
    	if (pl.getPlantation() != null && pl.getPlantation().getPlantations() != null) {
    		for (Measurement m : pl.getPlantation().getPlantations()) {
    			totalArea = totalArea.add(m.getArea());
    		}
    	}
    	return totalArea;
    }

    /**
     * Returns the plot area from the plan.
     *
     * @param pl The plan object.
     * @return Plot area as BigDecimal, or ZERO if not available.
     */
    private BigDecimal getPlotArea(Plan pl) {
    	return (pl.getPlot() != null) ? pl.getPlot().getArea() : BigDecimal.ZERO;
    }

    /**
     * Retrieves the occupancy type code from the plan's most restrictive FAR helper.
     *
     * @param pl The plan object.
     * @return Occupancy type code as String, or empty string if not available.
     */
    private String getOccupancyType(Plan pl) {
    	if (pl.getVirtualBuilding() != null &&
    			pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null &&
    			pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType() != null) {
    		return pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode();
    	}
    	return "";
    }

    /**
     * Retrieves the occupancy subtype code from the plan's most restrictive FAR helper.
     *
     * @param pl The plan object.
     * @return Occupancy subtype code as String, or empty string if not available.
     */
    private String getOccupancySubType(Plan pl) {
    	if (pl.getVirtualBuilding() != null &&
    			pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null &&
    			pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype() != null) {
    		return pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode();
    	}
    	return "";
    }

    /**
     * Calculates the plantation percentage with respect to the plot area.
     *
     * @param totalArea Total plantation area.
     * @param plotArea  Total plot area.
     * @return Plantation percentage as BigDecimal (0 if plot area is zero or null).
     */
    private BigDecimal calculatePlantationPercentage(BigDecimal totalArea, BigDecimal plotArea) {
    	if (plotArea != null && plotArea.compareTo(BigDecimal.ZERO) > 0) {
    		return totalArea.divide(plotArea, DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);
    	}
    	return BigDecimal.ZERO;
    }

    /**
     * Checks if the given occupancy type and subtype are relevant for plantation rules.
     *
     * @param type    Occupancy type code.
     * @param subType Occupancy subtype code.
     * @return True if the type/subtype qualifies for plantation check, false otherwise.
     */
    private boolean isRelevantOccupancyType(String type, String subType) {
    	return A.equals(type) || B.equals(type) || D.equals(type) || G.equals(type)
    			|| A_AF.equals(subType) || A_SA.equals(subType);
    }

    /**
     * Processes plantation rule comparison and adds the result to scrutiny details.
     *
     * @param pl              The plan object.
     * @param plantationPer   Calculated plantation percentage.
     * @param scrutinyDetail  ScrutinyDetail object to be updated.
     * @param detail         Initial rule details map to be filled.
     */
    private void processPlantationRule(Plan pl, BigDecimal plantationPer, ScrutinyDetail scrutinyDetail, ReportScrutinyDetail detail) {
		BigDecimal plantation = BigDecimal.ZERO;
		BigDecimal percent;

		List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.PLANTATION.getValue(), false);
		Optional<PlantationRequirement> matchedRule = rules.stream()
            .filter(PlantationRequirement.class::isInstance)
            .map(PlantationRequirement.class::cast)
            .findFirst();

		if (matchedRule.isPresent()) {
			PlantationRequirement rule = matchedRule.get();
			plantation = rule.getPermissible();
			percent = rule.getPercent();
		}
		detail.setRequired(GREATER_THAN_EQUAL_TO_FIVE + PERCENTAGE_SYMBOL);
		detail.setProvided(plantationPer.multiply(new BigDecimal(100)).toString() + PERCENTAGE_SYMBOL);

		if (plantationPer.compareTo(plantation) >= 0) {
			detail.setStatus(Result.Accepted.getResultVal());
		} else {
			detail.setStatus(Result.Not_Accepted.getResultVal());
		}
		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	}

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
