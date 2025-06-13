package org.egov.finance.master.entity;

import java.util.HashSet;
import java.util.Set;

import org.egov.finance.master.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractAuditable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "chartofaccounts")
@SequenceGenerator(name = CChartOfAccounts.SEQ_CHARTOFACCOUNTS, sequenceName = CChartOfAccounts.SEQ_CHARTOFACCOUNTS, allocationSize = 1)

@Data
public class CChartOfAccounts extends AuditDetailswithVersion {

	public static final String SEQ_CHARTOFACCOUNTS = "SEQ_CHARTOFACCOUNTS";

	@Id
	@GeneratedValue(generator = SEQ_CHARTOFACCOUNTS, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@Length(max = 50)
	@SafeHtml
	private String glcode;

	@NotNull
	@Length(max = 150)
	@SafeHtml
	private String name;

	private Long purposeId;

	@SafeHtml
	@Column(name = "description")
	private String desc;

	private Boolean isActiveForPosting;

	private Long parentId;

	@Column(name = "scheduleid")
	private Long schedule;

	private Character operation;

	@NotNull
	private Character type;

	private Long classification;

	private Boolean functionReqd;

	private Boolean budgetCheckReq;

	@SafeHtml
	@Length(max = 255)
	private String majorCode;

	@Column(name = "class")
	private Long myClass;

	@Transient
	private Boolean isSubLedger;

	@JsonIgnore
	@OneToMany(mappedBy = "glCodeId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable
	private Set<CChartOfAccountDetail> chartOfAccountDetails = new HashSet<>();
}
