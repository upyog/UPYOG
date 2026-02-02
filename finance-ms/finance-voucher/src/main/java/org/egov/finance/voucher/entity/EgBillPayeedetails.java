package org.egov.finance.voucher.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

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
@Table(name = "EG_BILLPAYEEDETAILS")
@SequenceGenerator(name = EgBillPayeedetails.SEQ_EG_BILLPAYEEDETAILS, sequenceName = EgBillPayeedetails.SEQ_EG_BILLPAYEEDETAILS, allocationSize = 1)
@Data
public class EgBillPayeedetails extends AuditDetailswithVersion implements Serializable {
	
	
	 private static final long serialVersionUID = -6620941691239597456L;

	    public static final String SEQ_EG_BILLPAYEEDETAILS = "SEQ_EG_BILLPAYEEDETAILS";

	    @Id
	    @GeneratedValue(generator = SEQ_EG_BILLPAYEEDETAILS, strategy = GenerationType.SEQUENCE)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "billdetailid")
	    @NotNull
	    private EgBilldetails egBilldetailsId;

	    @NotNull
	    private Integer accountDetailTypeId;

	    @NotNull
	    private Integer accountDetailKeyId;
	    @Min(1)
	    private BigDecimal debitAmount;
	    @Min(1)
	    private BigDecimal creditAmount;

	    private Date lastUpdatedTime;

	    @Transient
	    @SafeHtml
	    private String detailTypeName;

	    @Transient
	    @SafeHtml
	    private String detailKeyName;

	    @Transient
	    private Boolean isDebit;

	    @ManyToOne
	    @JoinColumn(name = "tdsid")
	    private Recovery recovery;

	    @Length(max = 250)
	    @SafeHtml
	    private String narration;

}
