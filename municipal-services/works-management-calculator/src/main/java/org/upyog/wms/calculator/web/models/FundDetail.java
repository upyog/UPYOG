package org.upyog.wms.calculator.web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Funding row as returned by works-management-service (only the fields the calculator needs). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundDetail {

    private String id;

    private String fundType;

    private String sourceCode;

    private String fundName;

    private BigDecimal amount;

    private Boolean isActive;
}
