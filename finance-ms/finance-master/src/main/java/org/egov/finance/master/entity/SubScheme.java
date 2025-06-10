package org.egov.finance.master.entity;


import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * SubScheme.java
 * 
 * @author bpattanayak
 * @date 10 Jun 2025
 * @version 1.0
 */
@Entity
@Table(name = "sub_scheme")
@SequenceGenerator(name = SubScheme.SEQ,sequenceName = SubScheme.SEQ,allocationSize = 1)
@Data
@EqualsAndHashCode(callSuper = false)
public class SubScheme extends AuditDetails{
	
	private static final long serialVersionUID = -1261345271879178855L;
	private static final String SEQ="seq_sub_scheme";
	
	@Id
	@GeneratedValue(generator = SubScheme.SEQ,strategy = GenerationType.SEQUENCE)
	private Long id;

	//private Scheme scheme;

	@NotBlank(message = "Code can not be blank")
	private String code;

	@NotBlank(message = "Name can not be blank")
	private String name;

	@NotBlank(message = "Validform can not be blank")
	private Date validfrom;

	@NotBlank(message = "Validto can not be blank")
	private Date validto;

	private Boolean isactive;

	private String department;
	private BigDecimal initialEstimateAmount;
	private String councilLoanProposalNumber;
	private String councilAdminSanctionNumber;
	private String govtLoanProposalNumber;
	private String govtAdminSanctionNumber;
	private Date councilLoanProposalDate;
	private Date councilAdminSanctionDate;
	private Date govtLoanProposalDate;
	private Date govtAdminSanctionDate;

}
