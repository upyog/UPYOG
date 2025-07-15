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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Chimney extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(Chimney.class);

    // Rule identifier and description for chimney scrutiny
    private static final String RULE_44_D = "44-d";
    public static final String CHIMNEY_DESCRIPTION = "Chimney";
    public static final String CHIMNEY_VERIFY_DESCRIPTION = "Verified whether chimney height is <= ";
    public static final String METERS = " meters";
    public static final String TO_BUILDING_HEIGHT = ") to building height";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	CacheManagerMdms cache;

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

	/**
	 * Processes the given plan to validate chimney height. Checks whether the
	 * chimney height is within permissible limits and updates the scrutiny details.
	 *
	 * @param pl The plan object to process.
	 * @return The processed plan object with scrutiny details added.
	 */
	
	 @Override
	    public Plan process(Plan pl) {
	        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.CHIMNEY, false);
	        Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
	        BigDecimal permissibleHeight = matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);

	        for (Block block : pl.getBlocks()) {
	            processBlock(pl, block, permissibleHeight);
	        }

	        return pl;
	    }

	    private void processBlock(Plan pl, Block block, BigDecimal permissibleHeight) {
	        if (block.getChimneys() == null || block.getChimneys().isEmpty())
	            return;

	        // Chimneys are not per floor, so no processFloor here
	        validate(pl, block, permissibleHeight);
	    }

	    private void validate(Plan pl, Block block, BigDecimal permissibleHeight) {
	        BigDecimal minChimneyHeight = block.getChimneys().stream()
	                .reduce(BigDecimal::min)
	                .orElse(BigDecimal.ZERO);

	        ScrutinyDetail scrutinyDetail = createScrutinyDetail();

	        Map<String, String> resultRow;
	        if (minChimneyHeight.compareTo(permissibleHeight) <= 0) {
	            resultRow = createResultRow(Result.Accepted.getResultVal(), permissibleHeight, minChimneyHeight, false);
	        } else {
	            resultRow = createResultRow(Result.Verify.getResultVal(), permissibleHeight, minChimneyHeight, true);
	        }

	        scrutinyDetail.getDetail().add(resultRow);
	        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	    }

	    private ScrutinyDetail createScrutinyDetail() {
	        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	        scrutinyDetail.setKey(Common_Chimney);
	        scrutinyDetail.addColumnHeading(1, RULE_NO);
	        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	        scrutinyDetail.addColumnHeading(3, VERIFIED);
	        scrutinyDetail.addColumnHeading(4, ACTION);
	        scrutinyDetail.addColumnHeading(5, STATUS);
	        return scrutinyDetail;
	    }

	    private Map<String, String> createResultRow(String status, BigDecimal permissibleHeight, BigDecimal actualHeight, boolean includedInBuildingHeight) {
	        Map<String, String> details = new HashMap<>();
	        details.put(RULE_NO, RULE_NO);
	        details.put(DESCRIPTION, CHIMNEY_DESCRIPTION);
	        details.put(VERIFIED, CHIMNEY_VERIFY_DESCRIPTION + permissibleHeight + METERS);
	        details.put(ACTION, (includedInBuildingHeight ? "Included" : "Not included") +
	                " chimney height (" + actualHeight + ") to building height");
	        details.put(STATUS, status);
	        return details;
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
