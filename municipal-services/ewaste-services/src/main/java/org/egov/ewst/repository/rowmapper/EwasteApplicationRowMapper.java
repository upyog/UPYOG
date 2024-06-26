package org.egov.ewst.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.ewst.models.Address;
import org.egov.ewst.models.Applicant;
import org.egov.ewst.models.AuditDetails;
import org.egov.ewst.models.Document;
import org.egov.ewst.models.EwasteApplication;
import org.egov.ewst.models.EwasteDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class EwasteApplicationRowMapper implements ResultSetExtractor<List<EwasteApplication>> {
	public List<EwasteApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, EwasteApplication> ewasteMap = new LinkedHashMap<>();
		while (rs.next()) {
			String uuid = rs.getString("rqrequestid");
			EwasteApplication ewasteApplication = ewasteMap.get(uuid);

			if (ewasteApplication == null) {
				Long lastModifiedTime = rs.getLong("rqlastmodifiedtime");
				if (rs.wasNull()) {
					lastModifiedTime = null;
				}

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("rqcreatedby"))
						.createdTime(rs.getLong("rqcreatedtime")).lastModifiedBy(rs.getString("rqlastmodifiedby"))
						.lastModifiedTime(lastModifiedTime).build();

				// Extract Applicant
				Applicant applicant = Applicant.builder().id(rs.getString("appid"))
						.applicantName(rs.getString("appapplicantname")).mobileNumber(rs.getString("appmobilenumber"))
						.altMobileNumber(rs.getString("appaltmobilenumber")).emailId(rs.getString("appemailid"))
						.build();

				// Extract EwasteApplication
				ewasteApplication = EwasteApplication.builder().id(rs.getString("rqid"))
						.requestId(rs.getString("rqrequestid")).calculatedAmount(rs.getString("rqcalculatedamount"))
						.pickUpDate(rs.getString("rqpickupdate")).transactionId(rs.getString("rqtransactionid"))
						.finalAmount(rs.getString("rqfinalamount")).requestStatus(rs.getString("rqrequeststatus"))
						.tenantId(rs.getString("rqtenantid")).vendorUuid(rs.getString("rqvendoruuid"))
						.transactionId(rs.getString("rqtransactionid")).auditDetails(auditdetails).applicant(applicant)
						.build();

				ewasteMap.put(uuid, ewasteApplication);
				addDocToEwasteApplication(rs,ewasteApplication);
			}
			else {
				addDocToEwasteApplication(rs,ewasteApplication);
			}

			addEwasteOverallDetails(rs, ewasteApplication);
		}
		return new ArrayList<>(ewasteMap.values());
	}

	private void addEwasteOverallDetails(ResultSet rs, EwasteApplication ewasteApplication) throws SQLException {
		addAddressToApplication(rs, ewasteApplication);
		addDocToEwasteApplication(rs, ewasteApplication);
		addEwasteDetailsToEwasteApplication(rs, ewasteApplication);
	}

	private void addAddressToApplication(ResultSet rs, EwasteApplication ewasteApplication) throws SQLException {
		Address address = Address.builder().id(rs.getString("adrid")).tenantId(rs.getString("adrtenantid"))
				.doorNo(rs.getString("adrdoorno")).latitude(rs.getDouble("adrlatitude"))
				.longitude(rs.getDouble("adrlongitude")).buildingName(rs.getString("adrbuildingname"))
				.addressId(rs.getString("adraddressid")).addressNumber(rs.getString("adraddressnumber"))
				.type(rs.getString("adrtype")).addressLine1(rs.getString("adraddressline1"))
				.addressLine2(rs.getString("adraddressline2")).landmark(rs.getString("adrlandmark"))
				.street(rs.getString("adrstreet")).city(rs.getString("adrcity")).pincode(rs.getString("adrpincode"))
				.detail(rs.getString("adrdetail")).build();

		ewasteApplication.setAddress(address);
	}

	private void addDocToEwasteApplication(ResultSet rs, EwasteApplication ewasteApplication) throws SQLException {
		String docId = rs.getString("docid");
		List<Document> docs = ewasteApplication.getDocuments();

		if (docId == null)
			return;

		if (docs != null) {
			for (Document doc : docs) {
				if (doc.getId().equals(docId))
					return;
			}
		} else {
			docs = new ArrayList<>();
			ewasteApplication.setDocuments(docs);
		}

		Document doc = Document.builder().documentType(rs.getString("docdocumenttype"))
				.filestoreId(rs.getString("docfilestoreid")).documentUid(rs.getString("docdocumentuid"))
				.id(rs.getString("docid")).build();

		ewasteApplication.addDocumentsItem(doc);
	}

	private void addEwasteDetailsToEwasteApplication(ResultSet rs, EwasteApplication ewasteApplication)
			throws SQLException {
		String ewid = rs.getString("ewdid");
		List<EwasteDetails> eDetails = ewasteApplication.getEwasteDetails();

		if (ewid == null)
			return;

		if (eDetails != null) {
			for (EwasteDetails ed : eDetails) {
				if (ewid.equals(ed.getId())) // ensure ewid is not null before calling equals
					return;
			}
		} else {
			eDetails = new ArrayList<>();
			ewasteApplication.setEwasteDetails(eDetails);
		}

		EwasteDetails ewasteDetails = EwasteDetails.builder().productId(rs.getString("ewdproductid"))
				.productName(rs.getString("ewdproductname")).price(rs.getString("ewdprice"))
				.quantity(rs.getString("ewdquantity")).build();

		ewasteApplication.addEwasteDetailItem(ewasteDetails);
	}
}
