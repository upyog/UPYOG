package org.egov.finance.voucher.service;

import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.repository.VoucherRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class VoucherService {

	@Autowired
	private VoucherRepository voucherRepository;

	@PersistenceContext
	protected EntityManager entityManager;

	// No need to manually apply auditing
	@Transactional
	public void persist(CVoucherHeader voucher) {
		voucherRepository.save(voucher);
	}

	@Transactional
	public void update(CVoucherHeader voucher) {
		voucherRepository.save(voucher);
	}

	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

}
