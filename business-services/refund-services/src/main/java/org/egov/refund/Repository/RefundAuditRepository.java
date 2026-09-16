package org.egov.refund.Repository;

import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.kafka.producer.Producer;
import org.egov.refund.model.RefundAudit;
import org.egov.refund.web.contracat.RefundAuditRequest;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RefundAuditRepository {

	private final Producer producer;
	private final ApplicationProperties properties;

	public RefundAuditRepository(Producer producer,ApplicationProperties properties) {
		this.producer = producer;
		this.properties = properties;
	}

	public void create(RefundAudit audit) {
		log.info("Publishing audit create request create request. refundId={}, refundNo={}, status={}", audit.getRefundId(),
				audit.getRefundNo(), audit.getStatus());
        RefundAuditRequest request = new RefundAuditRequest(audit);
		producer.push(properties.getSaveAuditRefundTopic(), request);
		 log.info("Refund audit create request published successfully. auditId={}, refundId={}, topic={}",
		            audit.getId(),
		            audit.getRefundId(),
		            properties.getSaveAuditRefundTopic());
	}

}