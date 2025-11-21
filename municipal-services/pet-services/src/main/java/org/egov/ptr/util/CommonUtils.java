package org.egov.ptr.util;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.AuditDetails;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.ptr.web.contracts.IdGenerationRequest;
import org.egov.ptr.web.contracts.IdGenerationResponse;
import org.egov.ptr.web.contracts.IdRequest;
import org.egov.ptr.web.contracts.IdResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;

@Getter
@Component
public class CommonUtils {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PetConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	/**
	 * Method to return auditDetails for create/update flows
	 *
	 * @param by
	 * @param isCreate
	 * @return AuditDetails
	 */
	public AuditDetails getAuditDetails(String by, Boolean isCreate) {

		Long time = System.currentTimeMillis();

		if (isCreate)
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	/**************************** ID GEN ****************************/

	/**
	 * Returns a list of numbers generated from idgen
	 * 
	 * @param requestInfo RequestInfo from the request
	 * @param tenantId    tenantId of the city
	 * @param idName      code of the field defined in application properties for
	 *                    which ids are generated for
	 * @param idformat    format in which ids are to be generated
	 * @param count       Number of ids to be generated
	 * @return List of ids generated using idGen service
	 */
	public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {

		List<IdRequest> reqList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
		}

		IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo)
				.build();
		StringBuilder uri = new StringBuilder(configs.getIdGenHost()).append(configs.getIdGenPath());
		IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request).get(),
				IdGenerationResponse.class);

		List<IdResponse> idResponses = response.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	/**************************
	 * JSON MERGE UTILITY METHODS
	 ****************************************/

	/**
	 * Method to merge additional details during update
	 * 
	 * @param mainNode
	 * @param updateNode
	 * @return
	 */
	public JsonNode jsonMerge(JsonNode mainNode, JsonNode updateNode) {

		if (isNull(mainNode) || mainNode.isNull())
			return updateNode;
		if (isNull(updateNode) || updateNode.isNull())
			return mainNode;

		Iterator<String> fieldNames = updateNode.fieldNames();
		while (fieldNames.hasNext()) {

			String fieldName = fieldNames.next();
			JsonNode jsonNode = mainNode.get(fieldName);
			// if field exists and is an embedded object
			if (jsonNode != null && jsonNode.isObject()) {
				jsonMerge(jsonNode, updateNode.get(fieldName));
			} else {
				if (mainNode instanceof ObjectNode) {
					// Overwrite field
					JsonNode value = updateNode.get(fieldName);
					((ObjectNode) mainNode).set(fieldName, value);
				}
			}

		}
		return mainNode;
	}

	public long calculateNextMarch31InEpoch() {
		LocalDate today = LocalDate.now();
		LocalDate nextMarch31 = LocalDate.of(today.getYear(), Month.MARCH, 31);
		if (today.isAfter(nextMarch31)) {
			nextMarch31 = nextMarch31.plusYears(1);
		}
		LocalDateTime nextMarch31At1159PM = LocalDateTime.of(nextMarch31, LocalTime.of(23, 59));
		return nextMarch31At1159PM.atZone(ZoneId.systemDefault()).toEpochSecond();
	}

	public long getTodaysEpoch() {
		LocalDate today = LocalDate.now();
		LocalDateTime startOfDay = today.atStartOfDay();
		return startOfDay.atZone(ZoneId.systemDefault()).toEpochSecond();
	}

	public static long getFinancialYearStart() {

		YearMonth currentYearMonth = YearMonth.now();
		int year = currentYearMonth.getYear();
		int month = currentYearMonth.getMonthValue();

		// If current month is Jan-March, start year should be last year
		if (month < Month.APRIL.getValue()) {
			year -= 1;
		}

		LocalDateTime startOfYear = LocalDateTime.of(year, Month.APRIL, 1, 0, 0, 0);
		return startOfYear.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

	}

	public static long getFinancialYearEnd() {

		YearMonth currentYearMonth = YearMonth.now();
		int year = currentYearMonth.getYear();
		int month = currentYearMonth.getMonthValue();

		// If current month is Jan-March, end year should be current year
		if (month < Month.APRIL.getValue()) {
			year -= 1;
		}

		LocalDateTime endOfYear = LocalDateTime.of(year + 1, Month.MARCH, 31, 23, 59, 59, 999000000);
		return endOfYear.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

	}

	public String getFinancialYearEndDate() {

		LocalDate today = LocalDate.now();
		int year = today.getMonthValue() > Month.MARCH.getValue() ? today.getYear() + 1 : today.getYear();
		LocalDate financialYearEndDate = LocalDate.of(year, Month.MARCH, 31);
		return financialYearEndDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

	}

}
