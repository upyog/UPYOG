package org.egov.finance.master.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.finance.master.customannotation.SafeHtml;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "voucher")
@SequenceGenerator(name = Voucher.SEQ, sequenceName = Voucher.SEQ, allocationSize = 1)
@Data
public class Voucher extends AuditDetailswithVersion {

	public static final String SEQ = "SEQ_Voucher";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@SafeHtml
	private String name;

	@SafeHtml
	private String type;

	@SafeHtml
	private String voucherNumber;

	@SafeHtml
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date voucherDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fundid")
	private Fund fund;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "functionid")
	private Function function;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fiscalperiodid")
	private FiscalPeriod fiscalPeriod;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "statusid")
	private EgwStatus status;

	private Long originalVhId;
	private Long refVhId;

	@SafeHtml
	private String cgvn;

	private Long moduleId;

	@SafeHtml
	private String department;

	@SafeHtml
	private String source;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schemeid")
	private Scheme scheme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subschemeid")
	private SubScheme subScheme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionaryid")
    private Functionary functionary;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fundsourceid")
   private Fundsource fundsource;

	@OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AccountDetail> ledgers = new ArrayList<>();

	@SafeHtml
	private String tenantId;

	@SafeHtml
	private String serviceName;

	@SafeHtml
	private String referenceDocument;
}
