package org.egov.user.repository.builder;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserSsoQueryBuilder {

	public static final String SELECT_USER_SSO_QUERY = "SELECT usersso.id, usersso.sso_id, usersso.user_uuid, usersso.createddate, usersso.createdby, usersso.lastmodifieddate, usersso.lastmodifiedby " 
            + " FROM ud_user_sso usersso ";

	public static final String COUNT_USER_SSO_QUERY = "SELECT count(*) FROM ud_user_sso WHERE sso_id = :ssoId ";

	public String getInsertUserSsoQuery() {
	    return "insert into ud_user_sso (sso_id, user_uuid, createddate, createdby, lastmodifieddate, lastmodifiedby) " +
	           "values (:ssoId, :userUuid, :createdDate, :createdBy, :lastModifiedDate, :lastModifiedBy)";
	}


}
