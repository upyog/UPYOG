package org.egov.finance.report.entity;

import java.util.HashSet;
import java.util.Set;

import org.egov.finance.report.customannotation.SafeHtml;
import org.egov.finance.report.util.CommonsConstants;
import org.hibernate.validator.constraints.Length;
import org.springframework.core.Constants;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "bankbranch", uniqueConstraints = @UniqueConstraint(columnNames = { "micr" }))
@SequenceGenerator(name = Bankbranch.SEQ_BANKBRANCH, sequenceName = Bankbranch.SEQ_BANKBRANCH, allocationSize = 1)
@Data
public class Bankbranch extends AuditDetailswithVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ_BANKBRANCH = "SEQ_BANKBRANCH";

	@Id
	@GeneratedValue(generator = SEQ_BANKBRANCH, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "bankid", nullable = false)
	private Bank bank;

	@NotNull
	@Length(max = 50)
	@SafeHtml
	private String branchcode;

	@NotNull
	@Length(max = 50)
	@SafeHtml
	private String branchname;

	@NotNull
	@Length(max = 50)
	@SafeHtml
	private String branchaddress1;

	@Length(max = 50)
	@SafeHtml
	private String branchaddress2;

	@Length(max = 50)
	@SafeHtml
	private String branchcity;

	@Length(max = 50)
	@SafeHtml
	private String branchstate;

	@Length(max = 50)
	@SafeHtml
	// @OptionalPattern(regex = CommonsConstants.alphaNumericwithoutspecialchar,
	// message = "Special Characters are not allowed in EPF No")
	private String branchpin;

	@Length(max = 15)
	@SafeHtml
	// @OptionalPattern(regex = Constants.MOBILE_NUM, message = "Please enter valid
	// mobile number")
	private String branchphone;

	@Length(max = 15)
	@SafeHtml
	private String branchfax;

	@Length(max = 50)
	@SafeHtml
	private String contactperson;

	@NotNull
	private Boolean isactive;

	@Length(max = 250)
	@SafeHtml
	private String narration;

	@Length(max = 50)
	@SafeHtml
	@Column(name = "micr")
	private String branchMICR;

	@JsonIgnore
	@OneToMany(mappedBy = "bankbranch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Bankaccount> bankaccounts = new HashSet<>();
}