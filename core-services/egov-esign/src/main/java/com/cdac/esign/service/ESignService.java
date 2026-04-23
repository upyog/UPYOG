package com.cdac.esign.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.egov.common.contract.request.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.NodeList;

import com.cdac.esign.encryptor.RSAKeyUtil;
import com.cdac.esign.form.FormXmlDataAsp;
import com.cdac.esign.form.RequestXmlForm;
import com.cdac.esign.xmlparser.AspXmlGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// iText 7 Imports
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.jayway.jsonpath.JsonPath;
import com.cdac.esign.eventListener.LastPositionListener;

@Service
public class ESignService {

    private static final Logger logger = LoggerFactory.getLogger(ESignService.class);

    @Autowired
    private AspXmlGenerator aspXmlGenerator;

    @Autowired
    private Environment env;
    
    @Autowired
    private MDMSService mdmsService;
    
    @Autowired
    private UserService userService;

    /**
     * PHASE 1: Prepare PDF with Dynamic Location & Custom TXN ID
     */
    public RequestXmlForm processDocumentUpload(String fileStoreId, String tenantId, String signerName, String callbackUrl, RequestInfo requestInfo) throws Exception {

        logger.info("Processing Phase 1 for tenant: {}, signer: {}", tenantId, signerName);

        if (signerName == null || signerName.trim().isEmpty()) {
            signerName = "Authorized Signatory"; 
        }

        String responseUrl = env.getProperty("esign.response.host") + env.getProperty("esign.response.url");
        
        //Added Custome response Url logic
        if(!StringUtils.isEmpty(callbackUrl))
        	responseUrl += "?callbackUrl=" + callbackUrl;
        
        // 1. Get Original PDF
        String pdfUrl = getPdfUrlFromFilestore(fileStoreId, tenantId);
        byte[] originalPdfBytes = downloadPdfFromUrlAsBytes(pdfUrl);

        // 2. PREPARE THE PDF (Text Only)
        ByteArrayOutputStream preparedPdfStream = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(originalPdfBytes));
        PdfSigner signer = new PdfSigner(reader, preparedPdfStream, new StampingProperties());
        
        Map<String, Object> positionMap = getEsignPosition(originalPdfBytes);
        
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        Float lastTextY = (Float)positionMap.getOrDefault("lastY", 50) - 64;
        appearance.setPageRect(new Rectangle(380, lastTextY < 0 ? 0 : lastTextY, 160, 64));
        appearance.setPageNumber((Integer)positionMap.getOrDefault("lastPage", 1));

        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        
        // --- DYNAMIC LOCATION LOGIC ---
        // Extract "Nabha" from "pb.nabha"
        String city = getCityFromTenantId(tenantId);
        String locationText = city.equalsIgnoreCase("Punjab") ? "India" : city + ", India";

        String layer2Text = "Digitally Signed by " + signerName + "\n" +
                            "Date: " + dateFormat.format(new Date()) + "\n" +
                            "Reason: mSeva eSign\n" + 
                            "Location: " + locationText; // <--- DYNAMIC LOCATION
        
        if(requestInfo.getUserInfo() != null) {
        	Object mdmsData = mdmsService.mDMSCall(requestInfo, tenantId.split("\\.")[0]);
        	String designation = userService.getEmployeeDesignation(requestInfo, requestInfo.getUserInfo().getUuid(), tenantId);
        	List<String> designations = JsonPath.read(mdmsData, "$.MdmsRes.common-masters.Designation.[?(@.code == '" + designation + "')].name");
        	if(CollectionUtils.isEmpty(designations))
        		designation = "Citizen";
        	else
        		designation = designations.get(0);
        	
        	List<String> ulbTypeList = JsonPath.read(mdmsData, "$.MdmsRes.tenant.tenants.[?(@.code == '" + tenantId + "')].city.ulbType");
			String ulbType = CollectionUtils.isEmpty(ulbTypeList) ? "" : ulbTypeList.get(0);
        	
        	layer2Text = "Digitally Signed by " + requestInfo.getUserInfo().getName() + "\n" +
                    "Date: " + dateFormat.format(new Date()) + "\n" +
                    ulbType + ", " + city +"\n" + 
                    designation; // <--- DYNAMIC LOCATION
        }
        
        appearance.setLayer2Text(layer2Text);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION); // Text Only

        signer.setFieldName("Signature1");

        // 3. Capture Hash
        HashCapturingContainer hashContainer = new HashCapturingContainer(PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached);
        signer.signExternalContainer(hashContainer, 16384); 

        String fileHash = hashContainer.getHashAsHex();

        // 4. Upload Prepared PDF
        byte[] preparedPdfBytes = preparedPdfStream.toByteArray();
        String tempFileResponse = uploadPdfToFilestore(preparedPdfBytes, tenantId);
        String rawFileStoreId = extractFileStoreIdFromResponse(tempFileResponse);

        // --- CUSTOM TXN ID LOGIC ("pb.nabha-UUID") ---
        String customTxnId = tenantId + "-" + rawFileStoreId; 
        logger.info("Generated Custom TXN ID: {}", customTxnId);

        // 5. Generate XML
        String pemKey = env.getProperty("esign.private.key");
        PrivateKey privateKey = RSAKeyUtil.loadPrivateKey(pemKey);

        Date now = new Date();
        DateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        xmlDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        FormXmlDataAsp formXmlDataAsp = new FormXmlDataAsp();
        formXmlDataAsp.setVer(env.getProperty("esign.version"));
        formXmlDataAsp.setSc(env.getProperty("esign.sc"));
        formXmlDataAsp.setTs(xmlDateFormat.format(now));
        formXmlDataAsp.setTxn(customTxnId); // SEND "pb.nabha-UUID"
        formXmlDataAsp.setEkycId("");
        formXmlDataAsp.setEkycIdType(env.getProperty("esign.ekyc.id.type"));
        formXmlDataAsp.setAspId(env.getProperty("esign.asp.id"));
        formXmlDataAsp.setAuthMode(env.getProperty("esign.auth.mode"));
        formXmlDataAsp.setResponseSigType(env.getProperty("esign.response.sig.type"));
        formXmlDataAsp.setResponseUrl(responseUrl);
        formXmlDataAsp.setId("1");
        formXmlDataAsp.setHashAlgorithm(env.getProperty("esign.hash.algorithm"));
        formXmlDataAsp.setDocInfo(env.getProperty("esign.doc.info"));
        formXmlDataAsp.setDocHashHex(fileHash); 

        String strToEncrypt = aspXmlGenerator.generateAspXml(formXmlDataAsp);
        String xmlData = new com.cdac.esign.xmlparser.XmlSigning().signXmlStringNew(strToEncrypt, privateKey);

        RequestXmlForm responseForm = new RequestXmlForm();
        responseForm.setId(fileStoreId); 
        responseForm.setAspTxnID(customTxnId); 
        responseForm.seteSignRequest(xmlData);
        responseForm.setContentType("application/xml");

        return responseForm;
    }

    /**
     * PHASE 2: Handle Response (Strip Prefix)
     */
    public Map<String, String> processDocumentCompletion(String eSignResponseXml, String customTxnId, HttpServletRequest request) throws Exception {
        logger.info("Processing Phase 2 for Custom ID: {}", customTxnId);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document xmlDoc = dBuilder.parse(new ByteArrayInputStream(eSignResponseXml.getBytes(StandardCharsets.UTF_8)));
        xmlDoc.getDocumentElement().normalize();

        if (!"1".equals(xmlDoc.getDocumentElement().getAttribute("status"))) {
            throw new RuntimeException("eSign failed: " + xmlDoc.getDocumentElement().getAttribute("errMsg"));
        }

        NodeList sigNodes = xmlDoc.getElementsByTagName("DocSignature");
        if (sigNodes.getLength() == 0) throw new RuntimeException("DocSignature tag missing");
        String cleanedPkcs7 = sigNodes.item(0).getTextContent().replaceAll("\\s+", ""); 
        final byte[] encodedSig = Base64.getDecoder().decode(cleanedPkcs7);

        // --- EXTRACT TENANT ID FROM CUSTOM TXN ID ---
        String extractedTenantId = env.getProperty("default.tenant.id", "pb");
        String originalFileStoreId = customTxnId;

        if (customTxnId.contains("-")) {
            // Split at the first hyphen only
            String[] parts = customTxnId.split("-", 2);
            if (parts.length > 1) {
                String potentialTenantId = parts[0]; 
                
                // If the first part contains "pb.", use it as the tenant
                if (potentialTenantId.contains("pb.")) {
                    extractedTenantId = potentialTenantId;
                    originalFileStoreId = parts[1]; // The rest is the UUID
                } else {
                    // If it's just a raw UUID (starts with numbers), keep customTxnId as is
                    extractedTenantId = env.getProperty("default.tenant.id", "pb");
                    originalFileStoreId = customTxnId;
                }
            }
        }

        logger.info("Extracted -> Tenant: {}, FileID: {}", extractedTenantId, originalFileStoreId);// Download using EXTRACTED tenant ID
        byte[] preparedPdfBytes = downloadPdfFromUrlAsBytes(getPdfUrlFromFilestore(originalFileStoreId, extractedTenantId));

        ByteArrayOutputStream signedBaos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(preparedPdfBytes));
        PdfSigner signer = new PdfSigner(reader, signedBaos, new StampingProperties());

        IExternalSignatureContainer external = new IExternalSignatureContainer() {
            @Override
            public byte[] sign(InputStream is) {
                return encodedSig; 
            }

            @Override
            public void modifySigningDictionary(PdfDictionary signDic) {
                signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
                signDic.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
            }
        };

        PdfSigner.signDeferred(signer.getDocument(), "Signature1", signedBaos, external);

        // Upload using EXTRACTED tenant ID
        String fileStoreResponse = uploadPdfToFilestore(signedBaos.toByteArray(), extractedTenantId);
        String finalFileStoreId = extractFileStoreIdFromResponse(fileStoreResponse);
        String returnFileStoreURL= getPdfUrlFromFilestore(finalFileStoreId, extractedTenantId);

        Map<String, String> result = new HashMap<>();
        result.put("fileStoreId", finalFileStoreId);
        result.put("fileUrl", returnFileStoreURL);
        
        return result;
    }

    // --- HELPER: Extract City Name (e.g. "pb.nabha" -> "Nabha") ---
    private String getCityFromTenantId(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) return "MSeva";
        
        try {
            // Split by dot (e.g. "pb.nabha" -> ["pb", "nabha"])
            String[] parts = tenantId.split("\\.");
            String city = parts.length > 1 ? parts[1] : parts[0];
            
            // Capitalize (nabha -> Nabha)
            if (city.length() > 0) {
                return city.substring(0, 1).toUpperCase() + city.substring(1);
            }
            return city;
        } catch (Exception e) {
            return "MSeva";
        }
    }

    // --- STANDARD HELPERS (No Changes) ---
 // ==========================================
    // HELPER: Hash Capturing Container
    // ==========================================
    private static class HashCapturingContainer implements IExternalSignatureContainer {
        private final PdfName filter;
        private final PdfName subFilter;
        private byte[] docHash;

        public HashCapturingContainer(PdfName filter, PdfName subFilter) {
            this.filter = filter;
            this.subFilter = subFilter;
        }

        @Override
        public byte[] sign(InputStream data) throws GeneralSecurityException {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] temp = new byte[16384]; 
                while ((nRead = data.read(temp, 0, temp.length)) != -1) {
                    buffer.write(temp, 0, nRead);
                }
                byte[] pdfBytes = buffer.toByteArray();

                // FIX: Use "SHA-256" (with hyphen), not "SHA256"
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                this.docHash = digest.digest(pdfBytes);
                
                return new byte[0]; 
            } catch (Exception e) {
                throw new GeneralSecurityException(e);
            }
        }

        @Override
        public void modifySigningDictionary(PdfDictionary signDic) {
            signDic.put(PdfName.Filter, filter);
            signDic.put(PdfName.SubFilter, subFilter);
        }

        public String getHashAsHex() {
            if (docHash == null) return "";
            StringBuilder hexString = new StringBuilder();
            for (byte b : docHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }

    private String getPdfUrlFromFilestore(String fileStoreId, String tenantId) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = env.getProperty("filestore.base.url", "http://localhost:1001");
            String filesUrl = env.getProperty("filestore.files.url", "/filestore/v1/files/url");
            String url = baseUrl + filesUrl + "?tenantId=" + tenantId + "&fileStoreIds=" + fileStoreId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", env.getProperty("http.header.accept", "application/json, text/plain, */*"));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode fileStoreIds = jsonNode.get("fileStoreIds");
                if (fileStoreIds != null && fileStoreIds.isArray() && fileStoreIds.size() > 0) {
                    JsonNode firstFile = fileStoreIds.get(0);
                    if (firstFile.has("url")) {
                        return firstFile.get("url").asText();
                    }
                }
                throw new RuntimeException("PDF URL not found in API response");
            } else {
                throw new RuntimeException("Failed to get PDF URL. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error calling filestore API", e);
            throw new RuntimeException("Failed to get PDF URL from filestore API", e);
        }
    }

    private byte[] downloadPdfFromUrlAsBytes(String pdfUrl) throws Exception {
        try {
            String baseUrl = env.getProperty("filestore.base.url", "http://localhost:1001");
            int splitIndex = pdfUrl.indexOf("/filestore");
            if (splitIndex != -1) {
                String relativePath = pdfUrl.substring(splitIndex);
                pdfUrl = baseUrl + relativePath;
            } 
            URL url = new URL(pdfUrl);
            try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] dataBuffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 8192)) != -1) {
                    baos.write(dataBuffer, 0, bytesRead);
                }
                return baos.toByteArray();
            }
        } catch (Exception e) {
            logger.error("Error downloading PDF from URL: " + pdfUrl, e);
            throw new RuntimeException("Failed to download PDF", e);
        }
    }

    public String uploadPdfToFilestore(byte[] pdfBytes, String tenantId) throws Exception {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String baseUrl = env.getProperty("filestore.base.url", "http://localhost:1001");
        String uploadUrl = env.getProperty("filestore.upload.url", "/filestore/v1/files");
        URL url = new URL(baseUrl + uploadUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("accept", "application/json, text/plain, */*");
        conn.setRequestProperty("content-type", "multipart/form-data; boundary=" + boundary);

        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"tenantId\"\r\n\r\n");
            out.writeBytes(tenantId + "\r\n");

            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"module\"\r\n\r\n");
            out.writeBytes(env.getProperty("default.module", "undefined") + "\r\n");

            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + env.getProperty("default.signed.filename", "signed.pdf") + "\"\r\n");
            out.writeBytes("Content-Type: " + env.getProperty("default.content.type", "application/pdf") + "\r\n\r\n");
            out.write(pdfBytes);
            out.writeBytes("\r\n");
            out.writeBytes("--" + boundary + "--\r\n");
        }

        int responseCode = conn.getResponseCode();
        InputStream responseStream = (responseCode == 201) ? conn.getInputStream() : conn.getErrorStream();
        return readStream(responseStream);
    }
    
    private static String readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    private String extractFileStoreIdFromResponse(String response) {
        try {
            if (response.contains("fileStoreId")) {
                int idx = response.indexOf("fileStoreId");
                int start = response.indexOf(":", idx) + 2;
                int end = response.indexOf("\"", start);
                if (response.charAt(start-1) != '"') {
                     end = response.indexOf(",", start);
                     if (end == -1) end = response.indexOf("}", start);
                }
                return response.substring(start, end).replace("\"", "").trim();
            }
        } catch (Exception e) {
            logger.error("Error extracting fileStoreId", e);
        }
        return response;
    }
    
    /**
     * Get last page and last X and Y of the Pdf file content
     * 
     * @param originalPdfBytes
     * @return
     * @throws Exception
     */
    private Map<String, Object> getEsignPosition(byte[] originalPdfBytes) throws Exception{
    	Map<String, Object> positionMap = new HashMap<>();
    	
    	PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(originalPdfBytes));
        PdfDocument document = new PdfDocument(pdfReader);
        int totalPages = document.getNumberOfPages();
        
        LastPositionListener listener = new LastPositionListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(document.getLastPage());
        
        if(listener.hasPosition()) {
        	positionMap.put("lastX", listener.getLastX());
        	positionMap.put("lastY", listener.getLastY());
        }
        
        document.close();
        pdfReader.close();
    	
        positionMap.put("lastPage", totalPages);
        
    	return positionMap;
    }
}