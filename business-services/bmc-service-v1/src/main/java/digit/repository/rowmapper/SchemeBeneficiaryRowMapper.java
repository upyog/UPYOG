package digit.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import digit.web.models.SchemeBeneficiaryDetails;

@Component
public class SchemeBeneficiaryRowMapper implements ResultSetExtractor<List<SchemeBeneficiaryDetails>>{


    @Override
    public List<SchemeBeneficiaryDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
    
        Map<Long, SchemeBeneficiaryDetails> schemeDetailsMap = new LinkedHashMap<>();

        
        while (rs.next()) {
            Long optedId = rs.getLong("optedid");
            SchemeBeneficiaryDetails schemeDetails = schemeDetailsMap.get(optedId);
            long createdOnTimestamp = rs.getLong("createdon");
            Instant createdOnInstant = Instant.ofEpochMilli(createdOnTimestamp);
            if (schemeDetails == null) {
               
                schemeDetails = SchemeBeneficiaryDetails.builder()
                        .machineId(rs.getLong("machineid"))
                        .optedId(optedId)
                        .courseId(rs.getLong("courseid"))
                        .startDate(createdOnInstant)
                        .schemeGroupId(rs.getLong("id"))
                        .schemeGroupName(rs.getString("name"))
                        .applicationNumber(rs.getString("applicationnumber"))
                        .build();
                schemeDetailsMap.put(optedId, schemeDetails);
            }
        }
        
        return new ArrayList<>(schemeDetailsMap.values());
    }
}
