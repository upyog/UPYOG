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

	public AccountDetail(final CGeneralLedger gl) {
		this.id = gl.getId();
		this.orderId = gl.getVoucherlineId() != null ? gl.getVoucherlineId().longValue() : null;
		this.glcode = gl.getGlcode();
		this.creditAmount = gl.getCreditAmount();
		this.debitAmount = gl.getDebitAmount();

		if (gl.getFunctionId() != null) {
			this.function = new Function(gl.getFunctionId().longValue());
		}

		if (gl.getGeneralLedgerDetails() != null) {
			for (final CGeneralLedgerDetail sub : gl.getGeneralLedgerDetails()) {
				SubledgerDetail subDetail = new SubledgerDetail(sub);
				subDetail.setAccountDetail(this); // set the back-reference
				this.subledgerDetails.add(subDetail);
			}
		}

	}
}
