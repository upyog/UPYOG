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
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Parapet extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Parapet.class);
	private static final String RULE_41_V = "41-v";
	public static final String PARAPET_DESCRIPTION = "Parapet";
	public static final String HEIGHT = "Height >= ";
	public static final String AND_HEIGHT = " and height <= ";

	@Override
	public Plan validate(Plan pl) {

		return pl;
	}
	
	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;
	
	  @Autowired
	  CacheManagerMdms cache;

@Override
public Plan process(Plan pl) {

    // Initialize scrutiny details for the report
    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
    scrutinyDetail.setKey("Common_Parapet"); // Key for the scrutiny detail
    scrutinyDetail.addColumnHeading(1, RULE_NO); // Column for rule number
    scrutinyDetail.addColumnHeading(2, DESCRIPTION); // Column for description
    scrutinyDetail.addColumnHeading(3, REQUIRED); // Column for required values
    scrutinyDetail.addColumnHeading(4, PROVIDED); // Column for provided values
    scrutinyDetail.addColumnHeading(5, STATUS); // Column for status (Accepted/Not Accepted)

    // Initialize a map to store rule details
    Map<String, String> details = new HashMap<>();
    details.put(RULE_NO, RULE_41_V); // Rule number for parapet
    details.put(DESCRIPTION, PARAPET_DESCRIPTION); // Description of the rule

    // Initialize variables to store minimum height and permissible parapet values
    BigDecimal minHeight = BigDecimal.ZERO;
    BigDecimal parapetValueOne = BigDecimal.ZERO;
    BigDecimal parapetValueTwo = BigDecimal.ZERO;

    // Determine the occupancy type
   
     String feature = MdmsFeatureConstants.PARAPET; // Feature name for parapet
  
	 String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl).toLowerCase();
	 String tenantId = pl.getTenantId();
	 String zone = pl.getPlanInformation().getZone().toLowerCase();
	 String subZone = pl.getPlanInformation().getSubZone().toLowerCase();
	 String riskType = fetchEdcrRulesMdms.getRiskType(pl).toLowerCase();
	 
	 RuleKey key = new RuleKey(EdcrRulesMdmsConstants.STATE, tenantId, zone, subZone, occupancyName, null, feature);
	 List<Object> rules = cache.getRules(tenantId, key);
		
	 Optional<MdmsFeatureRule> matchedRule = rules.stream()
	 	    .map(obj -> (MdmsFeatureRule) obj)
	 	    .findFirst();
	
	 	if (matchedRule.isPresent()) {
	 	    MdmsFeatureRule rule = matchedRule.get();
	 	   parapetValueOne = rule.getParapetValueOne();
	 	  parapetValueTwo = rule.getParapetValueTwo();
	 	} 

    // Iterate through all blocks in the plan
    for (Block b : pl.getBlocks()) {
        // Check if the block has parapets
        if (b.getParapets() != null && !b.getParapets().isEmpty()) {
            // Find the minimum height among the parapets
            minHeight = b.getParapets().stream().reduce(BigDecimal::min).get();

            // Validate the minimum height against the permissible values
            if (minHeight.compareTo(parapetValueOne) >= 0 && minHeight.compareTo(parapetValueTwo) <= 0) {
                // If the height is within the permissible range, mark as Accepted
                details.put(REQUIRED, HEIGHT + parapetValueOne.toString() + AND_HEIGHT + parapetValueTwo.toString());
                details.put(PROVIDED, HEIGHT + minHeight + AND_HEIGHT + minHeight);
                details.put(STATUS, Result.Accepted.getResultVal());
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            } else {
                // If the height is outside the permissible range, mark as Not Accepted
                details.put(REQUIRED, HEIGHT + parapetValueOne.toString() + AND_HEIGHT + parapetValueTwo.toString());
                details.put(PROVIDED, HEIGHT + minHeight + AND_HEIGHT + minHeight);
                details.put(STATUS, Result.Not_Accepted.getResultVal());
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }
        }
    }

    return pl; // Return the updated plan object
}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
