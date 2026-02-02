package org.egov.finance.voucher.daoimpl;

import org.egov.finance.voucher.dao.GeneralLedgerDAO;
import org.egov.finance.voucher.entity.CGeneralLedger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class GeneralLedgerHibernateDAO implements GeneralLedgerDAO {
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public CGeneralLedger create(final CGeneralLedger entity) {
		getCurrentSession().persist(entity);
		return entity;
	}

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	
}
