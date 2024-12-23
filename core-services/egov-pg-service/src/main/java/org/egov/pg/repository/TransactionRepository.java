package org.egov.pg.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.pg.models.Transaction;
import org.egov.pg.web.models.TransactionCriteria;
import org.egov.pg.web.models.TransactionCriteriaV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final TransactionRowMapper rowMapper = new TransactionRowMapper();

    @Autowired
    TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transaction> fetchTransactions(TransactionCriteria transactionCriteria) {
        List<Object> params = new ArrayList<>();
        String query = TransactionQueryBuilder.getPaymentSearchQueryByCreatedTimeRange(transactionCriteria, params);
        log.debug(query);
        return jdbcTemplate.query(query, params.toArray(), rowMapper);
    }
    
	public List<Transaction> fetchTransactions(TransactionCriteriaV2 transactionCriteriaV2) {
		List<Object> params = new ArrayList<>();
		String query = TransactionQueryBuilder.getPaymentSearchQueryByCreatedTimeRange(transactionCriteriaV2, params);
		log.debug(query);
		return jdbcTemplate.query(query, params.toArray(), rowMapper);
	}

    public List<Transaction> fetchTransactionsByTimeRange(TransactionCriteria transactionCriteria, Long startTime, Long endTime) {
        List<Object> params = new ArrayList<>();
        String query = TransactionQueryBuilder.getPaymentSearchQueryByCreatedTimeRange(transactionCriteria, startTime, endTime, params);
        log.debug(query);
        return jdbcTemplate.query(query, params.toArray(), rowMapper);
    }

}
