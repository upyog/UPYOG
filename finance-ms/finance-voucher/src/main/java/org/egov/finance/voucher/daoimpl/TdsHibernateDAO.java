package org.egov.finance.voucher.daoimpl;

import java.util.Optional;

import org.egov.finance.voucher.entity.Recovery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(readOnly = true)
public class TdsHibernateDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	public Optional<Recovery> findActiveTdsByGlcodeId(Long glcodeId) {
		Recovery result = (Recovery) getCurrentSession()
				.createQuery("FROM Recovery tds WHERE tds.isactive = true AND tds.chartofaccounts.id = :glcodeId")
				.setParameter("glcodeId", glcodeId).uniqueResult();

		return Optional.ofNullable(result);
	}
}
