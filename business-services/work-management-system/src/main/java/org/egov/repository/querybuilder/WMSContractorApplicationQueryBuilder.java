package org.egov.repository.querybuilder;

import java.util.List;


import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSContractorApplicationQueryBuilder {

	
	private static final String BASE_CONTR_QUERY = " SELECT contr.vendor_id as vVendorId, contr.vendor_type as vVendorType, contr.vendor_sub_type as vVendorSubType, contr.vendor_name as vVendorName, contr.vendor_status as vVendorStatus, contr.pfms_vendor_code as vPfmsVendorCode, contr.payto as vPayTo, contr.mobile_number as vMobileNumber, contr.email as vEmail, contr.uid_number as vUidNumber, contr.gst_number as vGstNumber, contr.pan_number as vPanNumber, contr.bank_branch_ifsc_code as vBankBranchIfscCode, contr.bank_account_number as vBankAccountNumber,contr.function as vFunction,contr.primary_account_head as vPrimaryAccountHead,contr.vendor_class as vVendorClass,contr.address as vAddress,contr.epfo_account_number as vEpfoAccountNumber,contr.vat_number as vVatNumber,contr.allow_direct_payment as vAllowDirectPayment,contr.createdby as vCreatedBy, contr.lastmodifiedby as vLastmodifiedby, contr.createdtime as vCreatedtime, contr.lastmodifiedtime as vLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM contractor contr";

    private final String ORDERBY_CREATEDTIME = " ORDER BY contr.vendor_type DESC ";

    public String getContractorApplicationSearchQuery(WMSContractorApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_CONTR_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getVendorId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" contr.vendor_id IN ( ").append(createQuery(criteria.getVendorId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getVendorId());
        }
        
        if(!ObjectUtils.isEmpty(criteria.getVendorName())){
       	 addClauseIfRequired(query, preparedStmtList);
            query.append(" contr.vendor_name IN ( ").append(createQueryString(criteria.getVendorName())).append(" ) ");
            addToPreparedStatementString(preparedStmtList, criteria.getVendorName());
       }
        
        if(!ObjectUtils.isEmpty(criteria.getPFMSVendorCode())){
          	 addClauseIfRequired(query, preparedStmtList);
               query.append(" contr.pfms_vendor_code IN ( ").append(createQueryString(criteria.getPFMSVendorCode())).append(" ) ");
               addToPreparedStatementString(preparedStmtList, criteria.getPFMSVendorCode());
          }

        // order birth registration applications based on their createdtime in latest first manner
        query.append(ORDERBY_CREATEDTIME);

        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList){
        if(preparedStmtList.isEmpty()){
            query.append(" WHERE ");
        }else{
            query.append(" AND ");
        }
    }

    private String createQuery(List<Integer> list) {
        StringBuilder builder = new StringBuilder();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }
    
    private String createQueryString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<Integer> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
    
    private void addToPreparedStatementString(List<Object> preparedStmtList, List<String> names) {
    	names.forEach(name -> {
            preparedStmtList.add(name);
        });
    }
}
