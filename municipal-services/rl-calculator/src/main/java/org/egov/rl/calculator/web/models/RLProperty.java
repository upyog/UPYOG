package org.egov.rl.calculator.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Property
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RLProperty {// extends PropertyInfo {

	@JsonProperty("propertyId")
	@SafeHtml
	private String propertyId;

	@JsonProperty("propertyName")
	private String propertyName;
	
	@JsonProperty("allotmentType")
	private String allotmentType;  /// Rent or Rease


	@Digits(integer = 8, fraction = 2)
	@JsonProperty("propertySizeOrArea")
	private Double propertySizeOrArea;
	
	@JsonProperty("address")
	private String address;

	@JsonProperty("geoLocation")
	private GeoLocation geoLocation;
	
	@JsonProperty("propertyImage")
	private String propertyImage;
	
	@JsonProperty("baseRent")
	private String baseRent;
	
	@JsonProperty("securityDeposit")
	private String securityDeposit;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("penaltyType")
	private String penaltyType;
	
//	@JsonProperty("latePayment")
//	private String latePayment;
//	
//	
//	
//	@JsonProperty("tradeLicenseNumber")
//	private String tradeLicenseNumber; // mandotroy for renewal 
//	
//	@JsonProperty("ownershipCategory")
//	@SafeHtml
//	private String ownershipCategory;
//
//	@JsonProperty("owners")
//	@Valid
//	private List<OwnerInfo> owners;
//	
//	@JsonProperty("rent_amount")
//	private String rentAmount;
//
//	@JsonProperty("gst_applicable")
//	private boolean gstApplicable;
//

//	
//	@JsonProperty("security_amount")
//	private String securityAmount;;
//	
//	@JsonProperty("refund_applicable_on_discontinuation")
//	private boolean refundApplicableOnDiscontinuation;;
//	
//	@JsonProperty("amount_to_be_refund")
//	private String amountToBeRefund;;
//	
//	@JsonProperty("increment_cycle")
//	private String incrementCycle;;
//	
//	@JsonProperty("increment_applicable")
//	private boolean incrementApplicable;;
//	
//	@JsonProperty("increment_percentage")
//	private String incrementPercentage;;
//	
//	@JsonProperty("last_payment_percantage")
//	private String lastPaymentPercantage;
//
//
////	@JsonProperty("institution")
////	private Institution institution;
//
////	@JsonProperty("creationReason")
////	@NotNull(message="The value provided is either Invald or null")
////	private CreationReason creationReason;
//	
//
////	@Max(value = 500)
////	@JsonProperty("noOfFloors")
////	private Long noOfFloors;
//
////	@Digits(integer = 8, fraction = 2)
////	@JsonProperty("landArea")
////	private Double landArea;
//
////	@Digits(integer = 8, fraction = 2)
////	@JsonProperty("superBuiltUpArea")
////	private BigDecimal superBuiltUpArea;
////
////	@JsonProperty("source")
////	private Source source;
////
////	@JsonProperty("channel")
////	private Channel channel;
////
////	@JsonProperty("documents")
////	@Valid
////	@DiffIgnore
////	private List<Document> documents;
////
////	@JsonProperty("units")
////	@Valid
////	private List<Unit> units;
////
////	@JsonProperty("dueAmount")
////	private String dueAmount;
////	
////	@JsonProperty("dueAmountYear")
////	private String dueAmountYear;
//	
//	@DiffIgnore
//	@JsonProperty("additionalDetails")
//	private JsonNode additionalDetails;
//	
//	@JsonProperty("auditDetails")
//	private AuditDetails auditDetails;
//
////	@JsonProperty("workflow")
////	@DiffIgnore
////	private ProcessInstance workflow;
//	
////	@JsonProperty("AlternateUpdated")
////	private boolean AlternateUpdated;
////
////	@Builder.Default
////	@JsonProperty("isOldDataEncryptionRequest")
////	private boolean isOldDataEncryptionRequest = false;

		
}

