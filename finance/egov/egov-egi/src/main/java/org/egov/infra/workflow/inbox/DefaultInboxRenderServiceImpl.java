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

package org.egov.infra.workflow.inbox;

import jakarta.persistence.criteria.*;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infstr.services.PersistenceService;
//import org.hibernate.FlushMode;
import jakarta.persistence.FlushModeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.egov.infra.config.core.ApplicationThreadLocals.getUserId;
import static org.egov.infra.workflow.entity.State.StateStatus.INPROGRESS;
import static org.egov.infra.workflow.entity.State.StateStatus.STARTED;
import static org.hibernate.FetchMode.JOIN;
import static org.hibernate.FlushMode.MANUAL;

/**
 * LTS Migration Fix (Hibernate 6): renders inbox / draft workflow items for a {@link StateAware} type.
 * <p>
 * Hibernate 6 removed {@code Session.createCriteria()}. The previous Hibernate
 * Criteria API (commented in {@link #getAssignedWorkflowItems}) was replaced with
 * JPA {@link CriteriaBuilder} / {@link CriteriaQuery}. Query semantics (owner,
 * status, createdDate order) are unchanged.
 * </p>
 * Every module which has StateAware should initialize this with their own
 * StateAware persistence service. The bean id must follow
 * {@code <YourStateAwareClassName>InboxRenderService} so
 * {@link InboxRenderServiceDelegate} can discover it.
 */
@SuppressWarnings("all")
public class DefaultInboxRenderServiceImpl<T extends StateAware> implements InboxRenderService<T> {

    private static final Logger log = LoggerFactory.getLogger(DefaultInboxRenderServiceImpl.class);

    private final Class<T> stateAwareType;
    private final PersistenceService<T, Long> stateAwarePersistenceService;

    public DefaultInboxRenderServiceImpl(PersistenceService<T, Long> stateAwarePersistenceService) {
        this.stateAwarePersistenceService = stateAwarePersistenceService;
        this.stateAwareType = stateAwarePersistenceService.getType();
    }

    @Override
    public List<T> getAssignedWorkflowItems(List<Long> owners) {
      
//    	List<T> list = this.stateAwarePersistenceService.getSession().createCriteria(this.stateAwareType)
//                .setFetchMode("state", JOIN).createAlias("state", "state")
//                .setFlushMode(MANUAL).setReadOnly(true).setCacheable(true)
//                .add(Restrictions.eq("state.type", this.stateAwareType.getSimpleName()))
//                .add(Restrictions.in("state.ownerPosition", owners))
//                .add(Restrictions.in("state.status", Arrays.asList(INPROGRESS, STARTED)))
//                .addOrder(Order.desc("state.createdDate"))
//                .list();
    	
//    	Criteria criteria =  this.stateAwarePersistenceService.getSession().createCriteria(this.stateAwareType)
//              .setFetchMode("state", JOIN).createAlias("state", "state")
//              .setFlushMode(MANUAL).setReadOnly(true).setCacheable(true)
//              .add(Restrictions.eq("state.type", this.stateAwareType.getSimpleName()))
//              .add(Restrictions.in("state.ownerPosition", owners))
//              .add(Restrictions.in("state.status", Arrays.asList(INPROGRESS, STARTED)))
//              .addOrder(Order.desc("state.createdDate"));
//
//    	List list = criteria.list();
//    	log.info("inbox list size {}", list.size());
//    	return list;

        // Hibernate 6: Session.createCriteria() (commented above) was removed.
        // Equivalent query using JPA CriteriaBuilder; list() became getResultList().
        CriteriaBuilder cb = this.stateAwarePersistenceService
                .getSession()
                .getCriteriaBuilder();

        CriteriaQuery<T> cq = cb.createQuery(this.stateAwareType);
        Root<T> root = cq.from(this.stateAwareType);


        Join<T, ?> stateJoin = root.join("state", JoinType.INNER);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(stateJoin.get("type"),
                this.stateAwareType.getSimpleName()));
        predicates.add(stateJoin.get("ownerPosition").in(owners));
        predicates.add(stateJoin.get("status").in(Arrays.asList(INPROGRESS, STARTED)));
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(stateJoin.get("createdDate")));

        List<T> list = this.stateAwarePersistenceService
                .getSession()
                .createQuery(cq)
                .setFlushMode(FlushModeType.COMMIT)
                .setReadOnly(true)
                .setCacheable(true)
                .getResultList();

        log.info("inbox list size {}", list.size());
        return list;


    }

    public List<T> getDraftWorkflowItems(List<Long> owners) {

        CriteriaBuilder cb = this.stateAwarePersistenceService
                .getSession()
                .getCriteriaBuilder();

        CriteriaQuery<T> cq = cb.createQuery(this.stateAwareType);
        Root<T> root = cq.from(this.stateAwareType);

        Join<T, ?> stateJoin = root.join("state", JoinType.INNER);

        Join<?, ?> ownerPositionJoin = stateJoin.join("ownerPosition", JoinType.INNER);

        Join<?, ?> createdByJoin = stateJoin.join("createdBy", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        // Restrictions.eq("state.type", ...)
        predicates.add(cb.equal(stateJoin.get("type"),
                this.stateAwareType.getSimpleName()));

        predicates.add(ownerPositionJoin.get("id").in(owners));

        predicates.add(cb.equal(stateJoin.get("status"), STARTED));

        predicates.add(cb.equal(createdByJoin.get("id"), getUserId()));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        cq.orderBy(cb.asc(stateJoin.get("createdDate")));

        return this.stateAwarePersistenceService
                .getSession()
                .createQuery(cq)
                .setFlushMode(FlushModeType.COMMIT)
                .setReadOnly(true)
                .setCacheable(true)
                .getResultList();
    }
}
