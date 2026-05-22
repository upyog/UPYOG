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
//import static org.egov.edcr.constants.DxfFileConstants.G;
//import static org.egov.edcr.constants.DxfFileConstants.G_GTKS;
//import static org.egov.edcr.constants.DxfFileConstants.G_IT;
//import static org.egov.edcr.constants.DxfFileConstants.I;
//import static org.egov.edcr.constants.DxfFileConstants.A_PO;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.REAR_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.YES;

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
public class RearYardService extends GeneralRule {
	private static final Logger LOG = LogManager.getLogger(RearYardService.class);
	private static final String RULE = "4.4.4";
	private static final String RULE_36 = "36";
	private static final String RULE_37_TWO_A = "37-2-A";
	private static final String RULE_37_TWO_B = "37-2-B";
	private static final String RULE_37_TWO_C = "37-2-C";
	private static final String RULE_37_TWO_D = "37-2-D";
	private static final String RULE_37_TWO_G = "37-2-G";
	private static final String RULE_37_TWO_H = "37-2-H";
	private static final String RULE_37_TWO_I = "37-2-I";
	private static final String RULE_47 = "47";

	private static final String MINIMUMLABEL = "Minimum distance";
	// Added by Bimal 18-March-2924 for method processRearYardResidential
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

	private static final BigDecimal REARYARDMINIMUM_DISTANCE_0_9 = BigDecimal.valueOf(0.9);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_1_2 = BigDecimal.valueOf(1.2);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_1_5 = BigDecimal.valueOf(1.5);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_1_8 = BigDecimal.valueOf(1.8);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_2 = BigDecimal.valueOf(2);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_2_5 = BigDecimal.valueOf(2.5);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_3 = BigDecimal.valueOf(3);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_3_6 = BigDecimal.valueOf(3.6);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_4 = BigDecimal.valueOf(4);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_4_5 = BigDecimal.valueOf(4.5);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_5 = BigDecimal.valueOf(5);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_6 = BigDecimal.valueOf(6);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_7 = BigDecimal.valueOf(7);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_8 = BigDecimal.valueOf(8);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_9 = BigDecimal.valueOf(9);
	private static final BigDecimal REARYARDMINIMUM_DISTANCE_12 = BigDecimal.valueOf(12);

	public static final String BSMT_REAR_YARD_DESC = "Basement Rear Setback";
	private static final int PLOTAREA_300 = 300;
	public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);
	
	// Constants for Commercial
	private static final BigDecimal COMMERCIAL_REAR_SETBACK_PERCENT_10 = BigDecimal.valueOf(0.10);
	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_20 = BigDecimal.valueOf(0.20);
	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_25 = BigDecimal.valueOf(0.25);
	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_30 = BigDecimal.valueOf(0.30);

	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_41_82 = BigDecimal.valueOf(41.82);
	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_104_5 = BigDecimal.valueOf(104.5);
	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_209 = BigDecimal.valueOf(209);
	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_418_21 = BigDecimal.valueOf(418.21);
	
	private static final double FOUR_MTR = 4;
	private static final double FIFTH_MTR = 5;
	private static final double SIX_MTR = 6;

	private class RearYardResult {
		String rule;
		String subRule;
		String blockName;
		Integer level;
		BigDecimal actualMeanDistance = BigDecimal.ZERO;
		BigDecimal actualMinDistance = BigDecimal.ZERO;
		String occupancy;
		BigDecimal expectedminimumDistance = BigDecimal.ZERO;
		BigDecimal expectedmeanDistance = BigDecimal.ZERO;
		boolean status = false;
		String setBackPercentage;
		boolean isSetbackCombine=false;
		String occupancyCode;
	}

	public void processRearYard(Plan pl, List<ScrutinyDetail> scrutinyDetailList) {
		HashMap<String, String> errors = new HashMap<>();
		//BigDecimal setBackPercentage = BigDecimal.valueOf(0.0);
		final Plot plot = pl.getPlot();
		if (plot == null)
			return;

		validateRearYard(pl);

		if (plot != null && !pl.getBlocks().isEmpty()) {
			for (Block block : pl.getBlocks()) { // for each block

				scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, LEVEL);
				scrutinyDetail.addColumnHeading(3, OCCUPANCY);
				scrutinyDetail.addColumnHeading(4, FIELDVERIFIED);
				scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
				scrutinyDetail.addColumnHeading(6, PROVIDED);
				scrutinyDetail.addColumnHeading(7, STATUS);
				scrutinyDetail.setHeading(REAR_YARD_DESC);
				RearYardResult rearYardResult = new RearYardResult();

				for (SetBack setback : block.getSetBacks()) {
					BigDecimal min;
					BigDecimal mean;					

					if (setback.getRearYard() != null
							&& setback.getRearYard().getMean().compareTo(BigDecimal.ZERO) > 0) {
						min = setback.getRearYard().getMinimumDistance();						
						mean = setback.getRearYard().getMean();

						// if height defined at rear yard level, then use elase use buidling height.
						BigDecimal buildingHeight = setback.getRearYard().getHeight() != null
								&& setback.getRearYard().getHeight().compareTo(BigDecimal.ZERO) > 0
										? setback.getRearYard().getHeight()
										: block.getBuilding().getBuildingHeight();
								buildingHeight.setScale(2, RoundingMode.HALF_UP);
								
						BigDecimal buildingHeightExcludeMP = block.getBuilding().getBuildingHeightExcludingMP();

						if (buildingHeight != null && (min.doubleValue() > 0 || mean.doubleValue() > 0)) {
							for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
								scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Rear Setback");

								if (setback.getLevel() < 0) {
									scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Basement Rear Setback");
									checkRearYardBasement(pl, block.getBuilding(), block.getName(), setback.getLevel(),
											plot, BSMT_REAR_YARD_DESC, min, mean, occupancy.getTypeHelper(),
											rearYardResult);
								}
								if ((occupancy.getTypeHelper().getSubtype() != null
										&& (A_R.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
												|| A_AF.equalsIgnoreCase(
														occupancy.getTypeHelper().getSubtype().getCode())
												|| A_FH.equalsIgnoreCase(
														occupancy.getTypeHelper().getSubtype().getCode())
												|| A_AIF.equalsIgnoreCase(
														occupancy.getTypeHelper().getSubtype().getCode())
												|| A_PO.equalsIgnoreCase(
														occupancy.getTypeHelper().getSubtype().getCode()))
								/* || F.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()) */)) {
									checkRearYardResidentialCommon(pl, block.getBuilding(), block.getName(), setback.getLevel(), plot,
											REAR_YARD_DESC, min, mean, occupancy.getTypeHelper(), rearYardResult, errors, buildingHeightExcludeMP);
								
									/*
									 * if (buildingHeight.compareTo(BigDecimal.valueOf(10)) <= 0 &&
									 * block.getBuilding() .getFloorsAboveGround().compareTo(BigDecimal.valueOf(3))
									 * <= 0) { checkRearYardUptoTenMts(pl, block.getBuilding(), block,
									 * setback.getLevel(), plot, REAR_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), rearYardResult, buildingHeight);
									 * 
									 * } else if (buildingHeight.compareTo(BigDecimal.valueOf(12)) <= 0 &&
									 * block.getBuilding().getFloorsAboveGround() .compareTo(BigDecimal.valueOf(4))
									 * <= 0) { checkRearYardUptoToTweleveMts(setback, block.getBuilding(), pl,
									 * block, setback.getLevel(), plot, REAR_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), rearYardResult, errors);
									 * 
									 * } else if (buildingHeight.compareTo(BigDecimal.valueOf(16)) <= 0) {
									 * checkRearYardUptoToSixteenMts(setback, block.getBuilding(), pl, block,
									 * setback.getLevel(), plot, REAR_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), rearYardResult, errors);
									 * 
									 * } else if (buildingHeight.compareTo(BigDecimal.valueOf(16)) > 0) {
									 * checkRearYardAboveSixteenMts(setback, block.getBuilding(), pl, block,
									 * setback.getLevel(), plot, REAR_YARD_DESC, min, mean,
									 * occupancy.getTypeHelper(), rearYardResult, buildingHeight);
									 * 
									 * }
									 */
								}else if (occupancy.getTypeHelper().getSubtype()!=null && G.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) {
										min = setback.getRearYard().getArea();
										mean = setback.getRearYard().getWidth();
									  checkRearYardForIndustrial(setback, block.getBuilding(), pl, block,
									  setback.getLevel(), plot, REAR_YARD_DESC, min, mean,
									  occupancy.getTypeHelper(), rearYardResult , buildingHeight); 
								}else if (occupancy.getTypeHelper().getSubtype()!=null && L.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) {
									min = setback.getRearYard().getArea();
									checkRearYardForPublicBuilding(setback, block.getBuilding(), pl, block,
									  setback.getLevel(), plot, REAR_YARD_DESC, min, mean,
									  occupancy.getTypeHelper(), rearYardResult , buildingHeight); 
								}else {
										min = setback.getRearYard().getArea();
										if(occupancy.getTypeHelper().getSubtype()!=null) {
											checkRearYardOtherOccupancies(setback, block.getBuilding(), pl, block,
													  setback.getLevel(), plot, REAR_YARD_DESC, min, mean,
													  occupancy.getTypeHelper(), rearYardResult, buildingHeight,errors);
										}
									   
								}
									 

							}
							Map<String, String> details = new HashMap<>();
							details.put(RULE_NO, rearYardResult.subRule);
							details.put(LEVEL, rearYardResult.level != null ? rearYardResult.level.toString() : "");
							
							String occupancy = rearYardResult.occupancy;
							if (occupancy != null && occupancy.contains(",")) {
							    occupancy = occupancy.split(",")[0].trim();
							}
							details.put(OCCUPANCY, occupancy);
							//details.put(OCCUPANCY, rearYardResult.occupancy);
							String permissableValueWithPercentage;
							String providedValue;
							
							
							if(rearYardResult.occupancyCode.equalsIgnoreCase("A") || 
									rearYardResult.occupancyCode.equalsIgnoreCase("A-R") ||
									rearYardResult.occupancyCode.equalsIgnoreCase("A-AF") ||
									rearYardResult.occupancyCode.equalsIgnoreCase("A-AIF")||
									rearYardResult.occupancyCode.equalsIgnoreCase("G") ||
									rearYardResult.occupancyCode.equalsIgnoreCase("L")
//									rearYardResult.occupancyCode.equalsIgnoreCase("G-GTKS") ||
//									rearYardResult.occupancyCode.equalsIgnoreCase("G-IT") ||
//									rearYardResult.occupancyCode.equalsIgnoreCase("G-F")
									) {
								permissableValueWithPercentage = rearYardResult.expectedminimumDistance.toString();
							    providedValue = rearYardResult.actualMinDistance.toString();
							    details.put("OccCode", rearYardResult.occupancyCode);
							    details.put("isSetbackCombine", String.valueOf(rearYardResult.isSetbackCombine));
							    
							}else if (rearYardResult.setBackPercentage != null 
							        && rearYardResult.setBackPercentage.contains("m")) {							    
							    permissableValueWithPercentage = rearYardResult.setBackPercentage;
							    providedValue = rearYardResult.actualMeanDistance.toString() + "m";
							    details.put("OccCode", rearYardResult.occupancyCode);
							    details.put("isSetbackCombine", String.valueOf(rearYardResult.isSetbackCombine));
							} else {								
//							    permissableValueWithPercentage = rearYardResult.setBackPercentage 
//							            + "% of the plot area (" 
//							            + rearYardResult.expectedminimumDistance.toPlainString() + ")";
							    permissableValueWithPercentage = rearYardResult.setBackPercentage;
								//permissableValueWithPercentage = rearYardResult.expectedminimumDistance.toPlainString();
							    providedValue = rearYardResult.actualMinDistance.toString();
							    details.put("OccCode", rearYardResult.occupancyCode);
							    details.put("isSetbackCombine", String.valueOf(rearYardResult.isSetbackCombine));
							}
							
							if (rearYardResult.expectedmeanDistance != null
									&& rearYardResult.expectedmeanDistance.compareTo(BigDecimal.valueOf(0)) == 0) {
								details.put(FIELDVERIFIED, MINIMUMLABEL);
								//details.put(PERMISSIBLE, rearYardResult.expectedminimumDistance.toString());
								details.put(PERMISSIBLE, permissableValueWithPercentage);								
								//details.put(PROVIDED, rearYardResult.actualMeanDistance.toString());
								details.put(PROVIDED, providedValue);
							} else {
								details.put(FIELDVERIFIED, MINIMUMLABEL);
								//details.put(PERMISSIBLE, rearYardResult.expectedminimumDistance.toString());
								details.put(PERMISSIBLE, permissableValueWithPercentage);
								//details.put(PROVIDED, rearYardResult.actualMeanDistance.toString());
								details.put(PROVIDED, providedValue);
							}
							
							if (rearYardResult.status) {
								details.put(STATUS, Result.Accepted.getResultVal());

							} else {
								details.put(STATUS, Result.Not_Accepted.getResultVal());
							}
							scrutinyDetail.getDetail().add(details);
							scrutinyDetailList.add(scrutinyDetail);
							//pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

						}
					}else {
						for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
							if (occupancy.getTypeHelper() != null
							        && occupancy.getTypeHelper().getSubtype() != null
							        && occupancy.getTypeHelper().getSubtype().getCode() != null
							        && A_AIF.equalsIgnoreCase(
							                occupancy.getTypeHelper().getSubtype().getCode())) {

							    errors.put("rearyardNodeDefined",
							            getLocaleMessage(OBJECTNOTDEFINED,
							                    " Rear Setback of Block " + block.getNumber()
							                            + " at level " + setback.getLevel()));

							    pl.addErrors(errors);
							}
						}
					}
				}
			}
		}

	}


	// Added by Bimal 18-March-2924 to check Rear yard based on plot are not on height
	private Boolean checkRearYardResidentialCommon(Plan pl, Building building, String blockName, Integer level,
			Plot plot, String rearYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
			HashMap<String, String> errors, BigDecimal buildingHeight) {
		Boolean valid = false;
		String subRule = RULE;
		String rule = REAR_YARD_DESC;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();
		BigDecimal plotArea = pl.getPlanInformation().getPlotArea();

		// Process only for A_R, A_AF, and A_ occupancy types
//		if(mostRestrictiveOccupancy.getSubtype() != null
//				&& (A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
//			valid = processRearYardResidentialAllTypes(blockName, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
//					valid, subRule, rule, meanVal, depthOfPlot, errors, pl, plotArea, buildingHeight);
//			
//		}else if (mostRestrictiveOccupancy.getSubtype() != null
//				&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())						
//						|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
//			valid = processRearYardResidential(blockName, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
//					valid, subRule, rule, meanVal, depthOfPlot, errors, pl, plotArea);
//		}
		
		if(mostRestrictiveOccupancy.getSubtype() != null
				&& (A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						|| A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()) 
						|| A_AIF.equals(mostRestrictiveOccupancy.getSubtype().getCode()))) {
			valid = processRearYardResidentialAllTypes(blockName, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
					valid, subRule, rule, meanVal, depthOfPlot, errors, pl, plotArea, buildingHeight);
		}

		return valid;
	}
	
	private Boolean processRearYardResidentialAllTypes(String blockName, Integer level,  BigDecimal min, BigDecimal mean,
	        OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, Boolean valid,
	        String subRule, String rule, BigDecimal meanVal, BigDecimal depthOfPlot,
	        HashMap<String, String> errors, Plan pl, BigDecimal plotArea, BigDecimal buildingHeight) {
		
		LOG.info("Processing RearYardResult:");

	    BigDecimal minVal = BigDecimal.ZERO; 
	    
	    if(mostRestrictiveOccupancy!=null && (mostRestrictiveOccupancy.getSubtype()!=null
	    		&& A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
	    	Optional<List> fullListOpt = BpaMdmsUtil.extractMdmsValue(
	        		pl.getMdmsMasterData().get("masterMdmsData"), 
	        		MdmsFilter.LIST_REAR_SETBACK_PATH, List.class);
	        
	        if (fullListOpt.isPresent()) {
	             List<Map<String, Object>> rearSetBacks = (List<Map<String, Object>>) fullListOpt.get();
	             
	             // Extraction 1B: Apply the tiered setback logic
	             Optional<BigDecimal> requiredSetback = BpaMdmsUtil.findSetbackValueByHeight(rearSetBacks, buildingHeight);

	             requiredSetback.ifPresent(
	                 setback -> LOG.info("Setback for Height " + buildingHeight + ": " + setback)
	             );
	             minVal = requiredSetback.get().abs().stripTrailingZeros();
	        }
	    }else {
	    	// getting permissible value from mdms
//	    		Optional<BigDecimal> minPlotArea = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.MIN_PLOT_AREA, BigDecimal.class);
//	    		minPlotArea.ifPresent(min1 -> LOG.info("Min plot are required : " + min1));
	    		        
	    		if (plotArea == null || plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
	    			errors.put("Plot Area Error:", "Plot area must be greater than : " + MIN_PLOT_AREA);
	    			pl.addErrors(errors);			        
	    		}
	    		
	    		
	    		
	    		 
	    	    if(mostRestrictiveOccupancy!=null && (mostRestrictiveOccupancy.getSubtype()!=null
	    	    		&& A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
	    	    	
	    	    	if (pl.getRoadReserveRear() != null
		    		        && pl.getRoadReserveRear().compareTo(BigDecimal.ZERO) > 0) {
	    	    		if(pl.getMdmsMasterData().get("masterMdmsData")!=null) {					
	    	    			Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"),
	    	    					MdmsFilter.FRONT_SETBACK_PATH, BigDecimal.class);
	    	    			scOpt.ifPresent(sc -> LOG.info("Rear Setback Value from mdms : " + sc));
	    	    			minVal = scOpt.get();
	    	    			BigDecimal oneFifthHeight = buildingHeight.divide(
	    			                BigDecimal.valueOf(FIFTH_MTR), 2, RoundingMode.HALF_UP
	    			        );
	    			        LOG.info("One fifth of building height is : " + oneFifthHeight);		        
	    			        minVal = oneFifthHeight.max(minVal);	
	    	    		}
		    		}else {
		    			if(pl.getMdmsMasterData().get("masterMdmsData")!=null) {					
			    			Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"), 
			    					MdmsFilter.REAR_SETBACK_PATH, BigDecimal.class);
			    			scOpt.ifPresent(sc -> LOG.info("Rear Setback Value from mdms : " + sc));
			    			minVal = scOpt.get();
			    			BigDecimal oneFifthHeight = buildingHeight.divide(
					                BigDecimal.valueOf(FIFTH_MTR), 2, RoundingMode.HALF_UP
					        );
					        LOG.info("One fifth of building height is : " + oneFifthHeight);		        
					        minVal = oneFifthHeight.max(minVal);	
			    		}
		    		}
	    	    }else {
	    	    	if(pl.getMdmsMasterData().get("masterMdmsData")!=null) {					
		    			Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.REAR_SETBACK_PATH, BigDecimal.class);
		    			scOpt.ifPresent(sc -> LOG.info("Rear Setback Value from mdms : " + sc));
		    			minVal = scOpt.get();
		    			BigDecimal oneFifthHeight = buildingHeight.divide(
				                BigDecimal.valueOf(FIFTH_MTR), 2, RoundingMode.HALF_UP
				        );
				        LOG.info("One fifth of building height is : " + oneFifthHeight);		        
				        minVal = oneFifthHeight.max(minVal);	
		    		}
	    	    }
	    		
	    		
	    }        

	    // Validate minimum and mean value
	    valid = validateMinimumAndMeanValue(min, mean, minVal, mean);
	    if(Far.shouldSkipValidation(pl.getEdcrRequest(), DcrConstants.EDCR_SKIP_PLOT_COVERAGE)) {
			valid=true;
		}

	    if(!valid) {
	    	LOG.info("Rear Yard Service: min value validity False: "+minVal+"/"+min);
	    	//errors.put("Minimum and Mean Value Validation", "Rear setback values are less than permissible value i.e." + minVal+" /" + " current values are " + min);
	    	
	    }
	    else {
	    	LOG.info("Rear Yard Service: min value validity True: "+minVal+"/"+min);
	    }
	    pl.addErrors(errors);
	    compareRearYardResult(blockName, min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule, rule, minVal, meanVal, level);
	    
	    return valid;
	}

	// Added by Bimal 18-March-2924 to check Rear yard based on plot are not on height
	private Boolean processRearYardResidential(String blockName, Integer level, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, Boolean valid, String subRule,
			String rule, BigDecimal meanVal, BigDecimal depthOfPlot, HashMap<String, String> errors, Plan pl,
			BigDecimal plotArea) {

		LOG.info("Processing RearYardResult:");

		BigDecimal minVal = BigDecimal.ZERO;

		// Set minVal based on plot area
		if (plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
			if (!Far.shouldSkipValidation(pl.getEdcrRequest(),DcrConstants.EDCR_SKIP_PLOT_AREA)) {				
				// Plot area is less than zero
				errors.put("Plot Area Error:", "Plot area cannot be less than " + MIN_PLOT_AREA);
				pl.addErrors(errors);
            }			
		} else if (plotArea.compareTo(PLOT_AREA_100_SQM) <= 0) {
			minVal = MIN_VAL_300_PlUS_SQM;
		} else if (plotArea.compareTo(PLOT_AREA_150_SQM) <= 0) {
			minVal = MIN_VAL_300_PlUS_SQM;
		} else if (plotArea.compareTo(PLOT_AREA_200_SQM) <= 0) {
			minVal = MIN_VAL_300_PlUS_SQM;
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
		valid = validateMinimumAndMeanValue(min, mean, minVal, mean, plotArea);
		if (min.compareTo(minVal) >= 0) {		    
		}else {
			valid=false;
		}

//		    // Add error if plot area is less than or equal to 10
//		    if (plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
//		        errors.put("uptoSixteenHeightUptoTenDepthFrontYard",
//		                "No construction shall be permitted if depth of plot is less than 10 and building height less than 16 having floors upto G+4.");
//		        pl.addErrors(errors);
//		    }
		if (!valid) {
			LOG.info("Rear Yard Service: min value validity False: " + minVal+"/"+min);
			errors.put("Minimum and Mean Value Validation",
					"Rear setback values are less than permissible value i.e." + minVal+" /" + " current values are " + min);

		} else {
			LOG.info("Rear Yard Service: min value validity True: " + minVal+"/"+min);
		}
		pl.addErrors(errors);
		compareRearYardResult(blockName, min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule, rule,
				minVal, meanVal, level);

		return valid;
	}
	
	// Added by Bimal 18-March-2924 to check Rear yard based on plotarea not on height
	private Boolean validateMinimumAndMeanValue(final BigDecimal min, final BigDecimal mean, final BigDecimal minval,
	        final BigDecimal meanval, final BigDecimal plotArea) {
	    Boolean valid = false;
	    
	    if (plotArea.compareTo(new BigDecimal("200")) <= 0) {
	        // For plot areas less than or equal to 200 sqm, min can be less than, equal to, or greater than minval
	    	if ((min.compareTo(minval) <= 0 || min.compareTo(minval) >= 0) && (mean.compareTo(meanval) <= 0 || mean.compareTo(meanval) >= 0)) {
	            valid = true;
	        }
	    } else {
	        // For plot areas greater than 200 sqm, min should be at least minval
	        if (min.compareTo(minval) >= 0 && mean.compareTo(meanval) >= 0) {
	            valid = true;
	        }
	    }
	    
	    return valid;
	}

	private Boolean checkRearYardUptoTenMts(final Plan pl, Building building, Block block, Integer level,
			final Plot plot, final String rearYardFieldName, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
			BigDecimal buildingHeight) {
		String subRule = RULE;
		String rule = REAR_YARD_DESC;
		Boolean valid = false;
		BigDecimal minVal = BigDecimal.valueOf(0);
		BigDecimal meanVal = BigDecimal.valueOf(0);
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();

		if (mostRestrictiveOccupancy.getSubtype() != null
				&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
			if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
					&& StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
					&& DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
					&& pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
				valid = commercialUptoSixteenMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
						DxfFileConstants.RULE_28, rule, minVal, meanVal, depthOfPlot, valid);
			} else {
				valid = residentialUptoTenMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
						subRule, rule, minVal, meanVal, depthOfPlot, valid);
			}

		} else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			valid = commercialUptoSixteenMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult, subRule,
					rule, minVal, meanVal, depthOfPlot, valid);
		}

		return valid;
	}

	private Boolean residentialUptoTenMts(Block block, Integer level, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot, Boolean valid) {

		if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_0_9;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_1_2;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_1_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_1_8;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_2_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_3;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(45)) > 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		}

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		/*
		 * if (-1 == level) { subRule = SUB_RULE_24_12; rule = BSMT_REAR_YARD_DESC;
		 * subRuleDesc = SUB_RULE_24_12_DESCRIPTION; }
		 */
		compareRearYardResult(block.getName(), min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule,
				rule, minVal, meanVal, level);
		return valid;
	}

	private Boolean checkRearYardBasement(Plan plan, Building building, String blockName, Integer level, Plot plot,
			String rearYardFieldName, BigDecimal min, BigDecimal mean, OccupancyTypeHelper mostRestrictiveOccupancy,
			RearYardResult rearYardResult) {
		Boolean valid = false;
		String subRule = RULE_47;
		String rule = REAR_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		if ((mostRestrictiveOccupancy.getSubtype() != null
				&& A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))
				|| F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			if (plot.getArea().compareTo(BigDecimal.valueOf(PLOTAREA_300)) <= 0) {
				minVal = REARYARDMINIMUM_DISTANCE_3;
				valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
			}

			rule = BSMT_REAR_YARD_DESC;

			compareRearYardResult(blockName, min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule, rule,
					minVal, meanVal, level);
		}
		return valid;
	}

	private Boolean checkRearYardUptoToTweleveMts(SetBack setback, Building building, final Plan pl, Block block,
			Integer level, final Plot plot, final String rearYardFieldName, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
			HashMap<String, String> errors) {
		String subRule = RULE;
		String rule = REAR_YARD_DESC;
		Boolean valid = false;
		BigDecimal minVal = BigDecimal.valueOf(0);
		BigDecimal meanVal = BigDecimal.valueOf(0);
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();

		if (mostRestrictiveOccupancy.getSubtype() != null
				&& A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {
			if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
					&& StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
					&& DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
					&& pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
				commercialUptoSixteenMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
						DxfFileConstants.RULE_28, rule, minVal, meanVal, depthOfPlot, valid);
			} else {
				valid = residentialUptoTwelveMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
						subRule, rule, minVal, meanVal, depthOfPlot, valid, errors, pl);
			}

		} else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			valid = commercialUptoSixteenMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult, subRule,
					rule, minVal, meanVal, depthOfPlot, valid);
		}

		return valid;
	}

	private Boolean checkRearYardForIndustrial(SetBack setback, Building building, final Plan pl, Block block,
			Integer level, final Plot plot, final String rearYardFieldName, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, BigDecimal buildingHeight) {
		String subRule = RULE;
		String rule = REAR_YARD_DESC;
		Boolean valid = false;
		BigDecimal minVal = BigDecimal.valueOf(0);
		BigDecimal meanVal = BigDecimal.valueOf(0);
		BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();

		valid = processRearYardForIndustrial(setback, block, level, min, mean, mostRestrictiveOccupancy, rearYardResult, subRule,
				rule, minVal, meanVal, pl, widthOfPlot, valid , buildingHeight);

		return valid;
	}
	
	private Boolean checkRearYardForPublicBuilding(SetBack setback, Building building, final Plan pl, Block block,
			Integer level, final Plot plot, final String rearYardFieldName, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, BigDecimal buildingHeight) {
		String subRule = RULE;
		String rule = REAR_YARD_DESC;
		Boolean valid = false;
		BigDecimal minVal = BigDecimal.valueOf(0);
		BigDecimal meanVal = BigDecimal.valueOf(0);
		BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();

		valid = processRearYardForPublicBuilding(setback, block, level, min, mean, mostRestrictiveOccupancy, rearYardResult, subRule,
				rule, minVal, meanVal, pl, widthOfPlot, valid , buildingHeight);

		return valid;
	}

	private Boolean residentialUptoTwelveMts(Block block, Integer level, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot, Boolean valid,
			HashMap<String, String> errors, Plan pl) {

		if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
			errors.put("uptoTwelveHeightUptoTenDepthRearYard",
					"No construction shall be permitted if depth of plot is less than 10 and building height less than 12 having floors upto G+2.");
			pl.addErrors(errors);
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_1_8;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_2;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_2_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_3;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(45)) > 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		}

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		/*
		 * if (-1 == level) { subRule = SUB_RULE_24_12; rule = BSMT_REAR_YARD_DESC;
		 * subRuleDesc = SUB_RULE_24_12_DESCRIPTION; }
		 */

		compareRearYardResult(block.getName(), min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule,
				rule, minVal, meanVal, level);
		return valid;
	}

//	private Boolean processRearYardForIndustrial(Block block, Integer level, final BigDecimal min,
//			final BigDecimal mean, final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
//			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal plotArea,
//			BigDecimal widthOfPlot, Boolean valid) {
//
//		if (plotArea.compareTo(BigDecimal.valueOf(550)) < 0) {
//			if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
//				minVal = REARYARDMINIMUM_DISTANCE_3;
//			} else if (widthOfPlot.compareTo(BigDecimal.valueOf(12)) <= 0) {
//				minVal = REARYARDMINIMUM_DISTANCE_3;
//			} else if (widthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
//				minVal = REARYARDMINIMUM_DISTANCE_3;
//			} else if (widthOfPlot.compareTo(BigDecimal.valueOf(18)) <= 0) {
//				minVal = REARYARDMINIMUM_DISTANCE_4;
//			} else if (widthOfPlot.compareTo(BigDecimal.valueOf(18)) > 0) {
//				minVal = REARYARDMINIMUM_DISTANCE_4_5;
//			}
//		} else if (plotArea.compareTo(BigDecimal.valueOf(550)) > 0
//				&& plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
//			minVal = REARYARDMINIMUM_DISTANCE_4_5;
//
//		} else if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
//				&& plotArea.compareTo(BigDecimal.valueOf(5000)) <= 0) {
//			minVal = REARYARDMINIMUM_DISTANCE_6;
//
//		} else if (plotArea.compareTo(BigDecimal.valueOf(5000)) > 0
//				&& plotArea.compareTo(BigDecimal.valueOf(30000)) <= 0) {
//			minVal = REARYARDMINIMUM_DISTANCE_9;
//
//		} else if (plotArea.compareTo(BigDecimal.valueOf(30000)) > 0) {
//			minVal = REARYARDMINIMUM_DISTANCE_12;
//
//		}
//
//		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
//		/*
//		 * if (-1 == level) { subRule = SUB_RULE_24_12; rule = BSMT_REAR_YARD_DESC;
//		 * subRuleDesc = SUB_RULE_24_12_DESCRIPTION; }
//		 */
//
//		compareRearYardResult(block.getName(), min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule,
//				rule, minVal, meanVal, level);
//		return valid;
//	}
	
	private Boolean processRearYardForPublicBuilding(SetBack setback, Block block, Integer level, final BigDecimal min,
	        final BigDecimal mean, final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
	        String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, Plan pl,
	        BigDecimal widthOfPlot, Boolean valid, BigDecimal buildingHeight) {
		
	    if(mostRestrictiveOccupancy != null &&
				(L.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))) {
			if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
			    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
			            pl.getMdmsMasterData().get("masterMdmsData"),
			            MdmsFilter.REAR_SETBACK_PATH,
			            BigDecimal.class
			    );
			    if (scOpt.isPresent()) {
			        BigDecimal mdmsValue = scOpt.get();
			        LOG.info("Rear Setback Value from MDMS : " + mdmsValue);			        	        
			        minVal = mdmsValue;		     
			    }else {
			    	LOG.error("No value found from mdms for the rear setback");
			    }
			}
		}

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		compareRearYardResultForPublicBuilding(block.getName(), min, mean, mostRestrictiveOccupancy,
		            rearYardResult, valid, subRule, rule, minVal, meanVal, level);
	      

	    return valid;
	}
	
	private Boolean processRearYardForIndustrial(SetBack setback, Block block, Integer level, final BigDecimal min,
	        final BigDecimal mean, final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
	        String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, Plan pl,
	        BigDecimal widthOfPlot, Boolean valid, BigDecimal buildingHeight) {
		Boolean isNbcType=false;

	    String occCode = mostRestrictiveOccupancy != null ? mostRestrictiveOccupancy.getType().getCode() : null;

//	    if (occCode != null) {
//	        switch (occCode) {
//	            case "G-SP": // Sports Industry
//	            case "G-RS": // Retail Service Industry
//	            case "G-H":  // Hazard Industries
//	            case "G-S":  // Storage
//	            case "G-F":  // Factory
//	            case "G-I":  // Industrial
//	                // 15% of plot area
//	                minVal = plotArea.multiply(BigDecimal.valueOf(0.15)).setScale(2, RoundingMode.HALF_UP);
//	                rearYardResult.setBackPercentage = "15";
//	                break;
//
//	            case "G-W": // Warehouse
//	                // 35% of plot area
//	                minVal = plotArea.multiply(BigDecimal.valueOf(0.35)).setScale(2, RoundingMode.HALF_UP);
//	                rearYardResult.setBackPercentage = "35";
//	                break;
//
//	            case "G-K": // Knitwear Industry
//	            case "G-T": // Textile Industry
//	            case "G-IT": // Information Technology
//	            case "G-GI": // General Industry
//	                // Follow NBC → based on height
//	                minVal = getNBCRearYardByHeight(buildingHeight);
//	                isNbcType=true;
//	                rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
//	                break;
//
//	            default:
//	                // fallback to NBC
//	                minVal = getNBCRearYardByHeight(buildingHeight);
//	                isNbcType=true;
//	                rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
//	        }
//	    } else {
//	        // If no occupancy → fallback NBC rule
//	        //minVal = getNBCRearYardByHeight(buildingHeight);
//	        //isNbcType=true;
//	    }

//	    if(mostRestrictiveOccupancy != null &&
//				(G_GTKS.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()) 
//						|| G_IT.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
//			if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
//			    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
//			            pl.getMdmsMasterData().get("masterMdmsData"),
//			            MdmsFilter.REAR_SETBACK_PATH,
//			            BigDecimal.class
//			    );
//			    if (scOpt.isPresent()) {
//			        BigDecimal mdmsValue = scOpt.get();
//			        LOG.info("Rear Setback Value from MDMS : " + mdmsValue);
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
//	        		MdmsFilter.LIST_REAR_SETBACK_PATH, List.class);
//	        
//	        if (fullListOpt.isPresent()) {
//	             List<Map<String, Object>> rearSetBacks = (List<Map<String, Object>>) fullListOpt.get();
//
//	             Optional<BigDecimal> requiredSetback = BpaMdmsUtil.findSetbackValueByHeight(rearSetBacks, buildingHeight);
//
//	             requiredSetback.ifPresent(
//	                 setbackRear -> LOG.info("Setback for Height " + buildingHeight + ": " + setbackRear)
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
			            MdmsFilter.REAR_SETBACK_PATH,
			            BigDecimal.class
			    );
			    if (scOpt.isPresent()) {
			        BigDecimal mdmsValue = scOpt.get();
			        LOG.info("Rear Setback Value from MDMS : " + mdmsValue);
			        BigDecimal oneSixHeight = buildingHeight.divide(
			                BigDecimal.valueOf(SIX_MTR), 2, RoundingMode.HALF_UP
			        );
			        LOG.info("One six of building height is : " + oneSixHeight);		        
			        minVal = oneSixHeight.max(mdmsValue);		     
			    }else {
			    	LOG.error("No value found from mdms for the rear setback");
			    }
			}
		}
	    
	    if(!isNbcType) {
	    	// Validate using common function
		    valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		    
		 // Save result
		    compareRearYardResultForIndustry(block.getName(), min, mean, mostRestrictiveOccupancy,
		            rearYardResult, valid, subRule, rule, minVal, meanVal, level);
	    }else {
	    	// ✅ Validate using common function
	    	valid = validateMinimumAndMeanValue(min, setback.getRearYard().getWidth(), minVal, meanVal);
	    	if (setback.getRearYard().getWidth().compareTo(minVal) >= 0) {		    
			}else {
				valid=false;
			}
	    	// Save result
		    compareRearYardResult(block.getName(), min, setback.getRearYard().getWidth(), mostRestrictiveOccupancy,
		            rearYardResult, valid, subRule, rule, minVal, meanVal, level);
	    }
	    

	    return valid;
	}

	private Boolean checkRearYardUptoToSixteenMts(SetBack setback, Building building, final Plan pl, Block block,
			Integer level, final Plot plot, final String rearYardFieldName, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
			HashMap<String, String> errors) {
		String subRule = RULE;
		String rule = REAR_YARD_DESC;
		Boolean valid = false;
		BigDecimal minVal = BigDecimal.valueOf(0);
		BigDecimal meanVal = BigDecimal.valueOf(0);
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();

		if (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
				|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						&& block.getBuilding().getFloorsAboveGround().compareTo(BigDecimal.valueOf(5)) <= 0) {
			if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
					&& StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
					&& DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
					&& pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
				valid = commercialUptoSixteenMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
						DxfFileConstants.RULE_28, rule, minVal, meanVal, depthOfPlot, valid);
			} else {
				valid = residentialUptoSixteenMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult,
						subRule, rule, minVal, meanVal, depthOfPlot, valid, errors, pl);
			}
		} else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			valid = commercialUptoSixteenMts(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult, subRule,
					rule, minVal, meanVal, depthOfPlot, valid);
		}

		return valid;
	}

	private Boolean residentialUptoSixteenMts(Block block, Integer level, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot, Boolean valid,
			HashMap<String, String> errors, Plan pl) {
		if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
			errors.put("uptoSixteenHeightUptoTenDepthRearYard",
					"No construction shall be permitted if depth of plot is less than 10 and building height less than 16 having floors upto G+4.");
			pl.addErrors(errors);
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_2;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_3;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_3_6;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(45)) > 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4_5;
		}

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		/*
		 * if (-1 == level) { subRule = SUB_RULE_24_12; rule = BSMT_REAR_YARD_DESC;
		 * subRuleDesc = SUB_RULE_24_12_DESCRIPTION; }
		 */

		compareRearYardResult(block.getName(), min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule,
				rule, minVal, meanVal, level);
		return valid;
	}

	private Boolean commercialUptoSixteenMts(Block block, Integer level, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot, Boolean valid) {
		if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_2;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_3;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
				&& depthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4_5;
		} else if (depthOfPlot.compareTo(BigDecimal.valueOf(45)) > 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4_5;
		}

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		/*
		 * if (-1 == level) { subRule = SUB_RULE_24_12; rule = BSMT_REAR_YARD_DESC;
		 * subRuleDesc = SUB_RULE_24_12_DESCRIPTION; }
		 */

		compareRearYardResult(block.getName(), min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule,
				rule, minVal, meanVal, level);
		return valid;
	}

	private Boolean checkRearYardAboveSixteenMts(SetBack setback, Building building, final Plan pl, Block block,
			Integer level, final Plot plot, final String rearYardFieldName, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
			BigDecimal buildingHeight) {
		Boolean valid = false;
		String subRule = RULE_36;
		String rule = REAR_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		valid = allOccupancyForHighRise(block, level, min, mean, mostRestrictiveOccupancy, rearYardResult, subRule,
				rule, minVal, meanVal, buildingHeight);
		return valid;
	}

	private Boolean allOccupancyForHighRise(Block block, Integer level, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal blockBuildingHeight) {
		Boolean valid = false;

		if (blockBuildingHeight.compareTo(BigDecimal.valueOf(16)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(19)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4_5;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(19)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(22)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_4_5;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(22)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(25)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_5;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(25)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(28)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_6;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(28)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(31)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_7;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(31)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(36)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_7;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(36)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(41)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_8;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(41)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(46)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_8;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(46)) > 0
				&& blockBuildingHeight.compareTo(BigDecimal.valueOf(51)) <= 0) {
			minVal = REARYARDMINIMUM_DISTANCE_9;
		} else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(51)) > 0) {
			minVal = REARYARDMINIMUM_DISTANCE_9;
		}

		/*
		 * if (-1 == level) { rule = BSMT_REAR_YARD_DESC; subRuleDesc =
		 * SUB_RULE_24_12_DESCRIPTION; subRule = SUB_RULE_24_12; }
		 */

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareRearYardResult(block.getName(), min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule,
				rule, minVal, meanVal, level);
		return valid;
	}

	private Boolean checkRearYardOtherOccupancies(SetBack setback, Building building, final Plan pl, Block block,
			Integer level, final Plot plot, final String rearYardFieldName, final BigDecimal min, final BigDecimal mean,
			final OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult,
			BigDecimal buildingHeight, HashMap<String, String> errors) {
		Boolean valid = false;
		String subRule = RULE_37_TWO_A;
		String rule = REAR_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		// Educational
		if (mostRestrictiveOccupancy.getType() != null
				&& B.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = REARYARDMINIMUM_DISTANCE_6;
			subRule = RULE_37_TWO_A;
		} // Institutional
		if (mostRestrictiveOccupancy.getType() != null
				&& B.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = REARYARDMINIMUM_DISTANCE_6;
			subRule = RULE_37_TWO_B;
		} // Assembly
		if (mostRestrictiveOccupancy.getType() != null
				&& D.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = REARYARDMINIMUM_DISTANCE_6;
			subRule = RULE_37_TWO_C;
		} // Malls and multiplex
		if (mostRestrictiveOccupancy.getType() != null
				&& D.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = REARYARDMINIMUM_DISTANCE_7;
			subRule = RULE_37_TWO_D;
		} // Hazardous
		if (mostRestrictiveOccupancy.getType() != null
				&& I.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = REARYARDMINIMUM_DISTANCE_9;
			subRule = RULE_37_TWO_G;
		} // Affordable
		if (mostRestrictiveOccupancy.getType() != null
				&& A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = REARYARDMINIMUM_DISTANCE_3;
			subRule = RULE_37_TWO_H;
		}
		// IT,ITES
		if (mostRestrictiveOccupancy.getType() != null
				&& F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			minVal = getMinValueForCommercialFromMdms(pl, plot.getArea(), errors, buildingHeight, rearYardResult);
			subRule = RULE_37_TWO_I;
			valid = validateMinimumAndMeanValue(min, setback.getRearYard().getWidth(), minVal, meanVal);
	    	if (setback.getRearYard().getWidth().compareTo(minVal) >= 0) {		    
			}else {
				valid=false;
			}	    	
		}

		compareRearYardResult(block.getName(), min, mean, mostRestrictiveOccupancy, rearYardResult, valid, subRule,
				rule, minVal, meanVal, level);
		return valid;
	}

	private Boolean validateMinimumAndMeanValue(final BigDecimal min, final BigDecimal mean, final BigDecimal minval,
			final BigDecimal meanval) {
		Boolean valid = false;
		if (min.compareTo(minval) >= 0 && mean.compareTo(meanval) >= 0)
			valid = true;
		return valid;
	}
	
	

	private void validateRearYard(final Plan pl) {
		for (Block block : pl.getBlocks()) {
			if (!block.getCompletelyExisting()) {
				Boolean rearYardDefined = false;
				for (SetBack setback : block.getSetBacks()) {
					if (setback.getRearYard() != null
							&& setback.getRearYard().getMean().compareTo(BigDecimal.valueOf(0)) > 0) {
						rearYardDefined = true;
					}
				}
//				if (!rearYardDefined && !pl.getPlanInformation().getNocToAbutRearDesc().equalsIgnoreCase(YES)) {
//					HashMap<String, String> errors = new HashMap<>();
//					errors.put(REAR_YARD_DESC,
//							prepareMessage(OBJECTNOTDEFINED, REAR_YARD_DESC + " for Block " + block.getName()));
//					pl.addErrors(errors);
//				}
			}

		}

	}

	private void compareRearYardResultForIndustry(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, Boolean valid, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		String occupanyCode;
		if (mostRestrictiveOccupancy.getSubtype() != null) {
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}else {
			occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}
		if (minVal.compareTo(rearYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(rearYardResult.expectedminimumDistance) == 0) {
				rearYardResult.rule = rearYardResult.rule != null ? rearYardResult.rule + "," + rule : rule;
				rearYardResult.occupancy = rearYardResult.occupancy != null
						? rearYardResult.occupancy + "," + occupancyName
						: occupancyName;
				rearYardResult.occupancyCode = rearYardResult.occupancyCode != null
						? rearYardResult.occupancyCode + "," + occupanyCode
						: occupanyCode;

				if (meanVal.compareTo(rearYardResult.expectedmeanDistance) >= 0) {
					rearYardResult.expectedmeanDistance = meanVal;
					rearYardResult.actualMeanDistance = mean;
				}
			} else {
				rearYardResult.rule = rule;
				rearYardResult.occupancy = occupancyName;
				rearYardResult.expectedmeanDistance = meanVal;
				rearYardResult.actualMeanDistance = mean;
				rearYardResult.occupancyCode = occupanyCode;

			}

			rearYardResult.subRule = subRule;
			rearYardResult.blockName = blockName;
			rearYardResult.level = level;
			rearYardResult.expectedminimumDistance = minVal;
			rearYardResult.actualMinDistance = mean;
			rearYardResult.status = valid;
			rearYardResult.occupancyCode = occupanyCode;

		}
	}
	
	private void compareRearYardResultForPublicBuilding(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, Boolean valid, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		String occupanyCode;
		if (mostRestrictiveOccupancy.getSubtype() != null) {
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}else {
			occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}
		if (minVal.compareTo(rearYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(rearYardResult.expectedminimumDistance) == 0) {
				rearYardResult.rule = rearYardResult.rule != null ? rearYardResult.rule + "," + rule : rule;
				rearYardResult.occupancy = rearYardResult.occupancy != null
						? rearYardResult.occupancy + "," + occupancyName
						: occupancyName;
				rearYardResult.occupancyCode = rearYardResult.occupancyCode != null
						? rearYardResult.occupancyCode + "," + occupanyCode
						: occupanyCode;

				if (meanVal.compareTo(rearYardResult.expectedmeanDistance) >= 0) {
					rearYardResult.expectedmeanDistance = meanVal;
					rearYardResult.actualMeanDistance = mean;
				}
			} else {
				rearYardResult.rule = rule;
				rearYardResult.occupancy = occupancyName;
				rearYardResult.expectedmeanDistance = meanVal;
				rearYardResult.actualMeanDistance = mean;
				rearYardResult.occupancyCode = occupanyCode;

			}

			rearYardResult.subRule = subRule;
			rearYardResult.blockName = blockName;
			rearYardResult.level = level;
			rearYardResult.expectedminimumDistance = minVal;
			rearYardResult.actualMinDistance = mean;
			rearYardResult.status = valid;
			rearYardResult.occupancyCode = occupanyCode;

		}
	}
	
	private void compareRearYardResult(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, RearYardResult rearYardResult, Boolean valid, String subRule,
			String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		String occupanyCode;
		if (mostRestrictiveOccupancy.getSubtype() != null) {
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getSubtype().getCode();
		}else {
			occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
		}
		if (minVal.compareTo(rearYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(rearYardResult.expectedminimumDistance) == 0) {
				rearYardResult.rule = rearYardResult.rule != null ? rearYardResult.rule + "," + rule : rule;
				rearYardResult.occupancy = rearYardResult.occupancy != null
						? rearYardResult.occupancy + "," + occupancyName
						: occupancyName;
				rearYardResult.occupancyCode = rearYardResult.occupancyCode != null
						? rearYardResult.occupancyCode + "," + occupanyCode
						: occupanyCode;

				if (meanVal.compareTo(rearYardResult.expectedmeanDistance) >= 0) {
					rearYardResult.expectedmeanDistance = meanVal;
					rearYardResult.actualMeanDistance = mean;
				}
			} else {
				rearYardResult.rule = rule;
				rearYardResult.occupancy = occupancyName;
				rearYardResult.expectedmeanDistance = meanVal;
				rearYardResult.actualMeanDistance = mean;
				rearYardResult.occupancyCode = occupanyCode;

			}

			rearYardResult.subRule = subRule;
			rearYardResult.blockName = blockName;
			rearYardResult.level = level;
			rearYardResult.expectedminimumDistance = minVal;
			rearYardResult.actualMinDistance = min.setScale(2, RoundingMode.HALF_UP);
			rearYardResult.status = valid;
			rearYardResult.occupancyCode = occupanyCode;

		}
	}
	
	private BigDecimal getMinValueForCommercial(Plan pl, BigDecimal plotArea, HashMap<String, String> errors, 
			BigDecimal buildingHeight, RearYardResult rearYardResult) {

	    LOG.info("getMinValueForCommercial for Commercial:");

	    BigDecimal minVal = BigDecimal.ZERO;
	    if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
	        errors.put("Plot Area error", "Plot area can not be 0");
	        pl.addErrors(errors);
	        return BigDecimal.ZERO;
	    }

	    // Set minVal dynamically using updated constants
	    // Rule: If height is not given or < 21, use plotArea coverage rule
	    if (buildingHeight == null || buildingHeight.compareTo(BigDecimal.valueOf(21)) < 0) {
	    	rearYardResult.isSetbackCombine=true;
		    if (plotArea.compareTo(BigDecimal.ZERO) > 0
		            && plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_41_82) <= 0) {	        
		        minVal = BigDecimal.ZERO; // Up to 41.82 → Not compulsory → keep ZERO
		    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_41_82) > 0
		            && plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_104_5) <= 0) {	        
		        minVal = plotArea.multiply(COMMERCIAL_REAR_SETBACK_PERCENT_10); // >41.82 and <=104.5 → 10%
		        rearYardResult.setBackPercentage = "10";
		    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_104_5) > 0
		            && plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_209) <= 0) {	        
		        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_20); // >104.5 and <=209 → 20%
		        rearYardResult.setBackPercentage = "20";
		    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_209) > 0
		            && plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_418_21) <= 0) {	        
		        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_25); // >209 and <=418.21 → 25%
		        rearYardResult.setBackPercentage = "25";
		    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_418_21) > 0) {	       
		        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_30);  // >418.21 → 30%
		        rearYardResult.setBackPercentage = "30";
		    }
	    }// Rule: If height >= 21, use setback rule
	    else {
	        if (buildingHeight.compareTo(BigDecimal.valueOf(21)) == 0) {
	            minVal = BigDecimal.valueOf(7);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(24)) <= 0) {
	            minVal = BigDecimal.valueOf(8);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(27)) <= 0) {
	            minVal = BigDecimal.valueOf(9);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(30)) <= 0) {
	            minVal = BigDecimal.valueOf(10);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(35)) <= 0) {
	            minVal = BigDecimal.valueOf(11);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(40)) <= 0) {
	            minVal = BigDecimal.valueOf(12);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(45)) <= 0) {
	            minVal = BigDecimal.valueOf(13);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(50)) <= 0) {
	            minVal = BigDecimal.valueOf(14);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(55)) >= 0) {
	            minVal = BigDecimal.valueOf(16);
	            rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        }
	    }

	    return minVal.setScale(2, RoundingMode.HALF_UP);
	}
	
	private BigDecimal getMinValueForCommercialFromMdms(Plan pl, BigDecimal plotArea, HashMap<String, String> errors, 
			BigDecimal buildingHeight, RearYardResult rearYardResult) {
	    LOG.info("getMinValueForCommercialFromMdms for Commercial:");

	    BigDecimal minVal = BigDecimal.ZERO;
	    if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
	        errors.put("Plot Area error", "Plot area can not be 0");
	        pl.addErrors(errors);
	        return BigDecimal.ZERO;
	    }

	    /* ======================================================
	     * HIGH RISE BUILDINGS (Height > 21 m)
	     * ====================================================== */
	    if (buildingHeight.compareTo(BigDecimal.valueOf(21)) > 0) {
	    	rearYardResult.isSetbackCombine=true;
	    	Optional<List> fullListOpt = BpaMdmsUtil.extractMdmsValue(
	        		pl.getMdmsMasterData().get("masterMdmsData"), 
	        		MdmsFilter.LIST_REAR_SETBACK_PATH, List.class);
	    	if (fullListOpt.isPresent()) {
	             List<Map<String, Object>> rearSetBacks = (List<Map<String, Object>>) fullListOpt.get();
	             Optional<BigDecimal> requiredSetback = BpaMdmsUtil.findSetbackValueByHeight(rearSetBacks, buildingHeight);
	             requiredSetback.ifPresent(
	                 setback -> LOG.info("Setback for Height " + buildingHeight + ": " + setback)
	             );
	             minVal = requiredSetback.get().abs().stripTrailingZeros();
	             rearYardResult.setBackPercentage = minVal.toPlainString().concat("m");
	        }	    	
	    }else {
	    	rearYardResult.isSetbackCombine=true;
	    	 /* ======================================================
	         * LOW RISE BUILDINGS (Height ≤ 21 m)
	         * ====================================================== */	    		
	    	//minVal= getPermisableForCommericalBelow21m(plotArea,pl, rearYardResult);
	    	if(pl.getMdmsMasterData().get("masterMdmsData")!=null) {					
    			Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.REAR_SETBACK_PATH, BigDecimal.class);
    			scOpt.ifPresent(sc -> LOG.info("Rear Setback Value from mdms : " + sc));
    			minVal = scOpt.get();
    			rearYardResult.setBackPercentage = minVal.toPlainString();
    		}
	    }

	    return minVal.setScale(2, RoundingMode.HALF_UP);
	}

	// calculate permissible Rear setback value for commercial below 21 m height
	private static BigDecimal getPermisableForCommericalBelow21m(BigDecimal plotArea, Plan pl, RearYardResult rearYardResult) {
		BigDecimal HUNDRED = BigDecimal.valueOf(100);

	    // Covered area
	    BigDecimal groundCoveredArea = Coverage
	            .calculateGroundCoverage(plotArea, pl)
	            .setScale(2, RoundingMode.HALF_UP);

	    // Ground coverage %
	    BigDecimal groundCoveragePercent = groundCoveredArea
	            .multiply(HUNDRED)
	            .divide(plotArea, 2, RoundingMode.HALF_UP);
	    
	    // Front setback area (10%)
	    BigDecimal frontSetbackArea = plotArea
	            .multiply(COMMERCIAL_REAR_SETBACK_PERCENT_10)
	            .setScale(2, RoundingMode.HALF_UP);

	    // Front setback %
	    BigDecimal frontSetbackPercent = frontSetbackArea
	            .multiply(HUNDRED)
	            .divide(plotArea, 2, RoundingMode.HALF_UP);

	    LOG.info("Front setback area: " + frontSetbackArea);
	    LOG.info("Ground covered area: " + groundCoveredArea);

	    // minVal = plotArea - (frontSetback + coveredArea)
	    BigDecimal minVal = plotArea
	            .subtract(frontSetbackArea.add(groundCoveredArea))
	            .max(BigDecimal.ZERO);
	    // Remaining %
	    BigDecimal remainingPercent = HUNDRED
	            .subtract(groundCoveragePercent.add(frontSetbackPercent))
	            .max(BigDecimal.ZERO);

	    // Update rear setback percentage here (NO hard coding)
	    rearYardResult.setBackPercentage = remainingPercent.stripTrailingZeros().toPlainString();
	    
	    return minVal;
	}

	
	private BigDecimal calculateRemainingAreaPercentage(
	        BigDecimal plotArea,
	        BigDecimal groundCoverageArea,
	        BigDecimal frontSetbackArea) {

	    BigDecimal HUNDRED = BigDecimal.valueOf(100);

	    // Ground coverage %
	    BigDecimal groundCoveragePercent = groundCoverageArea
	            .multiply(HUNDRED)
	            .divide(plotArea, 2, RoundingMode.HALF_UP);

	    // Front setback %
	    BigDecimal frontSetbackPercent = frontSetbackArea
	            .multiply(HUNDRED)
	            .divide(plotArea, 2, RoundingMode.HALF_UP);

	    // Remaining %
	    BigDecimal remainingPercent = HUNDRED
	            .subtract(groundCoveragePercent.add(frontSetbackPercent));

	    return remainingPercent.max(BigDecimal.ZERO);
	}

	
	 // NBC height-based rear yard distances.
	 
	private BigDecimal getNBCRearYardByHeight(BigDecimal buildingHeight) {
	    if (buildingHeight == null) return REARYARDMINIMUM_DISTANCE_3;

	    if (buildingHeight.compareTo(BigDecimal.valueOf(10)) <= 0) {
	        return REARYARDMINIMUM_DISTANCE_3; // 3m
	    } else if (buildingHeight.compareTo(BigDecimal.valueOf(15)) <= 0) {
	        return REARYARDMINIMUM_DISTANCE_5; // 5m
	    } else if (buildingHeight.compareTo(BigDecimal.valueOf(18)) <= 0) {
	        return REARYARDMINIMUM_DISTANCE_6; // 6m
	    } else {
	        return REARYARDMINIMUM_DISTANCE_6; // Above 24m = 6m
	    }
	}
	
}
