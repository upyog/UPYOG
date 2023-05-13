package org.ksmart.birth.common.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.BirthPdfRegisterRequest;
import org.ksmart.birth.common.contract.EgovPdfResp;
import org.ksmart.birth.newbirth.repository.rowmapper.*;
import org.ksmart.birth.web.model.birthnac.certificate.CertificateDetails;
import org.ksmart.birth.web.model.birthnac.certificate.CertificateRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
@Component
public class CertificateRowMapper  implements ResultSetExtractor<List<BirthCertificate>>, CommonBaseRowMapper {
    @Override
    public List<BirthCertificate> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<BirthCertificate> result = new ArrayList<>();
        while (rs.next()) {
            result.add(BirthCertificate.builder()
                    .id(rs.getString("id"))
                    .applicationNumber(rs.getString("ack_no"))
                    .registrtionNo(rs.getString("registrationno"))
                    .registrationId(rs.getString("registrydetailsid"))
                    .tenantId(rs.getString("tenantid"))
                    .filestoreid(rs.getString("filestoreid"))
                    .embeddedUrl(rs.getString("embeddedurl"))
                    .registrationId(rs.getString("registrydetailsid"))
                    .dateofissue(rs.getLong("dateofissue"))
                    .applicationId(rs.getString("applicationid"))
                    .birthCertificateNo(rs.getString("certificateno"))
                    .build());
        }
        return result;
    }
}

