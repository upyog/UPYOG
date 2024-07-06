package digit.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import digit.bmc.model.UserSchemeApplication;

@Repository
public class UserSchemeCitizenRepository {
    @Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<UserSchemeApplication> getApprovedUserSchemes() {
		 String sql = "SELECT * FROM eg_bmc_userschemeapplication WHERE firstapprovalstatus = true AND randomselection = false";
	        return jdbcTemplate.query(sql, new UserSchemeApplicationRowMapper());
	    }

	    private static class UserSchemeApplicationRowMapper implements RowMapper<UserSchemeApplication> {
	        @Override
	        public UserSchemeApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
	            UserSchemeApplication application = new UserSchemeApplication();
	            application.setId(rs.getLong("id"));
	            application.setFirstApprovalStatus(rs.getBoolean("firstapprovalstatus"));
	            application.setApplicationNumber(rs.getString("applicationNumber"));
	            application.setVerificationStatus(rs.getBoolean("verificationstatus"));
	            application.setOptedId(rs.getLong("optedid"));
	            application.setTenantId(rs.getString("tenantid"));
	            application.setApplicationStatus(rs.getBoolean("applicationstatus"));
	            return application;
	        }
	    }
	    
	    public void updateRandomSelection(List<Long> userIds) {
	        String sql = "UPDATE eg_bmc_userschemeapplication SET randomselection = true WHERE id IN (" +
	                     userIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";
	        jdbcTemplate.update(sql, userIds.toArray());
	    }
}
