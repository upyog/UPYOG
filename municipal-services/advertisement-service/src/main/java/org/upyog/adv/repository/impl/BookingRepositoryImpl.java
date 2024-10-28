package org.upyog.adv.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.kafka.Producer;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.repository.querybuilder.AdvertisementBookingQueryBuilder;
import org.upyog.adv.repository.rowmapper.AdvertisementSlotAvailabilityRowMapper;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingRequest;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BookingRepositoryImpl implements BookingRepository {

	@Autowired
	private Producer producer;

	@Autowired
	private BookingConfiguration bookingConfiguration;
	@Autowired
	private AdvertisementBookingQueryBuilder queryBuilder;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AdvertisementSlotAvailabilityRowMapper availabilityRowMapper;

	@Override
	public void saveBooking(BookingRequest bookingRequest) {
		log.info("Saving Advertisement booking request data for booking no : "
				+ bookingRequest.getBookingApplication().getBookingNo());
		producer.push(bookingConfiguration.getAdvertisementBookingSaveTopic(), bookingRequest);

	}

	@Override
	public List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			AdvertisementSlotSearchCriteria criteria) {
		List<Object> paramsList = new ArrayList<>();

		StringBuilder query = queryBuilder.getAdvertisementSlotAvailabilityQuery(criteria, paramsList);

		String addTypeQuery = " AND eacd.add_type ";
		String faceAreaQuery = " AND eacd.face_area ";
		String location = " AND eacd.location ";
		String nightLight = " AND eacd.night_light ";

		if (StringUtils.isNotBlank(criteria.getAddType())) {
			query.append(addTypeQuery).append(" = ? ");
			paramsList.add(criteria.getAddType());

		}
		if (StringUtils.isNotBlank(criteria.getAddType())) {
			query.append(faceAreaQuery).append(" = ? ");
			paramsList.add(criteria.getFaceArea());
		}
		if (StringUtils.isNotBlank(criteria.getAddType())) {
			query.append(location).append(" = ? ");
			paramsList.add(criteria.getLocation());
		}
		if (StringUtils.isNotBlank(criteria.getAddType())) {
			query.append(nightLight).append(" = ? ");
			paramsList.add(criteria.getNightLight());
		}

		log.info("getBookingDetails : Final query: " + query);
		log.info("paramsList : " + paramsList);
		List<AdvertisementSlotAvailabilityDetail> availabiltityDetails = jdbcTemplate.query(query.toString(),
				paramsList.toArray(), availabilityRowMapper);

		log.info("Fetched slot availabilty details : " + availabiltityDetails);
		return availabiltityDetails;
	}

}
