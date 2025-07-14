/*
2 * UPYOG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,


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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.CacheManagerMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlotArea extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(PlotArea.class);
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
	CacheManagerMdms cache;
//
//    @Override
//    public Plan process(Plan pl) {
//        // Check if the plot object is not null
//        if (pl.getPlot() != null) {
//            // Get the area of the plot
//            BigDecimal plotArea = pl.getPlot().getArea();
//            if (plotArea != null) {
//                // Initialize scrutiny details for the report
//                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//                scrutinyDetail.setKey(Common_Plot_Area); // Key for the scrutiny detail
//                scrutinyDetail.addColumnHeading(1, RULE_NO); // Column for rule number
//                scrutinyDetail.addColumnHeading(2, DESCRIPTION); // Column for description
//                scrutinyDetail.addColumnHeading(3, OCCUPANCY); // Column for occupancy type
//                scrutinyDetail.addColumnHeading(4, PERMITTED); // Column for permitted plot area
//                scrutinyDetail.addColumnHeading(5, PROVIDED); // Column for provided plot area
//                scrutinyDetail.addColumnHeading(6, STATUS); // Column for status (Accepted/Not Accepted)
//
//                // Initialize a map to store rule details
//                Map<String, String> details = new HashMap<>();
//                details.put(RULE_NO, RULE_34); // Rule number for plot area
//                details.put(DESCRIPTION, PLOTAREA_DESCRIPTION); // Description of the rule
//
//                // Fetch permissible plot area values based on occupancy
//                Map<String, BigDecimal> occupancyValuesMap = getOccupancyValues(pl);
//
//                // Check if the virtual building and its most restrictive FAR helper are not null
//                if (pl.getVirtualBuilding() != null && pl.getVirtualBuilding().getMostRestrictiveFarHelper() != null) {
//                    // Get the occupancy type (subtype or type)
//                    OccupancyHelperDetail occupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper()
//                            .getSubtype() != null
//                                    ? pl.getVirtualBuilding().getMostRestrictiveFarHelper().getSubtype()
//                                    : pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType();
//
//                    if (occupancyType != null) {
//                        // Add occupancy type to the details map
//                        details.put(OCCUPANCY, occupancyType.getName());
//
//                        // Fetch the permissible plot area value for the occupancy type
//                        BigDecimal occupancyValues = occupancyValuesMap.get(occupancyType.getCode());
//                        if (occupancyValues != null) {
//                            // Compare the provided plot area with the permissible value
//                            if (plotArea.compareTo(occupancyValues) >= 0) {
//                                // If the plot area is within permissible limits, mark as Accepted
//                                details.put(PERMITTED, String.valueOf(occupancyValues) + "m2");
//                                details.put(PROVIDED, plotArea.toString() + "m2");
//                                details.put(STATUS, Result.Accepted.getResultVal());
//                                scrutinyDetail.getDetail().add(details);
//                                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//                            } else {
//                                // If the plot area is not within permissible limits, mark as Not Accepted
//                                details.put(PERMITTED, String.valueOf(occupancyValues) + "m2");
//                                details.put(PROVIDED, plotArea.toString() + "m2");
//                                details.put(STATUS, Result.Not_Accepted.getResultVal());
//                                scrutinyDetail.getDetail().add(details);
//                                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return pl; // Return the updated plan object
//    }
//
//    public Map<String, BigDecimal> getOccupancyValues(Plan pl) {
//        // Initialize variables to store permissible plot area values
//        BigDecimal plotAreaValueOne = BigDecimal.ZERO;
//        BigDecimal plotAreaValueTwo = BigDecimal.ZERO;
//
//       
//        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.PLOT_AREA, false);
//    		
//            Optional<MdmsFeatureRule> matchedRule = rules.stream()
//            	    .map(obj -> (MdmsFeatureRule) obj)
//            	    .findFirst();
//
//            	if (matchedRule.isPresent()) {
//            	    MdmsFeatureRule rule = matchedRule.get();
//            	    plotAreaValueOne = rule.getPlotAreaValueOne();
//            	    plotAreaValueTwo = rule.getPlotAreaValueTwo();
//            	} 
//   
//
//        // Map the permissible plot area values to their respective occupancy codes
//        Map<String, BigDecimal> plotAreaValues = new HashMap<>();
//        plotAreaValues.put(F_RT, plotAreaValueOne);
//        plotAreaValues.put(M_NAPI, plotAreaValueOne);
//        plotAreaValues.put(F_CB, plotAreaValueOne);
//        plotAreaValues.put(S_MCH, plotAreaValueTwo);
//        plotAreaValues.put(E_PS, plotAreaValueOne);
//
//        return plotAreaValues; // Return the map of permissible plot area values
//    }
    
    @Override
    public Plan process(Plan pl) {
        if (pl.getPlot() == null || pl.getPlot().getArea() == null) {
            return pl;
        }

        BigDecimal plotArea = pl.getPlot().getArea();
        OccupancyHelperDetail occupancyType = getMostRestrictiveOccupancy(pl);
        if (occupancyType == null) {
            return pl;
        }

        BigDecimal permissibleArea = getPermissiblePlotArea(pl, occupancyType.getCode());
        if (permissibleArea == null) {
            return pl;
        }

        ScrutinyDetail scrutinyDetail = buildScrutinyDetail();
        Map<String, String> details = buildScrutinyDetailRow(occupancyType, plotArea, permissibleArea);

        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

        return pl;
    }

    private ScrutinyDetail buildScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(Common_Plot_Area);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, OCCUPANCY);
        scrutinyDetail.addColumnHeading(4, PERMITTED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        return scrutinyDetail;
    }

    private Map<String, String> buildScrutinyDetailRow(OccupancyHelperDetail occupancyType,
                                                       BigDecimal plotArea,
                                                       BigDecimal permissibleArea) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_34);
        details.put(DESCRIPTION, PLOTAREA_DESCRIPTION);
        details.put(OCCUPANCY, occupancyType.getName());
        details.put(PERMITTED, permissibleArea + "m2");
        details.put(PROVIDED, plotArea + "m2");

        String status = plotArea.compareTo(permissibleArea) >= 0
                ? Result.Accepted.getResultVal()
                : Result.Not_Accepted.getResultVal();

        details.put(STATUS, status);
        return details;
    }

    private BigDecimal getPermissiblePlotArea(Plan pl, String occupancyCode) {
        Map<String, BigDecimal> occupancyValuesMap = getOccupancyValues(pl);
        return occupancyValuesMap.get(occupancyCode);
    }
    
    public Map<String, BigDecimal> getOccupancyValues(Plan pl) {
        BigDecimal plotAreaValueOne = BigDecimal.ZERO;
        BigDecimal plotAreaValueTwo = BigDecimal.ZERO;

        Optional<MdmsFeatureRule> matchedRule = getFeatureRule(pl, MdmsFeatureConstants.PLOT_AREA);
        if (matchedRule.isPresent()) {
            MdmsFeatureRule rule = matchedRule.get();
            plotAreaValueOne = rule.getPlotAreaValueOne();
            plotAreaValueTwo = rule.getPlotAreaValueTwo();
        }

        Map<String, BigDecimal> plotAreaValues = new HashMap<>();
        plotAreaValues.put(F_RT, plotAreaValueOne);
        plotAreaValues.put(M_NAPI, plotAreaValueOne);
        plotAreaValues.put(F_CB, plotAreaValueOne);
        plotAreaValues.put(S_MCH, plotAreaValueTwo);
        plotAreaValues.put(E_PS, plotAreaValueOne);

        return plotAreaValues;
    }
    
    private Optional<MdmsFeatureRule> getFeatureRule(Plan pl, String featureName) {
        List<Object> rules = cache.getFeatureRules(pl, featureName, false);
        return rules.stream()
                    .map(obj -> (MdmsFeatureRule) obj)
                    .findFirst();
    }

    private OccupancyHelperDetail getMostRestrictiveOccupancy(Plan pl) {
        if (pl.getVirtualBuilding() == null || pl.getVirtualBuilding().getMostRestrictiveFarHelper() == null)
            return null;

        OccupancyTypeHelper farHelper = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
        return farHelper.getSubtype() != null ? farHelper.getSubtype() : farHelper.getType();
    }



}