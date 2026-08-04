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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.persistence.validator.annotation.CompositeUnique;
import org.hibernate.Session;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;



import jakarta.persistence.EntityManager;




import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CompositeUniqueCheckValidator implements ConstraintValidator<CompositeUnique, Object> {

    private CompositeUnique unique;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void initialize(final CompositeUnique unique) {
        this.unique = unique;
    }

    @Override
    public boolean isValid(final Object arg0, final ConstraintValidatorContext constraintValidatorContext) {
        try {
            final Number id = (Number) FieldUtils.readField(arg0, unique.id(), true);
            final boolean isValid = checkCompositeUniqueKey(arg0, id);
            if (!isValid && unique.enableDfltMsg())
                for (final String fieldName : unique.fields())
                    constraintValidatorContext.buildConstraintViolationWithTemplate(unique.message()).addPropertyNode(fieldName)
                            .addConstraintViolation();
            return isValid;
        } catch (final IllegalAccessException e) {
            throw new ApplicationRuntimeException("Error while validating composite unique key", e);
        }

    }

    private boolean checkCompositeUniqueKey(final Object arg0, final Number id)
            throws IllegalAccessException {

        // ✅ CriteriaBuilder setup
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ✅ Dynamic class — superclass ya actual class
        Class<?> targetClass = unique.isSuperclass()
                ? arg0.getClass().getSuperclass()
                : arg0.getClass();

        CriteriaQuery<Object> cq = cb.createQuery(Object.class);
        Root<?> root = cq.from(targetClass);

        // ✅ Conjunction — list of predicates
        List<Predicate> predicates = new ArrayList<>();

        for (final String fieldName : unique.fields()) {
            final Object fieldValue = FieldUtils.readField(arg0, fieldName, true);

            if (unique.checkForNull() && fieldValue == null) {
                // ✅ Restrictions.isNull() → cb.isNull()
                predicates.add(cb.isNull(root.get(fieldName)));

            } else if (fieldValue instanceof String) {
                // ✅ Restrictions.eq().ignoreCase() → cb.equal on lowercased values
                predicates.add(cb.equal(
                        cb.lower(root.get(fieldName)),
                        ((String) fieldValue).toLowerCase()
                ));

            } else {
                // ✅ Restrictions.eq() → cb.equal()
                predicates.add(cb.equal(root.get(fieldName), fieldValue));
            }
        }

        // ✅ Restrictions.ne(unique.id(), id) — exclude current record
        if (id != null) {
            predicates.add(cb.notEqual(root.get(unique.id()), id));
        }

        // ✅ Projections.id() → select id field
        cq.select(root.get(unique.id()))
                .where(cb.and(predicates.toArray(new Predicate[0])));

        // ✅ setMaxResults(1).uniqueResult() → setMaxResults(1).getSingleResult()
        List<Object> result = entityManager.createQuery(cq)
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty();  // ✅ null check → isEmpty()
    }

}
