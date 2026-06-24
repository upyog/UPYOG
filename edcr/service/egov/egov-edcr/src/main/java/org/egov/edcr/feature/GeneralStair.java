package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Flight;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.StairLanding;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GeneralStair extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(GeneralStair.class);
    private static final String FLOOR = "Floor";
    private static final String RULE = "4.4.4";
    private static final String GENERAL_STAIRS_WIDTH_RULE = "6.4.3";
    private static final String GENERAL_STAIRS_MID_LANDING_RULE = "6.4.3";
    private static final String GENERAL_STAIRS_RISER_HEIGHT_RULE = "5.15.4";
    private static final String RULERISER = "5.15.4.1";
    private static final String RULETREAD = "5.15.3";
    private static final BigDecimal MAXIMUM_HEIGHT_0_19 = BigDecimal.valueOf(0.19);
    private static final String EXPECTED_NO_OF_RISER = "12";
    private static final String NO_OF_RISER_DESCRIPTION = "Maximum no of risers required per flight for general stair %s flight %s";
    private static final String MAX_RISER_HEIGHT_DESCRIPTION = "Maximum height of riser";
    private static final String WIDTH_DESCRIPTION = "Minimum width for general stair %s flight %s";
    private static final String TREAD_DESCRIPTION = "Minimum tread for general stair %s flight %s";
    private static final String NO_OF_RISERS = "Number of risers ";
    private static final String FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION = "Flight polyline is not defined in layer ";
    private static final String FLIGHT_LENGTH_DEFINED_DESCRIPTION = "Flight polyline length is not defined in layer ";
    private static final String FLIGHT_WIDTH_DEFINED_DESCRIPTION = "Flight polyline width is not defined in layer ";
    private static final String WIDTH_LANDING_DESCRIPTION = "Minimum width for general stair %s mid landing %s";
    private static final String FLIGHT_NOT_DEFINED_DESCRIPTION = "General stair flight is not defined in block %s floor %s";

    @Override
    public Plan validate(Plan plan) {
        return plan;
    }

    @Override
    public Plan process(Plan plan) {
    	
    	
        // validate(planDetail);
        HashMap<String, String> errors = new HashMap<>();
        blk: for (Block block : plan.getBlocks()) {
            int generalStairCount = 0;
            int totalLandings = 0;
            int totalFlights = 0;
            BigDecimal riserHeigt = BigDecimal.ZERO;
           BigDecimal flrHt = BigDecimal.ZERO;
           //BigDecimal totalLandingWidth = BigDecimal.ZERO;
           BigDecimal totalFlightWidth = BigDecimal.ZERO;
           //BigDecimal totalRisers = BigDecimal.ZERO;
           //BigDecimal totalSteps = BigDecimal.ZERO;

            if (block.getBuilding() != null) {
                /*
                 * if (Util.checkExemptionConditionForBuildingParts(block) ||
                 * Util.checkExemptionConditionForSmallPlotAtBlkLevel(planDetail.getPlot(), block)) { continue blk; }
                 */
                ScrutinyDetail scrutinyDetail2 = new ScrutinyDetail();
                scrutinyDetail2.addColumnHeading(1, RULE_NO);
                scrutinyDetail2.addColumnHeading(2, FLOOR);
                scrutinyDetail2.addColumnHeading(3, DESCRIPTION);
                scrutinyDetail2.addColumnHeading(4, PERMISSIBLE);
                scrutinyDetail2.addColumnHeading(5, PROVIDED);
                scrutinyDetail2.addColumnHeading(6, STATUS);
                scrutinyDetail2.setKey("Block_" + block.getNumber() + "_" + "General Stair - Width");

                ScrutinyDetail scrutinyDetail3 = new ScrutinyDetail();
                scrutinyDetail3.addColumnHeading(1, RULE_NO);
                scrutinyDetail3.addColumnHeading(2, FLOOR);
                scrutinyDetail3.addColumnHeading(3, DESCRIPTION);
                scrutinyDetail3.addColumnHeading(4, PERMISSIBLE);
                scrutinyDetail3.addColumnHeading(5, PROVIDED);
                scrutinyDetail3.addColumnHeading(6, STATUS);
                scrutinyDetail3.setKey("Block_" + block.getNumber() + "_" + "General Stair - Tread width");

                ScrutinyDetail scrutinyDetailRise = new ScrutinyDetail();
                scrutinyDetailRise.addColumnHeading(1, RULE_NO);
                scrutinyDetailRise.addColumnHeading(2, FLOOR);
                scrutinyDetailRise.addColumnHeading(3, DESCRIPTION);
                scrutinyDetailRise.addColumnHeading(4, PERMISSIBLE);
                scrutinyDetailRise.addColumnHeading(5, PROVIDED);
                scrutinyDetailRise.addColumnHeading(6, STATUS);
                scrutinyDetailRise.setKey("Block_" + block.getNumber() + "_" + "General Stair - Number of risers");

                ScrutinyDetail scrutinyDetailLanding = new ScrutinyDetail();
                scrutinyDetailLanding.addColumnHeading(1, RULE_NO);
                scrutinyDetailLanding.addColumnHeading(2, FLOOR);
                scrutinyDetailLanding.addColumnHeading(3, DESCRIPTION);
                scrutinyDetailLanding.addColumnHeading(4, PERMISSIBLE);
                scrutinyDetailLanding.addColumnHeading(5, PROVIDED);
                scrutinyDetailLanding.addColumnHeading(6, STATUS);
                scrutinyDetailLanding.setKey("Block_" + block.getNumber() + "_" + "General Stair - Mid landing");
                
                ScrutinyDetail scrutinyDetail4 = new ScrutinyDetail();
                scrutinyDetail4.addColumnHeading(1, RULE_NO);
                scrutinyDetail4.addColumnHeading(2, FLOOR);
                scrutinyDetail4.addColumnHeading(3, DESCRIPTION);
                scrutinyDetail4.addColumnHeading(4, PERMISSIBLE);
                scrutinyDetail4.addColumnHeading(5, PROVIDED);
                scrutinyDetail4.addColumnHeading(6, STATUS);
                scrutinyDetail4.setKey("Block_" + block.getNumber() + "_" + "General Stair - Riser Height");


                OccupancyTypeHelper mostRestrictiveOccupancyType = block.getBuilding() != null
                        ? block.getBuilding().getMostRestrictiveFarHelper()
                        : null;

                /*
                 * String occupancyType = mostRestrictiveOccupancy != null ? mostRestrictiveOccupancy.getOccupancyType() : null;
                 */
                int noOfFloors = block.getBuilding().getFloors().size();
                List<Floor> floors = block.getBuilding().getFloors();
                Floor currentFloor = null;
                List<String> stairAbsent = new ArrayList<>();
                // BigDecimal floorSize = block.getBuilding().getFloorsAboveGround();
//                for (Floor floor : floors) {
//                	currentFloor = floor;
//                    if (!floor.getTerrace()) {
//                    	
//
//                        boolean isTypicalRepititiveFloor = false;
//                        Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor,
//                                isTypicalRepititiveFloor);
//
//                        List<org.egov.common.entity.edcr.GeneralStair> generalStairs = floor.getGeneralStairs();
//
//                        int size = generalStairs.size();
//                        generalStairCount = generalStairCount + size;
//
//                        if (!generalStairs.isEmpty()) {
//                            for (org.egov.common.entity.edcr.GeneralStair generalStair : generalStairs) {
//                            	
//                         flrHt = generalStair.getFloorHeight();
//                         LOG.info("flrHt___" + flrHt);
//                            	   List<StairLanding> landings1 = generalStair.getLandings();
//                                   totalLandings += landings1.size();
//                                   
//                                   List<Flight> flights = generalStair.getFlights();
//                                   totalFlights += flights.size();
//                                   
//                                   for (Flight flight : flights) {
//                                     
//                                       
//                                       BigDecimal risers = flight.getNoOfRises();
//                                       totalRisers = totalRisers.add(risers);
//                                   }
//                                   LOG.info("total totalRisers : " + totalRisers);
//                                   
//                                   // Sum the landing widths
//                                   for (StairLanding landing : landings1) {
//                                       List<BigDecimal> widths = landing.getWidths();
//                                       if (!widths.isEmpty()) {
//                                           BigDecimal landingWidth = widths.stream().reduce(BigDecimal::min).get();
//                                           totalLandingWidth = totalLandingWidth.add(landingWidth);  // Add to total
//                                       }
//                                   }
//                                   
//                                   
//                                   LOG.info("total landings : " + totalLandingWidth);
//                                   
//                                   totalSteps = totalRisers.add(totalLandingWidth);
//                                   
//                                   LOG.info("total totalSteps :" + totalSteps);
//                                   
//                                   
//
//                                   
//                                {
//                                    validateFlight(plan, errors, block, scrutinyDetail2, scrutinyDetail3,
//                                            scrutinyDetailRise, mostRestrictiveOccupancyType, floor, typicalFloorValues,
//                                            generalStair, generalStairCount);
//
//                                    List<StairLanding> landings = generalStair.getLandings();
//                                    if (!landings.isEmpty()) {
//                                        validateLanding(plan, block, scrutinyDetailLanding, mostRestrictiveOccupancyType,
//                                                floor,
//                                                typicalFloorValues, generalStair, landings, errors);
//                                    } else {
//                                    	if (floor.getNumber() != generalStairCount - 1) //This condition because in top most floor stairs are not mandatory for punjab, 
//                                            //so removing the error if stairs are not defined in top mist floor
//                                        errors.put(
//                                                "General Stair landing not defined in block " + block.getNumber() + " floor "                                                        + floor.getNumber()
//                                                        + " stair " + generalStair.getNumber(),
//                                                "General Stair landing not defined in block " + block.getNumber() + " floor "
//                                                        + floor.getNumber()
//                                                        + " stair " + generalStair.getNumber());
//                                        plan.addErrors(errors);
//                                    }
//
//                                }
//                               
//                            }
//                           
//                        } else {
//                        	if (floor.getNumber() != generalStairCount)
//                        	{
//                            stairAbsent.add("Block " + block.getNumber() + " floor " + floor.getNumber());
//                        }}
//
//                    }
//                }
                for (Floor floor : floors) {
                	BigDecimal totalSteps = BigDecimal.ZERO;
                	BigDecimal totalLandingWidth = BigDecimal.ZERO;
                	BigDecimal totalRisers = BigDecimal.ZERO;

                    currentFloor = floor;

                    boolean isTypicalRepititiveFloor = false;

                    // Get typical floor details
                    Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor,
                            isTypicalRepititiveFloor);

                    // Detect if this is a repeated typical floor
                    boolean isTypicalRepeat = false;

                    if (typicalFloorValues != null && typicalFloorValues.containsKey("isTypicalRepititiveFloor")) {
                        Object flagObj = typicalFloorValues.get("isTypicalRepititiveFloor");
                        if (flagObj instanceof Boolean) {
                            isTypicalRepeat = (Boolean) flagObj;
                        }
                    }
                    
                    if (!floor.getTerrace()) {

//                        boolean isTypicalRepititiveFloor = false;
//
//                        // Get typical floor details
//                        Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor,
//                                isTypicalRepititiveFloor);
//
//                        // Detect if this is a repeated typical floor
//                        boolean isTypicalRepeat = false;
//
//                        if (typicalFloorValues != null && typicalFloorValues.containsKey("isTypicalRepititiveFloor")) {
//                            Object flagObj = typicalFloorValues.get("isTypicalRepititiveFloor");
//                            if (flagObj instanceof Boolean) {
//                                isTypicalRepeat = (Boolean) flagObj;
//                            }
//                        }

                        List<org.egov.common.entity.edcr.GeneralStair> generalStairs = floor.getGeneralStairs();
                        int size = generalStairs.size();
                        generalStairCount += size;

                        if (!generalStairs.isEmpty()) {

                            for (org.egov.common.entity.edcr.GeneralStair generalStair : generalStairs) {

                                flrHt = generalStair.getFloorHeight();
                                LOG.info("flrHt___" + flrHt);

                                List<StairLanding> landings1 = generalStair.getLandings();
                                totalLandings += landings1.size();

                                List<Flight> flights = generalStair.getFlights();
                                totalFlights += flights.size();

                                for (Flight flight : flights) {
                                    BigDecimal risers = flight.getNoOfRises();
                                    totalRisers = totalRisers.add(risers);
                                }

                                // Landing width sum
                                for (StairLanding landing : landings1) {
                                    List<BigDecimal> widths = landing.getWidths();
                                    if (!widths.isEmpty()) {
                                        BigDecimal landingWidth = widths.stream().reduce(BigDecimal::min).get();
                                        totalLandingWidth = totalLandingWidth.add(landingWidth);
                                    }
                                }

                                totalSteps = totalRisers.add(totalLandingWidth);
                                LOG.info("total totalSteps :" + totalSteps);

                                validateFlight(plan, errors, block, scrutinyDetail2, scrutinyDetail3,
                                        scrutinyDetailRise, mostRestrictiveOccupancyType, floor,
                                        typicalFloorValues, generalStair, generalStairCount);

                                List<StairLanding> landings = generalStair.getLandings();

                                if (!landings.isEmpty()) {

                                    validateLanding(plan, block, scrutinyDetailLanding, mostRestrictiveOccupancyType,
                                            floor, typicalFloorValues, generalStair, landings, errors);

                                } else {
                                    // Skip error for typical floors
                                    if (!isTypicalRepeat) {
                                        if (floor.getNumber() != generalStairCount - 1) {
                                            String key = "General Stair landing not defined in block " + block.getNumber()
                                                    + " floor " + floor.getNumber() + " stair " + generalStair.getNumber();
                                            errors.put(key, key);
                                            plan.addErrors(errors);
                                        }
                                    }
                                }
                            }

                        } else {
                            //Skip stairAbsent addition for typical repeated floors
                            if (!isTypicalRepeat) {
                                if (floor.getNumber() != generalStairCount) {
                                    stairAbsent.add(
                                            "Block " + block.getNumber() + " floor " + floor.getNumber());
                                }
                            }
                        }
                    }
                    
                    LOG.info("landnig : " + totalLandings);
                    LOG.info("flights : " + totalFlights);
                   
//                    if(flrHt != null) {
//    	                //BigDecimal riserHeight = flrHt.divide(totalSteps, 2, RoundingMode.HALF_UP);
//    	                BigDecimal riserHeight = BigDecimal.ZERO;
//    	            	if (flrHt != null && totalRisers != null && totalRisers.compareTo(BigDecimal.ZERO) > 0) {
//    	            	    riserHeight = flrHt.divide(totalRisers,2,RoundingMode.HALF_UP);
//    	            	    LOG.info("Calculated Riser Height (m): " + riserHeight);
//    	            	}
//    	            	
////    	                if (currentFloor != null) {
////    	                    // Use currentFloor.getNumber() if currentFloor is not null
////    	                    String floorNumber = "" + currentFloor.getNumber().toString();
////    	                    if (riserHeight.compareTo(MAXIMUM_HEIGHT_0_19) <= 0) {
////    	                        setReportOutputDetailsFloorStairWise(plan, RULE, floorNumber, MAX_RISER_HEIGHT_DESCRIPTION, "" + 0.19, "" + riserHeight, Result.Accepted.getResultVal(), scrutinyDetail4);
////    	                    } else {
////    	                        setReportOutputDetailsFloorStairWise(plan, RULE, floorNumber, MAX_RISER_HEIGHT_DESCRIPTION, "" + 0.19, "" + riserHeight, Result.Not_Accepted.getResultVal(), scrutinyDetail4);
////    	                    }
////    	                } else {
////    	                	// Use " " if currentFloor is null
////    	                    if (riserHeight.compareTo(MAXIMUM_HEIGHT_0_19) <= 0) {
////    	                        setReportOutputDetailsFloorStairWise(plan, RULE, " ", MAX_RISER_HEIGHT_DESCRIPTION, "" + 0.19, "" + riserHeight, Result.Accepted.getResultVal(), scrutinyDetail4);
////    	                    } else {
////    	                        setReportOutputDetailsFloorStairWise(plan, RULE, " ", MAX_RISER_HEIGHT_DESCRIPTION, "" + 0.19, "" + riserHeight, Result.Not_Accepted.getResultVal(), scrutinyDetail4);
////    	                    }
////    	                }
//    	            	// Floor number handling
//    	            	String floorNumber = (currentFloor != null && currentFloor.getNumber() != null)
//    	            	        ? currentFloor.getNumber().toString()
//    	            	        : " ";
//
//    	            	// Condition: riserHeight > 0 AND <= 0.19
//    	            	boolean isRiserHeightValid = riserHeight.compareTo(BigDecimal.ZERO) > 0 
//    	            			&& riserHeight.compareTo(MAXIMUM_HEIGHT_0_19) <= 0;
//
//    	            	setReportOutputDetailsFloorStairWise(plan, RULE, floorNumber, MAX_RISER_HEIGHT_DESCRIPTION,
//    	            			String.valueOf(MAXIMUM_HEIGHT_0_19),riserHeight.toString(), isRiserHeightValid
//    	            	                ? Result.Accepted.getResultVal()
//    	            	                : Result.Not_Accepted.getResultVal(),
//    	            	        scrutinyDetail4);
//                    }                    

                    if (flrHt != null) {

                    	BigDecimal riserHeight = BigDecimal.ZERO;

                        if (totalRisers != null && totalRisers.compareTo(BigDecimal.ZERO) > 0) {
                            riserHeight = flrHt.divide(totalRisers, 2, RoundingMode.HALF_UP);
                            LOG.info("Calculated Riser Height (m): {}", riserHeight);
                        }

                        String floorNumber = (currentFloor != null && currentFloor.getNumber() != null)
                                ? currentFloor.getNumber().toString()
                                : " ";

                        int currentFloorNo = (currentFloor != null && currentFloor.getNumber() != null)
                                ? currentFloor.getNumber().intValue()
                                : -1;

                        int lastFloorNo = noOfFloors - 1;

                        boolean isRiserProvided =
                                riserHeight != null && riserHeight.compareTo(BigDecimal.ZERO) > 0;

                        boolean isRiserHeightWithinLimit =
                                riserHeight.compareTo(MAXIMUM_HEIGHT_0_19) <= 0;

                        boolean isRiserHeightValid;

                        if (currentFloorNo < lastFloorNo) {
                            // Mandatory floors (0 to N-2)
                            isRiserHeightValid =
                                    isRiserProvided && isRiserHeightWithinLimit;
                        } else {
                            // Last floor (optional)
                            isRiserHeightValid =
                                    !isRiserProvided || isRiserHeightWithinLimit;
                        }

                        setReportOutputDetailsFloorStairWise(
                                plan, GENERAL_STAIRS_RISER_HEIGHT_RULE, floorNumber, MAX_RISER_HEIGHT_DESCRIPTION, MAXIMUM_HEIGHT_0_19.toString(),
                                riserHeight.toString(), isRiserHeightValid 
                                		? Result.Accepted.getResultVal()
                                        : Result.Not_Accepted.getResultVal(), scrutinyDetail4
                        );
                    }

                }

                

                if (!stairAbsent.isEmpty()) {
                    for (String error : stairAbsent) {
                        errors.put("General Stair " + error,
                                "General stair not defined in " + error);
                        plan.addErrors(errors);
                    }
                }

                if (generalStairCount == 0) {
                	if(floors.size()==1) {                		
                	}else {
                		errors.put("General Stair not defined in blk " + block.getNumber(),
                                "General Stair not defined in block " + block.getNumber()
                                        + ", it is mandatory for building with floors more than one.");
                        plan.addErrors(errors);
                	}
                    
                }
            }
        }

        return plan;
    }

    private void validateLanding(Plan plan, Block block, ScrutinyDetail scrutinyDetailLanding,
            OccupancyTypeHelper mostRestrictiveOccupancyType, Floor floor, Map<String, Object> typicalFloorValues,
            org.egov.common.entity.edcr.GeneralStair generalStair, List<StairLanding> landings, HashMap<String, String> errors) {
        for (StairLanding landing : landings) {
            List<BigDecimal> widths = landing.getWidths();
            if(!widths.isEmpty()) {
            BigDecimal landingWidth = widths.stream().reduce(BigDecimal::min).get();
            BigDecimal minWidth = BigDecimal.ZERO;
            boolean valid = false;
            

            if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
                minWidth = Util.roundOffTwoDecimal(landingWidth);
                BigDecimal minimumWidth = getRequiredLandingWidth(block, mostRestrictiveOccupancyType);

                if (minWidth.compareTo(minimumWidth) >= 0) {
                    valid = true;
                }
                String value = typicalFloorValues.get("typicalFloors") != null
                        ? (String) typicalFloorValues.get("typicalFloors")
                        : "" + floor.getNumber();

                if (valid) {
                    setReportOutputDetailsFloorStairWise(plan, GENERAL_STAIRS_MID_LANDING_RULE, value,
                            String.format(WIDTH_LANDING_DESCRIPTION, generalStair.getNumber(),
                                    landing.getNumber()),
                            minimumWidth.toString(),
                            String.valueOf(minWidth), Result.Accepted.getResultVal(),
                            scrutinyDetailLanding);
                } else {
                    setReportOutputDetailsFloorStairWise(plan, GENERAL_STAIRS_MID_LANDING_RULE, value,
                            String.format(WIDTH_LANDING_DESCRIPTION, generalStair.getNumber(),
                                    landing.getNumber()),
                            minimumWidth.toString(),
                            String.valueOf(minWidth), Result.Not_Accepted.getResultVal(),
                            scrutinyDetailLanding);
                }
            }
            LOG.info("minn : " + minWidth);
            }else {
                errors.put(
                        "General Stair landing width not defined in block " + block.getNumber() + " floor "
                                + floor.getNumber()
                                + " stair " + generalStair.getNumber(),
                        "General Stair landing width not defined in block " + block.getNumber() + " floor "
                                + floor.getNumber()
                                + " stair " + generalStair.getNumber());
                plan.addErrors(errors);
                
            }
            
        }
       
    }

//    private void validateFlight(Plan plan, HashMap<String, String> errors, Block block, ScrutinyDetail scrutinyDetail2,
//            ScrutinyDetail scrutinyDetail3, ScrutinyDetail scrutinyDetailRise, OccupancyTypeHelper mostRestrictiveOccupancyType,
//            Floor floor, Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair, int generalStairCount) {
//        if (!generalStair.getFlights().isEmpty()) {
//        	
//            for (Flight flight : generalStair.getFlights()) {
//            	
//                List<Measurement> flightPolyLines = flight.getFlights();
//                List<BigDecimal> flightLengths = flight.getLengthOfFlights();
//                List<BigDecimal> flightWidths = flight.getWidthOfFlights();
//                BigDecimal noOfRises = flight.getNoOfRises();
//                Boolean flightPolyLineClosed = flight.getFlightClosed();
//               
//           
//            	//flight.getNumber();
//                BigDecimal minTread = BigDecimal.ZERO;
//                BigDecimal minFlightWidth = BigDecimal.ZERO;
//                String flightLayerName = String.format(DxfFileConstants.LAYER_STAIR_FLIGHT,
//                        block.getNumber(), floor.getNumber(), generalStair.getNumber(),
//                        flight.getNumber());
//
//                if (flightPolyLines != null && flightPolyLines.size() > 0) {
//                    if (flightPolyLineClosed) {
//                        if (flightWidths != null && flightWidths.size() > 0) {
//                            minFlightWidth = validateWidth(plan, scrutinyDetail2, floor, block,
//                                    typicalFloorValues, generalStair, flight, flightWidths,
//                                    minFlightWidth,
//                                    mostRestrictiveOccupancyType);
//
//                        } else {
//                            errors.put("Flight PolyLine width" + flightLayerName,
//                                    FLIGHT_WIDTH_DEFINED_DESCRIPTION + flightLayerName);
//                            plan.addErrors(errors);
//                        }
//
//                        /*
//                         * (Total length of polygons in layer BLK_n_FLR_i_STAIR_k_FLIGHT) / (Number of rises - number of polygons
//                         * in layer BLK_n_FLR_i_STAIR_k_FLIGHT - number of lines in layer BLK_n_FLR_i_STAIR_k_FLIGHT)
//                         */
//
//                        if (flightLengths != null && flightLengths.size() > 0) {
//                            try {
//                                minTread = validateTread(plan, errors, block, scrutinyDetail3,
//                                        floor, typicalFloorValues, generalStair, flight, flightLengths,
//                                        minTread,
//                                        mostRestrictiveOccupancyType);
//                            } catch (ArithmeticException e) {
//                                LOG.info("Denominator is zero");
//                            }
//                        } else {
//                        	
//                            errors.put("Flight PolyLine length" + flightLayerName,
//                                    FLIGHT_LENGTH_DEFINED_DESCRIPTION + flightLayerName);
//                            plan.addErrors(errors);
//
//                        }
//
//                        if (noOfRises.compareTo(BigDecimal.ZERO) > 0) {
//                            try {
//                                validateNoOfRises(plan, errors, block, scrutinyDetailRise, floor,
//                                        typicalFloorValues, generalStair, flight, noOfRises);
//                            } catch (ArithmeticException e) {
//                                LOG.info("Denominator is zero");
//                            }
//                        } else {
//                            /*
//                             * String layerName = String.format( DxfFileConstants.LAYER_STAIR_FLIGHT, block.getNumber(),
//                             * floor.getNumber(), generalStair.getNumber(), flight.getNumber());
//                             */
//                            errors.put("noofRise" + flightLayerName,
//                                    edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
//                                            new String[] { NO_OF_RISERS + flightLayerName },
//                                            LocaleContextHolder.getLocale()));
//                            plan.addErrors(errors);
//                        }
//
//                    }
//                } else {
//                    errors.put("Flight PolyLine " + flightLayerName,
//                            FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION + flightLayerName);
//                    plan.addErrors(errors);
//                }
//
//            }
//            
//        } else {
////        	if(floor.getNumber() != generalStairCount - 1) 
//        	{ //This condition because in top most floor stairs are not mandatory for punjab, 
//        		                                            //so removing the error if stairs are not defined in top mist floor
//            String error = String.format(FLIGHT_NOT_DEFINED_DESCRIPTION, block.getNumber(), floor.getNumber());
//            errors.put(error, error);
//            plan.addErrors(errors);
//        }}
//    }
    
    private void validateFlight(Plan plan, HashMap<String, String> errors, Block block, 
            ScrutinyDetail scrutinyDetail2, ScrutinyDetail scrutinyDetail3, ScrutinyDetail scrutinyDetailRise,
            OccupancyTypeHelper mostRestrictiveOccupancyType, Floor floor, 
            Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair, 
            int generalStairCount) {

        boolean isTypicalRepeat = (Boolean) typicalFloorValues.get("isTypicalRepititiveFloor");
        String floorLabel = typicalFloorValues.get("typicalFloors") != null
                ? (String) typicalFloorValues.get("typicalFloors")
                : " floor " + floor.getNumber();

        if (!generalStair.getFlights().isEmpty()) {

            for (Flight flight : generalStair.getFlights()) {

                List<Measurement> flightPolyLines = flight.getFlights();
                List<BigDecimal> flightLengths = flight.getLengthOfFlights();
                List<BigDecimal> flightWidths = flight.getWidthOfFlights();
                BigDecimal noOfRises = flight.getNoOfRises();
                Boolean flightPolyLineClosed = flight.getFlightClosed();

                BigDecimal minTread = BigDecimal.ZERO;
                BigDecimal minFlightWidth = BigDecimal.ZERO;

                String flightLayerName = String.format(DxfFileConstants.LAYER_STAIR_FLIGHT,
                        block.getNumber(), floor.getNumber(), generalStair.getNumber(), flight.getNumber());

                if (flightPolyLines != null && !flightPolyLines.isEmpty()) {

                    if (flightPolyLineClosed) {

                        // FLIGHT WIDTH
                        if (flightWidths != null && !flightWidths.isEmpty()) {
                            minFlightWidth = validateWidth(plan, scrutinyDetail2, floor, block,
                                    typicalFloorValues, generalStair, flight, flightWidths,
                                    minFlightWidth, mostRestrictiveOccupancyType);

                        } else {
                            // Skip error for repeated typical floors
                            if (!isTypicalRepeat) {
                                errors.put("Flight PolyLine width" + flightLayerName,
                                        FLIGHT_WIDTH_DEFINED_DESCRIPTION + flightLayerName);
                                plan.addErrors(errors);
                            }
                        }

                        // FLIGHT LENGTH
                        if (flightLengths != null && !flightLengths.isEmpty()) {
                            try {
                                minTread = validateTread(plan, errors, block, scrutinyDetail3,
                                        floor, typicalFloorValues, generalStair, flight, 
                                        flightLengths, minTread, mostRestrictiveOccupancyType);
                            } catch (ArithmeticException e) {
                                LOG.info("Denominator is zero");
                            }
                        } else {
                            if (!isTypicalRepeat) {
                                errors.put("Flight PolyLine length" + flightLayerName,
                                        FLIGHT_LENGTH_DEFINED_DESCRIPTION + flightLayerName);
                                plan.addErrors(errors);
                            }
                        }

                        // NO OF RISES
                        if (noOfRises != null && noOfRises.compareTo(BigDecimal.ZERO) > 0) {
                            try {
                                validateNoOfRises(plan, errors, block, scrutinyDetailRise, floor,
                                        typicalFloorValues, generalStair, flight, noOfRises);
                            } catch (ArithmeticException e) {
                                LOG.info("Denominator is zero");
                            }
                        } else {
                            if (!isTypicalRepeat) {
                                errors.put("noofRise" + flightLayerName,
                                        edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                                new String[] { NO_OF_RISERS + flightLayerName },
                                                LocaleContextHolder.getLocale()));
                                plan.addErrors(errors);
                            }
                        }
                    }

                } else {
                    // FLIGHT POLYLINE NOT DEFINED
                    if (!isTypicalRepeat) {
                        errors.put("Flight PolyLine " + flightLayerName,
                                FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION + flightLayerName);
                        plan.addErrors(errors);
                    }
                }

            }

        } else {
            // Do NOT show error for repeated typical floors
            if (!isTypicalRepeat) {
                // Ignore only top-most floor special rule (Punjab)
                if (floor.getNumber() != generalStairCount - 1) {
                    String errorMsg = String.format("General stair not defined in Block %s%s",
                            block.getNumber(), floorLabel);
                    errors.put(errorMsg, errorMsg);
                    plan.addErrors(errors);
                }
            }
        }
    }


    private BigDecimal validateWidth(Plan plan, ScrutinyDetail scrutinyDetail2, Floor floor, Block block,
            Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight,
            List<BigDecimal> flightWidths, BigDecimal minFlightWidth,
            OccupancyTypeHelper mostRestrictiveOccupancyType) {
        BigDecimal flightPolyLine = flightWidths.stream().reduce(BigDecimal::min).get();

        boolean valid = false;

        if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
            minFlightWidth = Util.roundOffTwoDecimal(flightPolyLine);
            BigDecimal minimumWidth = getRequiredWidth(block, mostRestrictiveOccupancyType);

            if (minFlightWidth.compareTo(minimumWidth) >= 0) {
                valid = true;
            }
            String value = typicalFloorValues.get("typicalFloors") != null
                    ? (String) typicalFloorValues.get("typicalFloors")
                    : "" + floor.getNumber();

            if (valid) {
                setReportOutputDetailsFloorStairWise(plan, GENERAL_STAIRS_WIDTH_RULE, value,
                        String.format(WIDTH_DESCRIPTION, generalStair.getNumber(), flight.getNumber()), minimumWidth.toString(),
                        String.valueOf(minFlightWidth), Result.Accepted.getResultVal(), scrutinyDetail2);
            } else {
                setReportOutputDetailsFloorStairWise(plan, GENERAL_STAIRS_WIDTH_RULE, value,
                        String.format(WIDTH_DESCRIPTION, generalStair.getNumber(), flight.getNumber()), minimumWidth.toString(),
                        String.valueOf(minFlightWidth), Result.Not_Accepted.getResultVal(), scrutinyDetail2);
            }
        }
        return minFlightWidth;
    }

    private BigDecimal getRequiredWidth(Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {
        if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
                && DxfFileConstants.A_AF.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
            return BigDecimal.valueOf(1.9);
        } else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
                && DxfFileConstants.A_AF_GH.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
            return BigDecimal.valueOf(0.75);
        }
        //else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
//                && DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())
//                && block.getBuilding().getBuildingHeight().compareTo(BigDecimal.valueOf(10)) <= 0
//                && block.getBuilding().getFloorsAboveGround().compareTo(BigDecimal.valueOf(3)) <= 0) {
//            return BigDecimal.ONE;
//        }
        else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
                && DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
            return BigDecimal.valueOf(0.76);
        } else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
                && DxfFileConstants.B.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
            return BigDecimal.valueOf(1.5);
        } else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
                && DxfFileConstants.D.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
            return BigDecimal.valueOf(2);
        } else {
            return BigDecimal.valueOf(1.5);
        }
    }
    
    private BigDecimal getRequiredLandingWidth(Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {
      
        if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
                && DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
            return BigDecimal.valueOf(0.76);
        }
     else {
            return BigDecimal.valueOf(1.5);
        }
    }

    private BigDecimal validateTread(Plan plan, HashMap<String, String> errors, Block block,
            ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues,
            org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight, List<BigDecimal> flightLengths,
            BigDecimal minTread,
            OccupancyTypeHelper mostRestrictiveOccupancyType) {
        BigDecimal totalLength = flightLengths.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        totalLength = Util.roundOffTwoDecimal(totalLength);

        BigDecimal requiredTread = getRequiredTread(mostRestrictiveOccupancyType);

        if (flight.getNoOfRises() != null) {
            /*
             * BigDecimal denominator = fireStair.getNoOfRises().subtract(BigDecimal.valueOf(flightLengths.size()))
             * .subtract(BigDecimal.valueOf(fireStair.getLinesInFlightLayer().size()));
             */
            BigDecimal noOfFlights = BigDecimal.valueOf(flightLengths.size());

            if (flight.getNoOfRises().compareTo(noOfFlights) > 0) {
                BigDecimal denominator = flight.getNoOfRises().subtract(noOfFlights);

                minTread = totalLength.divide(denominator, DcrConstants.DECIMALDIGITS_MEASUREMENTS,
                        DcrConstants.ROUNDMODE_MEASUREMENTS);

                boolean valid = false;

                if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {

                    if (Util.roundOffTwoDecimal(minTread).compareTo(Util.roundOffTwoDecimal(requiredTread)) >= 0) {
                        valid = true;
                    }

                    String value = typicalFloorValues.get("typicalFloors") != null
                            ? (String) typicalFloorValues.get("typicalFloors")
                            : "" + floor.getNumber();
                    if (valid) {
                        setReportOutputDetailsFloorStairWise(plan, RULETREAD, value,
                                String.format(TREAD_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
                                requiredTread.toString(),
                                String.valueOf(minTread), Result.Accepted.getResultVal(), scrutinyDetail3);
                    } else {
                        setReportOutputDetailsFloorStairWise(plan, RULETREAD, value,
                                String.format(TREAD_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
                                requiredTread.toString(),
                                String.valueOf(minTread), Result.Not_Accepted.getResultVal(), scrutinyDetail3);
                    }
                }
            } else {
                if (flight.getNoOfRises().compareTo(BigDecimal.ZERO) > 0) {
                    String flightLayerName = String.format(DxfFileConstants.LAYER_STAIR_FLIGHT, block.getNumber(),
                            floor.getNumber(), generalStair.getNumber(), flight.getNumber());
                    errors.put("NoOfRisesCount" + flightLayerName,
                            "Number of risers count should be greater than the count of length of flight dimensions defined in layer "
                                    + flightLayerName);
                    plan.addErrors(errors);
                }
            }
        }
        return minTread;
    }

	private BigDecimal getRequiredTread(OccupancyTypeHelper mostRestrictiveOccupancyType) {
        if (mostRestrictiveOccupancyType != null
        		//&& mostRestrictiveOccupancyType.getSubtype() != null
                //&& DxfFileConstants.A_AF.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())) {
        	 && DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
            return BigDecimal.valueOf(0.25);
        } else {
            return BigDecimal.valueOf(0.3);
        	//return null;
        }
    }

    private void validateNoOfRises(Plan plan, HashMap<String, String> errors, Block block,
            ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues,
            org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight, BigDecimal noOfRises) {
        boolean valid = false;

        if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
            if (Util.roundOffTwoDecimal(noOfRises).compareTo(Util.roundOffTwoDecimal(BigDecimal.valueOf(12))) <= 0) {
                valid = true;
            }

            String value = typicalFloorValues.get("typicalFloors") != null
                    ? (String) typicalFloorValues.get("typicalFloors")
                    : "" + floor.getNumber();
            if (valid) {
                setReportOutputDetailsFloorStairWise(plan, RULERISER, value,
                        String.format(NO_OF_RISER_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
                        EXPECTED_NO_OF_RISER,
                        String.valueOf(noOfRises), Result.Accepted.getResultVal(), scrutinyDetail3);
            } else {
                setReportOutputDetailsFloorStairWise(plan, RULERISER, value,
                        String.format(NO_OF_RISER_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
                        EXPECTED_NO_OF_RISER,
                        String.valueOf(noOfRises), Result.Not_Accepted.getResultVal(), scrutinyDetail3);
            }
        }
    }

    /*
     * private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual, String status,
     * ScrutinyDetail scrutinyDetail) { Map<String, String> details = new HashMap<>(); details.put(RULE_NO, ruleNo);
     * details.put(DESCRIPTION, ruleDesc); details.put(REQUIRED, expected); details.put(PROVIDED, actual); details.put(STATUS,
     * status); scrutinyDetail.getDetail().add(details); pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail); }
     */

    private void setReportOutputDetailsFloorStairWise(Plan pl, String ruleNo, String floor, String description,
            String expected, String actual, String status, ScrutinyDetail scrutinyDetail) {
    	if(actual.trim().equalsIgnoreCase("0"))
    		return;
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(FLOOR, floor);
        details.put(DESCRIPTION, description);
        details.put(PERMISSIBLE, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    
    /*
     * private void validateDimensions(Plan plan, String blockNo, int floorNo, String stairNo, List<Measurement> flightPolyLines)
     * { int count = 0; for (Measurement m : flightPolyLines) { if (m.getInvalidReason() != null && m.getInvalidReason().length()
     * > 0) { count++; } } if (count > 0) { plan.addError(String.format(DxfFileConstants. LAYER_FIRESTAIR_FLIGHT_FLOOR, blockNo,
     * floorNo, stairNo), count + " number of flight polyline not having only 4 points in layer " +
     * String.format(DxfFileConstants.LAYER_FIRESTAIR_FLIGHT_FLOOR, blockNo, floorNo, stairNo)); } }
     */
     

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}