

package org.egov.finance.inbox.service;

import java.util.List;

import org.egov.finance.inbox.repository.WorkflowTypeRepository;
import org.egov.finance.inbox.workflow.entity.WorkflowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WorkflowTypeService {

    @Autowired
    private WorkflowTypeRepository workflowTypeRepository;

    public WorkflowType getEnabledWorkflowTypeByType(String type) {
        return workflowTypeRepository.findByTypeAndEnabledIsTrue(type);
    }

    public WorkflowType getWorkflowTypeByType(String type) {
        return workflowTypeRepository.findByType(type);
    }

    public List<WorkflowType> getAllWorkflowTypes() {
        return workflowTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "type"));
    }

    public WorkflowType getWorkflowTypeById(Long id) {
        return workflowTypeRepository.findById(id).orElse(null);
    }
}