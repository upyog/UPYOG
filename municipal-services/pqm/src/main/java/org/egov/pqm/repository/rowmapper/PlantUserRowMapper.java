package org.egov.pqm.repository.rowmapper;

import static org.egov.pqm.util.RowMapperUtil.getAdditionalDetail;
import static org.egov.pqm.util.RowMapperUtil.getAuditDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pqm.web.model.plant.user.PlantUser;
import org.egov.pqm.web.model.plant.user.PlantUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PlantUserRowMapper implements ResultSetExtractor<List<PlantUser>> {

    @Autowired
    private ObjectMapper mapper;

    @Getter
    @Setter
    public int totalCount = 0;


    @Override
    public List<PlantUser> extractData(ResultSet resultSet) throws SQLException {

        Map<String, PlantUser> plantUserMap = new LinkedHashMap<>();
        this.setTotalCount(0);
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String plantUserTypeString = resultSet.getString("plantUserType");
            PlantUserType plantUserType = PlantUserType.valueOf(plantUserTypeString.toUpperCase());


            if (plantUserMap.get(id) == null) {
                plantUserMap.put(id, PlantUser.builder()
                        .id(id).tenantId(resultSet.getString("tenantId"))
                        .plantCode(resultSet.getString("plantCode"))
                        .plantUserType(plantUserType)
                        .plantUserUuid(resultSet.getString("plantUserUuid"))
                        .isActive(resultSet.getBoolean("isActive"))
                        .additionalDetails(getAdditionalDetail("additionaldetails", resultSet, mapper))
                        .auditDetails(getAuditDetails(resultSet))
                        .build());
                this.setTotalCount(resultSet.getInt("total_count"));
            }
        }

        return new ArrayList<>(plantUserMap.values());
    }
}
