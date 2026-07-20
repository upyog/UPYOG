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
