package org.egov.finance.inbox.workflow.entity;

import org.egov.finance.inbox.entity.AuditDetailswithVersion;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "EG_WF_ACTION")
@SequenceGenerator(name = WorkflowAction.SEQ_WF_ACTION, sequenceName = WorkflowAction.SEQ_WF_ACTION, allocationSize = 1)
@Data
public class WorkflowAction extends  AuditDetailswithVersion {

	public static final String SEQ_WF_ACTION = "SEQ_EG_WF_ACTION"; // Moved above & made public

	private static final long serialVersionUID = -7940804129929823917L;

	@Id
	@GeneratedValue(generator = SEQ_WF_ACTION, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@Length(min = 1, max = 255)
	private String name;

	@NotNull
	@Length(min = 1, max = 1024)
	private String description;

	@NotNull
	@Length(min = 1, max = 255)
	private String type;
}