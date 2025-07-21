package org.egov.common.entity.edcr;

import java.math.BigDecimal;
import java.util.Objects;

public final class FeatureRuleKey {
    private final String state;
    private final String city;
    private final String zone;
    private final String subZone;
    private final String occupancy;
    private final String riskType;
    private final String featureName;

    public FeatureRuleKey(String state, String city, String zone, String subZone,
                   String occupancy, String riskType, String featureName) {
        this.state = state;
        this.city = city;
        this.zone = zone;
        this.subZone = subZone;
        this.occupancy = occupancy;
        this.riskType = riskType;
        this.featureName = featureName;
    }
    
    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getZone() {
        return zone;
    }

    public String getSubZone() {
        return subZone;
    }

    public String getOccupancy() {
        return occupancy;
    }

    public String getRiskType() {
        return riskType;
    }

    public String getFeatureName() {
        return featureName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureRuleKey)) return false;
        FeatureRuleKey that = (FeatureRuleKey) o;
        return Objects.equals(state, that.state) &&
               Objects.equals(city, that.city) &&
               Objects.equals(zone, that.zone) &&
               Objects.equals(subZone, that.subZone) &&
               Objects.equals(occupancy, that.occupancy) &&
               Objects.equals(riskType, that.riskType) &&
               Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, city, zone, subZone, occupancy, riskType, featureName);
    }

    @Override
    public String toString() {
        return String.format("RuleKey[%s/%s/%s/%s/%s/%s/%s]",
            state, city, zone, subZone, occupancy, riskType, featureName);
    }

}
