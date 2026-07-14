package org.egov.custom.mapper.billing.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MeterReadingRowMapper implements ResultSetExtractor<List<Map<String, Object>>> {

    @Autowired
    private RestTemplate rest;

    @Value("${egov.user.contextpath}")
    private String userContext;

    @Value("${egov.user.searchpath}")
    private String userSearchPath;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException {

        Map<String, Map<String, Object>> connectionMap = new LinkedHashMap<>();
        Map<String, Map<String, Map<String, Object>>> readingIndexMap = new LinkedHashMap<>();
        Set<String> userIds = new HashSet<>();

        while (rs.next()) {

            String connectionNo = rs.getString("connectionno");

            Map<String, Object> connection = connectionMap.get(connectionNo);

            if (connection == null) {

                connection = new LinkedHashMap<>();

                connection.put("connectionNo", connectionNo);
                connection.put("propertyId", rs.getString("propertyid"));
                connection.put("applicationNo", rs.getString("applicationno"));
                connection.put("usageCategory", rs.getString("usagecategory"));

                connection.put("area", rs.getString("area"));
                connection.put("zoneCode", rs.getString("zonecode"));
                connection.put("zonename", rs.getString("zonename"));
                connection.put("blockCode", rs.getString("blockcode"));
                connection.put("blockname", rs.getString("blockname"));
                connection.put("localityCode", rs.getString("localitycode"));
                connection.put("localityname", rs.getString("localityname"));
                connection.put("groups", rs.getString("groups"));
                connection.put("landArea", rs.getString("landarea"));
                
                // Address
                Map<String, Object> address = new LinkedHashMap<>();
                address.put("doorNo", rs.getString("doorno"));
                address.put("street", rs.getString("street"));
                address.put("city", rs.getString("city"));
                address.put("pincode", rs.getString("pincode"));
                address.put("landmark", rs.getString("landmark"));

                connection.put("address", address);

                String ownerUserId = rs.getString("owner_userid");

                connection.put("_ownerUserId", ownerUserId);
                connection.put("ownerName", null);
                connection.put("mobileNumber", null);
                connection.put("guardianName", null);

                if (ownerUserId != null) {
                    userIds.add(ownerUserId);
                }

                Map<String, Object> additionalDetails = new LinkedHashMap<>();

                try {

                    String additionalDetailsJson =
                            rs.getString("additionaldetails");

                    if (additionalDetailsJson != null) {

                        JsonNode node =
                                objectMapper.readTree(additionalDetailsJson);

                        additionalDetails.put(
                                "dischargeConnection",
                                node.path("dischargeConnection").asBoolean(false));

                        additionalDetails.put(
                                "dischargeFee",
                                node.path("dischargeFee").isNull()
                                        ? null
                                        : node.path("dischargeFee").asText());

                        additionalDetails.put(
                                "waterSubUsageType",
                                node.path("waterSubUsageType").isMissingNode()
                                        ? null
                                        : node.path("waterSubUsageType").asText());
                    }

                } catch (Exception ex) {
                    log.error("Error parsing additional details", ex);
                }

                connection.put("additionalDetails", additionalDetails);

                connection.put("meterReadings",
                        new ArrayList<Map<String, Object>>());

                connectionMap.put(connectionNo, connection);
        		readingIndexMap.put(connectionNo, new LinkedHashMap<>());
            }

            Map<String, Object> reading = new LinkedHashMap<>();
			String readingId = rs.getString("id");
            reading.put("id", rs.getString("id"));
            reading.put("billingPeriod", rs.getString("billingperiod"));
            reading.put("meterStatus", rs.getString("meterstatus"));
            reading.put("lastReading", rs.getBigDecimal("lastreading"));
            reading.put("currentReading", rs.getBigDecimal("currentreading"));
            reading.put("lastReadingDate", rs.getObject("lastreadingdate"));
            reading.put("currentReadingDate", rs.getObject("currentreadingdate"));

			List<Map<String, Object>> meterReadings = (List<Map<String, Object>>) connection.get("meterReadings");
			Map<String, Map<String, Object>> readingIndex = readingIndexMap.get(connectionNo);

			// O(1) duplicate check via HashMap instead of linear stream scan
			Map<String, Object> existingReading = readingIndex.get(readingId);

			if (existingReading != null) {
				long existingDate = ((Number) existingReading.get("currentReadingDate")).longValue();
				long newDate = ((Number) reading.get("currentReadingDate")).longValue();

				if (newDate > existingDate) {
					meterReadings.remove(existingReading);
					meterReadings.add(reading);
					readingIndex.put(readingId, reading);
				}
			} else {
				meterReadings.add(reading);
				readingIndex.put(readingId, reading);
			}
		}

		// Sort once after all rows are processed (moved out of the loop)
		Comparator<Map<String, Object>> byDateDesc = Comparator
				.comparing(r -> ((Number) r.get("currentReadingDate")).longValue(), Comparator.reverseOrder());
		for (Map<String, Object> connection : connectionMap.values()) {
			List<Map<String, Object>> meterReadings = (List<Map<String, Object>>) connection.get("meterReadings");
			meterReadings.sort(byDateDesc);
		}

        assignUserDetails(connectionMap, userIds);

        return new ArrayList<>(connectionMap.values());
    }

    private void assignUserDetails(
            Map<String, Map<String, Object>> connectionMap,
            Set<String> userIds) {

        try {

            if (userIds == null || userIds.isEmpty()) {
                return;
            }

            UserSearchCriteria criteria =
                    UserSearchCriteria.builder()
                            .uuid(userIds)
                            .build();

            UserInfoResponse response =
                    rest.postForObject(
                            userContext + userSearchPath,
                            criteria,
                            UserInfoResponse.class);

            if (response == null || response.getUsers() == null) {
                return;
            }

            Map<String, UserInfo> userMap =
                    response.getUsers()
                            .stream()
                            .collect(Collectors.toMap(
                                    UserInfo::getUuid,
                                    user -> user));

            for (Map<String, Object> connection : connectionMap.values()) {

                String ownerUserId =
                        (String) connection.get("_ownerUserId");

                UserInfo user = userMap.get(ownerUserId);

                if (user != null) {

                    connection.put(
                            "ownerName",
                            user.getName());

                    connection.put(
                            "mobileNumber",
                            user.getMobileNumber());

                    connection.put(
                            "guardianName",
                            user.getFatherOrHusbandName());
                }

                connection.remove("_ownerUserId");
            }

        } catch (Exception e) {
            log.error("Error fetching user details", e);
        }
    }
}