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
 * Resolves the tenant (ULB schema) from the incoming HTTP request domain or tenant header.
 *
 * <p>City service lookups use the {@link org.egov.infra.admin.master.service.ICityService}
 * interface for proxy-safe dependency injection in servlet filters.</p>
 */
public class ApplicationTenantResolverFilter implements Filter {

    @Autowired
    private EnvironmentSettings environmentSettings;

    @Resource(name = "cities")
    private transient List<String> cities;

    public static Map<String, String> tenants = new HashMap<>();

    public static String stateUrl;

    @Autowired
    private TenantUtils tenantUtils;

    @Autowired
    private ICityService cityService;

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTenantResolverFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        // Wrap ALL requests — including multipart/form-data — in MultiReadRequestWrapper.
        //
        // MultiReadRequestWrapper's constructor now detects multipart requests and
        // deliberately does NOT drain getInputStream() for them. This leaves Undertow's
        // raw channel intact so that getParts() (called later by
        // StandardServletMultipartResolver) can parse the multipart body correctly,
        // allowing @RequestPart("planFile") to resolve as expected.
        //
        // For non-multipart requests the existing body-caching behaviour is preserved
        // unchanged (body eagerly copied to byte[], getInputStream() fully replayable).
        //
        // getParts() / getPart() on the wrapper always delegate to the underlying
        // Undertow request, so multipart parsing is performed by the container —
        // not by this filter — as the Servlet spec intends.
        final MultiReadRequestWrapper requestToUse = new MultiReadRequestWrapper(req);

        HttpSession session = requestToUse.getSession();
        LOG.info("Request URL-->" + requestToUse.getRequestURL());
        LOG.info("Request URI-->" + requestToUse.getRequestURI());
        String domainURL = extractRequestDomainURL(requestToUse, false);
        String domainName = extractRequestedDomainName(requestToUse);
        ApplicationThreadLocals.setTenantID(environmentSettings.schemaName(domainName));
        ApplicationThreadLocals.setDomainName(domainName);
        ApplicationThreadLocals.setDomainURL(domainURL);
        prepareRestService(requestToUse, session);
        LOG.info("***Tenant ID-->" + ApplicationThreadLocals.getTenantID());
        chain.doFilter(requestToUse, response);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // Nothing to be initialized
    }

    @Override
    public void destroy() {
        // Nothing to be cleaned up
    }

    private void prepareRestService(MultiReadRequestWrapper customRequest, HttpSession session) {
        if (tenants == null || tenants.isEmpty()) {
            tenants = tenantUtils.tenantsMap();
        }

        // Gate: state-URL REST/OAuth requests only.
        String requestURL = new StringBuilder().append(ApplicationThreadLocals.getDomainURL())
                .append(customRequest.getRequestURI()).toString();
        if (requestURL.contains(tenants.get("state"))
                && requestURL.contains("/edcr/")
                && (requestURL.contains("/rest/") || requestURL.contains("/oauth/"))) {

            LOG.debug("All tenants from config" + tenants);
            LOG.info("tenants.get(state))" + tenants.get("state"));
            LOG.info("Inside method to set tenant id and custom header");

            String tenantFromBody = StringUtils.EMPTY;

            if (customRequest.isMultipart()) {
                // Multipart/form-data: the edcrRequest JSON is a named form field.
                // At filter time Undertow has not yet fully committed its parsed-parts
                // cache, but getPart() on the underlying request triggers on-demand
                // parsing from the raw channel (which the wrapper constructor left
                // untouched for multipart). We read the "edcrRequest" part here to
                // extract the tenantId before the gate logic below.
                tenantFromBody = extractTenantFromMultipartField(customRequest);
            } else {
                // Non-multipart: read tenantId from the cached JSON body via the
                // existing setCustomHeader() regex approach.
                tenantFromBody = setCustomHeader(requestURL, tenantFromBody, customRequest);
            }

            LOG.info("Tenant from Body***" + tenantFromBody);

            // For non-multipart requests, also try getParameter() as a fallback
            // (query-string or form-urlencoded tenantId).
            String fullTenant = customRequest.isMultipart() ? tenantFromBody
                    : customRequest.getParameter("tenantId");
            if (StringUtils.isBlank(fullTenant)) {
                fullTenant = tenantFromBody;
            }
            LOG.info("fullTenant***" + fullTenant);
            if (StringUtils.isBlank(fullTenant)) {
                throw new ApplicationRestException("incorrect_request", "RestUrl does not contain tenantId: " + fullTenant);
            }

            String tenant = fullTenant.substring(fullTenant.lastIndexOf('.') + 1, fullTenant.length());
            LOG.info("tenant***" + tenant);
            LOG.info("tenant from rest request =" + tenant);
            LOG.info("City Code from session " + (String) session.getAttribute(CITY_CODE_KEY));

            boolean found = false;
            City stateCity = cityService.fetchStateCityDetails();
            if (tenant.equalsIgnoreCase("generic") || tenant.equalsIgnoreCase("state")) {
                ApplicationThreadLocals.setTenantID(tenant);
                found = true;
            } else if (tenant.equalsIgnoreCase(stateCity.getCode())) {
                ApplicationThreadLocals.setTenantID("state");
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
            }
            if (!found) {
                throw new ApplicationRestException("invalid_tenant", "Invalid Tenant Id: " + tenant);
            }
        }
    }

    /**
     * Extracts the {@code tenantId} value from the {@code edcrRequest} JSON
     * form-data part of a multipart/form-data request.
     *
     * <p>The scrutinize, scrutinizeplan, scrutinizeocplan, extractplan, and
     * anonymousScrutinize endpoints all send the tenant identifier inside a
     * JSON string carried in a form field called {@code edcrRequest}, e.g.:
     * <pre>
     *   {"tenantId":"pb.amritsar","RequestInfo":{...},...}
     * </pre>
     * This method reads that part at filter time (before DispatcherServlet)
     * using {@code request.getPart("edcrRequest")}. Undertow parses the
     * multipart body lazily/on-demand when {@code getPart()} is first called,
     * and the wrapper constructor has deliberately left the raw channel
     * unconsumed for multipart requests, so this call succeeds.</p>
     *
     * @param request the multipart request wrapper
     * @return the tenantId value (e.g. {@code "pb.amritsar"}), or an empty
     *         string if the part or field cannot be found/parsed
     */
    private String extractTenantFromMultipartField(MultiReadRequestWrapper request) {
        try {
            jakarta.servlet.http.Part edcrPart = request.getPart("edcrRequest");
            if (edcrPart == null) {
                LOG.warn("edcrRequest part not found in multipart request; tenant cannot be extracted at filter time.");
                return StringUtils.EMPTY;
            }
            StringWriter writer = new StringWriter();
            IOUtils.copy(edcrPart.getInputStream(), writer, StandardCharsets.UTF_8);
            String edcrJson = writer.toString();
            if (StringUtils.isNoneBlank(edcrJson)) {
                Pattern p = Pattern.compile("\"tenantId\"\\s*:\\s*\"([^\"]+)\"");
                Matcher m = p.matcher(edcrJson);
                if (m.find()) {
                    String tenantId = m.group(1);
                    LOG.info("Tenant extracted from multipart edcrRequest field: " + tenantId);
                    return tenantId;
                }
            }
        } catch (Exception e) {
            LOG.error("Error extracting tenantId from multipart edcrRequest field", e);
        }
        return StringUtils.EMPTY;
    }

    /*
     * public Map<String, String> tenantsMap() { URL url; LOG.info("cities" + applicationConfiguration.cities()); try { url = new
     * URL(ApplicationThreadLocals.getDomainURL()); // first get from override properties
     * environment.getPropertySources().iterator().forEachRemaining(propertySource -> { LOG.info( "Property Source" +
     * propertySource.getName() + " Class Name" + propertySource.getClass().getSimpleName()); if
     * (propertySource.getName().contains("egov-erp-override.properties") && propertySource instanceof MapPropertySource) {
     * ((MapPropertySource) propertySource).getSource().forEach((key, value) -> { if (key.startsWith(TENANT)) {
     * tenants.put(value.toString(), url.getProtocol() + "://" + key.replace(TENANT, "")); LOG.info("*****override tenants******"
     * + value.toString() + url.getProtocol() + "://" + key.replace(TENANT, "")); } }); } }); // second get from application
     * config only properties if it is not overriden environment.getPropertySources().iterator().forEachRemaining(propertySource
     * -> { LOG.info( "Property Source" + propertySource.getName() + " Class Name" + propertySource.getClass().getSimpleName());
     * if (propertySource.getName().contains("application-config.properties") && propertySource instanceof MapPropertySource) {
     * ((MapPropertySource) propertySource).getSource().forEach((key, value) -> { if (key.startsWith(TENANT) &&
     * !tenants.containsKey(value)) { tenants.put(value.toString(), url.getProtocol() + "://" + key.replace(TENANT, ""));
     * LOG.info( "*****application config tenants******" + value.toString() + url.getProtocol() + "://" + key.replace(TENANT,
     * "")); } }); } }); } catch (MalformedURLException e) { LOG.error("Error occurred, while forming URL", e); } return tenants;
     * }
     */

    private String setCustomHeader(String requestURL, String tenantAtBody,
            MultiReadRequestWrapper multiReadRequestWrapper) {

        if (requestURL.contains("/rest/")) {
            LOG.info("***********Inside method to fetch auth token and tenant from reqbody**************");
            try {
                StringWriter writer = new StringWriter();
                IOUtils.copy(multiReadRequestWrapper.getInputStream(), writer, StandardCharsets.UTF_8);
                String reqBody = String.valueOf(writer);
                if (StringUtils.isNoneBlank(reqBody)) {
                    Pattern p = Pattern.compile("\\{.*?\\}");
                    Matcher m = p.matcher(reqBody);
                    while (m.find()) {
                        CharSequence charSequence = m.group().subSequence(1, m.group().length() - 1);
                        String[] reqBodyParams = String.valueOf(charSequence).split(",");
                        if (LOG.isDebugEnabled())
                            LOG.debug("***********Request Body Params**************" + String.valueOf(charSequence));
                        for (String param : reqBodyParams) {
                            if (LOG.isDebugEnabled())
                                LOG.debug("*************************" + param);
                            if (param.contains("userInfo") && StringUtils.isNotBlank(tenantAtBody))
                                break;

                            if (param.contains("tenantId")) {
                                String[] tenant = param.split(":");
                                if (tenant[1].startsWith("\"") && tenant[1].endsWith("\""))
                                    tenantAtBody = tenant[1].substring(1, tenant[1].length() - 1);
                                else
                                    tenantAtBody = tenant[1];
                                if (LOG.isDebugEnabled())
                                    LOG.debug("############Tenant From Body######" + tenantAtBody);
                            } /*
                               * else if (param.contains("authToken")) { String[] authTokenVal = param.split(":"); // Next to
                               * 'bearer' word space is required to differentiate token type and access token String tokenType =
                               * "bearer "; if (authTokenVal[1].startsWith("\"") && authTokenVal[1].endsWith("\"")) { String
                               * authToken = authTokenVal[1].substring(1, authTokenVal[1].length() - 1);
                               * LOG.info("############Auth Token######" + tokenType + authToken);
                               * multiReadRequestWrapper.putHeader("Authorization", tokenType + authToken); } else {
                               * multiReadRequestWrapper.putHeader("Authorization", tokenType + authTokenVal[1]); } }
                               */
                        }
                    }
                }

            } catch (IOException e) {
                LOG.error("Error occurred, while parsing request body into json", e);
            }

        }
        return tenantAtBody;
    }

}
