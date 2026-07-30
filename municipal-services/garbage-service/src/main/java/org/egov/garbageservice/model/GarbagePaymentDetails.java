package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Encapsulates payment transaction details for a garbage service bill, including transaction ID, payment mode, and amount paid.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarbagePaymentDetails {
    private String id;
    private Long applicationId;
    private String applicationNo;
    private BigDecimal penaltyAmount;
    private BigDecimal rent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate previousMonth;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate paymentDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate lastDateOfPayment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate duePaymentDate;
    private String paymentStatus;
    private BigDecimal duePayment;
    private Integer validityDays;
    private String createdBy;
    private String lastModifiedBy;
    private Long createdTime;
    private Long lastModifiedTime;
}
