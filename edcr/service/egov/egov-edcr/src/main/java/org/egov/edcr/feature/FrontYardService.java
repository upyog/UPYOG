
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
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.A_PO;
import static org.egov.edcr.constants.DxfFileConstants.A_R;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.G;
import static org.egov.edcr.constants.DxfFileConstants.I;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;
import static org.egov.edcr.utility.DcrConstants.FRONT_YARD_DESC;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrontYardService extends GeneralRule {
	
	private static final Logger LOG = LogManager.getLogger(FrontYardService.class);
	

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;
	
	@Autowired
	MDMSCacheManager cache;

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
	}


	/**
	 * Validates the front yard setbacks based on occupancy type, plot details, and MDMS rules.
	 *
	 * @param pl                        The current Plan object being processed.
	 * @param building                 The building object associated with the block.
	 * @param blockName                The name of the block.
	 * @param level                    The level/floor being validated.
	 * @param plot                     The plot object from the plan.
	 * @param frontYardFieldName       The descriptor for the front yard field (used in reporting).
	 * @param min                      The minimum distance provided in the front yard.
	 * @param mean                     The mean distance provided in the front yard.
	 * @param mostRestrictiveOccupancy The most restrictive occupancy type applicable to this block.
	 * @param frontYardResult          The result object to store front yard validation outcome.
	 * @param errors                   A map to store error messages if any rule is violated.
	 * @return                         True if the front yard is valid, false otherwise.
	 */

	private Boolean checkFrontYard(Plan pl, Building building, String blockName, Integer level, Plot plot,
			String frontYardFieldName, BigDecimal min, BigDecimal mean, OccupancyTypeHelper mostRestrictiveOccupancy,
			FrontYardResult frontYardResult, HashMap<String, String> errors) {
		Boolean valid = false;
		String subRule = "";
		String rule = FRONT_YARD_DESC;
		BigDecimal minVal = BigDecimal.ZERO;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();
		String occupancyName = "";
		
//		if (mostRestrictiveOccupancy.getSubtype() != null
//				&& (A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())
//						|| A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
//						|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
//						|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {
		if (A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			if (pl.getPlanInformation() != null && pl.getPlanInformation().getRoadWidth() != null
					&& StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
					&& DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
//					&& pl.getPlanInformation().getRoadWidth().compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0
			) {
				occupancyName = MdmsFeatureConstants.COMMERCIAL;
				
			} else {
				occupancyName = MdmsFeatureConstants.RESIDENTIAL;
				
			}
		} else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			
			occupancyName = MdmsFeatureConstants.COMMERCIAL;
		
		} else if (G.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
			occupancyName = MdmsFeatureConstants.INDUSTRIAL;
			
		}else {
			 occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
		}
//		else if (mostRestrictiveOccupancy.getType() != null
//				&& J.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
//			   occupancyName = "Government/Semi Government";
//		}
		valid = processFrontYardService(blockName, level, min, mean, mostRestrictiveOccupancy, frontYardResult, valid,
				subRule, rule, minVal, meanVal, depthOfPlot, errors, pl,  occupancyName
				);
		return valid;
	}
	
	/**
	 * Processes the front yard validation for each block in the plan.
	 * Initiates validation and rule matching for the front yard setbacks.
	 *
	 * @param pl The Plan object containing plot and block information.
	 */


	public void processFrontYard(Plan pl) {
	    if (pl == null || pl.getPlot() == null || pl.getBlocks().isEmpty()) return;

	    validateFrontYard(pl);

	    for (Block block : pl.getBlocks()) {
	        processBlockFrontYard(pl, block);
	    }
	}
	
	/**
	 * Processes front yard validation for a specific block.
	 * Extracts setback values, height, occupancy, and initiates rule validation.
	 *
	 * @param pl    The complete Plan object.
	 * @param block The specific block for which front yard is being validated.
	 */

	private void processBlockFrontYard(Plan pl, Block block) {
	    ScrutinyDetail scrutinyDetail = createScrutinyDetail(block.getName());
	    FrontYardResult frontYardResult = new FrontYardResult();
	    HashMap<String, String> errors = new HashMap<>();

	    for (SetBack setback : block.getSetBacks()) {
	        if (setback.getFrontYard() == null) continue;

	        BigDecimal min = setback.getFrontYard().getMinimumDistance();
	        BigDecimal mean = setback.getFrontYard().getMean();

	        if ((min == null || min.compareTo(BigDecimal.ZERO) <= 0) &&
	            (mean == null || mean.compareTo(BigDecimal.ZERO) <= 0)) continue;

	        BigDecimal buildingHeight = getBuildingHeight(block, setback);

	        if (buildingHeight == null) continue;

	        Occupancy occupancy = block.getBuilding().getTotalArea().get(0); // Only first occupancy considered
	        checkFrontYard(pl, block.getBuilding(), block.getName(), setback.getLevel(), pl.getPlot(),
	                FRONT_YARD_DESC, min, mean, occupancy.getTypeHelper(), frontYardResult, errors);

	        if (errors.isEmpty()) {
	            Map<String, String> details = buildScrutinyDetailMap(frontYardResult);
	            scrutinyDetail.getDetail().add(details);
	            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	        }
	    }
	}
	
	/**
	 * Creates a ScrutinyDetail object for recording front yard validation results.
	 *
	 * @param blockName The name of the block for which the scrutiny detail is being generated.
	 * @return          A ScrutinyDetail object with initialized headings.
	 */


	private ScrutinyDetail createScrutinyDetail(String blockName) {
	    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.setKey(BLOCK + blockName + UNDERSCORE + FRONT_YARD_DESC);
	    scrutinyDetail.setHeading(FRONT_YARD_DESC);
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, LEVEL);
	    scrutinyDetail.addColumnHeading(3, OCCUPANCY);
	    scrutinyDetail.addColumnHeading(4, FIELDVERIFIED);
	    scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
	    scrutinyDetail.addColumnHeading(6, PROVIDED);
	    scrutinyDetail.addColumnHeading(7, STATUS);
	    return scrutinyDetail;
	}

	
	/**
	 * Determines the building height for validation.
	 * Uses height from front yard if available; otherwise, uses building's total height.
	 *
	 * @param block   The block whose building height is being queried.
	 * @param setback The setback object which may contain yard-specific height.
	 * @return        The height to be used for validation.
	 */

	private BigDecimal getBuildingHeight(Block block, SetBack setback) {
	    if (setback.getFrontYard().getHeight() != null && 
	        setback.getFrontYard().getHeight().compareTo(BigDecimal.ZERO) > 0) {
	        return setback.getFrontYard().getHeight();
	    }
	    return block.getBuilding().getBuildingHeight();
	}

	/**
	 * Builds a map of scrutiny details for a single front yard validation.
	 *
	 * @param result The result object containing comparison values and status.
	 * @return       A map with headings and values used in the scrutiny report.
	 */

	private Map<String, String> buildScrutinyDetailMap(FrontYardResult result) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(result.subRule);
		detail.setOccupancy(result.occupancy);
		detail.setLevel(result.level != null ? result.level.toString() : EMPTY_STRING);
		detail.setFieldVerified(MINIMUMLABEL);
		detail.setPermissible(result.expectedmeanDistance.toString());
		detail.setProvided(result.actualMinDistance.toString());
		detail.setStatus(result.status ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

	    return mapReportDetails(detail);
	}

	
	/**
	 * Processes front yard validation logic by fetching applicable MDMS rules
	 * and comparing against provided values.
	 *
	 * @param blockName                Name of the block being processed.
	 * @param level                    Level/floor of the block.
	 * @param min                      Minimum distance from input/setback.
	 * @param mean                     Mean distance from input/setback.
	 * @param mostRestrictiveOccupancy The most restrictive occupancy type.
	 * @param frontYardResult          Object that will store result of the comparison.
	 * @param valid                    Initial validity flag, may be updated.
	 * @param subRule                  Sub-rule identifier used in reporting.
	 * @param rule                     Rule identifier used in reporting.
	 * @param minVal                   Minimum permissible value from MDMS.
	 * @param meanVal                  Mean permissible value from MDMS.
	 * @param depthOfPlot              The depth of the plot from Plan.
	 * @param errors                   A map to store any errors encountered.
	 * @param pl                       The Plan object being processed.
	 * @param occupancyName            The resolved occupancy name string.
	 * @return                         True if validation passes, false otherwise.
	 */

	private Boolean processFrontYardService(String blockName, Integer level, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, BigDecimal depthOfPlot,
			HashMap<String, String> errors, Plan pl, String occupancyName) {

		BigDecimal plotArea = pl.getPlot().getArea();

		// Fetch all rules for the given plan from the cache.
		// Then, filter to find the first rule where the condition falls within the
		// defined range.
		// If a matching rule is found, proceed with its processing.

		 List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.FRONT_SET_BACK.getValue(), true);
	        Optional<FrontSetBackRequirement> matchedRule = rules.stream()
	            .filter(FrontSetBackRequirement.class::isInstance)
	            .map(FrontSetBackRequirement.class::cast)
	            .filter(ruleFeature -> plotArea.compareTo(ruleFeature.getFromPlotArea()) >= 0 && plotArea.compareTo(ruleFeature.getToPlotArea()) < 0)
	            .findFirst();

		if (matchedRule.isPresent()) {
			FrontSetBackRequirement mdmsRule = matchedRule.get();
			meanVal = mdmsRule.getPermissible();
		} else {
			meanVal = BigDecimal.ZERO;
		}

		System.out.println("meanVllll" + meanVal);
		/*
		 * if (-1 == level) { rule = BSMT_FRONT_YARD_DESC; subRuleDesc =
		 * SUB_RULE_24_12_DESCRIPTION; subRule = SUB_RULE_24_12; }
		 */

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
				minVal, meanVal, level);
		return valid;
	}



	// Added by Bimal 18-March-2924 to check front yard based on plot are not on height
	private Boolean checkFrontYardResidentialCommon(Plan pl, Building building, String blockName, Integer level,
			Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult,
			HashMap<String, String> errors) {
		Boolean valid = false;
		String subRule = RULE_4_4_4_I;
		String rule = FRONT_YARD_DESC;
		BigDecimal meanVal = BigDecimal.ZERO;
		BigDecimal depthOfPlot = pl.getPlanInformation().getDepthOfPlot();
		BigDecimal plotArea = pl.getPlanInformation().getPlotArea();

		// Process only for A_R, A_AF, and A_ occupancy types
		if (mostRestrictiveOccupancy.getSubtype() != null
				&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode())
						|| A_PO.equalsIgnoreCase(mostRestrictiveOccupancy.getSubtype().getCode()))) {

			valid = processFrontYardResidential(blockName, level, min, mean, mostRestrictiveOccupancy, frontYardResult,
					valid, subRule, rule, meanVal, depthOfPlot, errors, pl, plotArea);

		}

		return valid;
	}
	//Added by Bimal 18-March-2924 to check front yard based on plot are not on height
	private Boolean processFrontYardResidential(String blockName, Integer level,  BigDecimal min, BigDecimal mean,
	        OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
	        String subRule, String rule, BigDecimal meanVal, BigDecimal depthOfPlot,
	        HashMap<String, String> errors, Plan pl, BigDecimal plotArea) {
		
		LOG.info("Processing FrontYardResult:");

	    BigDecimal minVal = BigDecimal.ZERO; 

	    // Set minVal based on plot area
	    if (plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
	        // Plot area is less than zero
	    	errors.put(PLOT_AREA_ERROR, PLOT_AREA_CANNOT_BE_LESS +MIN_PLOT_AREA);
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
	    }

	    // Validate minimum and mean value
	    valid = validateMinimumAndMeanValue(min, mean, minVal, mean);

//	    // Add error if plot area is less than or equal to 10
//	    if (plotArea.compareTo(MIN_PLOT_AREA) <= 0) {
//	        errors.put("uptoSixteenHeightUptoTenDepthFrontYard",
//	                "No construction shall be permitted if depth of plot is less than 10 and building height less than 16 having floors upto G+4.");
//	        pl.addErrors(errors);
//	    }
	    if(!valid) {
	    	LOG.info("Front Yard Service: min value validity False: "+minVal+"/"+min);
	    	errors.put(MIN_AND_MEAN_VALUE, MIN_LESS_REQ_MIN + minVal+ SLASH +min);
	    	
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
					errors.put(FRONT_YARD_DESC,
							prepareMessage(OBJECTNOTDEFINED, FRONT_YARD_DESC + FOR_BLOCK + block.getName()));
					pl.addErrors(errors);
				}
			}

		}

	}

	private Boolean checkFrontYardUptoSixteenMts(SetBack setback, Building building, BigDecimal blockBuildingHeight,
			Plan pl, Integer level, Block block, Plot plot, String frontYardFieldName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult,
			HashMap<String, String> errors) {
		Boolean valid = false;
		String subRule = RULE_4_4_4_I;
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
			errors.put(SIXTEEN_HEIGHT_TEN_DEPTH_FRONTE_YARD, NOT_PERMITTED_DEPTH_LESS_10_HEIGHT_16);
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
		String subRule = RULE_4_4_4_I;
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
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult) {
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
			// nil as per commercial
			subRule = RULE_37_TWO_I;
		}

		valid = validateMinimumAndMeanValue(min, mean, minVal, meanVal);

		compareFrontYardResult(blockName, min, mean, mostRestrictiveOccupancy, frontYardResult, valid, subRule, rule,
				minVal, meanVal, level);
		return valid;
	}

	private void compareFrontYardResult(String blockName, BigDecimal min, BigDecimal mean,
			OccupancyTypeHelper mostRestrictiveOccupancy, FrontYardResult frontYardResult, Boolean valid,
			String subRule, String rule, BigDecimal minVal, BigDecimal meanVal, Integer level) {
		String occupancyName;
		if (mostRestrictiveOccupancy.getSubtype() != null)
			occupancyName = mostRestrictiveOccupancy.getSubtype().getName();
		else
			occupancyName = mostRestrictiveOccupancy.getType().getName();
		if (minVal.compareTo(frontYardResult.expectedminimumDistance) >= 0) {
			if (minVal.compareTo(frontYardResult.expectedminimumDistance) == 0) {
				frontYardResult.rule = frontYardResult.rule != null ? frontYardResult.rule + "," + rule : rule;
				frontYardResult.occupancy = frontYardResult.occupancy != null
						? frontYardResult.occupancy + "," + occupancyName
						: occupancyName;
			} else {
				frontYardResult.rule = rule;
				frontYardResult.occupancy = occupancyName;
			}

			frontYardResult.subRule = subRule;
			frontYardResult.blockName = blockName;
			frontYardResult.level = level;
			frontYardResult.expectedminimumDistance = minVal;
			frontYardResult.expectedmeanDistance = meanVal;
			frontYardResult.actualMinDistance = min;
			frontYardResult.actualMeanDistance = mean;
			frontYardResult.status = valid;

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
			errors.put(TWELVE_HEIGHT_TEN_DEPTH_FRONT_YARD, NOT_PERMITTED_DEPTH_LESS_10_HEIGHT_12);
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
}
