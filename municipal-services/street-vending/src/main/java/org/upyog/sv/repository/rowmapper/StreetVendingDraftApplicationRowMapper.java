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

@Component
public class StreetVendingDraftApplicationRowMapper implements ResultSetExtractor<List<StreetVendingDetail>> {

	@Autowired
	private ObjectMapper objectMapper;

	public List<StreetVendingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {

		List<StreetVendingDetail> applicationList = new ArrayList<StreetVendingDetail>();
		while (rs.next()) {
			/**
			 * SELECT draft_id, tenant_id, user_uuid, draft_application_data, createdby,
			 * lastmodifiedby, createdtime, lastmodifiedtime FROM
			 * eg_sv_street_vending_draft_detail;
			 */
			String draftId = rs.getString("draft_id");
			String draftApplicationData = rs.getString("draft_application_data");

			StreetVendingDetail streetVendingDetail = null;
			try {
				streetVendingDetail = objectMapper.readValue(draftApplicationData, StreetVendingDetail.class);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			;
			applicationList.add(streetVendingDetail);
		}

		return applicationList;
	}

}
