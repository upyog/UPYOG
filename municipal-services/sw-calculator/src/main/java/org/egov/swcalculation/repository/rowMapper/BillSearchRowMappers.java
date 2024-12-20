package org.egov.swcalculation.repository.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.swcalculation.web.models.BillSearchs;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class BillSearchRowMappers implements ResultSetExtractor<List<BillSearchs>> {

    @Override
    public List<BillSearchs> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<BillSearchs> billSearchList = new ArrayList<>();
        
        while (rs.next()) {
            BillSearchs billSearch = new BillSearchs();
            // Fetch consumercode for the given demandid
            billSearch.setConsumercode(rs.getString("consumercode"));  // Assuming consumercode is a Long
            billSearchList.add(billSearch);
        }
        
        return billSearchList;
    }

}
