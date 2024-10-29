package org.upyog.sv.repository.rowmapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.sv.web.models.*;
import org.upyog.sv.web.models.common.AuditDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class StreetVendingApplicationRowMapper implements ResultSetExtractor<List<StreetVendingDetail>> {

	public List<StreetVendingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, StreetVendingDetail> streetVendingApplicationMap = new LinkedHashMap<>();

		while (rs.next()) {
			String applicationId = rs.getString("SVAPPLICATIONID");
			StreetVendingDetail streetVendingDetail = streetVendingApplicationMap.get(applicationId);

			if (streetVendingDetail == null) {
				Long lastModifiedTime = rs.getLong("SVLASTMODIFIEDTIME");
				if (rs.wasNull())
					lastModifiedTime = null;

				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("SVCREATEDBY"))
						.createdTime(rs.getLong("SVCREATEDTIME")).lastModifiedBy(rs.getString("SVLASTMODIFIEDBY"))
						.lastModifiedTime(lastModifiedTime).build();

				streetVendingDetail = StreetVendingDetail.builder().applicationId(applicationId)
						.tenantId(rs.getString("SVTENANTID")).applicationNo(rs.getString("SVAPPLICATIONNO"))
						.applicationDate(rs.getLong("SVAPPLICATIONDATE")).certificateNo(rs.getString("SVCERTIFICATENO"))
						.approvalDate(rs.getLong("SVAPPROVALDATE"))
						.applicationStatus(rs.getString("SVAPPLICATIONSTATUS"))
						.tradeLicenseNo(rs.getString("SVTRADELICENSENO"))
						.vendingActivity(rs.getString("SVVENDINGACTIVITY")).vendingZone(rs.getString("SVVENDINGZONE"))
						.cartLatitude(rs.getBigDecimal("SVCARTLATITUDE"))
						.cartLongitude(rs.getBigDecimal("SVCARTLONGITUDE")).vendingArea(rs.getInt("SVVENDINGAREA"))
						.localAuthorityName(rs.getString("SVLOCALAUTHORITYNAME"))
						.vendingLicenseCertificateId(rs.getString("SVVENDINGLICENSECERTIFICATEID"))
						.disabilityStatus(rs.getString("SVDISABILITYSTATUS"))
						.benificiaryOfSocialSchemes(rs.getString("SVBENEFICIARYOFSOCIALSCHEMES"))
						.termsAndCondition(rs.getString("SVTERMSANDCONDITION")).auditDetails(auditDetails)
						.addressDetails(new ArrayList<>()).documentDetails(new ArrayList<>())
						.vendorDetail(new ArrayList<>()).vendingOperationTimeDetails(new ArrayList<>()).build();

				streetVendingApplicationMap.put(applicationId, streetVendingDetail);
			}

			addAddressToApplication(rs, streetVendingDetail);
			addBankDetailsToApplication(rs, streetVendingDetail);
			addVendorDetailsToApplication(rs, streetVendingDetail);
			addDocumentsToApplication(rs, streetVendingDetail);
			addVendingOperationTimeDetailsToApplication(rs, streetVendingDetail);
		}

		return new ArrayList<>(streetVendingApplicationMap.values());
	}

	private void addAddressToApplication(ResultSet rs, StreetVendingDetail streetVendingDetail) throws SQLException {
		String addressId = rs.getString("ADDRESSID");
		if (addressId != null && streetVendingDetail.getAddressDetails().stream()
				.noneMatch(address -> address.getAddressId().equals(addressId))) {
			Address address = Address.builder().addressId(addressId).vendorId(rs.getString("ADDRESSVENDORID"))
					.houseNo(rs.getString("ADDRESSHOUSENO")).addressLine1(rs.getString("ADDRESSLINE1"))
					.addressLine2(rs.getString("ADDRESSLINE2")).landmark(rs.getString("ADDRESSLANDMARK"))
					.city(rs.getString("ADDRESSCITY")).cityCode(rs.getString("ADDRESSCITYCODE"))
					.locality(rs.getString("ADDRESSLOCALITY")).localityCode(rs.getString("ADDRESSLOCALITYCODE"))
					.pincode(rs.getString("ADDRESSPINCODE")).addressType(rs.getString("ADDRESSTYPE")).build();
			streetVendingDetail.getAddressDetails().add(address);
		}
	}

	private void addBankDetailsToApplication(ResultSet rs, StreetVendingDetail streetVendingDetail)
			throws SQLException {
		BankDetail bankDetail = BankDetail.builder().id(rs.getString("BANKDETAILID"))
				.applicationId(rs.getString("BANKAPPLICATIONID")).accountNumber(rs.getString("BANKACCOUNTNO"))
				.ifscCode(rs.getString("BANKIFSCCODE")).bankName(rs.getString("BANKNAME"))
				.bankBranchName(rs.getString("BANKBRANCHNAME")).accountHolderName(rs.getString("BANKACCOUNTHOLDERNAME"))
				.build();

		streetVendingDetail.setBankDetail(bankDetail);
	}

	private void addVendorDetailsToApplication(ResultSet rs, StreetVendingDetail streetVendingDetail)
			throws SQLException {
		String vendorId = rs.getString("VENDORID");
		if (vendorId != null && streetVendingDetail.getVendorDetail().stream()
				.noneMatch(vendor -> vendor.getId().equals(vendorId))) {
			VendorDetail vendorDetail = VendorDetail.builder().id(vendorId)
					.applicationId(rs.getString("VENDORAPPLICATIONID")).name(rs.getString("VENDORNAME"))
					.dob(rs.getDate("VENDORDATEOFBIRTH").toLocalDate()).fatherName(rs.getString("VENDORFATHERNAME"))
					.mobileNo(rs.getString("VENDORMOBILENO")).emailId(rs.getString("VENDOREMAILID"))
					.gender(rs.getString("VENDORGENDER").charAt(0))
					.relationshipType(rs.getString("VENDORRELATIONSHIPTYPE")).build();
			streetVendingDetail.getVendorDetail().add(vendorDetail);
		}
	}

	private void addDocumentsToApplication(ResultSet rs, StreetVendingDetail streetVendingDetail) throws SQLException {
		String docId = rs.getString("DOCUMENTDETAILID");
		if (docId == null)
			return;

		if (streetVendingDetail.getDocumentDetails().stream()
				.anyMatch(doc -> doc.getDocumentDetailId().equals(docId))) {
			return;
		}

		DocumentDetail documentDetail = DocumentDetail.builder().documentDetailId(docId)
				.applicationId(rs.getString("DOCUMENTAPPLICATIONID")).documentType(rs.getString("DOCUMENTTYPE"))
				.fileStoreId(rs.getString("DOCUMENTFILESTOREID")).build();

		streetVendingDetail.getDocumentDetails().add(documentDetail);
	}

	private void addVendingOperationTimeDetailsToApplication(ResultSet rs, StreetVendingDetail streetVendingDetail)
			throws SQLException {
		String operationTimeId = rs.getString("OPERATIONTIMEID");
		if (operationTimeId != null && streetVendingDetail.getVendingOperationTimeDetails().stream()
				.noneMatch(opTime -> opTime.getId().equals(operationTimeId))) {
			VendingOperationTimeDetails operationTime = VendingOperationTimeDetails.builder().id(operationTimeId)
					.applicationId(rs.getString("OPERATIONTIMEAPPLICATIONID"))
					.dayOfWeek(DayOfWeek.valueOf(rs.getString("OPERATIONTIMEDAYOFWEEK")))
					.fromTime(rs.getTime("OPERATIONTIMEFROMTIME").toLocalTime())
					.toTime(rs.getTime("OPERATIONTIMETOTIME").toLocalTime()).build();
			streetVendingDetail.getVendingOperationTimeDetails().add(operationTime);
		}
	}
}
