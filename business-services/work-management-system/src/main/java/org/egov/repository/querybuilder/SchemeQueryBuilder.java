package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SchemeQueryBuilder {

	
	private static final String BASE_SCM_QUERY = " SELECT scm.Scheme_ID as sSchemeId, scm.Start_Date as sSchemeStartDate, scm.End_Date as sSchemeEndDate, scm.Source_Of_Fund as sSchemeSourceOfFund, scm.Scheme_Name_En as sSchemeName, scm.Scheme_Name_Reg as sSchemeNameReg, scm.Fund as sSchemeFund, scm.Description_Of_the_Scheme as sSchemeDescription, scm.Upload_Document as sSchemeUploadDocument ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM Scheme_Master scm ";

    private final String ORDERBY_CREATEDTIME = " ORDER BY scm.End_Date DESC ";

    public String getSchemeApplicationSearchQuery(SchemeApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_SCM_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getId())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" scm.Scheme_ID IN ( ").append(createQuery(criteria.getId())).append(" ) ");
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

    private String createQuery(List<Long> list) {
        StringBuilder builder = new StringBuilder();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<Long> list) {
        list.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}
