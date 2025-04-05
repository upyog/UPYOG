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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.SanityDetails;
import org.egov.common.entity.edcr.SanityHelper;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class Sanitation_Citya extends FeatureProcess {
    public static final String MSG_ERROR_MANDATORY = "msg.error.mandatory.object.not.defined";
    public static final String FEMALE = "Female ";
    public static final String MALE = "Male ";
    public static final String BLOCK = "Block ";
    public static final String SANITY_RULE_DESC = "Sanity facility for Occupancy ";
    public static final String NEWLINE = "\n";
    public static final String SANITATION = "Sanitation";
    public static final String BLOCK_U_S = "Block_";
    private static final String WITH = " with ";
    private static final String BLDG_PART_WATER_CLOSET = "Water Closet";
    private static final String BLDG_PART_SPECIAL_WATER_CLOSET = "Special Water Closet";
    private static final String BLDG_PART_URINAL = "Urinal";
    private static final String BLDG_PART_BATHROOM = "Bath Room";
    private static final String MALE_BATH_WITH_WC = BLDG_PART_BATHROOM + WITH + BLDG_PART_WATER_CLOSET;
    private static final String BLDG_PART_WASHBASIN = "Wash Basin";
    private static final String MINIMUM_SIDE_DIMENSION_VIOLATED = "Minimum Side Dimension of {0} M violated";
    private static final String MINIMUM_AREA_DIMENSION_VIOLATED = "Minimum Area of {0} M violated";
    private static final String DIMESION_DESC_KEY = "msg.sanity.dimension.desc";
    private static final Logger LOG = LogManager.getLogger(Sanitation_Citya.class);
    private static final String FEATURE_NAME = "Sanitary Detail";
    private static final String RULE_38_1 = "38-1";
    private static final String NOOFBEDS = "No Of Beds";
    public static final String RULE_55_12 = "55-12";
    public static final String RULE_40_A_4 = "40A-4";
    public static final String RULE_54_6 = "54-6";
    public static final BigDecimal MINAREAOFSPWC = BigDecimal.valueOf(2.625);
    public static final BigDecimal MINDIMENSIONOFSPWC = BigDecimal.valueOf(1.5);
    public static final String MINIMUM_AREA_SPWC = "2.625 M2";
    public static final String MINIMUM_DIMENSION_SPWC = "1.5 M";

   
    
	public static BigDecimal sanitationMinAreaofSPWC = BigDecimal.ZERO;
	public static BigDecimal sanitationMinDimensionofSPWC = BigDecimal.ZERO;
	public static BigDecimal sanitationMinatGroundFloor = BigDecimal.ZERO;
	public static BigDecimal sanitationFloorMultiplier = BigDecimal.ZERO;
    
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;  
    
    private Map<String, BigDecimal> fetchSanitationValues(Plan pl) {
        String occupancyName = null;
		String feature = MdmsFeatureConstants.SANITATION;
        Map<String, Object> params = new HashMap<>();
        
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.SANITATION_MIN_AREA_SPWC);
        valueFromColumn.add(EdcrRulesMdmsConstants.SANITATION_MIN_DIMENSION_SPWC);

		List<Map<String, Object>> permissibleValue = new ArrayList<>();
		
		permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
		LOG.info("permissibleValue" + permissibleValue);

        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.SANITATION_MIN_AREA_SPWC)) {
        	sanitationMinAreaofSPWC = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SANITATION_MIN_AREA_SPWC).toString()));
        	sanitationMinDimensionofSPWC = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SANITATION_MIN_DIMENSION_SPWC).toString()));
        	sanitationMinatGroundFloor = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SANITATION_MIN_AT_GROUND_FLOOR).toString()));
        	sanitationFloorMultiplier = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SANITATION_FLOOR_MULTIPLIER).toString()));
		}
        
        // Return the values as a map
        Map<String, BigDecimal> sanitationValues = new HashMap<>();
        sanitationValues.put("sanitationMinAreaofSPWC", sanitationMinAreaofSPWC);
        sanitationValues.put("sanitationMinDimensionofSPWC", sanitationMinDimensionofSPWC);
        sanitationValues.put("sanitationMinatGroundFloor", sanitationMinatGroundFloor);
        sanitationValues.put("sanitationFloorMultiplier", sanitationFloorMultiplier);
        return sanitationValues;
    }
    
    @Override
    public Plan validate(Plan pl) {

        for (Block b : pl.getBlocks()) {
            if (!b.getCompletelyExisting()) {

                int totalSpecialWC = 0;
                int totalWashBasins = 0;
                for (Floor f : b.getBuilding().getFloors()) {
                    totalSpecialWC += f.getSpecialWaterClosets().size();
                    totalWashBasins += f.getWashBasins().size();
                }
                b.getSanityDetails().setTotalSPWC(totalSpecialWC);
                b.getSanityDetails().setTotalwashBasins(totalWashBasins);

                /*
                 * If block is small plot and floors above ground less than or equal to three and occupancy type of entire block
                 * is either Residential or Commercial then sanitation validation not require.
                 */
                List<Occupancy> occupancies = b.getBuilding().getTotalArea();
                SanityDetails sanityDetails = b.getSanityDetails();
                validateDimensions(pl, b, sanityDetails);
                for (Occupancy occupancy : occupancies) {
                    OccupancyHelperDetail o = occupancy.getTypeHelper().getSubtype() != null
                            ? occupancy.getTypeHelper().getSubtype()
                            : occupancy.getTypeHelper().getType();
                  if(o!=null){          
                    switch (o.getCode()) {
                    case DxfFileConstants.A:
                    case DxfFileConstants.A_SR:
                    case DxfFileConstants.A_AF:
                        if (sanityDetails.getTotalSPWC() == 0)
                            pl.addError(BLDG_PART_SPECIAL_WATER_CLOSET, getLocaleMessage(MSG_ERROR_MANDATORY,
                                    FEATURE_NAME, BLDG_PART_SPECIAL_WATER_CLOSET, b.getNumber()));
                        break;
                    case DxfFileConstants.A_HE:
                        commonSanitationValidations(pl, b, sanityDetails, o);
                        validateBathRoom(pl, b, sanityDetails);
                        break;
                    case DxfFileConstants.B:
                    case DxfFileConstants.B2:
                    case DxfFileConstants.B_HEI:
                        commonSanitationValidations(pl, b, sanityDetails, o);
                        break;
                    case DxfFileConstants.C_MIP:
                        commonSanitationValidations(pl, b, sanityDetails, o);
                        if (pl.getPlanInformation().getNoOfBeds() == null)
                            pl.addError(NOOFBEDS,
                                    getLocaleMessage(MSG_ERROR_MANDATORY, FEATURE_NAME, NOOFBEDS, b.getNumber()));
                        break;
                    case DxfFileConstants.C_MOP:
                    case DxfFileConstants.C_MA:
                        commonSanitationValidations(pl, b, sanityDetails, o);
                        break;
                    case DxfFileConstants.D:
                    case DxfFileConstants.D_AW:
                    case DxfFileConstants.D_BT:
                        commonSanitationValidations(pl, b, sanityDetails, o);
                        break;
                    case DxfFileConstants.E:
                    case DxfFileConstants.F:
                    case DxfFileConstants.F_K:
                        commonSanitationValidations(pl, b, sanityDetails, o);
                        break;
                    case DxfFileConstants.F_H:
                        commonSanitationValidations(pl, b, sanityDetails, o);
                        validateBathRoom(pl, b, sanityDetails);
                        break;
                    case DxfFileConstants.G:
                    case DxfFileConstants.G_SI:
                    case DxfFileConstants.H:
                    case DxfFileConstants.I1:
                    case DxfFileConstants.I2:
                        if (sanityDetails.getMaleWaterClosets().isEmpty()
                                && sanityDetails.getFemaleWaterClosets().isEmpty()) {
                            pl.addError(BLDG_PART_WATER_CLOSET, getLocaleMessage(MSG_ERROR_MANDATORY, FEATURE_NAME,
                                    BLDG_PART_WATER_CLOSET, b.getNumber()));
                        }

                        if (sanityDetails.getUrinals().isEmpty()) {
                            pl.addError(BLDG_PART_URINAL, getLocaleMessage(MSG_ERROR_MANDATORY, FEATURE_NAME,
                                    BLDG_PART_URINAL, b.getNumber()));
                        }
                        break;
                    }
                  }
                }
            }
        }
        return pl;
    }

    private void validateBathRoom(Plan pl, Block b, SanityDetails sanityDetails) {
        if (sanityDetails.getMaleBathRooms().isEmpty() && sanityDetails.getFemaleBathRooms().isEmpty()
                && sanityDetails.getMaleRoomsWithWaterCloset().isEmpty()
                && sanityDetails.getFemaleRoomsWithWaterCloset().isEmpty()) {
            pl.addError(BLDG_PART_BATHROOM,
                    getLocaleMessage(MSG_ERROR_MANDATORY, FEATURE_NAME, BLDG_PART_BATHROOM, b.getNumber()));
        }
    }

    private void commonSanitationValidations(Plan pl, Block b, SanityDetails sanityDetails, OccupancyHelperDetail type) {
        if (sanityDetails.getMaleWaterClosets().isEmpty() && sanityDetails.getFemaleWaterClosets().isEmpty()
                && sanityDetails.getMaleRoomsWithWaterCloset().isEmpty()
                && sanityDetails.getFemaleRoomsWithWaterCloset().isEmpty()) {
            pl.addError(BLDG_PART_WATER_CLOSET,
                    getLocaleMessage(MSG_ERROR_MANDATORY, FEATURE_NAME, BLDG_PART_WATER_CLOSET, b.getNumber()));
        }

        if (sanityDetails.getUrinals().isEmpty()) {
            pl.addError(BLDG_PART_URINAL,
                    getLocaleMessage(MSG_ERROR_MANDATORY, FEATURE_NAME, BLDG_PART_URINAL, b.getNumber()));
        }

        if (!DxfFileConstants.F.equals(type.getCode()) && !DxfFileConstants.F_K.equals(type.getCode())
                && !DxfFileConstants.E.equals(type.getCode())
                && sanityDetails.getTotalwashBasins() == 0) {
            pl.addError(BLDG_PART_WASHBASIN,
                    getLocaleMessage(MSG_ERROR_MANDATORY, FEATURE_NAME, BLDG_PART_WASHBASIN, b.getNumber()));
        }

        if (sanityDetails.getTotalSPWC() == 0)
            pl.addError(BLDG_PART_SPECIAL_WATER_CLOSET,
                    getLocaleMessage(MSG_ERROR_MANDATORY, FEATURE_NAME, BLDG_PART_SPECIAL_WATER_CLOSET, b.getNumber()));

    }

    private void validateDimensions(Plan pl, Block b, SanityDetails sanityDetails) {
        if (!sanityDetails.getUrinals().isEmpty()) {
            int count = 0;
            for (Measurement m : sanityDetails.getUrinals()) {
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0) {
                    count++;
                }
            }
            if (count > 0) {
                pl.addError("Sanity_" + BLDG_PART_URINAL + b.getNumber(), count + " number of " + BLDG_PART_URINAL
                        + " polyline not having only 4 points in block" + b.getNumber());

            }
        }
        List<Measurement> wcList = new ArrayList<>();
        wcList.addAll(sanityDetails.getMaleWaterClosets());
        wcList.addAll(sanityDetails.getFemaleWaterClosets());
        wcList.addAll(sanityDetails.getCommonWaterClosets());
        if (!wcList.isEmpty()) {
            int count = 0;
            for (Measurement m : wcList) {
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0) {
                    count++;
                }
            }
            if (count > 0) {
                pl.addError("Sanity_" + BLDG_PART_WATER_CLOSET + b.getNumber(), count + " number of "
                        + BLDG_PART_WATER_CLOSET + " polyline not having only 4 points in block" + b.getNumber());

            }
        }

        List<Measurement> bath = new ArrayList<>();
        bath.addAll(sanityDetails.getMaleBathRooms());
        bath.addAll(sanityDetails.getFemaleBathRooms());
        bath.addAll(sanityDetails.getCommonBathRooms());

        List<Measurement> wcrList = new ArrayList<>();
        wcrList.addAll(sanityDetails.getMaleRoomsWithWaterCloset());
        wcrList.addAll(sanityDetails.getFemaleRoomsWithWaterCloset());
        wcrList.addAll(sanityDetails.getCommonRoomsWithWaterCloset());

        if (!bath.isEmpty()) {
            int count = 0;
            for (Measurement m : bath) {
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0) {
                    count++;
                }
            }
            if (count > 0) {
                pl.addError("Sanity_" + BLDG_PART_BATHROOM + b.getNumber(), count + " number of " + BLDG_PART_BATHROOM
                        + " polyline not having only 4 points in block" + b.getNumber());

            }
        }

        if (!wcrList.isEmpty()) {
            int count = 0;
            for (Measurement m : wcrList) {
                if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0) {
                    count++;
                }
            }

            if (count > 0) {
                pl.addError("Sanity_" + MALE_BATH_WITH_WC + b.getNumber(), count + " number of " + MALE_BATH_WITH_WC
                        + " polyline not having only 4 points in block" + b.getNumber());
            }
        }
    }

    @Override
    public Plan process(Plan pl) {
        verifyDimesions(pl);
        checkCount(pl);
        return pl;
    }

    private Plan verifyDimesions(Plan pl) {
        validate(pl);
        return pl;
    }

    private ScrutinyDetail getNewScrutinyDetail(String key) {
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);
        scrutinyDetail.setKey(key);
        return scrutinyDetail;
    }

    private void checkCount(Plan pl) {
    	
    	
        // Fetch sanitation values
        Map<String, BigDecimal> solarValues = fetchSanitationValues(pl);
        sanitationMinAreaofSPWC = solarValues.get("sanitationMinAreaofSPWC");
        sanitationMinDimensionofSPWC = solarValues.get("sanitationMinDimensionofSPWC"); 
        sanitationMinatGroundFloor = solarValues.get("sanitationMinatGroundFloor");
        sanitationFloorMultiplier = solarValues.get("sanitationFloorMultiplier"); 

        Boolean allStatus = true;
        Boolean accepted = true;
       
        for (Block b : pl.getBlocks()) {
            if (!b.getCompletelyExisting()) {

                LOG.info("Starting  Sanitation of ....." + b.getNumber());
                /*
                 * If block is small plot and floors above ground less than or equal to three and occupancy type of entire block
                 * is either Residential or Commercial then sanitation process not require.
                 */

                ScrutinyDetail scrutinyDetail = getNewScrutinyDetail(BLOCK_U_S + b.getNumber() + "_" + SANITATION);
                SanityHelper helper = new SanityHelper();
                Map<Integer, Integer> requiredSpWcMap = new ConcurrentHashMap<>();
                Map<Integer, Integer> providedSpWcMap = new ConcurrentHashMap<>();
                Map<Integer, Integer> failedAreaSpWcMap = new ConcurrentHashMap<>();
                Map<Integer, Integer> failedDimensionSpWcMap = new ConcurrentHashMap<>();
                for (Occupancy type : b.getBuilding().getTotalArea()) {
                    double carpetArea = 0d;
                    if (type.getCarpetArea() != null && type.getCarpetArea().doubleValue() > 0) {
                        carpetArea = type.getCarpetArea().doubleValue();
                    } else {
                        return ;
                    }
                    LOG.info(type.getType() + " area" + carpetArea);

                    OccupancyHelperDetail o = type.getTypeHelper().getSubtype() != null
                            ? type.getTypeHelper().getSubtype()
                            : type.getTypeHelper().getType();
                    switch (o.getCode()) {

                    case DxfFileConstants.A:
                    case DxfFileConstants.A_AF:
                        if (b.getResidentialBuilding())
                            accepted = processSpecialWaterClosetForResidential(b, helper, scrutinyDetail,
                                    requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap, failedDimensionSpWcMap, pl);
                        break;
                    case DxfFileConstants.A_SR:
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        break;
                    case DxfFileConstants.A_HE:
                        helper.maleWc += carpetArea * 2 / (4.75 * 3 * 10);
                        helper.femaleWc += carpetArea / (4.75 * 3 * 8);
                        helper.urinal += carpetArea * 2 / (4.75 * 3 * 25);
                        helper.maleWash += carpetArea * 2 / (4.75 * 3 * 10);
                        helper.femaleWash += carpetArea / (4.75 * 3 * 10);
                        helper.maleBath += carpetArea * 2 / (4.75 * 3 * 10);
                        helper.femaleBath += carpetArea / (4.75 * 3 * 10);
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        helper.ruleNo.add(RULE_54_6);
                        break;
                    case DxfFileConstants.B:
                    case DxfFileConstants.B2:
                    case DxfFileConstants.B_HEI:
                        helper.maleWc += carpetArea * 2 / (4.75 * 3 * 40);
                        helper.femaleWc += carpetArea / (4.75 * 3 * 25);
                        helper.urinal += carpetArea * 2 / (4.75 * 3 * 50);
                        helper.maleWash += carpetArea * 2 / (4.75 * 3 * 40);
                        helper.femaleWash += carpetArea / (4.75 * 3 * 40);
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        helper.ruleNo.add(RULE_54_6);
                        break;
                    case DxfFileConstants.C_MIP:
                        if (pl.getPlanInformation().getNoOfBeds() == null) {
                            break;
                        }
                        double noofBeds = pl.getPlanInformation().getNoOfBeds().doubleValue();
                        if (pl.getPlanInformation().getNoOfBeds() != null)
                            helper.maleWc += noofBeds / 8;
                        helper.femaleWc += noofBeds / 8;
                        helper.maleWash += 2 + (noofBeds - 30) / 30;
                        helper.femaleWash += 2 + (noofBeds - 30) / 30;
                        helper.maleBath += noofBeds / 8;
                        helper.femaleBath += noofBeds / 8;
                        helper.abultionTap += helper.maleWc + helper.femaleWc + carpetArea / (4.75 * 3);
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        helper.ruleNo.add(RULE_55_12);
                        break;

                    case DxfFileConstants.C_MOP:
                        helper.maleWc += carpetArea * 2 / (4.75 * 3 * 100);
                        helper.femaleWc += carpetArea / (4.75 * 3 * 8);
                        helper.urinal += carpetArea * 2 / (4.75 * 3 * 50);
                        helper.maleWash += carpetArea * 2 / (4.75 * 100);
                        helper.femaleWash += carpetArea / (4.75 * 100);
                        // helper.maleBath = carpetArea * 2 / (4.75 * 3 * 10);
                        // helper.femaleBath = carpetArea / (4.75 * 3 * 10);
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        helper.ruleNo.add(RULE_55_12);
                        break;

                    case DxfFileConstants.C_MA:
                        helper.ruleNo.add(RULE_55_12);
                        helper.maleWc += carpetArea * 2 / (4.75 * 3 * 25);
                        helper.femaleWc += carpetArea / (4.75 * 3 * 15);

                        helper.maleWash += carpetArea * 2 / (4.75 * 3 * 25);
                        helper.femaleWash += carpetArea * 2 / (4.75 * 3 * 25);
                        Double noOfPersons = carpetArea * 2 / (4.75 * 3);
                        BigDecimal noOfPersonsBig = BigDecimal.valueOf(noOfPersons).divide(BigDecimal.ONE,
                                RoundingMode.HALF_UP);
                        int noofPerson = noOfPersonsBig.intValue();

                        if (noofPerson >= 7 && noofPerson <= 20) {
                            helper.urinal += 1d;
                        } else if (noofPerson <= 45) {
                            helper.urinal += 2d;
                        } else if (noofPerson <= 70) {
                            helper.urinal += 3d;
                        } else if (noofPerson <= 100) {
                            helper.urinal += 4d;
                        } else if (noofPerson <= 200) {
                            helper.urinal += 4d + noofPerson * 0.3d;
                        } else if (noofPerson > 200) {
                            helper.urinal += 4d + noofPerson * 0.55d;
                        }

                        helper.abultionTap += helper.maleWc + helper.femaleWc + carpetArea / (4.75 * 50);
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        break;
                    case DxfFileConstants.D:
                    case DxfFileConstants.D_AW:
                        helper.maleWc += carpetArea * 2 / (3 * 200);
                        helper.femaleWc += carpetArea / (3 * 100);
                        helper.urinal += carpetArea * 2 / (3 * 50);
                        helper.maleWash += carpetArea * 2 / (3 * 200);
                        helper.femaleWash += carpetArea / (2 * 3 * 200);
                        // helper.maleBath += carpetArea * 2 / (4.75 * 3 * 10);
                        // helper.femaleBath += carpetArea / (4.75 * 3 * 10);
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        helper.ruleNo.add(RULE_55_12);
                        break;
                    case DxfFileConstants.D_BT:
                        helper.ruleNo.add(RULE_55_12);
                        if (carpetArea <= 1000) {
                            helper.maleWc += 4d;
                        } else {
                            helper.maleWc += 4d + (carpetArea - 1000);
                        }

                        if (carpetArea <= 1000) {
                            helper.urinal += 6d;
                        } else {
                            helper.urinal += 6d + (carpetArea - 1000) / 6;
                        }
                        helper.maleWc += carpetArea * 2 / (3 * 200);
                        helper.femaleWc += carpetArea / (3 * 100);
                        helper.urinal += carpetArea * 2 / (3 * 50);
                        helper.maleWash += 4d;
                        helper.femaleWash += 4d;
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        // helper.maleBath += carpetArea * 2 / (4.75 * 3 * 10);
                        // helper.femaleBath += carpetArea / (4.75 * 3 * 10);
                        helper.ruleNo.add(RULE_54_6);
                        break;
                    case DxfFileConstants.E:
                    case DxfFileConstants.F:
                    case DxfFileConstants.F_K:
                        helper.maleWc += carpetArea * 2 / (4.75 * 3 * 25);
                        helper.femaleWc += carpetArea / (4.75 * 3 * 15);
                        helper.urinal += carpetArea * 2 / (4.75 * 3 * 25);

                        helper.ruleNo.add("56(3C)");
                        helper.ruleDescription = SANITY_RULE_DESC + o.getCode();
                        if ((o.equals(DxfFileConstants.F) || o.equals(DxfFileConstants.F_K)))
                            processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                    failedDimensionSpWcMap, pl);

                        break;
                    case DxfFileConstants.H:
                        helper.maleWc += carpetArea * 2 / (3 * 30 * 50);
                        helper.femaleWc += carpetArea / (3 * 30 * 25);
                        helper.urinal += carpetArea * 2 / (3 * 30 * 100);
                        helper.ruleNo.add("58(6)");
                        break;
                    case DxfFileConstants.F_H:
                        helper.maleWc += carpetArea * 2 / (4.75 * 3 * 25);
                        helper.femaleWc += carpetArea / (4.75 * 3 * 15);
                        helper.urinal += carpetArea * 2 / (4.75 * 3 * 25);
                        // preferable one on each floor to be implemented
                        helper.maleWash += carpetArea * 2 / (4.75 * 3 * 100);
                        helper.femaleWash += carpetArea / (4.75 * 3 * 100);
                        helper.commonBath += carpetArea * 2 / (4.75 * 100);
                        processSpecialWaterCloset(b, requiredSpWcMap, providedSpWcMap, failedAreaSpWcMap,
                                failedDimensionSpWcMap, pl);
                        // helper.femaleBath += carpetArea / (4.75 * 3 * 10);
                        helper.ruleNo.add(RULE_54_6);
                        break;
                    case DxfFileConstants.G:
                    case DxfFileConstants.G_SI:
                        helper.maleWc += carpetArea * 2 / (4.75 * 3 * 25);
                        helper.femaleWc += carpetArea / (4.75 * 3 * 15);
                        helper.urinal += carpetArea * 2 / (4.75 * 3 * 25);
                        // preferable one on each floor to be implemented
                        helper.ruleNo.add("57(13)");
                        // accepted = processSanity(pl, b, carpetArea, helper,
                        // scrutinyDetail, type);
                        break;
                    case DxfFileConstants.I1:
                    case DxfFileConstants.I2:
                        double floorArea = carpetArea + (carpetArea * 25 / 100);
                        Double maleOccupant = floorArea * 2 / (3 * 30);
                        Double femaleOccupant = floorArea / (3 * 30);
                        if (maleOccupant.intValue() <= 50) {
                            helper.maleWc += 1d;
                        } else {
                            helper.maleWc += 1 + (maleOccupant - 50) / 70;
                        }

                        if (femaleOccupant.intValue() <= 50) {
                            helper.femaleWc += 2d;
                        } else {
                            helper.femaleWc += 2 + (maleOccupant - 50) / 70;
                        }
                        helper.maleWash += floorArea / (30 * 50);
                        double noOfPerson = floorArea * 2 / (3 * 30);
                        helper.urinal += noOfPerson / 100;
                        helper.ruleNo.add("59(7)");
                        // accepted = processSanity(pl, b, floorArea, helper,
                        // scrutinyDetail, type);
                        break;

                    }
                    if (!accepted) {
                        allStatus = false;
                    }

                }
                for (Map.Entry<Integer, Integer> req : requiredSpWcMap.entrySet()) {
                    helper.requiredSpecialWc += req.getValue();
                }
                for (Map.Entry<Integer, Integer> pro : providedSpWcMap.entrySet()) {
                    helper.providedSpecialWc += pro.getValue();
                }
                for (Map.Entry<Integer, Integer> pro : failedAreaSpWcMap.entrySet()) {
                    helper.failedAreaSpecialWc += pro.getValue();
                }
                for (Map.Entry<Integer, Integer> pro : failedDimensionSpWcMap.entrySet()) {
                    helper.failedDimensionSpecialWc += pro.getValue();
                }


                if (helper.requiredSpecialWc > 0) {
                    Set<String> ruleNo = new HashSet<>();
                    ruleNo.add(RULE_40_A_4);
                    if (helper.providedSpecialWc < helper.requiredSpecialWc) {
                        addReportDetail(ruleNo,
                                BLDG_PART_SPECIAL_WATER_CLOSET
                                        + " - Minimum one at Ground Floor + Minimum " + sanitationMinatGroundFloor.toString() + " at every floors in multiples of " + sanitationFloorMultiplier.toString() + ", (GF, 3rd, 6th etc)",
                                String.valueOf(helper.requiredSpecialWc.intValue()),
                                String.valueOf(helper.providedSpecialWc.intValue()), Result.Not_Accepted.getResultVal(),
                                scrutinyDetail);
                    } else {
                        addReportDetail(ruleNo,
                                BLDG_PART_SPECIAL_WATER_CLOSET
                                        + " - Minimum one at Ground Floor + Minimum " + sanitationMinatGroundFloor.toString() + " at every floors in multiples of " + sanitationFloorMultiplier.toString() + ", (GF, 3rd, 6th etc)",
                                String.valueOf(helper.requiredSpecialWc.intValue()),
                                String.valueOf(helper.providedSpecialWc.intValue()), Result.Accepted.getResultVal(),
                                scrutinyDetail);
                    }
                    if (helper.failedAreaSpecialWc > 0 && helper.failedAreaSpecialWc <= helper.requiredSpecialWc) {
                        addReportDetail(ruleNo, BLDG_PART_SPECIAL_WATER_CLOSET + " - Minimum Area", MINIMUM_AREA_SPWC,
                                String.valueOf(helper.failedAreaSpecialWc.intValue()) + " not having area " + sanitationMinAreaofSPWC.toString() + " M2",
                                Result.Not_Accepted.getResultVal(), scrutinyDetail);
                    } else {
                        addReportDetail(ruleNo, BLDG_PART_SPECIAL_WATER_CLOSET + " - Minimum Area", MINIMUM_AREA_SPWC,
                                String.valueOf(
                                        helper.providedSpecialWc.intValue() - helper.failedAreaSpecialWc.intValue())
                                        + " having area " + sanitationMinAreaofSPWC.toString() + " M2",
                                Result.Accepted.getResultVal(), scrutinyDetail);
                    }

                    if (helper.failedDimensionSpecialWc > 0
                            && helper.failedDimensionSpecialWc <= helper.requiredSpecialWc) {
                        addReportDetail(ruleNo, BLDG_PART_SPECIAL_WATER_CLOSET + " - Minimum Dimension",
                                MINIMUM_DIMENSION_SPWC,
                                String.valueOf(helper.failedDimensionSpecialWc.intValue())
                                        + " not having dimension " + sanitationMinDimensionofSPWC.toString() + "M",
                                Result.Not_Accepted.getResultVal(), scrutinyDetail);
                    } else {
                        addReportDetail(ruleNo, BLDG_PART_SPECIAL_WATER_CLOSET + " - Minimum Dimension",
                                MINIMUM_DIMENSION_SPWC,
                                String.valueOf(helper.providedSpecialWc.intValue()
                                        - helper.failedDimensionSpecialWc.intValue()) + " having dimension " + sanitationMinDimensionofSPWC.toString() + "M",
                                Result.Accepted.getResultVal(), scrutinyDetail);
                    }

                }
                accepted = processSanity(pl, b, helper, scrutinyDetail);

                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                LOG.info("Keys of the Sanitation Message ....." + scrutinyDetail.getKey() + "   "
                        + scrutinyDetail.getDetail().size());
            }
        }
    }

    private void processSpecialWaterCloset(Block block, Map<Integer, Integer> requiredSpWcMap,
            Map<Integer, Integer> providedSpWcMap, Map<Integer, Integer> failedAreaSpWcMap,
            Map<Integer, Integer> failedDimensionSpWcMap, Plan pl) {
        for (Floor f : block.getBuilding().getFloors()) {
            if (f.getNumber().intValue() < 0)
                continue;
            if (!f.getTerrace() && f.getNumber() % 3 == 0) {
                if (requiredSpWcMap.containsKey(f.getNumber()))
                    continue;
                else
                    requiredSpWcMap.put(f.getNumber(), 1);
                if (f.getSpecialWaterClosets().isEmpty()) {
                    // not defined
                } else {
                    if (providedSpWcMap.containsKey(f.getNumber()))
                        continue;
                    else
                        providedSpWcMap.put(f.getNumber(), 1);
                }

                validateDimensionOfSPWC(f.getSpecialWaterClosets(), f.getNumber(), failedAreaSpWcMap,
                        failedDimensionSpWcMap, providedSpWcMap, pl);
            }
        }
    }

    private Boolean processSpecialWaterClosetForResidential(Block block, SanityHelper helper, ScrutinyDetail detail,
            Map<Integer, Integer> requiredSpWcMap, Map<Integer, Integer> providedSpWcMap,
            Map<Integer, Integer> failedAreaSpWcMap, Map<Integer, Integer> failedDimensionSpWcMap, Plan pl) {
        boolean notFound = false;
        StringBuilder expectedResult = new StringBuilder();
        StringBuilder actualResult = new StringBuilder();
        expectedResult.append(" Minimum one at Ground Floor");
        int required = 0;
        int provided = 0;
        for (Floor f : block.getBuilding().getFloors()) {
            if (f.getNumber().intValue() < 0)
                continue;
            if (f.getNumber() == 0) {
                required++;
                provided++;
                if (f.getSpecialWaterClosets().isEmpty()) {
                    notFound = true;
                }

                validateDimensionOfSPWC(f.getSpecialWaterClosets(), f.getNumber(), failedAreaSpWcMap,
                        failedDimensionSpWcMap, providedSpWcMap, pl);

            }
        }
        Set<String> ruleNo = new HashSet<>();
        ruleNo.add(RULE_40_A_4);
        if (notFound) {
            actualResult.append("Not Found");
            addReportDetail(ruleNo, BLDG_PART_SPECIAL_WATER_CLOSET + " - Minimum one at Ground Floor",
                    String.valueOf(required), String.valueOf(provided), Result.Not_Accepted.getResultVal(), detail);
        } else {
            actualResult.append("Found");
            addReportDetail(ruleNo, BLDG_PART_SPECIAL_WATER_CLOSET + " - Minimum one at Ground Floor",
                    String.valueOf(required), String.valueOf(provided), Result.Accepted.getResultVal(), detail);
        }

        return !notFound;
    }

    private Boolean processSanity(Plan pl, Block b, SanityHelper helper, ScrutinyDetail detail) {

        // int specialWC = sanityDetails.getTotalSpecialWC().size();

        Boolean accepted = true;
        String description = "";
        String expected = "";
        String actual = "";
        SanityDetails sanityDetails = b.getSanityDetails();

        if (helper.maleWc > 0 || helper.femaleWc > 0) {

            int maleWcActual = sanityDetails.getMaleWaterClosets().size()
                    + sanityDetails.getMaleRoomsWithWaterCloset().size();
            int femaleWcActual = sanityDetails.getFemaleWaterClosets().size()
                    + sanityDetails.getFemaleRoomsWithWaterCloset().size();
            int commonWcActual = sanityDetails.getCommonWaterClosets().size()
                    + sanityDetails.getCommonRoomsWithWaterCloset().size();
            Double specialWC = helper.providedSpecialWc;

            Double totalWCActual = Math.ceil(maleWcActual + femaleWcActual + commonWcActual + specialWC);
            Double totalWCExpected = Math.ceil(helper.maleWc + helper.femaleWc);
            if (totalWCExpected >= 0) {
                List<Measurement> wcList = new ArrayList<>();
                wcList.addAll(sanityDetails.getMaleWaterClosets());
                wcList.addAll(sanityDetails.getFemaleWaterClosets());
                wcList.addAll(sanityDetails.getCommonWaterClosets());
                checkDimension(totalWCExpected.intValue(), detail, wcList, 1d, 1.1d, BLDG_PART_WATER_CLOSET,
                        DIMESION_DESC_KEY, RULE_38_1);

                expected = "" + totalWCExpected.intValue();
                actual = "" + totalWCActual.intValue();
                description = BLDG_PART_WATER_CLOSET + " - Count";
                if (totalWCExpected.intValue() > totalWCActual.intValue()) {
                    addReportDetail(helper.ruleNo, description, expected, actual, Result.Not_Accepted.getResultVal(),
                            detail);
                } else {
                    addReportDetail(helper.ruleNo, description, expected, actual, Result.Accepted.getResultVal(),
                            detail);

                }
            }
        }
        if (helper.urinal > 0) {
            helper.urinal = Math.ceil(helper.urinal);
            description = BLDG_PART_URINAL + " - Count";
            Integer urinalActual = sanityDetails.getUrinals().size();
            expected = "" + helper.urinal.intValue();
            actual = "" + urinalActual.intValue();
            if (helper.urinal.intValue() >= 0) {
                checkDimension(helper.urinal.intValue(), detail, sanityDetails.getUrinals(), 0.6d, 0.42d,
                        BLDG_PART_URINAL, DIMESION_DESC_KEY, RULE_38_1);
                if (helper.urinal.intValue() > urinalActual.intValue()) {
                    addReportDetail(helper.ruleNo, description, expected, actual, Result.Not_Accepted.getResultVal(),
                            detail);
                } else {
                    addReportDetail(helper.ruleNo, description, expected, actual, Result.Accepted.getResultVal(),
                            detail);
                }
            }
        }

        if (helper.maleWash > 0 || helper.femaleWash > 0) {

            int actualWash = 0;
            for (Floor f : b.getBuilding().getFloors()) {
                actualWash += f.getWashBasins().size();
            }
            description = BLDG_PART_WASHBASIN + " - Count";
            Double totalWashExpected = Math.ceil(helper.maleWash + helper.femaleWash);
            expected = "" + totalWashExpected.intValue();
            actual = "" + actualWash;
            if (totalWashExpected.intValue() >= 0) {
                if (totalWashExpected.intValue() > actualWash) {
                    addReportDetail(helper.ruleNo, description, expected, actual, Result.Not_Accepted.getResultVal(),
                            detail);
                } else {
                    addReportDetail(helper.ruleNo, description, expected, actual, Result.Accepted.getResultVal(),
                            detail);
                }
            }
        }

        if (helper.maleBath > 0 || helper.femaleBath > 0) {
            description = BLDG_PART_BATHROOM + " - Count";
            int maleBathActual = sanityDetails.getMaleBathRooms().size()
                    + sanityDetails.getMaleRoomsWithWaterCloset().size();
            int femaleBathActual = sanityDetails.getFemaleBathRooms().size()
                    + sanityDetails.getFemaleRoomsWithWaterCloset().size();
            int commomBathActual = sanityDetails.getCommonBathRooms().size()
                    + sanityDetails.getCommonRoomsWithWaterCloset().size();
            int totalActualBath = maleBathActual + femaleBathActual + commomBathActual;
            Double totalBathExpected = Math.ceil(helper.maleBath + helper.femaleBath);

            expected = "" + totalBathExpected.intValue();
            actual = "" + totalActualBath;
            List<Measurement> wcList = new ArrayList<>();
            wcList.addAll(sanityDetails.getMaleBathRooms());
            wcList.addAll(sanityDetails.getFemaleBathRooms());
            wcList.addAll(sanityDetails.getCommonBathRooms());

            List<Measurement> wcrList = new ArrayList<>();
            wcrList.addAll(sanityDetails.getMaleRoomsWithWaterCloset());
            wcrList.addAll(sanityDetails.getFemaleRoomsWithWaterCloset());
            wcrList.addAll(sanityDetails.getCommonRoomsWithWaterCloset());
            if (totalBathExpected.intValue() >= 0) {
                checkDimension(totalBathExpected.intValue(), detail, wcList, 1.1d, 1.5d, BLDG_PART_BATHROOM,
                        DIMESION_DESC_KEY, RULE_38_1);

                checkDimension(totalBathExpected.intValue(), detail, wcrList, 1.1d, 2.2d, MALE_BATH_WITH_WC,
                        DIMESION_DESC_KEY, RULE_38_1);

                if (totalBathExpected.intValue() > totalActualBath) {
                    addReportDetail(helper.ruleNo, description, expected, actual, Result.Not_Accepted.getResultVal(),
                            detail);
                } else {
                    addReportDetail(helper.ruleNo, description, expected, actual, Result.Accepted.getResultVal(),
                            detail);

                }
            }
        }
        return accepted;
    }

    private boolean checkDimension(Integer required, ScrutinyDetail scrutinyDetail, List<Measurement> list,
            double minSide, double minArea, String type, String desc, String ruleNum) {
        if (!list.isEmpty()) {
            int wcNotMeetingSide = checkDimensionSide(list, minSide);
            int wcNotMeetingArea = checkDimensionArea(list, minArea);

            int totalSize = list.size();
            desc = type + "- Minimum Dimension";

            String expectedResult = minSide + " M";
            String actualResult = "";
            Set<String> ruleNo = new HashSet<>();
            ruleNo.add(ruleNum);
            if (totalSize - wcNotMeetingSide < required && wcNotMeetingSide > 0) {
                actualResult = wcNotMeetingSide + " not having " + expectedResult;
                addReportDetail(ruleNo, desc, expectedResult, actualResult, Result.Not_Accepted.getResultVal(),
                        scrutinyDetail);

            } else {
                actualResult = totalSize + " having " + expectedResult;
                addReportDetail(ruleNo, desc, expectedResult, actualResult, Result.Accepted.getResultVal(),
                        scrutinyDetail);
            }

            desc = type + "- Minimum Area";
            expectedResult = minArea + " M2";
            if (totalSize - wcNotMeetingArea < required && wcNotMeetingArea > 0) {
                actualResult = wcNotMeetingArea + " not having " + expectedResult;
                addReportDetail(ruleNo, desc, expectedResult, actualResult, Result.Not_Accepted.getResultVal(),
                        scrutinyDetail);

            } else {
                actualResult = totalSize + "  having " + expectedResult;
                addReportDetail(ruleNo, desc, expectedResult, actualResult, Result.Accepted.getResultVal(),
                        scrutinyDetail);
            }

        }
        return true;
    }

    private void addReportDetail(Set<String> ruleNo, String ruleDesc, String expected, String actual, String status,
            ScrutinyDetail detail) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo.stream().map(String::new).collect(Collectors.joining(",")));
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        detail.getDetail().add(details);

    }

    private int checkDimensionSide(List<Measurement> measurements, Double minValue) {
        int failedCount = 0;
        for (Measurement m : measurements) {
            if (minValue == 0) {

            }
            double minSide = m.getMinimumSide()
                    .setScale(DcrConstants.ONE_DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
                    .doubleValue();
            if (minSide < minValue) {
                m.setIsValid(false);
                m.appendInvalidReason(String.format(MINIMUM_SIDE_DIMENSION_VIOLATED, minValue));
                failedCount++;
            }
        }
        return failedCount;
    }

    private int checkDimensionArea(List<Measurement> measurements, Double minValue) {
        int failedCount = 0;
        for (Measurement m : measurements) {

            double area = m.getArea()
                    .setScale(DcrConstants.THREE_DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
                    .doubleValue();
            if (area < minValue) {
                m.setIsValid(false);
                m.appendInvalidReason(String.format(MINIMUM_AREA_DIMENSION_VIOLATED, minValue));
                failedCount++;
                LOG.debug("Area not matching is " + m.getArea());
            }
        }
        return failedCount;
    }

    private void validateDimensionOfSPWC(List<Measurement> spwcs, int flrNo, Map<Integer, Integer> failedAreaSpWcMap,
            Map<Integer, Integer> failedDimensionSpWcMap, Map<Integer, Integer> providedSpWcMap, Plan pl) {

        Integer failedAreaCount = 0;
        Integer failedDimensionCount = 0;
        Integer providedSpecialWc = 0;
        
        // Fetch sanitation values
        Map<String, BigDecimal> solarValues = fetchSanitationValues(pl);
        sanitationMinAreaofSPWC = solarValues.get("sanitationMinAreaofSPWC");
        sanitationMinDimensionofSPWC = solarValues.get("sanitationMinDimensionofSPWC"); 

        for (Map.Entry<Integer, Integer> pro : providedSpWcMap.entrySet()) {
            providedSpecialWc += pro.getValue();
        }

        for (Measurement spwc : spwcs) {
            BigDecimal area = spwc.getArea().setScale(DcrConstants.THREE_DECIMALDIGITS_MEASUREMENTS,
                    DcrConstants.ROUNDMODE_MEASUREMENTS);
            BigDecimal width = spwc.getWidth().setScale(DcrConstants.ONE_DECIMALDIGITS_MEASUREMENTS,
                    DcrConstants.ROUNDMODE_MEASUREMENTS);
            if (area.compareTo(sanitationMinAreaofSPWC.setScale(DcrConstants.THREE_DECIMALDIGITS_MEASUREMENTS,
                    DcrConstants.ROUNDMODE_MEASUREMENTS)) < 0) {
                failedAreaCount++;
            }
            if (width.compareTo(sanitationMinDimensionofSPWC.setScale(DcrConstants.ONE_DECIMALDIGITS_MEASUREMENTS,
                    DcrConstants.ROUNDMODE_MEASUREMENTS)) < 0) {
                failedDimensionCount++;
            }

            if (providedSpecialWc == failedAreaCount) {
                failedAreaSpWcMap.put(flrNo, failedAreaCount);
            }

            if (providedSpecialWc == failedDimensionCount) {
                failedDimensionSpWcMap.put(flrNo, failedAreaCount);
            }

        }

    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
