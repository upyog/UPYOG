package org.egov.common.entity.edcr;

import java.util.List;

public class EdcrMasterConfig {
    private String featureName;
    private String state; 
    private String city;
    private String zone;
    private String occupancy;
    private String riskType;
    private String subZone;
    private String plotArea;
    private String roadWidth;
    private List<String> valuePermissible;

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getSubZone() {
        return subZone;
    }

    public void setSubZone(String subZone) {
        this.subZone = subZone;
    }

    public String getPlotArea() {
        return plotArea;
    }

    public void setPlotArea(String plotArea) {
        this.plotArea = plotArea;
    }

    public String getRoadWidth() {
        return roadWidth;
    }

    public void setRoadWidth(String roadWidth) {
        this.roadWidth = roadWidth;
    }
    
    public String getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }
    
    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }



    public List<String> getValuePermissible() {
        return valuePermissible;
    }

    public void setValuePermissible(List<String> valuePermissible) {
        this.valuePermissible = valuePermissible;
    }
}
