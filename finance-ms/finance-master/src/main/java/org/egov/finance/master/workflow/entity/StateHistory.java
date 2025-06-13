package org.egov.finance.master.workflow.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Immutable;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

@Entity
@Immutable
@Table(name = "eg_wf_state_history")
@SequenceGenerator(name = StateHistory.SEQ_STATEHISTORY, sequenceName = StateHistory.SEQ_STATEHISTORY, allocationSize = 1)
public class StateHistory implements Serializable {
	static final String SEQ_STATEHISTORY = "SEQ_EG_WF_STATE_HISTORY";
	private static final long serialVersionUID = -2286621991905578107L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_STATEHISTORY)
	private Long id;

	// @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdBy")
	private long createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	// @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lastModifiedBy")
	private long lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	@ManyToOne(targetEntity = State.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "state_id")
	private State state;

	@NotNull
	private String value;

	// @ManyToOne(targetEntity = OwnerGroup.class, fetch = FetchType.LAZY)
	@Column(name = "OWNER_POS")
	private Long ownerPosition;

	@Column(name = "OWNER_USER")
	private Long ownerUser;

	private String senderName;
	private String nextAction;
	private String comments;
	private String natureOfTask;
	private String extraInfo;
	private Date dateInfo;
	private Date extraDateInfo;

	// @ManyToOne(targetEntity = OwnerGroup.class, fetch = FetchType.LAZY)
	@Column(name = "INITIATOR_POS")
	private Long initiatorPosition;
}
