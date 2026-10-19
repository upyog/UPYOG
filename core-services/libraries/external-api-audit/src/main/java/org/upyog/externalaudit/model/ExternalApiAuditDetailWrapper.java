package org.upyog.externalaudit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Root JSON object expected by {@code external-api-audit-persister.yml}
 * ({@code basePath: $.apiAuditDetail}).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ExternalApiAuditDetailWrapper {

    @JsonProperty("apiAuditDetail")
    private ExternalApiAuditDetail apiAuditDetail;
}
