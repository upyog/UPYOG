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

package org.egov.infra.persistence.validator;

import static org.egov.infra.utils.DateUtils.endOfDay;
import static org.egov.infra.utils.DateUtils.startOfDay;

import java.util.Date;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.egov.infra.persistence.validator.annotation.UniqueDateOverlap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniqueDateOverlapValidator implements ConstraintValidator<UniqueDateOverlap, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniqueDateOverlapValidator.class);

    private UniqueDateOverlap uniqueDateOverlap;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void initialize(final UniqueDateOverlap uniqueDateOverlap) {
        this.uniqueDateOverlap = uniqueDateOverlap;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            boolean isValid = checkUnique(object);
            if (!isValid)
                context.buildConstraintViolationWithTemplate(uniqueDateOverlap.message()).
                        addPropertyNode(uniqueDateOverlap.fromField()).addConstraintViolation();
            return isValid;
        } catch (final IllegalAccessException e) {
            LOGGER.error("Error while validating unique key with date overlapping", e);
        }
        return false;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean checkUnique(Object object) throws IllegalAccessException {
        Number id = (Number) FieldUtils.readField(object, uniqueDateOverlap.id(), true);

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root root = query.from(object.getClass());

        // Build unique-field predicates (AND)
        java.util.List<Predicate> uniquePredicates = new java.util.ArrayList<>();
        for (String fieldName : uniqueDateOverlap.uniqueFields()) {
            Object fieldValue = FieldUtils.readField(object, fieldName, true);
            if (fieldValue instanceof String)
                uniquePredicates.add(cb.equal(cb.lower(root.get(fieldName)), ((String) fieldValue).toLowerCase()));
            else
                uniquePredicates.add(cb.equal(root.get(fieldName), fieldValue));
        }

        Date fromDate = startOfDay((Date) FieldUtils.readField(object, uniqueDateOverlap.fromField(), true));
        Date toDate = endOfDay((Date) FieldUtils.readField(object, uniqueDateOverlap.toField(), true));

        // existing.fromField <= fromDate AND existing.toField >= fromDate
        Predicate checkFromDate = cb.and(
                cb.lessThanOrEqualTo(root.get(uniqueDateOverlap.fromField()), fromDate),
                cb.greaterThanOrEqualTo(root.get(uniqueDateOverlap.toField()), fromDate));

        // existing.fromField <= toDate AND existing.toField >= toDate
        Predicate checkToDate = cb.and(
                cb.lessThanOrEqualTo(root.get(uniqueDateOverlap.fromField()), toDate),
                cb.greaterThanOrEqualTo(root.get(uniqueDateOverlap.toField()), toDate));

        // existing.fromField >= fromDate AND existing.toField <= toDate
        Predicate checkFromAndToDate = cb.and(
                cb.greaterThanOrEqualTo(root.get(uniqueDateOverlap.fromField()), fromDate),
                cb.lessThanOrEqualTo(root.get(uniqueDateOverlap.toField()), toDate));

        // Date overlap = any of the three cases above
        uniquePredicates.add(cb.or(checkFromDate, checkToDate, checkFromAndToDate));

        // Exclude current record when updating
        if (id != null)
            uniquePredicates.add(cb.notEqual(root.get(uniqueDateOverlap.id()), id));

        query.select(cb.count(root)).where(cb.and(uniquePredicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getSingleResult() == 0L;
    }
}
