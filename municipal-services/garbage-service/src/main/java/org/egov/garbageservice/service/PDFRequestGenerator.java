package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.contract.PDFRequest;
import org.egov.garbageservice.contract.bill.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PDFRequestGenerator {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private GrbgConstants applicationPropertiesAndConstant;

	public PDFRequest generatePdfRequestForBill(RequestInfoWrapper requestInfoWrapper, GarbageAccount grbgAccount,
			List<Bill> bill, List<GrbgBillTracker> grbgBillTracker) {

		Map<String, Object> dataObject = new HashMap<>();
		Map<String, Object> grbg = new HashMap<>();

		JsonNode AdditionalDetail = objectMapper.valueToTree(grbgAccount.getAdditionalDetail());
		grbg.put("ulbName", grbgAccount.getAddresses().get(0).getUlbName());
		grbg.put("ulbType", grbgAccount.getAddresses().get(0).getUlbType());

		Map<String, BigDecimal> grbgTaxMap = new HashMap<>();
		for (GrbgBillTracker grbgBillTrackerObj : grbgBillTracker) {
		    if (grbgBillTrackerObj.getGrbgBillAmount() != null) {
		        grbgTaxMap.put(grbgBillTrackerObj.getGrbgApplicationId(),
		                grbgBillTrackerObj.getGrbgBillAmount());
		    }
		}

		Map<String, String> ownerNameMap = new HashMap<>();
		Map<String, String> unitCategoryMap = new HashMap<>();
		ownerNameMap.put(grbgAccount.getGrbgApplicationNumber(), grbgAccount.getName());
		unitCategoryMap.put(grbgAccount.getGrbgApplicationNumber(),
				grbgAccount.getGrbgCollectionUnits().get(0).getCategory());

		for (GarbageAccount childGrbgAccount : grbgAccount.getChildGarbageAccounts()) {
			String appNo = childGrbgAccount.getGrbgApplicationNumber();
			ownerNameMap.put(appNo, childGrbgAccount.getName());
			unitCategoryMap.put(appNo, childGrbgAccount.getGrbgCollectionUnits().get(0).getCategory());
		}
		
		Map<String, List<String>> grbgObj = new HashMap<>();

		// initialize keys with empty lists
		grbgObj.put("serialNo", new ArrayList<>());
		grbgObj.put("grbgAccounts", new ArrayList<>());
		grbgObj.put("ownerNames", new ArrayList<>());
		grbgObj.put("propertyTypes", new ArrayList<>());
		grbgObj.put("units", new ArrayList<>());
		grbgObj.put("billNos", new ArrayList<>());
		grbgObj.put("grbgTaxs", new ArrayList<>());
		grbgObj.put("arrears", new ArrayList<>());
		grbgObj.put("interest", new ArrayList<>());
		grbgObj.put("grbgTaxPlusArrear", new ArrayList<>());


		int count = 1;
		for (int i = 0; i < bill.size(); i++) {
			if (bill.get(i).getConsumerCode().equals(grbgAccount.getGrbgApplicationNumber()) 
			        && bill.get(i).getConsumerCode().equals(grbgAccount.getGrbgApplicationNumber())) {
			    
			    grbg.put("billNo", bill.get(i).getBillNumber());
			}
			Bill billObj = bill.get(i);
			String consumerCode = billObj.getConsumerCode();
			grbgObj.get("grbgAccounts").add(consumerCode);

		    grbgObj.get("ownerNames").add(ownerNameMap.getOrDefault(consumerCode, "N/A"));
		    grbgObj.get("propertyTypes").add(unitCategoryMap.getOrDefault(consumerCode, "N/A"));

			String unit = "1";
		    BigDecimal tax = grbgTaxMap.getOrDefault(consumerCode, BigDecimal.ZERO);
			BigDecimal arrear = billObj.getTotalAmount().subtract(tax);
			BigDecimal interest = BigDecimal.ZERO;
			BigDecimal grbgTaxPlusArrear = tax.add(arrear);

		    grbgObj.get("serialNo").add(String.valueOf(count++));
			grbgObj.get("units").add(unit);
			grbgObj.get("billNos").add(billObj.getBillNumber());
			grbgObj.get("grbgTaxs").add(tax.toString());
			grbgObj.get("arrears").add(arrear.toString());
			grbgObj.get("interest").add(interest.toString());
			grbgObj.get("grbgTaxPlusArrear").add(grbgTaxPlusArrear.toString());
		}

		
		
		grbg.put("date", Instant.ofEpochMilli(grbgBillTracker.get(0).getAuditDetails().getCreatedDate())
				.atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

		grbg.put("billPeriod", grbgBillTracker.get(0).getYear());

		grbg.put("from", grbgBillTracker.get(0).getFromDate());

		grbg.put("to", grbgBillTracker.get(0).getToDate());

		grbg.put("billDueDate", Instant.ofEpochMilli(bill.get(0).getBillDetails().get(0).getExpiryDate())
				.atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

		int year = Integer.parseInt(grbgBillTracker.get(0).getYear());
		grbg.put("finYear", year + "-" + (year + 1));
		grbg.put("finYear", grbgBillTracker.get(0).getYear() + "-" + (year + 1));
		grbg.put("district", "district");
		grbg.put("wardNumber", "wardname");
		grbg.put("unitCategory", grbgAccount.getGrbgCollectionUnits().get(0).getCategory());
		grbg.put("address",
				grbgAccount.getAddresses().get(0).getAddress1().concat(", ")
						.concat(grbgAccount.getAddresses().get(0).getWardName()).concat(", ")
						.concat(grbgAccount.getAddresses().get(0).getUlbName()).concat(" (")
						.concat(grbgAccount.getAddresses().get(0).getUlbType()).concat(") ")
						.concat(grbgAccount.getAddresses().get(0).getAdditionalDetail().get("district").asText())
						.concat(", ").concat(grbgAccount.getAddresses().get(0).getPincode()));
		grbg.put("propertyId", grbgAccount.getPropertyId());

		grbg.put("grbgId", grbgAccount.getGrbgApplicationNumber());

		grbg.put("ownerOrOccupier", AdditionalDetail.get("propertyOwnerName").asText());
		
		StringBuilder uri = new StringBuilder(applicationPropertiesAndConstant.getFrontEndBaseUri());
		uri.append("citizen-payment");
		String qr = grbgAccount.getCreated_by().concat("/").concat(grbgAccount.getUuid()).concat("/")
				.concat(null != grbgAccount.getPropertyId() ? grbgAccount.getPropertyId() : "");
		uri.append("/").append(qr);
		grbg.put("qrCodeText", uri);
		

		// TODO END

		String paymentStatus = "";
		String paymentDate = "";
//		if (bill.getStatus().equals(StatusEnum.PAID)) {
//			amountPaid = bill.getTotalAmount();
//			paymentStatus = "Success";
//			paymentDate = ""; // TODO blank
//		}
		grbg.put("paymentStatus", paymentStatus);
		grbg.put("billGeneratedDate", Instant.ofEpochMilli(bill.get(0).getBillDate()).atZone(ZoneId.systemDefault())
				.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		grbg.put("paymentDate", paymentDate);
		
		Map<String, Object> gbDetailsTableRow = new HashMap<>();
		List<String> allSerialNo = grbgObj.get("serialNo");
		List<String> allGrbgAccounts = grbgObj.get("grbgAccounts");
		List<String> allOwnerNames = grbgObj.get("ownerNames");
		List<String> allPropertyTypes = grbgObj.get("propertyTypes");
		List<String> allUnits = grbgObj.get("units");
		List<String> allBillNos = grbgObj.get("billNos");
		List<String> allGrbgTaxs = grbgObj.get("grbgTaxs");
		List<String> allArrears = grbgObj.get("arrears");
		List<String> allInterest = grbgObj.get("interest");
		List<String> allGrbgTaxPlusArrear = grbgObj.get("grbgTaxPlusArrear");

		gbDetailsTableRow.put("allSerialNo", allSerialNo);
		gbDetailsTableRow.put("allGrbgAccounts", allGrbgAccounts);
		gbDetailsTableRow.put("allOwnerNames", allOwnerNames);
		gbDetailsTableRow.put("allPropertyTypes", allPropertyTypes);
		gbDetailsTableRow.put("allUnits", allUnits);
		gbDetailsTableRow.put("allBillNos", allBillNos);
		gbDetailsTableRow.put("allGrbgTaxs", allGrbgTaxs);
		gbDetailsTableRow.put("allArrears", allArrears);
		gbDetailsTableRow.put("allInterest", allInterest);
		gbDetailsTableRow.put("allGrbgTaxPlusArrear", allGrbgTaxPlusArrear);
		BigDecimal totalTax = allGrbgTaxPlusArrear.stream()
		        .map(BigDecimal::new)      
		        .reduce(BigDecimal.ZERO, BigDecimal::add);  
		grbg.put("totalTax", totalTax);

		Map<String, Object> tableRow = new HashMap<>();
		tableRow.put("tag", "GARBAGE_BILL_TABLE_ROW");
		tableRow.put("values", gbDetailsTableRow);

		List<Map<String, Object>> tableRows = new ArrayList<>();
		tableRows.add(tableRow);

		Map<String, Object> tableRowMap = new HashMap<>();
		tableRowMap.put("TABLE_ROW", tableRows);

		dataObject.putAll(tableRowMap);
		dataObject.put("grbg", grbg);

		return PDFRequest.builder().RequestInfo(requestInfoWrapper.getRequestInfo()).key("grbgBillReceipt")
				.tenantId("hp").data(dataObject).build();
	}

}
