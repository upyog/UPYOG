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

package org.egov.infra.workflow.matrix.service;

import org.egov.infra.workflow.entity.WorkflowTypes;
import org.egov.infra.workflow.matrix.entity.WorkFlowAdditionalRule;
import org.egov.infra.workflow.matrix.repository.WorkFlowAdditionalRuleRepository;
import org.egov.infra.workflow.service.WorkflowTypeService;
import org.egov.infstr.services.PersistenceService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class WorkFlowAdditionalDetailsService {


    public static final String OBJECTTYPEID_ID = "objecttypeid.id";
    public static final String ADDITIONAL_RULE = "additionalRule";

    @Autowired
    @Qualifier("entityQueryService")
    private PersistenceService entityQueryService;

    @Autowired
    private WorkflowTypeService workflowTypeService;

    @Autowired
    private WorkFlowAdditionalRuleRepository workFlowAdditionalRuleRepository;

    public List getAllModuleTypeforStatus() {
        return this.entityQueryService.findAllBy(" select distinct(moduletype) from EgwStatus order by moduletype asc");
    }

    public List<WorkflowTypes> getobjectTypeList() {
        return workflowTypeService.getAllWorkflowTypes();
    }

    @Transactional
    public WorkFlowAdditionalRule save(WorkFlowAdditionalRule wfAdditionalRule) {
        return workFlowAdditionalRuleRepository.save(wfAdditionalRule);
    }

    public List<WorkFlowAdditionalRule> getAdditionalRulesbyObject(final Long objectType) {

        CriteriaBuilder cb = entityQueryService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowAdditionalRule> cq = cb.createQuery(WorkFlowAdditionalRule.class);
        Root<WorkFlowAdditionalRule> root = cq.from(WorkFlowAdditionalRule.class);

        // ✅ Restrictions.eq() → cb.equal()
        cq.where(cb.equal(root.get(OBJECTTYPEID_ID), objectType));

        return entityQueryService.getSession()
                .createQuery(cq)
                .getResultList();  // ✅ .list() → .getResultList()
    }

    public WorkFlowAdditionalRule getObjectbyTypeandRule(final Long objectType,
                                                         final String additionalRules) {

        CriteriaBuilder cb = entityQueryService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowAdditionalRule> cq = cb.createQuery(WorkFlowAdditionalRule.class);
        Root<WorkFlowAdditionalRule> root = cq.from(WorkFlowAdditionalRule.class);

        List<Predicate> predicates = new ArrayList<>();

        // ✅ Restrictions.eq() → cb.equal()
        predicates.add(cb.equal(root.get(OBJECTTYPEID_ID), objectType));

        // ✅ Restrictions.isNull() / Restrictions.eq()
        if ("-1".equals(additionalRules)) {
            predicates.add(cb.isNull(root.get(ADDITIONAL_RULE)));
        } else {
            predicates.add(cb.equal(root.get(ADDITIONAL_RULE), additionalRules));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        List<WorkFlowAdditionalRule> wfAdditionalRules = entityQueryService.getSession()
                .createQuery(cq)
                .getResultList();
        // ✅ same logic — pehla element ya null
        return wfAdditionalRules.isEmpty() ? null : wfAdditionalRules.get(0);
    }

    public WorkFlowAdditionalRule getObjectbyTypeandRule(final Long objectId,
                                                         final Long objectType,
                                                         final String additionalRules) {

        CriteriaBuilder cb = entityQueryService.getSession().getCriteriaBuilder();
        CriteriaQuery<WorkFlowAdditionalRule> cq = cb.createQuery(WorkFlowAdditionalRule.class);
        Root<WorkFlowAdditionalRule> root = cq.from(WorkFlowAdditionalRule.class);

        List<Predicate> predicates = new ArrayList<>();

        // ✅ Restrictions.eq() → cb.equal()
        predicates.add(cb.equal(root.get(OBJECTTYPEID_ID), objectType));

        // ✅ Restrictions.ne() → cb.notEqual()
        predicates.add(cb.notEqual(root.get("id"), objectId));

        // ✅ null check — Restrictions.isNull() / Restrictions.eq()
        if (additionalRules == null) {
            predicates.add(cb.isNull(root.get(ADDITIONAL_RULE)));
        } else {
            predicates.add(cb.equal(root.get(ADDITIONAL_RULE), additionalRules));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        List<WorkFlowAdditionalRule> wfAdditionalRules = entityQueryService.getSession()
                .createQuery(cq)
                .getResultList();
        // ✅ same logic — pehla element ya null
        return wfAdditionalRules.isEmpty() ? null : wfAdditionalRules.get(0);
    }

}
