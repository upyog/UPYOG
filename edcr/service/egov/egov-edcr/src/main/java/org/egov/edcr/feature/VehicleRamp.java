/*UPYOG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.Flight;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.VehicleRampRequirement;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleRamp extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(VehicleRamp.class);
	private static final String SUBRULE_40_8 = "40-8";
	private static final String DESCRIPTION = "Vehicle Ramp";
	private static final String FLOOR = "Floor";
	private static final String FLIGHT = "Flight";

	@Autowired
	MDMSCacheManager cache;

	@Override
	public Plan validate(Plan pl) {
		// Loop through all blocks and floors to validate the vehicle ramp dimensions
		for (Block block : pl.getBlocks()) {
			if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
				for (Floor floor : block.getBuilding().getFloors()) {
					List<org.egov.common.entity.edcr.VehicleRamp> vehicleRamps = floor.getVehicleRamps();
					if (vehicleRamps != null && !vehicleRamps.isEmpty()) {
						for (org.egov.common.entity.edcr.VehicleRamp vehicleRamp : vehicleRamps) {
							List<Measurement> vehicleRampPolyLines = vehicleRamp.getRamps();
							if (vehicleRampPolyLines != null && !vehicleRampPolyLines.isEmpty()) {
								validateDimensions(pl, block.getNumber(), floor.getNumber(),
										vehicleRamp.getNumber().toString(), vehicleRampPolyLines);
							}
						}
					}
				}
			}
		}
		return pl;

	}

	@Override
	public Plan process(Plan pl) {
		validate(pl); // Initial validation of ramp geometry

		BigDecimal coverParkingArea = calculateCoveredParkingArea(pl);
		BigDecimal basementParkingArea = calculateBasementParkingArea(pl);
		BigDecimal totalProvidedCarParkArea = coverParkingArea.add(basementParkingArea);

		Map<String, String> details = initializeDetails();
		HashMap<String, String> errors = new HashMap<>();

		// Rule values from MDMS
		BigDecimal vehicleRampValue = BigDecimal.ZERO;
		BigDecimal vehicleRampSlopeValueOne = BigDecimal.ZERO;
		BigDecimal vehicleRampSlopeValueTwo = BigDecimal.ZERO;
		BigDecimal vehicleRampSlopeMinWidthValueOne = BigDecimal.ZERO;
		BigDecimal vehicleRampSlopeMinWidthValueTwo = BigDecimal.ZERO;
		BigDecimal vehicleRampSlopeMinWidthValueThree = BigDecimal.ZERO;

		 List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.VEHICLE_RAMP.getValue(), false);
	        Optional<VehicleRampRequirement> matchedRule = rules.stream()
	            .filter(VehicleRampRequirement.class::isInstance)
	            .map(VehicleRampRequirement.class::cast)
	            .findFirst();
		if (matchedRule.isPresent()) {
			VehicleRampRequirement rule = matchedRule.get();
			vehicleRampValue = rule.getPermissible();
			vehicleRampSlopeValueOne = rule.getVehicleRampSlopeValueOne();
			vehicleRampSlopeValueTwo = rule.getVehicleRampSlopeValueTwo();
			vehicleRampSlopeMinWidthValueOne = rule.getVehicleRampSlopeMinWidthValueOne();
			vehicleRampSlopeMinWidthValueTwo = rule.getVehicleRampSlopeMinWidthValueTwo();
			vehicleRampSlopeMinWidthValueThree = rule.getVehicleRampSlopeMinWidthValueThree();
		}

		if (totalProvidedCarParkArea != null && totalProvidedCarParkArea.compareTo(BigDecimal.ZERO) > 0) {
			processAllBlocks(pl, errors, details, vehicleRampValue, vehicleRampSlopeValueOne, vehicleRampSlopeValueTwo,
					vehicleRampSlopeMinWidthValueOne, vehicleRampSlopeMinWidthValueTwo,
					vehicleRampSlopeMinWidthValueThree);
		}

		return pl;
	}

	private BigDecimal calculateCoveredParkingArea(Plan pl) {
		BigDecimal coverParkingArea = BigDecimal.ZERO;
		for (Block block : pl.getBlocks()) {
			for (Floor floor : block.getBuilding().getFloors()) {
				coverParkingArea = coverParkingArea.add(floor.getParking().getCoverCars().stream()
						.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
			}
		}
		return coverParkingArea;
	}

	private BigDecimal calculateBasementParkingArea(Plan pl) {
		BigDecimal basementParkingArea = BigDecimal.ZERO;
		for (Block block : pl.getBlocks()) {
			for (Floor floor : block.getBuilding().getFloors()) {
				basementParkingArea = basementParkingArea.add(floor.getParking().getBasementCars().stream()
						.map(Measurement::getArea).reduce(BigDecimal.ZERO, BigDecimal::add));
			}
		}
		return basementParkingArea;
	}

	private Map<String, String> initializeDetails() {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, SUBRULE_40_8);
		details.put(DESCRIPTION, DESCRIPTION);
		return details;
	}

	private void processAllBlocks(Plan pl, HashMap<String, String> errors, Map<String, String> details,
			BigDecimal vehicleRampValue, BigDecimal vehicleRampSlopeValueOne, BigDecimal vehicleRampSlopeValueTwo,
			BigDecimal vehicleRampSlopeMinWidthValueOne, BigDecimal vehicleRampSlopeMinWidthValueTwo,
			BigDecimal vehicleRampSlopeMinWidthValueThree) {

		for (Block block : pl.getBlocks()) {
			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, FLOOR);
			scrutinyDetail.addColumnHeading(4, REQUIRED);
			scrutinyDetail.addColumnHeading(5, PROVIDED);
			scrutinyDetail.addColumnHeading(6, STATUS);
			scrutinyDetail.setKey("Vehicle Ramp");

			if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
				for (Floor floor : block.getBuilding().getFloors()) {
					if (!floor.getVehicleRamps().isEmpty()) {
						boolean rampWithFlight = floor.getVehicleRamps().stream()
								.anyMatch(vr -> !vr.getFlights().isEmpty());

						if (rampWithFlight) {
							processRampWitFlights(pl, errors, details, floor, vehicleRampValue,
									vehicleRampSlopeValueOne, vehicleRampSlopeValueTwo);
						} else {
							processRampWithOutFlights(pl, errors, details, floor, vehicleRampSlopeMinWidthValueOne,
									vehicleRampSlopeMinWidthValueTwo, vehicleRampSlopeMinWidthValueThree);
						}
					}
				}
			}
		}
	}

	private void processRampWitFlights(Plan pl, HashMap<String, String> errors, Map<String, String> details,
			Floor floor, BigDecimal vehicleRampValue, BigDecimal vehicleRampSlopeValueOne,
			BigDecimal vehicleRampSlopeValueTwo) {

		if (floor.getNumber() == 0)
			return;

		boolean hasRamps = floor.getVehicleRamps() != null && !floor.getVehicleRamps().isEmpty();
		boolean hasLifts = floor.getParking() != null && floor.getParking().getMechanicalLifts() != null
				&& !floor.getParking().getMechanicalLifts().isEmpty();

		if (!hasRamps && !hasLifts) {
			errors.put("Vehicle Ramp", "Either ramp or mechanical lift is required");
			pl.addErrors(errors);
			return;
		}

		if (hasRamps && (!hasLifts)) {
			for (org.egov.common.entity.edcr.VehicleRamp vehicleRamp : floor.getVehicleRamps()) {
				validateRampWidth(vehicleRamp, floor.getNumber(), vehicleRampValue, details, pl, errors);
				validateRampFlights(vehicleRamp, vehicleRampSlopeValueOne, vehicleRampSlopeValueTwo, pl, errors,
						details);
			}
		}
	}

	private void validateRampWidth(org.egov.common.entity.edcr.VehicleRamp ramp, int floorNumber,
			BigDecimal requiredWidth, Map<String, String> details, Plan pl, HashMap<String, String> errors) {

		if (ramp.getWidth().compareTo(BigDecimal.ZERO) <= 0) {
			errors.put("Vehicle Ramp width" + ramp.getNumber(),
					"Width of vehicle ramp not defined. Please check Vehicle Ramp " + ramp.getNumber());
			return;
		}

		details.put(FLOOR, "Floor " + floorNumber);
		details.put(REQUIRED, "Minimum " + requiredWidth.toString() + "m width");
		details.put(PROVIDED, ramp.getWidth().toString());

		if (ramp.getWidth().compareTo(requiredWidth) >= 0) {
			details.put(STATUS, Result.Accepted.getResultVal());
		} else {
			details.put(STATUS, Result.Not_Accepted.getResultVal());
		}

		scrutinyDetail.getDetail().add(new HashMap<>(details));
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private void validateRampFlights(org.egov.common.entity.edcr.VehicleRamp ramp, BigDecimal slopeValue1,
			BigDecimal slopeValue2, Plan pl, HashMap<String, String> errors, Map<String, String> details) {

		for (Flight flight : ramp.getFlights()) {
			if (flight.getLength().compareTo(BigDecimal.ZERO) <= 0 || flight.getHeight() == null) {
				errors.put("Vehicle Ramp Flight " + flight.getNumber(),
						"Ramp Flight length should be in polyline. Either flight length or RAMP_HT_M text not defined in vehicle ramp flight "
								+ flight.getNumber());
				continue;
			}

			BigDecimal slope = flight.getLength().divide(flight.getHeight(), 2, RoundingMode.HALF_UP);

			details.put(FLOOR, "Flight " + flight.getNumber());
			details.put(PROVIDED, "Slope 1:" + slope);

			if (flight.getColorCode() == 1) {
				validateSlope(details, slope, slopeValue1, pl, flight.getNumber());
			} else if (flight.getColorCode() == 2) {
				validateSlope(details, slope, slopeValue2, pl, flight.getNumber());
			} else {
				errors.put("Vehicle Ramp Flight " + flight.getNumber(),
						"Vehicle ramp polyline should be either 1 or 2. Please check Vehicle Ramp flight "
								+ flight.getNumber());
			}
		}
	}

	private void validateSlope(Map<String, String> details, BigDecimal actualSlope, BigDecimal requiredSlope, Plan pl,
			String flightNumber) {

		details.put(REQUIRED, "Slope 1:" + requiredSlope.toString());
		details.put(STATUS, actualSlope.compareTo(requiredSlope) >= 0 ? Result.Accepted.getResultVal()
				: Result.Not_Accepted.getResultVal());

		scrutinyDetail.getDetail().add(new HashMap<>(details));
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private void processRampWithOutFlights(Plan pl, HashMap<String, String> errors, Map<String, String> details,
			Floor floor, BigDecimal minWidthOne, BigDecimal maxSlope, BigDecimal minWidthTwo) {

		calculateRampSlopes(floor);

		if (floor.getNumber() != 0) {
			boolean hasLifts = floor.getParking() != null && floor.getParking().getMechanicalLifts() != null
					&& !floor.getParking().getMechanicalLifts().isEmpty();
			boolean hasRamps = floor.getVehicleRamps() != null && !floor.getVehicleRamps().isEmpty();

			if (!hasLifts && hasRamps) {
				validateRampsAndPopulateDetails(pl, details, floor, minWidthOne, maxSlope, minWidthTwo);
			} else if (!hasLifts && !hasRamps) {
				errors.put("Vehicle Ramp", "Either ramp or mechanical lift is required");
				pl.addErrors(errors);
			}
		}
	}

	private void calculateRampSlopes(Floor floor) {
		for (org.egov.common.entity.edcr.VehicleRamp ramp : floor.getVehicleRamps()) {
			if (Boolean.TRUE.equals(ramp.getRampClosed())) {
				BigDecimal totalLength = ramp.getRamps().stream().map(Measurement::getHeight).filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				if (totalLength.compareTo(BigDecimal.ZERO) > 0 && ramp.getFloorHeight() != null) {
					BigDecimal slope = ramp.getFloorHeight().divide(totalLength, 2, RoundingMode.HALF_UP);
					ramp.setSlope(slope);
				}
			}
		}
	}

	private void validateRampsAndPopulateDetails(Plan pl, Map<String, String> details, Floor floor,
			BigDecimal minWidthOne, BigDecimal maxSlope, BigDecimal minWidthTwo) {
		boolean valid = false, valid1 = false, valid2 = false;

		for (org.egov.common.entity.edcr.VehicleRamp ramp : floor.getVehicleRamps()) {
			BigDecimal minWidth = ramp.getRamps().stream().map(Measurement::getWidth).filter(Objects::nonNull)
					.min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);

			boolean isSlopeOk = ramp.getSlope() != null && ramp.getSlope().compareTo(maxSlope) <= 0;

			if (minWidth.compareTo(minWidthOne) >= 0 && isSlopeOk) {
				valid = true;
			}

			if (!valid1 && minWidth.compareTo(minWidthTwo) >= 0 && isSlopeOk) {
				valid1 = true;
			} else if (valid1 && minWidth.compareTo(minWidthTwo) >= 0 && isSlopeOk) {
				valid2 = true;
			}
		}

		addRampScrutinyDetails(pl, details, floor, valid, valid1 && valid2, minWidthOne, minWidthTwo);
	}

	private void addRampScrutinyDetails(Plan pl, Map<String, String> details, Floor floor, boolean valid,
			boolean validPair, BigDecimal minWidthOne, BigDecimal minWidthTwo) {
		details.put(FLOOR, "Floor " + floor.getNumber());
		details.put(REQUIRED, "At least two vehicle ramps of minimum " + minWidthTwo
				+ " m width or one vehicle ramp of minimum " + minWidthOne + " m width and in maximum 1:8 slope");

		if (valid) {
			details.put(PROVIDED, "Provided vehicle ramp with minimum " + minWidthOne + " width");
			details.put(STATUS, Result.Accepted.getResultVal());
		} else if (validPair) {
			details.put(PROVIDED, "Provided two vehicle ramps of minimum " + minWidthTwo + " m width");
			details.put(STATUS, Result.Accepted.getResultVal());
		} else {
			details.put(PROVIDED, "Not Provided vehicle ramp with minimum " + minWidthOne
					+ " width or two ramps of minimum " + minWidthTwo + " m width");
			details.put(STATUS, Result.Not_Accepted.getResultVal());
		}

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	// This method validates if all polylines of a vehicle ramp have exactly 4
	// points (rectangle)
	private void validateDimensions(Plan plan, String blockNo, int floorNo, String rampNo,
			List<Measurement> rampPolylines) {
		int count = 0;
		for (Measurement m : rampPolylines) {
			if (m.getInvalidReason() != null && m.getInvalidReason().length() > 0) {
				count++;
			}
		}

		// If any invalid polyline is found, add an error message
		if (count > 0) {
			plan.addError(String.format(DxfFileConstants.LAYER_VEHICLE_RAMP_WITH_NO, blockNo, floorNo, rampNo),
					count + " number of vehicle ramp polyline not having only 4 points in layer "
							+ String.format(DxfFileConstants.LAYER_VEHICLE_RAMP_WITH_NO, blockNo, floorNo, rampNo));
		}
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
