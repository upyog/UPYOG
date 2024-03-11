package org.egov.ptr.repository.rowmapper;

import org.egov.ptr.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pcreatedBy"))
						.createdTime(rs.getLong("pcreatedTime")).lastModifiedBy(rs.getString("plastModifiedBy"))
						.lastModifiedTime(lastModifiedTime).build();
				PetDetails petdetails = PetDetails.builder().id(rs.getString("ptid")).petName(rs.getString("ptpetName"))
						.petType(rs.getString("ptpetType")).breedType(rs.getString("ptbreedType"))
						.clinicName(rs.getString("ptclinicName")).doctorName(rs.getString("ptdoctorName"))
						.lastVaccineDate(rs.getString("ptlastVaccineDate"))
						.vaccinationNumber(rs.getString("ptvaccinationNumber")).petAge(rs.getString("ptpetAge"))
						.petGender(rs.getString("ptpetGender")).build();

				petRegistrationApplication = PetRegistrationApplication.builder()
						.applicationNumber(rs.getString("papplicationnumber")).tenantId(rs.getString("ptenantid"))
						.id(rs.getString("pid")).applicantName(rs.getString("papplicantname"))
						.fatherName(rs.getString("pfathername")).emailId(rs.getString("pemailId"))
						.mobileNumber(rs.getString("pmobileNumber")).petDetails(petdetails).auditDetails(auditdetails)
						.build();
				addDocToPetApplication(rs, petRegistrationApplication);

			} else {
				addDocToPetApplication(rs, petRegistrationApplication);
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

	private void addDocToPetApplication(ResultSet rs, PetRegistrationApplication petApplication) throws SQLException {
		String docId = rs.getString("did");
		List<Document> docs = petApplication.getDocuments();

		if (docId == null)
			return;

		if (!CollectionUtils.isEmpty(docs))
			for (Document doc : docs) {
				if (doc.getId().equals(docId))
					return;
			}

		Document doc = Document.builder().documentType(rs.getString("documentType"))
				.filestoreId(rs.getString("dfilestoreId")).documentUid(rs.getString("ddocumentUid")).id(docId).build();

		petApplication.addDocumentsItem(doc);
	}
}