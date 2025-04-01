package org.egov.garbageservice.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GrbgAddress;
import org.egov.garbageservice.model.GrbgCollectionUnit;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExcelUtils {

	@Autowired
	private ObjectMapper objectMapper;

	public List<GarbageAccount> parseExcel(MultipartFile file) {
//
		List<GarbageAccount> garbageAccountList = new ArrayList<>();
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

				// populate address details
				List<GrbgAddress> addresses = enrichAddresses(row);
				// populate garbage collection unit details
				List<GrbgCollectionUnit> grbgCollectionUnits = enrichGrbgCollectionUnits(row);
				// populate child garbage account details
				List<GarbageAccount> childGarbageAccounts = enrichChildGarbageAccounts(row);
				// populate additional details
				ObjectNode additionalDetails = enrichAdditionalDetails(row);

				GarbageAccount garbageAccount = GarbageAccount.builder().tenantId(getCellValue(row.getCell(0)))
						.name(getCellValue(row.getCell(1))).mobileNumber(getCellValue(row.getCell(2)))
						.gender(getCellValue(row.getCell(3))).emailId(getCellValue(row.getCell(4)))
						.isOwner(Boolean.valueOf(getCellValue(row.getCell(5)))).addresses(addresses)
						.grbgCollectionUnits(grbgCollectionUnits).childGarbageAccounts(childGarbageAccounts)
						.additionalDetail(additionalDetails).build();
				garbageAccountList.add(garbageAccount);
			}
		} catch (Exception e) {
			log.error("Error occurred while converting Resource to MultipartFile", e);
			throw new CustomException("ERR_TECHNICAL",
					"Error occurred while converting Resource to MultipartFile. Message: " + e.getMessage());
		}

		return garbageAccountList;
	}

	private ObjectNode enrichAdditionalDetails(Row row) {
		ObjectNode additionalDetails = objectMapper.createObjectNode();
		additionalDetails.put("propertyOwnerName", getCellValue(row.getCell(24)));
		additionalDetails.put("ownerFatherName", getCellValue(row.getCell(25)));
		additionalDetails.put("applicantName", getCellValue(row.getCell(26)));
		additionalDetails.put("applicantEmail", getCellValue(row.getCell(27)));
		additionalDetails.put("applicantPhoneNumber", getCellValue(row.getCell(28)));

		return additionalDetails;
	}

	private List<GarbageAccount> enrichChildGarbageAccounts(Row row) {
		// Get all the columns as a list of strings split by "`"
		List<String[]> columns = IntStream.range(17, 24).mapToObj(i -> getCellValue(row.getCell(i)).split("`"))
				.collect(Collectors.toList());

		// Determine the length of the data (assumed to be the same for all columns)
		int length = columns.get(0).length;

		// Check if all columns have the same length and all values are valid
		boolean validLength = columns.stream().allMatch(col -> col.length == length);
		boolean validData = columns.stream().flatMap(Arrays::stream).allMatch(this::isValidValue);

		if (validLength && validData) {
			// Process each index and create a list of ChildGarbageAccount
			return IntStream.range(0, length).mapToObj(i -> createChildGarbageAccount(columns, i))
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	private GarbageAccount createChildGarbageAccount(List<String[]> columns, int index) {
		// Extracting the relevant values for the given index from each column
		String isOwner = columns.get(0)[index];
		String name = columns.get(1)[index];
		String gender = columns.get(2)[index];
		String mobileNumber = columns.get(3)[index];

		// Validate fields
		if (!isValidValue(isOwner) || !isValidValue(name) || !isValidValue(gender) || !isValidValue(mobileNumber)) {
			throw new IllegalArgumentException("Invalid data at index " + index);
		}

		List<GrbgCollectionUnit> childGrbgCollectionUnits = enrichChildGrbgCollectionUnits(columns, index);

		return GarbageAccount.builder().isOwner(Boolean.valueOf(isOwner)).name(name).gender(gender)
				.mobileNumber(mobileNumber).grbgCollectionUnits(childGrbgCollectionUnits).build();
	}

	private List<GrbgCollectionUnit> enrichChildGrbgCollectionUnits(List<String[]> columns, int index) {
//		// Get all the column values as a list of strings split by "`"
//		List<String[]> columns = IntStream.range(13, 17).mapToObj(i -> getCellValue(row.getCell(i)).split("`"))
//				.collect(Collectors.toList());

		// Determine the length of the data (assumed to be the same for all columns)
		int length = columns.get(4).length;

		// Check if all columns have the same length and if the data is valid
		boolean validLength = columns.stream().allMatch(col -> col.length == length);
		boolean validData = columns.stream().flatMap(Arrays::stream).allMatch(this::isValidValue);

		if (validLength && validData) {
			// Process each index and create a list of GrbgChildCollectionUnit
			return IntStream.range(0, length).mapToObj(i -> createChildGrbgCollectionUnit(columns, i))
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	private GrbgCollectionUnit createChildGrbgCollectionUnit(List<String[]> columns, int index) {
		// Extracting the relevant values for the given index from each column
		String category = columns.get(4)[index];
		String subCategory = columns.get(5)[index];
		String subCategoryType = columns.get(6)[index];

		// Validate fields
		if (!isValidValue(category) || !isValidValue(subCategory) || !isValidValue(subCategoryType)) {
			throw new IllegalArgumentException("Invalid data at index " + index);
		}

		return GrbgCollectionUnit.builder().category(category).subCategory(subCategory).subCategoryType(subCategoryType)
				.build();
	}

	private List<GrbgCollectionUnit> enrichGrbgCollectionUnits(Row row) {
		// Get all the column values as a list of strings split by "`"
		List<String[]> columns = IntStream.range(13, 17).mapToObj(i -> getCellValue(row.getCell(i)).split("`"))
				.collect(Collectors.toList());

		// Determine the length of the data (assumed to be the same for all columns)
		int length = columns.get(0).length;

		// Check if all columns have the same length and if the data is valid
		boolean validLength = columns.stream().allMatch(col -> col.length == length);
		boolean validData = columns.stream().flatMap(Arrays::stream).allMatch(this::isValidValue);

		if (validLength && validData) {
			// Process each index and create a list of GrbgCollectionUnit
			return IntStream.range(0, length).mapToObj(i -> createGrbgCollectionUnit(columns, i))
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	// Helper method to create GrbgCollectionUnit from column data at the given
	// index
	private GrbgCollectionUnit createGrbgCollectionUnit(List<String[]> columns, int index) {
		// Extracting the relevant values for the given index from each column
		String unitType = columns.get(0)[index];
		String category = columns.get(1)[index];
		String subCategory = columns.get(2)[index];
		String subCategoryType = columns.get(3)[index];

		// Validate fields
		if (!isValidValue(unitType) || !isValidValue(category) || !isValidValue(subCategory)
				|| !isValidValue(subCategoryType)) {
			throw new IllegalArgumentException("Invalid data at index " + index);
		}

		return GrbgCollectionUnit.builder().unitType(unitType).category(category).subCategory(subCategory)
				.subCategoryType(subCategoryType).build();
	}

	private List<GrbgAddress> enrichAddresses(Row row) {
		// Get all the columns as a list of strings split by "`"
		List<String[]> columns = IntStream.range(6, 13).mapToObj(i -> getCellValue(row.getCell(i)).split("`"))
				.collect(Collectors.toList());

		// Determine the length of the data (assumed to be the same for all columns)
		int length = columns.get(0).length;

		// Check if all columns have the same length and all values are valid
		boolean validLength = columns.stream().allMatch(col -> col.length == length);
		boolean validData = columns.stream().flatMap(Arrays::stream).allMatch(this::isValidValue);

		if (validLength && validData) {
			// Process each index and create a list of Address
			return IntStream.range(0, length).mapToObj(i -> createAddress(columns, i)).collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	private GrbgAddress createAddress(List<String[]> columns, int index) {
		// Extracting the relevant values for the given index from each column
		String zone = columns.get(0)[index];
		String address1 = columns.get(1)[index];
		String ulbName = columns.get(2)[index];
		String ulbType = columns.get(3)[index];
		String wardName = columns.get(4)[index];
		String pincode = columns.get(5)[index];

		// Validate fields
		if (!isValidValue(zone) || !isValidValue(address1) || !isValidValue(ulbName) || !isValidValue(ulbType)
				|| !isValidValue(wardName) || !isValidValue(pincode)) {
			throw new IllegalArgumentException("Invalid data at index " + index);
		}

		ObjectNode addressAdditionalDetails = createAddressAdditionalDetails(columns, index);

		return GrbgAddress.builder().zone(zone).address1(address1).ulbName(ulbName).ulbType(ulbType).wardName(wardName)
				.pincode(pincode).additionalDetail(addressAdditionalDetails).build();
	}

	private ObjectNode createAddressAdditionalDetails(List<String[]> columns, int index) {
		// Create an ObjectNode for the additional details
		ObjectNode addressAdditionalDetails = objectMapper.createObjectNode();
		addressAdditionalDetails.put("district", columns.get(6)[index]);

		return addressAdditionalDetails;
	}

	// Helper method to check for valid data (non-null and non-empty)
	private boolean isValidValue(String value) {
		// Check if the value is neither null nor empty (additional validation can be
		// added here)
		return value != null && !value.trim().isEmpty();
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
