package org.upyog.wms.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {

    private String uuid;

    private String tenantId;

    private String state;

    private String applicationStatus;

    private Boolean isStartState;

    private Boolean isTerminateState;

    private Boolean isStateUpdatable;
}
