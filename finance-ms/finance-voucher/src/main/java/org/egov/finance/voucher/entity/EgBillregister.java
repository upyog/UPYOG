package org.egov.finance.voucher.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.workflow.entity.StateAware;
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
import lombok.Data;

@Entity
@Table(name = "EG_BILLREGISTER")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = EgBillregister.SEQ_EG_BILLREGISTER, sequenceName = EgBillregister.SEQ_EG_BILLREGISTER, allocationSize = 1)
@Data
public class EgBillregister extends StateAware implements java.io.Serializable {

	public static final String SEQ_EG_BILLREGISTER = "SEQ_EG_BILLREGISTER";
	private static final long serialVersionUID = -4312140421386028968L;
	@Id
	@GeneratedValue(generator = SEQ_EG_BILLREGISTER, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Length(min = 1, max = 50)
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
	@Transient
	private User approver;
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
}
