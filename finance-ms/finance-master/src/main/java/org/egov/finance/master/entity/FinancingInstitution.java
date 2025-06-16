package org.egov.finance.master.entity;

import java.io.Serializable;
import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name="financial_institution")
@Entity
@SequenceGenerator(name = FinancialYear.SEQ, sequenceName = FinancialYear.SEQ, allocationSize = 1)
@Data
public class FinancingInstitution implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String SEQ = "SEQ_FINANCIAL_INSTITUTION";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@SafeHtml
	private String name;



}
