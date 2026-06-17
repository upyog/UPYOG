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
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD1_DESC;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD2_DESC;
import static org.egov.edcr.utility.DcrConstants.SIDE_YARD_DESC;

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
import org.egov.common.entity.edcr.Yard;
import org.egov.commons.edcr.mdms.filter.MdmsFilter;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SideYardService extends GeneralRule {

    private static final BigDecimal SIDEVALUE_ONE = BigDecimal.valueOf(1);
    private static final BigDecimal SIDEVALUE_ONE_TWO = BigDecimal.valueOf(1.2);
    private static final BigDecimal SIDEVALUE_ONEPOINTFIVE = BigDecimal.valueOf(1.5);
    private static final BigDecimal SIDEVALUE_ONEPOINTEIGHT = BigDecimal.valueOf(1.8);
    private static final BigDecimal SIDEVALUE_TWO = BigDecimal.valueOf(2);
    private static final BigDecimal SIDEVALUE_TWOPOINTFIVE = BigDecimal.valueOf(2.5);
    private static final BigDecimal SIDEVALUE_THREE = BigDecimal.valueOf(3);
    private static final BigDecimal SIDEVALUE_THREEPOINTSIX = BigDecimal.valueOf(3.66);
    private static final BigDecimal SIDEVALUE_FOUR = BigDecimal.valueOf(4);
    private static final BigDecimal SIDEVALUE_FOURPOINTFIVE = BigDecimal.valueOf(4.5);
    private static final BigDecimal SIDEVALUE_FIVE = BigDecimal.valueOf(5);
    private static final BigDecimal SIDEVALUE_SIX = BigDecimal.valueOf(6);
    private static final BigDecimal SIDEVALUE_SEVEN = BigDecimal.valueOf(7);
    private static final BigDecimal SIDEVALUE_SEVENTYFIVE = BigDecimal.valueOf(0.75);
    private static final BigDecimal SIDEVALUE_EIGHT = BigDecimal.valueOf(8);
    private static final BigDecimal SIDEVALUE_NINE = BigDecimal.valueOf(9);
    private static final BigDecimal SIDEVALUE_TEN = BigDecimal.valueOf(10);

    private static final String SIDENUMBER = "Side Number";
    private static final String MINIMUMLABEL = "Minimum distance ";

    private static final String RULE_35 = "35 Table-9";
    private static final String RULE_36 = "36";
    private static final String RULE_37_TWO_A = "37-2-A";
    private static final String RULE_37_TWO_B = "37-2-B";
    private static final String RULE_37_TWO_C = "37-2-C";
    private static final String RULE_37_TWO_D = "37-2-D";
    private static final String RULE_37_TWO_G = "37-2-G";
    private static final String RULE_37_TWO_H = "37-2-H";
    private static final String RULE_37_TWO_I = "37-2-I";
    private static final String RULE_47 = "47";
    private static final String RULE_F = "4.7.4";
	private static final String RULE_L = "4.18";
	private static final String RULE_G = "4.14";
	private static final String RULE_A = "4.4.4";
    private static final String SIDE_YARD_2_NOTDEFINED = "side2yardNodeDefined";
    private static final String SIDE_YARD_1_NOTDEFINED = "side1yardNodeDefined";

    public static final String BSMT_SIDE_YARD_DESC = "Basement Side Yard";
    private static final int PLOTAREA_300 = 300;
    public static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);
    
    // Added by Bimal 18-March-2924 for method processSideYardResidential
    private static final BigDecimal MIN_PLOT_AREA = BigDecimal.valueOf(30);
    private static final BigDecimal PLOT_AREA_100_SQM = BigDecimal.valueOf(100);
	private static final BigDecimal PLOT_AREA_150_SQM = BigDecimal.valueOf(150);
	private static final BigDecimal PLOT_AREA_200_SQM = BigDecimal.valueOf(200);
	private static final BigDecimal PLOT_AREA_300_SQM = BigDecimal.valueOf(300);
	private static final BigDecimal PLOT_AREA_500_SQM = BigDecimal.valueOf(500);
	private static final BigDecimal PLOT_AREA_1000_SQM = BigDecimal.valueOf(1000);
    private static final double FIVE_MTR = 5;
    private static final double TWO_MTR = 2.0;
    private static final double THREE_MTR = 3.0;
    
 // Constants for Commercial
 	private static final BigDecimal COMMERCIAL_SIDE_SETBACK_PERCENT_10 = BigDecimal.valueOf(0.10);
 	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_20 = BigDecimal.valueOf(0.20);
 	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_25 = BigDecimal.valueOf(0.25);
 	private static final BigDecimal COMMERCIAL_FRONT_SETBACK_PERCENT_30 = BigDecimal.valueOf(0.30);

 	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_41_82 = BigDecimal.valueOf(41.82);
 	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_104_5 = BigDecimal.valueOf(104.5);
 	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_209 = BigDecimal.valueOf(209);
 	private static final BigDecimal COMMERCIAL_PLOT_AREA_LIMIT_418_21 = BigDecimal.valueOf(418.21);

 	
    private static final Logger LOG = LogManager.getLogger(SideYardService.class);

    private class SideYardResult {
        String rule;
        String desc;
        String subRule;
        String blockName;
        Integer level;
        BigDecimal actualMeanDistance = BigDecimal.ZERO;
        BigDecimal actualDistance = BigDecimal.ZERO;
        String occupancy;
        BigDecimal expectedDistance = BigDecimal.ZERO;
        BigDecimal expectedmeanDistance = BigDecimal.ZERO;
        boolean status = false;
        String setBackPercentage;
        boolean isSetbackCombine=false;
        String occupancyCode;
    }

    public void processSideYard(Plan pl, List<ScrutinyDetail> scrutinyDetailList) {
    	LOG.info("Processing SideYard:");
        HashMap<String, String> errors = new HashMap<>();
        Plot plot = pl.getPlot();
        if (plot == null)
            return;

        validateSideYardRule(pl);

        // Side yard 1 and side yard 2 both may not mandatory in same levels. Get
        // previous level side yards in this case.
        // In case of side yard 1 defined and other side not required, then consider
        // other side as zero distance ( in case of noc
        // provided cases).

        Boolean valid = false;
        if (plot != null && !pl.getBlocks().isEmpty()) {
            for (Block block : pl.getBlocks()) { // for each block
//                scrutinyDetail = new ScrutinyDetail();
//                scrutinyDetail.addColumnHeading(1, RULE_NO);
//                scrutinyDetail.addColumnHeading(2, LEVEL);
//                scrutinyDetail.addColumnHeading(3, OCCUPANCY);
//                scrutinyDetail.addColumnHeading(4, SIDENUMBER);
////                scrutinyDetail.addColumnHeading(5, FIELDVERIFIED);
//                scrutinyDetail.addColumnHeading(6, PERMISSIBLE);
//                scrutinyDetail.addColumnHeading(7, PROVIDED);
//                scrutinyDetail.addColumnHeading(8, STATUS);
//                scrutinyDetail.setHeading(SIDE_YARD_DESC);
                
                ScrutinyDetail scrutinyDetailSideYard1 = new ScrutinyDetail();
                scrutinyDetailSideYard1.addColumnHeading(1, RULE_NO);
                scrutinyDetailSideYard1.addColumnHeading(2, LEVEL);
                scrutinyDetailSideYard1.addColumnHeading(3, OCCUPANCY);
                scrutinyDetailSideYard1.addColumnHeading(4, SIDENUMBER);
                scrutinyDetailSideYard1.addColumnHeading(6, PERMISSIBLE);
                scrutinyDetailSideYard1.addColumnHeading(7, PROVIDED);
                scrutinyDetailSideYard1.addColumnHeading(8, STATUS);
                scrutinyDetailSideYard1.setHeading(SIDE_YARD_DESC);
                
                ScrutinyDetail scrutinyDetailSideYard2 = new ScrutinyDetail();
                scrutinyDetailSideYard2.addColumnHeading(1, RULE_NO);
                scrutinyDetailSideYard2.addColumnHeading(2, LEVEL);
                scrutinyDetailSideYard2.addColumnHeading(3, OCCUPANCY);
                scrutinyDetailSideYard2.addColumnHeading(4, SIDENUMBER);
                scrutinyDetailSideYard2.addColumnHeading(6, PERMISSIBLE);
                scrutinyDetailSideYard2.addColumnHeading(7, PROVIDED);
                scrutinyDetailSideYard2.addColumnHeading(8, STATUS);
                scrutinyDetailSideYard2.setHeading(SIDE_YARD_DESC);

                
                SideYardResult sideYard1Result = new SideYardResult();
                SideYardResult sideYard2Result = new SideYardResult();

                for (SetBack setback : block.getSetBacks()) {
                    Yard sideYard1 = null;
                    Yard sideYard2 = null;

                    if (setback.getSideYard1() != null
                            && setback.getSideYard1().getMean().compareTo(BigDecimal.ZERO) > 0) {
                        sideYard1 = setback.getSideYard1();
                    }else {
                    	exemptSideYardForAAndF(pl, block, sideYard1Result, sideYard2Result, scrutinyDetailSideYard1, scrutinyDetailSideYard2 );
                    }
                    if (setback.getSideYard2() != null
                            && setback.getSideYard2().getMean().compareTo(BigDecimal.ZERO) > 0) {
                        sideYard2 = setback.getSideYard2();
                    }
                    	else {
                        	exemptSideYardForAAndF(pl, block, sideYard1Result, sideYard2Result, scrutinyDetailSideYard1, scrutinyDetailSideYard2);
                        }
                    
                    BigDecimal buildingHeight;
                    if (sideYard1 != null || sideYard2 != null) {
                        // If there is changes in height of building, then consider the maximum height
                        // among both side
                        if (sideYard1 != null && sideYard1.getHeight() != null
                                && sideYard1.getHeight().compareTo(BigDecimal.ZERO) > 0
                                && sideYard2 != null && sideYard2.getHeight() != null
                                && sideYard2.getHeight().compareTo(BigDecimal.ZERO) > 0) {
                            buildingHeight = sideYard1.getHeight().compareTo(sideYard2.getHeight()) >= 0
                                    ? sideYard1.getHeight()
                                    : sideYard2.getHeight();
                        } else {
                            buildingHeight = sideYard1 != null && sideYard1.getHeight() != null
                                    && sideYard1.getHeight().compareTo(BigDecimal.ZERO) > 0
                                            ? sideYard1.getHeight()
                                            : sideYard2 != null && sideYard2.getHeight() != null
                                                    && sideYard2.getHeight().compareTo(BigDecimal.ZERO) > 0
                                                            ? sideYard2.getHeight()
                                                            : block.getBuilding().getBuildingHeight();
                        }

                        double minlength = 0;
                        double max = 0;
                        double minMeanlength = 0;
                        double maxMeanLength = 0;
                        if (sideYard2 != null && sideYard1 != null) {
                            if (sideYard2.getMinimumDistance().doubleValue() > sideYard1.getMinimumDistance()
                                    .doubleValue()) {
                                minlength = sideYard1.getMinimumDistance().doubleValue();
                                max = sideYard2.getMinimumDistance().doubleValue();
                            } else {
                                minlength = sideYard2.getMinimumDistance().doubleValue();
                                max = sideYard1.getMinimumDistance().doubleValue();
                            }
                        } else {
                            if (sideYard1 != null) {
                                max = sideYard1.getMinimumDistance().doubleValue();
                            } else {
                                minlength = sideYard2.getMinimumDistance().doubleValue();
                            }
                        }

                        if (buildingHeight != null && (minlength > 0 || max > 0)) {
                            for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
                                //scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Side Setback");
                            	scrutinyDetailSideYard1.setKey("Block_" + block.getName() + "_" + "Side Setback1");
                            	scrutinyDetailSideYard2.setKey("Block_" + block.getName() + "_" + "Side Setback2");
                                if (setback.getLevel() < 0) {
                                    //scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Basement Side Yard");
                                	scrutinyDetailSideYard1.setKey("Block_" + block.getName() + "_" + "Basement Side Yard1");
                                	scrutinyDetailSideYard2.setKey("Block_" + block.getName() + "_" + "Basement Side Yard2");

                                    checkSideYardBasement(pl, block.getBuilding(), buildingHeight, block.getName(),
                                            setback.getLevel(), plot, minlength, max, minMeanlength, maxMeanLength,
                                            occupancy.getTypeHelper(), sideYard1Result, sideYard2Result);

                                }

                                if ((occupancy.getTypeHelper().getSubtype() != null
                                        && (A_R.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
                                        || A_AF.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
                                        || A_FH.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
                                        || A_AIF.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())
                                        || A_PO.equalsIgnoreCase(occupancy.getTypeHelper().getSubtype().getCode())))
								/* || F.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode()) */) {
                                	//Added by Bimal 18-March-2924 to check side yard based on plotarea not on height
                                	//if (buildingHeight.compareTo(BigDecimal.valueOf(10)) <= 0 && block.getBuilding()
                                	//if (block.getBuilding().getFloorsAboveGround().compareTo(BigDecimal.valueOf(4)) <= 0) {                                		
                                		if(sideYard2==null) {
                                			minlength = sideYard1.getMinimumDistance().doubleValue();
                                			checkSideYardCommon(pl, block.getBuilding(), buildingHeight,
                                                    block.getName(), setback.getLevel(), plot, minlength, max, 
                                                    occupancy.getTypeHelper(),sideYard1Result,sideYard2Result, 
                                                    sideYard1.getMinimumDistance(), BigDecimal.ZERO);
                                		}else {
                                			checkSideYardCommon(pl, block.getBuilding(), buildingHeight,
                                                    block.getName(), setback.getLevel(), plot, minlength, max, 
                                                    occupancy.getTypeHelper(),sideYard1Result,sideYard2Result, 
                                                    sideYard1.getMinimumDistance(), sideYard2.getMinimumDistance());
                                		}
                                    //}
                                	
									/*
									 * if (buildingHeight.compareTo(BigDecimal.valueOf(10)) <= 0 &&
									 * block.getBuilding() .getFloorsAboveGround().compareTo(BigDecimal.valueOf(3))
									 * <= 0) { checkSideYardUptoTenMts(pl, block.getBuilding(), buildingHeight,
									 * block.getName(), setback.getLevel(), plot, minlength, max, minMeanlength,
									 * maxMeanLength, occupancy.getTypeHelper(), sideYard1Result, sideYard2Result);
									 * } else if (buildingHeight.compareTo(BigDecimal.valueOf(12)) <= 0 &&
									 * block.getBuilding().getFloorsAboveGround() .compareTo(BigDecimal.valueOf(4))
									 * <= 0) { checkSideYardUptoTwelveMts(pl, block.getBuilding(), buildingHeight,
									 * block.getName(), setback.getLevel(), plot, minlength, max, minMeanlength,
									 * maxMeanLength, occupancy.getTypeHelper(), sideYard1Result, sideYard2Result,
									 * errors); } else if (buildingHeight.compareTo(BigDecimal.valueOf(16)) <= 0) {
									 * checkSideYardUptoSixteenMts(pl, block.getBuilding(), buildingHeight,
									 * block.getName(), setback.getLevel(), plot, minlength, max, minMeanlength,
									 * maxMeanLength, occupancy.getTypeHelper(), sideYard1Result, sideYard2Result,
									 * errors); } else if (buildingHeight.compareTo(BigDecimal.valueOf(16)) > 0) {
									 * checkSideYardAboveSixteenMts(pl, block.getBuilding(), buildingHeight,
									 * block.getName(), setback.getLevel(), plot, minlength, max, minMeanlength,
									 * maxMeanLength, occupancy.getTypeHelper(), sideYard1Result, sideYard2Result);
									 * }
									 */
								}else if (occupancy.getTypeHelper().getSubtype() != null && 
										G.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) {									
			                            if (sideYard1 != null) {			                                
			                                minlength = sideYard1.getMinimumDistance().doubleValue();
			                            } else if (sideYard2 != null) {
			                                minlength = sideYard2.getMinimumDistance().doubleValue();
			                            }
			                        
									  	checkSideYardForIndustrial(pl, block.getBuilding(), buildingHeight,
									  			block.getName(), setback.getLevel(), plot, minlength, max, minMeanlength,
									  			maxMeanLength, occupancy.getTypeHelper(), sideYard1Result, sideYard2Result , 
									  			sideYard2, sideYard1, setback);
								}else if (occupancy.getTypeHelper().getSubtype() != null &&
										L.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) {									
		                            if (sideYard1 != null) {			                                
		                                minlength = sideYard1.getMinimumDistance().doubleValue();
		                            } else if (sideYard2 != null) {
		                                minlength = sideYard2.getMinimumDistance().doubleValue();
		                            }
		                        
		                            checkSideYardForPublicBuilding(pl, block.getBuilding(), buildingHeight,
								  			block.getName(), setback.getLevel(), plot, minlength, max, minMeanlength,
								  			maxMeanLength, occupancy.getTypeHelper(), sideYard1Result, sideYard2Result , 
								  			sideYard2, sideYard1, setback);
							}else {									
								if(occupancy.getTypeHelper().getType() != null
										&& occupancy.getTypeHelper().getSubtype() != null) {
									checkSideYardForOtherOccupancies(pl, block.getBuilding(),
											  buildingHeight, block.getName(), setback.getLevel(), plot, minlength, max,
											  minMeanlength, maxMeanLength, occupancy.getTypeHelper(), sideYard1Result,
											  sideYard2Result, errors, sideYard1, sideYard2); 
								}
								
								}
									 

                            }

                            addSideYardResult(pl, errors, sideYard1Result, sideYard2Result, scrutinyDetailList, 
                            		scrutinyDetailSideYard1, scrutinyDetailSideYard2);
                        }
                        
                        if (pl.getPlanInformation() != null
                                && pl.getPlanInformation().getWidthOfPlot().compareTo(BigDecimal.valueOf(10)) <= 0) {
                            //exemptSideYardForAAndF(pl, block, sideYard1Result, sideYard2Result);
                        }
                    } else {
                        if (pl.getPlanInformation() != null &&
                                pl.getPlanInformation().getWidthOfPlot().compareTo(BigDecimal.valueOf(10)) <= 0) {
                            //exemptSideYardForAAndF(pl, block, sideYard1Result, sideYard2Result);
                            //addSideYardResult(pl, errors,sideYard1Result, sideYard2Result);
                        }
                    }
                       
                }
            }
        }

    }
    // Added by Bimal 18-March-2924 to check Side yard based on plot are not on height
    private void checkSideYardCommon(final Plan pl, Building building, BigDecimal buildingHeight, String blockName,
            Integer level, final Plot plot, final double min, final double max, final OccupancyTypeHelper mostRestrictiveOccupancy, 
            SideYardResult sideYard1Result, SideYardResult sideYard2Result, BigDecimal minDistanceSideYard1, BigDecimal minDistanceSideYard2) {

    	BigDecimal plotArea = pl.getPlot().getArea();
        String rule = SIDE_YARD_DESC;
        String subRule = "4.4.4";
//        Boolean valid2 = false;
//        Boolean valid1 = false;
//        BigDecimal side2val = BigDecimal.ZERO;
//        BigDecimal side1val = BigDecimal.ZERO;
//        BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();

//        if (mostRestrictiveOccupancy.getSubtype() != null 
//        		&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
//        		|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
//        		|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
//
//        	processSideYardResidential(pl, blockName, level, min,
//        			mostRestrictiveOccupancy, rule, subRule, buildingHeight, plotArea, sideYard1Result,sideYard2Result, minDistanceSideYard1, minDistanceSideYard2);
//        }
        if (mostRestrictiveOccupancy.getSubtype() != null 
        		&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
        		|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
        		|| A_AIF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
        		|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {

        	processSideYardResidentialAllTypes(pl, blockName, level, min,
        			mostRestrictiveOccupancy, rule, subRule, buildingHeight, plotArea, sideYard1Result,sideYard2Result, minDistanceSideYard1, minDistanceSideYard2);
        }
    }
    
    private void processSideYardResidentialAllTypes(Plan pl, String blockName, Integer level, final double min,
    		final OccupancyTypeHelper mostRestrictiveOccupancy, String rule, String subRule,
    		BigDecimal buildingHeight, BigDecimal plotArea, SideYardResult sideYard1Result, SideYardResult sideYard2Result, 
    		BigDecimal minDistanceSideYard1, BigDecimal minDistanceSideYard2) {
    	LOG.info("Processing SideYardResidential:");
    	
    	// Set minVal based on plot area and buildingHeight
    	BigDecimal minVal = BigDecimal.ZERO;
    	HashMap<String, String> errors = new HashMap<>();
    	
    	if(mostRestrictiveOccupancy!=null && (mostRestrictiveOccupancy.getSubtype()!=null
	    		&& A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
	    	Optional<List> fullListOpt = BpaMdmsUtil.extractMdmsValue(
	        		pl.getMdmsMasterData().get("masterMdmsData"), 
	        		MdmsFilter.SIDE_SETBACK_PATH, List.class);
	        
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
			
			if (pl.getMdmsMasterData().get("masterMdmsData") != null) {

			    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
			            pl.getMdmsMasterData().get("masterMdmsData"),
			            MdmsFilter.SIDE_SETBACK_PATH,
			            BigDecimal.class
			    );

			    if (scOpt.isPresent()) {
			        BigDecimal mdmsValue = scOpt.get();
			        LOG.info("Side Setback Value from MDMS : " + mdmsValue);
			        BigDecimal oneFifthHeight = buildingHeight.divide(
			                BigDecimal.valueOf(FIVE_MTR), 2, RoundingMode.HALF_UP
			        );

			        minVal = oneFifthHeight.max(mdmsValue);
			        //minVal = mdmsValue;
			        LOG.info("One fifth of building height is : " + oneFifthHeight);	
			    }else {
			    	LOG.error("No value found from mdms for the side setback");
			    }
			}

	    }

    	minVal = minVal.setScale(2, RoundingMode.HALF_UP);
    	boolean valid = validateMinimumAndMeanValue(BigDecimal.valueOf(min), minVal, plotArea);
    	if(!valid) {
	    	LOG.info("Side Yard Service: min value validity False: actual/expected :"+min+"/"+minVal);
	    	//errors.put("Minimum and Mean Value Validation", "Side setback values are less than permissible value i.e." + minVal+" /" + " current values are " + min);
	    	
	    }
	    else {
	    	LOG.info("Side Yard Service: min value validity True: actual/expected :"+min+"/"+minVal);
	    }
    	compareSideYardResult(blockName, minVal, BigDecimal.valueOf(min),
    			mostRestrictiveOccupancy, subRule, rule, valid, level, sideYard1Result, sideYard2Result , minDistanceSideYard1, minDistanceSideYard2);
    }
    
    // Added by Bimal 18-March-2924 to check Side yard based on plot are not on height
//    private void processSideYardResidential(Plan pl, String blockName, Integer level, final double min,
//    		final OccupancyTypeHelper mostRestrictiveOccupancy, String rule, String subRule,
//    		BigDecimal buildingHeight, BigDecimal plotArea, SideYardResult sideYard1Result, SideYardResult sideYard2Result, 
//    		BigDecimal minDistanceSideYard1, BigDecimal minDistanceSideYard2) {
//    	LOG.info("Processing SideYardResidential:");
//    	
//    	// Set minVal based on plot area and buildingHeight
//    	BigDecimal minVal = BigDecimal.ZERO;
//    	HashMap<String, String> errors = new HashMap<>();
//
//    	if (plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
//    		// Plot area is less than zero
//    		errors.put("Plot Area Error:", "Plot area cannot be less than " + MIN_PLOT_AREA);
//    	} else if (plotArea.compareTo(PLOT_AREA_100_SQM) <= 0) {
//    		// Plot area is less than or equal to 100 sqm
//    		minVal = BigDecimal.valueOf(Math.max(buildingHeight.divide(BigDecimal.valueOf(FIVE_MTR)).doubleValue(), TWO_MTR)); // 1/5th of buildingHeight or 2.0 meters, whichever is highest
//    	} else if (plotArea.compareTo(PLOT_AREA_150_SQM) <= 0) {
//    		// Plot area is less than or equal to 150 sqm
//    		minVal = BigDecimal.valueOf(Math.max(buildingHeight.divide(BigDecimal.valueOf(FIVE_MTR)).doubleValue(), TWO_MTR)); // 1/5th of buildingHeight or 2.0 meters, whichever is highest
//    	} else if (plotArea.compareTo(PLOT_AREA_200_SQM) <= 0) {
//    		// Plot area is less than or equal to 200 sqm
//    		minVal = BigDecimal.valueOf(Math.max(buildingHeight.divide(BigDecimal.valueOf(FIVE_MTR)).doubleValue(), TWO_MTR)); // 1/5th of buildingHeight or 2.0 meters, whichever is highest
//    	} else if (plotArea.compareTo(PLOT_AREA_300_SQM) <= 0) {
//    		// Plot area is less than or equal to 300 sqm
//    		minVal = BigDecimal.valueOf(Math.max(buildingHeight.divide(BigDecimal.valueOf(FIVE_MTR)).doubleValue(), TWO_MTR)); // 1/5th of buildingHeight or 2.0 meters, whichever is highest
//    	} else if (plotArea.compareTo(PLOT_AREA_500_SQM) <= 0) {
//    		// Plot area is less than or equal to 500 sqm
//    		minVal = BigDecimal.valueOf(Math.max(buildingHeight.divide(BigDecimal.valueOf(FIVE_MTR)).doubleValue(), TWO_MTR)); // 1/5th of buildingHeight or 2.0 meters, whichever is highest
//    	} else if (plotArea.compareTo(PLOT_AREA_1000_SQM) <= 0) {
//    		// Plot area is less than or equal to 1000 sqm
//    		minVal = BigDecimal.valueOf(Math.max(buildingHeight.divide(BigDecimal.valueOf(FIVE_MTR)).doubleValue(), TWO_MTR)); // 1/5th of buildingHeight or 2.0 meters, whichever is highest
//    	}else if (plotArea.compareTo(PLOT_AREA_1000_SQM) > 0) {
//    		// Plot area is greather than 1000 sqm
//    		minVal = BigDecimal.valueOf(Math.max(buildingHeight.divide(BigDecimal.valueOf(FIVE_MTR)).doubleValue(), TWO_MTR)); // 1/5th of buildingHeight or 2.0 meters, whichever is highest
//    	}
//
//    	minVal = minVal.setScale(2, RoundingMode.HALF_UP);
//    	boolean valid = validateMinimumAndMeanValue(BigDecimal.valueOf(min), minVal, plotArea);
//    	if(!valid) {
//	    	LOG.info("Side Yard Service: min value validity False: actual/expected :"+min+"/"+minVal);
//	    	errors.put("Minimum and Mean Value Validation", "Side setback values are less than permissible value i.e." + minVal+" /" + " current values are " + min);
//	    	
//	    }
//	    else {
//	    	LOG.info("Side Yard Service: min value validity True: actual/expected :"+min+"/"+minVal);
//	    }
//    	compareSideYardResult(blockName, minVal, BigDecimal.valueOf(min),
//    			mostRestrictiveOccupancy, subRule, rule, valid, level, sideYard1Result, sideYard2Result , minDistanceSideYard1, minDistanceSideYard2);
//    }
    
    private Boolean validateMinimumAndMeanValue(final BigDecimal min,  final BigDecimal minval, BigDecimal plotArea) {
        Boolean valid = false;

        if (plotArea.compareTo(PLOT_AREA_200_SQM) <= 0) {
            // Plot area is up to 200 sqm
            valid = true;
            LOG.info("Plot less than 200Sqm excepted Distance is optional so true in all cases");
        } else {
            // Plot area is more than 200 sqm
            if (min.compareTo(minval) >= 0 )
                valid = true;
        }

        return valid;
    }

    // Added by Bimal 18-March-2924 to check Side yard based on plot are not on height
    private void compareSideYardResult(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
            OccupancyTypeHelper mostRestrictiveOccupancy, String subRule, String rule, Boolean valid, Integer level, 
            SideYardResult sideYard1Result, SideYardResult sideYard2Result, BigDecimal minDistanceSideYard1, 
            BigDecimal minDistanceSideYard2) {

        String occupancyName;
        String occupanyCode;
        if (mostRestrictiveOccupancy.getSubtype() != null) {
        	occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getSubtype().getCode();
        }else {
        	occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
    	}
        
        LOG.info("SideYard1Result outside: actualDistance/expectedDistance and status:" + actualDistance +" / "+exptDistance +" and "+valid);
        // Set the values for the side yard result
        sideYard1Result.rule = rule;
        sideYard1Result.occupancy = occupancyName;
        sideYard1Result.occupancyCode = occupanyCode;
        
        sideYard1Result.subRule = subRule;
        sideYard1Result.blockName = blockName;
        sideYard1Result.level = level;
        sideYard1Result.actualDistance = actualDistance;
        //sideYard1Result.actualDistance = minDistanceSideYard1;
        sideYard1Result.expectedDistance = exptDistance;
        if (sideYard1Result.actualDistance.compareTo(sideYard1Result.expectedDistance) >= 0) {
            sideYard1Result.status = true;  // ✅ OK if actual >= expected
        } else {
            sideYard1Result.status = false; // ❌ Invalid if actual < expected
        }
        sideYard1Result.status = valid;
        sideYard1Result.desc = "Plot less than 200Sqm excepted Distance is optional so true in all cases";
        LOG.info("SideYard1Result: actualDistance/expectedDistance and status:" + sideYard1Result.actualDistance +"/"+sideYard1Result.expectedDistance +"and "+sideYard1Result.status);
        // sideYard2Result = sideYard1Result; for both side assuming same value
        sideYard2Result.rule = rule;
        sideYard2Result.occupancy = occupancyName;
        sideYard2Result.occupancyCode = occupanyCode;
        sideYard2Result.subRule = subRule;
        sideYard2Result.blockName = blockName;
        sideYard2Result.level = level;
        //sideYard2Result.actualDistance = actualDistance;
        sideYard2Result.actualDistance = minDistanceSideYard2;
        sideYard2Result.expectedDistance = exptDistance;
        sideYard2Result.desc = "Plot less than 200Sqm excepted Distance is optional so true in all cases";
//        if (valid) {
//        	// Set status for the side yard result
//            sideYard2Result.status = valid;
//            
//        }
//        else {
//        	// Set status for the side yard result
//            sideYard2Result.status = valid;
//        }
        if (sideYard2Result.actualDistance.compareTo(sideYard2Result.expectedDistance) >= 0) {
            sideYard2Result.status = true;  // ✅ OK if actual >= expected
        } else {
            sideYard2Result.status = false; // ❌ Invalid if actual < expected
        }
    }

    
    private void addSideYardResult(final Plan pl, HashMap<String, String> errors, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result, List<ScrutinyDetail> scrutinyDetailList, 
            ScrutinyDetail scrutinyDetailSideYard1, ScrutinyDetail scrutinyDetailSideYard2) {
    	if(sideYard1Result != null && sideYard1Result.occupancyCode !=null) {
    		if(G.equalsIgnoreCase(sideYard1Result.occupancyCode))
    			sideYard1Result.subRule = RULE_G;
			else if(A.equalsIgnoreCase(sideYard1Result.occupancyCode))
				sideYard1Result.subRule = RULE_A;
			else if(F.equalsIgnoreCase(sideYard1Result.occupancyCode))
				sideYard1Result.subRule = RULE_F;
			else if(L.equalsIgnoreCase(sideYard1Result.occupancyCode))
				sideYard1Result.subRule = RULE_L;
    	}
    	
    	if(sideYard2Result != null && sideYard2Result.occupancyCode !=null) {
    		if(G.equalsIgnoreCase(sideYard2Result.occupancyCode))
    			sideYard2Result.subRule = RULE_G;
			else if(A.equalsIgnoreCase(sideYard2Result.occupancyCode))
				sideYard2Result.subRule = RULE_A;
			else if(F.equalsIgnoreCase(sideYard2Result.occupancyCode))
				sideYard2Result.subRule = RULE_F;
			else if(L.equalsIgnoreCase(sideYard2Result.occupancyCode))
				sideYard2Result.subRule = RULE_L;
    	}
        if (sideYard1Result != null) {
            Map<String, String> details = new HashMap<>();
            details.put(RULE_NO, sideYard1Result.subRule);
            details.put(LEVEL,
                    sideYard1Result.level != null ? sideYard1Result.level.toString() : "");
            String occupancy = sideYard1Result.occupancy;
			if (occupancy != null && occupancy.contains(",")) {
			    occupancy = occupancy.split(",")[0].trim();
			}
			details.put(OCCUPANCY, occupancy);
            //details.put(OCCUPANCY, sideYard1Result.occupancy);
            
            String permissableValueWithPercentage;
			String providedValue;
			
			if(sideYard1Result.occupancyCode !=null && (sideYard1Result.occupancyCode.equalsIgnoreCase("A") || 
					sideYard1Result.occupancyCode.equalsIgnoreCase("A-R")	||
					sideYard1Result.occupancyCode.equalsIgnoreCase("A-AF") ||
					sideYard1Result.occupancyCode.equalsIgnoreCase("A-AIF")||
					sideYard1Result.occupancyCode.equalsIgnoreCase("G")   ||
					sideYard1Result.occupancyCode.equalsIgnoreCase("L")
//					sideYard1Result.occupancyCode.equalsIgnoreCase("G-GTKS") ||
//					sideYard1Result.occupancyCode.equalsIgnoreCase("G-IT") ||
//					sideYard1Result.occupancyCode.equalsIgnoreCase("G-F")					
					)) {
				permissableValueWithPercentage = sideYard1Result.expectedDistance.toString();
			    providedValue = sideYard1Result.actualDistance.toString();
			    details.put("OccCode", sideYard1Result.occupancyCode);
			    details.put("isSetbackCombine", String.valueOf(sideYard1Result.isSetbackCombine));
			}else if (sideYard1Result.setBackPercentage != null 
			        && sideYard1Result.setBackPercentage.contains("m")) {							    
			    permissableValueWithPercentage = sideYard1Result.setBackPercentage;
			    providedValue = sideYard1Result.actualDistance.toString() + "m";
			    details.put("OccCode", sideYard1Result.occupancyCode);
			    details.put("isSetbackCombine", String.valueOf(sideYard1Result.isSetbackCombine));
			} else {								
//			    permissableValueWithPercentage = sideYard1Result.setBackPercentage 
//			            + "% of the plot area (" 
//			            + sideYard1Result.expectedDistance.toPlainString() + ")";
				permissableValueWithPercentage = sideYard1Result.setBackPercentage;
			    providedValue = sideYard1Result.actualDistance.toString();
			    details.put("OccCode", sideYard1Result.occupancyCode);
			    details.put("isSetbackCombine", String.valueOf(sideYard1Result.isSetbackCombine));
			}

            //details.put(FIELDVERIFIED, MINIMUMLABEL);
            //details.put(PERMISSIBLE, sideYard1Result.expectedDistance.toString());
            //details.put(PROVIDED, sideYard1Result.actualDistance.toString());
            details.put(PERMISSIBLE, permissableValueWithPercentage);
            details.put(PROVIDED, providedValue);

            details.put(SIDENUMBER, SIDE_YARD1_DESC);

            if (sideYard1Result.status) {
                details.put(STATUS, Result.Accepted.getResultVal());
            } else {
                details.put(STATUS, Result.Not_Accepted.getResultVal());
            }

//            scrutinyDetail.getDetail().add(details);
//            scrutinyDetailList.add(scrutinyDetail);
            scrutinyDetailSideYard1.getDetail().add(details);
            scrutinyDetailList.add(scrutinyDetailSideYard1);
            //pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }

        if (errors.isEmpty()) {
        	if (sideYard2Result != null 
        	        && sideYard2Result.actualDistance.toString() != null 
        	        && sideYard2Result.actualDistance.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, String> detailsSideYard2 = new HashMap<>();
                detailsSideYard2.put(RULE_NO, sideYard2Result.subRule);
                detailsSideYard2.put(LEVEL,
                        sideYard2Result.level != null ? sideYard2Result.level.toString() : "");
                
                String occupancy = sideYard2Result.occupancy;
    			if (occupancy != null && occupancy.contains(",")) {
    			    occupancy = occupancy.split(",")[0].trim();
    			}
    			detailsSideYard2.put(OCCUPANCY, occupancy);
                
                //detailsSideYard2.put(OCCUPANCY, sideYard2Result.occupancy);
                
                detailsSideYard2.put(SIDENUMBER, SIDE_YARD2_DESC);
                
                String permissableValueWithPercentage;
    			String providedValue;
    			
    			if(sideYard2Result.occupancyCode !=null && 
    					(sideYard2Result.occupancyCode.equalsIgnoreCase("A") || 
    					sideYard2Result.occupancyCode.equalsIgnoreCase("A-R")	||
    					sideYard2Result.occupancyCode.equalsIgnoreCase("A-AIF")	||
    					sideYard2Result.occupancyCode.equalsIgnoreCase("A-AF") ||
    					sideYard2Result.occupancyCode.equalsIgnoreCase("G") ||
    					sideYard2Result.occupancyCode.equalsIgnoreCase("L") 
//    					sideYard2Result.occupancyCode.equalsIgnoreCase("G-GTKS") ||
//    					sideYard2Result.occupancyCode.equalsIgnoreCase("G-IT") ||
//    					sideYard2Result.occupancyCode.equalsIgnoreCase("G-F"))
    					)) {
    				permissableValueWithPercentage = sideYard2Result.expectedDistance.toString();
    			    providedValue = sideYard2Result.actualDistance.toString();
    			    detailsSideYard2.put("OccCode", sideYard2Result.occupancyCode);
    			    detailsSideYard2.put("isSetbackCombine", String.valueOf(sideYard2Result.isSetbackCombine));
    			}else if (sideYard2Result.setBackPercentage != null 
    			        && sideYard2Result.setBackPercentage.contains("m")) {							    
    			    permissableValueWithPercentage = sideYard2Result.setBackPercentage;
    			    providedValue = sideYard2Result.actualDistance.toString() + "m";
    			    detailsSideYard2.put("OccCode", sideYard2Result.occupancyCode);
    			    detailsSideYard2.put("isSetbackCombine", String.valueOf(sideYard2Result.isSetbackCombine));
    			} else {								
//    			    permissableValueWithPercentage = sideYard2Result.setBackPercentage 
//    			            + "% of the plot area (" 
//    			            + sideYard2Result.expectedDistance.toPlainString() + ")";
    			    permissableValueWithPercentage = sideYard2Result.setBackPercentage;    			            
    			    providedValue = sideYard2Result.actualDistance.toString();
    			    detailsSideYard2.put("OccCode", sideYard2Result.occupancyCode);
    			    detailsSideYard2.put("isSetbackCombine", String.valueOf(sideYard2Result.isSetbackCombine));
    			}

//                detailsSideYard2.put(FIELDVERIFIED, MINIMUMLABEL);
                //detailsSideYard2.put(PERMISSIBLE, sideYard2Result.expectedDistance.toString());
                //detailsSideYard2.put(PROVIDED, sideYard2Result.actualDistance.toString());
    			detailsSideYard2.put(PERMISSIBLE, permissableValueWithPercentage);
    			detailsSideYard2.put(PROVIDED, providedValue);
                // }
                if (sideYard2Result.status) {
                    detailsSideYard2.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    detailsSideYard2.put(STATUS, Result.Not_Accepted.getResultVal());
                }

//                scrutinyDetail.getDetail().add(detailsSideYard2);
//                scrutinyDetailList.add(scrutinyDetail);
                
                scrutinyDetailSideYard2.getDetail().add(detailsSideYard2);
                scrutinyDetailList.add(scrutinyDetailSideYard2);
                //pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }
        }
    }

    private void exemptSideYardForAAndF(final Plan pl, Block block, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result, ScrutinyDetail scrutinyDetailSideYard1, ScrutinyDetail scrutinyDetailSideYard2) {
        for (final Occupancy occupancy : block.getBuilding().getTotalArea()) {
            //scrutinyDetail.setKey("Block_" + block.getName() + "_" + "Side Setback");
            scrutinyDetailSideYard1.setKey("Block_" + block.getName() + "_" + "Side Setback");
        	scrutinyDetailSideYard2.setKey("Block_" + block.getName() + "_" + "Side Setback");
            if (occupancy.getTypeHelper().getType() != null) {
            	if(A.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())
                    || F.equalsIgnoreCase(occupancy.getTypeHelper().getType().getCode())) {
                if (pl.getErrors().containsKey(SIDE_YARD_2_NOTDEFINED)) {
                    pl.getErrors().remove(SIDE_YARD_2_NOTDEFINED);
                }
                if (pl.getErrors().containsKey(SIDE_YARD_1_NOTDEFINED)) {
                    pl.getErrors().remove(SIDE_YARD_1_NOTDEFINED);
                }
                if (pl.getErrors().containsKey(SIDE_YARD_DESC)) {
                    pl.getErrors().remove(SIDE_YARD_DESC);
                }
                if (pl.getErrors().containsValue("BLK_" + block.getNumber() + "_LVL_0_SIDE_SETBACK1 not defined in the plan.")) {
                    pl.getErrors().remove("", "BLK_" + block.getNumber() + "_LVL_0_SIDE_SETBACK1 not defined in the plan.");
                }
                if (pl.getErrors().containsValue("BLK_" + block.getNumber() + "_LVL_0_SIDE_SETBACK2 not defined in the plan.")) {
                    pl.getErrors().remove("", "BLK_" + block.getNumber() + "_LVL_0_SIDE_SETBACK2 not defined in the plan.");
                }
                if (pl.getErrors().containsValue(
    					"Side Setback 1 of block" + block.getNumber() + "at level zero  not defined in the plan.")) {
    				pl.getErrors().remove("",
    						"Side Setback 1 of block" + block.getNumber() + "at level zero  not defined in the plan.");
    			}

            }

            compareSideYard2Result(block.getName(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, occupancy.getTypeHelper(), sideYard2Result, true, RULE_35, SIDE_YARD_DESC,
                    0);
            compareSideYard1Result(block.getName(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, occupancy.getTypeHelper(), sideYard1Result, true, RULE_35, SIDE_YARD_DESC,
                    0);
        }
        }
    }

    private void checkSideYardUptoTenMts(final Plan pl, Building building, BigDecimal buildingHeight, String blockName,
            Integer level, final Plot plot, final double min, final double max, double minMeanlength,
            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result) {

        String rule = SIDE_YARD_DESC;
        String subRule = RULE_35;
        Boolean valid2 = false;
        Boolean valid1 = false;
        BigDecimal side2val = BigDecimal.ZERO;
        BigDecimal side1val = BigDecimal.ZERO;

        BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();

        if (mostRestrictiveOccupancy.getSubtype() != null && (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
                || A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
                || A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
            if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
                    && StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
                    && DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
                    && pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
                checkCommercialUptoSixteen(blockName, level, min, max, minMeanlength, maxMeanLength,
                        mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, rule, DxfFileConstants.RULE_28,
                        valid2, valid1, side2val, side1val, widthOfPlot);
            } else {
                checkResidentialUptoTenMts(pl, blockName, level, min, max, minMeanlength, maxMeanLength,
                        mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, rule, subRule, valid2, valid1,
                        side2val, side1val, widthOfPlot);
            }
        }else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            checkCommercialUptoSixteen(blockName, level, min, max, minMeanlength, maxMeanLength,
                    mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, rule, subRule, valid2, valid1, side2val,
                    side1val, widthOfPlot);
        }
    }

    private void checkResidentialUptoTenMts(Plan pl, String blockName, Integer level, final double min, final double max,
            double minMeanlength, double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy,
            SideYardResult sideYard1Result, SideYardResult sideYard2Result, String rule, String subRule, Boolean valid2,
            Boolean valid1, BigDecimal side2val, BigDecimal side1val, BigDecimal widthOfPlot) {
        if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
            if (pl.getErrors().containsKey(SIDE_YARD_2_NOTDEFINED)) {
                pl.getErrors().remove(SIDE_YARD_2_NOTDEFINED);
            }
            if (pl.getErrors().containsKey(SIDE_YARD_1_NOTDEFINED)) {
                pl.getErrors().remove(SIDE_YARD_1_NOTDEFINED);
            }
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
            side2val = SIDEVALUE_SEVENTYFIVE;
            side1val = SIDEVALUE_SEVENTYFIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
            side2val = SIDEVALUE_ONE;
            side1val = SIDEVALUE_ONE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
            side2val = SIDEVALUE_ONEPOINTFIVE;
            side1val = SIDEVALUE_ONEPOINTFIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
            side2val = SIDEVALUE_ONEPOINTFIVE;
            side1val = SIDEVALUE_ONEPOINTFIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
            side2val = SIDEVALUE_TWO;
            side1val = SIDEVALUE_TWO;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
            side2val = SIDEVALUE_TWO;
            side1val = SIDEVALUE_TWO;
        }

        if (max >= side1val.doubleValue())
            valid1 = true;
        if (min >= side2val.doubleValue())
            valid2 = true;

        compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
                BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
                level);
        compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
                BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
                level);
    }

    private void checkSideYardBasement(final Plan pl, Building building, BigDecimal buildingHeight, String blockName,
            Integer level, final Plot plot, final double min, final double max, double minMeanlength,
            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result) {

        String rule = SIDE_YARD_DESC;
        String subRule = RULE_47;
        Boolean valid2 = false;
        Boolean valid1 = false;
        BigDecimal side2val = BigDecimal.ZERO;
        BigDecimal side1val = BigDecimal.ZERO;

        if ((mostRestrictiveOccupancy.getSubtype() != null
                && A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
                || A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))
                || F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            if (plot.getArea().compareTo(BigDecimal.valueOf(PLOTAREA_300)) <= 0) {
                side2val = SIDEVALUE_THREE;
                side1val = SIDEVALUE_THREE;

                if (max >= side1val.doubleValue())
                    valid1 = true;
                if (min >= side2val.doubleValue())
                    valid2 = true;

                rule = BSMT_SIDE_YARD_DESC;

                compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
                        BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule,
                        rule, level);
                compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
                        BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule,
                        rule, level);
            }
        }
    }

//    private void checkSideYardForIndustrial(final Plan pl, Building building, BigDecimal buildingHeight,
//            String blockName, Integer level, final Plot plot, final double min, final double max, double minMeanlength,
//            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
//            SideYardResult sideYard2Result) {
//
//        String rule = SIDE_YARD_DESC;
//        String subRule = RULE_35;
//        Boolean valid2 = false;
//        Boolean valid1 = false;
//        BigDecimal side2val = BigDecimal.ZERO;
//        BigDecimal side1val = BigDecimal.ZERO;
//
//        BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();
//        BigDecimal plotArea = pl.getPlot().getArea();
//
//        if (plotArea.compareTo(BigDecimal.valueOf(550)) < 0) {
//            if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
//                side2val = SIDEVALUE_ONEPOINTFIVE;
//                side1val = SIDEVALUE_ONEPOINTFIVE;
//            } else if (widthOfPlot.compareTo(BigDecimal.valueOf(12)) <= 0) {
//                side2val = SIDEVALUE_TWO;
//                side1val = SIDEVALUE_TWO;
//            } else if (widthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
//                side2val = SIDEVALUE_THREE;
//                side1val = SIDEVALUE_THREE;
//            } else if (widthOfPlot.compareTo(BigDecimal.valueOf(18)) <= 0) {
//                side2val = SIDEVALUE_FOUR;
//                side1val = SIDEVALUE_FOUR;
//            } else if (widthOfPlot.compareTo(BigDecimal.valueOf(18)) > 0) {
//                side2val = SIDEVALUE_FOURPOINTFIVE;
//                side1val = SIDEVALUE_FOURPOINTFIVE;
//            }
//        } else if (plotArea.compareTo(BigDecimal.valueOf(550)) > 0
//                && plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
//            side2val = SIDEVALUE_FOURPOINTFIVE;
//            side1val = SIDEVALUE_FOURPOINTFIVE;
//        } else if (plotArea.compareTo(BigDecimal.valueOf(1000)) > 0
//                && plotArea.compareTo(BigDecimal.valueOf(5000)) <= 0) {
//            side2val = SIDEVALUE_SIX;
//            side1val = SIDEVALUE_SIX;
//        } else if (plotArea.compareTo(BigDecimal.valueOf(5000)) > 0
//                && plotArea.compareTo(BigDecimal.valueOf(30000)) <= 0) {
//            side2val = SIDEVALUE_NINE;
//            side1val = SIDEVALUE_NINE;
//        } else if (plotArea.compareTo(BigDecimal.valueOf(30000)) > 0) {
//            side2val = SIDEVALUE_TEN;
//            side1val = SIDEVALUE_TEN;
//        }
//
//        if (max >= side1val.doubleValue())
//            valid1 = true;
//        if (min >= side2val.doubleValue())
//            valid2 = true;
//
//        compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
//                BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
//                level);
//        compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
//                BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
//                level);
//
//    }
    
    private void checkSideYardForPublicBuilding(final Plan pl, Building building, BigDecimal buildingHeight,
            String blockName, Integer level, final Plot plot, final double min, final double max, double minMeanlength,
            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy,
            SideYardResult sideYard1Result, SideYardResult sideYard2Result, Yard sideYard2, Yard sideYard1, SetBack setback) {

        String rule = SIDE_YARD_DESC;
        String subRule = "4.18";
        Boolean valid1 = false;
        Boolean valid2 = false;

        BigDecimal side1val = BigDecimal.ZERO;
        BigDecimal side2val = BigDecimal.ZERO;
        
        if(mostRestrictiveOccupancy != null &&
				(L.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))) {
      	if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
  		    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
  		            pl.getMdmsMasterData().get("masterMdmsData"),
  		            MdmsFilter.SIDE_SETBACK_PATH,
  		            BigDecimal.class
  		    );
  		    if (scOpt.isPresent()) {
  		        BigDecimal mdmsValue = scOpt.get();
  		        LOG.info("Side Setback Value from MDMS : " + mdmsValue);		        
  		        side1val = mdmsValue;	
  		        side2val =mdmsValue;
  		    }else {
  		    	LOG.error("No value found from mdms for the side setback");
  		    }
  		}
      	
      }
        
//        // Validation checks
//        if (BigDecimal.valueOf(max).compareTo(side1val) >= 0)
//            valid1 = true;
//        if (BigDecimal.valueOf(min).compareTo(side2val) >= 0)
//            valid2 = true;

	        if (BigDecimal.valueOf(max).compareTo(side1val) >= 0)
	            valid1 = true;
	        if (BigDecimal.valueOf(min).compareTo(side2val) >= 0)
	            valid2 = true;		    
	        if(sideYard2!=null) {	          
	        	compareSideYard2ResultForPublicBuilding(blockName, side2val, BigDecimal.valueOf(sideYard2.getMinimumDistance().doubleValue()), BigDecimal.ZERO,
	                      BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result,
	                      valid2, subRule, rule, level);        	
	        } 
	        if(sideYard1!=null) {
	        	compareSideYard1ResultForPublicBuilding(blockName, side1val, BigDecimal.valueOf(sideYard1.getMinimumDistance().doubleValue()), BigDecimal.ZERO,
				          BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result,
				          valid1, subRule, rule, level);
	        }
	    

    }
    
    private void checkSideYardForIndustrial(final Plan pl, Building building, BigDecimal buildingHeight,
            String blockName, Integer level, final Plot plot, final double min, final double max, double minMeanlength,
            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy,
            SideYardResult sideYard1Result, SideYardResult sideYard2Result, Yard sideYard2, Yard sideYard1, SetBack setback) {

        String rule = SIDE_YARD_DESC;
        String subRule = "4.14";
        Boolean valid1 = false;
        Boolean valid2 = false;

        BigDecimal side1val = BigDecimal.ZERO;
        BigDecimal side2val = BigDecimal.ZERO;
        Boolean isNbcType=false;

        BigDecimal plotArea = pl.getPlot().getArea();
        String occCode = mostRestrictiveOccupancy != null ? mostRestrictiveOccupancy.getType().getCode() : null;

//        if (occCode != null) {
//            switch (occCode) {
//                case "G-SP": // Sports Industry
//                case "G-RS": // Retail Service Industry
//                case "G-H":  // Hazard Industries
//                case "G-S":  // Storage
//                case "G-F":  // Factory
//                case "G-I":  // Industrial
//                    // 15% of plot area
//                    side1val = plotArea.multiply(BigDecimal.valueOf(0.15)).setScale(2, RoundingMode.HALF_UP);
//                    side2val = plotArea.multiply(BigDecimal.valueOf(0.15)).setScale(2, RoundingMode.HALF_UP);
//                    sideYard1Result.setBackPercentage = "15";
//                    sideYard2Result.setBackPercentage = "15";
//                    break;
//
//                case "G-W": // Warehouse
//                    // 35% of plot area
//                    side1val = plotArea.multiply(BigDecimal.valueOf(0.35)).setScale(2, RoundingMode.HALF_UP);
//                    side2val = plotArea.multiply(BigDecimal.valueOf(0.35)).setScale(2, RoundingMode.HALF_UP);
//                    sideYard1Result.setBackPercentage = "15";
//                    sideYard2Result.setBackPercentage = "15";
//                    break;
//
//                case "G-K":  // Knitwear Industry
//                case "G-T":  // Textile Industry
//                case "G-IT": // Information Technology
//                case "G-GI": // General Industry
//                    // Follow NBC → height based
//                    side1val = getNBCSideYardByHeight(buildingHeight);
//                    side2val = getNBCSideYardByHeight(buildingHeight);
//                    isNbcType=true;
//                    sideYard1Result.setBackPercentage = side1val.toPlainString().concat("m");
//                    sideYard2Result.setBackPercentage = side2val.toPlainString().concat("m");
//                    break;
//
//                default:
//                    // fallback to NBC
//                    side1val = getNBCSideYardByHeight(buildingHeight);
//                    side2val = getNBCSideYardByHeight(buildingHeight);
//                    isNbcType=true;
//                    sideYard1Result.setBackPercentage = side1val.toPlainString().concat("m");
//                    sideYard2Result.setBackPercentage = side2val.toPlainString().concat("m");
//            }
//        } else {
//            // no occupancy → fallback NBC
//            //side1val = getNBCSideYardByHeight(buildingHeight);
//            //side2val = getNBCSideYardByHeight(buildingHeight);
//        }
//        if(mostRestrictiveOccupancy != null &&
//				(G_GTKS.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()) 
//						|| G_IT.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
//        	if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
//    		    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
//    		            pl.getMdmsMasterData().get("masterMdmsData"),
//    		            MdmsFilter.SIDE_SETBACK_PATH,
//    		            BigDecimal.class
//    		    );
//    		    if (scOpt.isPresent()) {
//    		        BigDecimal mdmsValue = scOpt.get();
//    		        LOG.info("Side Setback Value from MDMS : " + mdmsValue);
//    		        BigDecimal oneForthHeight = buildingHeight.divide(
//    		                BigDecimal.valueOf(FIVE_MTR), 2, RoundingMode.HALF_UP
//    		        );
//    		        LOG.info("One forth of building height is : " + oneForthHeight);		        
//    		        side1val = oneForthHeight.max(mdmsValue);	
//    		        side2val = oneForthHeight.max(mdmsValue);
//    		    }else {
//    		    	LOG.error("No value found from mdms for the side setback");
//    		    }
//    		}
//        	
//        }else {
//        	Optional<List> fullListOpt = BpaMdmsUtil.extractMdmsValue(
//	        		pl.getMdmsMasterData().get("masterMdmsData"), 
//	        		MdmsFilter.LIST_SIDE_SETBACK_PATH, List.class);
//	        
//	        if (fullListOpt.isPresent()) {
//	             List<Map<String, Object>> frontSetBacks = (List<Map<String, Object>>) fullListOpt.get();
//	             
//	             // Extraction 1B: Apply the tiered setback logic
//	             Optional<BigDecimal> requiredSetback = BpaMdmsUtil.findSetbackValueByHeight(frontSetBacks, buildingHeight);
//
//	             requiredSetback.ifPresent(
//	                 setbackRear -> LOG.info("Setback for Height " + buildingHeight + ": " + setbackRear)
//	             );
//	             side1val = requiredSetback.get().abs().stripTrailingZeros();	
// 		        side2val = requiredSetback.get().abs().stripTrailingZeros();
//	        }else {
//	        	LOG.error("No value found from mdms for the side setback");
//	        }
//        }

        if(mostRestrictiveOccupancy != null &&
				(G.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))) {
      	if (pl.getMdmsMasterData().get("masterMdmsData") != null) {
  		    Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
  		            pl.getMdmsMasterData().get("masterMdmsData"),
  		            MdmsFilter.SIDE_SETBACK_PATH,
  		            BigDecimal.class
  		    );
  		    if (scOpt.isPresent()) {
  		        BigDecimal mdmsValue = scOpt.get();
  		        LOG.info("Side Setback Value from MDMS : " + mdmsValue);
  		        BigDecimal oneForthHeight = buildingHeight.divide(
  		                BigDecimal.valueOf(FIVE_MTR), 2, RoundingMode.HALF_UP
  		        );
  		        LOG.info("One forth of building height is : " + oneForthHeight);		        
  		        side1val = oneForthHeight.max(mdmsValue);	
  		        side2val = oneForthHeight.max(mdmsValue);
  		    }else {
  		    	LOG.error("No value found from mdms for the side setback");
  		    }
  		}
      	
      }
        
        // Validation checks
        if (BigDecimal.valueOf(max).compareTo(side1val) >= 0)
            valid1 = true;
        if (BigDecimal.valueOf(min).compareTo(side2val) >= 0)
            valid2 = true;
        
        if(!isNbcType) {
	    	// ✅ Validate using common function
		    //valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);
		 // Validation checks
	        if (BigDecimal.valueOf(max).compareTo(side1val) >= 0)
	            valid1 = true;
	        if (BigDecimal.valueOf(min).compareTo(side2val) >= 0)
	            valid2 = true;		    
		 // Save results 
	        if(sideYard2!=null) {
	          //compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
	          //BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result,
	          //valid2, subRule, rule, level);
	        	compareSideYard2ResultForIndustry(blockName, side2val, BigDecimal.valueOf(sideYard2.getMinimumDistance().doubleValue()), BigDecimal.ZERO,
	                      BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result,
	                      valid2, subRule, rule, level);        	
	        }      

	        if(sideYard1!=null) {
	          //compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(min), BigDecimal.ZERO,
	          //BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result,
	          //valid1, subRule, rule, level);
	        	compareSideYard1ResultForIndustry(blockName, side1val, BigDecimal.valueOf(sideYard1.getMinimumDistance().doubleValue()), BigDecimal.ZERO,
				          BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result,
				          valid1, subRule, rule, level);
	        }
	    }else {
	    	// ✅ Validate using common function
	    	//valid = validateMinimumAndMeanValue(min, setback.getRearYard().getWidth(), minVal, meanVal);
	    	
	    	// Validation checks
	        if (BigDecimal.valueOf(max).compareTo(side1val) >= 0)
	            valid1 = true;
	        if (BigDecimal.valueOf(min).compareTo(side2val) >= 0)
	            valid2 = true;
	    	
	    	if (setback.getSideYard1().getWidth().compareTo(side1val) >= 0 && setback.getSideYard2().getWidth().compareTo(side2val) >= 0) {		    
			}else {
				valid1=false;
				valid2 = false;
			}    	
		    
		 // Save results 
	        if(sideYard2!=null) {
	          //compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
	          //BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result,
	          //valid2, subRule, rule, level);
	        	  //compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(sideYard2.getArea().doubleValue()), BigDecimal.ZERO,
	                      //BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result,
	                      //valid2, subRule, rule, level);   
	        	compareSideYard2Result(blockName, side2val, setback.getSideYard2().getWidth(), BigDecimal.ZERO,
	                      BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result,
	                      valid2, subRule, rule, level);
	        }      

	        if(sideYard1!=null) {
	          //compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(min), BigDecimal.ZERO,
	          //BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result,
	          //valid1, subRule, rule, level);
				  //compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(sideYard1.getArea().doubleValue()), BigDecimal.ZERO,
				          //BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result,
				          //valid1, subRule, rule, level);
	        	compareSideYard1Result(blockName, side1val, setback.getSideYard1().getWidth(), BigDecimal.ZERO,
				          BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result,
				          valid1, subRule, rule, level);
	        }
	    }

//        // Save results 
//        if(sideYard2!=null) {
//          //compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
//          //BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result,
//          //valid2, subRule, rule, level);
//        	  compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(sideYard2.getArea().doubleValue()), BigDecimal.ZERO,
//                      BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result,
//                      valid2, subRule, rule, level);        	
//        }      
//
//        if(sideYard1!=null) {
//          //compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(min), BigDecimal.ZERO,
//          //BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result,
//          //valid1, subRule, rule, level);
//			  compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(sideYard1.getArea().doubleValue()), BigDecimal.ZERO,
//			          BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result,
//			          valid1, subRule, rule, level);
//        }

    }

    /**
     * NBC side yard rules → based on height
     */
    private BigDecimal getNBCSideYardByHeight(BigDecimal buildingHeight) {
        if (buildingHeight == null) return SIDEVALUE_THREE;

        if (buildingHeight.compareTo(BigDecimal.valueOf(10)) <= 0) {
            return SIDEVALUE_THREE; // 3m
        } else if (buildingHeight.compareTo(BigDecimal.valueOf(15)) <= 0) {
            return SIDEVALUE_FIVE; // 5m
        } else if (buildingHeight.compareTo(BigDecimal.valueOf(18)) <= 0) {
            return SIDEVALUE_SIX;  // 6m
        } else {
            return SIDEVALUE_SIX;  // Above 24m = 6m
        }
    }


    private void checkSideYardForOtherOccupancies(final Plan pl, Building building, BigDecimal buildingHeight,
            String blockName, Integer level, final Plot plot, final double min, final double max, double minMeanlength,
            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result, HashMap<String, String> errors, Yard sideYard1, 
            Yard sideYard2) {

        String rule = SIDE_YARD_DESC;
        String subRule = RULE_35;
        Boolean valid2 = false;
        Boolean valid1 = false;
        BigDecimal side2val = BigDecimal.ZERO;
        BigDecimal side1val = BigDecimal.ZERO;

        // Educational
        if (mostRestrictiveOccupancy.getType() != null && B.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            side2val = SIDEVALUE_SIX;
            side1val = SIDEVALUE_SIX;
            subRule = RULE_37_TWO_A;
        } // Institutional
        if (mostRestrictiveOccupancy.getType() != null && B.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            side2val = SIDEVALUE_SIX;
            side1val = SIDEVALUE_SIX;
            subRule = RULE_37_TWO_B;
        } // Assembly
        if (mostRestrictiveOccupancy.getType() != null && D.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            side2val = SIDEVALUE_SIX;
            side1val = SIDEVALUE_SIX;
            subRule = RULE_37_TWO_C;
        } // Malls and multiplex
        if (mostRestrictiveOccupancy.getType() != null && D.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            side2val = SIDEVALUE_SEVEN;
            side1val = SIDEVALUE_SEVEN;
            subRule = RULE_37_TWO_D;
        } // Hazardous
        if (mostRestrictiveOccupancy.getType() != null && I.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            side2val = SIDEVALUE_NINE;
            side1val = SIDEVALUE_NINE;
            subRule = RULE_37_TWO_G;
        } // Affordable
        if (mostRestrictiveOccupancy.getType() != null && A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            side2val = SIDEVALUE_THREE;
            side1val = SIDEVALUE_THREE;
            subRule = RULE_37_TWO_H;
        }
        // IT,ITES
        if (mostRestrictiveOccupancy.getType() != null && F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
        	side2val = getMinValueForCommercialFromMdms(pl, plot.getArea(), errors, buildingHeight, sideYard1Result, sideYard2Result);
        	side1val = getMinValueForCommercialFromMdms(pl, plot.getArea(), errors, buildingHeight, sideYard1Result, sideYard2Result);        	
            subRule = "4.7.4";;
        }
        
        if (max >= side1val.doubleValue())
            valid1 = true;
        if (min >= side2val.doubleValue())
            valid2 = true;


        if(sideYard1Result.setBackPercentage.contains("m") && sideYard1Result.setBackPercentage.contains("m")) {
	          compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
	          BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
	          level);
	          compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
	          BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
	          level);
        }else {
        
	        compareSideYard2Result(blockName, side2val, sideYard2.getArea(), BigDecimal.ZERO,
	                BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
	                level);
	        compareSideYard1Result(blockName, side1val, sideYard1.getArea(), BigDecimal.ZERO,
	                BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
	                level);
        }

    }

    private void checkSideYardUptoTwelveMts(final Plan pl, Building building, BigDecimal buildingHeight,
            String blockName, Integer level, final Plot plot, final double min, final double max, double minMeanlength,
            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result, HashMap<String, String> errors) {

        String rule = SIDE_YARD_DESC;
        String subRule = RULE_35;
        Boolean valid2 = false;
        Boolean valid1 = false;
        BigDecimal side2val = BigDecimal.ZERO;
        BigDecimal side1val = BigDecimal.ZERO;

        BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();

        if (mostRestrictiveOccupancy.getSubtype() != null && A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
                || A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
                || A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {
            if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
                    && StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
                    && DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
                    && pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
                checkCommercialUptoSixteen(blockName, level, min, max, minMeanlength, maxMeanLength,
                        mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, rule, DxfFileConstants.RULE_28,
                        valid2, valid1, side2val, side1val, widthOfPlot);
            } else {
                checkResidentialUptoTwelveMts(pl, blockName, level, min, max, minMeanlength, maxMeanLength,
                        mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, errors, rule, subRule, valid2,
                        valid1, side2val, side1val, widthOfPlot);
            }
        }else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            checkCommercialUptoSixteen(blockName, level, min, max, minMeanlength, maxMeanLength,
                    mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, rule, subRule, valid2, valid1, side2val,
                    side1val, widthOfPlot);
        }
    }

    private void checkResidentialUptoTwelveMts(final Plan pl, String blockName, Integer level, final double min,
            final double max, double minMeanlength, double maxMeanLength,
            final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result, HashMap<String, String> errors, String rule, String subRule, Boolean valid2,
            Boolean valid1, BigDecimal side2val, BigDecimal side1val, BigDecimal widthOfPlot) {
        if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
            errors.put("uptoTwelveHeightUptoTenWidthSideYard",
                    "No construction shall be permitted if width of plot is less than 10 and building height less than 12 having floors upto G+3.");
            pl.addErrors(errors);
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
            side2val = SIDEVALUE_ONEPOINTFIVE;
            side1val = SIDEVALUE_ONEPOINTFIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
            side2val = SIDEVALUE_ONEPOINTFIVE;
            side1val = SIDEVALUE_ONEPOINTFIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
            side2val = SIDEVALUE_TWO;
            side1val = SIDEVALUE_TWO;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
            side2val = SIDEVALUE_TWOPOINTFIVE;
            side1val = SIDEVALUE_TWOPOINTFIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
            side2val = SIDEVALUE_THREE;
            side1val = SIDEVALUE_THREE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
            side2val = SIDEVALUE_THREEPOINTSIX;
            side1val = SIDEVALUE_THREEPOINTSIX;
        }

        if (max >= side1val.doubleValue())
            valid1 = true;
        if (min >= side2val.doubleValue())
            valid2 = true;

        compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
                BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
                level);
        compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
                BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
                level);
    }

    private void checkSideYardUptoSixteenMts(final Plan pl, Building building, BigDecimal buildingHeight,
            String blockName, Integer level, final Plot plot, final double min, final double max, double minMeanlength,
            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result, HashMap<String, String> errors) {

        String rule = SIDE_YARD_DESC;
        String subRule = RULE_35;
        Boolean valid2 = false;
        Boolean valid1 = false;
        BigDecimal side2val = SIDEVALUE_ONE;
        BigDecimal side1val = SIDEVALUE_ONE_TWO;

        BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();

        if (mostRestrictiveOccupancy.getSubtype() != null && A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
                || A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
                || A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())) {
            if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
                    && StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
                    && DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
                    && pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
                checkCommercialUptoSixteen(blockName, level, min, max, minMeanlength, maxMeanLength,
                        mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, rule, DxfFileConstants.RULE_28,
                        valid2, valid1, side2val, side1val, widthOfPlot);
            } else {
                checkResidentialUptoSixteen(pl, blockName, level, min, max, minMeanlength, maxMeanLength,
                        mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, errors, subRule, valid2, valid1,
                        side2val, side1val, widthOfPlot);
            }
        } else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
            checkCommercialUptoSixteen(blockName, level, min, max, minMeanlength, maxMeanLength,
                    mostRestrictiveOccupancy, sideYard1Result, sideYard2Result, rule, subRule, valid2, valid1, side2val,
                    side1val, widthOfPlot);
        }
    }

    private void checkCommercialUptoSixteen(String blockName, Integer level, final double min, final double max,
            double minMeanlength, double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy,
            SideYardResult sideYard1Result, SideYardResult sideYard2Result, String rule, String subRule, Boolean valid2,
            Boolean valid1, BigDecimal side2val, BigDecimal side1val, BigDecimal widthOfPlot) {
        if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
            // NIL
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
            side2val = SIDEVALUE_TWO;
            side1val = SIDEVALUE_TWO;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
            side2val = SIDEVALUE_TWOPOINTFIVE;
            side1val = SIDEVALUE_TWOPOINTFIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
            side2val = SIDEVALUE_THREE;
            side1val = SIDEVALUE_THREE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
            side2val = SIDEVALUE_FOUR;
            side1val = SIDEVALUE_FOUR;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
            side2val = SIDEVALUE_FIVE;
            side1val = SIDEVALUE_FIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
            side2val = SIDEVALUE_SIX;
            side1val = SIDEVALUE_SIX;
        }

        if (max >= side1val.doubleValue())
            valid1 = true;
        if (min >= side2val.doubleValue())
            valid2 = true;

        compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
                BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
                level);
        compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
                BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
                level);
    }

    private void checkResidentialUptoSixteen(final Plan pl, String blockName, Integer level, final double min,
            final double max, double minMeanlength, double maxMeanLength,
            final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result, HashMap<String, String> errors, String rule, Boolean valid2, Boolean valid1,
            BigDecimal side2val, BigDecimal side1val, BigDecimal widthOfPlot) {
        if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) <= 0) {
            errors.put("uptoSixteenHeightUptoTenWidthSideYard",
                    "No construction shall be permitted if width of plot is less than 10 and building height less than 16 having floors upto G+4.");
            pl.addErrors(errors);
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(10)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(15)) <= 0) {
            side2val = SIDEVALUE_ONEPOINTEIGHT;
            side1val = SIDEVALUE_ONEPOINTEIGHT;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(15)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(21)) <= 0) {
            side2val = SIDEVALUE_TWO;
            side1val = SIDEVALUE_TWO;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(21)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(27)) <= 0) {
            side2val = SIDEVALUE_TWOPOINTFIVE;
            side1val = SIDEVALUE_TWOPOINTFIVE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(27)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(33)) <= 0) {
            side2val = SIDEVALUE_THREE;
            side1val = SIDEVALUE_THREE;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(33)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(39)) <= 0) {
            side2val = SIDEVALUE_THREEPOINTSIX;
            side1val = SIDEVALUE_THREEPOINTSIX;
        } else if (widthOfPlot.compareTo(BigDecimal.valueOf(39)) > 0
                && widthOfPlot.compareTo(BigDecimal.valueOf(45)) <= 0) {
            side2val = SIDEVALUE_FOUR;
            side1val = SIDEVALUE_FOUR;
        }

        if (max >= side1val.doubleValue())
            valid1 = true;
        if (min >= side2val.doubleValue())
            valid2 = true;

        compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
                BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, rule,
                rule, level);
        compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
                BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, rule,
                rule, level);
    }

    private void checkSideYardAboveSixteenMts(final Plan pl, Building building, BigDecimal blockBuildingHeight,
            String blockName, Integer level, final Plot plot, final double min, final double max, double minMeanlength,
            double maxMeanLength, final OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result,
            SideYardResult sideYard2Result) {

        String rule = SIDE_YARD_DESC;
        String subRule = RULE_36;
        Boolean valid2 = false;
        Boolean valid1 = false;
        BigDecimal side2val = SIDEVALUE_ONE;
        BigDecimal side1val = SIDEVALUE_ONE_TWO;

        if (blockBuildingHeight.compareTo(BigDecimal.valueOf(16)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(19)) <= 0) {
            side2val = SIDEVALUE_FOURPOINTFIVE;
            side1val = SIDEVALUE_FOURPOINTFIVE;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(19)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(22)) <= 0) {
            side2val = SIDEVALUE_FOURPOINTFIVE;
            side1val = SIDEVALUE_FOURPOINTFIVE;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(22)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(25)) <= 0) {
            side2val = SIDEVALUE_FIVE;
            side1val = SIDEVALUE_FIVE;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(25)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(28)) <= 0) {
            side2val = SIDEVALUE_SIX;
            side1val = SIDEVALUE_SIX;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(28)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(31)) <= 0) {
            side2val = SIDEVALUE_SEVEN;
            side1val = SIDEVALUE_SEVEN;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(31)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(36)) <= 0) {
            side2val = SIDEVALUE_SEVEN;
            side1val = SIDEVALUE_SEVEN;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(36)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(41)) <= 0) {
            side2val = SIDEVALUE_EIGHT;
            side1val = SIDEVALUE_EIGHT;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(41)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(46)) <= 0) {
            side2val = SIDEVALUE_EIGHT;
            side1val = SIDEVALUE_EIGHT;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(46)) > 0
                && blockBuildingHeight.compareTo(BigDecimal.valueOf(51)) <= 0) {
            side2val = SIDEVALUE_NINE;
            side1val = SIDEVALUE_NINE;
        } else if (blockBuildingHeight.compareTo(BigDecimal.valueOf(51)) > 0) {
            side2val = SIDEVALUE_NINE;
            side1val = SIDEVALUE_NINE;
        }

        if (max >= side1val.doubleValue())
            valid1 = true;
        if (min >= side2val.doubleValue())
            valid2 = true;

        compareSideYard2Result(blockName, side2val, BigDecimal.valueOf(min), BigDecimal.ZERO,
                BigDecimal.valueOf(minMeanlength), mostRestrictiveOccupancy, sideYard2Result, valid2, subRule, rule,
                level);
        compareSideYard1Result(blockName, side1val, BigDecimal.valueOf(max), BigDecimal.ZERO,
                BigDecimal.valueOf(maxMeanLength), mostRestrictiveOccupancy, sideYard1Result, valid1, subRule, rule,
                level);

    }

    private void compareSideYard1ResultForIndustry(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
            BigDecimal expectedMeanDistance, BigDecimal actualMeanDistance,
            OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result, Boolean valid, String subRule,
            String rule, Integer level) {
        String occupancyName;
        String occupanyCode;
        if (mostRestrictiveOccupancy.getSubtype() != null) {
        	occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
        }else {
        	occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
    	}
        
        if (exptDistance.compareTo(sideYard1Result.expectedDistance) >= 0) {
            if (exptDistance.compareTo(sideYard1Result.expectedDistance) == 0) {
                sideYard1Result.rule = sideYard1Result.rule != null ? sideYard1Result.rule + "," + rule : rule;
                sideYard1Result.occupancy = sideYard1Result.occupancy != null
                        ? sideYard1Result.occupancy + "," + occupancyName
                        : occupancyName;
                sideYard1Result.occupancy = sideYard1Result.occupancy != null
                        ? sideYard1Result.occupancy + "," + occupanyCode
                        : occupanyCode;
            } else {
                sideYard1Result.rule = rule;
                sideYard1Result.occupancy = occupancyName;
                sideYard1Result.occupancyCode = occupanyCode;
            }

            sideYard1Result.subRule = subRule;
            sideYard1Result.blockName = blockName;
            sideYard1Result.level = level;
            sideYard1Result.actualDistance = actualDistance;
            sideYard1Result.expectedDistance = exptDistance;
            sideYard1Result.status = valid;
            sideYard1Result.occupancyCode = occupanyCode;
        }
    }

    private void compareSideYard2ResultForIndustry(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
            BigDecimal expectedMeanDistance, BigDecimal actualMeanDistance,
            OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard2Result, Boolean valid, String subRule,
            String rule, Integer level) {
        String occupancyName;
        String occupanyCode;
        if (mostRestrictiveOccupancy.getSubtype() != null) {
        	occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
        }else {
        	occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
    	}
        
        if (exptDistance.compareTo(sideYard2Result.expectedDistance) >= 0) {
            if (exptDistance.compareTo(sideYard2Result.expectedDistance) == 0) {
                sideYard2Result.rule = sideYard2Result.rule != null ? sideYard2Result.rule + "," + rule : rule;
                sideYard2Result.occupancy = sideYard2Result.occupancy != null
                        ? sideYard2Result.occupancy + "," + occupancyName
                        : occupancyName;
                sideYard2Result.occupancy = sideYard2Result.occupancy != null
                        ? sideYard2Result.occupancy + "," + occupanyCode
                        : occupanyCode;
            } else {
                sideYard2Result.rule = rule;
                sideYard2Result.occupancy = occupancyName;
                sideYard2Result.occupancyCode = occupanyCode;
            }

            sideYard2Result.subRule = subRule;
            sideYard2Result.blockName = blockName;
            sideYard2Result.level = level;
            sideYard2Result.actualDistance = actualDistance;
            sideYard2Result.expectedDistance = exptDistance;
            sideYard2Result.status = valid;
            sideYard2Result.occupancyCode = occupanyCode;
        }
    }
    
    private void compareSideYard1Result(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
            BigDecimal expectedMeanDistance, BigDecimal actualMeanDistance,
            OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result, Boolean valid, String subRule,
            String rule, Integer level) {
        String occupancyName;
        String occupanyCode;
        if (mostRestrictiveOccupancy.getSubtype() != null) {
        	occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getSubtype().getCode();
        }else {
        	occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
    	}
        
        if (exptDistance.compareTo(sideYard1Result.expectedDistance) >= 0) {
            if (exptDistance.compareTo(sideYard1Result.expectedDistance) == 0) {
                sideYard1Result.rule = sideYard1Result.rule != null ? sideYard1Result.rule + "," + rule : rule;
                sideYard1Result.occupancy = sideYard1Result.occupancy != null
                        ? sideYard1Result.occupancy + "," + occupancyName
                        : occupancyName;
                sideYard1Result.occupancy = sideYard1Result.occupancy != null
                        ? sideYard1Result.occupancy + "," + occupanyCode
                        : occupanyCode;
            } else {
                sideYard1Result.rule = rule;
                sideYard1Result.occupancy = occupancyName;
                sideYard1Result.occupancyCode = occupanyCode;
            }

            sideYard1Result.subRule = subRule;
            sideYard1Result.blockName = blockName;
            sideYard1Result.level = level;
            sideYard1Result.actualDistance = actualDistance;
            sideYard1Result.expectedDistance = exptDistance;
            sideYard1Result.status = valid;
            sideYard1Result.occupancyCode = occupanyCode;
        }
    }
    
    private void compareSideYard1ResultForPublicBuilding(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
            BigDecimal expectedMeanDistance, BigDecimal actualMeanDistance,
            OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard1Result, Boolean valid, String subRule,
            String rule, Integer level) {
        String occupancyName;
        String occupanyCode;
        if (mostRestrictiveOccupancy.getSubtype() != null) {
        	occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
        }else {
        	occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
    	}
        
        if (exptDistance.compareTo(sideYard1Result.expectedDistance) >= 0) {
            if (exptDistance.compareTo(sideYard1Result.expectedDistance) == 0) {
                sideYard1Result.rule = sideYard1Result.rule != null ? sideYard1Result.rule + "," + rule : rule;
                sideYard1Result.occupancy = sideYard1Result.occupancy != null
                        ? sideYard1Result.occupancy + "," + occupancyName
                        : occupancyName;
                sideYard1Result.occupancy = sideYard1Result.occupancy != null
                        ? sideYard1Result.occupancy + "," + occupanyCode
                        : occupanyCode;
            } else {
                sideYard1Result.rule = rule;
                sideYard1Result.occupancy = occupancyName;
                sideYard1Result.occupancyCode = occupanyCode;
            }

            sideYard1Result.subRule = subRule;
            sideYard1Result.blockName = blockName;
            sideYard1Result.level = level;
            sideYard1Result.actualDistance = actualDistance;
            sideYard1Result.expectedDistance = exptDistance;
            sideYard1Result.status = valid;
            sideYard1Result.occupancyCode = occupanyCode;
        }
    }

    private void compareSideYard2ResultForPublicBuilding(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
            BigDecimal expectedMeanDistance, BigDecimal actualMeanDistance,
            OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard2Result, Boolean valid, String subRule,
            String rule, Integer level) {
        String occupancyName;
        String occupanyCode;
        if (mostRestrictiveOccupancy.getSubtype() != null) {
        	occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
        }else {
        	occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
    	}
        
        if (exptDistance.compareTo(sideYard2Result.expectedDistance) >= 0) {
            if (exptDistance.compareTo(sideYard2Result.expectedDistance) == 0) {
                sideYard2Result.rule = sideYard2Result.rule != null ? sideYard2Result.rule + "," + rule : rule;
                sideYard2Result.occupancy = sideYard2Result.occupancy != null
                        ? sideYard2Result.occupancy + "," + occupancyName
                        : occupancyName;
                sideYard2Result.occupancy = sideYard2Result.occupancy != null
                        ? sideYard2Result.occupancy + "," + occupanyCode
                        : occupanyCode;
            } else {
                sideYard2Result.rule = rule;
                sideYard2Result.occupancy = occupancyName;
                sideYard2Result.occupancyCode = occupanyCode;
            }

            sideYard2Result.subRule = subRule;
            sideYard2Result.blockName = blockName;
            sideYard2Result.level = level;
            sideYard2Result.actualDistance = actualDistance;
            sideYard2Result.expectedDistance = exptDistance;
            sideYard2Result.status = valid;
            sideYard2Result.occupancyCode = occupanyCode;
        }
    }

    private void compareSideYard2Result(String blockName, BigDecimal exptDistance, BigDecimal actualDistance,
            BigDecimal expectedMeanDistance, BigDecimal actualMeanDistance,
            OccupancyTypeHelper mostRestrictiveOccupancy, SideYardResult sideYard2Result, Boolean valid, String subRule,
            String rule, Integer level) {
        String occupancyName;
        String occupanyCode;
        if (mostRestrictiveOccupancy.getSubtype() != null) {
        	occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
			occupanyCode = mostRestrictiveOccupancy.getSubtype().getCode();
        }else {
        	occupancyName = mostRestrictiveOccupancy.getType().getName();
			occupanyCode = mostRestrictiveOccupancy.getType().getCode();
    	}
        
        if (exptDistance.compareTo(sideYard2Result.expectedDistance) >= 0) {
            if (exptDistance.compareTo(sideYard2Result.expectedDistance) == 0) {
                sideYard2Result.rule = sideYard2Result.rule != null ? sideYard2Result.rule + "," + rule : rule;
                sideYard2Result.occupancy = sideYard2Result.occupancy != null
                        ? sideYard2Result.occupancy + "," + occupancyName
                        : occupancyName;
                sideYard2Result.occupancy = sideYard2Result.occupancy != null
                        ? sideYard2Result.occupancy + "," + occupanyCode
                        : occupanyCode;
            } else {
                sideYard2Result.rule = rule;
                sideYard2Result.occupancy = occupancyName;
                sideYard2Result.occupancyCode = occupanyCode;
            }

            sideYard2Result.subRule = subRule;
            sideYard2Result.blockName = blockName;
            sideYard2Result.level = level;
            sideYard2Result.actualDistance = actualDistance;
            sideYard2Result.expectedDistance = exptDistance;
            sideYard2Result.status = valid;
            sideYard2Result.occupancyCode = occupanyCode;
        }
    }

    private void validateSideYardRule(final Plan pl) {

        for (Block block : pl.getBlocks()) {
            if (!block.getCompletelyExisting()) {
                Boolean sideYardDefined = false;
                for (SetBack setback : block.getSetBacks()) {
                    if (setback.getSideYard1() != null
                            && setback.getSideYard1().getMean().compareTo(BigDecimal.valueOf(0)) > 0) {
                        sideYardDefined = true;
                    } else if (setback.getSideYard2() != null
                            && setback.getSideYard2().getMean().compareTo(BigDecimal.valueOf(0)) > 0) {
                        sideYardDefined = true;
                    }
                }
                if (!sideYardDefined) {
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put(SIDE_YARD_DESC,
                            prepareMessage(OBJECTNOTDEFINED, SIDE_YARD_DESC + " for Block " + block.getName()));
                    pl.addErrors(errors);
                }
            }

        }

    }
    
    private BigDecimal getMinValueForCommercial(Plan pl, BigDecimal plotArea, HashMap<String, String> errors, 
			BigDecimal buildingHeight, SideYardResult sideYard1Result, SideYardResult sideYard2Result) {

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
	    	sideYard1Result.isSetbackCombine=true;
	    	sideYard2Result.isSetbackCombine=true;
		    if (plotArea.compareTo(BigDecimal.ZERO) > 0
		            && plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_41_82) <= 0) {	        
		        minVal = BigDecimal.ZERO; // Up to 41.82 → Not compulsory → keep ZERO
		    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_41_82) > 0
		            && plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_104_5) <= 0) {	        
		        minVal = plotArea.multiply(COMMERCIAL_SIDE_SETBACK_PERCENT_10); // >41.82 and <=104.5 → 10%
		        sideYard1Result.setBackPercentage = "10";
		        sideYard2Result.setBackPercentage = "10";
		    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_104_5) > 0
		            && plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_209) <= 0) {	        
		        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_20); // >104.5 and <=209 → 20%
		        sideYard1Result.setBackPercentage = "20";
		        sideYard2Result.setBackPercentage = "20";
		    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_209) > 0
		            && plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_418_21) <= 0) {	        
		        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_25); // >209 and <=418.21 → 25%
		        sideYard1Result.setBackPercentage = "25";
		        sideYard2Result.setBackPercentage = "25";
		    } else if (plotArea.compareTo(COMMERCIAL_PLOT_AREA_LIMIT_418_21) > 0) {	       
		        minVal = plotArea.multiply(COMMERCIAL_FRONT_SETBACK_PERCENT_30);  // >418.21 → 30%
		        sideYard1Result.setBackPercentage = "30";
		        sideYard2Result.setBackPercentage = "30";
		    }
	    }// Rule: If height >= 21, use setback rule
	    else {
	        if (buildingHeight.compareTo(BigDecimal.valueOf(21)) == 0) {
	            minVal = BigDecimal.valueOf(7);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(24)) <= 0) {
	            minVal = BigDecimal.valueOf(8);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(27)) <= 0) {
	            minVal = BigDecimal.valueOf(9);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(30)) <= 0) {
	            minVal = BigDecimal.valueOf(10);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(35)) <= 0) {
	            minVal = BigDecimal.valueOf(11);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(40)) <= 0) {
	            minVal = BigDecimal.valueOf(12);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(45)) <= 0) {
	            minVal = BigDecimal.valueOf(13);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(50)) <= 0) {
	            minVal = BigDecimal.valueOf(14);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        } else if (buildingHeight.compareTo(BigDecimal.valueOf(55)) >= 0) {
	            minVal = BigDecimal.valueOf(16);
	            sideYard1Result.setBackPercentage = minVal.toPlainString().concat("m");
		        sideYard2Result.setBackPercentage = minVal.toPlainString().concat("m");
	        }
	    }

	    return minVal.setScale(2, RoundingMode.HALF_UP);
	}

    private BigDecimal getMinValueForCommercialFromMdms(Plan pl, BigDecimal plotArea, HashMap<String, String> errors, 
			BigDecimal buildingHeight, SideYardResult sideYard1Result, SideYardResult sideYard2Result) {

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
	    	sideYard1Result.isSetbackCombine=true;
	    	sideYard2Result.isSetbackCombine=true;
	    	Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
		            pl.getMdmsMasterData().get("masterMdmsData"),
		            MdmsFilter.SIDE_SETBACK_PATH,
		            BigDecimal.class
		    );

		    if (scOpt.isPresent()) {
		        BigDecimal mdmsValue = scOpt.get();
		        LOG.info("Side Setback Value from MDMS : " + mdmsValue);
		        minVal = mdmsValue;
		    }	    
		    sideYard1Result.setBackPercentage = "10";
	        sideYard2Result.setBackPercentage = "10";
	    }else {
	    	sideYard1Result.isSetbackCombine=true;
	    	sideYard2Result.isSetbackCombine=true;
	    	 /* ======================================================
	         * LOW RISE BUILDINGS (Height ≤ 21 m)
	         * ====================================================== */	    	
	    	//minVal= getPermisableForCommericalBelow21m(plotArea,pl, sideYard1Result, sideYard2Result);
	    	Optional<BigDecimal> scOpt = BpaMdmsUtil.extractMdmsValue(
		            pl.getMdmsMasterData().get("masterMdmsData"),
		            MdmsFilter.SIDE_SETBACK_PATH,
		            BigDecimal.class
		    );

		    if (scOpt.isPresent()) {
		        BigDecimal mdmsValue = scOpt.get();
		        LOG.info("Side Setback Value from MDMS : " + mdmsValue);
		        minVal = mdmsValue;
		    }
		    sideYard1Result.setBackPercentage = "10";
	        sideYard2Result.setBackPercentage = "10";
	    }

	    return minVal.setScale(2, RoundingMode.HALF_UP);
	}
    
 // calculate permissible Rear setback value for commercial below 21 m height
 	private static BigDecimal getPermisableForCommericalBelow21m(BigDecimal plotArea, Plan pl, SideYardResult sideYard1Result, 
 			SideYardResult sideYard2Result) {
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
 	            .multiply(COMMERCIAL_SIDE_SETBACK_PERCENT_10)
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
 	    sideYard1Result.setBackPercentage = remainingPercent.stripTrailingZeros().toPlainString();
 	   sideYard2Result.setBackPercentage = remainingPercent.stripTrailingZeros().toPlainString();
 	    
 	    return minVal;
 	}
    
}