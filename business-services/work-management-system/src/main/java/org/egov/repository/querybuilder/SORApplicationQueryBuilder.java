package org.egov.repository.querybuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.egov.web.models.SORApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SORApplicationQueryBuilder {

	
	private static final String BASE_BTR_QUERY = " SELECT sor.sor_id as sSorId, sor.sor_name as ssorName, sor.start_date as sstartDate, sor.end_date as sendDate, sor.chapter as schapter, sor.item_no as sitemNo, sor.description_of_item as sdescOfItem, sor.unit as sunit, sor.rate as srate , sor.createdby as screatedBy, sor.lastmodifiedby as slastmodifiedby, sor.createdtime as screatedtime, sor.lastmodifiedtime as slastmodifiedtime";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM state_sor sor ";

    private final String ORDERBY_CREATEDTIME = " ORDER BY sor.end_date DESC ";
	
    public String getSORApplicationSearchQuery(SORApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_BTR_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);
        
        if(!ObjectUtils.isEmpty(criteria.getSorName())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" sor.sor_name Like '%"+criteria.getSorName()+"%' ");
            // preparedStmtList.add("'%"+criteria.getSorName()+"%'");
        }
        if(!ObjectUtils.isEmpty(criteria.getSorStartDate())){
        	//addClauseIfRequired(query, preparedStmtList);
            query.append(" AND TO_DATE(sor.end_date, 'YYYY-MM-DD') BETWEEN TO_DATE('"+criteria.getSorStartDate()+"', 'YYYY-MM-DD') ");
            //preparedStmtList.add("'"+criteria.getSorStartDate()+"'");
       }
        if(!ObjectUtils.isEmpty(criteria.getSorEndDate())){
            query.append(" AND TO_DATE('"+criteria.getSorEndDate()+"', 'YYYY-MM-DD') ");
            //preparedStmtList.add("'"+criteria.getSorEndDate()+"'");
       }

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
