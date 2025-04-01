package org.egov.user.repository.rowmapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.user.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Service
public class DigilockerUserResultSetExtractor implements ResultSetExtractor<List<User>> {
    private ObjectMapper objectMapper;

    @Autowired
    DigilockerUserResultSetExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, User> usersMap = new LinkedHashMap<>();

        while (rs.next()) {
            Long userId = rs.getLong("id");
            User user;

            if (!usersMap.containsKey(userId)) {
                user = User.builder().id(rs.getLong("id")).tenantId(rs.getString("tenantid")).title(rs.getString("title"))
                        .salutation(rs.getString("salutation"))
                        .dob(rs.getDate("dob")).locale(rs.getString("locale")).username(rs.getString("username"))
                        .password(rs.getString("password")).passwordExpiryDate(rs.getTimestamp("pwdexpirydate"))
                        .mobileNumber(rs.getString("mobilenumber")).altContactNumber(rs.getString("altcontactnumber"))
                        .emailId(rs.getString("emailid")).active(rs.getBoolean("active")).name(rs.getString("name"))
                        .lastModifiedBy(rs.getLong("lastmodifiedby")).lastModifiedDate(rs.getTimestamp("lastmodifieddate"))
                        .pan(rs.getString("pan")).aadhaarNumber(rs.getString("aadhaarnumber")).createdBy(rs.getLong("createdby"))
                        .createdDate(rs.getTimestamp("createddate")).guardian(rs.getString("guardian")).signature(rs.getString("signature"))
                        .accountLocked(rs.getBoolean("accountlocked")).photo(rs.getString("photo"))
                        .identificationMark(rs.getString("identificationmark")).uuid(rs.getString("uuid")).digilockerid(rs.getString("digilockerid"))
                        .accountLockedDate(rs.getLong("accountlockeddate")).alternateMobileNumber(rs.getString("alternatemobilenumber"))
                        .build();

                usersMap.put(userId, user);
            }
        }

        return new ArrayList<>(usersMap.values());
    }
}
