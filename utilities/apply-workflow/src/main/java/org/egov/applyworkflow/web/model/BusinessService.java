package org.egov.applyworkflow.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessService {

    @NotNull
    private String tenantId;

    @NotNull
    private String businessService;

    @NotNull
    private String business;

    @NotNull
    private Long businessServiceSla;

    @NotNull
    private List<State> states;

}
