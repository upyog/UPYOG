package org.egov.garbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.annotations.CustomSafeHtml;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
/**
 * Applicant declaration/consent captured on garbage registration (terms accepted, date).
 * Stored as part of application compliance on the garbage account.
 */
@NoArgsConstructor
public class GrbgDeclaration {

    @CustomSafeHtml
    private String uuid;
    @CustomSafeHtml
    private String statement;
    private Boolean isActive;
}
