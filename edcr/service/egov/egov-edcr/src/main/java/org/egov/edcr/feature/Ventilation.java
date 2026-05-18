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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;

@Service
public class Ventilation extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Ventilation.class);
	private static final String RULE_43 = "43";
	private static final String RULE_LIGHT_VENTILATION = "4.4.4(ix)";
	public static final String LIGHT_VENTILATION_DESCRIPTION = "Light and Ventilation";
	public static final String REGULAR_ROOM_LIGHT_VENTILATION_DESCRIPTION = "Regular Room Light and Ventilation";
	public static final String REQUIRED_LIGHT_VENTILATION_AREA = "20% of the floor area";
	public static final String REQUIRED_REGULAR_ROOM_LIGHT_VENTILATION_AREA = "10% of the floor area";
	

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}
	
	@Override
	public Plan process(Plan pl) {
	    HashMap<String, String> errorMsgs = new HashMap<>();

	    for (Block b : pl.getBlocks()) {
	        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	        scrutinyDetail.setKey("Common_Ventilation");
	        scrutinyDetail.addColumnHeading(1, RULE_NO);
	        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	        scrutinyDetail.addColumnHeading(3, REQUIRED);
	        scrutinyDetail.addColumnHeading(4, PROVIDED);
	        scrutinyDetail.addColumnHeading(5, STATUS);

	        ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
	        scrutinyDetail1.setKey("Bath_Ventilation");
	        scrutinyDetail1.addColumnHeading(1, RULE_NO);
	        scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
	        scrutinyDetail1.addColumnHeading(3, REQUIRED);
	        scrutinyDetail1.addColumnHeading(4, PROVIDED);
	        scrutinyDetail1.addColumnHeading(5, STATUS);

	        if (b.getBuilding() != null && b.getBuilding().getFloors() != null && !b.getBuilding().getFloors().isEmpty()) {
	            for (Floor f : b.getBuilding().getFloors()) {
	            	
	            	OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper();

	                // ----------------------------------------------------
	                // Common Ventilation
	                // ----------------------------------------------------
	                if (f.getLightAndVentilation() != null 
	                        && f.getLightAndVentilation().getMeasurements() != null
	                        && !f.getLightAndVentilation().getMeasurements().isEmpty()) {
	                    try {
	                        BigDecimal totalVentilationArea = f.getLightAndVentilation().getMeasurements().stream()
	                                .map(Measurement::getArea)
	                                .reduce(BigDecimal.ZERO, BigDecimal::add)
	                                .setScale(2, RoundingMode.HALF_UP);

	                        BigDecimal totalCarpetArea = f.getOccupancies().stream()
	                                .map(Occupancy::getCarpetArea)
	                                .reduce(BigDecimal.ZERO, BigDecimal::add)
	                                .setScale(2, RoundingMode.HALF_UP);

	                        if (totalVentilationArea.compareTo(BigDecimal.ZERO) > 0) {
	                            Map<String, String> details = new HashMap<>();
	                            details.put(RULE_NO, RULE_LIGHT_VENTILATION);
	                            details.put(DESCRIPTION, LIGHT_VENTILATION_DESCRIPTION);
	                            details.put(REQUIRED, REQUIRED_LIGHT_VENTILATION_AREA);
	                            

	                            BigDecimal totalFloorArea = f.getOccupancies() != null
                                        ? f.getOccupancies().stream()
                                                .map(Occupancy::getFloorArea)
                                                .filter(Objects::nonNull)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                                .setScale(2, RoundingMode.HALF_UP)
                                        : BigDecimal.ZERO;

                                // 20% of floor area required
                                BigDecimal requiredVentilationArea = totalFloorArea
                                        .multiply(BigDecimal.valueOf(0.20))
                                        .setScale(2, RoundingMode.HALF_UP);
	                            

//	                            if (totalVentilationArea.compareTo(
//	                                    totalCarpetArea.divide(BigDecimal.valueOf(8), 2, BigDecimal.ROUND_HALF_UP)) >= 0) {
                                if (totalVentilationArea.compareTo(requiredVentilationArea) >= 0) {
	                                details.put(PROVIDED, "Ventilation area " + totalVentilationArea + " at floor " + f.getNumber());
	                                details.put(STATUS, Result.Accepted.getResultVal());
	                            } else {
	                                details.put(PROVIDED, "Ventilation area " + totalVentilationArea + " at floor " + f.getNumber());
	                                details.put(STATUS, Result.Not_Accepted.getResultVal());
	                            }
	                            scrutinyDetail.getDetail().add(details);
	                        }
	                    } catch (Exception e) {
	                        errorMsgs.put("Common Ventilation Error", "Floor " + f.getNumber() + ": " + e.getMessage());
	                        pl.addErrors(errorMsgs);
	                    }
	                }else {
	                	if (mostRestrictiveOccupancyType != null
	                	        && mostRestrictiveOccupancyType.getType() != null
	                	        && mostRestrictiveOccupancyType.getType().getCode() != null
	                	        && DxfFileConstants.A.equalsIgnoreCase(
	                	                mostRestrictiveOccupancyType.getType().getCode())) {
	                		errorMsgs.put("Ventilation is mandatory",
		                			"Floor ventilation layer not defined in the plan. Kindly refer to the user manual.");	                	
	                        pl.addErrors(errorMsgs);
		            	}
	                	
	                }

	                // ----------------------------------------------------
	                // Bath Ventilation
	                // ----------------------------------------------------
	                if (f.getBathVentilaion() != null 
	                        && f.getBathVentilaion().getMeasurements() != null
	                        && !f.getBathVentilaion().getMeasurements().isEmpty()) {
	                    try {
	                        BigDecimal totalBathVentilationArea = f.getBathVentilaion().getMeasurements().stream()
	                                .map(Measurement::getArea)
	                                .reduce(BigDecimal.ZERO, BigDecimal::add)
	                                .setScale(2, RoundingMode.HALF_UP);

	                        if (totalBathVentilationArea.compareTo(BigDecimal.ZERO) > 0) {
	                            Map<String, String> details1 = new HashMap<>();
	                            details1.put(RULE_NO, RULE_43);
	                            details1.put(DESCRIPTION, "Bath Ventilation");
	                            details1.put(REQUIRED, "0.3");

	                            if (totalBathVentilationArea.compareTo(new BigDecimal("0.3")) >= 0) {
	                                details1.put(PROVIDED, "Bath Ventilation area " + totalBathVentilationArea + " at floor " + f.getNumber());
	                                details1.put(STATUS, Result.Accepted.getResultVal());
	                            } else {
	                                details1.put(PROVIDED, "Bath Ventilation area " + totalBathVentilationArea + " at floor " + f.getNumber());
	                                details1.put(STATUS, Result.Not_Accepted.getResultVal());
	                            }
	                            scrutinyDetail1.getDetail().add(details1);
	                        }
	                    } catch (Exception e) {
	                        errorMsgs.put("Bath Ventilation Error", "Floor " + f.getNumber() + ": " + e.getMessage());
	                        pl.addErrors(errorMsgs);
	                    }
	                }

	                // ----------------------------------------------------
	                // Regular Room Ventilation
	                // ----------------------------------------------------
	                
	             // Regular Room Ventilation start
	             // ----------------------------------------------------
	                
	                if (f.getRegularRooms() != null && !f.getRegularRooms().isEmpty()) {
	                    try {
	                        for (Room room : f.getRegularRooms()) {
	                            if (room.getLightAndVentilation() != null
	                                    && room.getLightAndVentilation().getMeasurements() != null
	                                    && !room.getLightAndVentilation().getMeasurements().isEmpty()) {

	                                String roomNo = room.getNumber();

	                                // Calculate total ventilation area for this room
	                                BigDecimal totalRegularRoomVentilationArea = room.getLightAndVentilation()
	                                        .getMeasurements()
	                                        .stream()
	                                        .map(Measurement::getArea)
	                                        .filter(Objects::nonNull)
	                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
	                                        .setScale(2, RoundingMode.HALF_UP);

	                                // Calculate total floor area safely
	                                BigDecimal totalFloorArea = f.getOccupancies() != null
	                                        ? f.getOccupancies().stream()
	                                                .map(Occupancy::getFloorArea)
	                                                .filter(Objects::nonNull)
	                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
	                                                .setScale(2, RoundingMode.HALF_UP)
	                                        : BigDecimal.ZERO;

	                                // Skip if either floor area or ventilation area is zero
	                                if (totalFloorArea.compareTo(BigDecimal.ZERO) <= 0
	                                        || totalRegularRoomVentilationArea.compareTo(BigDecimal.ZERO) <= 0) {
	                                    LOG.info("Skipping ventilation check: Floor " + f.getNumber() + " Room " + roomNo +
	                                             " has no valid floor area or ventilation area.");
	                                    continue; // skip this room
	                                }

	                                // 10% of floor area required
	                                BigDecimal requiredVentilationArea = totalFloorArea
	                                        .multiply(BigDecimal.valueOf(0.10))
	                                        .setScale(2, RoundingMode.HALF_UP);

	                                // Prepare details map
	                                Map<String, String> details = new HashMap<>();
	                                details.put(RULE_NO, RULE_LIGHT_VENTILATION);
	                                details.put(DESCRIPTION, REGULAR_ROOM_LIGHT_VENTILATION_DESCRIPTION);
	                                details.put(REQUIRED, "≥ 10% of Floor Area (" + requiredVentilationArea + ")");

	                                // Validation check
	                                if (totalRegularRoomVentilationArea.compareTo(requiredVentilationArea) >= 0) {
	                                    details.put(PROVIDED, "Ventilation area " + totalRegularRoomVentilationArea
	                                            + " at floor " + f.getNumber()
	                                            + " room " + roomNo);
	                                    details.put(STATUS, Result.Accepted.getResultVal());
	                                } else {
	                                    details.put(PROVIDED, "Ventilation area " + totalRegularRoomVentilationArea
	                                            + " at floor " + f.getNumber()
	                                            + " room " + roomNo);
	                                    details.put(STATUS, Result.Not_Accepted.getResultVal());
	                                }

	                                scrutinyDetail.getDetail().add(details);
	                            }
	                        }
	                    } catch (Exception e) {
	                        errorMsgs.put("Regular Room Ventilation Error",
	                                "Floor " + f.getNumber() + ": " + e.getMessage());
	                        pl.addErrors(errorMsgs);
	                    }
	                }

	             
	             // Regular Room Ventilation end
	             // ----------------------------------------------------

	            }
	        }

	        // ✅ Add scrutiny details only if filled
	        if (!scrutinyDetail.getDetail().isEmpty()) {
	            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	        }
	        if (!scrutinyDetail1.getDetail().isEmpty()) {
	            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail1);
	        }
	    }

	    return pl;
	}


//	@Override
//	public Plan process(Plan pl) {
//		HashMap<String, String> errorMsgs = new HashMap<>();
//		for (Block b : pl.getBlocks()) {
//			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//			scrutinyDetail.setKey("Common_Ventilation");
//			scrutinyDetail.addColumnHeading(1, RULE_NO);
//			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail.addColumnHeading(3, REQUIRED);
//			scrutinyDetail.addColumnHeading(4, PROVIDED);
//			scrutinyDetail.addColumnHeading(5, STATUS);
//			
//			ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
//			scrutinyDetail1.setKey("Bath_Ventilation");
//			scrutinyDetail1.addColumnHeading(1, RULE_NO);
//			scrutinyDetail1.addColumnHeading(2, DESCRIPTION);
//			scrutinyDetail1.addColumnHeading(3, REQUIRED);
//			scrutinyDetail1.addColumnHeading(4, PROVIDED);
//			scrutinyDetail1.addColumnHeading(5, STATUS);
//
//			if (b.getBuilding() != null && b.getBuilding().getFloors() != null
//					&& !b.getBuilding().getFloors().isEmpty()) {
//
//				for (Floor f : b.getBuilding().getFloors()) {					
//					// Common Ventilation (Light & Ventilation)
//					if (f.getLightAndVentilation() != null && f.getLightAndVentilation().getMeasurements() != null
//							&& !f.getLightAndVentilation().getMeasurements().isEmpty()) {
//
//						BigDecimal totalVentilationArea = f.getLightAndVentilation()
//								.getMeasurements()
//								.stream()
//								.map(Measurement::getArea)
//								.reduce(BigDecimal.ZERO, BigDecimal::add);
//						BigDecimal totalCarpetArea = f.getOccupancies()
//								.stream()
//								.map(Occupancy::getCarpetArea)
//								.reduce(BigDecimal.ZERO, BigDecimal::add);
//						
//						Map<String, String> details = new HashMap<>();					
//						details.put(RULE_NO, RULE_LIGHT_VENTILATION);
//						details.put(DESCRIPTION, LIGHT_VENTILATION_DESCRIPTION);
//						details.put(REQUIRED, REQUIRED_LIGHT_VENTILATION_AREA);
//						if (totalVentilationArea.compareTo(BigDecimal.ZERO) > 0) {
//							if (totalVentilationArea.compareTo(totalCarpetArea.divide(BigDecimal.valueOf(8)).setScale(2,
//									BigDecimal.ROUND_HALF_UP)) >= 0) {								
//								details.put(PROVIDED, "Ventilation area " + totalVentilationArea +" at floor " + f.getNumber());
//								details.put(STATUS, Result.Accepted.getResultVal());							
//							} else {								
//								details.put(PROVIDED, "Ventilation area " + totalVentilationArea +" at floor " + f.getNumber());
//								details.put(STATUS, Result.Not_Accepted.getResultVal());
//							}
//							scrutinyDetail.getDetail().add(details);
//						} 
//					} else { 
//						errorMsgs.put("Total Floor Ventilation", "Ventilation area not defined in floor" + f.getNumber());
//						pl.addErrors(errorMsgs);
//					}
//					
//					
//					// Added by Neha 
//					// For bath ventilation
////					if (f.getBathVentilaion() != null && f.getBathVentilaion().getMeasurements() != null
////							&& !f.getBathVentilaion().getMeasurements().isEmpty()) {
////
////						BigDecimal totalVentilationArea = f.getBathVentilaion().getMeasurements().stream()
////								.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add);
////						//BigDecimal totalCarpetArea = f.getOccupancies().stream().map(Occupancy::getCarpetArea)
////								//.reduce(BigDecimal.ZERO, BigDecimal::add);
////						
////						Map<String, String> details1 = new HashMap<>();
////						details1.put(RULE_NO, RULE_43);
////						details1.put(DESCRIPTION, "Bath Ventilation");
////						details1.put(REQUIRED, " 0.3 ");
////
////						if (totalVentilationArea.compareTo(new BigDecimal(0.3)) >= 0) {
////								details1.put(PROVIDED, "Bath Ventilation area " + totalVentilationArea + " at floor " + f.getNumber());
////								details1.put(STATUS, Result.Accepted.getResultVal());
////						}else {
////							details1.put(PROVIDED, "Bath Ventilation area " + totalVentilationArea + " at floor " + f.getNumber());
////							details1.put(STATUS, Result.Not_Accepted.getResultVal());
////						}
////						scrutinyDetail1.getDetail().add(details1);
////					} else { 
////						Map<String, String> details1 = new HashMap<>();
////					    details1.put(RULE_NO, RULE_43);
////					    details1.put(DESCRIPTION, "Bath Ventilation");
////						details1.put(REQUIRED,"0.3");
////						details1.put(PROVIDED,"Bath Ventilation area not defined in floor  " + f.getNumber()); 
////						details1.put(STATUS,Result.Not_Accepted.getResultVal());
////						scrutinyDetail.getDetail().add(details1);
////						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail1); 
////					}
//					
//						if (f.getRegularRooms() != null) {	
//							BigDecimal totalRegularRoomVentilationArea = f.getRegularRooms().stream()					
//									.filter(room -> room.getLightAndVentilation() != null && room.getLightAndVentilation().getMeasurements() != null)
//								    .flatMap(room -> room.getLightAndVentilation().getMeasurements().stream())
//								    .map(Measurement::getArea)
//								    .reduce(BigDecimal.ZERO, BigDecimal::add);
//							
//						
//							Map<String, String> details = new HashMap<>();					
//							details.put(RULE_NO, RULE_LIGHT_VENTILATION);
//							details.put(DESCRIPTION, REGULAR_ROOM_LIGHT_VENTILATION_DESCRIPTION);
//							details.put(REQUIRED, REQUIRED_REGULAR_ROOM_LIGHT_VENTILATION_AREA);
//							if (totalRegularRoomVentilationArea.compareTo(new BigDecimal(0.1)) >= 0) {															
//									details.put(PROVIDED, "Ventilation area " + totalRegularRoomVentilationArea + 
//											" at floor " + f.getNumber() + "Room ");
//									details.put(STATUS, Result.Accepted.getResultVal());							
//								} else {								
//									details.put(PROVIDED, "Ventilation area " + totalRegularRoomVentilationArea + 
//											" at floor " + f.getNumber());
//									details.put(STATUS, Result.Not_Accepted.getResultVal());
//								}
//								scrutinyDetail.getDetail().add(details);
//						} 
//					//} 
//
//				}
//			}
//			// ✅ Add scrutiny details to report only once per block
//	        if (!scrutinyDetail.getDetail().isEmpty()) {
//	            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//	        }
//	        if (!scrutinyDetail1.getDetail().isEmpty()) {
//	            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail1);
//	        }
//
//		}
//
//		return pl;
//	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
