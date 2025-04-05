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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadRoom_Citya extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(HeadRoom_Citya.class);

    // Constants for rule identifiers and descriptions
    private static final String RULE42_5_ii = "42-5-ii";
    private static final String RULE_42_5_ii_DESCRIPTION = "Minimum clear of stair head-room";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param plan The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan plan) {
        return plan;
    }

    /**
     * Processes the given plan to validate the headroom dimensions.
     * Fetches permissible values for headroom and validates them against the plan details.
     *
     * @param plan The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */
    @Override
    public Plan process(Plan plan) {
        // Variable to store permissible headroom value
        BigDecimal HeadRoomValue = BigDecimal.ZERO;

        // Determine the occupancy type and feature for fetching permissible values
        String occupancyName = null;
        String feature = MdmsFeatureConstants.HEAD_ROOM;

        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(plan.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch permissible values for headroom
        Map<String, List<Map<String, Object>>> edcrRuleList = plan.getEdcrRulesFeatures();
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE);

        List<Map<String, Object>> permissibleValue = new ArrayList<>();
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
            HeadRoomValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
        } else {
            HeadRoomValue = BigDecimal.ZERO;
        }

        // Iterate through all blocks in the plan
        for (Block block : plan.getBlocks()) {
            if (block.getBuilding() != null) {
                // Initialize scrutiny detail for headroom validation
                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                scrutinyDetail.addColumnHeading(3, REQUIRED);
                scrutinyDetail.addColumnHeading(4, PROVIDED);
                scrutinyDetail.addColumnHeading(5, STATUS);
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Stair Headroom");

                // Get headroom details for the block
                org.egov.common.entity.edcr.HeadRoom headRoom = block.getBuilding().getHeadRoom();

                if (headRoom != null) {
                    List<BigDecimal> headRoomDimensions = headRoom.getHeadRoomDimensions();

                    if (headRoomDimensions != null && !headRoomDimensions.isEmpty()) {
                        // Get the minimum headroom dimension
                        BigDecimal minHeadRoomDimension = headRoomDimensions.stream().reduce(BigDecimal::min).get();
                        BigDecimal minWidth = Util.roundOffTwoDecimal(minHeadRoomDimension);

                        // Validate headroom dimensions
                        if (minWidth.compareTo(HeadRoomValue) >= 0) {
                            setReportOutputDetails(plan, RULE42_5_ii, RULE_42_5_ii_DESCRIPTION,
                                    String.valueOf(HeadRoomValue), String.valueOf(minWidth), Result.Accepted.getResultVal(),
                                    scrutinyDetail);
                        } else {
                            setReportOutputDetails(plan, RULE42_5_ii, RULE_42_5_ii_DESCRIPTION,
                                    String.valueOf(HeadRoomValue), String.valueOf(minWidth), Result.Not_Accepted.getResultVal(),
                                    scrutinyDetail);
                        }
                    }
                }
            }
        }
        return plan;
    }

    /**
     * Sets the scrutiny details for the headroom validation.
     *
     * @param pl The plan object.
     * @param ruleNo The rule number.
     * @param ruleDesc The rule description.
     * @param expected The expected headroom value.
     * @param actual The actual headroom value provided.
     * @param status The validation status.
     * @param scrutinyDetail The scrutiny detail object to update.
     */
    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
            String status, ScrutinyDetail scrutinyDetail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
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
