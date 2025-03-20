package org.egov.egf.master.persistence.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.egf.master.domain.enums.BankAccountType;
import org.egov.egf.master.domain.model.Bank;
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
		Fund fund = Fund.builder().id(rs.getString("fundid")).name(rs.getString("efname")).code(rs.getString("efcode"))
				.level(Long.valueOf(rs.getString("eflevel"))).active(rs.getBoolean("efactive")).build();

		fund.setTenantId(rs.getString("eftenantid"));
		return fund;
	}

	private ChartOfAccount mapChartOfAccount(ResultSet rs) throws SQLException {
		ChartOfAccount chartOfAccount = ChartOfAccount.builder().id(rs.getString("chartofaccountid")).build();

		chartOfAccount.setTenantId(rs.getString("ectenantid"));

		return chartOfAccount;
	}

	private BankBranch mapBankBranch(ResultSet rs) throws SQLException {
		BankBranch bankBranch = BankBranch.builder().id(rs.getString("ebbid")).bank(mapBank(rs))
				.code(rs.getString("ebbcode")).name(rs.getString("ebbname")).address(rs.getString("ebbaddress"))
				.address2(rs.getString("ebbaddress2")).city(rs.getString("ebbcity")).state(rs.getString("ebbstate"))
				.pincode(rs.getString("ebbpincode")).phone(rs.getString("ebbphone")).fax(rs.getString("ebbfax"))
				.contactPerson(rs.getString("ebbcontactperson")).active(rs.getBoolean("ebbactive"))
				.description(rs.getString("ebbdescription")).micr(rs.getString("ebbmicr")).build();

		bankBranch.setTenantId(rs.getString("ebbtenantid"));

		return bankBranch;
	}

	private Bank mapBank(ResultSet rs) throws SQLException {
		Bank bank = Bank.builder().id(rs.getString("ebbbankid")).code(rs.getString("ebcode"))
				.name(rs.getString("ebname")).description(rs.getString("ebdescription"))
				.active(rs.getBoolean("ebactive")).type(rs.getString("ebtype")).build();

		bank.setTenantId(rs.getString("ebtenantid"));

		return bank;
	}
}
