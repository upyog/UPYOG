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
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.River;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RiverDistance_Citya extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(RiverDistance_Citya.class);
    private static final String RULE_22 = "22";
    public static final String MAIN_RIVER_DESCRIPTION = "Distance from main river";
    public static final String SUB_RIVER_DESCRIPTION = "Distance from sub river";
    public static final String MAIN_RIVER_PROTECTION_WALL_DESCRIPTION = "Distance from main river protection wall";
    public static final String MAIN_RIVER_EMBANKMENT_DESCRIPTION = "Distance from main river embankment";
    public static final String NO_DISTANCT_MENTIONED = "No distance is provided from protection wall embankment/river main edge or sub river";
    private static final Integer MAIN_RIVER = 1;
    private static final Integer SUB_RIVER = 2;

    @Override
    public Plan validate(Plan pl) {

        return pl;
    }
    
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan process(Plan pl) {

        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_River Distance");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, PERMITTED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        HashMap<String, String> errors = new HashMap<>();
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_22);

        BigDecimal minDistanceFromProtectionWall = BigDecimal.ZERO;
        BigDecimal minDistanceFromEmbankment = BigDecimal.ZERO;
        BigDecimal minDistanceFromMainRiverEdge = BigDecimal.ZERO;
        BigDecimal minDistanceFromSubRiver = BigDecimal.ZERO;
        
        BigDecimal rDminDistanceFromProtectionWall = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromEmbankment = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromMainRiverEdge = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromSubRiver = BigDecimal.ZERO;

        List<River> mainRiver = new ArrayList<>();
        List<River> subRiver = new ArrayList<>();
        List<River> rivers = pl.getDistanceToExternalEntity().getRivers();
        
        String occupancyName = null;
		
		 String feature = MdmsFeatureConstants.RIVER_DISTANCE;
			
			Map<String, Object> params = new HashMap<>();
			if(DxfFileConstants.A
					.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())){
				occupancyName = "Residential";
			}

			params.put("feature", feature);
			params.put("occupancy", occupancyName);
			

			Map<String,List<Map<String,Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
			
			ArrayList<String> valueFromColumn = new ArrayList<>();
			valueFromColumn.add(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_PROTECTION_WALL);
			valueFromColumn.add(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_EMBANKMENT);
			valueFromColumn.add(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_MAIN_RIVER_EDGE);
			valueFromColumn.add(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_SUB_RIVER);
			
			List<Map<String, Object>> permissibleValue = new ArrayList<>();
		
			
				permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
				LOG.info("permissibleValue" + permissibleValue);


			if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_PROTECTION_WALL)) {
				rDminDistanceFromProtectionWall = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_PROTECTION_WALL).toString()));
				rDminDistanceFromEmbankment = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_EMBANKMENT).toString()));
				rDminDistanceFromMainRiverEdge = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_MAIN_RIVER_EDGE).toString()));
				rDminDistanceFromSubRiver = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_SUB_RIVER).toString()));
				}

        if (!rivers.isEmpty()) {
            mainRiver = rivers.stream().filter(river -> river.getColorCode().equals(MAIN_RIVER))
                    .collect(Collectors.toList());
            subRiver = rivers.stream().filter(river -> river.getColorCode().equals(SUB_RIVER))
                    .collect(Collectors.toList());

        }
        List<BigDecimal> distancesFromRiverProtectionWall = !mainRiver.isEmpty()
                ? mainRiver.get(0).getDistancesFromProtectionWall()
                : new ArrayList<>();
        List<BigDecimal> distancesFromEmbankment = !mainRiver.isEmpty() ? mainRiver.get(0).getDistancesFromEmbankment()
                : new ArrayList<>();
        List<BigDecimal> distancesFromMainRiverEdge = !mainRiver.isEmpty()
                ? mainRiver.get(0).getDistancesFromRiverEdge()
                : new ArrayList<>();
        List<BigDecimal> distancesFromSubRiver = !subRiver.isEmpty() ? subRiver.get(0).getDistancesFromProtectionWall()
                : new ArrayList<>();

        if (StringUtils.isNotBlank(pl.getPlanInformation().getBuildingNearToRiver())
                && "YES".equalsIgnoreCase(pl.getPlanInformation().getBuildingNearToRiver())) {
            if (distancesFromRiverProtectionWall != null && !distancesFromRiverProtectionWall.isEmpty()) {

                minDistanceFromProtectionWall = distancesFromRiverProtectionWall.stream().reduce(BigDecimal::min).get();

                if (minDistanceFromProtectionWall.compareTo(rDminDistanceFromProtectionWall) > 0) {
                    details.put(DESCRIPTION, MAIN_RIVER_PROTECTION_WALL_DESCRIPTION);
                    details.put(PERMITTED, ">"+ rDminDistanceFromProtectionWall.toString());
                    details.put(PROVIDED, minDistanceFromProtectionWall.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                } else {
                    details.put(DESCRIPTION, MAIN_RIVER_PROTECTION_WALL_DESCRIPTION);
                    details.put(PERMITTED, "<="+rDminDistanceFromProtectionWall.toString());
                    details.put(PROVIDED, minDistanceFromProtectionWall.toString());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

                }
            } else if (distancesFromRiverProtectionWall != null && !distancesFromEmbankment.isEmpty()) {
                details = new HashMap<>();
                details.put(RULE_NO, RULE_22);

                minDistanceFromEmbankment = distancesFromEmbankment.stream().reduce(BigDecimal::min).get();

                if (minDistanceFromEmbankment.compareTo(rDminDistanceFromEmbankment) > 0) {
                    details.put(DESCRIPTION, MAIN_RIVER_EMBANKMENT_DESCRIPTION);
                    details.put(PERMITTED, ">"+ rDminDistanceFromEmbankment.toString());
                    details.put(PROVIDED, minDistanceFromEmbankment.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                } else {
                    details.put(DESCRIPTION, MAIN_RIVER_EMBANKMENT_DESCRIPTION);
                    details.put(PERMITTED, "<=" + rDminDistanceFromEmbankment.toString());
                    details.put(PROVIDED, minDistanceFromEmbankment.toString());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

                }
            } else if (distancesFromMainRiverEdge != null && !distancesFromMainRiverEdge.isEmpty()) {
                details = new HashMap<>();
                details.put(RULE_NO, RULE_22);

                minDistanceFromMainRiverEdge = distancesFromMainRiverEdge.stream().reduce(BigDecimal::min).get();

                if (minDistanceFromMainRiverEdge.compareTo(rDminDistanceFromMainRiverEdge) > 0) {
                    details.put(DESCRIPTION, MAIN_RIVER_DESCRIPTION);
                    details.put(PERMITTED, ">" + rDminDistanceFromMainRiverEdge.toString());
                    details.put(PROVIDED, minDistanceFromMainRiverEdge.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                } else {
                    details.put(DESCRIPTION, MAIN_RIVER_DESCRIPTION);
                    details.put(PERMITTED, "<=" + rDminDistanceFromMainRiverEdge.toString());
                    details.put(PROVIDED, minDistanceFromMainRiverEdge.toString());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

                }
            }

            if (distancesFromSubRiver != null && !distancesFromSubRiver.isEmpty()) {

                minDistanceFromSubRiver = distancesFromSubRiver.stream().reduce(BigDecimal::min).get();
                details = new HashMap<>();
                details.put(RULE_NO, RULE_22);

                if (minDistanceFromSubRiver.compareTo(rDminDistanceFromSubRiver) > 0) {
                    details.put(DESCRIPTION, SUB_RIVER_DESCRIPTION);
                    details.put(PERMITTED, ">" + rDminDistanceFromSubRiver.toString());
                    details.put(PROVIDED, minDistanceFromSubRiver.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                } else {
                    details.put(DESCRIPTION, SUB_RIVER_DESCRIPTION);
                    details.put(PERMITTED, "<=" + rDminDistanceFromSubRiver.toString());
                    details.put(PROVIDED, minDistanceFromSubRiver.toString());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

                }
            }

			/*
			 * if (distancesFromRiverProtectionWall.isEmpty() &&
			 * distancesFromEmbankment.isEmpty() && distancesFromMainRiverEdge.isEmpty() &&
			 * distancesFromSubRiver.isEmpty()) { errors.put("Distance_From_River",
			 * NO_DISTANCT_MENTIONED);
			 * 
			 * pl.addErrors(errors); }
			 */
        }
        return pl;
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}
