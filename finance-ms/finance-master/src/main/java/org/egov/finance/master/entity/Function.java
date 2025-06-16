/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *
 */
package org.egov.finance.master.entity;

import org.egov.finance.master.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Function", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "code" }))
@Data
@SequenceGenerator(name = Function.SEQ, sequenceName = Function.SEQ, allocationSize = 1)
public class Function extends AuditDetailswithVersion {

	private static final long serialVersionUID = 1L;
	public static final String SEQ = "SEQ_FUNCTION";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Length(max = 100, min = 2)
	@SafeHtml
	private String name;

	@Length(max = 50, min = 2)
	@SafeHtml
	private String code;

	@Length(max = 50)
	@SafeHtml
	private String type;

	private Integer llevel;

	private Boolean isActive;

	private Boolean isNotLeaf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentId")
	private Function parentId;

	public Function(Function function) {
		if (function != null) {
			this.id = function.getId();
			this.code = function.getCode();
			this.name = function.getName();
			this.type = function.getType();
			this.llevel = function.getLlevel();
			this.isActive = function.getIsActive();
			this.isNotLeaf = function.getIsNotLeaf();
			this.parentId = function.getParentId();
		}
	}

	public Function(Long id) {
		this.id = id;
	}

}
