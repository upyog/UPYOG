

package org.egov.finance.inbox.repository;

import java.util.List;

import org.egov.finance.inbox.workflow.entity.WorkflowAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowActionRepository extends JpaRepository<WorkflowAction, Long> {

    WorkflowAction findByNameAndType(String name, String type);

    List<WorkflowAction> findAllByTypeAndNameIn(String type, List<String> names);
}
