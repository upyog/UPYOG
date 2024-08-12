package digit.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import digit.web.models.BankDetails;

@Component
public class BankDetailsRowMapper implements ResultSetExtractor<List<BankDetails>> {

    @Override
    public List<BankDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<BankDetails> bankDetailsList = new ArrayList<>();

        while (rs.next()) {
            BankDetails bankDetails = BankDetails.builder()
                .branchId(rs.getLong("branchId"))
                .name(rs.getString("bankName"))
                .branchName(rs.getString("branchName"))
                .ifsc(rs.getString("ifsc"))
                .micr(rs.getString("micr"))
                .build();
            bankDetailsList.add(bankDetails);
        }

        return bankDetailsList;
    
    }
}
