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

import static org.egov.edcr.constants.CommonFeatureConstants.M;
import static org.egov.edcr.constants.CommonFeatureConstants.SQ_M;
import static org.egov.edcr.constants.CommonKeyConstants.COMMON_INTERIOR_OPEN_SPACE;
import static org.egov.edcr.constants.EdcrReportConstants.AREA;
import static org.egov.edcr.constants.EdcrReportConstants.AT_FLOOR;
import static org.egov.edcr.constants.EdcrReportConstants.INTERNALCOURTYARD_DESCRIPTION;
import static org.egov.edcr.constants.EdcrReportConstants.MINIMUM_WIDTH;
import static org.egov.edcr.constants.EdcrReportConstants.RULE_43;
import static org.egov.edcr.constants.EdcrReportConstants.RULE_43A;
import static org.egov.edcr.constants.EdcrReportConstants.VENTILATIONSHAFT_DESCRIPTION;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.InteriorOpenSpaceServiceRequirement;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ReportScrutinyDetail;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InteriorOpenSpaceService extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(InteriorOpenSpaceService.class);

    @Autowired
    private MDMSCacheManager cache;

    @Override
    public Plan validate(Plan pl) {
        LOG.debug("Validating plan: {}");
        return pl;
    }

    @Override
    public Plan process(Plan pl) {
        LOG.info("Processing Interior Open Space for plan");
        InteriorOpenSpaceServiceRequirement ruleValues = getRuleValues(pl);
        if (ruleValues != null) {
            LOG.debug("Fetched rule values for Interior Open Space: {}", ruleValues);
            processInteriorOpenSpaces(pl, ruleValues);
        } else {
            LOG.warn("No Interior Open Space rules found for plan");
        }
        return pl;
    }

    /**
     * Retrieves the matched rule for interior open space service feature from cache.
     */
    private InteriorOpenSpaceServiceRequirement getRuleValues(Plan pl) {
        LOG.debug("Fetching Interior Open Space rules for plan");
        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.INTERIOR_OPEN_SPACE_SERVICE.getValue(), false);
        InteriorOpenSpaceServiceRequirement matched = rules.stream()
                .filter(InteriorOpenSpaceServiceRequirement.class::isInstance)
                .map(InteriorOpenSpaceServiceRequirement.class::cast)
                .findFirst()
                .orElse(null);
        LOG.debug("Rule values found: {}", matched != null);
        return matched;
    }

    /**
     * Processes all interior open space components such as ventilation shafts and inner courtyards.
     */
    private void processInteriorOpenSpaces(Plan pl, InteriorOpenSpaceServiceRequirement ruleValues) {
        LOG.info("Processing interior open spaces for {} blocks in plan {}", 
                pl.getBlocks().size());

        for (Block b : pl.getBlocks()) {
            LOG.debug("Processing Block: {}", b.getNumber());
            ScrutinyDetail scrutinyDetail = createScrutinyDetail();

            if (b.getBuilding() != null && b.getBuilding().getFloors() != null && !b.getBuilding().getFloors().isEmpty()) {
                LOG.debug("Block {} has {} floors", b.getNumber(), b.getBuilding().getFloors().size());

                for (Floor f : b.getBuilding().getFloors()) {
                    LOG.debug("Processing Floor: {}", f.getNumber());

                    processOpenSpaceComponent(pl, scrutinyDetail, f,
                            f.getInteriorOpenSpace().getVentilationShaft().getMeasurements(),
                            ruleValues.getMinVentilationAreaValueOne(),
                            ruleValues.getMinVentilationAreaValueTwo(),
                            ruleValues.getMinVentilationWidthValueOne(),
                            ruleValues.getMinVentilationWidthValueTwo(),
                            RULE_43, RULE_43A, VENTILATIONSHAFT_DESCRIPTION);

                    processOpenSpaceComponent(pl, scrutinyDetail, f,
                            f.getInteriorOpenSpace().getInnerCourtYard().getMeasurements(),
                            ruleValues.getMinInteriorAreaValueOne(),
                            ruleValues.getMinInteriorAreaValueTwo(),
                            ruleValues.getMinInteriorWidthValueOne(),
                            ruleValues.getMinInteriorWidthValueTwo(),
                            RULE_43, RULE_43A, INTERNALCOURTYARD_DESCRIPTION);
                }
            } else {
                LOG.debug("Block {} has no floors or no building defined", b.getNumber());
            }
        }
    }

    /**
     * Validates a list of open space measurements (area and width) for a specific floor.
     */
    private void processOpenSpaceComponent(Plan pl, ScrutinyDetail scrutinyDetail, Floor f,
                                           List<Measurement> measurements,
                                           BigDecimal areaValueOne, BigDecimal areaValueTwo,
                                           BigDecimal widthValueOne, BigDecimal widthValueTwo,
                                           String ruleNoArea, String ruleNoWidth, String description) {

        if (measurements == null || measurements.isEmpty()) {
            LOG.debug("No measurements found for {} at floor {}", description, f.getNumber());
            return;
        }

        LOG.debug("Validating {} with {} measurements at floor {}", description, measurements.size(), f.getNumber());

        BigDecimal minArea = measurements.stream()
                .map(Measurement::getArea)
                .reduce(BigDecimal::min)
                .orElse(BigDecimal.ZERO);

        BigDecimal minWidth = measurements.stream()
                .map(Measurement::getWidth)
                .reduce(BigDecimal::min)
                .orElse(BigDecimal.ZERO);

        LOG.debug("{} minArea: {}, minWidth: {} at floor {}", description, minArea, minWidth, f.getNumber());

        // Area validation
        if (minArea.compareTo(areaValueOne) > 0) {
            LOG.info("Checking AREA for {} at floor {}: Required > {} sq.m, Provided: {}", 
                    description, f.getNumber(), areaValueTwo, minArea);

            ReportScrutinyDetail detail = new ReportScrutinyDetail();
            detail.setRuleNo(ruleNoArea);
            detail.setDescription(description);
            detail.setRequired(MINIMUM_WIDTH + areaValueTwo.toString() + SQ_M);
            detail.setProvided(AREA + minArea + AT_FLOOR + f.getNumber());
            detail.setStatus(minArea.compareTo(areaValueTwo) >= 0
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal());

            if (minArea.compareTo(areaValueTwo) >= 0) {
                LOG.info("AREA validation PASSED for {} at floor {}", description, f.getNumber());
            } else {
                LOG.warn("AREA validation FAILED for {} at floor {}", description, f.getNumber());
            }

            Map<String, String> details = mapReportDetails(detail);
            addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
        }

        // Width validation
        if (minWidth.compareTo(widthValueOne) > 0) {
            LOG.info("Checking WIDTH for {} at floor {}: Required > {} m, Provided: {}", 
                    description, f.getNumber(), widthValueTwo, minWidth);

            ReportScrutinyDetail detail = new ReportScrutinyDetail();
            detail.setRuleNo(ruleNoWidth);
            detail.setDescription(description);
            detail.setRequired(MINIMUM_WIDTH + widthValueTwo.toString() + M);
            detail.setProvided(AREA + minWidth + AT_FLOOR + f.getNumber());
            detail.setStatus(minWidth.compareTo(widthValueTwo) >= 0
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal());

            if (minWidth.compareTo(widthValueTwo) >= 0) {
                LOG.info("WIDTH validation PASSED for {} at floor {}", description, f.getNumber());
            } else {
                LOG.warn("WIDTH validation FAILED for {} at floor {}", description, f.getNumber());
            }

            Map<String, String> details = mapReportDetails(detail);
            addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
        }
    }

    private ScrutinyDetail createScrutinyDetail() {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(COMMON_INTERIOR_OPEN_SPACE);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
    }

    @Override
    public Map<String, Date> getAmendments() {
        LOG.debug("No amendments defined for Interior Open Space");
        return new LinkedHashMap<>();
    }
}
