package org.upyog.chb.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.web.models.VenueSlotSearchCriteria;

/**
 * Shared helpers for slot search / timer criteria.
 */
public final class CommunityHallSlotCriteriaUtil {

	private CommunityHallSlotCriteriaUtil() {
	}

	public static List<String> resolveHallCodes(VenueSlotSearchCriteria criteria) {
		var hallCodes = new ArrayList<String>();
		if (StringUtils.isNotBlank(criteria.getUnitCode())) {
			hallCodes.add(criteria.getUnitCode());
		}
		if (!CollectionUtils.isEmpty(criteria.getUnitCodes())) {
			hallCodes.addAll(criteria.getUnitCodes());
		}
		if (hallCodes.isEmpty()) {
			throw new CustomException("INVALID_HALL_CODE", "Hall code is required for slot timer");
		}
		return hallCodes;
	}

	public static List<LocalDate> resolveBookingDates(VenueSlotSearchCriteria criteria) {
		var startDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingStartDate());
		var endDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingEndDate());
		var dates = new ArrayList<LocalDate>();
		for (var date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			dates.add(date);
		}
		return dates;
	}
}
