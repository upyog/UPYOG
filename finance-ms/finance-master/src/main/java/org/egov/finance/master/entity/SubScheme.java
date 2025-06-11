package org.egov.finance.master.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sub_scheme")
@SequenceGenerator(name = SubScheme.SEQ, sequenceName = SubScheme.SEQ, allocationSize = 1)
@Data
public class SubScheme extends AuditDetails {

	public static final String SEQ = "SEQ_SubScheme";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scheme_id")
	private Scheme scheme;

	@Length(max = 50, min = 2)
	@SafeHtml
	private String code;

	@Length(max = 100, min = 2)
	@SafeHtml
	private String name;

	private Date validfrom;
	private Date validto;
	private Boolean isactive;
	private Date lastmodifieddate;
	private String department;
	private BigDecimal initialEstimateAmount;
	private String councilLoanProposalNumber;
	private String councilAdminSanctionNumber;
	private String govtLoanProposalNumber;
	private String govtAdminSanctionNumber;
	private Date councilLoanProposalDate;
	private Date councilAdminSanctionDate;
	private Date govtLoanProposalDate;
	private Date govtAdminSanctionDate;

}
