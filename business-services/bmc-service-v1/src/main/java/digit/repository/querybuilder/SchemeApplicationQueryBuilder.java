package digit.repository.querybuilder;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import digit.web.models.SchemeApplicationSearchCriteria;

@Component
public class SchemeApplicationQueryBuilder {

    // Base query to select fields from SchemeApplication table
    private static final String BASE_QUERY = """
            SELECT usa.id as usa_id, usa.applicationNumber as usa_applicationNumber, usa.tenantid as usa_tenantid, usa.optedId as usa_optedId, \
            usa.ApplicationStatus as usa_ApplicationStatus, usa.VerificationStatus as usa_VerificationStatus, usa.FirstApprovalStatus as usa_FirstApprovalStatus, \
            usa.RandomSelection as usa_RandomSelection, usa.FinalApproval as usa_FinalApproval, usa.Submitted as usa_Submitted, usa.ModifiedOn as usa_ModifiedOn, \
            usa.CreatedBy as usa_CreatedBy, usa.ModifiedBy as usa_ModifiedBy, \
            """;

    // Query to select fields from Address table
    private static final String ADDRESS_SELECT_QUERY = """
            addr.ID as addr_id, addr.userid as addr_userid, addr.tenantid as addr_tenantid, \
            addr.Address1 as addr_Address1, addr.Address2 as addr_Address2, addr.Location as addr_Location, \
            addr.Ward as addr_Ward, addr.City as addr_City, addr.District as addr_District, addr.State as addr_State, \
            addr.Country as addr_Country, addr.Pincode as addr_Pincode, \
            """;

    // Query to select fields from User table
    private static final String USER_SELECT_QUERY = """
            u.username as user_username, u.name as user_name, u.emailid as user_email, \
            u.mobilenumber as user_mobile, u.gender as user_gender, u.aadhaarnumber as user_aadhar \
            """;

    // From clause with join between SchemeApplication, Address, and User tables
    private static final String FROM_TABLES = """
            FROM eg_bmc_UserSchemeApplication usa \
            LEFT JOIN eg_bmc_Address addr ON usa.userid = addr.userid AND usa.tenantid = addr.tenantid \
            LEFT JOIN eg_user u ON usa.userid = u.id AND usa.tenantid = u.tenantid \
            """;

    // Order by clause to order results by the ModifiedOn field
    private static final String ORDERBY_MODIFIEDTIME = "ORDER BY usa.ModifiedOn DESC ";

    /**
     * Builds the SQL query for searching SchemeApplications based on the given search criteria.
     *
     * @param criteria The search criteria for SchemeApplications.
     * @param preparedStmtList The list to hold the parameters for the prepared statement.
     * @return The constructed SQL query.
     */
    public String getSchemeApplicationSearchQuery(SchemeApplicationSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        query.append(ADDRESS_SELECT_QUERY);
        query.append(USER_SELECT_QUERY);
        query.append(FROM_TABLES);

        // Add where clause for tenant ID if it is not empty
        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" usa.tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }

        // Add where clause for IDs if they are not empty
        if (!CollectionUtils.isEmpty(criteria.getIds())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" usa.id IN ( ").append(createQuery(criteria.getIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
        }

        // Add where clause for application status if it is not empty
        if (!ObjectUtils.isEmpty(criteria.getApplicationStatus())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" usa.ApplicationStatus = ? ");
            preparedStmtList.add(criteria.getApplicationStatus());
        }

        // Add where clause for application number if it is not empty
        if (!ObjectUtils.isEmpty(criteria.getApplicationNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" usa.applicationNumber = ? ");
            preparedStmtList.add(criteria.getApplicationNumber());
        }
        
        // Append the order by clause to the query
        query.append(ORDERBY_MODIFIEDTIME);

        return query.toString();
    }

    /**
     * Adds a clause to the query if required based on the state of the prepared statement list.
     *
     * @param query The query string builder.
     * @param preparedStmtList The list of parameters for the prepared statement.
     */
    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

    /**
     * Creates a query string with placeholders for the given list of IDs.
     *
     * @param ids The list of IDs.
     * @return The query string with placeholders.
     */
    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    /**
     * Adds the given list of IDs to the prepared statement list.
     *
     * @param preparedStmtList The list of parameters for the prepared statement.
     * @param ids The list of IDs to be added.
     */
    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(preparedStmtList::add);
    }
}
