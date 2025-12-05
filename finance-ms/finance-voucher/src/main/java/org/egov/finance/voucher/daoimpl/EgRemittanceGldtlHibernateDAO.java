package org.egov.finance.voucher.daoimpl;

import java.util.List;

import org.egov.finance.voucher.model.EgRemittanceGldtl;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class EgRemittanceGldtlHibernateDAO {

	@Transactional
	public EgRemittanceGldtl update(final EgRemittanceGldtl entity) {
		getCurrentSession().update(entity);
		return entity;
	}

	@Transactional
	public EgRemittanceGldtl create(final EgRemittanceGldtl entity) {
		getCurrentSession().persist(entity);
		return entity;
	}

	@Transactional
	public void delete(EgRemittanceGldtl entity) {
		getCurrentSession().delete(entity);
	}

	public EgRemittanceGldtl findById(Number id, boolean lock) {
		return (EgRemittanceGldtl) getCurrentSession().load(EgRemittanceGldtl.class, id);
	}

//	    public List<EgRemittanceGldtl> findAll() {
//	        return (List<EgRemittanceGldtl>) getCurrentSession().createCriteria(EgRemittanceGldtl.class).list();
//	    }

	@PersistenceContext
	private EntityManager entityManager;

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

}
