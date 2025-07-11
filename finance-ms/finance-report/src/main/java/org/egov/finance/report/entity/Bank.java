package org.egov.finance.report.entity;

import java.util.HashSet;
import java.util.Set;

import org.egov.finance.report.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "bank")
@SequenceGenerator(name = Bank.SEQ_BANK, sequenceName = Bank.SEQ_BANK, allocationSize = 1)

@Data
public class Bank extends AuditDetailswithVersion {

	public static final String SEQ_BANK = "SEQ_BANK";

	@Id
	@GeneratedValue(generator = SEQ_BANK, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@Length(max = 50, min = 2)
	@SafeHtml
	private String code;

	@NotNull
	@Length(max = 100)
	@SafeHtml
	private String name;

	@Length(max = 250)
	@SafeHtml
	private String narration;

	@NotNull
	private Boolean isactive;

	@Length(max = 50)
	@SafeHtml
	private String type;

	@OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Bankbranch> bankbranchs = new HashSet<>();

}
