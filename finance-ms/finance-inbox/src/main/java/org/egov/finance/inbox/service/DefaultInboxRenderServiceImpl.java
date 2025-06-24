package org.egov.finance.inbox.service;
import java.util.List;

import org.egov.finance.inbox.util.ApplicationThreadLocals;
import org.egov.finance.inbox.workflow.entity.State.StateStatus;
import org.egov.finance.inbox.workflow.entity.StateAware;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class DefaultInboxRenderServiceImpl<T extends StateAware> implements InboxRenderService<T> {

    private final Class<T> stateAwareType;

    @PersistenceContext
    private EntityManager entityManager;

    public DefaultInboxRenderServiceImpl(Class<T> stateAwareType) {
        this.stateAwareType = stateAwareType;
    }

    @Override
    public List<T> getAssignedWorkflowItems(List<Long> owners) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(stateAwareType);
        Root<T> root = query.from(stateAwareType);
        Join<T, ?> stateJoin = root.join("state");

        Predicate typePredicate = cb.equal(stateJoin.get("type"), stateAwareType.getSimpleName());
        Predicate positionPredicate = stateJoin.get("ownerPosition").get("id").in(owners);
        Predicate statusPredicate = stateJoin.get("status").in(StateStatus.INPROGRESS, StateStatus.STARTED);

        query.select(root)
             .where(cb.and(typePredicate, positionPredicate, statusPredicate))
             .orderBy(cb.desc(stateJoin.get("createdDate")));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<T> getDraftWorkflowItems(List<Long> owners) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(stateAwareType);
        Root<T> root = query.from(stateAwareType);
        Join<T, ?> stateJoin = root.join("state");

        Predicate typePredicate = cb.equal(stateJoin.get("type"), stateAwareType.getSimpleName());
        Predicate positionPredicate = stateJoin.get("ownerPosition").get("id").in(owners);
        Predicate statusPredicate = cb.equal(stateJoin.get("status"), StateStatus.STARTED);
        Predicate createdByPredicate = cb.equal(stateJoin.get("createdBy").get("id"), ApplicationThreadLocals.getUserId());

        query.select(root)
             .where(cb.and(typePredicate, positionPredicate, statusPredicate, createdByPredicate))
             .orderBy(cb.asc(stateJoin.get("createdDate")));

        return entityManager.createQuery(query).getResultList();
    }
}
