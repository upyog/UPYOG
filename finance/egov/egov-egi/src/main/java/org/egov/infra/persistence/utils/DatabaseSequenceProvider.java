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

package org.egov.infra.persistence.utils;

import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Locale;

/**
 * LTS Migration Fix (Hibernate 6 + WildFly JTA):
 * {@link #getNextSequence(String)} must only be called for a sequence that
 * already exists. A missing-sequence {@code NEXTVAL} used to be recovered by
 * catching {@code SQLGrammarException}; Hibernate 6 marks that JTA transaction
 * rollback-only, so {@code noRollbackFor} is not enough. Callers (see
 * {@link GenericSequenceNumberGenerator}) must use {@link #sequenceExists(String)}
 * first. Scoped to {@code current_schema()} for multi-tenant city schemas.
 */
@Service
public class DatabaseSequenceProvider {

    private static final String NEXT_SEQ_QUERY = "SELECT NEXTVAL (:sequenceName) AS NEXTVAL";
    private static final String SEQUENCE_EXISTS_QUERY =
            "SELECT COUNT(*) FROM information_schema.sequences "
                    + "WHERE sequence_schema = current_schema() AND sequence_name = :sequenceName";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * LTS: keep {@code REQUIRES_NEW} so nextval is isolated. {@code noRollbackFor}
     * is retained for non-missing SQL errors but does <em>not</em> prevent
     * Hibernate 6 from marking rollback-only when the relation is missing.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW, noRollbackFor = SQLGrammarException.class)
    public Serializable getNextSequence(String sequenceName) throws SQLGrammarException {
        return (Serializable) entityManager.unwrap(Session.class)
                .createNativeQuery(NEXT_SEQ_QUERY)
                .setParameter("sequenceName", sequenceName)
                .uniqueResult();
    }

    /**
     * LTS Migration Fix (Hibernate 6 + JTA): catalog lookup that does not throw
     * when the sequence is absent. Used instead of "try NEXTVAL and catch
     * SQLGrammarException", which poisoned the Bank to Bank Transfer create
     * transaction ({@code relation "sq_1_csl_202122" does not exist}).
     */
    @Transactional(readOnly = true)
    public boolean sequenceExists(String sequenceName) {
        if (sequenceName == null || sequenceName.trim().isEmpty())
            return false;
        final Object result = entityManager.unwrap(Session.class)
                .createNativeQuery(SEQUENCE_EXISTS_QUERY)
                .setParameter("sequenceName", sequenceName.toLowerCase(Locale.ROOT))
                .uniqueResult();
        return result instanceof Number && ((Number) result).longValue() > 0;
    }
}
