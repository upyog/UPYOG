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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.BLOCK;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class PorticoService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(PorticoService.class);

    @Autowired
	MDMSCacheManager cache;

    /**
     * Validates the Portico elements in each block of the provided plan.
     * Checks if the distance to the exterior wall is defined for each portico.
     * If not defined, it adds an appropriate error message to the plan.
     *
     * @param plan The plan object containing building blocks and portico details.
     * @return The same plan object with errors populated if validation fails.
     */
    @Override
    public Plan validate(Plan plan) {
        HashMap<String, String> errors = new HashMap<>();

        for (Block block : plan.getBlocks()) {
            for (Portico portico : block.getPorticos()) {
                if (portico.getDistanceToExteriorWall().isEmpty()) {
                    errors.put(String.format(PORTICO_DISTANCETO_EXTERIORWALL, block.getNumber(), portico.getName()),
                            edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                    new String[] { String.format(PORTICO_DISTANCETO_EXTERIORWALL, block.getNumber(), portico.getName()) },
                                    LocaleContextHolder.getLocale()));
                    plan.addErrors(errors);
                }
            }
        }
        return plan;
    }

    /**
     * Processes the portico data within the provided plan.
     * Performs validation and evaluates each portico against permissible length rules.
     * Adds the result to the report output.
     *
     * @param plan The plan object containing building block and portico data.
     * @return The processed plan with scrutiny details populated.
     */
    @Override
    public Plan process(Plan plan) {
        validate(plan); 

        Optional<PorticoServiceRequirement> matchedRule = fetchPermissiblePorticoLength(plan);
        if (matchedRule.isPresent()) {
        	PorticoServiceRequirement rule = matchedRule.get();
        	BigDecimal permissibleValue = rule.getPermissible();

        for (Block block : plan.getBlocks()) {
            processBlockPorticos(plan, block, permissibleValue);
        }}

        return plan;
    }
    
    /**
     * Fetches the permissible maximum length value for a portico from the feature rules cache.
     *
     * @param plan The plan object used to retrieve feature-specific rules.
     * @return A BigDecimal representing the permissible portico length. Defaults to zero if no rule is found.
     */
    private Optional<PorticoServiceRequirement> fetchPermissiblePorticoLength(Plan plan) {
    	List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.PORTICO_SERVICE.getValue(), false);
       return rules.stream()
            .filter(PorticoServiceRequirement.class::isInstance)
            .map(PorticoServiceRequirement.class::cast)
            .findFirst();
    }

    /**
     * Iterates over all porticos in a block and compares each portico's length to the permissible value.
     * Populates scrutiny details based on the compliance status.
     *
     * @param plan             The plan object to update with scrutiny output.
     * @param block            The block containing porticos to be validated.
     * @param permissibleValue The permissible length value for porticos.
     */
	private void processBlockPorticos(Plan plan, Block block, BigDecimal permissibleValue) {
		for (Portico portico : block.getPorticos()) {
			ScrutinyDetail scrutinyDetail = createScrutinyDetail(block.getNumber());

			if (portico.getLength() != null) {
				String status = portico.getLength().compareTo(permissibleValue) >= 0 ? Result.Accepted.getResultVal()
						: Result.Not_Accepted.getResultVal();

				setReportOutputDetails(plan, SUBRULE_PORTICO,
						String.format(SUBRULE_PORTICO_MAX_LENGTHDESCRIPTION, portico.getName()),
						MAX_PREFIX + permissibleValue + MTR_SUFFIX, portico.getLength() + MTR_SUFFIX, status, scrutinyDetail);
			}
		}
	}

	/**
	 * Creates and initializes a ScrutinyDetail object for a specific block and sets column headings.
	 *
	 * @param blockNumber The number of the block for which scrutiny details are created.
	 * @return A new ScrutinyDetail object with headings and key set.
	 */
	private ScrutinyDetail createScrutinyDetail(String blockNumber) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		scrutinyDetail.setKey(BLOCK + blockNumber + PORTICO_SUFFIX);
		return scrutinyDetail;
	}
    
	/**
	 * Populates and adds the scrutiny report details to the provided plan.
	 *
	 * @param pl             The plan object containing report output.
	 * @param ruleNo         The rule number being checked.
	 * @param ruleDesc       A description of the rule.
	 * @param expected       The permissible or required value.
	 * @param actual         The actual value found in the plan.
	 * @param status         The validation result (Accepted/Not Accepted).
	 * @param scrutinyDetail The scrutiny detail object to which the row will be added.
	 */
	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual, String status,
		  ScrutinyDetail scrutinyDetail) {
			  ReportScrutinyDetail detail = new ReportScrutinyDetail();
			  detail.setRuleNo(ruleNo);
			  detail.setDescription(ruleDesc);
			  detail.setRequired(expected);
			  detail.setProvided(actual);
			  detail.setStatus(status);

			  Map<String, String> details = mapReportDetails(detail);
			  addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	}


    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>(); 
    }
}
