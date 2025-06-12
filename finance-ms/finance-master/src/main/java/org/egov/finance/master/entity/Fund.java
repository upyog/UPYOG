/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.master.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "fund")
@SequenceGenerator(name = Fund.SEQ, sequenceName = Fund.SEQ, allocationSize = 1)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Fund  extends AuditDetailswithVersion{

	public static final String SEQ = "SEQ_Fund";
	private static final long serialVersionUID = 7977534010758407945L;
	@Id
	@GeneratedValue(generator = Fund.SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	private String name;

	private String code;
	private Character identifier;
	private BigDecimal llevel = BigDecimal.ONE;
	// @JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	private Fund parentId;
	private Boolean isnotleaf;
	private Boolean isactive;
}
