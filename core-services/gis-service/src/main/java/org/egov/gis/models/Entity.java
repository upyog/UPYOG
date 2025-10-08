package org.egov.gis.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Entity model that can represent any municipal service entity
 * (Property, Trade License, Building Plan, etc.)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entity {

    private String id;
    private String applicationNumber;
    private String tenantId;
    private String status;
    private String businessService;
    private Map<String, Object> address;
    private Map<String, Object> additionalDetails;
    private Map<String, Object> attributes;
    private Long createdTime;
    private Long lastModifiedTime;
    private String createdBy;
    private String lastModifiedBy;
}
