package org.egov.pt.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.EnumUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.pt.models.Address;
import org.egov.pt.models.ConstructionDetail;
import org.egov.pt.models.Locality;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.Unit;
import org.egov.pt.models.enums.Channel;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Source;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExcelUtils {

	@Autowired
	private ObjectMapper objectMapper;

	public List<Property> parseExcel(MultipartFile file) {

		List<Property> propertyList = new ArrayList<>();
		// Check if the file is empty
		if (file.isEmpty()) {
			throw new CustomException("ERR_EXCEL_FILE_SERVICE", "Error occurred while fetching documents. Message:");
		}
		// Create a Workbook from the uploaded file
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			// Iterate over the rows in the sheet
			for (Row row : sheet) {
				// Skip the header row
				if (row.getRowNum() == 0) {
					continue;
				}
				// populate owner details
				List<OwnerInfo> owners = enrichOwners(row);
				// populate unit details
				List<Unit> units = enrichUnits(row);
				// populate address details
				Address address = enrichAddress(row);
				// populate additional details
				ObjectNode additionalDetails = enrichAdditionalDetails(row);

				Property property = Property.builder().tenantId(getCellValue(row.getCell(1)))
						.propertyType(getCellValue(row.getCell(2))).ownershipCategory(getCellValue(row.getCell(3)))
						.owners(owners)
						.creationReason(
								EnumUtils.isValidEnum(CreationReason.class, getCellValue(row.getCell(21)).toUpperCase())
										? CreationReason.valueOf(getCellValue(row.getCell(21)).toUpperCase())
										: null)
						.usageCategory(getCellValue(row.getCell(22)))
						.noOfFloors(parseLong(getCellValue(row.getCell(23))))
						.landArea(parseDouble(getCellValue(row.getCell(24))))
						.source(EnumUtils.isValidEnum(Source.class, getCellValue(row.getCell(25)).toUpperCase())
								? Source.valueOf(getCellValue(row.getCell(25)).toUpperCase())
								: null)
						.channel(EnumUtils.isValidEnum(Channel.class, getCellValue(row.getCell(26)).toUpperCase())
								? Channel.valueOf(getCellValue(row.getCell(26)).toUpperCase())
								: null)
						.units(units).address(address).additionalDetails(additionalDetails).build();
				propertyList.add(property);
			}
		} catch (Exception e) {
			log.error("Error occurred while converting Resource to MultipartFile", e);
			throw new CustomException("ERR_TECHNICAL",
					"Error occurred while converting Resource to MultipartFile. Message: " + e.getMessage());
		}

		return propertyList;
	}

	private ObjectNode enrichAdditionalDetails(Row row) {
		ObjectNode additionalDetails = objectMapper.createObjectNode();
		// Add propertyId
		additionalDetails.put("propertyId", getCellValue(row.getCell(0)));
		// Get and validate Khasra details arrays
		String[] khataOrKhatauniNos = getSplitCellValue(row.getCell(45));
		String[] khasraNos = getSplitCellValue(row.getCell(46));
		String[] areas = getSplitCellValue(row.getCell(47));
		String[] units = getSplitCellValue(row.getCell(48));

		if (!areKhasraArraysValid(khataOrKhatauniNos, khasraNos, areas, units)) {
			return additionalDetails; // Early return if the arrays are invalid
		}
		// Add multiple Khasra details
		ArrayNode multipleKhasraArray = buildKhasraArray(khataOrKhatauniNos, khasraNos, areas, units);
		additionalDetails.set("multipleKhasra", multipleKhasraArray);
		// Add map reference number
		additionalDetails.put("mapRefNumber", getCellValue(row.getCell(49)));

		return additionalDetails;
	}

	private String[] getSplitCellValue(Cell cell) {
		return getCellValue(cell).split("`");
	}

	private boolean areKhasraArraysValid(String[] khataOrKhatauniNos, String[] khasraNos, String[] areas,
			String[] units) {
		return khataOrKhatauniNos.length == khasraNos.length && khataOrKhatauniNos.length == areas.length
				&& khataOrKhatauniNos.length == units.length;
	}

	private ArrayNode buildKhasraArray(String[] khataOrKhatauniNos, String[] khasraNos, String[] areas,
			String[] units) {
		ArrayNode multipleKhasraArray = objectMapper.createArrayNode();
		for (int i = 0; i < khataOrKhatauniNos.length; i++) {
			ObjectNode khasraObject = objectMapper.createObjectNode();
			khasraObject.put("khataOrKhatauniNo", khataOrKhatauniNos[i]);
			khasraObject.put("khasraNo", khasraNos[i]);
			khasraObject.put("area", areas[i]);
			khasraObject.put("unit", units[i]);

			multipleKhasraArray.add(khasraObject);
		}

		return multipleKhasraArray;
	}

	private Address enrichAddress(Row row) {
		Locality locality = Locality.builder().code(getCellValue(row.getCell(39))).build();

		ObjectNode addressAdditionalDetails = objectMapper.createObjectNode();
		addressAdditionalDetails.put("propertyAddress", getCellValue(row.getCell(40)));
		addressAdditionalDetails.put("ulbName", getCellValue(row.getCell(41)));
		addressAdditionalDetails.put("ulbType", getCellValue(row.getCell(42)));
		addressAdditionalDetails.put("wardNumber", getCellValue(row.getCell(43)));
		addressAdditionalDetails.put("zone", getCellValue(row.getCell(44)));

		Address address = Address.builder().district(getCellValue(row.getCell(37)))
				.pincode(getCellValue(row.getCell(38))).locality(locality).additionalDetails(addressAdditionalDetails)
				.build();
		return address;
	}

	private List<Unit> enrichUnits(Row row) {
		// Get all the column values as a list of strings split by "`"
		List<String[]> columns = IntStream.range(27, 37).mapToObj(i -> getCellValue(row.getCell(i)).split("`"))
				.collect(Collectors.toList());

		// Determine the length of the data (assumed to be the same for all columns)
		int length = columns.get(0).length;

		// Check if all columns have the same length and if the data is valid
		boolean validLength = columns.stream().allMatch(col -> col.length == length);
		boolean validData = columns.stream().flatMap(Arrays::stream).allMatch(this::isValidValue);

		if (validLength && validData) {
			// Process each index and create a list of Unit
			return IntStream.range(0, length).mapToObj(i -> createUnit(columns, i)).collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	// Helper method to create Unit from column data at the given index
	private Unit createUnit(List<String[]> columns, int index) {
		// Extract values from columns at the given index
		String propArea = columns.get(0)[index];
		String propBuildingType = columns.get(1)[index];
		String propType = columns.get(2)[index];
		String propYearOfCons = columns.get(3)[index];
		String useOfBuilding = columns.get(4)[index];

		// Create unit additional details object
		ObjectNode unitAdditionalDetails = createUnitAdditionalDetails(propArea, propBuildingType, propType,
				propYearOfCons, useOfBuilding);

		// Extract and parse construction detail values
		BigDecimal plinthArea = parseBigDecimal(columns.get(5)[index]);
		BigDecimal superBuiltUpArea = parseBigDecimal(columns.get(6)[index]);

		// Create construction detail object
		ConstructionDetail constructionDetail = createConstructionDetail(plinthArea, superBuiltUpArea);

		// Extract unit-specific values
		String usageCategory = columns.get(7)[index];
		Integer floorNo = parseInteger(columns.get(8)[index]);
		String occupancyType = columns.get(9)[index];

		// Create the Unit object
		return createUnit(unitAdditionalDetails, constructionDetail, usageCategory, floorNo, occupancyType);
	}

	// Helper method to create unit additional details
	private ObjectNode createUnitAdditionalDetails(String propArea, String propBuildingType, String propType,
			String propYearOfCons, String useOfBuilding) {
		ObjectNode unitAdditionalDetails = objectMapper.createObjectNode();
		unitAdditionalDetails.put("propArea", propArea);
		unitAdditionalDetails.put("propBuildingType", propBuildingType);
		unitAdditionalDetails.put("propType", propType);
		unitAdditionalDetails.put("propYearOfCons", propYearOfCons);
		unitAdditionalDetails.put("useOfBuilding", useOfBuilding);
		return unitAdditionalDetails;
	}

	// Helper method to create construction detail
	private ConstructionDetail createConstructionDetail(BigDecimal plinthArea, BigDecimal superBuiltUpArea) {
		return ConstructionDetail.builder().plinthArea(plinthArea).superBuiltUpArea(superBuiltUpArea).build();
	}

	// Helper method to parse BigDecimal safely
	private BigDecimal parseBigDecimal(String value) {
		try {
			return (value != null && !value.trim().isEmpty()) ? new BigDecimal(value) : BigDecimal.ZERO;
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO; // Return default value in case of a parsing error
		}
	}

	// Helper method to parse Integer safely
	private Integer parseInteger(String value) {
		try {
			return (value != null && !value.trim().isEmpty()) ? Integer.valueOf(value) : 0;
		} catch (NumberFormatException e) {
			return 0; // Return default value in case of a parsing error
		}
	}

	// Helper method to parse Long safely
	private Long parseLong(String value) {
		try {
			return (value != null && !value.trim().isEmpty()) ? Long.valueOf(value) : 0L;
		} catch (NumberFormatException e) {
			return 0L; // Return default value in case of a parsing error
		}
	}

	// Helper method to parse Long safely
	private Double parseDouble(String value) {
		try {
			return (value != null && !value.trim().isEmpty()) ? Double.valueOf(value) : 0.0;
		} catch (NumberFormatException e) {
			return 0.0; // Return default value in case of a parsing error
		}
	}

	// Helper method to create Unit object
	private Unit createUnit(ObjectNode unitAdditionalDetails, ConstructionDetail constructionDetail,
			String usageCategory, Integer floorNo, String occupancyType) {
		return Unit.builder().additionalDetails(unitAdditionalDetails).constructionDetail(constructionDetail)
				.usageCategory(usageCategory).floorNo(floorNo).occupancyType(occupancyType).build();
	}

	private List<OwnerInfo> enrichOwners(Row row) {
		// Get all the columns as a list of strings split by "`"
		List<String[]> columns = IntStream.range(4, 21).mapToObj(i -> getCellValue(row.getCell(i)).split("`"))
				.collect(Collectors.toList());

		// Determine the length of the data (assumed to be the same for all columns)
		int length = columns.get(0).length;

		// Check if all columns have the same length and all values are valid
		boolean validLength = columns.stream().allMatch(col -> col.length == length);
		boolean validData = columns.stream().flatMap(Arrays::stream).allMatch(this::isValidValue);

//		if (validLength && validData) {
			// Process each index and create a list of OwnerInfo
			return IntStream.range(0, length).mapToObj(i -> createOwnerInfo(columns, i)).collect(Collectors.toList());
//		}

//		return Collections.emptyList();
	}

	// Helper method to check for valid data (non-null and non-empty)
	private boolean isValidValue(String value) {
		// Check if the value is neither null nor empty (additional validation can be
		// added here)
		return value != null && !value.trim().isEmpty();
	}

	private OwnerInfo createOwnerInfo(List<String[]> columns, int index) {
		// Extracting the relevant values for the given index from each column
		String name = columns.get(0)[index];
		String mobileNumber = columns.get(1)[index];
		String ownerType = columns.get(2)[index];

		// Validate fields (e.g., name, mobile number, owner type could have specific
		// validation logic)
		if (!isValidValue(name) || !isValidValue(mobileNumber)) {
			throw new IllegalArgumentException("Invalid data at index " + index);
		}

		ObjectNode ownerAdditionalDetails = createOwnerAdditionalDetails(columns, index);

		return OwnerInfo.builder().name(name).mobileNumber(mobileNumber).ownerType(ownerType)
				.additionalDetails(ownerAdditionalDetails).build();
	}

	private ObjectNode createOwnerAdditionalDetails(List<String[]> columns, int index) {
		// Create an ObjectNode for the additional details
		ObjectNode ownerAdditionalDetails = objectMapper.createObjectNode();
		ownerAdditionalDetails.put("emailId", columns.get(3)[index]);
		ownerAdditionalDetails.put("gender", columns.get(4)[index]);
		ownerAdditionalDetails.put("fatherOrHusbandName", columns.get(5)[index]);
		ownerAdditionalDetails.put("correspondenceAddress", columns.get(6)[index]);
		ownerAdditionalDetails.put("anyOtherCoWorker", columns.get(7)[index]);
		ownerAdditionalDetails.put("ownerPinCode", columns.get(8)[index]);
		ownerAdditionalDetails.put("ownerOldCustomerId", columns.get(9)[index]);
		ownerAdditionalDetails.put("coOwnerName", columns.get(10)[index]);
		ownerAdditionalDetails.put("coOwnerMobile", columns.get(11)[index]);
		ownerAdditionalDetails.put("coOwnerEmail", columns.get(12)[index]);
		ownerAdditionalDetails.put("addressSameAsOwner", columns.get(13)[index]);
		ownerAdditionalDetails.put("coOwnerAddress", columns.get(14)[index]);
		ownerAdditionalDetails.put("coOwnerPinCode", columns.get(15)[index]);
		ownerAdditionalDetails.put("relationWithCoOwner", columns.get(16)[index]);

		return ownerAdditionalDetails;
	}

	private static String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			// Check for large numbers that may have precision issues in Excel
			if (cell.getNumericCellValue() > Long.MAX_VALUE || cell.getNumericCellValue() < Long.MIN_VALUE) {
				return String.valueOf(cell.getNumericCellValue()); // Return as a string directly
			}
			double numericValue = cell.getNumericCellValue();
			return (numericValue % 1 == 0) ? String.valueOf((long) numericValue) : String.valueOf(numericValue);
		case BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		default:
			return "";
		}
	}

}
