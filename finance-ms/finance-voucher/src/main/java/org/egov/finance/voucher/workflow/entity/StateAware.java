package org.egov.finance.voucher.workflow.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.egov.finance.voucher.entity.AuditDetailswithVersion;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.model.StateInfoBuilder;
import org.egov.finance.voucher.workflow.entity.State.StateStatus;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@MappedSuperclass
@Slf4j
@Data
public abstract class StateAware extends AuditDetailswithVersion {

    private static final long serialVersionUID = 5776408218810221246L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = State.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "STATE_ID")
    private State state;

    @Transient
    @JsonIgnore
    private Transition transition;

    /**
     * Need to overridden by the implementing class to give details about the State <I>Used by Inbox to fetch the State Detail at
     * runtime</I>
     *
     * @return String Detail
     */
    public abstract String getStateDetails();

    /**
     * To set the Group Link, Any State Aware Object which needs Grouping should override this method
     **/
 
    public String myLinkId() {
        return getId().toString();
    }

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    public State getState() {
        return state;
    }

    private void setState(State state) {
        this.state = state;
    }

    public final State getCurrentState() {
        return getState();
    }

    public final Long currentAssignee() {
        return getState().getOwnerPosition();
    }

    public final Long previousAssignee() {
        return getState().getPreviousOwner();
    }

    public final List<StateHistory> getStateHistory() {
        return state == null ? Collections.emptyList() : new LinkedList(getState().getHistory());
    }

    public final String getStateType() {
        return this.getClass().getSimpleName();
    }

    public <S> S extraInfoAs(Class<S> type) {
        return state.extraInfoAs(type);
    }

    public final boolean transitionInitialized() {
        return hasState() && getCurrentState().getId()==null;
    }

    public final boolean transitionCompleted() {
        return hasState() && getCurrentState().isEnded();
    }

    public final boolean transitionInprogress() {
        return hasState() && (transitionInitialized() || getCurrentState().isInprogress());
    }

    public final boolean hasState() {
        return getCurrentState() != null;
    }

    public final Transition transition() {
        if (this.transition == null)
            this.transition = new Transition();
        return this.transition;
    }

    public final void changeProcessOwner(Long position) {
        if (transitionInprogress())
            this.state.setOwnerPosition(position);
    }

    public final void changeProcessInitiator(Long position) {
        if (transitionInprogress())
            this.state.setInitiatorPosition(position);
    }

    protected StateInfoBuilder buildStateInfo() {
        return new StateInfoBuilder().task(this.getState().getNatureOfTask()).itemDetails(this.getStateDetails())
                .status(getCurrentState().getStatus().name()).refDate(this.getCreatedDate())
                .sender(this.getState().getSenderName()).senderPhoneno(this.getState().getExtraInfo());
    }

    public String getStateInfoJson() {
        return this.buildStateInfo().toJson();
    }

    public final class Transition implements Serializable {
        private static final long serialVersionUID = -6035435855091367838L;
        private boolean transitionInitiated;

        private void checkinTransition() {
            if (transitionInitiated)
                throw new ApplicationRuntimeException("Calling multiple transitions not supported");
            transitionInitiated = true;
        }

        private void checkTransitionStatus() {
            if (!transitionInitiated)
                throw new ApplicationRuntimeException("Use start|progress|end API before setting values");
        }

        public final Transition start() {
            checkinTransition();
            if (hasState())
                throw new ApplicationRuntimeException("Transition already started.");
            else {
                setState(new State());
                state.setType(getStateType());
                state.setStatus(StateStatus.STARTED);
                state.setValue(State.DEFAULT_STATE_VALUE_CREATED);
                state.setComments(State.DEFAULT_STATE_VALUE_CREATED);
            }
            return this;
        }

        public final Transition startNext() {
            if (state == null)
                throw new ApplicationRuntimeException("Transition never started, use start() instead");
            if (!transitionCompleted())
                throw new ApplicationRuntimeException(
                        "Transition can not be started, end the current transition first");
            State previousState = state;
            state = null;
            start();
            state.setPreviousStateRef(previousState);
            return this;
        }

        public final Transition progress() {
            Long previousOwner = state.getOwnerPosition();
            progressWithStateCopy();
            resetState();
            state.setPreviousOwner(previousOwner);
            return this;
        }

        public final Transition progressWithStateCopy() {
            checkinTransition();
            if (transitionCompleted())
                throw new ApplicationRuntimeException("Transition already ended");
            if (hasState()) {
                state.addStateHistory(new StateHistory(state));
                state.setPreviousOwner(state.getOwnerPosition());
                state.setStatus(StateStatus.INPROGRESS);
            }
            return this;
        }

        public final Transition end() {
            checkinTransition();
            if (transitionCompleted())
                throw new ApplicationRuntimeException("Transition already ended");
            else {
                state.addStateHistory(new StateHistory(state));
                state.setValue(State.DEFAULT_STATE_VALUE_CLOSED);
                state.setStatus(StateStatus.ENDED);
                state.setComments(State.DEFAULT_STATE_VALUE_CLOSED);
            }
            return this;
        }

        public final Transition reopen() {
            checkinTransition();
            if (transitionCompleted()) {
                state.addStateHistory(new StateHistory(state));
                state.setPreviousOwner(state.getOwnerPosition());
                state.setValue(State.STATE_REOPENED);
                state.setStatus(StateStatus.INPROGRESS);
            } else
                throw new ApplicationRuntimeException(
                        "Transition can not be reopened, end the current transition first");
            return this;
        }

        public final Transition withOwner(Long owner) {
            checkTransitionStatus();
            state.setOwnerPosition(owner);
            return this;
        }

        public final Transition withOwner(Object owner) {
            checkTransitionStatus();
            state.setOwnerPosition(this.getLongValue(owner, "getId"));
            return this;
        }

        public final Transition withInitiator(Object owner) {
            checkTransitionStatus();
            state.setInitiatorPosition(this.getLongValue(owner, "getId"));
            return this;
        }
        
        public final Transition withInitiator(Long owner) {
            checkTransitionStatus();
            state.setInitiatorPosition(owner);
            return this;
        }

        public final Transition withStateValue(String currentStateValue) {
            checkTransitionStatus();
            state.setValue(currentStateValue);
            return this;
        }

        public final Transition withNextAction(String nextAction) {
            checkTransitionStatus();
            state.setNextAction(nextAction);
            return this;
        }

        public final Transition withComments(String comments) {
            checkTransitionStatus();
            state.setComments(comments);
            return this;
        }

        public final Transition withNatureOfTask(String natureOfTask) {
            checkTransitionStatus();
            state.setNatureOfTask(natureOfTask);
            return this;
        }

        public final Transition withExtraInfo(String extraInfo) {
            checkTransitionStatus();
            state.setExtraInfo(extraInfo);
            return this;
        }

        public final Transition withExtraInfo(Object extraInfo) {
            checkTransitionStatus();
            state.setExtraInfo(new Gson().toJson(extraInfo));
            return this;
        }

        public final Transition withDateInfo(Date dateInfo) {
            checkTransitionStatus();
            state.setDateInfo(dateInfo);
            return this;
        }

        public final Transition withExtraDateInfo(Date extraDateInfo) {
            checkTransitionStatus();
            state.setExtraDateInfo(extraDateInfo);
            return this;
        }

        public final Transition withSenderName(String senderName) {
            checkTransitionStatus();
            state.setSenderName(senderName);
            return this;
        }

        public final Transition withCreatedBy(long id) {
            checkTransitionStatus();
            state.setCreatedBy(id);
            return this;
        }

        public final Transition withtLastModifiedBy(long id) {
            checkTransitionStatus();
            state.setLastModifiedBy(id);
            return this;
        }

        private void resetState() {
            state.setComments("");
            state.setDateInfo(null);
            state.setExtraDateInfo(null);
            state.setExtraInfo("");
            state.setNextAction("");
            state.setValue("");
            state.setSenderName("");
            state.setNatureOfTask("");
            state.setOwnerUser(null);
            state.setOwnerPosition(null);
            state.setInitiatorPosition(null);
        }

        private Long getLongValue(Object obj, String methodName) {

            Method[] methods = obj.getClass().getMethods();
            Object value = 0;
            for (Method method : methods) {
                try {
                    if (method.getName().equalsIgnoreCase(methodName)) {
                        value = method.invoke(obj);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("error  to get method= {}" + e);
                }
            }
            return (long) value;
        }

    }
    
    


}
