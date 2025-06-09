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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.egov.pt.models.Unit;
import org.egov.pt.models.collection.Bill;
import org.egov.pt.models.collection.Bill.StatusEnum;
import org.egov.pt.web.contracts.RequestInfoWrapper;
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

	public PDFRequest generatePdfRequest(RequestInfoWrapper requestInfoWrapper, Property property,
			PtTaxCalculatorTracker ptTaxCalculatorTracker, Bill bill, Map<String, Integer> tenantIdDaysMap) {

		Map<String, Object> dataObject = new HashMap<>();
		Map<String, String> ptbr = new HashMap<>();

		JsonNode addressAdditionalDetails = objectMapper.valueToTree(property.getAddress().getAdditionalDetails());

		ptbr.put("ulbType", addressAdditionalDetails.get("ulbType").asText());
		ptbr.put("ulbName", addressAdditionalDetails.get("ulbName").asText());
		ptbr.put("billNo", bill.getBillNumber());

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
				property.getOwners().stream().map(OwnerInfo::getName).collect(Collectors.joining(", ")));

		ptbr.put("fatherOrHusbandName", property.getOwners().stream().map(owner -> {
			if (owner.getAdditionalDetails() == null)
				return null;
			Object nameObj = owner.getAdditionalDetails().get("fatherOrHusbandName");
			if (nameObj == null)
				return null;
			return nameObj.toString().replaceAll("\"", "");
		}).filter(name -> name != null && !name.isEmpty()).collect(Collectors.joining(", ")));

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
		BigDecimal plinthAreaTotal = BigDecimal.ZERO;

		for (Unit unit : property.getUnits()) {
			JsonNode unitAdditionalDetails = objectMapper.valueToTree(unit.getAdditionalDetails());
			slNos.add(String.valueOf(conut++));
			f1Values.add(addressAdditionalDetails.get("zone").asText());
			f2Values.add(unitAdditionalDetails.get("propBuildingType").asText());
			f3Values.add(unitAdditionalDetails.get("propYearOfCons").asText());
			f4Values.add(unitAdditionalDetails.get("propType").asText());
			f5Values.add(unitAdditionalDetails.get("useOfBuilding").asText());
			plinthAreas.add(unitAdditionalDetails.get("propArea").asText());
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
		plinthAreaTotal = plinthAreas.stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, Object> tableRow = new HashMap<>();
		tableRow.put("tag", "PROPERTY_TAX_BILL_TABLE_ROW");
		tableRow.put("values", ptDetailsTableRow);

		List<Map<String, Object>> tableRows = new ArrayList<>();
		tableRows.add(tableRow);

		Map<String, Object> tableRowMap = new HashMap<>();
		tableRowMap.put("TABLE_ROW", tableRows);

		ptbr.put("plinthAreaTotal", String.valueOf(plinthAreaTotal));

		BigDecimal propertyTax = ptTaxCalculatorTracker.getPropertyTaxWithoutRebate();
		ptbr.put("propertyTax", String.valueOf(propertyTax));

		BigDecimal arrear = bill.getTotalAmount().subtract(ptTaxCalculatorTracker.getPropertyTax());
		ptbr.put("arrear", String.valueOf(arrear));

		ptbr.put("propertyTaxPlusArrear", String.valueOf(propertyTax.add(arrear)));

		ptbr.put("rebateDays", String.valueOf(tenantIdDaysMap.getOrDefault(property.getTenantId(), 15)));

		BigDecimal rebate = ptTaxCalculatorTracker.getRebateAmount();
		ptbr.put("rebate", String.valueOf(rebate));

		// TODO START
		BigDecimal interest = new BigDecimal("0.00");
		ptbr.put("interest", String.valueOf(interest));

		BigDecimal penalty = new BigDecimal("0.00");
		ptbr.put("penalty", String.valueOf(penalty));

		ptbr.put("totalTax", String.valueOf(propertyTax.add(arrear).subtract(rebate).add(interest).add(penalty)));
		// TODO END

		BigDecimal amountPaid = BigDecimal.ZERO;
		String paymentStatus = "";
		String paymentDate = "";
		if (bill.getStatus().equals(StatusEnum.PAID)) {
			amountPaid = bill.getTotalAmount();
			paymentStatus = "Success";
			paymentDate = ""; // TODO blank
		}
		ptbr.put("amountPaid", String.valueOf(amountPaid));
		ptbr.put("paymentStatus", paymentStatus);
		ptbr.put("billGeneratedDate", Instant.ofEpochMilli(bill.getBillDate()).atZone(ZoneId.systemDefault())
				.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		ptbr.put("paymentDate", paymentDate);

		dataObject.putAll(tableRowMap);
		dataObject.put("ptbr", ptbr);

		return PDFRequest.builder().RequestInfo(requestInfoWrapper.getRequestInfo()).key("PropertyTaxBillReceipt")
				.tenantId("hp").data(dataObject).build();
	}

}
