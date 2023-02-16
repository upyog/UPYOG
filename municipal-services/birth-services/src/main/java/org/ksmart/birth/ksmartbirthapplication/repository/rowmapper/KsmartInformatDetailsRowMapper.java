package org.ksmart.birth.ksmartbirthapplication.repository.rowmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartInformatDetail;

import javax.validation.constraints.Size;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface KsmartInformatDetailsRowMapper {
        default KsmartInformatDetail getKsmartInformatDetail(ResultSet rs) throws SQLException {
            return KsmartInformatDetail.builder()

                    .isDeclarationInfo(rs.getString("info_isDeclarationInfo"))
                    .informerAddress(rs.getString("info_informerAddress"))
                    .infomantMobile(rs.getString("info_infomantMobile"))
                    .infomantAadhar(rs.getString("info_"))
                    .infomantFirstNameEn(rs.getString("info_infomantFirstNameEn"))
                    .informerDesi(rs.getString("info_informerDesi"))
                    .build();
        }
}
