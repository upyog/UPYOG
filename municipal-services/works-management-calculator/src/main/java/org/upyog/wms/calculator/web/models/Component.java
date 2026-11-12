package org.upyog.wms.calculator.web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Component {

    private String id;

    private String componentNumber;

    private String componentName;

    private String category;

    private String fundSource;

    private BigDecimal approvedAmount;

    private String status;

    private Boolean isActive;

    private List<FundDetail> fundDetails;
}
