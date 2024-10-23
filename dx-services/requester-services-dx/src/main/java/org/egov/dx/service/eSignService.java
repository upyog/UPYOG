package org.egov.dx.service;

import java.io.ByteArrayOutputStream;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.egov.common.contract.request.RequestInfo;
import org.egov.dx.producer.Producer;
import org.egov.dx.util.Configurations;
import org.egov.dx.util.Utilities;
import org.egov.dx.web.models.FileResponse;
import org.egov.dx.web.models.IdGenerationResponse;
import org.egov.dx.web.models.RequestInfoWrapper;
import org.egov.dx.repository.TransactionRepository;
import org.egov.dx.web.models.Transaction;
import org.egov.dx.web.models.TransactionCriteria;
import org.egov.tracer.model.CustomException;
import org.egov.dx.service.IdGenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;

import com.emudhra.esign.ReturnDocument;
import com.emudhra.esign.eSign;
import com.emudhra.esign.eSignInput;
import com.emudhra.esign.eSignInputBuilder;
import com.emudhra.esign.eSignServiceReturn;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
@Slf4j
public class eSignService {

	@Autowired
	Configurations configurations;
	
	@Autowired
    private IdGenService idGenService;
    @Autowired
	private Producer producer;
    @Autowired
    private TransactionRepository transactionRepository;
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
    
    String generateTxnId(RequestInfoWrapper requestInfoWrapper) {
    	
        Transaction transaction = requestInfoWrapper.getTransaction();
        RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();

//        
//        IdGenerationResponse response = idGenRepository.getId(requestInfo, transaction.getTenantId(),
//                config.getIdGenName(), config.getIdGenFormat(), 1);

        //String txnId = response.getIdResponses().get(0).getId();
        //log.info("Transaction ID Generated: " + txnId);

        return "dd";

    }
    
    public String processPDF(RequestInfoWrapper requestInfoWrapper) throws IOException {
    	String txnId=null;
        String pdfBase64 = getPdfAsBase64(requestInfoWrapper.getTransaction().getPdfUrl());
        eSignInput signInput = eSignInputBuilder.init()
                .setDocBase64(pdfBase64)
                .setDocInfo("1723479092483kqKfUdtnch.pdf")//pdf name
                .setDocURL(requestInfoWrapper.getTransaction().getPdfUrl())//for v3 only pdf view purpose
                .setLocation("") // reason or signed (optional)
                .setReason("")	//(optional)
                .setSignedBy("Manvi") //(mandatory)
                .setCoSign(true) 
                .setAppearanceType(eSign.AppearanceType.StandardSignature)
                .setPageTobeSigned(eSign.PageTobeSigned.First)
                .setCoordinates(eSign.Coordinates.TopLeft)
                .build();

        ArrayList<eSignInput> inputList = new ArrayList<>();
        inputList.add(signInput);
        
        eSign eSignObj = new eSign(configurations.getLicenceFile(), configurations.getPfxPath(),configurations.getPfxPassword(), configurations.getPfxAllias());
        
        
        txnId = idGenService.generateTxnId(requestInfoWrapper);
        requestInfoWrapper.getTransaction().setTxnId(txnId);
        Long time = System.currentTimeMillis();
        String uuid = requestInfoWrapper.getRequestInfo().getUserInfo().getUuid();
        Transaction transaction = requestInfoWrapper.getTransaction();
	    transaction.setCreatedTime(time);
	    transaction.setCreatedBy(uuid);
	    transaction.setLastModifiedTime(time);
	    transaction.setLastModifiedBy(uuid);
		
        producer.push(configurations.getSaveTLEsignTxnTopic(), requestInfoWrapper);
        
        // Obtain the gateway parameter
        eSignServiceReturn serviceReturn = eSignObj.getGatewayParameter(
                inputList, "Manvi", (txnId+"-"+ requestInfoWrapper.getTransaction().getModule()) , configurations.getRedirectUrl(),configurations.getRedirectUrl(), configurations.getTempFolder(), eSign.eSignAPIVersion.V2, eSign.AuthMode.OTP);

        String gatewayParam = serviceReturn.getGatewayParameter();
        String gatewayURL = "https://authenticate.sandbox.emudhra.com/AadhaareSign.jsp"; // Adjust if needed

        return gatewayURL + "?txnref=" + gatewayParam;
    }
    
    public String getEsignedPDF(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        InputStream xmlStream;
        String xml = "";
        String txnref = null;
        byte[] signedBytes = null;
        String pdfPath=null;
        String txnId=null;

        try {
            xmlStream = request.getInputStream();
            txnref = Utilities.getStringFromInputStream(xmlStream);

            String txns = URLDecoder.decode(txnref).substring(7).split("&")[0];

            String txn = new String(java.util.Base64.getDecoder().decode(txns.getBytes()));
            String[] strArr = txn.split("\\|");
            txnId = strArr[0];
 
            String xmlString = URLDecoder.decode(txnref);
            xml = xmlString.split("&XML=")[1];

            Document doc = Utilities.convertStringToDocument(xml);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String status = Utilities.GetXpathValue(xPath, "/EsignResp/@status", doc);
            txnId = Utilities.GetXpathValue(xPath, "/EsignResp/@txn", doc);	
            if ("1".equals(status)) {

                // complete signing
                eSign eSign = new eSign(configurations.getLicenceFile(), configurations.getPfxPath(), configurations.getPfxPassword(), configurations.getPfxAllias());
                eSignServiceReturn serviceReturn = eSign.getSigedDocument(xml, configurations.getTempFolder() + File.separator + txnId + ".sig");

                // To convert signed pdf from base 64 encoded string to pdf file
                if (serviceReturn.getStatus() == 1) {
                    ArrayList<ReturnDocument> returnDocuments = serviceReturn.getReturnDocuments();
                    int i = 0;
                    for (ReturnDocument returnDocument : returnDocuments) {
                        String pdfBase64 = returnDocument.getSignedDocument();
                        System.out.println("Print" +pdfBase64);
                        signedBytes = esign.text.pdf.codec.Base64.decode(pdfBase64);
                        pdfPath = configurations.getOutputFolder() + File.separator + txnId + "_" + i + ".pdf";
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

   
        OkHttpClient client = new OkHttpClient().newBuilder()
        		  .build();
        		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
        		  .addFormDataPart("tenantId","pg")
        		  .addFormDataPart("module","TL")
        		  .addFormDataPart("file",pdfPath,
        		    RequestBody.create(MediaType.parse("application/pdf"),
        		    new File(pdfPath)))
        		  .build();
        		Request request1 = new Request.Builder()
        		  .url("https://upyog-test.niua.org/filestore/v1/files")
        		  .method("POST", body)
        		  .addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryf04Aqi5zqaexyYxK")
        		  .build();
        
        String responsee = client.newCall(request1).execute().body().string();
        log.info(responsee);
        Gson gson = new Gson();
		FileResponse files = gson.fromJson(responsee , FileResponse.class);
		String fileStoreId= files.getFiles().get(0).getFileStoreId();


        txnId = txnId.split("-")[0];
		TransactionCriteria criteria = new TransactionCriteria();
		criteria.setTxnId(txnId);
		
		//Search function --> with txn id and update signed filestore id 

		Long time = System.currentTimeMillis();
        List<Transaction> transactions = this.getTransactions(criteria);
		RequestInfoWrapper requestInfoWrapper= new RequestInfoWrapper();
		requestInfoWrapper.setTransaction(transactions.get(0));
		
		requestInfoWrapper.getTransaction().setSignedFilestoreId(fileStoreId);
		requestInfoWrapper.getTransaction().setLastModifiedTime(time);
		
        producer.push(configurations.getUpdateTLEsignTxnTopic(), requestInfoWrapper);
        return fileStoreId;

    }
    
    
    public List<Transaction> getTransactions(TransactionCriteria transactionCriteria) {
        log.info(transactionCriteria.toString());
        try {
            return transactionRepository.fetchTransactions(transactionCriteria);
        } catch (DataAccessException e) {
            log.error("Unable to fetch data from the database for criteria: " + transactionCriteria.toString(), e);
            throw new CustomException("FETCH_TXNS_FAILED", "Unable to fetch transactions from store");
        }
    }

}