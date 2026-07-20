package upyog.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerLog {
    private String id;
    private String allotmentId;
    private String tenantId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate billingDate;
    private Long billingPeriodFrom;
    private Long billingPeriodTo;
    private BigDecimal amount;
    private BigDecimal penaltyAmount;
    private String paymentType;
    private String status;
    private String createdBy;
    private Long createdTime;
    private String lastModifiedBy;
    private Long lastModifiedTime;
}
