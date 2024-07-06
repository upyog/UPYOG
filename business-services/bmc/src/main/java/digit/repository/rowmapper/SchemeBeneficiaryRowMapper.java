package digit.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
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
            if (schemeDetails == null) {
               
                schemeDetails = SchemeBeneficiaryDetails.builder()
                        .machineId(rs.getLong("machineid"))
                        .optedId(optedId)
                        .courseId(rs.getLong("courseid"))
                        .has_applied_for_pension(rs.getInt("has_applied_for_pension"))
                        .startDate(rs.getTimestamp("startdt").toInstant())
                        .endDate(rs.getTimestamp("enddt").toInstant())
                        .build();
                schemeDetailsMap.put(optedId, schemeDetails);
            }
        }
        
        return new ArrayList<>(schemeDetailsMap.values());
    }
}
