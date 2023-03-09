package org.ksmart.birth.newbirth.repository.rowmapper;
import org.ksmart.birth.web.model.InitiatorDetail;

import javax.validation.constraints.Size;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface InitiatorDetailsRowMapper {
    default InitiatorDetail getInitiatorDetail(ResultSet rs) throws SQLException {
        return InitiatorDetail.builder()
                .initiatorAadharNo(rs.getString("ini_aadharno"))
                .initiatorAddress(rs.getString("ini_initiator_address"))
                .relation(rs.getString("ini_relation"))
                .initiatorDesi(rs.getString("ini_initiator_inst_desig"))
                .initiatorNameEn(rs.getString("ini_initiator_name"))
                .initiatorMobileNo(rs.getString("ini_mobileno"))
                .isInitiatorDeclaration(rs.getBoolean("ini_is_declared"))
                .isCaretaker(rs.getBoolean("ini_is_care_taker"))
                .build();
    }
}

