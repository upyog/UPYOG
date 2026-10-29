package org.upyog.dashboard.mdms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Top-level response envelope for MDMS tenant search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MdmsTenantResponse {

    @JsonProperty("MdmsRes")
    private MdmsRes mdmsRes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MdmsRes {
        @JsonProperty("tenant")
        private TenantModule tenant;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TenantModule {
        @JsonProperty("nationalInfo")
        private List<String> nationalInfo;

        public List<String> getEffectiveTenants() {
            return nationalInfo != null ? nationalInfo : Collections.emptyList();
        }
    }
}
