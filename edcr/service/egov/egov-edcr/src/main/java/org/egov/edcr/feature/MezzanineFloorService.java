package org.egov.edcr.feature;

import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.HEIGHTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.IN_METER;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.SQMTRS;

import java.math.BigDecimal;
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
import org.egov.common.entity.edcr.Balcony;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Hall;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Occupancy;
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
public class MezzanineFloorService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(MezzanineFloorService.class);
    private static final String SUBRULE_46 = "46";
    private static final String RULE46_MAXAREA_DESC = "Maximum allowed area of mezzanine floor";
    private static final String RULE46_MINAREA_DESC = "Minimum area of mezzanine floor";
    private static final String RULE46_DIM_DESC = "Minimum height of mezzanine floor";
    public static final String SUB_RULE_55_7_DESC = "Maximum allowed area of balcony";
    public static final String SUB_RULE_55_7 = "55-7";
    private static final String FLOOR = "Floor";
    public static final String HALL_NUMBER = "Hall Number";

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
     * Processes the given plan to validate mezzanine floor dimensions.
     * Fetches permissible values for mezzanine floor area, height, and built-up area, and validates them against the plan details.
     *
     * @param pl The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */

    @Override
    public Plan process(Plan pl) {
        validate(pl);

        if (pl != null && !pl.getBlocks().isEmpty()) {
            String subRule = SUBRULE_46;
            MezzanineRuleValues ruleValues = getPermissibleMezzanineRuleValues(pl);

            for (Block block : pl.getBlocks()) {
                processBlockMezzanineFloors(pl, block, subRule, ruleValues);
            }
        }

        return pl;
    }

    private MezzanineRuleValues getPermissibleMezzanineRuleValues(Plan pl) {
        BigDecimal area = BigDecimal.ZERO;
        BigDecimal height = BigDecimal.ZERO;
        BigDecimal builtUp = BigDecimal.ONE; // prevent divide by zero

        List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.MEZZANINE_FLOOR_SERVICE, false);
        Optional<MdmsFeatureRule> matchedRule = rules.stream()
            .map(obj -> (MdmsFeatureRule) obj)
            .findFirst();

        if (matchedRule.isPresent()) {
            MdmsFeatureRule rule = matchedRule.get();
            area = rule.getMezzanineArea();
            height = rule.getMezzanineHeight();
            builtUp = rule.getMezzanineBuiltUpArea();
        }

        return new MezzanineRuleValues(area, height, builtUp);
    }

    private void processBlockMezzanineFloors(Plan pl, Block block, String subRule, MezzanineRuleValues ruleValues) {
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, FLOOR);
        scrutinyDetail.addColumnHeading(4, REQUIRED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Mezzanine Floor");

        if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
            for (Floor floor : block.getBuilding().getFloors()) {
                validateMezzanineFloors(pl, block, floor, subRule, ruleValues);
            }
        }

        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    private void validateMezzanineFloors(Plan pl, Block block, Floor floor, String subRule, MezzanineRuleValues ruleValues) {
        BigDecimal builtUpArea = floor.getOccupancies().stream()
            .filter(occ -> !occ.getIsMezzanine() && occ.getBuiltUpArea() != null)
            .map(occ -> occ.getBuiltUpArea().subtract(occ.getDeduction()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (Occupancy mezzanine : floor.getOccupancies()) {
            if (!mezzanine.getIsMezzanine() || floor.getNumber() == 0) continue;

            String floorNo = " floor " + floor.getNumber();
            String mezzNo = mezzanine.getMezzanineNumber();

            if (mezzanine.getBuiltUpArea() != null && mezzanine.getBuiltUpArea().doubleValue() > 0
                && mezzanine.getTypeHelper() == null) {
                pl.addError(OBJECTNOTDEFINED_DESC,
                		 getLocaleMessage("msg.error.mezz.occupancy.not.defined", block.getNumber(),
                               String.valueOf(floor.getNumber()), mezzNo));
            }

            BigDecimal mezzArea = (mezzanine.getBuiltUpArea() != null)
                ? mezzanine.getBuiltUpArea().subtract(mezzanine.getDeduction()) : BigDecimal.ZERO;

            validateMezzanineHeight(pl, mezzanine, block, floor, subRule, ruleValues, floorNo);
            validateMezzanineMinArea(pl, mezzanine, ruleValues, subRule, floorNo);
            validateMezzanineMaxArea(pl, mezzanine, mezzArea, builtUpArea, ruleValues, subRule, floorNo);
        }
    }

    private void validateMezzanineHeight(Plan pl, Occupancy mezzanine, Block block, Floor floor,
            String subRule, MezzanineRuleValues ruleValues, String floorNo) {

        BigDecimal height = mezzanine.getHeight();
        String mezzNo = mezzanine.getMezzanineNumber();

        if (height.compareTo(BigDecimal.ZERO) == 0) {
            pl.addError(RULE46_DIM_DESC,
                getLocaleMessage(HEIGHTNOTDEFINED, "Mezzanine floor " + mezzNo, block.getName(), String.valueOf(floor.getNumber())));
        } else if (height.compareTo(ruleValues.height) >= 0) {
            setReportOutputDetails(pl, subRule, RULE46_DIM_DESC + " " + mezzNo, floorNo,
                ruleValues.height + IN_METER, height + IN_METER, Result.Accepted.getResultVal());
        } else {
            setReportOutputDetails(pl, subRule, RULE46_DIM_DESC + " " + mezzNo, floorNo,
                ruleValues.height + IN_METER, height + IN_METER, Result.Not_Accepted.getResultVal());
        }
    }

    private void validateMezzanineMinArea(Plan pl, Occupancy mezzanine, MezzanineRuleValues ruleValues,
            String subRule, String floorNo) {

        String mezzNo = mezzanine.getMezzanineNumber();
        BigDecimal actual = mezzanine.getBuiltUpArea();

        String status = (actual.compareTo(ruleValues.area) >= 0) ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal();
        setReportOutputDetails(pl, subRule, RULE46_MINAREA_DESC + " " + mezzNo, floorNo,
            ruleValues.area + SQMTRS, actual + SQMTRS, status);
    }

    private void validateMezzanineMaxArea(Plan pl, Occupancy mezzanine, BigDecimal mezzArea, BigDecimal builtUpArea,
            MezzanineRuleValues ruleValues, String subRule, String floorNo) {

        String mezzNo = mezzanine.getMezzanineNumber();
        BigDecimal oneThirdBuiltUp = builtUpArea.divide(ruleValues.builtUp, DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);
        String status = (mezzArea.compareTo(oneThirdBuiltUp) <= 0) ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal();

        setReportOutputDetails(pl, subRule, RULE46_MAXAREA_DESC + " " + mezzNo, floorNo,
            oneThirdBuiltUp + SQMTRS, mezzArea + SQMTRS, status);
    }

    // Holds permissible values
    private static class MezzanineRuleValues {
        BigDecimal area, height, builtUp;

        MezzanineRuleValues(BigDecimal area, BigDecimal height, BigDecimal builtUp) {
            this.area = area;
            this.height = height;
            this.builtUp = (builtUp.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ONE : builtUp;
        }
    }
    
//  /**
//  * Sets the scrutiny details for mezzanine floor validation.
//  *
//  * @param pl The plan object.
//  * @param ruleNo The rule number.
//  * @param ruleDesc The rule description.
//  * @param floor The floor being validated.
//  * @param expected The expected value.
//  * @param actual The actual value provided.
//  * @param status The validation status.
//  */
 private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String floor, String expected, String actual,
         String status) {
     Map<String, String> details = new HashMap<>();
     details.put(RULE_NO, ruleNo);
     details.put(DESCRIPTION, ruleDesc);
     details.put(FLOOR, floor);
     details.put(REQUIRED, expected);
     details.put(PROVIDED, actual);
     details.put(STATUS, status);
     scrutinyDetail.getDetail().add(details);
     pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
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