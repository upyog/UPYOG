package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Commercial establishment details for non-residential garbage accounts.
 * Captures trade name, units, and charge-related attributes linked to the parent account.
 */
@EqualsAndHashCode(exclude = {"uuid"})
public class GrbgCommercialDetails {

    @CustomSafeHtml
    private String uuid;
    private Long garbageId;
    @CustomSafeHtml
    private String businessName;
    @CustomSafeHtml
    private String businessType;
    @CustomSafeHtml
    private String ownerUserUuid;
}
