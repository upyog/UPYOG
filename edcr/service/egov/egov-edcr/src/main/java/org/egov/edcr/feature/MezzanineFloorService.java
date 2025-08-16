package org.egov.edcr.feature;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonFeatureConstants.FLOOR;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.HEIGHTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.IN_METER;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED_DESC;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.SQMTRS;

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
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MezzanineFloorService extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(MezzanineFloorService.class);

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    @Autowired
	MDMSCacheManager cache;
	
	

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

    /**
     * Retrieves the permissible rule values for mezzanine floors from the cached feature rules.
     *
     * @param pl the {@link Plan} object containing plan details
     * @return a {@link MezzanineRuleValues} object containing permissible area, height, and built-up area ratio
     */
    private MezzanineRuleValues getPermissibleMezzanineRuleValues(Plan pl) {
        BigDecimal area = BigDecimal.ZERO;
        BigDecimal height = BigDecimal.ZERO;
        BigDecimal builtUp = BigDecimal.ONE; // prevent divide by zero

        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.MEZZANINE_FLOOR_SERVICE.getValue(), false);
        Optional<MezzanineFloorServiceRequirement> matchedRule = rules.stream()
            .filter(MezzanineFloorServiceRequirement.class::isInstance)
            .map(MezzanineFloorServiceRequirement.class::cast)
            .findFirst();

        if (matchedRule.isPresent()) {
        	MezzanineFloorServiceRequirement rule = matchedRule.get();
            area = rule.getMezzanineArea();
            height = rule.getMezzanineHeight();
            builtUp = rule.getMezzanineBuiltUpArea();
        }

        return new MezzanineRuleValues(area, height, builtUp);
    }

    /**
     * Processes all mezzanine floors within a given block of the plan by validating each floor
     * against the provided rule values and adding the results to the scrutiny report.
     *
     * @param pl the {@link Plan} object
     * @param block the {@link Block} to process
     * @param subRule the rule number or reference
     * @param ruleValues the permissible rule values for mezzanine floors
     */
    private void processBlockMezzanineFloors(Plan pl, Block block, String subRule, MezzanineRuleValues ruleValues) {
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, FLOOR);
        scrutinyDetail.addColumnHeading(4, REQUIRED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);
        scrutinyDetail.setKey(BLOCK + block.getNumber() + UNDERSCORE + MEZZANINE_FLOOR);

        if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
            for (Floor floor : block.getBuilding().getFloors()) {
                validateMezzanineFloors(pl, block, floor, subRule, ruleValues);
            }
        }

        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    /**
     * Validates mezzanine floors on a given floor of a block against the permissible rule values.
     *
     * @param pl the {@link Plan} object
     * @param block the {@link Block} that contains the floor
     * @param floor the {@link Floor} to validate
     * @param subRule the rule number or reference
     * @param ruleValues the permissible rule values for mezzanine floors
     */
    private void validateMezzanineFloors(Plan pl, Block block, Floor floor, String subRule, MezzanineRuleValues ruleValues) {
        BigDecimal builtUpArea = floor.getOccupancies().stream()
            .filter(occ -> !occ.getIsMezzanine() && occ.getBuiltUpArea() != null)
            .map(occ -> occ.getBuiltUpArea().subtract(occ.getDeduction()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (Occupancy mezzanine : floor.getOccupancies()) {
            if (!mezzanine.getIsMezzanine() || floor.getNumber() == 0) continue;

            String floorNo = FLOOR_SPACED + floor.getNumber();
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

    /**
     * Validates the height of the given mezzanine floor against the permissible height rule.
     *
     * @param pl the {@link Plan} object
     * @param mezzanine the {@link Occupancy} representing the mezzanine
     * @param block the {@link Block} in which the mezzanine is located
     * @param floor the {@link Floor} the mezzanine belongs to
     * @param subRule the rule number or reference
     * @param ruleValues the permissible rule values
     * @param floorNo the floor number as a string for reporting
     */
    private void validateMezzanineHeight(Plan pl, Occupancy mezzanine, Block block, Floor floor,
            String subRule, MezzanineRuleValues ruleValues, String floorNo) {

        BigDecimal height = mezzanine.getHeight();
        String mezzNo = mezzanine.getMezzanineNumber();

        if (height.compareTo(BigDecimal.ZERO) == 0) {
            pl.addError(RULE46_DIM_DESC,
                getLocaleMessage(HEIGHTNOTDEFINED, MEZZANINE_FLOOR + SINGLE_SPACE_STRING + mezzNo, block.getName(), String.valueOf(floor.getNumber())));
        } else if (height.compareTo(ruleValues.height) >= 0) {
            setReportOutputDetails(pl, subRule, RULE46_DIM_DESC + SINGLE_SPACE_STRING + mezzNo, floorNo,
                ruleValues.height + IN_METER, height + IN_METER, Result.Accepted.getResultVal());
        } else {
            setReportOutputDetails(pl, subRule, RULE46_DIM_DESC + SINGLE_SPACE_STRING + mezzNo, floorNo,
                ruleValues.height + IN_METER, height + IN_METER, Result.Not_Accepted.getResultVal());
        }
    }

    /**
     * Validates the minimum built-up area of the mezzanine against the permissible minimum area rule.
     *
     * @param pl the {@link Plan} object
     * @param mezzanine the {@link Occupancy} representing the mezzanine
     * @param ruleValues the permissible rule values
     * @param subRule the rule number or reference
     * @param floorNo the floor number as a string for reporting
     */	
    private void validateMezzanineMinArea(Plan pl, Occupancy mezzanine, MezzanineRuleValues ruleValues,
            String subRule, String floorNo) {

        String mezzNo = mezzanine.getMezzanineNumber();
        BigDecimal actual = mezzanine.getBuiltUpArea();

        String status = (actual.compareTo(ruleValues.area) >= 0) ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal();
        setReportOutputDetails(pl, subRule, RULE46_MINAREA_DESC + SINGLE_SPACE_STRING + mezzNo, floorNo,
            ruleValues.area + SQMTRS, actual + SQMTRS, status);
    }

    /**
     * Validates the maximum permissible area for a mezzanine, which should not exceed 1/3 of the built-up area.
     *
     * @param pl the {@link Plan} object
     * @param mezzanine the {@link Occupancy} representing the mezzanine
     * @param mezzArea the net area of the mezzanine after deduction
     * @param builtUpArea the total built-up area of the floor
     * @param ruleValues the permissible rule values
     * @param subRule the rule number or reference
     * @param floorNo the floor number as a string for reporting
     */
    private void validateMezzanineMaxArea(Plan pl, Occupancy mezzanine, BigDecimal mezzArea, BigDecimal builtUpArea,
            MezzanineRuleValues ruleValues, String subRule, String floorNo) {

        String mezzNo = mezzanine.getMezzanineNumber();
        BigDecimal oneThirdBuiltUp = builtUpArea.divide(ruleValues.builtUp, DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);
        String status = (mezzArea.compareTo(oneThirdBuiltUp) <= 0) ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal();

        setReportOutputDetails(pl, subRule, RULE46_MAXAREA_DESC + SINGLE_SPACE_STRING + mezzNo, floorNo,
            oneThirdBuiltUp + SQMTRS, mezzArea + SQMTRS, status);
    }


/**
 * Holds the permissible rule values for mezzanine floor validation.
 */
    private static class MezzanineRuleValues {
        BigDecimal area, height, builtUp;

        MezzanineRuleValues(BigDecimal area, BigDecimal height, BigDecimal builtUp) {
            this.area = area;
            this.height = height;
            this.builtUp = (builtUp.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ONE : builtUp;
        }
    }
    
  /**
  * Sets the scrutiny details for mezzanine floor validation.
  *
  * @param pl The plan object.
  * @param ruleNo The rule number.
  * @param ruleDesc The rule description.
  * @param floor The floor being validated.
  * @param expected The expected value.
  * @param actual The actual value provided.
  * @param status The validation status.
  */
 private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String floor, String expected, String actual,
         String status) {
     ReportScrutinyDetail detail = new ReportScrutinyDetail();
     detail.setRuleNo(ruleNo);
     detail.setDescription(ruleDesc);
     detail.setFloorNo(floor);
     detail.setRequired(expected);
     detail.setProvided(actual);
     detail.setStatus(status);

     Map<String, String> details = mapReportDetails(detail);
     addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
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