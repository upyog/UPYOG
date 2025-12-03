package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.AuditDetails;
import org.egov.egovsurveyservices.web.models.SurveyResponseNew;
import org.egov.egovsurveyservices.web.models.enums.SurveyStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SurveyResponseRowMapper implements ResultSetExtractor<List<SurveyResponseNew>> {
    @Override
    public List<SurveyResponseNew> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, SurveyResponseNew> responseMap = new LinkedHashMap<>();

        while (rs.next()) {
            String uuid = rs.getString("uuid");
            SurveyResponseNew surveyResponse = responseMap.get(uuid);

            if (surveyResponse == null) {
                AuditDetails auditDetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                        .build();

                surveyResponse = SurveyResponseNew.builder()
                        .uuid(uuid)
                        .surveyUuid(rs.getString("surveyuuid"))
                        .citizenId(rs.getString("citizenid"))
                        .tenantId(rs.getString("tenantid"))
                        .locality(rs.getString("locality"))
                        .coordinates(rs.getString("coordinates"))
                        .status(SurveyStatus.fromValue(rs.getString("status")))
                        .build();
            }

            responseMap.put(uuid, surveyResponse);
        }
        return new ArrayList<>(responseMap.values());
    }
}
