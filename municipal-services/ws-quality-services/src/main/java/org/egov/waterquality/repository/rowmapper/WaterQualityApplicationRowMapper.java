package org.egov.waterquality.repository.rowmapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.egov.tracer.model.CustomException;
import org.egov.waterquality.web.models.collection.ApplicationType;
import org.egov.waterquality.web.models.collection.WaterQuality;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class WaterQualityApplicationRowMapper implements ResultSetExtractor<List<WaterQuality>> {

	@Autowired
	private ObjectMapper mapper;

	private int full_count=0;

	public int getFull_count() {
		return full_count;
	}

	public void setFull_count(int full_count) {
		this.full_count = full_count;
	}
	
	@Override
	public List<WaterQuality> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, WaterQuality> applicationListMap = new LinkedHashMap<>();
		WaterQuality currentWaterQualityApplication = new WaterQuality();
		while (rs.next()) {
			String Id = rs.getString("connection_Id");
			if (applicationListMap.getOrDefault(Id, null) == null) {
				currentWaterQualityApplication = new WaterQuality();
				currentWaterQualityApplication.setId(rs.getString("id"));
				currentWaterQualityApplication.setTenantId(rs.getString("tenantid"));
				currentWaterQualityApplication.setType(ApplicationType.valueOf(rs.getString("tenantid")));
				currentWaterQualityApplication.setApplicationNo(rs.getString("applicationno"));
				currentWaterQualityApplication.setCreatedBy(rs.getString("createdby"));
				currentWaterQualityApplication.setCreatedTime(rs.getLong("createdtime"));
				
				PGobject pgObj = (PGobject) rs.getObject("applicationdetails");
				this.setFull_count(rs.getInt("full_count"));
				ObjectNode applicationDetails = null;
				if (pgObj != null) {

					try {
						applicationDetails = mapper.readValue(pgObj.getValue(), ObjectNode.class);
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						throw new CustomException("PARSING ERROR", "The additionalDetail json cannot be parsed");
					}
				} else {
					applicationDetails = mapper.createObjectNode();
				}
				
				currentWaterQualityApplication.setApplicationDetails(applicationDetails);

				applicationListMap.put(Id, currentWaterQualityApplication);
			}
		}
		return new ArrayList<>(applicationListMap.values());
	}

}