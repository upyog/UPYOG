package org.upyog.wms.calculator.web.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Query parameters of /_updatedemand. consumerCodes = application numbers. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBillCriteria {

    @NotBlank
    private String tenantId;

    @NotEmpty
    @Size(max = 50)
    private List<String> consumerCodes;
}
