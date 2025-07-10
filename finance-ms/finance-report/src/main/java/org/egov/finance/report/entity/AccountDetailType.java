package org.egov.finance.report.entity;

import org.egov.finance.report.customannotation.SafeHtml;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accountdetailtype")
@SequenceGenerator(name = AccountDetailType.SEQ, sequenceName = AccountDetailType.SEQ, allocationSize = 1)
@Data
@AllArgsConstructor
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

    @SafeHtml
    @Column(name="name")
    private String name;

    @SafeHtml
    private String description;

    @SafeHtml
    @Column(name="tablename")
    private String tableName;

    @SafeHtml
    @Column(name="full_qualified_name")
    private String fullyQualifiedName;
    
    @Column(name="isactive")
    private Boolean active;
    
    public AccountDetailType(Long id) {
        this.id = id;
    }
}
