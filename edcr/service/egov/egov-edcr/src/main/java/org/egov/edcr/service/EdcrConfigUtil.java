package org.egov.edcr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.edcr.EdcrMasterConfig;
import org.egov.common.entity.edcr.Plan;

public class EdcrConfigUtil {

    public static class ParamsAndColumns {
        private Map<String, Object> params;
        private List<String> valueFromColumn;

        public ParamsAndColumns(Map<String, Object> params, List<String> valueFromColumn) {
            this.params = params;
            this.valueFromColumn = valueFromColumn;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public List<String> getValueFromColumn() {
            return valueFromColumn;
        }
    }

    public static ParamsAndColumns buildParamsAndValues(Plan pl,
            String feature,
            String occupancyName,
            List<EdcrMasterConfig> masterConfigList
    ) {
        Map<String, Object> params = new HashMap<>();
        List<String> valueFromColumn = new ArrayList<>();

        for (EdcrMasterConfig config : masterConfigList) {
            if (config.getFeatureName().equalsIgnoreCase(feature)) {
            	if ("yes".equalsIgnoreCase(config.getPlotArea())) params.put("plotArea", pl.getPlot().getArea());
            	if ("yes".equalsIgnoreCase(config.getOccupancy())) params.put("occupancy", occupancyName);
                params.put("feature", feature);
                if ("yes".equalsIgnoreCase(config.getZone())) params.put("zone", "x");
                if ("yes".equalsIgnoreCase(config.getSubZone())) params.put("subZone", "y");
                if ("yes".equalsIgnoreCase(config.getRoadWidth())) params.put("roadWidth", 150);
                if ("yes".equalsIgnoreCase(config.getRiskType())) params.put("riskType", "low");
                if (config.getValuePermissible() != null) {
                    valueFromColumn.addAll(config.getValuePermissible());
                }

                break;
            }
        }

        return new ParamsAndColumns(params, valueFromColumn);
    }
}
