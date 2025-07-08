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

	@Override
	public Plan validate(Plan plan) {
		return plan;
	}

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;

	@Autowired
	CacheManagerMdms cache;

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
			BigDecimal totalLandingWidth = BigDecimal.ZERO;
			BigDecimal totalFlightWidth = BigDecimal.ZERO;
			BigDecimal totalRisers = BigDecimal.ZERO;
			BigDecimal totalSteps = BigDecimal.ZERO;

			if (block.getBuilding() != null) {
				/*
				 * if (Util.checkExemptionConditionForBuildingParts(block) ||
				 * Util.checkExemptionConditionForSmallPlotAtBlkLevel(planDetail.getPlot(),
				 * block)) { continue blk; }
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
				 * String occupancyType = mostRestrictiveOccupancy != null ?
				 * mostRestrictiveOccupancy.getOccupancyType() : null;
				 */

				List<Floor> floors = block.getBuilding().getFloors();
				List<String> stairAbsent = new ArrayList<>();
				// BigDecimal floorSize = block.getBuilding().getFloorsAboveGround();
				for (Floor floor : floors) {
					if (!floor.getTerrace()) {

						boolean isTypicalRepititiveFloor = false;
						Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor,
								isTypicalRepititiveFloor);

						List<org.egov.common.entity.edcr.GeneralStair> generalStairs = floor.getGeneralStairs();

						int size = generalStairs.size();
						generalStairCount = generalStairCount + size;

						if (!generalStairs.isEmpty()) {
							for (org.egov.common.entity.edcr.GeneralStair generalStair : generalStairs) {

								flrHt = generalStair.getFloorHeight();
								System.out.println("flrHt___" + flrHt);
								List<StairLanding> landings1 = generalStair.getLandings();
								totalLandings += landings1.size();

								List<Flight> flights = generalStair.getFlights();
								totalFlights += flights.size();

								for (Flight flight : flights) {

									BigDecimal risers = flight.getNoOfRises();
									totalRisers = totalRisers.add(risers);
								}
								System.out.println("total totalRisers " + totalRisers);

								// Sum the landing widths
								for (StairLanding landing : landings1) {
									List<BigDecimal> widths = landing.getWidths();
									if (!widths.isEmpty()) {
										BigDecimal landingWidth = widths.stream().reduce(BigDecimal::min).get();
										totalLandingWidth = totalLandingWidth.add(landingWidth); // Add to total
									}
								}

								System.out.println("total landings " + totalLandingWidth);

								totalSteps = totalRisers.add(totalLandingWidth);

								System.out.println("total totalSteps " + totalSteps);

								{
									validateFlight(plan, errors, block, scrutinyDetail2, scrutinyDetail3,
											scrutinyDetailRise, mostRestrictiveOccupancyType, floor, typicalFloorValues,
											generalStair, generalStairCount);

									List<StairLanding> landings = generalStair.getLandings();
									if (!landings.isEmpty()) {
										validateLanding(plan, block, scrutinyDetailLanding,
												mostRestrictiveOccupancyType, floor, typicalFloorValues, generalStair,
												landings, errors);
									} else {
										if (floor.getNumber() != generalStairCount - 1) // This condition because in top
																						// most floor stairs are not
																						// mandatory for punjab,
											// so removing the error if stairs are not defined in top mist floor
											errors.put(
													"General Stair landing not defined in block " + block.getNumber()
															+ " floor " + floor.getNumber() + " stair "
															+ generalStair.getNumber(),
													"General Stair landing not defined in block " + block.getNumber()
															+ " floor " + floor.getNumber() + " stair "
															+ generalStair.getNumber());
										plan.addErrors(errors);
									}

								}

							}

						} else {
							if (floor.getNumber() != generalStairCount) {
								stairAbsent.add("Block " + block.getNumber() + " floor " + floor.getNumber());
							}
						}

					}
				}

				String occupancyName = "";

				String featureName = MdmsFeatureConstants.RISER_HEIGHT;

				BigDecimal value = BigDecimal.ZERO;

				occupancyName = fetchEdcrRulesMdms.getOccupancyName(plan).toLowerCase();
				String tenantId = plan.getTenantId();
				String zone = plan.getPlanInformation().getZone().toLowerCase();
				String subZone = plan.getPlanInformation().getSubZone().toLowerCase();
				String riskType = fetchEdcrRulesMdms.getRiskType(plan).toLowerCase();

				RuleKey key = new RuleKey(EdcrRulesMdmsConstants.STATE, tenantId, zone, subZone, occupancyName, null,
						featureName);
				List<Object> rules = cache.getRules(tenantId, key);

				Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

				if (matchedRule.isPresent()) {
					MdmsFeatureRule rule = matchedRule.get();
					value = rule.getPermissible();
				} else {
					value = BigDecimal.ZERO;
				}

				if (flrHt != null) {
					BigDecimal riserHeight = flrHt.divide(totalSteps, 2, RoundingMode.HALF_UP);

					if (riserHeight.compareTo(value) <= 0) {
						setReportOutputDetailsFloorStairWise(plan, RULE, "", "", "" + value, "" + riserHeight,

								Result.Accepted.getResultVal(), scrutinyDetail4);
					} else {
						setReportOutputDetailsFloorStairWise(plan, RULE, "", "", "" + value, "" + riserHeight,
								Result.Not_Accepted.getResultVal(), scrutinyDetail4);
					}
				}

				if (!stairAbsent.isEmpty()) {
					for (String error : stairAbsent) {
						errors.put("General Stair " + error, "General stair not defined in " + error);
						plan.addErrors(errors);
					}
				}

				if (generalStairCount == 0) {
					errors.put("General Stair not defined in blk " + block.getNumber(),
							"General Stair not defined in block " + block.getNumber()
									+ ", it is mandatory for building with floors more than one.");
					plan.addErrors(errors);
				}
			}
		}

		return plan;
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
			ScrutinyDetail scrutinyDetail3, ScrutinyDetail scrutinyDetailRise,
			OccupancyTypeHelper mostRestrictiveOccupancyType, Floor floor, Map<String, Object> typicalFloorValues,
			org.egov.common.entity.edcr.GeneralStair generalStair, int generalStairCount) {
		if (!generalStair.getFlights().isEmpty()) {

			for (Flight flight : generalStair.getFlights()) {

				List<Measurement> flightPolyLines = flight.getFlights();
				List<BigDecimal> flightLengths = flight.getLengthOfFlights();
				List<BigDecimal> flightWidths = flight.getWidthOfFlights();
				BigDecimal noOfRises = flight.getNoOfRises();
				Boolean flightPolyLineClosed = flight.getFlightClosed();

				// flight.getNumber();
				BigDecimal minTread = BigDecimal.ZERO;
				BigDecimal minFlightWidth = BigDecimal.ZERO;
				String flightLayerName = String.format(DxfFileConstants.LAYER_STAIR_FLIGHT, block.getNumber(),
						floor.getNumber(), generalStair.getNumber(), flight.getNumber());

				if (flightPolyLines != null && flightPolyLines.size() > 0) {
					if (flightPolyLineClosed) {
						if (flightWidths != null && flightWidths.size() > 0) {
							minFlightWidth = validateWidth(plan, scrutinyDetail2, floor, block, typicalFloorValues,
									generalStair, flight, flightWidths, minFlightWidth, mostRestrictiveOccupancyType);

						} else {
							errors.put("Flight PolyLine width" + flightLayerName,
									FLIGHT_WIDTH_DEFINED_DESCRIPTION + flightLayerName);
							plan.addErrors(errors);
						}

						/*
						 * (Total length of polygons in layer BLK_n_FLR_i_STAIR_k_FLIGHT) / (Number of
						 * rises - number of polygons in layer BLK_n_FLR_i_STAIR_k_FLIGHT - number of
						 * lines in layer BLK_n_FLR_i_STAIR_k_FLIGHT)
						 */

						if (flightLengths != null && flightLengths.size() > 0) {
							try {
								minTread = validateTread(plan, errors, block, scrutinyDetail3, floor,
										typicalFloorValues, generalStair, flight, flightLengths, minTread,
										mostRestrictiveOccupancyType);
							} catch (ArithmeticException e) {
								LOG.info("Denominator is zero");
							}
						} else {

							errors.put("Flight PolyLine length" + flightLayerName,
									FLIGHT_LENGTH_DEFINED_DESCRIPTION + flightLayerName);
							plan.addErrors(errors);

						}

						if (noOfRises.compareTo(BigDecimal.ZERO) > 0) {
							try {
								validateNoOfRises(plan, errors, block, scrutinyDetailRise, floor, typicalFloorValues,
										generalStair, flight, noOfRises);
							} catch (ArithmeticException e) {
								LOG.info("Denominator is zero");
							}
						} else {
							/*
							 * String layerName = String.format( DxfFileConstants.LAYER_STAIR_FLIGHT,
							 * block.getNumber(), floor.getNumber(), generalStair.getNumber(),
							 * flight.getNumber());
							 */
							errors.put("noofRise" + flightLayerName,
									edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
											new String[] { NO_OF_RISERS + flightLayerName },
											LocaleContextHolder.getLocale()));
							plan.addErrors(errors);
						}

					}
				} else {
					errors.put("Flight PolyLine " + flightLayerName,
							FLIGHT_POLYLINE_NOT_DEFINED_DESCRIPTION + flightLayerName);
					plan.addErrors(errors);
				}

			}

		} else {
//        	if(floor.getNumber() != generalStairCount - 1) 
			{ // This condition because in top most floor stairs are not mandatory for punjab,
				// so removing the error if stairs are not defined in top mist floor
				String error = String.format(FLIGHT_NOT_DEFINED_DESCRIPTION, block.getNumber(), floor.getNumber());
				errors.put(error, error);
				plan.addErrors(errors);
			}
		}
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

//    private BigDecimal getRequiredWidth(Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {
//        if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
//                && DxfFileConstants.A_AF.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//            return BigDecimal.valueOf(1.9);
//        } else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
//                && DxfFileConstants.A_AF_GH.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//            return BigDecimal.valueOf(0.75);
//        }
	// else if (mostRestrictiveOccupancyType != null &&
	// mostRestrictiveOccupancyType.getType() != null
//                && DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())
//                && block.getBuilding().getBuildingHeight().compareTo(BigDecimal.valueOf(10)) <= 0
//                && block.getBuilding().getFloorsAboveGround().compareTo(BigDecimal.valueOf(3)) <= 0) {
//            return BigDecimal.ONE;
//        }
//        else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
//                && DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//            return BigDecimal.valueOf(0.76);
//        } else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
//                && DxfFileConstants.B.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//            return BigDecimal.valueOf(1.5);
//        } else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
//                && DxfFileConstants.D.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//            return BigDecimal.valueOf(2);
//        } else {
//            return BigDecimal.valueOf(1.5);
//        }
//    }

	private BigDecimal getRequiredWidth(Plan pl, Block block, OccupancyTypeHelper mostRestrictiveOccupancyType) {
//		BigDecimal buildingHeight = block.getBuilding().getHeight();

		String occupancyName = "";
		String subOccupancyName = "";
		String featureName = MdmsFeatureConstants.REQUIRED_WIDTH;

		BigDecimal value = BigDecimal.ZERO;

		if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.A_R.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
			occupancyName = "residential";
			subOccupancyName = "apartment/Flat";
//			return BigDecimal.valueOf(0.85);
		} else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.A_AF_GH.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
			occupancyName = "residential";
			subOccupancyName = "";
//			return BigDecimal.valueOf(0.85);
		} else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
			occupancyName = "residential";
//			return BigDecimal.valueOf(0.85);
		} else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.B.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())
				&& block.getBuilding().getBuildingHeight().compareTo(BigDecimal.valueOf(24)) <= 0) {
			occupancyName = "educational";
			// return BigDecimal.valueOf(1.5);
		} else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.B.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
			occupancyName = "educational";
//			return BigDecimal.valueOf(2.0);
		} else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.C.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
			occupancyName = "medical/hospital";
//			return BigDecimal.valueOf(1.5);
		} else if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getType() != null
				&& DxfFileConstants.D.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
			occupancyName = "assembly";
//			return BigDecimal.valueOf(1.5);
		} else {
			occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl).toLowerCase();
//			return BigDecimal.valueOf(1.5);
		}

		String tenantId = pl.getTenantId();
		String zone = pl.getPlanInformation().getZone().toLowerCase();
		String subZone = pl.getPlanInformation().getSubZone().toLowerCase();
		String riskType = fetchEdcrRulesMdms.getRiskType(pl).toLowerCase();

		RuleKey key = new RuleKey(EdcrRulesMdmsConstants.STATE, tenantId, zone, subZone, occupancyName, null,
				featureName);
		List<Object> rules = cache.getRules(tenantId, key);

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
		BigDecimal totalLength = flightLengths.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		// Map<String, List<Map<String, Object>>> edcrRuleList =
		// plan.getEdcrRulesFeatures();

		totalLength = Util.roundOffTwoDecimal(totalLength);

		BigDecimal requiredTread = getRequiredTread(plan, mostRestrictiveOccupancyType);

		if (flight.getNoOfRises() != null) {
			/*
			 * BigDecimal denominator =
			 * fireStair.getNoOfRises().subtract(BigDecimal.valueOf(flightLengths.size()))
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
							: " floor " + floor.getNumber();
					if (valid) {
						setReportOutputDetailsFloorStairWise(plan, RULETREAD, value,
								String.format(TREAD_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
								requiredTread.toString(), String.valueOf(minTread), Result.Accepted.getResultVal(),
								scrutinyDetail3);
					} else {
						setReportOutputDetailsFloorStairWise(plan, RULETREAD, value,
								String.format(TREAD_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
								requiredTread.toString(), String.valueOf(minTread), Result.Not_Accepted.getResultVal(),
								scrutinyDetail3);
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

//	private BigDecimal getRequiredTread(OccupancyTypeHelper mostRestrictiveOccupancyType) {
//        if (mostRestrictiveOccupancyType != null
//        		//&& mostRestrictiveOccupancyType.getSubtype() != null
//                //&& DxfFileConstants.A_AF.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())) {
//        	 && DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//            return BigDecimal.valueOf(0.25);
//        } else {
//            return BigDecimal.valueOf(0.3);
//        	//return null;
//        }
//    }

	private BigDecimal getRequiredTread(Plan pl, OccupancyTypeHelper mostRestrictiveOccupancyType) {

		String occupancyName = "";
		String subOccupancyName = "";
		String featureName = MdmsFeatureConstants.REQUIRED_TREAD;

		BigDecimal value = BigDecimal.ZERO;

		if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getSubtype() != null
				&& DxfFileConstants.A_R.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())) {
			occupancyName = MdmsFeatureConstants.RESIDENTIAL;
			subOccupancyName = MdmsFeatureConstants.APARTMENT_FLAT;
//			return BigDecimal.valueOf(0.25);
		} else {
			occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl).toLowerCase();
//			return BigDecimal.valueOf(0.3);
		}

		String tenantId = pl.getTenantId();
		String zone = pl.getPlanInformation().getZone().toLowerCase();
		String subZone = pl.getPlanInformation().getSubZone().toLowerCase();
		String riskType = fetchEdcrRulesMdms.getRiskType(pl).toLowerCase();

		RuleKey key = new RuleKey(EdcrRulesMdmsConstants.STATE, tenantId, zone, subZone, occupancyName, null,
				featureName);
		List<Object> rules = cache.getRules(tenantId, key);

		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

		if (matchedRule.isPresent()) {
			MdmsFeatureRule rule = matchedRule.get();
			value = rule.getPermissible();
		} else {
			value = BigDecimal.ZERO;
		}
		return value;
	}

//    private void validateNoOfRises(Plan plan, HashMap<String, String> errors, Block block,
//            ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues,
//            org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight, BigDecimal noOfRises) {
//        boolean valid = false;
//
//        if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
//            if (Util.roundOffTwoDecimal(noOfRises).compareTo(Util.roundOffTwoDecimal(BigDecimal.valueOf(12))) <= 0) {
//                valid = true;
//            }
//
//            String value = typicalFloorValues.get("typicalFloors") != null
//                    ? (String) typicalFloorValues.get("typicalFloors")
//                    : " floor " + floor.getNumber();
//            if (valid) {
//                setReportOutputDetailsFloorStairWise(plan, RULERISER, value,
//                        String.format(NO_OF_RISER_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
//                        EXPECTED_NO_OF_RISER,
//                        String.valueOf(noOfRises), Result.Accepted.getResultVal(), scrutinyDetail3);
//            } else {
//                setReportOutputDetailsFloorStairWise(plan, RULERISER, value,
//                        String.format(NO_OF_RISER_DESCRIPTION, generalStair.getNumber(), flight.getNumber()),
//                        EXPECTED_NO_OF_RISER,
//                        String.valueOf(noOfRises), Result.Not_Accepted.getResultVal(), scrutinyDetail3);
//            }
//        }
//    }

	private void validateNoOfRises(Plan plan, HashMap<String, String> errors, Block block,
			ScrutinyDetail scrutinyDetail3, Floor floor, Map<String, Object> typicalFloorValues,
			org.egov.common.entity.edcr.GeneralStair generalStair, Flight flight, BigDecimal noOfRises) {
		boolean valid = false;

		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {
			String featureName = MdmsFeatureConstants.NO_OF_RISER;
			// String featureName1 = "noOfRiser";
			BigDecimal noOfRisersValue = BigDecimal.ZERO;

			String occupancyName = fetchEdcrRulesMdms.getOccupancyName(plan).toLowerCase();
			String tenantId = plan.getTenantId();
			String zone = plan.getPlanInformation().getZone().toLowerCase();
			String subZone = plan.getPlanInformation().getSubZone().toLowerCase();
			String riskType = fetchEdcrRulesMdms.getRiskType(plan).toLowerCase();

			RuleKey key = new RuleKey(EdcrRulesMdmsConstants.STATE, tenantId, zone, subZone, occupancyName, null,
					featureName);
			List<Object> rules = cache.getRules(tenantId, key);

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