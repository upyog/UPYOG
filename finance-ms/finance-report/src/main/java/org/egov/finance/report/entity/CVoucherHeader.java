package org.egov.finance.report.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "voucherheader")
@SequenceGenerator(name = CVoucherHeader.SEQ_VOUCHERHEADER, sequenceName = CVoucherHeader.SEQ_VOUCHERHEADER, allocationSize = 1)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CVoucherHeader extends StateAware implements Serializable {

	public static final String SEQ_VOUCHERHEADER = "SEQ_VOUCHERHEADER";
	private static final long serialVersionUID = -1950866465902911747L;

	@Id
	@GeneratedValue(generator = SEQ_VOUCHERHEADER, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@SafeHtml
	@Length(max = 50)
	private String name;

	@NotNull
	@SafeHtml
	@Length(max = 100)
	private String type;

	@SafeHtml
	private String description;

	@Temporal(TemporalType.DATE)
	private Date effectiveDate;

	@SafeHtml
	@Length(max = 30)
	@Column(updatable = false)
	private String voucherNumber;

	@Temporal(TemporalType.DATE)
	private Date voucherDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fundId")
	private Fund fundId;

	private Integer fiscalPeriodId;

	private Integer status;

	private Long originalvcId;

	private Integer isConfirmed;

	private Long refvhId;

	@SafeHtml
	@Length(max = 50)
	private String cgvn;

	private Integer moduleId;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "voucherHeaderId", targetEntity = CGeneralLedger.class)
	private Set<CGeneralLedger> generalLedger;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "voucherheaderid", targetEntity = Vouchermis.class)
	private Vouchermis vouchermis;

	// Transient fields
	@Transient
	private String voucherSubType;
	@Transient
	private Boolean isRestrictedtoOneFunctionCenter;
	@Transient
	private String voucherNumberPrefix;
	@Transient
	private List<CGeneralLedger> accountDetails = new ArrayList<>();
	@Transient
	private List<CGeneralLedgerDetail> subLedgerDetails = new ArrayList<>();
	@Transient
	private String partyName;
	@Transient
	private String partyBillNumber;
	@Transient
	private Date partyBillDate;
	@Transient
	private String billNumber;
	@Transient
	private String departmentName;
	@Transient
	private Date billDate;
	@Transient
	private Long approvalDepartment;
	@Transient
	private String approvalComent;
	@Transient
	private String voucherNumType;
	@Transient
	private String fiscalName;
	
	@Override
    public String getStateDetails() {
        return getState().getComments().isEmpty() ? voucherNumber : voucherNumber + "-" + getState().getComments();
    }
}
