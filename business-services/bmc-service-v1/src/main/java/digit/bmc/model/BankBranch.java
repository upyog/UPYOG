package digit.bmc.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Data
@Table(name="eg_bmc_bankbranch")
public class BankBranch {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branchcode")
    private String branchcode;

    @Column(name = "branchname")
    private String branchname;

    @Column(name = "branchaddress1")
    private String branchaddress1;

    @Column(name = "branchaddress2")
    private String branchaddress2;

    @Column(name = "branchcity")
    private String branchcity;

    @Column(name = "branchstate")
    private String branchstate;

    @Column(name = "branchpin")
    private String branchpin;

    @Column(name = "branchphone")
    private String branchphone;
    @Column(name = "ifsc")
	private String ifsc;

    @Column(name = "branchfax")
    private String branchfax;
    @Column(name = "bankid")
    private Long bankid;

    @Column(name = "contactperson")
    private String contactperson;

    @Column(name = "isactive")
    private boolean isactive;

    @Column(name = "narration")
    private String narration;

    @Column(name = "micr")
    private String micr;

    @Column(name = "createddate")
    private Timestamp createddate;

    @Column(name = "lastmodifieddate")
    private Timestamp lastmodifieddate;
    @Column(name = "lastmodifiedby")
    private Long lastmodifiedby;

    @Column(name = "version")
    private Long version;
    @Column(name = "createdby")
    private Long createdby;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBranchcode() {
		return branchcode;
	}
	public void setBranchcode(String branchcode) {
		this.branchcode = branchcode;
	}
	public String getBranchname() {
		return branchname;
	}
	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}
	public String getBranchaddress1() {
		return branchaddress1;
	}
	public void setBranchaddress1(String branchaddress1) {
		this.branchaddress1 = branchaddress1;
	}
	public String getBranchaddress2() {
		return branchaddress2;
	}
	public void setBranchaddress2(String branchaddress2) {
		this.branchaddress2 = branchaddress2;
	}
	public String getBranchcity() {
		return branchcity;
	}
	public void setBranchcity(String branchcity) {
		this.branchcity = branchcity;
	}
	public String getBranchstate() {
		return branchstate;
	}
	public void setBranchstate(String branchstate) {
		this.branchstate = branchstate;
	}
	public String getBranchpin() {
		return branchpin;
	}
	public void setBranchpin(String branchpin) {
		this.branchpin = branchpin;
	}
	public String getBranchphone() {
		return branchphone;
	}
	public void setBranchphone(String branchphone) {
		this.branchphone = branchphone;
	}
	public String getBranchfax() {
		return branchfax;
	}
	public void setBranchfax(String branchfax) {
		this.branchfax = branchfax;
	}
	public Long getBankid() {
		return bankid;
	}
	public void setBankid(Long bankid) {
		this.bankid = bankid;
	}
	public String getContactperson() {
		return contactperson;
	}
	public void setContactperson(String contactperson) {
		this.contactperson = contactperson;
	}
	public boolean isIsactive() {
		return isactive;
	}
	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getMicr() {
		return micr;
	}
	public void setMicr(String micr) {
		this.micr = micr;
	}
	public Timestamp getCreateddate() {
		return createddate;
	}
	public void setCreateddate(Timestamp createddate) {
		this.createddate = createddate;
	}
	public Timestamp getLastmodifieddate() {
		return lastmodifieddate;
	}
	public void setLastmodifieddate(Timestamp lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}
	public Long getLastmodifiedby() {
		return lastmodifiedby;
	}
	public void setLastmodifiedby(Long lastmodifiedby) {
		this.lastmodifiedby = lastmodifiedby;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Long getCreatedby() {
		return createdby;
	}
	public void setCreatedby(Long createdby) {
		this.createdby = createdby;
	}
	@Override
	public String toString() {
		return "BankBranch [id=" + id + ", branchcode=" + branchcode + ", branchname=" + branchname
				+ ", branchaddress1=" + branchaddress1 + ", branchaddress2=" + branchaddress2 + ", branchcity="
				+ branchcity + ", branchstate=" + branchstate + ", branchpin=" + branchpin + ", branchphone="
				+ branchphone + ", branchfax=" + branchfax + ", bankid=" + bankid + ", contactperson=" + contactperson
				+ ", isactive=" + isactive + ", narration=" + narration + ", micr=" + micr + ", createddate="
				+ createddate + ", lastmodifieddate=" + lastmodifieddate + ", lastmodifiedby=" + lastmodifiedby
				+ ", version=" + version + ", createdby=" + createdby + "]";
	}
   
    // code
    
    

}
