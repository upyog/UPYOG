package org.egov.pg.repository;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.pg.models.Refund;
import org.egov.pg.web.models.RefundCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RefundRepository {
	
	 private final JdbcTemplate jdbcTemplate;
	 private final RefundTransactionRowMapper rowMapper = new RefundTransactionRowMapper();
	 
	 @Autowired
	 public RefundRepository(JdbcTemplate jdbcTemplate) {
		 this.jdbcTemplate=jdbcTemplate;
	 }

	public List<Refund> fetchRefundTransactions(@Valid RefundCriteria refundCriteria) {
		List<Object> params = new ArrayList<>();
		String query = RefundTransactionQueryBuilder.getRefundSearchQueryByCreatedTimeRange(refundCriteria, params);
		return jdbcTemplate.query(query, params.toArray(), rowMapper);
	}

}
