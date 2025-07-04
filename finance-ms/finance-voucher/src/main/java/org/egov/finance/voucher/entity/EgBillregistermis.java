package org.egov.finance.voucher.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "EG_BILLREGISTERMIS")
@SequenceGenerator(name = EgBillregistermis.SEQ_EG_BILLREGISTERMIS, sequenceName = EgBillregistermis.SEQ_EG_BILLREGISTERMIS, allocationSize = 1)
@Data
public class EgBillregistermis extends AuditDetailswithVersion implements Serializable {
	
	 private static final long serialVersionUID = -4947159761135531623L;

	    public static final String SEQ_EG_BILLREGISTERMIS = "SEQ_EG_BILLREGISTERMIS";

	    @Id
	    @GeneratedValue(generator = SEQ_EG_BILLREGISTERMIS, strategy = GenerationType.SEQUENCE)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "billid")
	    @NotNull
	    private EgBillregister egBillregister;
	    @Min(1)
	    private BigDecimal segmentid;
	    @Min(1)
	    private BigDecimal subsegmentid;

	    private Date paybydate;

	    @ManyToOne
	    @JoinColumn(name = "fieldid")
	    private Boundary fieldid;

	    private BigDecimal subfieldid;

	    @ManyToOne
	    @JoinColumn(name = "functionaryid")
	    private Functionary functionaryid;

	    @Length(max = 30)
	    @SafeHtml
	    private String sanctionedby;

	    private Date sanctiondate;

	    @Length(max = 200)
	    @SafeHtml
	    private String sanctiondetail;

	    @Length(max = 1024)
	    @SafeHtml
	    private String narration;

	    private Date lastupdatedtime;

	    @Length(max = 30)
	    @SafeHtml
	    private String disbursementtype;
	    @Min(1)
	    private BigDecimal escalation;
	    @Min(1)
	    private BigDecimal advancepayments;
	    @Min(1)
	    private BigDecimal securedadvances;
	    @Min(1)
	    private BigDecimal deductamountwitheld;
	    @Min(1)
	    private BigDecimal month;
	    @SafeHtml
	    @Column(updatable = false)
	    private String departmentcode;

	    @Transient
	    @SafeHtml
	    private String departmentName;

	    @ManyToOne
	    @JoinColumn(name = "financialyearid")
	    private FinancialYear financialyear;

	    @ManyToOne
	    @JoinColumn(name = "fundsourceid")
	    private Fundsource fundsource;

	    @ManyToOne
	    @JoinColumn(name = "fundid")
	    private Fund fund;

	    @ManyToOne
	    @JoinColumn(name = "billsubtype")
	    private EgBillSubType egBillSubType;

	    @Length(max = 350)
	    @SafeHtml
	    private String payto;
	    @SafeHtml
	    @Length(max = 200)
	    private String mbRefNo;

	    @ManyToOne
	    @JoinColumn(name = "functionid")
	    private Function function;

	    @ManyToOne
	    @JoinColumn(name = "schemeid")
	    private Scheme scheme;

	    @ManyToOne
	    @JoinColumn(name = "subschemeid")
	    private SubScheme subScheme;

	    @ManyToOne
	    @JoinColumn(name = "voucherheaderid")
	    private CVoucherHeader voucherHeader;

	    @Length(max = 150)
	    @SafeHtml
	    private String sourcePath;

	    @Length(max = 50)
	    @SafeHtml
	    private String partyBillNumber;

	    private Date partyBillDate;

	    @Length(max = 50)
	    @SafeHtml
	    private String inwardSerialNumber;

	    @Length(max = 30)
	    @SafeHtml
	    @Column(name = "budgetary_appnumber")
	    private String budgetaryAppnumber;

	    @Transient
	    private Long schemeId;

	    @Transient
	    private Long subSchemeId;

	    private Boolean budgetCheckReq = true;
	    
	    
	    public EgBillregistermis(final Long id, final EgBillregister egBillregister,
	            final Date lastupdatedtime, final Date paybydate) {
	        this.id = id;
	        this.egBillregister = egBillregister;
	        this.lastupdatedtime = lastupdatedtime;
	        this.paybydate = paybydate;
	    }

	    public EgBillregistermis(final Long id, final EgBillregister egBillregister, final Function function,
	            final Fund fundid, final BigDecimal segmentid, final BigDecimal subsegmentid,
	            final Boundary fieldid, final BigDecimal subfieldid,
	            final Functionary functionaryid, final String sanctionedby, final Date sanctiondate,
	            final String sanctiondetail, final String narration, final Date lastupdatedtime,
	            final String disbursementtype, final BigDecimal escalation,
	            final BigDecimal advancepayments, final BigDecimal securedadvances,
	            final BigDecimal deductamountwitheld, final String departmentcode,
	            final BigDecimal month, final FinancialYear financialyear,
	            final Fundsource fundsource, final Date paybydate, final EgBillSubType egBillSubtype,
	            final String ptyBillNumber, final Date ptyBillDate, final String inwrdSlNumber) {
	        this.id = id;
	        this.egBillregister = egBillregister;
	        fund = fundid;
	        this.function = function;
	        this.segmentid = segmentid;
	        this.subsegmentid = subsegmentid;
	        this.fieldid = fieldid;
	        this.subfieldid = subfieldid;
	        this.functionaryid = functionaryid;
	        this.sanctionedby = sanctionedby;
	        this.sanctiondate = sanctiondate;
	        this.sanctiondetail = sanctiondetail;
	        this.narration = narration;
	        this.lastupdatedtime = lastupdatedtime;
	        this.disbursementtype = disbursementtype;
	        this.escalation = escalation;
	        this.advancepayments = advancepayments;
	        this.securedadvances = securedadvances;
	        this.deductamountwitheld = deductamountwitheld;
	        this.departmentcode = departmentcode;
	        this.month = month;
	        this.financialyear = financialyear;
	        this.fundsource = fundsource;
	        this.paybydate = paybydate;
	        egBillSubType = egBillSubtype;
	        partyBillNumber = ptyBillNumber;
	        partyBillDate = ptyBillDate;
	        inwardSerialNumber = inwrdSlNumber;
	    }


}
