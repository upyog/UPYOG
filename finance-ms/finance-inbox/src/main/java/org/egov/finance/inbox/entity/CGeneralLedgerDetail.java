package org.egov.finance.inbox.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "generalledgerdetail")
@SequenceGenerator(name = CGeneralLedgerDetail.SEQ_GENERALLEDGERDETAIL, sequenceName = CGeneralLedgerDetail.SEQ_GENERALLEDGERDETAIL, allocationSize = 1)
@Data
public class CGeneralLedgerDetail implements Serializable {

	public static final String SEQ_GENERALLEDGERDETAIL = "seq_generalledgerdetail";
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = SEQ_GENERALLEDGERDETAIL, strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "generalledgerid")
	private CGeneralLedger generalLedgerId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "detailtypeid")
	private AccountDetailType detailTypeId;

	@Column(name = "detailkeyid")
	private Integer detailKeyId;

	private BigDecimal amount;

	@Transient
	private String detailKeyName;
}
