package org.egov.finance.voucher.service;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component
public class DatabaseSequenceProvider {

	private static final String NEXT_SEQ_QUERY = "SELECT NEXTVAL (:sequenceName) AS NEXTVAL";

	@PersistenceContext
	private EntityManager entityManager;

//	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW, noRollbackFor = SQLGrammarException.class)
//	public Serializable getNextSequence(String sequenceName) throws SQLGrammarException {
//		Query query = entityManager.createNativeQuery(NEXT_SEQ_QUERY);
//		query.setParameter("sequenceName", sequenceName);
//		return (Serializable) query.getSingleResult();
//	}

	// REMOVE readOnly=true — nextval() increments the sequence (it's not read-only)
	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = SQLGrammarException.class)
	public Serializable getNextSequence(String sequenceName) throws SQLGrammarException {
		String sql = "SELECT NEXTVAL('" + sequenceName + "')";
		return (Serializable) entityManager.createNativeQuery(sql).getSingleResult();
	}

}
