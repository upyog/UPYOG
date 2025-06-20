package org.egov.finance.master.entity;

import java.io.Serializable;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "eg_surrendered_cheques")
@SequenceGenerator(name = EgSurrenderedCheques.SEQ_EGSURRENDEREDCHEQUES, sequenceName = EgSurrenderedCheques.SEQ_EGSURRENDEREDCHEQUES, allocationSize = 1)
@Data
public class EgSurrenderedCheques extends AuditDetailswithVersion{

	private static final long serialVersionUID = 1L;
	public static final String SEQ_EGSURRENDEREDCHEQUES = "seq_egsurrenderedcheques";

	@Id
	@GeneratedValue(generator = SEQ_EGSURRENDEREDCHEQUES, strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bankaccountid", nullable = false)
	private Bankaccount bankaccount;

	@NotNull
	@SafeHtml
	@Length(max = 50)
	private String chequenumber;

	@Temporal(TemporalType.DATE)
	private Date chequedate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voucherheaderid")
	private CVoucherHeader voucherheader;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastmodifieddate;
}
