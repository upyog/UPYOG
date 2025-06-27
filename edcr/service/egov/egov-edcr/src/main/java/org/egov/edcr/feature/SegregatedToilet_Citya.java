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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SegregatedToilet_Citya extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(SegregatedToilet_Citya.class);
    
    private static final String RULE_59_10  = "59-10-i";
    public static final String SEGREGATEDTOILET_DESCRIPTION = "Num. of segregated toilets";
    public static final String SEGREGATEDTOILET_DIMENSION_DESCRIPTION = "Segregated toilet distance from main entrance";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan validate(Plan pl) {
        return pl; // No specific validation logic implemented
    }

    @Override
    public Plan process(Plan pl) {

        // Scrutiny setup
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Segregated Toilet");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_59_10);

        // Variables for comparison and rules
        BigDecimal minDimension = BigDecimal.ZERO;
        BigDecimal maxHeightOfBuilding = BigDecimal.ZERO;
        BigDecimal maxNumOfFloorsOfBuilding = BigDecimal.ZERO;

        BigDecimal sTValueOne = BigDecimal.ZERO;
        BigDecimal sTValueTwo = BigDecimal.ZERO;
        BigDecimal sTValueThree = BigDecimal.ZERO;
        BigDecimal sTValueFour = BigDecimal.ZERO;
        BigDecimal sTSegregatedToiletProvided = BigDecimal.ZERO;
        BigDecimal sTSegregatedToiletRequired = BigDecimal.ZERO;
        BigDecimal sTminDimensionRequired = BigDecimal.ZERO;

        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
        String feature = MdmsFeatureConstants.SEGREGATED_TOILET;

        // Determine occupancy based on code (A = Residential)
        Map<String, Object> params = new HashMap<>();
       
        // Add params for MDMS fetch
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // List of column names to extract values from
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.ST_VALUE_ONE);
        valueFromColumn.add(EdcrRulesMdmsConstants.ST_VALUE_TWO);
        valueFromColumn.add(EdcrRulesMdmsConstants.ST_VALUE_THREE);
        valueFromColumn.add(EdcrRulesMdmsConstants.ST_VALUE_FOUR);
        valueFromColumn.add(EdcrRulesMdmsConstants.ST_SEGREGATED_TOILET_REQUIRED);
        valueFromColumn.add(EdcrRulesMdmsConstants.ST_SEGREGATED_TOILET_PROVIDED);
        valueFromColumn.add(EdcrRulesMdmsConstants.ST_MIN_DIMENSION_REQUIRED);

        // Fetch rule values from MDMS
        List<Map<String, Object>> permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        // Extract values from result
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.ST_VALUE_ONE)) {
            sTValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.ST_VALUE_ONE).toString()));
            sTValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.ST_VALUE_TWO).toString()));
            sTValueThree = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.ST_VALUE_THREE).toString()));
            sTValueFour = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.ST_VALUE_FOUR).toString()));
            sTSegregatedToiletRequired = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.ST_SEGREGATED_TOILET_REQUIRED).toString()));
            sTSegregatedToiletProvided = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.ST_SEGREGATED_TOILET_PROVIDED).toString()));
            sTminDimensionRequired = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.ST_MIN_DIMENSION_REQUIRED).toString()));
        }

        // Find minimum distance from main entrance among all toilets
        if (pl.getSegregatedToilet() != null && !pl.getSegregatedToilet().getDistancesToMainEntrance().isEmpty())
            minDimension = pl.getSegregatedToilet().getDistancesToMainEntrance().stream().reduce(BigDecimal::min).get();

        // Find the tallest building and one with the most floors
        for (Block b : pl.getBlocks()) {
            if (b.getBuilding().getBuildingHeight() != null && b.getBuilding().getBuildingHeight().compareTo(maxHeightOfBuilding) > 0) {
                maxHeightOfBuilding = b.getBuilding().getBuildingHeight();
            }
            if (b.getBuilding().getFloorsAboveGround() != null
                    && b.getBuilding().getFloorsAboveGround().compareTo(maxNumOfFloorsOfBuilding) > 0) {
                maxNumOfFloorsOfBuilding = b.getBuilding().getFloorsAboveGround();
            }
        }

        // Apply logic only if the rule is applicable based on occupancy type and limits
        if (pl.getVirtualBuilding() != null && pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null
                && pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType() != null
                && (
                    (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
                        && maxHeightOfBuilding.compareTo(sTValueOne) >= 0)
                    || ((DxfFileConstants.I.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
                        || DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
                        || DxfFileConstants.E.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode()))
                        && pl.getVirtualBuilding().getTotalBuitUpArea() != null
                        && pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(sTValueTwo) >= 0
                        && maxNumOfFloorsOfBuilding.compareTo(sTValueThree) >= 0)
                    || (DxfFileConstants.C.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())
                        && pl.getVirtualBuilding().getTotalBuitUpArea() != null
                        && pl.getVirtualBuilding().getTotalBuitUpArea().compareTo(sTValueFour) >= 0))) {

            // Check if segregated toilets are provided
            if (pl.getSegregatedToilet() != null && pl.getSegregatedToilet().getSegregatedToilets() != null
                    && !pl.getSegregatedToilet().getSegregatedToilets().isEmpty()) {
                details.put(DESCRIPTION, SEGREGATEDTOILET_DESCRIPTION);
                details.put(REQUIRED, sTSegregatedToiletRequired.toString());
                details.put(PROVIDED, String.valueOf(pl.getSegregatedToilet().getSegregatedToilets().size()));
                details.put(STATUS, Result.Accepted.getResultVal());
            } else {
                details.put(DESCRIPTION, SEGREGATEDTOILET_DESCRIPTION);
                details.put(REQUIRED, sTSegregatedToiletRequired.toString());
                details.put(PROVIDED, sTSegregatedToiletProvided.toString());
                details.put(STATUS, Result.Not_Accepted.getResultVal());
            }

            scrutinyDetail.getDetail().add(details);
            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

            // Check if minimum dimension (distance to entrance) is satisfied
            if (minDimension != null && minDimension.compareTo(sTminDimensionRequired) >= 0) {
                details.put(DESCRIPTION, SEGREGATEDTOILET_DIMENSION_DESCRIPTION);
                details.put(REQUIRED, ">= " + sTminDimensionRequired.toString());
                details.put(PROVIDED, minDimension.toString());
                details.put(STATUS, Result.Accepted.getResultVal());
            } else {
                details.put(DESCRIPTION, SEGREGATEDTOILET_DIMENSION_DESCRIPTION);
                details.put(REQUIRED, ">= " + sTminDimensionRequired.toString());
                details.put(PROVIDED, minDimension.toString());
                details.put(STATUS, Result.Not_Accepted.getResultVal());
            }

            scrutinyDetail.getDetail().add(details);
            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }

        return pl; // Return the processed plan
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>(); // No amendments
    }
}

