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
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.Util;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class LandUse extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(LandUse.class);
    
    @Autowired
	MDMSCacheManager cache;
	

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */

    @Override
    public Plan validate(Plan pl) {
        return pl;
    }
    
    /**
     * Processes the given Plan object to validate commercial zone requirements.
     * 
     * @param pl the Plan object to be processed
     * @return the processed Plan with any scrutiny details added
     */

    @Override
    public Plan process(Plan pl) {
        Map<String, String> errors = new HashMap<>();
        validateCommercialZone(pl, errors);
        return pl;
    }

    /**
     * Validates whether the commercial zone requirements are met for the given Plan.
     *
     * @param pl the Plan object containing zoning and building details
     * @param errors a map to capture validation errors (currently unused)
     */
    private void validateCommercialZone(Plan pl, Map<String, String> errors) {
        Optional<LandUseRequirement> matchedRule = fetchCommercialZoneRule(pl);
        if (!matchedRule.isPresent()) return;

        BigDecimal permissibleRoadWidth = matchedRule.get().getPermissible();

        for (Block block : pl.getBlocks()) {
            validateBlockCommercialZone(pl, block, permissibleRoadWidth);
        }
    }

    /**
     * Fetches the commercial zone rule from the MDMS feature rules.
     *
     * @param pl the Plan object used to extract rule context
     * @return an Optional containing the first matched commercial zone rule if present
     */
    private Optional<LandUseRequirement> fetchCommercialZoneRule(Plan pl) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.LAND_USE.getValue(), false);
       return rules.stream()
            .filter(LandUseRequirement.class::isInstance)
            .map(LandUseRequirement.class::cast)
            .findFirst();
        
    }

    /**
     * Validates a specific block in the plan against commercial zone rules.
     *
     * @param pl the Plan object
     * @param block the block within the plan to validate
     * @param permissibleRoadWidth the minimum road width required for commercial zoning
     */
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

        initializeScrutinyHeader(BLOCK + blkNo + U_LANDUSE);
        Map<String, String> details = buildScrutinyDetails(pl, commercialFloors, isAccepted);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    /**
     * Retrieves the floor numbers marked as commercial in a block.
     *
     * @param block the block to analyze
     * @return a comma-separated StringBuffer of commercial floor numbers
     */
    private StringBuffer getCommercialFloors(Block block) {
        StringBuffer floorNos = new StringBuffer();
        for (Floor floor : block.getBuilding().getFloors()) {
            if (isFloorCommercial(floor)) {
                floorNos.append(floor.getNumber()).append(",");
            }
        }
        return floorNos;
    }

    /**
     * Checks if a floor is used for commercial occupancy based on type or subtype.
     *
     * @param floor the floor to check
     * @return true if commercial occupancy is found, false otherwise
     */
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

    /**
     * Initializes the scrutiny detail header for a specific block zone validation.
     *
     * @param key the unique key identifying the scrutiny header
     */
    private void initializeScrutinyHeader(String key) {
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, ROAD_WIDTH);
        scrutinyDetail.addColumnHeading(4, REQUIRED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        scrutinyDetail.setKey(key);
    }

    /**
     * Builds the scrutiny detail map that will be added to the report.
     *
     * @param pl the Plan object
     * @param commercialFloors the string buffer containing commercial floor numbers
     * @param isAccepted indicates whether the commercial zoning is valid
     * @return a map of scrutiny detail values
     */
    private Map<String, String> buildScrutinyDetails(Plan pl, StringBuffer commercialFloors, boolean isAccepted) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_28);
        detail.setDescription(LAND_USE_ZONE);
        detail.setRoadWidth(pl.getPlanInformation().getRoadWidth().toString());
        detail.setRequired(AT_LEAST_ONE_COMMERCIAL);
        detail.setProvided(commercialFloors.length() == 0
                ? NO_COMMERCIAL_FLOOR
                : commercialFloors.substring(0, commercialFloors.length() - 1) + FLOORS_ARE_COMMERCIAL);
        detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

        return mapReportDetails(detail);
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
