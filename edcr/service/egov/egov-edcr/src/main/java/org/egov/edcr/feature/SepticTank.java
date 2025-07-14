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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SepticTank extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(SepticTank.class);

	// Constants for rule number and field descriptions
	private static final String RULE_45_E = "45-e";
	public static final String DISTANCE_FROM_WATERSOURCE = "Distance from watersource";
	public static final String DISTANCE_FROM_BUILDING = "Distance from Building";
	public static final String MIN_DISTANCE_FROM_GOVTBUILDING_DESC = "Minimum distance fcrom government building";

	// Default minimum distances (fallback values)
	public static final BigDecimal MIN_DIS_WATERSRC = BigDecimal.valueOf(18);
	public static final BigDecimal MIN_DIS_BUILDING = BigDecimal.valueOf(6);

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms; // Service to fetch rules from MDMS
	
	@Autowired
	CacheManagerMdms cache;

	@Override
	public Plan validate(Plan pl) {
		// No validation rules implemented here for now
		return pl;
	}

//	@Override
//	public Plan process(Plan pl) {
//
//		// Setting up scrutiny detail metadata
//		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//		scrutinyDetail.setKey("Common_Septic Tank ");
//		scrutinyDetail.addColumnHeading(1, RULE_NO);
//		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//		scrutinyDetail.addColumnHeading(3, PERMITTED);
//		scrutinyDetail.addColumnHeading(4, PROVIDED);
//		scrutinyDetail.addColumnHeading(5, STATUS);
//
//		// Fetch septic tank details from the plan
//		List<org.egov.common.entity.edcr.SepticTank> septicTanks = pl.getSepticTanks();
//
//    	BigDecimal septicTankMinDisWatersrc = BigDecimal.ZERO;
//    	BigDecimal septicTankMinDisBuilding = BigDecimal.ZERO;
//   
//	        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.SEPTIC_TANK, false);
//			
//	        Optional<MdmsFeatureRule> matchedRule = rules.stream()
//	        	    .map(obj -> (MdmsFeatureRule) obj)
//	        	    .findFirst();
//
//	        	if (matchedRule.isPresent()) {
//	        	    MdmsFeatureRule rule = matchedRule.get();
//	        	    septicTankMinDisWatersrc = rule.getSepticTankMinDisWatersrc();
//	        	    septicTankMinDisBuilding = rule.getSepticTankMinDisBuilding();
//	        	} 
//	
//		// Validate each septic tank’s distance from water source and building
//		for (org.egov.common.entity.edcr.SepticTank septicTank : septicTanks) {
//			boolean validWaterSrcDistance = false;
//			boolean validBuildingDistance = false;
//
//			// Validate distance from water source
//			if (!septicTank.getDistanceFromWaterSource().isEmpty()) {
//				BigDecimal minDistWaterSrc = septicTank.getDistanceFromWaterSource().stream().reduce(BigDecimal::min).get();
//				if (minDistWaterSrc != null && minDistWaterSrc.compareTo(septicTankMinDisWatersrc) >= 0) {
//					validWaterSrcDistance = true;
//				}
//				buildResult(pl, scrutinyDetail, validWaterSrcDistance, DISTANCE_FROM_WATERSOURCE, ">= " + septicTankMinDisWatersrc.toString(), minDistWaterSrc.toString());
//			}
//
//			// Validate distance from building
//			if (!septicTank.getDistanceFromBuilding().isEmpty()) {
//				BigDecimal minDistBuilding = septicTank.getDistanceFromBuilding().stream().reduce(BigDecimal::min).get();
//				if (minDistBuilding != null && minDistBuilding.compareTo(septicTankMinDisBuilding) >= 0) {
//					validBuildingDistance = true;
//				}
//				buildResult(pl, scrutinyDetail, validBuildingDistance, DISTANCE_FROM_BUILDING, ">= " + septicTankMinDisBuilding.toString(), minDistBuilding.toString());
//			}
//		}
//
//		return pl;
//	}
//
//	// Helper method to build scrutiny detail report row
//	private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, boolean valid, String description, String permited,
//			String provided) {
//		Map<String, String> details = new HashMap<>();
//		details.put(RULE_NO, RULE_45_E);
//		details.put(DESCRIPTION, description);
//		details.put(PERMITTED, permited);
//		details.put(PROVIDED, provided);
//		details.put(STATUS, valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
//
//		// Add details to the scrutiny detail and attach to the plan report
//		scrutinyDetail.getDetail().add(details);
//		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//	}
	
	@Override
	public Plan process(Plan pl) {
	    ScrutinyDetail scrutinyDetail = createScrutinyDetail();

	    List<org.egov.common.entity.edcr.SepticTank> septicTanks = pl.getSepticTanks();
	    if (septicTanks == null || septicTanks.isEmpty()) {
	        return pl;
	    }

	    BigDecimal septicTankMinDisWatersrc = BigDecimal.ZERO;
	    BigDecimal septicTankMinDisBuilding = BigDecimal.ZERO;

	    Optional<MdmsFeatureRule> matchedRule = getMatchedSepticTankRule(pl);
	    if (matchedRule.isPresent()) {
	        septicTankMinDisWatersrc = matchedRule.get().getSepticTankMinDisWatersrc();
	        septicTankMinDisBuilding = matchedRule.get().getSepticTankMinDisBuilding();
	    }

	    validateSepticTanks(pl, scrutinyDetail, septicTanks, septicTankMinDisWatersrc, septicTankMinDisBuilding);

	    return pl;
	}

	private ScrutinyDetail createScrutinyDetail() {
	    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.setKey(Common_Septic_Tank);
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	    scrutinyDetail.addColumnHeading(3, PERMITTED);
	    scrutinyDetail.addColumnHeading(4, PROVIDED);
	    scrutinyDetail.addColumnHeading(5, STATUS);
	    return scrutinyDetail;
	}

	private Optional<MdmsFeatureRule> getMatchedSepticTankRule(Plan pl) {
	    List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.SEPTIC_TANK, false);
	    return rules.stream()
	                .filter(MdmsFeatureRule.class::isInstance)
	                .map(obj -> (MdmsFeatureRule) obj)
	                .findFirst();
	}

	private void validateSepticTanks(Plan pl, ScrutinyDetail scrutinyDetail, List<org.egov.common.entity.edcr.SepticTank> septicTanks,
	                                  BigDecimal minDisWaterSrc, BigDecimal minDisBuilding) {
	    for (org.egov.common.entity.edcr.SepticTank septicTank : septicTanks) {
	        validateDistance(pl, scrutinyDetail, septicTank.getDistanceFromWaterSource(), minDisWaterSrc,
	                DISTANCE_FROM_WATERSOURCE);
	        validateDistance(pl, scrutinyDetail, septicTank.getDistanceFromBuilding(), minDisBuilding,
	                DISTANCE_FROM_BUILDING);
	    }
	}

	private void validateDistance(Plan pl, ScrutinyDetail scrutinyDetail, List<BigDecimal> distances, BigDecimal minPermittedDistance,
	                              String description) {
	    if (distances == null || distances.isEmpty()) return;

	    BigDecimal minProvided = distances.stream().reduce(BigDecimal::min).orElse(null);
	    if (minProvided == null) return;

	    boolean isValid = minProvided.compareTo(minPermittedDistance) >= 0;
	    buildResult(pl, scrutinyDetail, isValid, description,
	            ">= " + minPermittedDistance.toString(), minProvided.toString());
	}

	private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, boolean valid, String description, String permited,
	                         String provided) {
	    Map<String, String> details = new HashMap<>();
	    details.put(RULE_NO, RULE_45_E);
	    details.put(DESCRIPTION, description);
	    details.put(PERMITTED, permited);
	    details.put(PROVIDED, provided);
	    details.put(STATUS, valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

	    scrutinyDetail.getDetail().add(details);
	    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}


	@Override
	public Map<String, Date> getAmendments() {
		// No amendments applicable for now
		return new LinkedHashMap<>();
	}
}

