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
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.FLOOR;
import static org.egov.edcr.constants.CommonFeatureConstants.GREATER_THAN;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class OverHangs extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(OverHangs.class);

    @Autowired
   	MDMSCacheManager cache;
    
    @Override
    public Plan validate(Plan pl) {

        return pl;
    }

    /**
     * Processes the given plan to validate overhangs for each block and floor.
     *
     * @param pl the plan object containing building data
     * @return the updated plan with scrutiny results for overhangs
     */
    @Override
    public Plan process(Plan pl) {
        ReportScrutinyDetail details = initializeRuleDetails();
        BigDecimal overHangsValue = getOverhangPermissibleValue(pl);

        for (Block block : pl.getBlocks()) {
            processBlock(block, pl, overHangsValue, details);
        }

        return pl;
    }

    /**
     * Initializes the rule details for overhang scrutiny.
     *
     * @return a map containing the rule number and description for overhangs
     */
    private ReportScrutinyDetail initializeRuleDetails() {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(RULE_45);
        detail.setDescription(OVERHANGS_DESCRIPTION);
        return detail;
    }

    /**
     * Retrieves the permissible overhang width from MDMS rules.
     *
     * @param pl the plan object
     * @return the permissible overhang value, or zero if not defined
     */
    private BigDecimal getOverhangPermissibleValue(Plan pl) {
    	 List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.OVERHANGS.getValue(), false);
         Optional<OverHangsRequirement> matchedRule = rules.stream()
             .filter(OverHangsRequirement.class::isInstance)
             .map(OverHangsRequirement.class::cast)
             .findFirst();

        return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
    }

    /**
     * Processes a single block within the plan for overhang validations.
     *
     * @param block the block to process
     * @param pl the parent plan object
     * @param overHangsValue the permissible overhang value
     * @param detailsTemplate the template map for rule details
     */
    private void processBlock(Block block, Plan pl, BigDecimal overHangsValue, ReportScrutinyDetail detailsTemplate) {
        ScrutinyDetail scrutinyDetail = initializeScrutinyDetail(block);

        Building building = block.getBuilding();
        if (building != null) {
            for (Floor floor : building.getFloors()) {
                processFloor(floor, scrutinyDetail, overHangsValue, detailsTemplate);
            }
        }

        if (!scrutinyDetail.getDetail().isEmpty()) {
            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }
    }

    /**
     * Initializes the scrutiny detail structure for the given block.
     *
     * @param block the block for which scrutiny detail is created
     * @return a new {@link ScrutinyDetail} instance with column headers set
     */
    private ScrutinyDetail initializeScrutinyDetail(Block block) {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(BLOCK + block.getNumber() + CHAJJA_SUFFIX);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, FLOOR);
        scrutinyDetail.addColumnHeading(3, DESCRIPTION);
        scrutinyDetail.addColumnHeading(4, PERMISSIBLE);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        return scrutinyDetail;
    }

    /**
     * Processes a single floor to validate the overhang width against the permissible value.
     *
     * @param floor the floor to validate
     * @param scrutinyDetail the scrutiny detail object to record the result
     * @param overHangsValue the permissible overhang width
     * @param detail the rule details template
     */
    private void processFloor(Floor floor, ScrutinyDetail scrutinyDetail, BigDecimal overHangsValue, ReportScrutinyDetail detail) {
        if (floor.getOverHangs() == null || floor.getOverHangs().isEmpty()) {
            return;
        }

        List<BigDecimal> widths = floor.getOverHangs().stream()
        	    .map(overhang -> overhang.getWidth())
        	    .collect(Collectors.toList());

        BigDecimal minWidth = widths.stream().reduce(BigDecimal::min).get();
        detail.setPermissible(GREATER_THAN + overHangsValue.toString());
        detail.setFloorNo(floor.getNumber().toString());
        detail.setProvided(minWidth.toString());
        if (minWidth.compareTo(overHangsValue) > 0) {
            detail.setStatus(Result.Accepted.getResultVal());
        } else {
            detail.setStatus(Result.Not_Accepted.getResultVal());
        }
        Map<String, String> details = mapReportDetails(detail);

        scrutinyDetail.getDetail().add(details);
    }

    
   
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}
