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
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BathRoomWaterClosets extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(BathRoomWaterClosets.class);

    // Rule identifier and description for bathroom water closets scrutiny
    private static final String RULE_41_IV = "41-iv";
    public static final String BathroomWaterClosets_DESCRIPTION = "Bathroom Water Closets";
    public static final String TOTAL_AREA = ", Total Area >= ";
    public static final String WIDTH = ", Width >= ";
    public static final String HEIGHT = "Height >= ";

    
    /**
     * This method is used to validate the plan object.
     * Currently, no validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
   	CacheManagerMdms cache;

	/**
	 * This method processes the plan to validate bathroom water closets dimensions
	 * against permissible values. It checks the height, width, and total area of
	 * bathroom water closets in the plan and generates scrutiny details.
	 *
	 * @param pl The plan object to process.
	 * @return The processed plan object with scrutiny details added.
	 */
	/**
	 *
	 */

    @Override
    public Plan process(Plan pl) {
        ScrutinyDetail scrutinyDetail = createScrutinyDetail();

        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.BATHROOM_WATER_CLOSETS, false);
        Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

        if (!matchedRule.isPresent()) return pl;

        MdmsFeatureRule rule = matchedRule.get();
        BigDecimal reqArea = rule.getBathroomWCRequiredArea() != null ? rule.getBathroomWCRequiredArea() : BigDecimal.ZERO;
        BigDecimal reqWidth = rule.getBathroomWCRequiredWidth() != null ? rule.getBathroomWCRequiredWidth() : BigDecimal.ZERO;
        BigDecimal reqHeight = rule.getBathroomWCRequiredHeight() != null ? rule.getBathroomWCRequiredHeight() : BigDecimal.ZERO;

        for (Block block : pl.getBlocks()) {
            processBlock(pl, block, reqArea, reqWidth, reqHeight, scrutinyDetail);
        }

        if (!scrutinyDetail.getDetail().isEmpty()) {
            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }

        return pl;
    }

    private void processBlock(Plan plan, Block block, BigDecimal reqArea, BigDecimal reqWidth, BigDecimal reqHeight,
                              ScrutinyDetail scrutinyDetail) {
        if (block.getBuilding() == null || block.getBuilding().getFloors() == null) return;

        for (Floor floor : block.getBuilding().getFloors()) {
            processFloor(plan, floor, reqArea, reqWidth, reqHeight, scrutinyDetail);
        }
    }

    private void processFloor(Plan plan, Floor floor, BigDecimal reqArea, BigDecimal reqWidth, BigDecimal reqHeight,
                              ScrutinyDetail scrutinyDetail) {
        org.egov.common.entity.edcr.Room bathWC = floor.getBathRoomWaterClosets();
        if (bathWC == null || bathWC.getHeights() == null || bathWC.getHeights().isEmpty()
                || bathWC.getRooms() == null || bathWC.getRooms().isEmpty()) return;

        validateBathroomWaterCloset(plan, floor, bathWC.getRooms(), bathWC.getHeights(), reqArea, reqWidth, reqHeight, scrutinyDetail);
    }

    private void validateBathroomWaterCloset(Plan plan, Floor floor, List<Measurement> rooms, List<RoomHeight> heights,
                                             BigDecimal reqArea, BigDecimal reqWidth, BigDecimal reqHeight,
                                             ScrutinyDetail scrutinyDetail) {

        BigDecimal totalArea = BigDecimal.ZERO;
        BigDecimal minWidth = rooms.get(0).getWidth();
        BigDecimal minHeight = heights.get(0).getHeight();

        for (Measurement m : rooms) {
            totalArea = totalArea.add(m.getArea());
            if (m.getWidth().compareTo(minWidth) < 0) {
                minWidth = m.getWidth();
            }
        }

        for (RoomHeight rh : heights) {
            if (rh.getHeight().compareTo(minHeight) < 0) {
                minHeight = rh.getHeight();
            }
        }

        boolean isAccepted = minHeight.compareTo(reqHeight) >= 0
                && totalArea.compareTo(reqArea) >= 0
                && minWidth.compareTo(reqWidth) >= 0;

        Map<String, String> resultRow = createResultRow(floor, reqArea, reqWidth, reqHeight, totalArea, minWidth, minHeight, isAccepted);
        scrutinyDetail.getDetail().add(resultRow);
    }

    private ScrutinyDetail createScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(Common_Bathroom_Water_Closets);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
    }

    private Map<String, String> createResultRow(Floor floor, BigDecimal reqArea, BigDecimal reqWidth, BigDecimal reqHeight,
                                                BigDecimal totalArea, BigDecimal minWidth, BigDecimal minHeight, boolean isAccepted) {
        Map<String, String> resultRow = new HashMap<>();
        resultRow.put(RULE_NO, RULE_41_IV);
        resultRow.put(DESCRIPTION, BathroomWaterClosets_DESCRIPTION);
        resultRow.put(REQUIRED, HEIGHT + reqHeight + TOTAL_AREA + reqArea + ", Width >= " + reqWidth);
        resultRow.put(PROVIDED, HEIGHT + minHeight + TOTAL_AREA + totalArea + WIDTH + minWidth);
        resultRow.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
        return resultRow;
    }


    /**
     * This method returns an empty map as no amendments are defined for this feature.
     *
     * @return An empty map of amendments.
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
