

package org.egov.finance.inbox.model;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.egov.finance.inbox.util.ApplicationThreadLocals;
import org.egov.finance.inbox.util.CommonUtils;
import org.egov.finance.inbox.util.DateUtils;
import org.egov.finance.inbox.workflow.entity.State;
import org.egov.finance.inbox.workflow.entity.StateAware;
import org.egov.finance.inbox.workflow.entity.StateHistory;
import org.egov.finance.inbox.workflow.entity.WorkflowType;
import org.springframework.util.ObjectUtils;

import lombok.Builder;


public class InboxModel {
    private String id;
    private String sender;
    private String date;
    private String task;
    private String status;
    private String details;
    private String link;
    private String moduleName;
    private Date createdDate;
    private boolean draft;

    public InboxModel() {
        //Default constructor for external inbox integration
    }

	
    private InboxModel(StateAware stateAware, WorkflowType workflowTypes, String nextAction) {
        State state = stateAware.getCurrentState();
        this.id = workflowTypes.isGrouped() ? "" : new StringBuilder(5).append(state.getId()).append("#")
                .append(workflowTypes.getId()).toString();
        this.date = DateUtils.toDefaultDateTimeFormat(state.getCreatedDate());
        this.sender = state.getSenderName();
        
        this.task = StringUtils.defaultIfBlank(state.getNatureOfTask(), workflowTypes.getDisplayName());
        this.status = state.getValue() + (ObjectUtils.isEmpty(nextAction) ? "" : " - " + nextAction);
        this.details = StringUtils.defaultIfBlank(stateAware.getStateDetails(), "");
        this.link = workflowTypes.getLink().replace(":ID", stateAware.myLinkId());
        this.moduleName = workflowTypes.getModule().getDisplayName();
        this.createdDate = state.getCreatedDate();
        this.draft = state.isNew() && (state.getCreatedBy()==ApplicationThreadLocals.getUserId());
    }

    private InboxModel(StateHistory stateHistory, WorkflowType workflowTypes) {
        this.id = stateHistory.getState().getId().toString();
        this.date = DateUtils.toDefaultDateTimeFormat(stateHistory.getLastModifiedDate());
        this.sender = stateHistory.getSenderName();
        this.task = StringUtils.defaultIfBlank(stateHistory.getNatureOfTask(), workflowTypes.getDisplayName());
        this.status = stateHistory.getValue()
                + (ObjectUtils.isEmpty(stateHistory.getNextAction()) ? "" : " - " + stateHistory.getNextAction());
        this.details = ObjectUtils.isEmpty(stateHistory.getComments()) ? "" : CommonUtils.removeSpecialCharacters (stateHistory.getComments());
        this.link ="" ;
    }

    public static InboxModel build(StateAware stateAware, WorkflowType workflowType, String nextAction) {
        return new InboxModel(stateAware, workflowType, nextAction);
    }

    public static InboxModel buildHistory(StateHistory stateHistory, WorkflowType workflowType) {
        return new InboxModel(stateHistory, workflowType);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(final String task) {
        this.task = task;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(final String details) {
        this.details = details;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }

    public int getElapsed() {
        return DateUtils.daysBetween(createdDate, new Date());
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(final boolean draft) {
        this.draft = draft;
    }
}
