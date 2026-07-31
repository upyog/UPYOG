package org.upyog.dashboard.extractor.impl;

import org.upyog.dashboard.config.DashboardProperties;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.pt.dto.PTCollectionDTO;
import org.upyog.dashboard.pt.dto.PTAggregatedData;
import org.upyog.dashboard.pt.dto.PTDTO;
import org.upyog.dashboard.pt.mapper.PTRowmapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Property Tax (PT) implementation of {@link ModuleExtractor} which extracts
 * raw database metrics and maps them into {@link PTDTO}.
 */
@Slf4j
@Component
public class PtModuleExtractor implements ModuleExtractor<PTDTO> {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private SchemaMappingConfig schemaMappingConfig;

	@Autowired
	private DashboardProperties dashboardProperties;

	private String ulb;
	private String ward;
	private String region;
	private String state;
	private String dbTenantId;
	private int dbMaxAttempts;
	private long dbBaseDelayMs;
	private long dbMaxDelayMs;

	@jakarta.annotation.PostConstruct
	public void init() {
		this.ulb = dashboardProperties.getMetricUlb();
		this.ward = dashboardProperties.getMetricWard();
		this.region = dashboardProperties.getMetricRegion();
		this.state = dashboardProperties.getMetricState();
		this.dbTenantId = dashboardProperties.getMetricUlb();
		this.dbMaxAttempts = dashboardProperties.getDbMaxAttempts();
		this.dbBaseDelayMs = dashboardProperties.getDbBaseDelayMs();
		this.dbMaxDelayMs = dashboardProperties.getDbMaxDelayMs();
	}

	@Override
	public Module getModule() {
		return Module.PT;
	}

	@Override
	public PTDTO extractData(LocalDate targetDate) {
		String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
		long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;
		String tenantId = dbTenantId;

		Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime, "tenantId", tenantId);

		SchemaMappingConfig.ModuleQueries ptQueries = schemaMappingConfig.getQueriesForModule(Module.PT);
		if (ptQueries == null) {
			throw new IllegalStateException("No query mapping found in schema-mapping.yml for module PT");
		}

		// DB CALL 1: Combined scalars + JSON arrays query
		PTAggregatedData combinedResult = executeQueryWithRetry(ptQueries.getCombinedMetricsQuery(), params);

		// DB CALL 2: Payment and tax account breakdown query
		List<PTCollectionDTO> collectionRows = executeQueryListWithRetry(ptQueries.getCollectionMetricsQuery(), params);

		return PTDTO.builder()
				.date(dateStr)
				.module(getModule().name())
				.ward(ward)
				.ulb(ulb)
				.region(region)
				.state(state)
				.combinedMetrics(combinedResult)
				.collectionMetrics(collectionRows)
				.build();
	}

	private PTAggregatedData executeQueryWithRetry(String query, Map<String, Object> params) {
		int attempt = 0;
		while (true) {
			attempt++;
			try {
				return namedParameterJdbcTemplate.queryForObject(query, params, PTRowmapper.COMBINED_ROW_MAPPER);
			} catch (Exception exception) {
				if (attempt >= dbMaxAttempts) {
					log.error("PtModuleExtractor | DB query failed after {} attempts.", attempt, exception);
					throw exception;
				}
				long backoff = calculateDbBackoffWithJitter(attempt);
				log.warn("PtModuleExtractor | DB query failed (attempt {}/{}). Retrying in {} ms. Error: {}", 
						attempt, dbMaxAttempts, backoff, exception.getMessage());
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("DB query retry interrupted", ie);
				}
			}
		}
	}

	private List<PTCollectionDTO> executeQueryListWithRetry(String query, Map<String, Object> params) {
		int attempt = 0;
		while (true) {
			attempt++;
			try {
				return namedParameterJdbcTemplate.query(query, params, PTRowmapper.COLLECTION_ROW_MAPPER);
			} catch (Exception exception) {
				if (attempt >= dbMaxAttempts) {
					log.error("PtModuleExtractor | DB query dataList failed after {} attempts.", attempt, exception);
					throw exception;
				}
				long backoff = calculateDbBackoffWithJitter(attempt);
				log.warn("PtModuleExtractor | DB query dataList failed (attempt {}/{}). Retrying in {} ms. Error: {}", 
						attempt, dbMaxAttempts, backoff, exception.getMessage());
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("DB query dataList retry interrupted", ie);
				}
			}
		}
	}

	private long calculateDbBackoffWithJitter(int attempt) {
		int power = Math.min(attempt - 1, 30);
		long expDelay = dbBaseDelayMs * (1L << power);
		if (expDelay < 0) {
			expDelay = dbMaxDelayMs;
		}
		long currentMaxDelay = Math.min(dbMaxDelayMs, expDelay);
		return java.util.concurrent.ThreadLocalRandom.current().nextLong(0, currentMaxDelay + 1);
	}
}
