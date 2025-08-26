package org.egov.common.entity.edcr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

// Used for JSON serialization/deserialization with Jackson library
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// Maps Java field names to JSON property names during serialization/deserialization
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MdmsFeatureRule {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("fromPlotArea")
    private BigDecimal fromPlotArea;

    @JsonProperty("toPlotArea")
    private BigDecimal toPlotArea;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("zone")
    private String zone;

    @JsonProperty("subZone")
    private String subZone;

    @JsonProperty("occupancy")
    private String occupancy;

    @JsonProperty("fromPlotDepth")
    private BigDecimal fromPlotDepth;

    @JsonProperty("toPlotDepth")
    private BigDecimal toPlotDepth;

    @JsonProperty("fromBuildingHeight")
    private BigDecimal fromBuildingHeight;

    @JsonProperty("toBuildingHeigh")
    private BigDecimal toBuildingHeight;

    @JsonProperty("subOccupancy")
    private String subOccupancy;

    @JsonProperty("riskType")
    private String riskType;

    @JsonProperty("permissible")
    private BigDecimal permissible;

    @JsonProperty("fromRoadWidth")
    private BigDecimal fromRoadWidth;

    @JsonProperty("toRoadWidth")
    private BigDecimal toRoadWidth;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("valuePermissible")
    private List<String> valuePermissible;

    @JsonProperty("featureName")
    private String featureName;

    public BigDecimal getFromBuildingHeight() {
        return fromBuildingHeight;
    }

    public void setFromBuildingHeight(BigDecimal fromBuildingHeight) {
        this.fromBuildingHeight = fromBuildingHeight;
    }

    public BigDecimal getToBuildingHeight() {
        return toBuildingHeight;
    }

    public void setToBuildingHeight(BigDecimal toBuildingHeight) {
        this.toBuildingHeight = toBuildingHeight;
    }

    public BigDecimal getFromPlotDepth() {
        return fromPlotDepth;
    }

    public void setFromPlotDepth(BigDecimal fromPlotDepth) {
        this.fromPlotDepth = fromPlotDepth;
    }

    public BigDecimal getToPlotDepth() {
        return toPlotDepth;
    }

    public void setToPlotDepth(BigDecimal toPlotDepth) {
        this.toPlotDepth = toPlotDepth;
    }

    public String getSubOccupancy() {
        return subOccupancy;
    }

    public void setSubOccupancy(String subOccupancy) {
        this.subOccupancy = subOccupancy;
    }

    // Standard Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getFromPlotArea() {
        return fromPlotArea;
    }

    public void setFromPlotArea(BigDecimal fromPlotArea) {
        this.fromPlotArea = fromPlotArea;
    }

    public BigDecimal getFromRoadWidth() {
        return fromRoadWidth;
    }

    public void setFromRoadWidth(BigDecimal fromRoadWidth) {
        this.fromRoadWidth = fromRoadWidth;
    }

    public BigDecimal getToRoadWidth() {
        return toRoadWidth;
    }

    public void setToRoadWidth(BigDecimal toRoadWidth) {
        this.toRoadWidth = toRoadWidth;
    }

    public BigDecimal getToPlotArea() {
        return toPlotArea;
    }

    public void setToPlotArea(BigDecimal toPlotArea) {
        this.toPlotArea = toPlotArea;
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

    public BigDecimal getPermissible() {
        return permissible;
    }

    public void setPermissible(BigDecimal permissible) {
        this.permissible = permissible;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public List<String> getValuePermissible() {
        return valuePermissible;
    }

    public void setValuePermissible(List<String> valuePermissible) {
        this.valuePermissible = valuePermissible;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Returns a string representation of the MdmsFeatureRule object
     *
     * @return formatted string containing all non-null field values
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        if (id != null) sb.append("id=").append(id).append(", ");
        if (fromPlotArea != null) sb.append("fromPlotArea=").append(fromPlotArea).append(", ");
        if (toPlotArea != null) sb.append("toPlotArea=").append(toPlotArea).append(", ");
        if (fromRoadWidth != null) sb.append("fromRoadWidth=").append(fromRoadWidth).append(", ");
        if (toRoadWidth != null) sb.append("toRoadWidth=").append(toRoadWidth).append(", ");
        if (state != null) sb.append("state=").append(state).append(", ");
        if (city != null) sb.append("city=").append(city).append(", ");
        if (zone != null) sb.append("zone=").append(zone).append(", ");
        if (subZone != null) sb.append("subZone=").append(subZone).append(", ");
        if (occupancy != null) sb.append("occupancy=").append(occupancy).append(", ");
        if (riskType != null) sb.append("riskType=").append(riskType).append(", ");
        if (permissible != null) sb.append("permissible=").append(permissible).append(", ");

        if (active != null) sb.append("active=").append(active).append(", ");
        if (featureName != null) sb.append("featureName=").append(featureName).append(", ");
        if (valuePermissible != null) sb.append("valuePermissible=").append(valuePermissible).append(", ");

        // Remove last comma and space if present
        if (sb.lastIndexOf(", ") == sb.length() - 2) {
            sb.delete(sb.length() - 2, sb.length());
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Compares this MdmsFeatureRule with another object for equality
     *
     * @param o the object to compare with
     * @return true if objects are equal based on id field
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MdmsFeatureRule that = (MdmsFeatureRule) o;
        return Objects.equals(id, that.id); // or use a combination of fields
    }

    /**
     * Generates hash code for this MdmsFeatureRule object
     *
     * @return hash code based on the id field
     */
    @Override
    public int hashCode() {
        return Objects.hash(id); // or relevant fields
    }


}
