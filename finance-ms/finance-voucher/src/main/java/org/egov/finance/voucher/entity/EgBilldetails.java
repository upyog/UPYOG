package org.egov.finance.voucher.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "EG_BILLDETAILS")
@SequenceGenerator(name = EgBilldetails.SEQ_EG_BILLDETAILS, sequenceName = EgBilldetails.SEQ_EG_BILLDETAILS, allocationSize = 1)
@Data
public class EgBilldetails extends AuditDetailswithVersion implements Serializable{
	
	private static final long serialVersionUID = -6045669915919744421L;
    public static final String SEQ_EG_BILLDETAILS = "SEQ_EG_BILLDETAILS";

    @Id
    @GeneratedValue(generator = SEQ_EG_BILLDETAILS, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "billid")
    @NotNull
    private EgBillregister egBillregister;

    private BigDecimal functionid;
    @NotNull
    private BigDecimal glcodeid;
    @Min(1)
    private BigDecimal debitamount;
    @Min(1)
    private BigDecimal creditamount;

    private Date lastupdatedtime;

    @Length(max = 250)
    @SafeHtml
    private String narration;

    @Transient
    private CChartOfAccounts chartOfAccounts;
    
    @Transient
    private Function function;

    @OrderBy("id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "egBilldetailsId", targetEntity = EgBillPayeedetails.class)
    private Set<EgBillPayeedetails> egBillPaydetailes = new HashSet<EgBillPayeedetails>(0);

    public EgBilldetails() {
    }

    public EgBilldetails(final Long id, final EgBillregister egBillregister,
            final BigDecimal glcodeid, final Date lastupdatedtime) {
        this.id = id;
        this.egBillregister = egBillregister;
        this.glcodeid = glcodeid;
        this.lastupdatedtime = lastupdatedtime;
    }

    public EgBilldetails(final Long id, final EgBillregister egBillregister,
            final BigDecimal functionid, final BigDecimal glcodeid, final BigDecimal debitamount,
            final BigDecimal creditamount, final Date lastupdatedtime, final Set<EgBillPayeedetails> egBillPaydetailes,
            final String narration) {
        this.id = id;
        this.egBillregister = egBillregister;
        this.functionid = functionid;
        this.glcodeid = glcodeid;
        this.debitamount = debitamount;
        this.creditamount = creditamount;
        this.lastupdatedtime = lastupdatedtime;
        this.egBillPaydetailes = egBillPaydetailes;
        this.narration = narration;
    }

}
