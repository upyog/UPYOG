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
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonumentDistance extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(MonumentDistance.class);

    // Constants for rule identifiers and descriptions
    private static final String RULE_20 = "20";
    public static final String MONUMENT_DESCRIPTION = "Distance from monument";

    @Autowired
	CacheManagerMdms cache;

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
            pl.addError("Distance_From_Monument", "No distance is provided from monument");
            return pl;
        }

        ScrutinyDetail scrutinyDetail = initScrutinyDetail();
        Map<String, String> details = initRuleDetails();

        BigDecimal minDistance = distances.stream().min(Comparator.naturalOrder()).get();

        
        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.MONUMENT_DISTANCE, false);
        if (rules.isEmpty()) return pl;

        MdmsFeatureRule rule = (MdmsFeatureRule) rules.get(0);
        BigDecimal distanceOne = rule.getMonumentDistance_distanceOne();
        BigDecimal minDistanceTwo = rule.getMonumentDistance_minDistanceTwo();
        BigDecimal maxHeightAllowed = rule.getMonumentDistance_maxHeightofbuilding();
        BigDecimal maxFloorsAllowed = rule.getMonumentDistance_maxbuildingheightblock();

        if (hasNocNearMonument(pl)) {
            addScrutinyDetail(scrutinyDetail, details, ">" + distanceOne,
                    "Permitted with NOC", minDistance + " with NOC", Result.Accepted.getResultVal());
        } else {
            handleWithoutNoc(pl, scrutinyDetail, details, minDistance, distanceOne, minDistanceTwo,
                    maxHeightAllowed, maxFloorsAllowed);
        }

        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        return pl;
    }
    
    private boolean isNearMonument(Plan pl) {
        return "YES".equalsIgnoreCase(pl.getPlanInformation().getBuildingNearMonument());
    }

    private boolean hasNocNearMonument(Plan pl) {
        return "YES".equalsIgnoreCase(pl.getPlanInformation().getNocNearMonument());
    }

    private ScrutinyDetail initScrutinyDetail() {
        ScrutinyDetail sd = new ScrutinyDetail();
        sd.setKey("Common_Monument Distance");
        sd.addColumnHeading(1, RULE_NO);
        sd.addColumnHeading(2, DESCRIPTION);
        sd.addColumnHeading(3, DISTANCE);
        sd.addColumnHeading(4, PERMITTED);
        sd.addColumnHeading(5, PROVIDED);
        sd.addColumnHeading(6, STATUS);
        return sd;
    }

    private Map<String, String> initRuleDetails() {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_20);
        details.put(DESCRIPTION, MONUMENT_DESCRIPTION);
        return details;
    }

    private void handleWithoutNoc(Plan pl, ScrutinyDetail sd, Map<String, String> details,
                                   BigDecimal minDist, BigDecimal distanceOne, BigDecimal minDistTwo,
                                   BigDecimal maxHeightAllowed, BigDecimal maxFloorsAllowed) {

        Block maxBlock = pl.getBlocks().stream()
                .max(Comparator.comparing(b -> b.getBuilding().getBuildingHeight()))
                .orElse(null);

        if (maxBlock == null) return;

        BigDecimal actualHeight = maxBlock.getBuilding().getBuildingHeight();
        BigDecimal actualFloors = maxBlock.getBuilding().getFloorsAboveGround();

        if (minDist.compareTo(distanceOne) > 0) {
            addScrutinyDetail(sd, details, ">" + distanceOne, "ALL", minDist.toString(), Result.Accepted.getResultVal());
        } else if (minDist.compareTo(minDistTwo) <= 0) {
            addScrutinyDetail(sd, details, ">" + distanceOne,
                    "No Construction is allowed within 100 mts from monument",
                    minDist.toString(), Result.Not_Accepted.getResultVal());
        } else {
            String permitted = String.format("Building Height: %smt, No of floors: %s", maxHeightAllowed, maxFloorsAllowed);
            String provided = String.format("Building Height: %smt, No of floors: %s", actualHeight, actualFloors);

            String status = (actualHeight.compareTo(maxHeightAllowed) <= 0 && actualFloors.compareTo(maxFloorsAllowed) <= 0)
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal();

            String range = String.format("From %s to %s", minDistTwo, distanceOne);
            addScrutinyDetail(sd, details, range, permitted, provided, status);
        }
    }

    private void addScrutinyDetail(ScrutinyDetail sd, Map<String, String> details,
                                   String distance, String permitted, String provided, String status) {
        Map<String, String> row = new HashMap<>(details);
        row.put(DISTANCE, distance);
        row.put(PERMITTED, permitted);
        row.put(PROVIDED, provided);
        row.put(STATUS, status);
        sd.getDetail().add(row);
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
