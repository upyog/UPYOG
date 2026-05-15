
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

import static org.egov.edcr.constants.DxfFileConstants.*;
//import static org.egov.edcr.constants.DxfFileConstants.A_AF;
//import static org.egov.edcr.constants.DxfFileConstants.A_AIF;
//import static org.egov.edcr.constants.DxfFileConstants.A_R;
//import static org.egov.edcr.constants.DxfFileConstants.B;
//import static org.egov.edcr.constants.DxfFileConstants.D;
//import static org.egov.edcr.constants.DxfFileConstants.F;
//import static org.egov.edcr.constants.DxfFileConstants.I;
//import static org.egov.edcr.constants.DxfFileConstants.A_PO;
//import static org.egov.edcr.constants.DxfFileConstants.G;
//import static org.egov.edcr.constants.DxfFileConstants.G_GTKS;
//import static org.egov.edcr.constants.DxfFileConstants.G_IT;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Plot;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.commons.edcr.mdms.filter.MdmsFilter;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class FrontYardService extends GeneralRule {
	
	private static final Logger LOG = LogManager.getLogger(FrontYardService.class);
	private static final String RULE_35 = "35 Table-8";
	private static final String RULE_36 = "36";
	private static final String RULE_37_TWO_A = "37-2-A";
	private static final String RULE_37_TWO_B = "37-2-B";
	private static final String RULE_37_TWO_C = "37-2-C";
	private static final String RULE_37_TWO_D = "37-2-D";
	private static final String RULE_37_TWO_G = "37-2-G";
	private static final String RULE_37_TWO_H = "37-2-H";
	private static final String RULE_37_TWO_I = "37-2-I";
	private static final String RULE = "4.4.4";

	private static final String MINIMUMLABEL = "Minimum distance ";
	
	// Added by Bimal 18-March-2924 for method processFrontYardResidential
	private static final BigDecimal MIN_PLOT_AREA = BigDecimal.valueOf(30);
	private static final BigDecimal MIN_VAL_100_SQM = BigDecimal.valueOf(1.54);
	private static final BigDecimal MIN_VAL_150_SQM = BigDecimal.valueOf(1.8);
	private static final BigDecimal MIN_VAL_200_SQM = BigDecimal.valueOf(2.16);
	private static final BigDecimal MIN_VAL_300_PlUS_SQM = BigDecimal.valueOf(3.0);
	private static final BigDecimal PLOT_AREA_100_SQM = BigDecimal.valueOf(100);
	private static final BigDecimal PLOT_AREA_150_SQM = BigDecimal.valueOf(150);
	private static final BigDecimal PLOT_AREA_200_SQM = BigDecimal.valueOf(200);
	private static final BigDecimal PLOT_AREA_300_SQM = BigDecimal.valueOf(300);
	private static final BigDecimal PLOT_AREA_500_SQM = BigDecimal.valueOf(500);
	private static final BigDecimal PLOT_AREA_1000_SQM = BigDecimal.valueOf(1000);

	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_1_5 = BigDecimal.valueOf(1.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_1_8 = BigDecimal.valueOf(1.8);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_2_5 = BigDecimal.valueOf(2.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_3 = BigDecimal.valueOf(3);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_3_6 = BigDecimal.valueOf(3.6);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_4 = BigDecimal.valueOf(4);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_4_5 = BigDecimal.valueOf(4.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_5 = BigDecimal.valueOf(5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_5_5 = BigDecimal.valueOf(5.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_6 = BigDecimal.valueOf(6);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_6_5 = BigDecimal.valueOf(6.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_7 = BigDecimal.valueOf(7);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_7_5 = BigDecimal.valueOf(7.5);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_8 = BigDecimal.valueOf(8);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_9 = BigDecimal.valueOf(9);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_10 = BigDecimal.valueOf(10);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_11 = BigDecimal.valueOf(11);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_12 = BigDecimal.valueOf(12);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_13 = BigDecimal.valueOf(13);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_14 = BigDecimal.valueOf(14);
	private static final BigDecimal FRONTYARDMINIMUM_DISTANCE_15 = BigDecimal.valueOf(15);
	public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);

	public static final String BSMT_FRONT_YARD_DESC = "Basement Front Yard";
	private static final int PLOTAREA_300 = 300;
	
	// Constants for Commercial
	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_20 = BigDecimal.valueOf(0.20);
	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_15 = BigDecimal.valueOf(0.15);
	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_10 = BigDecimal.valueOf(0.10);

	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_41_82 = BigDecimal.valueOf(41.82);
	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_104_5 = BigDecimal.valueOf(104.5);
	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_209 = BigDecimal.valueOf(209);
	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_418_21 = BigDecimal.valueOf(418.21);
	
	// Industrial plot area thresholds
	private static final BigDecimal INDUSTRIAL_PLOTAREA_LIMIT_300 = BigDecimal.valueOf(300);
	private static final BigDecimal INDUSTRIAL_PLOTAREA_LIMIT_2000 = BigDecimal.valueOf(2000);

	// Front setback percentages
	private static final BigDecimal INDUSTRIAL_FRONT_SETBACK_PERCENT_20 = BigDecimal.valueOf(0.20);
	private static final BigDecimal INDUSTRIAL_FRONT_SETBACK_PERCENT_25 = BigDecimal.valueOf(0.25);
	
	private Boolean isNbcType=false;
	
	private static final double FOUR_MTR = 4;
	private static final double FIVE_MTR = 5;
	private static final double SIX_MTR = 6;

	
private class FrontYardResult {
		String rule;
		String subRule;
		String blockName;
		Integer level;
		BigDecimal actualMeanDistance = BigDecimal.ZERO;
		BigDecimal actualMinDistance = BigDecimal.ZERO;
		String occupancy;
		BigDecimal expectedminimumDistance = BigDecimal.ZERO;
		BigDecimal expectedmeanDistance = BigDecimal.ZERO;
		String additionalCondition;
		boolean status = false;
		String setBackPercentage;
		String occupancyCode;
	}

	public void processFrontYard(Plan pl, List<ScrutinyDetail> scrutinyDetailList) {
		Plot plot = pl.getPlot();
		HashMap<String, String> errors = new HashMap<>();
		if (plot == null)
			return;
		// each blockwise, check height , floor area, buildup area. check most restricve
		// based on occupancy and front yard values
		// of occupancies.
		// If floor area less than 150 mt and occupancy type D, then consider as
		// commercial building.
		// In output show blockwise required and provided information.

		validateFrontYard(pl);

		if (plot != null && !pl.getBlocks().isEmpty()) {
			for (Block block : pl.getBlocks()) { // for each block

				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, LEVEL);
				scrutinyDetail.addColumnHeading(3, OCCUPANCY);
				scrutinyDetail.addColumnHeading(4, FIELDVERIFIED);
				scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
				scrutinyDetail.addColumnHeading(6, PROVIDED);
				scrutinyDetail.addColumnHeading(7, STATUS);
				scrutinyDetail.setHeading(FRONT_YARD_DESC);

				FrontYardResult frontYardResult = new FrontYardResult();

				for (SetBack setback : block.getSetBacks()) {
					BigDecimal min;
					BigDecimal mean;
					// consider height,floor area,buildup area, different occupancies of block
					// Get occupancies of perticular block and use the same.

					if (setback.getFrontYard() != null) {
						min = setback.getFrontYard().getMinimumDistance();
						mean = setback.getFrontYard().getMean();

						// if height defined at frontyard level, then use else use buidling height.
						BigDecimal buildingHeight = setback.getFrontYard().getHeight() != null
								&& setback.getFrontYard().getHeight().compareTo(BigDecimal.ZERO) > 0
										? setback.getFrontYard().getHeight()
										: block.getBuilding().getBuildingHeight();
								
						BigDecimal buildingHeightExcludeMP = block.getBuilding().getBuildingHeightExcludingMP();
					    if (buildingHeight != null) {
					      buildingHeight = buildingHeight.setScale(2, RoundingMode.HALF_UP);
					    }

						if (buildingHeight != null && (min.doubleValue() > 0 || mean.doubleValue() > 0)) {
							for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
								scrutinyDetail.setKey("Block_" + block.getName() + "_" + FRONT_YARD_DESC);

								if (setback.getLevel() < 0) {
									scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Basement Front Yard");
									checkFrontYardBasement(pl, block.getBuilding(), block.getName(), setback.getLevel(),
											plot, BSMT_FRONT_YARD_DESC, min, mean, occupancy.getTypeHelper(),
											frontYardResult);

								}

								/*if ((occupancy.getTypeHelper().getSubtype() != null
										&& (A_R.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
												|| A_AF.equalsIgnoreCase(
														occupancy.getTypeHelper().getSubtype().getCode())
												|| A_PO.equalsIgnoreCase(
														occupancy.getTypeHelper().getSubtype().getCode()))
										|| F.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()))) {*/
								// updated by Bimal 18-March-2924 type F condition is removed
								if ((occupancy.getTypeHelper().getSubtype() != null
										&& (A_R.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
												|| A_AF.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
												|| A_FH.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
												|| A_AIF.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
												|| A_PO.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())))) {
									//Added by Bimal 18-March-2924 to check front yard based on plotarea not on height
									checkFrontYardResidentialCommon(pl, block.getBuilding(), block.getName(),
											setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
											occupancy.getTypeHelper(), frontYardResult, errors, buildingHeightExcludeMP);
									// Commented by Bimal 18-March-2924 to check front yard based on plot are not on height
									/*
									 * if (buildingHeight.compareTo(BigDecimal.valueOf(10)) <= 0 &&
									 * block.getBuilding() .getFloorsAboveGround().compareTo(BigDecimal.valueOf(3))
									 * <= 0) { checkFrontYardUptoTenMts(pl, block.getBuilding(), block.getName(),
									 * setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), frontYardResult, errors); } else if
									 * (buildingHeight.compareTo(BigDecimal.valueOf(12)) <= 0 &&
									 * block.getBuilding().getFloorsAboveGround() .compareTo(BigDecimal.valueOf(4))
									 * <= 0) { checkFrontYardUptoTwelveMts(setback, block.getBuilding(), pl,
									 * setback.getLevel(), block.getName(), plot, FRONT_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), frontYardResult, errors); } else if
									 * (buildingHeight.compareTo(BigDecimal.valueOf(16)) <= 0) {
									 * checkFrontYardUptoSixteenMts(setback, block.getBuilding(), buildingHeight,
									 * pl, setback.getLevel(), block, plot, FRONT_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), frontYardResult, errors); } else if
									 * (buildingHeight.compareTo(BigDecimal.valueOf(16)) > 0) {
									 * checkFrontYardAboveSixteenMts(setback, block.getBuilding(), buildingHeight,
									 * pl, setback.getLevel(), block.getName(), plot, FRONT_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), frontYardResult); }
									 */
								 	// Condition removed by Bimal 18-March-2924 type to calculate frontyard for residential occupancy only 
									/*
									 * else if (G.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) {
									 * checkFrontYardForIndustrial(pl, block.getBuilding(), block.getName(),
									 * setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), frontYardResult); }
									 */
									
								}else { 
									min = setback.getFrontYard().getArea();
									checkFrontYardOtherOccupancies(pl, block.getBuilding(),
									  block.getName(), setback.getLevel(), plot, FRONT_YARD_DESC, min, mean,
									  occupancy.getTypeHelper(), frontYardResult, errors, buildingHeight, setback); 
									 }
									 

							}

							//if (errors.isEmpty()) {
								Map<String, String> details = new HashMap<>();
								details.put(RULE_NO, frontYardResult.subRule);
								details.put(LEVEL,
										frontYardResult.level != null ? frontYardResult.level.toString() : "");
								
								String occupancy = frontYardResult.occupancy;
								if (occupancy != null && occupancy.contains(",")) {
								    occupancy = occupancy.split(",")[0].trim();
								}
								details.put(OCCUPANCY, occupancy);

								//details.put(OCCUPANCY, frontYardResult.occupancy);
								details.put(FIELDVERIFIED, MINIMUMLABEL);
								
								String permissableValueWithPercentage;
								String providedValue;
								
								if(frontYardResult.occupancyCode.equalsIgnoreCase("A") || 
										frontYardResult.occupancyCode.equalsIgnoreCase("A-R")	||
										frontYardResult.occupancyCode.equalsIgnoreCase("A-AF") ||
										frontYardResult.occupancyCode.equalsIgnoreCase("A-AIF") ||
										frontYardResult.occupancyCode.equalsIgnoreCase("G") || 
										frontYardResult.occupancyCode.equalsIgnoreCase("L")
//										frontYardResult.occupancyCode.equalsIgnoreCase("G-GTKS") ||
//										frontYardResult.occupancyCode.equalsIgnoreCase("G-IT") ||
//										frontYardResult.occupancyCode.equalsIgnoreCase("G-F")
										) {
									permissableValueWithPercentage = frontYardResult.expectedminimumDistance.toString();
								    providedValue = frontYardResult.actualMinDistance.toString();
								    details.put("OccCode", frontYardResult.occupancyCode);
								}else if (frontYardResult.setBackPercentage != null 
								        && frontYardResult.setBackPercentage.contains("m")) {							    
								    permissableValueWithPercentage = frontYardResult.setBackPercentage;
								    providedValue = frontYardResult.actualMeanDistance.toString() + "m";
								    details.put("OccCode", frontYardResult.occupancyCode);
								} else {								
								    permissableValueWithPercentage = frontYardResult.setBackPercentage 
								            + "% of the plot area (" 
								            + frontYardResult.expectedminimumDistance.toPlainString() + ")";
								    //providedValue = frontYardResult.actualMinDistance.toString();
								    providedValue = frontYardResult.actualMinDistance.setScale(2, RoundingMode.HALF_UP).toString();
								    details.put("OccCode", frontYardResult.occupancyCode);
								}
								
								details.put(PERMISSIBLE, permissableValueWithPercentage);
								details.put(PROVIDED, providedValue);

								if (frontYardResult.status) {
									details.put(STATUS, Result.Accepted.getResultVal());
								} else {
									details.put(STATUS, Result.Not_Accepted.getResultVal());
								}
								scrutinyDetail.getDetail().add(details);
								scrutinyDetailList.add(scrutinyDetail);
								//pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
							//}

						}
					}
				}
			}
		}
	}

	// Added by Bimal 18-March-2924 to check front yard based on plot are not on height
	private Boolean checkFrontYardResidentialCommon(Plan pl, Building building, String blockName, Integer level,
			Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult,
			HashMap<String, String> errors, BigDecimal buildingHeight) {
		Boolean valid = false;
		String subRule = RULE;
		String rule = FRONT_YARD_DESC;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();
		//BigDecimal plotArea = pl.getPlanInformation().getPlotArea();
		BigDecimal plotArea = pl.getPlot().getArea();

//		// Process only for A_R, A_AF, and A_ occupancy types
//		if(mostRestrictiveOccupancy.getSubtype() != null
//				&& (A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
//			valid = processFrontYardResidentialGroupHousing(blockName, level, min, mean, mostRestrictiveOccupancy, frontYardResult,
//					valid, subRule, rule, meanVal, depthOfPlot, errors, pl, plotArea, buildingHeight);
//			
//		}else if (mostRestrictiveOccupancy.getSubtype() != null
//				&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())						
//						|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
//
//			valid = processFrontYardResidential(blockName, level, min, mean, mostRestrictiveOccupancy, frontYardResult,
//					valid, subRule, rule, meanVal, depthOfPlot, errors, pl, plotArea, buildingHeight);
//
//		}
		// Process only for A_R, A_AF, and A_ occupancy types
				if(mostRestrictiveOccupancy.getSubtype() != null
						&& (A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
								|| A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()) 
								|| A_AIF.equals(mostRestrictiveOccupancy.getSubtype().getCode()))) {
					valid = processFrontYardResidentialAllTypes(blockName, level, min, mean, mostRestrictiveOccupancy, frontYardResult,
							valid, subRule, rule, meanVal, depthOfPlot, errors, pl, plotArea, buildingHeight);
				}


		return valid;
	}
	//Added by Bimal 18-March-2924 to check front yard based on plot are not on height
	private Boolean processFrontYardResidential(String blockName, Integer level,  BigDecimal min, BigDecimal mean,
	        OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
	        String subRule, String rule, BigDecimal meanVal, BigDecimal depthOfPlot,
	        HashMap<String, String> errors, Plan pl, BigDecimal plotArea, BigDecimal buildingHeight) {
		
		LOG.info("Processing FrontYardResult:");

	    BigDecimal minVal = BigDecimal.ZERO; 

	    // Set minVal based on plot area
	    if (plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
	    	if (!Far.shouldSkipValidation(pl.getEdcrRequest(),DcrConstants.EDCR_SKIP_PLOT_AREA)) {				
				// Plot area is less than zero
		    	errors.put("Plot Area Error:", "Plot area cannot be less than "+MIN_PLOT_AREA);
				pl.addErrors(errors);
            }
	        
	    }else if (plotArea.compareTo(PLOT_AREA_100_SQM) <= 0) {
	        minVal = MIN_VAL_100_SQM;
	    } else if (plotArea.compareTo(PLOT_AREA_150_SQM) <= 0) {
	        minVal = MIN_VAL_150_SQM;
	    } else if (plotArea.compareTo(PLOT_AREA_200_SQM) <= 0) {
	        minVal = MIN_VAL_200_SQM;
	    } else if (plotArea.compareTo(PLOT_AREA_300_SQM) <= 0) {
	        minVal = MIN_VAL_300_PlUS_SQM;
	    } else if (plotArea.compareTo(PLOT_AREA_500_SQM) <= 0) {
	        minVal = MIN_VAL_300_PlUS_SQM;
	    } else if (plotArea.compareTo(PLOT_AREA_1000_SQM) <= 0) {
	        minVal = MIN_VAL_300_PlUS_SQM;
	    } else if (plotArea.compareTo(PLOT_AREA_1000_SQM) > 0) {
	        minVal = MIN_VAL_300_PlUS_SQM;
	    }

	    // Validate minimum and mean value
	    valid = validateMinimumAndMeanValue(min, mean, minVal, mean);
	    if(Far.shouldSkipValidation(pl.getEdcrRequest(), DcrConstants.EDCR_SKIP_PLOT_COVERAGE)) {
			valid=true;
		}

//	    // Add error if plot area is less than or equal to 10
//	    if (plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
//	        errors.put("uptoSixteenHeightUptoTenDepthFrontYard",
//	                "No construction shall be permitted if depth of plot is less than 10 and building height less than 16 having floors upto G+4.");
//	        pl.addErrors(errors);
//	    }
	    if(!valid) {
	    	LOG.info("Front Yard Service: min value validity False: "+minVal+"/"+min);
	    	errors.put("Minimum and Mean Value Validation", "Front setback values are less than permissible value i.e." + minVal+" /" + " current values are " + min);
	    	
	    }
	    else {
	    	LOG.info("Front Yard Service: min value validity True: "+minVal+"/"+min);
	    }
	    pl.addErrors(errors);
	    compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule, minVal, meanVal, level);
	    
	    return valid;
	}
	
	private Boolean processFrontYardResidentialAllTypes(String blockName, Integer level,  BigDecimal min, BigDecimal mean,
	        OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
	        String subRule, String rule, BigDecimal meanVal, BigDecimal depthOfPlot,
	        HashMap<String, String> errors, Plan pl, BigDecimal plotArea, BigDecimal buildingHeight) {
		
		LOG.info("Processing FrontYardResult:");

	    BigDecimal minVal = BigDecimal.ZERO; 
	    
	    if(mostRestrictiveOccupancy!=null && (mostRestrictiveOccupancy.getSubtype()!=null
	    		&& A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
	    	Optional<List> fullListOpt = BpaMdmsUtil.extractMdmsValue(
	        		pl.getMdmsMasterData().get("masterMdmsData"), 
	        		MdmsFilter.LIST_FRONT_SETBACK_PATH, List.class);
	        
	        if (fullListOpt.isPresent()) {
	             List<Map<String, Object>> frontSetBacks = (List<Map<String, Object>>) fullListOpt.get();
	             
	             // Extraction 1B: Apply the tiered setback logic
	             Optional<BigDecimal> requiredSetback = BpaMdmsUtil.findSetbackValueByHeight(frontSetBacks, buildingHeight);

	             requiredSetback.ifPresent(
	                 setback -> LOG.info("Setback for Height " + buildingHeight + ": " + setback)
	             );
	             minVal = requiredSetback.get().abs().stripTrailingZeros();
	        }	    	
	    }else {
//	    	// getting permissible value from mdms
//			Optional<BigDecimal> minPlotArea = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.MIN_PLOT_AREA, BigDecimal.class);
//			minPlotArea.ifPresent(min1 -> LOG.info("Min plot are required : " + min1));
	        
			if (plotArea == null || plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
				errors.put("Plot Area Error:", "Plot area must be greater than : " + MIN_PLOT_AREA);
		        pl.addErrors(errors);			        
		    }
			
			if(pl.getMdmsMasterData().get("masterMdmsData")!=null) {					
				Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.FRONT_SETBACK_PATH, BigDecimal.class);
		        scOpt.ifPresent(sc -> LOG.info("Front Setback Value from mdms : " + sc));
		        minVal = scOpt.get();
			}
	    }        
	    

	    // Validate minimum and mean value
	    valid = validateMinimumAndMeanValue(min, mean, minVal, mean);
	    if(Far.shouldSkipValidation(pl.getEdcrRequest(), DcrConstants.EDCR_SKIP_PLOT_COVERAGE)) {
			valid=true;
		}

	    if(!valid) {
	    	LOG.info("Front Yard Service: min value validity False: "+minVal+"/"+min);
	    	//errors.put("Minimum and Mean Value Validation", "Front setback values are less than permissible value i.e." + minVal+" /" + " current values are " + min);
	    }
	    else {
	    	LOG.info("Front Yard Service: min value validity True: "+minVal+"/"+min);
	    }
	    pl.addErrors(errors);
	    compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule, minVal, meanVal, level);
	    
	    return valid;
	}

	private void validateFrontYard(Plan pl) {

		// Front yard may not be mandatory at each level. We can check whether in any
		// level front yard defined or not ?

		for (Block block : pl.getBlocks()) {
			if (!block.getCompletelyExisting()) {
				Boolean frontYardDefined = false;
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getFrontYard() != null
							&& setback.getFrontYard().getMean().compareTo(BigDecimal.valueOf(0)) > 0) {
						frontYardDefined = true;
					}
				}
				if (!frontYardDefined) {
					HashMap<String, String> errors = new HashMap<>();
					if (!Far.shouldSkipValidation(pl.getEdcrRequest(),DcrConstants.EDCR_SKIP_FRONT_SETBACK)) {				
						errors.put(FRONT_YARD_DESC,
								prepareMessage(OBJECTNOTDEFINED, FRONT_YARD_DESC + " for Block " + block.getName()));
						pl.addErrors(errors);
                    }
					
				}
			}

		}

	}

	private Boolean checkFrontYardUptoSixteenMts(SetBack setback, Building building, BigDecimal blockBuildingHeight,
			Plan pl, Integer level, Block block, Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult,
			HashMap<String, String> errors) {
		Boolean valid = false;
		String subRule = RULE;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();
		if (mostRestrictiveOccupancy.getSubtype() != null
				&& A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						&& block.getBuilding().getFloorsAboveGround().compareTo(BigDecimal.valueOf(5)) <= 0) {
			if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
					&& StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
					&& DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
					&& pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
				valid = commercialUptoSixteenMts(level, block.getName(), min, mean, mostRestrictiveOccupancy,
						frontYardResult, valid, DxfFileConstants.RULE_28, rule, minVal, meanVal, depthOfPlot);
			} else {
				valid = residentialUptoSixteenMts(level, block.getName(), min, mean, mostRestrictiveOccupancy,
						frontYardResult, valid, subRule, rule, minVal, meanVal, depthOfPlot, errors, pl);
			}
		} else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			valid = commercialUptoSixteenMts(level, block.getName(), min, mean, mostRestrictiveOccupancy,
					frontYardResult, valid, subRule, rule, minVal, meanVal, depthOfPlot);
		}

		return valid;
	}

	private Boolean residentialUptoSixteenMts(Integer level, String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot,
			HashMap<String, String> errors, Plan pl) {
		if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
			errors.put("uptoSixteenHeightUptoTenDepthFrontYard",
					"No construction shall be permitted if depth of plot is less than 10 and building height less than 16 having floors upto G+4.");
			pl.addErrors(errors);
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_3;
			valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_5_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_6;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(45)) > 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_6;
		}

		/*
		 * if (-1 == level) { rule = BSMT_FRONT_YARD_DESC; subRuleDesc =
		 * SUB_RULE_24_12_DESCRIPTION; subRule = SUB_RULE_24_12; }
		 */

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
				minVal, meanVal, level);
		return valid;
	}

	private Boolean commercialUptoSixteenMts(Integer level, String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot) {
		if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_5_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_6;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_6_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_7;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_7_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(45)) > 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_8;
		}

		/*
		 * if (-1 == level) { rule = BSMT_FRONT_YARD_DESC; subRuleDesc =
		 * SUB_RULE_24_12_DESCRIPTION; subRule = SUB_RULE_24_12; }
		 */

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
				minVal, meanVal, level);
		return valid;
	}

	private Boolean checkFrontYardAboveSixteenMts(SetBack setback, Building building, BigDecimal blockBuildingHeight,
			Plan pl, Integer level, String blockName, Plot plot, String frontYardFieldName, BigDecimal min,
			BigDecimal mean, OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_36;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		valid = allOccupancyForHighRise(level, blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid,
				subRule, rule, minVal, meanVal, blockBuildingHeight);
		return valid;
	}

	private Boolean allOccupancyForHighRise(Integer level, String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal blockBuildingHeight) {
		if (blockBuildingHeight.compareTo(BigDecimal.valueOf(16)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(19)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_6_5;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(19)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(22)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_7_5;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(22)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(25)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_8;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(25)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(28)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_9;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(28)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(31)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_10;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(31)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(36)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_11;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(36)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(41)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_12;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(41)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(46)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_13;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(46)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(51)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_14;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(51)) > 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_15;
		}

		/*
		 * if (-1 == level) { rule = BSMT_FRONT_YARD_DESC; subRuleDesc =
		 * SUB_RULE_24_12_DESCRIPTION; subRule = SUB_RULE_24_12; }
		 */

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
				minVal, meanVal, level);
		return valid;
	}

	private Boolean checkFrontYardUptoTenMts(Plan pl, Building building, String blockName, Integer level, Plot plot,
			String frontYardFieldName, BigDecimal min, BigDecimal mean, OccupancyTypeHelper mostRestrictiveOccupancy,
			FrontYardResult frontYardResult, HashMap<String, String> errors) {
		Boolean valid = false;
		String subRule = RULE_35;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();
		if (mostRestrictiveOccupancy.getSubtype() != null
				&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
			if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
					&& StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
					&& DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
					&& pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
				valid = commercialUptoSixteenMts(level, blockName, min, mean, mostRestrictiveOccupancy, frontYardResult,
						valid, DxfFileConstants.RULE_28, rule, minVal, meanVal, depthOfPlot);
			} else {
				valid = residentialUptoTenMts(blockName, level, min, mean, mostRestrictiveOccupancy, frontYardResult,
						valid, subRule, rule, minVal, meanVal, depthOfPlot, errors, pl);
			}
		} else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			valid = commercialUptoSixteenMts(level, blockName, min, mean, mostRestrictiveOccupancy, frontYardResult,
					valid, subRule, rule, minVal, meanVal, depthOfPlot);
		}
		return valid;
	}

	private Boolean checkFrontYardBasement(Plan plan, Building building, String blockName, Integer level, Plot plot,
			String frontYardFieldName, BigDecimal min, BigDecimal mean, OccupancyTypeHelper mostRestrictiveOccupancy,
			FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		if ((mostRestrictiveOccupancy.getSubtype() != null
				&& A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))
				|| F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			if (plot.getArea().compareTo(BigDecimal.valueOf(PLOTAREA_300)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_3;
				valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
			}

			rule = BSMT_FRONT_YARD_DESC;

			compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule,
					rule, minVal, meanVal, level);
		}
		return valid;
	}

	private Boolean checkFrontYardForIndustrial(Plan pl, Building building, String blockName, Integer level, Plot plot,
			String frontYardFieldName, BigDecimal min, BigDecimal mean, OccupancyTypeHelper mostRestrictiveOccupancy,
			FrontYardResult frontYardResult) {
		Boolean valid = false;
		String subRule = RULE_35;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();
		valid = processFrontYardForIndustrial(blockName, level, min, mean, mostRestrictiveOccupancy, frontYardResult,
				valid, subRule, rule, minVal, meanVal, pl.getPlot().getArea(), widthOfPlot);
		return valid;
	}

	private Boolean checkFrontYardOtherOccupancies(Plan pl, Building building, String blockName, Integer level,
			Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, 
			HashMap<String, String> errors, BigDecimal buildingHeight, SetBack setback) {
		Boolean valid = false;
		String subRule = RULE_37_TWO_A;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		// Educational
		if (mostRestrictiveOccupancy.getType() != null
				&& B.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = FRONTYARDMINIMUM_DISTANCE_9;
			subRule = RULE_37_TWO_A;
		} // Institutional
		if (mostRestrictiveOccupancy.getType() != null
				&& B.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = FRONTYARDMINIMUM_DISTANCE_9;
			subRule = RULE_37_TWO_B;
		} // Assembly
		if (mostRestrictiveOccupancy.getType() != null
				&& D.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = FRONTYARDMINIMUM_DISTANCE_12;
			subRule = RULE_37_TWO_C;
		} // Malls and multiplex
		if (mostRestrictiveOccupancy.getType() != null
				&& D.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = FRONTYARDMINIMUM_DISTANCE_12;
			subRule = RULE_37_TWO_D;
		} // Hazardous
		if (mostRestrictiveOccupancy.getType() != null
				&& I.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = BigDecimal.ZERO;
			subRule = RULE_37_TWO_G;
		} // Affordable
		if (mostRestrictiveOccupancy.getType() != null
				&& A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = BigDecimal.ZERO;
			subRule = RULE_37_TWO_H;
		}
		// IT,ITES
		if (mostRestrictiveOccupancy.getType() != null
				&& F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {		
			minVal = getMinValueForCommercialFromMdms(pl,plot.getArea(),errors, frontYardResult, buildingHeight);
			subRule = RULE_37_TWO_I;
			valid = validateMinimumAndMeanValue(min, setback.getFrontYard().getWidth(), minVal, meanVal);
	    	if (setback.getFrontYard().getArea().compareTo(minVal) >= 0) {		    
			}else {
				valid=false;
			}
	    	// Save result
	    	compareFrontYardResult(blockName, min, setback.getFrontYard().getWidth(), mostRestrictiveOccupancy,
	    			frontYardResult, valid, subRule, rule, minVal, meanVal, level);	 
		}
		if (mostRestrictiveOccupancy.getType() != null
				&& G.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {		
			minVal = getMinValueForIndustrial(pl,plot.getArea(), buildingHeight, mostRestrictiveOccupancy, errors, frontYardResult);
			subRule = RULE_37_TWO_I;
			valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);	
			compareFrontYardResultIndustry(blockName, setback.getFrontYard().getMinimumDistance(), mean, mostRestrictiveOccupancy,
		    		frontYardResult, valid, subRule, rule, minVal, meanVal, level);	
		}
		if (mostRestrictiveOccupancy.getType() != null
				&& L.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {		
			minVal = getMinValueForPublicBuildingByMDMS(pl,plot.getArea(), buildingHeight, mostRestrictiveOccupancy, errors, frontYardResult);
			subRule = RULE_37_TWO_I;
			valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
			if(L_NH.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {
				if (setback.getFrontYard().getArea().compareTo(minVal) >= 0) {		    
				}else {
					valid=false;
				}
				compareFrontYardResultPublicBuilding(blockName, setback.getFrontYard().getArea(), mean, mostRestrictiveOccupancy,
			    		frontYardResult, valid, subRule, rule, minVal, meanVal, level);	
			}else {
				if (setback.getFrontYard().getMinimumDistance().compareTo(minVal) >= 0) {		    
				}else {
					valid=false;
				}
				compareFrontYardResultPublicBuilding(blockName, setback.getFrontYard().getMinimumDistance(), mean, mostRestrictiveOccupancy,
			    		frontYardResult, valid, subRule, rule, minVal, meanVal, level);	
			}
			
		}

		//valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		
//		if(!isNbcType) {
//	    	// Validate using common function
//		    valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);		    
//		    // Save result
//		    compareFrontYardResultIndustry(blockName, min, mean, mostRestrictiveOccupancy,
//		    		frontYardResult, valid, subRule, rule, minVal, meanVal, level);		   
//	    }else {
//	    	// Validate using common function
//	    	valid = validateMinimumAndMeanValue(min, setback.getFrontYard().getWidth(), minVal, meanVal);
//	    	if (setback.getFrontYard().getWidth().compareTo(minVal) >= 0) {		    
//			}else {
//				valid=false;
//			}
//	    	// Save result
//	    	compareFrontYardResult(blockName, min, setback.getFrontYard().getWidth(), mostRestrictiveOccupancy,
//	    			frontYardResult, valid, subRule, rule, minVal, meanVal, level);	    	
//	    }

//		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
//				minVal, meanVal, level);
		return valid;
	}	
	
	private void compareFrontYardResultPublicBuilding(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		String occupanyCode;
		if (mostRestrictiveOccupancy.getSubtype() != null) {
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			if(L_NH.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {
				occupanyCode = mostRestrictiveOccupancy.getSubtype().getCode();
			}else {
				occupanyCode = mostRestrictiveOccupancy.getType().getCode();
			}
			
		}else {
			occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}
		
		if (minVal.compareTo(frontYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(frontYardResult.expectedminimumDistance) == 0) {
				frontYardResult.rule = frontYardResult.rule != null ? frontYardResult.rule + "," + rule : rule;
				frontYardResult.occupancy = frontYardResult.occupancy != null
						? frontYardResult.occupancy + "," + occupancyName
						: occupancyName;
				frontYardResult.occupancyCode = frontYardResult.occupancyCode != null
						? frontYardResult.occupancyCode + "," + occupanyCode
						: occupanyCode;
			} else {
				frontYardResult.rule = rule;
				frontYardResult.occupancy = occupancyName;
				frontYardResult.occupancyCode = occupanyCode;
			}

			frontYardResult.subRule = subRule;
			frontYardResult.blockName = blockName;
			frontYardResult.level = level;
			frontYardResult.expectedminimumDistance = minVal;
			frontYardResult.expectedmeanDistance = meanVal;
			frontYardResult.actualMinDistance = min.setScale(2, RoundingMode.HALF_UP);
			frontYardResult.actualMeanDistance = mean;
			frontYardResult.status = valid;
			frontYardResult.occupancyCode = occupanyCode;
			LOG.info("FrontYardResult +" + frontYardResult.toString());

		}
	}
	
	private void compareFrontYardResultIndustry(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		String occupanyCode;
		if (mostRestrictiveOccupancy.getSubtype() != null) {
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}else {
			occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}
		
		if (minVal.compareTo(frontYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(frontYardResult.expectedminimumDistance) == 0) {
				frontYardResult.rule = frontYardResult.rule != null ? frontYardResult.rule + "," + rule : rule;
				frontYardResult.occupancy = frontYardResult.occupancy != null
						? frontYardResult.occupancy + "," + occupancyName
						: occupancyName;
				frontYardResult.occupancyCode = frontYardResult.occupancyCode != null
						? frontYardResult.occupancyCode + "," + occupanyCode
						: occupanyCode;
			} else {
				frontYardResult.rule = rule;
				frontYardResult.occupancy = occupancyName;
				frontYardResult.occupancyCode = occupanyCode;
			}

			frontYardResult.subRule = subRule;
			frontYardResult.blockName = blockName;
			frontYardResult.level = level;
			frontYardResult.expectedminimumDistance = minVal;
			frontYardResult.expectedmeanDistance = meanVal;
			frontYardResult.actualMinDistance = min;
			frontYardResult.actualMeanDistance = mean;
			frontYardResult.status = valid;
			frontYardResult.occupancyCode = occupanyCode;
			LOG.info("FrontYardResult +" + frontYardResult.toString());

		}
	}
	
	private void compareFrontYardResult(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		String occupanyCode;
		if (mostRestrictiveOccupancy.getSubtype() != null) {
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getSubtype().getCode();
		}else {
			occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}
		
		if (minVal.compareTo(frontYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(frontYardResult.expectedminimumDistance) == 0) {
				frontYardResult.rule = frontYardResult.rule != null ? frontYardResult.rule + "," + rule : rule;
				frontYardResult.occupancy = frontYardResult.occupancy != null
						? frontYardResult.occupancy + "," + occupancyName
						: occupancyName;
				frontYardResult.occupancyCode = frontYardResult.occupancyCode != null
						? frontYardResult.occupancyCode + "," + occupanyCode
						: occupanyCode;
			} else {
				frontYardResult.rule = rule;
				frontYardResult.occupancy = occupancyName;
				frontYardResult.occupancyCode = occupanyCode;
			}

			frontYardResult.subRule = subRule;
			frontYardResult.blockName = blockName;
			frontYardResult.level = level;
			frontYardResult.expectedminimumDistance = minVal;
			frontYardResult.expectedmeanDistance = meanVal;
			frontYardResult.actualMinDistance = min;
			frontYardResult.actualMeanDistance = mean;
			frontYardResult.status = valid;
			frontYardResult.occupancyCode = occupanyCode;
			LOG.info("FrontYardResult +" + frontYardResult.toString());

		}
	}

	private Boolean checkFrontYardUptoTwelveMts(SetBack setback, Building building, Plan pl, Integer level,
			String blockName, Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult,
			HashMap<String, String> errors) {
		Boolean valid = false;
		String subRule = RULE_35;
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();

		if (mostRestrictiveOccupancy.getSubtype() != null
				&& A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {
			if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
					&& StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
					&& DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
					&& pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
				valid = commercialUptoSixteenMts(level, blockName, min, mean, mostRestrictiveOccupancy, frontYardResult,
						valid, DxfFileConstants.RULE_28, rule, minVal, meanVal, depthOfPlot);
			} else {
				valid = residentialUptoTwelveMts(level, blockName, min, mean, mostRestrictiveOccupancy, frontYardResult,
						valid, subRule, rule, minVal, meanVal, depthOfPlot, errors, pl);
			}
		} else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			valid = commercialUptoSixteenMts(level, blockName, min, mean, mostRestrictiveOccupancy, frontYardResult,
					valid, subRule, rule, minVal, meanVal, depthOfPlot);
		}
		return valid;
	}

	private Boolean residentialUptoTwelveMts(Integer level, String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot,
			HashMap<String, String> errors, Plan pl) {
		if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
			errors.put("uptoTwelveHeightUptoTenDepthFrontYard",
					"No construction shall be permitted if depth of plot is less than 10 and building height less than 12 having floors upto G+2.");
			pl.addErrors(errors);
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_2_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_3_6;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(45)) > 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_6;
		}

		/*
		 * if (-1 == level) { rule = BSMT_FRONT_YARD_DESC; subRuleDesc =
		 * SUB_RULE_24_12_DESCRIPTION; subRule = SUB_RULE_24_12; }
		 */

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
				minVal, meanVal, level);
		return valid;
	}

	private Boolean residentialUptoTenMts(String blockName, Integer level, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot,
			HashMap<String, String> errors, Plan pl) {
		if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_1_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_1_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_1_8;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_2_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_3;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_3;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(45)) > 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_4;
		}

		/*
		 * if (-1 == level) { rule = BSMT_FRONT_YARD_DESC; subRuleDesc =
		 * SUB_RULE_24_12_DESCRIPTION; subRule = SUB_RULE_24_12; }
		 */

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
				minVal, meanVal, level);
		return valid;
	}

	private Boolean processFrontYardForIndustrial(String blockName, Integer level, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal plotArea,
			BigDecimal widthOfPlot) {
		if (plotArea.compareTo(BigDecimal.valueOf(550)) < 0) {
			if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_3;
			} else if (widthOfPlot.compareTo(BigDecimal.valueOf(12)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_4;
			} else if (widthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_5;
			} else if (widthOfPlot.compareTo(BigDecimal.valueOf(18)) <= 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_6;
			} else if (widthOfPlot.compareTo(BigDecimal.valueOf(18)) > 0) {
				minVal = FRONTYARDMINIMUM_DISTANCE_6;
			}
		} else if (plotArea.compareTo(BigDecimal.valueOf(550)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_9;

		} else if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_10;

		} else if (plotArea.compareTo(BigDecimal.valueOf(5000)) > 0
				&& plotArea.compareTo(BigDecimal.valueOf(30000)) <= 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_12;

		} else if (plotArea.compareTo(BigDecimal.valueOf(30000)) > 0) {
			minVal = FRONTYARDMINIMUM_DISTANCE_15;

		}

		/*
		 * if (-1 == level) { rule = BSMT_FRONT_YARD_DESC; subRuleDesc =
		 * SUB_RULE_24_12_DESCRIPTION; subRule = SUB_RULE_24_12; }
		 */
		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
				minVal, meanVal, level);
		return valid;
	}

	private Boolean validateMinimumAndMeanValue(BigDecimal min, BigDecimal mean, BigDecimal minval,
			BigDecimal meanval) {
		Boolean valid = false;
		if (min.compareTo(minval) >= 0 && mean.compareTo(meanval) >= 0) {
			valid = true;
		}		
		return valid;
	}
	
	private BigDecimal getMinValueForCommercialFromMdms(Plan pl,BigDecimal plotArea, HashMap<String, String> errors, 
			FrontYardResult frontYardResult, BigDecimal buildingHeight) {
	    LOG.info("getMinValueForCommercialFromMdms for Commercial:");
	    BigDecimal minVal = BigDecimal.ZERO;
	    if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
	    	errors.put("Plot Area error","Plot area can not be 0");
	    	pl.addErrors(errors);
	    	return BigDecimal.ZERO;
	    }
	    
	    /* ======================================================
	     * HIGH RISE BUILDINGS (Height > 21 m)
	     * ====================================================== */
	    if (buildingHeight.compareTo(BigDecimal.valueOf(21)) > 0) {
	    	Optional<List> fullListOpt = BpaMdmsUtil.extractMdmsValue(
	        		pl.getMdmsMasterData().get("masterMdmsData"), 
	        		MdmsFilter.LIST_FRONT_SETBACK_PATH, List.class);
	    	if (fullListOpt.isPresent()) {
	             List<Map<String, Object>> frontSetBacks = (List<Map<String, Object>>) fullListOpt.get();
	             Optional<BigDecimal> requiredSetback = BpaMdmsUtil.findSetbackValueByHeight(frontSetBacks, buildingHeight);
	             requiredSetback.ifPresent(
	                 setback -> LOG.info("Setback for Height " + buildingHeight + ": " + setback)
	             );
	             minVal = requiredSetback.get().abs().stripTrailingZeros();
	             frontYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        }	    	
	    }else {
	    	 /* ======================================================
	         * LOW RISE BUILDINGS (Height ≤ 21 m)
	         * ====================================================== */
	    	minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_10); // 10%
		    frontYardResult.setBackPercentage = "10";			
	    }
	    return minVal.setScale(2, RoundingMode.HALF_UP);
	}
	
	private BigDecimal getMinValueForCommercial(Plan pl,BigDecimal plotArea, HashMap<String, String> errors, 
			FrontYardResult frontYardResult) {
	    LOG.info("getMinValueForCommercial for Commercial:");
	    BigDecimal minVal = BigDecimal.ZERO;
	    if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
	    	errors.put("Plot Area error","Plot area can not be 0");
	    	pl.addErrors(errors);
	    	return BigDecimal.ZERO;
	    }
	    //Set minVal dynamically using constants
	    if (plotArea.compareTo(BigDecimal.ZERO) > 0 
	    		&& plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_41_82) <= 0) {
	        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_20); // 20%
	        frontYardResult.setBackPercentage = "20";
	    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_41_82) > 0 
	    		&& plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_104_5) <= 0) {
	        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_15); // 15%
	        frontYardResult.setBackPercentage = "15";
	    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_104_5) > 0 
	    		&& plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_209) <= 0) {
	        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_15); // 15%
	        frontYardResult.setBackPercentage = "15";
	    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_209) > 0 
	    		&& plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_418_21) <= 0) {
	        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_15); // 15%
	        frontYardResult.setBackPercentage = "15";
	    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_418_21) > 0) {
	        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_20); // 20%
	        frontYardResult.setBackPercentage = "20";
	    }

	    return minVal.setScale(2, RoundingMode.HALF_UP);
	}
	
	private BigDecimal getMinValueForPublicBuildingByMDMS(Plan pl, BigDecimal plotArea,BigDecimal buildingHeight,
            OccupancyTypeHelper mostRestrictiveOccupancy, HashMap<String, String> errors, FrontYardResult frontYardResult) {

		LOG.info("getMinValueFor Public building");
		
		BigDecimal minVal = BigDecimal.ZERO;
		
		if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
			errors.put("Plot Area Error", "Plot area must be greater than 0.");
			pl.addErrors(errors);
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		}
		
		if(mostRestrictiveOccupancy != null &&
				(L.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))) {
			
			if(L_NH.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {
				if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
				    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
				            pl.getMdmsMasterData().get("masterMdmsData"),
				            MdmsFilter.FRONT_SETBACK_PATH,
				            BigDecimal.class
				    );
				    if (scOpt.isPresent()) {
				        BigDecimal mdmsValue = scOpt.get();
				        LOG.info("Front Setback Value from MDMS : " + mdmsValue);		        
				        //minVal = mdmsValue;	   
				        minVal = (plotArea.multiply(mdmsValue)).divide(BigDecimal.valueOf(100)); 
					    frontYardResult.setBackPercentage = mdmsValue.toPlainString();	
				    }else {
				    	LOG.error("No value found from mdms for the front setback");
				    }
				}
			}else {
				if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
				    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
				            pl.getMdmsMasterData().get("masterMdmsData"),
				            MdmsFilter.FRONT_SETBACK_PATH,
				            BigDecimal.class
				    );
				    if (scOpt.isPresent()) {
				        BigDecimal mdmsValue = scOpt.get();
				        LOG.info("Front Setback Value from MDMS : " + mdmsValue);		        
				        minVal = mdmsValue;	     
				    }else {
				    	LOG.error("No value found from mdms for the front setback");
				    }
				}
			}
			
		}
		
		
		return minVal.setScale(2, RoundingMode.HALF_UP);
}
	
	private BigDecimal getMinValueForIndustrial(Plan pl, BigDecimal plotArea,BigDecimal buildingHeight,
            OccupancyTypeHelper mostRestrictiveOccupancy, HashMap<String, String> errors, FrontYardResult frontYardResult) {

		LOG.info("getMinValueForIndustrial for Industrial:");
		
		BigDecimal minVal = BigDecimal.ZERO;
		
		if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
			errors.put("Plot Area Error", "Plot area must be greater than 0.");
			pl.addErrors(errors);
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		}
		
		if (mostRestrictiveOccupancy == null || mostRestrictiveOccupancy.getSubtype() == null) {
			errors.put("Occupancy Error", "Subtype is missing for Industrial.");
			pl.addErrors(errors);
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		}
		
		String subType = mostRestrictiveOccupancy.getSubtype().getCode();
		LOG.info("Evaluating setback for subType: {}", subType);
		
//		switch (subType) {
//			
//			// -------- Sports Industry (20% after min plotArea 300) --------
//			case "G-SP":
//			case "G-RS":
//			case "G-H":
//			case "G-S":
//			case "G-F":
//			case "G-I":
//				if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_300) >= 0) {
//					minVal = plotArea.multiply(INDUSTRIAL_FRONT_SETBACK_PERCENT_20);
//					frontYardResult.setBackPercentage = "20";
//				}
//				break;
//			
//			// -------- Warehouse (25% after min plotArea 300) --------
//			case "G-W":
//				if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_300) >= 0) {
//					minVal = plotArea.multiply(INDUSTRIAL_FRONT_SETBACK_PERCENT_25);
//					frontYardResult.setBackPercentage = "25";
//				}
//				break;
//			
//			// -------- Knitwear, Textile, IT, General Industry → NBC based --------
//			case "G-K":
//			case "G-T":
//			case "G-IT":
//			case "G-GI":
//				if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_2000) >= 0) {
//					minVal = getNBCFrontSetback(buildingHeight);
//					isNbcType=true;
//					frontYardResult.setBackPercentage = minVal.toPlainString().concat("m");
//				}
//				break;
//			
//			default:
//				LOG.warn("No Industrial setback rule defined for subType: {}", subType);
//		}
		
//		if(mostRestrictiveOccupancy != null &&
//				(G_GTKS.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()) 
//						|| G_IT.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
//			if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
//			    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
//			            pl.getMdmsMasterData().get("masterMdmsData"),
//			            MdmsFilter.FRONT_SETBACK_PATH,
//			            BigDecimal.class
//			    );
//			    if (scOpt.isPresent()) {
//			        BigDecimal mdmsValue = scOpt.get();
//			        LOG.info("Front Setback Value from MDMS : " + mdmsValue);
//			        BigDecimal oneForthHeight = buildingHeight.divide(
//			                BigDecimal.valueOf(FOUR_MTR), 2, RoundingMode.HALF_UP
//			        );
//			        LOG.info("One forth of building height is : " + oneForthHeight);		        
//			        minVal = oneForthHeight.max(mdmsValue);		     
//			    }else {
//			    	LOG.error("No value found from mdms for the side setback");
//			    }
//			}
//		}else {
//			Optional<List> fullListOpt = BpaMdmsUtil.extractMdmsValue(
//	        		pl.getMdmsMasterData().get("masterMdmsData"), 
//	        		MdmsFilter.LIST_FRONT_SETBACK_PATH, List.class);
//	        
//	        if (fullListOpt.isPresent()) {
//	             List<Map<String, Object>> frontSetBacks = (List<Map<String, Object>>) fullListOpt.get();
//
//	             Optional<BigDecimal> requiredSetback = BpaMdmsUtil.findSetbackValueByHeight(frontSetBacks, buildingHeight);
//
//	             requiredSetback.ifPresent(
//	                 setback -> LOG.info("Setback for Height " + buildingHeight + ": " + setback)
//	             );
//	             minVal = requiredSetback.get().abs().stripTrailingZeros();
//	        }else {
//	        	LOG.error("No value found from mdms for the side setback");
//	        }			
//		}
		
		if(mostRestrictiveOccupancy != null &&
				(G.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))) {
			if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
			    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
			            pl.getMdmsMasterData().get("masterMdmsData"),
			            MdmsFilter.FRONT_SETBACK_PATH,
			            BigDecimal.class
			    );
			    if (scOpt.isPresent()) {
			        BigDecimal mdmsValue = scOpt.get();
			        LOG.info("Front Setback Value from MDMS : " + mdmsValue);
			        BigDecimal oneSixHeight = buildingHeight.divide(
			                BigDecimal.valueOf(SIX_MTR), 2, RoundingMode.HALF_UP
			        );
			        LOG.info("One six of building height is : " + oneSixHeight);		        
			        minVal = oneSixHeight.max(mdmsValue);		     
			    }else {
			    	LOG.error("No value found from mdms for the front setback");
			    }
			}
		}
		
		
		//return minVal.setScale(2, RoundingMode.HALF_UP);
		return minVal.stripTrailingZeros();

}


// Helper method to calculate NBC-based front setback using building height.

	private BigDecimal getNBCFrontSetback(BigDecimal buildingHeight) {
		if (buildingHeight == null) {
			return BigDecimal.ZERO;
		}
		
		if (buildingHeight.compareTo(BigDecimal.valueOf(10)) <= 0) {
			return BigDecimal.valueOf(3); // up to 10m → 3m
		} else if (buildingHeight.compareTo(BigDecimal.valueOf(15)) <= 0) {
			return BigDecimal.valueOf(5); // up to 15m → 5m
		} else if (buildingHeight.compareTo(BigDecimal.valueOf(18)) <= 0) {
			return BigDecimal.valueOf(6); // up to 18m → 6m
		} else {
			return BigDecimal.valueOf(6); // above 24m → 6m
		}
		}
}
