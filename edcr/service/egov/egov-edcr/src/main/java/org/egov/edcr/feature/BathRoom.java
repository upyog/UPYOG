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
public class BathRoom extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(BathRoom.class);

    @Autowired
	MDMSCacheManager cache;

    @Override
    public Plan validate(Plan pl) {
        // Currently, no validation logic is implemented
        return pl;
    }
    
    /**
     * Processes the given {@link Plan} object to validate bathroom dimensions (area and width) on each floor of all blocks,
     * based on the rules retrieved from MDMS configuration. Adds scrutiny details to the report if validations are performed.
     *
     * @param pl The plan to be processed.
     * @return The processed plan with scrutiny details updated.
     */
    
    @Override
    public Plan process(Plan pl) {
        ScrutinyDetail scrutinyDetail = createScrutinyDetail();

        for (Block block : pl.getBlocks()) {
            processBlock(pl, block, scrutinyDetail);
        }

        if (!scrutinyDetail.getDetail().isEmpty()) {
            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }

        return pl;
    }

    /**
     * Processes an individual block within the plan to validate bathroom rules.
     *
     * @param plan            The plan containing the block.
     * @param block           The block to be processed.
     * @param scrutinyDetail  The scrutiny detail to which validation results will be added.
     */
    
    private void processBlock(Plan plan, Block block, ScrutinyDetail scrutinyDetail) {
        if (block.getBuilding() == null || block.getBuilding().getFloors() == null) return;

        List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.BATHROOM.getValue(), false);
        Optional<BathroomRequirement> matchedRule = rules.stream()
            .filter(BathroomRequirement.class::isInstance)
            .map(BathroomRequirement.class::cast)
            .findFirst();

        if (!matchedRule.isPresent()) return;

        BathroomRequirement rule = matchedRule.get();
        BigDecimal permittedArea = rule.getBathroomtotalArea() != null ? rule.getBathroomtotalArea() : BigDecimal.ZERO;
        BigDecimal permittedMinWidth = rule.getBathroomMinWidth() != null ? rule.getBathroomMinWidth() : BigDecimal.ZERO;

        for (Floor floor : block.getBuilding().getFloors()) {
            processFloor(plan, floor, permittedArea, permittedMinWidth, scrutinyDetail);
        }
    }

    /**
     * Processes an individual floor to extract bathroom measurements and perform validations.
     *
     * @param plan              The overall plan context.
     * @param floor             The floor to process.
     * @param permittedArea     The minimum required area for a bathroom.
     * @param permittedMinWidth The minimum required width for a bathroom.
     * @param scrutinyDetail    The scrutiny detail object to populate with validation results.
     */
    
    private void processFloor(Plan plan, Floor floor, BigDecimal permittedArea, BigDecimal permittedMinWidth,
                              ScrutinyDetail scrutinyDetail) {
        Room bathRoom = floor.getBathRoom();
        if (bathRoom == null || bathRoom.getRooms() == null || bathRoom.getHeights() == null) return;

        List<Measurement> rooms = bathRoom.getRooms();
        List<RoomHeight> heights = bathRoom.getHeights();

        if (rooms.isEmpty() || heights.isEmpty()) return;

        validateBathroom(plan, floor, rooms, heights, permittedArea, permittedMinWidth, scrutinyDetail);
    }

    
    /**
     * Validates the area, width, and height of bathroom rooms on a floor against the permitted values.
     * Adds the result to the scrutiny detail.
     *
     * @param plan              The plan context.
     * @param floor             The floor under validation.
     * @param rooms             The list of bathroom room measurements.
     * @param heights           The list of bathroom room heights.
     * @param permittedArea     The minimum permissible total bathroom area.
     * @param permittedMinWidth The minimum permissible bathroom width.
     * @param scrutinyDetail    The scrutiny detail object to update.
     */
    private void validateBathroom(Plan plan, Floor floor, List<Measurement> rooms, List<RoomHeight> heights,
                                  BigDecimal permittedArea, BigDecimal permittedMinWidth, ScrutinyDetail scrutinyDetail) {

        BigDecimal totalArea = BigDecimal.ZERO;
        BigDecimal minWidth = rooms.get(0).getWidth();

        for (Measurement m : rooms) {
            totalArea = totalArea.add(m.getArea());
            if (m.getWidth().compareTo(minWidth) < 0) {
                minWidth = m.getWidth();
            }
        }

        BigDecimal minHeight = heights.get(0).getHeight();
        for (RoomHeight rh : heights) {
            if (rh.getHeight().compareTo(minHeight) < 0) {
                minHeight = rh.getHeight();
            }
        }

        boolean isAccepted = totalArea.compareTo(permittedArea) >= 0 && minWidth.compareTo(permittedMinWidth) >= 0;
        Map<String, String> resultRow = createResultRow(floor, permittedArea, permittedMinWidth, totalArea, minWidth, isAccepted);
        scrutinyDetail.getDetail().add(resultRow);
    }

    
    /**
     * Creates a new {@link ScrutinyDetail} object and initializes it with column headings.
     *
     * @return A new ScrutinyDetail instance with predefined column headers.
     */
    private ScrutinyDetail createScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(Common_Bathroom);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
    }

    
    /**
     * Creates a map representing a single row of bathroom validation results for a floor.
     *
     * @param floor             The floor being validated.
     * @param permittedArea     The required minimum bathroom area.
     * @param permittedMinWidth The required minimum bathroom width.
     * @param totalArea         The actual total bathroom area found.
     * @param minWidth          The actual minimum bathroom width found.
     * @param isAccepted        The result of the validation (true if passed).
     * @return A map containing all details of the result row.
     */
    private Map<String, String> createResultRow(Floor floor, BigDecimal permittedArea, BigDecimal permittedMinWidth,
                                                BigDecimal totalArea, BigDecimal minWidth, boolean isAccepted) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_41_IV);
        detail.setDescription(BATHROOM_DESCRIPTION);
        detail.setRequired(TOTAL_AREA + permittedArea.toString() + WIDTH + permittedMinWidth.toString());
        detail.setProvided(TOTAL_AREA + totalArea.toString() + WIDTH + minWidth.toString());
        detail.setStatus(isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
        return mapReportDetails(detail);
    }

    /**
     * Retrieves a new Amendment object instance.
     * This method creates and returns a fresh Amendment entity that can be used
     * for processing amendment-related operations in the EDCR system.
     *
     * @return A new Amendment object with default initialization
     */
  @Override
  public Map<String, Date> getAmendments() {
      // Return an empty map as no amendments are defined
      return new LinkedHashMap<>();
  }

}
