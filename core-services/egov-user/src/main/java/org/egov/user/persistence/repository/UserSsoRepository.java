package org.egov.user.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.user.domain.model.UserSso;
import org.egov.user.repository.builder.UserSsoQueryBuilder;
import org.egov.user.repository.rowmapper.UserSsoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserSsoRepository {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

//	@Autowired
//  private UserResultSetExtractor userResultSetExtractor;
//
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private UserSsoQueryBuilder userSsoQueryBuilder;
//	
	@Autowired
    private UserSsoRowMapper userSsoRowMapper;


	public UserSso create(UserSso newUserSso) {
		Map<String, Object> inputs = new HashMap<String, Object>();
//		userInputs.put("id", newUserSso.getId());
		inputs.put("ssoId", newUserSso.getSsoId());
		inputs.put("userUuid", newUserSso.getUserUuid());
		inputs.put("createdBy", newUserSso.getAuditDetails().getCreatedBy());
		inputs.put("createdDate", newUserSso.getAuditDetails().getCreatedDate());
		inputs.put("lastModifiedBy", newUserSso.getAuditDetails().getLastModifiedBy());
		inputs.put("lastModifiedDate", newUserSso.getAuditDetails().getLastModifiedDate());
		
		namedParameterJdbcTemplate.update(userSsoQueryBuilder.getInsertUserSsoQuery(), inputs);
		
        return newUserSso;
	}

	public UserSso getCountBySsoId(Long ssoId) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("ssoId", ssoId);
		
		List<UserSso> users = namedParameterJdbcTemplate.query(
		        userSsoQueryBuilder.SELECT_USER_SSO_QUERY, 
		        inputs, 
		        userSsoRowMapper
		    );
		
	    return users.isEmpty() ? null : users.get(0);

//		return user;
	}

}
