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

import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ExitWidth_Citya extends FeatureProcess {

    private static final String EXIT_WIDTH_DESC = "Exit Width";
    // private static final String SUB_RULE_DESCRIPTION = "Minimum exit width";
    public static final BigDecimal VAL_0_75 = BigDecimal.valueOf(0.75);
    public static final BigDecimal VAL_1_2 = BigDecimal.valueOf(1.2);
    private static final String SUBRULE_42_3 = "42-3";
    // private static final String SUB_RULE_OCCUPANTS_DESCRIPTION = "Maximum number of occupants that can be allowed through";
    private static final String OCCUPANCY = "Occupancy";
    private static final String EXIT_WIDTH = "Exit Width";
    private static final String FLOOR = "Floor";

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

    @Override
    public Plan process(Plan pl) {

        String rule = EXIT_WIDTH_DESC;
        String subRule = null;
        validateExitWidth(pl);
        
        BigDecimal exitWidthOccupancyTypeHandlerVal = BigDecimal.ZERO;
        BigDecimal exitWidthNotOccupancyTypeHandlerVal = BigDecimal.ZERO;
        BigDecimal exitWidth_A_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_A_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_A_SR_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_A_SR_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_B_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_B_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_C_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_C_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_D_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_D_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_E_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_E_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_F_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_F_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_G_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_G_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_H_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_H_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        BigDecimal exitWidth_I_occupantLoadDivisonFactor = BigDecimal.ZERO;
        BigDecimal exitWidth_I_noOfDoors = BigDecimal.ZERO;
        BigDecimal exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.ZERO;
        
        String occupancyName = null;
		
			 String feature = "Basement";
				
				Map<String, Object> params = new HashMap<>();
				if(DxfFileConstants.A
						.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())){
					occupancyName = "Residential";
				}

				params.put("feature", feature);
				params.put("occupancy", occupancyName);
				
				Map<String,List<Map<String,Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
				
				ArrayList<String> valueFromColumn = new ArrayList<>();
				valueFromColumn.add("exitWidthOccupancyTypeHandlerVal");
				valueFromColumn.add("exitWidthNotOccupancyTypeHandlerVal");
				valueFromColumn.add("exitWidth_A_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_A_noOfDoors");
				valueFromColumn.add("exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_A_SR_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_A_SR_noOfDoors");
				valueFromColumn.add("exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_B_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_B_noOfDoors");
				valueFromColumn.add("exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_C_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_C_noOfDoors");
				valueFromColumn.add("exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_D_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_D_noOfDoors");
				valueFromColumn.add("exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_E_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_E_noOfDoors");
				valueFromColumn.add("exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_F_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_F_noOfDoors");
				valueFromColumn.add("exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_G_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_G_noOfDoors");
				valueFromColumn.add("exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_H_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_H_noOfDoors");
				valueFromColumn.add("exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay");
				valueFromColumn.add("exitWidth_I_occupantLoadDivisonFactor");
				valueFromColumn.add("exitWidth_I_noOfDoors");
				valueFromColumn.add("exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay");


				List<Map<String, Object>> permissibleValue = new ArrayList<>();
			
				try {
					permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
					LOG.info("permissibleValue" + permissibleValue);
					

					} catch (NullPointerException e) {

						LOG.error("Permissible Value for ExitWidth not found--------", e);
						return null;
					}

				if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey("exitWidthOccupancyTypeHandlerVal")) {
					exitWidthOccupancyTypeHandlerVal = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidthOccupancyTypeHandlerVal").toString()));
					exitWidthNotOccupancyTypeHandlerVal = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidthNotOccupancyTypeHandlerVal").toString()));
					exitWidth_A_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_A_occupantLoadDivisonFactor").toString()));
					exitWidth_A_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_A_noOfDoors").toString()));
					exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_A_SR_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_A_SR_occupantLoadDivisonFactor").toString()));
					exitWidth_A_SR_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_A_SR_noOfDoors").toString()));
					exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_B_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_B_occupantLoadDivisonFactor").toString()));
					exitWidth_B_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_B_noOfDoors").toString()));
					exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_C_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_C_occupantLoadDivisonFactor").toString()));
					exitWidth_C_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_C_noOfDoors").toString()));
					exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_D_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_D_occupantLoadDivisonFactor").toString()));
					exitWidth_D_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_D_noOfDoors").toString()));
					exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_E_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_E_occupantLoadDivisonFactor").toString()));
					exitWidth_E_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_E_noOfDoors").toString()));
					exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_F_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_F_occupantLoadDivisonFactor").toString()));
					exitWidth_F_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_F_noOfDoors").toString()));
					exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_G_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_G_occupantLoadDivisonFactor").toString()));
					exitWidth_G_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_G_noOfDoors").toString()));
					exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_H_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_H_occupantLoadDivisonFactor").toString()));
					exitWidth_H_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_H_noOfDoors").toString()));
					exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
					exitWidth_I_occupantLoadDivisonFactor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_I_occupantLoadDivisonFactor").toString()));
					exitWidth_I_noOfDoors = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_I_noOfDoors").toString()));
					exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay").toString()));
				}
        
        
        
        if (!pl.getBlocks().isEmpty()) {
            blk: for (Block block : pl.getBlocks()) {
                scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, FLOOR);
                scrutinyDetail.addColumnHeading(3, OCCUPANCY);
                scrutinyDetail.addColumnHeading(4, REQUIRED);
                scrutinyDetail.addColumnHeading(5, PROVIDED);
                scrutinyDetail.addColumnHeading(6, STATUS);
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" +
                        "Exit Width- Minimum Exit Width");
                ScrutinyDetail scrutinyDetail2 = new ScrutinyDetail();
                scrutinyDetail2.addColumnHeading(1, RULE_NO);
                scrutinyDetail2.addColumnHeading(2, FLOOR);
                scrutinyDetail2.addColumnHeading(3, REQUIRED);
                scrutinyDetail2.addColumnHeading(4, PROVIDED);
                scrutinyDetail2.addColumnHeading(5, STATUS);
                scrutinyDetail2.setKey("Block_" + block.getNumber() + "_" +
                        "Exit Width- Maximum Occupant Load");
                if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
                    if (ProcessHelper.checkExemptionConditionForBuildingParts(block)) {
                        continue blk;
                    }
                    for (Floor flr : block.getBuilding().getFloors()) {
                        BigDecimal totalOccupantLoadForAFloor = BigDecimal.ZERO;
                        List<BigDecimal> listOfMaxOccupantsAllowedThrghExits = new ArrayList<>();
                        BigDecimal value;
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
                                if (occupancyTypeHelper.equals(DxfFileConstants.A)
                                        || occupancyTypeHelper.equals(DxfFileConstants.A_R) ||
                                        occupancyTypeHelper.equals(DxfFileConstants.A_SR)
                                        || occupancyTypeHelper.equals(DxfFileConstants.A_HE) ||
                                        occupancyTypeHelper.equals(DxfFileConstants.A_AF)
                                        || occupancyTypeHelper.equals(DxfFileConstants.A_PO)) {
                                    value = exitWidthOccupancyTypeHandlerVal;
                                } else {
                                    value = exitWidthNotOccupancyTypeHandlerVal;
                                }
                                if(occupancyHelperDetail != null)
                                occupancyTypeValueMap.put(OCCUPANCY, occupancyHelperDetail.getName());
                                occupancyTypeValueMap.put(EXIT_WIDTH, value);
                                occupancyTypeValueListMap.add(occupancyTypeValueMap);
                            }
                            /*
                             * calculating maximum exit width, if map has two enteries with same exit width , occupancy needs to
                             * be comma separated if it is different and it need not be duplicated if occupancy is same
                             */
                            if (!occupancyTypeValueListMap.isEmpty()) {
                                Map<String, Object> mostRestrictiveOccupancyAndMaxValueMap = occupancyTypeValueListMap.get(0);
                                for (Map<String, Object> occupancyValueMap : occupancyTypeValueListMap) {
                                    if (((BigDecimal) occupancyValueMap.get(EXIT_WIDTH)).compareTo(
                                            (BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH)) == 0) {
                                        if (mostRestrictiveOccupancyAndMaxValueMap.get(OCCUPANCY) != null && !(occupancyValueMap.get(OCCUPANCY))
                                                .equals(mostRestrictiveOccupancyAndMaxValueMap.get(OCCUPANCY))) {
                                            SortedSet<String> uniqueOccupancies = new TreeSet<>();
                                            String[] occupancyString = (occupancyValueMap.get(OCCUPANCY) + " , " +
                                                    mostRestrictiveOccupancyAndMaxValueMap.get(OCCUPANCY)).split(" , ");
                                            for (String str : occupancyString) {
                                                uniqueOccupancies.add(str);
                                            }
                                            String occupancyStr = removeDuplicates(uniqueOccupancies);
                                            mostRestrictiveOccupancyAndMaxValueMap.put(OCCUPANCY, occupancyStr);
                                        }
                                        continue;
                                    }
                                    if (((BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH))
                                            .compareTo((BigDecimal) occupancyValueMap.get(EXIT_WIDTH)) < 0) {
                                        mostRestrictiveOccupancyAndMaxValueMap.putAll(occupancyValueMap);
                                    }
                                }
                                validateExitWidth(flr, pl, subRule, rule,
                                        block, (BigDecimal) mostRestrictiveOccupancyAndMaxValueMap.get(EXIT_WIDTH),
                                        (String) mostRestrictiveOccupancyAndMaxValueMap.get(OCCUPANCY));
                            }
                        }
                        for (Occupancy occupancy : flr.getOccupancies()) {
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
                            if (occupancyTypeHelper.equals(DxfFileConstants.A) ||
                                    occupancyTypeHelper.equals(DxfFileConstants.A_R)
                                    || occupancyTypeHelper.equals(DxfFileConstants.A_AF) ||
                                    occupancyTypeHelper.equals(DxfFileConstants.A_PO)) {
                                occupantLoadDivisonFactor = exitWidth_A_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_A_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.A_SR)
                                    || occupancyTypeHelper.equals(DxfFileConstants.A_HE)) {
                                occupantLoadDivisonFactor = exitWidth_A_SR_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy,
                                        occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_A_SR_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.B)
                                    || occupancyTypeHelper.equals(DxfFileConstants.B2) ||
                                    occupancyTypeHelper.equals(DxfFileConstants.B_HEI)) {
                                occupantLoadDivisonFactor = exitWidth_B_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_B_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.C)
                                    || occupancyTypeHelper.equals(DxfFileConstants.C_MIP) ||
                                    occupancyTypeHelper.equals(DxfFileConstants.C_MOP)
                                    || occupancyTypeHelper.equals(DxfFileConstants.C_MA)) {
                                occupantLoadDivisonFactor = exitWidth_C_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy,
                                        occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_C_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.D)
                                    || occupancyTypeHelper.equals(DxfFileConstants.D_AW) ||
                                    occupancyTypeHelper.equals(DxfFileConstants.D_BT)) {
                                occupantLoadDivisonFactor = exitWidth_D_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_D_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.E)) {
                                occupantLoadDivisonFactor = exitWidth_E_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_E_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.F)
                                    || occupancyTypeHelper.equals(DxfFileConstants.F_PP) ||
                                    occupancyTypeHelper.equals(DxfFileConstants.F_PA)
                                    || occupancyTypeHelper.equals(DxfFileConstants.F_H) ||
                                    occupancyTypeHelper.equals(DxfFileConstants.F_K)) {
                                occupantLoadDivisonFactor = exitWidth_F_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_F_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.G)
                                    || occupancyTypeHelper.equals(DxfFileConstants.G_SI)
                                    || occupancyTypeHelper.equals(DxfFileConstants.G_PHI)
                                    || occupancyTypeHelper.equals(DxfFileConstants.G_NPHI)) {
                                occupantLoadDivisonFactor = exitWidth_G_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy,
                                        occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_G_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.H)) {
                                occupantLoadDivisonFactor = exitWidth_H_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy, occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_H_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            } else if (occupancyTypeHelper.equals(DxfFileConstants.I)
                                    || occupancyTypeHelper.equals(DxfFileConstants.I1)
                                    || occupancyTypeHelper.equals(DxfFileConstants.I2)) {
                                occupantLoadDivisonFactor = exitWidth_I_occupantLoadDivisonFactor;
                                occupantLoad = getOccupantLoadOfAFloor(occupancy,
                                        occupantLoadDivisonFactor);
                                BigDecimal noOfDoors = exitWidth_I_noOfDoors;
                                BigDecimal noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay;
                                maxOccupantsAllowedThrghExits = getMaximumNumberOfOccupantsAllwdThroughExits(flr, noOfDoors,
                                        noOfOccupantsPerUnitExitWidthOfStairWay);
                            }
                            totalOccupantLoadForAFloor = totalOccupantLoadForAFloor.add(occupantLoad);
                            listOfMaxOccupantsAllowedThrghExits.add(maxOccupantsAllowedThrghExits);
                        }
                        if (!listOfMaxOccupantsAllowedThrghExits.isEmpty()) {
                            BigDecimal minimumOfMaxOccupantsAllowedThrghExits = listOfMaxOccupantsAllowedThrghExits.get(0);
                            for (BigDecimal occupantsAllowedThroughExits : listOfMaxOccupantsAllowedThrghExits) {
                                if (occupantsAllowedThroughExits.compareTo(minimumOfMaxOccupantsAllowedThrghExits) < 0) {
                                    minimumOfMaxOccupantsAllowedThrghExits = occupantsAllowedThroughExits;
                                }
                            }
                            validateRuleOccupantLoad(rule, subRule,
                                    totalOccupantLoadForAFloor, minimumOfMaxOccupantsAllowedThrghExits, pl, block, flr,
                                    scrutinyDetail2);
                        }
                    }
                }
            }
        }

        return pl;
    }

    private void validateRuleOccupantLoad(String rule, String subRule, BigDecimal occupantLoadInAFlr,
            BigDecimal maxOccupantsAllowedThrghExits, Plan pl, Block block, Floor floor, ScrutinyDetail scrutinyDetail2) {
        boolean valid = false;
        boolean isTypicalRepititiveFloor = false;
        if (maxOccupantsAllowedThrghExits != null && occupantLoadInAFlr != null
                && maxOccupantsAllowedThrghExits.compareTo(BigDecimal.ZERO) > 0
                && occupantLoadInAFlr.compareTo(BigDecimal.ZERO) > 0) {
            Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);
            if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
                if (maxOccupantsAllowedThrghExits.compareTo(occupantLoadInAFlr) >= 0) {
                    valid = true;
                    subRule = SUBRULE_42_3;
                }
                String value = typicalFloorValues.get("typicalFloors") != null ? (String) typicalFloorValues.get("typicalFloors")
                        : " floor " + floor.getNumber();
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

    private BigDecimal getOccupantLoadOfAFloor(Occupancy occupancy, BigDecimal occupantLoadDivisonFactor) {
        return BigDecimal
                .valueOf(Math.ceil(occupancy.getBuiltUpArea().divide(occupantLoadDivisonFactor, DECIMALDIGITS_MEASUREMENTS,
                        ROUNDMODE_MEASUREMENTS).doubleValue()));
    }

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
            if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
                Boolean valid = false;
                subRule = SUBRULE_42_3;
                if (minimumExitWidth.compareTo(value) >= 0) {
                    valid = true;
                }
                String typclFloor = typicalFloorValues.get("typicalFloors") != null
                        ? (String) typicalFloorValues.get("typicalFloors")
                        : " floor " + floor.getNumber();
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

    private void setReportOutputDetails(Plan pl, String ruleNo, String floor, String occupancy, String expected, String actual,
            String status) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(FLOOR, floor);
        details.put(OCCUPANCY, occupancy);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    private void setReportOutputDetailsWithoutOccupancy(Plan pl, String ruleNo, String floor, String expected, String actual,
            String status, ScrutinyDetail scrutinyDetail2) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(FLOOR, floor);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail2.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail2);
    }

    private String removeDuplicates(SortedSet<String> uniqueData) {
        StringBuilder str = new StringBuilder();
        List<String> unqList = new ArrayList<>(uniqueData);
        for (String unique : unqList) {
            str.append(unique);
            if (!unique.equals(unqList.get(unqList.size() - 1))) {
                str.append(" , ");
            }
        }
        return str.toString();
    }

    @Override
    public Plan validate(Plan pl) {
        validateExitWidth(pl);
        return pl;
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}