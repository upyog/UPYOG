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
import static org.egov.edcr.constants.CommonKeyConstants.COMMON_PARKING;
import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.PARKING_SLOT;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.constants.RuleKeyConstants.FOUR_P_TWO_P_ONE;
import static org.egov.edcr.utility.DcrConstants.SQMTRS;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.ParkingDetails;
import org.egov.common.entity.edcr.ParkingHelper;
import org.egov.common.entity.edcr.ParkingRequirement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.FeatureRuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Parking extends FeatureProcess {

    private static final Logger LOGGER = LogManager.getLogger(Parking.class);
    
    @Autowired
   	MDMSCacheManager cache;

    @Override
    public Plan validate(Plan pl) {
        //validateDimensions(pl);
        return pl;
    }

    @Override
    public Plan process(Plan pl) {
        validate(pl);
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(COMMON_PARKING);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        
        processParking(pl);
        //processMechanicalParking(pl);
        return pl;
    }

    private void validateDimensions(Plan pl) {
        ParkingDetails parkDtls = pl.getParkingDetails();
        if (!parkDtls.getCars().isEmpty()) {
            int count = 0;
            for (Measurement m : parkDtls.getCars())
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0)
                    count++;
            if (count > 0)
                pl.addError(PARKING_SLOT, PARKING_SLOT + count + SLOT_HAVING_GT_4_PTS);
        }

        if (!parkDtls.getOpenCars().isEmpty()) {
            int count = 0;
            for (Measurement m : parkDtls.getOpenCars())
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0)
                    count++;
            if (count > 0)
                pl.addError(OPEN_PARKING_DIM_DESC, OPEN_PARKING_DIM_DESC + count + SLOT_HAVING_GT_4_PTS);
        }

        if (!parkDtls.getCoverCars().isEmpty()) {
            int count = 0;
            for (Measurement m : parkDtls.getCoverCars())
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0)
                    count++;
            if (count > 0)
                pl.addError(COVER_PARKING_DIM_DESC, COVER_PARKING_DIM_DESC + count + SLOT_HAVING_GT_4_PTS);
        }

        if (!parkDtls.getCoverCars().isEmpty()) {
            int count = 0;
            for (Measurement m : parkDtls.getBasementCars())
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0)
                    count++;
            if (count > 0)
                pl.addError(BSMNT_PARKING_DIM_DESC, BSMNT_PARKING_DIM_DESC + count + SLOT_HAVING_GT_4_PTS);
        }

        if (!parkDtls.getSpecial().isEmpty()) {
            int count = 0;
            for (Measurement m : parkDtls.getDisabledPersons())
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0)
                    count++;
            if (count > 0)
                pl.addError(SPECIAL_PARKING_DIM_DESC, SPECIAL_PARKING_DIM_DESC + count
                        + N_OF_DA_PARKING_SLOT_POLYGON_NOT_HAVING_4_POINTS);
        }
        
        if (!parkDtls.getLoadUnload().isEmpty()) {
            int count = 0;
            for (Measurement m : parkDtls.getLoadUnload())
                if (m.getArea().compareTo(BigDecimal.valueOf(30)) < 0)
                    count++;
            if (count > 0)
                pl.addError(LOAD_UNLOAD, count + LOAD_UNLOAD_PARKING_SPACE_NOT_CONTAIN_30M2);
        }
        
        if (!parkDtls.getMechParking().isEmpty()) {
            int count = 0;
            for (Measurement m : parkDtls.getMechParking())
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0)
                    count++;
            if (count > 0)
                pl.addError(MECHANICAL_PARKING, count
                        + NO_MECHANICAL_PARKING_SLOT_POLYGON_NOT_4_PTS);
        }
        
        if (!parkDtls.getTwoWheelers().isEmpty()) {
			int count = 0;
			for (Measurement m : parkDtls.getTwoWheelers())
				if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0)
					count++;
			if (count > 0)
				pl.addError(TWO_WHEELER_DIM_DESC, TWO_WHEELER_DIM_DESC + count
						+ NO_TWO_WHEELER_PARKING_SLOT_POLYGON_4_PTS);
		}
    }

    public void processParking(Plan pl) {
        ParkingHelper helper = new ParkingHelper();
        BigDecimal plotArea = pl.getPlot() != null ? pl.getPlot().getArea() : BigDecimal.ZERO;
        ScrutinyDetail scrutinyDetail1 = initializeScrutinyDetail();

        OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
                ? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
                : null;
        BigDecimal totalBuiltupArea = pl.getOccupancies().stream().map(Occupancy::getBuiltUpArea)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ParkingAreas parkingAreas = calculateParkingAreas(pl);

        double totalECS = calculateTotalECS(helper, parkingAreas);

        Double requiredCarParkArea = 0d;
        Double requiredVisitorParkArea = 0d;
        BigDecimal providedVisitorParkArea = BigDecimal.ZERO;

        validateSpecialParking(pl, helper, totalBuiltupArea);

        ParkingRuleResult ruleResult = fetchApplicableRule(pl, plotArea);

        if (mostRestrictiveOccupancy != null && A.equals(mostRestrictiveOccupancy.getType().getCode())) {
            requiredCarParkArea = calculateRequiredParkingArea(parkingAreas, ruleResult.ecs, ruleResult.noOfRequiredParking);
        }

        BigDecimal requiredCarParkingArea = Util.roundOffTwoDecimal(BigDecimal.valueOf(requiredCarParkArea));
        BigDecimal totalProvidedCarParkingArea = Util.roundOffTwoDecimal(parkingAreas.getTotal());
        BigDecimal requiredVisitorParkingArea = Util.roundOffTwoDecimal(BigDecimal.valueOf(requiredVisitorParkArea));
        BigDecimal roundedVisitorParkingArea = Util.roundOffTwoDecimal(providedVisitorParkArea);

        if (parkingAreas.getTotal().doubleValue() == 0) {
            pl.addError(RULE__DESCRIPTION, getLocaleMessage("msg.error.not.defined", RULE__DESCRIPTION));
        } else if (requiredCarParkArea > 0 && totalProvidedCarParkingArea.compareTo(requiredCarParkingArea) < 0) {
            setReportOutputDetails1(pl, FOUR_P_TWO_P_ONE, PARKING_STRING,
                    ruleResult.noOfRequiredParking + ECS_STRING + PLOTAREA_STRING + plotArea + CLOSING_BRACKET,
                    totalECS + ECS_STRING, Result.Not_Accepted.getResultVal());
        } else {
            setReportOutputDetails1(pl, FOUR_P_TWO_P_ONE, PARKING_STRING,
                    ruleResult.noOfRequiredParking + ECS_STRING + PLOTAREA_STRING + plotArea + CLOSING_BRACKET,
                    totalECS + ECS_STRING, Result.Accepted.getResultVal());
        }

        if (requiredVisitorParkArea > 0 && roundedVisitorParkingArea.compareTo(requiredVisitorParkingArea) < 0) {
            setReportOutputDetails(pl, SUB_RULE_40_10, SUB_RULE_40_10_DESCRIPTION, requiredVisitorParkingArea + SQMTRS,
                    roundedVisitorParkingArea + SQMTRS, Result.Not_Accepted.getResultVal());
        } else if (requiredVisitorParkArea > 0) {
            setReportOutputDetails(pl, SUB_RULE_40_10, SUB_RULE_40_10_DESCRIPTION, requiredVisitorParkingArea + SQMTRS,
                    roundedVisitorParkingArea + SQMTRS, Result.Accepted.getResultVal());
        }

        addIndividualParkingReports(pl, parkingAreas);
        LOGGER.info("******************Require no of Car Parking***************" + helper.totalRequiredCarParking);
    }

    private ScrutinyDetail initializeScrutinyDetail() {
        ScrutinyDetail detail = new ScrutinyDetail();
        detail.addColumnHeading(1, RULE_NO);
        detail.addColumnHeading(2, DESCRIPTION);
        detail.addColumnHeading(3, EMPTY_STRING);
        detail.addColumnHeading(4, REQUIRED);
        detail.addColumnHeading(5, PROVIDED);
        detail.addColumnHeading(6, STATUS);
        return detail;
    }

    private ParkingAreas calculateParkingAreas(Plan pl) {
        BigDecimal cover = BigDecimal.ZERO;
        BigDecimal basement = BigDecimal.ZERO;

        for (Block block : pl.getBlocks()) {
            for (Floor floor : block.getBuilding().getFloors()) {
                cover = cover.add(floor.getParking().getCoverCars().stream().map(Measurement::getArea)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.UP);
                basement = basement.add(floor.getParking().getBasementCars().stream().map(Measurement::getArea)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.UP);
            }
        }

        BigDecimal open = pl.getParkingDetails().getOpenCars().stream().map(Measurement::getArea)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UP);
        BigDecimal stilt = pl.getParkingDetails().getStilts().stream().map(Measurement::getArea)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UP);

        return new ParkingAreas(open, cover, basement, stilt);
    }

    private double calculateTotalECS(ParkingHelper helper, ParkingAreas areas) {
        helper.totalRequiredCarParking += areas.open.doubleValue() / OPEN_ECS;
        helper.totalRequiredCarParking += areas.cover.doubleValue() / COVER_ECS;
        helper.totalRequiredCarParking += areas.basement.doubleValue() / BSMNT_ECS;
        helper.totalRequiredCarParking += areas.stilt.doubleValue() / STILT_ECS;

        return roundECS(areas.open.doubleValue() / OPEN_ECS)
             + roundECS(areas.cover.doubleValue() / COVER_ECS)
             + roundECS(areas.basement.doubleValue() / BSMNT_ECS)
             + roundECS(areas.stilt.doubleValue() / STILT_ECS);
    }

    private double roundECS(double val) {
        return Double.parseDouble(String.format("%.2f", val));
    }

    private ParkingRuleResult fetchApplicableRule(Plan pl, BigDecimal plotArea) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.PARKING.getValue(), false);
        Optional<ParkingRequirement> matchedRule = rules.stream()
            .filter(ParkingRequirement.class::isInstance)
            .map(ParkingRequirement.class::cast)
            .findFirst();
        if (matchedRule.isPresent()) {
        	ParkingRequirement rule = matchedRule.get();
        	BigDecimal noOfParking = rule.getNoOfParking() != null ? rule.getNoOfParking() : BigDecimal.ZERO;
        	BigDecimal permissible = rule.getPermissible() != null ? rule.getPermissible() : BigDecimal.ZERO;

            return new ParkingRuleResult(noOfParking.doubleValue(), permissible.doubleValue());
        }
        return new ParkingRuleResult(0d, 0d);
    }

    private double calculateRequiredParkingArea(ParkingAreas areas, double ecs, double noOfRequiredParking) {
        if (areas.open.doubleValue() > 0 || areas.stilt.doubleValue() > 0
                || areas.basement.doubleValue() > 0 || areas.cover.doubleValue() > 0) {
            return ecs * noOfRequiredParking;
        }
        return 0d;
    }

    private void addIndividualParkingReports(Plan pl, ParkingAreas areas) {
        if (areas.open.doubleValue() > 0) {
            setReportOutputDetails(pl, FOUR_P_TWO_P_ONE, OPEN_PARKING_AREA, EMPTY_STRING,
                    roundECS(areas.open.doubleValue() / OPEN_ECS) + ECS_STRING + OPENING_BRACKET + areas.open + SQMTRS + CLOSING_BRACKET, EMPTY_STRING);
        }
        if (areas.cover.doubleValue() > 0) {
            setReportOutputDetails(pl, FOUR_P_TWO_P_ONE, COVER_PARKING_AREA, SINGLE_SPACE_STRING,
                    roundECS(areas.cover.doubleValue() / COVER_ECS) + ECS_STRING + OPENING_BRACKET + areas.cover + SQMTRS + CLOSING_BRACKET, EMPTY_STRING);
        }
        if (areas.basement.doubleValue() > 0) {
            setReportOutputDetails(pl, FOUR_P_TWO_P_ONE, BASEMENT_PARKING_AREA, EMPTY_STRING,
                    roundECS(areas.basement.doubleValue() / BSMNT_ECS) + ECS_STRING + OPENING_BRACKET + areas.basement + SQMTRS + CLOSING_BRACKET, EMPTY_STRING);
        }
        if (areas.stilt.doubleValue() > 0) {
            setReportOutputDetails(pl, FOUR_P_TWO_P_ONE, STILT_PARKING_AREA, EMPTY_STRING,
                    roundECS(areas.stilt.doubleValue() / STILT_ECS) + ECS_STRING + OPENING_BRACKET + areas.stilt + SQMTRS + CLOSING_BRACKET, EMPTY_STRING);
        }
    }

    private static class ParkingAreas {
        BigDecimal open, cover, basement, stilt;

        ParkingAreas(BigDecimal open, BigDecimal cover, BigDecimal basement, BigDecimal stilt) {
            this.open = open;
            this.cover = cover;
            this.basement = basement;
            this.stilt = stilt;
        }

        BigDecimal getTotal() {
            return open.add(cover).add(basement).add(stilt);
        }
    }

    private static class ParkingRuleResult {
        double noOfRequiredParking;
        double ecs;

        ParkingRuleResult(double noOfRequiredParking, double ecs) {
            this.noOfRequiredParking = noOfRequiredParking;
            this.ecs = ecs;
        }
    }


    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
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
    
    private void setReportOutputDetails1(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
            String status) {
    	
    	 ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
         scrutinyDetail1.addColumnHeading(1, RULE_NO);
         scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
         scrutinyDetail1.addColumnHeading(3, EMPTY_STRING);
         scrutinyDetail1.addColumnHeading(4, REQUIRED);
         scrutinyDetail1.addColumnHeading(5, PROVIDED);
         scrutinyDetail1.addColumnHeading(6, STATUS);
         
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    
    

    private void validateSpecialParking(Plan pl, ParkingHelper helper, BigDecimal totalBuiltupArea) {
        BigDecimal maxHeightOfBuilding = BigDecimal.ZERO;
        int failedCount = 0;
        int success = 0;
        if (!pl.getParkingDetails().getSpecial().isEmpty()) {
            for (Measurement m : pl.getParkingDetails().getSpecial()) {
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0)
                    failedCount++;
                else
                    success++;
            }
            if (failedCount > 0)
                pl.addError(SPECIAL_PARKING_DIM_DESC,
                        SPECIAL_PARKING_DIM_DESC + failedCount + NO_NOT_HAVING_4_PTS);
            pl.getParkingDetails().setValidSpecialSlots(success);
        }

        for (Block block : pl.getBlocks()) {
            if (block.getBuilding().getBuildingHeight() != null && block.getBuilding().getBuildingHeight().compareTo(maxHeightOfBuilding) > 0) {
                maxHeightOfBuilding = block.getBuilding().getBuildingHeight();
            }
        }
        if (maxHeightOfBuilding.compareTo(new BigDecimal(15)) >= 0
                || (pl.getPlot() != null && pl.getPlot().getArea().compareTo(new BigDecimal(500)) > 0)) {
            if (pl.getParkingDetails().getValidSpecialSlots() == 0) {
               // pl.addError(T_RULE, getLocaleMessage(DcrConstants.OBJECTNOTDEFINED, SP_PARKING));
            } else {
                for (Measurement m : pl.getParkingDetails().getSpecial()) {
                    if (m.getMinimumSide().compareTo(new BigDecimal(0)) > 0
                            && m.getMinimumSide().compareTo(new BigDecimal(3.6)) >= 0) {
                        setReportOutputDetails(pl, T_RULE, SP_PARKING, 1 + NUMBERS,
                                pl.getParkingDetails().getValidSpecialSlots() + NUMBERS,
                                Result.Accepted.getResultVal());
                    } else if (m.getMinimumSide().compareTo(new BigDecimal(0)) > 0) {
                        setReportOutputDetails(pl, T_RULE, SP_PARKING, 1 + NUMBERS,
                                pl.getParkingDetails().getValidSpecialSlots() + NUMBERS,
                                Result.Not_Accepted.getResultVal());
                    }
                }
            }
        }

    }

    private void processTwoWheelerParking(Plan pl, ParkingHelper helper) {
        helper.twoWheelerParking = BigDecimal.valueOf(0.25 * helper.totalRequiredCarParking * 2.70 * 5.50)
                .setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        double providedArea = 0;
        for (Measurement measurement : pl.getParkingDetails().getTwoWheelers()) {
            providedArea = providedArea + measurement.getArea().doubleValue();
        }
        if (providedArea < helper.twoWheelerParking) {
            setReportOutputDetails(pl, SUB_RULE_34_2, TWO_WHEELER_PARK_AREA,
                    helper.twoWheelerParking + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
                    BigDecimal.valueOf(providedArea).setScale(2, BigDecimal.ROUND_HALF_UP) + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
                    Result.Not_Accepted.getResultVal());
        } else {
            setReportOutputDetails(pl, SUB_RULE_34_2, TWO_WHEELER_PARK_AREA,
                    helper.twoWheelerParking + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
                    BigDecimal.valueOf(providedArea).setScale(2, BigDecimal.ROUND_HALF_UP) + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
                    Result.Accepted.getResultVal());
        }
    }
    
    private void processMechanicalParking(Plan pl) {
        int count = 0;
        for (Measurement m : pl.getParkingDetails().getMechParking())
            if (m.getWidth().compareTo(BigDecimal.valueOf(MECH_PARKING_WIDTH)) < 0
                    || m.getHeight().compareTo(BigDecimal.valueOf(MECH_PARKING_HEIGHT)) < 0)
                count++;
        if (count > 0) {
            setReportOutputDetails(pl, SUB_RULE_34_2, MECH_PARKING_DESC, MECH_PARKING_DIM_DESC,
                    count + MECH_PARKING_DIM_DESC_NA,
                    Result.Not_Accepted.getResultVal());
        } else {
            setReportOutputDetails(pl, SUB_RULE_34_2, MECH_PARKING_DESC, MECH_PARKING_DIM_DESC,
                    count + MECH_PARKING_DIM_DESC_NA,
                    Result.Accepted.getResultVal());
        }
    }

    /*
     * private double processMechanicalParking(Plan pl, ParkingHelper helper) { Integer noOfMechParkingFromPlInfo =
     * pl.getPlanInformation().getNoOfMechanicalParking(); Integer providedSlots = pl.getParkingDetails().getMechParking().size();
     * double maxAllowedMechPark = BigDecimal.valueOf(helper.totalRequiredCarParking / 2).setScale(0, RoundingMode.UP)
     * .intValue(); if (noOfMechParkingFromPlInfo > 0) { if (noOfMechParkingFromPlInfo > 0 && providedSlots == 0) {
     * setReportOutputDetails(pl, SUB_RULE_34_2, MECHANICAL_PARKING, 1 + NUMBERS, providedSlots + NUMBERS,
     * Result.Not_Accepted.getResultVal()); } else if (noOfMechParkingFromPlInfo > 0 && providedSlots > 0 &&
     * noOfMechParkingFromPlInfo > maxAllowedMechPark) { setReportOutputDetails(pl, SUB_RULE_34_2, MAX_ALLOWED_MECH_PARK,
     * maxAllowedMechPark + NUMBERS, noOfMechParkingFromPlInfo + NUMBERS, Result.Not_Accepted.getResultVal()); } else if
     * (noOfMechParkingFromPlInfo > 0 && providedSlots > 0) { setReportOutputDetails(pl, SUB_RULE_34_2, MECHANICAL_PARKING, EMPTY_STRING,
     * noOfMechParkingFromPlInfo + NUMBERS, Result.Accepted.getResultVal()); } } return 0; }
     */

    /*
     * private void buildResultForYardValidation(Plan Plan, BigDecimal parkSlotAreaInFrontYard, BigDecimal maxAllowedArea, String
     * type) { Plan.reportOutput .add(buildRuleOutputWithSubRule(DcrConstants.RULE34, SUB_RULE_34_1,
     * "Parking space should not exceed 50% of the area of mandatory " + type,
     * "Parking space should not exceed 50% of the area of mandatory " + type, "Maximum allowed area for parking in " + type +SINGLE_SPACE_STRING
     * + maxAllowedArea + DcrConstants.SQMTRS, "Parking provided in more than the allowed area " + parkSlotAreaInFrontYard +
     * DcrConstants.SQMTRS, Result.Not_Accepted, null)); } private BigDecimal validateParkingSlotsAreWithInYard(Plan Plan, Polygon
     * yardPolygon) { BigDecimal area = BigDecimal.ZERO; for (Measurement parkingSlot : Plan.getParkingDetails().getCars()) {
     * Iterator parkSlotIterator = parkingSlot.getPolyLine().getVertexIterator(); while (parkSlotIterator.hasNext()) { DXFVertex
     * dxfVertex = (DXFVertex) parkSlotIterator.next(); Point point = dxfVertex.getPoint(); if (rayCasting.contains(point,
     * yardPolygon)) { area = area.add(parkingSlot.getArea()); } } } return area; }
     */

    private void checkDimensionForCarParking(Plan pl, ParkingHelper helper) {

        /*
         * for (Block block : Plan.getBlocks()) { for (SetBack setBack : block.getSetBacks()) { if (setBack.getFrontYard() != null
         * && setBack.getFrontYard().getPresentInDxf()) { Polygon frontYardPolygon =
         * ProcessHelper.getPolygon(setBack.getFrontYard().getPolyLine()); BigDecimal parkSlotAreaInFrontYard =
         * validateParkingSlotsAreWithInYard(Plan, frontYardPolygon); BigDecimal maxAllowedArea =
         * setBack.getFrontYard().getArea().divide(BigDecimal.valueOf(2), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
         * RoundingMode.HALF_UP); if (parkSlotAreaInFrontYard.compareTo(maxAllowedArea) > 0) { buildResultForYardValidation(Plan,
         * parkSlotAreaInFrontYard, maxAllowedArea, "front yard space"); } } if (setBack.getRearYard() != null &&
         * setBack.getRearYard().getPresentInDxf()) { Polygon rearYardPolygon =
         * ProcessHelper.getPolygon(setBack.getRearYard().getPolyLine()); BigDecimal parkSlotAreaInRearYard =
         * validateParkingSlotsAreWithInYard(Plan, rearYardPolygon); BigDecimal maxAllowedArea =
         * setBack.getRearYard().getArea().divide(BigDecimal.valueOf(2), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
         * RoundingMode.HALF_UP); if (parkSlotAreaInRearYard.compareTo(maxAllowedArea) > 0) { buildResultForYardValidation(Plan,
         * parkSlotAreaInRearYard, maxAllowedArea, "rear yard space"); } } if (setBack.getSideYard1() != null &&
         * setBack.getSideYard1().getPresentInDxf()) { Polygon sideYard1Polygon =
         * ProcessHelper.getPolygon(setBack.getSideYard1().getPolyLine()); BigDecimal parkSlotAreaInSideYard1 =
         * validateParkingSlotsAreWithInYard(Plan, sideYard1Polygon); BigDecimal maxAllowedArea =
         * setBack.getSideYard1().getArea().divide(BigDecimal.valueOf(2), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
         * RoundingMode.HALF_UP); if (parkSlotAreaInSideYard1.compareTo(maxAllowedArea) > 0) { buildResultForYardValidation(Plan,
         * parkSlotAreaInSideYard1, maxAllowedArea, "side yard1 space"); } } if (setBack.getSideYard2() != null &&
         * setBack.getSideYard2().getPresentInDxf()) { Polygon sideYard2Polygon =
         * ProcessHelper.getPolygon(setBack.getSideYard2().getPolyLine()); BigDecimal parkSlotAreaInFrontYard =
         * validateParkingSlotsAreWithInYard(Plan, sideYard2Polygon); BigDecimal maxAllowedArea =
         * setBack.getSideYard2().getArea().divide(BigDecimal.valueOf(2), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
         * RoundingMode.HALF_UP); if (parkSlotAreaInFrontYard.compareTo(maxAllowedArea) > 0) { buildResultForYardValidation(Plan,
         * parkSlotAreaInFrontYard, maxAllowedArea, "side yard2 space"); } } } }
         */

        int parkingCount = pl.getParkingDetails().getCars().size();
        int failedCount = 0;
        int success = 0;
        for (Measurement slot : pl.getParkingDetails().getCars()) {
            if (slot.getHeight().setScale(2, RoundingMode.UP).doubleValue() >= PARKING_SLOT_HEIGHT
                    && slot.getWidth().setScale(2, RoundingMode.UP).doubleValue() >= PARKING_SLOT_WIDTH)
                success++;
            else
                failedCount++;
        }
        pl.getParkingDetails().setValidCarParkingSlots(parkingCount - failedCount);
        if (parkingCount > 0)
            if (failedCount > 0) {
                if (helper.totalRequiredCarParking.intValue() == pl.getParkingDetails().getValidCarParkingSlots()) {
                    setReportOutputDetails(pl, SUB_RULE_40, SUB_RULE_34_1_DESCRIPTION,
                            PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING,
                            OUT_OF + parkingCount + PARKING + failedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Accepted.getResultVal());
                } else {
                    setReportOutputDetails(pl, SUB_RULE_40, SUB_RULE_34_1_DESCRIPTION,
                            PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING,
                            OUT_OF + parkingCount + PARKING + failedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Not_Accepted.getResultVal());
                }
            } else {
                setReportOutputDetails(pl, SUB_RULE_40, SUB_RULE_34_1_DESCRIPTION,
                        PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING, NO_VIOLATION_OF_AREA + parkingCount + PARKING,
                        Result.Accepted.getResultVal());
            }
        int openParkCount = pl.getParkingDetails().getOpenCars().size();
        int openFailedCount = 0;
        int openSuccess = 0;
        for (Measurement slot : pl.getParkingDetails().getOpenCars()) {
            if (slot.getHeight().setScale(2, RoundingMode.UP).doubleValue() >= PARKING_SLOT_HEIGHT
                    && slot.getWidth().setScale(2, RoundingMode.UP).doubleValue() >= PARKING_SLOT_WIDTH)
                openSuccess++;
            else
                openFailedCount++;
        }
        pl.getParkingDetails().setValidOpenCarSlots(openParkCount - openFailedCount);
        if (openParkCount > 0)
            if (openFailedCount > 0) {
                if (helper.totalRequiredCarParking.intValue() == pl.getParkingDetails().getValidOpenCarSlots()) {
                    setReportOutputDetails(pl, SUB_RULE_40, OPEN_PARKING_DIM_DESC,
                            PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING,
                            OUT_OF + openParkCount + PARKING + openFailedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Accepted.getResultVal());
                } else {
                    setReportOutputDetails(pl, SUB_RULE_40, OPEN_PARKING_DIM_DESC,
                            PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING,
                            OUT_OF + openParkCount + PARKING + openFailedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Not_Accepted.getResultVal());
                }
            } else {
                setReportOutputDetails(pl, SUB_RULE_40, OPEN_PARKING_DIM_DESC,
                        PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING, NO_VIOLATION_OF_AREA + openParkCount + PARKING,
                        Result.Accepted.getResultVal());
            }

        int coverParkCount = pl.getParkingDetails().getCoverCars().size();
        int coverFailedCount = 0;
        int coverSuccess = 0;
        for (Measurement slot : pl.getParkingDetails().getCoverCars()) {
            if (slot.getHeight().setScale(2, RoundingMode.UP).doubleValue() >= PARKING_SLOT_HEIGHT
                    && slot.getWidth().setScale(2, RoundingMode.UP).doubleValue() >= PARKING_SLOT_WIDTH)
                coverSuccess++;
            else
                coverFailedCount++;
        }
        pl.getParkingDetails().setValidCoverCarSlots(coverParkCount - coverFailedCount);
        if (coverParkCount > 0)
            if (coverFailedCount > 0) {
                if (helper.totalRequiredCarParking.intValue() == pl.getParkingDetails().getValidCoverCarSlots()) {
                    setReportOutputDetails(pl, SUB_RULE_40, COVER_PARKING_DIM_DESC,
                            PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING,
                            OUT_OF + coverParkCount + PARKING + coverFailedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Accepted.getResultVal());
                } else {
                    setReportOutputDetails(pl, SUB_RULE_40, COVER_PARKING_DIM_DESC,
                            PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING,
                            OUT_OF + coverParkCount + PARKING + coverFailedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Not_Accepted.getResultVal());
                }
            } else {
                setReportOutputDetails(pl, SUB_RULE_40, COVER_PARKING_DIM_DESC,
                        PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING, NO_VIOLATION_OF_AREA + coverParkCount + PARKING,
                        Result.Accepted.getResultVal());
            }

        // Validate dimension of basement
        int bsmntParkCount = pl.getParkingDetails().getBasementCars().size();
        int bsmntFailedCount = 0;
        int bsmntSuccess = 0;
        for (Measurement slot : pl.getParkingDetails().getBasementCars()) {
            if (slot.getHeight().setScale(2, RoundingMode.UP).doubleValue() >= PARKING_SLOT_HEIGHT
                    && slot.getWidth().setScale(2, RoundingMode.UP).doubleValue() >= PARKING_SLOT_WIDTH)
                bsmntSuccess++;
            else
                bsmntFailedCount++;
        }
        pl.getParkingDetails().setValidBasementCarSlots(bsmntParkCount - bsmntFailedCount);
        if (bsmntParkCount > 0)
            if (bsmntFailedCount > 0) {
                if (helper.totalRequiredCarParking.intValue() == pl.getParkingDetails().getValidBasementCarSlots()) {
                    setReportOutputDetails(pl, SUB_RULE_40, BSMNT_PARKING_DIM_DESC,
                            PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING,
                            OUT_OF + bsmntParkCount + PARKING + bsmntFailedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Accepted.getResultVal());
                } else {
                    setReportOutputDetails(pl, SUB_RULE_40, BSMNT_PARKING_DIM_DESC,
                            PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING,
                            OUT_OF + bsmntParkCount + PARKING + bsmntFailedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Not_Accepted.getResultVal());
                }
            } else {
                setReportOutputDetails(pl, SUB_RULE_40, BSMNT_PARKING_DIM_DESC,
                        PARKING_MIN_AREA + MIN_AREA_EACH_CAR_PARKING, NO_VIOLATION_OF_AREA + bsmntParkCount + PARKING,
                        Result.Accepted.getResultVal());
            }

    }

    private void checkDimensionForSpecialParking(Plan pl, ParkingHelper helper) {

        int success = 0;
        int specialFailedCount = 0;
        int specialParkCount = pl.getParkingDetails().getSpecial().size();
        for (Measurement spParkSlot : pl.getParkingDetails().getSpecial()) {
            if (spParkSlot.getMinimumSide().doubleValue() >= SP_PARK_SLOT_MIN_SIDE)
                success++;
            else
                specialFailedCount++;
        }
        pl.getParkingDetails().setValidSpecialSlots(specialParkCount - specialFailedCount);
        if (specialParkCount > 0)
            if (specialFailedCount > 0) {
                if (helper.daParking.intValue() == pl.getParkingDetails().getValidSpecialSlots()) {
                    setReportOutputDetails(pl, SUB_RULE_40_8, SP_PARKING_SLOT_AREA,
                            DA_PARKING_MIN_AREA + MINIMUM_AREA_OF_EACH_DA_PARKING,
                            NO_VIOLATION_OF_AREA + pl.getParkingDetails().getValidSpecialSlots() + PARKING,
                            Result.Accepted.getResultVal());
                } else {
                    setReportOutputDetails(pl, SUB_RULE_40_8, SP_PARKING_SLOT_AREA,
                            DA_PARKING_MIN_AREA + MINIMUM_AREA_OF_EACH_DA_PARKING,
                            OUT_OF + specialParkCount + PARKING + specialFailedCount + PARKING_VIOLATED_MINIMUM_AREA,
                            Result.Not_Accepted.getResultVal());
                }
            } else {
                setReportOutputDetails(pl, SUB_RULE_40_8, SP_PARKING_SLOT_AREA,
                        DA_PARKING_MIN_AREA + MINIMUM_AREA_OF_EACH_DA_PARKING,
                        NO_VIOLATION_OF_AREA + specialParkCount + PARKING, Result.Accepted.getResultVal());
            }
    }
    
    private void checkDimensionForTwoWheelerParking(Plan pl, ParkingHelper helper) {
		double providedArea = 0;
		int twoWheelParkingCount = pl.getParkingDetails().getTwoWheelers().size();
		int failedTwoWheelCount = 0;
		helper.twoWheelerParking = BigDecimal.valueOf(0.25 * helper.totalRequiredCarParking * 2.70 * 5.50)
				.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (!pl.getParkingDetails().getTwoWheelers().isEmpty()) {
			for (Measurement m : pl.getParkingDetails().getTwoWheelers()) {
				if (m.getWidth().setScale(2, RoundingMode.UP).doubleValue() < TWO_WHEEL_PARKING_AREA_WIDTH
						|| m.getHeight().setScale(2, RoundingMode.UP).doubleValue() < TWO_WHEEL_PARKING_AREA_HEIGHT)
					failedTwoWheelCount++;

				providedArea = providedArea + m.getArea().doubleValue();
			}
		}
		
		if (providedArea < helper.twoWheelerParking) {
			setReportOutputDetails(pl, SUB_RULE_34_2, TWO_WHEELER_PARK_AREA,
					helper.twoWheelerParking + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
					BigDecimal.valueOf(providedArea).setScale(2, BigDecimal.ROUND_HALF_UP) + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
					Result.Not_Accepted.getResultVal());
		} else {
			setReportOutputDetails(pl, SUB_RULE_34_2, TWO_WHEELER_PARK_AREA,
					helper.twoWheelerParking + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
					BigDecimal.valueOf(providedArea).setScale(2, BigDecimal.ROUND_HALF_UP) + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
					Result.Accepted.getResultVal());
		}

		if (providedArea >= helper.twoWheelerParking && failedTwoWheelCount >= 0) {
			setReportOutputDetails(pl, SUB_RULE_40, TWO_WHEELER_DIM_DESC, PARKING_AREA_DIM,
					OUT_OF + twoWheelParkingCount + PARKING + failedTwoWheelCount + PARKING_VIOLATED_DIM,
					Result.Accepted.getResultVal());
		} else {
			setReportOutputDetails(pl, SUB_RULE_40, TWO_WHEELER_DIM_DESC, PARKING_AREA_DIM,
					OUT_OF + twoWheelParkingCount + PARKING + failedTwoWheelCount + PARKING_VIOLATED_DIM,
					Result.Not_Accepted.getResultVal());
		}
	}

    private BigDecimal getTotalCarpetAreaByOccupancy(Plan pl, OccupancyType type) {
        BigDecimal totalArea = BigDecimal.ZERO;
        for (Block b : pl.getBlocks())
            for (Occupancy occupancy : b.getBuilding().getTotalArea())
                if (occupancy.getType().equals(type))
                    totalArea = totalArea.add(occupancy.getCarpetArea());
        return totalArea;
    }
    
    private void checkAreaForLoadUnloadSpaces(Plan pl) {
        double providedArea = 0;
        BigDecimal totalBuiltupArea = pl.getOccupancies().stream().map(Occupancy::getBuiltUpArea)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double requiredArea = Math.abs(((totalBuiltupArea.doubleValue() - 700) / 1000) * 30);
        if (!pl.getParkingDetails().getLoadUnload().isEmpty()) {
                for (Measurement m : pl.getParkingDetails().getLoadUnload()) {
                        if (m.getArea().compareTo(BigDecimal.valueOf(30)) >= 0)
                            providedArea = providedArea + m.getArea().doubleValue();
                }
        }
        if (providedArea < requiredArea) {
                setReportOutputDetails(pl, SUB_RULE_40, LOADING_UNLOADING_AREA,
                        requiredArea + SINGLE_SPACE_STRING + DcrConstants.SQMTRS, 
                                BigDecimal.valueOf(providedArea).setScale(2, BigDecimal.ROUND_HALF_UP) + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
                                Result.Not_Accepted.getResultVal());
        } else {
                setReportOutputDetails(pl, SUB_RULE_40, LOADING_UNLOADING_AREA,
                        requiredArea + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
                                BigDecimal.valueOf(providedArea).setScale(2, BigDecimal.ROUND_HALF_UP) + SINGLE_SPACE_STRING + DcrConstants.SQMTRS,
                                Result.Accepted.getResultVal());
        }
}
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
