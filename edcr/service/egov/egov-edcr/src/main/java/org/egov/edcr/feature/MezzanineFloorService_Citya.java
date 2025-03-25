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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Balcony;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Hall;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MezzanineFloorService_Citya extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(MezzanineFloorService_Citya.class);
    private static final String SUBRULE_46 = "46";
    private static final String RULE46_MAXAREA_DESC = "Maximum allowed area of mezzanine floor";
    private static final String RULE46_MINAREA_DESC = "Minimum area of mezzanine floor";
    private static final String RULE46_DIM_DESC = "Minimum height of mezzanine floor";
    public static final String SUB_RULE_55_7_DESC = "Maximum allowed area of balcony";
    public static final String SUB_RULE_55_7 = "55-7";
    private static final String FLOOR = "Floor";
    public static final String HALL_NUMBER = "Hall Number";
    private static final BigDecimal AREA_9_POINT_5 = BigDecimal.valueOf(9.5);
    private static final BigDecimal HEIGHT_2_POINT_2 = BigDecimal.valueOf(2.2);

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

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

        // Rule identifier for mezzanine floor validation
        String subRule = SUBRULE_46;

        // Variables to store permissible values
        BigDecimal mezzanineArea = BigDecimal.ZERO;
        BigDecimal mezzanineHeight = BigDecimal.ZERO;
        BigDecimal mezzanineBuiltUpArea = BigDecimal.ZERO;

        if (pl != null && !pl.getBlocks().isEmpty()) {
            // Determine the occupancy type and feature for fetching permissible values
            String occupancyName = null;
            String feature = "MezzanineFloorService";

            Map<String, Object> params = new HashMap<>();
            if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
                occupancyName = "Residential";
            }

            params.put("feature", feature);
            params.put("occupancy", occupancyName);

            // Fetch permissible values for mezzanine floor
            Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
            ArrayList<String> valueFromColumn = new ArrayList<>();
            valueFromColumn.add("mezzanineArea");
            valueFromColumn.add("mezzanineHeight");
            valueFromColumn.add("mezzanineBuiltUpArea");

   			List<Map<String, Object>> permissibleValue = new ArrayList<>();
   		
   			
   				permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
            LOG.info("permissibleValue" + permissibleValue);

            if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey("mezzanineArea")) {
                mezzanineArea = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("mezzanineArea").toString()));
                mezzanineHeight = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("mezzanineHeight").toString()));
                mezzanineBuiltUpArea = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("mezzanineBuiltUpArea").toString()));
            }

            // Iterate through all blocks in the plan
            for (Block block : pl.getBlocks()) {
                scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                scrutinyDetail.addColumnHeading(3, FLOOR);
                scrutinyDetail.addColumnHeading(4, REQUIRED);
                scrutinyDetail.addColumnHeading(5, PROVIDED);
                scrutinyDetail.addColumnHeading(6, STATUS);
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Mezzanine Floor");

                if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
                    BigDecimal totalBuiltupArea = BigDecimal.ZERO;

                    // Iterate through all floors in the block
                    for (Floor floor : block.getBuilding().getFloors()) {
                        BigDecimal builtupArea = BigDecimal.ZERO;

                        // Calculate total built-up area for the floor
                        for (Occupancy occ : floor.getOccupancies()) {
                            if (!occ.getIsMezzanine() && occ.getBuiltUpArea() != null) {
                                builtupArea = builtupArea.add(occ.getBuiltUpArea().subtract(occ.getDeduction()));
                            }
                        }
                        totalBuiltupArea = totalBuiltupArea.add(builtupArea);

                        // Validate mezzanine floors in the current floor
                        for (Occupancy mezzanineFloor : floor.getOccupancies()) {
                            if (mezzanineFloor.getIsMezzanine() && floor.getNumber() != 0) {
                                if (mezzanineFloor.getBuiltUpArea() != null && mezzanineFloor.getBuiltUpArea().doubleValue() > 0
                                        && mezzanineFloor.getTypeHelper() == null) {
                                    pl.addError(OBJECTNOTDEFINED_DESC,
                                            getLocaleMessage("msg.error.mezz.occupancy.not.defined", block.getNumber(),
                                                    String.valueOf(floor.getNumber()), mezzanineFloor.getMezzanineNumber()));
                                }

                                BigDecimal mezzanineFloorArea = BigDecimal.ZERO;
                                if (mezzanineFloor.getBuiltUpArea() != null) {
                                    mezzanineFloorArea = mezzanineFloor.getBuiltUpArea().subtract(mezzanineFloor.getDeduction());
                                }

                                boolean valid = false;
                                BigDecimal oneThirdOfBuiltup = builtupArea.divide(mezzanineBuiltUpArea, DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);

                                // Validate mezzanine floor area
                                if (mezzanineFloorArea.doubleValue() > 0 && mezzanineFloorArea.compareTo(oneThirdOfBuiltup) <= 0) {
                                    valid = true;
                                }

                                String floorNo = " floor " + floor.getNumber();

                                // Validate mezzanine floor height
                                BigDecimal height = mezzanineFloor.getHeight();
                                if (height.compareTo(BigDecimal.ZERO) == 0) {
                                    pl.addError(RULE46_DIM_DESC,
                                            getLocaleMessage(HEIGHTNOTDEFINED,
                                                    "Mezzanine floor " + mezzanineFloor.getMezzanineNumber(),
                                                    block.getName(), String.valueOf(floor.getNumber())));
                                } else if (height.compareTo(mezzanineHeight) >= 0) {
                                    setReportOutputDetails(pl, subRule,
                                            RULE46_DIM_DESC + " " + mezzanineFloor.getMezzanineNumber(), floorNo,
                                            mezzanineHeight + IN_METER, height + IN_METER, Result.Accepted.getResultVal());
                                } else {
                                    setReportOutputDetails(pl, subRule,
                                            RULE46_DIM_DESC + " " + mezzanineFloor.getMezzanineNumber(), floorNo,
                                            mezzanineHeight + IN_METER, height + IN_METER, Result.Not_Accepted.getResultVal());
                                }

                                // Validate minimum mezzanine floor area
                                if (mezzanineFloor.getBuiltUpArea().compareTo(mezzanineArea) >= 0) {
                                    setReportOutputDetails(pl, subRule,
                                            RULE46_MINAREA_DESC + " " + mezzanineFloor.getMezzanineNumber(), floorNo,
                                            mezzanineArea + SQMTRS, mezzanineFloor.getBuiltUpArea() + SQMTRS,
                                            Result.Accepted.getResultVal());
                                } else {
                                    setReportOutputDetails(pl, subRule,
                                            RULE46_MINAREA_DESC + " " + mezzanineFloor.getMezzanineNumber(), floorNo,
                                            mezzanineArea + SQMTRS, mezzanineFloor.getBuiltUpArea() + SQMTRS,
                                            Result.Not_Accepted.getResultVal());
                                }

                                // Validate maximum mezzanine floor area
                                if (valid) {
                                    setReportOutputDetails(pl, subRule,
                                            RULE46_MAXAREA_DESC + " " + mezzanineFloor.getMezzanineNumber(), floorNo,
                                            oneThirdOfBuiltup + SQMTRS, mezzanineFloorArea + SQMTRS,
                                            Result.Accepted.getResultVal());
                                } else {
                                    setReportOutputDetails(pl, subRule,
                                            RULE46_MAXAREA_DESC + " " + mezzanineFloor.getMezzanineNumber(), floorNo,
                                            oneThirdOfBuiltup + SQMTRS, mezzanineFloorArea + SQMTRS,
                                            Result.Not_Accepted.getResultVal());
                                }
                            }
                        }
                    }
                }
            }
        }
        return pl;
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