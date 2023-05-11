package org.ksmart.birth.birthnacregistry.repository.rowmapperfornewapplicationnac;

import static org.ksmart.birth.utils.BirthConstants.COUNTRY_CODE;

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
public class RegisterRowMapperForNacApp implements ResultSetExtractor<List<RegisterNac>>, BaseRegRowMapper {

	@Override
	public List<RegisterNac> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<RegisterNac> result = new ArrayList<>();
		
		while (rs.next()) {
			System.out.println("nac reg 11"+rs.getString("per_locality_en"));
			result.add(RegisterNac.builder()
					.dateOfReport(rs.getLong("ba_dateofreport"))
					.applicantnameen(rs.getString("ebap_name_en"))
					.careofapplicantnameen(rs.getString("ebap_care_of_applicant"))
					.applicationtype(rs.getString("ba_applicationtype"))
					.birthdetailsid(rs.getString("ebap_birthdtlid"))
					.childnameen(rs.getString("ba_firstname_en"))
					.mothernameen(rs.getString("mo_firstname_en"))
					.fathernameen(rs.getString("fa_firstname_en"))
					.registrationno(rs.getString("ba_registrationno"))
					.birthplaceen(rs.getString("per_locality_en"))
					.birthdistrictid(rs.getString("per_districtid"))
					.birthtalukid(rs.getString("per_talukid"))
					.birthvillageid(rs.getString("per_villageid"))
					.birthstateid(rs.getString("per_stateid"))
					.dateofbirth(rs.getLong("ba_dateofbirth"))
					.tenantid(rs.getString("ba_tenantid"))
				    .ackNumber(rs.getString("ba_applicationno"))
				    .permenantAddDetails(getPermanentAddressEnByResidenceType(rs))
					.auditDetails(getAuditDetails(rs))
					.build());
		}
			return result;
		}
	
	  private String getPermanentAddressEnByResidenceType(ResultSet rs) throws SQLException {
	        String address = "";
	        if(rs.getString("per_countryid") != null) {
	            if (rs.getString("per_countryid").contains(COUNTRY_CODE)) {
	                address = new StringBuilder().append(rs.getString("per_housename_no_en") == null ? "" : rs.getString("per_housename_no_ml"))
	                        .append(", ")
	                        .append(rs.getString("per_locality_en") == null ? "" : rs.getString("per_locality_en"))
	                        .append(", ")
	                        .append(rs.getString("per_street_name_en") == null ? "" : rs.getString("per_street_name_en"))
	                        .append(", ")
	                        .append(rs.getString("per_poid") == null ? "" : rs.getString("per_poid"))
	                        .append(", ")
	                        .append(rs.getString("per_pinno") == null ? "" : rs.getString("per_pinno"))
	                        .append(", ")
	                        .append(rs.getString("per_districtid") == null ? "" : rs.getString("per_districtid"))
	                        .append(", ")
	                        .append(rs.getString("per_stateid") == null ? "" : rs.getString("per_stateid"))
	                        .append(", ")
	                        .append(rs.getString("per_countryid") == null ? "" : rs.getString("per_countryid")).toString();

	            } else {

	                address = new StringBuilder()
	                        .append(rs.getString("per_ot_address1_en") == null ? "" : rs.getString("per_ot_address1_en"))
	                        .append(", ")
	                        .append(rs.getString("per_ot_address2_en") == null ? "" : rs.getString("per_ot_address2_en"))
	                        .append(", ")
	                        .append(rs.getString("per_ot_state_region_province_en") == null ? "" : rs.getString("per_ot_state_region_province_en"))
	                        .append(", ")
	                        .append(rs.getString("per_ot_zipcode") == null ? "" : rs.getString("per_ot_zipcode"))
	                        .append(", ")
	                        .append(rs.getString("per_countryid") == null ? "" : rs.getString("per_countryid")).toString();
	            }
	        }
	        return address;
	    }
}
