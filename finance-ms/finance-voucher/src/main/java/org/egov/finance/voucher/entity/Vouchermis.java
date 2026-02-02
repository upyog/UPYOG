package org.egov.finance.voucher.entity;

import java.io.Serializable;
import java.util.Optional;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "VOUCHERMIS")
@Data
@SequenceGenerator(name = "SEQ_VOUCHERMIS", sequenceName = "SEQ_VOUCHERMIS", allocationSize = 1)
public class Vouchermis implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "SEQ_VOUCHERMIS", strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fundsourceid")
	private Fundsource fundsource;

	private Integer billnumber;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Boundary.class)
	@JoinColumn(name = "divisionid")
	private Boundary divisionid;

	private String departmentcode;

	@Transient
	private String departmentName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schemeid")
	private Scheme schemeid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subschemeid")
	private SubScheme subschemeid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "functionaryid")
	private Functionary functionary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voucherheaderid")
	private CVoucherHeader voucherheaderid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "functionid")
	private Function function;

	private String sourcePath;

	@Column(name = "budgetary_appnumber")
	private String budgetaryAppnumber;

	private Boolean budgetCheckReq = true;

	@Length(max = 50)
	@Column(name = "referencedocument")
	private String referenceDocument;

	@Length(max = 100)
	@Column(name = "servicename")
	private String serviceName;
}
