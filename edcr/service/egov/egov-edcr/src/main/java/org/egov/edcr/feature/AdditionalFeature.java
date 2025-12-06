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
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.constants.RuleKeyConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.CommonKeyConstants;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.FeatureUtil;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdditionalFeature extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(AdditionalFeature.class);

    @Autowired
    MDMSCacheManager cache;

    @Override
    public Plan validate(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();

        List<Block> blocks = pl.getBlocks();

        for (Block block : blocks) {
            if (block.getBuilding() != null) {
                if (block.getBuilding().getBuildingHeight().compareTo(BigDecimal.ZERO) == 0) {
                    errors.put(String.format(DcrConstants.BLOCK_BUILDING_HEIGHT, block.getNumber()),
                            edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                    new String[]{
                                            String.format(DcrConstants.BLOCK_BUILDING_HEIGHT, block.getNumber())},
                                    LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                }
            }
        }

        return pl;
    }


    @Override
    public Plan process(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        validate(pl);

        String typeOfArea = pl.getPlanInformation().getTypeOfArea();
        BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();

        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.ADDITIONAL_FEATURE.getValue(), false);
        Optional<AdditionalFeatureRequirement> matchedRule = rules.stream()
                .filter(AdditionalFeatureRequirement.class::isInstance)
                .map(AdditionalFeatureRequirement.class::cast)
                .findFirst();

        if (!matchedRule.isPresent()) {
            LOG.error("No matching rule found for Additional Feature");
            return pl;
        }

        AdditionalFeatureRequirement rule = matchedRule.get();
        BigDecimal additionalFeatureMinRequiredFloorHeight = rule.getAdditionalFeatureMinRequiredFloorHeight();
        BigDecimal additionalFeatureMaxPermissibleFloorHeight = rule.getAdditionalFeatureMaxPermissibleFloorHeight();
        BigDecimal additionalFeatureStiltFloor = rule.getAdditionalFeatureStiltFloor();
        BigDecimal additionalFeatureRoadWidthA = rule.getAdditionalFeatureRoadWidthA();
        BigDecimal additionalFeatureRoadWidthB = rule.getAdditionalFeatureRoadWidthB();
        BigDecimal additionalFeatureRoadWidthC = rule.getAdditionalFeatureRoadWidthC();
        BigDecimal additionalFeatureRoadWidthD = rule.getAdditionalFeatureRoadWidthD();
        BigDecimal additionalFeatureRoadWidthE = rule.getAdditionalFeatureRoadWidthE();
        BigDecimal additionalFeatureRoadWidthF = rule.getAdditionalFeatureRoadWidthF();
        BigDecimal additionalFeatureNewRoadWidthA = rule.getAdditionalFeatureNewRoadWidthA();
        BigDecimal additionalFeatureNewRoadWidthB = rule.getAdditionalFeatureNewRoadWidthB();
        BigDecimal additionalFeatureNewRoadWidthC = rule.getAdditionalFeatureNewRoadWidthC();
        BigDecimal additionalFeatureRoadWidthAcceptedBC = rule.getAdditionalFeatureRoadWidthAcceptedBC();
        BigDecimal additionalFeatureRoadWidthAcceptedCD = rule.getAdditionalFeatureRoadWidthAcceptedCD();
        BigDecimal additionalFeatureRoadWidthAcceptedDE = rule.getAdditionalFeatureRoadWidthAcceptedDE();
        BigDecimal additionalFeatureRoadWidthAcceptedEF = rule.getAdditionalFeatureRoadWidthAcceptedEF();
        BigDecimal additionalFeatureRoadWidthNewAcceptedAB = rule.getAdditionalFeatureRoadWidthNewAcceptedAB();
        BigDecimal additionalFeatureRoadWidthNewAcceptedBC = rule.getAdditionalFeatureRoadWidthNewAcceptedBC();

        BigDecimal additionalFeatureFloorsAcceptedBC = rule.getAdditionalFeatureFloorsAcceptedBC();
        BigDecimal additionalFeatureFloorsAcceptedCD = rule.getAdditionalFeatureFloorsAcceptedCD();
        BigDecimal additionalFeatureFloorsAcceptedDE = rule.getAdditionalFeatureFloorsAcceptedDE();
        BigDecimal additionalFeatureFloorsAcceptedEF = rule.getAdditionalFeatureFloorsAcceptedEF();
        BigDecimal additionalFeatureFloorsNewAcceptedAB = rule.getAdditionalFeatureFloorsNewAcceptedAB();
        BigDecimal additionalFeatureFloorsNewAcceptedBC = rule.getAdditionalFeatureFloorsNewAcceptedBC();
        BigDecimal additionalFeatureBasementPlotArea = rule.getAdditionalFeatureBasementPlotArea();
        BigDecimal additionalFeatureBasementAllowed = rule.getAdditionalFeatureBasementAllowed();
        BigDecimal additionalFeatureBarrierValue = rule.getAdditionalFeatureBarrierValue();

        BigDecimal afGreenBuildingValueA = rule.getAfGreenBuildingValueA();
        BigDecimal afGreenBuildingValueB = rule.getAfGreenBuildingValueB();
        BigDecimal afGreenBuildingValueC = rule.getAfGreenBuildingValueC();
        BigDecimal afGreenBuildingValueD = rule.getAfGreenBuildingValueD();

        if (StringUtils.isNotBlank(typeOfArea) && roadWidth != null) {
            validateNumberOfFloors(pl, errors, typeOfArea, roadWidth,
                    additionalFeatureRoadWidthA,
                    additionalFeatureRoadWidthB,
                    additionalFeatureRoadWidthC,
                    additionalFeatureRoadWidthD,
                    additionalFeatureRoadWidthE,
                    additionalFeatureRoadWidthF,
                    additionalFeatureNewRoadWidthA,
                    additionalFeatureNewRoadWidthB,
                    additionalFeatureNewRoadWidthC,
                    additionalFeatureFloorsAcceptedBC,
                    additionalFeatureFloorsAcceptedCD,
                    additionalFeatureFloorsAcceptedDE,
                    additionalFeatureFloorsAcceptedEF,
                    additionalFeatureFloorsNewAcceptedAB,
                    additionalFeatureFloorsNewAcceptedBC
            );
            validateHeightOfBuilding(pl, errors, typeOfArea, roadWidth,
                    additionalFeatureRoadWidthA,
                    additionalFeatureRoadWidthB,
                    additionalFeatureRoadWidthC,
                    additionalFeatureRoadWidthD,
                    additionalFeatureRoadWidthE,
                    additionalFeatureRoadWidthF,
                    additionalFeatureNewRoadWidthA,
                    additionalFeatureNewRoadWidthB,
                    additionalFeatureNewRoadWidthC,
                    additionalFeatureRoadWidthAcceptedBC,
                    additionalFeatureRoadWidthAcceptedCD,
                    additionalFeatureRoadWidthAcceptedDE,
                    additionalFeatureRoadWidthAcceptedEF,
                    additionalFeatureRoadWidthNewAcceptedAB,
                    additionalFeatureRoadWidthNewAcceptedBC
            );
            validateHeightOfFloors(pl, errors, additionalFeatureMinRequiredFloorHeight, additionalFeatureMaxPermissibleFloorHeight, additionalFeatureStiltFloor);
        }

        validatePlinthHeight(pl, errors);
        validateBarrierFreeAccess(pl, errors, additionalFeatureBarrierValue);
        validateBasement(pl, errors, additionalFeatureBasementPlotArea, additionalFeatureBasementAllowed);
        validateGreenBuildingsAndSustainability(pl, errors,
                afGreenBuildingValueA,
                afGreenBuildingValueB,
                afGreenBuildingValueC,
                afGreenBuildingValueD
        );
        validateFireDeclaration(pl, errors);

        return pl;
    }

    private void validateFireDeclaration(Plan pl, HashMap<String, String> errors) {
        ScrutinyDetail scrutinyDetail = getNewScrutinyDetail(CommonKeyConstants.FIRE_PROTEC_SAFETY_REQ);
        OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding() != null
                ? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
                : null;
        if (pl.getBlocks() != null && !pl.getBlocks().isEmpty()) {
            for (Block b : pl.getBlocks()) {
                if (b.getBuilding() != null && (b.getBuilding().getIsHighRise()
                        || isCommercialAbv750sqm(pl, mostRestrictiveOccupancyType))) {
                    ReportScrutinyDetail detail = new ReportScrutinyDetail();
                    detail.setRuleNo(RULE_56);
                    detail.setDescription(FIRE_PROTECTION_AND_FIRE_SAFETY_REQUIREMENTS_DESC);
                    detail.setPermissible(YES_NO_NA);
                    detail.setProvided(pl.getPlanInformation().getFireProtectionAndFireSafetyRequirements());

                    if (pl.getPlanInformation() != null && !pl.getPlanInformation().getFireProtectionAndFireSafetyRequirements().isEmpty()) {
                        detail.setStatus(Result.Accepted.getResultVal());
                    } else {
                        detail.setStatus(Result.Not_Accepted.getResultVal());
                    }

                    Map<String, String> details = mapReportDetails(detail);
                    addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
                }
            }
        }

    }

    private boolean isCommercialAbv750sqm(Plan pl, OccupancyTypeHelper mostRestrictiveOccupancyType) {
        return pl.getVirtualBuilding() != null && mostRestrictiveOccupancyType != null
                && mostRestrictiveOccupancyType.getType() != null
                && DxfFileConstants.F.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())
                && pl.getVirtualBuilding().getTotalCoverageArea().compareTo(BigDecimal.valueOf(750)) > 0;
    }

    private void validateBarrierFreeAccess(Plan pl, HashMap<String, String> errors, BigDecimal additionalFeatureBarrierValue) {
        ScrutinyDetail scrutinyDetail = getNewScrutinyDetail(BARRIER_FREE_ACCESS_FOR_PHYSICALLY_CHALLENGED_PEOPLE_DESC);
        if (pl.getVirtualBuilding() != null && pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null
                && pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype() != null && !DxfFileConstants.A_R
                .equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype().getCode())
                && pl.getPlot() != null && pl.getPlot().getArea().compareTo(additionalFeatureBarrierValue) > 0) {

            ReportScrutinyDetail detail = new ReportScrutinyDetail();
            detail.setRuleNo(RULE_50);
            detail.setDescription(BARRIER_FREE_ACCESS_FOR_PHYSICALLY_CHALLENGED_PEOPLE_DESC);
            detail.setPermissible(DcrConstants.YES);

            if (pl.getPlanInformation() != null
                    && !pl.getPlanInformation().getBarrierFreeAccessForPhyChlngdPpl().isEmpty()
                    && DcrConstants.YES.equals(pl.getPlanInformation().getBarrierFreeAccessForPhyChlngdPpl())) {
                detail.setProvided(DcrConstants.YES);
                detail.setStatus(Result.Accepted.getResultVal());
            } else {
                detail.setProvided(pl.getPlanInformation().getBarrierFreeAccessForPhyChlngdPpl());
                detail.setStatus(Result.Not_Accepted.getResultVal());
            }

            Map<String, String> details = mapReportDetails(detail);
            addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
        }

    }

    private void validateNumberOfFloors(Plan pl, HashMap<String, String> errors, String typeOfArea, BigDecimal roadWidth,
                                        BigDecimal additionalFeatureRoadWidthA,
                                        BigDecimal additionalFeatureRoadWidthB,
                                        BigDecimal additionalFeatureRoadWidthC,
                                        BigDecimal additionalFeatureRoadWidthD,
                                        BigDecimal additionalFeatureRoadWidthE,
                                        BigDecimal additionalFeatureRoadWidthF,
                                        BigDecimal additionalFeatureNewRoadWidthA,
                                        BigDecimal additionalFeatureNewRoadWidthB,
                                        BigDecimal additionalFeatureNewRoadWidthC,
                                        BigDecimal additionalFeatureFloorsAcceptedBC,
                                        BigDecimal additionalFeatureFloorsAcceptedCD,
                                        BigDecimal additionalFeatureFloorsAcceptedDE,
                                        BigDecimal additionalFeatureFloorsAcceptedEF,
                                        BigDecimal additionalFeatureFloorsNewAcceptedAB,
                                        BigDecimal additionalFeatureFloorsNewAcceptedBC
    ) {
        for (Block block : pl.getBlocks()) {

            boolean isAccepted = false;
            ScrutinyDetail scrutinyDetail = getNewScrutinyDetailRoadArea(
                    BLOCK + block.getNumber() + UNDERSCORE + NUMBER_OF_FLOORS);
            BigDecimal floorAbvGround = block.getBuilding().getFloorsAboveGround();
            String requiredFloorCount = StringUtils.EMPTY;

            if (typeOfArea.equalsIgnoreCase(OLD)) {
                if (roadWidth.compareTo(additionalFeatureRoadWidthA) < 0) {
                    errors.put(OLD_AREA_ERROR, OLD_AREA_ERROR_MSG);
                    pl.addErrors(errors);
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthB) >= 0
                        && roadWidth.compareTo(additionalFeatureRoadWidthC) < 0) {
                    isAccepted = floorAbvGround.compareTo(additionalFeatureFloorsAcceptedBC) <= 0;
                    requiredFloorCount = LESS_THAN_EQUAL_TO + additionalFeatureFloorsAcceptedBC;
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthC) >= 0
                        && roadWidth.compareTo(additionalFeatureRoadWidthD) < 0) {
                    isAccepted = floorAbvGround.compareTo(additionalFeatureFloorsAcceptedCD) <= 0;
                    requiredFloorCount = LESS_THAN_EQUAL_TO + additionalFeatureFloorsAcceptedCD;
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthD) >= 0
                        && roadWidth.compareTo(additionalFeatureRoadWidthE) < 0) {
                    isAccepted = floorAbvGround.compareTo(additionalFeatureFloorsAcceptedDE) <= 0;
                    requiredFloorCount = LESS_THAN_EQUAL_TO + additionalFeatureFloorsAcceptedDE;
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthE) >= 0
                        && roadWidth.compareTo(additionalFeatureRoadWidthF) < 0) {
                    isAccepted = floorAbvGround.compareTo(additionalFeatureFloorsAcceptedEF) <= 0;
                    requiredFloorCount = LESS_THAN_EQUAL_TO + additionalFeatureFloorsAcceptedEF;
                }
            }

            if (typeOfArea.equalsIgnoreCase(NEW)) {
                if (roadWidth.compareTo(additionalFeatureNewRoadWidthA) < 0) {
                    errors.put(NEW_AREA_ERROR, NEW_AREA_ERROR_MSG);
                    pl.addErrors(errors);
                } else if (roadWidth.compareTo(additionalFeatureNewRoadWidthA) >= 0
                        && roadWidth.compareTo(additionalFeatureNewRoadWidthB) < 0) {
                    isAccepted = floorAbvGround.compareTo(additionalFeatureFloorsNewAcceptedAB) <= 0;
                    requiredFloorCount = LESS_THAN_EQUAL_TO + additionalFeatureFloorsNewAcceptedAB;
                } else if (roadWidth.compareTo(additionalFeatureNewRoadWidthB) >= 0
                        && roadWidth.compareTo(additionalFeatureNewRoadWidthC) < 0) {
                    isAccepted = floorAbvGround.compareTo(additionalFeatureFloorsNewAcceptedBC) <= 0;
                    requiredFloorCount = LESS_THAN_EQUAL_TO + additionalFeatureFloorsNewAcceptedBC;
                }
            }

            if (errors.isEmpty() && StringUtils.isNotBlank(requiredFloorCount)) {
                ReportScrutinyDetail detail = new ReportScrutinyDetail();
                detail.setRuleNo(RULE_4_4_4);
                detail.setDescription(NO_OF_FLOORS);
                detail.setAreaType(typeOfArea);
                detail.setRoadWidth(roadWidth.toString());
                detail.setPermissible(requiredFloorCount);
                detail.setProvided(String.valueOf(block.getBuilding().getFloorsAboveGround()));
                detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

                Map<String, String> details = mapReportDetails(detail);
                addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
            }
        }
    }

    private void validateHeightOfFloors(Plan pl, HashMap<String, String> errors, BigDecimal additionalFeatureMinRequiredFloorHeight, BigDecimal additionalFeatureMaxPermissibleFloorHeight, BigDecimal additionalFeatureStiltFloor) {
        System.out.println(INSIDE_HIEGHT_OF_FLOOR);
        for (Block block : pl.getBlocks()) {

            boolean isAccepted = false;
            ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
            scrutinyDetail.addColumnHeading(1, RULE_NO);
            scrutinyDetail.addColumnHeading(2, FLOOR_NO);
            scrutinyDetail.addColumnHeading(3, MIN_REQUIRED);
            scrutinyDetail.addColumnHeading(4, PROVIDED);
            scrutinyDetail.addColumnHeading(5, STATUS);
            scrutinyDetail.setKey(BLOCK + block.getNumber() + UNDERSCORE + HEIGHT_OF_FLOOR);
            OccupancyTypeHelper occupancyTypeHelper = block.getBuilding().getMostRestrictiveFarHelper();
            for (Floor floor : block.getBuilding().getFloors()) {
                BigDecimal floorHeight = floor.getFloorHeights() != null ? floor.getFloorHeights().get(0)
                        : BigDecimal.ZERO;

                int floorNumber = floor.getNumber();

                String minRequiredFloorHeight = StringUtils.EMPTY;
                String maxPermissibleFloorHeight = StringUtils.EMPTY;

                minRequiredFloorHeight = additionalFeatureMinRequiredFloorHeight.toString() + DcrConstants.IN_METER;
                maxPermissibleFloorHeight = additionalFeatureMaxPermissibleFloorHeight.toString() + DcrConstants.IN_METER;
                floor.setIsStiltFloor(false);

                if (floor.getIsStiltFloor() == false) {
                    if (floorHeight.compareTo(additionalFeatureMinRequiredFloorHeight) >= 0
                    ) {
                        isAccepted = true;
                    }
                } else if (floor.getIsStiltFloor() == true) {
                    if (floorHeight.compareTo(additionalFeatureStiltFloor) >= 0
                    ) {
                        isAccepted = true;
                    }
                }

                if (errors.isEmpty() && StringUtils.isNotBlank(minRequiredFloorHeight)
                        && StringUtils.isNotBlank(maxPermissibleFloorHeight)) {
                    ReportScrutinyDetail detail = new ReportScrutinyDetail();
                    detail.setRuleNo(RULE_4_4_4);
                    detail.setFloorNo(String.valueOf(floorNumber));
                    detail.setMinRequired(minRequiredFloorHeight);
                    detail.setProvided(floorHeight.toString() + DcrConstants.IN_METER);
                    detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

                    Map<String, String> details = mapReportDetails(detail);
                    addScrutinyDetailtoPlan(scrutinyDetail, pl, details);

                }
            }

        }
    }


    private void validateHeightOfBuilding(Plan pl, HashMap<String, String> errors, String typeOfArea, BigDecimal roadWidth,
                                          BigDecimal additionalFeatureRoadWidthA,
                                          BigDecimal additionalFeatureRoadWidthB,
                                          BigDecimal additionalFeatureRoadWidthC,
                                          BigDecimal additionalFeatureRoadWidthD,
                                          BigDecimal additionalFeatureRoadWidthE,
                                          BigDecimal additionalFeatureRoadWidthF,
                                          BigDecimal additionalFeatureNewRoadWidthA,
                                          BigDecimal additionalFeatureNewRoadWidthB,
                                          BigDecimal additionalFeatureNewRoadWidthC,
                                          BigDecimal additionalFeatureRoadWidthAcceptedBC,
                                          BigDecimal additionalFeatureRoadWidthAcceptedCD,
                                          BigDecimal additionalFeatureRoadWidthAcceptedDE,
                                          BigDecimal additionalFeatureRoadWidthAcceptedEF,
                                          BigDecimal additionalFeatureRoadWidthNewAcceptedAB,
                                          BigDecimal additionalFeatureRoadWidthNewAcceptedBC
    ) {

        for (Block block : pl.getBlocks()) {

            boolean isAccepted = false;
            String ruleNo = RULE_4_4_4;
            ScrutinyDetail scrutinyDetail = getNewScrutinyDetailRoadArea(
                    BLOCK + block.getNumber() + UNDERSCORE + HEIGHT_OF_BUILDING);
            String requiredBuildingHeight = StringUtils.EMPTY;
            BigDecimal buildingHeight = block.getBuilding().getBuildingHeight();

            if (typeOfArea.equalsIgnoreCase(OLD)) {
                if (roadWidth.compareTo(additionalFeatureRoadWidthA) < 0) {
                    errors.put(OLD_AREA_ERROR, OLD_AREA_ERROR_MSG);
                    pl.addErrors(errors);
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthB) >= 0
                        && roadWidth.compareTo(additionalFeatureRoadWidthC) < 0) {
                    isAccepted = buildingHeight.compareTo(additionalFeatureRoadWidthAcceptedBC) <= 0;
                    requiredBuildingHeight = LESS_THAN_EQUAL_TO + additionalFeatureRoadWidthAcceptedBC;
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthC) >= 0
                        && roadWidth.compareTo(additionalFeatureRoadWidthD) < 0) {
                    isAccepted = buildingHeight.compareTo(additionalFeatureRoadWidthAcceptedCD) <= 0;
                    requiredBuildingHeight = LESS_THAN_EQUAL_TO + additionalFeatureRoadWidthAcceptedCD;
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthD) >= 0
                        && roadWidth.compareTo(additionalFeatureRoadWidthE) < 0) {
                    isAccepted = buildingHeight.compareTo(additionalFeatureRoadWidthAcceptedDE) <= 0;
                    requiredBuildingHeight = LESS_THAN_EQUAL_TO + additionalFeatureRoadWidthAcceptedDE;
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthE) >= 0
                        && roadWidth.compareTo(additionalFeatureRoadWidthF) < 0) {
                    isAccepted = buildingHeight.compareTo(additionalFeatureRoadWidthAcceptedEF) <= 0;
                    requiredBuildingHeight = LESS_THAN_EQUAL_TO + additionalFeatureRoadWidthAcceptedEF;
                } else if (roadWidth.compareTo(additionalFeatureRoadWidthF) >= 0) {
                    List<SetBack> setBacks = block.getSetBacks();
                    BigDecimal permitedHeight = getPermitedHeight(roadWidth, setBacks);
                    isAccepted = buildingHeight.compareTo(permitedHeight) <= 0;
                    requiredBuildingHeight = LESS_THAN_EQUAL_TO + permitedHeight.toString();
                    ruleNo = RULE_39;
                }

            }

            if (typeOfArea.equalsIgnoreCase(NEW)) {
                if (roadWidth.compareTo(additionalFeatureNewRoadWidthA) < 0) {
                    errors.put(NEW_AREA_ERROR, NEW_AREA_ERROR_MSG);
                    pl.addErrors(errors);
                } else if (roadWidth.compareTo(additionalFeatureNewRoadWidthA) >= 0
                        && roadWidth.compareTo(additionalFeatureNewRoadWidthB) < 0) {
                    isAccepted = buildingHeight.compareTo(additionalFeatureRoadWidthNewAcceptedAB) <= 0;
                    requiredBuildingHeight = LESS_THAN_EQUAL_TO + additionalFeatureRoadWidthNewAcceptedAB;
                } else if (roadWidth.compareTo(additionalFeatureNewRoadWidthB) >= 0
                        && roadWidth.compareTo(additionalFeatureNewRoadWidthC) < 0) {
                    isAccepted = buildingHeight.compareTo(additionalFeatureRoadWidthNewAcceptedBC) <= 0;
                    requiredBuildingHeight = LESS_THAN_EQUAL_TO + additionalFeatureRoadWidthNewAcceptedBC;
                } else if (roadWidth.compareTo(additionalFeatureNewRoadWidthC) > 0) {
                    List<SetBack> setBacks = block.getSetBacks();
                    BigDecimal permitedHeight = getPermitedHeight(roadWidth, setBacks);
                    isAccepted = buildingHeight.compareTo(permitedHeight) <= 0;
                    requiredBuildingHeight = LESS_THAN_EQUAL_TO + permitedHeight.toString();
                }

            }

            if (errors.isEmpty() && StringUtils.isNotBlank(requiredBuildingHeight)) {
                ReportScrutinyDetail detail = new ReportScrutinyDetail();
                detail.setRuleNo(ruleNo);
                detail.setDescription(HEIGHT_BUILDING);
                detail.setAreaType(typeOfArea);
                detail.setRoadWidth(roadWidth.toString());
                detail.setPermissible(requiredBuildingHeight);
                detail.setProvided(String.valueOf(buildingHeight));
                detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
                
                Map<String, String> details = mapReportDetails(detail);
                addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
            }
        }
    }

    private BigDecimal getPermitedHeight(BigDecimal roadWidth, List<SetBack> setBacks) {
        BigDecimal frontYardHeight = BigDecimal.ZERO;
        for (SetBack setBack : setBacks) {
            Yard frontYard = setBack.getFrontYard();
            frontYardHeight = frontYard != null && frontYard.getMinimumDistance() != null
                    ? frontYard.getMinimumDistance()
                    : frontYardHeight;
        }

        BigDecimal sum = roadWidth.add(frontYardHeight);
        return ONE_POINTFIVE.multiply(sum).setScale(DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);
    }

    private void validatePlinthHeight(Plan pl, HashMap<String, String> errors) {
        for (Block block : pl.getBlocks()) {


            BigDecimal plintHeight = BigDecimal.ZERO;
            boolean isAccepted = false;
            BigDecimal minPlinthHeight = BigDecimal.ZERO;
            String blkNo = block.getNumber();
            ScrutinyDetail scrutinyDetail = getNewScrutinyDetail(BLOCK + blkNo + UNDERSCORE + PLINTH);
            List<BigDecimal> plinthHeights = block.getPlinthHeight();

            List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.PLINTH_HEIGHT.getValue(), false);
            Optional<PlinthHeightRequirement> matchedRule = rules.stream()
                    .filter(PlinthHeightRequirement.class::isInstance)
                    .map(PlinthHeightRequirement.class::cast)
                    .findFirst();

            if (matchedRule.isPresent()) {
                plintHeight = matchedRule.get().getPermissible();
            } else {
                plintHeight = BigDecimal.ZERO;
            }

            if (!plinthHeights.isEmpty()) {
                minPlinthHeight = plinthHeights.stream().reduce(BigDecimal::min).get().setScale(2, BigDecimal.ROUND_HALF_UP);
                if (minPlinthHeight.compareTo(plintHeight) >= 0) {
                    isAccepted = true;
                }
            } else {
                String plinthHeightLayer = String.format(DxfFileConstants.LAYER_PLINTH_HEIGHT, block.getNumber());
                errors.put(plinthHeightLayer, PLINTH_HEIGHT_IS_NOT_DEFINED_IN_LAYER + plinthHeightLayer);
                pl.addErrors(errors);
            }

            if (errors.isEmpty()) {
                ReportScrutinyDetail detail = new ReportScrutinyDetail();
                detail.setRuleNo(RULE);
                detail.setDescription(MIN_PLINTH_HEIGHT_DESC);
                detail.setPermissible(EMPTY_STRING + plintHeight);
                detail.setProvided(String.valueOf(minPlinthHeight));
                detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

                Map<String, String> details = mapReportDetails(detail);
                addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
            }
        }
    }

    private void validateBasement(Plan pl, HashMap<String, String> errors, BigDecimal additionalFeatureBasementPlotArea, BigDecimal additionalFeatureBasementAllowed) {
        for (Block block : pl.getBlocks()) {

            boolean isAccepted = false;
            String allowedBsmnt = null;
            String blkNo = block.getNumber();
            ScrutinyDetail scrutinyDetail = getNewScrutinyDetail(BLOCK + blkNo + UNDERSCORE + BASEMENT_CELLAR);
            List<SetBack> setBacks = block.getSetBacks();
            List<SetBack> basementSetbacks = setBacks.stream().filter(setback -> setback.getLevel() < 0)
                    .collect(Collectors.toList());
            OccupancyTypeHelper mostRestrictiveFarHelper = pl.getVirtualBuilding() != null
                    ? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
                    : null;

            if (!basementSetbacks.isEmpty()) {
                if (mostRestrictiveFarHelper != null && mostRestrictiveFarHelper.getType() != null
                        && (DxfFileConstants.A_AF.equalsIgnoreCase(mostRestrictiveFarHelper.getSubtype().getCode())
                        || DxfFileConstants.A_R
                        .equalsIgnoreCase(mostRestrictiveFarHelper.getSubtype().getCode())
                        || DxfFileConstants.F.equalsIgnoreCase(mostRestrictiveFarHelper.getType().getCode()))
                        && pl.getPlot() != null
                        && pl.getPlot().getArea().compareTo(additionalFeatureBasementPlotArea) <= 0) {
                    isAccepted = basementSetbacks.size() <= 1 ? true : false;
                    allowedBsmnt = additionalFeatureBasementAllowed.toString();
                } else if (mostRestrictiveFarHelper != null && mostRestrictiveFarHelper.getType() != null
                        && mostRestrictiveFarHelper.getSubtype() != null
                        && (DxfFileConstants.A_AF.equalsIgnoreCase(mostRestrictiveFarHelper.getSubtype().getCode())
                        || DxfFileConstants.A_R
                        .equalsIgnoreCase(mostRestrictiveFarHelper.getSubtype().getCode())
                        || DxfFileConstants.F.equalsIgnoreCase(mostRestrictiveFarHelper.getType().getCode()))) {
                    isAccepted = basementSetbacks.size() <= 2 ? true : false;
                    allowedBsmnt = additionalFeatureBasementAllowed.toString();
                }

                ReportScrutinyDetail detail = new ReportScrutinyDetail();
                detail.setRuleNo(RULE_47);
                detail.setDescription(MAX_BSMNT_CELLAR);
                detail.setPermissible(allowedBsmnt);
                detail.setProvided(String.valueOf(basementSetbacks.size()));
                detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

                Map<String, String> details = mapReportDetails(detail);
                addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
            }
        }
    }

    private void validateGreenBuildingsAndSustainability(Plan pl, HashMap<String, String> errors,
                                                         BigDecimal afGreenBuildingValueA,
                                                         BigDecimal afGreenBuildingValueB,
                                                         BigDecimal afGreenBuildingValueC,
                                                         BigDecimal afGreenBuildingValueD
                                                         ) {
        OccupancyTypeHelper mostRestrictiveFarHelper = pl.getVirtualBuilding() != null
                ? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
                : null;
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(COM_GREEN_BUILDINGS_SUSTAINABILITY);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        if (pl.getPlot() != null && pl.getPlot().getArea().compareTo(afGreenBuildingValueA) >= 0) {

            if (StringUtils.isNotBlank(pl.getPlanInformation().getProvisionsForGreenBuildingsAndSustainability())
                    && pl.getPlanInformation().getProvisionsForGreenBuildingsAndSustainability().equals(YES)) {

                if (mostRestrictiveFarHelper != null && mostRestrictiveFarHelper.getType() != null
                        && DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveFarHelper.getType().getCode())) {

                    if (pl.getPlot().getArea().compareTo(afGreenBuildingValueA) >= 0
                            && pl.getPlot().getArea().compareTo(afGreenBuildingValueB) < 0) {
                        validate1a(pl, scrutinyDetail);
                        validate2a(pl, scrutinyDetail);
                        validate2b(pl, scrutinyDetail);
                        validate4a(pl, scrutinyDetail);

                    } else if (pl.getPlot().getArea().compareTo(afGreenBuildingValueB) >= 0
                            && pl.getPlot().getArea().compareTo(afGreenBuildingValueC) < 0) {
                        validate1a(pl, scrutinyDetail);
                        validate2b(pl, scrutinyDetail);
                        validate4a(pl, scrutinyDetail);

                    } else if (pl.getPlot().getArea().compareTo(afGreenBuildingValueC) >= 0
                            && pl.getPlot().getArea().compareTo(afGreenBuildingValueD) < 0) {
                        validate1a(pl, scrutinyDetail);
                        validate2a(pl, scrutinyDetail);
                        validate2b(pl, scrutinyDetail);
                        validate4a(pl, scrutinyDetail);

                    } else {
                        validate1a(pl, scrutinyDetail);
                        validate2a(pl, scrutinyDetail);
                        validate2b(pl, scrutinyDetail);
                        validate4a(pl, scrutinyDetail);
                    }
                } else {

                    if (pl.getPlot().getArea().compareTo(afGreenBuildingValueA) >= 0
                            && pl.getPlot().getArea().compareTo(afGreenBuildingValueB) < 0) {
                        validate1a(pl, scrutinyDetail);
                        validate2b(pl, scrutinyDetail);
                        validate4a(pl, scrutinyDetail);

                    } else if (pl.getPlot().getArea().compareTo(afGreenBuildingValueB) >= 0
                            && pl.getPlot().getArea().compareTo(afGreenBuildingValueC) < 0) {
                        validate1a(pl, scrutinyDetail);
                        validate2a(pl, scrutinyDetail);
                        validate2b(pl, scrutinyDetail);
                        validate4a(pl, scrutinyDetail);

                    } else if (pl.getPlot().getArea().compareTo(afGreenBuildingValueC) >= 0
                            && pl.getPlot().getArea().compareTo(afGreenBuildingValueD) < 0) {
                        validate1a(pl, scrutinyDetail);
                        validate2a(pl, scrutinyDetail);
                        validate2b(pl, scrutinyDetail);
                        validate4a(pl, scrutinyDetail);
                    } else {
                        validate1a(pl, scrutinyDetail);
                        validate2a(pl, scrutinyDetail);
                        validate2b(pl, scrutinyDetail);
                        validate4a(pl, scrutinyDetail);
                    }
                }
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

            } else {
                errors.put(GREEN_BUILDINGS_AND_SUSTAINABILITY_PROVISIONS_ERROR_CODE,
                        GREEN_BUILDINGS_AND_SUSTAINABILITY_PROVISIONS_ERROR_MSG);
                pl.addErrors(errors);
            }
        }

    }

    private void validate4a(Plan pl, ScrutinyDetail scrutinyDetail) {
        if (pl.getUtility().getSegregationOfWaste() != null && !pl.getUtility().getSegregationOfWaste().isEmpty()) {
            addDetails(scrutinyDetail, FIFTY_FIVE_FOUR_A, SEG_OF_WASTE, SEG_OF_WASTE_DETAILS,
                    PROVIDED_SEG_OF_WASTE_DETAILS, Result.Accepted.getResultVal());
        }
    }

    private void validate2b(Plan pl, ScrutinyDetail scrutinyDetail) {
        if (pl.getUtility().getSolarWaterHeatingSystems() != null
                && !pl.getUtility().getSolarWaterHeatingSystems().isEmpty()) {
            addDetails(scrutinyDetail, FIFTY_FIVE_TWO_B, INSTALL_SOLAR_ASSISTED_WATER_HEATING_SYSTEM,
                    SOLAR_ASSISTED_WATER_HEATING_SYSTEM_DETAILS,
                    PROVIDED_SOLAR_ASSISTED_WATER_HEATING_SYSTEM_DETAILS, Result.Accepted.getResultVal());
        }
    }

    private void validate2a(Plan pl, ScrutinyDetail scrutinyDetail) {
        if (pl.getUtility().getSolar() != null && !pl.getUtility().getSolar().isEmpty()) {
            addDetails(scrutinyDetail, FIFTY_FIVE_TWO_A, INSTALL_SOLAR_PHOTOVOLTAIC_PANELS,
                    SOLAR_PHOTOVOLTAIC_PANEL_DETAILS, PROVIDED_SOLAR_PHOTOVOLTAIC_PANELS,
                    Result.Accepted.getResultVal());
        }
    }

    private void validate1a(Plan pl, ScrutinyDetail scrutinyDetail) {
        if (pl.getUtility().getRainWaterHarvest() != null && !pl.getUtility().getRainWaterHarvest().isEmpty()) {
            addDetails(scrutinyDetail, TEN_3, RAIN_WATER_HARVESTING, RAIN_WATER_HARVESTING_DETAILS,
                    PROVIDED_RAIN_WATER_HARVESTING, Result.Accepted.getResultVal());
        } else {
            addDetails(scrutinyDetail, TEN_3, RAIN_WATER_HARVESTING, RAIN_WATER_HARVESTING_DETAILS,
                    NOT_PROVIDED_RAIN_WATER_HARVESTING, Result.Not_Accepted.getResultVal());
        }
    }

    private void addDetails(ScrutinyDetail scrutinyDetail, String rule, String description, String required,
                            String provided, String status) {

        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(rule);
        detail.setDescription(description);
        detail.setRequired(required);
        detail.setProvided(provided);
        detail.setStatus(status);
        Map<String, String> details = mapReportDetails(detail);

        scrutinyDetail.getDetail().add(details);
    }

    private ScrutinyDetail getNewScrutinyDetailRoadArea(String key) {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, DxfFileConstants.AREA_TYPE);
        scrutinyDetail.addColumnHeading(4, DxfFileConstants.ROAD_WIDTH);
        scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
        scrutinyDetail.addColumnHeading(6, PROVIDED);
        scrutinyDetail.addColumnHeading(7, STATUS);
        scrutinyDetail.setKey(key);
        return scrutinyDetail;
    }

    private ScrutinyDetail getNewScrutinyDetail(String key) {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        scrutinyDetail.setKey(key);
        return scrutinyDetail;
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}