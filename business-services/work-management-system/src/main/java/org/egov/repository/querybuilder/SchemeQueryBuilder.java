package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SchemeQueryBuilder {

	
	private static final String BASE_SCM_QUERY = " SELECT scm.scheme_id as sSchemeId, scm.start_date as sSchemeStartDate, scm.end_date as sSchemeEndDate, scm.source_of_fund as sSchemeSourceOfFund, scm.scheme_name_en as sSchemeName, scm.scheme_name_reg as sSchemeNameReg, scm.fund as sSchemeFund, scm.description_of_the_scheme as sSchemeDescription, scm.upload_document as sSchemeUploadDocument, scm.createdby as sCreatedBy, scm.lastmodifiedby as sLastmodifiedby, scm.createdtime as sCreatedtime, scm.lastmodifiedtime as sLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM scheme_master scm ";

    private final String ORDERBY_CREATEDTIME = " ORDER BY scm.end_date DESC ";

    public String getSchemeApplicationSearchQuery(SchemeApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_SCM_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" scm.scheme_id IN ( ").append(createQuery(criteria.getId())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getId());
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

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> list) {
        list.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}
