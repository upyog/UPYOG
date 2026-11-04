/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.web.filter;

import org.apache.log4j.Logger;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.core.EnvironmentSettings;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import static org.egov.infra.utils.ApplicationConstant.MS_TENANTID_KEY;
import static org.egov.infra.web.utils.WebUtils.extractRequestDomainURL;
import static org.egov.infra.web.utils.WebUtils.extractRequestedDomainName;

/*
 * Multi-Jurisdiction & Single-URL Architecture Tenant Resolution:
 * -------------------------------------------------------------
 * 1. Single URL Support: Eliminates legacy subdomain dependency (e.g. citya.egov.org -> niuatt.niua.in).
 * 2. Dynamic Jurisdiction Switching:
 *    - When a multi-jurisdiction employee switches cities in upyog-ui (e.g., City A -> City B), the incoming
 *      entry request carries the target 'tenantId' as a request parameter.
 *    - To prevent the user from being locked into the old tenant stored in the active HTTP/Redis session,
 *      this filter gives highest priority to the incoming request parameter 'tenantId'.
 *    - The filter immediately updates the HTTP/Redis session with the new tenant ID (session.setAttribute)
 *      so that the active session is dynamically synchronized across the distributed cluster.
 * 3. Session Fallback for Internal AJAX / Dropdown APIs:
 *    - Legacy JSP/Struts/AJAX calls (like /designations, /departments, bill searches) do not explicitly send
 *      'tenantId' in the request parameters.
 *    - For these subsequent requests, the filter seamlessly falls back to reading 'ms_tenant_id' from the
 *      active session, ensuring complete data isolation within the selected ULB's PostgreSQL schema.
 * 4. Fallback to Default Schema: Unauthenticated public requests route to the default schema.
 */

public class ApplicationTenantResolverFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(ApplicationTenantResolverFilter.class);

    @Autowired
    private EnvironmentSettings environmentSettings;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        String domainURL = extractRequestDomainURL(httpRequest, false);
        String domainName = extractRequestedDomainName(domainURL);

        String userTenantId = null;
        String schemaName = null;

        // Step 1: Check request parameters first (Handles UI entry, redirects, and Multi-Jurisdiction city switching)
        String reqTenantId = httpRequest.getParameter("tenantId");
        if (reqTenantId == null || reqTenantId.isEmpty()) {
            reqTenantId = httpRequest.getParameter("tenant_id");
        }

        if (reqTenantId != null && !reqTenantId.isEmpty()) {
            userTenantId = reqTenantId;
            LOGGER.info(" *** User Tenant ID from request parameter: " + userTenantId);
            
            // Live Session Sync: Update the active HTTP/Redis session so subsequent internal AJAX calls inherit this tenant
            if (session != null) {
                session.setAttribute(MS_TENANTID_KEY, userTenantId);
            }
        } else if (session != null) {
            // Step 2: Fallback to session for internal AJAX, bill registers, dropdown fetches, and static resources
            userTenantId = (String) session.getAttribute(MS_TENANTID_KEY);
            LOGGER.info(" *** User Tenant ID from session: " + userTenantId);
        }

        // Step 3: Determine schema based on resolved tenant ID (e.g., "pg.citya" -> schema "citya")
        if (userTenantId != null && !userTenantId.isEmpty()) {
            String cityCode = extractCityCodeFromTenantId(userTenantId);
            schemaName = cityCode; // Use city code directly as schema name
            LOGGER.info(" *** Schema resolved from user tenant: " + schemaName);
        } else {
            // Fallback to default schema for unauthenticated requests
            schemaName = environmentSettings.defaultSchemaName();
            LOGGER.info(" *** Using default schema (no user session): " + schemaName);
        }

        // Step 4: Bind resolved tenant context to ApplicationThreadLocals for Hibernate / DB DataSource routing
        ApplicationThreadLocals.setTenantID(schemaName);
        ApplicationThreadLocals.setCollectionVersion(environmentSettings.collectionVersion());
        ApplicationThreadLocals.setDomainName(domainName);
        ApplicationThreadLocals.setDomainURL(domainURL);

        LOGGER.info(" *** Schema name: " + schemaName);
        LOGGER.info(" *** User Tenant ID: " + userTenantId);
        LOGGER.info(" *** domainName: " + domainName);
        LOGGER.info(" *** domainURL: " + domainURL);

        chain.doFilter(request, response);
    }

    /**
     * Extract city code from tenant ID
     * Example: "pg.citya" -> "citya"
     * Example: "citya" -> "citya"
     */
    private String extractCityCodeFromTenantId(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            return null;
        }
        String[] parts = tenantId.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1]; // Return "citya" from "pg.citya"
        }
        return tenantId;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        //Nothing to be initialized
    }

    @Override
    public void destroy() {
        //Nothing to be cleaned up
    }
}