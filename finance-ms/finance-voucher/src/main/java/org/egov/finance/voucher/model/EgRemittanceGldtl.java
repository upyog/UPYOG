package org.egov.finance.voucher.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.voucher.entity.Recovery;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "eg_remittance_gldtl")
@Data
public class EgRemittanceGldtl implements Serializable {

	private static final long serialVersionUID = -226329871221883883L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "generalledgerdetail_id") // match the foreign key column
	private org.egov.finance.voucher.entity.CGeneralLedgerDetail generalledgerdetail;

	private BigDecimal gldtlamt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastmodifieddate;

	private BigDecimal remittedamt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recovery_id") // match the foreign key column
	private Recovery recovery;

	public EgRemittanceGldtl(final org.egov.finance.voucher.entity.CGeneralLedgerDetail generalledgerdetail,
			final BigDecimal gldtlamt, final Date lastmodifieddate, final BigDecimal remittedamt,
			final Recovery recovery) {
		this.generalledgerdetail = generalledgerdetail;
		this.gldtlamt = gldtlamt;
		this.lastmodifieddate = lastmodifieddate;
		this.remittedamt = remittedamt;
		this.recovery = recovery;
	}

	public EgRemittanceGldtl() {
		// default constructor
	}
}
