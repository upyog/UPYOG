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
import org.egov.refund.web.contracat.RefundRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		log.info("Publishing refund save request. refundId={}, refundNo={}, tenantId={}", refund.getId(),
				refund.getRefundNo(), refund.getTenantId());

		RefundRequest request = RefundRequest.builder().refund(refund).build();
		producer.push(properties.getSaveRefundTopic(), request);
		log.info("Refund save request published successfully. refundId={}, topic={}", refund.getId(),
				properties.getSaveRefundTopic());
	}

	public void update(Refund refund) {
		log.info("Publishing refund update request. refundId={}, refundNo={}, status={}", refund.getId(),
				refund.getRefundNo(), refund.getStatus());
		RefundRequest request = RefundRequest.builder().refund(refund).build();
		producer.push(properties.getUpdateRefundTopic(), request);
		log.info("Refund update request published successfully. refundId={}, topic={}", refund.getId(),
				properties.getUpdateRefundTopic());
	}

	public Refund findById(UUID id) {
		log.debug("Fetching refund by id. refundId={}", id);
		return jdbcTemplate.query(refundQueryBuilder.getFindByIdQuery(), refundRowMapper, id).stream().findFirst()
				.orElse(null);
	}

	public Refund findByRefundNo(String refundNo) {
		log.debug("Fetching refund by refundNo={}", refundNo);
		return jdbcTemplate.query(refundQueryBuilder.getFindByRefundNoQuery(), refundRowMapper, refundNo).stream()
				.findFirst().orElse(null);
	}

	public List<Refund> search(RefundSearchCriteria criteria) {

		RefundSearchQuery searchQuery = refundQueryBuilder.buildSearchQuery(criteria);

		return jdbcTemplate.query(searchQuery.getQuery(), refundRowMapper, searchQuery.getParams());
	}

	public Refund findByGatwayRefundId(String refundId, String tenentId) {
		log.debug("Fetching refund by gatewayRefundId={}, tenantId={}", refundId, tenentId);
		return jdbcTemplate
				.query(refundQueryBuilder.getFindByGatwayRefundIdQuery(), refundRowMapper, refundId, tenentId).stream()
				.findFirst().orElse(null);
	}

	public void sendToFinanceComplete(Refund refund) {
		producer.push(properties.getEgovRefundFinancePaymentTopic(), refund);
		log.info("Refund published to finance successfully. refundId={}, topic={}", refund.getId(),
				properties.getEgovRefundFinancePaymentTopic());
	}
}