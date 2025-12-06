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
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class BathRoomWaterClosets extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(BathRoomWaterClosets.class);

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
   	MDMSCacheManager cache;

    /**
     * Processes the given {@link Plan} to validate bathroom water closet areas, widths, and heights
     * based on feature rules defined in MDMS.
     *
     * @param pl the plan to process
     * @return the processed plan with scrutiny details added if validation results are present
     */

    @Override
    public Plan process(Plan pl) {
        ScrutinyDetail scrutinyDetail = createScrutinyDetail();

//        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.BATHROOM_WATER_CLOSETS, false);
//        Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
//
//        if (!matchedRule.isPresent()) return pl;
//
//        MdmsFeatureRule rule = matchedRule.get();
//        BigDecimal reqArea = rule.getBathroomWCRequiredArea() != null ? rule.getBathroomWCRequiredArea() : BigDecimal.ZERO;
//        BigDecimal reqWidth = rule.getBathroomWCRequiredWidth() != null ? rule.getBathroomWCRequiredWidth() : BigDecimal.ZERO;
//        BigDecimal reqHeight = rule.getBathroomWCRequiredHeight() != null ? rule.getBathroomWCRequiredHeight() : BigDecimal.ZERO;
        
//        List<Object> rule = cache.getFeatureRules1(pl, MdmsFeatureConstants.BATHROOM_WATER_CLOSETS, false);
//        Optional<BathroomWCRule> matchedRule = rules.stream()
//            .filter(obj -> obj instanceof BathroomWCRule)
//            .map(obj -> (BathroomWCRule) obj)
//            .findFirst();

       
//        
//        List<BathroomWCRule> rules = cache.getFeatureRules1(
//        	    pl,
//        	    MdmsFeatureConstants.BATHROOM_WATER_CLOSETS,
//        	    false,
//        	    BathroomWCRule.class
//        	);
        // Now safely filter for BathroomWCRule
//        Optional<BathroomWCRule> matchedRule = rules.stream()
//            .filter(r -> r instanceof BathroomWCRule)
//            .map(r -> (BathroomWCRule) r)
//            .findFirst();
      
//
//        BigDecimal reqArea   = rule.getBathroomWCRequiredArea() != null ? rule.getBathroomWCRequiredArea() : BigDecimal.ZERO;
//        BigDecimal reqWidth  = rule.getBathroomWCRequiredWidth() != null ? rule.getBathroomWCRequiredWidth() : BigDecimal.ZERO;
//        BigDecimal reqHeight = rule.getBathroomWCRequiredHeight() != null ? rule.getBathroomWCRequiredHeight() : BigDecimal.ZERO;
        
        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.BATHROOM_WATER_CLOSETS.getValue(), false);
        Optional<BathroomWCRequirement> matchedRule = rules.stream()
            .filter(BathroomWCRequirement.class::isInstance)
            .map(BathroomWCRequirement.class::cast)
            .findFirst();
        
        if (!matchedRule.isPresent()) return pl;

            BathroomWCRequirement rule = matchedRule.get();
            BigDecimal reqArea = rule.getBathroomWCRequiredArea();
            BigDecimal reqWidth = rule.getBathroomWCRequiredWidth();
            BigDecimal reqHeight = rule.getBathroomWCRequiredHeight();
       


        // Use these values accordingly


        for (Block block : pl.getBlocks()) {
            processBlock(pl, block, reqArea, reqWidth, reqHeight, scrutinyDetail);
        }

        if (!scrutinyDetail.getDetail().isEmpty()) {
            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }

        return pl;
    }
    
    /**
     * Processes each block in the plan and validates the bathroom water closet dimensions for each floor.
     *
     * @param plan           the plan containing the block
     * @param block          the block to process
     * @param reqArea        required minimum area for bathroom WC
     * @param reqWidth       required minimum width for bathroom WC
     * @param reqHeight      required minimum height for bathroom WC
     * @param scrutinyDetail the scrutiny detail object to collect validation results
     */

    private void processBlock(Plan plan, Block block, BigDecimal reqArea, BigDecimal reqWidth, BigDecimal reqHeight,
                              ScrutinyDetail scrutinyDetail) {
        if (block.getBuilding() == null || block.getBuilding().getFloors() == null) return;

        for (Floor floor : block.getBuilding().getFloors()) {
            processFloor(plan, floor, reqArea, reqWidth, reqHeight, scrutinyDetail);
        }
    }

    
    /**
     * Processes each floor of a block and validates bathroom WC measurements.
     *
     * @param plan           the plan object
     * @param floor          the floor being processed
     * @param reqArea        required minimum area
     * @param reqWidth       required minimum width
     * @param reqHeight      required minimum height
     * @param scrutinyDetail the scrutiny detail to collect validation output
     */
    private void processFloor(Plan plan, Floor floor, BigDecimal reqArea, BigDecimal reqWidth, BigDecimal reqHeight,
                              ScrutinyDetail scrutinyDetail) {
        org.egov.common.entity.edcr.Room bathWC = floor.getBathRoomWaterClosets();
        if (bathWC == null || bathWC.getHeights() == null || bathWC.getHeights().isEmpty()
                || bathWC.getRooms() == null || bathWC.getRooms().isEmpty()) return;

        validateBathroomWaterCloset(plan, floor, bathWC.getRooms(), bathWC.getHeights(), reqArea, reqWidth, reqHeight, scrutinyDetail);
    }

    /**
     * Validates the area, width, and height of bathroom water closets on a given floor.
     * Collects validation results and populates the scrutiny detail.
     *
     * @param plan           the plan object
     * @param floor          the floor being validated
     * @param rooms          list of measurements for bathroom WCs
     * @param heights        list of height measurements
     * @param reqArea        required minimum area
     * @param reqWidth       required minimum width
     * @param reqHeight      required minimum height
     * @param scrutinyDetail the scrutiny detail to record results
     */
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

    /**
     * Creates and initializes a {@link ScrutinyDetail} object for bathroom water closet validation.
     *
     * @return a new scrutiny detail instance with column headings set
     */
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

    /**
     * Creates a result row map containing the outcome of bathroom water closet validation for a given floor.
     *
     * @param floor      the floor being validated
     * @param reqArea    required area
     * @param reqWidth   required width
     * @param reqHeight  required height
     * @param totalArea  total area provided
     * @param minWidth   minimum width provided
     * @param minHeight  minimum height provided
     * @param isAccepted whether the validation passed
     * @return a map representing one row of validation result
     */
    private Map<String, String> createResultRow(Floor floor, BigDecimal reqArea, BigDecimal reqWidth, BigDecimal reqHeight,
                                                BigDecimal totalArea, BigDecimal minWidth, BigDecimal minHeight, boolean isAccepted) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_41_IV);
        detail.setDescription(BathroomWaterClosets_DESCRIPTION);
        detail.setRequired(HEIGHT + reqHeight + TOTAL_AREA + reqArea + WIDTH + reqWidth);
        detail.setProvided(HEIGHT + minHeight + TOTAL_AREA + totalArea + WIDTH + minWidth);
        detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
        return mapReportDetails(detail);
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
