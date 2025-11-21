package org.upyog.adv.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.adv.web.models.BookingDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
 /**
     * Extracts data from the ResultSet and maps it to a list of BookingDetail objects.
     * Each row in the ResultSet represents a draft application, which is deserialized
     * from JSON format into a BookingDetail object.
     *
     * @param rs The ResultSet containing the data retrieved from the database.
     * @return A list of BookingDetail objects representing the draft applications.
     * @throws SQLException If there is an error accessing the ResultSet.
     * @throws DataAccessException If there is an error during data access.
     */
@Component
@Slf4j
public class AdvertisementDraftApplicationRowMapper implements ResultSetExtractor<List<BookingDetail>> {

	@Autowired
	private ObjectMapper objectMapper;

	public List<BookingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {

		List<BookingDetail> applicationList = new ArrayList<BookingDetail>();
		while (rs.next()) {
			
			String draftId = rs.getString("draft_id");
			String draftApplicationData = rs.getString("draft_application_data");

			BookingDetail advertisementDetail = null;
			try {
				advertisementDetail = objectMapper.readValue(draftApplicationData, BookingDetail.class);
			} catch (JsonMappingException e) {
				log.error("JsonMappingException : Error coccure while parsing draft application for draftid {}", draftId, e);
			} catch (JsonProcessingException e) {
				log.error("JsonProcessingException : Error coccure while parsing draft application for draftid {}", draftId, e);
			}
			;
			applicationList.add(advertisementDetail);
		}

		return applicationList;
	}

}
