package org.egov.egf.master.persistence.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.egf.master.domain.enums.BankAccountType;
import org.egov.egf.master.domain.model.BankAccount;
import org.egov.egf.master.domain.model.BankBranch;
import org.egov.egf.master.domain.model.ChartOfAccount;
import org.egov.egf.master.domain.model.Fund;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class BankAccountsRowMapper implements RowMapper<BankAccount> {

	@Override
	public BankAccount mapRow(ResultSet rs, int rowNum) throws SQLException {

		BankAccount bankAccount = BankAccount.builder().id(rs.getString("id")).bankBranch(mapBankBranch(rs))
				.chartOfAccount(mapChartOfAccount(rs)).fund(mapFund(rs)).accountNumber(rs.getString("accountnumber"))
				.accountType(rs.getString("accounttype")).description(rs.getString("description"))
				.active(rs.getBoolean("active")).payTo(rs.getString("payto"))
				.type(BankAccountType.valueOf(rs.getString("type"))).build();

		bankAccount.setTenantId(rs.getString("tenantid"));

		return bankAccount;
	}

	private Fund mapFund(ResultSet rs) throws SQLException {
		return Fund.builder().id(rs.getString("fundid")).build();
	}

	private ChartOfAccount mapChartOfAccount(ResultSet rs) throws SQLException {
		return ChartOfAccount.builder().id(rs.getString("chartofaccountid")).build();
	}

	private BankBranch mapBankBranch(ResultSet rs) throws SQLException {
		return BankBranch.builder().id(rs.getString("ebbid")).code(rs.getString("ebbcode"))
				.name(rs.getString("ebbname")).address(rs.getString("ebbaddress")).address2(rs.getString("ebbaddress2"))
				.city(rs.getString("ebbcity")).state(rs.getString("ebbstate")).pincode(rs.getString("ebbpincode"))
				.build();
	}
}
