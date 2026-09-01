package org.upyog.dashboard.util;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Spring component that parses a dot-notation tenant ID string into a map of
 * hierarchy levels (state, ulb, region, ward).
 *
 * <p>
 * Expected tenant ID format: {@code state.ulb[.region[.ward]]}. Missing levels
 * are substituted with configurable defaults read from application properties.
 */
@Component
public class HierarchyParser {

    private final String defaultWard;
    private final String defaultRegion;
    private final String statePrefix;

    /**
     * Constructs a {@code HierarchyParser} with configurable default fallback
     * values.
     *
     * @param defaultWard default ward value used when the tenant ID contains no
     * ward segment (configured via {@code dashboard-data.metric.ward}); may be
     * empty
     * @param defaultRegion default region value used when the tenant ID
     * contains no region segment (configured via
     * {@code dashboard-data.metric.region}); may be empty
     * @param statePrefix tenant prefix string (configured via
     * {@code dashboard-data.system.user.tenantId})
     */
    @Autowired
    public HierarchyParser(
            @Value("${dashboard-data.metric.ward:}") String defaultWard,
            @Value("${dashboard-data.metric.region:}") String defaultRegion,
            @Value("${dashboard-data.system.user.tenantId:pg}") String statePrefix) {
        this.defaultWard = defaultWard;
        this.defaultRegion = defaultRegion;
        this.statePrefix = StringUtils.isNotBlank(statePrefix) ? statePrefix : "pg";
    }

    public HierarchyParser(String defaultWard, String defaultRegion) {
        this(defaultWard, defaultRegion, "pg");
    }

    /**
     * Parses the tenantId dot-notation string and returns a map of hierarchy
     * levels. Expected format: state.ulb.region.ward
     */
    public Map<String, String> parseTenantId(String tenantId) {
        Map<String, String> hierarchy = new HashMap<>();

        if (StringUtils.isBlank(tenantId)) {
            hierarchy.put("state", statePrefix);
            hierarchy.put("ulb", statePrefix);
            hierarchy.put("region", defaultRegion != null ? defaultRegion : "");
            hierarchy.put("ward", defaultWard != null ? defaultWard : "");
            return hierarchy;
        }

        String[] parts = tenantId.split("\\.");

        if (parts.length == 1) {
            // Only ULB name segment provided (e.g. "citya")
            hierarchy.put("state", statePrefix);
            hierarchy.put("ulb", statePrefix.equals(parts[0]) ? parts[0] : statePrefix + "." + parts[0]);
            hierarchy.put("region", defaultRegion != null ? defaultRegion : "");
            hierarchy.put("ward", defaultWard != null ? defaultWard : "");
        } else {
            // Full format provided (e.g. "pg.citya" or "pg.citya.region.ward")
            hierarchy.put("state", parts[0]);
            hierarchy.put("ulb", parts[0] + "." + parts[1]);

            String region = parts.length > 2 && StringUtils.isNotBlank(parts[2]) ? parts[2] : defaultRegion;
            hierarchy.put("region", region != null ? region : "");

            String ward = parts.length > 3 && StringUtils.isNotBlank(parts[3]) ? parts[3] : defaultWard;
            hierarchy.put("ward", ward != null ? ward : "");
        }

        return hierarchy;
    }
}
