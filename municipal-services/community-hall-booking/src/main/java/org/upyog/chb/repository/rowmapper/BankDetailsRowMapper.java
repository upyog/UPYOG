package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.BankDetails;

@Component
public class BankDetailsRowMapper implements ResultSetExtractor<List<BankDetails>> {

	@Override
	public List<BankDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<BankDetails> bankDetails = new ArrayList<BankDetails>();

		while (rs.next()) {
			/**
			 * bank_detail_id, booking_id, account_no, ifsc_code, bank_name,
			 * bank_branch_name, account_holder_name, refund_status, createdby,
			 * lastmodifiedby, createdtime, lastmodifiedtime
			 */

			AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
					.createdTime(rs.getLong("createdtime")).lastModifiedBy(rs.getString("lastmodifiedby"))
					.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

			BankDetails details = BankDetails.builder().bankDetailId(rs.getString("bank_detail_id"))
					.bookingId(rs.getString("booking_id")).accountNumber(rs.getString("account_no"))
					.ifscCode(rs.getString("ifsc_code")).bankName(rs.getString("bank_name"))
					.bankBranchName(rs.getString("bank_branch_name"))
					.accountHolderName(rs.getString("account_holder_name")).refundStatus(rs.getString("refund_status"))
					.auditDetails(auditdetails).build();
			bankDetails.add(details);
		}
		return bankDetails;
	}

}
