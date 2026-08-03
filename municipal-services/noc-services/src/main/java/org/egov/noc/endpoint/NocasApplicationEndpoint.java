package org.egov.noc.endpoint;

import jakarta.servlet.http.HttpServletResponse;

import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.service.AAINOCService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * SOAP endpoint for AAI NOCAS integration to fetch newly created NOC applications
 * This endpoint handles authentication and returns BPA application data in XML format
 * 
 * Authentication: AAI team sends complete RequestInfo JSON object in SOAP request
 * The RequestInfo is obtained from OAuth token generation response
 *
 */
@Slf4j
@Endpoint
public class NocasApplicationEndpoint {

	private static final String NAMESPACE_URI = "http://upyog.org/noc";

	@Autowired
	private AAINOCService nocasApplicationService;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Handles SOAP request for fetching newly created NOCAS applications.
	 * Extracts RequestInfo from request, validates authentication, and returns
	 * BPA application details in XML format as per AAI NOCAS specification.
	 * 
	 * @param request SOAP request element containing RequestInfo JSON
	 * @return XML element containing BPADETAILS with application data
	 * @throws Exception if authentication fails or XML generation fails
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetApplicationsRequest")
	public void getApplications(@RequestPayload Element request) throws Exception {
		try {
			String requestInfoJson = extractElementValue(request, "RequestInfo");
			String tenantId = extractElementValue(request, "tenantId");

			if (StringUtils.isEmpty(requestInfoJson)) {
				throw new CustomException("REQUESTINFO_MISSING", "RequestInfo is required");
			}

			RequestInfo requestInfo = parseRequestInfoFromJson(requestInfoJson);
			String applicationsXml = nocasApplicationService.generateNocasXml(requestInfo, tenantId);

			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			if (requestAttributes instanceof ServletRequestAttributes) {
				HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
				if (response != null) {
					response.setContentType("text/xml; charset=utf-8");
					response.setCharacterEncoding("utf-8");
					response.getWriter().write(applicationsXml);
					response.getWriter().flush();
				}
			}

		} catch (CustomException ce) {
			throw ce;
		} catch (Exception e) {
			throw new Exception("Failed to process request: " + e.getMessage(), e);
		}
	}

	/**
	 * Extracts value of a child element from the parent XML element
	 * 
	 * @param parent Parent XML element
	 * @param elementName Name of child element to extract
	 * @return Element text content or null if not found
	 */
	private String extractElementValue(Element parent, String elementName) {
		NodeList nodeList = parent.getElementsByTagNameNS(NAMESPACE_URI, elementName);
		if (nodeList != null && nodeList.getLength() > 0) {
			Element element = (Element) nodeList.item(0);
			return element.getTextContent();
		}
		return null;
	}

	/**
	 * Parses RequestInfo JSON string to RequestInfo object.
	 * Supports both wrapped format with "RequestInfo" key and direct format.
	 * Validates that authToken and userInfo are present.
	 * 
	 * @param requestInfoJson JSON string containing RequestInfo structure
	 * @return Parsed and validated RequestInfo object
	 * @throws CustomException if parsing fails or required fields missing
	 */
	private RequestInfo parseRequestInfoFromJson(String requestInfoJson) {
		try {
			JsonNode rootNode = objectMapper.readTree(requestInfoJson);
			RequestInfo requestInfo;

			if (rootNode.has("RequestInfo")) {
				requestInfo = objectMapper.treeToValue(rootNode.get("RequestInfo"), RequestInfo.class);
			} else if (rootNode.has("authToken") && rootNode.has("userInfo")) {
				requestInfo = objectMapper.treeToValue(rootNode, RequestInfo.class);
			} else {
				throw new CustomException("INVALID_REQUESTINFO_FORMAT", "Invalid RequestInfo format");
			}

			if (requestInfo.getUserInfo() == null) {
				throw new CustomException("USER_INFO_MISSING", "userInfo is required");
			}
			if (StringUtils.isEmpty(requestInfo.getAuthToken())) {
				throw new CustomException("AUTH_TOKEN_MISSING", "authToken is required");
			}

			return requestInfo;
		} catch (CustomException ce) {
			throw ce;
		} catch (Exception e) {
			throw new CustomException("REQUESTINFO_PARSE_ERROR", "Failed to parse RequestInfo");
		}
	}
}