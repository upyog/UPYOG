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

import static org.egov.infra.utils.ApplicationConstant.CITY_CODE_KEY;
import static org.egov.infra.web.utils.WebUtils.extractRequestDomainURL;
import static org.egov.infra.web.utils.WebUtils.extractRequestedDomainName;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.ICityService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.core.EnvironmentSettings;
import org.egov.infra.rest.support.MultiReadRequestWrapper;
import org.egov.infra.utils.TenantUtils;
import org.egov.infra.validation.exception.ApplicationRestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Servlet {@link Filter} responsible for dynamic tenant identification and
 * multi-tenant context resolution.
 * <p>
 * This filter inspects incoming HTTP requests, determines the target urban
 * local body (ULB) / city tenant
 * based on domain names, URL paths, query parameters, JSON request payloads, or
 * multipart form fields, and initializes
 * the thread-local execution context via {@link ApplicationThreadLocals}.
 * </p>
 *
 * <p>
 * <b>Key Responsibilities:</b>
 * </p>
 * <ul>
 * <li>Wraps incoming HTTP requests in {@link MultiReadRequestWrapper} for
 * reusable request body consumption</li>
 * <li>Extracts domain URL and host name to set schema and domain properties in
 * {@link ApplicationThreadLocals}</li>
 * <li>Resolves tenant identifiers for REST and OAuth2 endpoints across
 * single-tenant and state-level URLs</li>
 * <li>Safely extracts {@code tenantId} from {@code multipart/form-data}
 * requests (e.g. DXF uploads) via {@code getPart("edcrRequest")}
 * without consuming the raw file stream before DispatcherServlet</li>
 * <li>Validates the resolved tenant against configured tenant mappings, setting
 * the appropriate city context</li>
 * </ul>
 *
 * @author eGovernments Foundation
 * @see MultiReadRequestWrapper
 * @see ApplicationThreadLocals
 */
public class ApplicationTenantResolverFilter implements Filter {

    /**
     * Environment settings provider for resolving tenant schema configurations.
     */
    @Autowired
    private EnvironmentSettings environmentSettings;

    /**
     * List of configured city codes injected from Spring context.
     */
    @Resource(name = "cities")
    private transient List<String> cities;

    /**
     * Map of configured tenant names to their base URLs or codes.
     */
    public static Map<String, String> tenants = new HashMap<>();

    /**
     * State-level base URL identifier.
     */
    public static String stateUrl;

    /**
     * Utility service for querying tenant configurations and URL mappings.
     */
    @Autowired
    private TenantUtils tenantUtils;

    /**
     * City master service for retrieving city and state metadata.
     */
    @Autowired
    private ICityService cityService;

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTenantResolverFilter.class);

    /**
     * Intercepts the servlet request, resolves the tenant context into
     * {@link ApplicationThreadLocals},
     * and continues the filter chain with the wrapped request.
     *
     * @param request  the incoming {@link ServletRequest}
     * @param response the outgoing {@link ServletResponse}
     * @param chain    the filter execution chain
     * @throws IOException      if an I/O error occurs during filtering
     * @throws ServletException if a servlet error occurs during filtering
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        MultiReadRequestWrapper customRequest = new MultiReadRequestWrapper(req);
        HttpSession session = customRequest.getSession();
        LOG.info("Request URL-->" + customRequest.getRequestURL());
        LOG.info("Request URI-->" + customRequest.getRequestURI());
        String domainURL = extractRequestDomainURL(customRequest, false);
        String domainName = extractRequestedDomainName(customRequest);
        ApplicationThreadLocals.setTenantID(environmentSettings.schemaName(domainName));
        ApplicationThreadLocals.setDomainName(domainName);
        ApplicationThreadLocals.setDomainURL(domainURL);
        prepareRestService(customRequest, session);
        LOG.info("***Tenant ID-->" + ApplicationThreadLocals.getTenantID());
        chain.doFilter(customRequest, response);
    }

    /**
     * Initializes the filter.
     *
     * @param filterConfig the filter configuration object
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // Nothing to be initialized
    }

    /**
     * Destroys the filter and releases any allocated resources.
     */
    @Override
    public void destroy() {
        // Nothing to be cleaned up
    }

    /**
     * Resolves the tenant context for REST and OAuth API requests.
     * <p>
     * For state-level URL requests targeting {@code /edcr/rest/} or
     * {@code /edcr/oauth/}, this method extracts
     * the tenant identifier from multipart form parts (if multipart) or request
     * body / query parameters,
     * validates it against registered tenants, and sets
     * {@link ApplicationThreadLocals#setTenantID(String)}.
     *
     * @param customRequest the wrapped HTTP request
     * @param session       the current HTTP session
     * @throws ApplicationRestException if the tenant identifier is missing or
     *                                  invalid
     */
    private void prepareRestService(MultiReadRequestWrapper customRequest, HttpSession session) {
        if (tenants == null || tenants.isEmpty()) {
            tenants = tenantUtils.tenantsMap();
        }

        // restricted only the state URL to access the rest API
        // LOG.info("***********Enter to set tenant id and custom header**************"
        // + req.getRequestURL().toString());
        String requestURL = new StringBuilder().append(ApplicationThreadLocals.getDomainURL())
                .append(customRequest.getRequestURI()).toString();
        if (requestURL.contains(tenants.get("state"))
                &&
                (requestURL.contains("/edcr/") && (requestURL.contains("/rest/")
                        || requestURL.contains("/oauth/")))) {

            LOG.debug("All tenants from config" + tenants);
            LOG.info("tenants.get(state))" + tenants.get("state"));
            LOG.info("Inside method to set tenant id and custom header");
            String tenantFromBody = StringUtils.EMPTY;
            tenantFromBody = setCustomHeader(requestURL, tenantFromBody, customRequest);

            LOG.info("Tenant from Body***" + tenantFromBody);
            String fullTenant = customRequest.isMultipart() ? tenantFromBody
                    : customRequest.getParameter("tenantId");
            LOG.info("fullTenant***" + fullTenant);
            if (StringUtils.isBlank(fullTenant)) {
                fullTenant = tenantFromBody;
            }
            if (StringUtils.isBlank(fullTenant)) {
                throw new ApplicationRestException("incorrect_request",
                        "RestUrl does not contain tenantId: " + fullTenant);
            }
            String tenant = fullTenant.substring(fullTenant.lastIndexOf('.') + 1, fullTenant.length());
            LOG.info("tenant***" + tenant);
            LOG.info("tenant from rest request =" + tenant);
            LOG.info("City Code from session " + (String) session.getAttribute(CITY_CODE_KEY));
            boolean found = false;
            if (tenant.equalsIgnoreCase("generic") || tenant.equalsIgnoreCase("state")) {
                ApplicationThreadLocals.setTenantID(tenant);
                found = true;
            } else {
                for (String city : tenants.keySet()) {
                    LOG.info("Key :" + city + " ,Value :" + tenants.get(city) + "request tenant" + tenant);

                    if (tenants.get(city).contains(tenant)) {
                        ApplicationThreadLocals.setTenantID(city);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    try {
                        City stateCity = cityService.fetchStateCityDetails();
                        if (stateCity != null && tenant.equalsIgnoreCase(stateCity.getCode())) {
                            ApplicationThreadLocals.setTenantID("state");
                            found = true;
                        }
                    } catch (Exception e) {
                        LOG.warn("Could not fetch state city details from state schema: {}", e.getMessage());
                    }
                }
            }

            if (!found) {
                throw new ApplicationRestException("invalid_tenant", "Invalid Tenant Id: " + tenant);
            }

        }
    }

    /**
     * Extracts the {@code tenantId} from the request body or multipart form fields.
     * <p>
     * - For multipart requests (e.g. file scrutiny uploads), reads ONLY the
     * {@code edcrRequest}
     * form field to preserve the raw binary DXF/PDF stream without buffering heavy
     * CAD files in RAM.
     * - For standard REST requests, reads the cached JSON payload.
     *
     * @param requestURL    the full request URL
     * @param tenantAtBody  the existing tenant string buffer
     * @param customRequest the cached request wrapper
     * @return the extracted tenant identifier from the request body, or unchanged
     *         if not found
     */
    private String setCustomHeader(String requestURL, String tenantAtBody,
            MultiReadRequestWrapper customRequest) {

        if (requestURL.contains("/rest/")) {
            LOG.info("***********Inside method to fetch auth token and tenant from reqbody**************");
            try {
                StringWriter writer = new StringWriter();

                if (customRequest.isMultipart()) {
                    // Multipart/form-data: the edcrRequest JSON is a named form field.
                    jakarta.servlet.http.Part edcrPart = customRequest.getPart("edcrRequest");
                    if (edcrPart != null) {
                        IOUtils.copy(edcrPart.getInputStream(), writer, StandardCharsets.UTF_8);
                    } else {
                        LOG.warn(
                                "edcrRequest part not found in multipart request; tenant cannot be extracted at filter time.");
                    }
                } else {
                    // Non-multipart: read from the cached JSON input stream.
                    IOUtils.copy(customRequest.getInputStream(), writer, StandardCharsets.UTF_8);
                }

                String jsonContent = writer.toString();

                // Null/blank check after parsing jsonContent
                if (StringUtils.isNotBlank(jsonContent)) {
                    Pattern p = Pattern.compile("\"tenantId\"\\s*:\\s*\"([^\"]+)\"");
                    Matcher m = p.matcher(jsonContent);
                    if (m.find()) {
                        String extractedTenant = m.group(1);
                        if (StringUtils.isNotBlank(extractedTenant)) {
                            tenantAtBody = extractedTenant.trim();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("############Tenant From Body######" + tenantAtBody);
                            }
                        }
                    } else {
                        LOG.warn("tenantId was not found in the parsed edcrRequest / request payload.");
                    }
                } else {
                    LOG.warn("edcrRequest / request payload is empty or null; cannot extract tenantId.");
                }
            } catch (Exception e) {
                LOG.error("Error occurred, while parsing request body for tenantId", e);
            }
        }
        return tenantAtBody;
    }

}
