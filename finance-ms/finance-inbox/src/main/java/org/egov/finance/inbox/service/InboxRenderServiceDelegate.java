
package org.egov.finance.inbox.service;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.egov.finance.inbox.model.EmployeeInfo;
import org.egov.finance.inbox.model.InboxModel;
import org.egov.finance.inbox.util.ApplicationThreadLocals;
import org.egov.finance.inbox.util.MicroserviceUtils;
import org.egov.finance.inbox.workflow.entity.State;
import org.egov.finance.inbox.workflow.entity.StateAware;
import org.egov.finance.inbox.workflow.entity.StateHistory;
import org.egov.finance.inbox.workflow.entity.WorkflowAction;
import org.egov.finance.inbox.workflow.entity.WorkflowType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

@Service
@Transactional(readOnly = true)
public class InboxRenderServiceDelegate<T extends StateAware> {
    private static final String SUPPLIER_BILL = "Supplier Bill";
    private static final String EXPENSE_BILL = "Expense Bill";
    private static final String WORKS_BILL = "Works Bill";
    private static final Logger LOG = LoggerFactory.getLogger(InboxRenderServiceDelegate.class);
    private static final String INBOX_RENDER_SERVICE_SUFFIX = "%sInboxRenderService";
    private static final Map<String, WorkflowType> WORKFLOW_TYPE_CACHE = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StateService stateService;

//    @Autowired
//    @Qualifier("eisService")
//    private OwnerGroupService<? extends OwnerGroup> ownerGroupService;

    @Autowired
    private WorkflowTypeService workflowTypeService;

    @Autowired
    private WorkflowActionService workflowActionService;

    @Autowired
    private MicroserviceUtils microserviceUtils;

    @ReadOnlyProperty
    public List<InboxModel> getCurrentUserInboxItems() {
        return buildInbox(getAssignedWorkflowItems())
                .parallelStream()
                .filter(item -> !item.isDraft())
                .collect(Collectors.toList());
    }
    @ReadOnlyProperty
    public List<InboxModel> getCurrentUserInboxItems(String token) {
        return buildInbox(getAssignedWorkflowItems())
                .parallelStream()
                .filter(item -> !item.isDraft())
                .collect(Collectors.toList());
    }
    @ReadOnlyProperty
    public List<InboxModel> getCurrentUserDraftItems() {
        return buildInbox(getAssignedWorkflowDrafts());
    }

    @ReadOnlyProperty
    public List<InboxModel> getWorkflowHistoryItems(Long stateId) {
        List<InboxModel> inboxHistoryItems = new LinkedList<>();
        for (StateHistory stateHistory : getStateHistory(stateId)) {
            inboxHistoryItems.add(InboxModel
                    .buildHistory(stateHistory, getWorkflowType(stateHistory.getState().getType())));
        }
        return inboxHistoryItems;
    }

    @ReadOnlyProperty
    public List<T> getAssignedWorkflowItems() {
        return getAssignedWorkflowItems(false);
    }

    @ReadOnlyProperty
    public List<T> getAssignedWorkflowDrafts() {
        return getAssignedWorkflowItems(true);
    }

    @ReadOnlyProperty
    public List<StateHistory> getStateHistory(Long stateId) {
        return new LinkedList<>(stateService.getStateById(stateId).getHistory());
    }

    private List<T> getAssignedWorkflowItems(boolean draft) {
        List<T> workflowItems = new ArrayList<>();
        List<Long> owners = currentUserPositionIds();
//        List<Long> owners = new ArrayList<>();
//        owners.add(4L);
//        owners.add(1L);
        if (!owners.isEmpty()) {
            List<String> types = stateService.getAssignedWorkflowTypeNames(owners);
            for (String type : types) {
                Optional<InboxRenderService<T>> inboxRenderService = this.getInboxRenderService(type);
                if (inboxRenderService.isPresent()) {
                    InboxRenderService<T> renderService = inboxRenderService.get();
                    workflowItems.addAll(draft ? renderService.getDraftWorkflowItems(owners) :
                            renderService.getAssignedWorkflowItems(owners));
                }
            }
        }
        return workflowItems;
    }

    private List<InboxModel> buildInbox(List<T> items) {
        List<InboxModel> inboxItems = new ArrayList<>();
        for (StateAware stateAware : items) {
            WorkflowType workflowType = getWorkflowType(stateAware.getStateType());
            if(WORKS_BILL.equals(stateAware.getCurrentState().getNatureOfTask())){
                workflowType.setLink(workflowType.getLink().replace("/expensebill/", "/contractorbill/"));
                workflowType.setLink(workflowType.getLink().replace("/supplierbill/", "/contractorbill/"));
            }else if(EXPENSE_BILL.equals(stateAware.getCurrentState().getNatureOfTask())){
                workflowType.setLink(workflowType.getLink().replace("/contractorbill/", "/expensebill/"));
                workflowType.setLink(workflowType.getLink().replace("/supplierbill/", "/expensebill/"));
            }else if(SUPPLIER_BILL.equals(stateAware.getCurrentState().getNatureOfTask())){
                workflowType.setLink(workflowType.getLink().replace("/expensebill/", "/supplierbill/"));
                workflowType.setLink(workflowType.getLink().replace("/contractorbill/", "/supplierbill/"));
            }
            inboxItems.add(InboxModel
                    .build(stateAware,
                            workflowType,
                            getNextAction(stateAware.getState())));
        }
        //inboxItems.addAll(microserviceUtils.getInboxItems());
        return inboxItems.stream()
                .sorted(Comparator.comparing(InboxModel::getCreatedDate).reversed())
                .collect(Collectors.toList());
    }

    private Optional<InboxRenderService<T>> getInboxRenderService(String type) {
        try {
            String beanName = type + "InboxRenderService";
            InboxRenderService<T> service = (InboxRenderService<T>) applicationContext.getBean(beanName);
            return Optional.of(service);
        } catch (BeansException e) {
            LOG.warn("{}InboxRenderService bean not found", type, e);
            return Optional.empty();
        }
    }


    private String getNextAction(State state) {
        String nextAction = EMPTY;
        if (state.getNextAction() != null) {
            WorkflowAction workflowAction = workflowActionService.getWorkflowActionByNameAndType(state.getNextAction(), state.getType());
            if (workflowAction == null)
                nextAction = state.getNextAction();
            else
                nextAction = workflowAction.getDescription() == null ? state.getNextAction() : workflowAction.getDescription();
        }
        return nextAction;
    }

    private WorkflowType getWorkflowType(String type) {
    	WorkflowType workflowType = WORKFLOW_TYPE_CACHE.get(type);
        if (workflowType == null) {
            workflowType = workflowTypeService.getEnabledWorkflowTypeByType(type);
            if (workflowType != null)
                WORKFLOW_TYPE_CACHE.put(type, workflowType);
        }
        return workflowType;
    }

    private List<Long> currentUserPositionIds() {
       
    	List<Long> positions = new ArrayList();
    	Long empId = ApplicationThreadLocals.getUserId();
    	List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
    	
    	if(null !=employs && employs.size()>0 )
    		
    	employs.get(0).getAssignments().forEach(assignment->{
    		positions.add(assignment.getPosition());
    	});
    	
    	return positions;
//    	return this.ownerGroupService.getOwnerGroupsByUserId(getUserId())
//                .parallelStream()
//                .map(OwnerGroup::getId)
//                .collect(Collectors.toList());
    }
    
    @PostConstruct
    public void listRegisteredRenderServices() {
        String[] beanNames = applicationContext.getBeanNamesForType(InboxRenderService.class);
        Arrays.stream(beanNames).forEach(name -> System.out.println("Registered InboxRenderService bean: " + name));
    }
}