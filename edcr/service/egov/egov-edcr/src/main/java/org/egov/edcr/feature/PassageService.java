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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class PassageService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(PassageService.class);

    @Autowired
   	MDMSCacheManager cache;
    
    /**
     * Validates the given plan for passage-related checks.
     * Currently, no specific validation logic is implemented.
     *
     * @param plan the plan object containing passage details
     * @return the same plan object, unchanged
     */
    @Override
    public Plan validate(Plan plan) {
        return plan;
    }

    /**
     * Processes the plan to validate passage and passage stair widths against
     * minimum permissible values defined in MDMS rules. Appends scrutiny results
     * to the plan's report output.
     *
     * @param plan the plan object containing building blocks and passage data
     * @return the plan object updated with passage scrutiny details
     */
    @Override
    public Plan process(Plan plan) {
        BigDecimal passageStairMinimumWidth = BigDecimal.ZERO;
        BigDecimal passageMinWidth = BigDecimal.ZERO;

        List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.PASSAGE_SERVICE.getValue(), false);
       rules.stream()
            .filter(PassageRequirement.class::isInstance)
            .map(PassageRequirement.class::cast)
            .findFirst();

        for (Object obj : rules) {
        	PassageRequirement rule = (PassageRequirement) obj;
            passageStairMinimumWidth = rule.getPassageServiceValueOne();
            passageMinWidth = rule.getPassageServiceValueTwo();
            break; 
        }

        for (Block block : plan.getBlocks()) {
            if (block.getBuilding() == null || block.getBuilding().getPassage() == null) {
                continue;
            }

            ScrutinyDetail passageDetail = createScrutinyDetail(BLOCK + block.getNumber() + PASSAGE_SUFFIX);
            ScrutinyDetail passageStairDetail = createScrutinyDetail(BLOCK + block.getNumber() + PASSAGE_STAIR_SUFFIX);

            org.egov.common.entity.edcr.Passage passage = block.getBuilding().getPassage();

            validatePassageDimension(
                passage.getPassageDimensions(), 
                passageMinWidth, 
                RULE41, 
                RULE_41_DESCRIPTION, 
                passageDetail, 
                plan
            );

            validatePassageDimension(
                passage.getPassageStairDimensions(), 
                passageStairMinimumWidth, 
                RULE39_6, 
                RULE39_6_DESCRIPTION, 
                passageStairDetail, 
                plan
            );
        }

        return plan;
    }

    /**
     * Validates whether the minimum passage or passage stair width from the provided dimensions
     * meets or exceeds the permissible width. Adds the result to the scrutiny report.
     *
     * @param dimensions list of width dimensions for passage or passage stair
     * @param permissibleWidth the required minimum width from MDMS rules
     * @param ruleNo the rule number to be displayed in the report
     * @param ruleDesc the description of the rule
     * @param detail the scrutiny detail to which results are appended
     * @param plan the plan to which scrutiny details are added
     */
    private void validatePassageDimension(List<BigDecimal> dimensions, BigDecimal permissibleWidth,
                                          String ruleNo, String ruleDesc, ScrutinyDetail detail, Plan plan) {
        if (dimensions != null && !dimensions.isEmpty()) {
            BigDecimal minWidth = Util.roundOffTwoDecimal(dimensions.stream().reduce(BigDecimal::min).get());
            String result = minWidth.compareTo(permissibleWidth) >= 0
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal();

            setReportOutputDetails(plan, ruleNo, ruleDesc, permissibleWidth.toString(),
                    String.valueOf(minWidth), result, detail);
        }
    }

    /**
     * Creates a new {@link ScrutinyDetail} instance with a specified key and predefined column headings.
     *
     * @param key the key used to identify the scrutiny section
     * @return a new ScrutinyDetail object with standard headings
     */
    private ScrutinyDetail createScrutinyDetail(String key) {
        ScrutinyDetail detail = new ScrutinyDetail();
        detail.addColumnHeading(1, RULE_NO);
        detail.addColumnHeading(2, REQUIRED);
        detail.addColumnHeading(3, PROVIDED);
        detail.addColumnHeading(4, STATUS);
        detail.setKey(key);
        return detail;
    }

    /**
     * Populates and appends a scrutiny detail entry to the plan report.
     *
     * @param pl the plan to which the scrutiny detail is added
     * @param ruleNo the rule number being enforced
     * @param ruleDesc the rule description
     * @param expected the required value as per the rule
     * @param actual the actual value from the plan
     * @param status the result of the validation (Accepted/Not Accepted)
     * @param scrutinyDetail the scrutiny detail section where the result is recorded
     */
    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
                                        String status, ScrutinyDetail scrutinyDetail) {
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
