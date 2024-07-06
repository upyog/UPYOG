package digit.bmc.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "aadhar_user")
public class AadharUser {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long  id;
	@Column(name = "aadharRef")
    private String aadharRef;
	@Column(name = "uuid")
    private String uuid ;
	@Column(name = "aadhar_fatherName")
     private String aadhar_fatherName;
	@Column(name = "aadhar_name")
     private String aadhar_name;
	@Column(name = "aadhar_dob")
     private Date aadhar_dob;
	@Column(name = "aadhar_mobile")
     private String aadhar_mobile;
	@Column(name = "createdOn")
    private Date createdOn;
	@Column(name = "modifiedOn")
   private Date modifiedOn;
	@Column(name = "createdBy")
    private Integer createdBy;
	@Column(name = "modifiedBy")
   private Integer modifiedBy;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAadharRef() {
		return aadharRef;
	}
	public void setAadharRef(String aadharRef) {
		this.aadharRef = aadharRef;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getAadhar_fatherName() {
		return aadhar_fatherName;
	}
	public void setAadhar_fatherName(String aadhar_fatherName) {
		this.aadhar_fatherName = aadhar_fatherName;
	}
	public String getAadhar_name() {
		return aadhar_name;
	}
	public void setAadhar_name(String aadhar_name) {
		this.aadhar_name = aadhar_name;
	}
	public Date getAadhar_dob() {
		return aadhar_dob;
	}
	public void setAadhar_dob(Date aadhar_dob) {
		this.aadhar_dob = aadhar_dob;
	}
	public String getAadhar_mobile() {
		return aadhar_mobile;
	}
	public void setAadhar_mobile(String aadhar_mobile) {
		this.aadhar_mobile = aadhar_mobile;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
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
	@Override
	public String toString() {
		return "AadharUser [id=" + id + ", aadharRef=" + aadharRef + ", uuid=" + uuid + ", aadhar_fatherName="
				+ aadhar_fatherName + ", aadhar_name=" + aadhar_name + ", aadhar_dob=" + aadhar_dob + ", aadhar_mobile="
				+ aadhar_mobile + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + "]";
	}
	
	// code 
	
	

}
