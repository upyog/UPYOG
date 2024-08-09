package digit.web.models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "eg_user")
public class BmcUser {

    @Id
	@Column(name = "id")
	private Long id;
	@Column(name = "title")
    private String title;

    @Column(name = "salutation")
    private String salutation;

    @Column(name = "dob")
    private Timestamp dob;

    @Column(name = "locale")
    private String locale;

    
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "pwdexpirydate")
    private Timestamp pwdExpiryDate;

    @Column(name = "mobilenumber")
    private String mobileNumber;

    @Column(name = "altcontactnumber")
    private String altContactNumber;

    @Column(name = "emailid")
    private String emailId;

    @Column(name = "createddate")
    private Timestamp createdDate;

    @Column(name = "lastmodifieddate")
    private Timestamp lastModifiedDate;

    @Column(name = "createdby")
    private Long createdBy;

    @Column(name = "lastmodifiedby")
    private Long lastModifiedBy;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private Short gender;

    @Column(name = "pan")
    private String pan;

    @Column(name = "aadhaarnumber")
    private String aadhaarNumber;

    @Column(name = "type")
    private String type;

    @Column(name = "version")
    private Double version;

    @Column(name = "guardian")
    private String guardian;

    @Column(name = "guardianrelation")
    private String guardianRelation;

    @Column(name = "signature")
    private String signature;

    @Column(name = "accountlocked")
    private Boolean accountLocked;

    @Column(name = "bloodgroup")
    private String bloodGroup;

    @Column(name = "photo")
    private String photo;

    @Column(name = "identificationmark")
    private String identificationMark;

    @Column(name = "tenantid")
    private String tenantId;

   
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "accountlockeddate")
    private Long accountLockedDate;

}
