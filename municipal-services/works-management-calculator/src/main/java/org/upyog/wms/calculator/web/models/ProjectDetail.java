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
public class ProjectDetail {

    private String id;

    private String projectId;

    private String projectName;

    private BigDecimal approvedAmount;

    private Boolean isActive;

    private List<Component> components;
}
