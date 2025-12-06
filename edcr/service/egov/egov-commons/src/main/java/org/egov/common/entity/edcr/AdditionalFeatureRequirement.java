package org.egov.common.entity.edcr;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class AdditionalFeatureRequirement extends MdmsFeatureRule {

    @JsonProperty("additionalFeatureMinRequiredFloorHeight")
    private BigDecimal additionalFeatureMinRequiredFloorHeight;
    @JsonProperty("additionalFeatureMaxPermissibleFloorHeight")
    private BigDecimal additionalFeatureMaxPermissibleFloorHeight;
    @JsonProperty("additionalFeatureStiltFloor")
    private  BigDecimal additionalFeatureStiltFloor;
    @JsonProperty("additionalFeatureRoadWidthA")
    private BigDecimal additionalFeatureRoadWidthA;
    @JsonProperty("additionalFeatureRoadWidthB")
    private BigDecimal additionalFeatureRoadWidthB;
    @JsonProperty("additionalFeatureRoadWidthC")
    private BigDecimal additionalFeatureRoadWidthC;
    @JsonProperty("additionalFeatureRoadWidthD")
    private BigDecimal additionalFeatureRoadWidthD;
    @JsonProperty("additionalFeatureRoadWidthE")
    private BigDecimal additionalFeatureRoadWidthE;
    @JsonProperty("additionalFeatureRoadWidthF")
    private BigDecimal additionalFeatureRoadWidthF;

    @JsonProperty("additionalFeatureNewRoadWidthA")
    private BigDecimal additionalFeatureNewRoadWidthA;
    @JsonProperty("additionalFeatureNewRoadWidthB")
    private BigDecimal additionalFeatureNewRoadWidthB;
    @JsonProperty("additionalFeatureNewRoadWidthC")
    private BigDecimal additionalFeatureNewRoadWidthC;
    @JsonProperty("additionalFeatureRoadWidthAcceptedBC")
    private BigDecimal additionalFeatureRoadWidthAcceptedBC;
    @JsonProperty("additionalFeatureRoadWidthAcceptedCD")
    private BigDecimal additionalFeatureRoadWidthAcceptedCD;
    @JsonProperty("additionalFeatureRoadWidthAcceptedDE")
    private BigDecimal additionalFeatureRoadWidthAcceptedDE;
    @JsonProperty("additionalFeatureRoadWidthAcceptedEF")
    private BigDecimal additionalFeatureRoadWidthAcceptedEF;

    @JsonProperty("additionalFeatureRoadWidthNewAcceptedAB")
    private BigDecimal additionalFeatureRoadWidthNewAcceptedAB;
    @JsonProperty("additionalFeatureRoadWidthNewAcceptedBC")
    private BigDecimal additionalFeatureRoadWidthNewAcceptedBC;

    @JsonProperty("additionalFeatureFloorsAcceptedBC")
    private BigDecimal additionalFeatureFloorsAcceptedBC;
    @JsonProperty("additionalFeatureFloorsAcceptedCD")
    private BigDecimal additionalFeatureFloorsAcceptedCD;
    @JsonProperty("additionalFeatureFloorsAcceptedDE")
    private BigDecimal additionalFeatureFloorsAcceptedDE;
    @JsonProperty("additionalFeatureFloorsAcceptedEF")
    private BigDecimal additionalFeatureFloorsAcceptedEF;

    @JsonProperty("additionalFeatureFloorsNewAcceptedAB")
    private BigDecimal additionalFeatureFloorsNewAcceptedAB;
    @JsonProperty("additionalFeatureFloorsNewAcceptedBC")
    private BigDecimal additionalFeatureFloorsNewAcceptedBC;

    @JsonProperty("additionalFeatureBasementPlotArea")
    private BigDecimal additionalFeatureBasementPlotArea;
    @JsonProperty("additionalFeatureBasementAllowed")
    private BigDecimal additionalFeatureBasementAllowed;
    @JsonProperty("additionalFeatureBarrierValue")
    private BigDecimal additionalFeatureBarrierValue;

    @JsonProperty("afGreenBuildingValueA")
    private BigDecimal afGreenBuildingValueA;
    @JsonProperty("afGreenBuildingValueB")
    private BigDecimal afGreenBuildingValueB;
    @JsonProperty("afGreenBuildingValueC")
    private BigDecimal afGreenBuildingValueC;
    @JsonProperty("afGreenBuildingValueD")
    private BigDecimal afGreenBuildingValueD;


    public BigDecimal getAdditionalFeatureRoadWidthA() {
        return additionalFeatureRoadWidthA;
    }

    public void setAdditionalFeatureRoadWidthA(BigDecimal additionalFeatureRoadWidthA) {
        this.additionalFeatureRoadWidthA = additionalFeatureRoadWidthA;
    }

    public BigDecimal getAdditionalFeatureRoadWidthB() {
        return additionalFeatureRoadWidthB;
    }

    public void setAdditionalFeatureRoadWidthB(BigDecimal additionalFeatureRoadWidthB1) {
        this.additionalFeatureRoadWidthB = additionalFeatureRoadWidthB1;
    }

    public BigDecimal getAdditionalFeatureRoadWidthE() {
        return additionalFeatureRoadWidthE;
    }

    public void setAdditionalFeatureRoadWidthE(BigDecimal additionalFeatureRoadWidthE) {
        this.additionalFeatureRoadWidthE = additionalFeatureRoadWidthE;
    }

    public BigDecimal getAdditionalFeatureRoadWidthD() {
        return additionalFeatureRoadWidthD;
    }

    public void setAdditionalFeatureRoadWidthD(BigDecimal additionalFeatureRoadWidthD) {
        this.additionalFeatureRoadWidthD = additionalFeatureRoadWidthD;
    }

    public BigDecimal getAdditionalFeatureRoadWidthC() {
        return additionalFeatureRoadWidthC;
    }

    public void setAdditionalFeatureRoadWidthC(BigDecimal additionalFeatureRoadWidthC) {
        this.additionalFeatureRoadWidthC = additionalFeatureRoadWidthC;
    }

    public BigDecimal getAdditionalFeatureRoadWidthF() {
        return additionalFeatureRoadWidthF;
    }

    public void setAdditionalFeatureRoadWidthF(BigDecimal additionalFeatureRoadWidthF) {
        this.additionalFeatureRoadWidthF = additionalFeatureRoadWidthF;
    }

    public BigDecimal getAdditionalFeatureMinRequiredFloorHeight() {
        return additionalFeatureMinRequiredFloorHeight;
    }

    public void setAdditionalFeatureMinRequiredFloorHeight(BigDecimal additionalFeatureMinRequiredFloorHeight) {
        this.additionalFeatureMinRequiredFloorHeight = additionalFeatureMinRequiredFloorHeight;
    }

    public BigDecimal getAdditionalFeatureMaxPermissibleFloorHeight() {
        return additionalFeatureMaxPermissibleFloorHeight;
    }

    public void setAdditionalFeatureMaxPermissibleFloorHeight(BigDecimal additionalFeatureMaxPermissibleFloorHeight) {
        this.additionalFeatureMaxPermissibleFloorHeight = additionalFeatureMaxPermissibleFloorHeight;
    }

    public BigDecimal getAdditionalFeatureNewRoadWidthA() {
        return additionalFeatureNewRoadWidthA;
    }

    public void setAdditionalFeatureNewRoadWidthA(BigDecimal additionalFeatureNewRoadWidthA) {
        this.additionalFeatureNewRoadWidthA = additionalFeatureNewRoadWidthA;
    }

    public BigDecimal getAdditionalFeatureNewRoadWidthB() {
        return additionalFeatureNewRoadWidthB;
    }

    public void setAdditionalFeatureNewRoadWidthB(BigDecimal additionalFeatureNewRoadWidthB) {
        this.additionalFeatureNewRoadWidthB = additionalFeatureNewRoadWidthB;
    }

    public BigDecimal getAdditionalFeatureNewRoadWidthC() {
        return additionalFeatureNewRoadWidthC;
    }

    public void setAdditionalFeatureNewRoadWidthC(BigDecimal additionalFeatureNewRoadWidthC) {
        this.additionalFeatureNewRoadWidthC = additionalFeatureNewRoadWidthC;
    }

    public BigDecimal getAdditionalFeatureRoadWidthAcceptedBC() {
        return additionalFeatureRoadWidthAcceptedBC;
    }

    public void setAdditionalFeatureRoadWidthAcceptedBC(BigDecimal additionalFeatureRoadWidthAcceptedBC) {
        this.additionalFeatureRoadWidthAcceptedBC = additionalFeatureRoadWidthAcceptedBC;
    }

    public BigDecimal getAdditionalFeatureRoadWidthAcceptedCD() {
        return additionalFeatureRoadWidthAcceptedCD;
    }

    public void setAdditionalFeatureRoadWidthAcceptedCD(BigDecimal additionalFeatureRoadWidthAcceptedCD) {
        this.additionalFeatureRoadWidthAcceptedCD = additionalFeatureRoadWidthAcceptedCD;
    }

    public BigDecimal getAdditionalFeatureRoadWidthAcceptedDE() {
        return additionalFeatureRoadWidthAcceptedDE;
    }

    public void setAdditionalFeatureRoadWidthAcceptedDE(BigDecimal additionalFeatureRoadWidthAcceptedDE) {
        this.additionalFeatureRoadWidthAcceptedDE = additionalFeatureRoadWidthAcceptedDE;
    }

    public BigDecimal getAdditionalFeatureRoadWidthAcceptedEF() {
        return additionalFeatureRoadWidthAcceptedEF;
    }

    public void setAdditionalFeatureRoadWidthAcceptedEF(BigDecimal additionalFeatureRoadWidthAcceptedEF) {
        this.additionalFeatureRoadWidthAcceptedEF = additionalFeatureRoadWidthAcceptedEF;
    }

    public BigDecimal getAdditionalFeatureRoadWidthNewAcceptedAB() {
        return additionalFeatureRoadWidthNewAcceptedAB;
    }

    public void setAdditionalFeatureRoadWidthNewAcceptedAB(BigDecimal additionalFeatureRoadWidthNewAcceptedAB) {
        this.additionalFeatureRoadWidthNewAcceptedAB = additionalFeatureRoadWidthNewAcceptedAB;
    }

    public BigDecimal getAdditionalFeatureRoadWidthNewAcceptedBC() {
        return additionalFeatureRoadWidthNewAcceptedBC;
    }

    public void setAdditionalFeatureRoadWidthNewAcceptedBC(BigDecimal additionalFeatureRoadWidthNewAcceptedBC) {
        this.additionalFeatureRoadWidthNewAcceptedBC = additionalFeatureRoadWidthNewAcceptedBC;
    }

    public BigDecimal getAdditionalFeatureStiltFloor() {
        return additionalFeatureStiltFloor;
    }

    public void setAdditionalFeatureStiltFloor(BigDecimal additionalFeatureStiltFloor) {
        this.additionalFeatureStiltFloor = additionalFeatureStiltFloor;
    }

    public BigDecimal getAdditionalFeatureFloorsAcceptedBC() {
        return additionalFeatureFloorsAcceptedBC;
    }

    public void setAdditionalFeatureFloorsAcceptedBC(BigDecimal additionalFeatureFloorsAcceptedBC) {
        this.additionalFeatureFloorsAcceptedBC = additionalFeatureFloorsAcceptedBC;
    }

    public BigDecimal getAdditionalFeatureFloorsAcceptedCD() {
        return additionalFeatureFloorsAcceptedCD;
    }

    public void setAdditionalFeatureFloorsAcceptedCD(BigDecimal additionalFeatureFloorsAcceptedCD) {
        this.additionalFeatureFloorsAcceptedCD = additionalFeatureFloorsAcceptedCD;
    }

    public BigDecimal getAdditionalFeatureFloorsAcceptedDE() {
        return additionalFeatureFloorsAcceptedDE;
    }

    public void setAdditionalFeatureFloorsAcceptedDE(BigDecimal additionalFeatureFloorsAcceptedDE) {
        this.additionalFeatureFloorsAcceptedDE = additionalFeatureFloorsAcceptedDE;
    }

    public BigDecimal getAdditionalFeatureFloorsAcceptedEF() {
        return additionalFeatureFloorsAcceptedEF;
    }

    public void setAdditionalFeatureFloorsAcceptedEF(BigDecimal additionalFeatureFloorsAcceptedEF) {
        this.additionalFeatureFloorsAcceptedEF = additionalFeatureFloorsAcceptedEF;
    }

    public BigDecimal getAdditionalFeatureFloorsNewAcceptedAB() {
        return additionalFeatureFloorsNewAcceptedAB;
    }

    public void setAdditionalFeatureFloorsNewAcceptedAB(BigDecimal additionalFeatureFloorsNewAcceptedAB) {
        this.additionalFeatureFloorsNewAcceptedAB = additionalFeatureFloorsNewAcceptedAB;
    }

    public BigDecimal getAdditionalFeatureFloorsNewAcceptedBC() {
        return additionalFeatureFloorsNewAcceptedBC;
    }

    public void setAdditionalFeatureFloorsNewAcceptedBC(BigDecimal additionalFeatureFloorsNewAcceptedBC) {
        this.additionalFeatureFloorsNewAcceptedBC = additionalFeatureFloorsNewAcceptedBC;
    }

    public BigDecimal getAdditionalFeatureBasementPlotArea() {
        return additionalFeatureBasementPlotArea;
    }

    public void setAdditionalFeatureBasementPlotArea(BigDecimal additionalFeatureBasementPlotArea) {
        this.additionalFeatureBasementPlotArea = additionalFeatureBasementPlotArea;
    }

    public BigDecimal getAdditionalFeatureBasementAllowed() {
        return additionalFeatureBasementAllowed;
    }

    public void setAdditionalFeatureBasementAllowed(BigDecimal additionalFeatureBasementAllowed) {
        this.additionalFeatureBasementAllowed = additionalFeatureBasementAllowed;
    }

    public BigDecimal getAdditionalFeatureBarrierValue() {
        return additionalFeatureBarrierValue;
    }

    public void setAdditionalFeatureBarrierValue(BigDecimal additionalFeatureBarrierValue) {
        this.additionalFeatureBarrierValue = additionalFeatureBarrierValue;
    }

    public BigDecimal getAfGreenBuildingValueA() {
        return afGreenBuildingValueA;
    }

    public void setAfGreenBuildingValueA(BigDecimal afGreenBuildingValueA) {
        this.afGreenBuildingValueA = afGreenBuildingValueA;
    }

    public BigDecimal getAfGreenBuildingValueB() {
        return afGreenBuildingValueB;
    }

    public void setAfGreenBuildingValueB(BigDecimal afGreenBuildingValueB) {
        this.afGreenBuildingValueB = afGreenBuildingValueB;
    }

    public BigDecimal getAfGreenBuildingValueC() {
        return afGreenBuildingValueC;
    }

    public void setAfGreenBuildingValueC(BigDecimal afGreenBuildingValueC) {
        this.afGreenBuildingValueC = afGreenBuildingValueC;
    }

    public BigDecimal getAfGreenBuildingValueD() {
        return afGreenBuildingValueD;
    }

    public void setAfGreenBuildingValueD(BigDecimal afGreenBuildingValueD) {
        this.afGreenBuildingValueD = afGreenBuildingValueD;
    }

    @Override
    public String toString() {
        return "AdditionalFeatureRequirement [additionalFeatureMinRequiredFloorHeight=" + additionalFeatureMinRequiredFloorHeight
                + ", additionalFeatureMaxPermissibleFloorHeight=" + additionalFeatureMaxPermissibleFloorHeight + "]";
    }

}
