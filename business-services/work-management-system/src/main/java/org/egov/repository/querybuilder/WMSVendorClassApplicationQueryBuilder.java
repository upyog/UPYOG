package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorSubTypeApplicationSearchCriteria;
import org.egov.web.models.WMSVendorClassApplicationSearchCriteria;
import org.egov.web.models.WMSVendorTypeApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSVendorClassApplicationQueryBuilder {

	
	private static final String BASE_VEND_QUERY = " SELECT vclass.vendor_class_id as cVendorClassId, vclass.vendor_class_name as cVendorClassName, vclass.vendor_class_status as cVendorClassStatus,vclass.createdby as cCreatedBy, vclass.lastmodifiedby as cLastmodifiedby, vclass.createdtime as cCreatedtime, vclass.lastmodifiedtime as cLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";
    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM vendor_class vclass";

    private final String ORDERBY_CREATEDTIME = " ORDER BY vclass.vendor_class_status DESC ";

    public String getVendorClassApplicationSearchQuery(WMSVendorClassApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_VEND_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getVendorClassId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" vclass.vendor_class_id IN ( ").append(createQuery(criteria.getVendorClassId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getVendorClassId());
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

    private String createQuery(List<String> list) {
        StringBuilder builder = new StringBuilder();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }
    
    

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
    
    
}
