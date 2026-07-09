package org.egov.swcalculation.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.swcalculation.config.SWCalculationConfiguration;
import org.egov.swcalculation.producer.SWCalculationProducer;
import org.egov.swcalculation.util.SWCalculationUtil;
import org.egov.swcalculation.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DemandSchedulerNotificationService {

	@Autowired
	private SWCalculationConfiguration configs;

	@Autowired
	private SWCalculationProducer swCalculationProducer;

	@Autowired
	private SWCalculationUtil utils;

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public void sendStartEmail(String tenantId, Long from, Long to, int totalCount, RequestInfo requestInfo) {
		if (configs.getIsMailEnabled() == null || !configs.getIsMailEnabled() || StringUtils.isEmpty(configs.getMailRecipients())) return;

		String localizationMessage = "";
		try {
			localizationMessage = utils.getLocalizationMessages(tenantId, requestInfo);
		} catch (Exception e) {
			log.warn("Failed to fetch localization messages", e);
		}

		String template = utils.getMessageTemplate("SW_DEMAND_GEN_START_EMAIL_TEMPLATE", localizationMessage);
		if (StringUtils.isEmpty(template)) {
			log.warn("⚠️ SW_DEMAND_GEN_START_EMAIL_TEMPLATE not found in localization service. Skipping start email.");
			return;
		}

		String formattedFrom = new SimpleDateFormat("dd-MMM-yyyy").format(new Date(from));
		String formattedTo = new SimpleDateFormat("dd-MMM-yyyy").format(new Date(to));

		String htmlBody = template
				.replace("{tenantId}", tenantId)
				.replace("{formattedFrom}", formattedFrom)
				.replace("{formattedTo}", formattedTo)
				.replace("{totalCount}", String.valueOf(totalCount));

		Email email = Email.builder()
				.emailTo(Arrays.stream(configs.getMailRecipients().split(",")).map(String::trim).collect(Collectors.toSet()))
				.subject("Sewerage Demand Generation Started - " + tenantId)
				.body(htmlBody)
				.isHTML(true)
				.build();
		swCalculationProducer.push(configs.getEmailNotifTopic(), EmailRequest.builder().email(email).build());
		log.info("📧 Start email notification pushed to Kafka for tenant: {}", tenantId);
	}

	public void sendCompletionEmail(String tenantId, Long from, Long to, List<String> allConnectionNos, long startTime, RequestInfo requestInfo) {
		if (configs.getIsMailEnabled() == null || !configs.getIsMailEnabled() || StringUtils.isEmpty(configs.getMailRecipients())) return;

		// Inline Poll until consumers finish processing
		long pollStartTime = System.currentTimeMillis();
		long totalCount = allConnectionNos.size();
		long successCount = 0;
		BigDecimal totalAmount = BigDecimal.ZERO;

		while (System.currentTimeMillis() - pollStartTime < 15 * 60 * 1000) { // 15 mins timeout
			try {
				Map<String, Object> stats = getBatchStats(tenantId, from, to);
				successCount = ((Number) stats.get("success_count")).longValue();
				totalAmount = new BigDecimal(stats.get("total_amount").toString());

				if (successCount >= totalCount) {
					log.info("🎯 All Sewerage connections processed successfully for tenant: {}", tenantId);
					break;
				}
			} catch (Exception e) {
				log.error("⚠️ Error fetching batch stats during polling: {}", e.getMessage());
			}
			try {
				Thread.sleep(10000); // Poll every 10 seconds
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				log.error("❌ Polling thread interrupted");
				break;
			}
		}

		long failureCount = Math.max(0, totalCount - successCount);
		boolean timedOut = (System.currentTimeMillis() - pollStartTime) >= 15 * 60 * 1000;

		// Query successes to find failed connection numbers
		List<String> successfulConnectionNos = getSuccessfulConnectionNos(tenantId, from, to);
		Set<String> successSet = new HashSet<>(successfulConnectionNos);

		List<String> failedConnectionNos = allConnectionNos.stream()
				.filter(conn -> !successSet.contains(conn))
				.collect(Collectors.toList());

		// Query reasons for failures from error log if any
		Map<String, String> failureReasons = getFailureReasons(tenantId, from, to);

		// Build failures HTML rows
		StringBuilder failedRowsBuilder = new StringBuilder();
		if (failedConnectionNos.isEmpty()) {
			failedRowsBuilder.append("<tr><td colspan=\"3\" style=\"color: #10b981; font-weight: bold; text-align: center;\">🎉 All connections processed successfully! No failures reported.</td></tr>");
		} else {
			int count = 1;
			for (String failedConn : failedConnectionNos) {
				if (count > 50) {
					failedRowsBuilder.append("<tr><td colspan=\"3\" style=\"text-align: center; font-style: italic; color: #4b5563;\">... and ")
							.append(failedConnectionNos.size() - 50)
							.append(" more failed connections.</td></tr>");
					break;
				}
				String reason = failureReasons.getOrDefault(failedConn, "Calculation failed / check server logs");
				failedRowsBuilder.append("<tr>")
						.append("<td>").append(count++).append("</td>")
						.append("<td>").append(failedConn).append("</td>")
						.append("<td style=\"color: #ef4444;\">").append(reason).append("</td>")
						.append("</tr>");
			}
		}

		String localizationMessage = "";
		try {
			localizationMessage = utils.getLocalizationMessages(tenantId, requestInfo);
		} catch (Exception e) {
			log.warn("Failed to fetch localization messages", e);
		}

		String template = utils.getMessageTemplate("SW_DEMAND_GEN_COMPLETION_EMAIL_TEMPLATE", localizationMessage);
		if (StringUtils.isEmpty(template)) {
			log.warn("⚠️ SW_DEMAND_GEN_COMPLETION_EMAIL_TEMPLATE not found in localization service. Skipping completion email.");
			return;
		}

		Date fromDate = new Date(from);
		String billingCycle = new SimpleDateFormat("MMMM yyyy").format(fromDate);

		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int year = cal.get(Calendar.YEAR);
		String financialYear = year + "-" + ((year + 1) % 100);

		String generatedOn = new SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(new Date(startTime));
		String completedOn = new SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(new Date());

		long durationMs = System.currentTimeMillis() - startTime;
		long durationMin = (durationMs / 1000) / 60;
		long durationSec = (durationMs / 1000) % 60;
		String duration = durationMin + "m " + durationSec + "s";

		String statusLabel = timedOut ? "COMPLETED (TIMEOUT)" : "SUCCESS";
		String statusColor = timedOut ? "#ef4444" : "#10b981";

		String htmlBody = template
				.replace("{tenantId}", tenantId)
				.replace("{billingCycle}", billingCycle)
				.replace("{financialYear}", financialYear)
				.replace("{generatedOn}", generatedOn)
				.replace("{completedOn}", completedOn)
				.replace("{duration}", duration)
				.replace("{statusLabel}", statusLabel)
				.replace("{statusColor}", statusColor)
				.replace("{totalCount}", String.valueOf(totalCount))
				.replace("{successCount}", String.valueOf(successCount))
				.replace("{failureCount}", String.valueOf(failureCount))
				.replace("{totalAmount}", totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString())
				.replace("{failedConnectionsRows}", failedRowsBuilder.toString());

		Email email = Email.builder()
				.emailTo(Arrays.stream(configs.getMailRecipients().split(",")).map(String::trim).collect(Collectors.toSet()))
				.subject("Sewerage Demand Generation Completed - " + tenantId)
				.body(htmlBody)
				.isHTML(true)
				.build();
		swCalculationProducer.push(configs.getEmailNotifTopic(), EmailRequest.builder().email(email).build());
		log.info("📧 Completion email notification pushed to Kafka for tenant: {}", tenantId);
	}

	private Map<String, Object> getBatchStats(String tenantId, Long from, Long to) {
		String statsQuery = "SELECT COUNT(*) as success_count, COALESCE(SUM(taxamount), 0) as total_amount " +
				"FROM eg_sw_batch_connection_log " +
				"WHERE tenantid = :tenantId AND taxperiodfrom = :from AND taxperiodto = :to";

		Map<String, Object> params = new HashMap<>();
		params.put("tenantId", tenantId);
		params.put("from", from);
		params.put("to", to);

		return jdbcTemplate.queryForMap(statsQuery, params);
	}

	private List<String> getSuccessfulConnectionNos(String tenantId, Long from, Long to) {
		String query = "SELECT connectionno FROM eg_sw_batch_connection_log " +
				"WHERE tenantid = :tenantId AND taxperiodfrom = :from AND taxperiodto = :to";

		Map<String, Object> params = new HashMap<>();
		params.put("tenantId", tenantId);
		params.put("from", from);
		params.put("to", to);

		try {
			return jdbcTemplate.queryForList(query, params, String.class);
		} catch (Exception e) {
			log.warn("⚠️ Failed to query successful connections list: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	private Map<String, String> getFailureReasons(String tenantId, Long from, Long to) {
		Map<String, String> results = new HashMap<>();
		// Try to query eg_sw_demand_generation_error if it exists
		String query = "SELECT connectionno, errormessage FROM eg_sw_demand_generation_error " +
				"WHERE tenantid = :tenantId AND fromdate = :from AND todate = :to";

		Map<String, Object> params = new HashMap<>();
		params.put("tenantId", tenantId);
		params.put("from", from);
		params.put("to", to);

		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, params);
			for (Map<String, Object> row : rows) {
				String conn = (String) row.get("connectionno");
				String msg = (String) row.get("errormessage");
				if (conn != null) {
					results.put(conn, msg != null ? msg : "Calculation failed");
				}
			}
		} catch (Exception e) {
			log.info("ℹ️ Table eg_sw_demand_generation_error not available, using default fallback messages. Details: {}", e.getMessage());
		}
		return results;
	}
}
