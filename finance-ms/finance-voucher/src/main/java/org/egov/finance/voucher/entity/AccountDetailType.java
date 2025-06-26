package org.egov.finance.voucher.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accountdetailtype")
@SequenceGenerator(name = AccountDetailType.SEQ, sequenceName = AccountDetailType.SEQ, allocationSize = 1)
@Data
@Getter
@Setter
@NoArgsConstructor
public class AccountDetailType extends AuditDetailswithVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ = "SEQ_AccountDetailType";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(nullable = false, unique = true)
	@Length(max = 50)
	@SafeHtml
	@NotNull
	private String name;

	@NotNull
	@Length(max = 50)
	@SafeHtml
	private String description;

	@Length(max = 25)
	@SafeHtml
	private String tablename;

	@Length(max = 25)
	@SafeHtml
	private String columnname;

	@Column(nullable = false, unique = true)
	@Length(max = 50)
	@SafeHtml
	private String attributename;

	@NotNull
	private BigDecimal nbroflevels = BigDecimal.ZERO;

	private Boolean isactive;

	@Column(name = "FULL_QUALIFIED_NAME")
	@Length(max = 250)
	private String fullQualifiedName;

	private Date createdDate;

	private Date lastModifiedDate;

	private Long lastModifiedBy;
	
	

	public AccountDetailType(Long id) {
		this.id = id;
	}
}
