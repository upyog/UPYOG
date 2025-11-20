package org.upyog.cdwm.repository.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.web.models.FacilityCenterDetail;

import lombok.extern.slf4j.Slf4j;

/**
 * RowMapper implementation for extracting {@link FacilityCenterDetail} objects from a JDBC {@link ResultSet}.
 * This class implements {@link ResultSetExtractor} to map each row of the result set to a 
 * {@link FacilityCenterDetail} object and collects them into a list.
 * <p>
 * Used in JDBC operations to convert database records related to facility center disposal details into model objects.
 * </p>
 */

@Component
@Slf4j
public class FacilityCenterDetailRowMapper implements ResultSetExtractor <List<FacilityCenterDetail>> {

	  /**
     * Extracts data from the given {@link ResultSet} and maps it to a list of {@link FacilityCenterDetail} objects.
     * This method iterates through each row in the result set and uses a builder to construct each object.
     * 
     * @param rs the {@link ResultSet} containing the query results
     * @return a list of {@link FacilityCenterDetail} objects mapped from the result set
     * @throws SQLException if an SQL exception is encountered while accessing the result set
     * @throws DataAccessException if a data access exception occurs
     */
   
    @Override
    public List<FacilityCenterDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
    	
    	List<FacilityCenterDetail> facilityCenterDetails = new ArrayList<FacilityCenterDetail>();
		while (rs.next()) {
		
			FacilityCenterDetail details = FacilityCenterDetail.builder() 
				  .disposalId(rs.getString("disposal_id"))
                  .applicationId(rs.getString("application_id"))
                  .vehicleId(rs.getString("vehicle_id"))
                  .vehicleDepotNo(rs.getString("vehicle_depot_no"))
                  .netWeight(rs.getBigDecimal("net_weight"))
                  .grossWeight(rs.getBigDecimal("gross_weight"))
                  .dumpingStationName(rs.getString("dumping_station_name"))
                  .disposalDate(rs.getTimestamp("disposal_date") != null
                          ? rs.getTimestamp("disposal_date").toLocalDateTime()
                          : null)
                  .disposalType(rs.getString("disposal_type"))
                  .nameOfDisposalSite(rs.getString("name_of_disposal_site"))
                  .build();


			facilityCenterDetails.add(details);

		}
		return facilityCenterDetails;
	}
}
