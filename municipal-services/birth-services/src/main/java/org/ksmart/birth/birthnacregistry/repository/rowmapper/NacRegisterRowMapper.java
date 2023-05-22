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
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Component
public class NacRegisterRowMapper  implements ResultSetExtractor<List<RegisterNac>>,BaseRegRowMapper{
	@Override
    public List<RegisterNac> extractData(ResultSet rs) throws SQLException, DataAccessException { 
		List<RegisterNac> result = new ArrayList<>();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calenderBefore = Calendar.getInstance(), calenderAfter = Calendar.getInstance();
		 while (rs.next()) {
			  Date regDate = new Date(rs.getLong("bn_registration_date"));
			  Date dobDate = new Date(rs.getLong("bn_dateofbirth"));
			 calenderBefore.setTime(dobDate);
			 calenderAfter.setTime(dobDate);
			 calenderBefore.add(Calendar.YEAR, -3);
			 calenderAfter.add(Calendar.YEAR, 3);
			 Date beforeDate = calenderBefore.getTime();
			 Date afterDate = calenderAfter.getTime();
			 Long currentTime = Long.valueOf(System.currentTimeMillis());
			 result.add(RegisterNac.builder()
					 .id(rs.getString("bn_id"))
					 .dateOfReport(rs.getLong("bn_dateofreport"))
                     .applicantnameen(rs.getString("bn_applicant_name_en"))
                     .careofapplicantnameen(rs.getString("bn_care_of_applicant_name_en"))
                     .applicationtype(rs.getString("bn_applicationtype"))
                     .birthdetailsid(rs.getString("bn_birthdetailsid"))
                     .childnameen(rs.getString("bn_child_name_en"))
                     .mothernameen(rs.getString("bn_mother_name_en"))
                     .registrationno(rs.getString("bn_registrationno"))  
                     .registrationDate(rs.getLong("bn_registration_date"))
                     .PlaceOfBirthId(rs.getString("bn_birth_place_en"))
                     .ackNumber(rs.getString("bn_ack_no"))
                     .fathernameen(rs.getString("bn_father_name_en"))
                     .permenantAddDetails(rs.getString("bn_perm_address"))
                     .birthdistrictid(rs.getString("bn_birth_districtid"))
                     .birthvillageid(rs.getString("bn_birth_villageid"))
                     .birthstateid(rs.getString("bn_birth_stateid"))
                     .dateofbirth(rs.getLong("bn_dateofbirth"))
                     .dobStr(formatter.format(dobDate))
                     .tenantid(rs.getString("bn_tenantid"))
                     .auditDetails(getAuditDetails(rs))
                     .registrationDateStr(formatter.format(regDate))
					 .period(formatter.format(beforeDate) + " to " + formatter.format(afterDate))
					 .dateofissue(currentTime)
					 .isBirthNAC(rs.getBoolean("isnac"))
					 .isBirthNIA(rs.getBoolean("isnia"))
                     .build());
		 }
		 return result;
	}

}
