package digit.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.repository.querybuilder.CommonQueryBuilder;
import digit.repository.rowmapper.CommonRowMapper;
import digit.web.models.common.CommonDetails;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class CommonRepository {

    @Autowired
    private CommonQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommonRowMapper rowMapper;

    public List<CommonDetails>getCommonDetails(CommonSearchCriteria searchCriteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getSchemeSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());
    }
}
