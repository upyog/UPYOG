package org.egov.finance.voucher.daoimpl;

import org.egov.finance.voucher.entity.CGeneralLedgerDetail;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(readOnly = true)
public class GeneralledgerdetailHibernateDAO {

	@Transactional
	public CGeneralLedgerDetail update(final CGeneralLedgerDetail entity) {
		getCurrentSession().update(entity);
		return entity;
	}

	@Transactional
	public CGeneralLedgerDetail create(final CGeneralLedgerDetail entity) {
		getCurrentSession().persist(entity);
		return entity;
	}

	@Transactional
	public void delete(CGeneralLedgerDetail entity) {
		getCurrentSession().delete(entity);
	}

	public CGeneralLedgerDetail findById(Number id, boolean lock) {
		return (CGeneralLedgerDetail) getCurrentSession().load(CGeneralLedgerDetail.class, id);
	}

//	public List<CGeneralLedgerDetailModel> findAll() {
//		return (List<CGeneralLedgerDetailModel>) getCurrentSession().createCriteria(CGeneralLedgerDetailModel.class).list();
//	}

	@PersistenceContext
	private EntityManager entityManager;

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

}
