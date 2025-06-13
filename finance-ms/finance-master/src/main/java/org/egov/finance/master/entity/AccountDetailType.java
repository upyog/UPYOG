package org.egov.finance.master.entity;

import org.egov.finance.master.customannotation.SafeHtml;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "accountdetailtype")
@SequenceGenerator(name = AccountDetailType.SEQ, sequenceName = AccountDetailType.SEQ, allocationSize = 1)
@Data
public class AccountDetailType extends AuditDetailswithVersion {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ = "SEQ_AccountDetailType";

    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;

    @SafeHtml
    private String name;

    @SafeHtml
    private String description;

    @SafeHtml
    private String tableName;

    @SafeHtml
    private String fullyQualifiedName;

    private Boolean active;
}
