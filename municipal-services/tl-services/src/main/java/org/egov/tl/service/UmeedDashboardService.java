package org.egov.tl.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.UmeedDashboardRepository;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.contract.UmeedDashboardResponse;
import org.egov.tl.web.models.niuadata.Bucket;
import org.egov.tl.web.models.niuadata.DataItem;
import org.egov.tl.web.models.niuadata.GroupedData;
import org.egov.tl.web.models.niuadata.Metrics;
import org.egov.tl.web.models.niuadata.ULBMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UmeedDashboardService {

	@Autowired
	private TLConfiguration tlConfiguration;

	@Autowired
	private UmeedDashboardRepository umeedDashboardRepository;

	public UmeedDashboardResponse prepareDataMetrics(RequestInfoWrapper requestInfoWrapper) {

		int slaDays = Optional.ofNullable(tlConfiguration.getUmeedDashboardSlaDays()).orElse(7);

		// get yesterday's date
//		String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		String yesterday = "30-08-2025";

		// Define the month (August 2025)
		LocalDate startDate = LocalDate.of(2025, 8, 1);
		// LocalDate endDate = LocalDate.of(2025, 8, 3);
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

		List<DataItem> allProcessedItems = new ArrayList<>();

		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

			List<DataItem> dataItems = umeedDashboardRepository.getUniqueWards(formattedDate);

			if (CollectionUtils.isEmpty(dataItems)) {
				continue;
			}

			List<DataItem> processedItems = dataItems.stream()
					.map(dataItem -> buildDataItemMetrics(dataItem, formattedDate, slaDays))
					.collect(Collectors.toList());

			allProcessedItems.addAll(processedItems);

		}
		// List<DataItem> processedItems = dataItems.stream()
		// .map(dataItem -> buildDataItemMetrics(dataItem, yesterday,
		// slaDays)).collect(Collectors.toList());

		return UmeedDashboardResponse.builder().data(allProcessedItems).build();
	}

	private DataItem buildDataItemMetrics(DataItem dataItem, String date, int slaDays) {
		DataItem returnObj = DataItem.builder().date(date).module("TL").state("Himachal Pradesh")
				.ward(dataItem.getWard()).ulb(ULBMappings.getCode(dataItem.getUlb())).build(); // dataItem.setDate(date);
																								// //dataItem.setModule("TL");
		// dataItem.setState("Himachal Pradesh"); 
		// get ulb data mappings
		// //dataItem.setUlb(ULBMappings.getCode(dataItem.getUlb()));
		// TODO map ulb if required
		Metrics metrics = umeedDashboardRepository.getDataMetrics(date, returnObj.getWard(), slaDays);
		// Add today's collection data
		metrics.setTodaysCollection(buildTodaysCollection(date, returnObj.getWard()));
		// Add trade license and application moved data
		Map<String, List<GroupedData>> statusGroups = umeedDashboardRepository
				.getTodaysTradeLicensesAndApplicationsMoved(date, returnObj.getWard());
		metrics.setTodaysTradeLicenses(statusGroups.get("todaysTradeLicenses"));
		metrics.setApplicationsMovedToday(statusGroups.get("applicationsMovedToday")); // dataItem.setMetrics(metrics);
		returnObj.setMetrics(metrics);
		return returnObj;
	}
	

	private List<GroupedData> buildTodaysCollection(String date, String wardName) {
		GroupedData tradeTypeGroup = umeedDashboardRepository.getTodaysCollectionTradeTypeGroup(date, wardName);

		BigDecimal totalPayment = tradeTypeGroup.getBuckets().stream().map(Bucket::getValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		GroupedData paymentChannelGroup = GroupedData.builder().groupBy("paymentChannelType")
				.buckets(Collections.singletonList(Bucket.builder().name("Digital").value(totalPayment).build()))
				.build();

		return Arrays.asList(tradeTypeGroup, paymentChannelGroup);
	}

}
