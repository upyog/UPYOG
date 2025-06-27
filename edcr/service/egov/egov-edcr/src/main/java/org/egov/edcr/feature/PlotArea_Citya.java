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

import static org.egov.edcr.constants.DxfFileConstants.E_PS;
import static org.egov.edcr.constants.DxfFileConstants.F_CB;
import static org.egov.edcr.constants.DxfFileConstants.F_RT;
import static org.egov.edcr.constants.DxfFileConstants.M_NAPI;
import static org.egov.edcr.constants.DxfFileConstants.S_MCH;

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
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlotArea_Citya extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(PlotArea_Citya.class);
    private static final String RULE_34 = "34-1";
    public static final String PLOTAREA_DESCRIPTION = "Minimum Plot Area";

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan process(Plan pl) {
        // Check if the plot object is not null
        if (pl.getPlot() != null) {
            // Get the area of the plot
            BigDecimal plotArea = pl.getPlot().getArea();
            if (plotArea != null) {
                // Initialize scrutiny details for the report
                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.setKey("Common_Plot Area"); // Key for the scrutiny detail
                scrutinyDetail.addColumnHeading(1, RULE_NO); // Column for rule number
                scrutinyDetail.addColumnHeading(2, DESCRIPTION); // Column for description
                scrutinyDetail.addColumnHeading(3, OCCUPANCY); // Column for occupancy type
                scrutinyDetail.addColumnHeading(4, PERMITTED); // Column for permitted plot area
                scrutinyDetail.addColumnHeading(5, PROVIDED); // Column for provided plot area
                scrutinyDetail.addColumnHeading(6, STATUS); // Column for status (Accepted/Not Accepted)

                // Initialize a map to store rule details
                Map<String, String> details = new HashMap<>();
                details.put(RULE_NO, RULE_34); // Rule number for plot area
                details.put(DESCRIPTION, PLOTAREA_DESCRIPTION); // Description of the rule

                // Fetch permissible plot area values based on occupancy
                Map<String, BigDecimal> occupancyValuesMap = getOccupancyValues(pl);

                // Check if the virtual building and its most restrictive FAR helper are not null
                if (pl.getVirtualBuilding() != null && pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null) {
                    // Get the occupancy type (subtype or type)
                    OccupancyHelperDetail occupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper()
                            .getSubtype() != null
                                    ? pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype()
                                    : pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType();

                    if (occupancyType != null) {
                        // Add occupancy type to the details map
                        details.put(OCCUPANCY, occupancyType.getName());

                        // Fetch the permissible plot area value for the occupancy type
                        BigDecimal occupancyValues = occupancyValuesMap.get(occupancyType.getCode());
                        if (occupancyValues != null) {
                            // Compare the provided plot area with the permissible value
                            if (plotArea.compareTo(occupancyValues) >= 0) {
                                // If the plot area is within permissible limits, mark as Accepted
                                details.put(PERMITTED, String.valueOf(occupancyValues) + "m2");
                                details.put(PROVIDED, plotArea.toString() + "m2");
                                details.put(STATUS, Result.Accepted.getResultVal());
                                scrutinyDetail.getDetail().add(details);
                                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                            } else {
                                // If the plot area is not within permissible limits, mark as Not Accepted
                                details.put(PERMITTED, String.valueOf(occupancyValues) + "m2");
                                details.put(PROVIDED, plotArea.toString() + "m2");
                                details.put(STATUS, Result.Not_Accepted.getResultVal());
                                scrutinyDetail.getDetail().add(details);
                                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                            }
                        }
                    }
                }
            }
        }
        return pl; // Return the updated plan object
    }

    public Map<String, BigDecimal> getOccupancyValues(Plan pl) {
        // Initialize variables to store permissible plot area values
        BigDecimal plotAreaValueOne = BigDecimal.ZERO;
        BigDecimal plotAreaValueTwo = BigDecimal.ZERO;

        // Determine the occupancy type
        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
        String feature = MdmsFeatureConstants.PLOT_AREA; // Feature name for plot area

        // Prepare parameters for fetching MDMS values
        Map<String, Object> params = new HashMap<>();
       
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch the list of rules from the plan object
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // Specify the columns to fetch from the rules
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PLOT_AREA_VALUE_ONE); // First permissible plot area value
        valueFromColumn.add(EdcrRulesMdmsConstants.PLOT_AREA_VALUE_TWO); // Second permissible plot area value

        // Initialize a list to store permissible values
        List<Map<String, Object>> permissibleValue = new ArrayList<>();

        // Fetch permissible values from MDMS
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue); // Log the fetched permissible values

        // Check if permissible values are available and update the plot area values
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PLOT_AREA_VALUE_ONE)) {
            plotAreaValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PLOT_AREA_VALUE_ONE).toString()));
            plotAreaValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PLOT_AREA_VALUE_TWO).toString()));
        }

        // Map the permissible plot area values to their respective occupancy codes
        Map<String, BigDecimal> plotAreaValues = new HashMap<>();
        plotAreaValues.put(F_RT, plotAreaValueOne);
        plotAreaValues.put(M_NAPI, plotAreaValueOne);
        plotAreaValues.put(F_CB, plotAreaValueOne);
        plotAreaValues.put(S_MCH, plotAreaValueTwo);
        plotAreaValues.put(E_PS, plotAreaValueOne);

        return plotAreaValues; // Return the map of permissible plot area values
    }
}