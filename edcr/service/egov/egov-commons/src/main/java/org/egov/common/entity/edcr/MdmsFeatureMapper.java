package org.egov.common.entity.edcr;


import java.util.HashMap;
import java.util.Map;

public class MdmsFeatureMapper {

    private static final Map<String, Class<? extends MdmsFeatureRule>> FEATURE_RULE_MAP = new HashMap<>();

    static {
        FEATURE_RULE_MAP.put("Balcony", BalconyRequirement.class);
        FEATURE_RULE_MAP.put("BathroomWaterClosets", BathroomWCRequirement.class);
       
        // Add more as you define them
    }

    public static Class<? extends MdmsFeatureRule> getRuleClass(String featureName) {
        return FEATURE_RULE_MAP.get(featureName);
    }
}
