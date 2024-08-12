package digit.repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import digit.bmc.model.Bank;
import digit.repository.querybuilder.BankQueryBuilder;
import digit.repository.rowmapper.BankDetailsRowMapper;
import digit.web.models.BankDetails;
import digit.web.models.bank.BankSearchCriteria;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Repository
public class BankRepository {

    @Autowired
    private BankQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BankDetailsRowMapper rowMapper;
    
    private SimpleJdbcInsert simpleJdbcInsert;

    @PostConstruct
    public void init() {
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("eg_bmc_bank")
                .usingGeneratedKeyColumns("id");
    }

    public List<BankDetails>getCommonDetails(BankSearchCriteria searchCriteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getBankDetails(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());
    }

    public Long getBankId(String bankCode) {
        String query = "select b.id from eg_bmc_bank b where b.code = ? ";
        try {
            return jdbcTemplate.queryForObject(query,Long.class,bankCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   
     public Long insertBank(Bank bank) {
        
        String query = queryBuilder.getInsertBankQuery();
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, bank.getCode());
            ps.setString(2, bank.getName());
            ps.setString(3, bank.getNarration());
            ps.setBoolean(4, bank.getIsActive());
            ps.setString(5, bank.getType());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

}