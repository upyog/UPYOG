package org.hpud.service;

import org.hpud.entity.UmeedDashboardLogger;
import org.hpud.model.UmeedLogRequest;
import org.hpud.repository.UmeedDashboardLogRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UmeedDashboardLogService {

	private final UmeedDashboardLogRepository logRepository;
	
	private final ObjectMapper objectMapper;

	/**
	 * Save log entry into umeed_dashboard_log table
	 * @return 
	 */
	public UmeedDashboardLogger saveLog(UmeedLogRequest request) {
		try {

			JsonNode requestJson = objectMapper.valueToTree(request.getRequestPayload());
			JsonNode responseJson = objectMapper.valueToTree(request.getResponsePayload());
			UmeedDashboardLogger umeedLogger = new UmeedDashboardLogger();
			umeedLogger.setDate(request.getDate());
			umeedLogger.setServiceType(request.getServiceType());
			umeedLogger.setRequestPayload(requestJson);
			umeedLogger.setResponsePayload(responseJson);
			umeedLogger.setCreatedTime(System.currentTimeMillis());
			return  logRepository.save(umeedLogger);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
