package org.egov.finance.voucher.daoimpl;

import org.egov.finance.voucher.entity.AccountDetailType;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class AccountdetailtypeHibernateDAO {

	@PersistenceContext
	private EntityManager entityManager;

	protected Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	public AccountDetailType findById(Number id, boolean lock) {
		return (AccountDetailType) getCurrentSession().load(AccountDetailType.class, id);
	}

}
