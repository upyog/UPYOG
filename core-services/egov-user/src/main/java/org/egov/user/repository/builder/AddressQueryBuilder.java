package org.egov.user.repository.builder;

import org.egov.user.domain.model.AddressSearchCriteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressQueryBuilder {

    public static final String GET_ADDRESSBY_IDAND_TENANT = "select * from eg_user_address where userid=:userId and tenantid=:tenantId";

    public static final String DELETE_ADDRESS = "delete from eg_user_address where id IN (:id) ";

    // Adding new fields in Address select query: address2, houseNumber, houseName, streetName, landmark, locality
    private static final String GET_ADDRESS_BY_USER_UUID_V2 = "select uadd.id as id, uadd.type as type, uadd.address as address, uadd.city as city, uadd.pincode as pincode, uadd.userid as userid,\n" +
            "uadd.tenantid as tenantid, uadd.createddate as createddate, uadd.lastmodifieddate as lastmodifieddate, uadd.createdby as createdby,\n" +
            "uadd.address2, uadd.houseNumber, uadd.houseName, uadd.streetName, uadd.landmark, uadd.locality, " +
            "uadd.lastmodifiedby as lastmodifiedby from eg_user_address uadd \n" ;
    // Adding new fields in Address insert query: address2, houseNumber, houseName, streetName, landmark, locality
    public static final String INSERT_ADDRESS_BYUSERID_V2 = "insert into eg_user_address (id,type,address,city,pincode,userid,tenantid,createddate,lastmodifieddate,createdby,lastmodifiedby, address2, houseNumber, houseName, streetName, landmark, locality, status) "
            + "values(:id,:type,:address,:city,:pincode,:userid,:tenantid,:createddate,:lastmodifieddate,:createdby,:lastmodifiedby, :address2, :houseNumber, :houseName, :streetName, :landmark, :locality, :status)";

    // To update the address status with last modified date and last modified by
    public static final String UPDATE_ADDRESS_STATUS = "update eg_user_address set status=:status, lastmodifieddate=:lastmodifieddate, lastmodifiedby=:lastmodifiedby where id=:addressid";

    public String getAddressQuery(AddressSearchCriteria addressSearchCriteria, final List prepareStatementValues) {
        final StringBuilder selectQuery;
        selectQuery = new StringBuilder(GET_ADDRESS_BY_USER_UUID_V2);
        addWhereClause(addressSearchCriteria, selectQuery, prepareStatementValues);

        return selectQuery.toString();
    }

    private void addWhereClause(AddressSearchCriteria addressSearchCriteria, StringBuilder selectQuery, List prepareStatementValues) {

        if (addressSearchCriteria.getUserUuid() != null) {
            addClauseIfRequired(prepareStatementValues, selectQuery);
            selectQuery.append(" uadd.userid = (select id from eg_user where uuid = ?) ");
            prepareStatementValues.add(addressSearchCriteria.getUserUuid());
        }
        if (addressSearchCriteria.getUserId() != null) {
            addClauseIfRequired(prepareStatementValues, selectQuery);
            selectQuery.append(" uadd.userid = ?");
            prepareStatementValues.add(addressSearchCriteria.getUserId());
        }
        if (addressSearchCriteria.getTenantId() != null) {
            addClauseIfRequired(prepareStatementValues, selectQuery);
            selectQuery.append(" uadd.tenantid = ?");
            prepareStatementValues.add(addressSearchCriteria.getTenantId());
        }
        if (addressSearchCriteria.getAddressType() != null) {
            addClauseIfRequired(prepareStatementValues, selectQuery);
            selectQuery.append(" uadd.type = ?");
            prepareStatementValues.add(addressSearchCriteria.getAddressType().toString()); // Convert enum to String
        }
        if (addressSearchCriteria.getCity() != null) {
            addClauseIfRequired(prepareStatementValues, selectQuery);
            selectQuery.append(" uadd.city = ?");
            prepareStatementValues.add(addressSearchCriteria.getCity());
        }
        if (addressSearchCriteria.getPinCode() != null) {
            addClauseIfRequired(prepareStatementValues, selectQuery);
            selectQuery.append(" uadd.pincode = ?");
            prepareStatementValues.add(addressSearchCriteria.getPinCode());
        }
        if (addressSearchCriteria.getId() != null) {
            addClauseIfRequired(prepareStatementValues, selectQuery);
            selectQuery.append(" uadd.id = ?");
            prepareStatementValues.add(addressSearchCriteria.getId());
        }
        if (addressSearchCriteria.getStatus() != null) {
            addClauseIfRequired(prepareStatementValues, selectQuery);
            selectQuery.append(" uadd.status = ?");
            prepareStatementValues.add(addressSearchCriteria.getStatus());
        }

    }

    /**
     * add if clause to the Statement if required or else AND
     *
     * @param values
     * @param queryString
     */
    private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
        if (values.isEmpty())
            queryString.append(" WHERE ");
        else {
            queryString.append(" AND ");
        }
    }

}
