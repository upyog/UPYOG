package org.egov.pg.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.pg.models.Transaction;
import org.egov.pg.web.models.TransactionCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final TransactionRowMapper rowMapper = new TransactionRowMapper();

    @Autowired
    TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transaction> fetchTransactions(TransactionCriteria transactionCriteria) {
        List<Object> params = new ArrayList<>();
        String query = TransactionQueryBuilder.getPaymentSearchQueryByCreatedTimeRange(transactionCriteria, params);
        log.info(query);
        return jdbcTemplate.query(query, params.toArray(), rowMapper);
    }

    public List<Transaction> fetchTransactionsByTimeRange(TransactionCriteria transactionCriteria, Long startTime, Long endTime) {
        List<Object> params = new ArrayList<>();
        String query = TransactionQueryBuilder.getPaymentSearchQueryByCreatedTimeRange(transactionCriteria, startTime, endTime, params);
        log.info(query);
        return jdbcTemplate.query(query, params.toArray(), rowMapper);
    }
    
    @Transactional
    public void savePushresponseLog(String msg,String response,String transactionId,String transactionAmmount)
    {
    	LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String formattedDate = currentDate.format(formatter);
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
		String timeString = zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		String status_code = response.split("\\|")[0];
		
		jdbcTemplate.update(TransactionQueryBuilder.INSERT_PUSHRESPONSE_DATA_LOG, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1, formattedDate);
				ps.setString(2, timeString);
				ps.setString(3, msg);
				ps.setString(4, response);
				ps.setString(5, transactionId);
				ps.setString(6, transactionAmmount);
				ps.setString(7, status_code);
				
			}
		});
    }

}
