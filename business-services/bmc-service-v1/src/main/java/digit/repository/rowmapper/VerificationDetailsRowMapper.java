package digit.repository.rowmapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import digit.bmc.model.VerificationDetails;
import digit.repository.UserRepository;
import digit.repository.UserSearchCriteria;
import digit.web.models.user.UserDetails;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import digit.bmc.model.VerificationDetails;

@Component
public class VerificationDetailsRowMapper implements ResultSetExtractor<List<VerificationDetails>> {

    @Autowired
    UserRepository userRepository;

     @Override
    public List<VerificationDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, VerificationDetails> verificationDetailsMap = new LinkedHashMap<>();

        while (rs.next()) {
            String applicationNumber = rs.getString("applicationnumber");
            VerificationDetails verificationDetails = verificationDetailsMap.get(applicationNumber);

            if (verificationDetails == null) {
                verificationDetails = new VerificationDetails();
                verificationDetails.setApplicationNumber(applicationNumber);
                verificationDetails.setUserId(rs.getLong("userid"));
                verificationDetails.setTenantId(rs.getString("tenantid"));
                verificationDetails.setScheme(rs.getString("scheme"));
                verificationDetails.setMachine(getFieldValue(rs, "machine"));
                verificationDetails.setCourse(getFieldValue(rs, "course"));
                UserSearchCriteria criteria  = new UserSearchCriteria("full",verificationDetails.getUserId(),verificationDetails.getTenantId());
                List<UserDetails> userDetails = userRepository.getUserDetails(criteria);
                verificationDetails.setUserDetails(userDetails);
                verificationDetailsMap.put(applicationNumber, verificationDetails);
            }
        }

        return new ArrayList<>(verificationDetailsMap.values());
    }

    private List<String> getFieldValue(ResultSet rs, String fieldName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            if (metaData.getColumnName(i).equalsIgnoreCase(fieldName)) {
                int columnType = metaData.getColumnType(i);
                if (columnType == java.sql.Types.ARRAY) {
                    String[] array = (String[]) rs.getArray(fieldName).getArray();
                    return Arrays.asList(array);
                } else if (columnType == java.sql.Types.VARCHAR) {
                    return Collections.singletonList(rs.getString(fieldName));
                }
            }
        }
        return Collections.emptyList();
    }
}