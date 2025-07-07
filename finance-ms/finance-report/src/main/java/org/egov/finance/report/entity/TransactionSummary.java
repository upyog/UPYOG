/**
 * @author bpattanayak
 * @date 1 Jul 2025
 */

package org.egov.finance.report.entity;

import java.math.BigDecimal;

import org.egov.finance.report.customannotation.SafeHtml;
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
@Table(name = "TRANSACTIONSUMMARY")
@SequenceGenerator(name = TransactionSummary.SEQ_TRANSACTIONSUMMARY, sequenceName = TransactionSummary.SEQ_TRANSACTIONSUMMARY, allocationSize = 1)
@Data
public class TransactionSummary extends AuditDetailswithVersion{
	
	private static final long serialVersionUID = 7967417290025689100L;

	public static final String SEQ_TRANSACTIONSUMMARY = "SEQ_TRANSACTIONSUMMARY";

    @Id
    @GeneratedValue(generator = SEQ_TRANSACTIONSUMMARY, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ACCOUNTDETAILTYPEID")
    private AccountDetailType accountdetailtype;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "FINANCIALYEARID")
    private FinancialYear financialyear;

    @ManyToOne
    @JoinColumn(name = "FUNDSOURCEID")
    private Fundsource fundsource;

    @ManyToOne
    @JoinColumn(name = "FUNDID")
    private Fund fund;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "GLCODEID")
    private CChartOfAccounts glcodeid;

    @Transient
    @SafeHtml
    private String glcodeDetail;

    @NotNull
    @Min(1)
    private BigDecimal openingdebitbalance;

    @NotNull
    @Min(1)
    private BigDecimal openingcreditbalance;

    private Integer accountdetailkey;

    @Length(max = 300)
    @SafeHtml
    private String narration;
    @SafeHtml
    private String departmentCode;

    @ManyToOne
    @JoinColumn(name = "FUNCTIONARYID")
    private Functionary functionaryid;

    @ManyToOne
    @JoinColumn(name = "FUNCTIONID")
    private Function functionid;

    private Integer divisionid;

}
