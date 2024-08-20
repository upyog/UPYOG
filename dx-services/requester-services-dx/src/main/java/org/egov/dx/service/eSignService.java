package org.egov.dx.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.egov.dx.util.Configurations;
import org.egov.dx.util.Utilities;

import org.egov.dx.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import com.emudhra.esign.ReturnDocument;
import com.emudhra.esign.eSign;
import com.emudhra.esign.eSignInput;
import com.emudhra.esign.eSignInputBuilder;
import com.emudhra.esign.eSignServiceReturn;


@Service
public class eSignService {

	@Autowired
	Configurations configurations;
	

    private final RestTemplate restTemplate = new RestTemplate();
    
    private byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toByteArray();
        }
    }
    public String getPdfAsBase64(String pdfUrl) throws IOException {
        URL url = new URL(pdfUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream()) {
            byte[] data = readInputStreamToByteArray(inputStream);
            return Base64.getEncoder().encodeToString(data);
        }
    }
    
    public String processPDF(RequestInfoWrapper requestInfoWrapper) throws IOException {


         String pdfBase64 = getPdfAsBase64(requestInfoWrapper.getPdfUrl());
////         InputStream openStream = u.openStream();
//         byte[] binaryData = new byte[contentLength];
         //String pdfBase64 = new String(Base64.getEncoder().encode(binaryData), StandardCharsets.UTF_8);
         //String pdfBase64 = new String (Files.readAllBytes(Paths.get("C:/Users/Administrator/Downloads/Base64.txt")));
        eSignInput signInput = eSignInputBuilder.init()
                .setDocBase64(pdfBase64)
                .setDocInfo("1723479092483kqKfUdtnch.pdf")//pdf name
                .setDocURL(requestInfoWrapper.getPdfUrl()) //for v3 only pdf view purpose
                .setLocation("") // reason or signed (optional)
                .setReason("")	//(optional)
                .setSignedBy("Manvi") //(mandatory)
                .setCoSign(true) 
                .setAppearanceType(eSign.AppearanceType.StandardSignature)
                .setPageTobeSigned(eSign.PageTobeSigned.Even)
                .setCoordinates(eSign.Coordinates.TopLeft)
                .build();

        ArrayList<eSignInput> inputList = new ArrayList<>();
        inputList.add(signInput);
        
        eSign eSignObj = new eSign(configurations.getLicenceFile(), configurations.getPfxPath(),configurations.getPfxPassword(), configurations.getPfxAllias());
        System.out.println("Object passed");

        // Obtain the gateway parameter
        eSignServiceReturn serviceReturn = eSignObj.getGatewayParameter(
                inputList, "Manvi", "", configurations.getRedirectUrl(),configurations.getRedirectUrl(), configurations.getTempFolder(), eSign.eSignAPIVersion.V2, eSign.AuthMode.OTP);
        
        System.out.println("Error is" + serviceReturn.getErrorCode());
        System.out.println("Error is" + serviceReturn.getErrorMessage());

//        return serviceReturn.getErrorMessage() + "-" + serviceReturn.getErrorCode() + "-" + serviceReturn;
        String gatewayParam = serviceReturn.getGatewayParameter();
        String gatewayURL = "https://authenticate.sandbox.emudhra.com/AadhaareSign.jsp"; // Adjust if needed

        // Return the constructed URL with parameters
        return gatewayURL + "?txnref=" + gatewayParam;
    }
    
    public String getEsignedPDF(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
       System.out.println("getsignedPdf");
        InputStream xmlStream;
        String xml = "";
        String txnref = null;
        try {
            // Get response from gateway
            xmlStream = request.getInputStream();
            txnref = Utilities.getStringFromInputStream(xmlStream);

            String txns = URLDecoder.decode(txnref).substring(7).split("&")[0];

            String txn = new String(java.util.Base64.getDecoder().decode(txns.getBytes()));
            String[] strArr = txn.split("\\|");
            String txnid = strArr[0];

            eSign eSign = new eSign(configurations.getLicenceFile(), configurations.getPfxPath(), configurations.getPfxPassword(), configurations.getPfxAllias());
            eSignServiceReturn returnService = eSign.getStatus(txnid);
            xml = returnService.getResponseXML();

            Document doc = Utilities.convertStringToDocument(xml);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String status = Utilities.GetXpathValue(xPath, "/EsignResp/@status", doc);
            String txnId = Utilities.GetXpathValue(xPath, "/EsignResp/@txn", doc);

            if ("1".equals(status)) {

                // complete signing
                eSignServiceReturn serviceReturn = eSign.getSigedDocument(xml, configurations.getTempFolder() + File.separator + txnId + ".sig");

                // To convert signed pdf from base 64 encoded string to pdf file
                if (serviceReturn.getStatus() == 1) {
                    ArrayList<ReturnDocument> returnDocuments = serviceReturn.getReturnDocuments();
                    int i = 0;
                    for (ReturnDocument returnDocument : returnDocuments) {
                        String pdfBase64 = returnDocument.getSignedDocument();
                        byte[] signedBytes = esign.text.pdf.codec.Base64.decode(pdfBase64);
                        String pdfPath = configurations.getOutputFolder() + File.separator + txnId + "_" + i + ".pdf";
                        try (FileOutputStream fos = new FileOutputStream(pdfPath)) {
                            fos.write(signedBytes);
                        }
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ("Document Signed and saved successfully");


    }


}
