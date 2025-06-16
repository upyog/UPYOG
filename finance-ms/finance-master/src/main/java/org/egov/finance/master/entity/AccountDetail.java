package org.egov.finance.master.entity;

import java.util.ArrayList;
import java.util.List;

import org.egov.finance.master.customannotation.SafeHtml;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "accountentitymaster")
@SequenceGenerator(name = AccountDetail.SEQ, sequenceName = AccountDetail.SEQ, allocationSize = 1)
@Data
public class AccountDetail extends AuditDetailswithVersion {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ = "seq_account_detail";

    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long orderId;

    @SafeHtml
    private String glcode;

    private Double debitAmount;
    private Double creditAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id")
    private Function function;

    @OneToMany(mappedBy = "accountDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubledgerDetail> subledgerDetails = new ArrayList<>();
    
    
    
}
