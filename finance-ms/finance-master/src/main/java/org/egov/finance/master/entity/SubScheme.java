package org.egov.finance.master.entity;


import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



/**
 * SubScheme.java
 * 
 * @author bpattanayak
 * @date 10 Jun 2025
 * 
 */
@Entity
@Table(name = "sub_scheme",uniqueConstraints = @UniqueConstraint(columnNames = {"code","schemeid"}))
@SequenceGenerator(name = SubScheme.SEQ,sequenceName = SubScheme.SEQ,allocationSize = 1)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class SubScheme extends AuditDetailswithoutVersion{
	
	private static final long serialVersionUID = -1261345271879178855L;
	public static final String SEQ="seq_sub_scheme";
	
	@Id
	@GeneratedValue(generator = SubScheme.SEQ,strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull(message = "scheme can not be blank")
	@Column(name = "schemeid")
	private Long scheme;

	@NotBlank(message = "Code can not be blank")
	private String code;

	@NotBlank(message = "Name can not be blank")
	@Size(min = 3,max = 100,message = "Name should be between 100 words")
	private String name;

	@NotNull(message = "Validform can not be blank")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date validfrom;

	@NotNull(message = "Validto can not be blank")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date validto;

	private Boolean isactive;

	@Column(name = "department")
	private String department;
	
	@Column(name = "initial_estimate_amount")
	private BigDecimal initialEstimateAmount;
	@Column(name = "council_loan_proposal_number")
	private String councilLoanProposalNumber;
	@Column(name = "council_admin_sanction_number")
	private String councilAdminSanctionNumber;
	@Column(name = "govt_loan_proposal_number")
	private String govtLoanProposalNumber;
	@Column(name = "govt_admin_sanction_number")
	private String govtAdminSanctionNumber;
	@Column(name = "council_loan_proposal_date")
	private Date councilLoanProposalDate;
	@Column(name = "council_admin_sanction_date")
	private Date councilAdminSanctionDate;
	@Column(name = "govt_loan_proposal_date")
	private Date govtLoanProposalDate;
	@Column(name = "govt_admin_sanction_date")
	private Date govtAdminSanctionDate;

}
