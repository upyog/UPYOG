package digit.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.repository.querybuilder.SchemeDetailQueryBuilder;
import digit.repository.rowmapper.SchemeRowMapper;
import digit.web.models.scheme.EventDetails;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class SchemesRepository {

    @Autowired
    private SchemeDetailQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SchemeRowMapper rowMapper;

    public List<EventDetails>getSchemeDetails(SchemeSearchCriteria searchCriteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getSchemeSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());
    }
}
