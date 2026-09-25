package org.egov.demand.repository.rowmapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.demand.web.contract.ShortBillV2;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class ShortBillRowMapperV2 implements ResultSetExtractor<List<ShortBillV2>> {

	@Override
	public List<ShortBillV2> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, ShortBillV2> billMap = new LinkedHashMap<>();

		while (rs.next()) {
			String billId = rs.getString("b_id");
			ShortBillV2 bill = billMap.get(billId);
			BigDecimal amount = rs.getBigDecimal("bd_totalamount");
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}
			if (bill == null) {
				bill = ShortBillV2.builder()
						.id(billId)
						.totalAmount(amount)
						.businessService(rs.getString("bd_businessservice"))
						.billNumber(rs.getString("bd_billno"))
						.billDate(rs.getLong("bd_billdate"))
						.consumerCode(rs.getString("bd_consumercode"))
						.dueDate(rs.getLong("bd_expirydate"))
						.build();
				billMap.put(billId, bill);
			} else {
				bill.setTotalAmount(bill.getTotalAmount().add(amount));
			}
		}
		return new ArrayList<>(billMap.values());
	}
}
