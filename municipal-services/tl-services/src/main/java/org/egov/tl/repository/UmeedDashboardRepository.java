package org.egov.tl.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.tl.repository.builder.UmeedDashboardQueryBuilder;
import org.egov.tl.web.models.niuadata.Bucket;
import org.egov.tl.web.models.niuadata.DataItem;
import org.egov.tl.web.models.niuadata.GroupedData;
import org.egov.tl.web.models.niuadata.Metrics;
import org.egov.tl.web.models.niuadata.StatusCounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class UmeedDashboardRepository {

	@Autowired
	private UmeedDashboardQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<DataItem> getUniqueWards(String stringDate) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getUniqueWardsSearchQuery(stringDate, preparedStmtList);

		return jdbcTemplate.query(query, preparedStmtList.toArray(), (rs, rowNum) -> {
			DataItem dataItem = new DataItem();
			dataItem.setWard(rs.getString("ward"));
			dataItem.setUlb(rs.getString("ulb"));
			dataItem.setRegion(rs.getString("region"));
			return dataItem;
		});
	}

	public Metrics getDataMetrics(String stringDate, String wardName, int slaDays) {
		List<Object> preparedStmtList = new ArrayList<>();

		String query = queryBuilder.getDataMetricsSearchQuery(stringDate, wardName, slaDays, preparedStmtList);

		return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), (rs, rowNum) -> {
			Metrics metrics = new Metrics();
			metrics.setTransactions(rs.getLong("transactions"));
			metrics.setTodaysLicenseIssuedWithinSLA(rs.getLong("todaysLicenseIssuedWithinSLA"));
			metrics.setTodaysApprovedApplications(rs.getLong("todaysApprovedApplications"));
			metrics.setPendingApplicationsBeyondTimeline(rs.getLong("pendingApplicationsBeyondTimeline"));
			metrics.setTodaysApprovedApplicationsWithinSLA(rs.getLong("todaysApprovedApplicationsWithinSLA"));
			double value = rs.getDouble("avgDaysForApplicationApproval");
			BigDecimal rounded = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
			metrics.setAvgDaysForApplicationApproval(rounded.doubleValue());
			metrics.setStipulatedDays(rs.getInt("StipulatedDays"));
			return metrics;
		});
	}

	public GroupedData getTodaysCollectionTradeTypeGroup(String stringDate, String wardName) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getTodaysCollectionSearchQuery(stringDate, wardName, preparedStmtList);

		List<Bucket> buckets = jdbcTemplate.query(query, preparedStmtList.toArray(), (rs, rowNum) -> {
			Bucket bucket = new Bucket();
			bucket.setName(rs.getString("tradeType"));
			bucket.setValue(rs.getBigDecimal("totalTxnAmount"));
			return bucket;
		});

		GroupedData todaysCollectionTradeTypeGroup = new GroupedData();
		todaysCollectionTradeTypeGroup.setGroupBy("tradeType");
		todaysCollectionTradeTypeGroup.setBuckets(buckets);

		return todaysCollectionTradeTypeGroup;
	}

	public Map<String, List<GroupedData>> getTodaysTradeLicensesAndApplicationsMoved(String stringDate,
			String wardName) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getTodaysTradeLicensesSearchQuery(stringDate, wardName, preparedStmtList);

		// Fetch all statuses from the query
		List<StatusCounts> statusCountsList = jdbcTemplate.query(query, preparedStmtList.toArray(), (rs, rowNum) -> {
			StatusCounts counts = new StatusCounts();
			counts.setStatus(rs.getString("status"));
			counts.setCreatedTodayCount(rs.getLong("created_today_count"));
			counts.setModifiedTodayCount(rs.getLong("modified_today_count"));
			return counts;
		});

		// Get unique status list dynamically from results
		List<String> allStatuses = statusCountsList.stream().map(StatusCounts::getStatus).distinct().sorted()
				.collect(Collectors.toList());

		// Map results to buckets for created_today_count
		List<Bucket> createdBuckets = allStatuses.stream().map(status -> {
			long value = statusCountsList.stream().filter(c -> status.equals(c.getStatus()))
					.mapToLong(StatusCounts::getCreatedTodayCount).findFirst().orElse(0L);
			return Bucket.builder().name(status).value(BigDecimal.valueOf(value)).build();
		}).collect(Collectors.toList());

		// Map results to buckets for modified_today_count
		List<Bucket> modifiedBuckets = allStatuses.stream().map(status -> {
			long value = statusCountsList.stream().filter(c -> status.equals(c.getStatus()))
					.mapToLong(StatusCounts::getModifiedTodayCount).findFirst().orElse(0L);
			return Bucket.builder().name(status).value(BigDecimal.valueOf(value)).build();
		}).collect(Collectors.toList());

		// Build grouped data objects
		GroupedData todaysTradeLicensesGroup = GroupedData.builder().groupBy("status").buckets(createdBuckets).build();

		GroupedData applicationsMovedTodayGroup = GroupedData.builder().groupBy("status").buckets(modifiedBuckets)
				.build();

		// Return as map
		Map<String, List<GroupedData>> result = new HashMap<>();
		result.put("todaysTradeLicenses", Collections.singletonList(todaysTradeLicensesGroup));
		result.put("applicationsMovedToday", Collections.singletonList(applicationsMovedTodayGroup));

		return result;
	}

}
