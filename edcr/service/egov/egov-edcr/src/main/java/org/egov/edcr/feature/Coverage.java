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

/* 
 * Edited by @Bhupesh Dewangan
 */
package org.egov.edcr.feature;

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.G;
//import static org.egov.edcr.constants.DxfFileConstants.J;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class Coverage extends FeatureProcess {
	// private static final String OCCUPANCY2 = "OCCUPANCY";

	private static final Logger LOG = LogManager.getLogger(Coverage.class);

	// private static final String RULE_NAME_KEY = "coverage.rulename";
	private static final String RULE_DESCRIPTION_KEY = "coverage.description";
	private static final String RULE_EXPECTED_KEY = "coverage.expected";
	private static final String RULE_ACTUAL_KEY = "coverage.actual";
//	private static final BigDecimal Thirty = BigDecimal.valueOf(30);
//	private static final BigDecimal ThirtyFive = BigDecimal.valueOf(35);
//	private static final BigDecimal Forty = BigDecimal.valueOf(40);

	/*
	 * private static final BigDecimal FortyFive = BigDecimal.valueOf(45); private
	 * static final BigDecimal Fifty = BigDecimal.valueOf(50); private static final
	 * BigDecimal FiftyFive = BigDecimal.valueOf(55); private static final
	 * BigDecimal Sixty = BigDecimal.valueOf(60); private static final BigDecimal
	 * SixtyFive = BigDecimal.valueOf(65); private static final BigDecimal Seventy =
	 * BigDecimal.valueOf(70); private static final BigDecimal SeventyFive =
	 * BigDecimal.valueOf(75); private static final BigDecimal Eighty =
	 * BigDecimal.valueOf(80);
	 */

	public static final String RULE_38 = "38";
	public static final String RULE_7_C_1 = "Table 7-C-1";
	public static final String RULE = "4.4.4";
	private static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);
	private static final BigDecimal ROAD_WIDTH_THIRTY_POINTFIVE = BigDecimal.valueOf(30.5);

	@Override
	public Plan validate(Plan pl) {
		for (Block block : pl.getBlocks()) {
			if (block.getCoverage().isEmpty()) {
				pl.addError("coverageArea" + block.getNumber(),
						"Coverage Area for block " + block.getNumber() + " not Provided");
			}
		}
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		LOG.info("inside Coverage process()");
		validate(pl);
		System.out.println("coverage corearea" + pl.getCoreArea());
		BigDecimal totalCoverage = BigDecimal.ZERO;
		BigDecimal totalCoverageArea = BigDecimal.ZERO;
//		BigDecimal area = pl.getPlot().getArea(); // add for get total plot area
		BigDecimal plotArea = pl.getPlot().getArea(); // add for get total plot area

		String coreArea = pl.getCoreArea();
		int noOfFloors = 0;
		Set<OccupancyTypeHelper> occupancyList = new HashSet<>();
		// add for getting OccupancyType
		OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
//		String a=mostRestrictiveOccupancy.getType().getCode();
		// add for getting OccupancyType
//		OccupancyType mostRestrictiveOccupancy = getMostRestrictiveCoverage(pl.getVirtualBuilding().getOccupancies());
		for (Block block : pl.getBlocks()) {

			for (Floor flr : block.getBuilding().getFloors()) {
				for (Occupancy occupancy : flr.getOccupancies()) {
					if (occupancy.getTypeHelper() != null && occupancy.getTypeHelper().getType() != null)
						occupancyList.add(occupancy.getTypeHelper());
				}
			}

			BigDecimal coverageAreaWithoutDeduction = BigDecimal.ZERO;
			BigDecimal coverageDeductionArea = BigDecimal.ZERO;

			noOfFloors = block.getBuilding().getFloors().size();

			for (Measurement coverage : block.getCoverage()) {
				coverageAreaWithoutDeduction = coverageAreaWithoutDeduction.add(coverage.getArea());
			}
			for (Measurement deduct : block.getCoverageDeductions()) {
				coverageDeductionArea = coverageDeductionArea.add(deduct.getArea());
			}
			if (block.getBuilding() != null) {
				block.getBuilding().setCoverageArea(coverageAreaWithoutDeduction.subtract(coverageDeductionArea));
				BigDecimal coverage = BigDecimal.ZERO;
				if (pl.getPlot().getPlotBndryArea().doubleValue() > 0)
					coverage = block.getBuilding().getCoverageArea().multiply(BigDecimal.valueOf(100)).divide(
							plotArea, DcrConstants.DECIMALDIGITS_MEASUREMENTS,
							DcrConstants.ROUNDMODE_MEASUREMENTS);

				block.getBuilding().setCoverage(coverage);

				totalCoverageArea = totalCoverageArea.add(block.getBuilding().getCoverageArea());
				// totalCoverage =
				// totalCoverage.add(block.getBuilding().getCoverage());
			}

		}

		// pl.setCoverageArea(totalCoverageArea);
		// use plotArea
		if (pl.getPlot() != null && pl.getPlot().getArea().doubleValue() > 0)
			totalCoverage = totalCoverageArea.multiply(BigDecimal.valueOf(100)).divide(plotArea,
					DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
		pl.setCoverage(totalCoverage);
		if (pl.getVirtualBuilding() != null) {
			pl.getVirtualBuilding().setTotalCoverageArea(totalCoverageArea);
		}

		BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();
//		String areaCategory = pl.getAreaCategory();
		BigDecimal permissibleCoverageValue = BigDecimal.ZERO;
		//String developmentZone = pl.getPlanInformation().getDevelopmentZone(); //
//		if (developmentZone == null) {
//			pl.addError(getLocaleMessage(OBJECTNOTDEFINED, DEVELOPMENT_ZONE + " of PLAN_INFO layer"));
//		}
//		String occupancyType;

		// get coverage permissible value from method and store in
		// permissibleCoverageValue
		if (plotArea.compareTo(BigDecimal.valueOf(0)) > 0 && mostRestrictiveOccupancy != null &&
				A.equals(mostRestrictiveOccupancy.getType().getCode())
				) {
//			occupancyType = mostRestrictiveOccupancy.getType().getCode();
			permissibleCoverageValue = getPermissibleCoverageForResidential(plotArea, coreArea);
		}
				//permissibleCoverageValue = getPermissibleCoverageForMix(plotArea);
//			} else if (A.equals(mostRestrictiveOccupancy.getType().getCode())) { // if
//				permissibleCoverageValue = getPermissibleCoverageForResidential(plotArea);
//			} else if (F.equals(mostRestrictiveOccupancy.getType().getCode())) { // if
//				permissibleCoverageValue = getPermissibleCoverageForCommercial(plotArea, developmentZone, noOfFloors);
//			} else if (J.equals(mostRestrictiveOccupancy.getType().getCode())) { // if
//				permissibleCoverageValue = getPermissibleCoverageForGovernment(plotArea, developmentZone,
//						noOfFloors);
//			} else if (G.equals(mostRestrictiveOccupancy.getType().getCode())) { // if
//				permissibleCoverageValue = getPermissibleCoverageForIndustrial();
//			}
		

		if (permissibleCoverageValue.compareTo(BigDecimal.valueOf(0)) > 0
				&& A.equals(mostRestrictiveOccupancy.getType().getCode())
				) {
			//if (occupancyList != null && occupancyList.size() > 1) {
				processCoverage(pl,mostRestrictiveOccupancy.getType().getName(), totalCoverage, permissibleCoverageValue);
		//	}
//			else if (A.equals(mostRestrictiveOccupancy.getType().getCode())
//					|| F.equals(mostRestrictiveOccupancy.getType().getCode())) {
//
//			} 
//			else {
//				processCoverage(pl, mostRestrictiveOccupancy.getType().getName(), totalCoverage,
//						permissibleCoverageValue);
//			}
		}

//		if (roadWidth != null && roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) >= 0
//				&& roadWidth.compareTo(ROAD_WIDTH_THIRTY_POINTFIVE) <= 0) {
//
//			processCoverage(pl, StringUtils.EMPTY, totalCoverage, permissibleCoverageValue);
//		}

		return pl;
	}

//	private BigDecimal getPermissibleCoverage(OccupancyType type, BigDecimal area) {

	/*
	 * to get coverage permissible value for Residential
	 */
	private BigDecimal getPermissibleCoverageForResidential(BigDecimal plotArea, String coreArea) {
		LOG.info("inside getPermissibleCoverageForResidential()");
		BigDecimal permissibleCoverage = BigDecimal.ZERO;

		if(coreArea.equalsIgnoreCase("Yes")) {
			permissibleCoverage = BigDecimal.valueOf(90);
		}else {
		if (plotArea.compareTo(BigDecimal.valueOf(150)) <= 0) {
            permissibleCoverage = BigDecimal.valueOf(90); // 90% coverage for plot area up to 150 sqm
//            Log.info("permissibleCoverage: for plotare: "+plotArea +"is: "+ permissibleCoverage);
//        
//            Log.info("permissibleCoverage: for plotare: "+plotArea +"is: "+ permissibleCoverage);
        } else if (plotArea.compareTo(BigDecimal.valueOf(150)) > 0 &&  plotArea.compareTo(BigDecimal.valueOf(200)) <= 0) {
            permissibleCoverage = BigDecimal.valueOf(70); // 70% coverage for plot area 150-200 sqm
         //   Log.info("permissibleCoverage: for plotare: "+plotArea +"is: "+ permissibleCoverage);
        } else if (plotArea.compareTo(BigDecimal.valueOf(200)) > 0 && plotArea.compareTo(BigDecimal.valueOf(300)) <= 0) {
            permissibleCoverage =  BigDecimal.valueOf(65); // 65% coverage for plot area 200-300 sqm
          //  Log.info("permissibleCoverage: for plotare: "+plotArea +"is: "+ permissibleCoverage);
        } else if (plotArea.compareTo(BigDecimal.valueOf(300)) > 0 && plotArea.compareTo(BigDecimal.valueOf(500)) <= 0) {
            permissibleCoverage =  BigDecimal.valueOf(60); // 60% coverage for plot area 300-500 sqm
          //  Log.info("permissibleCoverage: for plotare: "+plotArea +"is: "+ permissibleCoverage);
        } else if (plotArea.compareTo(BigDecimal.valueOf(500)) > 0 && plotArea.compareTo(BigDecimal.valueOf(1000)) <= 0) {
            permissibleCoverage =  BigDecimal.valueOf(50); // 50% coverage for plot area 500-1000 sqm
           // Log.info("permissibleCoverage: for plotare: "+plotArea +"is: "+ permissibleCoverage);
        } else {
            permissibleCoverage =  BigDecimal.valueOf(40); // 40% coverage for plot area above 1000 sqm
          //  Log.info("permissibleCoverage: for plotare: "+plotArea +"is: "+ permissibleCoverage);
        }
		}
		return permissibleCoverage;
	}

	/*
	 * to get coverage permissible value for Commercial
	 */

	private BigDecimal getPermissibleCoverageForCommercial(BigDecimal area, String developmentZone, int noOfFloors) {
		LOG.info("inside getPermissibleCoverageForCommercial()");
		BigDecimal permissibleCoverage = BigDecimal.ZERO;

		if (area.compareTo(BigDecimal.valueOf(1000)) <= 0) {
			permissibleCoverage = BigDecimal.valueOf(60);
		} else if (area.compareTo(BigDecimal.valueOf(1000)) > 0) {
			permissibleCoverage = BigDecimal.valueOf(50);
		}

		return permissibleCoverage;
	}

	private BigDecimal getPermissibleCoverageForMix(BigDecimal area, String developmentZone, int noOfFloors) {
		LOG.info("inside getPermissibleCoverageForCommercial()");
		BigDecimal permissibleCoverage = BigDecimal.ZERO;

		if (area.compareTo(BigDecimal.valueOf(1000)) <= 0) {
			permissibleCoverage = BigDecimal.valueOf(60);
		} else if (area.compareTo(BigDecimal.valueOf(1000)) > 0) {
			permissibleCoverage = BigDecimal.valueOf(50);
		}

		return permissibleCoverage;
	}

	private BigDecimal getPermissibleCoverageForIndustrial() {
		LOG.info("inside getPermissibleCoverageForIndustrial()");

		BigDecimal permissibleCoverage = BigDecimal.ZERO;
		permissibleCoverage = BigDecimal.valueOf(60);

		return permissibleCoverage;
	}

	private BigDecimal getPermissibleCoverageForGovernment(BigDecimal area, String developmentZone, int noOfFloors) {
		LOG.info("inside getPermissibleCoverageForGovernment()");
		BigDecimal permissibleCoverage = BigDecimal.ZERO;

		if (area.compareTo(BigDecimal.valueOf(1000)) <= 0) {
			permissibleCoverage = BigDecimal.valueOf(40);
		} else if (area.compareTo(BigDecimal.valueOf(1000)) > 0) {
			permissibleCoverage = BigDecimal.valueOf(30);
		}

		return permissibleCoverage;
	}

	private void processCoverage(Plan pl, String occupancy, BigDecimal coverage, BigDecimal upperLimit
			) {
		LOG.info("inside processCoverage()");
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Coverage");
		scrutinyDetail.setHeading("Coverage in Percentage");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
	   // scrutinyDetail.addColumnHeading(2, DEVELOPMENT_ZONE);

		scrutinyDetail.addColumnHeading(2, OCCUPANCY);
		  scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);

		if (!(occupancy.equalsIgnoreCase("Residential") || occupancy.equalsIgnoreCase("Mercantile / Commercial"))) {
			scrutinyDetail.addColumnHeading(6, DESCRIPTION);
			//scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
		}

		String desc = getLocaleMessage(RULE_DESCRIPTION_KEY, upperLimit.toString());
		String actualResult = getLocaleMessage(RULE_ACTUAL_KEY, coverage.toString());
		String expectedResult = getLocaleMessage(RULE_EXPECTED_KEY, upperLimit.toString());
		if (coverage.doubleValue() <= upperLimit.doubleValue() || occupancy.equalsIgnoreCase("Residential")
				|| occupancy.equalsIgnoreCase("Mercantile / Commercial")) {
			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, RULE);
		//	details.put(DEVELOPMENT_ZONE, developmentZone);

			details.put(OCCUPANCY, occupancy);
			details.put(PERMISSIBLE, expectedResult);
			details.put(PROVIDED, actualResult);
			details.put(STATUS, Result.Accepted.getResultVal());

			if (!(occupancy.equalsIgnoreCase("Residential") || occupancy.equalsIgnoreCase("Mercantile / Commercial"))) {
				details.put(DESCRIPTION, desc);
				//details.put(PERMISSIBLE, expectedResult);
			}
			scrutinyDetail.getDetail().add(details);
			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

		} else {
			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, RULE);
		//	details.put(DEVELOPMENT_ZONE, developmentZone);
			details.put(OCCUPANCY, occupancy);
			details.put(PERMISSIBLE, expectedResult);
			details.put(PROVIDED, actualResult);
			details.put(STATUS, Result.Not_Accepted.getResultVal());

			if (!(occupancy.equalsIgnoreCase("Residential") || occupancy.equalsIgnoreCase("Mercantile / Commercial"))) {
				details.put(DESCRIPTION, desc);
				//details.put(PERMISSIBLE, expectedResult);
			}
			scrutinyDetail.getDetail().add(details);
			pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

		}

	}

	protected OccupancyType getMostRestrictiveCoverage(EnumSet<OccupancyType> distinctOccupancyTypes) {

		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B1))
			return OccupancyType.OCCUPANCY_B1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B2))
			return OccupancyType.OCCUPANCY_B2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B3))
			return OccupancyType.OCCUPANCY_B3;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_D))
			return OccupancyType.OCCUPANCY_D;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_D1))
			return OccupancyType.OCCUPANCY_D1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_I2))
			return OccupancyType.OCCUPANCY_I2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_I1))
			return OccupancyType.OCCUPANCY_I1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_C))
			return OccupancyType.OCCUPANCY_C;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A1))
			return OccupancyType.OCCUPANCY_A1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A4))
			return OccupancyType.OCCUPANCY_A4;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A2))
			return OccupancyType.OCCUPANCY_A2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_G1))
			return OccupancyType.OCCUPANCY_G1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_E))
			return OccupancyType.OCCUPANCY_E;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_F))
			return OccupancyType.OCCUPANCY_F;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_F4))
			return OccupancyType.OCCUPANCY_F4;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_G2))
			return OccupancyType.OCCUPANCY_G2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_H))
			return OccupancyType.OCCUPANCY_H;

		else
			return null;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
