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

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_SA;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.G;
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
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Plantation extends FeatureProcess {

    private static final Logger LOGGER = LogManager.getLogger(Plantation.class);
    private static final String RULE_32 = "4.4.4 (XI)";
    public static final String PLANTATION_TREECOVER_DESCRIPTION = "Plantation tree cover";

    @Autowired
  	CacheManagerMdms cache;
    
    @Override
    public Plan validate(Plan pl) {
        return null;
    }

    @Override
    public Plan process(Plan pl) {
    	validate(pl);
    	ScrutinyDetail scrutinyDetail = createScrutinyDetail();
    	Map<String, String> details = createInitialDetails();

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

    private Map<String, String> createInitialDetails() {
    	Map<String, String> details = new HashMap<>();
    	details.put(RULE_NO, RULE_32);
    	details.put(DESCRIPTION, PLANTATION_TREECOVER_DESCRIPTION);
    	return details;
    }

    private BigDecimal getTotalPlantationArea(Plan pl) {
    	BigDecimal totalArea = BigDecimal.ZERO;
    	if (pl.getPlantation() != null && pl.getPlantation().getPlantations() != null) {
    		for (Measurement m : pl.getPlantation().getPlantations()) {
    			totalArea = totalArea.add(m.getArea());
    		}
    	}
    	return totalArea;
    }

    private BigDecimal getPlotArea(Plan pl) {
    	return (pl.getPlot() != null) ? pl.getPlot().getArea() : BigDecimal.ZERO;
    }

    private String getOccupancyType(Plan pl) {
    	if (pl.getVirtualBuilding() != null &&
    			pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null &&
    			pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType() != null) {
    		return pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode();
    	}
    	return "";
    }

    private String getOccupancySubType(Plan pl) {
    	if (pl.getVirtualBuilding() != null &&
    			pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null &&
    			pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype() != null) {
    		return pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode();
    	}
    	return "";
    }

    private BigDecimal calculatePlantationPercentage(BigDecimal totalArea, BigDecimal plotArea) {
    	if (plotArea != null && plotArea.compareTo(BigDecimal.ZERO) > 0) {
    		return totalArea.divide(plotArea, DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);
    	}
    	return BigDecimal.ZERO;
    }

    private boolean isRelevantOccupancyType(String type, String subType) {
    	return A.equals(type) || B.equals(type) || D.equals(type) || G.equals(type)
    			|| A_AF.equals(subType) || A_SA.equals(subType);
    }

    private void processPlantationRule(Plan pl, BigDecimal plantationPer, ScrutinyDetail scrutinyDetail, Map<String, String> details) {
    	BigDecimal plantation = BigDecimal.ZERO;
    	BigDecimal percent;

    	List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.PLANTATION, false);
    	Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

    	if (matchedRule.isPresent()) {
    		MdmsFeatureRule rule = matchedRule.get();
    		plantation = rule.getPermissible();
    		percent = rule.getPercent();
    	}

    	if (plantationPer.compareTo(plantation) >= 0) {
    		details.put(REQUIRED, ">= 5%");
    		details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
    		details.put(STATUS, Result.Accepted.getResultVal());
    	} else {
    		details.put(REQUIRED, ">= 5%");
    		details.put(PROVIDED, plantationPer.multiply(new BigDecimal(100)).toString() + "%");
    		details.put(STATUS, Result.Not_Accepted.getResultVal());
    	}

    	scrutinyDetail.getDetail().add(details);
    	pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
