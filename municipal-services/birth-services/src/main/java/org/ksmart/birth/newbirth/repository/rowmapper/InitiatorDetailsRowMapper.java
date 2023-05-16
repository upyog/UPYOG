package org.ksmart.birth.newbirth.repository.rowmapper;
import org.ksmart.birth.web.model.InitiatorDetail;

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
                .initiator(rs.getString("ini_initiator"))
                .isGuardian(rs.getBoolean("ini_isguardian"))
                .ipopList(rs.getString("pla_hosp_ip_op"))
                .ipopNumber(rs.getString("pla_hosp_ip_op_number"))
                .obstetricsNumber(rs.getString("pla_obstetrics_gync_number"))
                .build();
    }
}

