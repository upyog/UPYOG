/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.workflow.service;

import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.script.entity.Script;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infra.workflow.entity.WorkflowAction;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infstr.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This is a generic bean so do not use this to do transition of your own StateAware objects<br/>
 * For your own StateAware object transition declare a separate bean definition like<br/>
 * <p>
 * <pre>
 *
 *       &lt;bean id="myStateAwareWorkflowService" parent="workflowService"&gt;
 *               &lt;constructor-arg index="0" ref="myStateAwarePersistenceService"/&gt;
 *       &lt;/bean&gt;
 * </pre>
 **/
public class SimpleWorkflowService<T extends StateAware> implements WorkflowService<T> {

    private static final String WF_ACTION_ARG          = "action";
    private static final String WF_ITEM_ARG            = "wfItem";
    private static final String PERSISTENCE_SERVICE_ARG = "persistenceService";
    private static final String CURRENT_DESIGNATION    = "currentDesignation";
    private static final String DEPARTMENT             = "department";
    private static final String FROM_QTY               = "fromQty";
    private static final String TO_QTY                 = "toQty";
    private static final String ANY                    = "ANY";

    private final PersistenceService<T, Long> stateAwarePersistenceService;

    @Autowired
    private WorkflowActionService workflowActionService;

    @Autowired
    private ScriptService scriptService;

    public SimpleWorkflowService(PersistenceService<T, Long> stateAwarePersistenceService) {
        this.stateAwarePersistenceService = stateAwarePersistenceService;
    }

    // =========================================================
    // Transition methods — unchanged
    // =========================================================

    @Override
    public T transition(WorkflowAction workflowAction, T stateAware, String comments) {
        scriptService.executeScript(getScript(stateAware, workflowAction.getName()),
                ScriptService.createContext(WF_ACTION_ARG, this,
                        WF_ITEM_ARG, stateAware,
                        PERSISTENCE_SERVICE_ARG, this.stateAwarePersistenceService,
                        "workflowService", this,
                        "comments", comments));
        return this.stateAwarePersistenceService.persist(stateAware);
    }

    @Override
    public T transition(String actionName, T stateAware, String comment) {
        WorkflowAction workflowAction = workflowActionService
                .getWorkflowActionByNameAndType(actionName, stateAware.getStateType());
        if (workflowAction == null)
            workflowAction = new WorkflowAction(actionName, stateAware.getStateType(), actionName);
        return transition(workflowAction, stateAware, comment);
    }

    @Override
    public List<WorkflowAction> getValidActions(T stateAware) {
        String scriptName = stateAware.getStateType() + ".workflow.validactions";
        Script transitionScript = this.scriptService.getByName(scriptName);
        List<String> actionNames = (List<String>) scriptService.executeScript(transitionScript,
                ScriptService.createContext(WF_ITEM_ARG, stateAware,
                        "workflowService", this,
                        PERSISTENCE_SERVICE_ARG, this.stateAwarePersistenceService));
        List<WorkflowAction> savedWorkflowActions = workflowActionService
                .getAllWorkflowActionByTypeAndActionNames(stateAware.getStateType(), actionNames);
        return savedWorkflowActions.isEmpty()
                ? createActions(stateAware, actionNames)
                : savedWorkflowActions;
    }

    public Object execute(T stateAware) {
        return scriptService.executeScript(getScript(stateAware, EMPTY),
                ScriptService.createContext(WF_ACTION_ARG, this,
                        WF_ITEM_ARG, stateAware,
                        PERSISTENCE_SERVICE_ARG, this.stateAwarePersistenceService));
    }

    public Object execute(T stateAware, String comments) {
        return scriptService.executeScript(getScript(stateAware, EMPTY),
                ScriptService.createContext(WF_ACTION_ARG, this,
                        WF_ITEM_ARG, stateAware,
                        PERSISTENCE_SERVICE_ARG, this.stateAwarePersistenceService,
                        "comments", comments));
    }

    private Script getScript(T stateAware, String actionName) {
        Script script = null;
        if (isNotBlank(actionName))
            script = this.scriptService.getByName(new StringBuilder(10)
                    .append(stateAware.getStateType())
                    .append(".workflow.")
                    .append(actionName)
                    .toString());
        if (script == null)
            script = scriptService.getByName(stateAware.getStateType() + ".workflow");
        if (script == null)
            throw new ApplicationRuntimeException("workflow.script.notfound");
        return script;
    }

    private List<WorkflowAction> createActions(T stateAware, List<String> actionNames) {
        List<WorkflowAction> workflowActions = new ArrayList<>();
        for (String action : actionNames)
            workflowActions.add(new WorkflowAction(action, stateAware.getStateType(), action));
        return workflowActions;
    }

    // =========================================================
    // WfMatrix methods — all migrated to JPA Criteria API
    // =========================================================

    @Override
    public WorkFlowMatrix getWfMatrix(String type, String department, BigDecimal amountRule,
                                      String additionalRule, String currentState, String pendingActions) {
        // ✅ No date filter — delegate to helper directly
        CriteriaQuery<WorkFlowMatrix> wfMatrixCriteria = createWfMatrixAdditionalCriteria(
                type, department, amountRule, additionalRule, currentState, pendingActions, null);
        return getWorkflowMatrixObj(type, additionalRule, currentState, pendingActions,
                null, wfMatrixCriteria);
    }

    /*
     * Hibernate 6 JPA Criteria API Migration Fix for Workflow Matrix Queries:
     * Reused the existing `Root<WorkFlowMatrix>` from `wfMatrixCriteria.getRoots().iterator().next()`
     * instead of calling `wfMatrixCriteria.from(WorkFlowMatrix.class)` multiple times across helper methods.
     * In JPA Criteria API, calling `.from(Entity.class)` on an existing CriteriaQuery creates a second FROM root clause
     * (e.g. `FROM eg_wf_matrix w1, eg_wf_matrix w2`), which causes Hibernate 6 to throw:
     * "java.lang.IllegalArgumentException: Criteria has multiple query roots".
     */
    @Override
    public WorkFlowMatrix getWfMatrix(String type, String department, BigDecimal amountRule,
                                      String additionalRule, String currentState,
                                      String pendingActions, Date date) {
        CriteriaBuilder cb = this.stateAwarePersistenceService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowMatrix> wfMatrixCriteria = createWfMatrixAdditionalCriteria(
                type, department, amountRule, additionalRule, currentState, pendingActions, null);

        @SuppressWarnings("unchecked")
        Root<WorkFlowMatrix> root = (Root<WorkFlowMatrix>) (Root<?>) wfMatrixCriteria.getRoots().iterator().next();
        Date effectiveDate = date == null ? new Date() : date;

        // ✅ Criterion fromDateCriteria — Restrictions.le("fromDate", date)
        Predicate fromDatePredicate = cb.lessThanOrEqualTo(root.get("fromDate"), effectiveDate);

        // ✅ Criterion toDateCriteria — Restrictions.ge("toDate", date)
        Predicate toDatePredicate = cb.greaterThanOrEqualTo(root.get("toDate"), effectiveDate);

        // ✅ dateCriteria — conjunction of from + to
        Predicate dateCriteria = cb.and(fromDatePredicate, toDatePredicate);

        // ✅ Restrictions.or(dateCriteria, fromDateCriteria)
        Predicate existingWhere = wfMatrixCriteria.getRestriction();
        if (existingWhere != null)
            wfMatrixCriteria.where(cb.and(existingWhere, cb.or(dateCriteria, fromDatePredicate)));
        else
            wfMatrixCriteria.where(cb.or(dateCriteria, fromDatePredicate));

        return getWorkflowMatrixObj(type, additionalRule, currentState, pendingActions,
                null, wfMatrixCriteria);
    }

    @Override
    public WorkFlowMatrix getWfMatrix(String type, String department, BigDecimal amountRule,
                                      String additionalRule, String currentState,
                                      String pendingActions, Date date, String designation) {
        // ✅ 8-param version — with date + designation
        CriteriaBuilder cb = this.stateAwarePersistenceService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowMatrix> wfMatrixCriteria = createWfMatrixAdditionalCriteria(
                type, department, amountRule, additionalRule, currentState, pendingActions, designation);

        @SuppressWarnings("unchecked")
        Root<WorkFlowMatrix> root = (Root<WorkFlowMatrix>) (Root<?>) wfMatrixCriteria.getRoots().iterator().next();
        Date effectiveDate = date == null ? new Date() : date;

        // ✅ Date predicates
        Predicate fromDatePredicate = cb.lessThanOrEqualTo(root.get("fromDate"), effectiveDate);
        Predicate toDatePredicate   = cb.greaterThanOrEqualTo(root.get("toDate"), effectiveDate);
        Predicate dateCriteria      = cb.and(fromDatePredicate, toDatePredicate);

        // ✅ ilike(CURRENT_DESIGNATION, designation) → cb.like + cb.lower
        Predicate designationPredicate = cb.like(
                cb.lower(root.get(CURRENT_DESIGNATION)),
                (isNotBlank(designation) ? designation : EMPTY).toLowerCase()
        );

        // ✅ Combine all
        Predicate existingWhere = wfMatrixCriteria.getRestriction();
        Predicate datePredicate = cb.or(dateCriteria, fromDatePredicate);

        if (existingWhere != null)
            wfMatrixCriteria.where(cb.and(existingWhere, designationPredicate, datePredicate));
        else
            wfMatrixCriteria.where(cb.and(designationPredicate, datePredicate));

        return getWorkflowMatrixObj(type, additionalRule, currentState, pendingActions,
                designation, wfMatrixCriteria);
    }

    private WorkFlowMatrix getWorkflowMatrixObj(String type, String additionalRule,
                                                String currentState, String pendingActions,
                                                String designation,
                                                CriteriaQuery<WorkFlowMatrix> wfMatrixCriteria) {
        // ✅ .list() → .getResultList()
        List<WorkFlowMatrix> workflowMatrix = this.stateAwarePersistenceService.getSession()
                .createQuery(wfMatrixCriteria)
                .getResultList();

        if (workflowMatrix.isEmpty()) {
            CriteriaBuilder cb = this.stateAwarePersistenceService.getSession().getCriteriaBuilder();
            CriteriaQuery<WorkFlowMatrix> defaultCq = commonWorkFlowMatrixCriteria(
                    type, additionalRule, currentState, pendingActions);
            @SuppressWarnings("unchecked")
            Root<WorkFlowMatrix> root = (Root<WorkFlowMatrix>) (Root<?>) defaultCq.getRoots().iterator().next();

            List<Predicate> predicates = new ArrayList<>(
                    Arrays.asList(defaultCq.getRestriction() != null
                            ? new Predicate[]{defaultCq.getRestriction()}
                            : new Predicate[0])
            );

            // ✅ Restrictions.eq(DEPARTMENT, ANY)
            predicates.add(cb.equal(root.get(DEPARTMENT), ANY));

            // ✅ ilike(CURRENT_DESIGNATION, designation)
            if (isNotBlank(designation))
                predicates.add(cb.like(
                        cb.lower(root.get(CURRENT_DESIGNATION)),
                        designation.toLowerCase()
                ));

            defaultCq.where(cb.and(predicates.toArray(new Predicate[0])));

            List<WorkFlowMatrix> defaultMatrix = this.stateAwarePersistenceService.getSession()
                    .createQuery(defaultCq)
                    .getResultList();

            return defaultMatrix.isEmpty() ? null : defaultMatrix.get(0);

        } else {
            // ✅ same logic — toDate null wala pehle return karo
            for (WorkFlowMatrix matrix : workflowMatrix)
                if (matrix.getToDate() == null)
                    return matrix;
            return workflowMatrix.get(0);
        }
    }

    private CriteriaQuery<WorkFlowMatrix> createWfMatrixAdditionalCriteria(String type, String department,
                                                                           BigDecimal amountRule, String additionalRule,
                                                                           String currentState, String pendingActions,
                                                                           String designation) {
        CriteriaBuilder cb = this.stateAwarePersistenceService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowMatrix> cq = commonWorkFlowMatrixCriteria(
                type, additionalRule, currentState, pendingActions);
        @SuppressWarnings("unchecked")
        Root<WorkFlowMatrix> root = (Root<WorkFlowMatrix>) (Root<?>) cq.getRoots().iterator().next();

        List<Predicate> predicates = new ArrayList<>(
                Arrays.asList(cq.getRestriction() != null
                        ? new Predicate[]{cq.getRestriction()}
                        : new Predicate[0])
        );

        // ✅ Restrictions.eq(DEPARTMENT, department)
        if (isNotBlank(department))
            predicates.add(cb.equal(root.get(DEPARTMENT), department));

        // ✅ Amount rule — Disjunction of two Conjunctions
        if (amountRule != null && BigDecimal.ZERO.compareTo(amountRule) != 0) {
            // amount1st — fromQty <= amountRule AND toQty >= amountRule
            Predicate amount1st = cb.and(
                    cb.le(root.get(FROM_QTY), amountRule),
                    cb.ge(root.get(TO_QTY), amountRule)
            );
            // amount2nd — fromQty <= amountRule AND toQty IS NULL
            Predicate amount2nd = cb.and(
                    cb.le(root.get(FROM_QTY), amountRule),
                    cb.isNull(root.get(TO_QTY))
            );
            predicates.add(cb.or(amount1st, amount2nd));
        }

        // ✅ ilike(CURRENT_DESIGNATION, designation) → cb.like + cb.lower
        if (isNotBlank(designation))
            predicates.add(cb.like(
                    cb.lower(root.get(CURRENT_DESIGNATION)),
                    designation.toLowerCase()
            ));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        return cq;
    }

    public WorkFlowMatrix getPreviousStateFromWfMatrix(String type, String department,
                                                       BigDecimal amountRule, String additionalRule,
                                                       String currentState, String pendingActions) {
        // ✅ FIXED — was still using old Criteria API
        CriteriaBuilder cb = this.stateAwarePersistenceService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowMatrix> cq = previousWorkFlowMatrixCriteria(
                type, additionalRule, currentState, pendingActions);
        @SuppressWarnings("unchecked")
        Root<WorkFlowMatrix> root = (Root<WorkFlowMatrix>) (Root<?>) cq.getRoots().iterator().next();

        List<Predicate> predicates = new ArrayList<>(
                Arrays.asList(cq.getRestriction() != null
                        ? new Predicate[]{cq.getRestriction()}
                        : new Predicate[0])
        );

        // ✅ department check
        if (department != null && !"".equals(department))
            predicates.add(cb.equal(root.get(DEPARTMENT), department));
        else
            predicates.add(cb.equal(root.get(DEPARTMENT), ANY));

        // ✅ Amount rule — same disjunction pattern
        if (amountRule != null && BigDecimal.ZERO.compareTo(amountRule) != 0) {
            Predicate amount1st = cb.and(
                    cb.le(root.get(FROM_QTY), amountRule),
                    cb.ge(root.get(TO_QTY), amountRule)
            );
            Predicate amount2nd = cb.and(
                    cb.le(root.get(FROM_QTY), amountRule),
                    cb.isNull(root.get(TO_QTY))
            );
            predicates.add(cb.or(amount1st, amount2nd));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // ✅ .list() → .getResultList()
        List<WorkFlowMatrix> workflowMatrix = this.stateAwarePersistenceService.getSession()
                .createQuery(cq)
                .getResultList();

        return workflowMatrix.isEmpty() ? null : workflowMatrix.get(0);
    }

    private CriteriaQuery<WorkFlowMatrix> previousWorkFlowMatrixCriteria(String type, String additionalRule,
                                                                         String currentState, String pendingActions) {
        CriteriaBuilder cb = this.stateAwarePersistenceService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowMatrix> cq = cb.createQuery(WorkFlowMatrix.class);
        Root<WorkFlowMatrix> root = cq.from(WorkFlowMatrix.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("objectType"), type));

        if (isNotBlank(additionalRule))
            predicates.add(cb.equal(root.get("additionalRule"), additionalRule));

        // ✅ ilike("nextAction", ..., MatchMode.EXACT) → cb.like + lower, EXACT
        if (isNotBlank(pendingActions))
            predicates.add(cb.like(
                    cb.lower(root.get("nextAction")),
                    pendingActions.toLowerCase()
            ));

        // ✅ ilike("nextState", ..., MatchMode.EXACT) → cb.like + lower, EXACT
        if (isNotBlank(currentState))
            predicates.add(cb.like(
                    cb.lower(root.get("nextState")),
                    currentState.toLowerCase()
            ));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        return cq;
    }

    private CriteriaQuery<WorkFlowMatrix> commonWorkFlowMatrixCriteria(String type, String additionalRule,
                                                                       String currentState, String pendingActions) {
        CriteriaBuilder cb = this.stateAwarePersistenceService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowMatrix> cq = cb.createQuery(WorkFlowMatrix.class);
        Root<WorkFlowMatrix> root = cq.from(WorkFlowMatrix.class);

        List<Predicate> predicates = new ArrayList<>();

        // ✅ Restrictions.eq("objectType", type)
        predicates.add(cb.equal(root.get("objectType"), type));

        // ✅ Restrictions.eq("additionalRule", additionalRule)
        if (isNotBlank(additionalRule))
            predicates.add(cb.equal(root.get("additionalRule"), additionalRule));

        // ✅ ilike("pendingActions", ..., MatchMode.ANYWHERE) → "%value%"
        if (isNotBlank(pendingActions))
            predicates.add(cb.like(
                    cb.lower(root.get("pendingActions")),
                    "%" + pendingActions.toLowerCase() + "%"
            ));

        // ✅ ilike("currentState", ..., MatchMode.EXACT) → exact value
        if (isNotBlank(currentState))
            predicates.add(cb.like(
                    cb.lower(root.get("currentState")),
                    currentState.toLowerCase()
            ));
        else
            predicates.add(cb.like(
                    cb.lower(root.get("currentState")),
                    "new"                                   // default
            ));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        return cq;
    }
}