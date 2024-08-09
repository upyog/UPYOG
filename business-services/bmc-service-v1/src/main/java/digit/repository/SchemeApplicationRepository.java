package digit.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.repository.querybuilder.SchemeApplicationQueryBuilder;
import digit.repository.querybuilder.SchemeBenificiaryBuilder;
import digit.repository.rowmapper.SchemeApplicationRowMapper;
import digit.repository.rowmapper.SchemeBeneficiaryRowMapper;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationSearchCriteria;
import digit.web.models.SchemeBeneficiaryDetails;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SchemeApplicationRepository {

    @Autowired
    private  SchemeApplicationQueryBuilder queryBuilder;
    @Autowired
    private  JdbcTemplate jdbcTemplate;
    @Autowired
    private  SchemeApplicationRowMapper rowMapper;
    @Autowired
    private SchemeBeneficiaryRowMapper schemeBeneficiaryRowMapper;
    @Autowired
    private SchemeBenificiaryBuilder schemeBenificiaryBuilder;
   

    // // Constructor-based dependency injection
    // @Inject
    // public SchemeApplicationRepository(SchemeApplicationQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate, SchemeApplicationRowMapper rowMapper) {
    //     this.queryBuilder = queryBuilder;
    //     this.jdbcTemplate = jdbcTemplate;
    //     this.rowMapper = rowMapper;
    // }

    /**
     * Retrieves a list of SchemeApplication objects based on the given search criteria.
     *
     * @param searchCriteria The criteria to filter the SchemeApplications.
     * @return A list of SchemeApplication objects.
     */
    public List<SchemeApplication> getApplications(SchemeApplicationSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getSchemeApplicationSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());
    }

    public List<SchemeBeneficiaryDetails> initialEligibilityCheck(SchemeBeneficiarySearchCritaria searchCriteria){

        List<Object> preparedStmtList = new ArrayList<>();
        String query = schemeBenificiaryBuilder.getSchemeDetailsSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query : "+query);
        return jdbcTemplate.query(query, schemeBeneficiaryRowMapper, preparedStmtList.toArray());

    }






}