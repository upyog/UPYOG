package org.ksmart.birth.birthnacregistry.repository.rowmapperfornewapplicationnac;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.birthnacregistry.repository.rowmapper.BaseRegRowMapper;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class RegisterRowMapperForNacApp implements ResultSetExtractor<List<RegisterNac>>, BaseRegRowMapper{
	
	@Override
    public List<RegisterNac> extractData(ResultSet rs) throws SQLException, DataAccessException { 
		List<RegisterNac> result = new ArrayList<>();
		 while (rs.next()) {
		 
			 result.add(RegisterNac.builder()
                     .applicantnameen(rs.getString("ebap_name_en"))
                     .careofapplicantnameen(rs.getString("ebap_address_en"))
                     .applicationtype(rs.getString("ba_applicationtype"))
                     .birthdetailsid(rs.getString("ebap_birthdtlid"))
                     .childnameen(rs.getString("ba_firstname_en"))
                     .mothernameen(rs.getString("mo_firstname_en"))
                     .registrationno(rs.getString("ba_registrationno"))
                     .birthplaceen(rs.getString("ba_registrationno"))
                     .birthdistrictid(rs.getString("ba_registrationno"))
                     .birthvillageid(rs.getString("ba_registrationno"))
                     .birthstateid(rs.getString("ba_registrationno"))
                     .dateofbirth(rs.getLong("ba_dateofbirth"))
                     .tenantid(rs.getString("ba_tenantid"))
                     .auditDetails(getAuditDetails(rs))
                     .build());
		 }
		 return result;
	}
}



