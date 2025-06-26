package org.egov.user.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.user.domain.model.UserSso;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


@Component
public class UserSsoRowMapper implements RowMapper<UserSso> {

    @Override
    public UserSso mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return UserSso.builder()
            .id(rs.getLong("id"))
            .ssoId(rs.getLong("sso_id"))
            .userUuid(rs.getString("user_uuid"))
            .build();
    }
}
