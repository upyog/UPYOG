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

package org.egov.infstr.services;

import java.io.Serializable;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path.Node;

import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.models.BaseModel;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Order;
import org.hibernate.query.Query;



import org.hibernate.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Old persistence service
 *
 * @deprecated use Repositories
 **/
@Transactional(readOnly = true)
@Deprecated
public class PersistenceService<T, ID extends Serializable> {
	private static final Logger LOG = LoggerFactory.getLogger(PersistenceService.class);

	@PersistenceContext
	protected EntityManager entityManager;

	private Class<T> type;

	@Autowired
	@Qualifier("entityValidator")
	private LocalValidatorFactoryBean entityValidator;

	public PersistenceService(final Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return this.type;
	}

	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	public void validate(final T model) {
		final List<ValidationError> errors = this.validateModel(model);
		if (!errors.isEmpty())
			throw new ValidationException(errors);
	}

	public List<ValidationError> validateModel(final T model) {
		LOG.debug("Validating Model");
		final List<ValidationError> errors = new ArrayList<>();
		if (model == null) {
			errors.add(new ValidationError("", "model.null"));
			return errors;
		}
		final Set<ConstraintViolation<T>> constraintViolations = entityValidator.validate(model);
		for (final ConstraintViolation<T> constraintViolation : constraintViolations) {
			final Iterator<Node> nodes = constraintViolation.getPropertyPath().iterator();
			while (nodes.hasNext())
				errors.add(new ValidationError(nodes.next().getName(), constraintViolation.getMessage()));
		}
		if (model instanceof BaseModel) {
			final BaseModel basemodel = (BaseModel) model;
			final List<ValidationError> dependentValMessages = basemodel.validate();
			if (dependentValMessages != null)
				errors.addAll(dependentValMessages);
		}
		return errors;
	}

	public T find(final String query, final Object... params) {
		final List<T> results = findAllBy(query, params);
		return results.isEmpty() ? null : results.get(0);
	}

	public T find(final String query) {
		final List<T> results = getQueryWithParams(query).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	protected T findById(final ID id) {
		return id == null ? null : getSession().get(this.type, id);
	}

	public List<T> findAllBy(final String query, final Object... params) {
		return getQueryWithParams(query, params).getResultList();
	}

	/**
	 * @param query
	 * @param pageNumber
	 *            used to determine the offset from which to return the results
	 * @param pageSize
	 *            Number of records to be returned in the page. If null then all
	 *            records that match query are returned
	 * @param params
	 * @return
	 */
	public Page findPageBy(final String query, final Integer pageNumber, final Integer pageSize,
	                       final Object... params) {
		final TypedQuery<T> q = getQueryWithParams(query, params);
		return new Page(q, pageNumber, pageSize, 0);
	}

	private TypedQuery<T> getQueryWithParams(final String query, final Object... params) {
		final TypedQuery<T> q = entityManager.createQuery(query, this.type);
		int index = 0;
		for (final Object param : params) {
			q.setParameter(index, param);
			index++;
		}
		return q;
	}

	public List<T> findAllByNamedQuery(final String namedQuery, final Object... params) {
		return getNamedQueryWithParams(namedQuery, params).getResultList();
	}

	/**
	 * @param namedQuery
	 * @param pageNumber
	 *            used to determine the offset from which to return the results
	 * @param pageSize
	 *            Number of records to be returned in the page. If null then all
	 *            records that match query are returned
	 * @param params
	 * @return Page instance that can be used to implement pagination
	 */
	public Page findPageByNamedQuery(final String namedQuery, final Integer pageNumber, final Integer pageSize,
	                                 final Object... params) {
		final TypedQuery<T> q = getNamedQueryWithParams(namedQuery, params);
		return new Page(q, pageNumber, pageSize, 0);
	}

	private TypedQuery<T> getNamedQueryWithParams(final String namedQuery, final Object... params) {
		final TypedQuery<T> q = entityManager.createNamedQuery(namedQuery, this.type);
		int index = 0;
		for (final Object param : params) {
			if (param instanceof Collection)
				q.setParameter(String.valueOf("param_" + index), (Collection) param);
			else
				q.setParameter(index, param);
			index++;
		}
		return q;
	}

	public T findByNamedQuery(final String namedQuery, final Object... params) {
		final List<T> results = findAllByNamedQuery(namedQuery, params);
		return results.isEmpty() ? null : results.get(0);
	}

	@Transactional
	public T persist(final T model) {
		validate(model);
		getSession().saveOrUpdate(model);
		return model;
	}

	@Transactional
	public T merge(final T model) {
		validate(model);
		return (T) getSession().merge(model);
	}

	@Transactional
	public T create(final T entity) {
		validate(entity);
		final Long id = (Long) getSession().save(entity);
		return getSession().load(this.type, id);
	}

	public T load(final Serializable id, Class cls) {
		return (T) getSession().load(cls, id);
	}

	@Transactional
	public void delete(final T entity) {
		getSession().delete(entity);
	}

	public List<T> findAll() {
		final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(this.type);
		final Root<T> root = cq.from(this.type);
		cq.select(root);
		return entityManager.createQuery(cq).getResultList();
	}

	public List<T> findByExample(final T exampleT) {
		final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(this.type);
		final Root<T> root = cq.from(this.type);
		final List<Predicate> predicates = buildExamplePredicates(cb, root, exampleT);
		cq.select(root).where(predicates.toArray(new Predicate[0]));
		return entityManager.createQuery(cq).getResultList();
	}

	public T findById(final ID id, final boolean lock) {
		return findById(id);
	}

	public T findByIdWithJoinFetch(final ID id, final String joinFetchPropertyName) {
		final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(this.type);
		final Root<T> root = cq.from(this.type);
		root.fetch(joinFetchPropertyName, JoinType.LEFT);
		cq.select(root).where(cb.equal(root.get("id"), id));
		final List<T> results = entityManager.createQuery(cq).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	@Transactional
	public T update(final T entity) {
		validate(entity);
		getSession().update(entity);
		return entity;
	}

	public List<T> findAll(final String... orderByFields) {
		final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(this.type);
		final Root<T> root = cq.from(this.type);
		cq.select(root);
		final List<Order> orders = new ArrayList<>();
		for (final String orderBy : orderByFields)
			orders.add(cb.asc(root.get(orderBy)));
		if (!orders.isEmpty())
			cq.orderBy(orders);
		return entityManager.createQuery(cq).getResultList();
	}

	public String getNamedQuery(final String namedQuery) {
		return entityManager.createNamedQuery(namedQuery).unwrap(org.hibernate.query.Query.class).getQueryString();
	}

	public void addIndexparams(final Map<String, List> indexparams, final String key, final Object... values) {
		final List objparams = new ArrayList();
		for (final Object value : values)
			objparams.add(value);
		indexparams.put(key, objparams);
	}

	private List<Predicate> buildExamplePredicates(final CriteriaBuilder cb, final Root<T> root, final T exampleT) {
		final List<Predicate> predicates = new ArrayList<>();
		try {
			final BeanInfo beanInfo = Introspector.getBeanInfo(exampleT.getClass(), Object.class);
			for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				if (descriptor.getReadMethod() == null)
					continue;
				final Object value = descriptor.getReadMethod().invoke(exampleT);
				if (value != null)
					predicates.add(cb.equal(root.get(descriptor.getName()), value));
			}
		} catch (final IntrospectionException | ReflectiveOperationException e) {
			throw new IllegalStateException("Failed to build example query for " + this.type.getName(), e);
		}
		return predicates;
	}

	public void addFilterCriteriaForObject(final Map<String, List> params, final CriteriaQuery<T> cq,
	                                       final Root<T> root, final CriteriaBuilder cb, final String... orderbyFields) {
		final List<Predicate> predicates = new ArrayList<>();
		for (final Map.Entry<String, List> entry : params.entrySet())
			if (entry.getKey().contains("date") || entry.getKey().contains("Date"))
				predicates.add(cb.between(root.get(entry.getKey()), (Comparable) entry.getValue().get(0),
						(Comparable) entry.getValue().get(1)));
			else
				predicates.add(cb.equal(root.get(entry.getKey()), entry.getValue().get(0)));
		cq.where(predicates.toArray(new Predicate[0]));
		final List<Order> orders = new ArrayList<>();
		for (final String orderBy : orderbyFields)
			orders.add(cb.asc(root.get(orderBy)));
		if (!orders.isEmpty())
			cq.orderBy(orders);
	}

	/**
	 * This method is a workaround to apply auditing field values for JPA entity
	 * when JPA mixed with hbm based entities, this will be removed in future
	 * once modules are migrated to JPA annotation.
	 **/
	public void applyAuditing(AbstractAuditable auditable) {
		Date currentDate = new Date();
		if (auditable.isNew()) {
			auditable.setCreatedBy(ApplicationThreadLocals.getUserId());
			auditable.setCreatedDate(currentDate);
		}
		auditable.setLastModifiedBy(ApplicationThreadLocals.getUserId());
		auditable.setLastModifiedDate(currentDate);
	}

	public void applyAuditing(BaseModel baseModel) {
		Date currentDate = new Date();
		if (baseModel.getId() == null) {
			baseModel.setCreatedBy(ApplicationThreadLocals.getUserId());
			baseModel.setCreatedDate(currentDate);
		}
		baseModel.setModifiedBy(ApplicationThreadLocals.getUserId());
		baseModel.setModifiedDate(currentDate);
	}

	public jakarta.persistence.Query populateQueryWithParams(final jakarta.persistence.Query query, final Map<String, Object> params) {

		for (Entry<String, Object> entry : params.entrySet()) {
			if (entry.getValue() instanceof Collection)
				query.setParameter(entry.getKey(), entry.getValue());
			else
				query.setParameter(entry.getKey(), entry.getValue());
		}
		return query;
	}

}
