package org.ksmart.birth.ksmartbirthapplication.repository.rowmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartInformatDetail;

import javax.validation.constraints.Size;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface KsmartInformatDetailsRowMapper {
        default KsmartInformatDetail getKsmartInformatDetail(ResultSet rs) throws SQLException {
            return KsmartInformatDetail.builder()

                    .informerAddress(rs.getString("pla_informantsaddress_en"))
                    .infomantMobile(rs.getString("pla_informants_mobileno"))
                    .infomantAadhar(rs.getString("pla_informants_aadhaar_no"))
                    .infomantFirstNameEn(rs.getString("pla_informantsname_en"))
                    .build();
        }
}
