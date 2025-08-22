

package org.egov.finance.inbox.service;

import java.util.List;

import org.egov.finance.inbox.repository.WorkflowActionRepository;
import org.egov.finance.inbox.workflow.entity.WorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WorkflowActionService {

    @Autowired
    private WorkflowActionRepository workflowActionRepository;

    public WorkflowAction getWorkflowActionByNameAndType(String name, String type) {
        return workflowActionRepository.findByNameAndType(name, type);
    }

    public List<WorkflowAction> getAllWorkflowActionByTypeAndActionNames(String type, List<String> names) {
        return workflowActionRepository.findAllByTypeAndNameIn(type, names);
    }
}
