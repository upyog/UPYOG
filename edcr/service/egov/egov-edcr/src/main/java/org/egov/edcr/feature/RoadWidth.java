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

import static org.egov.edcr.constants.CommonFeatureConstants.METER;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.F_CB;
import static org.egov.edcr.constants.DxfFileConstants.F_RT;
import static org.egov.edcr.constants.DxfFileConstants.G;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

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
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoadWidth extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(RoadWidth.class);

    @Autowired
	MDMSCacheManager cache;

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>(); // No amendments defined for this feature
    }

    /**
     * Validates the provided Plan object.
     * No specific validation logic is implemented for road width in this method.
     *
     * @param pl the Plan object to validate
     * @return the same Plan object without modifications
     */
    @Override
    public Plan validate(Plan pl) {
        return pl; // No validation logic currently implemented
    }

    /**
     * Processes the Plan object to verify if the provided road width meets
     * the required minimum width for the most restrictive occupancy type.
     * Adds appropriate scrutiny detail entries to the report.
     *
     * @param pl the Plan object to process
     * @return the modified Plan object with road width scrutiny results
     */
    @Override
    public Plan process(Plan pl) {
        if (pl.getPlanInformation() == null || pl.getPlanInformation().getRoadWidth() == null) {
            return pl;
        }

        BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();
        String typeOfArea = pl.getPlanInformation().getTypeOfArea();

        if (!NEW.equalsIgnoreCase(typeOfArea)) {
            return pl;
        }

        ScrutinyDetail scrutinyDetail = buildRoadWidthScrutinyDetail();

        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_34);
        detail.setDescription(ROADWIDTH_DESCRIPTION);

        Map<String, BigDecimal> occupancyValuesMap = getOccupancyValues(pl);

        if (pl.getVirtualBuilding() == null || pl.getVirtualBuilding().getMostRestrictiveFarHelper() == null) {
            return pl;
        }

        OccupancyHelperDetail occupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype();
        if (occupancyType == null) {
            occupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType();
        }

        if (occupancyType == null) {
            return pl;
        }

        detail.setOccupancy(occupancyType.getName());

        BigDecimal roadWidthRequired = occupancyValuesMap.get(occupancyType.getCode());
        if (roadWidthRequired != null) {
            detail.getPermitted(roadWidthRequired + METER);
            detail.getProvided(roadWidth + METER);
            String status = roadWidth.compareTo(roadWidthRequired) >= 0
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal();
            detail.setProvided(roadWidth + METER);
            detail.setStatus(status);
            Map<String, String> details = mapReportDetails(detail);
            addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
        }

        return pl;
    }
    /**
     * Retrieves the permissible road width values for various occupancy types from the MDMS feature rules.
     * These values are used to validate the provided road width against the expected requirement.
     *
     * @param plan the Plan object for which rules are fetched
     * @return a map of occupancy codes to their respective permissible road width values
     */
    public Map<String, BigDecimal> getOccupancyValues(Plan plan) {
        BigDecimal roadWidthValue = BigDecimal.ZERO;

    	List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.ROAD_WIDTH.getValue(), false);
        Optional<RoadWidthRequirement> matchedRule = rules.stream()
            .filter(RoadWidthRequirement.class::isInstance)
            .map(RoadWidthRequirement.class::cast)
            .findFirst();
        if (matchedRule.isPresent()) {
            roadWidthValue = matchedRule.get().getPermissible();
        }

        Map<String, BigDecimal> roadWidthValues = new HashMap<>();
        roadWidthValues.put(B, roadWidthValue);
        roadWidthValues.put(D, roadWidthValue);
        roadWidthValues.put(G, roadWidthValue);
        roadWidthValues.put(F, roadWidthValue);
        roadWidthValues.put(F_RT, roadWidthValue);
        roadWidthValues.put(F_CB, roadWidthValue);

        return roadWidthValues;
    }
    

/**
 * Constructs a ScrutinyDetail object pre-populated with column headers
 * relevant for road width validation.
 *
 * @return a configured ScrutinyDetail object ready to store validation results
 */
    private ScrutinyDetail buildRoadWidthScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(Common_Road_Width);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, OCCUPANCY);
        scrutinyDetail.addColumnHeading(4, PERMITTED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        return scrutinyDetail;
    }


}

