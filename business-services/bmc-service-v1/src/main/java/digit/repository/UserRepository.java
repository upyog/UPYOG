package digit.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.models.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import digit.repository.querybuilder.UserQueryBuilder;
import digit.repository.rowmapper.UserDetailRowMapper;
import digit.web.models.BankDetails;
import digit.web.models.user.DocumentDetails;
import digit.web.models.user.QualificationDetails;
import digit.web.models.user.UserDetails;
import digit.web.models.user.UserRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class UserRepository {

    @Autowired
    private UserQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDetailRowMapper rowMapper;

    public List<UserDetails> getUserDetails(UserSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getUserSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());
    }

    public Long getUserAddressMaxId() {
        String sql = "SELECT MAX(id) FROM eg_address";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    

}
