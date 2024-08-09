package digit.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import digit.bmc.model.BmcRegistrationApplication;

public class BmcApplicationRowMapper implements ResultSetExtractor<List<BmcRegistrationApplication>>{

	@Override
	public List<BmcRegistrationApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
