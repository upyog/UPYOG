package org.egov.repository;

import org.egov.repository.querybuilder.SchemeQueryBuilder;
import org.egov.repository.rowmapper.SchemeRowMapper;
import org.egov.web.models.ScheduleOfRateApplication;
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

import javax.validation.Valid;


@Slf4j
@Repository
public class SchemeMasterRepository {

	@Autowired
    private SchemeQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SchemeRowMapper rowMapper;
    

    
    //For Update
	public List<Scheme> updateScheme(SchemeApplicationSearchCriteria searchCriteria){
		// TODO Auto-generated method stub
		
		
		
	        List<Object> preparedStmtList = new ArrayList<>();
	        String query = queryBuilder.getSchemeApplicationSearchQuery(searchCriteria, preparedStmtList);
	        log.info("Final query: " + query);
	        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
		
        
			}

	

	
	
	// Method to retrieve all schemes
	/*
	 * public List<Scheme> getAllSchemes() { String sql =
	 * "SELECT * FROM Scheme_Master"; return jdbcTemplate.query(sql, new
	 * BeanPropertyRowMapper<>(Scheme.class)); }
	 */
    
 // Method to retrieve a scheme by ID
	/*
	 * public Scheme getSchemeById(Long id) { String sql =
	 * "SELECT * FROM Scheme_Master WHERE Scheme_ID = ?"; return
	 * jdbcTemplate.queryForObject(sql, new Object[]{id}, new
	 * BeanPropertyRowMapper<>(Scheme.class)); }
	 */
   //for searching,Fetch
	public List<Scheme> getApplications(
			 SchemeApplicationSearchCriteria schemerApplicationSearchCriteria) {
		// TODO Auto-generated method stub
		List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getSchemeApplicationSearchQuery(schemerApplicationSearchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
	}





	
	
}