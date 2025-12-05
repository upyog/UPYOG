
package org.egov.finance.inbox.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.inbox.customannotation.SafeHtml;
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

@Entity
@Table(name = "EG_BILLPAYEEDETAILS")
@SequenceGenerator(name = EgBillPayeedetails.SEQ_EG_BILLPAYEEDETAILS, sequenceName = EgBillPayeedetails.SEQ_EG_BILLPAYEEDETAILS, allocationSize = 1)
public class EgBillPayeedetails extends AuditDetailswithoutVersion {

    private static final long serialVersionUID = -6620941691239597456L;

    public static final String SEQ_EG_BILLPAYEEDETAILS = "SEQ_EG_BILLPAYEEDETAILS";

    @Id
    @GeneratedValue(generator = SEQ_EG_BILLPAYEEDETAILS, strategy = GenerationType.SEQUENCE)
    private Integer id;

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

    public Integer getAccountDetailKeyId() {
        return accountDetailKeyId;
    }

    public void setAccountDetailKeyId(final Integer accountDetailKeyId) {
        this.accountDetailKeyId = accountDetailKeyId;
    }

    public EgBilldetails getEgBilldetailsId() {
        return egBilldetailsId;
    }

    public void setEgBilldetailsId(final EgBilldetails egBilldetailsId) {
        this.egBilldetailsId = egBilldetailsId;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(final BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(final BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

	
	/*
	 * @Override public Integer getId() { return id; }
	 * 
	 * @Override public void setId(final Integer id) { this.id = id; }
	 */
	 

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(final Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Integer getAccountDetailTypeId() {
        return accountDetailTypeId;
    }

    public void setAccountDetailTypeId(final Integer accountDetailTypeId) {
        this.accountDetailTypeId = accountDetailTypeId;
    }

    public Recovery getRecovery() {
        return recovery;
    }

    public void setRecovery(final Recovery recovery) {
        this.recovery = recovery;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(final String narration) {
        this.narration = narration;
    }

    public String getDetailTypeName() {
        return detailTypeName;
    }

    public void setDetailTypeName(final String detailTypeName) {
        this.detailTypeName = detailTypeName;
    }

    public String getDetailKeyName() {
        return detailKeyName;
    }

    public void setDetailKeyName(final String detailKeyName) {
        this.detailKeyName = detailKeyName;
    }

    public Boolean getIsDebit() {
        return isDebit;
    }

    public void setIsDebit(final Boolean isDebit) {
        this.isDebit = isDebit;
    }

}
