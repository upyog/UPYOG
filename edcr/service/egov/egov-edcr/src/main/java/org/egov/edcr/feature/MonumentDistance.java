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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.DISTANCE_FROM_MONUMENT;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class MonumentDistance extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(MonumentDistance.class);

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
     * Processes the given plan to validate the distance from monuments.
     * Fetches permissible values for distances and validates them against the plan details.
     *
     * @param pl The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */
    
    @Override
    public Plan process(Plan pl) {
        if (!isNearMonument(pl)) return pl;

        List<BigDecimal> distances = pl.getDistanceToExternalEntity().getMonuments();
        if (distances.isEmpty()) {
            pl.addError(DISTANCE_FROM_MONUMENT, NO_DISTANCE_PROVIDED);
            return pl;
        }

        ScrutinyDetail scrutinyDetail = initScrutinyDetail();
        ReportScrutinyDetail details = initRuleDetails();

        BigDecimal minDistance = distances.stream().min(Comparator.naturalOrder()).get();

        
        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.MONUMENT_DISTANCE.getValue(), false);
         rules.stream()
            .filter(MonumentDistanceRequirement.class::isInstance)
            .map(MonumentDistanceRequirement.class::cast)
            .findFirst();

        MonumentDistanceRequirement rule = (MonumentDistanceRequirement) rules.get(0);
        BigDecimal distanceOne = rule.getMonumentDistance_distanceOne();
        BigDecimal minDistanceTwo = rule.getMonumentDistance_minDistanceTwo();
        BigDecimal maxHeightAllowed = rule.getMonumentDistance_maxHeightofbuilding();
        BigDecimal maxFloorsAllowed = rule.getMonumentDistance_maxbuildingheightblock();

        if (hasNocNearMonument(pl)) {
            addScrutinyDetail(scrutinyDetail, details, GREATER_THAN + distanceOne,
                    PERMITTED_WITH_NOC, minDistance + WITH_NOC, Result.Accepted.getResultVal());
        } else {
            handleWithoutNoc(pl, scrutinyDetail, details, minDistance, distanceOne, minDistanceTwo,
                    maxHeightAllowed, maxFloorsAllowed);
        }

        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        return pl;
    }
    
    /**
     * Checks whether the building is located near a monument based on plan information.
     *
     * @param pl the {@link Plan} object containing plan information.
     * @return true if the building is near a monument, false otherwise.
     */
    private boolean isNearMonument(Plan pl) {
        return DcrConstants.YES.equalsIgnoreCase(pl.getPlanInformation().getBuildingNearMonument());
    }

    /**
     * Checks whether the required NOC is available for buildings near monuments.
     *
     * @param pl the {@link Plan} object containing plan information.
     * @return true if NOC is present for proximity to monument, false otherwise.
     */
    private boolean hasNocNearMonument(Plan pl) {
        return DcrConstants.YES.equalsIgnoreCase(pl.getPlanInformation().getNocNearMonument());
    }

    /**
     * Initializes and returns a {@link ScrutinyDetail} object with standard headings
     * for monument distance validation.
     *
     * @return initialized {@link ScrutinyDetail} object.
     */
    private ScrutinyDetail initScrutinyDetail() {
        ScrutinyDetail sd = new ScrutinyDetail();
        sd.setKey(COMMON_MONUMENT_DISTANCE);
        sd.addColumnHeading(1, RULE_NO);
        sd.addColumnHeading(2, DESCRIPTION);
        sd.addColumnHeading(3, DISTANCE);
        sd.addColumnHeading(4, PERMITTED);
        sd.addColumnHeading(5, PROVIDED);
        sd.addColumnHeading(6, STATUS);
        return sd;
    }

    /**
     * Initializes and returns a map containing rule number and description
     * for monument distance regulations.
     *
     * @return a {@link Map} with keys {@code RULE_NO} and {@code DESCRIPTION}.
     */
    private ReportScrutinyDetail initRuleDetails() {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_20);
        detail.setDescription(MONUMENT_DESCRIPTION);
        return detail;
    }

    /**
     * Validates construction conditions when no NOC is provided and the building is near a monument.
     * It applies different validation logic based on the distance from the monument.
     *
     * @param pl the {@link Plan} object representing the full building plan.
     * @param sd the {@link ScrutinyDetail} object to add validation results to.
     * @param details the rule details map containing description and rule number.
     * @param minDist actual minimum distance from monument.
     * @param distanceOne threshold distance for applying stricter rules.
     * @param minDistTwo the absolute minimum distance where construction is not allowed.
     * @param maxHeightAllowed maximum permissible height in the restricted zone.
     * @param maxFloorsAllowed maximum permissible floors in the restricted zone.
     */
    private void handleWithoutNoc(Plan pl, ScrutinyDetail sd, ReportScrutinyDetail details,
                                   BigDecimal minDist, BigDecimal distanceOne, BigDecimal minDistTwo,
                                   BigDecimal maxHeightAllowed, BigDecimal maxFloorsAllowed) {

        Block maxBlock = pl.getBlocks().stream()
                .max(Comparator.comparing(b -> b.getBuilding().getBuildingHeight()))
                .orElse(null);

        if (maxBlock == null) return;

        BigDecimal actualHeight = maxBlock.getBuilding().getBuildingHeight();
        BigDecimal actualFloors = maxBlock.getBuilding().getFloorsAboveGround();

        if (minDist.compareTo(distanceOne) > 0) {
            addScrutinyDetail(sd, details, GREATER_THAN + distanceOne, ALL, minDist.toString(), Result.Accepted.getResultVal());
        } else if (minDist.compareTo(minDistTwo) <= 0) {
            addScrutinyDetail(sd, details, GREATER_THAN + distanceOne,
                    NO_CONSTRUCTION_ALLOWED,
                    minDist.toString(), Result.Not_Accepted.getResultVal());
        } else {
            String permitted = String.format(BUILDING_HEIGHT_FORMAT, maxHeightAllowed, maxFloorsAllowed);
            String provided = String.format(BUILDING_HEIGHT_FORMAT, actualHeight, actualFloors);

            String status = (actualHeight.compareTo(maxHeightAllowed) <= 0 && actualFloors.compareTo(maxFloorsAllowed) <= 0)
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal();

            String range = String.format(FROM_TO_FORMAT, minDistTwo, distanceOne);
            addScrutinyDetail(sd, details, range, permitted, provided, status);
        }
    }

    /**
     * Adds a single row of monument scrutiny result to the {@link ScrutinyDetail} object.
     *
     * @param sd the scrutiny detail object to update.
     * @param detail map containing rule number and description.
     * @param distance actual distance from the monument as a string.
     * @param permitted permitted construction condition based on distance.
     * @param provided actual construction parameters provided in plan.
     * @param status result of validation (Accepted or Not Accepted).
     */
    private void addScrutinyDetail(ScrutinyDetail sd, ReportScrutinyDetail detail,
                                   String distance, String permitted, String provided, String status) {
        detail.setDistance(distance);
        detail.setPermitted(permitted);
        detail.setProvided(provided);
        detail.setStatus(status);
        Map<String, String> details = mapReportDetails(detail);
        sd.getDetail().add(details);
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
