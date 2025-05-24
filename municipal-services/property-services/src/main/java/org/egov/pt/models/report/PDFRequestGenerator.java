package org.egov.pt.models.report;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.egov.pt.models.collection.Bill;
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
			PtTaxCalculatorTracker ptTaxCalculatorTracker, Bill bill) {

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
		ptbr.put("buildingNo", ""); // blank
		// TODO

		dataObject.put("ptbr", ptbr);

		return PDFRequest.builder().RequestInfo(requestInfoWrapper.getRequestInfo()).key("PropertyTaxBillReceipt")
				.tenantId("hp").data(dataObject).build();
	}

}
