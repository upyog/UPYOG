package org.egov.pt.repository.rowmapper;

import org.egov.pt.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class PetApplicationRowMapper implements ResultSetExtractor<List<PetRegistrationApplication>> {

	public List<PetRegistrationApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<String, PetRegistrationApplication> petRegistrationApplicationMap = new LinkedHashMap<>();
		while (rs.next()) {
			String uuid = rs.getString("papplicationnumber");
			PetRegistrationApplication petRegistrationApplication = petRegistrationApplicationMap.get(uuid);

			if (petRegistrationApplication == null) {

				Long lastModifiedTime = rs.getLong("plastModifiedTime");
				if (rs.wasNull()) {
					lastModifiedTime = null;
				}
//                Applicant father = Applicant.builder().id(rs.getString("pfatherid")).build();
//                Applicant mother = Applicant.builder().id(rs.getString("pmotherid")).build();

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pcreatedBy"))
						.createdTime(rs.getLong("pcreatedTime")).lastModifiedBy(rs.getString("plastModifiedBy"))
						.lastModifiedTime(lastModifiedTime).build();
				PetDetails petdetails = PetDetails.builder().id(rs.getString("ptid")).petName(rs.getString("ptpetName"))
						.breedType(rs.getString("ptbreedType")).clinicName(rs.getString("ptclinicName"))
						.doctorName(rs.getString("ptdoctorName")).lastVaccineDate(rs.getString("ptlastVaccineDate"))
						.petAge(rs.getString("ptpetAge")).petGender(rs.getString("ptpetGender")).build();

				petRegistrationApplication = PetRegistrationApplication.builder()
						.applicationNumber(rs.getString("papplicationnumber")).tenantId(rs.getString("ptenantid"))
						.id(rs.getString("pid"))
						// .petName(rs.getString("ppetname"))
						.applicantName(rs.getString("papplicantname"))
//                        .babyLastName(rs.getString("pbabylastname"))
						.fatherName("pfathername")
//                        .motherOfApplicant(mother)
						// .doctorName(rs.getString("pdoctorname"))
						// .hospitalName(rs.getString("phospitalname"))
						// .breedName(rs.getString("pbreedname"))
						// .petAge(rs.getInt("ppetage"))
						.petDetails(petdetails).auditDetails(auditdetails).build();
			}
			addPetRegistrationDetails(rs, petRegistrationApplication);
			petRegistrationApplicationMap.put(uuid, petRegistrationApplication);
		}
		return new ArrayList<>(petRegistrationApplicationMap.values());
	}

	private void addPetRegistrationDetails(ResultSet rs, PetRegistrationApplication petRegistrationApplication)
			throws SQLException {
		addAddressToApplication(rs, petRegistrationApplication);
	}

	private void addAddressToApplication(ResultSet rs, PetRegistrationApplication petRegistrationApplication)
			throws SQLException {
		Address address = Address.builder().id(rs.getString("aid")).tenantId(rs.getString("atenantid"))
				.doorNo(rs.getString("adoorno")).latitude(rs.getDouble("alatitude"))
				.longitude(rs.getDouble("alongitude")).buildingName(rs.getString("abuildingname"))
				.addressId(rs.getString("aaddressid")).addressNumber(rs.getString("aaddressnumber"))
				.type(rs.getString("atype")).addressLine1(rs.getString("aaddressline1"))
				.addressLine2(rs.getString("aaddressline2")).landmark(rs.getString("alandmark"))
				.street(rs.getString("astreet")).city(rs.getString("acity")).pincode(rs.getString("apincode"))
				.detail("adetail").registrationId("aregistrationid").build();

		petRegistrationApplication.setAddress(address);
	}
}