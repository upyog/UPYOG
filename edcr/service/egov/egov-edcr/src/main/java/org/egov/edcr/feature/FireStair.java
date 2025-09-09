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
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

/*
 * This class is responsible for validating and processing fire stair dimensions
 * in building plans. It ensures compliance with predefined rules for fire stair
 * width, tread, risers, and other related parameters.
 */

@Service
public class FireStair extends FeatureProcess {

	// Logger for logging information and errors
	private static final Logger LOG = LogManager.getLogger(FireStair.class);

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;

	@Autowired
	MDMSCacheManager cache;

	/**
	 * Validates the given plan object. Currently, no specific validation logic is
	 * implemented.
	 *
	 * @param plan The plan object to validate.
	 * @return The same plan object without any modifications.
	 */
	@Override
	public Plan validate(Plan plan) {
		return plan;
	}
	

	/**
	 * Processes the given plan to validate fire stair dimensions. Fetches
	 * permissible values for fire stair dimensions and validates them against the
	 * plan details.
	 *
	 * @param plan The plan object to process.
	 * @return The processed plan object with scrutiny details added.
	 */
	
	 public Plan process(Plan plan) {
	        Map<String, String> errors = new HashMap<>();

	        // Load permissible fire stair values (now method-scoped, not class-level)
	        FireStairRequirement fireStairReq = loadFireStairRuleValues(plan);

	        if (fireStairReq != null) {
	            for (Block block : plan.getBlocks()) {
	                if (block.getBuilding() != null) {
	                    processFireStairBlock(plan, block, errors, fireStairReq);
	                }
	            }
	        }

	        return plan;
	    }


		/**
		 * Loads the fire stair related rule values from the MDMS rule set for the given plan.
		 * <p>
		 * It fetches the feature rules for {@link MdmsFeatureConstants#FIRE_STAIR} and sets
		 * the expected number of risers, minimum width, and required tread width of the fire stair
		 * based on the first matching rule.
		 * </p>
		 *
		 * @param plan the building plan for which the fire stair rules are to be loaded
		 */
		
	    private FireStairRequirement loadFireStairRuleValues(Plan plan) {
	        List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.FIRE_STAIR.getValue(), false);
	        return rules.stream()
	                .filter(FireStairRequirement.class::isInstance)
	                .map(FireStairRequirement.class::cast)
	                .findFirst()
	                .orElse(null);
	    }

	   

		/**
		 * Validates an individual fire stair for tread, riser count, mid-landing, and abutment.
		 * Adds the corresponding details to scrutiny reports.
		 *
		 * @param plan                the building plan
		 * @param block               the block containing the fire stair
		 * @param floor               the floor on which fire stair exists
		 * @param fireStair           the fire stair object to process
		 * @param typicalFloorValues  values used for typical floor comparison
		 * @param errors              map for collecting validation errors
		 * @param sdWidth             scrutiny detail for stair width
		 * @param sdTread             scrutiny detail for stair tread
		 * @param sdRise              scrutiny detail for stair riser count
		 * @param sdLanding           scrutiny detail for stair landing
		 * @param sdAbutWall          scrutiny detail for stair abutment to external wall
		 */

	    private void processFireStair(Plan plan, Block block, Floor floor,
	                                  org.egov.common.entity.edcr.FireStair fireStair,
	                                  Map<String, Object> typicalFloorValues, Map<String, String> errors,
	                                  ScrutinyDetail sdWidth, ScrutinyDetail sdTread, ScrutinyDetail sdRise,
	                                  ScrutinyDetail sdLanding, ScrutinyDetail sdAbutWall,
	                                  FireStairRequirement fireStairReq) {

	        setReportOutputDetailsBltUp(plan, RULE42_5_II, floor.getNumber().toString(),
	                FIRE_STAIR_ABUT_DESC,
	                fireStair.isAbuttingBltUp() ? FIRE_STAIR_ABUT_PROVIDED_YES : FIRE_STAIR_ABUT_PROVIDED_NO,
	                fireStair.isAbuttingBltUp() ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal(),
	                sdAbutWall);

	        validateFlight(plan, (HashMap<String, String>) errors,
	                block, sdWidth, sdTread, sdRise, null,
	                floor, typicalFloorValues, fireStair,
	                fireStairReq.getFireStairRequiredTread(),
	                fireStairReq.getFireStairExpectedNoofRise(),
	                fireStairReq.getFireStairMinimumWidth());

	        List<StairLanding> landings = fireStair.getLandings();
	        if (!landings.isEmpty()) {
	            validateLanding(plan, block, sdLanding, floor, typicalFloorValues, fireStair, landings,
	                    (HashMap<String, String>) errors, fireStairReq.getFireStairMinimumWidth());
	        } else {
	            String errorMsg = FIRE_STAIR_LANDING_NOT_DEFINED_BLK + block.getNumber() + FLOOR_SPACED
	                    + floor.getNumber() + FIRE_STAIR + fireStair.getNumber();
	            errors.put(errorMsg, errorMsg);
	            plan.addErrors(errors);
	        }
	    }
	

		/**
		 * Processes the fire stair information for a given block in the plan. It
		 * validates the presence of fire stairs and ensures all required rules are
		 * checked such as tread, width, number of risers, mid-landing, and abutment
		 * with external walls.
		 *
		 * @param plan   the plan object containing building data
		 * @param block  the block to process fire stairs for
		 * @param errors map for collecting validation errors
		 */
		private void processFireStairBlock(Plan plan, Block block, Map<String, String> errors,
				FireStairRequirement fireStairReq) {
			int fireStairCount = 0;
			List<String> fireStairAbsent = new ArrayList<>();

			ScrutinyDetail scrutinyDetailWidth = createScrutinyDetail(block, FIRE_STAIR_WIDTH);
			ScrutinyDetail scrutinyDetailTread = createScrutinyDetail(block, FIRE_STAIR_TREAD);
			ScrutinyDetail scrutinyDetailRise = createScrutinyDetail(block, FIRE_STAIR_RISERS);
			ScrutinyDetail scrutinyDetailLanding = createScrutinyDetail(block, FIRE_STAIR_LANDING);
			ScrutinyDetail scrutinyDetailAbutWall = createScrutinyDetail(block, FIRE_STAIR_ABUTTING);

			for (Floor floor : block.getBuilding().getFloors()) {
				if (!floor.getTerrace()) {
					boolean isTypicalRepititiveFloor = false;
					Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor,
							isTypicalRepititiveFloor);

					List<org.egov.common.entity.edcr.FireStair> fireStairs = floor.getFireStairs();
					fireStairCount += fireStairs.size();

					if (!fireStairs.isEmpty()) {
						for (org.egov.common.entity.edcr.FireStair fireStair : fireStairs) {
							processFireStair(plan, block, floor, fireStair, typicalFloorValues, errors,
									scrutinyDetailWidth, scrutinyDetailTread, scrutinyDetailRise, scrutinyDetailLanding,
									scrutinyDetailAbutWall, fireStairReq);
						}
					} else if (block.getBuilding().getIsHighRise()) {
						fireStairAbsent.add(BLOCK + block.getNumber() + FLOOR_SPACED + floor.getNumber());
					}
				}
			}

			if (!fireStairAbsent.isEmpty()) {
				for (String error : fireStairAbsent) {
					errors.put(FIRE_STAIR_C + error, FIRE_STAIR_NOT_DEFINED + error);
				}
				plan.addErrors(errors);
			}

			if (block.getBuilding().getIsHighRise() && fireStairCount == 0) {
				errors.put(FIRE_STAIR_NOT_DEFINED_BLK + block.getNumber(),
						FIRE_STAIR_NOT_DEFINED_BLOCK + block.getNumber() + MANDATORY_BUILDING_HEIGHT_15M);
				plan.addErrors(errors);
			}
		}



	/**
	 * Creates a scrutiny detail object for a given block with specified heading.
	 *
	 * @param block     the block for which scrutiny detail is created
	 * @param keySuffix a suffix used to identify the detail category
	 * @return a populated ScrutinyDetail object with appropriate headings
	 */
	private ScrutinyDetail createScrutinyDetail(Block block, String keySuffix) {
		ScrutinyDetail sd = new ScrutinyDetail();
		sd.addColumnHeading(1, RULE_NO);
		sd.addColumnHeading(2, FLOOR);
		sd.addColumnHeading(3, DESCRIPTION);
		sd.addColumnHeading(4, PERMISSIBLE);
		sd.addColumnHeading(5, PROVIDED);
		sd.addColumnHeading(6, STATUS);
		sd.setKey(BLOCK + block.getNumber() + UNDERSCORE + keySuffix);
		return sd;
	}

	/**
	 * Validates the landings for a fire stair. Checks if the landing width meets
	 * the permissible value.
	 *
	 * @param plan                  The plan object.
	 * @param block                 The block containing the fire stair.
	 * @param scrutinyDetailLanding The scrutiny detail object for landing
	 *                              validation.
	 * @param floor                 The floor containing the fire stair.
	 * @param typicalFloorValues    Typical floor values for validation.
	 * @param fireStair             The fire stair object.
	 * @param landings              The list of landings to validate.
	 * @param errors                The map to store validation errors.
	 */
	private void validateLanding(Plan plan, Block block, ScrutinyDetail scrutinyDetailLanding, Floor floor,
			Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.FireStair fireStair,
			List<StairLanding> landings, HashMap<String, String> errors, BigDecimal fireStairMinimumWidth) {
		for (StairLanding landing : landings) {
			List<BigDecimal> widths = landing.getWidths();
			if (!widths.isEmpty()) {
				BigDecimal landingWidth = widths.stream().reduce(BigDecimal::min).get();
				BigDecimal minWidth = BigDecimal.ZERO;
				boolean valid = false;

				if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
					minWidth = Util.roundOffTwoDecimal(landingWidth);

					if (minWidth.compareTo(fireStairMinimumWidth) >= 0) {
						valid = true;
					}
					String value = typicalFloorValues.get(TYPICAL_FLOOR) != null
							? (String) typicalFloorValues.get(TYPICAL_FLOOR)
							: FLOOR_SPACED + floor.getNumber();

					if (valid) {
						setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
								String.format(WIDTH_LANDING_DESCRIPTION, fireStair.getNumber(), landing.getNumber()),
								fireStairMinimumWidth.toString(), String.valueOf(minWidth),
								Result.Accepted.getResultVal(), scrutinyDetailLanding);
					} else {
						setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
								String.format(WIDTH_LANDING_DESCRIPTION, fireStair.getNumber(), landing.getNumber()),
								fireStairMinimumWidth.toString(), String.valueOf(minWidth),
								Result.Not_Accepted.getResultVal(), scrutinyDetailLanding);
					}
				}
			} else {
				errors.put(
						FIRE_STAIR_LANDING_WIDTH_NOT_DEFINED_BLK + block.getNumber() + FLOOR_SPACED
								+ floor.getNumber() + FIRE_STAIR + fireStair.getNumber(),
						FIRE_STAIR_LANDING_WIDTH_NOT_DEFINED_BLK + block.getNumber() + FLOOR_SPACED
								+ floor.getNumber() + FIRE_STAIR + fireStair.getNumber());
				plan.addErrors(errors);
			}
		}
	}

	/**
	 * Validates a flight in a fire stair for width, tread, and number of risers.
	 * Ensures polyline definitions exist and the flight is closed. Adds errors if conditions are not met.
	 *
	 * @param plan                     the building plan
	 * @param errors                   map to collect validation errors
	 * @param block                    the block where the flight is located
	 * @param scrutinyDetail2          scrutiny detail for width
	 * @param scrutinyDetail3          scrutiny detail for tread
	 * @param scrutinyDetailRise       scrutiny detail for riser count
	 * @param mostRestrictiveOccupancyType most restrictive occupancy type, if applicable
	 * @param floor                    the floor on which flight exists
	 * @param typicalFloorValues       values used for typical floor comparison
	 * @param fireStair                the fire stair the flight belongs to
	 * @param fireStairRequiredTread   required minimum tread depth
	 * @param fireStairExpectedNoofRise expected number of risers
	 */
	private void validateFlight(Plan plan, HashMap<String, String> errors, Block block, ScrutinyDetail scrutinyDetail2,
			ScrutinyDetail scrutinyDetail3, ScrutinyDetail scrutinyDetailRise,
			OccupancyTypeHelper mostRestrictiveOccupancyType, Floor floor, Map<String, Object> typicalFloorValues,
			org.egov.common.entity.edcr.FireStair fireStair, BigDecimal fireStairRequiredTread,
			BigDecimal fireStairExpectedNoofRise, BigDecimal fireStairMinimumWidth) {

		if (!fireStair.getFlights().isEmpty()) {
			for (Flight flight : fireStair.getFlights()) {
				List<Measurement> flightPolyLines = flight.getFlights();
				List<BigDecimal> flightLengths = flight.getLengthOfFlights();
				List<BigDecimal> flightWidths = flight.getWidthOfFlights();
				BigDecimal noOfRises = flight.getNoOfRises();
				Boolean flightPolyLineClosed = flight.getFlightClosed();

				BigDecimal minTread = BigDecimal.ZERO;
				BigDecimal minFlightWidth = BigDecimal.ZERO;
				String flightLayerName = String.format(DxfFileConstants.LAYER_FIRESTAIR_FLIGHT, block.getNumber(),
						floor.getNumber(), fireStair.getNumber(), flight.getNumber());

				if (!floor.getTerrace()) {
					if (flightPolyLines != null && flightPolyLines.size() > 0) {
						if (flightPolyLineClosed) {
							if (flightWidths != null && flightWidths.size() > 0) {
								minFlightWidth = validateWidth(plan, scrutinyDetail2, floor, block, typicalFloorValues,
										fireStair, flight, flightWidths, minFlightWidth, mostRestrictiveOccupancyType, fireStairMinimumWidth);

							} else {
								errors.put(FLIGHT_POLYLINE_WIDTH + flightLayerName,
										FLIGHT_WIDTH_DEFINED_DESCRIPTION + flightLayerName);
								plan.addErrors(errors);
							}

							/*
							 * (Total length of polygons in layer BLK_n_FLR_i_FIRESTAIR_k_FLIGHT) / (Number
							 * of rises - number of polygons in layer BLK_n_FLR_i_FIRESTAIR_k_FLIGHT -
							 * number of lines in layer BLK_n_FLR_i_FIRESTAIR_k_FLIGHT)
							 */

							if (flightLengths != null && flightLengths.size() > 0) {
								try {
									minTread = validateTread(plan, errors, block, scrutinyDetail3, floor,
											typicalFloorValues, fireStair, flight, flightLengths, minTread,
											mostRestrictiveOccupancyType, fireStairRequiredTread);
								} catch (ArithmeticException e) {
									LOG.info("Denominator is zero");
								}
							} else {
								errors.put(FLIGHT_POLYLINE_LENGTH + flightLayerName,
										FLIGHT_LENGTH_DEFINED_DESCRIPTION + flightLayerName);
								plan.addErrors(errors);

							}

							if (noOfRises.compareTo(BigDecimal.ZERO) > 0) {
								try {
									validateNoOfRises(plan, errors, block, scrutinyDetailRise, floor,
											typicalFloorValues, flight, fireStair, noOfRises,
											fireStairExpectedNoofRise);
								} catch (ArithmeticException e) {
									LOG.info("Denominator is zero");
								}
							} else {
								String layerName = String.format(DxfFileConstants.LAYER_FIRESTAIR_FLIGHT,
										block.getNumber(), floor.getNumber(), fireStair.getNumber());
								errors.put(NO_OF_RISE + layerName,
										edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
												new String[] { NO_OF_RISERS + layerName },
												LocaleContextHolder.getLocale()));
								plan.addErrors(errors);
							}

						}
					} else {
						errors.put(FLIGHT_POLYLINE + flightLayerName,
								FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION + flightLayerName);
						plan.addErrors(errors);
					}
				}

				/*
				 * List<Line> lines = fireStair.getLinesInFlightLayer(); if (lines != null &&
				 * lines.size() > 0) { Line line =
				 * lines.stream().min(Comparator.comparing(Line::getLength)).get(); boolean
				 * valid = false; if (line != null) { BigDecimal lineLength =
				 * Util.roundOffTwoDecimal(line.getLength()); if (!(Boolean)
				 * typicalFloorValues.get("isTypicalRepititiveFloor")) { BigDecimal
				 * minLineLength = Util.roundOffTwoDecimal(BigDecimal.valueOf(0.75)); if
				 * (lineLength.compareTo(minLineLength) >= 0) { valid = true; } String value =
				 * typicalFloorValues.get("typicalFloors") != null ? (String)
				 * typicalFloorValues.get("typicalFloors") : " floor " + floor.getNumber(); if
				 * (valid) setReportOutputDetailsFloorStairWise(planDetail, RULE114, value,
				 * String.format(LINE_DESCRIPTION, fireStair.getNumber()), EXPECTED_LINE,
				 * String.valueOf(lineLength), Result.Accepted.getResultVal(), scrutinyDetail6);
				 * else setReportOutputDetailsFloorStairWise(planDetail, RULE114, value,
				 * String.format(LINE_DESCRIPTION, fireStair.getNumber()), EXPECTED_LINE,
				 * String.valueOf(lineLength), Result.Not_Accepted.getResultVal(),
				 * scrutinyDetail6); } } }
				 */

				/*
				 * if (minFlightWidth.compareTo(BigDecimal.valueOf(1.2)) >= 0 &&
				 * minTread.compareTo(BigDecimal.valueOf(0.3)) >= 0 && !floor.getTerrace()) {
				 * fireStair.setGeneralStair(true); }
				 */

			}
		} else {
			String error = String.format(FLIGHT_NOT_DEFINED_DESCRIPTION, block.getNumber(), floor.getNumber());
			errors.put(error, error);
			plan.addErrors(errors);
		}
	}

	/**
	 * Validates the minimum width of a fire stair flight on a specific floor against the required fire stair minimum width.
	 * If the floor is not a typical repetitive floor, it compares the minimum width from all polylines with the required width,
	 * logs the result in the scrutiny report, and returns the validated width.
	 *
	 * @param plan                      the overall building plan object
	 * @param scrutinyDetail2          the scrutiny detail object for logging the width validation result
	 * @param floor                     the specific floor being validated
	 * @param block                     the block to which the floor and fire stair belong
	 * @param typicalFloorValues       map containing flags and floor numbers for typical/repetitive floor conditions
	 * @param fireStair                 the fire stair object associated with the flight
	 * @param flight                    the specific flight to be validated
	 * @param flightWidths             list of widths derived from the flight geometry (e.g., polylines)
	 * @param minFlightWidth           the minimum flight width calculated so far (will be updated and returned)
	 * @param mostRestrictiveOccupancyType the most restrictive occupancy type applicable for validation
	 * @return the updated minimum flight width after validation
	 */
	private BigDecimal validateWidth(Plan plan, ScrutinyDetail scrutinyDetail2, Floor floor, Block block,
			Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.FireStair fireStair, Flight flight,
			List<BigDecimal> flightWidths, BigDecimal minFlightWidth,
			OccupancyTypeHelper mostRestrictiveOccupancyType, BigDecimal fireStairMinimumWidth) {
		BigDecimal flightPolyLine = flightWidths.stream().reduce(BigDecimal::min).get();

		boolean valid = false;

		if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
			minFlightWidth = Util.roundOffTwoDecimal(flightPolyLine);

			if (minFlightWidth.compareTo(fireStairMinimumWidth) >= 0) {
				valid = true;
			}
			String value = typicalFloorValues.get(TYPICAL_FLOOR) != null
					? (String) typicalFloorValues.get(TYPICAL_FLOOR)
					: FLOOR_SPACED + floor.getNumber();

			if (valid) {
				setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
						String.format(WIDTH_DESCRIPTION_FIRE_STAIR, fireStair.getNumber(), flight.getNumber()),
						fireStairMinimumWidth.toString(), String.valueOf(minFlightWidth),
						Result.Accepted.getResultVal(), scrutinyDetail2);
			} else {
				setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
						String.format(WIDTH_DESCRIPTION_FIRE_STAIR, fireStair.getNumber(), flight.getNumber()),
						fireStairMinimumWidth.toString(), String.valueOf(minFlightWidth),
						Result.Not_Accepted.getResultVal(), scrutinyDetail2);
			}
		}
		return minFlightWidth;
	}

	/*
	 * private BigDecimal getRequiredWidth(Block block, OccupancyTypeHelper
	 * mostRestrictiveOccupancyType) { if (mostRestrictiveOccupancyType.getType() !=
	 * null &&
	 * DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType(
	 * ).getCode()) &&
	 * block.getBuilding().getBuildingHeight().compareTo(BigDecimal.valueOf(10)) <=
	 * 0 &&
	 * block.getBuilding().getFloorsAboveGround().compareTo(BigDecimal.valueOf(3 ))
	 * <= 0) { return BigDecimal.ONE; } else if
	 * (mostRestrictiveOccupancyType.getType() != null &&
	 * DxfFileConstants.A_AF_GH.equalsIgnoreCase(mostRestrictiveOccupancyType.
	 * getType().getCode())) { return BigDecimal.valueOf(0.75); } else if
	 * (mostRestrictiveOccupancyType.getType() != null &&
	 * DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType(
	 * ).getCode())) { return BigDecimal.valueOf(1.25); } else if
	 * (mostRestrictiveOccupancyType.getType() != null &&
	 * DxfFileConstants.B.equalsIgnoreCase(mostRestrictiveOccupancyType.getType(
	 * ).getCode())) { return BigDecimal.valueOf(1.5); } else if
	 * (mostRestrictiveOccupancyType.getType() != null &&
	 * DxfFileConstants.D.equalsIgnoreCase(mostRestrictiveOccupancyType.getType(
	 * ).getCode())) { return BigDecimal.valueOf(2); } else { return
	 * BigDecimal.valueOf(1.5); } }
	 */

	private BigDecimal validateTread(Plan plan, HashMap<String, String> errors, Block block,
			ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues,
			org.egov.common.entity.edcr.FireStair fireStair, Flight flight, List<BigDecimal> flightLengths,
			BigDecimal minTread, OccupancyTypeHelper mostRestrictiveOccupancyType, BigDecimal fireStairRequiredTread) {
		BigDecimal totalLength = flightLengths.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

		totalLength = Util.roundOffTwoDecimal(totalLength);

		if (flight.getNoOfRises() != null) {
			/*
			 * BigDecimal denominator =
			 * fireStair.getNoOfRises().subtract(BigDecimal.valueOf( flightLengths.size()))
			 * .subtract(BigDecimal.valueOf(fireStair.getLinesInFlightLayer(). size()));
			 */
			BigDecimal noOfFlights = BigDecimal.valueOf(flightLengths.size());

			if (flight.getNoOfRises().compareTo(noOfFlights) > 0) {
				BigDecimal denominator = flight.getNoOfRises().subtract(noOfFlights);

				minTread = totalLength.divide(denominator, DcrConstants.DECIMALDIGITS_MEASUREMENTS,
						DcrConstants.ROUNDMODE_MEASUREMENTS);

				boolean valid = false;

				if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {

					if (Util.roundOffTwoDecimal(minTread)
							.compareTo(Util.roundOffTwoDecimal(fireStairRequiredTread)) >= 0) {
						valid = true;
					}

					String value = typicalFloorValues.get(TYPICAL_FLOOR) != null
							? (String) typicalFloorValues.get(TYPICAL_FLOOR)
							: FLOOR_SPACED + floor.getNumber();
					if (valid) {
						setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
								String.format(TREAD_DESCRIPTION, fireStair.getNumber(), flight.getNumber()),
								fireStairRequiredTread.toString(), String.valueOf(minTread),
								Result.Accepted.getResultVal(), scrutinyDetail3);
					} else {
						setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
								String.format(TREAD_DESCRIPTION, fireStair.getNumber(), flight.getNumber()),
								fireStairRequiredTread.toString(), String.valueOf(minTread),
								Result.Not_Accepted.getResultVal(), scrutinyDetail3);
					}
				}
			} else {
				if (flight.getNoOfRises().compareTo(BigDecimal.ZERO) > 0) {
					String flightLayerName = String.format(DxfFileConstants.LAYER_FIRESTAIR_FLIGHT, block.getNumber(),
							floor.getNumber(), fireStair.getNumber(), flight.getNumber());
					errors.put(NO_OF_RISES_COUNT + flightLayerName,
							NO_OF_RISERS_GREATER_THAN_LENGTH_FLIGHT_DIMENSIONS
									+ flightLayerName);
					plan.addErrors(errors);
				}
			}
		}
		return minTread;
	}

	/*
	 * private BigDecimal getRequiredTread(OccupancyTypeHelper
	 * mostRestrictiveOccupancyType) { if (mostRestrictiveOccupancyType.getSubtype()
	 * != null &&
	 * DxfFileConstants.A_AF.equalsIgnoreCase(mostRestrictiveOccupancyType.
	 * getSubtype().getCode())) { return BigDecimal.valueOf(0.25); } else { return
	 * BigDecimal.valueOf(0.3); } }
	 */

	private void validateNoOfRises(Plan plan, HashMap<String, String> errors, Block block,
			ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues, Flight flight,
			org.egov.common.entity.edcr.FireStair fireStair, BigDecimal noOfRises,
			BigDecimal fireStairExpectedNoofRise) {
		boolean valid = false;

		if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
			if (Util.roundOffTwoDecimal(noOfRises).compareTo(Util.roundOffTwoDecimal(fireStairExpectedNoofRise)) <= 0) {
				valid = true;
			}

			String value = typicalFloorValues.get(TYPICAL_FLOOR) != null
					? (String) typicalFloorValues.get(TYPICAL_FLOOR)
					: FLOOR_SPACED + floor.getNumber();
			if (valid) {
				setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
						String.format(NO_OF_RISER_DESCRIPTION, fireStair.getNumber(), flight.getNumber()),
						fireStairExpectedNoofRise.toString(), String.valueOf(noOfRises), Result.Accepted.getResultVal(),
						scrutinyDetail3);
			} else {
				setReportOutputDetailsFloorStairWise(plan, RULE42_5_II, value,
						String.format(NO_OF_RISER_DESCRIPTION, fireStair.getNumber(), flight.getNumber()),
						fireStairExpectedNoofRise.toString(), String.valueOf(noOfRises),
						Result.Not_Accepted.getResultVal(), scrutinyDetail3);
			}
		}
	}

	/*
	 * private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc,
	 * String expected, String actual, String status, ScrutinyDetail scrutinyDetail)
	 * { Map<String, String> details = new HashMap<>(); details.put(RULE_NO,
	 * ruleNo); details.put(DESCRIPTION, ruleDesc); details.put(REQUIRED, expected);
	 * details.put(PROVIDED, actual); details.put(STATUS, status);
	 * scrutinyDetail.getDetail().add(details);
	 * pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail); }
	 */

	private void setReportOutputDetailsFloorStairWise(Plan pl, String ruleNo, String floor, String description,
			String expected, String actual, String status, ScrutinyDetail scrutinyDetail) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(ruleNo);
		detail.setDescription(description);
		detail.setFloorNo(floor);
		detail.setPermissible(expected);
		detail.setProvided(actual);
		detail.setStatus(status);

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	}

	private void setReportOutputDetailsBltUp(Plan pl, String ruleNo, String floor, String description, String actual,
			String status, ScrutinyDetail scrutinyDetail) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(ruleNo);
		detail.setDescription(description);
		detail.setFloorNo(floor);
		detail.setProvided(actual);
		detail.setStatus(status);

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	}

	/*
	 * private void validateDimensions(Plan plan, String blockNo, int floorNo,
	 * String stairNo, List<Measurement> flightPolyLines) { int count = 0; for
	 * (Measurement m : flightPolyLines) { if (m.getInvalidReason() != null &&
	 * m.getInvalidReason().length() > 0) { count++; } } if (count > 0) {
	 * plan.addError(String.format(DxfFileConstants. LAYER_FIRESTAIR_FLIGHT_FLOOR,
	 * blockNo, floorNo, stairNo), count +
	 * " number of flight polyline not having only 4 points in layer " +
	 * String.format(DxfFileConstants.LAYER_FIRESTAIR_FLIGHT_FLOOR, blockNo,
	 * floorNo, stairNo)); } }
	 */

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