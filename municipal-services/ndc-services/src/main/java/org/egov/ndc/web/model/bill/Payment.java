package org.egov.ndc.web.model.bill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import org.egov.ndc.web.model.AuditDetails;
import org.egov.ndc.validation.SanitizeHtml;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {

    @Size(max=64)
    @JsonProperty("id")
    private String id;

    @NotNull
    @Size(max=64)
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("totalDue")
    private BigDecimal totalDue;

    @NotNull
    @JsonProperty("totalAmountPaid")
    private BigDecimal totalAmountPaid;

    @SanitizeHtml
    @Size(max=128)
    @JsonProperty("transactionNumber")
    private String transactionNumber;

    @JsonProperty("transactionDate")
    private Long transactionDate;

    @JsonProperty("meterMake")
    private String meterMake;
    
    @JsonProperty("avarageMeterReading")
    private String avarageMeterReading;
    
    
    @JsonProperty("initialMeterReading")
    private String initialMeterReading;
    
    
    @JsonProperty("MeterId")
    private String meterId;
    
    @JsonProperty("MeterinstallationDate")
    private String meterInstallationDate;
    
    
    @JsonProperty("ledgerId")
    private String ledgerId;
    
    @JsonProperty("groupId")
    private String groupId;
    
    
    @JsonProperty("landarea")
    private String landarea;
    
    
    @JsonProperty("roadtype")
    private String roadtype;
    
    
    @JsonProperty("roadlength")
    private String roadlength;
    
    
    @JsonProperty("connectionCategory")
    private String connectionCategory;
    
    @NotNull
    @JsonProperty("paymentMode")
    private PaymentModeEnum paymentMode;

    
    @JsonProperty("instrumentDate")
    private Long instrumentDate;

    @SanitizeHtml
    @Size(max=128)
    @JsonProperty("instrumentNumber")
    private String instrumentNumber;

    @JsonProperty("instrumentStatus")
    private InstrumentStatusEnum instrumentStatus;

    @SanitizeHtml
    @Size(max=64)
    @JsonProperty("ifscCode")
    private String ifscCode;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("additionalDetails")
    private JsonNode additionalDetails;

    @JsonProperty("paymentDetails")
    @Valid
    private List<PaymentDetail> paymentDetails;

    @SanitizeHtml
    @Size(max=128)
    @NotNull
  //  @Pattern(regexp = "^[a-zA-Z]+(([_\\-'`\\. ][a-zA-Z ])?[a-zA-Z]*)*$", message = "Invalid name. Only alphabets and special characters -, ',`, ., _")
    @JsonProperty("paidBy")
    private String paidBy = null;

    @SanitizeHtml
    @Size(max=64)
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    @JsonProperty("mobileNumber")
    private String mobileNumber = null;
    
    @JsonProperty("ownerNumber")
    private List<String> ownerNumber = null;

    @SanitizeHtml
    @Size(max=128)
    //@Pattern(regexp = "^[a-zA-Z ]+(([_\\-'`\\. ][a-zA-Z ])?[a-zA-Z]*)*$", message = "Invalid name. Only alphabets and special characters -, ',`, ., _")
    @JsonProperty("payerName")
    private String payerName = null;

    @SanitizeHtml
    @Size(max=1024)
    @JsonProperty("payerAddress")
    private String payerAddress = null;

    @SanitizeHtml
    @Size(max=64)
    @Pattern(regexp = "^$|^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "Invalid emailId")
    @JsonProperty("payerEmail")
    private String payerEmail = null;

    @SanitizeHtml
    @Size(max=64)
    @JsonProperty("payerId")
    private String payerId = null;

    @JsonProperty("paymentStatus")
    private PaymentStatusEnum paymentStatus;

    @SanitizeHtml
    @JsonProperty("fileStoreId")
    private String fileStoreId;
    
    
    @JsonProperty("ownername")
    private List<String> ownername;

	@JsonProperty("usageCategory")
	private String usageCategory;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("propertyDetail")
	private HashMap<String, String> propertyDetail;

	@JsonProperty("propertyid")
        private String propertyId = null;

    public Payment addpaymentDetailsItem(PaymentDetail paymentDetail) {
        if (this.paymentDetails == null) {
            this.paymentDetails = new ArrayList<>();
        }
        this.paymentDetails.add(paymentDetail);
        return this;
    }




}
