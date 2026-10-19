package org.upyog.externalaudit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Bindable settings for {@code external.api.audit.*}.
 * <p>
 * Enable {@code reconciliation} and {@code cleanup} only on the service whose datasource hosts {@code ug_external_api_*}.
 * </p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "external.api.audit")
public class ExternalApiAuditProperties {

    /**
     * Kafka topic consumed by egov-persister ({@code external-api-audit-persister.yml}).
     */
    private String detailTopic = "external-api-audit-details";

    /**
     * Maximum serialized payload size in bytes. Larger bodies are replaced with a truncated marker.
     */
    private int maxPayloadBytes = 204800;

    /**
     * When false, request/response bodies are not persisted (metadata envelope only).
     */
    private boolean capturePayloadEnabled = true;

    /**
     * Age in milliseconds after which an {@code INITIATED} row is marked {@code TIMED_OUT}.
     */
    private long staleThresholdMs = 600000;

    /**
     * JSON field names whose values are replaced with {@code ********} before persist.
     * Matching is case-insensitive after stripping non-alphanumeric characters.
     */
    private List<String> sensitiveFields = new ArrayList<>(List.of(
            "password",
            "accessToken",
            "access_token",
            "refreshToken",
            "refresh_token",
            "authorization",
            "authToken",
            "token",
            "ulbBankAccountNumber",
            "beneficiaryAccountNumber",
            "fromAccount",
            "toAccount",
            "accountNumber",
            "bankAccount",
            "ULBBankAccountNumber",
            "BeneficiaryAccountNumber",
            "FromAccount",
            "ToAccount",
            "Password",
            "AccessToken",
            "RefreshToken"
    ));

    private Reconciliation reconciliation = new Reconciliation();

    private Cleanup cleanup = new Cleanup();

    /**
     * Marks leftover INITIATED rows as TIMED_OUT.
     */
    @Getter
    @Setter
    public static class Reconciliation {
        /**
         * Enable only in the service whose datasource hosts {@code ug_external_api_*} tables.
         */
        private boolean enabled = false;
        private String cron = "0 0 6 * * *";
        private String zone = "Asia/Kolkata";
    }

    /**
     * Deletes audit rows older than {@link #retentionMs}.
     */
    @Getter
    @Setter
    public static class Cleanup {
        /**
         * Enable only in the service whose datasource hosts {@code ug_external_api_*} tables.
         */
        private boolean enabled = false;
        private String cron = "0 30 3 * * *";
        private String zone = "Asia/Kolkata";
        /**
         * Retention window in milliseconds. Default 30 days.
         */
        private long retentionMs = 2592000000L;
    }
}
