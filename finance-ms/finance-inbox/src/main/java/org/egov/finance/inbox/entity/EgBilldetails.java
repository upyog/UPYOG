
package org.egov.finance.inbox.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.egov.finance.inbox.customannotation.SafeHtml;
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

@Entity
@Table(name = "EG_BILLDETAILS")
@SequenceGenerator(name = EgBilldetails.SEQ_EG_BILLDETAILS, sequenceName = EgBilldetails.SEQ_EG_BILLDETAILS, allocationSize = 1)
public class EgBilldetails  implements java.io.Serializable {

    private static final long serialVersionUID = -6045669915919744421L;
    public static final String SEQ_EG_BILLDETAILS = "SEQ_EG_BILLDETAILS";

    @Id
    @GeneratedValue(generator = SEQ_EG_BILLDETAILS, strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "billid")
    @NotNull
    private EgBillregister egBillregister;

    private Long functionid;
    @NotNull
    private Long glcodeid;
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

   // @OrderBy("id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "egBilldetailsId")
    private Set<EgBillPayeedetails> egBillPaydetailes = new HashSet<EgBillPayeedetails>(0);

    public EgBilldetails() {
    }

    public EgBilldetails(final Integer id, final EgBillregister egBillregister,
            final Long glcodeid, final Date lastupdatedtime) {
        this.id = id;
        this.egBillregister = egBillregister;
        this.glcodeid = glcodeid;
        this.lastupdatedtime = lastupdatedtime;
    }

    public EgBilldetails(final Integer id, final EgBillregister egBillregister,
            final Long functionid, final Long glcodeid, final BigDecimal debitamount,
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

	/*
	 * @Override public Integer getId() { return id; }
	 * 
	 * @Override public void setId(final Integer id) { this.id = id; }
	 */

    public EgBillregister getEgBillregister() {
        return egBillregister;
    }

    public void setEgBillregister(final EgBillregister egBillregister) {
        this.egBillregister = egBillregister;
    }

    public Long getFunctionid() {
        return functionid;
    }

    public void setFunctionid(final Long functionid) {
        this.functionid = functionid;
    }

    public Long getGlcodeid() {
        return glcodeid;
    }

    public void setGlcodeid(final Long glcodeid) {
        this.glcodeid = glcodeid;
    }

    public BigDecimal getDebitamount() {
        return debitamount;
    }

    public void setDebitamount(final BigDecimal debitamount) {
        this.debitamount = debitamount;
    }

    public BigDecimal getCreditamount() {
        return creditamount;
    }

    public void setCreditamount(final BigDecimal creditamount) {
        this.creditamount = creditamount;
    }

    public Date getLastupdatedtime() {
        return lastupdatedtime;
    }

    public void setLastupdatedtime(final Date lastupdatedtime) {
        this.lastupdatedtime = lastupdatedtime;
    }

    public Set<EgBillPayeedetails> getEgBillPaydetailes() {
        return egBillPaydetailes;
    }

    public void setEgBillPaydetailes(final Set<EgBillPayeedetails> egBillPaydetailes) {
        this.egBillPaydetailes = egBillPaydetailes;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(final String narration) {
        this.narration = narration;
    }

    public void addEgBillPayeedetail(final EgBillPayeedetails egbillpayee) {
        if (egbillpayee != null)
            getEgBillPaydetailes().add(egbillpayee);
    }

    public void removeEgBillPayeedetail(final EgBillPayeedetails egbillpayee) {
        if (egbillpayee != null)
            getEgBillPaydetailes().remove(egbillpayee);
    }

    public CChartOfAccounts getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(final CChartOfAccounts chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

}
