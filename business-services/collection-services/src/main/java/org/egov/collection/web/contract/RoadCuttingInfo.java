package org.egov.collection.web.contract;

import java.time.LocalDateTime;

public class RoadCuttingInfo {
    private String id; // Unique identifier for the road cutting info
    private String tenantId; // ID of the tenant
    private String wsId; // ID for the workstream
    private String active; // Status of the road cutting info
    private String roadType; // Type of the road
    private String roadCuttingArea; // Area where the road cutting is done
    private String createdBy; // User who created the entry
    private String lastModifiedBy; // User who last modified the entry
    private Long createdTime; // Timestamp when the entry was created
    private Long lastModifiedTime; // Timestamp when the entry was last modified

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getWsId() {
        return wsId;
    }

    public void setWsId(String wsId) {
        this.wsId = wsId;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public String getRoadCuttingArea() {
        return roadCuttingArea;
    }

    public void setRoadCuttingArea(String roadCuttingArea) {
        this.roadCuttingArea = roadCuttingArea;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
