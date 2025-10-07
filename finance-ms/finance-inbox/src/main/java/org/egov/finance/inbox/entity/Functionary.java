package org.egov.finance.inbox.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "functionary")
@Data
@SequenceGenerator(name = "seq_functionary", sequenceName = "seq_functionary", allocationSize = 1)
public class Functionary extends AuditDetailswithVersion {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "seq_functionary", strategy = GenerationType.SEQUENCE)
	private Integer id;

	private BigDecimal code;

	private String name;

	private Date createtimestamp;

	private Date updatetimestamp;

	private Boolean isactive;
}
