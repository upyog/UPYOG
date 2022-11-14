package org.egov.filemgmnt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.filemgmnt.web.models.CommunicationFile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class CommunicationFileManagementRowMapper
        implements ResultSetExtractor<List<CommunicationFile>>, BaseRowMapper {
    @Override
    public List<CommunicationFile> extractData(ResultSet rs) throws SQLException, DataAccessException { // NOPMD

        List<CommunicationFile> result = new ArrayList<>();

        while (rs.next()) {
            result.add(CommunicationFile.builder()
                                        .id(rs.getString("id"))
                                        .subjectTypeId(rs.getString("subjecttypeid"))
                                        .senderId(rs.getString("senderid"))
                                        .priorityId(rs.getString("priorityid"))
                                        .fileStoreId(rs.getString("filestoreid"))
                                        .details(rs.getString("details"))
                                        .auditDetails(getAuditDetails(rs))
                                        .build());

        }
        return result;
    }

}
