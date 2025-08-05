package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.*;
import org.springframework.stereotype.Service;

import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class OpenStairService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(OpenStairService.class);

    @Override
    public Plan validate(Plan plan) {

        return plan;
    }

    @Override
    public Plan process(Plan plan) {/*
                                     * List<Block> blocks = plan.getBlocks(); for (Block block : blocks) { if (block.getBuilding()
                                     * != null && block.getOpenStairs() != null && block.getOpenStairs().size() > 0) {
                                     * scrutinyDetail = new ScrutinyDetail(); scrutinyDetail.addColumnHeading(1, RULE_NO);
                                     * scrutinyDetail.addColumnHeading(2, REQUIRED); scrutinyDetail.addColumnHeading(3, PROVIDED);
                                     * scrutinyDetail.addColumnHeading(4, STATUS); scrutinyDetail.setHeading("Open Stair");
                                     * scrutinyDetail.setKey("Block_" + block.getName() + "_OPEN STAIR"); for (Measurement
                                     * measurement : block.getOpenStairs()) { if (measurement.getMinimumDistance().setScale(2,
                                     * RoundingMode.HALF_UP).compareTo(OPENSTAIR_DISTANCE) >= 0) { setReportOutputDetails(plan,
                                     * SUB_RULE_24_11, String.format(SUB_RULE_24_11, block.getNumber()),
                                     * OPENSTAIR_DISTANCE.toString(), measurement.getMinimumDistance().toString(),
                                     * Result.Accepted.getResultVal(), scrutinyDetail); } else { setReportOutputDetails(plan,
                                     * SUB_RULE_24_11, String.format(SUB_RULE_24_11, block.getNumber()),
                                     * OPENSTAIR_DISTANCE.toString(), measurement.getMinimumDistance().toString(),
                                     * Result.Not_Accepted.getResultVal(), scrutinyDetail); } } } }
                                     */

        return plan;
    }

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
