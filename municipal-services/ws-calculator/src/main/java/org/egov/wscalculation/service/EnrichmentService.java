package org.egov.wscalculation.service;


import static org.egov.wscalculation.constants.WSCalculationConstant.APP_CREATED_DATE;
import static org.egov.wscalculation.constants.WSCalculationConstant.FINAL_CONNECTION_STATES;
import static org.egov.wscalculation.constants.WSCalculationConstant.MODIFIED_FINAL_STATE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.egov.common.contract.request.RequestInfo;
import org.egov.wscalculation.web.models.AuditDetails;
import org.egov.wscalculation.web.models.Connection;
import org.egov.wscalculation.web.models.MeterReading;
import org.egov.wscalculation.web.models.WaterConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EnrichmentService {

	/**
	 * Enriches the incoming createRequest
	 * 
	 * @param meterConnectionRequest The create request for the meter reading
	 */
	@Autowired
	private ObjectMapper mapper;

	public void enrichMeterReadingRequest(RequestInfo requestInfo, MeterReading meterReading) {
		AuditDetails auditDetails = getAuditDetails(requestInfo.getUserInfo().getUuid(),
				true);
		meterReading.setId(UUID.randomUUID().toString());
		if (meterReading.getLastReadingDate() == null
				|| meterReading.getLastReadingDate() == 0) {
			Long lastReadingDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30);
			meterReading.setLastReadingDate(lastReadingDate);
		}
		meterReading.setAuditDetails(auditDetails);
	}
	
	/**
     * Method to return auditDetails for create/update flows
     *
     * @param by - UUID of the User
     * @param isCreate - TRUE in case of create scenario and FALSE for modify scenario.
     * @return AuditDetails
     */
    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if(isCreate)
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        else
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }

	public List<WaterConnection> filterConnections(List<WaterConnection> connectionList) {
		HashMap<String, Connection> connectionHashMap = new HashMap<>();
		connectionList.forEach(connection -> {
			if (!StringUtils.isEmpty(connection.getConnectionNo())) {
				if (connectionHashMap.get(connection.getConnectionNo()) == null
						&& FINAL_CONNECTION_STATES.contains(connection.getApplicationStatus())) {
					connectionHashMap.put(connection.getConnectionNo(), connection);
				} else if (connectionHashMap.get(connection.getConnectionNo()) != null
						&& FINAL_CONNECTION_STATES.contains(connection.getApplicationStatus())) {
					if (connectionHashMap.get(connection.getConnectionNo()).getApplicationStatus()
							.equals(connection.getApplicationStatus())) {
						HashMap additionalDetail1 = new HashMap<>();
						HashMap additionalDetail2 = new HashMap<>();
						additionalDetail1 = mapper.convertValue(
								connectionHashMap.get(connection.getConnectionNo()).getAdditionalDetails(),
								HashMap.class);
						additionalDetail2 = mapper.convertValue(connection.getAdditionalDetails(), HashMap.class);
						BigDecimal creationDate1 = (BigDecimal) additionalDetail1.get(APP_CREATED_DATE);
						BigDecimal creationDate2 = (BigDecimal) additionalDetail2.get(APP_CREATED_DATE);
						if (creationDate1.compareTo(creationDate2) == -1) {
							connectionHashMap.put(connection.getConnectionNo(), connection);
						}
					} else if (connection.getApplicationStatus().equals(MODIFIED_FINAL_STATE)) {
							connectionHashMap.put(connection.getConnectionNo(), connection);
						}
				}
			}
		});
		return new ArrayList(connectionHashMap.values());
	}

}
