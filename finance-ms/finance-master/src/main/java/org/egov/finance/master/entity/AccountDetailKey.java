package org.egov.finance.master.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "accountdetailkey")
@Data
public class AccountDetailKey extends AuditDetailswithVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_accountdetailkey")
	@SequenceGenerator(name = "seq_accountdetailkey", sequenceName = "seq_accountdetailkey", allocationSize = 1)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "accountdetailtypeid")
	private AccountDetailType accountDetailType;
	
	 private Integer groupid;
	 private String detailname;
	 private Integer detailkey;

	private Long key;

	public AccountDetailKey(Long id) {
		this.id = id;
	}
}