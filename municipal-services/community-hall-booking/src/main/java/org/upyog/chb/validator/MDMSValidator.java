package org.upyog.chb.validator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.web.models.BookingSlotDetail;
import org.upyog.chb.web.models.VenueBookingRequest;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MDMSValidator {

	private static final String VENUES_KEY = "Venues";
	private static final String CODE_KEY = "code";
	private static final String TIME_SLOT_KEY = "timeSlot";
	private static final String MIN_DURATION_KEY = "minDuration";
	private static final String MAX_DURATION_KEY = "maxDuration";
	private static final String MDMS_DATA_ERROR_KEY = "MDMS DATA ERROR ";

	public void validateMdmsData(VenueBookingRequest venueBookingRequest, Object mdmsData,
			Object venueTypeMasterData) {

		Map<String, List<String>> masterData = getAttributeValues(mdmsData);
		String[] masterArray = { CommunityHallBookingConstants.CHB_PURPOSE,
				CommunityHallBookingConstants.CHB_SPECIAL_CATEGORY, CommunityHallBookingConstants.CHB_COMMNUITY_HALLS,
				CommunityHallBookingConstants.CHB_HALL_CODES, CommunityHallBookingConstants.CHB_PARKS,
				CommunityHallBookingConstants.CHB_PARK_CODES, CommunityHallBookingConstants.CHB_STADIUMS,
				CommunityHallBookingConstants.CHB_STADIUM_CODES, CommunityHallBookingConstants.CHB_CREMATORIUMS,
				CommunityHallBookingConstants.CHB_CREMATORIUM_CODES,
				CommunityHallBookingConstants.CHB_GUEST_HOUSE_CODES, CommunityHallBookingConstants.CHB_GUEST_HOUSES,
				CommunityHallBookingConstants.CHB_DOCUMENTS };

		log.info("Validating master data from MDMS for : {}",
				venueBookingRequest.getVenueBookingApplication().getBookingNo());

		validateIfMasterPresent(masterArray, masterData);

		validateTimeSlot(venueBookingRequest.getVenueBookingApplication().getBookingSlotDetails(), venueTypeMasterData,
				venueBookingRequest.getVenueBookingApplication().getVenueType());
	}

	@SuppressWarnings("unchecked")
	private void validateTimeSlot(List<BookingSlotDetail> bookingSlotDetails, Object venueTypeMasterData,
			String venueType) {

		if (bookingSlotDetails == null || bookingSlotDetails.isEmpty()) {
			return;
		}

		Map<String, Object> masterDataMap = (Map<String, Object>) venueTypeMasterData;
		List<Map<String, Object>> venueMasters = (List<Map<String, Object>>) masterDataMap.get(VENUES_KEY);
		if (venueMasters == null || venueMasters.isEmpty()) {
			log.error("Venue Type Master Data is empty or invalid.");
			return;
		}

		for (BookingSlotDetail slot : bookingSlotDetails) {
			if (!isSlotWithinConfiguredDuration(slot, venueMasters, venueType)) {
				return;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isSlotWithinConfiguredDuration(BookingSlotDetail slot, List<Map<String, Object>> venueMasters,
			String venueType) {
		LocalTime fromTime = slot.getBookingFromTime();
		LocalTime toTime = slot.getBookingToTime();

		if (fromTime == null || toTime == null || !fromTime.isBefore(toTime)) {
			return false;
		}

		Map<String, Object> matchingVenue = findMatchingVenue(venueMasters, venueType);
		if (matchingVenue == null) {
			log.warn("No matching venue configuration found for code: {}", venueType);
			return false;
		}

		Map<String, String> timeSlotConfig = (Map<String, String>) matchingVenue.get(TIME_SLOT_KEY);
		if (timeSlotConfig == null) {
			return false;
		}

		return isDurationWithinLimits(fromTime, toTime, timeSlotConfig);
	}

	private Map<String, Object> findMatchingVenue(List<Map<String, Object>> venueMasters, String venueType) {
		return venueMasters.stream()
				.filter(master -> master.get(CODE_KEY) != null
						&& master.get(CODE_KEY).toString().equalsIgnoreCase(venueType))
				.findFirst()
				.orElse(null);
	}

	private boolean isDurationWithinLimits(LocalTime fromTime, LocalTime toTime, Map<String, String> timeSlotConfig) {
		long durationInMinutes = Duration.between(fromTime, toTime).toMinutes();
		long minMinutes = parseDurationToMinutes(timeSlotConfig.get(MIN_DURATION_KEY));
		long maxMinutes = parseDurationToMinutes(timeSlotConfig.get(MAX_DURATION_KEY));

		if (durationInMinutes < minMinutes || durationInMinutes > maxMinutes) {
			log.info("Validation failed. Requested: {} mins. Allowed: {} to {} mins.", durationInMinutes, minMinutes,
					maxMinutes);
			return false;
		}
		return true;
	}

	private long parseDurationToMinutes(String durationStr) {
		if (durationStr == null || !durationStr.contains(":")) {
			return 0;
		}
		try {
			String[] parts = durationStr.split(":");
			long hours = Long.parseLong(parts[0].trim());
			long minutes = Long.parseLong(parts[1].trim());
			return (hours * 60) + minutes;
		} catch (NumberFormatException e) {
			log.error("Failed to parse duration limit string: {}", durationStr, e);
			return 0;
		}
	}

	public Map<String, List<String>> getAttributeValues(Object mdmsData) {

		List<String> modulepaths = Arrays.asList(CommunityHallBookingConstants.CHB_JSONPATH_CODE);
		final Map<String, List<String>> mdmsResMap = new HashMap<>();
		modulepaths.forEach(modulepath -> {
			try {
				mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
			} catch (Exception e) {
				throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT_ID_MDMS_KEY,
						CommunityHallBookingConstants.INVALID_TENANT_ID_MDMS_MSG);
			}
		});
		return mdmsResMap;
	}

	private void validateIfMasterPresent(String[] masterNames, Map<String, List<String>> codes) {
		log.info("Master names in validation : {}", Arrays.toString(masterNames));
		log.info("Mdms data map : {}", codes);
		Map<String, String> errorMap = new HashMap<>();
		for (String masterName : masterNames) {
			if (CollectionUtils.isEmpty(codes.get(masterName))) {
				errorMap.put(MDMS_DATA_ERROR_KEY, "Unable to fetch " + masterName + " codes from MDMS");
			}
		}
		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}

}
