package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
/**
 * Legacy garbage connection details when migrating or linking prior garbage ids.
 * Preserves historical identifiers and amounts for arrear and continuity checks.
 */
@EqualsAndHashCode(exclude = {"uuid"})
public class GrbgOldDetails {

    @CustomSafeHtml
    private String uuid;
    private Long garbageId;
    @CustomSafeHtml
    private String oldGarbageId;
}
