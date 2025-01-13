package org.egov.pg.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.pg.models.TransactionDetails;
import org.egov.pg.web.models.TransactionDetailsCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class TransactionDetailsRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TransactionDetailsRowMapper rowMapper;

	public List<TransactionDetails> fetchTransactionDetails(TransactionDetailsCriteria transactionDetailsCriteria) {
		List<Object> params = new ArrayList<>();
		String query = TransactionDetailsQueryBuilder.getTransactionDetails(transactionDetailsCriteria, params);
		log.debug(query);
		return jdbcTemplate.query(query, params.toArray(), rowMapper);
	}

}
