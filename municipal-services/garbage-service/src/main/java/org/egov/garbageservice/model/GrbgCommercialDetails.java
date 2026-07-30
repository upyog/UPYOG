package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.egov.tracer.annotations.CustomSafeHtml;

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
