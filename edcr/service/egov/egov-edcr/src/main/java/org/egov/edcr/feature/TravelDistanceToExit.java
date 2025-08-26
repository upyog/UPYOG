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

import static org.egov.edcr.constants.CommonKeyConstants.COM_TRAVEL_DIS_EMERGENCY_EXIT;
import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.G;
import static org.egov.edcr.constants.DxfFileConstants.H;
import static org.egov.edcr.constants.DxfFileConstants.I;
import static org.egov.edcr.constants.EdcrReportConstants.SUBRULE_42_2;
import static org.egov.edcr.constants.EdcrReportConstants.SUBRULE_42_2_DESC;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TravelDistanceToExit extends FeatureProcess {

	// Logger for logging important information
	private static final Logger LOG = LogManager.getLogger(TravelDistanceToExit.class);

	// Permissible travel distances fetched from MDMS
	public static BigDecimal travelDistanceToExitValueOne = BigDecimal.ZERO;
	public static BigDecimal travelDistanceToExitValueTwo = BigDecimal.ZERO;
	public static BigDecimal travelDistanceToExitValueThree = BigDecimal.ZERO;


	@Autowired
	MDMSCacheManager cache;

	/**
	 * Validates the building plan for travel distance to exit requirements.
	 * Currently performs no validation and returns the plan as-is.
	 *
	 * @param pl The building plan to validate
	 * @return The unmodified plan
	 */
	// No validation logic implemented for this feature
	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	/**
	 * Processes travel distance to exit requirements for the building plan.
	 * Extracts rules from MDMS, checks for exemptions, validates missing travel distances,
	 * and generates scrutiny details for compliance verification.
	 *
	 * @param pl The building plan to process
	 * @return The processed plan with scrutiny details or errors added
	 */
	@Override
    public Plan process(Plan pl) {
        if (pl == null) return pl;

        // Extract rule values into local variables
        TravelDistanceToExitRequirement rule = extractTravelDistanceRules(pl);
        BigDecimal travelDistanceToExitValueOne = rule.getTravelDistanceToExitValueOne();
        BigDecimal travelDistanceToExitValueTwo = rule.getTravelDistanceToExitValueTwo();
        BigDecimal travelDistanceToExitValueThree = rule.getTravelDistanceToExitValueThree();

        boolean exemption = isExempted(pl, travelDistanceToExitValueThree);
        if (exemption) return pl;

        if (pl.getTravelDistancesToExit().isEmpty()) {
            addMissingTravelDistanceError(pl);
            return pl;
        }

        validateTravelDistances(pl, travelDistanceToExitValueOne, travelDistanceToExitValueTwo);
        return pl;
    }
	
	 

	/**
	 * Extracts travel distance requirement rules from MDMS cache.
	 * Fetches and sets the three travel distance values used for different
	 * occupancy type validations.
	 *
	 * @param pl The building plan containing configuration details
	 */
	 private TravelDistanceToExitRequirement extractTravelDistanceRules(Plan pl) {
	        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.TRAVEL_DISTANCE_TO_EXIT.getValue(), false);
	        Optional<TravelDistanceToExitRequirement> matchedRule = rules.stream()
	            .filter(TravelDistanceToExitRequirement.class::isInstance)
	            .map(TravelDistanceToExitRequirement.class::cast)
	            .findFirst();
	        return matchedRule.orElse(new TravelDistanceToExitRequirement());
	    }

	/**
	 * Checks if the building plan is exempted from travel distance requirements.
	 * Exemptions apply to residential buildings with max 3 floors above ground
	 * or buildings on small plots.
	 *
	 * @param pl The building plan to check for exemptions
	 * @return true if exempted from travel distance requirements, false otherwise
	 */
	 private boolean isExempted(Plan pl, BigDecimal travelDistanceToExitValueThree) {
	        if (pl.getVirtualBuilding() == null || pl.getVirtualBuilding().getOccupancyTypes().isEmpty() || pl.getBlocks().isEmpty()) {
	            return false;
	        }

	        boolean allBlocksWithMax3Floors = pl.getBlocks().stream().allMatch(block ->
	            block.getBuilding() != null &&
	            block.getBuilding().getFloorsAboveGround() != null &&
	            block.getBuilding().getFloorsAboveGround().compareTo(travelDistanceToExitValueThree) <= 0
	        );

	        boolean isResidential = Boolean.TRUE.equals(pl.getVirtualBuilding().getResidentialBuilding());
	        return (isResidential && allBlocksWithMax3Floors) || ProcessHelper.isSmallPlot(pl);
	    }

	/**
	 * Adds validation error when travel distance measurements are missing from the plan.
	 * Creates an error message indicating that travel distance to exit is not defined.
	 *
	 * @param pl The building plan to add the error to
	 */
	private void addMissingTravelDistanceError(Plan pl) {
        Map<String, String> errors = new HashMap<>();
        errors.put(DcrConstants.TRAVEL_DIST_EXIT,
            edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                new String[]{DcrConstants.TRAVEL_DIST_EXIT}, LocaleContextHolder.getLocale()));
        pl.addErrors(errors);
    }

	/**
	 * Validates actual travel distances against required limits based on occupancy
	 * type. Creates scrutiny details comparing provided distances with
	 * occupancy-specific maximum allowable travel distances.
	 *
	 * @param pl The building plan containing travel distance measurements
	 */
	private void validateTravelDistances(Plan pl, BigDecimal travelDistanceToExitValueOne,
			BigDecimal travelDistanceToExitValueTwo) {
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey(COM_TRAVEL_DIS_EMERGENCY_EXIT);
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, REQUIRED);
		scrutinyDetail.addColumnHeading(3, PROVIDED);
		scrutinyDetail.addColumnHeading(4, STATUS);
		scrutinyDetail.setSubHeading(SUBRULE_42_2_DESC);

		OccupancyTypeHelper occupancyHelper = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
		String occupancyCode = occupancyHelper.getType().getCode();

		Map<String, BigDecimal> occupancyValues = getOccupancyValues(travelDistanceToExitValueOne,
				travelDistanceToExitValueTwo);
		BigDecimal requiredValue = occupancyValues.get(occupancyCode);

		if (requiredValue != null) {
			for (BigDecimal providedValue : pl.getTravelDistancesToExit()) {
				boolean isAccepted = providedValue.compareTo(requiredValue) <= 0;
				setReportOutputDetails(pl, SUBRULE_42_2, requiredValue + DcrConstants.IN_METER,
						providedValue + DcrConstants.IN_METER,
						isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
			}
		}
	}

	/**
	 * Adds a single validation result entry to the scrutiny report.
	 * Creates a detailed report entry with rule information, requirements,
	 * and compliance status.
	 *
	 * @param pl The building plan
	 * @param ruleNo The rule number being validated
	 * @param expected The required/maximum allowable value
	 * @param actual The actual/provided value
	 * @param status The compliance status (Accepted/Not_Accepted)
	 */
//	// Helper to append result details to the scrutiny report
	private void setReportOutputDetails(Plan pl, String ruleNo, String expected, String actual, String status) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(ruleNo);
		detail.setRequired(expected);
		detail.setProvided(actual);
		detail.setStatus(status);

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	}

	/**
	 * Maps occupancy type codes to their respective maximum travel distance limits.
	 * Returns a map with occupancy codes as keys and their corresponding
	 * maximum allowable travel distances as values.
	 *
	 * @param valueOne Travel distance limit for occupancy types D, G, F, H
	 * @param valueTwo Travel distance limit for occupancy types A, I, B
	 * @return Map of occupancy codes to their maximum travel distance limits
	 */
//	// Mapping occupancy types to their respective travel distance limits
	public Map<String, BigDecimal> getOccupancyValues(BigDecimal valueOne, BigDecimal valueTwo) {

		Map<String, BigDecimal> roadWidthValues = new HashMap<>();

		roadWidthValues.put(D, valueOne);
		roadWidthValues.put(G, valueOne);
		roadWidthValues.put(F, valueOne);
		roadWidthValues.put(H, valueOne);

		roadWidthValues.put(A, valueTwo);
		roadWidthValues.put(I, valueTwo);
		roadWidthValues.put(B, valueTwo);

		return roadWidthValues;
	}

	/**
	 * Returns amendment dates for travel distance to exit rules.
	 * Currently returns an empty map as no amendments are defined.
	 *
	 * @return Empty LinkedHashMap of amendment dates
	 */
	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}