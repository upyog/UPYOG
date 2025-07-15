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
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Flight;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.StairLanding;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GeneralStair extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(GeneralStair.class);
	private static final String FLOOR = "Floor";
	private static final String RULE = "4.4.4";
	private static final String RULERISER = "5.15.4.1";
	private static final String RULETREAD = "5.15.3";
	private static final String EXPECTED_NO_OF_RISER = "12";
	private static final String NO_OF_RISER_DESCRIPTION = "Maximum no of risers required per flight for general stair %s flight %s";
	private static final String WIDTH_DESCRIPTION = "Minimum width for general stair %s flight %s";
	private static final String TREAD_DESCRIPTION = "Minimum tread for general stair %s flight %s";
	private static final String NO_OF_RISERS = "Number of risers ";
	private static final String FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION = "Flight polyline is not defined in layer ";
	private static final String FLIGHT_LENGTH_DEFINED_DESCRIPTION = "Flight polyline length is not defined in layer ";
	private static final String FLIGHT_WIDTH_DEFINED_DESCRIPTION = "Flight polyline width is not defined in layer ";
	private static final String WIDTH_LANDING_DESCRIPTION = "Minimum width for general stair %s mid landing %s";
	private static final String FLIGHT_NOT_DEFINED_DESCRIPTION = "General stair flight is not defined in block %s floor %s";

	@Autowired
	CacheManagerMdms cache;
	
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

	private void processBlock(Plan plan, Block block, HashMap<String, String> errors) {
		int generalStairCount = 0;
		BigDecimal flrHt = BigDecimal.ZERO;
		BigDecimal totalLandingWidth = BigDecimal.ZERO;
		BigDecimal totalRisers = BigDecimal.ZERO;
		BigDecimal totalSteps = BigDecimal.ZERO;

		if (block.getBuilding() == null) return;

		ScrutinyDetail scrutinyDetail2 = createScrutinyDetail(block, "General Stair - Width");
		ScrutinyDetail scrutinyDetail3 = createScrutinyDetail(block, "General Stair - Tread width");
		ScrutinyDetail scrutinyDetailRise = createScrutinyDetail(block, "General Stair - Number of risers");
		ScrutinyDetail scrutinyDetailLanding = createScrutinyDetail(block, "General Stair - Mid landing");
		ScrutinyDetail scrutinyDetail4 = createScrutinyDetail(block, "General Stair - Riser Height");

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
							errors.put("General Stair landing not defined in block " + block.getNumber() + " floor " + floor.getNumber() + " stair " + generalStair.getNumber(),
									"General Stair landing not defined in block " + block.getNumber() + " floor " + floor.getNumber() + " stair " + generalStair.getNumber());
							plan.addErrors(errors);
						}
					}
				} else {
					if (floor.getNumber() != generalStairCount) {
						stairAbsent.add("Block " + block.getNumber() + " floor " + floor.getNumber());
					}
				}
			}
		}

		validateRiserHeight(plan, block, flrHt, totalSteps, scrutinyDetail4);
		handleStairErrors(plan, block, stairAbsent, generalStairCount, errors);
	}

	private ScrutinyDetail createScrutinyDetail(Block block, String title) {
		ScrutinyDetail detail = new ScrutinyDetail();
		detail.addColumnHeading(1, RULE_NO);
		detail.addColumnHeading(2, FLOOR);
		detail.addColumnHeading(3, DESCRIPTION);
		detail.addColumnHeading(4, PERMISSIBLE);
		detail.addColumnHeading(5, PROVIDED);
		detail.addColumnHeading(6, STATUS);
		detail.setKey("Block_" + block.getNumber() + "_" + title);
		return detail;
	}

	private BigDecimal updateTotalRisers(org.egov.common.entity.edcr.GeneralStair stair, BigDecimal totalRisers) {
		for (Flight flight : stair.getFlights()) {
			totalRisers = totalRisers.add(flight.getNoOfRises());
		}
		return totalRisers;
	}

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

	private void validateRiserHeight(Plan plan, Block block, BigDecimal flrHt, BigDecimal totalSteps, ScrutinyDetail scrutinyDetail4) {
		BigDecimal value = getPermissibleRiserHeight(plan);
		if (flrHt != null) {
			BigDecimal riserHeight = flrHt.divide(totalSteps, 2, RoundingMode.HALF_UP);
			String result = (riserHeight.compareTo(value) <= 0) ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal();
			setReportOutputDetailsFloorStairWise(plan, RULE, "", "", "" + value, "" + riserHeight, result, scrutinyDetail4);
		}
	}

	private BigDecimal getPermissibleRiserHeight(Plan plan) {
		List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.RISER_HEIGHT, false);
		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
		return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
	}

	private void handleStairErrors(Plan plan, Block block, List<String> stairAbsent, int generalStairCount, HashMap<String, String> errors) {
		for (String error : stairAbsent) {
			errors.put("General Stair " + error, "General stair not defined in " + error);
			plan.addErrors(errors);
		}
		if (generalStairCount == 0) {
			errors.put("General Stair not defined in blk " + block.getNumber(),
					"General Stair not defined in block " + block.getNumber() + ", it is mandatory for building with floors more than one.");
			plan.addErrors(errors);
		}
	}


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

				if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
					minWidth = Util.roundOffTwoDecimal(landingWidth);
					BigDecimal minimumWidth = getRequiredWidth(plan, block, mostRestrictiveOccupancyType);

					if (minWidth.compareTo(minimumWidth) >= 0) {
						valid = true;
					}
					String value = typicalFloorValues.get("typicalFloors") != null
							? (String) typicalFloorValues.get("typicalFloors")
							: " floor " + floor.getNumber();

					if (valid) {
						setReportOutputDetailsFloorStairWise(plan, RULE, value,
								String.format(WIDTH_LANDING_DESCRIPTION, generalStair.getNumber(), landing.getNumber()),
								minimumWidth.toString(), String.valueOf(minWidth), Result.Accepted.getResultVal(),
								scrutinyDetailLanding);
					} else {
						setReportOutputDetailsFloorStairWise(plan, RULE, value,
								String.format(WIDTH_LANDING_DESCRIPTION, generalStair.getNumber(), landing.getNumber()),
								minimumWidth.toString(), String.valueOf(minWidth), Result.Not_Accepted.getResultVal(),
								scrutinyDetailLanding);
					}
				}
				System.out.println("minn" + minWidth);
			} else {
				errors.put(
						"General Stair landing width not defined in block " + block.getNumber() + " floor "
								+ floor.getNumber() + " stair " + generalStair.getNumber(),
						"General Stair landing width not defined in block " + block.getNumber() + " floor "
								+ floor.getNumber() + " stair " + generalStair.getNumber());
				plan.addErrors(errors);

			}

		}

	}


	
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
	        errors.put("Flight PolyLine " + flightLayerName,
	                FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION + flightLayerName);
	        plan.addErrors(errors);
	    }
	}
	private void validateFlightWidth(Plan plan, HashMap<String, String> errors, ScrutinyDetail scrutinyDetail2,
	        Floor floor, Block block, Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
	        Flight flight, List<BigDecimal> flightWidths, OccupancyTypeHelper mostRestrictiveOccupancyType, String flightLayerName) {

	    if (flightWidths != null && !flightWidths.isEmpty()) {
	        validateWidth(plan, scrutinyDetail2, floor, block, typicalFloorValues,
	                generalStair, flight, flightWidths, BigDecimal.ZERO, mostRestrictiveOccupancyType);
	    } else {
	        errors.put("Flight PolyLine width" + flightLayerName,
	                FLIGHT_WIDTH_DEFINED_DESCRIPTION + flightLayerName);
	        plan.addErrors(errors);
	    }
	}
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
	        errors.put("Flight PolyLine length" + flightLayerName,
	                FLIGHT_LENGTH_DEFINED_DESCRIPTION + flightLayerName);
	        plan.addErrors(errors);
	    }
	}
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
	        errors.put("noofRise" + flightLayerName,
	                edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
	                        new String[]{NO_OF_RISERS + flightLayerName},
	                        LocaleContextHolder.getLocale()));
	        plan.addErrors(errors);
	    }
	}
	private void handleMissingFlights(Plan plan, HashMap<String, String> errors, Block block, Floor floor) {
	    String error = String.format(FLIGHT_NOT_DEFINED_DESCRIPTION, block.getNumber(), floor.getNumber());
	    errors.put(error, error);
	    plan.addErrors(errors);
	}


	private BigDecimal validateWidth(Plan plan, ScrutinyDetail scrutinyDetail2, Floor floor, Block block,
			Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
			Flight flight, List<BigDecimal> flightWidths, BigDecimal minFlightWidth,
			OccupancyTypeHelper mostRestrictiveOccupancyType) {
		BigDecimal flightPolyLine = flightWidths.stream().reduce(BigDecimal::min).get();
		boolean valid = false;

		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
			minFlightWidth = Util.roundOffTwoDecimal(flightPolyLine);
			BigDecimal minimumWidth = getRequiredWidth(plan, block, mostRestrictiveOccupancyType);

			if (minFlightWidth.compareTo(minimumWidth) >= 0) {
				valid = true;
			}
			String value = typicalFloorValues.get("typicalFloors") != null
					? (String) typicalFloorValues.get("typicalFloors")
					: " floor " + floor.getNumber();

			if (valid) {
				setReportOutputDetailsFloorStairWise(plan, RULE, value,
						String.format(WIDTH_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
						minimumWidth.toString(), String.valueOf(minFlightWidth), Result.Accepted.getResultVal(),
						scrutinyDetail2);
			} else {
				setReportOutputDetailsFloorStairWise(plan, RULE, value,
						String.format(WIDTH_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
						minimumWidth.toString(), String.valueOf(minFlightWidth), Result.Not_Accepted.getResultVal(),
						scrutinyDetail2);
			}
		}
		return minFlightWidth;
	}


	private BigDecimal getRequiredWidth(Plan pl, Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {


		BigDecimal value = BigDecimal.ZERO;
		
		// Fetch all rules for the given plan from the cache.
		// Then, filter to find the first rule where the condition falls within the
		// defined range.
		// If a matching rule is found, proceed with its processing.

		List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.REQUIRED_WIDTH, false);

		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

		if (matchedRule.isPresent()) {
			MdmsFeatureRule rule = matchedRule.get();
			value = rule.getPermissible();
		} else {
			value = BigDecimal.ZERO;
		}
		return value;
	}

	private BigDecimal getRequiredLandingWidth(Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {

		if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
			return BigDecimal.valueOf(0.76);
		} else {
			return BigDecimal.valueOf(1.5);
		}
	}

	
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

	            if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
	                validateTreadAgainstRequired(plan, scrutinyDetail3, floor, typicalFloorValues,
	                        generalStair, flight, requiredTread, minTread);
	            }

	        } else if (flight.getNoOfRises().compareTo(BigDecimal.ZERO) > 0) {
	            addNoOfRisesError(plan, errors, block, floor, generalStair, flight);
	        }
	    }

	    return minTread;
	}
	private BigDecimal calculateTotalFlightLength(List<BigDecimal> flightLengths) {
	    BigDecimal totalLength = flightLengths.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	    return Util.roundOffTwoDecimal(totalLength);
	}
	private void validateTreadAgainstRequired(Plan plan, ScrutinyDetail scrutinyDetail3, Floor floor,
	        Map<String, Object> typicalFloorValues, org.egov.common.entity.edcr.GeneralStair generalStair,
	        Flight flight, BigDecimal requiredTread, BigDecimal minTread) {

	    boolean isValid = Util.roundOffTwoDecimal(minTread)
	            .compareTo(Util.roundOffTwoDecimal(requiredTread)) >= 0;

	    String value = typicalFloorValues.get("typicalFloors") != null
	            ? (String) typicalFloorValues.get("typicalFloors")
	            : " floor " + floor.getNumber();

	    String description = String.format(TREAD_DESCRIPTION, generalStair.getNumber(), flight.getNumber());

	    setReportOutputDetailsFloorStairWise(plan, RULETREAD, value, description,
	            requiredTread.toString(), minTread.toString(),
	            isValid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal(), scrutinyDetail3);
	}
	private void addNoOfRisesError(Plan plan, HashMap<String, String> errors, Block block, Floor floor,
	        org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight) {

	    String flightLayerName = String.format(DxfFileConstants.LAYER_STAIR_FLIGHT, block.getNumber(),
	            floor.getNumber(), generalStair.getNumber(), flight.getNumber());

	    errors.put("NoOfRisesCount" + flightLayerName,
	            "Number of risers count should be greater than the count of length of flight dimensions defined in layer "
	                    + flightLayerName);
	    plan.addErrors(errors);
	}


	private BigDecimal getRequiredTread(Plan pl, OccupancyTypeHelper mostRestrictiveOccupancyType) {

		
		BigDecimal value = BigDecimal.ZERO;

		// Fetch all rules for the given plan from the cache.
		// Then, filter to find the first rule where the condition falls within the
		// defined range.
		// If a matching rule is found, proceed with its processing.

		List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.REQUIRED_TREAD, false);

		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

		if (matchedRule.isPresent()) {
			MdmsFeatureRule rule = matchedRule.get();
			value = rule.getPermissible();
		} else {
			value = BigDecimal.ZERO;
		}
		return value;
	}

	private void validateNoOfRises(Plan plan, HashMap<String, String> errors, Block block,
			ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues,
			org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight, BigDecimal noOfRises) {
		boolean valid = false;

		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
			
		
			BigDecimal noOfRisersValue = BigDecimal.ZERO;

			// Fetch all rules for the given plan from the cache.
			// Then, filter to find the first rule where the condition falls within the
			// defined range.
			// If a matching rule is found, proceed with its processing.

			List<Object> rules = cache.getFeatureRules(plan, MdmsFeatureConstants.NO_OF_RISER, false);

			Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

			if (matchedRule.isPresent()) {
				MdmsFeatureRule rule = matchedRule.get();
				noOfRisersValue = rule.getPermissible();
			} else {
				noOfRisersValue = BigDecimal.ZERO;
			}
			if (Util.roundOffTwoDecimal(noOfRises).compareTo(Util.roundOffTwoDecimal(noOfRisersValue)) <= 0) {
				valid = true;
			}
			valid = true;
			String value = typicalFloorValues.get("typicalFloors") != null
					? (String) typicalFloorValues.get("typicalFloors")
					: " floor " + floor.getNumber();
			if (valid) {
				setReportOutputDetailsFloorStairWise(plan, RULERISER, value,
						String.format(NO_OF_RISER_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
						"" + noOfRisersValue, String.valueOf(noOfRises), Result.Accepted.getResultVal(),
						scrutinyDetail3);
			} else {
				setReportOutputDetailsFloorStairWise(plan, RULERISER, value,
						String.format(NO_OF_RISER_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
						"" + noOfRisersValue, String.valueOf(noOfRises), Result.Not_Accepted.getResultVal(),
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