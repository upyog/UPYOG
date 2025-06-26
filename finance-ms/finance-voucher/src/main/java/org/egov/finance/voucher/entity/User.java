package org.egov.finance.voucher.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.egov.finance.voucher.customannotation.CompositeUnique;
import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.enumeration.Gender;
import org.egov.finance.voucher.enumeration.UserType;
import org.egov.finance.voucher.util.Constants;
import org.egov.finance.voucher.validation.Unique;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractAuditable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "eg_user")
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@SequenceGenerator(name = User.SEQ_USER, sequenceName = User.SEQ_USER, allocationSize = 1)
@Unique(fields = { "username", "pan", "aadhaarNumber", "emailId" }, enableDfltMsg = true, isSuperclass = true)
@CompositeUnique(fields = { "type", "mobileNumber" }, enableDfltMsg = true, message = "{user.exist.with.same.mobileno}")
@JsonIgnoreProperties({ "createdBy", "lastModifiedBy" })
public class User extends AuditDetailswithVersion {
	public static final String SEQ_USER = "SEQ_EG_USER";
	private static final long serialVersionUID = -2415368058955783970L;
	@Expose
	@Id
	@GeneratedValue(generator = SEQ_USER, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "username", unique = true)
	@NotNull
	@Length(min = 2, max = 64)
	private String username;

	@NotNull
	@Length(min = 4, max = 64)
	// @Audited
	private String password;

	private String salutation;

	@SafeHtml
	@Length(min = 2, max = 64)
	private String guardian;

	@SafeHtml
	@Length(min = 2, max = 64)
	private String guardianRelation;

	@NotNull
	@SafeHtml
	@Length(min = 2, max = 100)
	// @Audited
	private String name;

	@Enumerated(EnumType.ORDINAL)
	private Gender gender;

	@Pattern(regexp = Constants.MOBILE_NUM)
	@SafeHtml
	@Length(max = 15)
	// @Audited
	private String mobileNumber;

	@Email(regexp = Constants.EMAIL)
	@SafeHtml
	@Length(max = 128)
	// @Audited
	private String emailId;

	@SafeHtml
	private String altContactNumber;

	@SafeHtml
	@Length(max = 10)
	private String pan;

	@SafeHtml
	@Length(max = 20)
	private String aadhaarNumber;

	@OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<Address> address = new ArrayList<>();

	private boolean active;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "eg_userrole", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "roleid"))
	// @Audited(targetAuditMode = NOT_AUDITED)
	// @AuditJoinTable
	private Set<Role> roles = new HashSet<>();

	@Temporal(TemporalType.DATE)
	private Date dob;

	@NotNull
	private Date pwdExpiryDate = new Date();

	@NotNull
	private String locale = "en_IN";

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private UserType type;

	private byte[] signature;

	private boolean accountLocked;

	@Transient
	private String uuid;
	
	 @Override
	    public Long getId() {
	        return id;
	    }

	    @Override
	    public void setId(final Long id) {
	        this.id = id;
	    }


}
