package org.ksmart.birth.birthnac.repository.rowmapper; 
import org.ksmart.birth.web.model.birthnac.NacApplicantDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface NacApplicantDetailsRowMapper {
	default NacApplicantDetail  getApplicant(ResultSet rs) throws SQLException {
		return NacApplicantDetail.builder()
				.applicantNameEn(rs.getString("ebap_name_en"))
				.applicantAddressEn(rs.getString("ebap_address_en"))
				.aadharNo(rs.getString("ebap_aadharno"))
				.mobileNo(rs.getString("ebap_mobileno"))
				.isDeclared(rs.getBoolean("ebap_is_declared"))
				.declarationId(rs.getString("ebap_declaration_id"))
				.isEsigned(rs.getBoolean("ebap_is_esigned"))
				.careofapplicant(rs.getString("ebap_care_of_applicant"))
				.build();
		
	}

}
