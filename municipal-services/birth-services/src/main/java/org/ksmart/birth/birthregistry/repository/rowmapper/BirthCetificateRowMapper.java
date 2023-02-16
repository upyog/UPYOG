package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BirthCetificateRowMapper implements ResultSetExtractor<List<RegisterCertificateData>> {
    @Override
    public List<RegisterCertificateData> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<RegisterCertificateData> result = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        while (rs.next()) {
            Date regDate = new Date(rs.getLong("registration_date"));
            Date dobDate = new Date(rs.getLong("dateofbirth"));
            result.add(RegisterCertificateData.builder()
                    .id(rs.getString("id"))
                    .dateOfReport(rs.getLong("dateofreport"))
                    .dobStr(formatter.format(dobDate))
                    .dateOfBirth(rs.getLong("dateofbirth"))

                    .build());
        }
        return result;
    }

}
