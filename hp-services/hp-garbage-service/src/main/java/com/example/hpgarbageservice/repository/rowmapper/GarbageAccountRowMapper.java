package com.example.hpgarbageservice.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.AuditDetails;
import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageBill;

@Component
public class GarbageAccountRowMapper implements ResultSetExtractor<List<GarbageAccount>> {

	@Override
	public List<GarbageAccount> extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		Map<Long, GarbageAccount> accountsMap = new LinkedHashMap<>();

		 while (rs.next()) {
			 
			 Long accountId = rs.getLong("id");
			 GarbageAccount garbageAccount = accountsMap.get(accountId);

			 if(null == garbageAccount) {
				 
				 AuditDetails audit = AuditDetails.builder()
					        .createdBy(rs.getString("created_by"))
					        .createdDate(rs.getLong("created_date"))
					        .lastModifiedBy(rs.getString("last_modified_by"))
					        .lastModifiedDate(rs.getLong("last_modified_date"))
					        .build();
				 
				 garbageAccount = GarbageAccount.builder()
					        .id(rs.getLong("id"))
					        .garbageId(rs.getLong("garbage_id"))
					        .propertyId(rs.getLong("property_id"))
					        .type(rs.getString("type"))
					        .name(rs.getString("name"))
					        .mobileNumber(rs.getString("mobile_number"))
					        .parentId(rs.getLong("parent_id"))
					        .garbageBills(new ArrayList<>())
					        .auditDetails(audit)
					        .build();
				 
				 accountsMap.put(accountId, garbageAccount);
			 }
			 
			 if(null != rs.getString("bill_id")) {
					String billId = rs.getString("bill_id");
					GarbageBill garbageBill = findBillByUuid(garbageAccount.getGarbageBills(),billId); 
					if(null == garbageBill) {
						GarbageBill garbageBill1 = populateGarbageBill(rs);
						garbageAccount.getGarbageBills().add(garbageBill1);
					}
				}
			 
		 }
		
		
		return new ArrayList<>(accountsMap.values());
	}
	
	private GarbageBill findBillByUuid(List<GarbageBill> garbageBills, String bill_id) {
		
		if (!CollectionUtils.isEmpty(garbageBills)) {
			return garbageBills.stream()
					.filter(bill -> bill.getId().equals(bill_id))
					.findFirst()
					.orElse(null);
		}
		return null;
	}
	
	private GarbageBill populateGarbageBill(ResultSet rs) throws SQLException {

		GarbageBill bill = GarbageBill.builder()
	            .id(rs.getLong("bill_id"))
	            .billRefNo(rs.getString("bill_bill_ref_no"))
	            .garbageId(rs.getLong("bill_garbage_id"))
	            .billAmount(rs.getDouble("bill_bill_amount"))
	            .arrearAmount(rs.getDouble("bill_arrear_amount"))
	            .paneltyAmount(rs.getDouble("bill_panelty_amount"))
	            .discountAmount(rs.getDouble("bill_discount_amount"))
	            .totalBillAmount(rs.getDouble("bill_total_bill_amount"))
	            .totalBillAmountAfterDueDate(rs.getDouble("bill_total_bill_amount_after_due_date"))
	            .billGeneratedBy(rs.getString("bill_bill_generated_by"))
	            .billGeneratedDate(rs.getLong("bill_bill_generated_date"))
	            .billDueDate(rs.getLong("bill_bill_due_date"))
	            .billPeriod(rs.getString("bill_bill_period"))
	            .bankDiscountAmount(rs.getDouble("bill_bank_discount_amount"))
	            .paymentId(rs.getString("bill_payment_id"))
	            .paymentStatus(rs.getString("bill_payment_status"))
	            .auditDetails(AuditDetails.builder()
	                .createdBy(rs.getString("bill_created_by"))
	                .createdDate(rs.getLong("bill_created_date"))
	                .lastModifiedBy(rs.getString("bill_last_modified_by"))
	                .lastModifiedDate(rs.getLong("bill_last_modified_date"))
	                .build())
	            .build();

		return bill;
	}
}
