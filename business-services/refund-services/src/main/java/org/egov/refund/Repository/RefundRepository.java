package org.egov.refund.Repository;

import java.util.List;
import java.util.UUID;

import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.kafka.producer.Producer;
import org.egov.refund.model.Refund;
import org.egov.refund.querybuilder.RefundQueryBuilder;
import org.egov.refund.querybuilder.RefundSearchCriteria;
import org.egov.refund.querybuilder.RefundSearchQuery;
import org.egov.refund.rowmapper.RefundRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefundRepository {

	private final JdbcTemplate jdbcTemplate;
	private final RefundQueryBuilder refundQueryBuilder;
	private final RefundRowMapper refundRowMapper;
	private final Producer producer;
	private final ApplicationProperties properties;

	public RefundRepository(JdbcTemplate jdbcTemplate, RefundQueryBuilder refundQueryBuilder,
			RefundRowMapper refundRowMapper, Producer producer, ApplicationProperties properties) {

		this.jdbcTemplate = jdbcTemplate;
		this.refundQueryBuilder = refundQueryBuilder;
		this.refundRowMapper = refundRowMapper;
		this.producer = producer;
		this.properties = properties;
	}

	public void save(Refund refund) {
		producer.push(properties.getSaveRefundTopic(), refund);
	}

	public void update(Refund refund) {
		producer.push(properties.getUpdateRefundTopic(), refund);
	}

	public Refund findById(UUID id) {

		return jdbcTemplate.query(refundQueryBuilder.getFindByIdQuery(), refundRowMapper, id).stream().findFirst()
				.orElse(null);
	}

	public Refund findByRefundNo(String refundNo) {

		return jdbcTemplate.query(refundQueryBuilder.getFindByRefundNoQuery(), refundRowMapper, refundNo).stream()
				.findFirst().orElse(null);
	}

	public List<Refund> search(RefundSearchCriteria criteria) {

		RefundSearchQuery searchQuery = refundQueryBuilder.buildSearchQuery(criteria);

		return jdbcTemplate.query(searchQuery.getQuery(), refundRowMapper, searchQuery.getParams());
	}

}