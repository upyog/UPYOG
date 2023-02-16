package org.ksmart.birth.ksmartbirthapplication.repository.rowmapper;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartInitiatorDetail;

import javax.validation.constraints.Size;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface KsmartInitiatorDetailsRowMapper {
    default KsmartInitiatorDetail getKsmartInitiatorDetail(ResultSet rs) throws SQLException {
        return KsmartInitiatorDetail.builder()
                .initiatorAadharNo(rs.getString("init_initiatorAadharNo"))
                .initiatorAddress(rs.getString("init_initiatorAddress"))
                .relation(rs.getString("init_relation"))
                .initiatorDesi(rs.getString("init_initiatorDesi"))
                .initiatorNameEn(rs.getString("init_initiatorNameEn"))
                .initiatorMobileNo(rs.getString("init_initiatorMobileNo"))
                .isInitiatorDeclaration(Boolean.valueOf(rs.getString("init_isInitiatorDeclaration")))
                .isCaretaker(Boolean.valueOf(rs.getString("init_isCaretaker")))
                .build();
    }
}

