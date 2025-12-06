package org.egov.applyworkflow.util;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface WorkflowMapper {

    // Mapping BusinessService to workflow.BusinessService
    @Mapping(target = "uuid", ignore = true) // Preserve the target's existing uuid
    @Mapping(target = "businessServiceId", ignore = true)
    @Mapping(target = "auditDetails", ignore = true)
    void updateBusinessService(
            org.egov.applyworkflow.web.model.BusinessService source,
            @MappingTarget org.egov.applyworkflow.web.model.workflow.BusinessService target
    );

    // Mapping State to workflow.State
    @Mapping(target = "uuid", ignore = true) // Preserve the target's existing uuid
    @Mapping(target = "businessServiceId", ignore = true)
    @Mapping(target = "auditDetails", ignore = true)
    void updateState(
            org.egov.applyworkflow.web.model.State source,
            @MappingTarget org.egov.applyworkflow.web.model.workflow.State target
    );

    // Mapping Action to workflow.Action
    @Mapping(target = "uuid", ignore = true) // Preserve the target's existing uuid
    @Mapping(target = "auditDetails", ignore = true)
    void updateAction(
            org.egov.applyworkflow.web.model.Action source,
            @MappingTarget org.egov.applyworkflow.web.model.workflow.Action target
    );
}
