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
 */package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;


@Service
public class ToiletDetails extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(ToiletDetails.class);

    /**
     * Validates the building plan for toilet requirements.
     * Currently performs no validation and returns the plan as-is.
     *
     * @param pl The building plan to validate
     * @return The unmodified plan
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    @Autowired
	MDMSCacheManager cache;

    /**
     * Processes toilet requirements for all blocks in the building plan.
     * Creates scrutiny details and validates each block's toilet specifications
     * against minimum area, width, and ventilation requirements.
     *
     * @param pl The building plan to process
     * @return The processed plan with scrutiny details added
     */
    @Override
    public Plan process(Plan pl) {
        ScrutinyDetail scrutinyDetail = createToiletScrutinyDetail();

        for (Block block : pl.getBlocks()) {
            processBlockToilets(pl, block, scrutinyDetail);
        }

        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        return pl;
    }

    /**
     * Creates and initializes a scrutiny detail object for toilet validation reporting.
     * Sets up column headings and key for the toilet scrutiny report.
     *
     * @return Configured ScrutinyDetail object with appropriate headings and key
     */
    private ScrutinyDetail createToiletScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(Common_Toilet);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, FLOOR_NO);
        scrutinyDetail.addColumnHeading(4, REQUIRED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        return scrutinyDetail;
    }

    /**
     * Processes all toilets within a specific building block.
     * Iterates through floors and toilet measurements to validate each toilet
     * against the required specifications.
     *
     * @param pl The building plan
     * @param block The building block containing toilets
     * @param scrutinyDetail The scrutiny detail object to add results to
     */
    private void processBlockToilets(Plan pl, Block block, ScrutinyDetail scrutinyDetail) {
        if (block.getBuilding() == null || block.getBuilding().getFloors() == null) return;

        for (Floor floor : block.getBuilding().getFloors()) {
            if (floor.getToilet() == null || floor.getToilet().isEmpty()) continue;

            for (Toilet toilet : floor.getToilet()) {
                if (toilet.getToilets() == null || toilet.getToilets().isEmpty()) continue;

                for (Measurement toiletMeasurement : toilet.getToilets()) {
                    evaluateToiletMeasurement(pl, floor, toilet, toiletMeasurement, scrutinyDetail);
                }
            }
        }
    }

    /**
     * Evaluates a single toilet measurement against minimum requirements.
     * Validates area, width, and ventilation height against MDMS rules and
     * generates compliance status for the scrutiny report.
     *
     * @param pl The building plan
     * @param floor The floor containing the toilet
     * @param toilet The toilet object being evaluated
     * @param measurement The specific toilet measurement to validate
     * @param scrutinyDetail The scrutiny detail object to add results to
     */
    private void evaluateToiletMeasurement(Plan pl, Floor floor, Toilet toilet, Measurement measurement,
                                           ScrutinyDetail scrutinyDetail) {
        BigDecimal area = measurement.getArea().setScale(2, RoundingMode.HALF_UP);
        BigDecimal width = measurement.getWidth().setScale(2, RoundingMode.HALF_UP);
        BigDecimal ventilationHeight = toilet.getToiletVentilation() != null
                ? toilet.getToiletVentilation().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_41_5_5);
        detail.setDescription(TOILET_DESCRIPTION);
        detail.setFloorNo(String.valueOf(floor.getNumber()));

        Optional<ToiletRequirement> toiletRule = getToiletRule(pl);
        if (toiletRule == null) return;
        ToiletRequirement rule = toiletRule.get();
        BigDecimal minArea = rule.getMinToiletArea();
        BigDecimal minWidth = rule.getMinToiletWidth();
        BigDecimal minVentilation = rule.getMinToiletVentilation();

        String required = TOTAL_AREA_STRING + GREATER_THAN_EQUAL + minArea + COMMA_WIDTH_STRING + GREATER_THAN_EQUAL + minWidth + VENTILATION_STRING + GREATER_THAN_EQUAL + minVentilation;
        String provided = TOTAL_AREA_STRING + IS_EQUAL_TO + area + COMMA_WIDTH_STRING + IS_EQUAL_TO + width + VENTILATION_HEIGHT_STRING + ventilationHeight;

        detail.setRequired(required);
        detail.setProvided(provided);
        if (area.compareTo(minArea) >= 0 && width.compareTo(minWidth) >= 0 && ventilationHeight.compareTo(minVentilation) >= 0) {
            detail.setStatus(Result.Accepted.getResultVal());
        } else {
            detail.setStatus(Result.Not_Accepted.getResultVal());
        }

        Map<String, String> details = mapReportDetails(detail);
        scrutinyDetail.getDetail().add(details);
    }

    /**
     * Retrieves toilet requirement rules from MDMS cache.
     * Fetches the first matching toilet requirement rule based on plan configuration.
     *
     * @param pl The building plan containing configuration details
     * @return Optional containing ToiletRequirement rule if found, empty otherwise
     */
    private Optional<ToiletRequirement> getToiletRule(Plan pl) {
    	List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.TOILET.getValue(), false);
       return rules.stream()
            .filter(ToiletRequirement.class::isInstance)
            .map(ToiletRequirement.class::cast)
            .findFirst();
    }

    /**
     * Returns amendment dates for toilet requirement rules.
     * Currently returns an empty map as no amendments are defined.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
