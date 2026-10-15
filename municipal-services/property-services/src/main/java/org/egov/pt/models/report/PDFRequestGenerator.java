package org.egov.pt.models.report;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.egov.pt.models.Unit;
import org.egov.pt.models.collection.Bill;
import org.egov.pt.models.collection.Bill.StatusEnum;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.egov.pt.models.collection.BillDetail;
import org.egov.pt.models.collection.BillAccountDetail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.CommonUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PDFRequestGenerator {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PropertyRepository repository;

	@Autowired
	@Qualifier("propertyUtil")
	private CommonUtils commonUtils;

	public PDFRequest generatePdfRequest(RequestInfoWrapper requestInfoWrapper, Property property,
			PtTaxCalculatorTracker ptTaxCalculatorTracker, Bill bill, Map<String, Integer> tenantIdDaysMap) {

		Map<String, Object> dataObject = new HashMap<>();
		Map<String, String> ptbr = new HashMap<>();

		JsonNode addressAdditionalDetails = objectMapper.valueToTree(property.getAddress().getAdditionalDetails());

		ptbr.put("ulbType", addressAdditionalDetails.get("ulbType").asText());
		ptbr.put("ulbName", addressAdditionalDetails.get("ulbName").asText());
		ptbr.put("billNo", bill.getBillNumber());
		ptbr.put("mobileNumber", bill.getMobileNumber());
		ptbr.put(
			    "billDueDate",
			    Instant.ofEpochMilli(bill.getBillDetails().get(0).getExpiryDate())
			        .atZone(ZoneId.systemDefault())
			        .toLocalDate()
			        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
			);

		ptbr.put("date",
				Instant.ofEpochMilli(ptTaxCalculatorTracker.getAuditDetails().getCreatedTime())
						.atZone(ZoneId.systemDefault()).toLocalDateTime()
						.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

		ptbr.put("billPeriod", ptTaxCalculatorTracker.getFinancialYear());

		ptbr.put("from", ptTaxCalculatorTracker.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		ptbr.put("to", ptTaxCalculatorTracker.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		ptbr.put("district", property.getAddress().getDistrict());
		ptbr.put("wardNumber", addressAdditionalDetails.get("wardNumber").asText());

		ptbr.put("address",
				Stream.of(addressAdditionalDetails.get("propertyAddress"), addressAdditionalDetails.get("wardNumber"),
						addressAdditionalDetails.get("ulbType"), addressAdditionalDetails.get("ulbName"),
						property.getAddress().getDistrict(), property.getAddress().getPincode())
						.filter(Objects::nonNull).map(Object::toString).map(s -> s.replace("\"", ""))
						.filter(s -> !s.isEmpty()).collect(Collectors.joining(", ")));

		ptbr.put("customerId", property.getOwners().stream().map(owner -> {
			if (owner.getAdditionalDetails() == null)
				return null;
			Object nameObj = owner.getAdditionalDetails().get("ownerOldCustomerId");
			if (nameObj == null)
				return null;
			return nameObj.toString().replaceAll("\"", "");
		}).filter(name -> name != null && !name.isEmpty()).collect(Collectors.joining(", ")));

		ptbr.put("propertyId", property.getPropertyId());

		ptbr.put("ownerOrOccupier",
			    property.getOwners().stream()
			        .flatMap(owner -> {
			            List<String> names = new ArrayList<>();

			            if (owner.getPropertyOwnerName() != null) {
			                names.add(owner.getPropertyOwnerName());
			            }

							if (owner.getAdditionalDetails() != null
									&& owner.getAdditionalDetails().has("coOwnerName")) {

								String anyOtherCoWorker = owner.getAdditionalDetails().has("anyOtherCoWorker")
										? owner.getAdditionalDetails().get("anyOtherCoWorker").asText()
										: null;
								
								if (!"No".equalsIgnoreCase(anyOtherCoWorker)) {
									String coOwner = owner.getAdditionalDetails().get("coOwnerName").asText();
									if (coOwner != null && !coOwner.isEmpty()) {
										names.add(coOwner);
									}

								}
							}

							return names.stream();
						})
			        .distinct()
			        .collect(Collectors.joining(" & "))
			);

		ptbr.put("fatherOrHusbandName", property.getOwners().stream().map(owner -> {
			if (owner.getAdditionalDetails() == null)
				return null;
			Object nameObj = owner.getAdditionalDetails().get("fatherOrHusbandName");
			if (nameObj == null)
				return null;
			return nameObj.toString().replaceAll("\"", "");
		}).filter(name -> name != null && !name.isEmpty()).collect(Collectors.joining(", ")));

		
		String fatherOrHusbandName = property.getOwners().stream().map(owner -> {
			if (owner.getAdditionalDetails() == null)
				return null;

			Object nameObj = owner.getAdditionalDetails().get("fatherOrHusbandName");
			if (nameObj == null)
				return null;

			return nameObj.toString().replaceAll("\"", "");
		}).filter(name -> name != null && !name.isEmpty()).collect(Collectors.joining(", "));

		ptbr.put("fatherOrHusbandName", escapeHtml(fatherOrHusbandName));
		ptbr.put("noOfStories", String.valueOf(property.getNoOfFloors()));
		ptbr.put("buildingNo", ""); // TODO blank

		// table data generation
		int conut = 1;
		List<String> slNos = new ArrayList<>();
		List<String> f1Values = new ArrayList<>();
		List<String> f2Values = new ArrayList<>();
		List<String> f3Values = new ArrayList<>();
		List<String> f4Values = new ArrayList<>();
		List<String> f5Values = new ArrayList<>();
		List<String> plinthAreas = new ArrayList<>();
		List<String> taxCalculated = new ArrayList<>();
		List<String> floorNos = new ArrayList<>();
		BigDecimal plinthAreaTotal = BigDecimal.ZERO;

		JsonNode additionalDetailsNode = ptTaxCalculatorTracker.getAdditionalDetails(); // This is a JsonNode (array)
		
		for (Unit unit : property.getUnits()) {
			JsonNode matchedNode = StreamSupport.stream(additionalDetailsNode.spliterator(), false)
			        .filter(node -> node.has("unitId") && node.get("unitId").asText().equals(unit.getId()))
			        .findFirst()
			        .orElse(null);
			if (matchedNode !=null) { 
				JsonNode unitAdditionalDetails = objectMapper.valueToTree(unit.getAdditionalDetails());
				slNos.add(String.valueOf(conut++));
				f1Values.add(escapeHtml(addressAdditionalDetails.get("zone").asText()));
				f2Values.add(escapeHtml(unitAdditionalDetails.get("propBuildingType").asText()));
				f3Values.add(escapeHtml(unitAdditionalDetails.get("propYearOfCons").asText()));
				f4Values.add(escapeHtml(unitAdditionalDetails.get("propType").asText()));
				f5Values.add(escapeHtml(unitAdditionalDetails.get("useOfBuilding").asText()));
				plinthAreas.add(escapeHtml(unitAdditionalDetails.get("propArea").asText()));
				taxCalculated.add(matchedNode.get("propertyTaxCalculated").asText());
				floorNos.add(escapeHtml(unit.getFloorNo().toString()));
			}
		}	
		Map<String, Object> ptDetailsTableRow = new HashMap<>();

		ptDetailsTableRow.put("slNo", slNos);
		ptDetailsTableRow.put("f1", f1Values);
		ptDetailsTableRow.put("f2", f2Values);
		ptDetailsTableRow.put("f3", f3Values);
		ptDetailsTableRow.put("f4", f4Values);
		ptDetailsTableRow.put("f5", f5Values);
		ptDetailsTableRow.put("f5", f5Values);
		ptDetailsTableRow.put("plinthArea", plinthAreas);
		ptDetailsTableRow.put("taxCalculated", taxCalculated);
		ptDetailsTableRow.put("floorNo", floorNos);
		plinthAreaTotal = plinthAreas.stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, Object> tableRow = new HashMap<>();
		tableRow.put("tag", "PROPERTY_TAX_BILL_TABLE_ROW");
		tableRow.put("values", ptDetailsTableRow);
		
		int arrearCount = 1;
		List<String> arrearSlNos = new ArrayList<>();
		List<String> financialYears = new ArrayList<>();
		List<String> arrearTotals = new ArrayList<>();

		if (bill.getBillDetails() != null) {

		    for (BillDetail billDetail : bill.getBillDetails()) {
		        arrearSlNos.add(String.valueOf(arrearCount++));
		        LocalDate fromDate = Instant.ofEpochMilli(billDetail.getFromPeriod())
		                .atZone(ZoneId.systemDefault())
		                .toLocalDate();

		        LocalDate toDate = Instant.ofEpochMilli(billDetail.getToPeriod())
		                .atZone(ZoneId.systemDefault())
		                .toLocalDate();

		        String financialYear = fromDate.getYear() + "-" + toDate.getYear();
		        financialYears.add(financialYear);
		        BigDecimal total = BigDecimal.ZERO;
		        if (billDetail.getBillAccountDetails() != null) {
		            for (BillAccountDetail acc : billDetail.getBillAccountDetails()) {
		                BigDecimal amt = acc.getAmount() != null ? acc.getAmount() : BigDecimal.ZERO;
		                total = total.add(amt);
		            }
		        }
		        arrearTotals.add(total.toString());
		    }
		}
		
		// Make arrear values a map of column -> list to match the expected table shape
		Map<String, Object> arrearValuesMap = new HashMap<>();
		arrearValuesMap.put("slNo", arrearSlNos);
		arrearValuesMap.put("financialYear", financialYears);
		arrearValuesMap.put("totalTax", arrearTotals);

		Map<String, Object> arrearRowWrapper = new HashMap<>();
		arrearRowWrapper.put("tag", "ARREAR_BILL_TABLE_ROW");
		arrearRowWrapper.put("values", arrearValuesMap);

		List<Map<String, Object>> tableRows = new ArrayList<>();
		if (ptTaxCalculatorTracker.getType().equals("ARREAR")) {
		    tableRows.add(arrearRowWrapper);
		} else {
		    tableRows.add(tableRow);
		}
		
		Map<String, Object> tableRowMap = new HashMap<>();
		tableRowMap.put("TABLE_ROW", tableRows);
		ptbr.put("plinthAreaTotal", String.valueOf(plinthAreaTotal));
		BigDecimal propertyTax = ptTaxCalculatorTracker.getPropertyTaxWithoutRebate();
		ptbr.put("propertyTax", String.valueOf(propertyTax));
		BigDecimal penalty = null != ptTaxCalculatorTracker.getPenaltyAmount()
				? ptTaxCalculatorTracker.getPenaltyAmount()
				: new BigDecimal("0.00");
		ptbr.put("penalty", String.valueOf(penalty));
		
		BigDecimal arrear = bill.getTotalAmount().subtract(ptTaxCalculatorTracker.getPropertyTax());

		ptbr.put("arrear", String.valueOf(arrear));

		ptbr.put("propertyTaxPlusArrear", String.valueOf(propertyTax.add(arrear)));

		ptbr.put("rebateDays", String.valueOf(tenantIdDaysMap.getOrDefault(property.getTenantId(), 15)));

		BigDecimal rebate = ptTaxCalculatorTracker.getRebateAmount();
		ptbr.put("rebate", String.valueOf(rebate));

		ptbr.put("totalTax", String.valueOf(bill.getTotalAmount()));

		BigDecimal amountPaid = BigDecimal.ZERO;
		BigDecimal amountDue = BigDecimal.ZERO;
		BigDecimal advancePaid = ptTaxCalculatorTracker.getAdvancePaid() != null
				? ptTaxCalculatorTracker.getAdvancePaid()
				: BigDecimal.ZERO;
		String paymentStatus = "";
		String paymentDate = "";
		BigDecimal totalAmount = bill.getTotalAmount() != null
		        ? bill.getTotalAmount()
		        : BigDecimal.ZERO;

		if (bill.getStatus() == StatusEnum.PAID) {
		    amountPaid = totalAmount;
		} else if (bill.getStatus() == StatusEnum.ACTIVE
		        || bill.getStatus() == StatusEnum.ADVANCE_ADJUSTED) {
		    amountDue = totalAmount;
		} else if (bill.getStatus() == StatusEnum.PARTIALLY_PAID) {

		    for (BillDetail billDetail : bill.getBillDetails()) {
		        if (billDetail.getBillAccountDetails() != null) {
		            for (BillAccountDetail accDetail : billDetail.getBillAccountDetails()) {
		                BigDecimal adjusted = accDetail.getAdjustedAmount() != null
		                        ? accDetail.getAdjustedAmount()
		                        : BigDecimal.ZERO;
		                amountPaid = amountPaid.add(adjusted);
		            }
		        }
		    }
		    
		    if (arrear.compareTo(BigDecimal.ZERO) < 0) {
		        amountPaid = amountPaid.add(arrear);
		    }

		    amountDue = totalAmount.subtract(amountPaid);
		}

		if (Boolean.TRUE.equals(ptTaxCalculatorTracker.getIsPaymentProcessing())) {
			BigDecimal chequeTxnAmount = BigDecimal.ZERO;
			JsonNode trackerAdditionalDetails = ptTaxCalculatorTracker.getAdditionalDetails();
			if (trackerAdditionalDetails != null && trackerAdditionalDetails.isArray()
					&& !trackerAdditionalDetails.isEmpty()) {
				JsonNode firstDetail = trackerAdditionalDetails.get(0);
				if (firstDetail.has("chequeTxnAmount") && !firstDetail.get("chequeTxnAmount").isNull()) {
					chequeTxnAmount = firstDetail.get("chequeTxnAmount").decimalValue();
				}
			}
			BigDecimal adjustedAmount = BigDecimal.ZERO;
			if (bill.getBillDetails() != null) {
				for (BillDetail billDetail : bill.getBillDetails()) {
					if (billDetail.getBillAccountDetails() != null) {
						for (BillAccountDetail accDetail : billDetail.getBillAccountDetails()) {
							if (accDetail.getAdjustedAmount() != null) {
								adjustedAmount = adjustedAmount.add(accDetail.getAdjustedAmount());
							}
						}
					}
				}
			}
			BigDecimal totalPaidAmount = chequeTxnAmount.add(adjustedAmount);
			if (totalPaidAmount.compareTo(bill.getTotalAmount()) < 0) {
				paymentStatus = "Partially Paid";
			} else {
				paymentStatus = "Paid";
			}
		} else if (bill.getStatus().equals(StatusEnum.PAID)) {
			paymentStatus = "Success";
		} else if (bill.getStatus().equals(StatusEnum.PARTIALLY_PAID)) {
			paymentStatus = "Partially Paid";
		} else {
			paymentStatus = "Pending";
		}
		
		if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
		    paymentDate = Instant.ofEpochMilli(bill.getAuditDetails().getLastModifiedTime())
		            .atZone(ZoneId.systemDefault())
		            .toLocalDateTime()
		            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}
		ptbr.put("amountDue", String.valueOf(amountDue));
		ptbr.put("amountPaid", String.valueOf(amountPaid));
		ptbr.put("paymentStatus", paymentStatus);
		ptbr.put("billGeneratedDate", Instant.ofEpochMilli(bill.getBillDate()).atZone(ZoneId.systemDefault())
				.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		ptbr.put("paymentDueDate", Instant.ofEpochMilli(bill.getBillDate()).atZone(ZoneId.systemDefault())
				.toLocalDateTime().plusDays(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		ptbr.put("paymentDate", paymentDate);
		ptbr.put("advancePaid", advancePaid.toString());

		dataObject.putAll(tableRowMap);
		dataObject.put("ptbr", ptbr);
		
		
		if(ptTaxCalculatorTracker.getType().equals("ARREAR")) {
			return PDFRequest.builder().RequestInfo(requestInfoWrapper.getRequestInfo()).key("PropertyTaxArrearBillReceipt")
					.tenantId("hp").data(dataObject).build();
		}else {
			return PDFRequest.builder().RequestInfo(requestInfoWrapper.getRequestInfo()).key("PropertyTaxBillReceipt")
					.tenantId("hp").data(dataObject).build();
		}
	}
	
	public PDFRequest generateNoticePdfRequest(RequestInfoWrapper requestInfoWrapper, Property property,
			PtTaxCalculatorTracker ptTaxCalculatorTracker, Bill bill, Map<String, Integer> tenantIdDaysMap, String tenantId) {

		Map<String, Object> dataObject = new HashMap<>();
		Map<String, String> notice = new HashMap<>();
		
		String approverName = getApproverNameFromMdms(requestInfoWrapper.getRequestInfo(), property.getTenantId());		
		notice.put("approverName", approverName);

		JsonNode addressAdditionalDetails = objectMapper.valueToTree(property.getAddress().getAdditionalDetails());
		notice.put("ownerName", property.getOwners().stream().flatMap(owner -> {
			List<String> names = new ArrayList<>();

			if (owner.getPropertyOwnerName() != null) {
				names.add(owner.getPropertyOwnerName());
			}

			if (owner.getAdditionalDetails() != null && owner.getAdditionalDetails().has("coOwnerName")) {

				String anyOtherCoWorker = owner.getAdditionalDetails().has("anyOtherCoWorker")
						? owner.getAdditionalDetails().get("anyOtherCoWorker").asText()
						: null;

				if (!"No".equalsIgnoreCase(anyOtherCoWorker)) {
					String coOwner = owner.getAdditionalDetails().get("coOwnerName").asText();

					if (coOwner != null && !coOwner.isEmpty()) {
						names.add(coOwner);
					}
				}
			}

			return names.stream();
		}).distinct().collect(Collectors.joining(" & ")));
		notice.put("address",
				Stream.of(addressAdditionalDetails.get("propertyAddress"), addressAdditionalDetails.get("wardNumber"),
						addressAdditionalDetails.get("ulbType"), addressAdditionalDetails.get("ulbName"),
						property.getAddress().getDistrict(), property.getAddress().getPincode())
						.filter(Objects::nonNull).map(Object::toString).map(s -> s.replace("\"", ""))
						.filter(s -> !s.isEmpty()).collect(Collectors.joining(", ")));

		notice.put("mobileNumber", bill.getMobileNumber());
		notice.put("propertyId", property.getPropertyId());
		notice.put("noticeDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		notice.put("amountDue", bill.getTotalAmount() != null ? bill.getTotalAmount().toPlainString() : "0.00");
		dataObject.put("notice", notice);

		return PDFRequest.builder()
				.RequestInfo(requestInfoWrapper.getRequestInfo())
				.key("PropertyNotice")
				.tenantId(tenantId)
				.data(dataObject)
				.build();
	}
	
	private String escapeHtml(String input) {
	    if (input == null) return null;
	    return input.replace("&", "&amp;")
	                .replace("<", "&lt;")
	                .replace(">", "&gt;")
	                .replace("\"", "&quot;")
	                .replace("'", "&#39;");
	}
	
	private String getApproverNameFromMdms(RequestInfo requestInfo, String tenantId) {

		List<String> approverNames = commonUtils.getAttributeValueList(tenantId, "Signature",
				Collections.singletonList("Signature"), null, "$.MdmsRes.Signature.Signature[*].userName", requestInfo);

		if (CollectionUtils.isEmpty(approverNames)) {
			throw new CustomException("MDMS_APPROVER_NOT_FOUND", "No Signature found for tenant " + tenantId);
		}

		String approverNameHrms = repository.getPropertyApproverCode(tenantId);

		if (approverNames.contains(approverNameHrms)) {
			return approverNameHrms;
		}

		throw new CustomException("MDMS_APPROVER_NOT_FOUND",
				"No Signature found for PROPERTY_APPROVER " + approverNameHrms);
	}

}
