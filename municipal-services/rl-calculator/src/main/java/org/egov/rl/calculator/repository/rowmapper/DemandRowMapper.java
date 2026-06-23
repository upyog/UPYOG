package org.egov.rl.calculator.repository.rowmapper;

import org.egov.rl.calculator.web.models.Owner;
import org.egov.rl.calculator.web.models.demand.Demand;
import org.egov.rl.calculator.web.models.demand.DemandDetail;
import org.egov.rl.calculator.web.models.property.AuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DemandRowMapper implements ResultSetExtractor<List<Demand>> {

	@Override
	public List<Demand> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<Demand> demandList = new ArrayList<>();
		while (rs.next()) {
			Long fixedBillExpiryDate = rs.getLong("fixedbillexpirydate");
			if (rs.wasNull()) {
				fixedBillExpiryDate = null;
			}

			Long billExpiryTime = rs.getLong("billexpirytime");
			if (rs.wasNull()) {
				billExpiryTime = null;
			}

			Long createdTime = rs.getLong("createdtime");
			if (rs.wasNull()) {
				createdTime = null;
			}

			Long lastModifiedTime = rs.getLong("lastmodifiedtime");
			if (rs.wasNull()) {
				lastModifiedTime = null;
			}

			AuditDetails auditDetails = AuditDetails.builder()
					.createdBy(rs.getString("createdby"))
					.createdTime(createdTime)
					.lastModifiedBy(rs.getString("lastmodifiedby"))
					.lastModifiedTime(lastModifiedTime)
					.build();

			// Map the basic fields of the Demand object
			demandList.add(Demand.builder().id(rs.getString("id"))
					.ispaymentcompleted(rs.getBoolean("ispaymentcompleted"))
					.consumerCode(rs.getString("consumercode"))
					.consumerType(rs.getString("consumertype"))
					.tenantId(rs.getString("tenantid"))
					.payer(Owner.builder().uuid(rs.getString("payer")).build())
					.taxPeriodFrom(rs.getLong("taxperiodfrom"))
					.taxPeriodTo(rs.getLong("taxperiodto"))
					.businessService(rs.getString("businessservice"))
					.fixedbillexpirydate(fixedBillExpiryDate)
					.billExpiryTime(billExpiryTime)
					.auditDetails(auditDetails)
					.minimumAmountPayable(rs.getBigDecimal("minimumamountpayable"))
					.status(Demand.DemandStatusEnum.valueOf(rs.getString("status"))).build());
		}
		return demandList;
	}
}
