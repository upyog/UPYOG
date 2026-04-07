package org.egov.dx.service;

import static org.egov.dx.util.PTServiceDXConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.egov.common.contract.request.RequestInfo;
import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataExchangeService {

    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private Configurations configurations;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${egov.collectionservice.host}")
    private String egovHost;

    private final XStream xstream;
    private final RestTemplate restTemplate;

    public DataExchangeService() {
        this.xstream = initializeXStream();
        this.restTemplate = new RestTemplate();
    }

    private XStream initializeXStream() {
        XStream xs = new XStream();
        xs.addPermission(NoTypePermission.NONE);
        xs.addPermission(NullPermission.NULL);
        xs.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xs.addPermission(AnyTypePermission.ANY);
        xs.allowTypesByWildcard(new String[] { "org.egov.dx.web.models.**" });
        
        xs.processAnnotations(new Class[] {
            PullURIResponse.class, PullDocResponse.class, Certificate.class,
            Organization.class, Address.class, IssuedBy.class, IssuedTo.class,
            ResponseStatus.class, DocDetailsResponse.class, Person.class,
            WaterSewerageBill.class
        });
        
        return xs;
    }

    // --- Public API Methods ---

    public String handlePullURIRequest(SearchCriteria criteria) throws IOException {
        if (!ORIGIN.equals(criteria.getOrigin())) {
            log.error("Unsupported origin: {}", criteria.getOrigin());
            return DIGILOCKER_ORIGIN_NOT_SUPPORTED;
        }
        return processRequest(criteria, true);
    }

    public String handlePullDocRequest(SearchCriteria criteria) throws IOException {
        if (!ORIGIN.equals(criteria.getOrigin())) {
            log.error("Unsupported origin: {}", criteria.getOrigin());
            return DIGILOCKER_ORIGIN_NOT_SUPPORTED;
        }
        return processRequest(criteria, false);
    }

    // --- Routing Logic ---

    private String processRequest(SearchCriteria searchCriteria, boolean isUriRequest) throws IOException {
        String txnId = searchCriteria.getTxn();
        
        try {
            if (!isUriRequest) {
                extractUriDetails(searchCriteria);
            }
            
            RequestInfoWrapper requestWrapper = prepareRequestInfo();
            String docType = searchCriteria.getDocType();

            log.info("Processing DigiLocker Request for DocType: {}", docType);

            if (DIGILOCKER_DOCTYPE.equalsIgnoreCase(docType)) {
                return handlePropertyTax(searchCriteria, isUriRequest, requestWrapper);
            } else if ("WS".equalsIgnoreCase(docType) || "SW".equalsIgnoreCase(docType) || DIGILOCKER_WS_DOCTYPE.equalsIgnoreCase(docType)) {
            	
					if ("WS".equalsIgnoreCase(searchCriteria.getConnType())|| "Water Connection".equalsIgnoreCase(searchCriteria.getConnType())) {
						searchCriteria.setDocType("WS"); // Ensure docType is set for WS/SW processing
						docType = "WS"; // Override docType for WS/SW processing
					} else if ("SW".equalsIgnoreCase(searchCriteria.getConnType()) || "Sewerage Connection".equalsIgnoreCase(searchCriteria.getConnType())) {
						searchCriteria.setDocType("SW"); // Ensure docType is set for WS/SW processing
						docType = "SW"; // Override docType for WS/SW processing
					}
                return handleWaterSewerage(searchCriteria, isUriRequest, requestWrapper, docType);
            } else {
                log.error("Unsupported DocType: {}", docType);
                return generateErrorResponse(txnId, isUriRequest);
            }

        } catch (Exception e) {
            log.error("Critical error during data exchange: ", e);
            return generateErrorResponse(txnId, isUriRequest);
        }
    }

    // --- Property Tax Logic ---

    private String handlePropertyTax(SearchCriteria searchCriteria, boolean isUriRequest, RequestInfoWrapper wrapper) throws IOException {
        PaymentSearchCriteria paymentCriteria = new PaymentSearchCriteria();
        paymentCriteria.setTenantId(TENANT_PREFIX + searchCriteria.getCity());
        paymentCriteria.setConsumerCodes(Collections.singleton(searchCriteria.getPropertyId()));

        List<Payment> payments = paymentService.getPayments(paymentCriteria, DIGILOCKER_DOCTYPE, wrapper);
        
        if (!isValidResponse(searchCriteria, payments)) {
            return generateErrorResponse(searchCriteria.getTxn(), isUriRequest);
        }
        
        Payment payment = payments.get(0);
        String fileStoreId = payment.getFileStoreId();
        if (fileStoreId == null) {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setPayments(Collections.singletonList(payment));
            paymentRequest.setRequestInfo(wrapper.getRequestInfo()); 
            fileStoreId = paymentService.createPDF(paymentRequest);
        }

        Object filestoreObj = paymentService.getFilestore(fileStoreId);
        JsonNode root = objectMapper.valueToTree(filestoreObj);
        String pdfUrl = null;
        
        if (root.has("fileStoreIds") && root.get("fileStoreIds").isArray() && root.get("fileStoreIds").size() > 0) {
            pdfUrl = root.get("fileStoreIds").get(0).path("url").asText(null);
        } else if (root.isArray() && root.size() > 0) {
            pdfUrl = root.get(0).path("url").asText(null);
        }

        if (pdfUrl == null || pdfUrl.isEmpty() || "null".equals(pdfUrl)) {
            throw new IOException("URL not found in Filestore response");
        }

        String base64Pdf = downloadAndEncodePdf(pdfUrl);
        ResponseStatus status = new ResponseStatus(getFormattedNow(), searchCriteria.getTxn(), "1");
        
        String actualPropertyId = payment.getPaymentDetails().get(0).getBill().getConsumerCode();
        String replacedPropertyId = actualPropertyId.replace("-", "QW");
        
        String customUri = String.format("%s-%s-%sQW%s", 
                DIGILOCKER_ISSUER_ID, DIGILOCKER_DOCTYPE, replacedPropertyId, searchCriteria.getCity());

        DocDetailsResponse docDetails = new DocDetailsResponse();
        docDetails.setURI(customUri);
        docDetails.setDocContent(base64Pdf);
        
        Certificate cert = populateCertificate(payment);
        docDetails.setIssuedTo(cert.getIssuedTo());
        
        String xmlData = xstream.toXML(cert);
        docDetails.setDataContent(Base64.getEncoder().encodeToString(xmlData.getBytes()));

        return buildFinalResponse(status, docDetails, isUriRequest);
    }

    // --- Water & Sewerage Logic ---

private String handleWaterSewerage(SearchCriteria searchCriteria, boolean isUriRequest, RequestInfoWrapper wrapper, String docType) throws IOException {
        
        // 1. Fetch Connection List
        JsonNode connectionResponse = fetchWaterSewerageConnection(searchCriteria, docType, wrapper);
        if (connectionResponse == null) return generateErrorResponse(searchCriteria.getTxn(), isUriRequest);

        String arrayNodeName = "WS".equalsIgnoreCase(docType) ? "WaterConnection" : "SewerageConnections";
        JsonNode connections = connectionResponse.path(arrayNodeName);
        
        // 2. Find ANY active connection
        JsonNode activeConn = null;
        for (JsonNode node : connections) {
            if ("Active".equalsIgnoreCase(node.path("status").asText())) {
                activeConn = node;
                break; 
            }
        }

        // 3. Error if no Active connection exists
        if (activeConn == null) {
            log.error("No active connection found for consumer {}", searchCriteria.getPropertyId());
            return generateErrorResponse(searchCriteria.getTxn(), isUriRequest);
        }

        // 4. Chain to Property Search for Owner Details
        JsonNode propertyResponse = fetchPropertyDetails(activeConn.path("tenantId").asText(), activeConn.path("propertyId").asText(), wrapper);
        if (propertyResponse == null || propertyResponse.path("Properties").size() == 0) {
            log.error("Failed to fetch linked property details.");
            return generateErrorResponse(searchCriteria.getTxn(), isUriRequest);
        }
        
        JsonNode firstProperty = propertyResponse.path("Properties").get(0);
        String oName = firstProperty.path("owners").get(0).path("name").asText("");
        String oMobile = firstProperty.path("owners").get(0).path("mobileNumber").asText("");

        // 5. Validate Case-Insensitive Name and Mobile
        if (!isValidWsResponse(searchCriteria, oName, oMobile)) {
            log.error("Owner validation failed against Property Master.");
            return generateErrorResponse(searchCriteria.getTxn(), isUriRequest);
        }

        // 6. Get existing PDF from sanctionFileStoreId
        String fileStoreId = activeConn.path("additionalDetails").path("sanctionFileStoreId").asText("");
        if (fileStoreId.isEmpty() || "null".equals(fileStoreId)) {
            log.error("sanctionFileStoreId is missing in additionalDetails");
            return generateErrorResponse(searchCriteria.getTxn(), isUriRequest);
        }

        // 7. Fetch the actual URL from the Filestore
        Object filestoreObj = paymentService.getFilestore(fileStoreId);
        JsonNode root = objectMapper.valueToTree(filestoreObj);
        String pdfUrl = null;
        
        // Fixed: Use null-safe extraction for filestore URL
        if (root.has("fileStoreIds") && root.get("fileStoreIds").isArray() && root.get("fileStoreIds").size() > 0) {
            pdfUrl = root.get("fileStoreIds").get(0).path("url").asText(null);
        } else if (root.isArray() && root.size() > 0) {
            pdfUrl = root.get(0).path("url").asText(null);
        }

        if (pdfUrl == null || pdfUrl.isEmpty() || "null".equals(pdfUrl)) {
            throw new IOException("URL not found in Filestore response");
        }

        // 8. Final Response Assembly
        String base64Pdf = downloadAndEncodePdf(pdfUrl);
        ResponseStatus status = new ResponseStatus(getFormattedNow(), searchCriteria.getTxn(), "1");
        
        // Fixed: Use activeConn and connectionNo
        String actualConsumerCode = activeConn.path("connectionNo").asText("");
        String replacedConsumerCode = actualConsumerCode.replace("-", "QW");
        String customUri = String.format("%s-%s-%sQW%s", 
                DIGILOCKER_ISSUER_ID, docType, replacedConsumerCode, searchCriteria.getCity());

        DocDetailsResponse docDetails = new DocDetailsResponse();
        docDetails.setURI(customUri);
        docDetails.setDocContent(base64Pdf);
        
        // Fixed: Use oName and oMobile
        Certificate cert = populateWSCertificate(activeConn, docType, oName, oMobile);
        docDetails.setIssuedTo(cert.getIssuedTo());
        
        docDetails.setDataContent(Base64.getEncoder().encodeToString(xstream.toXML(cert).getBytes()));

        return buildFinalResponse(status, docDetails, isUriRequest);
    }

    private JsonNode fetchWaterSewerageConnection(SearchCriteria sc, String docType, RequestInfoWrapper requestInfo) {
        String endpoint = "WS".equalsIgnoreCase(docType) ? configurations.getSearchWSConnEndpoint() : configurations.getSearchSwConnEndpoint();
        boolean isWaterService = "WS".equalsIgnoreCase(docType);
        String host = isWaterService ? configurations.getWsHost() : configurations.getSwHost();
        StringBuilder urlBuilder = new StringBuilder(host);
        urlBuilder.append(endpoint)
                  .append("?searchType=CONNECTION")
                  .append("&tenantId=").append(TENANT_PREFIX).append(sc.getCity());
                  
        if (sc.getMobile() != null && !sc.getMobile().isEmpty()) {
            urlBuilder.append("&mobileNumber=").append(sc.getMobile());
        }
        
        if (sc.getPropertyId() != null && !sc.getPropertyId().isEmpty()) {
            urlBuilder.append("&connectionNumber=").append(sc.getPropertyId());
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("RequestInfo", requestInfo.getRequestInfo());

        try {
            return restTemplate.postForObject(urlBuilder.toString(), requestBody, JsonNode.class);
        } catch (Exception e) {
            log.error("Error fetching WS/SW connection details: ", e);
            return null;
        }
    }

    private JsonNode fetchPropertyDetails(String tenantId, String propertyId, RequestInfoWrapper requestInfo) {
        String url = configurations.getSerachPropertyHost() + configurations.getSearchPropertyEndpoint() + tenantId + "&propertyIds=" + propertyId;
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("RequestInfo", requestInfo.getRequestInfo());

        try {
            return restTemplate.postForObject(url, requestBody, JsonNode.class);
        } catch (Exception e) {
            log.error("Error fetching Property details for linked WS/SW connection: ", e);
            return null;
        }
    }

    private boolean isValidWsResponse(SearchCriteria sc, String ownerName, String ownerMobile) {
        if ("FALSE".equalsIgnoreCase(configurations.getValidationFlag())) return true;
        
        boolean isNameMatch = ownerName.equalsIgnoreCase(sc.getPayerName());
        
        boolean isMobileMatch = true; 
        if (ownerMobile != null && !ownerMobile.isEmpty()) {
            isMobileMatch = Objects.equals(sc.getMobile(), ownerMobile);
        }
        
        return isNameMatch && isMobileMatch;
    }

    private Certificate populateWSCertificate(JsonNode connection, String docType, String ownerName, String ownerMobile) {
        Certificate cert = new Certificate();
        cert.setLanguage(LANGUAGE_CODE); 
        cert.setname("WS".equalsIgnoreCase(docType) ? "Water Connection" : "Sewerage Connection");
        cert.setType(docType);
        cert.setNumber(connection.path("connectionNo").asText("")); 
        cert.setPrevNumber(connection.path("oldConnectionNo").asText(""));
        
        long epochDate = connection.path("connectionExecutionDate").asLong(System.currentTimeMillis());
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(epochDate));
        
        cert.setIssueDate(formattedDate);
        cert.setExpiryDate("");
        cert.setValidFromDate("");
        cert.setIssuedAt(connection.path("tenantId").asText());
        cert.setStatus(CERT_STATUS_ACTIVE); 

        Organization org = new Organization();
        org.setName(ISSUER_ORG_NAME); 
        org.setType(ISSUER_ORG_TYPE); 
        org.setTin("");
        org.setCode("");
        org.setuuid("");
        
        Address orgAddr = new Address();
        orgAddr.setType("");
        orgAddr.setLine1("");
        orgAddr.setLine2("");
        orgAddr.setHouse("");
        orgAddr.setLandmark("");
        orgAddr.setLocality("");
        orgAddr.setVtc("");
        orgAddr.setPin(ISSUER_PIN); 
        orgAddr.setDistrict(ISSUER_DISTRICT); 
        orgAddr.setState(ISSUER_STATE); 
        orgAddr.setCountry("IN");
        org.setAddress(orgAddr);

        IssuedBy issuer = new IssuedBy();
        issuer.setOrganisation(org);
        cert.setIssuedBy(issuer);

        Person person = new Person();
        person.setUid("");
        person.setTitle("");
        person.setName(ownerName); 
        person.setDob("");
        person.setAge("");
        person.setSwd("");
        person.setSwdIndicator("");
        person.setMotherName("");
        person.setGender("");
        person.setMaritalStatus("");
        person.setRelationWithHof("");
        person.setDisabilityStatus("");
        person.setCategory("");
        person.setReligion("");
        person.setPhone(ownerMobile);
        person.setEmail("");
        person.setPhoto("");
        
        Address pAddr = new Address();
        pAddr.setType(PERSON_ADDR_TYPE); 
        pAddr.setLine1("");
        pAddr.setLine2("");
        pAddr.setHouse("");
        pAddr.setLandmark("");
        pAddr.setLocality(connection.path("additionalDetails").path("locality").asText(""));
        pAddr.setVtc("");
        pAddr.setPin("");
        pAddr.setDistrict(connection.path("tenantId").asText());
        pAddr.setState(PERSON_STATE); 
        pAddr.setCountry("IN");
        person.setAddress(pAddr);

        IssuedTo issuedTo = new IssuedTo();
        issuedTo.setPerson(person);
        cert.setIssuedTo(issuedTo);

        CertificateData data = new CertificateData();
        WaterSewerageBill wsBill = new WaterSewerageBill(); 
        
        wsBill.setConsumerNo(connection.path("connectionNo").asText(""));
        wsBill.setConsumerName(ownerName);
        wsBill.setMobileNumber(ownerMobile); 
        wsBill.setAddress(connection.path("additionalDetails").path("locality").asText(""));
        wsBill.setBillNumber(connection.path("applicationNo").asText("")); 
        wsBill.setBillAmount("0.0"); 
        wsBill.setBillDate(formattedDate);
        wsBill.setStatus(connection.path("status").asText(""));
        
        data.setWaterSewerageBill(wsBill);
        cert.setCertificateData(data);

        return cert;
    }

    // --- Core Helper Methods ---

    private void extractUriDetails(SearchCriteria sc) {
        if (sc.getURI() != null && !sc.getURI().isEmpty()) {
            String[] uriParts = sc.getURI().split("-");
            String docType = uriParts[1];
            sc.setDocType(docType);
            
            String rawIdData = uriParts[2]; 
            String[] qwParts = rawIdData.split("QW");
            
            if (DIGILOCKER_DOCTYPE.equalsIgnoreCase(docType)) {
                sc.setPropertyId("PT-" + qwParts[1] + "-" + qwParts[2]);
                sc.setCity(qwParts[3]);
            } else {
                sc.setPropertyId(docType + "-" + qwParts[1]); 
                sc.setCity(qwParts[2]);
            }
        }
    }

    private String buildFinalResponse(ResponseStatus status, DocDetailsResponse docDetails, boolean isUriRequest) {
        if (isUriRequest) {
            PullURIResponse res = new PullURIResponse();
            res.setResponseStatus(status);
            res.setDocDetails(docDetails);
            return xstream.toXML(res);
        } else {
            PullDocResponse res = new PullDocResponse();
            res.setResponseStatus(status);
            res.setDocDetails(docDetails);
            return xstream.toXML(res);
        }
    }

    private String downloadAndEncodePdf(String urlString) throws IOException {

        // Step 1: Convert any domain to localhost
        try {
            URL originalUrl = new URL(urlString);

            urlString = configurations.getFilestoreHost()
                    + originalUrl.getPath()
                    + (originalUrl.getQuery() != null ? "?" + originalUrl.getQuery() : "");

        } catch (Exception e) {
            throw new RuntimeException("Invalid URL: " + urlString, e);
        }

        // Step 2: Open connection
        URL url = java.net.URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept", "*/*");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        // Step 3: Validate response
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server returned HTTP " + responseCode + " for URL: " + urlString);
        }

        // Step 4: Read + Encode
        try (InputStream is = connection.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[8192];
            int nRead;

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return Base64.getEncoder().encodeToString(buffer.toByteArray());

        } finally {
            connection.disconnect();
        }
    }
    
    private boolean isValidResponse(SearchCriteria sc, List<Payment> payments) {
        if (payments.isEmpty()) return false;
        if ("FALSE".equalsIgnoreCase(configurations.getValidationFlag())) return true;
        
        Payment latest = payments.get(0);
        return Objects.equals(sc.getPayerName(), latest.getPayerName()) &&
               Objects.equals(sc.getMobile(), latest.getMobileNumber());
    }

    private RequestInfoWrapper prepareRequestInfo() {
        UserResponse user = userService.getUser();
        RequestInfo info = new RequestInfo();
        info.setApiId(API_ID);
        info.setMsgId(String.format(MSG_ID_PATTERN, System.currentTimeMillis()));
        info.setAuthToken(user.getAuthToken());
        info.setUserInfo(user.getUser());
        
        RequestInfoWrapper wrapper = new RequestInfoWrapper();
        wrapper.setRequestInfo(info);
        return wrapper;
    }

    private String generateErrorResponse(String txnId, boolean isUriRequest) {
        ResponseStatus status = new ResponseStatus(getFormattedNow(), txnId, "0");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        if (isUriRequest) {
            PullURIResponse res = new PullURIResponse();
            res.setResponseStatus(status);
            return xstream.toXML(res);
        } else {
            PullDocResponse res = new PullDocResponse();
            res.setResponseStatus(status);
            return xstream.toXML(res);
        }
    }

    private String getFormattedNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
    }

    private Certificate populateCertificate(Payment payment) {
        Certificate cert = new Certificate();
        cert.setLanguage(LANGUAGE_CODE);
        cert.setname("Property Tax Receipt");
        cert.setType(DIGILOCKER_DOCTYPE);
        cert.setNumber("");
        cert.setPrevNumber("");
        cert.setIssueDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        cert.setExpiryDate("");
        cert.setValidFromDate("");
        cert.setIssuedAt(payment.getTenantId());
        cert.setStatus(CERT_STATUS_ACTIVE);

        Organization org = new Organization();
        org.setName(ISSUER_ORG_NAME);
        org.setType(ISSUER_ORG_TYPE);
        org.setTin("");
        org.setCode("");
        org.setuuid("");
        
        Address orgAddr = new Address();
        orgAddr.setType("");
        orgAddr.setLine1("");
        orgAddr.setLine2("");
        orgAddr.setHouse("");
        orgAddr.setLandmark("");
        orgAddr.setLocality("");
        orgAddr.setVtc("");
        orgAddr.setPin(ISSUER_PIN);
        orgAddr.setDistrict(ISSUER_DISTRICT);
        orgAddr.setState(ISSUER_STATE);
        orgAddr.setCountry("IN");
        org.setAddress(orgAddr);

        IssuedBy issuer = new IssuedBy();
        issuer.setOrganisation(org);
        cert.setIssuedBy(issuer);

        Person person = new Person();
        person.setUid("");
        person.setTitle("");
        person.setName(payment.getPayerName());
        person.setDob("");
        person.setAge("");
        person.setSwd("");
        person.setSwdIndicator("");
        person.setMotherName("");
        person.setGender("");
        person.setMaritalStatus("");
        person.setRelationWithHof("");
        person.setDisabilityStatus("");
        person.setCategory("");
        person.setReligion("");
        person.setPhone(payment.getMobileNumber());
        person.setEmail(payment.getPayerEmail() != null ? payment.getPayerEmail() : "");
        person.setPhoto("");
        
        Address pAddr = new Address();
        pAddr.setType(PERSON_ADDR_TYPE);
        pAddr.setLine1("");
        pAddr.setLine2("");
        pAddr.setHouse("");
        pAddr.setLandmark("");
        pAddr.setLocality("");
        pAddr.setVtc("");
        pAddr.setPin("");
        pAddr.setDistrict(payment.getTenantId());
        pAddr.setState(PERSON_STATE);
        pAddr.setCountry("IN");
        person.setAddress(pAddr);

        IssuedTo recipient = new IssuedTo();
        recipient.setPerson(person);
        cert.setIssuedTo(recipient);

        PropertyTaxReceipt receipt = new PropertyTaxReceipt();
        receipt.setPaymentDate(payment.getPaymentDetails().get(0).getReceiptDate().toString());
        receipt.setServicetype(payment.getPaymentDetails().get(0).getBusinessService());
        receipt.setReceiptNo(payment.getPaymentDetails().get(0).getReceiptNumber());
        receipt.setPropertyID(payment.getPaymentDetails().get(0).getBill().getConsumerCode());
        receipt.setOwnerName(payment.getPayerName());
        receipt.setOwnerContact(payment.getMobileNumber());
        receipt.setPaymentstatus(payment.getPaymentStatus().toString());

        PaymentForReceipt pfr = new PaymentForReceipt();
        pfr.setPaymentMode(payment.getPaymentMode().toString());
        String billingPeriod = (payment.getPaymentDetails().get(0).getBill().getBillDetails().get(0).getFromPeriod().toString()) + "-" +
                               (payment.getPaymentDetails().get(0).getBill().getBillDetails().get(0).getToPeriod().toString());
        pfr.setBillingPeriod(billingPeriod);
        pfr.setPropertyTaxAmount(payment.getTotalDue().toString());
        pfr.setPaidAmount(payment.getTotalAmountPaid().toString());
        pfr.setPendingAmount((payment.getTotalDue().subtract(payment.getTotalAmountPaid())).toString());
        pfr.setExcessAmount("");
        pfr.setTransactionID(payment.getTransactionNumber());
        pfr.setG8ReceiptDate(payment.getPaymentDetails().get(0).getManualReceiptNumber());
        pfr.setG8ReceiptNo(payment.getPaymentDetails().get(0).getManualReceiptDate().toString());
        
        receipt.setPaymentForReceipt(pfr);

        CertificateData data = new CertificateData();
        data.setPropertyTaxReceipt(receipt);
        cert.setCertificateData(data);

        return cert;
    }
}