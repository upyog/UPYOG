package org.egov.egf.masters.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "property_tax_receipt_register")
public class PropertyTaxReceiptRegister implements Serializable{
	@Id
	private Integer id;
    private String tenantid;
    private String propertyid;
    private String receiptnumber;
    private String receiptdate;
    private String paymentmode;
    private String transactionnumber;
    private String interest;
    private String penalty;
    private String swatchathatax;
    private String propertytax;
    private String totalcollection;
    private String username;
    private String mohallaname;
    private String doorno;
    private String name;
    private String user_uuid;
    private Date system_updateddate;
    private int flag;
    private String note;
    private Date system_createddate;
    private Date createddate;
    private Date updateddate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTenantid() {
		return tenantid;
	}
	public void setTenantid(String tenantid) {
		this.tenantid = tenantid;
	}
	public String getPropertyid() {
		return propertyid;
	}
	public void setPropertyid(String propertyid) {
		this.propertyid = propertyid;
	}
	public String getReceiptnumber() {
		return receiptnumber;
	}
	public void setReceiptnumber(String receiptnumber) {
		this.receiptnumber = receiptnumber;
	}
	public String getReceiptdate() {
		return receiptdate;
	}
	public void setReceiptdate(String receiptdate) {
		this.receiptdate = receiptdate;
	}
	public String getPaymentmode() {
		return paymentmode;
	}
	public void setPaymentmode(String paymentmode) {
		this.paymentmode = paymentmode;
	}
	public String getTransactionnumber() {
		return transactionnumber;
	}
	public void setTransactionnumber(String transactionnumber) {
		this.transactionnumber = transactionnumber;
	}
	public String getInterest() {
		return interest;
	}
	public void setInterest(String interest) {
		this.interest = interest;
	}
	public String getPenalty() {
		return penalty;
	}
	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}
	public String getSwatchathatax() {
		return swatchathatax;
	}
	public void setSwatchathatax(String swatchathatax) {
		this.swatchathatax = swatchathatax;
	}
	public String getPropertytax() {
		return propertytax;
	}
	public void setPropertytax(String propertytax) {
		this.propertytax = propertytax;
	}
	public String getTotalcollection() {
		return totalcollection;
	}
	public void setTotalcollection(String totalcollection) {
		this.totalcollection = totalcollection;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMohallaname() {
		return mohallaname;
	}
	public void setMohallaname(String mohallaname) {
		this.mohallaname = mohallaname;
	}
	public String getDoorno() {
		return doorno;
	}
	public void setDoorno(String doorno) {
		this.doorno = doorno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser_uuid() {
		return user_uuid;
	}
	public void setUser_uuid(String user_uuid) {
		this.user_uuid = user_uuid;
	}
	public Date getSystem_updateddate() {
		return system_updateddate;
	}
	public void setSystem_updateddate(Date system_updateddate) {
		this.system_updateddate = system_updateddate;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Date getSystem_createddate() {
		return system_createddate;
	}
	public void setSystem_createddate(Date system_createddate) {
		this.system_createddate = system_createddate;
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
	@Override
	public String toString() {
		return "PropertyTaxReceiptRegister [id=" + id + ", tenantid=" + tenantid + ", propertyid=" + propertyid
				+ ", receiptnumber=" + receiptnumber + ", receiptdate=" + receiptdate + ", paymentmode=" + paymentmode
				+ ", transactionnumber=" + transactionnumber + ", interest=" + interest + ", penalty=" + penalty
				+ ", swatchathatax=" + swatchathatax + ", propertytax=" + propertytax + ", totalcollection="
				+ totalcollection + ", username=" + username + ", mohallaname=" + mohallaname + ", doorno=" + doorno
				+ ", name=" + name + ", user_uuid=" + user_uuid + ", system_updateddate=" + system_updateddate
				+ ", flag=" + flag + ", note=" + note + ", system_createddate=" + system_createddate + ", createddate="
				+ createddate + ", updateddate=" + updateddate + "]";
	}
	
	//code 
    
    
	
	
}
