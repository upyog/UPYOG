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

import static org.egov.edcr.constants.DxfFileConstants.*;
//import static org.egov.edcr.constants.DxfFileConstants.A2;
//import static org.egov.edcr.constants.DxfFileConstants.A_AF;
//import static org.egov.edcr.constants.DxfFileConstants.A_FH;
//import static org.egov.edcr.constants.DxfFileConstants.A_R;
//import static org.egov.edcr.constants.DxfFileConstants.A_SA;
//import static org.egov.edcr.constants.DxfFileConstants.B2;
//import static org.egov.edcr.constants.DxfFileConstants.D_A;
//import static org.egov.edcr.constants.DxfFileConstants.D_B;
//import static org.egov.edcr.constants.DxfFileConstants.D_C;
//import static org.egov.edcr.constants.DxfFileConstants.E_CLG;
//import static org.egov.edcr.constants.DxfFileConstants.E_EARC;
//import static org.egov.edcr.constants.DxfFileConstants.E_NS;
//import static org.egov.edcr.constants.DxfFileConstants.E_PS;
//import static org.egov.edcr.constants.DxfFileConstants.E_SACA;
//import static org.egov.edcr.constants.DxfFileConstants.E_SFDAP;
//import static org.egov.edcr.constants.DxfFileConstants.E_SFMC;
//import static org.egov.edcr.constants.DxfFileConstants.F;
//import static org.egov.edcr.constants.DxfFileConstants.H_PP;
//import static org.egov.edcr.constants.DxfFileConstants.M_DFPAB;
//import static org.egov.edcr.constants.DxfFileConstants.M_HOTHC;
//import static org.egov.edcr.constants.DxfFileConstants.M_NAPI;
//import static org.egov.edcr.constants.DxfFileConstants.M_OHF;
//import static org.egov.edcr.constants.DxfFileConstants.M_VH;
//import static org.egov.edcr.constants.DxfFileConstants.S_BH;
//import static org.egov.edcr.constants.DxfFileConstants.S_CA;
//import static org.egov.edcr.constants.DxfFileConstants.S_CRC;
//import static org.egov.edcr.constants.DxfFileConstants.S_ECFG;
//import static org.egov.edcr.constants.DxfFileConstants.S_ICC;
//import static org.egov.edcr.constants.DxfFileConstants.S_MCH;
//import static org.egov.edcr.constants.DxfFileConstants.S_SAS;
//import static org.egov.edcr.constants.DxfFileConstants.S_SC;
//import static org.egov.edcr.constants.DxfFileConstants.G;
//import static org.egov.edcr.constants.DxfFileConstants.G_PHI;
//import static org.egov.edcr.constants.DxfFileConstants.G_NPHI;
//
//import static org.egov.edcr.constants.DxfFileConstants.C;
//import static org.egov.edcr.constants.DxfFileConstants.C_MA;
//import static org.egov.edcr.constants.DxfFileConstants.C_MIP;
//import static org.egov.edcr.constants.DxfFileConstants.C_MOP;
//
//import static org.egov.edcr.constants.DxfFileConstants.J;
//import static org.egov.edcr.constants.DxfFileConstants.J_FS;
//import static org.egov.edcr.constants.DxfFileConstants.J_FCSS;
//import static org.egov.edcr.constants.DxfFileConstants.J_CNG;
//import static org.egov.edcr.constants.DxfFileConstants.A_AIF;

import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.PLOT_AREA;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.edcr.model.EdcrRequest;
import org.egov.common.entity.dcr.helper.ErrorDetail;
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.FarDetails;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.commons.edcr.mdms.filter.MdmsFilter;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.commons.mdms.config.MdmsConfiguration;
import org.egov.commons.mdms.validator.MDMSValidator;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.ProcessPrintHelper;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.egov.commons.edcr.mdms.dataParser.*;

@Service
public class Far extends FeatureProcess {

	private static final Logger LOG = LogManager.getLogger(Far.class);
	
	@Autowired
    private MdmsConfiguration mdmsConfiguration;
	
	@Autowired
    private BpaMdmsUtil bpaMdmsUtil;
	
	@Autowired
    private MDMSValidator mDMSValidator;

	private static final String VALIDATION_NEGATIVE_FLOOR_AREA = "msg.error.negative.floorarea.occupancy.floor";
	private static final String VALIDATION_NEGATIVE_EXISTING_FLOOR_AREA = "msg.error.negative.existing.floorarea.occupancy.floor";
	private static final String VALIDATION_NEGATIVE_BUILTUP_AREA = "msg.error.negative.builtuparea.occupancy.floor";
	private static final String VALIDATION_NEGATIVE_EXISTING_BUILTUP_AREA = "msg.error.negative.existing.builtuparea.occupancy.floor";
	public static final String RULE_31_1 = "31-1";
	public static final String RULE = "4.4.4";

	private static final BigDecimal POINTTWO = BigDecimal.valueOf(0.2);
	private static final BigDecimal POINTFOUR = BigDecimal.valueOf(0.4);
	private static final BigDecimal POINTFIVE = BigDecimal.valueOf(0.5);
	private static final BigDecimal POINTSIX = BigDecimal.valueOf(0.6);
	private static final BigDecimal POINTSEVEN = BigDecimal.valueOf(0.7);
	private static final BigDecimal ONE = BigDecimal.valueOf(1);
	private static final BigDecimal ONE_POINTTWO = BigDecimal.valueOf(1.2);
	private static final BigDecimal ONE_POINTFIVE = BigDecimal.valueOf(1.5);
	private static final BigDecimal ONE_POINTEIGHT = BigDecimal.valueOf(1.8);
	private static final BigDecimal TWO = BigDecimal.valueOf(2);
	private static final BigDecimal TWO_POINTFIVE = BigDecimal.valueOf(2.5);
	private static final BigDecimal THREE = BigDecimal.valueOf(3);
	private static final BigDecimal THREE_POINTTWOFIVE = BigDecimal.valueOf(3.25);
	private static final BigDecimal THREE_POINTFIVE = BigDecimal.valueOf(3.5);
	private static final BigDecimal FIFTEEN = BigDecimal.valueOf(15);

	private static final BigDecimal ROAD_WIDTH_TWO_POINTFOUR = BigDecimal.valueOf(2.4);
	private static final BigDecimal ROAD_WIDTH_TWO_POINTFOURFOUR = BigDecimal.valueOf(2.44);
	private static final BigDecimal ROAD_WIDTH_THREE_POINTSIX = BigDecimal.valueOf(3.6);
	private static final BigDecimal ROAD_WIDTH_FOUR_POINTEIGHT = BigDecimal.valueOf(4.8);
	private static final BigDecimal ROAD_WIDTH_SIX_POINTONE = BigDecimal.valueOf(6.1);
	private static final BigDecimal ROAD_WIDTH_NINE_POINTONE = BigDecimal.valueOf(9.1);
	private static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);

	private static final BigDecimal ROAD_WIDTH_EIGHTEEN_POINTTHREE = BigDecimal.valueOf(18.3);
	private static final BigDecimal ROAD_WIDTH_TWENTYFOUR_POINTFOUR = BigDecimal.valueOf(24.4);
	private static final BigDecimal ROAD_WIDTH_TWENTYSEVEN_POINTFOUR = BigDecimal.valueOf(27.4);
	private static final BigDecimal ROAD_WIDTH_THIRTY_POINTFIVE = BigDecimal.valueOf(30.5);

	public static final String OLD = "OLD";
	public static final String NEW = "NEW";
	public static final String OLD_AREA_ERROR = "road width old area";
	public static final String NEW_AREA_ERROR = "road width new area";
	public static final String OLD_AREA_ERROR_MSG = "No construction shall be permitted if the road width is less than 2.4m for old area.";
	public static final String NEW_AREA_ERROR_MSG = "No construction shall be permitted if the road width is less than 6.1m for new area.";

	// Constants for Residential FAR Added by Bimal Kumar
	public static final BigDecimal FAR_UP_TO_2_00 = new BigDecimal("2.00");
	public static final BigDecimal FAR_UP_TO_1_90 = new BigDecimal("1.90");
	public static final BigDecimal FAR_UP_TO_1_75 = new BigDecimal("1.75");
	public static final BigDecimal FAR_UP_TO_1_65 = new BigDecimal("1.65");
	public static final BigDecimal FAR_UP_TO_1_50 = new BigDecimal("1.50");
	public static final BigDecimal FAR_UP_TO_1_25 = new BigDecimal("1.25");

	// Plot Area Categories (Integer Values) Added by Bimal Kumar on 11 March 2024
	// for residential far updation
	public static final BigDecimal PLOT_AREA_UP_TO_100_SQM = new BigDecimal("100");
	public static final BigDecimal PLOT_AREA_100_150_SQM = new BigDecimal("150");
	public static final BigDecimal PLOT_AREA_150_200_SQM = new BigDecimal("200");
	public static final BigDecimal PLOT_AREA_200_300_SQM = new BigDecimal("300");
	public static final BigDecimal PLOT_AREA_300_500_SQM = new BigDecimal("500");
	public static final BigDecimal PLOT_AREA_500_1000_SQM = new BigDecimal("1000");
	public static final BigDecimal PLOT_AREA_ABOVE_1000_SQM = new BigDecimal(Integer.MAX_VALUE); // Use an appropriate
																									// upper bound

	public static final BigDecimal EXCLUDE_BALCONY_WIDTH_ABOVE_4_FEET_FROM_FAR = new BigDecimal("0.91");
	
	// Constants for Commercial
	private static final BigDecimal COMMERCIAL_PLOTAREA_LIMIT_41_82   = BigDecimal.valueOf(41.82);
	private static final BigDecimal COMMERCIAL_PLOTAREA_LIMIT_104_5   = BigDecimal.valueOf(104.5);
	private static final BigDecimal COMMERCIAL_PLOTAREA_LIMIT_209     = BigDecimal.valueOf(209);
	private static final BigDecimal COMMERCIAL_PLOTAREA_LIMIT_418_21  = BigDecimal.valueOf(418.21);

	private static final BigDecimal COMMERCIAL_FAR_1_50 = BigDecimal.valueOf(1.50);
	private static final BigDecimal COMMERCIAL_FAR_1_75 = BigDecimal.valueOf(1.75);
	private static final BigDecimal COMMERCIAL_FAR_2_00 = BigDecimal.valueOf(2.00);
	private static final BigDecimal COMMERCIAL_FAR_2_50 = BigDecimal.valueOf(2.50);
	private static final BigDecimal COMMERCIAL_FAR_3_00 = BigDecimal.valueOf(3.00);
	
	// Constants for Industrial FAR
	private static final BigDecimal INDUSTRIAL_FAR_1_50 = BigDecimal.valueOf(1.50);
	private static final BigDecimal INDUSTRIAL_FAR_3_00 = BigDecimal.valueOf(3.00);
	private static final BigDecimal INDUSTRIAL_FAR_2_50 = BigDecimal.valueOf(2.50);

	// Industrial Plot Area Limits
	private static final BigDecimal INDUSTRIAL_PLOTAREA_LIMIT_300 = BigDecimal.valueOf(300);
	private static final BigDecimal INDUSTRIAL_PLOTAREA_LIMIT_2000 = BigDecimal.valueOf(2000);
	private static final BigDecimal INDUSTRIAL_PLOTAREA_LIMIT_10000 = BigDecimal.valueOf(10000);
	
	private static final BigDecimal ROAD_WIDTH_18 = BigDecimal.valueOf(18);
	private static final BigDecimal ROAD_WIDTH_24 = BigDecimal.valueOf(24);
	private static final BigDecimal ROAD_WIDTH_45 = BigDecimal.valueOf(45);

	private static final BigDecimal FAR_2 = BigDecimal.valueOf(2.00);
	private static final BigDecimal FAR_3 = BigDecimal.valueOf(3.00);
	
	@Override
	public Plan validate(Plan pl) {
		if (pl.getPlot() == null || (pl.getPlot() != null
				&& (pl.getPlot().getArea() == null || pl.getPlot().getArea().doubleValue() == 0))) {
			pl.addError(PLOT_AREA, getLocaleMessage(OBJECTNOTDEFINED, PLOT_AREA));
			
		}
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		decideNocIsRequired(pl);
		HashMap<String, String> errorMsgs = new HashMap<>();
		int errors = pl.getErrors().size();
		LOG.info("hi inside process");
		validate(pl);
		
		LOG.info("plotarea : " + pl.getPlot().getArea());
		
		int validatedErrors = pl.getErrors().size();
		if (validatedErrors > errors) {
			LOG.info("hi inside error");
			LOG.error("error" + pl.getErrors().get(PLOT_AREA));
			return pl;
		}
		BigDecimal totalExistingBuiltUpArea = BigDecimal.ZERO;
		BigDecimal totalExistingFloorArea = BigDecimal.ZERO;
		BigDecimal totalBuiltUpArea = BigDecimal.ZERO;
		BigDecimal totalFloorArea = BigDecimal.ZERO;
		BigDecimal totalCarpetArea = BigDecimal.ZERO;
		BigDecimal totalExistingCarpetArea = BigDecimal.ZERO;
		Set<OccupancyTypeHelper> distinctOccupancyTypesHelper = new HashSet<>();
		for (Block blk : pl.getBlocks()) {
			BigDecimal flrArea = BigDecimal.ZERO;
			BigDecimal bltUpArea = BigDecimal.ZERO;
			BigDecimal existingFlrArea = BigDecimal.ZERO;
			BigDecimal existingBltUpArea = BigDecimal.ZERO;
			BigDecimal carpetArea = BigDecimal.ZERO;
			BigDecimal existingCarpetArea = BigDecimal.ZERO;
			Building building = blk.getBuilding();
			for (Floor flr : building.getFloors()) {
				for (Occupancy occupancy : flr.getOccupancies()) {
					validate2(pl, blk, flr, occupancy);
					/*
					 * occupancy.setCarpetArea(occupancy.getFloorArea().multiply
					 * (BigDecimal.valueOf(0.80))); occupancy
					 * .setExistingCarpetArea(occupancy.getExistingFloorArea().
					 * multiply(BigDecimal.valueOf(0.80)));
					 */
					if(!flr.getIsStiltFloor()) {
						bltUpArea = bltUpArea.add(
								occupancy.getBuiltUpArea() == null ? BigDecimal.valueOf(0) : occupancy.getBuiltUpArea());
						existingBltUpArea = existingBltUpArea
								.add(occupancy.getExistingBuiltUpArea() == null ? BigDecimal.valueOf(0)
										: occupancy.getExistingBuiltUpArea());
						flrArea = flrArea.add(occupancy.getFloorArea());
						existingFlrArea = existingFlrArea.add(occupancy.getExistingFloorArea());
						carpetArea = carpetArea.add(occupancy.getCarpetArea());
						existingCarpetArea = existingCarpetArea.add(occupancy.getExistingCarpetArea());
					}
					
				}
			}
			building.setTotalFloorArea(flrArea);
			building.setTotalBuitUpArea(bltUpArea);
			building.setTotalExistingBuiltUpArea(existingBltUpArea);
			building.setTotalExistingFloorArea(existingFlrArea);

			// check block is completely existing building or not.
			if (existingBltUpArea.compareTo(bltUpArea) == 0)
				blk.setCompletelyExisting(Boolean.TRUE);

			totalFloorArea = totalFloorArea.add(flrArea);
			totalBuiltUpArea = totalBuiltUpArea.add(bltUpArea);
			totalExistingBuiltUpArea = totalExistingBuiltUpArea.add(existingBltUpArea);
			totalExistingFloorArea = totalExistingFloorArea.add(existingFlrArea);
			totalCarpetArea = totalCarpetArea.add(carpetArea);
			totalExistingCarpetArea = totalExistingCarpetArea.add(existingCarpetArea);

			// Find Occupancies by block and add
			Set<OccupancyTypeHelper> occupancyByBlock = new HashSet<>();
			for (Floor flr : building.getFloors()) {
				for (Occupancy occupancy : flr.getOccupancies()) {
					if (occupancy.getTypeHelper() != null)
						occupancyByBlock.add(occupancy.getTypeHelper());
				}
			}

			List<Map<String, Object>> listOfMapOfAllDtls = new ArrayList<>();
			List<OccupancyTypeHelper> listOfOccupancyTypes = new ArrayList<>();

			for (OccupancyTypeHelper occupancyType : occupancyByBlock) {

				Map<String, Object> allDtlsMap = new HashMap<>();
				BigDecimal blockWiseFloorArea = BigDecimal.ZERO;
				BigDecimal blockWiseBuiltupArea = BigDecimal.ZERO;
				BigDecimal blockWiseExistingFloorArea = BigDecimal.ZERO;
				BigDecimal blockWiseExistingBuiltupArea = BigDecimal.ZERO;
				for (Floor flr : blk.getBuilding().getFloors()) {
					for (Occupancy occupancy : flr.getOccupancies()) {
						if (occupancyType.getType() != null && occupancyType.getType().getCode() != null
								&& occupancy.getTypeHelper() != null && occupancy.getTypeHelper().getType() != null
								&& occupancy.getTypeHelper().getType().getCode() != null && occupancy.getTypeHelper()
										.getType().getCode().equals(occupancyType.getType().getCode())) {
							if(!flr.getIsStiltFloor()) {
								blockWiseFloorArea = blockWiseFloorArea.add(occupancy.getFloorArea());
								blockWiseBuiltupArea = blockWiseBuiltupArea
										.add(occupancy.getBuiltUpArea() == null ? BigDecimal.valueOf(0)
												: occupancy.getBuiltUpArea());
								blockWiseExistingFloorArea = blockWiseExistingFloorArea
										.add(occupancy.getExistingFloorArea());
								blockWiseExistingBuiltupArea = blockWiseExistingBuiltupArea
										.add(occupancy.getExistingBuiltUpArea() == null ? BigDecimal.valueOf(0)
												: occupancy.getExistingBuiltUpArea());
							}
							

						}
					}
				}
				Occupancy occupancy = new Occupancy();
				occupancy.setBuiltUpArea(blockWiseBuiltupArea);
				occupancy.setFloorArea(blockWiseFloorArea);
				occupancy.setExistingFloorArea(blockWiseExistingFloorArea);
				occupancy.setExistingBuiltUpArea(blockWiseExistingBuiltupArea);
				occupancy.setCarpetArea(blockWiseFloorArea.multiply(BigDecimal.valueOf(.80)));
				occupancy.setTypeHelper(occupancyType);
				building.getTotalArea().add(occupancy);

				allDtlsMap.put("occupancy", occupancyType);
				allDtlsMap.put("totalFloorArea", blockWiseFloorArea);
				allDtlsMap.put("totalBuiltUpArea", blockWiseBuiltupArea);
				allDtlsMap.put("existingFloorArea", blockWiseExistingFloorArea);
				allDtlsMap.put("existingBuiltUpArea", blockWiseExistingBuiltupArea);

				listOfOccupancyTypes.add(occupancyType);

				listOfMapOfAllDtls.add(allDtlsMap);
			}
			Set<OccupancyTypeHelper> setOfOccupancyTypes = new HashSet<>(listOfOccupancyTypes);

			List<Occupancy> listOfOccupanciesOfAParticularblock = new ArrayList<>();
			// for each distinct converted occupancy types
			for (OccupancyTypeHelper occupancyType : setOfOccupancyTypes) {
				if (occupancyType != null) {
					Occupancy occupancy = new Occupancy();
					BigDecimal totalFlrArea = BigDecimal.ZERO;
					BigDecimal totalBltUpArea = BigDecimal.ZERO;
					BigDecimal totalExistingFlrArea = BigDecimal.ZERO;
					BigDecimal totalExistingBltUpArea = BigDecimal.ZERO;

					for (Map<String, Object> dtlsMap : listOfMapOfAllDtls) {
						if (occupancyType.equals(dtlsMap.get("occupancy"))) {
							totalFlrArea = totalFlrArea.add((BigDecimal) dtlsMap.get("totalFloorArea"));
							totalBltUpArea = totalBltUpArea.add((BigDecimal) dtlsMap.get("totalBuiltUpArea"));

							totalExistingBltUpArea = totalExistingBltUpArea
									.add((BigDecimal) dtlsMap.get("existingBuiltUpArea"));
							totalExistingFlrArea = totalExistingFlrArea
									.add((BigDecimal) dtlsMap.get("existingFloorArea"));

						}
					}
					occupancy.setTypeHelper(occupancyType);
					occupancy.setBuiltUpArea(totalBltUpArea);
					occupancy.setFloorArea(totalFlrArea);
					occupancy.setExistingBuiltUpArea(totalExistingBltUpArea);
					occupancy.setExistingFloorArea(totalExistingFlrArea);
					occupancy.setExistingCarpetArea(totalExistingFlrArea.multiply(BigDecimal.valueOf(0.80)));
					occupancy.setCarpetArea(totalFlrArea.multiply(BigDecimal.valueOf(0.80)));

					listOfOccupanciesOfAParticularblock.add(occupancy);
				}
			}
			blk.getBuilding().setOccupancies(listOfOccupanciesOfAParticularblock);

			if (!listOfOccupanciesOfAParticularblock.isEmpty()) {
				// listOfOccupanciesOfAParticularblock already converted. In
				// case of professional building type, converted into A1
				// type
				boolean singleFamilyBuildingTypeOccupancyPresent = false;
				boolean otherThanSingleFamilyOccupancyTypePresent = false;

				for (Occupancy occupancy : listOfOccupanciesOfAParticularblock) {
					if (occupancy.getTypeHelper().getSubtype() != null
							&& A_R.equals(occupancy.getTypeHelper().getSubtype().getCode()))
						singleFamilyBuildingTypeOccupancyPresent = true;
					else {
						otherThanSingleFamilyOccupancyTypePresent = true;
						break;
					}
				}
				blk.setSingleFamilyBuilding(
						!otherThanSingleFamilyOccupancyTypePresent && singleFamilyBuildingTypeOccupancyPresent);
				int allResidentialOccTypes = 0;
				int allResidentialOrCommercialOccTypes = 0;

				for (Occupancy occupancy : listOfOccupanciesOfAParticularblock) {
					if (occupancy.getTypeHelper() != null && occupancy.getTypeHelper().getType() != null) {
						// setting residentialBuilding
						int residentialOccupancyType = 0;
						if (A.equals(occupancy.getTypeHelper().getType().getCode())) {
							residentialOccupancyType = 1;
						}
						if (residentialOccupancyType == 0) {
							allResidentialOccTypes = 0;
							break;
						} else {
							allResidentialOccTypes = 1;
						}
					}
				}
				blk.setResidentialBuilding(allResidentialOccTypes == 1);
				for (Occupancy occupancy : listOfOccupanciesOfAParticularblock) {
					if (occupancy.getTypeHelper() != null && occupancy.getTypeHelper().getType() != null) {
						// setting residentialOrCommercial Occupancy Type
						int residentialOrCommercialOccupancyType = 0;
						if (A.equals(occupancy.getTypeHelper().getType().getCode())
								|| F.equals(occupancy.getTypeHelper().getType().getCode())) {
							residentialOrCommercialOccupancyType = 1;
						}
						if (residentialOrCommercialOccupancyType == 0) {
							allResidentialOrCommercialOccTypes = 0;
							break;
						} else {
							allResidentialOrCommercialOccTypes = 1;
						}
					}
				}
				blk.setResidentialOrCommercialBuilding(allResidentialOrCommercialOccTypes == 1);
			}

			if (blk.getBuilding().getFloors() != null && !blk.getBuilding().getFloors().isEmpty()) {
				BigDecimal noOfFloorsAboveGround = BigDecimal.ZERO;
				for (Floor floor : blk.getBuilding().getFloors()) {
					if (floor.getNumber() != null && floor.getNumber() >= 0) {
						noOfFloorsAboveGround = noOfFloorsAboveGround.add(BigDecimal.valueOf(1));
					}
				}

				boolean hasTerrace = blk.getBuilding().getFloors().stream()
						.anyMatch(floor -> floor.getTerrace().equals(Boolean.TRUE));

				noOfFloorsAboveGround = hasTerrace ? noOfFloorsAboveGround.subtract(BigDecimal.ONE)
						: noOfFloorsAboveGround;

				blk.getBuilding().setMaxFloor(noOfFloorsAboveGround);
				blk.getBuilding().setFloorsAboveGround(noOfFloorsAboveGround);
				blk.getBuilding().setTotalFloors(BigDecimal.valueOf(blk.getBuilding().getFloors().size()));
			}

		}

		for (Block blk : pl.getBlocks()) {
			Building building = blk.getBuilding();
			List<OccupancyTypeHelper> blockWiseOccupancyTypes = new ArrayList<>();
			for (Occupancy occupancy : blk.getBuilding().getOccupancies()) {
				if (occupancy.getTypeHelper() != null)
					blockWiseOccupancyTypes.add(occupancy.getTypeHelper());
			}
			Set<OccupancyTypeHelper> setOfBlockDistinctOccupancyTypes = new HashSet<>(blockWiseOccupancyTypes);
			OccupancyTypeHelper mostRestrictiveFar = getMostRestrictiveFar(setOfBlockDistinctOccupancyTypes);
			blk.getBuilding().setMostRestrictiveFarHelper(mostRestrictiveFar);

			for (Floor flr : building.getFloors()) {
				BigDecimal flrArea = BigDecimal.ZERO;
				BigDecimal existingFlrArea = BigDecimal.ZERO;
				BigDecimal carpetArea = BigDecimal.ZERO;
				BigDecimal existingCarpetArea = BigDecimal.ZERO;
				BigDecimal existingBltUpArea = BigDecimal.ZERO;
				for (Occupancy occupancy : flr.getOccupancies()) {
					flrArea = flrArea.add(occupancy.getFloorArea());
					existingFlrArea = existingFlrArea.add(occupancy.getExistingFloorArea());
					carpetArea = carpetArea.add(occupancy.getCarpetArea());
					existingCarpetArea = existingCarpetArea.add(occupancy.getExistingCarpetArea());
				}

				List<Occupancy> occupancies = flr.getOccupancies();
				for (Occupancy occupancy : occupancies) {
					existingBltUpArea = existingBltUpArea
							.add(occupancy.getExistingBuiltUpArea() != null ? occupancy.getExistingBuiltUpArea()
									: BigDecimal.ZERO);
				}

				if (mostRestrictiveFar != null && mostRestrictiveFar.getConvertedSubtype() != null
						&& !A_R.equals(mostRestrictiveFar.getSubtype().getCode())) {
					if (carpetArea.compareTo(BigDecimal.ZERO) == 0) {
						pl.addError("Carpet area in block " + blk.getNumber() + "floor " + flr.getNumber(),
								"Carpet area is not defined in block " + blk.getNumber() + "floor " + flr.getNumber());
					}

					if (existingBltUpArea.compareTo(BigDecimal.ZERO) > 0
							&& existingCarpetArea.compareTo(BigDecimal.ZERO) == 0) {
						pl.addError("Existing Carpet area in block " + blk.getNumber() + "floor " + flr.getNumber(),
								"Existing Carpet area is not defined in block " + blk.getNumber() + "floor "
										+ flr.getNumber());
					}
				}

				if (flrArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
						.compareTo(carpetArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
								DcrConstants.ROUNDMODE_MEASUREMENTS)) < 0) {
					pl.addError("Floor area in block " + blk.getNumber() + "floor " + flr.getNumber(),
							"Floor area is less than carpet area in block " + blk.getNumber() + "floor "
									+ flr.getNumber());
				}

				if (existingBltUpArea.compareTo(BigDecimal.ZERO) > 0 && existingFlrArea
						.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
						.compareTo(existingCarpetArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
								DcrConstants.ROUNDMODE_MEASUREMENTS)) < 0) {
					pl.addError("Existing floor area in block " + blk.getNumber() + "floor " + flr.getNumber(),
							"Existing Floor area is less than carpet area in block " + blk.getNumber() + "floor "
									+ flr.getNumber());
				}
			}
		}

		List<OccupancyTypeHelper> plotWiseOccupancyTypes = new ArrayList<>();
		for (Block block : pl.getBlocks()) {
			for (Occupancy occupancy : block.getBuilding().getOccupancies()) {
				if (occupancy.getTypeHelper() != null)
					plotWiseOccupancyTypes.add(occupancy.getTypeHelper());
			}
		}

		Set<OccupancyTypeHelper> setOfDistinctOccupancyTypes = new HashSet<>(plotWiseOccupancyTypes);

		distinctOccupancyTypesHelper.addAll(setOfDistinctOccupancyTypes);

		List<Occupancy> occupanciesForPlan = new ArrayList<>();

		for (OccupancyTypeHelper occupancyType : setOfDistinctOccupancyTypes) {
			if (occupancyType != null) {
				BigDecimal totalFloorAreaForAllBlks = BigDecimal.ZERO;
				BigDecimal totalBuiltUpAreaForAllBlks = BigDecimal.ZERO;
				BigDecimal totalCarpetAreaForAllBlks = BigDecimal.ZERO;
				BigDecimal totalExistBuiltUpAreaForAllBlks = BigDecimal.ZERO;
				BigDecimal totalExistFloorAreaForAllBlks = BigDecimal.ZERO;
				BigDecimal totalExistCarpetAreaForAllBlks = BigDecimal.ZERO;
				Occupancy occupancy = new Occupancy();
				for (Block block : pl.getBlocks()) {
					for (Occupancy buildingOccupancy : block.getBuilding().getOccupancies()) {
						if (occupancyType.equals(buildingOccupancy.getTypeHelper())) {
							totalFloorAreaForAllBlks = totalFloorAreaForAllBlks.add(buildingOccupancy.getFloorArea());
							totalBuiltUpAreaForAllBlks = totalBuiltUpAreaForAllBlks
									.add(buildingOccupancy.getBuiltUpArea());
							totalCarpetAreaForAllBlks = totalCarpetAreaForAllBlks
									.add(buildingOccupancy.getCarpetArea());
							totalExistBuiltUpAreaForAllBlks = totalExistBuiltUpAreaForAllBlks
									.add(buildingOccupancy.getExistingBuiltUpArea());
							totalExistFloorAreaForAllBlks = totalExistFloorAreaForAllBlks
									.add(buildingOccupancy.getExistingFloorArea());
							totalExistCarpetAreaForAllBlks = totalExistCarpetAreaForAllBlks
									.add(buildingOccupancy.getExistingCarpetArea());
						}
					}
				}
				occupancy.setTypeHelper(occupancyType);
				occupancy.setBuiltUpArea(totalBuiltUpAreaForAllBlks);
				occupancy.setCarpetArea(totalCarpetAreaForAllBlks);
				occupancy.setFloorArea(totalFloorAreaForAllBlks);
				occupancy.setExistingBuiltUpArea(totalExistBuiltUpAreaForAllBlks);
				occupancy.setExistingFloorArea(totalExistFloorAreaForAllBlks);
				occupancy.setExistingCarpetArea(totalExistCarpetAreaForAllBlks);
				occupanciesForPlan.add(occupancy);
			}
		}

		pl.setOccupancies(occupanciesForPlan);
		pl.getVirtualBuilding().setTotalFloorArea(totalFloorArea); //pl.getPlot().getArea()
		pl.getVirtualBuilding().setTotalFloorArea(pl.getPlot().getArea());
		pl.getVirtualBuilding().setTotalCarpetArea(totalCarpetArea);
		pl.getVirtualBuilding().setTotalExistingBuiltUpArea(totalExistingBuiltUpArea);
		pl.getVirtualBuilding().setTotalExistingFloorArea(totalExistingFloorArea);
		pl.getVirtualBuilding().setTotalExistingCarpetArea(totalExistingCarpetArea);
		pl.getVirtualBuilding().setOccupancyTypes(distinctOccupancyTypesHelper);
		pl.getVirtualBuilding().setTotalBuitUpArea(totalBuiltUpArea);
		pl.getVirtualBuilding().setMostRestrictiveFarHelper(getMostRestrictiveFar(setOfDistinctOccupancyTypes));

		if (!distinctOccupancyTypesHelper.isEmpty()) {
			int allResidentialOccTypesForPlan = 0;
			for (OccupancyTypeHelper occupancy : distinctOccupancyTypesHelper) {
				//LOG.info("occupancy :" + occupancy.getType().getName());
				// setting residentialBuilding
				int residentialOccupancyType = 0;
				if(occupancy.getType() != null && occupancy.getType().getCode()!=null) {
					if (A.equals(occupancy.getType().getCode())) {
						residentialOccupancyType = 1;
					}
				}
				
				if (residentialOccupancyType == 0) {
					allResidentialOccTypesForPlan = 0;
					break;
				} else {
					allResidentialOccTypesForPlan = 1;
				}
			}
			pl.getVirtualBuilding().setResidentialBuilding(allResidentialOccTypesForPlan == 1);
			int allResidentialOrCommercialOccTypesForPlan = 0;
			for (OccupancyTypeHelper occupancyType : distinctOccupancyTypesHelper) {
				int residentialOrCommercialOccupancyTypeForPlan = 0;
				if (occupancyType.getType() != null && (A.equals(occupancyType.getType().getCode())
						|| F.equals(occupancyType.getType().getCode()))) {
					residentialOrCommercialOccupancyTypeForPlan = 1;
				}
				if (residentialOrCommercialOccupancyTypeForPlan == 0) {
					allResidentialOrCommercialOccTypesForPlan = 0;
					break;
				} else {
					allResidentialOrCommercialOccTypesForPlan = 1;
				}
			}
			pl.getVirtualBuilding().setResidentialOrCommercialBuilding(allResidentialOrCommercialOccTypesForPlan == 1);
		}

		OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
				? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
				: null;

		/*
		 * if (!(pl.getVirtualBuilding().getResidentialOrCommercialBuilding() ||
		 * (mostRestrictiveOccupancy != null && mostRestrictiveOccupancy.getType() !=
		 * null &&
		 * DxfFileConstants.G.equalsIgnoreCase(mostRestrictiveOccupancy.getType().
		 * getCode())))) { pl.getErrors().put(DxfFileConstants.OCCUPANCY_ALLOWED_KEY,
		 * DxfFileConstants.OCCUPANCY_ALLOWED); return pl; }
		 */

		Set<String> occupancyCodes = new HashSet<>();
		for (OccupancyTypeHelper oth : pl.getVirtualBuilding().getOccupancyTypes()) {
			if (oth.getSubtype() != null) {
				occupancyCodes.add(oth.getSubtype().getCode());
			}
		}

		/*
		 * if (occupancyCodes.size() == 1 &&
		 * occupancyCodes.contains(DxfFileConstants.A_PO)) {
		 * pl.getErrors().put(DxfFileConstants.OCCUPANCY_PO_NOT_ALLOWED_KEY,
		 * DxfFileConstants.OCCUPANCY_PO_NOT_ALLOWED); return pl; }
		 */

		OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
		BigDecimal providedFar = BigDecimal.ZERO;
		BigDecimal surrenderRoadArea = BigDecimal.ZERO;

		if (!pl.getSurrenderRoads().isEmpty()) {
			for (Measurement measurement : pl.getSurrenderRoads()) {
				surrenderRoadArea = surrenderRoadArea.add(measurement.getArea());
			}
		}

		pl.setTotalSurrenderRoadArea(surrenderRoadArea.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
				DcrConstants.ROUNDMODE_MEASUREMENTS));
		BigDecimal plotArea = pl.getPlot() != null ? pl.getPlot().getArea().add(surrenderRoadArea) : BigDecimal.ZERO;
		
		updateFarAsPerBalconyWidth(pl);
		
		if (plotArea.doubleValue() > 0)
			providedFar = pl.getVirtualBuilding().getTotalBuitUpArea()
							.divide(plotArea, DECIMALDIGITS_MEASUREMENTS,ROUNDMODE_MEASUREMENTS);		
		
		
		
		pl.setFarDetails(new FarDetails());
		pl.getFarDetails().setProvidedFar(providedFar.doubleValue());
		
		
		
		String typeOfArea = pl.getPlanInformation().getTypeOfArea();
		BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();

//		if (mostRestrictiveOccupancyType != null && StringUtils.isNotBlank(typeOfArea) && roadWidth != null
//				&& !processFarForSpecialOccupancy(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth,
//						errorMsgs)) {
		if (mostRestrictiveOccupancyType != null && StringUtils.isNotBlank(typeOfArea) && roadWidth != null) {
			// getting Rules from mdms
			getMdmsMastersData(pl, mostRestrictiveOccupancyType.getType().getCode(), mostRestrictiveOccupancyType);
			
			if ((mostRestrictiveOccupancyType.getType() != null
					&& DxfFileConstants.A.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode()))
					|| (mostRestrictiveOccupancyType.getSubtype() != null
							&& (A_R.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())
									|| A_AF.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())
									|| A_FH.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())
									))) {				

				processFarResidential(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth, errorMsgs,
						plotArea);
			}
			if (mostRestrictiveOccupancyType.getType() != null
					&& (DxfFileConstants.G.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())
							|| DxfFileConstants.B.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())
							//|| DxfFileConstants.D.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())
							)) {
				processFarForGBDOccupancy(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth,
						errorMsgs, plotArea);
			}
//			if (mostRestrictiveOccupancyType.getType() != null
//					&& DxfFileConstants.I.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//				processFarHaazardous(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth, errorMsgs);
//			}
			if (mostRestrictiveOccupancyType.getType() != null
					&& DxfFileConstants.F.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//				processFarNonResidential(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth,
//						errorMsgs,plotArea);
				processFarCommercial(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth, errorMsgs, plotArea);
			}
			if (mostRestrictiveOccupancyType.getType() != null
					&& DxfFileConstants.L.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
				processFarPublicBuilding(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth,
						errorMsgs,plotArea);
			}
			//if (mostRestrictiveOccupancyType.getType() != null
				//	&& DxfFileConstants.C.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode())) {
//				processFarHospital(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth,
//						errorMsgs,plotArea);
			//}
//			if ((mostRestrictiveOccupancyType.getType() != null
//			        && DxfFileConstants.J.equalsIgnoreCase(mostRestrictiveOccupancyType.getType().getCode()))
//			    || (mostRestrictiveOccupancyType.getSubtype() != null
//			        && (J_FS.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())
//			            || J_FCSS.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())
//			            || J_CNG.equalsIgnoreCase(mostRestrictiveOccupancyType.getSubtype().getCode())))) {
//
//			    processFarPetrolPump(pl, mostRestrictiveOccupancyType, providedFar, typeOfArea, roadWidth,
//			            errorMsgs, plotArea);
//			}
		}
		
		//getFarDetailsFromMDMS(pl,mostRestrictiveOccupancyType.getType().getCode());
		
		// processing the deduction
		splitDeductionFloorWise(pl);
		
		ProcessPrintHelper.print(pl);
		//getFarDetailsFromMDMS(pl, );
		return pl;
	}

	private void splitDeductionFloorWise(Plan pl) {
	    LOG.info("Inside splitDeductionFloorWise() method for optimized reporting and formatting");

	    List<Map<String, String>> allDeductionRecords = new ArrayList<>();

	    // Regular Expression to capture the Block Number, Floor Number, and Deduction Type
	    final String REGEX = "BLK_(\\d+)_FLR_(-?\\d+)_BLT_UP_AREA_DEDUCT_(.+)";
	    final Pattern pattern = Pattern.compile(REGEX);
	    
	    // Iterate through all Blocks
	    for (Block blk : pl.getBlocks()) {
	        String blockNumber = String.valueOf(blk.getNumber()); // Ensure block number is treated as a String if needed for the Map key
	        
	        LOG.info("\n=======================================================");
	        LOG.info("Block " + blockNumber + " Deduction (Built-Up Area Deductions)");
	        LOG.info("=======================================================");

	        if (blk.getBuilding() == null || blk.getBuilding().getFloors().isEmpty()) {
	            LOG.info("  Block " + blockNumber + " has no floors. Skipping.");
	            continue;
	        }
	        
	        LOG.info("Block " + blockNumber + " Deductions");

	        Map<Integer, Floor> sortedFloors = new TreeMap<>();
	        for (Floor flr : blk.getBuilding().getFloors()) {
	            sortedFloors.put(flr.getNumber(), flr);
	        }

	        for (Map.Entry<Integer, Floor> entry : sortedFloors.entrySet()) {
	            Floor flr = entry.getValue();
	            String floorNumber = String.valueOf(flr.getNumber());
	            
	            Map<String, BigDecimal> deductionMap = flr.getFloordeductions();

	            if (deductionMap == null || deductionMap.isEmpty()) {
	                continue; 
	            }
	           
	            Map<String, BigDecimal> floorDeductionTypes = new TreeMap<>();	            
	           
	            deductionMap.forEach((layerKey, area) -> {
	                Matcher matcher = pattern.matcher(layerKey);
	                if (matcher.matches()) {
	                    String deductionType = matcher.group(3); 	                    
	                   
	                    BigDecimal currentArea = floorDeductionTypes.getOrDefault(deductionType, BigDecimal.ZERO);
	                    floorDeductionTypes.put(deductionType, currentArea.add(area));
	                }
	            });	            
	            
	            if (!floorDeductionTypes.isEmpty()) {
	                LOG.info("Floor No : " + floorNumber);
	                
	                // Iterate through the finalized, grouped deductions for the current floor
	                floorDeductionTypes.forEach((type, area) -> {
	                    // Apply the scale and rounding mode (formatting)
	                    BigDecimal formattedArea = area.setScale(2, RoundingMode.HALF_UP);
	                    
	                    // 1. CLEAN THE TYPE STRING FOR DISPLAY AND REPORTING
	                    String displayType = cleanDeductionType(type);

	                    // Logging
	                    // Changed log format to display the cleaned type
	                    LOG.info(displayType + " : " + formattedArea + " m²"); 
	                    
	                    // *** STORE RECORD TO THE CENTRAL LIST ***
	                    Map<String, String> recordDetails = new HashMap<>();
	                    recordDetails.put("BLOCK", blockNumber);
	                    recordDetails.put("FLOOR", floorNumber);
	                    // 2. USE THE CLEANED TYPE FOR THE REPORT
	                    recordDetails.put("TYPE", displayType); 
	                    recordDetails.put("DEDUCTION", formattedArea.toString() + " m²");
	                    recordDetails.put("STATUS", "Accepted");
	                    allDeductionRecords.add(recordDetails);
	                });
	            }
	        } // End of Floor loop

	    } // End of Block loop
	    
	    // 3. BUILD THE FINAL REPORT ONCE AFTER ALL BLOCKS/FLOORS ARE PROCESSED
	    if (!allDeductionRecords.isEmpty()) {
	        buildFinalDeductionReport(pl, allDeductionRecords);
	    }
	    
	    LOG.info("\n=======================================================");
	    LOG.info("Exiting splitDeductionFloorWise() method");
	}
	
	/**
	 * Cleans the deduction type string (e.g., "LIFT_1") to a readable format (e.g., "LIFT 1").
	 * This assumes the suffix is an underscore followed by a digit (e.g., _1, _2).
	 */
	private String cleanDeductionType(String deductionType) {
	    if (deductionType == null) {
	        return "";
	    }
	    // Regex to find an underscore followed by one or more digits at the end of the string
	    // and replace it with a space and the digits.
	    // Example: "LIFT_1" -> "LIFT 1"
	    // Example: "STAIRCASE" -> "STAIRCASE" (no change)
	    return deductionType.replaceAll("_(\\d+)$", " $1");
	}
	
	private void decideNocIsRequired(Plan pl) {
		Boolean isHighRise = false;
		for (Block b : pl.getBlocks()) {
			if ((b.getBuilding() != null && b.getBuilding().getBuildingHeight() != null
					&& b.getBuilding().getBuildingHeight().compareTo(new BigDecimal(5)) > 0)
					|| (b.getBuilding() != null && b.getBuilding().getCoverageArea() != null
							&& b.getBuilding().getCoverageArea().compareTo(new BigDecimal(500)) > 0)) {
				isHighRise = true;

			}
		}
		if (isHighRise) {
			pl.getPlanInformation().setNocFireDept("YES");
		}

		if (StringUtils.isNotBlank(pl.getPlanInformation().getBuildingNearMonument())
				&& "YES".equalsIgnoreCase(pl.getPlanInformation().getBuildingNearMonument())) {
			BigDecimal minDistanceFromMonument = BigDecimal.ZERO;
			List<BigDecimal> distancesFromMonument = pl.getDistanceToExternalEntity().getMonuments();
			if (!distancesFromMonument.isEmpty()) {

				minDistanceFromMonument = distancesFromMonument.stream().reduce(BigDecimal::min).get();

				if (minDistanceFromMonument.compareTo(BigDecimal.valueOf(300)) > 0) {
					pl.getPlanInformation().setNocNearMonument("YES");
				}
			}

		}

	}

	private void validate2(Plan pl, Block blk, Floor flr, Occupancy occupancy) {
		String occupancyTypeHelper = StringUtils.EMPTY;
		if (occupancy.getTypeHelper() != null) {
			if (occupancy.getTypeHelper().getType() != null) {
				occupancyTypeHelper = occupancy.getTypeHelper().getType().getName();
			} else if (occupancy.getTypeHelper().getSubtype() != null) {
				occupancyTypeHelper = occupancy.getTypeHelper().getSubtype().getName();
			}
		}

		if (occupancy.getBuiltUpArea() != null && occupancy.getBuiltUpArea().compareTo(BigDecimal.valueOf(0)) < 0) {
			pl.addError(VALIDATION_NEGATIVE_BUILTUP_AREA, getLocaleMessage(VALIDATION_NEGATIVE_BUILTUP_AREA,
					blk.getNumber(), flr.getNumber().toString(), occupancyTypeHelper));
		}
		if (occupancy.getExistingBuiltUpArea() != null
				&& occupancy.getExistingBuiltUpArea().compareTo(BigDecimal.valueOf(0)) < 0) {
			pl.addError(VALIDATION_NEGATIVE_EXISTING_BUILTUP_AREA,
					getLocaleMessage(VALIDATION_NEGATIVE_EXISTING_BUILTUP_AREA, blk.getNumber(),
							flr.getNumber().toString(), occupancyTypeHelper));
		}
		occupancy.setFloorArea((occupancy.getBuiltUpArea() == null ? BigDecimal.ZERO : occupancy.getBuiltUpArea())
				.subtract(occupancy.getDeduction() == null ? BigDecimal.ZERO : occupancy.getDeduction()));
		if (occupancy.getFloorArea() != null && occupancy.getFloorArea().compareTo(BigDecimal.valueOf(0)) < 0) {
			pl.addError(VALIDATION_NEGATIVE_FLOOR_AREA, getLocaleMessage(VALIDATION_NEGATIVE_FLOOR_AREA,
					blk.getNumber(), flr.getNumber().toString(), occupancyTypeHelper));
		}
		occupancy.setExistingFloorArea(
				(occupancy.getExistingBuiltUpArea() == null ? BigDecimal.ZERO : occupancy.getExistingBuiltUpArea())
						.subtract(occupancy.getExistingDeduction() == null ? BigDecimal.ZERO
								: occupancy.getExistingDeduction()));
		if (occupancy.getExistingFloorArea() != null
				&& occupancy.getExistingFloorArea().compareTo(BigDecimal.valueOf(0)) < 0) {
			pl.addError(VALIDATION_NEGATIVE_EXISTING_FLOOR_AREA,
					getLocaleMessage(VALIDATION_NEGATIVE_EXISTING_FLOOR_AREA, blk.getNumber(),
							flr.getNumber().toString(), occupancyTypeHelper));
		}
	}

	protected OccupancyTypeHelper getMostRestrictiveFar(Set<OccupancyTypeHelper> distinctOccupancyTypes) {
		Set<String> codes = new HashSet<>();
		Map<String, OccupancyTypeHelper> codesMap = new HashMap<>();
		for (OccupancyTypeHelper typeHelper : distinctOccupancyTypes) {

			if (typeHelper.getType() != null)
				codesMap.put(typeHelper.getType().getCode(), typeHelper);
			if (typeHelper.getSubtype() != null)
				codesMap.put(typeHelper.getSubtype().getCode(), typeHelper);
		}
		codes = codesMap.keySet();
		if (codes.contains(S_ECFG))
			return codesMap.get(S_ECFG);
		else if (codes.contains(A_FH))
			return codesMap.get(A_FH);
		else if (codes.contains(S_SAS))
			return codesMap.get(S_SAS);
		else if (codes.contains(D_B))
			return codesMap.get(D_B);
		else if (codes.contains(D_C))
			return codesMap.get(D_C);
		else if (codes.contains(D_A))
			return codesMap.get(D_A);
		else if (codes.contains(H_PP))
			return codesMap.get(H_PP);
		else if (codes.contains(E_NS))
			return codesMap.get(E_NS);
		else if (codes.contains(M_DFPAB))
			return codesMap.get(M_DFPAB);
		else if (codes.contains(E_PS))
			return codesMap.get(E_PS);
		else if (codes.contains(E_SFMC))
			return codesMap.get(E_SFMC);
		else if (codes.contains(E_SFDAP))
			return codesMap.get(E_SFDAP);
		else if (codes.contains(E_EARC))
			return codesMap.get(E_EARC);
		else if (codes.contains(S_MCH))
			return codesMap.get(S_MCH);
		else if (codes.contains(S_BH))
			return codesMap.get(S_BH);
		else if (codes.contains(S_CRC))
			return codesMap.get(S_CRC);
		else if (codes.contains(S_CA))
			return codesMap.get(S_CA);
		else if (codes.contains(S_SC))
			return codesMap.get(S_SC);
		else if (codes.contains(S_ICC))
			return codesMap.get(S_ICC);
		else if (codes.contains(A2))
			return codesMap.get(A2);
		else if (codes.contains(E_CLG))
			return codesMap.get(E_CLG);
		else if (codes.contains(M_OHF))
			return codesMap.get(M_OHF);
		else if (codes.contains(M_VH))
			return codesMap.get(M_VH);
		else if (codes.contains(M_NAPI))
			return codesMap.get(M_NAPI);
		else if (codes.contains(A_SA))
			return codesMap.get(A_SA);
		else if (codes.contains(M_HOTHC))
			return codesMap.get(M_HOTHC);
		else if (codes.contains(E_SACA))
			return codesMap.get(E_SACA);
		
		else if (codes.contains(C_MA))
			return codesMap.get(C_MA);
		
		else if (codes.contains(C_MIP))
			return codesMap.get(C_MIP);
		
		else if (codes.contains(C_MOP))
			return codesMap.get(C_MOP);
		
//		else if (codes.contains(G))
//			return codesMap.get(G);
		
		else if (codes.contains(F))
			return codesMap.get(F);
		else if (codes.contains(A))
			return codesMap.get(A);
		
		else if (codes.contains(C_MA))
			return codesMap.get(C_MA);
		
		else if (codes.contains(C_MIP))
			return codesMap.get(C_MIP);
		
		else if (codes.contains(C_MOP))
			return codesMap.get(C_MOP);
		
		else if (codes.contains(C))
			return codesMap.get(C);
		
		else if (codes.contains(J_FS))
			return codesMap.get(J_FS);
		
		else if (codes.contains(J_FCSS))
			return codesMap.get(J_FCSS);
		
		else if (codes.contains(J_CNG))
			return codesMap.get(J_CNG);
		
		else if (codes.contains(J))
			return codesMap.get(J);
		
		else if (codes.contains(A_AF))
			return codesMap.get(A_AF);
		
		else if (codes.contains(A_AIF))
			return codesMap.get(A_AIF);		
	
		else if (codes.contains(G_G))
		    return codesMap.get(G_G);

		else if (codes.contains(G_F))
		    return codesMap.get(G_F);

		else if (codes.contains(G_S))
		    return codesMap.get(G_S);

		else if (codes.contains(G_HI))
		    return codesMap.get(G_HI);

		else if (codes.contains(G_WT))
		    return codesMap.get(G_WT);

		else if (codes.contains(G_RSI))
		    return codesMap.get(G_RSI);

		else if (codes.contains(G_GIP))
		    return codesMap.get(G_GIP);

		else if (codes.contains(G_GIF))
		    return codesMap.get(G_GIF);

		else if (codes.contains(G_ITP))
		    return codesMap.get(G_ITP);

		else if (codes.contains(G_ITF))
		    return codesMap.get(G_ITF);

		else if (codes.contains(G_TI))
		    return codesMap.get(G_TI);

		else if (codes.contains(G_KI))
		    return codesMap.get(G_KI);

		else if (codes.contains(G_SI))
		    return codesMap.get(G_SI);
		
		// Public Building (L) Color Codes
		else if (codes.contains(L_GP))
		    return codesMap.get(L_GP);

		else if (codes.contains(L_GO))
		    return codesMap.get(L_GO);

		else if (codes.contains(L_NS))
		    return codesMap.get(L_NS);

		else if (codes.contains(L_PS))
		    return codesMap.get(L_PS);

		else if (codes.contains(L_CO))
		    return codesMap.get(L_CO);

		else if (codes.contains(L_ERC))
		    return codesMap.get(L_ERC);

		else if (codes.contains(L_MP))
		    return codesMap.get(L_MP);

		else if (codes.contains(L_NH))
		    return codesMap.get(L_NH);

		else if (codes.contains(L_C))
		    return codesMap.get(L_C);

		// Mixed Land Use
		else if (codes.contains(R_R))
		    return codesMap.get(R_R);

		
		else if (codes.contains(F_RB))
		    return codesMap.get(F_RB);
		else if (codes.contains(F_HM))
		    return codesMap.get(F_HM);
		else if (codes.contains(F_SCC))
			return codesMap.get(F_SCC);
		else if (codes.contains(F_PO))
		    return codesMap.get(F_PO);
		else if (codes.contains(F_B))
		    return codesMap.get(F_B);
		else if (codes.contains(F_LB))
		    return codesMap.get(F_LB);
		else if (codes.contains(F_D))
		    return codesMap.get(F_D);
		else if (codes.contains(F_CA))
		    return codesMap.get(F_CA);
		else if (codes.contains(F_VGP))
		    return codesMap.get(F_VGP);
		else if (codes.contains(F_BU))
		    return codesMap.get(F_BU);
		else if (codes.contains(F_PFSF))
		    return codesMap.get(F_PFSF);
		else if (codes.contains(F_PFST))
		    return codesMap.get(F_PFST);
		else if (codes.contains(F_PFSS))
		    return codesMap.get(F_PFSS);
		else if (codes.contains(F_PS))
		    return codesMap.get(F_PS);
		else if (codes.contains(F_CNGS))
		    return codesMap.get(F_CNGS);

		
		else
			return null;

	}

	private Boolean processFarForSpecialOccupancy(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far,
			String typeOfArea, BigDecimal roadWidth, HashMap<String, String> errors) {

		OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding() != null
				? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
				: null;
		String expectedResult = StringUtils.EMPTY;
		boolean isAccepted = false;
		if (mostRestrictiveOccupancyType != null && mostRestrictiveOccupancyType.getSubtype() != null) {
			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(S_ECFG)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(A_FH)) {
				isAccepted = far.compareTo(POINTTWO) <= 0;
				expectedResult = "<= 0.2";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(S_SAS)) {
				isAccepted = far.compareTo(POINTFOUR) <= 0;
				expectedResult = "<= 0.4";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(D_B)) {
				isAccepted = far.compareTo(POINTFIVE) <= 0;
				expectedResult = "<= 0.5";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(D_C)) {
				isAccepted = far.compareTo(POINTSIX) <= 0;
				expectedResult = "<= 0.6";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(D_A)) {
				isAccepted = far.compareTo(POINTSEVEN) <= 0;
				expectedResult = "<= 0.7";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(H_PP)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(E_NS)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(M_DFPAB)) {
				isAccepted = far.compareTo(ONE) <= 0;
				expectedResult = "<= 1";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(E_PS)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(E_SFMC)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(E_SFDAP)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(E_EARC)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(S_MCH)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(S_BH)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(S_CRC)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(S_CA)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(S_SC)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(S_ICC)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(A2)) {
				isAccepted = far.compareTo(ONE_POINTTWO) <= 0;
				expectedResult = "<= 1.2";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(B2)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(E_CLG)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(M_OHF)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(M_VH)
					|| mostRestrictiveOccupancyType.getSubtype().getCode().equals(M_NAPI)) {
				isAccepted = far.compareTo(ONE_POINTFIVE) <= 0;
				expectedResult = "<= 1.5";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(A_SA)) {
				isAccepted = far.compareTo(TWO_POINTFIVE) <= 0;
				expectedResult = "<= 2.5";
				return true;
			}

			if (mostRestrictiveOccupancyType.getSubtype().getCode().equals(E_SACA)) {
				isAccepted = far.compareTo(FIFTEEN) <= 0;
				expectedResult = "<= 15";
				return true;
			}

		}

		String occupancyName = occupancyType.getSubtype() != null ? occupancyType.getSubtype().getName()
				: occupancyType.getType().getName();

		if (StringUtils.isNotBlank(expectedResult)) {
			buildResult(pl, occupancyName, far, typeOfArea, roadWidth, expectedResult, isAccepted);
		}

		return false;
	}

	private void processFarPublicBuilding(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far, String typeOfArea,
			BigDecimal roadWidth, HashMap<String, String> errors, BigDecimal plotArea) {
		LOG.info("************* Processing FAR (Public Building) **************");
		LOG.info("Type of area: " + typeOfArea);		
		getFarDetailsFromMDMS(pl, occupancyType.getType().getCode(), typeOfArea, occupancyType);
	}
	
	// Method updated by Bimal Kumar on 14 March 2024
	private void processFarResidential(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far, String typeOfArea,
			BigDecimal roadWidth, HashMap<String, String> errors, BigDecimal plotArea) {

		String expectedResult = StringUtils.EMPTY;
		boolean isAccepted = false;
		
		LOG.info("Processing FAR (Residential) for punjab as per new Bylaws");
		LOG.info("Processing FAR (Residential) for punjab as per new Bylaws");
		LOG.info("Type of area: " + typeOfArea);
		LOG.info("Type of area: " + typeOfArea);
		
		// Start Rule updated by Bimal on 14 March 2024
		
//			if (plotArea.compareTo(BigDecimal.ZERO) < 0) {				
//				if (!shouldSkipValidation(pl.getEdcrRequest(),DcrConstants.EDCR_SKIP_PLOT_AREA)) {
//					errors.put("Plot Area Error:", "Plot area cannot be less than 0.");
//					pl.addErrors(errors);
//                }
//				
//			} else if (plotArea.compareTo(PLOT_AREA_UP_TO_100_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_2_00) <= 0;
//				LOG.info("FAR_UP_TO_2_00: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_2_00.doubleValue());
//				expectedResult = "<= 2.00";
//			} else if (plotArea.compareTo(PLOT_AREA_100_150_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_90) <= 0;
//				LOG.info("FAR_UP_TO_1_90: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_90.doubleValue());
//				expectedResult = "<= 1.90";
//			} else if (plotArea.compareTo(PLOT_AREA_150_200_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_75) <= 0;
//				LOG.info("FAR_UP_TO_1_75: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_75.doubleValue());
//				expectedResult = "<= 1.75";
//			} else if (plotArea.compareTo(PLOT_AREA_200_300_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_65) <= 0;
//				LOG.info("FAR_UP_TO_1_65: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_65.doubleValue());
//				expectedResult = "<= 1.65";
//			} else if (plotArea.compareTo(PLOT_AREA_300_500_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_50) <= 0;
//				LOG.info("FAR_UP_TO_1_50: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_50.doubleValue());
//				expectedResult = "<= 1.50";
//			} else if (plotArea.compareTo(PLOT_AREA_500_1000_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_50) <= 0;
//				LOG.info("FAR_UP_TO_1_50 2nd: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_50.doubleValue());
//				expectedResult = "<= 1.50";
//			} else {
//				isAccepted = far.compareTo(FAR_UP_TO_1_25) <= 0;
//				LOG.info("FAR_UP_TO_1_25: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_25.doubleValue());
//				expectedResult = "<= 1.25";
//			}
		

//		if (typeOfArea.equalsIgnoreCase(NEW)) {
//
//			if (plotArea.compareTo(BigDecimal.ZERO) < 0) {
//				errors.put("Plot Area Error:", "Plot area cannot be less than 0.");
//				pl.addErrors(errors);
//			} else if (plotArea.compareTo(PLOT_AREA_UP_TO_100_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_2_00) <= 0;
//				LOG.info("FAR_UP_TO_2_00: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_2_00.doubleValue());
//				expectedResult = "<= 2.00";
//			} else if (plotArea.compareTo(PLOT_AREA_100_150_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_90) <= 0;
//				LOG.info("FAR_UP_TO_1_90: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_90.doubleValue());
//				expectedResult = "<= 1.90";
//			} else if (plotArea.compareTo(PLOT_AREA_150_200_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_75) <= 0;
//				LOG.info("FAR_UP_TO_1_75: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_75.doubleValue());
//				expectedResult = "<= 1.75";
//			} else if (plotArea.compareTo(PLOT_AREA_200_300_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_65) <= 0;
//				LOG.info("FAR_UP_TO_1_65: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_65.doubleValue());
//				expectedResult = "<= 1.65";
//			} else if (plotArea.compareTo(PLOT_AREA_300_500_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_50) <= 0;
//				LOG.info("FAR_UP_TO_1_50: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_50.doubleValue());
//				expectedResult = "<= 1.50";
//			} else if (plotArea.compareTo(PLOT_AREA_500_1000_SQM) <= 0) {
//				isAccepted = far.compareTo(FAR_UP_TO_1_50) <= 0;
//				LOG.info("FAR_UP_TO_1_50 2nd: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_50.doubleValue());
//				expectedResult = "<= 1.50";
//			} else {
//				isAccepted = far.compareTo(FAR_UP_TO_1_25) <= 0;
//				LOG.info("FAR_UP_TO_1_25: " + isAccepted);
//				pl.getFarDetails().setPermissableFar(FAR_UP_TO_1_25.doubleValue());
//				expectedResult = "<= 1.25";
//			}
//		}
		// End Rule updated by Bimal on 14 March 2024

		// Old rules commented by Bimal on 14 March 2024
		/*
		 * if (typeOfArea.equalsIgnoreCase(OLD)) { if
		 * (roadWidth.compareTo(ROAD_WIDTH_TWO_POINTFOUR) < 0) {
		 * errors.put(OLD_AREA_ERROR, OLD_AREA_ERROR_MSG); pl.addErrors(errors); } else
		 * if (roadWidth.compareTo(ROAD_WIDTH_TWO_POINTFOURFOUR) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_THREE_POINTSIX) < 0) { isAccepted =
		 * far.compareTo(ONE_POINTTWO) <= 0;
		 * pl.getFarDetails().setPermissableFar(ONE_POINTTWO.doubleValue());
		 * expectedResult = "<= 1.2"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_THREE_POINTSIX) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_FOUR_POINTEIGHT) < 0) { isAccepted =
		 * far.compareTo(ONE_POINTFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(ONE_POINTFIVE.doubleValue());
		 * expectedResult = "<= 1.5"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_FOUR_POINTEIGHT) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) < 0) { isAccepted =
		 * far.compareTo(ONE_POINTEIGHT) <= 0;
		 * pl.getFarDetails().setPermissableFar(ONE_POINTEIGHT.doubleValue());
		 * expectedResult = "<= 1.8"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_NINE_POINTONE) < 0) { isAccepted =
		 * far.compareTo(TWO) <= 0;
		 * pl.getFarDetails().setPermissableFar(TWO.doubleValue()); expectedResult =
		 * "<= 2"; } else if (roadWidth.compareTo(ROAD_WIDTH_NINE_POINTONE) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) { isAccepted =
		 * far.compareTo(TWO_POINTFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
		 * expectedResult = "<= 2.5"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_EIGHTEEN_POINTTHREE) < 0) { isAccepted =
		 * far.compareTo(TWO_POINTFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
		 * expectedResult = "<= 2.5"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_EIGHTEEN_POINTTHREE) >= 0) { isAccepted =
		 * far.compareTo(TWO_POINTFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
		 * expectedResult = "<= 2.5"; }
		 * 
		 * }
		 * 
		 * if (typeOfArea.equalsIgnoreCase(NEW)) { if
		 * (roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) < 0) {
		 * errors.put(NEW_AREA_ERROR, NEW_AREA_ERROR_MSG); pl.addErrors(errors); } else
		 * if (roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_NINE_POINTONE) < 0) { isAccepted =
		 * far.compareTo(TWO) <= 0;
		 * pl.getFarDetails().setPermissableFar(TWO.doubleValue()); expectedResult =
		 * "<= 2"; } else if (roadWidth.compareTo(ROAD_WIDTH_NINE_POINTONE) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) { isAccepted =
		 * far.compareTo(TWO_POINTFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
		 * expectedResult = "<= 2.5"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_EIGHTEEN_POINTTHREE) < 0) { isAccepted =
		 * far.compareTo(TWO_POINTFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
		 * expectedResult = "<= 2.5"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_EIGHTEEN_POINTTHREE) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_TWENTYFOUR_POINTFOUR) < 0) { isAccepted =
		 * far.compareTo(TWO_POINTFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
		 * expectedResult = "<= 2.5"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_TWENTYFOUR_POINTFOUR) >= 0 &&
		 * roadWidth.compareTo(ROAD_WIDTH_TWENTYSEVEN_POINTFOUR) < 0) { isAccepted =
		 * far.compareTo(THREE) <= 0;
		 * pl.getFarDetails().setPermissableFar(THREE.doubleValue()); expectedResult =
		 * "<= 3"; } else if (roadWidth.compareTo(ROAD_WIDTH_TWENTYSEVEN_POINTFOUR) >= 0
		 * && roadWidth.compareTo(ROAD_WIDTH_THIRTY_POINTFIVE) < 0) { isAccepted =
		 * far.compareTo(THREE_POINTTWOFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(THREE_POINTTWOFIVE.doubleValue());
		 * expectedResult = "<= 3.25"; } else if
		 * (roadWidth.compareTo(ROAD_WIDTH_THIRTY_POINTFIVE) >= 0) { isAccepted =
		 * far.compareTo(THREE_POINTFIVE) <= 0;
		 * pl.getFarDetails().setPermissableFar(THREE_POINTFIVE.doubleValue());
		 * expectedResult = "<= 3.5"; }
		 * 
		 * }
		 */

		getFarDetailsFromMDMS(pl, occupancyType.getType().getCode(), typeOfArea, occupancyType);
			
		String occupancyName = occupancyType.getType().getName();
		if (errors.isEmpty() && StringUtils.isNotBlank(expectedResult)) {
			//buildResult(pl, occupancyType, far, typeOfArea, roadWidth, expectedResult, isAccepted);
		}
	}

	private void processFarCommercial(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far,
	        String typeOfArea, BigDecimal roadWidth, HashMap<String, String> errors,
	        BigDecimal plotArea) {

	    String expectedResult = StringUtils.EMPTY;
	    boolean isAccepted = false;

	    /* ---------- Plot Area Validation ---------- */
	    if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
	        if (!shouldSkipValidation(pl.getEdcrRequest(), DcrConstants.EDCR_SKIP_PLOT_AREA)) {
	            errors.put("Plot Area Error", "Plot area must be greater than 0.");
	            pl.addErrors(errors);
	        }
	        return;
	    }

//	    /* ---------- Road Width Validation ---------- */
//	    if (roadWidth == null || roadWidth.compareTo(ROAD_WIDTH_18) < 0) {
//	        errors.put("Road Width Error", "Road width below 18 meters is not permitted for FAR.");
//	        pl.addErrors(errors);
//	        return;
//	    }
	    
	    BigDecimal minPlotArea = getMinPlotAreaByOccupancy(occupancyType.getSubtype().getCode());
	    if (plotArea.compareTo(minPlotArea) >= 0) {
		} else {
			errors.put("Min Plot Area Error:", "Minimun plot area for " + occupancyType.getSubtype().getName() + " is " 
					+ minPlotArea + " sqm. Please correct the file and try again");
	        pl.addErrors(errors);
		}
		if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {				
		    if (!shouldSkipValidation(pl.getEdcrRequest(), DcrConstants.EDCR_SKIP_PLOT_AREA)) {
		        errors.put("Plot Area Error:", "Plot area must be greater than 0.");
		        pl.addErrors(errors);
		    }
		} else if (occupancyType != null 
		        && occupancyType.getType() != null 
		        && occupancyType.getType().getCode() != null) {
			//OccupancyHelperDetail subtype = occupancyType.getSubtype();
			//occupancyName = subtype.getName();
			getFarDetailsFromMDMS(pl, occupancyType.getType().getCode(), typeOfArea, occupancyType);
		    
		}
//	    /* ---------- FAR Determination (Ascending Order) ---------- */
//	    if (roadWidth.compareTo(ROAD_WIDTH_18) >= 0
//	            && roadWidth.compareTo(ROAD_WIDTH_24) < 0) {
//
//	        expectedResult = "2.0";
//	        isAccepted = far != null && far.compareTo(FAR_2) <= 0;
//
//	    } else if (roadWidth.compareTo(ROAD_WIDTH_24) >= 0
//	            && roadWidth.compareTo(ROAD_WIDTH_45) < 0) {
//
//	        expectedResult = "3.0";
//	        isAccepted = far != null && far.compareTo(FAR_3) <= 0;
//
//	    } else { // roadWidth >= 45
//
//	        expectedResult = "UNLIMITED";
//	        isAccepted = true;
//	    }
//
//	    /* ---------- Build Result ---------- */
//	    String occupancyName = occupancyType.getType().getName();
//
//	    if (errors.isEmpty() && StringUtils.isNotBlank(expectedResult)) {
//	        buildResult(pl, occupancyName, far, typeOfArea, roadWidth, expectedResult, isAccepted);
//	    }
	}
	
	private void processFarCommercialByMBMS(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far,
	        String typeOfArea, BigDecimal roadWidth, HashMap<String, String> errors,
	        BigDecimal plotArea) {

	    String expectedResult = StringUtils.EMPTY;
	    boolean isAccepted = false;

	    /* ---------- Plot Area Validation ---------- */
	    if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
	        if (!shouldSkipValidation(pl.getEdcrRequest(), DcrConstants.EDCR_SKIP_PLOT_AREA)) {
	            errors.put("Plot Area Error", "Plot area must be greater than 0.");
	            pl.addErrors(errors);
	        }
	        return;
	    }

	    /* ---------- Road Width Validation ---------- */
	    if (roadWidth == null || roadWidth.compareTo(ROAD_WIDTH_18) < 0) {
	        errors.put("Road Width Error", "Road width below 18 meters is not permitted for FAR.");
	        pl.addErrors(errors);
	        return;
	    }
	    
	    if (occupancyType != null 
		        && occupancyType.getType() != null 
		        && occupancyType.getType().getCode() != null) {
			OccupancyHelperDetail subtype = occupancyType.getSubtype();
			//occupancyName = subtype.getName();
			getFarDetailsFromMDMS(pl, occupancyType.getType().getCode(), typeOfArea, occupancyType);
	    }

//	    /* ---------- FAR Determination (Ascending Order) ---------- */
//	    if (roadWidth.compareTo(ROAD_WIDTH_18) >= 0
//	            && roadWidth.compareTo(ROAD_WIDTH_24) < 0) {
//
//	        expectedResult = "2.0";
//	        isAccepted = far != null && far.compareTo(FAR_2) <= 0;
//
//	    } else if (roadWidth.compareTo(ROAD_WIDTH_24) >= 0
//	            && roadWidth.compareTo(ROAD_WIDTH_45) < 0) {
//
//	        expectedResult = "3.0";
//	        isAccepted = far != null && far.compareTo(FAR_3) <= 0;
//
//	    } else { // roadWidth >= 45
//
//	        expectedResult = "UNLIMITED";
//	        isAccepted = true;
//	    }
//
//	    /* ---------- Build Result ---------- */
//	    String occupancyName = occupancyType.getType().getName();
//
//	    if (errors.isEmpty() && StringUtils.isNotBlank(expectedResult)) {
//	        buildResult(pl, occupancyName, far, typeOfArea, roadWidth, expectedResult, isAccepted);
//	    }
	}



	private void processFarNonResidential(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far, String typeOfArea,
			BigDecimal roadWidth, HashMap<String, String> errors, BigDecimal plotArea) {

		String expectedResult = StringUtils.EMPTY;
		boolean isAccepted = false;
		
		if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {				
		    if (!shouldSkipValidation(pl.getEdcrRequest(), DcrConstants.EDCR_SKIP_PLOT_AREA)) {
		        errors.put("Plot Area Error:", "Plot area must be greater than 0.");
		        pl.addErrors(errors);
		    }
		} else if (plotArea.compareTo(COMMERCIAL_PLOTAREA_LIMIT_41_82) <= 0) {
		    // 0–41.82 sqm → FAR = 1.50
		    isAccepted = far.compareTo(COMMERCIAL_FAR_1_50) <= 0;
		    LOG.info("COMMERCIAL_FAR_1_50: " + isAccepted);
		    pl.getFarDetails().setPermissableFar(COMMERCIAL_FAR_1_50.doubleValue());
		    expectedResult = "<= " + COMMERCIAL_FAR_1_50;

		} else if (plotArea.compareTo(COMMERCIAL_PLOTAREA_LIMIT_104_5) <= 0) {
		    // 41.82–104.5 sqm → FAR = 2.50
		    isAccepted = far.compareTo(COMMERCIAL_FAR_2_50) <= 0;
		    LOG.info("COMMERCIAL_FAR_2_50: " + isAccepted);
		    pl.getFarDetails().setPermissableFar(COMMERCIAL_FAR_2_50.doubleValue());
		    expectedResult = "<= " + COMMERCIAL_FAR_2_50;

		} else if (plotArea.compareTo(COMMERCIAL_PLOTAREA_LIMIT_209) <= 0) {
		    // 104.5–209 sqm → FAR = 1.75
		    isAccepted = far.compareTo(COMMERCIAL_FAR_1_75) <= 0;
		    LOG.info("COMMERCIAL_FAR_1_75: " + isAccepted);
		    pl.getFarDetails().setPermissableFar(COMMERCIAL_FAR_1_75.doubleValue());
		    expectedResult = "<= " + COMMERCIAL_FAR_1_75;

		} else if (plotArea.compareTo(COMMERCIAL_PLOTAREA_LIMIT_418_21) <= 0) {
		    // 209–418.21 sqm → FAR = 2.00
		    isAccepted = far.compareTo(COMMERCIAL_FAR_2_00) <= 0;
		    LOG.info("COMMERCIAL_FAR_2_00: " + isAccepted);
		    pl.getFarDetails().setPermissableFar(COMMERCIAL_FAR_2_00.doubleValue());
		    expectedResult = "<= " + COMMERCIAL_FAR_2_00;

		} else {
		    // >418.21 sqm → FAR = 3.00
		    isAccepted = far.compareTo(COMMERCIAL_FAR_3_00) <= 0;
		    LOG.info("COMMERCIAL_FAR_3_00: " + isAccepted);
		    pl.getFarDetails().setPermissableFar(COMMERCIAL_FAR_3_00.doubleValue());
		    expectedResult = "<= " + COMMERCIAL_FAR_3_00;
		}

//		if (typeOfArea.equalsIgnoreCase(OLD)) {
//			if (roadWidth.compareTo(ROAD_WIDTH_TWO_POINTFOUR) < 0) {
//				errors.put(OLD_AREA_ERROR, OLD_AREA_ERROR_MSG);
//				pl.addErrors(errors);
//			} else if (roadWidth.compareTo(ROAD_WIDTH_TWO_POINTFOURFOUR) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_THREE_POINTSIX) < 0) {
//				isAccepted = far.compareTo(ONE_POINTTWO) <= 0;
//				pl.getFarDetails().setPermissableFar(TWO.doubleValue());
//				expectedResult = "<= 1.2";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_THREE_POINTSIX) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_FOUR_POINTEIGHT) < 0) {
//				isAccepted = far.compareTo(BigDecimal.ZERO) >= 0;
//				pl.getFarDetails().setPermissableFar(BigDecimal.ZERO.doubleValue());
//				expectedResult = "0";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_FOUR_POINTEIGHT) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) < 0) {
//				isAccepted = far.compareTo(BigDecimal.ZERO) >= 0;
//				pl.getFarDetails().setPermissableFar(BigDecimal.ZERO.doubleValue());
//				expectedResult = "0";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_NINE_POINTONE) < 0) {
//				isAccepted = far.compareTo(BigDecimal.ZERO) >= 0;
//				pl.getFarDetails().setPermissableFar(BigDecimal.ZERO.doubleValue());
//				expectedResult = "0";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_NINE_POINTONE) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
//				isAccepted = far.compareTo(BigDecimal.ZERO) >= 0;
//				pl.getFarDetails().setPermissableFar(BigDecimal.ZERO.doubleValue());
//				expectedResult = "0";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_EIGHTEEN_POINTTHREE) < 0) {
//				isAccepted = far.compareTo(TWO) <= 0;
//				pl.getFarDetails().setPermissableFar(TWO.doubleValue());
//				expectedResult = "<= 2";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_TWENTYFOUR_POINTFOUR) >= 0) {
//				isAccepted = far.compareTo(TWO_POINTFIVE) <= 0;
//				pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
//				expectedResult = "<= 2.5";
//			}
//
//		}

//		if (typeOfArea.equalsIgnoreCase(NEW)) {
//			if (roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) < 0) {
//				errors.put(NEW_AREA_ERROR, NEW_AREA_ERROR_MSG);
//				pl.addErrors(errors);
//			} else if (roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_NINE_POINTONE) < 0) {
//				isAccepted = far.compareTo(BigDecimal.ZERO) >= 0;
//				pl.getFarDetails().setPermissableFar(BigDecimal.ZERO.doubleValue());
//				expectedResult = "0";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_NINE_POINTONE) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) < 0) {
//				isAccepted = far.compareTo(BigDecimal.ZERO) >= 0;
//				pl.getFarDetails().setPermissableFar(BigDecimal.ZERO.doubleValue());
//				expectedResult = "0";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_TWELVE_POINTTWO) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_EIGHTEEN_POINTTHREE) < 0) {
//				isAccepted = far.compareTo(TWO) <= 0;
//				pl.getFarDetails().setPermissableFar(TWO.doubleValue());
//				expectedResult = "<= 2";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_EIGHTEEN_POINTTHREE) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_TWENTYFOUR_POINTFOUR) < 0) {
//				isAccepted = far.compareTo(TWO_POINTFIVE) <= 0;
//				pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
//				expectedResult = "<= 2.5";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_TWENTYFOUR_POINTFOUR) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_TWENTYSEVEN_POINTFOUR) < 0) {
//				isAccepted = far.compareTo(TWO_POINTFIVE) <= 0;
//				pl.getFarDetails().setPermissableFar(TWO_POINTFIVE.doubleValue());
//				expectedResult = "<= 2.5";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_TWENTYSEVEN_POINTFOUR) >= 0
//					&& roadWidth.compareTo(ROAD_WIDTH_THIRTY_POINTFIVE) < 0) {
//				isAccepted = far.compareTo(THREE) <= 0;
//				pl.getFarDetails().setPermissableFar(THREE.doubleValue());
//				expectedResult = "<= 3";
//			} else if (roadWidth.compareTo(ROAD_WIDTH_THIRTY_POINTFIVE) >= 0) {
//				isAccepted = far.compareTo(THREE_POINTFIVE) <= 0;
//				pl.getFarDetails().setPermissableFar(THREE_POINTFIVE.doubleValue());
//				expectedResult = "<= 3";
//			}
//
//		}

		String occupancyName = occupancyType.getType().getName();

		if (errors.isEmpty() && StringUtils.isNotBlank(expectedResult)) {
			buildResult(pl, occupancyName, far, typeOfArea, roadWidth, expectedResult, isAccepted);
		}
	}
	
	private void processFarPetrolPump(Plan pl, OccupancyTypeHelper mostRestrictiveOccupancy, BigDecimal far, String typeOfArea,
			BigDecimal roadWidth, HashMap<String, String> errors, BigDecimal plotArea) {

	    LOG.info("inside processFarPetrolPump()");
	    String expectedResult = StringUtils.EMPTY;
	    boolean isAccepted = false;

	   // Normalize building type
	    OccupancyHelperDetail subtype = mostRestrictiveOccupancy.getSubtype();
	    String subType = subtype.getCode();

//	    // Validation and FAR assignment based on building type
//	    switch (subType) {
//	    	case "J-FS":
//	            if (plotArea.compareTo(MIN_PLOT_AREA_FILLING_STATION) < 0) {
//	                errors.put("Plot Area Error:", 
//	                    "Plot area cannot be less than " + MIN_PLOT_AREA_FILLING_STATION + " sq.m for FILLING STATION");
//	                pl.addErrors(errors);
//	            } else {
//	                isAccepted = far.compareTo(FAR_FILLING_STATION) <= 0;
//	                pl.getFarDetails().setPermissableFar(FAR_FILLING_STATION.doubleValue());
//	                expectedResult = "<= " + FAR_FILLING_STATION;
//	            }
//	            break;
//
//	    	case "J-FCSS":
//	            if (plotArea.compareTo(MIN_PLOT_AREA_FILLING_CUM_SERVICE) < 0) {
//	                errors.put("Plot Area Error:", 
//	                    "Plot area cannot be less than " + MIN_PLOT_AREA_FILLING_CUM_SERVICE + " sq.m for FILLING-CUM-SERVICE STATION");
//	                pl.addErrors(errors);
//	            } else {
//	                isAccepted = far.compareTo(FAR_FILLING_CUM_SERVICE) <= 0;
//	                pl.getFarDetails().setPermissableFar(FAR_FILLING_CUM_SERVICE.doubleValue());
//	                expectedResult = "<= " + FAR_FILLING_CUM_SERVICE;
//	            }
//	            break;
//
//	    	case "J-CNG":
//	            if (plotArea.compareTo(MIN_PLOT_AREA_CNG_MOTHER) < 0) {
//	                errors.put("Plot Area Error:", 
//	                    "Plot area cannot be less than " + MIN_PLOT_AREA_CNG_MOTHER + " sq.m for CNG MOTHER STATION");
//	                pl.addErrors(errors);
//	            } else {
//	                isAccepted = far.compareTo(FAR_CNG_MOTHER) <= 0;
//	                pl.getFarDetails().setPermissableFar(FAR_CNG_MOTHER.doubleValue());
//	                expectedResult = "<= " + FAR_CNG_MOTHER;
//	            }
//	            break;
//
//	        default:
//	            errors.put("Building Type Error:", 
//	                "Invalid building type provided for FAR calculation of Petrol Pump.");
//	            pl.addErrors(errors);
//	    }

	    // Fetch additional FAR details from MDMS (if configured)
	    getFarDetailsFromMDMS(pl, subType, typeOfArea, mostRestrictiveOccupancy);

	    //  Build result only if no validation errors
	    //String occupancyName = occupancyType.getType().getName();
	    //if (errors.isEmpty() && StringUtils.isNotBlank(expectedResult)) {
	        //buildResult(pl, occupancyName, far, typeOfArea, roadWidth, expectedResult, isAccepted);
	    //}
	}

	private void processFarForGBDOccupancy(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far,
			String typeOfArea, BigDecimal roadWidth, HashMap<String, String> errors, BigDecimal plotArea) {

		String expectedResult = StringUtils.EMPTY;
		boolean isAccepted = false;
		String occupancyName = occupancyType.getType().getName();

//		if (typeOfArea.equalsIgnoreCase(OLD)) {
//			if (roadWidth.compareTo(ROAD_WIDTH_TWO_POINTFOUR) < 0) {
//				errors.put(OLD_AREA_ERROR, OLD_AREA_ERROR_MSG);
//				pl.addErrors(errors);
//				return;
//			} else {
//				isAccepted = far.compareTo(ONE_POINTFIVE) <= 0;
//				pl.getFarDetails().setPermissableFar(ONE_POINTFIVE.doubleValue());
//				expectedResult = "<=" + ONE_POINTFIVE;
//			}
//
//		}

//		if (typeOfArea.equalsIgnoreCase(NEW)) {
//			if (roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) < 0) {
//				errors.put(NEW_AREA_ERROR, NEW_AREA_ERROR_MSG);
//				pl.addErrors(errors);
//				//return;
//			} else {
//				isAccepted = far.compareTo(ONE_POINTFIVE) <= 0;
//				pl.getFarDetails().setPermissableFar(ONE_POINTFIVE.doubleValue());
//				expectedResult = "<=" + ONE_POINTFIVE;
//			}
//
//		}

//		String occupancyName1 = occupancyType.getType().getName();
//
//		if (occupancyType.getSubtype() != null) {
//			OccupancyHelperDetail subtype = occupancyType.getSubtype();
//			occupancyName = subtype.getName();
//			String code = subtype.getCode();
//
//			if (G_PHI.equalsIgnoreCase(code)) {
//				isAccepted = far.compareTo(POINTFIVE) <= 0;
//				pl.getFarDetails().setPermissableFar(POINTFIVE.doubleValue());
//				expectedResult = "<=" + POINTFIVE;
//			} else if (G_NPHI.equalsIgnoreCase(code)) {
//				isAccepted = far.compareTo(ONE_POINTFIVE) <= 0;
//				pl.getFarDetails().setPermissableFar(ONE_POINTFIVE.doubleValue());
//				expectedResult = "<=" + ONE_POINTFIVE;
//			}
//		}

		BigDecimal minPlotArea = getMinPlotAreaByOccupancy(occupancyType.getSubtype().getCode());
		if (plotArea.compareTo(minPlotArea) >= 0) {
		} else {
			errors.put("Min Plot Area Error:", "Minimun plot area for " + occupancyType.getSubtype().getName() + " is " 
					+ minPlotArea + " sqm. Please correct the file and try again");
	        pl.addErrors(errors);
		}
		if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {				
		    if (!shouldSkipValidation(pl.getEdcrRequest(), DcrConstants.EDCR_SKIP_PLOT_AREA)) {
		        errors.put("Plot Area Error:", "Plot area must be greater than 0.");
		        pl.addErrors(errors);
		    }
		} else if (occupancyType != null 
		        && occupancyType.getType() != null 
		        && occupancyType.getType().getCode() != null) {
			//OccupancyHelperDetail subtype = occupancyType.getSubtype();
			//occupancyName = subtype.getName();
			getFarDetailsFromMDMS(pl, occupancyType.getType().getCode(), typeOfArea, occupancyType);
		    
		    //String subType = occupancyType.getSubtype().get().getCode();

//		    switch (subType) {
//		        // *********** INDUSTRIAL PLOTTED ***********
//		        case "G-I": // Industrial
//		            if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_300) <= 0) {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_1_50) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_1_50.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_1_50;
//		            } else {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_3_00) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_3_00.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_3_00;
//		            }
//		            break;
//
//		        // *********** INFORMATION TECHNOLOGY PLOTTED ***********
//		        case "G-IT": // Information Technology
//		            if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_300) > 0) {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_2_50) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_2_50.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_2_50;
//		            }
//		            break;
//
//		        // *********** TEXTILE PLOTTED ***********
//		        case "G-T": // Textile Industry
//		            if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_2000) > 0) {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_2_50) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_2_50.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_2_50;
//		            }
//		            break;
//
//		        // *********** KNITWEAR PLOTTED ***********
//		        case "G-K": // Knitwear Industry
//		            if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_2000) > 0) {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_2_50) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_2_50.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_2_50;
//		            }
//		            break;
//
//		        // *********** GENERAL INDUSTRY FLATTED ***********
//		        case "G-GI": // General Industry
//		            if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_2000) > 0) {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_3_00) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_3_00.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_3_00;
//		            }
//		            break;
//
//		        // *********** TEXTILE FLATTED ***********
//		        // Same rule as Textile PLOTTED
//		        case "G-TF": // Textile Flatted (custom code if needed)
//		            if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_2000) > 0) {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_2_50) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_2_50.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_2_50;
//		            }
//		            break;
//
//		        // *********** KNITWEAR FLATTED ***********
//		        // Same rule as Knitwear PLOTTED
//		        case "G-KF": // Knitwear Flatted (custom code if needed)
//		            if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_2000) > 0) {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_2_50) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_2_50.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_2_50;
//		            }
//		            break;
//
//		        // *********** WHOLESALE TRADE / WAREHOUSE / FREIGHT COMPLEX ***********
//		        case "G-W": // Warehouse
//		            if (plotArea.compareTo(INDUSTRIAL_PLOTAREA_LIMIT_10000) > 0) {
//		                isAccepted = far.compareTo(INDUSTRIAL_FAR_1_50) <= 0;
//		                pl.getFarDetails().setPermissableFar(INDUSTRIAL_FAR_1_50.doubleValue());
//		                expectedResult = "<= " + INDUSTRIAL_FAR_1_50;
//		            }
//		            break;
//
//		        default:
//		            LOG.info("No Industrial FAR rule matched for subType: " + subType);
//		    }
		}
		
		if (errors.isEmpty() && StringUtils.isNotBlank(expectedResult)) {
			//buildResult(pl, occupancyName, far, typeOfArea, roadWidth, expectedResult, isAccepted);
		}
	}

	private void processFarHaazardous(Plan pl, OccupancyTypeHelper occupancyType, BigDecimal far, String typeOfArea,
			BigDecimal roadWidth, HashMap<String, String> errors) {

		String expectedResult = StringUtils.EMPTY;
		boolean isAccepted = false;

		if (typeOfArea.equalsIgnoreCase(OLD)) {
			if (roadWidth.compareTo(ROAD_WIDTH_TWO_POINTFOUR) < 0) {
				errors.put(OLD_AREA_ERROR, OLD_AREA_ERROR_MSG);
				pl.addErrors(errors);
			} else {
				isAccepted = far.compareTo(POINTFIVE) <= 0;
				pl.getFarDetails().setPermissableFar(POINTFIVE.doubleValue());
				expectedResult = "<=" + POINTFIVE;
			}

		}

		if (typeOfArea.equalsIgnoreCase(NEW)) {
			if (roadWidth.compareTo(ROAD_WIDTH_SIX_POINTONE) < 0) {
				errors.put(NEW_AREA_ERROR, NEW_AREA_ERROR_MSG);
				pl.addErrors(errors);
			} else {
				isAccepted = far.compareTo(POINTFIVE) <= 0;
				pl.getFarDetails().setPermissableFar(POINTFIVE.doubleValue());
				expectedResult = "<=" + POINTFIVE;
			}

		}

		String occupancyName = occupancyType.getType().getName();

		if (errors.isEmpty() && StringUtils.isNotBlank(expectedResult)) {
			buildResult(pl, occupancyName, far, typeOfArea, roadWidth, expectedResult, isAccepted);
		}
	}
	
	/**
	 * Builds a single ScrutinyDetail object containing all collected deduction records
	 * and adds it to the final report output.
	 */
	private void buildFinalDeductionReport(Plan pl, List<Map<String, String>> records) {
	    if (records.isEmpty()) {
	        return;
	    }

	    // 1. Create ONE ScrutinyDetail object for ALL deductions
	    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	    
	    // Set column headings and key ONCE
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, "BLOCK");
	    scrutinyDetail.addColumnHeading(3, "FLOOR");
	    scrutinyDetail.addColumnHeading(4, "TYPE");
	    scrutinyDetail.addColumnHeading(5, "DEDUCTION");
	    scrutinyDetail.setKey("Common_Deduction");

	    // 2. Iterate through the collected records and add them all to the SAME ScrutinyDetail
	    for (Map<String, String> record : records) {
	        
	        // Add the required RULE_NO (Assuming RULE is a constant)
	        record.put(RULE_NO, RULE); 
	        
	        // Add this specific record to the SINGLE ScrutinyDetail's detail list
	        scrutinyDetail.getDetail().add(record);
	    }
	    
	    // 3. Add the single, complete ScrutinyDetail object to the plan report ONCE
	    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

//	private void buildResultDeduction(Plan pl, String BlockNo, String FloorNo, String deductionType, BigDecimal deductArea) {
//		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
//		scrutinyDetail.addColumnHeading(1, RULE_NO);
//		scrutinyDetail.addColumnHeading(2, "BLOCK");
//		scrutinyDetail.addColumnHeading(5, "FLOOR");
//		scrutinyDetail.addColumnHeading(2, "TYPE");
//		scrutinyDetail.addColumnHeading(6, "DEDUCATION");
//		scrutinyDetail.setKey("Common_Deducation");
//
//		Map<String, String> details = new HashMap<>();
//		details.put(RULE_NO, RULE);
//		details.put("BLOCK", BlockNo);
//		details.put("FLOOR", FloorNo);
//		details.put("TYPE", deductionType);
//		details.put("DEDUCATION", String.valueOf(deductArea));	
//		
//		scrutinyDetail.getDetail().add(details);
//		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//
//	}
	
	private void buildResult1(Plan pl, String occupancyName, Double totalProvidedFar, Double purchasableFar, String typeOfArea,
			Double expectedResult, boolean isAccepted) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, OCCUPANCY);
		scrutinyDetail.addColumnHeading(3, AREA_TYPE);
		//scrutinyDetail.addColumnHeading(4, ROAD_WIDTH);
		scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(6, PURCHASABLE);
		scrutinyDetail.addColumnHeading(7, PROVIDED);
		scrutinyDetail.addColumnHeading(8, STATUS);
		scrutinyDetail.setKey("Common_FAR");

		String totalProvidedFar1 = totalProvidedFar.toString();

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, RULE);
		details.put(OCCUPANCY, occupancyName);
		details.put(AREA_TYPE, typeOfArea);
	//	details.put(ROAD_WIDTH, roadWidth.toString());
		details.put(PERMISSIBLE, String.valueOf(expectedResult));
		details.put(PURCHASABLE, String.valueOf(purchasableFar));		
		details.put(PROVIDED, totalProvidedFar1);
		details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//		Map<String, String> details1 = new HashMap<>();
//		details1.put(RULE_NO, RULE);
//		details1.put(OCCUPANCY, "Purchaseable FAR");
//		details1.put(AREA_TYPE, typeOfArea);
//	//	details.put(ROAD_WIDTH, roadWidth.toString());
//		details1.put(PERMISSIBLE, expectedResult);
//		details1.put(PROVIDED, actualResult);
//		details1.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
//		scrutinyDetail.getDetail().add(details1);
//		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}
	
	private void buildResult(Plan pl, String occupancyName, BigDecimal far, String typeOfArea, BigDecimal roadWidth,
			String expectedResult, boolean isAccepted) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, OCCUPANCY);
		scrutinyDetail.addColumnHeading(3, AREA_TYPE);
		//scrutinyDetail.addColumnHeading(4, ROAD_WIDTH);
		scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(6, PROVIDED);
		scrutinyDetail.addColumnHeading(7, STATUS);
		scrutinyDetail.setKey("Common_FAR");

		String actualResult = far.toString();

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, RULE);
		details.put(OCCUPANCY, occupancyName);
		details.put(AREA_TYPE, typeOfArea);
	//	details.put(ROAD_WIDTH, roadWidth.toString());
		details.put(PERMISSIBLE, expectedResult);
		details.put(PROVIDED, actualResult);
		details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
//		Map<String, String> details1 = new HashMap<>();
//		details1.put(RULE_NO, RULE);
//		details1.put(OCCUPANCY, "Purchaseable FAR");
//		details1.put(AREA_TYPE, typeOfArea);
//	//	details.put(ROAD_WIDTH, roadWidth.toString());
//		details1.put(PERMISSIBLE, expectedResult);
//		details1.put(PROVIDED, actualResult);
//		details1.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
//		scrutinyDetail.getDetail().add(details1);
//		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	private ScrutinyDetail getFarScrutinyDetail(String key) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, "Area Type");
		//scrutinyDetail.addColumnHeading(3, "Road Width");
		scrutinyDetail.addColumnHeading(4, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(5, PROVIDED);
		scrutinyDetail.addColumnHeading(6, STATUS);
		scrutinyDetail.setKey(key);
		return scrutinyDetail;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
	
	private void updateFarAsPerBalconyWidth(Plan pl){
		BigDecimal totalBuiltUpArea1 = pl.getVirtualBuilding().getTotalBuitUpArea();
		//LOG.info("Far before new case added 0.91 > -> Far : " +  " totalBuildUpArea : " + totalBuiltUpArea);
		// Code added for: Balcony width > 0.91m is included in FAR calculation
		//BigDecimal totalBuiltUpArea1 = BigDecimal.ZERO;

		for (Block block : pl.getBlocks()) {
		    for (Floor floor : block.getBuilding().getFloors()) {
	            //BigDecimal floorArea = floor.getArea() != null ? floor.getArea() : BigDecimal.ZERO;
	            
	            //LOG.info("Floor Area : -> " + floorArea);

		        for (org.egov.common.entity.edcr.Balcony balcony : floor.getBalconies()) {
		        	
		        	LOG.info("Data for Floor :::: " + floor.getNumber());

		            List<BigDecimal> widths = balcony.getWidths();
		            List<Measurement> measurements = balcony.getMeasurements();

		            // Skip if measurement list is empty
		            if (measurements == null || measurements.isEmpty()) {
		                LOG.warn("No measurement found for balcony, skipping.");
		                continue;
		            }

		            Measurement m = measurements.get(0);

		            // Round measurement values once
		            BigDecimal area = m.getArea() != null
		                ? m.getArea().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
		                : BigDecimal.ZERO;
		            BigDecimal length = m.getLength() != null
		                ? m.getLength().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
		                : BigDecimal.ZERO;
		            BigDecimal height = m.getHeight() != null
		                ? m.getHeight().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
		                : BigDecimal.ZERO;

		            // Round threshold for comparison
		            BigDecimal threshold = EXCLUDE_BALCONY_WIDTH_ABOVE_4_FEET_FROM_FAR.setScale(
		                DcrConstants.DECIMALDIGITS_MEASUREMENTS,
		                DcrConstants.ROUNDMODE_MEASUREMENTS
		            );

		            boolean addedToFar = false;

		            for (BigDecimal rawWidth : widths) {
		                BigDecimal width = rawWidth.setScale(
		                    DcrConstants.DECIMALDIGITS_MEASUREMENTS,
		                    DcrConstants.ROUNDMODE_MEASUREMENTS
		                );

		                LOG.info("Balcony width above 0.91m, included in FAR: " + width);
	                    LOG.info("Balcony area   : " + area);
	                    LOG.info("Balcony length : " + length);
	                    LOG.info("Balcony height : " + height);
		                if (width.compareTo(threshold) > 0) {
		                    

		                    //totalBuiltUpArea1 = totalBuiltUpArea1.add(area);
		                    //providedFar = providedFar.add(area);
		                    
		                    // Add to this floor’s floor area
	                        //floorArea = floorArea.add(area);
		                    
		                 // Add to total built-up area
	                        totalBuiltUpArea1 = totalBuiltUpArea1.add(area);

	                        // ✅ Also distribute balcony area into each occupancy of this floor
	                        for (Occupancy occ : floor.getOccupancies()) {
	                            BigDecimal occFloorArea = occ.getFloorArea() != null ? occ.getFloorArea() : BigDecimal.ZERO;
	                            BigDecimal occBuiltUpArea = occ.getBuiltUpArea() != null ? occ.getBuiltUpArea() : BigDecimal.ZERO;

	                            LOG.info("Floor Area : " + occ.getFloorArea());
	                            LOG.info("Floor Built Up Area : " + occ.getBuiltUpArea());

	                            
	                            occ.setFloorArea(occFloorArea.add(area));
	                            occ.setBuiltUpArea(occBuiltUpArea.add(area));

	                            LOG.info("Updated Occupancy [type=" + occ.getType() + "] floorArea=" + occ.getFloorArea()
	                                    + ", builtUpArea=" + occ.getBuiltUpArea());
	                        }
		                    
		                    addedToFar = true;
		                    break; // Only add once per balcony
		                }
		            }

		            if (!addedToFar) {
		                LOG.info("Balcony width <= 0.91m, excluded from FAR.");
		            }
		        }
		     // update floor area for current floor
	            //floor.setArea(floorArea);
	            //LOG.info("Updated floor area for Floor " + floor.getNumber() + " : " + floorArea);
		    }
		}

		//pl.getVirtualBuilding().setTotalBuitUpArea(totalBuiltUpArea1);


		LOG.info("Far after new case added 0.91 > " + " totalBuildUpArea : " + totalBuiltUpArea1);
		pl.getVirtualBuilding().setTotalBuitUpArea(totalBuiltUpArea1);
	}
	
	static boolean shouldSkipValidation(EdcrRequest edcrRequest, String validationType) {
	    if (edcrRequest == null || edcrRequest.getAreaType() == null) {
	        return false;
	    }

	    // SCHEME_AREA
	    if ("SCHEME_AREA".equalsIgnoreCase(edcrRequest.getAreaType())) {
	        if (edcrRequest.getSchName() != null && !edcrRequest.getSchName().isEmpty()) {
	            if (Boolean.TRUE.equals(edcrRequest.getSiteReserved())) {
	                if (!Boolean.TRUE.equals(edcrRequest.getApprovedCS())) {
	                    // Skip PlotArea & RoadWidth validation
	                    if ("PlotArea".equalsIgnoreCase(validationType) || "RoadWidth".equalsIgnoreCase(validationType)) {
	                        return true;
	                    }
	                }
	            }
	        }
	    }

	    // NON_SCHEME_AREA
	    else if ("NON_SCHEME_AREA".equalsIgnoreCase(edcrRequest.getAreaType())) {
	        if (Boolean.TRUE.equals(edcrRequest.getCluApprove())) {
	            // Skip PlotArea & RoadWidth validation
	            if ("PlotArea".equalsIgnoreCase(validationType) || "RoadWidth".equalsIgnoreCase(validationType)) {
	                return true;
	            }
	        }

	        if ("yes".equalsIgnoreCase(edcrRequest.getCoreArea())) {
	            // Skip plot coverage, front setback and ECS validation
	            if ("PlotCoverage".equalsIgnoreCase(validationType)
	                    || "FrontSetback".equalsIgnoreCase(validationType)
	                    || "ECS".equalsIgnoreCase(validationType)) {
	                return true;
	            }
	        }
	    }

	    return false;
	}
	
	private void getMdmsMastersData(Plan pl, String occType, OccupancyTypeHelper occupancyType) {
	    Boolean mdmsEnabled = mdmsConfiguration.getMdmsEnabled();
	    if (Boolean.TRUE.equals(mdmsEnabled)) {
	        try {
	            BigDecimal plotArea = pl.getPlot().getArea() != null ? pl.getPlot().getArea() : BigDecimal.ZERO;
	            Object mdmsData = null;
	            
//	            if (occupancyType != null
//	                    && occupancyType.getSubtype() != null
//	                    && occupancyType.getSubtype().getCode() != null
//	                    && !DxfFileConstants.F.equalsIgnoreCase(occupancyType.getType().getCode())) {
//	                mdmsData = bpaMdmsUtil.mDMSCall(
//	                		new RequestInfo(),pl.getEdcrRequest(), occupancyType.getSubtype().getCode(), plotArea);	               
//	            }else {
//	            	mdmsData = bpaMdmsUtil.mDMSCall(new RequestInfo(), pl.getEdcrRequest(), occupancyType.getType().getCode(), plotArea);
//	            }
	            
	            
	            mdmsData = bpaMdmsUtil.mDMSCall(
                		new RequestInfo(),pl.getEdcrRequest(), occupancyType.getSubtype().getCode(), plotArea);		            

	            if (mdmsData != null) {
	            	Map<String, List<Map<String, Object>>> masterPlanData1 =
                            BpaMdmsUtil.mdmsResponseMapper(mdmsData, MdmsFilter.MASTER_PLAN_FILTER);
                    if (masterPlanData1 != null) {
                        List<Object> wrapperList = new ArrayList<>();
                        wrapperList.add(masterPlanData1);
                        pl.getMdmsMasterData().putIfAbsent("masterMdmsData", wrapperList);
                    }
	            }else {
	            	LOG.info("No matching MasterPlan record found for OccupancyType=" 
                            + occType + ", area=" + plotArea);
	            }
	        } catch (Exception e) {
	            LOG.error("Error while fetching details from MDMS", e);
	        }
	    }else {
	    	LOG.info("MDSM enable property is : False , Skipping FAR calculation by MDMS.");
	    }
	}
	
//	private void getFarDetailsFromMDMS(Plan pl, String occType, String typeOfArea, OccupancyTypeHelper occupancyType) {
//	        try {	           	  
//	        	BigDecimal plotArea = pl.getPlot().getArea() != null ? pl.getPlot().getArea() : BigDecimal.ZERO;
//	            Optional<Double> normalFAR = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.NORMAL_FAR, Double.class);
//	            normalFAR.ifPresent(normalFar -> LOG.info("normalFar get by mdms : " + normalFar));
//				
//				Optional<Double> purchasableFAR = BpaMdmsUtil.extractMdmsValue(pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.PURCHASABLE_FAR, Double.class);
//				purchasableFAR.ifPresent(purchasableFar -> LOG.info("purchasableFAR get by mdms : : " + purchasableFar));
//
//				Double regularPermissableFar = normalFAR.get();
//	            Double purchasablePermissableFar = purchasableFAR.get();
//	            Double providedFar = pl.getFarDetails() != null ? pl.getFarDetails().getProvidedFar() : 0.0;	                     
//	            Double purchasableFar = 0.0;		           
//	                        
//		        if (Boolean.TRUE.equals(pl.getEdcrRequest().getPurchasableFar())) {	
//			        // Step 1: Calculate purchasable FAR (if provided > regular)
//			        if (providedFar != null && regularPermissableFar != null && providedFar > regularPermissableFar) {
//			        	  purchasableFar = providedFar - regularPermissableFar;
//			         }
//	
//		            // Step 2: Determine total permissible FAR conditionally
//		            Double totalPermissableFar;
//		            if (providedFar != null && regularPermissableFar != null && providedFar > regularPermissableFar) {
//		                                // Provided FAR exceeds regular, allow adding purchasable permissible FAR
//		                totalPermissableFar = (regularPermissableFar != null ? regularPermissableFar : 0.0)
//		                                        + (purchasablePermissableFar != null ? purchasablePermissableFar : 0.0);
//		            } else {
//		                                // Provided FAR is within regular permissible, no need to add extra FAR
//		                totalPermissableFar = regularPermissableFar != null ? regularPermissableFar : 0.0;
//		            }
//	
//		            // Step 3: Round all values to 2 decimals
//		            regularPermissableFar = BigDecimal.valueOf(regularPermissableFar).setScale(2, RoundingMode.HALF_UP).doubleValue();
//		            purchasablePermissableFar = BigDecimal.valueOf(purchasablePermissableFar).setScale(2, RoundingMode.HALF_UP).doubleValue();
//		            providedFar = BigDecimal.valueOf(providedFar).setScale(2, RoundingMode.HALF_UP).doubleValue();
//		            purchasableFar = BigDecimal.valueOf(purchasableFar).setScale(2, RoundingMode.HALF_UP).doubleValue();
//		            totalPermissableFar = BigDecimal.valueOf(totalPermissableFar).setScale(2, RoundingMode.HALF_UP).doubleValue();
//	
//		            // Step 4: Check acceptance
//		            boolean isAccepted = (providedFar <= totalPermissableFar);
//	
//		            // Step 5: Update FAR details in plan
//		            pl.getFarDetails().setPermissableFar(regularPermissableFar);
//		            pl.getFarDetails().setPurchasableFar(purchasablePermissableFar);
//		            pl.getFarDetails().setProvidedPurchasableFar(purchasableFar);
//		            pl.getFarDetails().setProvidedFar(providedFar);
//	
//		            // Step 6: Logging
//		            LOG.info("Matched FAR -> OccupancyType: " + occType + ", PlotArea: " + plotArea);
//		            LOG.info("Regular Permissible FAR: " + regularPermissableFar
//		                    + ", Purchasable FAR Allowed: " + purchasablePermissableFar
//		                    + ", Provided FAR: " + providedFar
//		                    + ", Total Permissible FAR: " + totalPermissableFar
//		                    + ", Accepted: " + isAccepted);
//	
//		            // Step 7: Build result
//		            buildResult1(pl, occupancyType.getType().getName(), providedFar, purchasablePermissableFar,
//		                                    typeOfArea, regularPermissableFar, isAccepted);	                           
//
//		        } else {
//		        	LOG.info("Purchasable FAR is false, processing as normal Rules");
//		            // If purchasable FAR is not enabled
//		            boolean isAccepted = (providedFar <= regularPermissableFar);
//		            pl.getFarDetails().setPermissableFar(regularPermissableFar);
//		            pl.getFarDetails().setProvidedFar(providedFar);
//		            pl.getFarDetails().setPurchasableFar(mdmsDataParser.toDouble(0.0));
//		            pl.getFarDetails().setProvidedPurchasableFar(mdmsDataParser.toDouble(0.0));		                            
//	
//		            buildResult1(pl, occupancyType.getType().getName(), providedFar, purchasableFar,
//	                                    typeOfArea, regularPermissableFar, isAccepted);	                            
//	        }     
//	        } catch (Exception e) {
//	            LOG.error("Error while fetching FAR details from MDMS", e);
//	        }
//	    
//	}

	private void getFarDetailsFromMDMS(Plan pl, String occType, String typeOfArea, OccupancyTypeHelper occupancyType) {
	    try {
	    	BigDecimal plotArea = pl.getPlot().getArea() != null ? pl.getPlot().getArea() : BigDecimal.ZERO;
	    	// --- 1. Fetch & Initialize FAR values ---
	    	Double regularPermissibleFar = 0.0;
	    	if(A_AIF.equals(occupancyType.getSubtype().getCode())) {
	    		BigDecimal farValue = calculateFarProgressively(plotArea, pl);
	    		regularPermissibleFar = farValue.setScale(2, RoundingMode.HALF_UP).doubleValue();	    		
	    	}else {	    		
		        regularPermissibleFar = BpaMdmsUtil.extractMdmsValue(
		                pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.NORMAL_FAR, Double.class)
		                .orElse(0.0);
	    	}

	        Double purchasablePermissibleFar = BpaMdmsUtil.extractMdmsValue(
	                pl.getMdmsMasterData().get("masterMdmsData"), MdmsFilter.PURCHASABLE_FAR, Double.class)
	                .orElse(0.0);
	        
	        Double providedFar = Optional.ofNullable(pl.getFarDetails())
	                .map(FarDetails::getProvidedFar)
	                .orElse(0.0);

	        // Logging the fetched values
	        LOG.info("Regular Permissible FAR (MDMS): {}", regularPermissibleFar);
	        LOG.info("Purchasable Permissible FAR (MDMS): {}", purchasablePermissibleFar);

	        // Initialize variables for the plan details
	        Double calculatedProvidedPurchasableFar = 0.0;
	        Double finalTotalPermissibleFar = regularPermissibleFar; // Default total is just regular
	        boolean isAccepted;

	        // --- 2. Purchasable FAR Logic  ---
	        
	        if (Boolean.TRUE.equals(pl.getEdcrRequest().getPurchasableFar())) {
	            LOG.info("Purchasable FAR is enabled. Applying conditional rules.");
	            // Calculate purchasable FAR only if provided FAR exceeds regular permissible
	            if (providedFar > regularPermissibleFar) {
	                calculatedProvidedPurchasableFar = providedFar - regularPermissibleFar;
	                // If provided FAR is greater than regular, total permissible includes purchasable limit
	                finalTotalPermissibleFar = regularPermissibleFar + purchasablePermissibleFar;
	            } 
	        } else {
	            LOG.info("Purchasable FAR is false, processing as normal Rules (Purchasable FAR set to 0.0).");
	            purchasablePermissibleFar = 0.0; // Explicitly set to 0.0 for setting in pl object later
	        }

	        // --- 3. Rounding ---
	        // Check acceptance against the determined total permissible FAR
	        isAccepted = (providedFar <= finalTotalPermissibleFar);
	        regularPermissibleFar = round(regularPermissibleFar);
	        purchasablePermissibleFar = round(purchasablePermissibleFar);
	        providedFar = round(providedFar);
	        calculatedProvidedPurchasableFar = round(calculatedProvidedPurchasableFar);
	        finalTotalPermissibleFar = round(finalTotalPermissibleFar);

	        // Update FAR details in the plan object
	        pl.getFarDetails().setPermissableFar(regularPermissibleFar);
	        pl.getFarDetails().setPurchasableFar(purchasablePermissibleFar);
	        pl.getFarDetails().setProvidedPurchasableFar(calculatedProvidedPurchasableFar);
	        pl.getFarDetails().setProvidedFar(providedFar); 

	        // --- 4. Logging and Building Result ---
	        LOG.info("Matched FAR -> OccupancyType: {}, PlotArea: {}", occType, 
	                 Optional.ofNullable(plotArea).orElse(BigDecimal.ZERO));
	        LOG.info("Regular Permissible FAR: {}, Purchasable FAR Allowed: {}, Provided FAR: {}, "
	                 + "Total Permissible FAR: {}, Accepted: {}", regularPermissibleFar, 
	                 purchasablePermissibleFar, providedFar, finalTotalPermissibleFar, isAccepted);

	        // Build result (assuming buildResult1 is part of the class)
	        buildResult1(pl, occupancyType.getType().getName(), providedFar, purchasablePermissibleFar,
	                     typeOfArea, regularPermissibleFar, isAccepted);

	    } catch (Exception e) {
	        LOG.error("Error while fetching FAR details from MDMS", e);	       
	    }
	}
	
	private Double round(Double value) {
	    if (value == null) {
	        return 0.0;
	    }
	    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}	
	
	public static BigDecimal calculateFarProgressively(BigDecimal plotArea, Plan pl) {

	    LOG.info("=== FAR Progressive Calculation Started ===");

	    if (plotArea == null || plotArea.compareTo(BigDecimal.ZERO) <= 0) {
	        LOG.warn("Invalid Plot Area. FAR = 0");
	        return BigDecimal.ZERO;
	    }

	    // ------------------ LOAD FAR SLABS FROM MDMS ------------------
	    Optional<List> fullListOpt = BpaMdmsUtil.extractMdmsValue(
	            pl.getMdmsMasterData().get("masterMdmsData"),
	            MdmsFilter.LIST_FAR_PATH,
	            List.class
	    );

	    if (!fullListOpt.isPresent()) {
	        LOG.error("No FAR data found in MDMS!");
	        pl.addError("No FAR data found in MDMS", "No FAR data found in MDMS");
	        return BigDecimal.ZERO;
	    }

	    @SuppressWarnings("unchecked")
	    List<Map<String, Object>> slabList = (List<Map<String, Object>>) fullListOpt.get();

	    if (slabList.isEmpty()) {
	        LOG.error("FAR slab list is EMPTY in MDMS!");
	        return BigDecimal.ZERO;
	    }

	    // ------------------ SORT SLABS BY 'upto' ------------------
	    slabList.sort((a, b) -> {
	        BigDecimal ua = new BigDecimal(a.get("upto").toString());
	        BigDecimal ub = new BigDecimal(b.get("upto").toString());
	        if (ua.compareTo(BigDecimal.ZERO) < 0) return 1;   // -1 goes last
	        if (ub.compareTo(BigDecimal.ZERO) < 0) return -1;
	        return ua.compareTo(ub);
	    });

	    LOG.info("Sorted FAR Slabs: {}", slabList);

	    // ------------------ FAR CALCULATION ------------------
	    BigDecimal remaining = plotArea;
	    BigDecimal totalBuiltUpArea = BigDecimal.ZERO;
	    BigDecimal previousUpto = BigDecimal.ZERO;

	    for (int i = 0; i < slabList.size(); i++) {

	        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
	            break;
	        }

	        Map<String, Object> slab = slabList.get(i);

	        BigDecimal upto = new BigDecimal(slab.get("upto").toString());
	        BigDecimal far = new BigDecimal(slab.get("far").toString());

	        boolean isRemainingSlab = upto.compareTo(BigDecimal.ZERO) < 0;

	        BigDecimal slabArea;
	        if (isRemainingSlab) {
	            slabArea = remaining;
	        } else {
	            slabArea = upto.subtract(previousUpto).min(remaining);
	        }

	        BigDecimal slabBuiltUp = slabArea.multiply(far);

	        LOG.info(
	            "Slab {} → PlotArea: {}, FAR: {}, BuiltUp: {}",
	            i + 1, slabArea, far, slabBuiltUp
	        );

	        totalBuiltUpArea = totalBuiltUpArea.add(slabBuiltUp);
	        remaining = remaining.subtract(slabArea);
	        previousUpto = isRemainingSlab ? previousUpto : upto;
	    }

	    // ------------------ EFFECTIVE FAR ------------------
	    BigDecimal effectiveFar = totalBuiltUpArea.divide(
	            plotArea, 4, RoundingMode.HALF_UP
	    );

	    LOG.info("Total Permissible Built-up Area: {}", totalBuiltUpArea);
	    LOG.info("Effective FAR (Ratio): {}", effectiveFar);
	    LOG.info("=== FAR Progressive Calculation Completed ===");

	    return effectiveFar.setScale(2, RoundingMode.HALF_UP);
	}


	public BigDecimal getMinPlotAreaByOccupancy(String occupancyCode) {

	    if (occupancyCode == null || occupancyCode.trim().isEmpty()) {
	        return BigDecimal.ZERO;
	    }

	    switch (occupancyCode.trim()) {
	        // 300 sqm category
	        case "G-G":   // Industrial
	        case "G-F":   // Factory
	        case "G-S":   // Storage
	        case "G-HI":  // Hazard Industries
	        case "G-RSI": // Retail Service Industry
	        case "G-ITP": // IT - Plotted
	        case "G-ITF": // IT - Flatted
	        case "G-TI":  // Textile Industry
	        case "G-KI":  // Knitwear Industry
	        case "G-SI":  // Sports Industry
	            return new BigDecimal("300");

	        // 2000 sqm category
	        case "G-GIP": // General Industry - Plotted
	        case "G-GIF": // General Industry - Flatted
	            return new BigDecimal("2000");

	        // 10000 sqm category
	        case "G-WT":  // Wholesale / Warehouse / IFC
	            return new BigDecimal("10000");
	            
	        case "F-HM":
	            return new BigDecimal("836.43");
	            
	        case "F-PFSF":
	        case "F-PFST":
	        case "F-PS":
	            return new BigDecimal("270");	            
	        case "F-PFSS":
	        case "F-CNGS":	        
	            return new BigDecimal("1080");

	        default:
	            return BigDecimal.ZERO; // or throw exception if needed
	    }
	}

	
}
