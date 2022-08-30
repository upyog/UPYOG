package org.egov.filemgmnt.common.mappers;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

// TODO: Change ReportingPolicy.WARN to ReportingPolicy.ERROR in development
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GlobalMapperConfig {

}
