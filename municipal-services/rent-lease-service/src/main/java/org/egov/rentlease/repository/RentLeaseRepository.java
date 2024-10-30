package org.egov.rentlease.repository;

import org.egov.rentlease.repo.rowmapper.RentLeaseRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RentLeaseRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RentLeaseRowMapper rentLeaseRowMapper;
	
	 private static final String baseSearchQuery = "Select * from eg_asset_assetdetails";
	 
	 


}
