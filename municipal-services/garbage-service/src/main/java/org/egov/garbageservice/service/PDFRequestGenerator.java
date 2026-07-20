package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.contract.PDFRequest;
import org.egov.garbageservice.contract.bill.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.egov.garbageservice.contract.bill.BillDetail;
import org.egov.garbageservice.contract.bill.BillAccountDetail;
import java.time.LocalDate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Builds PDFRequest payloads with bill and account data for garbage tax receipts and notices.
 * Assembles template keys, tenant context, and line items from billing and account entities.
 */
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
		grbg.put(
			    "oldGarbageId",
			    grbgAccount.getGrbgOldDetails() != null &&
			    grbgAccount.getGrbgOldDetails().getOldGarbageId() != null
			        ? grbgAccount.getGrbgOldDetails().getOldGarbageId()
			        : "NA"
			);


		Map<String, BigDecimal> grbgTaxMap = new HashMap<>();
		for (GrbgBillTracker grbgBillTrackerObj : grbgBillTracker) {
			if (grbgBillTrackerObj.getGrbgBillAmount() != null) {
				grbgTaxMap.put(grbgBillTrackerObj.getGrbgApplicationId(), grbgBillTrackerObj.getGrbgBillAmount());
			}
		}

		Map<String, String> ownerNameMap = new HashMap<>();
		Map<String, String> unitCategoryMap = new HashMap<>();
		ownerNameMap.put(grbgAccount.getGrbgApplicationNumber(), grbgAccount.getName());
		unitCategoryMap.put(grbgAccount.getGrbgApplicationNumber(),
				escapeHtml(grbgAccount.getGrbgCollectionUnits().get(0).getCategory()));

		for (GarbageAccount childGrbgAccount : grbgAccount.getChildGarbageAccounts()) {
			String appNo = childGrbgAccount.getGrbgApplicationNumber();
			ownerNameMap.put(appNo, escapeHtml(childGrbgAccount.getName()));
			unitCategoryMap.put(appNo, escapeHtml(childGrbgAccount.getGrbgCollectionUnits().get(0).getCategory()));
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
		grbgObj.put("paymentDates", new ArrayList<>());
		grbgObj.put("paymentStatuses", new ArrayList<>());
		grbgObj.put("grbgTaxPlusArrear", new ArrayList<>());

		int count = 1;
		for (int i = 0; i < bill.size(); i++) {
			if (bill.get(i).getConsumerCode().equals(grbgAccount.getGrbgApplicationNumber())
					&& bill.get(i).getConsumerCode().equals(grbgAccount.getGrbgApplicationNumber())) {
				int lastIndex = bill.get(i).getBillDetails().size() - 1;

				grbg.put("billNo", bill.get(i).getBillNumber());
				grbg.put("mobileNo", grbgAccount.getMobileNumber());
				grbg.put("billDueDate", Instant.ofEpochMilli(bill.get(i).getBillDetails().get(lastIndex).getExpiryDate())
						.atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			}
			Bill billObj = bill.get(i);
			String consumerCode = billObj.getConsumerCode();
			grbgObj.get("grbgAccounts").add(consumerCode);

			grbgObj.get("ownerNames").add(escapeHtml(ownerNameMap.getOrDefault(consumerCode, "N/A")));
			grbgObj.get("propertyTypes").add(escapeHtml(unitCategoryMap.getOrDefault(consumerCode, "N/A")));

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
			String paymentDate = Bill.StatusEnum.PAID.equals(billObj.getStatus())
			        ? Instant.ofEpochMilli(billObj.getAuditDetails().getLastModifiedTime())
			                .atZone(ZoneId.systemDefault())
			                .toLocalDateTime()
			                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
			        : "";

			grbgObj.get("paymentDates").add(paymentDate);
			grbgObj.get("paymentStatuses").add(billObj.getStatus().toString());
			grbgObj.get("grbgTaxPlusArrear").add(grbgTaxPlusArrear.toString());
		}

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
		List<String> allPaymentDates = grbgObj.get("paymentDates");
		List<String> allPaymentStatuses = grbgObj.get("paymentStatuses");
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
		gbDetailsTableRow.put("allPaymentDates", allPaymentDates);
		gbDetailsTableRow.put("allPaymentStatuses", allPaymentStatuses);
		gbDetailsTableRow.put("allGrbgTaxPlusArrear", allGrbgTaxPlusArrear);
		
		BigDecimal totalTax = allGrbgTaxPlusArrear.stream().map(BigDecimal::new).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		grbg.put("totalTax", totalTax);
		
		BigDecimal totalArrear = allArrears.stream()
		        .map(BigDecimal::new)
		        .reduce(BigDecimal.ZERO, BigDecimal::add);

		grbg.put("arrearAmount", totalArrear);
		
		BigDecimal totalPaid = BigDecimal.ZERO;
		BigDecimal totalDue = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;

		for (Bill billObj : bill) {

		    BigDecimal amount = billObj.getTotalAmount() != null 
		            ? billObj.getTotalAmount() 
		            : BigDecimal.ZERO;

		    if (billObj.getStatus() == Bill.StatusEnum.PAID) {
		        totalPaid = totalPaid.add(amount);
		        totalAmount = amount;
		    } else if (billObj.getStatus() == Bill.StatusEnum.ACTIVE) {
		        totalDue = totalDue.add(amount);
		        totalAmount = amount;
		    } else if (billObj.getStatus() == Bill.StatusEnum.PARTIALLY_PAID) {
		        BigDecimal billPaid = BigDecimal.ZERO;
		        for (BillDetail billDetail : billObj.getBillDetails()) {
		            if (billDetail.getBillAccountDetails() != null) {
		                for (BillAccountDetail accDetail : billDetail.getBillAccountDetails()) {
		                    BigDecimal adjusted = accDetail.getAdjustedAmount() != null
		                            ? accDetail.getAdjustedAmount()
		                            : BigDecimal.ZERO;
		                    billPaid = billPaid.add(adjusted);
		                }
		            }
		        }
		        totalPaid = totalPaid.add(billPaid);
		        totalDue = totalDue.add(amount.subtract(billPaid));
		        totalAmount = amount;
		    }
		}

		BigDecimal totalRebate = BigDecimal.ZERO;
		BigDecimal totalWithoutRebate = BigDecimal.ZERO;

		for (GrbgBillTracker tracker : grbgBillTracker) {
			if (tracker.getRebateAmount() != null) {
				totalRebate = totalRebate.add(tracker.getRebateAmount());
			}

			if (tracker.getGarbageBillWithoutRebate() != null) {
				totalWithoutRebate = totalWithoutRebate.add(tracker.getGarbageBillWithoutRebate());
			}
		}
		
		grbg.put("amountPaid", totalPaid);
		grbg.put("amountDue", totalDue);
		grbg.put("totalAmount", totalAmount);
		grbg.put("rebateAmount", totalRebate);
		grbg.put("amountWithoutRebate", totalWithoutRebate);

		Map<String, Object> tableRow = new HashMap<>();
		tableRow.put("tag", "GARBAGE_BILL_TABLE_ROW");
		tableRow.put("values", gbDetailsTableRow);

		List<Map<String, Object>> tableRows = new ArrayList<>();
		tableRows.add(tableRow);

		Map<String, Object> tableRowMap = new HashMap<>();
		tableRowMap.put("TABLE_ROW", tableRows);

		dataObject.putAll(tableRowMap);

		grbg.put("billGeneratedDate", Instant.ofEpochMilli(bill.get(0).getBillDate()).atZone(ZoneId.systemDefault())
				.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		grbg.put("date", Instant.ofEpochMilli(grbgBillTracker.get(0).getAuditDetails().getCreatedDate())
				.atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

		grbg.put("billPeriod", grbgBillTracker.get(0).getYear());

		Long minFromPeriod = Long.MAX_VALUE;
		Long maxToPeriod = Long.MIN_VALUE;
		for (Bill billObj : bill) {
		    if (billObj.getBillDetails() != null) {
		        for (BillDetail detail : billObj.getBillDetails()) {

		            if (detail.getFromPeriod() != null) {
		                minFromPeriod = Math.min(minFromPeriod, detail.getFromPeriod());
		            }

		            if (detail.getToPeriod() != null) {
		                maxToPeriod = Math.max(maxToPeriod, detail.getToPeriod());
		            }
		        }
		    }
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		String fromDate = Instant.ofEpochMilli(minFromPeriod)
		        .atZone(ZoneId.systemDefault())
		        .toLocalDate()
		        .format(formatter);

		String toDate = Instant.ofEpochMilli(maxToPeriod)
		        .atZone(ZoneId.systemDefault())
		        .toLocalDate()
		        .format(formatter);
		
		grbg.put("from", fromDate);
		grbg.put("to", toDate);
		
		//CODE to set from date according to from date from bills GOT
		
//		Long fromPeriod = bill.get(0).getBillDetails().get(0).getFromPeriod();
//		int year = Instant.ofEpochMilli(fromPeriod).atZone(ZoneId.systemDefault()).getYear();
//		int month = Instant.ofEpochMilli(fromPeriod).atZone(ZoneId.systemDefault()).getMonthValue();
//		int startYear;
//		if (month <= 3) {
//				startYear = year - 1;
//		} else {
//				startYear = year;
//		}
		
		LocalDate today = LocalDate.now();
		int year = today.getYear();
		int month = today.getMonthValue();
		int startYear;
		if (month <= 3) {
			startYear = year - 1;
		} else {
			startYear = year;
		}
		
		grbg.put("finYear", startYear + "-" + (startYear + 1));
		grbg.put("district", "district");
		grbg.put("wardNumber", grbgAccount.getAddresses().get(0).getWardName());
		grbg.put("unitCategory", escapeHtml(grbgAccount.getGrbgCollectionUnits().get(0).getCategory()));
		grbg.put("address",
				grbgAccount.getAddresses().get(0).getAddress1().concat(", ")
						.concat(grbgAccount.getAddresses().get(0).getWardName()).concat(", ")
						.concat(grbgAccount.getAddresses().get(0).getUlbName()).concat(" (")
						.concat(grbgAccount.getAddresses().get(0).getUlbType()).concat(") ")
						.concat(grbgAccount.getAddresses().get(0).getAdditionalDetail().get("district").asText())
						.concat(", ").concat(grbgAccount.getAddresses().get(0).getPincode()));
		grbg.put("propertyId", grbgAccount.getPropertyId());

		grbg.put("grbgId", grbgAccount.getGrbgApplicationNumber());

		grbg.put("ownerOrOccupier",
				AdditionalDetail.has("propertyOwnerName") && !AdditionalDetail.get("propertyOwnerName").isNull()
						? AdditionalDetail.get("propertyOwnerName").asText()
						: "N/A");
		grbg.put("fatherOrHusbandName",
				AdditionalDetail.has("ownerFatherName") && !AdditionalDetail.get("ownerFatherName").isNull()
						? AdditionalDetail.get("ownerFatherName").asText()
						: "N/A");

		StringBuilder uri = new StringBuilder(applicationPropertiesAndConstant.getFrontEndBaseUri());
		
		uri.append("citizen-payment");
		
		String qr = grbgAccount.getCreated_by().concat("/").concat(grbgAccount.getUuid()).concat("/")
				.concat(null != grbgAccount.getPropertyId() ? grbgAccount.getPropertyId() : "");
		uri.append("/").append(qr);
		grbg.put("qrCodeText", uri);
		dataObject.put("grbg", grbg);

		return PDFRequest.builder().RequestInfo(requestInfoWrapper.getRequestInfo()).key("grbgBillReceipt")
				.tenantId("hp").data(dataObject).build();
	}
	
	private String escapeHtml(String input) {
	    if (input == null) return null;
	    return input.replace("&", "&amp;")
	                .replace("<", "&lt;")
	                .replace(">", "&gt;")
	                .replace("\"", "&quot;")
	                .replace("'", "&#39;");
	}


}
