package org.egov.services.budget.inbox;

import org.egov.infra.workflow.inbox.InboxRenderService;
import org.egov.model.budget.BudgetRegister;
import org.egov.model.repository.BudgetRegisterWorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Inbox rendering service for {@link BudgetRegister} workflow items.
 *
 * <p>This service implements the {@link InboxRenderService} interface to integrate
 * {@link BudgetRegister} entities with the eGov workflow inbox system. It provides
 * functionality to fetch and display budget register items in the user's workflow inbox.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Retrieves budget registers assigned to specific positions/users for workflow action.</li>
 *   <li>Fetches draft budget registers that have not yet been submitted to workflow.</li>
 *   <li>Filters out closed/completed workflow items from the inbox.</li>
 *   <li>Handles empty owner lists gracefully by returning empty results.</li>
 * </ul>
 *
 * <p><b>Workflow Integration:</b></p>
 * <ul>
 *   <li><b>Assigned items:</b> Budget registers with active workflow states assigned to user positions.</li>
 *   <li><b>Draft items:</b> Budget registers created by users but not yet submitted (state is {@code NULL}).</li>
 * </ul>
 *
 * <p>Used by the workflow inbox UI to populate the user's pending tasks and draft items.</p>
 *
 * @see InboxRenderService
 * @see BudgetRegister
 */

@Service("BudgetRegisterInboxRenderService")
public class BudgetRegisterInboxRenderService implements InboxRenderService<BudgetRegister> {

//    @Autowired
//    private BudgetRegisterWorkflowRepository budgetRegisterWorkflowRepository;


    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Retrieves all {@link BudgetRegister} workflow items currently assigned to the given owner positions.
     *
     * <p>Fetches budget registers whose workflow state is assigned to one of the provided
     * position IDs and whose workflow status is not closed (status {@code <> 2}).</p>
     *
     * @param owners a {@link List} of position IDs representing the current user's assigned positions;
     *               must not be {@code null}
     * @return a {@link List} of {@link BudgetRegister} entities assigned to the given positions
     *         with an active (non-closed) workflow state, or an empty list if {@code owners}
     *         is {@code null} or empty
     */

    @Override
    public List<BudgetRegister> getAssignedWorkflowItems(List<Long> owners) {
        if (owners == null || owners.isEmpty()) return Collections.emptyList();

        String jpql = "SELECT b FROM BudgetRegister b " +
                "WHERE b.state.ownerPosition IN :owners " +
                "AND b.state.status <> 2";

        return entityManager.createQuery(jpql, BudgetRegister.class)
                .setParameter("owners", owners)
                .getResultList();
    }


    /**
     * Retrieves all draft {@link BudgetRegister} items created by the given users
     * that have not yet been submitted to the workflow.
     *
     * <p>A budget register is considered a draft when its workflow {@code state} is {@code NULL},
     * meaning it has been saved but not yet forwarded for approval. Results are filtered
     * to only include records created by users whose IDs match the provided list.</p>
     *
     * @param owners a {@link List} of user IDs representing the current user's identities;
     *               must not be {@code null}
     * @return a {@link List} of draft {@link BudgetRegister} entities created by the specified
     *         users with no associated workflow state, or an empty list if {@code owners}
     *         is {@code null} or empty
     */

    @Override
    public List<BudgetRegister> getDraftWorkflowItems(List<Long> owners) {
        if (owners == null || owners.isEmpty()) return Collections.emptyList();

        String jpql = "SELECT b FROM BudgetRegister b " +
                "WHERE b.state IS NULL " +
                "AND b.createdBy IN :userIds";

        return entityManager.createQuery(jpql, BudgetRegister.class)
                .setParameter("userIds", owners)
                .getResultList();
    }
}
