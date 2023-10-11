package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSDepartmentApplicationSearchCriteria;
import org.egov.web.models.WMSTenderCategoryApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSTenderCategoryApplicationQueryBuilder {

	
	private static final String BASE_TCAT_QUERY = " SELECT tcat.category_id as tCategoryId, tcat.category_name as tCategoryName, tcat.category_status as tCategoryStatus, tcat.createdby as tCreatedBy, tcat.lastmodifiedby as tLastmodifiedby, tcat.createdtime as tCreatedtime, tcat.lastmodifiedtime as tLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";
    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM tender_category tcat";

    private final String ORDERBY_CREATEDTIME = " ORDER BY tcat.category_name DESC ";

    public String getTenderCategoryApplicationSearchQuery(WMSTenderCategoryApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_TCAT_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getCategoryId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" tcat.category_id IN ( ").append(createQuery(criteria.getCategoryId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getCategoryId());
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
