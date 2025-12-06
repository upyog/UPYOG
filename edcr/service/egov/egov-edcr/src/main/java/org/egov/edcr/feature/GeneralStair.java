package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonFeatureConstants.FLOOR;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class GeneralStair extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(GeneralStair.class);

	@Autowired
	MDMSCacheManager cache;
	
	@Override
	public Plan validate(Plan plan) {
		return plan;
	}

	@Override
	public Plan process(Plan plan) {
		HashMap<String, String> errors = new HashMap<>();
		for (Block block : plan.getBlocks()) {
			processBlock(plan, block, errors);
		}
		return plan;
	}

	/**
	 * Processes general stair information for a specific block in the plan.
	 * It validates stair attributes like width, tread width, number of risers,
	 * landing width, riser height, and accumulates errors if any.
	 *
	 * @param plan   The overall building plan.
	 * @param block  The block for which general stair information needs to be validated.
	 * @param errors The map to collect validation errors.
	 */
	private void processBlock(Plan plan, Block block, HashMap<String, String> errors) {
		int generalStairCount = 0;
		BigDecimal flrHt = BigDecimal.ZERO;
		BigDecimal totalLandingWidth = BigDecimal.ZERO;
		BigDecimal totalRisers = BigDecimal.ZERO;
		BigDecimal totalSteps = BigDecimal.ZERO;

		if (block.getBuilding() == null) return;

		ScrutinyDetail scrutinyDetail2 = createScrutinyDetail(block, GENERAL_STAIR_WIDTH);
		ScrutinyDetail scrutinyDetail3 = createScrutinyDetail(block, GENERAL_STAIR_TREAD_WIDTH);
		ScrutinyDetail scrutinyDetailRise = createScrutinyDetail(block, GENERAL_STAIR_NUMBER_OF_RISERS);
		ScrutinyDetail scrutinyDetailLanding = createScrutinyDetail(block, GENERAL_STAIR_MID_LANDING);
		ScrutinyDetail scrutinyDetail4 = createScrutinyDetail(block, GENERAL_STAIR_RISER_HEIGHT);

		OccupancyTypeHelper mostRestrictiveOccupancyType = block.getBuilding().getMostRestrictiveFarHelper();
		List<Floor> floors = block.getBuilding().getFloors();
		List<String> stairAbsent = new ArrayList<>();

		for (Floor floor : floors) {
			if (!floor.getTerrace()) {
				Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor, false);
				List<org.egov.common.entity.edcr.GeneralStair> generalStairs = floor.getGeneralStairs();

				generalStairCount += generalStairs.size();
				if (!generalStairs.isEmpty()) {
					for (org.egov.common.entity.edcr.GeneralStair generalStair : generalStairs) {
						flrHt = generalStair.getFloorHeight();
						totalRisers = updateTotalRisers(generalStair, totalRisers);
						totalLandingWidth = updateLandingWidths(generalStair, totalLandingWidth);
						totalSteps = totalRisers.add(totalLandingWidth);

						validateFlight(plan, errors, block, scrutinyDetail2, scrutinyDetail3, scrutinyDetailRise,
								mostRestrictiveOccupancyType, floor, typicalFloorValues, generalStair, generalStairCount);

						List<StairLanding> landings = generalStair.getLandings();
						if (!landings.isEmpty()) {
							validateLanding(plan, block, scrutinyDetailLanding, mostRestrictiveOccupancyType, floor,
									typicalFloorValues, generalStair, landings, errors);
						} else if (floor.getNumber() != generalStairCount - 1) {
							errors.put(GENERAL_STAIR_LANDING_NOT_DEFINED + block.getNumber() + FLOOR_SPACED + floor.getNumber() + STAIR_PREFIX + generalStair.getNumber(),
									GENERAL_STAIR_LANDING_NOT_DEFINED + block.getNumber() + FLOOR_SPACED + floor.getNumber() + STAIR_PREFIX + generalStair.getNumber());
							plan.addErrors(errors);
						}
					}
				} else {
					if (floor.getNumber() != generalStairCount) {
						stairAbsent.add(BLOCK_PREFIX + block.getNumber() + FLOOR_SPACED + floor.getNumber());
					}
				}
			}
		}

		validateRiserHeight(plan, block, flrHt, totalSteps, scrutinyDetail4);
		handleStairErrors(plan, block, stairAbsent, generalStairCount, errors);
	}

	/**
	 * Creates a ScrutinyDetail object with appropriate column headings and a unique key for a given block and title.
	 *
	 * @param block The block to which this scrutiny detail belongs.
	 * @param title The title for the scrutiny detail.
	 * @return A configured ScrutinyDetail instance.
	 */
	private ScrutinyDetail createScrutinyDetail(Block block, String title) {
		ScrutinyDetail detail = new ScrutinyDetail();
		detail.addColumnHeading(1, RULE_NO);
		detail.addColumnHeading(2, FLOOR);
		detail.addColumnHeading(3, DESCRIPTION);
		detail.addColumnHeading(4, PERMISSIBLE);
		detail.addColumnHeading(5, PROVIDED);
		detail.addColumnHeading(6, STATUS);
		detail.setKey(BLOCK + block.getNumber() + "_" + title);
		return detail;
	}

	/**
	 * Updates and returns the total number of risers for the given general stair.
	 *
	 * @param stair        The GeneralStair entity to evaluate.
	 * @param totalRisers  The running total of risers.
	 * @return Updated total number of risers including the given stair's flights.
	 */
	private BigDecimal updateTotalRisers(org.egov.common.entity.edcr.GeneralStair stair, BigDecimal totalRisers) {
		for (Flight flight : stair.getFlights()) {
			totalRisers = totalRisers.add(flight.getNoOfRises());
		}
		return totalRisers;
	}

	/**
	 * Calculates and returns the total landing width for the given general stair.
	 *
	 * @param stair              The GeneralStair entity to evaluate.
	 * @param totalLandingWidth  The running total of landing widths.
	 * @return Updated total landing width.
	 */
	private BigDecimal updateLandingWidths(org.egov.common.entity.edcr.GeneralStair stair, BigDecimal totalLandingWidth) {
		for (StairLanding landing : stair.getLandings()) {
			List<BigDecimal> widths = landing.getWidths();
			if (!widths.isEmpty()) {
				BigDecimal landingWidth = widths.stream().reduce(BigDecimal::min).get();
				totalLandingWidth = totalLandingWidth.add(landingWidth);
			}
		}
		return totalLandingWidth;
	}

	/**
	 * Validates the riser height for a stair based on floor height and total steps.
	 * Adds the result to the scrutiny report.
	 *
	 * @param plan             The building plan.
	 * @param block            The block being evaluated.
	 * @param flrHt            The floor height.
	 * @param totalSteps       The total number of steps including landings.
	 * @param scrutinyDetail4  ScrutinyDetail object for riser height validation.
	 */
	private void validateRiserHeight(Plan plan, Block block, BigDecimal flrHt, BigDecimal totalSteps, ScrutinyDetail scrutinyDetail4) {
	    LOG.info("Validating riser height for Block: {}", block.getName());

	    BigDecimal value = getPermissibleRiserHeight(plan);
	    LOG.info("Permissible riser height: {}", value);

	    if (flrHt != null && totalSteps != null && totalSteps.compareTo(BigDecimal.ZERO) > 0) {
	        LOG.info("Floor height: {}, Total steps: {}", flrHt, totalSteps);

	        BigDecimal riserHeight = flrHt.divide(totalSteps, 2, RoundingMode.HALF_UP);
	        LOG.info("Calculated riser height: {}", riserHeight);

	        for (Floor floor : block.getBuilding().getFloors()) {
	            String floorValue = FLOOR_SPACED + floor.getNumber();
	            LOG.info("Processing Floor: {}", floorValue);

	            String result = (riserHeight.compareTo(value) <= 0)
	                    ? Result.Accepted.getResultVal()
	                    : Result.Not_Accepted.getResultVal();

	            LOG.info("Result for Floor {}: {} (Permissible: {}, Provided: {})", 
	                    floorValue, result, value, riserHeight);

	            setReportOutputDetailsFloorStairWise(plan, RULE_4_4_4, floorValue, RISER_HEIGHT_DESC, 
	                    EMPTY_STRING + value, EMPTY_STRING + riserHeight, result, scrutinyDetail4);
	        }
	    } else {
	        LOG.warn("Floor height or total steps is null/invalid for Block: {}", block.getName());
	    }
	} 

	/**
	 * Retrieves the permissible riser height from the rule cache.
	 *
	 * @param plan The building plan.
	 * @return The permissible riser height value.
	 */
	private BigDecimal getPermissibleRiserHeight(Plan plan) {
		List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.RISER_HEIGHT.getValue(), false);
        Optional<RiserHeightRequirement> matchedRule = rules.stream()
            .filter(RiserHeightRequirement.class::isInstance)
            .map(RiserHeightRequirement.class::cast)
            .findFirst();
		return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
	}

	/**
	 * Handles stair-related errors and appends them to the plan.
	 *
	 * @param plan               The building plan.
	 * @param block              The block being evaluated.
	 * @param stairAbsent        List of floors missing stair definitions.
	 * @param generalStairCount  The total number of general stairs found.
	 * @param errors             Map to collect error messages.
	 */
	private void handleStairErrors(Plan plan, Block block, List<String> stairAbsent, int generalStairCount, HashMap<String, String> errors) {
		for (String error : stairAbsent) {
			errors.put(GENERAL_STAIR + error, GENERAL_STAIR_NOT_DEFINED + error);
			plan.addErrors(errors);
		}
		if (generalStairCount == 0) {
			errors.put(GENERAL_STAIR_MANDATORY + "blk " + block.getNumber(),
					GENERAL_STAIR_MANDATORY + block.getNumber() + GENERAL_STAIR_MANDATORY_SUFFIX);
			plan.addErrors(errors);
		}
	}

	/**
	 * Validates the width of each landing for the general stair and adds the result to scrutiny report.
	 *
	 * @param plan                      The building plan.
	 * @param block                     The block containing the stair.
	 * @param scrutinyDetailLanding     ScrutinyDetail for landing width.
	 * @param mostRestrictiveOccupancyType Most restrictive occupancy type of the block.
	 * @param floor                     The floor under validation.
	 * @param typicalFloorValues        Map of values used for typical floor validation.
	 * @param generalStair              The stair to be validated.
	 * @param landings                  List of landings in the stair.
	 * @param errors                    Map to collect validation errors.
	 */

	private void validateLanding(Plan plan, Block block, ScrutinyDetail scrutinyDetailLanding,
			OccupancyTypeHelper mostRestrictiveOccupancyType, Floor floor, Map<String, Object> typicalFloorValues,
			org.egov.common.entity.edcr.GeneralStair generalStair, List<StairLanding> landings,
			HashMap<String, String> errors) {
		for (StairLanding landing : landings) {
			List<BigDecimal> widths = landing.getWidths();
			if (!widths.isEmpty()) {
				BigDecimal landingWidth = widths.stream().reduce(BigDecimal::min).get();
				BigDecimal minWidth = BigDecimal.ZERO;
				boolean valid = false;
				// Map<String, List<Map<String, Object>>> edcrRuleList =
				// plan.getEdcrRulesFeatures();

				if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
					minWidth = Util.roundOffTwoDecimal(landingWidth);
					BigDecimal minimumWidth = getRequiredWidth(plan, block, mostRestrictiveOccupancyType);

					if (minWidth.compareTo(minimumWidth) >= 0) {
						valid = true;
					}
					String value = typicalFloorValues.get(TYPICAL_FLOOR) != null
							? (String) typicalFloorValues.get(TYPICAL_FLOOR)
							: FLOOR_SPACED + floor.getNumber();

					if (valid) {
						setReportOutputDetailsFloorStairWise(plan, RULE_4_4_4, value,
								String.format(WIDTH_LANDING_DESCRIPTION, generalStair.getNumber(), landing.getNumber()),
								minimumWidth.toString(), String.valueOf(minWidth), Result.Accepted.getResultVal(),
								scrutinyDetailLanding);
					} else {
						setReportOutputDetailsFloorStairWise(plan, RULE_4_4_4, value,
								String.format(WIDTH_LANDING_DESCRIPTION, generalStair.getNumber(), landing.getNumber()),
								minimumWidth.toString(), String.valueOf(minWidth), Result.Not_Accepted.getResultVal(),
								scrutinyDetailLanding);
					}
				}
				System.out.println("minn" + minWidth);
			} else {
				errors.put(
						GENERAL_STAIR_LANDING_WIDTH_NOT_DEFINED + block.getNumber() + FLOOR_SPACED
								+ floor.getNumber() + STAIR_PREFIX + generalStair.getNumber(),
						GENERAL_STAIR_LANDING_WIDTH_NOT_DEFINED + block.getNumber() + FLOOR_SPACED
								+ floor.getNumber() + STAIR_PREFIX + generalStair.getNumber());
				plan.addErrors(errors);

			}

		}

	}

	/**
	 * Validates the flights associated with a general stair in a specific floor and block of the plan.
	 * <p>
	 * It ensures each flight has proper dimensions, including width, tread, and number of rises,
	 * as per the rules defined for the most restrictive occupancy type.
	 * <p>
	 * If flights are missing, an appropriate error is added to the plan.
	 *
	 * @param plan                        the building plan being validated
	 * @param errors                      map containing validation errors
	 * @param block                       the block in which the flight is located
	 * @param scrutinyDetail2            scrutiny details for width validation
	 * @param scrutinyDetail3            scrutiny details for tread validation
	 * @param scrutinyDetailRise         scrutiny details for rise validation
	 * @param mostRestrictiveOccupancyType the most restrictive occupancy type for rule evaluation
	 * @param floor                       the floor where the general stair is present
	 * @param typicalFloorValues         map containing typical floor information
	 * @param generalStair               the general stair object containing flights
	 * @param generalStairCount          the index or count of general stairs being validated
	 */
	
	private void validateFlight(Plan plan, HashMap<String, String> errors, Block block, ScrutinyDetail scrutinyDetail2,
	        ScrutinyDetail scrutinyDetail3, ScrutinyDetail scrutinyDetailRise, OccupancyTypeHelper mostRestrictiveOccupancyType,
	        Floor floor, Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
	        int generalStairCount) {

	    if (!generalStair.getFlights().isEmpty()) {
	        for (Flight flight : generalStair.getFlights()) {
	            validateSingleFlight(plan, errors, block, scrutinyDetail2, scrutinyDetail3, scrutinyDetailRise,
	                    mostRestrictiveOccupancyType, floor, typicalFloorValues, generalStair, flight);
	        }
	    } else {
	        handleMissingFlights(plan, errors, block, floor);
	    }
	}
	
	/**
	 * Validates a single flight object by checking its width, tread, and number of rises.
	 *
	 * @param plan                        the building plan
	 * @param errors                      map of error messages
	 * @param block                       the block in the plan
	 * @param scrutinyDetail2            scrutiny details for flight width
	 * @param scrutinyDetail3            scrutiny details for flight tread
	 * @param scrutinyDetailRise         scrutiny details for flight rises
	 * @param mostRestrictiveOccupancyType the most restrictive occupancy type
	 * @param floor                       the floor containing the flight
	 * @param typicalFloorValues         map containing values related to typical floor repetition
	 * @param generalStair               the stair object to which this flight belongs
	 * @param flight                      the flight object to be validated
	 */

	private void validateSingleFlight(Plan plan, HashMap<String, String> errors, Block block,
	        ScrutinyDetail scrutinyDetail2, ScrutinyDetail scrutinyDetail3, ScrutinyDetail scrutinyDetailRise,
	        OccupancyTypeHelper mostRestrictiveOccupancyType, Floor floor, Map<String, Object> typicalFloorValues,
	        org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight) {

	    List<Measurement> flightPolyLines = flight.getFlights();
	    List<BigDecimal> flightLengths = flight.getLengthOfFlights();
	    List<BigDecimal> flightWidths = flight.getWidthOfFlights();
	    BigDecimal noOfRises = flight.getNoOfRises();
	    Boolean flightPolyLineClosed = flight.getFlightClosed();

	    BigDecimal minTread = BigDecimal.ZERO;
	    BigDecimal minFlightWidth = BigDecimal.ZERO;

	    String flightLayerName = String.format(DxfFileConstants.LAYER_STAIR_FLIGHT, block.getNumber(),
	            floor.getNumber(), generalStair.getNumber(), flight.getNumber());

	    if (flightPolyLines != null && !flightPolyLines.isEmpty()) {
	        if (flightPolyLineClosed) {
	            validateFlightWidth(plan, errors, scrutinyDetail2, floor, block, typicalFloorValues,
	                    generalStair, flight, flightWidths, mostRestrictiveOccupancyType, flightLayerName);

	            validateFlightTread(plan, errors, block, scrutinyDetail3, floor, typicalFloorValues,
	                    generalStair, flight, flightLengths, mostRestrictiveOccupancyType, flightLayerName);

	            validateFlightRises(plan, errors, block, scrutinyDetailRise, floor, typicalFloorValues,
	                    generalStair, flight, noOfRises, flightLayerName);
	        }
	    } else {
	        errors.put(FLIGHT_POLYLINE + flightLayerName,
	                FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION + flightLayerName);
	        plan.addErrors(errors);
	    }
	}
	
	/**
	 * Validates the width of the flight against required rules.
	 *
	 * @param plan             the plan object
	 * @param errors           error map
	 * @param scrutinyDetail2  scrutiny details for reporting
	 * @param floor            current floor
	 * @param block            current block
	 * @param typicalFloorValues map with typical floor repetition details
	 * @param generalStair     the general stair object
	 * @param flight           the flight being validated
	 * @param flightWidths     list of measured flight widths
	 * @param mostRestrictiveOccupancyType the most restrictive occupancy type
	 * @param flightLayerName  layer name of the flight
	 */

	private void validateFlightWidth(Plan plan, HashMap<String, String> errors, ScrutinyDetail scrutinyDetail2,
	        Floor floor, Block block, Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
	        Flight flight, List<BigDecimal> flightWidths, OccupancyTypeHelper mostRestrictiveOccupancyType, String flightLayerName) {

	    if (flightWidths != null && !flightWidths.isEmpty()) {
	        validateWidth(plan, scrutinyDetail2, floor, block, typicalFloorValues,
	                generalStair, flight, flightWidths, BigDecimal.ZERO, mostRestrictiveOccupancyType);
	    } else {
	        errors.put(FLIGHT_POLYLINE_WIDTH + flightLayerName,
	                FLIGHT_WIDTH_DEFINED_DESCRIPTION + flightLayerName);
	        plan.addErrors(errors);
	    }
	}
	
	/**
	 * Validates the tread (length of step) of the flight.
	 *
	 * @param plan             the plan object
	 * @param errors           map containing error messages
	 * @param block            the block in which validation is occurring
	 * @param scrutinyDetail3  scrutiny details for treads
	 * @param floor            current floor
	 * @param typicalFloorValues map with typical floor repetition info
	 * @param generalStair     stair object that includes the flight
	 * @param flight           flight being validated
	 * @param flightLengths    list of tread lengths
	 * @param mostRestrictiveOccupancyType the occupancy type used for rule checks
	 * @param flightLayerName  layer name of the flight
	 */

	private void validateFlightTread(Plan plan, HashMap<String, String> errors, Block block, ScrutinyDetail scrutinyDetail3,
	        Floor floor, Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
	        Flight flight, List<BigDecimal> flightLengths, OccupancyTypeHelper mostRestrictiveOccupancyType, String flightLayerName) {

	    if (flightLengths != null && !flightLengths.isEmpty()) {
	        try {
	            validateTread(plan, errors, block, scrutinyDetail3, floor, typicalFloorValues,
	                    generalStair, flight, flightLengths, BigDecimal.ZERO, mostRestrictiveOccupancyType);
	        } catch (ArithmeticException e) {
	            LOG.info("Denominator is zero");
	        }
	    } else {
	        errors.put(FLIGHT_POLYLINE_LENGTH + flightLayerName,
	                FLIGHT_LENGTH_DEFINED_DESCRIPTION + flightLayerName);
	        plan.addErrors(errors);
	    }
	}
	
	/**
	 * Validates the number of rises in the flight.
	 *
	 * @param plan             the plan object
	 * @param errors           error map
	 * @param block            block in which validation occurs
	 * @param scrutinyDetailRise scrutiny detail for number of rises
	 * @param floor            current floor
	 * @param typicalFloorValues map with floor repetition info
	 * @param generalStair     stair object containing the flight
	 * @param flight           the flight to be validated
	 * @param noOfRises        number of rises in the flight
	 * @param flightLayerName  name of the flight layer
	 */

	private void validateFlightRises(Plan plan, HashMap<String, String> errors, Block block, ScrutinyDetail scrutinyDetailRise,
	        Floor floor, Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
	        Flight flight, BigDecimal noOfRises, String flightLayerName) {

	    if (noOfRises != null && noOfRises.compareTo(BigDecimal.ZERO) > 0) {
	        try {
	            validateNoOfRises(plan, errors, block, scrutinyDetailRise, floor, typicalFloorValues,
	                    generalStair, flight, noOfRises);
	        } catch (ArithmeticException e) {
	            LOG.info("Denominator is zero");
	        }
	    } else {
	        errors.put(NO_OF_RISE + flightLayerName,
	                edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
	                        new String[]{NO_OF_RISERS + flightLayerName},
	                        LocaleContextHolder.getLocale()));
	        plan.addErrors(errors);
	    }
	}
	
	/**
	 * Adds error to the plan if no flight is defined for a given stair in a floor and block.
	 *
	 * @param plan   the plan object
	 * @param errors map of error messages
	 * @param block  current block
	 * @param floor  current floor
	 */

	private void handleMissingFlights(Plan plan, HashMap<String, String> errors, Block block, Floor floor) {
	    String error = String.format(FLIGHT_NOT_DEFINED_DESCRIPTION, block.getNumber(), floor.getNumber());
	    errors.put(error, error);
	    plan.addErrors(errors);
	}


	/**
	 * Compares measured flight width with required width and adds result to the scrutiny report.
	 *
	 * @param plan                        the plan object
	 * @param scrutinyDetail2            scrutiny detail for width
	 * @param floor                       current floor
	 * @param block                       current block
	 * @param typicalFloorValues         typical floor repetition info
	 * @param generalStair               the stair object
	 * @param flight                     the flight object
	 * @param flightWidths               measured widths of the flight
	 * @param minFlightWidth             minimum measured width (may be recalculated)
	 * @param mostRestrictiveOccupancyType the occupancy type for determining rule applicability
	 * @return the minimum flight width used in validation
	 */

	private BigDecimal validateWidth(Plan plan, ScrutinyDetail scrutinyDetail2, Floor floor, Block block,
			Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
			Flight flight, List<BigDecimal> flightWidths, BigDecimal minFlightWidth,
			OccupancyTypeHelper mostRestrictiveOccupancyType) {
		BigDecimal flightPolyLine = flightWidths.stream().reduce(BigDecimal::min).get();
		boolean valid = false;

		if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
			minFlightWidth = Util.roundOffTwoDecimal(flightPolyLine);
			BigDecimal minimumWidth = getRequiredWidth(plan, block, mostRestrictiveOccupancyType);

			if (minFlightWidth.compareTo(minimumWidth) >= 0) {
				valid = true;
			}
			String value = typicalFloorValues.get(TYPICAL_FLOOR) != null
					? (String) typicalFloorValues.get(TYPICAL_FLOOR)
					: FLOOR_SPACED + floor.getNumber();

			if (valid) {
				setReportOutputDetailsFloorStairWise(plan, RULE_4_4_4, value,
						String.format(WIDTH_DESCRIPTION_GEN_STAIR, generalStair.getNumber(), flight.getNumber()),
						minimumWidth.toString(), String.valueOf(minFlightWidth), Result.Accepted.getResultVal(),
						scrutinyDetail2);
			} else {
				setReportOutputDetailsFloorStairWise(plan, RULE_4_4_4, value,
						String.format(WIDTH_DESCRIPTION_GEN_STAIR, generalStair.getNumber(), flight.getNumber()),
						minimumWidth.toString(), String.valueOf(minFlightWidth), Result.Not_Accepted.getResultVal(),
						scrutinyDetail2);
			}
		}
		return minFlightWidth;
	}


	/**
	 * Fetches the required minimum flight width from cached feature rules based on plan and occupancy.
	 *
	 * @param pl                         the plan object
	 * @param block                      current block
	 * @param mostRestrictiveOccupancyType the most restrictive occupancy type
	 * @return permissible flight width as per rules or BigDecimal.ZERO if not found
	 */

	private BigDecimal getRequiredWidth(Plan pl, Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {


		BigDecimal value = BigDecimal.ZERO;
		
		// Fetch all rules for the given plan from the cache.
		// Then, filter to find the first rule where the condition falls within the
		// defined range.
		// If a matching rule is found, proceed with its processing.

		 List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.REQUIRED_WIDTH.getValue(), false);
	        Optional<RequiredWidthRequirement> matchedRule = rules.stream()
	            .filter(RequiredWidthRequirement.class::isInstance)
	            .map(RequiredWidthRequirement.class::cast)
	            .findFirst();

		if (matchedRule.isPresent()) {
			RequiredWidthRequirement rule = matchedRule.get();
			value = rule.getPermissible();
		} else {
			value = BigDecimal.ZERO;
		}
		return value;
	}

	/**
	 * Returns the required landing width based on occupancy type.
	 * Residential (code "A") requires 0.76m; others require 1.5m.
	 *
	 * @param block                      the block object (not used here but kept for consistency)
	 * @param mostRestrictiveOccupancyType occupancy type to determine landing width
	 * @return required landing width in meters
	 */

	private BigDecimal getRequiredLandingWidth(Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {

		if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
			return BigDecimal.valueOf(0.76);
		} else {
			return BigDecimal.valueOf(1.5);
		}
	}

	/**
	 * Validates the tread of a stair flight by calculating the minimum tread based on 
	 * the total length of all flight segments and the number of risers. If the number 
	 * of risers is greater than the number of flights, the tread is calculated. 
	 * Additionally, it validates the calculated tread against the required tread 
	 * value based on occupancy type.
	 *
	 * @param plan                  The building plan object containing all plan-level details.
	 * @param errors                Map to collect validation errors.
	 * @param block                 The block to which the stair flight belongs.
	 * @param scrutinyDetail3       Object to capture scrutiny details for reporting.
	 * @param floor                 The floor on which the flight is located.
	 * @param typicalFloorValues    Map containing typical floor configuration details.
	 * @param generalStair          Staircase entity containing stair number and other info.
	 * @param flight                The flight being validated.
	 * @param flightLengths         List of lengths of all segments of the flight.
	 * @param minTread              The minimum tread value, to be calculated or reused.
	 * @param mostRestrictiveOccupancyType Occupancy type used to determine required tread rule.
	 *
	 * @return The calculated minimum tread value for the flight.
	 */
	private BigDecimal validateTread(Plan plan, HashMap<String, String> errors, Block block,
	        ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues,
	        org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight, List<BigDecimal> flightLengths,
	        BigDecimal minTread, OccupancyTypeHelper mostRestrictiveOccupancyType) {

	    BigDecimal totalLength = calculateTotalFlightLength(flightLengths);
	    BigDecimal requiredTread = getRequiredTread(plan, mostRestrictiveOccupancyType);

	    if (flight.getNoOfRises() != null) {
	        BigDecimal noOfFlights = BigDecimal.valueOf(flightLengths.size());

	        if (flight.getNoOfRises().compareTo(noOfFlights) > 0) {
	            BigDecimal denominator = flight.getNoOfRises().subtract(noOfFlights);
	            minTread = totalLength.divide(denominator, DcrConstants.DECIMALDIGITS_MEASUREMENTS,
	                    DcrConstants.ROUNDMODE_MEASUREMENTS);

	            if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
	                validateTreadAgainstRequired(plan, scrutinyDetail3, floor, typicalFloorValues,
	                        generalStair, flight, requiredTread, minTread);
	            }

	        } else if (flight.getNoOfRises().compareTo(BigDecimal.ZERO) > 0) {
	            addNoOfRisesError(plan, errors, block, floor, generalStair, flight);
	        }
	    }

	    return minTread;
	}
	
	/**
	 * Calculates the total length of a stair flight by summing up all its segment lengths
	 * and rounding the result to two decimal places.
	 *
	 * @param flightLengths List of individual flight segment lengths.
	 * @return The total flight length, rounded to two decimal places.
	 */
	private BigDecimal calculateTotalFlightLength(List<BigDecimal> flightLengths) {
	    BigDecimal totalLength = flightLengths.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	    return Util.roundOffTwoDecimal(totalLength);
	}
	
	/**
	 * Compares the calculated minimum tread value with the required tread value and
	 * records the result in the scrutiny report. Only runs validation for non-typical floors.
	 *
	 * @param plan               The building plan being processed.
	 * @param scrutinyDetail3    Scrutiny detail for recording results.
	 * @param floor              The floor on which the tread is being validated.
	 * @param typicalFloorValues Map containing information about typical floor configuration.
	 * @param generalStair       Stair entity associated with the tread.
	 * @param flight             The flight being validated.
	 * @param requiredTread      The tread value as per applicable rules.
	 * @param minTread           The calculated minimum tread value.
	 */
	private void validateTreadAgainstRequired(Plan plan, ScrutinyDetail scrutinyDetail3, Floor floor,
	        Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
	        Flight flight, BigDecimal requiredTread, BigDecimal minTread) {

	    boolean isValid = Util.roundOffTwoDecimal(minTread)
	            .compareTo(Util.roundOffTwoDecimal(requiredTread)) >= 0;

	    String value = typicalFloorValues.get(TYPICAL_FLOOR) != null
	            ? (String) typicalFloorValues.get(TYPICAL_FLOOR)
	            : FLOOR_SPACED + floor.getNumber();

	    String description = String.format(TREAD_DESCRIPTION_GEN_STAIR, generalStair.getNumber(), flight.getNumber());

	    setReportOutputDetailsFloorStairWise(plan, RULETREAD, value, description,
	            requiredTread.toString(), minTread.toString(),
	            isValid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal(), scrutinyDetail3);
	}
	
	/**
	 * Adds an error to the plan if the number of risers in the flight is less than or 
	 * equal to the number of defined flight segments.
	 *
	 * @param plan         The plan where the error is to be recorded.
	 * @param errors       Map to collect errors.
	 * @param block        The block in which the error occurred.
	 * @param floor        The floor containing the problematic flight.
	 * @param generalStair The stair associated with the flight.
	 * @param flight       The flight which has incorrect number of risers.
	 */
	private void addNoOfRisesError(Plan plan, HashMap<String, String> errors, Block block, Floor floor,
	        org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight) {

	    String flightLayerName = String.format(DxfFileConstants.LAYER_STAIR_FLIGHT, block.getNumber(),
	            floor.getNumber(), generalStair.getNumber(), flight.getNumber());

	    errors.put(NO_OF_RISES_COUNT + flightLayerName,
	            NO_OF_RISES_COUNT_ERROR + flightLayerName);
	    plan.addErrors(errors);
	}


	/**
	 * Retrieves the required tread value from the feature rules cache based on the 
	 * most restrictive occupancy type.
	 *
	 * @param pl                           The plan from which rules are derived.
	 * @param mostRestrictiveOccupancyType The most restrictive occupancy type used to find applicable rules.
	 * @return The permissible tread value as per rule, or zero if no rule matches.
	 */
	private BigDecimal getRequiredTread(Plan pl, OccupancyTypeHelper mostRestrictiveOccupancyType) {

		
		BigDecimal value = BigDecimal.ZERO;

		// Fetch all rules for the given plan from the cache.
		// Then, filter to find the first rule where the condition falls within the
		// defined range.
		// If a matching rule is found, proceed with its processing.

		 List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.REQUIRED_TREAD.getValue(), false);
	        Optional<RequiredTreadRequirement> matchedRule = rules.stream()
	            .filter(RequiredTreadRequirement.class::isInstance)
	            .map(RequiredTreadRequirement.class::cast)
	            .findFirst();

		if (matchedRule.isPresent()) {
			RequiredTreadRequirement rule = matchedRule.get();
			value = rule.getPermissible();
		} else {
			value = BigDecimal.ZERO;
		}
		return value;
	}

	/**
	 * Validates the number of risers in a flight against the permissible value 
	 * from the rules. Adds the result to the scrutiny report for non-typical floors.
	 *
	 * @param plan               The plan being validated.
	 * @param errors             Map to collect any validation errors.
	 * @param block              The block containing the flight.
	 * @param scrutinyDetail3    Object for collecting scrutiny output.
	 * @param floor              The floor containing the flight.
	 * @param typicalFloorValues Map with details of typical floors.
	 * @param generalStair       Stair entity containing stair number info.
	 * @param flight             The flight for which riser count is validated.
	 * @param noOfRises          The actual number of risers in the flight.
	 */
	private void validateNoOfRises(Plan plan, HashMap<String, String> errors, Block block,
			ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues,
			org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight, BigDecimal noOfRises) {
		boolean valid = false;

		if (!(Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR)) {
			
		
			BigDecimal noOfRisersValue = BigDecimal.ZERO;

			// Fetch all rules for the given plan from the cache.
			// Then, filter to find the first rule where the condition falls within the
			// defined range.
			// If a matching rule is found, proceed with its processing.

			 List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.NO_OF_RISER.getValue(), false);
		        Optional<NoOfRiserRequirement> matchedRule = rules.stream()
		            .filter(NoOfRiserRequirement.class::isInstance)
		            .map(NoOfRiserRequirement.class::cast)
		            .findFirst();

			if (matchedRule.isPresent()) {
				NoOfRiserRequirement rule = matchedRule.get();
				noOfRisersValue = rule.getPermissible();
			} else {
				noOfRisersValue = BigDecimal.ZERO;
			}
			if (Util.roundOffTwoDecimal(noOfRises).compareTo(Util.roundOffTwoDecimal(noOfRisersValue)) <= 0) {
				valid = true;
			}
			valid = true;
			String value = typicalFloorValues.get(TYPICAL_FLOOR) != null
					? (String) typicalFloorValues.get(TYPICAL_FLOOR)
					: FLOOR_SPACED + floor.getNumber();
			if (valid) {
				setReportOutputDetailsFloorStairWise(plan, RULERISER, value,
						String.format(NO_OF_RISER_DESCRIPTION_GENERAL_STAIR, generalStair.getNumber(), flight.getNumber()),
						noOfRisersValue.toString(), String.valueOf(noOfRises), Result.Accepted.getResultVal(),
						scrutinyDetail3);
			} else {
				setReportOutputDetailsFloorStairWise(plan, RULERISER, value,
						String.format(NO_OF_RISER_DESCRIPTION_GENERAL_STAIR, generalStair.getNumber(), flight.getNumber()),
						 noOfRisersValue.toString(), String.valueOf(noOfRises), Result.Not_Accepted.getResultVal(),
						scrutinyDetail3);
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

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}