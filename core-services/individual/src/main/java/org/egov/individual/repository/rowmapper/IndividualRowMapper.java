package org.egov.individual.repository.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.AuditDetails;
import digit.models.coremodels.user.enums.UserType;
import org.egov.common.models.individual.AdditionalFields;
import org.egov.common.models.individual.BloodGroup;
import org.egov.common.models.individual.Gender;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.Name;
import org.egov.common.models.individual.UserDetails;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class IndividualRowMapper implements RowMapper<Individual> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Individual mapRow(ResultSet resultSet, int i) throws SQLException {
        try {
            String tenantId = resultSet.getString("tenantId");
            AuditDetails auditDetails = AuditDetails.builder()
                    .createdBy(resultSet.getString("createdBy"))
                    .lastModifiedBy(resultSet.getString("lastModifiedBy"))
                    .createdTime(resultSet.getLong("createdTime"))
                    .lastModifiedTime(resultSet.getLong("lastModifiedTime"))
                    .build();
            AuditDetails clientAuditDetails = AuditDetails.builder()
                    .createdTime(resultSet.getLong("clientCreatedTime"))
                    .createdBy(resultSet.getString("clientCreatedBy"))
                    .lastModifiedTime(resultSet.getLong("clientLastModifiedTime"))
                    .lastModifiedBy(resultSet.getString("clientLastModifiedBy"))
                    .build();
            return Individual.builder().id(resultSet.getString("id"))
                    .individualId(resultSet.getString("individualid"))
                    .userId(resultSet.getString("userId"))
                    .clientReferenceId(resultSet.getString("clientReferenceId"))
                    .tenantId(tenantId)
                    .name(Name.builder().givenName(resultSet.getString("givenName"))
                            .familyName(resultSet.getString("familyName"))
                            .otherNames(resultSet.getString("otherNames")).build())
                    .dateOfBirth(resultSet.getDate("dateOfBirth") != null ?
                            resultSet.getDate("dateOfBirth"): null)
                    .gender(Gender.fromValue(resultSet.getString("gender")))
                    .bloodGroup(BloodGroup.fromValue(resultSet.getString("bloodGroup")))
                    .mobileNumber(resultSet.getString("mobileNumber"))
                    .altContactNumber(resultSet.getString("altContactNumber"))
                    .email(resultSet.getString("email"))
                    .fatherName(resultSet.getString("fatherName"))
                    .husbandName(resultSet.getString("husbandName"))
                    .relationship(resultSet.getString("relationship"))
                    .photo(resultSet.getString("photo"))
                    .additionalFields(resultSet.getString("additionalDetails") == null ? null :
                            objectMapper.readValue(resultSet.getString("additionalDetails"),
                                    AdditionalFields.class)
                    )
                    .auditDetails(auditDetails)
                    .rowVersion(resultSet.getInt("rowVersion"))
                    .isDeleted(resultSet.getBoolean("isDeleted"))
                    .isSystemUser(resultSet.getBoolean("isSystemUser"))
                    .isSystemUserActive(resultSet.getBoolean("isSystemUserActive"))
                    .userDetails(UserDetails.builder()
                            .username(resultSet.getString("username"))
                            .password(resultSet.getString("password"))
                            .userType(UserType.fromValue(resultSet.getString("type")))
                            .roles(resultSet.getString("roles") == null ? null :
                                    objectMapper.readValue(resultSet.getString("roles"),
                                            List.class))
                            .tenantId(tenantId)
                            .build())
                    .userUuid(resultSet.getString("userUuid"))
                    .clientAuditDetails(clientAuditDetails)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
