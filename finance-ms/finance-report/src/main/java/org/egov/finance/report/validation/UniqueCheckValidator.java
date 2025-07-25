
package org.egov.finance.report.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueCheckValidator implements ConstraintValidator<Unique, Object> {

	private Unique unique;

	@PersistenceContext
	private jakarta.persistence.EntityManager entityManager;

	@Override
	public void initialize(final Unique unique) {
		this.unique = unique;
	}

	@Override
	public boolean isValid(final Object arg0, final ConstraintValidatorContext constraintValidatorContext) {
		boolean isValid = true;
		try {
			final Number id = (Number) FieldUtils.readField(arg0, unique.id(), true);

			for (final String fieldName : unique.fields())
				if (!checkUnique(arg0, id, fieldName)) {
					isValid = false;
					if (unique.enableDfltMsg())
						constraintValidatorContext.buildConstraintViolationWithTemplate(unique.message())
								.addPropertyNode(fieldName).addConstraintViolation();

				}

		} catch (final IllegalAccessException e) {
			// throw new ApplicationRuntimeException("Error while validating unique key",
			// e);
		}
		return isValid;
	}

	private boolean checkUnique(final Object entity, final Number id, final String fieldName)
			throws IllegalAccessException {
		final Object fieldValue = FieldUtils.readField(entity, fieldName, true);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);

		Class<?> entityClass = unique.isSuperclass() ? entity.getClass().getSuperclass() : entity.getClass();
		Root<?> root = cq.from(entityClass);

		List<Predicate> predicates = new ArrayList<>();

		// Case-insensitive check for strings
		if (fieldValue instanceof String) {
			predicates.add(cb.equal(cb.lower(root.get(fieldName)), ((String) fieldValue).toLowerCase()));
		} else {
			predicates.add(cb.equal(root.get(fieldName), fieldValue));
		}

		// Exclude entity with same ID (e.g., on update)
		if (id != null) {
			predicates.add(cb.notEqual(root.get(unique.id()), id));
		}

		// Apply all predicates
		cq.select(cb.count(root)).where(cb.and(predicates.toArray(new Predicate[0])));

		Long count = entityManager.createQuery(cq).getSingleResult();

		return count == 0; // true = unique
	}

}
