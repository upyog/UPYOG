package org.egov.finance.inbox.entity;

import java.io.Serializable;

import org.egov.finance.inbox.customannotation.SafeHtml;

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
public class EgwStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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