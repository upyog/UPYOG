package org.upyog.sv.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.sv.web.models.StreetVendingDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StreetVendingDraftApplicationRowMapper implements ResultSetExtractor<List<StreetVendingDetail>> {

	@Autowired
	private ObjectMapper objectMapper;

	public List<StreetVendingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {

		List<StreetVendingDetail> applicationList = new ArrayList<StreetVendingDetail>();
		while (rs.next()) {
			
			String draftId = rs.getString("draft_id");
			String draftApplicationData = rs.getString("draft_application_data");

			StreetVendingDetail streetVendingDetail = null;
			try {
				streetVendingDetail = objectMapper.readValue(draftApplicationData, StreetVendingDetail.class);
			} catch (JsonMappingException e) {
				log.error("JsonMappingException : Error coccure while parsing draft applicagtion for draftid {}", draftId, e);
			} catch (JsonProcessingException e) {
				log.error("JsonProcessingException : Error coccure while parsing draft applicagtion for draftid {}", draftId, e);
			}
			;
			applicationList.add(streetVendingDetail);
		}

		return applicationList;
	}

}
