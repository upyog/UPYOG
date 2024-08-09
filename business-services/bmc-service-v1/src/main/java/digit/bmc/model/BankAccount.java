package digit.bmc.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table
public class BankAccount {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "id")
	    private Long id;

	    @Column(name = "branchid")
	    private Long branchId;

	    @Column(name = "accountnumber")
	    private String accountNumber;

	    @Column(name = "accounttype")
	    private String accountType;

	    @Column(name = "narration")
	    private String narration;

	    @Column(name = "isactive")
	    private Boolean isActive;

	    @Column(name = "glcodeid")
	    private Long glCodeId;

	    @Column(name = "fundid")
	    private Long fundId;

	    @Column(name = "payto")
	    private String payTo;

	    @Column(name = "type")
	    private String type;

	    @Column(name = "createdby")
	    private Long createdBy;

	    @Column(name = "lastmodifiedby")
	    private Long lastModifiedBy;

	    @Column(name = "createddate")
	    private Timestamp createdDate;

	    @Column(name = "lastmodifieddate")
	    private Timestamp lastModifiedDate;

	    @Column(name = "version")
	    private Long version;

	    @Column(name = "chequeformatid")
	    private Long chequeFormatId;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getBranchId() {
			return branchId;
		}

		public void setBranchId(Long branchId) {
			this.branchId = branchId;
		}

		public String getAccountNumber() {
			return accountNumber;
		}

		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}

		public String getAccountType() {
			return accountType;
		}

		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}

		public String getNarration() {
			return narration;
		}

		public void setNarration(String narration) {
			this.narration = narration;
		}

		public Boolean getIsActive() {
			return isActive;
		}

		public void setIsActive(Boolean isActive) {
			this.isActive = isActive;
		}

		public Long getGlCodeId() {
			return glCodeId;
		}

		public void setGlCodeId(Long glCodeId) {
			this.glCodeId = glCodeId;
		}

		public Long getFundId() {
			return fundId;
		}

		public void setFundId(Long fundId) {
			this.fundId = fundId;
		}

		public String getPayTo() {
			return payTo;
		}

		public void setPayTo(String payTo) {
			this.payTo = payTo;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
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

		public Timestamp getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Timestamp createdDate) {
			this.createdDate = createdDate;
		}

		public Timestamp getLastModifiedDate() {
			return lastModifiedDate;
		}

		public void setLastModifiedDate(Timestamp lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}

		public Long getVersion() {
			return version;
		}

		public void setVersion(Long version) {
			this.version = version;
		}

		public Long getChequeFormatId() {
			return chequeFormatId;
		}

		public void setChequeFormatId(Long chequeFormatId) {
			this.chequeFormatId = chequeFormatId;
		}

		@Override
		public String toString() {
			return "BankAccount [id=" + id + ", branchId=" + branchId + ", accountNumber=" + accountNumber
					+ ", accountType=" + accountType + ", narration=" + narration + ", isActive=" + isActive
					+ ", glCodeId=" + glCodeId + ", fundId=" + fundId + ", payTo=" + payTo + ", type=" + type
					+ ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + ", createdDate=" + createdDate
					+ ", lastModifiedDate=" + lastModifiedDate + ", version=" + version + ", chequeFormatId="
					+ chequeFormatId + "]";
		}
	    
	    // code 
	    
	    

}
