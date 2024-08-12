package digit.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import digit.bmc.model.SchemeCriteria;
import digit.bmc.model.VerificationDetails;
import digit.repository.querybuilder.SchemeApplicationQueryBuilder;
import digit.repository.querybuilder.SchemeBenificiaryBuilder;
import digit.repository.querybuilder.VerifierQueryBuilder;
import digit.repository.rowmapper.SchemeApplicationRowMapper;
import digit.repository.rowmapper.SchemeBeneficiaryRowMapper;
import digit.repository.rowmapper.VerificationDetailsRowMapper;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationSearchCriteria;
import digit.web.models.SchemeBeneficiaryDetails;
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
    
    @Autowired
    private VerifierQueryBuilder verifierQueryBuilder;
    
    @Autowired
    private VerificationDetailsRowMapper verificationDetailsRowMapper;
   

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
    
    public List<SchemeCriteria> getCriteriaBySchemeIdAndType(Long schemeId) {
        String sql = "SELECT ct.criteriatype, c.criteriavalue, cc.criteriacondition " +
                     "FROM eg_bmc_criteria c " +
                     "LEFT JOIN eg_bmc_scheme_criteria sc ON c.id = sc.criteriaid " +
                     "LEFT JOIN eg_bmc_criteriatype ct on c.criteriatype = ct.id " +
                     "LEFT JOIN eg_bmc_criteriacondition cc on c.criteriacondition = cc.id " +
                     "LEFT JOIN eg_bmc_schemes s on sc.schemeid = s.id " +
                     "WHERE s.id = ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SchemeCriteria.class), schemeId);
    }

    public String getSchemeById(Long schemeId) {
       String sql = "SELECT s.name from eg_bmc_schemes s where s.id = ?";
       return jdbcTemplate.queryForObject(sql,String.class,schemeId);

    }
    public List<String> getQualifications() {
        String sql = "SELECT q.name FROM eg_bmc_qualificationmaster q where q.id > 1";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"));
    }

    public List<VerificationDetails> getApplicationForVerification(SchemeApplicationSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = verifierQueryBuilder.getVerificationSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final Query: " + query);
        return jdbcTemplate.query(query, verificationDetailsRowMapper,preparedStmtList.toArray());
    }

   

}