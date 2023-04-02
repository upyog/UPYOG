package org.ksmart.birth.birthnacregistry.repository.rowmapper;


import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.repository.rowmapper.BaseRegRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class NacRegisterRowMapper  implements ResultSetExtractor<List<RegisterNac>>,BaseRegRowMapper{
	@Override
    public List<RegisterNac> extractData(ResultSet rs) throws SQLException, DataAccessException { 
		List<RegisterNac> result = new ArrayList<>();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		 while (rs.next()) {
			 Date regDate = new Date(rs.getLong("registration_date"));
			  Date dobDate = new Date(rs.getLong("ba_dateofbirth"));
			 result.add(RegisterNac.builder()
                     .applicantnameen(rs.getString("ebap_name_en"))
                     .careofapplicantnameen(rs.getString("ebap_address_en"))
                     .applicationtype(rs.getString("ba_applicationtype"))
                     .birthdetailsid(rs.getString("ebap_birthdtlid"))
                     .childnameen(rs.getString("ba_firstname_en"))
                     .mothernameen(rs.getString("mo_firstname_en"))
                     .registrationno(rs.getString("ba_registrationno"))                     
                     .PlaceOfBirthId(rs.getString("pla_placeofbirthid"))
                     .HospitalId(rs.getString("pla_hospitalid"))
                     .InstitutionId(rs.getString("pla_institution_id"))
                     .birthdistrictid(rs.getString("ba_registrationno"))
                     .birthvillageid(rs.getString("ba_registrationno"))
                     .birthstateid(rs.getString("ba_registrationno"))
                     .dateofbirth(rs.getLong("ba_dateofbirth"))
                     .dobStr(formatter.format(dobDate))
                     .tenantid(rs.getString("ba_tenantid"))
                     .auditDetails(getAuditDetails(rs))
                     .registrationDateStr(formatter.format(regDate))
                     .build());
		 }
		 return result;
	}

}
