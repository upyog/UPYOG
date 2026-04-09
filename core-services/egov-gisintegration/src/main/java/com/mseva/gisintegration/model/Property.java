package com.mseva.gisintegration.model;

import javax.persistence.*;

@Entity
@Table(name = "propertytax_backup") // You can change this to your preferred table name
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Integer sno;

    private String tenantid;

    private String propertyid;

    private String surveyid;

    private String oldpropertyid;

    private String firmbusinessname;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String localitycode;

    private String localityname;

    private String blockname;

    private String zonename;

    private String plotsize;

    private String propertyusagetype;

    private String propertytype;

    private String ownershipcategory;

    private String paymentdate;

    private String receiptnumber;

    private String amoutpaid; // Named 'amoutpaid' to match your image typo exactly

    private String assessmentyear;

    public Property() {
    }

    // Standard Getters and Setters
    public Integer getSno() { return sno; }
    public void setSno(Integer sno) { this.sno = sno; }

    public String getTenantid() { return tenantid; }
    public void setTenantid(String tenantid) { this.tenantid = tenantid; }

    public String getPropertyid() { return propertyid; }
    public void setPropertyid(String propertyid) { this.propertyid = propertyid; }

    public String getSurveyid() { return surveyid; }
    public void setSurveyid(String surveyid) { this.surveyid = surveyid; }

    public String getOldpropertyid() { return oldpropertyid; }
    public void setOldpropertyid(String oldpropertyid) { this.oldpropertyid = oldpropertyid; }

    public String getFirmbusinessname() { return firmbusinessname; }
    public void setFirmbusinessname(String firmbusinessname) { this.firmbusinessname = firmbusinessname; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLocalitycode() { return localitycode; }
    public void setLocalitycode(String localitycode) { this.localitycode = localitycode; }

    public String getLocalityname() { return localityname; }
    public void setLocalityname(String localityname) { this.localityname = localityname; }

    public String getBlockname() { return blockname; }
    public void setBlockname(String blockname) { this.blockname = blockname; }

    public String getZonename() { return zonename; }
    public void setZonename(String zonename) { this.zonename = zonename; }

    public String getPlotsize() { return plotsize; }
    public void setPlotsize(String plotsize) { this.plotsize = plotsize; }

    public String getPropertyusagetype() { return propertyusagetype; }
    public void setPropertyusagetype(String propertyusagetype) { this.propertyusagetype = propertyusagetype; }

    public String getPropertytype() { return propertytype; }
    public void setPropertytype(String propertytype) { this.propertytype = propertytype; }

    public String getOwnershipcategory() { return ownershipcategory; }
    public void setOwnershipcategory(String ownershipcategory) { this.ownershipcategory = ownershipcategory; }

    public String getPaymentdate() { return paymentdate; }
    public void setPaymentdate(String paymentdate) { this.paymentdate = paymentdate; }

    public String getReceiptnumber() { return receiptnumber; }
    public void setReceiptnumber(String receiptnumber) { this.receiptnumber = receiptnumber; }

    public String getAmoutpaid() { return amoutpaid; }
    public void setAmoutpaid(String amoutpaid) { this.amoutpaid = amoutpaid; }

    public String getAssessmentyear() { return assessmentyear; }
    public void setAssessmentyear(String assessmentyear) { this.assessmentyear = assessmentyear; }
}