/**
 * Reusable UPYOG external-integration audit.
 * <p>
 * Services wrap explicitly identified third-party inbound or outbound calls with
 * {@link org.upyog.externalaudit.service.ExternalApiAuditLogger}. Events go to Kafka
 * and egov-persister upserts {@code ug_external_api_*} tables. This is not a global
 * HTTP interceptor and must not be used for internal UPYOG APIs.
 * </p>
 */
package org.upyog.externalaudit;
