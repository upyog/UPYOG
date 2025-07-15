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
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.CacheManagerMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BathRoom extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(BathRoom.class);

    // Rule identifier and description for bathroom scrutiny
    private static final String RULE_41_IV = "41-iv";
    public static final String BATHROOM_DESCRIPTION = "Bathroom";
    public static final String TOTAL_AREA = "Total Area >= ";
    public static final String WIDTH = ", Width >= ";

    @Autowired
	CacheManagerMdms cache;

    @Override
    public Plan validate(Plan pl) {
        // Currently, no validation logic is implemented
        return pl;
    }
    
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

    private void processBlock(Plan plan, Block block, ScrutinyDetail scrutinyDetail) {
        if (block.getBuilding() == null || block.getBuilding().getFloors() == null) return;

        List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.BATHROOM, false);
        Optional<MdmsFeatureRule> matchedRule = rules.stream()
                .map(obj -> (MdmsFeatureRule) obj)
                .findFirst();

        if (!matchedRule.isPresent()) return;

        MdmsFeatureRule rule = matchedRule.get();
        BigDecimal permittedArea = rule.getBathroomtotalArea() != null ? rule.getBathroomtotalArea() : BigDecimal.ZERO;
        BigDecimal permittedMinWidth = rule.getBathroomMinWidth() != null ? rule.getBathroomMinWidth() : BigDecimal.ZERO;

        for (Floor floor : block.getBuilding().getFloors()) {
            processFloor(plan, floor, permittedArea, permittedMinWidth, scrutinyDetail);
        }
    }

    private void processFloor(Plan plan, Floor floor, BigDecimal permittedArea, BigDecimal permittedMinWidth,
                              ScrutinyDetail scrutinyDetail) {
        Room bathRoom = floor.getBathRoom();
        if (bathRoom == null || bathRoom.getRooms() == null || bathRoom.getHeights() == null) return;

        List<Measurement> rooms = bathRoom.getRooms();
        List<RoomHeight> heights = bathRoom.getHeights();

        if (rooms.isEmpty() || heights.isEmpty()) return;

        validateBathroom(plan, floor, rooms, heights, permittedArea, permittedMinWidth, scrutinyDetail);
    }

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

    private Map<String, String> createResultRow(Floor floor, BigDecimal permittedArea, BigDecimal permittedMinWidth,
                                                BigDecimal totalArea, BigDecimal minWidth, boolean isAccepted) {
        Map<String, String> resultRow = new HashMap<>();
        resultRow.put(RULE_NO, RULE_41_IV);
        resultRow.put(DESCRIPTION, BATHROOM_DESCRIPTION);
        resultRow.put(REQUIRED, TOTAL_AREA + permittedArea.toString() + WIDTH + permittedMinWidth.toString());
        resultRow.put(PROVIDED, TOTAL_AREA + totalArea.toString() + WIDTH + minWidth.toString());
        resultRow.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
        return resultRow;
    }
    
  @Override
  public Map<String, Date> getAmendments() {
      // Return an empty map as no amendments are defined
      return new LinkedHashMap<>();
  }

}
