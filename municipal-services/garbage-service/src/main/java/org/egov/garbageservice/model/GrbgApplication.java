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
 * Application form data nested under GarbageAccount (type, category, dates, status).
 * Holds garbage service registration details submitted by citizens or ULB staff.
 */
@EqualsAndHashCode(exclude = {"uuid"})
public class GrbgApplication {

    @CustomSafeHtml
    private String uuid;
    @CustomSafeHtml
    private String applicationNo;
    @CustomSafeHtml
    private String status;
    private Long garbageId;
}
