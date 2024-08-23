package digit.bmc.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table
public class Application {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("id")
	private Long id = null;
	@JsonProperty
	@Column(name = "uuid")
    private String uuid = null;
	@JsonProperty
	@Column(name = "optedid")
    private Integer optedId= null;
	@JsonProperty
	@Column(name = "application_status")
    private String applicationStatus = null;
	@JsonProperty
	@Column(name = "verification_status")
    private String verificationStatus = null;
	@JsonProperty
	@Column(name = "first_approval_status")
    private String firstApprovalStatus = null;
	@JsonProperty
	@Column(name = "random_selection")
    private Boolean randomSelection = null;
	@JsonProperty
	@Column(name = "final_approval")
    private Boolean finalApproval = null;
	@JsonProperty
	@Column(name = "submitted")
    private Boolean submitted = null;
	@JsonProperty
	@Column(name = "modifiedon")
    private Date modifiedOn = null;
	@JsonProperty
	@Column(name = "createdon")
    private Date createdOn = null;
	@JsonProperty
	@Column(name = "createdby")
    private Integer createdBy = null;
	@JsonProperty
	@Column(name = "modifiedby")
    private Integer modifiedBy = null;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Integer getOptedId() {
		return optedId;
	}
	public void setOptedId(Integer optedId) {
		this.optedId = optedId;
	}
	public String getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public String getVerificationStatus() {
		return verificationStatus;
	}
	public void setVerificationStatus(String verificationStatus) {
		this.verificationStatus = verificationStatus;
	}
	public String getFirstApprovalStatus() {
		return firstApprovalStatus;
	}
	public void setFirstApprovalStatus(String firstApprovalStatus) {
		this.firstApprovalStatus = firstApprovalStatus;
	}
	public Boolean getRandomSelection() {
		return randomSelection;
	}
	public void setRandomSelection(Boolean randomSelection) {
		this.randomSelection = randomSelection;
	}
	public Boolean getFinalApproval() {
		return finalApproval;
	}
	public void setFinalApproval(Boolean finalApproval) {
		this.finalApproval = finalApproval;
	}
	public Boolean getSubmitted() {
		return submitted;
	}
	public void setSubmitted(Boolean submitted) {
		this.submitted = submitted;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	// code 
	
	

}
