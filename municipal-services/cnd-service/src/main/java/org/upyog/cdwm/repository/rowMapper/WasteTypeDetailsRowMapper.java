package org.upyog.cdwm.repository.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.web.models.WasteTypeDetail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WasteTypeDetailsRowMapper implements ResultSetExtractor<List<WasteTypeDetail>> {

    @Override
    public List<WasteTypeDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<WasteTypeDetail> wasteTypeDetails = new ArrayList<>();

        while (rs.next()) {
            try {

                WasteTypeDetail wasteTypeDetail = WasteTypeDetail.builder()
                        .applicationId(rs.getString("wasteDetailApplicationId"))
                        .wasteTypeId(rs.getString("wasteTypeId"))
                        .enteredByUserType(rs.getString("enteredByUserType"))
                        .wasteType(rs.getString("wasteType"))
                        .quantity(rs.getBigDecimal("quantity"))
                        .metrics(rs.getString("metrics"))
                        .build();

                wasteTypeDetails.add(wasteTypeDetail);

            } catch (SQLException e) {
                log.error("Error extracting WasteTypeDetail", e);
                throw e;
            }
        }
        return wasteTypeDetails;
    }
}
