package com.mseva.gisintegration.model;

import javax.persistence.*;

@Entity
@Table(name = "watertax_backup") // Updated to a standard table name
public class WaterTax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Integer sno; // Changed from UUID to Integer per specification

    private String tenantid;

    private String connectionno;

    private String property_id;

    private String propertyid;

    private String surveyid;

    private String oldpropertyid;

    private String propertytype;

    private String ownershipcategory;

    private String propertyusagetype;

    private String nooffloors;

    private String plotsize;

    private String superbuilduparea;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String localityname;

    private String blockname;

    private String assessmentyear;

    public WaterTax() {
    }

    // --- Getters and Setters ---

    public Integer getSno() {
        return sno;
    }

    public void setSno(Integer sno) {
        this.sno = sno;
    }

    public String getTenantid() {
        return tenantid;
    }

    public void setTenantid(String tenantid) {
        this.tenantid = tenantid;
    }

    public String getConnectionno() {
        return connectionno;
    }

    public void setConnectionno(String connectionno) {
        this.connectionno = connectionno;
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public String getPropertyid() {
        return propertyid;
    }

    public void setPropertyid(String propertyid) {
        this.propertyid = propertyid;
    }

    public String getSurveyid() {
        return surveyid;
    }

    public void setSurveyid(String surveyid) {
        this.surveyid = surveyid;
    }

    public String getOldpropertyid() {
        return oldpropertyid;
    }

    public void setOldpropertyid(String oldpropertyid) {
        this.oldpropertyid = oldpropertyid;
    }

    public String getPropertytype() {
        return propertytype;
    }

    public void setPropertytype(String propertytype) {
        this.propertytype = propertytype;
    }

    public String getOwnershipcategory() {
        return ownershipcategory;
    }

    public void setOwnershipcategory(String ownershipcategory) {
        this.ownershipcategory = ownershipcategory;
    }

    public String getPropertyusagetype() {
        return propertyusagetype;
    }

    public void setPropertyusagetype(String propertyusagetype) {
        this.propertyusagetype = propertyusagetype;
    }

    public String getNooffloors() {
        return nooffloors;
    }

    public void setNooffloors(String nooffloors) {
        this.nooffloors = nooffloors;
    }

    public String getPlotsize() {
        return plotsize;
    }

    public void setPlotsize(String plotsize) {
        this.plotsize = plotsize;
    }

    public String getSuperbuilduparea() {
        return superbuilduparea;
    }

    public void setSuperbuilduparea(String superbuilduparea) {
        this.superbuilduparea = superbuilduparea;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocalityname() {
        return localityname;
    }

    public void setLocalityname(String localityname) {
        this.localityname = localityname;
    }

    public String getBlockname() {
        return blockname;
    }

    public void setBlockname(String blockname) {
        this.blockname = blockname;
    }

    public String getAssessmentyear() {
        return assessmentyear;
    }

    public void setAssessmentyear(String assessmentyear) {
        this.assessmentyear = assessmentyear;
    }

    @Override
    public String toString() {
        return "WaterTax{" +
                "sno=" + sno +
                ", tenantid='" + tenantid + '\'' +
                ", connectionno='" + connectionno + '\'' +
                ", property_id='" + property_id + '\'' +
                ", propertyid='" + propertyid + '\'' +
                ", assessmentyear='" + assessmentyear + '\'' +
                '}';
    }
}