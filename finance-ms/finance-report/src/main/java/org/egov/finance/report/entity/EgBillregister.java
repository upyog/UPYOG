
package org.egov.finance.report.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;



import org.egov.finance.report.customannotation.SafeHtml;
import org.egov.finance.report.workflow.entity.StateAware;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "EG_BILLREGISTER")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = EgBillregister.SEQ_EG_BILLREGISTER, sequenceName = EgBillregister.SEQ_EG_BILLREGISTER, allocationSize = 1)
public class EgBillregister extends StateAware implements java.io.Serializable {

    public static final String SEQ_EG_BILLREGISTER = "SEQ_EG_BILLREGISTER";
    private static final long serialVersionUID = -4312140421386028968L;
    @Id
    @GeneratedValue(generator = SEQ_EG_BILLREGISTER, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Length(min = 1,max=50)
    @SafeHtml
    @Column(updatable = false)
    private String billnumber;
    
    @NotNull
    private Date billdate;
    
    @NotNull
    @Min(1)
    private BigDecimal billamount;
    @Min(1)
    private BigDecimal fieldid;
    @SafeHtml
    @Length(max = 50)
    private String billstatus;
    @SafeHtml
    @Length(max = 1024)
    private String narration;
    @Min(1)
    private BigDecimal passedamount;
    @SafeHtml
    @Length(max = 50)
    private String billtype;
    @SafeHtml
    @Length(max = 20)
    private String expendituretype;
    private BigDecimal advanceadjusted;
    @SafeHtml
    @Length(max = 20)
    private String zone;
    @SafeHtml
    @Length(max = 50)
    private String division;
    @SafeHtml
    @Length(max = 50)
    @Column(updatable = false)
    private String workordernumber;
    @SafeHtml
    @Length(max = 50)
    private String billapprovalstatus;
    private Boolean isactive;
    private Date billpasseddate;
    private Date workorderdate;
    @ManyToOne
    @JoinColumn(name = "statusid")
    private EgwStatus status;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "egBillregister", targetEntity = EgBillregistermis.class)
    private EgBillregistermis egBillregistermis;
    @SafeHtml
    private String worksdetailId;
   // @Transient
   // private User approver;
    @Transient
    private Date approvedOn;
    @OrderBy("id")
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "egBillregister", targetEntity = EgBilldetails.class)
    private Set<EgBilldetails> egBilldetailes = new LinkedHashSet<>();
    @Transient
    private List<EgBilldetails> billDetails = new ArrayList<>();
    @Transient
    private List<EgBilldetails> debitDetails = new ArrayList<>();
    @Transient
    private List<EgBilldetails> creditDetails = new ArrayList<>();
    @Transient
    private List<EgBilldetails> netPayableDetails = new ArrayList<>();
    @Transient
    private List<EgBillPayeedetails> billPayeedetails = new ArrayList<>();
    @Transient
    private List<EgChecklists> checkLists = new ArrayList<>();
    @Transient
    private List<DocumentUpload> documentDetail = new ArrayList<>();
    @Transient
    @SafeHtml
    private String approvalDepartment;
    @Transient
    @SafeHtml
    private String approvalComent;

    public EgBillregister() {
    }

    public EgBillregister(final String billnumber, final Date billdate, final BigDecimal billamount,
                          final String billstatus, final String expendituretype, final BigDecimal createdby, final Date createddate) {
        this.billnumber = billnumber;
        this.billdate = billdate;
        this.billamount = billamount;
        this.billstatus = billstatus;
        this.expendituretype = expendituretype;
    }

    public EgBillregister(final String billnumber, final Date billdate, final BigDecimal billamount,
                          final BigDecimal fieldid, final String billstatus, final String narration, final BigDecimal passedamount,
                          final String billtype, final String expendituretype, final BigDecimal advanceadjusted,
                          final BigDecimal createdby, final Date createddate, final BigDecimal lastmodifiedby,
                          final Date lastmodifieddate, final String zone, final String division, final String workordernumber,
                          final String billapprovalstatus, final Boolean isactive, final Date billpasseddate,
                          final Date workorderdate, final EgBillregistermis egBillregistermis,
                          final Set<EgBilldetails> egBilldetailes, final EgwStatus status) {
        this.billnumber = billnumber;
        this.billdate = billdate;
        this.billamount = billamount;
        this.fieldid = fieldid;
        this.billstatus = billstatus;
        this.narration = narration;
        this.passedamount = passedamount;
        this.billtype = billtype;
        this.expendituretype = expendituretype;
        this.advanceadjusted = advanceadjusted;
        this.zone = zone;
        this.division = division;
        this.workordernumber = workordernumber;
        this.billapprovalstatus = billapprovalstatus;
        this.isactive = isactive;
        this.billpasseddate = billpasseddate;
        this.workorderdate = workorderdate;
        this.egBillregistermis = egBillregistermis;
        this.egBilldetailes = egBilldetailes;
        this.status = status;
    }

    /**
     * @return the worksdetail
     */
    public String getWorksdetailId() {
        return worksdetailId;
    }

    /**
     * @param worksdetail the worksdetail to set
     */
    public void setWorksdetailId(final String worksdetail) {
        worksdetailId = worksdetail;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getBillnumber() {
        return billnumber;
    }

    public void setBillnumber(final String billnumber) {
        this.billnumber = billnumber;
    }

    public Date getBilldate() {
        return billdate;
    }

    public void setBilldate(final Date billdate) {
        this.billdate = billdate;
    }

    public BigDecimal getBillamount() {
        return billamount;
    }

    public void setBillamount(final BigDecimal billamount) {
        this.billamount = billamount;
    }

    public BigDecimal getFieldid() {
        return fieldid;
    }

    public void setFieldid(final BigDecimal fieldid) {
        this.fieldid = fieldid;
    }

    public String getBillstatus() {
        return billstatus;
    }

    public void setBillstatus(final String billstatus) {
        this.billstatus = billstatus;
    }

    @Length(max = 1024, message = "Max 1024 characters are allowed for narration")
    public String getNarration() {
        return narration;
    }

    public void setNarration(final String narration) {
        this.narration = narration;
    }

    public BigDecimal getPassedamount() {
        return passedamount;
    }

    public void setPassedamount(final BigDecimal passedamount) {
        this.passedamount = passedamount;
    }

    public String getBilltype() {
        return billtype;
    }

    public void setBilltype(final String billtype) {
        this.billtype = billtype;
    }

    public String getExpendituretype() {
        return expendituretype;
    }

    public void setExpendituretype(final String expendituretype) {
        this.expendituretype = expendituretype;
    }

    public BigDecimal getAdvanceadjusted() {
        return advanceadjusted;
    }

    public void setAdvanceadjusted(final BigDecimal advanceadjusted) {
        this.advanceadjusted = advanceadjusted;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(final String zone) {
        this.zone = zone;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(final String division) {
        this.division = division;
    }

    public String getWorkordernumber() {
        return workordernumber;
    }

    public void setWorkordernumber(final String workordernumber) {
        this.workordernumber = workordernumber;
    }

    public String getBillapprovalstatus() {
        return billapprovalstatus;
    }

    public void setBillapprovalstatus(final String billapprovalstatus) {
        this.billapprovalstatus = billapprovalstatus;
    }

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(final Boolean isactive) {
        this.isactive = isactive;
    }

    public Date getBillpasseddate() {
        return billpasseddate;
    }

    public void setBillpasseddate(final Date billpasseddate) {
        this.billpasseddate = billpasseddate;
    }

    public Date getWorkorderdate() {
        return workorderdate;
    }

    public void setWorkorderdate(final Date workorderdate) {
        this.workorderdate = workorderdate;
    }

    public EgBillregistermis getEgBillregistermis() {
        return egBillregistermis;
    }

    public void setEgBillregistermis(final EgBillregistermis egBillregistermis) {
        this.egBillregistermis = egBillregistermis;
    }

    public Set<EgBilldetails> getEgBilldetailes() {
        return egBilldetailes;
    }

    public void setEgBilldetailes(final Set<EgBilldetails> egBilldetailes) {
        this.egBilldetailes = egBilldetailes;
    }

    public void addEgBilldetailes(final EgBilldetails egBilldetail) {
        getEgBilldetailes().add(egBilldetail);
    }

    public EgwStatus getStatus() {
        return status;
    }

    public void setStatus(final EgwStatus status) {
        this.status = status;
    }

    @Override
    public String getStateDetails() {
        return getState().getComments().isEmpty() ? billnumber : billnumber + "-" + getState().getComments();
    }

	/*
	 * public User getApprover() { return approver; }
	 * 
	 * public void setApprover(final User approver) { this.approver = approver; }
	 */

    public Date getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(final Date approvedOn) {
        this.approvedOn = approvedOn;
    }

    public void removeEgBilldetailes(final EgBilldetails egBilldetail) {
        if (egBilldetail != null)
            getEgBilldetailes().remove(egBilldetail);
    }

    public List<EgBilldetails> getBillDetails() {
        return billDetails;
    }

    public void setBillDetails(final List<EgBilldetails> billDetails) {
        this.billDetails = billDetails;
    }

    public List<EgBilldetails> getDebitDetails() {
        return debitDetails;
    }

    public void setDebitDetails(final List<EgBilldetails> debitDetails) {
        this.debitDetails = debitDetails;
    }

    public List<EgBilldetails> getCreditDetails() {
        return creditDetails;
    }

    public void setCreditDetails(final List<EgBilldetails> creditDetails) {
        this.creditDetails = creditDetails;
    }

    public List<EgBilldetails> getNetPayableDetails() {
        return netPayableDetails;
    }

    public void setNetPayableDetails(final List<EgBilldetails> netPayableDetails) {
        this.netPayableDetails = netPayableDetails;
    }

    public List<EgBillPayeedetails> getBillPayeedetails() {
        return billPayeedetails;
    }

    public void setBillPayeedetails(final List<EgBillPayeedetails> billPayeedetails) {
        this.billPayeedetails = billPayeedetails;
    }

    public List<EgChecklists> getCheckLists() {
        return checkLists;
    }

    public void setCheckLists(final List<EgChecklists> checkLists) {
        this.checkLists = checkLists;
    }

    public String getApprovalDepartment() {
        return approvalDepartment;
    }

    public void setApprovalDepartment(final String approvalDepartment) {
        this.approvalDepartment = approvalDepartment;
    }

    public String getApprovalComent() {
        return approvalComent;
    }

    public void setApprovalComent(final String approvalComent) {
        this.approvalComent = approvalComent;
    }

    public List<DocumentUpload> getDocumentDetail() {
        return documentDetail;
    }

    public void setDocumentDetail(List<DocumentUpload> documentDetail) {
        this.documentDetail.clear();
        if (documentDetail != null)
            this.documentDetail.addAll(documentDetail);
    }

    public enum BillStatus {
        CREATED, APPROVED, REJECTED, CANCELLED
    }
}
