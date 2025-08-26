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

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.utility.DcrConstants.HEIGHTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.WRONGHEIGHTDEFINED;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetBackService extends FeatureProcess {

    @Autowired
    private FrontYardService frontYardService;

  //  @Autowired
 //   private SideYardService sideYardService;

    @Autowired
    private RearYardService rearYardService;
    
    private static final BigDecimal TWO_HUNDRED = BigDecimal.valueOf(200);

    /**
     * Validates setback requirements for building plans according to DCR rules.
     * Checks mandatory yards at ground level and height validations for upper levels.
     *
     * @param pl The building plan to validate
     * @return The plan with validation errors added if any violations found
     */
    @Override
    public Plan validate(Plan pl) {
        Map<String, String> errors = new HashMap<>();

        for (Block block : pl.getBlocks()) {
            if (block.getCompletelyExisting()) continue;

            BigDecimal buildingHeight = block.getBuilding().getBuildingHeight();
            int setbackIndex = 0;

            for (SetBack setback : block.getSetBacks()) {
                

                setbackIndex++;

                if (setback.getLevel() == 0) {
                    validateMandatoryYardsAtZeroLevel(pl, block, setback, errors);
                } else {
                    validateHeightsAboveZeroLevel(block, setback, errors);
                }

                if (setback.getLevel() > 0 && isLastSetback(block, setbackIndex)) {
                    validateSetbackHeightsAgainstBuildingHeight(block, setback, buildingHeight, errors);
                }
            }
        }

        if (!errors.isEmpty()) {
            pl.addErrors(errors);
        }

        return pl;
    }

    /**
     * Validates mandatory yard requirements at ground level (level 0).
     * Front yard is always mandatory, rear yard required for plots > 200 sq.m unless NOC exists.
     *
     * @param pl The building plan
     * @param block The building block being validated
     * @param setback The setback at level 0
     * @param errors Map to collect validation errors
     */
    private void validateMandatoryYardsAtZeroLevel(Plan pl, Block block, SetBack setback, Map<String, String> errors) {
        String blockName = block.getName();
        
        if (setback.getFrontYard() == null) {
            errors.put(FRONTYARDNOTDEFINED, getLocaleMessage(OBJECTNOTDEFINED, FRONT_SETBACK + blockName + AT_LEVEL_ZERO));
        }

        if (pl.getPlot().getArea().compareTo(TWO_HUNDRED) > 0) {
            if (setback.getRearYard() == null &&
                !DcrConstants.YES.equalsIgnoreCase(pl.getPlanInformation().getNocToAbutRearDesc())) {
                errors.put(REARYARDNOTDEFINED, getLocaleMessage(OBJECTNOTDEFINED, REAR_SETBACK + blockName + AT_LEVEL_ZERO));
            }

            // Uncomment and use these if side yard validation is needed
//            if (setback.getSideYard1() == null) {
//                errors.put("side1yardNodeDefined", getLocaleMessage(OBJECTNOTDEFINED, "Side Setback 1 of block " + blockName + " at level zero"));
//            }
//            if (setback.getSideYard2() == null &&
//                !"Yes".equalsIgnoreCase(pl.getPlanInformation().getNocToAbutSideDesc())) {
//                errors.put("side2yardNodeDefined", getLocaleMessage(OBJECTNOTDEFINED, "Side Setback 2 of block " + blockName + " at level zero"));
//            }
        }
    }

    /**
     * Validates that setback heights are defined for levels above ground.
     * All existing yards must have height values specified.
     *
     * @param block The building block
     * @param setback The setback at upper level
     * @param errors Map to collect validation errors
     */
    private void validateHeightsAboveZeroLevel(Block block, SetBack setback, Map<String, String> errors) {
        String blockName = block.getName();
        String level = setback.getLevel().toString();

        if (setback.getFrontYard() != null && setback.getFrontYard().getHeight() == null) {
            errors.put(FRONTYARDNOTHEIGHT, getLocaleMessage(HEIGHTNOTDEFINED, FRONT_SETBACK_SPACE, blockName, level));
        }
        if (setback.getRearYard() != null && setback.getRearYard().getHeight() == null) {
            errors.put(REARYARDNOTHEIGHT, getLocaleMessage(HEIGHTNOTDEFINED, REAR_SETBACK_SPACE, blockName, level));
        }

        // Uncomment and use these if side yard height validation is needed
//        if (setback.getSideYard1() != null && setback.getSideYard1().getHeight() == null) {
//            errors.put("side1yardnotDefinedHeight", getLocaleMessage(HEIGHTNOTDEFINED, "Side Setback 1 ", blockName, level));
//        }
//        if (setback.getSideYard2() != null && setback.getSideYard2().getHeight() == null) {
//            errors.put("side2yardnotDefinedHeight", getLocaleMessage(HEIGHTNOTDEFINED, "Side Setback 2 ", blockName, level));
//        }
    }

    /**
     * Validates that setback heights at the topmost level match the building height.
     * Ensures consistency between setback definitions and actual building height.
     *
     * @param block The building block
     * @param setback The topmost setback level
     * @param buildingHeight The total building height
     * @param errors Map to collect validation errors
     */
    private void validateSetbackHeightsAgainstBuildingHeight(Block block, SetBack setback, BigDecimal buildingHeight, Map<String, String> errors) {
        String blockName = block.getName();
        String level = setback.getLevel().toString();
        String expectedHeight = buildingHeight.toString();

        if (setback.getFrontYard() != null && heightMismatch(setback.getFrontYard().getHeight(), buildingHeight)) {
            errors.put(FRONTYARDWRONGHEIGHT, getLocaleMessage(WRONGHEIGHTDEFINED, FRONT_SETBACK_SPACE, blockName, level, expectedHeight));
        }
        if (setback.getRearYard() != null && heightMismatch(setback.getRearYard().getHeight(), buildingHeight)) {
            errors.put(REARYARDWRONGHEIGHT, getLocaleMessage(WRONGHEIGHTDEFINED, REAR_SETBACK_SPACE, blockName, level, expectedHeight));
        }

        // Uncomment and use these if side yard height comparison is needed
//        if (setback.getSideYard1() != null && heightMismatch(setback.getSideYard1().getHeight(), buildingHeight)) {
//            errors.put("side1yardDefinedWrongHeight", getLocaleMessage(WRONGHEIGHTDEFINED, "Side Setback 1 ", blockName, level, expectedHeight));
//        }
//        if (setback.getSideYard2() != null && heightMismatch(setback.getSideYard2().getHeight(), buildingHeight)) {
//            errors.put("side2yardDefinedWrongHeight", getLocaleMessage(WRONGHEIGHTDEFINED, "Side Setback 2 ", blockName, level, expectedHeight));
//        }
    }

    /**
     * Checks if the current setback is the last (topmost) setback for the block.
     *
     * @param block The building block
     * @param index Current setback index (1-based)
     * @return true if this is the last setback
     */
    private boolean isLastSetback(Block block, int index) {
        return block.getSetBacks().size() == index;
    }

    /**
     * Compares two height values to check for mismatch.
     *
     * @param actual The actual height value
     * @param expected The expected height value
     * @return true if heights don't match
     */
    private boolean heightMismatch(BigDecimal actual, BigDecimal expected) {
        return actual != null && actual.compareTo(expected) != 0;
    }

    /**
     * Processes setback validations and calculations for the building plan.
     * Handles front yard processing and conditional rear yard processing based on plot characteristics.
     *
     * @param pl The building plan to process
     * @return The processed plan with setback calculations
     */

    @Override
    public Plan process(Plan pl) {
        validate(pl);
        Map<String, String> errors = new HashMap<>();

        BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();
        if (depthOfPlot != null && depthOfPlot.compareTo(BigDecimal.ZERO) > 0) {
            frontYardService.processFrontYard(pl);

                boolean rearRoadReservePresent = pl.getRoadReserveRear() != null && pl.getRoadReserveRear().compareTo(BigDecimal.ZERO) > 0;
                boolean largePlot = pl.getPlot().getArea().compareTo(TWO_HUNDRED) > 0;

                if (rearRoadReservePresent) {
                    for (Block block : pl.getBlocks()) {
                        for (SetBack setback : block.getSetBacks()) {
                            if (setback.getRearYard() == null) {
                                errors.put(REARYARDNOTDEFINED, getLocaleMessage(
                                        OBJECTNOTDEFINED,
                                        REAR_SETBACK + block.getName() + MANDATORY_REAR_SETBACK
                                ));
                            }
                        }
                    }
                    rearYardService.processRearYard(pl);
                } else if (largePlot) {
                    rearYardService.processRearYard(pl);
                }
            
        }

        // Uncomment if side yard processing is needed
//        BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();
//        if (widthOfPlot != null && widthOfPlot.compareTo(BigDecimal.ZERO) > 0) {
//            sideYardService.processSideYard(pl);
//        }

        if (!errors.isEmpty()) {
            pl.addErrors(errors);
        }

        return pl;
    }

    /**
     * Returns amendment dates for setback regulations.
     * Currently returns empty map as no amendments are tracked.
     *
     * @return Empty LinkedHashMap of amendment dates
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
