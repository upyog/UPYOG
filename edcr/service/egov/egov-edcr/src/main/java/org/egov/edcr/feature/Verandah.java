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
import java.util.Comparator;
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
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.VerandahRequirement;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Verandah extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Verandah.class);
	private static final String RULE_43A = "43A";
	private static final String RULE_43 = "43";

	public static final String VERANDAH_DESCRIPTION = "Verandah";

	@Override
	public Plan validate(Plan pl) {
		// Currently no validation logic required for Verandah feature
		return pl;
	}
	
	@Autowired
	MDMSCacheManager cache;


	@Override
	public Plan process(Plan pl) {
	    for (Block block : pl.getBlocks()) {
	        ScrutinyDetail scrutinyDetail = createScrutinyDetail();
	        VerandahRequirement verandahRule = getVerandahRule(pl);

	        if (verandahRule == null) {
	            continue;
	        }

	        BigDecimal permissibleWidth = verandahRule.getVerandahWidth();
	        BigDecimal permissibleDepth = verandahRule.getVerandahDepth();

	        if (block.getBuilding() == null || block.getBuilding().getFloors() == null) continue;

	        for (Floor floor : block.getBuilding().getFloors()) {
	            if (floor.getVerandah() == null || floor.getVerandah().getMeasurements() == null
	                    || floor.getVerandah().getMeasurements().isEmpty()) {
	                continue;
	            }

	            evaluateVerandahWidth(pl, scrutinyDetail, floor, permissibleWidth);
	            evaluateVerandahDepth(pl, scrutinyDetail, floor, permissibleDepth);
	        }
	    }
	    return pl;
	}

	private ScrutinyDetail createScrutinyDetail() {
	    ScrutinyDetail detail = new ScrutinyDetail();
	    detail.setKey(Common_Verandah);
	    detail.addColumnHeading(1, RULE_NO);
	    detail.addColumnHeading(2, DESCRIPTION);
	    detail.addColumnHeading(3, REQUIRED);
	    detail.addColumnHeading(4, PROVIDED);
	    detail.addColumnHeading(5, STATUS);
	    return detail;
	}

	private VerandahRequirement getVerandahRule(Plan pl) {
		 List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.VERANDAH.getValue(), false);
	        Optional<VerandahRequirement> matchedRule = rules.stream()
	            .filter(VerandahRequirement.class::isInstance)
	            .map(VerandahRequirement.class::cast)
	            .findFirst();
	    return matchedRule.orElse(null);
	}

	private void evaluateVerandahWidth(Plan pl, ScrutinyDetail scrutinyDetail, Floor floor, BigDecimal permissibleWidth) {
	    Optional<BigDecimal> minWidthOpt = floor.getVerandah().getMeasurements().stream()
	            .map(Measurement::getWidth)
	            .min(Comparator.naturalOrder());

	    if (minWidthOpt.isPresent() && minWidthOpt.get().compareTo(BigDecimal.ZERO) > 0) {
	        BigDecimal minWidth = minWidthOpt.get();
	        Map<String, String> details = new HashMap<>();
	        details.put(RULE_NO, RULE_43);
	        details.put(DESCRIPTION, VERANDAH_DESCRIPTION);
	        details.put(REQUIRED, "Minimum width " + permissibleWidth + "m");
	        details.put(PROVIDED, "Width area " + minWidth + " at floor " + floor.getNumber());
	        details.put(STATUS, minWidth.compareTo(permissibleWidth) >= 0 ?
	                Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

	        scrutinyDetail.getDetail().add(details);
	        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	    }
	}

	private void evaluateVerandahDepth(Plan pl, ScrutinyDetail scrutinyDetail, Floor floor, BigDecimal permissibleDepth) {
	    Optional<BigDecimal> minDepthOpt = floor.getVerandah().getHeightOrDepth().stream()
	            .min(Comparator.naturalOrder());

	    if (minDepthOpt.isPresent() && minDepthOpt.get().compareTo(BigDecimal.ZERO) > 0) {
	        BigDecimal minDepth = minDepthOpt.get();
	        Map<String, String> details = new HashMap<>();
	        details.put(RULE_NO, RULE_43A);
	        details.put(DESCRIPTION, VERANDAH_DESCRIPTION);
	        details.put(REQUIRED, "Minimum depth not more than " + permissibleDepth + " m");
	        details.put(PROVIDED, "Depth area " + minDepth + " at floor " + floor.getNumber());
	        details.put(STATUS, minDepth.compareTo(permissibleDepth) <= 0 ?
	                Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

	        scrutinyDetail.getDetail().add(details);
	        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	    }
	}

	@Override
	public Map<String, Date> getAmendments() {
		// No amendments for this feature as of now
		return new LinkedHashMap<>();
	}


}
