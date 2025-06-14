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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StairCover extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(StairCover.class);

    // Rule identifier as per building code
    private static final String RULE_44_C = "44-c";

    // Description used in scrutiny reports
    public static final String STAIRCOVER_DESCRIPTION = "Mumty";
	public static final String STAIRCOVER_HEIGHT_DESC = "Verified whether stair cover height is <= ";
	public static final String MTS = " meters";

    // Autowired service to fetch MDMS rule values
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    // Placeholder validate method (not performing any logic here)
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    @Override
    public Plan process(Plan pl) {

        // Initialize scrutiny report object with appropriate columns
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Mumty"); // Key used in report output
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, VERIFIED);
        scrutinyDetail.addColumnHeading(4, ACTION);
        scrutinyDetail.addColumnHeading(5, STATUS);

        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_44_C);

        BigDecimal minHeight = BigDecimal.ZERO;        // Will hold the minimum stair cover height in a block
        BigDecimal stairCoverValue = BigDecimal.ZERO;  // Permissible stair cover height from MDMS
        
        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);

        // Feature key used to fetch related values from MDMS
        String feature = MdmsFeatureConstants.STAIR_COVER;
			
        // Prepare parameters for MDMS fetch call
        Map<String, Object> params = new HashMap<>();
       

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch the rules feature map from plan
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // Specify the required column(s) from MDMS
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE);

        List<Map<String, Object>> permissibleValue = new ArrayList<>();

        // Fetch permissible values for stair cover from MDMS
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        // Extract stair cover height limit from permissible values
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
            stairCoverValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
        }

        // Loop through all blocks of the plan
        for (Block b : pl.getBlocks()) {
            minHeight = BigDecimal.ZERO;

            // Check if the block has stair cover data
            if (b.getStairCovers() != null && !b.getStairCovers().isEmpty()) {
                
                // Find the minimum height among all stair covers in the block
                minHeight = b.getStairCovers().stream().reduce(BigDecimal::min).get();

                // Compare the stair cover height to the permissible value
                if (minHeight.compareTo(stairCoverValue) <= 0) {
                    // If height is within permissible limit, it is excluded from total building height
                    details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
                    details.put(VERIFIED, STAIRCOVER_HEIGHT_DESC + stairCoverValue.toString() + MTS);
                    details.put(ACTION, "Not included stair cover height(" + minHeight + ") to building height");
                    details.put(STATUS, Result.Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                } else {
                    // If height exceeds permissible limit, it must be included in total building height
                    details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
                    details.put(VERIFIED, STAIRCOVER_HEIGHT_DESC + stairCoverValue.toString() + MTS);
                    details.put(ACTION, "Included stair cover height(" + minHeight + ") to building height");
                    details.put(STATUS, Result.Verify.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                }
            }
        }
        return pl;
    }

    // No amendments implemented currently
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}

