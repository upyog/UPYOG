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
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.Util;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LandUse extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(LandUse.class);

    // Constants for rule identifiers and descriptions
    private static final String RULE_28 = "28";
    private static final String ROAD_WIDTH = "Road Width";

    // Variable to store permissible road width
    public static BigDecimal RoadWidth = BigDecimal.ZERO;

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	CacheManagerMdms cache;
	

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */
//    @Override
//    public Plan validate(Plan pl) {
//        return pl;
//    }
//
//    /**
//     * Processes the given plan to validate land use zones.
//     * Fetches permissible values for road width and validates them against the plan details.
//     *
//     * @param pl The plan object to process.
//     * @return The processed plan object with scrutiny details added.
//     */
//    @Override
//    public Plan process(Plan pl) {
//        // Map to store validation errors
//        HashMap<String, String> errors = new HashMap<>();
//
//        // Validate commercial zones in the plan
//        validateCommercialZone(pl, errors);
//        return pl;
//    }
//
//    /**
//     * Validates the commercial zone for the given plan.
//     * Checks if the road width and land use zone meet the required conditions.
//     *
//     * @param pl The plan object to validate.
//     * @param errors The map to store validation errors.
//     */
//    private void validateCommercialZone(Plan pl, HashMap<String, String> errors) {
//    	List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.LAND_USE, false);
//        Optional<MdmsFeatureRule> matchedRule = rules.stream()
//                .map(obj -> (MdmsFeatureRule) obj)
//                .findFirst();
//		
//		
//			if (matchedRule.isPresent()) {
//			    MdmsFeatureRule rule = matchedRule.get();
//			    RoadWidth = rule.getPermissible();
//			    
//			}
//
//        // Iterate through all blocks in the plan
//        for (Block block : pl.getBlocks()) {
//            StringBuffer floorNos = new StringBuffer();
//            boolean isAccepted = false;
//            String blkNo = block.getNumber();
//
//            // Initialize scrutiny detail for land use validation
//            scrutinyDetail.addColumnHeading(1, RULE_NO);
//            scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//            scrutinyDetail.addColumnHeading(3, ROAD_WIDTH);
//            scrutinyDetail.addColumnHeading(4, REQUIRED);
//            scrutinyDetail.addColumnHeading(5, PROVIDED);
//            scrutinyDetail.addColumnHeading(6, STATUS);
//            scrutinyDetail.setKey("Block_" + blkNo + "_" + "Land Use");
//
//            // Validate road width and land use zone
//            BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();
//            if (pl.getPlanInformation() != null && roadWidth != null
//                    && StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
//                    && DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
//                    && Util.roundOffTwoDecimal(roadWidth).compareTo(RoadWidth) >= 0) {
//
//                List<Floor> floors = block.getBuilding().getFloors();
//
//                // Check if at least one floor is commercial
//                for (Floor floor : floors) {
//                    List<Occupancy> occupancies = floor.getOccupancies();
//                    List<String> occupancyTypes = new ArrayList<>();
//
//                    for (Occupancy occupancy : occupancies) {
//                        if (occupancy.getTypeHelper() != null && occupancy.getTypeHelper().getType() != null) {
//                            occupancyTypes.add(occupancy.getTypeHelper().getType().getCode());
//                        }
//
//                        if (occupancy.getTypeHelper() != null && occupancy.getTypeHelper().getSubtype() != null) {
//                            occupancyTypes.add(occupancy.getTypeHelper().getSubtype().getCode());
//                        }
//                    }
//
//                    if (occupancyTypes.size() > 0) {
//                        if (occupancyTypes.contains(DxfFileConstants.F)) {
//                            floorNos.append(floor.getNumber()).append(",");
//                        }
//                    }
//                }
//
//                // Add scrutiny details for land use validation
//                Map<String, String> details = new HashMap<>();
//                details.put(RULE_NO, RULE_28);
//                details.put(DESCRIPTION, "Land use Zone");
//                details.put(ROAD_WIDTH, pl.getPlanInformation().getRoadWidth().toString());
//                details.put(REQUIRED, "At least one floor should be commercial");
//                details.put(PROVIDED, floorNos.length() == 0 ? "Commercial floor is not present"
//                        : floorNos.toString().substring(0, floorNos.length() - 1) + " floors are commercial");
//                details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
//                scrutinyDetail.getDetail().add(details);
//                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//            }
//        }
//    }
    
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    @Override
    public Plan process(Plan pl) {
        Map<String, String> errors = new HashMap<>();
        validateCommercialZone(pl, errors);
        return pl;
    }

    private void validateCommercialZone(Plan pl, Map<String, String> errors) {
        Optional<MdmsFeatureRule> matchedRule = fetchCommercialZoneRule(pl);
        if (!matchedRule.isPresent()) return;

        BigDecimal permissibleRoadWidth = matchedRule.get().getPermissible();

        for (Block block : pl.getBlocks()) {
            validateBlockCommercialZone(pl, block, permissibleRoadWidth);
        }
    }

    private Optional<MdmsFeatureRule> fetchCommercialZoneRule(Plan pl) {
        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.LAND_USE, false);
        return rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
    }

    private void validateBlockCommercialZone(Plan pl, Block block, BigDecimal permissibleRoadWidth) {
        BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();
        String landUseZone = pl.getPlanInformation().getLandUseZone();

        if (pl.getPlanInformation() == null || roadWidth == null || 
            StringUtils.isBlank(landUseZone) || 
            !DxfFileConstants.COMMERCIAL.equalsIgnoreCase(landUseZone) || 
            Util.roundOffTwoDecimal(roadWidth).compareTo(permissibleRoadWidth) < 0) {
            return;
        }

        String blkNo = block.getNumber();
        StringBuffer commercialFloors = getCommercialFloors(block);
        boolean isAccepted = commercialFloors.length() > 0;

        initializeScrutinyHeader("Block_" + blkNo + "_Land Use");
        Map<String, String> details = buildScrutinyDetails(pl, commercialFloors, isAccepted);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    private StringBuffer getCommercialFloors(Block block) {
        StringBuffer floorNos = new StringBuffer();
        for (Floor floor : block.getBuilding().getFloors()) {
            if (isFloorCommercial(floor)) {
                floorNos.append(floor.getNumber()).append(",");
            }
        }
        return floorNos;
    }

    private boolean isFloorCommercial(Floor floor) {
        for (Occupancy occupancy : floor.getOccupancies()) {
            if (occupancy.getTypeHelper() != null) {
                String type = occupancy.getTypeHelper().getType() != null 
                              ? occupancy.getTypeHelper().getType().getCode() : null;
                String subtype = occupancy.getTypeHelper().getSubtype() != null 
                                 ? occupancy.getTypeHelper().getSubtype().getCode() : null;

                if (DxfFileConstants.F.equalsIgnoreCase(type) || DxfFileConstants.F.equalsIgnoreCase(subtype)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initializeScrutinyHeader(String key) {
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, ROAD_WIDTH);
        scrutinyDetail.addColumnHeading(4, REQUIRED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        scrutinyDetail.setKey(key);
    }

    private Map<String, String> buildScrutinyDetails(Plan pl, StringBuffer commercialFloors, boolean isAccepted) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_28);
        details.put(DESCRIPTION, "Land use Zone");
        details.put(ROAD_WIDTH, pl.getPlanInformation().getRoadWidth().toString());
        details.put(REQUIRED, "At least one floor should be commercial");
        details.put(PROVIDED, commercialFloors.length() == 0 
            ? "Commercial floor is not present"
            : commercialFloors.substring(0, commercialFloors.length() - 1) + " floors are commercial");
        details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
        return details;
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
