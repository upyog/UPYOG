package org.egov.common.entity.edcr;

import org.springframework.stereotype.Component;

@Component
public class ReportScrutinyDetail {

    @ReportScrutinyField("Section")
    private String ruleNo;

    @ReportScrutinyField("Floor")
    private String floorNo;

    @ReportScrutinyField("Description")
    private String description;

    @ReportScrutinyField("Required")
    private String required;

    @ReportScrutinyField("Min Required")
    private String minRequired;

    @ReportScrutinyField("Provided")
    private String provided;

    @ReportScrutinyField("Status")
    private String status;

    @ReportScrutinyField("Permissible")
    private String permissible;

    @ReportScrutinyField("Area Type")
    private String areaType;

    @ReportScrutinyField("Road Width")
    private String roadWidth;

    @ReportScrutinyField("Declared")
    private String declared;

    @ReportScrutinyField("Up to")
    private String upto;

    @ReportScrutinyField("Verified")
    private String verified;

    @ReportScrutinyField("Action")
    private String action;

    @ReportScrutinyField("Remarks")
    private String remarks;

    @ReportScrutinyField("Occupancy")
    private String occupancy;

    @ReportScrutinyField("Level")
    private String level;

    @ReportScrutinyField("Field Verified")
    private String fieldVerified;

    @ReportScrutinyField("Distance")
    private String distance;

    @ReportScrutinyField("Permitted")
    private String permitted;

    @ReportScrutinyField("Room")
    private String room;

    @ReportScrutinyField("Occupancy/Condition")
    private String occupancyCondition;

    @ReportScrutinyField("Voltage")
    private String voltage;

    @ReportScrutinyField("Side Number")
    private String sideNumber;

    public String getRuleNo(){
        return ruleNo;
    }
    public void setRuleNo(String ruleNo) {
        this.ruleNo = ruleNo;
    }

    public String getFloorNo() {
        return floorNo;
    }
    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
    }

    public String getDescription(){
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinRequired() {
        return minRequired;
    }
    public void setMinRequired(String minRequired) {
        this.minRequired = minRequired;
    }

    public void setRequired(String required) {
        this.required = required;
    }
    public String getRequired(){
        return required;
    }

    public String getProvided(String s) {
        return provided;
    }
    public void setProvided(String provided) {
        this.provided = provided;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getPermissible() {
        return permissible;
    }
    public void setPermissible(String permissible) {
        this.permissible = permissible;
    }

    public String getAreaType() {
        return areaType;
    }
    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getRoadWidth() {
        return roadWidth;
    }
    public void setRoadWidth(String roadWidth) {
        this.roadWidth = roadWidth;
    }

    public String getDeclared() {
        return declared;
    }
    public void setDeclared(String declared) {
        this.declared = declared;
    }

    public String getUpto() {
        return upto;
    }
    public void setUpto(String upto) {
        this.upto = upto;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getOccupancy() {
        return occupancy;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFieldVerified() {
        return fieldVerified;
    }

    public void setFieldVerified(String fieldVerified) {
        this.fieldVerified = fieldVerified;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPermitted(String s) {
        return permitted;
    }

    public void setPermitted(String permitted) {
        this.permitted = permitted;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getOccupancyCondition() {
        return occupancyCondition;
    }

    public void setOccupancyCondition(String occupancyCondition) {
        this.occupancyCondition = occupancyCondition;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getSideNumber() {
        return sideNumber;
    }

    public void setSideNumber(String sideNumber) {
        this.sideNumber = sideNumber;
    }
}