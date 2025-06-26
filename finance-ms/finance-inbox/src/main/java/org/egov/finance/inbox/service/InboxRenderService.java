
package org.egov.finance.inbox.service;



import java.util.List;

import org.egov.finance.inbox.workflow.entity.StateAware;

public interface InboxRenderService<T extends StateAware> {

    List<T> getAssignedWorkflowItems(List<Long> owners);

    List<T> getDraftWorkflowItems(List<Long> owners);

}
