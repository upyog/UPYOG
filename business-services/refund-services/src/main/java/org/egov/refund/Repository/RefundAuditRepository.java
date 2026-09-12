package org.egov.refund.Repository;

import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.kafka.producer.Producer;
import org.egov.refund.model.RefundAudit;
import org.springframework.stereotype.Repository;

@Repository
public class RefundAuditRepository {

	private final Producer producer;
	private final ApplicationProperties properties;

	public RefundAuditRepository(Producer producer,ApplicationProperties properties) {
		this.producer = producer;
		this.properties = properties;
	}

	public void create(RefundAudit audit) {
		producer.push(properties.getSaveAuditRefundTopic(), audit);

	}

}