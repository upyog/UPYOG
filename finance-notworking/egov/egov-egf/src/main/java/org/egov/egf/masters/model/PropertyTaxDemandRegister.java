package org.egov.egf.masters.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "property_tax_demand_register")
public class PropertyTaxDemandRegister implements Serializable{
	
	@Id
    private Long id;
	@Column(name = "propertyid")
    private String propertyid;
	@Column(name = "oldpropertyid")
    private String oldpropertyid;
	@Column(name = "doorno")
    private String doorno;
	@Column(name = "mohalla")
    private String mohalla;
	@Column(name = "propertytype")
    private String propertytype;
	@Column(name = "usage")
    private String usage;
	@Column(name = "name")
    private String name;
	@Column(name = "currentarv")
    private String currentarv;
	@Column(name = "currenttax")
    private String currenttax;
	@Column(name = "currentrebate")
    private String currentrebate;
	@Column(name = "arreartax")
    private String arreartax;
	@Column(name = "penaltytax")
    private String penaltytax;
	@Column(name = "rebate")
    private String rebate;
	@Column(name = "totaltax")
    private String totaltax;
	@Column(name = "currentcollected")
    private String currentcollected;
	@Column(name = "arrearcollected")
    private String arrearcollected;
	@Column(name = "penaltycollected")
    private String penaltycollected;
	@Column(name = "totalcollected")
    private String totalcollected;
	@Column(name = "voucherid")
    private String voucherid;
	@Column(name = "flag")
    private Integer flag;
	@Column(name = "createddate")
    private Date createddate;
	@Column(name = "updateddate")
    private Date updateddate;
	@Column(name = "note")
    private String note;
	@Column(name = "systemcreateddate")
    private Date systemcreateddate;
	@Column(name = "systemupdateddate")
    private Date systemupdateddate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPropertyid() {
		return propertyid;
	}
	public void setPropertyid(String propertyid) {
		this.propertyid = propertyid;
	}
	public String getOldpropertyid() {
		return oldpropertyid;
	}
	public void setOldpropertyid(String oldpropertyid) {
		this.oldpropertyid = oldpropertyid;
	}
	public String getDoorno() {
		return doorno;
	}
	public void setDoorno(String doorno) {
		this.doorno = doorno;
	}
	public String getMohalla() {
		return mohalla;
	}
	public void setMohalla(String mohalla) {
		this.mohalla = mohalla;
	}
	public String getPropertytype() {
		return propertytype;
	}
	public void setPropertytype(String propertytype) {
		this.propertytype = propertytype;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCurrentarv() {
		return currentarv;
	}
	public void setCurrentarv(String currentarv) {
		this.currentarv = currentarv;
	}
	public String getCurrenttax() {
		return currenttax;
	}
	public void setCurrenttax(String currenttax) {
		this.currenttax = currenttax;
	}
	public String getCurrentrebate() {
		return currentrebate;
	}
	public void setCurrentrebate(String currentrebate) {
		this.currentrebate = currentrebate;
	}
	public String getArreartax() {
		return arreartax;
	}
	public void setArreartax(String arreartax) {
		this.arreartax = arreartax;
	}
	public String getPenaltytax() {
		return penaltytax;
	}
	public void setPenaltytax(String penaltytax) {
		this.penaltytax = penaltytax;
	}
	public String getRebate() {
		return rebate;
	}
	public void setRebate(String rebate) {
		this.rebate = rebate;
	}
	public String getTotaltax() {
		return totaltax;
	}
	public void setTotaltax(String totaltax) {
		this.totaltax = totaltax;
	}
	public String getCurrentcollected() {
		return currentcollected;
	}
	public void setCurrentcollected(String currentcollected) {
		this.currentcollected = currentcollected;
	}
	public String getArrearcollected() {
		return arrearcollected;
	}
	public void setArrearcollected(String arrearcollected) {
		this.arrearcollected = arrearcollected;
	}
	public String getPenaltycollected() {
		return penaltycollected;
	}
	public void setPenaltycollected(String penaltycollected) {
		this.penaltycollected = penaltycollected;
	}
	public String getTotalcollected() {
		return totalcollected;
	}
	public void setTotalcollected(String totalcollected) {
		this.totalcollected = totalcollected;
	}
	public String getVoucherid() {
		return voucherid;
	}
	public void setVoucherid(String voucherid) {
		this.voucherid = voucherid;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public Date getCreateddate() {
		return createddate;
	}
	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}
	public Date getUpdateddate() {
		return updateddate;
	}
	public void setUpdateddate(Date updateddate) {
		this.updateddate = updateddate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Date getSystemcreateddate() {
		return systemcreateddate;
	}
	public void setSystemcreateddate(Date systemcreateddate) {
		this.systemcreateddate = systemcreateddate;
	}
	public Date getSystemupdateddate() {
		return systemupdateddate;
	}
	public void setSystemupdateddate(Date systemupdateddate) {
		this.systemupdateddate = systemupdateddate;
	}
	@Override
	public String toString() {
		return "PropertyTaxDemandRegister [id=" + id + ", propertyid=" + propertyid + ", oldpropertyid=" + oldpropertyid
				+ ", doorno=" + doorno + ", mohalla=" + mohalla + ", propertytype=" + propertytype + ", usage=" + usage
				+ ", name=" + name + ", currentarv=" + currentarv + ", currenttax=" + currenttax + ", currentrebate="
				+ currentrebate + ", arreartax=" + arreartax + ", penaltytax=" + penaltytax + ", rebate=" + rebate
				+ ", totaltax=" + totaltax + ", currentcollected=" + currentcollected + ", arrearcollected="
				+ arrearcollected + ", penaltycollected=" + penaltycollected + ", totalcollected=" + totalcollected
				+ ", voucherid=" + voucherid + ", flag=" + flag + ", createddate=" + createddate + ", updateddate="
				+ updateddate + ", note=" + note + ", systemcreateddate=" + systemcreateddate + ", systemupdateddate="
				+ systemupdateddate + "]";
	}
	
	//code
	

}
