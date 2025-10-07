

package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.workflow.entity.WorkflowType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowTypeRepository extends JpaRepository<WorkflowType, Long> {

	WorkflowType findByTypeAndEnabledIsTrue(String type);

	WorkflowType findByType(String type);

}