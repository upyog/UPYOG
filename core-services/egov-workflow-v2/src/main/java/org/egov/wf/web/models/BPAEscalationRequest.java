package org.egov.wf.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Contract class to receive request. Array of TradeLicense items are used in case of create, whereas single TradeLicense item is used for update
 */
@ApiModel(description = "Contract class to receive request. Array of TradeLicense items are used in case of create, whereas single TradeLicense item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BPAEscalationRequest {
   
        @JsonProperty("BPAEscalationInstances")
        @Valid
        @NotNull
        private List<BPAEscalationInstance> bPAEscalationInstance;


        public BPAEscalationRequest addProcessInstanceItem(BPAEscalationInstance bPAEscalationInstance) {
            if (this.bPAEscalationInstance == null) {
            this.bPAEscalationInstance = new ArrayList<>();
            }
        this.bPAEscalationInstance.add(bPAEscalationInstance);
        return this;
        }

}

