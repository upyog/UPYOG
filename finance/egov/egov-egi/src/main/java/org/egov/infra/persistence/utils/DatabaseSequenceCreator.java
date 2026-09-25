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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static java.lang.String.format;

/**
 * LTS Migration Fix (Hibernate 6 + WildFly JTA):
 * Creates per-fund/year voucher sequences (e.g. {@code sq_1_csl_202122}) that
 * are not shipped in Flyway. {@code IF NOT EXISTS} is required so two concurrent
 * first-voucher requests do not fail. {@code REQUIRES_NEW} commits the DDL
 * independently of the voucher create transaction — needed because a failed
 * {@code NEXTVAL} on Hibernate 6 would otherwise leave the outer JTA txn
 * rollback-only. Sequence name is validated before interpolation.
 */
@Service
public class DatabaseSequenceCreator {
    private static final String CREATE_SEQ_QUERY = "CREATE SEQUENCE IF NOT EXISTS %s";
    private static final String VALID_SEQUENCE_NAME = "[A-Za-z][A-Za-z0-9_]*";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * LTS: {@code CREATE SEQUENCE IF NOT EXISTS} in a new transaction so the
     * subsequent {@code NEXTVAL} can see the sequence. Name must match
     * {@code [A-Za-z][A-Za-z0-9_]*} (voucher sequences are already normalized
     * by {@link GenericSequenceNumberGenerator}).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSequence(String sequenceName) {
        if (sequenceName == null || !sequenceName.matches(VALID_SEQUENCE_NAME))
            throw new IllegalArgumentException("Invalid sequence name: " + sequenceName);
        entityManager.unwrap(Session.class)
                .createNativeQuery(format(CREATE_SEQ_QUERY, sequenceName))
                .executeUpdate();
    }
}
