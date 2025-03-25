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
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Basement_Citya extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(Basement_Citya.class);
    private static final String RULE_46_6A = "46-6a";
    private static final String RULE_46_6C = "46-6c";
    public static final String BASEMENT_DESCRIPTION_ONE = "Height from the floor to the soffit of the roof slab or ceiling";
    public static final String BASEMENT_DESCRIPTION_TWO = "Minimum height of the ceiling of upper basement above ground level";
    
    /**
     * Validates the provided building plan.
     * Currently, this method does not perform any validation and simply returns the plan.
     *
     * @param pl The building plan to be validated.
     * @return The unchanged building plan.
     */
    @Override
    public Plan validate(Plan pl) {

        return pl;
    }
    
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;
    
    /**
     * Processes the basement-related validation and scrutiny for a given building plan.
     * It checks if the basement's height conditions comply with the permissible values
     * retrieved from the MDMS rules and records the scrutiny results.
     *
     * @param pl The building plan to be processed.
     * @return The processed plan with scrutiny details added.
     */
    @Override
    public Plan process(Plan pl) {

        // Initialize scrutiny details for basement verification
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Basement");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        Map<String, String> details = new HashMap<>();
        BigDecimal minLength = BigDecimal.ZERO;
        BigDecimal basementValue = BigDecimal.ZERO;
        BigDecimal basementValuetwo = BigDecimal.ZERO;
        BigDecimal basementValuethree = BigDecimal.ZERO;
        BigDecimal basementValuefour = BigDecimal.ZERO;

        // Check if the building plan has blocks
        if (pl.getBlocks() != null) {
            for (Block b : pl.getBlocks()) {
                if (b.getBuilding() != null && b.getBuilding().getFloors() != null
                        && !b.getBuilding().getFloors().isEmpty()) {

                    String occupancyName = null;
                    String feature = "Basement";

                    // Prepare parameters to fetch permissible values
                    Map<String, Object> params = new HashMap<>();
                    if (DxfFileConstants.A
                            .equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
                        occupancyName = "Residential";
                    }

                    params.put("feature", feature);
                    params.put("occupancy", occupancyName);

                    Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

                    // Define the keys for permissible values to be fetched
                    ArrayList<String> valueFromColumn = new ArrayList<>();
                    valueFromColumn.add("permissibleone");
                    valueFromColumn.add("permissibletwo");
                    valueFromColumn.add("permissiblethree");
                    valueFromColumn.add("permissiblefour");

                    List<Map<String, Object>> permissibleValue = new ArrayList<>();

                    try {
                        // Fetch permissible values from MDMS rules
                        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
                        LOG.info("permissibleValue" + permissibleValue);

                    } catch (NullPointerException e) {
                        LOG.error("Permissible Value for Basement not found : ", e);
                        return null;
                    }

                    // Extract permissible values if available
                    if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey("permissibleone")) {
                        basementValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("permissibleone").toString()));
                        basementValuetwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("permissibletwo").toString()));
                        basementValuethree = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("permissiblethree").toString()));
                        basementValuefour = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("permissiblefour").toString()));
                    }

                    // Iterate through each floor to check basement conditions
                    for (Floor f : b.getBuilding().getFloors()) {

                        // Check if it's a basement floor
                        if (f.getNumber() == -1) {

                            // Validate basement height from floor to ceiling
                            if (f.getHeightFromTheFloorToCeiling() != null
                                    && !f.getHeightFromTheFloorToCeiling().isEmpty()) {

                                minLength = f.getHeightFromTheFloorToCeiling().stream().reduce(BigDecimal::min).get();

                                if (minLength.compareTo(basementValuetwo) >= 0) {
                                    // Acceptable height condition
                                    details.put(RULE_NO, RULE_46_6A);
                                    details.put(DESCRIPTION, BASEMENT_DESCRIPTION_ONE);
                                    details.put(REQUIRED, ">=" + basementValue.toString());
                                    details.put(PROVIDED, minLength.toString());
                                    details.put(STATUS, Result.Accepted.getResultVal());
                                    scrutinyDetail.getDetail().add(details);
                                } else {
                                    // Non-compliant height condition
                                    details = new HashMap<>();
                                    details.put(RULE_NO, RULE_46_6A);
                                    details.put(DESCRIPTION, BASEMENT_DESCRIPTION_ONE);
                                    details.put(REQUIRED, ">=" + basementValue.toString());
                                    details.put(PROVIDED, minLength.toString());
                                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                                    scrutinyDetail.getDetail().add(details);
                                }
                            }

                            minLength = BigDecimal.ZERO;

                            // Validate height of the ceiling of upper basement
                            if (f.getHeightOfTheCeilingOfUpperBasement() != null
                                    && !f.getHeightOfTheCeilingOfUpperBasement().isEmpty()) {

                                minLength = f.getHeightOfTheCeilingOfUpperBasement().stream().reduce(BigDecimal::min).get();

                                if (minLength.compareTo(basementValuethree) >= 0
                                        && minLength.compareTo(basementValuefour) < 0) {
                                    // Acceptable ceiling height condition
                                    details = new HashMap<>();
                                    details.put(RULE_NO, RULE_46_6C);
                                    details.put(DESCRIPTION, BASEMENT_DESCRIPTION_TWO);
                                    details.put(REQUIRED, "Between " + basementValuethree.toString() + " to " + basementValuefour.toString());
                                    details.put(PROVIDED, minLength.toString());
                                    details.put(STATUS, Result.Accepted.getResultVal());
                                    scrutinyDetail.getDetail().add(details);
                                } else {
                                    // Non-compliant ceiling height condition
                                    details = new HashMap<>();
                                    details.put(RULE_NO, RULE_46_6C);
                                    details.put(DESCRIPTION, BASEMENT_DESCRIPTION_TWO);
                                    details.put(REQUIRED, "Between " + basementValuethree.toString() + " to " + basementValuefour.toString());
                                    details.put(PROVIDED, minLength.toString());
                                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                                    scrutinyDetail.getDetail().add(details);
                                }
                            }

                            // Add scrutiny details to the report output
                            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                        }
                    }
                }
            }
        }
        return pl;
    }

    
    /**
     * Retrieves the list of amendments applicable to the basement feature.
     * Currently, this method returns an empty map.
     *
     * @return A map containing amendment details.
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}
