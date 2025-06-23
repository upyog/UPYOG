package org.egov.finance.inbox.workflow.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.egov.finance.inbox.customannotation.SafeHtml;
import org.egov.finance.inbox.entity.AuditDetailswithVersion;
import org.egov.finance.inbox.model.StateModel.StateStatus;
import org.hibernate.validator.constraints.Length;

import com.google.gson.Gson;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EG_WF_STATES")
@SequenceGenerator(name = State.SEQ_STATE, sequenceName = State.SEQ_STATE, allocationSize = 1)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class State extends AuditDetailswithVersion {

    public static final String SEQ_STATE = "SEQ_EG_WF_STATES"; // Moved up and made public

    public static final String DEFAULT_STATE_VALUE_CREATED = "Created";
    public static final String DEFAULT_STATE_VALUE_CLOSED = "Closed";
    public static final String STATE_REOPENED = "Reopened";

    private static final long serialVersionUID = -9159043292636575746L;

    @Id
    @GeneratedValue(generator = SEQ_STATE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @Length(max = 255)
    @SafeHtml
    private String type;

    @NotBlank
    @Length(max = 255)
    @SafeHtml
    private String value;

    @Column(name = "OWNER_POS")
    private Long ownerPosition;

    @Column(name = "OWNER_USER")
    private Long ownerUser;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER, mappedBy = "state", targetEntity = StateHistory.class)
    @OrderBy("id")
    private Set<StateHistory> history = new HashSet<>();

    @Length(max = 100)
    @SafeHtml
    private String senderName;

    @Length(max = 255)
    @SafeHtml
    private String nextAction;

    @Length(max = 1024)
    @SafeHtml
    private String comments;

    @Length(max = 100)
    @SafeHtml
    private String natureOfTask;

    @Length(max = 1024)
    @SafeHtml
    private String extraInfo;

    private Date dateInfo;
    private Date extraDateInfo;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private StateStatus status;

    @Column(name = "INITIATOR_POS")
    private Long initiatorPosition;

    @Column(name = "previousOwner")
    private Long previousOwner;

    @ManyToOne(targetEntity = State.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "previousStateRef")
    private State previousStateRef;

    @Transient
    private String deptCode;
    @Transient
    private String deptName;
    @Transient
    private String desgCode;
    @Transient
    private String desgName;
    
    protected void addStateHistory(final StateHistory history) {
		getHistory().add(history);
	}
    
    public enum StateStatus {
		STARTED, INPROGRESS, ENDED
	}
    
    public boolean isInprogress() {
		return status.equals(StateStatus.INPROGRESS);
	}

	public boolean isEnded() {
		return status.equals(StateStatus.ENDED);
	}
	
	public <S> S extraInfoAs(Class<S> type) {
		return new Gson().fromJson(getExtraInfo(), type);
		
	}
}