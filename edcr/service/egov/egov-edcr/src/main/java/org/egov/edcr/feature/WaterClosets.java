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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.CacheManagerMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WaterClosets extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(WaterClosets.class);
	private static final String RULE_41_IV = "41-iv";
	public static final String WATERCLOSETS_DESCRIPTION = "Water Closets";

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}
	
	@Autowired
	CacheManagerMdms cache;

	@Override
	public Plan process(Plan pl) {
	    ScrutinyDetail dimScrutinyDetail = createScrutinyDetail("Common_Water Closets");
	    ScrutinyDetail ventScrutinyDetail = createScrutinyDetail("Water Closets Ventilation");

	    MdmsFeatureRule wcRule = getWaterClosetsRule(pl);

	    if (wcRule == null) return pl;

	    for (Block block : pl.getBlocks()) {
	        if (block.getBuilding() == null || block.getBuilding().getFloors() == null) continue;

	        for (Floor floor : block.getBuilding().getFloors()) {
	            if (!hasValidWaterClosets(floor)) continue;

	            BigDecimal minHeight = getMinHeight(floor.getWaterClosets().getHeights());
	            BigDecimal minWidth = getMinWidth(floor.getWaterClosets().getRooms());
	            BigDecimal totalArea = getTotalArea(floor.getWaterClosets().getRooms());

	            // Ventilation validation
	            if (floor.getWaterClosetVentilation() != null
	                    && floor.getWaterClosetVentilation().getMeasurements() != null
	                    && !floor.getWaterClosetVentilation().getMeasurements().isEmpty()) {

	                BigDecimal ventArea = floor.getWaterClosetVentilation().getMeasurements().stream()
	                        .map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
	                Map<String, String> ventDetails = buildVentilationDetails(
	                	    wcRule.getWaterClosetsVentilationArea(), ventArea
	                	);
	                	ventScrutinyDetail.getDetail().add(ventDetails);

	            }

	            Map<String, String> dimDetails = buildDimensionDetails(
	            	    wcRule.getWaterClosetsHeight(), wcRule.getWaterClosetsArea(), wcRule.getWaterClosetsWidth(),
	            	    minHeight, totalArea, minWidth
	            	);
	            	dimScrutinyDetail.getDetail().add(dimDetails);

	        }
	    }

	    pl.getReportOutput().getScrutinyDetails().add(dimScrutinyDetail);
	    pl.getReportOutput().getScrutinyDetails().add(ventScrutinyDetail);
	    return pl;
	}
	
	private ScrutinyDetail createScrutinyDetail(String key) {
	    ScrutinyDetail detail = new ScrutinyDetail();
	    detail.setKey(key);
	    detail.addColumnHeading(1, RULE_NO);
	    detail.addColumnHeading(2, DESCRIPTION);
	    detail.addColumnHeading(3, REQUIRED);
	    detail.addColumnHeading(4, PROVIDED);
	    detail.addColumnHeading(5, STATUS);
	    return detail;
	}

	private MdmsFeatureRule getWaterClosetsRule(Plan pl) {
	    List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.WATER_CLOSETS, false);
	    return rules.stream()
	            .map(obj -> (MdmsFeatureRule) obj)
	            .findFirst()
	            .orElse(null);
	}

	private boolean hasValidWaterClosets(Floor floor) {
	    return floor.getWaterClosets() != null
	            && floor.getWaterClosets().getHeights() != null
	            && !floor.getWaterClosets().getHeights().isEmpty()
	            && floor.getWaterClosets().getRooms() != null
	            && !floor.getWaterClosets().getRooms().isEmpty();
	}

	private BigDecimal getMinHeight(List<RoomHeight> heights) {
	    return heights.stream()
	            .map(RoomHeight::getHeight)
	            .min(Comparator.naturalOrder())
	            .orElse(BigDecimal.ZERO);
	}

	private BigDecimal getMinWidth(List<Measurement> rooms) {
	    return rooms.stream()
	            .map(Measurement::getWidth)
	            .min(Comparator.naturalOrder())
	            .orElse(BigDecimal.ZERO);
	}

	private BigDecimal getTotalArea(List<Measurement> rooms) {
	    return rooms.stream()
	            .map(Measurement::getArea)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private Map<String, String> buildVentilationDetails(BigDecimal requiredVentArea, BigDecimal providedVentArea) {
	    Map<String, String> ventDetails = new HashMap<>();
	    ventDetails.put(RULE_NO, RULE_41_IV);
	    ventDetails.put(DESCRIPTION, WATERCLOSETS_DESCRIPTION);
	    ventDetails.put(REQUIRED, requiredVentArea.toString());
	    ventDetails.put(PROVIDED, "Water Closet Ventilation area " + providedVentArea);
	    ventDetails.put(STATUS, providedVentArea.compareTo(requiredVentArea) >= 0
	            ? Result.Accepted.getResultVal()
	            : Result.Not_Accepted.getResultVal());
	    return ventDetails;
	}

	private Map<String, String> buildDimensionDetails(BigDecimal requiredHeight, BigDecimal requiredArea, BigDecimal requiredWidth,
	                                                  BigDecimal providedHeight, BigDecimal providedArea, BigDecimal providedWidth) {
	    Map<String, String> dimDetails = new HashMap<>();
	    dimDetails.put(RULE_NO, RULE_41_IV);
	    dimDetails.put(DESCRIPTION, WATERCLOSETS_DESCRIPTION);
	    dimDetails.put(REQUIRED, String.format("Height ≥ %s, Area ≥ %s, Width ≥ %s",
	            requiredHeight, requiredArea, requiredWidth));
	    dimDetails.put(PROVIDED, String.format("Height = %s, Area = %s, Width = %s",
	            providedHeight, providedArea, providedWidth));
	    dimDetails.put(STATUS, (providedHeight.compareTo(requiredHeight) >= 0
	            && providedArea.compareTo(requiredArea) >= 0
	            && providedWidth.compareTo(requiredWidth) >= 0)
	            ? Result.Accepted.getResultVal()
	            : Result.Not_Accepted.getResultVal());
	    return dimDetails;
	}



	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
