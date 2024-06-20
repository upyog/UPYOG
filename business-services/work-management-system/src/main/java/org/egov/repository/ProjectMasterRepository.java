package org.egov.repository;

import org.egov.repository.querybuilder.ProjectQueryBuilder;
import org.egov.repository.querybuilder.SchemeQueryBuilder;
import org.egov.repository.rowmapper.ProjectRowMapper;
import org.egov.repository.rowmapper.SchemeRowMapper;
import org.egov.web.models.Project;
import org.egov.web.models.ProjectApplicationSearchCriteria;
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
public class ProjectMasterRepository {

	@Autowired
    private ProjectQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProjectRowMapper rowMapper;
    

    
    //For Update
	public List<Project> updateProject(ProjectApplicationSearchCriteria searchCriteria){
		// TODO Auto-generated method stub
		
		
		
	        List<Object> preparedStmtList = new ArrayList<>();
	        String query = queryBuilder.getProjectApplicationSearchQuery(searchCriteria, preparedStmtList);
	        log.info("Final query: " + query);
	        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
		
        
			}

	

	
	
	// Method to retrieve all schemes
	/*
	 * public List<Project> getAllProject() { String sql =
	 * "SELECT * FROM project_master"; return jdbcTemplate.query(sql, new
	 * BeanPropertyRowMapper<>(Project.class)); }
	 */
    
 // Method to retrieve a scheme by ID
	/*
	 * public Project getProjectById(Long id) { String sql =
	 * "SELECT * FROM project_master WHERE project_id  = ?"; return
	 * jdbcTemplate.queryForObject(sql, new Object[]{id}, new
	 * BeanPropertyRowMapper<>(Project.class)); }
	 */
   //for searching,Fetch
	public List<Project> getApplications(
			 ProjectApplicationSearchCriteria projectrApplicationSearchCriteria) {
		// TODO Auto-generated method stub
		List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getProjectApplicationSearchQuery(projectrApplicationSearchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
	}





	
	
}