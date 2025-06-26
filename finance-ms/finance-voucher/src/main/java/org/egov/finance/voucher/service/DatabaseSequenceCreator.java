package org.egov.finance.voucher.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import static java.lang.String.format;

@Component
public class DatabaseSequenceCreator {

	private static final String CREATE_SEQ_QUERY = "CREATE SEQUENCE IF NOT EXISTS %s";

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createSequence(String sequenceName) {
		// Sanitize the name to avoid injection if needed
		String safeSequenceName = sequenceName.replaceAll("[^a-zA-Z0-9_]", "_");
		String query = String.format(CREATE_SEQ_QUERY, safeSequenceName);
		entityManager.createNativeQuery(query).executeUpdate();
	}

}
