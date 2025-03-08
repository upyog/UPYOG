package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.upyog.chb.web.models.User;


@Component
public class UserDetailMapper implements ResultSetExtractor<User> {
	@Override
	public User extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		User userDetail = new User();
		while (rs.next()) {
			userDetail = User.builder().userName(rs.getString("username")).name(rs.getString("name")).build();
		};
		return userDetail;
	}

}