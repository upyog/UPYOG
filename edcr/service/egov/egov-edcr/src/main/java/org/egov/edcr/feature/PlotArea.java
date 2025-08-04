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

import static org.egov.edcr.constants.CommonFeatureConstants.SQUARE_METER;
import static org.egov.edcr.constants.DxfFileConstants.E_PS;
import static org.egov.edcr.constants.DxfFileConstants.F_CB;
import static org.egov.edcr.constants.DxfFileConstants.F_RT;
import static org.egov.edcr.constants.DxfFileConstants.M_NAPI;
import static org.egov.edcr.constants.DxfFileConstants.S_MCH;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

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
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlotArea extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(PlotArea.class);

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

   
    @Autowired
	MDMSCacheManager cache;

    /**
     * Processes the Plan to validate the plot area against permissible values based on occupancy.
     *
     * @param pl the Plan object containing plot and occupancy information
     * @return the updated Plan object with scrutiny details added
     */
    
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
    /**
     * Builds the base ScrutinyDetail object for capturing plot area validation results.
     *
     * @return the ScrutinyDetail object with headings initialized
     */

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

    /**
     * Builds a row of scrutiny detail containing rule info, occupancy, permissible and provided plot areas, and status.
     *
     * @param occupancyType the occupancy type being validated
     * @param plotArea the provided plot area
     * @param permissibleArea the permissible plot area from rules
     * @return a Map representing one row in the scrutiny detail
     */
    private Map<String, String> buildScrutinyDetailRow(OccupancyHelperDetail occupancyType,
                                                       BigDecimal plotArea,
                                                       BigDecimal permissibleArea) {

        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_34);
        detail.setDescription(PLOTAREA_DESCRIPTION);
        detail.setOccupancy(occupancyType.getName());
        detail.setPermitted(permissibleArea + SQUARE_METER);
        detail.setProvided(plotArea + SQUARE_METER);
        detail.setStatus(plotArea.compareTo(permissibleArea) >= 0
                ? Result.Accepted.getResultVal()
                : Result.Not_Accepted.getResultVal());

        return mapReportDetails(detail);
        }

    /**
     * Retrieves the permissible plot area based on the given occupancy code.
     *
     * @param pl the Plan object
     * @param occupancyCode the occupancy type code
     * @return the permissible plot area for the given occupancy type, or null if not found
     */
    private BigDecimal getPermissiblePlotArea(Plan pl, String occupancyCode) {
        Map<String, BigDecimal> occupancyValuesMap = getOccupancyValues(pl);
        return occupancyValuesMap.get(occupancyCode);
    }
    
    /**
     * Constructs a mapping of occupancy codes to their corresponding permissible plot areas from the feature rule.
     *
     * @param pl the Plan object
     * @return a Map of occupancy codes to permissible plot areas
     */
    public Map<String, BigDecimal> getOccupancyValues(Plan pl) {
        BigDecimal plotAreaValueOne = BigDecimal.ZERO;
        BigDecimal plotAreaValueTwo = BigDecimal.ZERO;

        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.PLOT_AREA.getValue(), false);
        Optional<PlotAreaRequirement> matchedRule = rules.stream()
            .filter(PlotAreaRequirement.class::isInstance)
            .map(PlotAreaRequirement.class::cast)
            .findFirst();
        if (matchedRule.isPresent()) {
        	PlotAreaRequirement rule = matchedRule.get();
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
    
    /**
     * Retrieves the feature rule from MDMS for the given feature name.
     *
     * @param pl the Plan object
     * @param featureName the name of the feature to fetch the rule for
     * @return an Optional containing the MdmsFeatureRule if found
     */
    private Optional<MdmsFeatureRule> getFeatureRule(Plan pl, String featureName) {
        List<Object> rules = cache.getFeatureRules(pl, featureName, false);
        return rules.stream()
                    .map(obj -> (MdmsFeatureRule) obj)
                    .findFirst();
    }

    /**
     * Retrieves the most restrictive occupancy type used in the FAR calculation.
     *
     * @param pl the Plan object
     * @return the most restrictive OccupancyHelperDetail, or null if not found
     */
    private OccupancyHelperDetail getMostRestrictiveOccupancy(Plan pl) {
        if (pl.getVirtualBuilding() == null || pl.getVirtualBuilding().getMostRestrictiveFarHelper() == null)
            return null;

        OccupancyTypeHelper farHelper = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
        return farHelper.getSubtype() != null ? farHelper.getSubtype() : farHelper.getType();
    }



}