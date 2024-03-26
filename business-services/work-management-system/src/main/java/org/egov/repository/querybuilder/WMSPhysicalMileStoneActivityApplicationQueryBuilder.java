package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSPhysicalMileStoneActivityApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSPhysicalMileStoneActivityApplicationQueryBuilder {

	
	private static final String BASE_PMA_QUERY = " SELECT pma.pma_id as pPmaId, pma.description_of_the_item as pDescriptionOfTheItem, pma.percentage_weightage as pPercentageWeightage, pma.start_date as pStartDate, pma.end_date as pEndDate, pma.createdby as pCreatedBy, pma.lastmodifiedby as pLastmodifiedby, pma.createdtime as pCreatedtime, pma.lastmodifiedtime as pLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";
    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM physical_milestone_activity pma";

    private final String ORDERBY_CREATEDTIME = " ORDER BY pma.start_date DESC ";

    public String getPhysicalMileStoneActivityApplicationSearchQuery(WMSPhysicalMileStoneActivityApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_PMA_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getPmaId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" pma.pma_id IN ( ").append(createQuery(criteria.getPmaId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getPmaId());
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
