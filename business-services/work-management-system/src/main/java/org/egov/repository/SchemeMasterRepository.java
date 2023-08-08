package org.egov.repository;

import org.egov.repository.querybuilder.SchemeQueryBuilder;
import org.egov.repository.rowmapper.SchemeRowMapper;
//import org.wms.repository.querybuilder.SchemeQueryBuilder;
//import org.wms.repository.rowmapper.BirthApplicationRowMapper;
//import org.wms.web.models.BirthApplicationSearchCriteria;
//import org.wms.web.models.BirthRegistrationApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.egov.web.models.SchemeCreationApplication;
//import org.wms.web.models.SchemeCreationRequest;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
public class SchemeMasterRepository {

	@Autowired
    private SchemeQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SchemeRowMapper rowMapper;
    

    public List<Scheme> searchScheme(String keyword){
    	//String sql = "SELECT * FROM Scheme_Master WHERE (name_en LIKE ? OR name_reg LIKE ?)";
    	String sql = "SELECT * FROM Scheme_Master WHERE Scheme_Name_En LIKE ? OR Scheme_Name_Reg LIKE ?";
        
        //String keywordWithWildcards = "%" + keyword + "%";

        return jdbcTemplate.query(sql, new Object[]{keyword, keyword}, new BeanPropertyRowMapper<>(Scheme.class));
    }

	public List<Scheme> updateScheme(SchemeApplicationSearchCriteria searchCriteria){
		// TODO Auto-generated method stub
		
		
		
	        List<Object> preparedStmtList = new ArrayList<>();
	        String query = queryBuilder.getSchemeApplicationSearchQuery(searchCriteria, preparedStmtList);
	        log.info("Final query: " + query);
	        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
		
        
			}

	/*
	 * public boolean updateScheme(Scheme scheme) {
	 * 
	 * String sql =
	 * "UPDATE Scheme_Master SET Scheme_Name_En = ?, Scheme_Name_Reg = ? WHERE Scheme_ID = ?"
	 * ;
	 * 
	 * // Use JdbcTemplate to execute the update query with the required parameters
	 * int rowsAffected = jdbcTemplate.update(sql, scheme.getSchemeNameEn(),
	 * scheme.getSchemeNameReg(), scheme.getId());
	 * 
	 * // If the number of affected rows is greater than 0, the update was
	 * successful return rowsAffected > 0;
	 * 
	 * 
	 * }
	 */

	/*
	 * public Optional<Scheme> findById(Long id) { String sql =
	 * "SELECT * FROM Scheme_Master WHERE id = ?";
	 * 
	 * // Use JdbcTemplate to execute the query and retrieve the scheme by ID Scheme
	 * scheme = jdbcTemplate.queryForObject(sql, new Object[]{id}, new
	 * BeanPropertyRowMapper<>(Scheme.class));
	 * 
	 * // Wrap the result in an Optional to handle null values return
	 * Optional.ofNullable(scheme); }
	 */
	
	// Method to retrieve all schemes
    public List<Scheme> getAllSchemes() {
        String sql = "SELECT * FROM Scheme_Master";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Scheme.class));
    }
    
 // Method to retrieve a scheme by ID
    public Scheme getSchemeById(Long id) {
        String sql = "SELECT * FROM Scheme_Master WHERE Scheme_ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(Scheme.class));
    }
	
	
}