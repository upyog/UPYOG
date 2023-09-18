package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorSubTypeApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSContractorSubTypeApplicationQueryBuilder {

	
	private static final String BASE_CONST_QUERY = " SELECT cstype.contractor_id as cContractorId, cstype.contractor_stype_name as cContractorStypeName, cstype.contractor_stype_status as cContractorStypeStatus,cstype.createdby as cCreatedBy, cstype.lastmodifiedby as cLastmodifiedby, cstype.createdtime as cCreatedtime, cstype.lastmodifiedtime as cLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";
    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM contractor_subtype cstype";

    private final String ORDERBY_CREATEDTIME = " ORDER BY cstype.contractor_stype_status DESC ";

    public String getContractorSubTypeApplicationSearchQuery(WMSContractorSubTypeApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_CONST_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getContractorId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" cstype.contractor_id IN ( ").append(createQuery(criteria.getContractorId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getContractorId());
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
