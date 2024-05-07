package org.egov.egf.masters.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.metamodel.SingularAttribute;

import org.egov.commons.Fund;
import org.egov.commons.Scheme;
import org.egov.infra.persistence.validator.annotation.Unique;

@Entity 
@Table(name = "grant_amount_transfer")

//@Unique( id = "id", tableName = "grant_amount_transfer", enableDfltMsg = true)

public class GrantAmountTransfer implements Serializable {
	
	private final static String  SEQ_EGF_GRANTAMOUNT= "SEQ_EGF_GRANTAMOUNT";
	
	@Id
    @GeneratedValue(generator = SEQ_EGF_GRANTAMOUNT, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = GrantAmountTransfer.SEQ_EGF_GRANTAMOUNT, sequenceName = "\"seq_egf_grantamount\"", allocationSize = 1)

    private Long id; 
    @Column(name = "code")                                  //granttype  UI
    private String code;
    @Column(name = "name") 
    private String name;                                     //grant  in UI
    @Column(name = "date")
    private Date date;
    @Column(name = "isactive")
    private Boolean isActive;
    @Column(name = "description")
    private String description;
    
    @Column(name = "fundid")
    private Long fundid;
    
    @Column(name = "schemeid")
    private Long schemeId;
    @Column(name = "ulbname")
    private String ulbName;
    @Column(name = "ulbcode")
    private String ulbCode;
    @Column(name = "amount")
    private BigDecimal amount;
    
    
    
    
	/*
	 * @Column(name = "bankAccount") private String bankAccount;
	 */
    @Column(name = "bankid")
    private String bankId;
    
    @Column(name = "branchid")
    private Long branchid;
    
    @Column(name = "bankaccountnumber")
    private String bankAccountNumber;

    @Column(name = "bankifsc")
    private String bankIFSC;        
    @Column(name = "createddate")
    private Date createdDate;
    @Column(name = "lastmodifieddate")
    private Date lastModifiedDate;
    @Column(name = "createdby")
    private Long createdBy;
    @Column(name = "lastmodifiedby")
    private Long lastModifiedBy;
    
    @Column(name= "bankaccounttype")
    private String bankAccountType;
    @Column (name="flag")
    private int flag;
    
    
    
    
    
    public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	@Transient
    private String bankAndBranch;
    
   
	public String getBankAndBranch() {
		return bankAndBranch;
	}
	public void setBankAndBranch(String bankAndBranch) {
		this.bankAndBranch = bankAndBranch;
	}
	
	public String getBankAccountType() {
		return bankAccountType;
	}
	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getFundid() {
		return fundid;
	}
	public void setFundid(Long fundid) {
		this.fundid = fundid;
	}
	public Long getSchemeId() {
		return schemeId;
	}
	public void setSchemeId(Long schemeId) {
		this.schemeId = schemeId;
	}
	public String getUlbName() {
		return ulbName;
	}
	public void setUlbName(String ulbName) {
		this.ulbName = ulbName;
	}
	public String getUlbCode() {
		return ulbCode;
	}
	public void setUlbCode(String ulbCode) {
		this.ulbCode = ulbCode;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/*
	 * public String getBankAccount() { return bankAccount; } public void
	 * setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
	 */
	
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public Long getBranchid() {
		return branchid;
	}
	public void setBranchid(Long branchid) {
		this.branchid = branchid;
	}
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	public String getBankIFSC() {
		return bankIFSC;
	}
	public void setBankIFSC(String bankIFSC) {
		this.bankIFSC = bankIFSC;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public Long getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(Long lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	@Override
	public String toString() {
		return "GrantAmountTransfer [id=" + id + ", code=" + code + ", name=" + name + ", date=" + date + ", isActive="
				+ isActive + ", description=" + description + ", fundid=" + fundid + ", schemeId=" + schemeId
				+ ", ulbName=" + ulbName + ", ulbCode=" + ulbCode + ", amount=" + amount + ", bankId=" + bankId
				+ ", branchid=" + branchid + ", bankAccountNumber=" + bankAccountNumber + ", bankIFSC=" + bankIFSC
				+ ", createdDate=" + createdDate + ", lastModifiedDate=" + lastModifiedDate + ", createdBy=" + createdBy
				+ ", lastModifiedBy=" + lastModifiedBy + ", bankAccountType=" + bankAccountType + ", flag=" + flag
				+ ", bankAndBranch=" + bankAndBranch + "]";
	}
	
	
	
	


    
    //code 
    

}