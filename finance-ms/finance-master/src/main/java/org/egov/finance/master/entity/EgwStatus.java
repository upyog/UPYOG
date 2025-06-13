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
@Table(name = "egw_status")
@SequenceGenerator(name = EgwStatus.SEQ, sequenceName = EgwStatus.SEQ, allocationSize = 1)
@Data
public class EgwStatus extends AuditDetailswithVersion {

	public static final String SEQ = "SEQ_EGW_STATUS";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@SafeHtml
	private String moduleType;

	@SafeHtml
	private String code;

	@SafeHtml
	private String description;
}