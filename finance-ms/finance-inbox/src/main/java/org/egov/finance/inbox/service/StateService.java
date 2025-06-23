

package org.egov.finance.inbox.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.egov.finance.inbox.repository.StateRepository;
import org.egov.finance.inbox.workflow.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StateService {

    private final StateRepository stateRepository;

    @Autowired
    public StateService(final StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    public boolean isPositionUnderWorkflow(final Long posId, final Date givenDate) {
        return stateRepository.countByOwnerPositionAndCreatedDateGreaterThanEqual(posId, givenDate) > 0;
    }

    public List<String> getAssignedWorkflowTypeNames(final List<Long> ownerIds) {
        return ownerIds.isEmpty() ? Collections.emptyList() :
                stateRepository.findAllTypeByOwnerAndStatus(ownerIds);
    }

    public State getStateById(final Long id) {
        return stateRepository.findById(id).orElse(null);
    }

    public Date getMaxCreatedDateByPositionId(final Long posId) {
        return stateRepository.findMaxCreatedDateByOwnerPosId(posId);
    }

}