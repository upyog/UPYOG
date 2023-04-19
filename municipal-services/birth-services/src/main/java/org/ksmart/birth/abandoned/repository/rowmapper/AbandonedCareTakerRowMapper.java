package org.ksmart.birth.abandoned.repository.rowmapper;

import org.ksmart.birth.web.model.InformatDetail;
import org.ksmart.birth.web.model.abandoned.CareTaker;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface AbandonedCareTakerRowMapper {
    default CareTaker getCareTakerDetail(ResultSet rs) throws SQLException {
        return CareTaker.builder()
                .institutionName(rs.getString("ct_care_taker_institution"))
                .caretakerName(rs.getString("ct_care_taker_name"))
                .caretakerDesignation(rs.getString("ct_care_taker_inst_designation"))
                .caretakerMobile(rs.getString("ct_care_taker_mobileno"))
                .caretakerAddress(rs.getString("ct_care_taker_address"))
                .build();
    }
}
