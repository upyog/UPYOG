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

import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.REAR_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.REAR_AND_SIDE_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.HEIGHTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.WRONGHEIGHTDEFINED;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.SetBack;
import org.egov.edcr.service.PlanService;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetBackService extends FeatureProcess {
	
	private final Logger LOG = LogManager.getLogger(SetBackService.class);
	private final String RULE = "4.4.4";
	private final String MINIMUMLABEL = "Minimum distance";

    @Autowired
    private FrontYardService frontYardService;

    @Autowired
    private SideYardService sideYardService;

    @Autowired
    private RearYardService rearYardService;
    
    private static final BigDecimal TWO_HUNDRED = BigDecimal.valueOf(200);

    @Override
    public Plan validate(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        // Assumption: if height of one level, should be less than next level. this condition not validated.as in each level user
        // can define different height.
        BigDecimal heightOfBuilding = BigDecimal.ZERO;
        for (Block block : pl.getBlocks()) {
            heightOfBuilding = block.getBuilding().getBuildingHeight();
            int i = 0;
            if (!block.getCompletelyExisting()) {
                for (SetBack setback : block.getSetBacks()) {
                	if(pl.getCoreArea().equalsIgnoreCase("No")) {
                    i++;
                    // if height not defined other than 0 level , then throw error.
                    if (setback.getLevel() == 0) {
                        // for level 0, all the yards are mandatory. Else throw error.
                        if (setback.getFrontYard() == null) {
                        	if (!Far.shouldSkipValidation(pl.getEdcrRequest(),DcrConstants.EDCR_SKIP_FRONT_SETBACK)) {				
                        		errors.put("frontyardNodeDefined",
                                        getLocaleMessage(OBJECTNOTDEFINED, " Front SetBack of " + block.getName() + "  at level zero "));
                            }
                            
                        }
                            if( pl.getPlot().getArea().compareTo(TWO_HUNDRED) > 0) {
	                        if (setback.getRearYard() == null
	                                && !pl.getPlanInformation().getNocToAbutRearDesc().equalsIgnoreCase(DcrConstants.YES))
	                            errors.put("rearyardNodeDefined",
	                                    getLocaleMessage(OBJECTNOTDEFINED, " Rear Setback of  " + block.getName() + "  at level zero "));
	                        //if (setback.getSideYard1() == null)
	                            //errors.put("side1yardNodeDefined", getLocaleMessage(OBJECTNOTDEFINED,
	                                    //" Side Setback 1 of block " + block.getName() + " at level zero"));
	                        //if (setback.getSideYard2() == null
	                          //      && !pl.getPlanInformation().getNocToAbutSideDesc().equalsIgnoreCase(DcrConstants.YES))
	                            //errors.put("side2yardNodeDefined", getLocaleMessage(OBJECTNOTDEFINED,
	                              //      " Side Setback 2 of block " + block.getName() + " at level zero "));
	                        } 
                    } else if (setback.getLevel() > 0) {
                        // height defined in level other than zero must contain height
                        if (setback.getFrontYard() != null && setback.getFrontYard().getHeight() == null)
                            errors.put("frontyardnotDefinedHeight", getLocaleMessage(HEIGHTNOTDEFINED, "Front Setback ",
                                    block.getName(), setback.getLevel().toString()));
                        if (setback.getRearYard() != null && setback.getRearYard().getHeight() == null)
                            errors.put("rearyardnotDefinedHeight", getLocaleMessage(HEIGHTNOTDEFINED, "Rear Setback ",
                                    block.getName(), setback.getLevel().toString()));
                        if (setback.getSideYard1() != null && setback.getSideYard1().getHeight() == null)
                            errors.put("side1yardnotDefinedHeight", getLocaleMessage(HEIGHTNOTDEFINED, "Side Setback 1 ",
                                    block.getName(), setback.getLevel().toString()));
                        if (setback.getSideYard2() != null && setback.getSideYard2().getHeight() == null)
                            errors.put("side2yardnotDefinedHeight", getLocaleMessage(HEIGHTNOTDEFINED, "Side Setback 2 ",
                                    block.getName(), setback.getLevel().toString()));
                    }
                    
                   


                    // if height of setback greater than building height ?
                    // last level height should match with building height.

                    if (setback.getLevel() > 0 && block.getSetBacks().size() == i) {
                        if (setback.getFrontYard() != null && setback.getFrontYard().getHeight() != null
                                && setback.getFrontYard().getHeight().compareTo(heightOfBuilding) != 0)
                            errors.put("frontyardDefinedWrongHeight", getLocaleMessage(WRONGHEIGHTDEFINED, "Front Setback ",
                                    block.getName(), setback.getLevel().toString(), heightOfBuilding.toString()));
                        if (setback.getRearYard() != null && setback.getRearYard().getHeight() != null
                                && setback.getRearYard().getHeight().compareTo(heightOfBuilding) != 0)
                            errors.put("rearyardDefinedWrongHeight", getLocaleMessage(WRONGHEIGHTDEFINED, "Rear Setback ",
                                    block.getName(), setback.getLevel().toString(), heightOfBuilding.toString()));
                        if (setback.getSideYard1() != null && setback.getSideYard1().getHeight() != null
                                && setback.getSideYard1().getHeight().compareTo(heightOfBuilding) != 0)
                            errors.put("side1yardDefinedWrongHeight", getLocaleMessage(WRONGHEIGHTDEFINED, "Side Setback 1 ",
                                    block.getName(), setback.getLevel().toString(), heightOfBuilding.toString()));
                        if (setback.getSideYard2() != null && setback.getSideYard2().getHeight() != null
                                && setback.getSideYard2().getHeight().compareTo(heightOfBuilding) != 0)
                            errors.put("side2yardDefinedWrongHeight", getLocaleMessage(WRONGHEIGHTDEFINED, "Side Setback 2 ",
                                    block.getName(), setback.getLevel().toString(), heightOfBuilding.toString()));
                    }
                   
                }
            }
            }
        }
        if (errors.size() > 0)
            pl.addErrors(errors);

        return pl;
    }

    @Override
    public Plan process(Plan pl) {    	
    	//ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
    	List<ScrutinyDetail> scrutinyDetailList = new ArrayList<>();
//		scrutinyDetail = new ScrutinyDetail();
//		scrutinyDetail.addColumnHeading(1, RULE_NO);
//		scrutinyDetail.addColumnHeading(2, LEVEL);
//		scrutinyDetail.addColumnHeading(3, OCCUPANCY);
//		scrutinyDetail.addColumnHeading(4, FIELDVERIFIED);
//		scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
//		scrutinyDetail.addColumnHeading(6, PROVIDED);
//		scrutinyDetail.addColumnHeading(7, STATUS);
//		scrutinyDetail.setHeading(FRONT_YARD_DESC);
		
        validate(pl);
        
        HashMap<String, String> errors = new HashMap<>();
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();
//		if (depthOfPlot != null && depthOfPlot.compareTo(BigDecimal.ZERO) > 0) {
//			frontYardService.processFrontYard(pl, scrutinyDetail);
//			//  if( pl.getPlot().getArea().compareTo(TWO_HUNDRED) > 0) {
//			rearYardService.processRearYard(pl , scrutinyDetail);
//			//  }
//			 if (pl.getRoadReserveRear() != BigDecimal.ZERO && pl.getCoreArea().equalsIgnoreCase("No")){
//				 for (Block block : pl.getBlocks()) {
//				 for (SetBack setback : block.getSetBacks()) {
//                 // Rear yard call is mandatory if reserve is not null, irrespective of plot area
//                 if (setback.getRearYard() == null) {
//                     // Throw an error if rear yard is not present
//                 	errors.put("rearyardNodeDefined",
//                             getLocaleMessage(OBJECTNOTDEFINED, " Rear Setback of  " + block.getName()  + 
//                 	"Rear setback is mandatory when rear road width is present."));
//                 }
//                 rearYardService.processRearYard(pl, scrutinyDetail);
//             }}} else if (pl.getPlot().getArea().compareTo(TWO_HUNDRED) > 0 &&
//            		 pl.getCoreArea().equalsIgnoreCase("No")) {
//                 // Process rear yard only if roadreserverear is null and plot area is greater than 200
//                 rearYardService.processRearYard(pl, scrutinyDetail);
//             }	
//		}
//		
//		BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();
//		if (widthOfPlot != null && widthOfPlot.compareTo(BigDecimal.ZERO) > 0) {
//			sideYardService.processSideYard(pl, scrutinyDetail);
//		}

		if (depthOfPlot != null && depthOfPlot.compareTo(BigDecimal.ZERO) > 0) {
		    // Always process front yard if depth is valid
		    frontYardService.processFrontYard(pl, scrutinyDetailList);

		    boolean shouldProcessRearYard = true;

		    if (pl.getCoreArea().equalsIgnoreCase("No")) {
		        if (pl.getRoadReserveRear() != null && pl.getRoadReserveRear().compareTo(BigDecimal.ZERO) > 0) {
		            // Rear yard call is mandatory if road reserve rear is present
		            for (Block block : pl.getBlocks()) {
		                for (SetBack setback : block.getSetBacks()) {
		                    if (setback.getRearYard() == null) {
		                        errors.put("rearyardNodeDefined","Rear Setback of " + block.getName() 
		                                        + " is mandatory when rear road width is present.");
		                        pl.addErrors(errors);
		                    }
		                }
		            }
		            shouldProcessRearYard = true;
		        } else if (pl.getPlot().getArea().compareTo(TWO_HUNDRED) > 0) {
		            // Process rear yard only if no road reserve rear AND plot area > 200
		            shouldProcessRearYard = true;
		        }
		    }else {
		    	shouldProcessRearYard = true;
		    }
		    

		    // Process rear yard only once if conditions matched
		    if (shouldProcessRearYard) {
		        rearYardService.processRearYard(pl, scrutinyDetailList);
		    }
		}

		// Side yard processing
		BigDecimal widthOfPlot = pl.getPlanInformation().getWidthOfPlot();
		if (widthOfPlot != null && widthOfPlot.compareTo(BigDecimal.ZERO) > 0) {
		    sideYardService.processSideYard(pl, scrutinyDetailList);
		}

		buildResult(pl,scrutinyDetailList);
        return pl;
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
    
    private void buildResult(Plan pl, List<ScrutinyDetail> scrutinyDetailList) {
        if (scrutinyDetailList == null || scrutinyDetailList.isEmpty()) {
            return;
        }

        BigDecimal plotArea = pl.getPlot() != null && pl.getPlot().getArea() != null
                ? pl.getPlot().getArea()
                : BigDecimal.ZERO;

//        // Group all scrutinyDetails by block name (Block_1, Block_2, etc.)
//        Map<String, List<ScrutinyDetail>> blockWiseDetails = new HashMap<>();
//
//        for (ScrutinyDetail sd : scrutinyDetailList) {
//            String key = sd.getKey();
//            if (key == null) continue;
//
//            // Extract block prefix: "Block_1", "Block_2", etc.
//            String blockPrefix = "Unknown_Block";
//            if (key.startsWith("Block_")) {
//                String[] parts = key.split("_", 3);
//                if (parts.length >= 2) {
//                    blockPrefix = parts[0] + "_" + parts[1];
//                }
//            }
//
//            blockWiseDetails.computeIfAbsent(blockPrefix, k -> new ArrayList<>()).add(sd);
//        }
     // Group all scrutinyDetails by block name (Block_1, Block_2, etc.)
     // Group all scrutinyDetails by block name (Block_1, Block_2, etc.)
        Map<String, List<ScrutinyDetail>> blockWiseDetails = new HashMap<>();

        // Track duplicate Side Setback keys
        Map<String, Integer> sideSetbackCounter = new HashMap<>();

        for (ScrutinyDetail sd : scrutinyDetailList) {
            String key = sd.getKey();
            if (key == null) continue;

            // Apply numbering ONLY for Side Setback
            if (key.contains("Side Setback") && !key.matches(".*Side Setback\\d+$")) {

                int count = sideSetbackCounter.getOrDefault(key, 0) + 1;
                sideSetbackCounter.put(key, count);

                sd.setKey(key + count);
            }

            key = sd.getKey();

            // Extract block prefix: "Block_1", "Block_2"
            String blockPrefix = "Unknown_Block";
            if (key.startsWith("Block_")) {
                String[] parts = key.split("_", 3);
                if (parts.length >= 2) {
                    blockPrefix = parts[0] + "_" + parts[1];
                }
            }

            blockWiseDetails.computeIfAbsent(blockPrefix, k -> new ArrayList<>()).add(sd);
        }



        // Process each block separately
        for (Map.Entry<String, List<ScrutinyDetail>> entry : blockWiseDetails.entrySet()) {
            String blockPrefix = entry.getKey();
            List<ScrutinyDetail> blockDetails = entry.getValue();

            BigDecimal rearAndSideSetback = BigDecimal.ZERO;
            Map<String, String> detailsMap = new HashMap<>();
            String status = Result.Not_Accepted.getResultVal();
            boolean permissibleIsMeters = false; // flag to decide if we append "m"
            boolean isResidential = false;

            for (ScrutinyDetail scrutinyDetail : blockDetails) {
                String key = scrutinyDetail.getKey();
                
             // ✅ Extract occupancy from details
                String occupancyType = "";
                boolean isSetbackCombine = false;
                if (scrutinyDetail.getDetail() != null && !scrutinyDetail.getDetail().isEmpty()) {
                    Map<String, String> detail = scrutinyDetail.getDetail().get(0);
                    if (detail.containsKey("Occupancy")) {
                        occupancyType = detail.get("Occupancy");
                        isSetbackCombine = Boolean.valueOf(detail.get("isSetbackCombine"));
                    }
                }                
                
                if (!isSetbackCombine) {
                	//isResidential = true;
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                }else {
                	if (key.contains("Front")) {
                        // Keep Front setbacks directly
                        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                    } else if (key.contains("Rear") || key.contains("Side")) {
                        List<Map<String, String>> detailsList = scrutinyDetail.getDetail();
                        if (detailsList == null) continue;

                        for (Map<String, String> detail : detailsList) {
                            detail.forEach((k, v) -> {
                                if (v != null) {
                                    detailsMap.put(k, v);
                                }
                            });
                        }

                        // Sum up Provided values
                     // Take only the first valid "Provided" value from the detailsList (avoid summing multiple side setbacks inside same ScrutinyDetail)
                        BigDecimal providedValue = detailsList.stream()
                                .map(detail -> detail.get("Provided"))
                                .filter(Objects::nonNull)
                                .map(String::trim)
                                // remove non-numeric characters except dot and minus (handles "2.00m", "2.00 m", etc.)
                                .map(s -> s.replaceAll("[^0-9.\\-]", ""))
                                .filter(s -> !s.isEmpty())
                                .map(s -> {
                                    try {
                                        return new BigDecimal(s);
                                    } catch (NumberFormatException e) {
                                        LOG.warn("Invalid Provided value for {}: {}", key, s);
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .findFirst()   // <-- get only the first valid value
                                .orElse(BigDecimal.ZERO);


                        rearAndSideSetback = rearAndSideSetback.add(providedValue);
                        LOG.info("Provided setback for key [{}] = {}", key, providedValue);
                    }
                }

                
            }
            
            if(!isResidential) {
            	rearAndSideSetback = rearAndSideSetback.setScale(2, RoundingMode.HALF_UP);

                if (rearAndSideSetback.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal permissibleValue = BigDecimal.ZERO;
                    String permissibleDisplay = "";
                    String permissibleStr = detailsMap.get("Permissible");

                    if (permissibleStr != null && !permissibleStr.trim().isEmpty()) {
                        permissibleStr = permissibleStr.trim();

                        // Case 1: Permissible in meters (contains "m")
                        if (permissibleStr.toLowerCase().contains("m")) {
                            permissibleIsMeters = true;
                            String numericPart = permissibleStr.replaceAll("[^0-9.]", "");
                            if (!numericPart.isEmpty()) {
                                permissibleValue = new BigDecimal(numericPart).setScale(2, RoundingMode.HALF_UP);
                                permissibleDisplay = permissibleValue + " m";
                            }
                        }
                        // Case 2: Permissible as percentage (e.g., "20")
                        else {
                            try {
                                BigDecimal percentage = new BigDecimal(permissibleStr);
                                permissibleValue = plotArea
                                        .multiply(percentage)
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                                permissibleDisplay = permissibleStr + "% of plot area (" + permissibleValue + ")";
                            } catch (NumberFormatException e) {
                                LOG.warn("Invalid numeric Permissible value: {}", permissibleStr);
                            }
                        }
                    }

                    // ✅ Compare permissible vs provided
                    if (rearAndSideSetback.compareTo(permissibleValue) >= 0) {
                        status = Result.Accepted.getResultVal();
                    }

                    // ✅ Create combined key like "Block_1_Rear_And_Side Setback"
                    String combinedKey = blockPrefix + "_Rear Setback";

                    LOG.info("Block [{}]: Provided = {}, Permissible = {}, Status = {}",
                            blockPrefix, rearAndSideSetback, permissibleValue, status);

                    // ✅ Create new scrutiny detail entry
                    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                    scrutinyDetail.addColumnHeading(1, RULE_NO);
                    scrutinyDetail.addColumnHeading(2, LEVEL);
                    scrutinyDetail.addColumnHeading(3, OCCUPANCY);
                    scrutinyDetail.addColumnHeading(4, FIELDVERIFIED);
                    scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
                    scrutinyDetail.addColumnHeading(6, PROVIDED);
                    scrutinyDetail.addColumnHeading(7, STATUS);
                    //scrutinyDetail.setHeading(REAR_AND_SIDE_YARD_DESC);

                    scrutinyDetail.setKey(combinedKey);

                    Map<String, String> details = new HashMap<>();
                    details.put(RULE_NO, RULE);
                    details.put(LEVEL, detailsMap.get("Level"));
                    details.put(OCCUPANCY, detailsMap.get("Occupancy"));
                    details.put(PERMISSIBLE, permissibleDisplay);

                    // ✅ Only append "m" to provided if permissible was in meters
                    details.put(PROVIDED, permissibleIsMeters ? rearAndSideSetback + " m" : rearAndSideSetback.toPlainString());
                    details.put(STATUS, status);
                    details.put("isSetbackCombine", detailsMap.get("isSetbackCombine"));

                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                }
            }

            
        }
        
//        for (Map.Entry<String, List<ScrutinyDetail>> entry : blockWiseDetails.entrySet()) {
//            String blockPrefix = entry.getKey();
//            List<ScrutinyDetail> blockDetails = entry.getValue();
//
//            BigDecimal rearAndSideSetback = BigDecimal.ZERO;
//            Map<String, String> detailsMap = new HashMap<>();
//            String status = Result.Not_Accepted.getResultVal();
//            boolean permissibleIsMeters = false;
//            boolean isResidential = false;
//
//            // Collect all Rear+Side detail rows for this block (process them together, once)
//            List<Map<String, String>> rearSideAll = new ArrayList<>();
//
//            for (ScrutinyDetail scrutinyDetail : blockDetails) {
//                String key = scrutinyDetail.getKey();
//
//                // Extract occupancy type (from current scrutinyDetail)
//                String occupancyType = "";
//                if (scrutinyDetail.getDetail() != null && !scrutinyDetail.getDetail().isEmpty()) {
//                    Map<String, String> firstDetail = scrutinyDetail.getDetail().get(0);
//                    occupancyType = firstDetail.getOrDefault("Occupancy", "");
//                }
//
//                // If residential, push the whole scrutinyDetail to report as-is
//                if ("Residential".equalsIgnoreCase(occupancyType)) {
//                    isResidential = true;
//                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//                    continue; // skip rear/side aggregation for this scrutinyDetail (residential handled separately)
//                }
//
//                // For non-residential: front goes directly; rear & side are collected for combined processing
//                if (key != null && key.contains("Front")) {
//                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//                } else if (key != null && (key.contains("Rear") || key.contains("Side"))) {
//                    List<Map<String, String>> detailsList = scrutinyDetail.getDetail();
//                    if (detailsList != null && !detailsList.isEmpty()) {
//                        // copy detail rows into the combined list for block-level processing
//                        for (Map<String, String> d : detailsList) {
//                            // also capture meta keys once (prefer first occurrences)
//                            if (d.containsKey("Level")) detailsMap.putIfAbsent("Level", d.get("Level"));
//                            if (d.containsKey("Occupancy")) detailsMap.putIfAbsent("Occupancy", d.get("Occupancy"));
//                            if (d.containsKey("Permissible")) detailsMap.putIfAbsent("Permissible", d.get("Permissible"));
//
//                            rearSideAll.add(new HashMap<>(d)); // add a copy to avoid accidental mutation
//                        }
//                    }
//                }
//            }
//
//            // Process the collected Rear+Side rows once per block (deduplicate and sum)
//            if (!isResidential && !rearSideAll.isEmpty()) {
//                // Use a set to avoid duplicate rows. Key uses Side Number + Provided normalized (fallback to Provided alone)
//                Set<String> seen = new HashSet<>();
//                for (Map<String, String> detail : rearSideAll) {
//                    String sideNumber = detail.getOrDefault("Side Number", detail.getOrDefault("SideNumber", detail.get("Side Number"))); // safe read
//                    String providedRaw = detail.get("Provided");
//                    if (providedRaw == null || providedRaw.trim().isEmpty()) continue;
//
//                    // normalize provided: remove non-numeric except dot and minus
//                    String normalizedProvided = providedRaw.trim().replaceAll("[^0-9.\\-]", "");
//                    if (normalizedProvided.isEmpty()) continue;
//
//                    // dedupe key: prefer sideNumber if present
//                    String uniqKey = (StringUtils.isNotBlank(sideNumber) ? sideNumber.trim() + "|" : "") + normalizedProvided;
//
//                    if (seen.contains(uniqKey)) {
//                        LOG.debug("Skipping duplicate rear/side detail (dupKey={})", uniqKey);
//                        continue;
//                    }
//                    seen.add(uniqKey);
//
//                    // parse and add
//                    BigDecimal providedValue;
//                    try {
//                        providedValue = new BigDecimal(normalizedProvided);
//                    } catch (NumberFormatException e) {
//                        LOG.warn("Invalid Provided value for block {}: {}", blockPrefix, providedRaw);
//                        continue;
//                    }
//
//                    rearAndSideSetback = rearAndSideSetback.add(providedValue);
//                    LOG.info("Block [{}] - adding Provided {} (sideNumber={}): running total = {}", blockPrefix, providedValue, sideNumber, rearAndSideSetback);
//                }
//            }
//
//            // After processing, if not residential, produce the summary row (same as earlier)
//            if (!isResidential) {
//                rearAndSideSetback = rearAndSideSetback.setScale(2, RoundingMode.HALF_UP);
//
//                if (rearAndSideSetback.compareTo(BigDecimal.ZERO) > 0) {
//                    BigDecimal permissibleValue = BigDecimal.ZERO;
//                    String permissibleDisplay = "";
//                    String permissibleStr = detailsMap.get("Permissible");
//
//                    if (permissibleStr != null && !permissibleStr.trim().isEmpty()) {
//                        permissibleStr = permissibleStr.trim();
//                        if (permissibleStr.toLowerCase().contains("m")) {
//                            permissibleIsMeters = true;
//                            String numericPart = permissibleStr.replaceAll("[^0-9.]", "");
//                            if (!numericPart.isEmpty()) {
//                                permissibleValue = new BigDecimal(numericPart).setScale(2, RoundingMode.HALF_UP);
//                                permissibleDisplay = permissibleValue + " m";
//                            }
//                        } else {
//                            try {
//                                BigDecimal percentage = new BigDecimal(permissibleStr);
//                                permissibleValue = plotArea
//                                        .multiply(percentage)
//                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//                                permissibleDisplay = permissibleStr + "% of plot area (" + permissibleValue + ")";
//                            } catch (NumberFormatException e) {
//                                LOG.warn("Invalid numeric Permissible value: {}", permissibleStr);
//                            }
//                        }
//                    }
//
//                    if (rearAndSideSetback.compareTo(permissibleValue) >= 0) {
//                        status = Result.Accepted.getResultVal();
//                    }
//
//                    String combinedKey = blockPrefix + "_Rear Setback";
//                    LOG.info("Block [{}]: Provided = {}, Permissible = {}, Status = {}", blockPrefix, rearAndSideSetback, permissibleValue, status);
//
//                    ScrutinyDetail summaryDetail = new ScrutinyDetail();
//                    summaryDetail.addColumnHeading(1, RULE_NO);
//                    summaryDetail.addColumnHeading(2, LEVEL);
//                    summaryDetail.addColumnHeading(3, OCCUPANCY);
//                    summaryDetail.addColumnHeading(4, FIELDVERIFIED);
//                    summaryDetail.addColumnHeading(5, PERMISSIBLE);
//                    summaryDetail.addColumnHeading(6, PROVIDED);
//                    summaryDetail.addColumnHeading(7, STATUS);
//
//                    summaryDetail.setKey(combinedKey);
//
//                    Map<String, String> summaryMap = new HashMap<>();
//                    summaryMap.put(RULE_NO, RULE);
//                    summaryMap.put(LEVEL, detailsMap.get("Level"));
//                    summaryMap.put(OCCUPANCY, detailsMap.get("Occupancy"));
//                    summaryMap.put(PERMISSIBLE, permissibleDisplay);
//                    summaryMap.put(PROVIDED, permissibleIsMeters ? rearAndSideSetback + " m" : rearAndSideSetback.toPlainString());
//                    summaryMap.put(STATUS, status);
//
//                    summaryDetail.getDetail().add(summaryMap);
//                    pl.getReportOutput().getScrutinyDetails().add(summaryDetail);
//                }
//            }
//        }


    }






}
