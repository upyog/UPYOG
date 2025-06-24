package org.egov.finance.voucher.entity;

import java.util.HashSet;
import java.util.Set;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.util.Constants;
import org.egov.finance.voucher.validation.Unique;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "eg_designation")
@Unique(fields = { "name", "code" }, enableDfltMsg = true)

@NamedQuery(name = "getDesignationForListOfDesgNames", query = "from Designation where trim(upper(name)) in(:param_0)")
public class Designation extends AuditDetailswithVersion {

	public static final String SEQ_DESIGNATION = "SEQ_EG_DESIGNATION";
	private static final long serialVersionUID = -3775503109625394145L;
	@SequenceGenerator(name = SEQ_DESIGNATION, sequenceName = SEQ_DESIGNATION, allocationSize = 1)
	@Id
	@GeneratedValue(generator = SEQ_DESIGNATION, strategy = GenerationType.SEQUENCE)
	private Long id;
	@NotBlank
	@SafeHtml
	// @Pattern(regexp = Constants.ALLTYPESOFALPHABETS_WITHMIXEDCHAR, message =
	// "Name should contain letters with space and (-,_)")
	private String name;
	@NotBlank
	@SafeHtml
	private String code;
	@SafeHtml
	private String description;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chartofaccounts")
	private CChartOfAccounts chartOfAccounts;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "egeis_desig_rolemapping", joinColumns = @JoinColumn(name = "designationid"), inverseJoinColumns = @JoinColumn(name = "roleid"))
	private Set<Role> roles = new HashSet<>();

}
