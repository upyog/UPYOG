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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SegregatedToilet extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(SegregatedToilet.class);
    
    private static final String RULE_59_10  = "59-10-i";
    public static final String SEGREGATEDTOILET_DESCRIPTION = "Num. of segregated toilets";
    public static final String SEGREGATEDTOILET_DIMENSION_DESCRIPTION = "Segregated toilet distance from main entrance";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	CacheManagerMdms cache;

    @Override
    public Plan validate(Plan pl) {
        return pl; // No specific validation logic implemented
    }

//	@Override
//	public Plan process(Plan pl) {
//
//		// Scrutiny setup
//		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//		scrutinyDetail.setKey(Common_Segregated_Toilet);
//		scrutinyDetail.addColumnHeading(1, RULE_NO);
//		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//		scrutinyDetail.addColumnHeading(3, REQUIRED);
//		scrutinyDetail.addColumnHeading(4, PROVIDED);
//		scrutinyDetail.addColumnHeading(5, STATUS);
//
//		Map<String, String> details = new HashMap<>();
//		details.put(RULE_NO, RULE_59_10);
//
//		// Variables for comparison and rules
//		BigDecimal minDimension = BigDecimal.ZERO;
//		BigDecimal maxHeightOfBuilding = BigDecimal.ZERO;
//		BigDecimal maxNumOfFloorsOfBuilding = BigDecimal.ZERO;
//
//		BigDecimal sTValueOne = BigDecimal.ZERO;
//		BigDecimal sTValueTwo = BigDecimal.ZERO;
//		BigDecimal sTValueThree = BigDecimal.ZERO;
//		BigDecimal sTValueFour = BigDecimal.ZERO;
//		BigDecimal sTSegregatedToiletProvided = BigDecimal.ZERO;
//		BigDecimal sTSegregatedToiletRequired = BigDecimal.ZERO;
//		BigDecimal sTminDimensionRequired = BigDecimal.ZERO;
//
//		List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.SEGREGATED_TOILET, false);
//		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
//
//		if (matchedRule.isPresent()) {
//			MdmsFeatureRule rule = matchedRule.get();
//			sTValueOne = rule.getSTValueOne();
//			sTValueTwo = rule.getSTValueTwo();
//			sTValueThree = rule.getSTValueThree();
//			sTValueFour = rule.getSTValueFour();
//			sTSegregatedToiletRequired = rule.getSTSegregatedToiletRequired();
//			sTSegregatedToiletProvided = rule.getSTSegregatedToiletProvided();
//			sTminDimensionRequired = rule.getSTminDimensionRequired();
//		}
//		// Find minimum distance from main entrance among all toilets
//		if (pl.getSegregatedToilet() != null && !pl.getSegregatedToilet().getDistancesToMainEntrance().isEmpty())
//			minDimension = pl.getSegregatedToilet().getDistancesToMainEntrance().stream().reduce(BigDecimal::min).get();
//
//		// Find the tallest building and one with the most floors
//		for (Block b : pl.getBlocks()) {
//			if (b.getBuilding().getBuildingHeight() != null
//					&& b.getBuilding().getBuildingHeight().compareTo(maxHeightOfBuilding) > 0) {
//				maxHeightOfBuilding = b.getBuilding().getBuildingHeight();
//			}
//			if (b.getBuilding().getFloorsAboveGround() != null
//					&& b.getBuilding().getFloorsAboveGround().compareTo(maxNumOfFloorsOfBuilding) > 0) {
//				maxNumOfFloorsOfBuilding = b.getBuilding().getFloorsAboveGround();
//			}
//		}
//
//		// Apply logic only if the rule is applicable based on occupancy type and limits
//		if (pl.getVirtualBuilding() != null && pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null
//				&& pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType() != null
//				&& ((DxfFileConstants.A
//						.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
//						&& maxHeightOfBuilding.compareTo(sTValueOne) >= 0)
//						|| ((DxfFileConstants.I
//								.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
//								|| DxfFileConstants.A.equals(
//										pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
//								|| DxfFileConstants.E.equals(
//										pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode()))
//								&& pl.getVirtualBuilding().getTotalBuitUpArea() != null
//								&& pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(sTValueTwo) >= 0
//								&& maxNumOfFloorsOfBuilding.compareTo(sTValueThree) >= 0)
//						|| (DxfFileConstants.C
//								.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
//								&& pl.getVirtualBuilding().getTotalBuitUpArea() != null
//								&& pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(sTValueFour) >= 0))) {
//
//			// Check if segregated toilets are provided
//			if (pl.getSegregatedToilet() != null && pl.getSegregatedToilet().getSegregatedToilets() != null
//					&& !pl.getSegregatedToilet().getSegregatedToilets().isEmpty()) {
//				details.put(DESCRIPTION, SEGREGATEDTOILET_DESCRIPTION);
//				details.put(REQUIRED, sTSegregatedToiletRequired.toString());
//				details.put(PROVIDED, String.valueOf(pl.getSegregatedToilet().getSegregatedToilets().size()));
//				details.put(STATUS, Result.Accepted.getResultVal());
//			} else {
//				details.put(DESCRIPTION, SEGREGATEDTOILET_DESCRIPTION);
//				details.put(REQUIRED, sTSegregatedToiletRequired.toString());
//				details.put(PROVIDED, sTSegregatedToiletProvided.toString());
//				details.put(STATUS, Result.Not_Accepted.getResultVal());
//			}
//
//			scrutinyDetail.getDetail().add(details);
//			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//
//			// Check if minimum dimension (distance to entrance) is satisfied
//			if (minDimension != null && minDimension.compareTo(sTminDimensionRequired) >= 0) {
//				details.put(DESCRIPTION, SEGREGATEDTOILET_DIMENSION_DESCRIPTION);
//				details.put(REQUIRED, ">= " + sTminDimensionRequired.toString());
//				details.put(PROVIDED, minDimension.toString());
//				details.put(STATUS, Result.Accepted.getResultVal());
//			} else {
//				details.put(DESCRIPTION, SEGREGATEDTOILET_DIMENSION_DESCRIPTION);
//				details.put(REQUIRED, ">= " + sTminDimensionRequired.toString());
//				details.put(PROVIDED, minDimension.toString());
//				details.put(STATUS, Result.Not_Accepted.getResultVal());
//			}
//
//			scrutinyDetail.getDetail().add(details);
//			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//		}
//
//		return pl; // Return the processed plan
//	}
    
    @Override
    public Plan process(Plan pl) {

        ScrutinyDetail scrutinyDetail = initializeScrutinyDetail();
        Map<String, String> details = initializeDetails();

        SegregatedToiletRuleValues ruleValues = getSegregatedToiletRuleValues(pl);
        BigDecimal minDimension = findMinimumDistanceToEntrance(pl);
        BigDecimal maxHeightOfBuilding = getMaxBuildingHeight(pl);
        BigDecimal maxNumOfFloors = getMaxFloorsAboveGround(pl);

        if (isRuleApplicable(pl, ruleValues, maxHeightOfBuilding, maxNumOfFloors)) {
            processSegregatedToilet(pl, scrutinyDetail, details, ruleValues);
            processMinimumDimension(pl, scrutinyDetail, details, ruleValues, minDimension);
        }

        return pl;
    }

    private ScrutinyDetail initializeScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(Common_Segregated_Toilet);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
    }

    private Map<String, String> initializeDetails() {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_59_10);
        return details;
    }

    private SegregatedToiletRuleValues getSegregatedToiletRuleValues(Plan pl) {
        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.SEGREGATED_TOILET, false);
        Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

        SegregatedToiletRuleValues values = new SegregatedToiletRuleValues();
        if (matchedRule.isPresent()) {
            MdmsFeatureRule rule = matchedRule.get();
            values.sTValueOne = rule.getSTValueOne();
            values.sTValueTwo = rule.getSTValueTwo();
            values.sTValueThree = rule.getSTValueThree();
            values.sTValueFour = rule.getSTValueFour();
            values.sTSegregatedToiletRequired = rule.getSTSegregatedToiletRequired();
            values.sTSegregatedToiletProvided = rule.getSTSegregatedToiletProvided();
            values.sTminDimensionRequired = rule.getSTminDimensionRequired();
        }
        return values;
    }

    private BigDecimal findMinimumDistanceToEntrance(Plan pl) {
        if (pl.getSegregatedToilet() != null && !pl.getSegregatedToilet().getDistancesToMainEntrance().isEmpty()) {
            return pl.getSegregatedToilet().getDistancesToMainEntrance().stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getMaxBuildingHeight(Plan pl) {
        BigDecimal max = BigDecimal.ZERO;
        for (Block b : pl.getBlocks()) {
            if (b.getBuilding().getBuildingHeight() != null && b.getBuilding().getBuildingHeight().compareTo(max) > 0) {
                max = b.getBuilding().getBuildingHeight();
            }
        }
        return max;
    }

    private BigDecimal getMaxFloorsAboveGround(Plan pl) {
        BigDecimal max = BigDecimal.ZERO;
        for (Block b : pl.getBlocks()) {
            if (b.getBuilding().getFloorsAboveGround() != null && b.getBuilding().getFloorsAboveGround().compareTo(max) > 0) {
                max = b.getBuilding().getFloorsAboveGround();
            }
        }
        return max;
    }

    private boolean isRuleApplicable(Plan pl, SegregatedToiletRuleValues vals, BigDecimal maxHeight, BigDecimal maxFloors) {
        if (pl.getVirtualBuilding() == null || pl.getVirtualBuilding().getMostRestrictiveFarHelper() == null
                || pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType() == null)
            return false;

        String typeCode = pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode();
        BigDecimal totalBuiltUp = pl.getVirtualBuilding().getTotalBuitUpArea();

        return (DxfFileConstants.A.equals(typeCode) && maxHeight.compareTo(vals.sTValueOne) >= 0)
                || ((DxfFileConstants.I.equals(typeCode) || DxfFileConstants.A.equals(typeCode) || DxfFileConstants.E.equals(typeCode))
                    && totalBuiltUp != null && totalBuiltUp.compareTo(vals.sTValueTwo) >= 0
                    && maxFloors.compareTo(vals.sTValueThree) >= 0)
                || (DxfFileConstants.C.equals(typeCode)
                    && totalBuiltUp != null && totalBuiltUp.compareTo(vals.sTValueFour) >= 0);
    }

    private void processSegregatedToilet(Plan pl, ScrutinyDetail scrutinyDetail, Map<String, String> details, SegregatedToiletRuleValues vals) {
        if (pl.getSegregatedToilet() != null && pl.getSegregatedToilet().getSegregatedToilets() != null
                && !pl.getSegregatedToilet().getSegregatedToilets().isEmpty()) {
            details.put(DESCRIPTION, SEGREGATEDTOILET_DESCRIPTION);
            details.put(REQUIRED, vals.sTSegregatedToiletRequired.toString());
            details.put(PROVIDED, String.valueOf(pl.getSegregatedToilet().getSegregatedToilets().size()));
            details.put(STATUS, Result.Accepted.getResultVal());
        } else {
            details.put(DESCRIPTION, SEGREGATEDTOILET_DESCRIPTION);
            details.put(REQUIRED, vals.sTSegregatedToiletRequired.toString());
            details.put(PROVIDED, vals.sTSegregatedToiletProvided.toString());
            details.put(STATUS, Result.Not_Accepted.getResultVal());
        }

        scrutinyDetail.getDetail().add(new HashMap<>(details));
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    private void processMinimumDimension(Plan pl, ScrutinyDetail scrutinyDetail, Map<String, String> details,
                                         SegregatedToiletRuleValues vals, BigDecimal minDimension) {

        details.put(DESCRIPTION, SEGREGATEDTOILET_DIMENSION_DESCRIPTION);
        details.put(REQUIRED, ">= " + vals.sTminDimensionRequired.toString());
        details.put(PROVIDED, minDimension.toString());

        if (minDimension != null && minDimension.compareTo(vals.sTminDimensionRequired) >= 0) {
            details.put(STATUS, Result.Accepted.getResultVal());
        } else {
            details.put(STATUS, Result.Not_Accepted.getResultVal());
        }

        scrutinyDetail.getDetail().add(new HashMap<>(details));
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    // Helper class to encapsulate rule values
    private static class SegregatedToiletRuleValues {
        BigDecimal sTValueOne = BigDecimal.ZERO;
        BigDecimal sTValueTwo = BigDecimal.ZERO;
        BigDecimal sTValueThree = BigDecimal.ZERO;
        BigDecimal sTValueFour = BigDecimal.ZERO;
        BigDecimal sTSegregatedToiletProvided = BigDecimal.ZERO;
        BigDecimal sTSegregatedToiletRequired = BigDecimal.ZERO;
        BigDecimal sTminDimensionRequired = BigDecimal.ZERO;
    }


    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>(); // No amendments
    }
}

