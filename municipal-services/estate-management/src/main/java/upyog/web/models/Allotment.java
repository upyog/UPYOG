package upyog.web.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.egov.common.contract.models.AuditDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Allotment {
    private String allotmentId;
    @NotBlank
    private String assetNo;
    @NotBlank
    @Size(max = 100, message = "Tenant ID must be less than or equal to 100 characters")
    private String tenantId;
    private String userUuid;
    @NotBlank
    @Size(max = 100, message = "Allottee name must be less than or equal to 100 characters")
    private String alloteeName;
    @NotBlank
    @Size(min = 10, max = 10, message = "Mobile no must be exactly 10 digits")
    private String mobileNo;
    @NotBlank
    @Size(min = 10, max = 10, message = "Alternate Mobile no must be exactly 10 digits")
    private String alternateMobileNo;
    @NotBlank
    @Size(max = 100, message = "Email ID must be less than or equal to 100 characters")
    private String emailId;
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate agreementStartDate;
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "dd-MM-yyyy")
    private LocalDate agreementEndDate;
    @NotBlank
    private Integer duration;
    @NotBlank
    private BigDecimal rentRate;
    @NotBlank
    private BigDecimal monthlyRent;
    @NotBlank
    private BigDecimal advancePayment;
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "dd-MM-yyyy")
    private LocalDate advancePaymentDate;
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "dd-MM-yyyy")
    private LocalDate allotmentDate;
    @NotBlank
    private String eofficeFileNo;
    @NotBlank
    private String assetReferenceNo;
    @NotBlank
    private String propertyType;
    @NotBlank
    private String citizenRequestLetter;
    @NotBlank
    private String allotmentLetter;
    @NotBlank
    private String signedDeed;

    private Object additionalDetails;

    private AuditDetails auditDetails;

    // MONTHLY , QUARTERLY, YEARLY
    @NotBlank
    private String billingCycle;
}