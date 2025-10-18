package org.egov.finance.voucher.daoimpl;

import java.util.Date;

import org.egov.finance.voucher.dao.FiscalPeriodDAO;
import org.egov.finance.voucher.entity.FiscalPeriod;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class FiscalPeriodHibernateDAO implements FiscalPeriodDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	@Override
	public String getFiscalPeriodIds(String financialYearId) {
		// TODO Auto-generated method stub
		return null;
	}

	public FiscalPeriod getFiscalPeriodByDate(Date voucherDate) {
		TypedQuery<FiscalPeriod> query = entityManager.createQuery(
				"from FiscalPeriod fp where :voucherDate between fp.startingDate and fp.endingDate",
				FiscalPeriod.class);
		query.setParameter("voucherDate", voucherDate);
		return query.getSingleResult();
	}

}
