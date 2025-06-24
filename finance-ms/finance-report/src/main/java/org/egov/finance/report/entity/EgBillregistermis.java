
package org.egov.finance.report.entity;

import java.math.BigDecimal;
import java.util.Date;


import org.egov.finance.report.customannotation.SafeHtml;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "EG_BILLREGISTERMIS")
@Setter
@Getter
@SequenceGenerator(name = EgBillregistermis.SEQ_EG_BILLREGISTERMIS, sequenceName = EgBillregistermis.SEQ_EG_BILLREGISTERMIS, allocationSize = 1)
public class EgBillregistermis implements java.io.Serializable {

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

    public Boolean isBudgetCheckReq() {
        return budgetCheckReq;
    }

    public void setBudgetCheckReq(final Boolean budgetCheckReq) {
        this.budgetCheckReq = budgetCheckReq;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(final String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * @return the mbRefNo
     */
    public String getMbRefNo() {
        return mbRefNo;
    }

    /**
     * @param mbRefNo the mbRefNo to set
     */
    public void setMbRefNo(final String mbRefNo) {
        this.mbRefNo = mbRefNo;
    }

    public EgBillregistermis() {
    }

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

    public BigDecimal getSegmentid() {
        return segmentid;
    }

    public void setSegmentid(final BigDecimal segmentid) {
        this.segmentid = segmentid;
    }

    public BigDecimal getSubsegmentid() {
        return subsegmentid;
    }

    public void setSubsegmentid(final BigDecimal subsegmentid) {
        this.subsegmentid = subsegmentid;
    }

    public Boundary getFieldid() {
        return fieldid;
    }

    public void setFieldid(final Boundary fieldid) {
        this.fieldid = fieldid;
    }

    public BigDecimal getSubfieldid() {
        return subfieldid;
    }

    public void setSubfieldid(final BigDecimal subfieldid) {
        this.subfieldid = subfieldid;
    }

    public Functionary getFunctionaryid() {
        return functionaryid;
    }

    public void setFunctionaryid(final Functionary functionaryid) {
        this.functionaryid = functionaryid;
    }

    public String getSanctionedby() {
        return sanctionedby;
    }

    public void setSanctionedby(final String sanctionedby) {
        this.sanctionedby = sanctionedby;
    }

    public Date getSanctiondate() {
        return sanctiondate;
    }

    public void setSanctiondate(final Date sanctiondate) {
        this.sanctiondate = sanctiondate;
    }

    public String getSanctiondetail() {
        return sanctiondetail;
    }

    public void setSanctiondetail(final String sanctiondetail) {
        this.sanctiondetail = sanctiondetail;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(final String narration) {
        this.narration = narration;
    }

    public Date getLastupdatedtime() {
        return lastupdatedtime;
    }

    public void setLastupdatedtime(final Date lastupdatedtime) {
        this.lastupdatedtime = lastupdatedtime;
    }

    public String getDisbursementtype() {
        return disbursementtype;
    }

    public void setDisbursementtype(final String disbursementtype) {
        this.disbursementtype = disbursementtype;
    }

    public BigDecimal getEscalation() {
        return escalation;
    }

    public void setEscalation(final BigDecimal escalation) {
        this.escalation = escalation;
    }

    public BigDecimal getAdvancepayments() {
        return advancepayments;
    }

    public void setAdvancepayments(final BigDecimal advancepayments) {
        this.advancepayments = advancepayments;
    }

    public BigDecimal getSecuredadvances() {
        return securedadvances;
    }

    public void setSecuredadvances(final BigDecimal securedadvances) {
        this.securedadvances = securedadvances;
    }

    public BigDecimal getDeductamountwitheld() {
        return deductamountwitheld;
    }

    public void setDeductamountwitheld(final BigDecimal deductamountwitheld) {
        this.deductamountwitheld = deductamountwitheld;
    }

    public BigDecimal getMonth() {
        return month;
    }

    public void setMonth(final BigDecimal month) {
        this.month = month;
    }

    public FinancialYear getFinancialyear() {
        return financialyear;
    }

    public void setFinancialyear(final FinancialYear financialyear) {
        this.financialyear = financialyear;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(final Fund fund) {
        this.fund = fund;
    }

    public Fundsource getFundsource() {
        return fundsource;
    }

    public void setFundsource(final Fundsource fundsource) {
        this.fundsource = fundsource;
    }

    public Date getPaybydate() {
        return paybydate;
    }

    public void setPaybydate(final Date paybydate) {
        this.paybydate = paybydate;
    }

    public String getPayto() {
        return payto;
    }

    public void setPayto(final String payto) {
        this.payto = payto;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(final Scheme scheme) {
        this.scheme = scheme;
    }

    public SubScheme getSubScheme() {
        return subScheme;
    }

    public void setSubScheme(final SubScheme subScheme) {
        this.subScheme = subScheme;
    }

    public CVoucherHeader getVoucherHeader() {
        return voucherHeader;
    }

    public void setVoucherHeader(final CVoucherHeader voucherHeader) {
        this.voucherHeader = voucherHeader;
    }

    public EgBillSubType getEgBillSubType() {
        return egBillSubType;
    }

    public void setEgBillSubType(final EgBillSubType egBillSubType) {
        this.egBillSubType = egBillSubType;
    }

    public String getPartyBillNumber() {
        return partyBillNumber;
    }

    public void setPartyBillNumber(final String partyBillNumber) {
        this.partyBillNumber = partyBillNumber;
    }

    public Date getPartyBillDate() {
        return partyBillDate;
    }

    public void setPartyBillDate(final Date partyBillDate) {
        this.partyBillDate = partyBillDate;
    }

    public String getInwardSerialNumber() {
        return inwardSerialNumber;
    }

    public void setInwardSerialNumber(final String inwardSerialNumber) {
        this.inwardSerialNumber = inwardSerialNumber;
    }

    public String getBudgetaryAppnumber() {
        return budgetaryAppnumber;
    }

    public void setBudgetaryAppnumber(final String budgetaryAppnumber) {
        this.budgetaryAppnumber = budgetaryAppnumber;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(final Function function) {
        this.function = function;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(final Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getSubSchemeId() {
        return subSchemeId;
    }

    public void setSubSchemeId(final Long subSchemeId) {
        this.subSchemeId = subSchemeId;
    }

    public String getDepartmentcode() {
        return departmentcode;
    }

    public void setDepartmentcode(String departmentcode) {
        this.departmentcode = departmentcode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

}
