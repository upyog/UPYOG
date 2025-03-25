/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InteriorOpenSpaceService_Citya extends FeatureProcess {

    // Logger for logging information and errors
    private static Logger LOG = LogManager.getLogger(InteriorOpenSpaceService_Citya.class);

    // Constants for rule identifiers and descriptions
    private static final String RULE_43A = "43A";
    private static final String RULE_43 = "43";
    public static final String INTERNALCOURTYARD_DESCRIPTION = "Internal Courtyard";
    public static final String VENTILATIONSHAFT_DESCRIPTION = "Ventilation Shaft";

    // Variables to store permissible values for interior open spaces
    public static BigDecimal minInteriorAreaValueOne = BigDecimal.ZERO;
    public static BigDecimal minInteriorAreaValueTwo = BigDecimal.ZERO;
    public static BigDecimal minInteriorWidthValueOne = BigDecimal.ZERO;
    public static BigDecimal minInteriorWidthValueTwo = BigDecimal.ZERO;
    public static BigDecimal minVentilationAreaValueOne = BigDecimal.ZERO;
    public static BigDecimal minVentilationAreaValueTwo = BigDecimal.ZERO;
    public static BigDecimal minVentilationWidthValueOne = BigDecimal.ZERO;
    public static BigDecimal minVentilationWidthValueTwo = BigDecimal.ZERO;

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
     * Processes the given plan to validate interior open spaces.
     * Fetches permissible values for interior courtyards and ventilation shafts and validates them against the plan details.
     *
     * @param pl The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */
    @Override
    public Plan process(Plan pl) {
        String occupancyName = null;
        String feature = "InteriorOpenSpaceService";

        // Determine the occupancy type for fetching permissible values
        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch permissible values for interior open spaces
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add("minInteriorAreaValueOne");
        valueFromColumn.add("minInteriorAreaValueTwo");
        valueFromColumn.add("minInteriorWidthValueOne");
        valueFromColumn.add("minInteriorWidthValueTwo");
        valueFromColumn.add("minVentilationAreaValueOne");
        valueFromColumn.add("minVentilationAreaValueTwo");
        valueFromColumn.add("minVentilationWidthValueOne");
        valueFromColumn.add("minVentilationWidthValueTwo");

        List<Map<String, Object>> permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey("minInteriorAreaValueOne")) {
            minInteriorAreaValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("minInteriorAreaValueOne").toString()));
            minInteriorAreaValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("minInteriorAreaValueTwo").toString()));
            minInteriorWidthValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("minInteriorWidthValueOne").toString()));
            minInteriorWidthValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("minInteriorWidthValueTwo").toString()));
            minVentilationAreaValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("minVentilationAreaValueOne").toString()));
            minVentilationAreaValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("minVentilationAreaValueTwo").toString()));
            minVentilationWidthValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("minVentilationWidthValueOne").toString()));
            minVentilationWidthValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("minVentilationWidthValueTwo").toString()));
        }

        // Iterate through all blocks in the plan
        for (Block b : pl.getBlocks()) {
            ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
            scrutinyDetail.setKey("Common_Interior Open Space");
            scrutinyDetail.addColumnHeading(1, RULE_NO);
            scrutinyDetail.addColumnHeading(2, DESCRIPTION);
            scrutinyDetail.addColumnHeading(3, REQUIRED);
            scrutinyDetail.addColumnHeading(4, PROVIDED);
            scrutinyDetail.addColumnHeading(5, STATUS);

            if (b.getBuilding() != null && b.getBuilding().getFloors() != null && !b.getBuilding().getFloors().isEmpty()) {
                for (Floor f : b.getBuilding().getFloors()) {
                    processVentilationShaft(pl, scrutinyDetail, f);
                    processInteriorCourtYard(pl, scrutinyDetail, f);
                }
            }
        }
        return pl;
    }

    /**
     * Processes and validates the interior courtyard for a given floor.
     *
     * @param pl The plan object.
     * @param scrutinyDetail The scrutiny detail object to update.
     * @param f The floor object to process.
     */
    private void processInteriorCourtYard(Plan pl, ScrutinyDetail scrutinyDetail, Floor f) {
        if (f.getInteriorOpenSpace() != null && f.getInteriorOpenSpace().getInnerCourtYard() != null
                && f.getInteriorOpenSpace().getInnerCourtYard().getMeasurements() != null
                && !f.getInteriorOpenSpace().getInnerCourtYard().getMeasurements().isEmpty()) {

            BigDecimal minInteriorCourtYardArea = f.getInteriorOpenSpace().getInnerCourtYard().getMeasurements()
                    .stream().map(Measurement::getArea).reduce(BigDecimal::min).get();
            BigDecimal minInteriorCourtYardWidth = f.getInteriorOpenSpace().getInnerCourtYard().getMeasurements()
                    .stream().map(Measurement::getWidth).reduce(BigDecimal::min).get();

            // Validate area
            if (minInteriorCourtYardArea.compareTo(minInteriorAreaValueOne) > 0) {
                Map<String, String> details = new HashMap<>();
                details.put(RULE_NO, RULE_43);
                details.put(DESCRIPTION, INTERNALCOURTYARD_DESCRIPTION);

                if (minInteriorCourtYardArea.compareTo(minInteriorAreaValueTwo) >= 0) {
                    details.put(REQUIRED, "Minimum area " + minInteriorAreaValueTwo.toString() + " Sq. M  ");
                    details.put(PROVIDED, "Area " + minInteriorCourtYardArea + " at floor " + f.getNumber());
                    details.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    details.put(REQUIRED, "Minimum area " + minInteriorAreaValueTwo.toString() + " Sq. M  ");
                    details.put(PROVIDED, "Area " + minInteriorCourtYardArea + " at floor " + f.getNumber());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }

            // Validate width
            if (minInteriorCourtYardWidth.compareTo(minInteriorWidthValueOne) > 0) {
                Map<String, String> details = new HashMap<>();
                details.put(RULE_NO, RULE_43A);
                details.put(DESCRIPTION, INTERNALCOURTYARD_DESCRIPTION);

                if (minInteriorCourtYardWidth.compareTo(minInteriorWidthValueTwo) >= 0) {
                    details.put(REQUIRED, "Minimum width " + minInteriorWidthValueTwo.toString() + " M ");
					details.put(PROVIDED, "Area  " + minInteriorCourtYardWidth + " at floor " + f.getNumber());
                    details.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    details.put(REQUIRED, "Minimum width " + minInteriorWidthValueTwo.toString() + " M ");
					details.put(PROVIDED, "Area  " + minInteriorCourtYardWidth + " at floor " + f.getNumber());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }
        }
    }

    /**
     * Processes and validates the ventilation shaft for a given floor.
     *
     * @param pl The plan object.
     * @param scrutinyDetail The scrutiny detail object to update.
     * @param f The floor object to process.
     */
    private void processVentilationShaft(Plan pl, ScrutinyDetail scrutinyDetail, Floor f) {
        if (f.getInteriorOpenSpace() != null && f.getInteriorOpenSpace().getVentilationShaft() != null
                && f.getInteriorOpenSpace().getVentilationShaft().getMeasurements() != null
                && !f.getInteriorOpenSpace().getVentilationShaft().getMeasurements().isEmpty()) {

            BigDecimal minVentilationShaftArea = f.getInteriorOpenSpace().getVentilationShaft().getMeasurements()
                    .stream().map(Measurement::getArea).reduce(BigDecimal::min).get();
            BigDecimal minVentilationShaftWidth = f.getInteriorOpenSpace().getVentilationShaft().getMeasurements()
                    .stream().map(Measurement::getWidth).reduce(BigDecimal::min).get();

            // Validate area
            if (minVentilationShaftArea.compareTo(minVentilationAreaValueOne) > 0) {
                Map<String, String> details = new HashMap<>();
                details.put(RULE_NO, RULE_43);
                details.put(DESCRIPTION, VENTILATIONSHAFT_DESCRIPTION);

                if (minVentilationShaftArea.compareTo(minVentilationAreaValueTwo) >= 0) {
                    details.put(REQUIRED, "Minimum area " + minVentilationAreaValueTwo.toString() + " Sq. M  ");
                    details.put(PROVIDED, "Area " + minVentilationShaftArea + " at floor " + f.getNumber());
                    details.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    details.put(REQUIRED, "Minimum area " + minVentilationAreaValueTwo.toString() + " Sq. M  ");
                    details.put(PROVIDED, "Area " + minVentilationShaftArea + " at floor " + f.getNumber());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }

            // Validate width
            if (minVentilationShaftWidth.compareTo(minVentilationWidthValueOne) > 0) {
                Map<String, String> details = new HashMap<>();
                details.put(RULE_NO, RULE_43A);
                details.put(DESCRIPTION, VENTILATIONSHAFT_DESCRIPTION);

                if (minVentilationShaftWidth.compareTo(minVentilationWidthValueTwo) >= 0) {
                    details.put(REQUIRED, "Minimum width " + minVentilationWidthValueTwo.toString() + " M ");
					details.put(PROVIDED, "Area  " + minVentilationShaftWidth + " at floor " + f.getNumber());
                    details.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    details.put(REQUIRED, "Minimum width " + minVentilationWidthValueTwo.toString() + " M ");
					details.put(PROVIDED, "Area  " + minVentilationShaftWidth + " at floor " + f.getNumber());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }
        }
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
