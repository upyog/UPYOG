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

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ExitWidth extends FeatureProcess {
	
	private static final Logger LOG = LogManager.getLogger(ExitWidth.class);

    /**
     * Validates the given plan object for exit width compliance.
     * Ensures that either exit width for doors or stairs is defined for each floor.
     *
     * @param pl The plan object to validate.
     * @return The validated plan object.
     */
    private Plan validateExitWidth(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        // validate either exit width door or exit width stair should be compulsory
        if (!pl.getBlocks().isEmpty()) {
            blk: for (Block block : pl.getBlocks()) {
                if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
                    OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
                    if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getSubtype() != null
                            && A_R.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())) {
                        continue blk;
                    }
                    for (Floor floor : block.getBuilding().getFloors()) {
                        if (floor.getExitWidthDoor().isEmpty() && floor.getExitWidthStair().isEmpty()) {
                            errors.put(String.format(DcrConstants.EXIT_WIDTH_DOORSTAIRWAYS, block.getNumber(),
                                    floor.getNumber()),
                                    edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                            new String[] { String.format(DcrConstants.EXIT_WIDTH_DOORSTAIRWAYS, block.getNumber(),
                                                    floor.getNumber()) },
                                            LocaleContextHolder.getLocale()));
                            pl.addErrors(errors);
                        }
                    }
                }
            }
        }
        return pl;
    }
    
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	MDMSCacheManager cache;


//	@Override
//	public Plan process(Plan pl) {
//
//		String rule = EXIT_WIDTH_DESC;
//		String subRule = null;
//		validateExitWidth(pl);
//
//		BigDecimal exitWidthOccupancyTypeHandlerVal = BigDecimal.ZERO;
//		BigDecimal exitWidthNotOccupancyTypeHandlerVal = BigDecimal.ZERO;
//		BigDecimal exitWidth_A_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_A_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_A_SR_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_A_SR_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_B_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_B_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_C_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_C_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_D_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_D_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_E_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_E_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_F_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_F_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_G_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_G_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_H_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_H_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//		BigDecimal exitWidth_I_occupantLoadDivisonFactor = BigDecimal.ZERO;
//		BigDecimal exitWidth_I_noOfDoors = BigDecimal.ZERO;
//		BigDecimal exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
//
//		// Fetch all rules for the given plan from the cache.
//		// Then, filter to find the first rule where the condition falls within the
//		// defined range.
//		// If a matching rule is found, proceed with its processing.
//
//		List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.EXIT_WIDTH.getValue(), false);
//        Optional<ExitWidthRequirement> matchedRule = rules.stream()
//            .filter(ExitWidthRequirement.class::isInstance)
//            .map(ExitWidthRequirement.class::cast)
//            .findFirst();
//		if (matchedRule.isPresent()) {
//			ExitWidthRequirement mdmsRule = matchedRule.get();
//			exitWidthOccupancyTypeHandlerVal = mdmsRule.getExitWidthOccupancyTypeHandlerVal();
//			exitWidthNotOccupancyTypeHandlerVal = mdmsRule.getExitWidthNotOccupancyTypeHandlerVal();
//			exitWidth_A_occupantLoadDivisonFactor = mdmsRule.getExitWidth_A_occupantLoadDivisonFactor();
//			exitWidth_A_noOfDoors = mdmsRule.getExitWidth_A_noOfDoors();
//			exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay();
//			exitWidth_A_SR_occupantLoadDivisonFactor = mdmsRule.getExitWidth_A_SR_occupantLoadDivisonFactor();
//			exitWidth_A_SR_noOfDoors = mdmsRule.getExitWidth_A_SR_noOfDoors();
//			exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//			exitWidth_B_occupantLoadDivisonFactor = mdmsRule.getExitWidth_B_occupantLoadDivisonFactor();
//			exitWidth_B_noOfDoors = mdmsRule.getExitWidth_B_noOfDoors();
//			exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//			exitWidth_C_occupantLoadDivisonFactor = mdmsRule.getExitWidth_C_occupantLoadDivisonFactor();
//			exitWidth_C_noOfDoors = mdmsRule.getExitWidth_C_noOfDoors();
//			exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//			exitWidth_D_occupantLoadDivisonFactor = mdmsRule.getExitWidth_D_occupantLoadDivisonFactor();
//			exitWidth_D_noOfDoors = mdmsRule.getExitWidth_D_noOfDoors();
//			exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//			exitWidth_E_occupantLoadDivisonFactor = mdmsRule.getExitWidth_E_occupantLoadDivisonFactor();
//			exitWidth_E_noOfDoors = mdmsRule.getExitWidth_E_noOfDoors();
//			exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//			exitWidth_F_occupantLoadDivisonFactor = mdmsRule.getExitWidth_F_occupantLoadDivisonFactor();
//			exitWidth_F_noOfDoors = mdmsRule.getExitWidth_F_noOfDoors();
//			exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//			exitWidth_G_occupantLoadDivisonFactor = mdmsRule.getExitWidth_G_occupantLoadDivisonFactor();
//			exitWidth_G_noOfDoors = mdmsRule.getExitWidth_G_noOfDoors();
//			exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//			exitWidth_H_occupantLoadDivisonFactor = mdmsRule.getExitWidth_H_occupantLoadDivisonFactor();
//			exitWidth_H_noOfDoors = mdmsRule.getExitWidth_H_noOfDoors();
//			exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//			exitWidth_I_occupantLoadDivisonFactor = mdmsRule.getExitWidth_I_occupantLoadDivisonFactor();
//			exitWidth_I_noOfDoors = mdmsRule.getExitWidth_I_noOfDoors();
//			exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule
//					.getExitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay();
//
//		}
//
//		if (!pl.getBlocks().isEmpty()) {
//			blk: for (Block block : pl.getBlocks()) {
//				scrutinyDetail = new ScrutinyDetail();
//				scrutinyDetail.addColumnHeading(1, RULE_NO);
//				scrutinyDetail.addColumnHeading(2, FLOOR_SPACED);
//				scrutinyDetail.addColumnHeading(3, E_OCCUPANCY);
//				scrutinyDetail.addColumnHeading(4, REQUIRED);
//				scrutinyDetail.addColumnHeading(5, PROVIDED);
//				scrutinyDetail.addColumnHeading(6, STATUS);
//				scrutinyDetail.setKey(BLOCK + block.getNumber() + UNDERSCORE + EXIT_WIDTH_MINIMUM);
//				ScrutinyDetail scrutinyDetail2 = new ScrutinyDetail();
//				scrutinyDetail2.addColumnHeading(1, RULE_NO);
//				scrutinyDetail2.addColumnHeading(2, FLOOR_SPACED);
//				scrutinyDetail2.addColumnHeading(3, REQUIRED);
//				scrutinyDetail2.addColumnHeading(4, PROVIDED);
//				scrutinyDetail2.addColumnHeading(5, STATUS);
//				scrutinyDetail2.setKey(BLOCK + block.getNumber() + UNDERSCORE + EXIT_WIDTH_MAXIMUM);
//				if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
//					if (ProcessHelper.checkExemptionConditionForBuildingParts(block)) {
//						continue blk;
//					}
//					for (Floor flr : block.getBuilding().getFloors()) {
//						BigDecimal totalOccupantLoadForAFloor = BigDecimal.ZERO;
//						List<BigDecimal> listOfMaxOccupantsAllowedThrghExits = new ArrayList<>();
//						BigDecimal value;
//						List<Map<String, Object>> occupancyTypeValueListMap = new ArrayList<>();
//						if (!flr.getOccupancies().isEmpty()) {
//							for (Occupancy occupancy : flr.getOccupancies()) {
//								Map<String, Object> occupancyTypeValueMap = new HashMap<>();
//								String occupancyTypeHelper = StringUtils.EMPTY;
//								OccupancyHelperDetail occupancyHelperDetail = null;
//								if (occupancy.getTypeHelper().getSubtype() != null) {
//									occupancyHelperDetail = occupancy.getTypeHelper().getSubtype();
//									occupancyTypeHelper = occupancy.getTypeHelper().getSubtype().getCode();
//								} else if (occupancy.getTypeHelper() != null) {
//									if (occupancy.getTypeHelper().getType() != null) {
//										occupancyHelperDetail = occupancy.getTypeHelper().getType();
//										occupancyTypeHelper = occupancy.getTypeHelper().getType().getCode();
//									}
//								}
//								if (occupancyTypeHelper.equals(DxfFileConstants.A)
//										|| occupancyTypeHelper.equals(DxfFileConstants.A_R)
//										|| occupancyTypeHelper.equals(DxfFileConstants.A_SR)
//										|| occupancyTypeHelper.equals(DxfFileConstants.A_HE)
//										|| occupancyTypeHelper.equals(DxfFileConstants.A_AF)
//										|| occupancyTypeHelper.equals(DxfFileConstants.A_PO)) {
//									value = exitWidthOccupancyTypeHandlerVal;
//								} else {
//									value = exitWidthNotOccupancyTypeHandlerVal;
//								}
//								if (occupancyHelperDetail != null)
//									occupancyTypeValueMap.put(E_OCCUPANCY, occupancyHelperDetail.getName());
//								occupancyTypeValueMap.put(EXIT_WIDTH, value);
//								occupancyTypeValueListMap.add(occupancyTypeValueMap);
//							}
//							/*
//							 * calculating maximum exit width, if map has two enteries with same exit width
//							 * , occupancy needs to be comma separated if it is different and it need not be
//							 * duplicated if occupancy is same
//							 */
//							if (!occupancyTypeValueListMap.isEmpty()) {
//								Map<String, Object> mostRestrictiveOccupancyAndMaxValueMap = occupancyTypeValueListMap
//										.get(0);
//								for (Map<String, Object> occupancyValueMap : occupancyTypeValueListMap) {
//									if (((BigDecimal) occupancyValueMap.get(EXIT_WIDTH)).compareTo(
//											(BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH)) == 0) {
//										if (mostRestrictiveOccupancyAndMaxValueMap.get(E_OCCUPANCY) != null
//												&& !(occupancyValueMap.get(E_OCCUPANCY)).equals(
//														mostRestrictiveOccupancyAndMaxValueMap.get(E_OCCUPANCY))) {
//											SortedSet<String> uniqueOccupancies = new TreeSet<>();
//											String[] occupancyString = (occupancyValueMap.get(E_OCCUPANCY) + " , "
//													+ mostRestrictiveOccupancyAndMaxValueMap.get(E_OCCUPANCY))
//													.split(" , ");
//											for (String str : occupancyString) {
//												uniqueOccupancies.add(str);
//											}
//											String occupancyStr = removeDuplicates(uniqueOccupancies);
//											mostRestrictiveOccupancyAndMaxValueMap.put(E_OCCUPANCY, occupancyStr);
//										}
//										continue;
//									}
//									if (((BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH))
//											.compareTo((BigDecimal) occupancyValueMap.get(EXIT_WIDTH)) < 0) {
//										mostRestrictiveOccupancyAndMaxValueMap.putAll(occupancyValueMap);
//									}
//								}
//								validateExitWidth(flr, pl, subRule, rule, block,
//										(BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH),
//										(String) mostRestrictiveOccupancyAndMaxValueMap.get(E_OCCUPANCY));
//							}
//						}
//						for (Occupancy occupancy : flr.getOccupancies()) {
//							BigDecimal occupantLoad = BigDecimal.ZERO;
//							BigDecimal maxOccupantsAllowedThrghExits = BigDecimal.ZERO;
//							BigDecimal occupantLoadDivisonFactor;
//							String occupancyTypeHelper = StringUtils.EMPTY;
//							if (occupancy.getTypeHelper() != null) {
//								if (occupancy.getTypeHelper().getSubtype() != null) {
//									occupancyTypeHelper = occupancy.getTypeHelper().getSubtype().getCode();
//								} else if (occupancy.getTypeHelper().getType() != null) {
//									occupancyTypeHelper = occupancy.getTypeHelper().getType().getCode();
//								}
//							}
//							if (occupancyTypeHelper.equals(DxfFileConstants.A)
//									|| occupancyTypeHelper.equals(DxfFileConstants.A_R)
//									|| occupancyTypeHelper.equals(DxfFileConstants.A_AF)
//									|| occupancyTypeHelper.equals(DxfFileConstants.A_PO)) {
//								occupantLoadDivisonFactor = exitWidth_A_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_A_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.A_SR)
//									|| occupancyTypeHelper.equals(DxfFileConstants.A_HE)) {
//								occupantLoadDivisonFactor = exitWidth_A_SR_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_A_SR_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.B)
//									|| occupancyTypeHelper.equals(DxfFileConstants.B2)
//									|| occupancyTypeHelper.equals(DxfFileConstants.B_HEI)) {
//								occupantLoadDivisonFactor = exitWidth_B_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_B_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.C)
//									|| occupancyTypeHelper.equals(DxfFileConstants.C_MIP)
//									|| occupancyTypeHelper.equals(DxfFileConstants.C_MOP)
//									|| occupancyTypeHelper.equals(DxfFileConstants.C_MA)) {
//								occupantLoadDivisonFactor = exitWidth_C_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_C_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.D)
//									|| occupancyTypeHelper.equals(DxfFileConstants.D_AW)
//									|| occupancyTypeHelper.equals(DxfFileConstants.D_BT)) {
//								occupantLoadDivisonFactor = exitWidth_D_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_D_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.E)) {
//								occupantLoadDivisonFactor = exitWidth_E_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_E_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.F)
//									|| occupancyTypeHelper.equals(DxfFileConstants.F_PP)
//									|| occupancyTypeHelper.equals(DxfFileConstants.F_PA)
//									|| occupancyTypeHelper.equals(DxfFileConstants.F_H)
//									|| occupancyTypeHelper.equals(DxfFileConstants.F_K)) {
//								occupantLoadDivisonFactor = exitWidth_F_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_F_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.G)
//									|| occupancyTypeHelper.equals(DxfFileConstants.G_SI)
//									|| occupancyTypeHelper.equals(DxfFileConstants.G_PHI)
//									|| occupancyTypeHelper.equals(DxfFileConstants.G_NPHI)) {
//								occupantLoadDivisonFactor = exitWidth_G_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_G_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.H)) {
//								occupantLoadDivisonFactor = exitWidth_H_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_H_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							} else if (occupancyTypeHelper.equals(DxfFileConstants.I)
//									|| occupancyTypeHelper.equals(DxfFileConstants.I1)
//									|| occupancyTypeHelper.equals(DxfFileConstants.I2)) {
//								occupantLoadDivisonFactor = exitWidth_I_occupantLoadDivisonFactor;
//								occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
//								BigDecimal noOfDoors = exitWidth_I_noOfDoors;
//								BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay;
//								maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
//										noOfDoors, noOfOccupantsPerUnitExitWidthOfStairWay);
//							}
//							totalOccupantLoadForAFloor = totalOccupantLoadForAFloor.add(occupantLoad);
//							listOfMaxOccupantsAllowedThrghExits.add(maxOccupantsAllowedThrghExits);
//						}
//						if (!listOfMaxOccupantsAllowedThrghExits.isEmpty()) {
//							BigDecimal minimumOfMaxOccupantsAllowedThrghExits = listOfMaxOccupantsAllowedThrghExits
//									.get(0);
//							for (BigDecimal occupantsAllowedThroughExits : listOfMaxOccupantsAllowedThrghExits) {
//								if (occupantsAllowedThroughExits
//										.compareTo(minimumOfMaxOccupantsAllowedThrghExits) < 0) {
//									minimumOfMaxOccupantsAllowedThrghExits = occupantsAllowedThroughExits;
//								}
//							}
//							validateRuleOccupantLoad(rule, subRule, totalOccupantLoadForAFloor,
//									minimumOfMaxOccupantsAllowedThrghExits, pl, block, flr, scrutinyDetail2);
//						}
//					}
//				}
//			}
//		}
//
//		return pl;
//	}


	/**
	 * Data class to hold exit width configuration values
	 */
	private static class ExitWidthConfig {
		public BigDecimal exitWidthOccupancyTypeHandlerVal = BigDecimal.ZERO;
		public BigDecimal exitWidthNotOccupancyTypeHandlerVal = BigDecimal.ZERO;
		public BigDecimal exitWidth_A_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_A_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_A_SR_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_A_SR_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_B_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_B_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_C_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_C_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_D_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_D_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_E_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_E_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_F_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_F_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_G_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_G_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_H_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_H_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
		public BigDecimal exitWidth_I_occupantLoadDivisonFactor = BigDecimal.ZERO;
		public BigDecimal exitWidth_I_noOfDoors = BigDecimal.ZERO;
		public BigDecimal exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
	}

	/**
	 * Processes the given plan to validate exit width dimensions. Fetches
	 * permissible values for exit width and validates them against the plan
	 * details.
	 *
	 * @param pl The plan object to process.
	 * @return The processed plan object with scrutiny details added.
	 */
	@Override
	public Plan process(Plan pl) {
		String rule = EXIT_WIDTH_DESC;
		String subRule = null;
		validateExitWidth(pl);

		ExitWidthConfig config = loadExitWidthConfig(pl);

		if (!pl.getBlocks().isEmpty()) {
			for (Block block : pl.getBlocks()) {
				processBlock(block, pl, rule, subRule, config);
			}
		}

		return pl;
	}

	/**
	 * Loads exit width configuration from MDMS rules
	 */
	private ExitWidthConfig loadExitWidthConfig(Plan pl) {
		ExitWidthConfig config = new ExitWidthConfig();

		List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.EXIT_WIDTH.getValue(), false);
		Optional<ExitWidthRequirement> matchedRule = rules.stream()
				.filter(ExitWidthRequirement.class::isInstance)
				.map(ExitWidthRequirement.class::cast)
				.findFirst();

		if (matchedRule.isPresent()) {
			ExitWidthRequirement mdmsRule = matchedRule.get();
			config.exitWidthOccupancyTypeHandlerVal = mdmsRule.getExitWidthOccupancyTypeHandlerVal();
			config.exitWidthNotOccupancyTypeHandlerVal = mdmsRule.getExitWidthNotOccupancyTypeHandlerVal();
			config.exitWidth_A_occupantLoadDivisonFactor = mdmsRule.getExitWidth_A_occupantLoadDivisonFactor();
			config.exitWidth_A_noOfDoors = mdmsRule.getExitWidth_A_noOfDoors();
			config.exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_A_SR_occupantLoadDivisonFactor = mdmsRule.getExitWidth_A_SR_occupantLoadDivisonFactor();
			config.exitWidth_A_SR_noOfDoors = mdmsRule.getExitWidth_A_SR_noOfDoors();
			config.exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_B_occupantLoadDivisonFactor = mdmsRule.getExitWidth_B_occupantLoadDivisonFactor();
			config.exitWidth_B_noOfDoors = mdmsRule.getExitWidth_B_noOfDoors();
			config.exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_C_occupantLoadDivisonFactor = mdmsRule.getExitWidth_C_occupantLoadDivisonFactor();
			config.exitWidth_C_noOfDoors = mdmsRule.getExitWidth_C_noOfDoors();
			config.exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_D_occupantLoadDivisonFactor = mdmsRule.getExitWidth_D_occupantLoadDivisonFactor();
			config.exitWidth_D_noOfDoors = mdmsRule.getExitWidth_D_noOfDoors();
			config.exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_E_occupantLoadDivisonFactor = mdmsRule.getExitWidth_E_occupantLoadDivisonFactor();
			config.exitWidth_E_noOfDoors = mdmsRule.getExitWidth_E_noOfDoors();
			config.exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_F_occupantLoadDivisonFactor = mdmsRule.getExitWidth_F_occupantLoadDivisonFactor();
			config.exitWidth_F_noOfDoors = mdmsRule.getExitWidth_F_noOfDoors();
			config.exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_G_occupantLoadDivisonFactor = mdmsRule.getExitWidth_G_occupantLoadDivisonFactor();
			config.exitWidth_G_noOfDoors = mdmsRule.getExitWidth_G_noOfDoors();
			config.exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_H_occupantLoadDivisonFactor = mdmsRule.getExitWidth_H_occupantLoadDivisonFactor();
			config.exitWidth_H_noOfDoors = mdmsRule.getExitWidth_H_noOfDoors();
			config.exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay();
			config.exitWidth_I_occupantLoadDivisonFactor = mdmsRule.getExitWidth_I_occupantLoadDivisonFactor();
			config.exitWidth_I_noOfDoors = mdmsRule.getExitWidth_I_noOfDoors();
			config.exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay = mdmsRule.getExitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay();
		}

		return config;
	}

	/**
	 * Creates occupancy type value map for exit width validation
	 */
	private List<Map<String, Object>> createOccupancyTypeValueMap(Floor flr, ExitWidthConfig config) {
		List<Map<String, Object>> occupancyTypeValueListMap = new ArrayList<>();

		if (!flr.getOccupancies().isEmpty()) {
			for (Occupancy occupancy : flr.getOccupancies()) {
				Map<String, Object> occupancyTypeValueMap = new HashMap<>();
				String occupancyTypeHelper = StringUtils.EMPTY;
				OccupancyHelperDetail occupancyHelperDetail = null;

				if (occupancy.getTypeHelper().getSubtype() != null) {
					occupancyHelperDetail = occupancy.getTypeHelper().getSubtype();
					occupancyTypeHelper = occupancy.getTypeHelper().getSubtype().getCode();
				} else if (occupancy.getTypeHelper() != null) {
					if (occupancy.getTypeHelper().getType() != null) {
						occupancyHelperDetail = occupancy.getTypeHelper().getType();
						occupancyTypeHelper = occupancy.getTypeHelper().getType().getCode();
					}
				}

				BigDecimal value;
				if (occupancyTypeHelper.equals(DxfFileConstants.A) || occupancyTypeHelper.equals(DxfFileConstants.A_R)
						|| occupancyTypeHelper.equals(DxfFileConstants.A_SR) || occupancyTypeHelper.equals(DxfFileConstants.A_HE)
						|| occupancyTypeHelper.equals(DxfFileConstants.A_AF) || occupancyTypeHelper.equals(DxfFileConstants.A_PO)) {
					value = config.exitWidthOccupancyTypeHandlerVal;
				} else {
					value = config.exitWidthNotOccupancyTypeHandlerVal;
				}

				if (occupancyHelperDetail != null)
					occupancyTypeValueMap.put(E_OCCUPANCY, occupancyHelperDetail.getName());
				occupancyTypeValueMap.put(EXIT_WIDTH, value);
				occupancyTypeValueListMap.add(occupancyTypeValueMap);
			}
		}

		return occupancyTypeValueListMap;
	}

	/**
	 * Finds the most restrictive occupancy and maximum value from occupancy type value map
	 */
	private Map<String, Object> findMostRestrictiveOccupancy(List<Map<String, Object>> occupancyTypeValueListMap) {
		if (occupancyTypeValueListMap.isEmpty()) {
			return new HashMap<>();
		}

		Map<String, Object> mostRestrictiveOccupancyAndMaxValueMap = occupancyTypeValueListMap.get(0);

		for (Map<String, Object> occupancyValueMap : occupancyTypeValueListMap) {
			if (((BigDecimal) occupancyValueMap.get(EXIT_WIDTH)).compareTo(
					(BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH)) == 0) {
				if (mostRestrictiveOccupancyAndMaxValueMap.get(E_OCCUPANCY) != null
						&& !(occupancyValueMap.get(E_OCCUPANCY)).equals(
						mostRestrictiveOccupancyAndMaxValueMap.get(E_OCCUPANCY))) {
					SortedSet<String> uniqueOccupancies = new TreeSet<>();
					String[] occupancyString = (occupancyValueMap.get(E_OCCUPANCY) + " , "
							+ mostRestrictiveOccupancyAndMaxValueMap.get(E_OCCUPANCY))
							.split(" , ");
					for (String str : occupancyString) {
						uniqueOccupancies.add(str);
					}
					String occupancyStr = removeDuplicates(uniqueOccupancies);
					mostRestrictiveOccupancyAndMaxValueMap.put(E_OCCUPANCY, occupancyStr);
				}
				continue;
			}
			if (((BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH))
					.compareTo((BigDecimal) occupancyValueMap.get(EXIT_WIDTH)) < 0) {
				mostRestrictiveOccupancyAndMaxValueMap.putAll(occupancyValueMap);
			}
		}

		return mostRestrictiveOccupancyAndMaxValueMap;
	}

	/**
	 * Calculates occupant load and maximum occupants allowed for a single occupancy
	 */
	private void calculateOccupancyValues(Occupancy occupancy, Floor flr, ExitWidthConfig config,
										  List<BigDecimal> listOfMaxOccupantsAllowedThrghExits, BigDecimal[] totalOccupantLoadForAFloor) {

		BigDecimal occupantLoad = BigDecimal.ZERO;
		BigDecimal maxOccupantsAllowedThrghExits = BigDecimal.ZERO;
		BigDecimal occupantLoadDivisonFactor;
		String occupancyTypeHelper = StringUtils.EMPTY;

		if (occupancy.getTypeHelper() != null) {
			if (occupancy.getTypeHelper().getSubtype() != null) {
				occupancyTypeHelper = occupancy.getTypeHelper().getSubtype().getCode();
			} else if (occupancy.getTypeHelper().getType() != null) {
				occupancyTypeHelper = occupancy.getTypeHelper().getType().getCode();
			}
		}

		if (occupancyTypeHelper.equals(DxfFileConstants.A) || occupancyTypeHelper.equals(DxfFileConstants.A_R)
				|| occupancyTypeHelper.equals(DxfFileConstants.A_AF) || occupancyTypeHelper.equals(DxfFileConstants.A_PO)) {
			occupantLoadDivisonFactor = config.exitWidth_A_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_A_noOfDoors, config.exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.A_SR) || occupancyTypeHelper.equals(DxfFileConstants.A_HE)) {
			occupantLoadDivisonFactor = config.exitWidth_A_SR_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_A_SR_noOfDoors, config.exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.B) || occupancyTypeHelper.equals(DxfFileConstants.B2)
				|| occupancyTypeHelper.equals(DxfFileConstants.B_HEI)) {
			occupantLoadDivisonFactor = config.exitWidth_B_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_B_noOfDoors, config.exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.C) || occupancyTypeHelper.equals(DxfFileConstants.C_MIP)
				|| occupancyTypeHelper.equals(DxfFileConstants.C_MOP) || occupancyTypeHelper.equals(DxfFileConstants.C_MA)) {
			occupantLoadDivisonFactor = config.exitWidth_C_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_C_noOfDoors, config.exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.D) || occupancyTypeHelper.equals(DxfFileConstants.D_AW)
				|| occupancyTypeHelper.equals(DxfFileConstants.D_BT)) {
			occupantLoadDivisonFactor = config.exitWidth_D_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_D_noOfDoors, config.exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.E)) {
			occupantLoadDivisonFactor = config.exitWidth_E_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_E_noOfDoors, config.exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.F) || occupancyTypeHelper.equals(DxfFileConstants.F_PP)
				|| occupancyTypeHelper.equals(DxfFileConstants.F_PA) || occupancyTypeHelper.equals(DxfFileConstants.F_H)
				|| occupancyTypeHelper.equals(DxfFileConstants.F_K)) {
			occupantLoadDivisonFactor = config.exitWidth_F_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_F_noOfDoors, config.exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.G) || occupancyTypeHelper.equals(DxfFileConstants.G_SI)
				|| occupancyTypeHelper.equals(DxfFileConstants.G_PHI) || occupancyTypeHelper.equals(DxfFileConstants.G_NPHI)) {
			occupantLoadDivisonFactor = config.exitWidth_G_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_G_noOfDoors, config.exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.H)) {
			occupantLoadDivisonFactor = config.exitWidth_H_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_H_noOfDoors, config.exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay);
		} else if (occupancyTypeHelper.equals(DxfFileConstants.I) || occupancyTypeHelper.equals(DxfFileConstants.I1)
				|| occupancyTypeHelper.equals(DxfFileConstants.I2)) {
			occupantLoadDivisonFactor = config.exitWidth_I_occupantLoadDivisonFactor;
			occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
			maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr,
					config.exitWidth_I_noOfDoors, config.exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay);
		}

		totalOccupantLoadForAFloor[0] = totalOccupantLoadForAFloor[0].add(occupantLoad);
		listOfMaxOccupantsAllowedThrghExits.add(maxOccupantsAllowedThrghExits);
	}

	/**
	 * Processes a single floor for exit width validation
	 */
	private void processFloor(Floor flr, Plan pl, Block block, String rule, String subRule,
							  ExitWidthConfig config, ScrutinyDetail scrutinyDetail2) {

		BigDecimal totalOccupantLoadForAFloor = BigDecimal.ZERO;
		List<BigDecimal> listOfMaxOccupantsAllowedThrghExits = new ArrayList<>();

		// Create occupancy type value map and validate exit width
		List<Map<String, Object>> occupancyTypeValueListMap = createOccupancyTypeValueMap(flr, config);
		if (!occupancyTypeValueListMap.isEmpty()) {
			Map<String, Object> mostRestrictiveOccupancyAndMaxValueMap = findMostRestrictiveOccupancy(occupancyTypeValueListMap);
			validateExitWidth(flr, pl, subRule, rule, block,
					(BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH),
					(String) mostRestrictiveOccupancyAndMaxValueMap.get(E_OCCUPANCY));
		}

		// Calculate occupant loads and maximum occupants allowed
		BigDecimal[] totalOccupantArray = {totalOccupantLoadForAFloor};
		for (Occupancy occupancy : flr.getOccupancies()) {
			calculateOccupancyValues(occupancy, flr, config, listOfMaxOccupantsAllowedThrghExits, totalOccupantArray);
		}
		totalOccupantLoadForAFloor = totalOccupantArray[0];

		// Find minimum of maximum occupants allowed and validate
		if (!listOfMaxOccupantsAllowedThrghExits.isEmpty()) {
			BigDecimal minimumOfMaxOccupantsAllowedThrghExits = listOfMaxOccupantsAllowedThrghExits.get(0);
			for (BigDecimal occupantsAllowedThroughExits : listOfMaxOccupantsAllowedThrghExits) {
				if (occupantsAllowedThroughExits.compareTo(minimumOfMaxOccupantsAllowedThrghExits) < 0) {
					minimumOfMaxOccupantsAllowedThrghExits = occupantsAllowedThroughExits;
				}
			}
			validateRuleOccupantLoad(rule, subRule, totalOccupantLoadForAFloor,
					minimumOfMaxOccupantsAllowedThrghExits, pl, block, flr, scrutinyDetail2);
		}
	}

	/**
	 * Processes a single block for exit width validation
	 */
	private void processBlock(Block block, Plan pl, String rule, String subRule, ExitWidthConfig config) {
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, FLOOR);
		scrutinyDetail.addColumnHeading(3, E_OCCUPANCY);
		scrutinyDetail.addColumnHeading(4, REQUIRED);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);
		scrutinyDetail.setKey(BLOCK + block.getNumber() + UNDERSCORE + EXIT_WIDTH_MINIMUM);

		ScrutinyDetail scrutinyDetail2 = new ScrutinyDetail();
		scrutinyDetail2.addColumnHeading(1, RULE_NO);
		scrutinyDetail2.addColumnHeading(2, FLOOR);
		scrutinyDetail2.addColumnHeading(3, REQUIRED);
		scrutinyDetail2.addColumnHeading(4, PROVIDED);
		scrutinyDetail2.addColumnHeading(5, STATUS);
		scrutinyDetail2.setKey(BLOCK + block.getNumber() + UNDERSCORE + EXIT_WIDTH_MAXIMUM);

		if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
			if (ProcessHelper.checkExemptionConditionForBuildingParts(block)) {
				return;
			}
			for (Floor flr : block.getBuilding().getFloors()) {
				processFloor(flr, pl, block, rule, subRule, config, scrutinyDetail2);
			}
		}
	}
	
	
    /**
     * Validates the occupant load for a floor against the maximum allowed occupants through exits.
     *
     * @param rule The rule description.
     * @param subRule The sub-rule identifier.
     * @param occupantLoadInAFlr The occupant load for the floor.
     * @param maxOccupantsAllowedThrghExits The maximum allowed occupants through exits.
     * @param pl The plan object.
     * @param block The block containing the floor.
     * @param floor The floor being validated.
     * @param scrutinyDetail2 The scrutiny detail object for validation.
     */
    private void validateRuleOccupantLoad(String rule, String subRule, BigDecimal occupantLoadInAFlr,
            BigDecimal maxOccupantsAllowedThrghExits, Plan pl, Block block, Floor floor, ScrutinyDetail scrutinyDetail2) {
        boolean valid = false;
        boolean isTypicalRepititiveFloor = false;
        if (maxOccupantsAllowedThrghExits != null && occupantLoadInAFlr != null
                && maxOccupantsAllowedThrghExits.compareTo(BigDecimal.ZERO) > 0
                && occupantLoadInAFlr.compareTo(BigDecimal.ZERO) > 0) {
            Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);
            if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
                if (maxOccupantsAllowedThrghExits.compareTo(occupantLoadInAFlr) >= 0) {
                    valid = true;
                    subRule = SUBRULE_42_3;
                }
                String value = typicalFloorValues.get(TYPICAL_FLOOR) != null ? (String) typicalFloorValues.get(TYPICAL_FLOOR)
                        : FLOOR_SPACED + floor.getNumber();
                if (valid) {
                    setReportOutputDetailsWithoutOccupancy(pl, subRule, value, occupantLoadInAFlr.toString(),
                            maxOccupantsAllowedThrghExits.toString(),
                            Result.Accepted.getResultVal(), scrutinyDetail2);
                } else {
                    setReportOutputDetailsWithoutOccupancy(pl, subRule, value, occupantLoadInAFlr.toString(),
                            maxOccupantsAllowedThrghExits.toString(),
                            Result.Not_Accepted.getResultVal(), scrutinyDetail2);

                }
            }
        }
    }

/**
 * Calculates the maximum number of occupants allowed through exits for a given floor.
 *
 * @param floor The floor object.
 * @param noOfDoors The number of doors on the floor.
 * @param noOfOccupantsPerUnitExitWidthOfStairWay The number of occupants allowed per unit exit width of stairways.
 * @return The maximum number of occupants allowed through exits.
 */
    private BigDecimal getMaximumNumberOfOccupantsAllwdThroughExits(Floor floor, BigDecimal noOfDoors,
            BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay) {
        if (!floor.getExitWidthDoor().isEmpty() || !floor.getExitWidthStair().isEmpty()) {
            Double sumOfAccessWidthDoor = Double.valueOf(0);
            Double sumOfAccessWidthStair = Double.valueOf(0);
            BigDecimal augend1 = BigDecimal.ZERO;
            BigDecimal augend2 = BigDecimal.ZERO;
            if (!floor.getExitWidthDoor().isEmpty()) {
                sumOfAccessWidthDoor = floor.getExitWidthDoor().stream().mapToDouble(BigDecimal::doubleValue).sum();
            }
            if (!floor.getExitWidthStair().isEmpty()) {
                sumOfAccessWidthStair = floor.getExitWidthStair().stream().mapToDouble(BigDecimal::doubleValue).sum();
            }
            if (sumOfAccessWidthDoor.compareTo(Double.valueOf(0)) > 0) {
                Double roundedValue = Math.floor(sumOfAccessWidthDoor * Double.valueOf(4)) / Double.valueOf(4);
                augend1 = BigDecimal.valueOf(Math.ceil(roundedValue * noOfDoors.doubleValue() / 0.5d));
                /* augend1 = (BigDecimal.valueOf(roundedValue).multiply(noOfDoors)).divide(BigDecimal.valueOf(0.5)); */
            }
            if (sumOfAccessWidthStair.compareTo(Double.valueOf(0)) > 0) {
                Double roundedValue = Math.floor(sumOfAccessWidthStair * Double.valueOf(4)) / Double.valueOf(4);
                augend2 = BigDecimal
                        .valueOf(Math.ceil(roundedValue * noOfOccupantsPerUnitExitWidthOfStairWay.doubleValue() / 0.5d));
                /*
                 * augend2 =
                 * (BigDecimal.valueOf(roundedValue).multiply(noOfOccupantsPerUnitExitWidthOfStairWay)).divide(BigDecimal.valueOf(
                 * 0.5));
                 */
            }
            return augend1.add(augend2);
        }
        return BigDecimal.ZERO;
    }

/**
 * Calculates the occupant load for a given floor based on its built-up area and division factor.
 *
 * @param occupancy The occupancy object containing details of the floor.
 * @param occupantLoadDivisonFactor The division factor for calculating occupant load.
 * @return The calculated occupant load for the floor.
 */
    private BigDecimal getOccupantLoadOfAFloor(Occupancy occupancy, BigDecimal occupantLoadDivisonFactor) {
        return BigDecimal
                .valueOf(Math.ceil(occupancy.getBuiltUpArea().divide(occupantLoadDivisonFactor, DECIMALDIGITS_MEASUREMENTS,
                        ROUNDMODE_MEASUREMENTS).doubleValue()));
    }

/**
 * Validates the exit width for a given floor against the permissible value.
 * Ensures that the minimum exit width provided meets the required value.
 *
 * @param floor The floor object being validated.
 * @param pl The plan object.
 * @param subRule The sub-rule identifier.
 * @param rule The rule description.
 * @param block The block containing the floor.
 * @param value The permissible exit width value.
 * @param occupancyType The type of occupancy for the floor.
 */
    private void validateExitWidth(Floor floor, Plan pl, String subRule, String rule, Block block, BigDecimal value,
            String occupancyType) {
        // calculate minimum of exit widths provided and validate for that.
        boolean isTypicalRepititiveFloor = false;
        if (!floor.getExitWidthDoor().isEmpty()) {
            BigDecimal minimumExitWidth = floor.getExitWidthDoor().get(0);
            for (BigDecimal exitWidthDoor : floor.getExitWidthDoor()) {
                if (exitWidthDoor.compareTo(minimumExitWidth) < 0) {
                    minimumExitWidth = exitWidthDoor;
                }
            }
            Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);
            if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
                Boolean valid = false;
                subRule = SUBRULE_42_3;
                if (minimumExitWidth.compareTo(value) >= 0) {
                    valid = true;
                }
                String typclFloor = typicalFloorValues.get(TYPICAL_FLOOR) != null
                        ? (String) typicalFloorValues.get(TYPICAL_FLOOR)
                        : FLOOR_SPACED + floor.getNumber();
                if (valid) {
                    setReportOutputDetails(pl, subRule, typclFloor, occupancyType, value + DcrConstants.IN_METER,
                            minimumExitWidth + DcrConstants.IN_METER,
                            Result.Accepted.getResultVal());
                } else {
                    setReportOutputDetails(pl, subRule, typclFloor, occupancyType, value + DcrConstants.IN_METER,
                            minimumExitWidth + DcrConstants.IN_METER,
                            Result.Accepted.getResultVal());
                }
            }
        }
    }

/**
 * Sets the scrutiny details for exit width validation with occupancy information.
 *
 * @param pl The plan object.
 * @param ruleNo The rule number.
 * @param floor The floor being validated.
 * @param occupancy The occupancy type for the floor.
 * @param expected The expected exit width value.
 * @param actual The actual exit width value provided.
 * @param status The validation status (Accepted/Not Accepted).
 */
    private void setReportOutputDetails(Plan pl, String ruleNo, String floor, String occupancy, String expected, String actual,
            String status) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(ruleNo);
		detail.setFloorNo(floor);
		detail.setOccupancy(occupancy);
		detail.setRequired(expected);
		detail.setProvided(actual);
		detail.setStatus(status);

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
    }

/**
 * Sets the scrutiny details for exit width validation without occupancy information.
 *
 * @param pl The plan object.
 * @param ruleNo The rule number.
 * @param floor The floor being validated.
 * @param expected The expected exit width value.
 * @param actual The actual exit width value provided.
 * @param status The validation status (Accepted/Not Accepted).
 * @param scrutinyDetail2 The scrutiny detail object for validation.
 */
    private void setReportOutputDetailsWithoutOccupancy(Plan pl, String ruleNo, String floor, String expected, String actual,
            String status, ScrutinyDetail scrutinyDetail2) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(ruleNo);
		detail.setFloorNo(floor);
		detail.setRequired(expected);
		detail.setProvided(actual);
		detail.setStatus(status);

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail2, pl, details);
    }

/**
 * Removes duplicate entries from a sorted set of strings and returns a comma-separated string.
 *
 * @param uniqueData The sorted set of unique strings.
 * @return A comma-separated string of unique values.
 */
    private String removeDuplicates(SortedSet<String> uniqueData) {
        StringBuilder str = new StringBuilder();
        List<String> unqList = new ArrayList<>(uniqueData);
        for (String unique : unqList) {
            str.append(unique);
            if (!unique.equals(unqList.get(unqList.size() - 1))) {
                str.append(COMMA);
            }
        }
        return str.toString();
    }

/**
 * Validates the given plan object for exit width compliance.
 * Ensures that either exit width for doors or stairs is defined for each floor.
 *
 * @param pl The plan object to validate.
 * @return The validated plan object.
 */
    @Override
    public Plan validate(Plan pl) {
        validateExitWidth(pl);
        return pl;
    }

/**
 * Returns an empty map as no amendments are defined for this feature.
 *
 * @return An empty map of amendments.
 */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}