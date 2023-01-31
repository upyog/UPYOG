package org.egov.dx.service;
import static org.egov.dx.util.PTServiceDXConstants.DIGILOCKER_DOCTYPE;
import static org.egov.dx.util.PTServiceDXConstants.DIGILOCKER_ISSUER_ID;
import static org.egov.dx.util.PTServiceDXConstants.DIGILOCKER_ORIGIN_NOT_SUPPORTED;
import static org.egov.dx.util.PTServiceDXConstants.EXCEPTION_TEXT_VALIDATION;
import static org.egov.dx.util.PTServiceDXConstants.ORIGIN;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.Address;
import org.egov.dx.web.models.Certificate;
import org.egov.dx.web.models.CertificateData;
import org.egov.dx.web.models.CertificateForData;
import org.egov.dx.web.models.DocDetailsResponse;
import org.egov.dx.web.models.IssuedBy;
import org.egov.dx.web.models.IssuedTo;
import org.egov.dx.web.models.Organization;
import org.egov.dx.web.models.Payment;
import org.egov.dx.web.models.PaymentForReceipt;
import org.egov.dx.web.models.PaymentRequest;
import org.egov.dx.web.models.PaymentSearchCriteria;
import org.egov.dx.web.models.Person;
import org.egov.dx.web.models.PropertyTaxReceipt;
import org.egov.dx.web.models.PullDocResponse;
import org.egov.dx.web.models.PullURIResponse;
import org.egov.dx.web.models.RequestInfoWrapper;
import org.egov.dx.web.models.ResponseStatus;
import org.egov.dx.web.models.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataExchangeService {

	@Autowired
    private PaymentService paymentService;
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private Configurations configurations;
	
	public String searchPullURIRequest(SearchCriteria  searchCriteria) throws IOException {
		
		if(searchCriteria.getOrigin().equals(ORIGIN))
		{
			return searchForDigiLockerURIRequest(searchCriteria);
		}
		return DIGILOCKER_ORIGIN_NOT_SUPPORTED;
	}
	
	public String searchPullDocRequest(SearchCriteria  searchCriteria) throws IOException {
		
		if(searchCriteria.getOrigin().equals(ORIGIN))
		{
			return searchForDigiLockerDocRequest(searchCriteria);
		}
		
		return DIGILOCKER_ORIGIN_NOT_SUPPORTED;
	}
	

	public String searchForDigiLockerURIRequest(SearchCriteria  searchCriteria) throws IOException
	{
		PaymentSearchCriteria criteria = new PaymentSearchCriteria();
    	criteria.setTenantId("pg."+searchCriteria.getCity());
        criteria.setConsumerCodes(Collections.singleton(searchCriteria.getPropertyId()));
        RequestInfo request=new RequestInfo();
        request.setApiId("Rainmaker");
        request.setMsgId("1670564653696|en_IN");
        RequestInfoWrapper requestInfoWrapper=new RequestInfoWrapper();
//        UserResponse userResponse =new UserResponse();
//        try {
//        	userResponse=userService.getUser();
//        	}
//        catch(Exception e)
//        {
//        	
//        }
        User userInfo = User.builder()
                .uuid(configurations.getAuthTokenVariable())
                .type("EMPLOYEE")
                .roles(Collections.emptyList()).id(0L).tenantId("pg.".concat(searchCriteria.getCity())).build();

        request = new RequestInfo("", "", 0L, "", "", "", "", "", "", userInfo);
        //request.setAuthToken(userResponse.getAuthToken());
        //request.setUserInfo(userResponse.getUser());
        requestInfoWrapper.setRequestInfo(request);
		List<Payment> payments = paymentService.getPayments(criteria,searchCriteria.getDocType(), requestInfoWrapper);
		log.info("Payments found are:---" + ((!payments.isEmpty()?payments.size():"No payments found")));
		PullURIResponse model= new PullURIResponse();
		XStream xstream = new XStream();   
		xstream .addPermission(NoTypePermission.NONE); //forbid everything
		xstream .addPermission(NullPermission.NULL);   // allow "null"
		xstream .addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream .addPermission(AnyTypePermission.ANY);
		log.info("Name to search is " +searchCriteria.getPayerName());
		log.info("Mobile to search is " +searchCriteria.getMobile());
		if(!payments.isEmpty()) {
		log.info("Name in latest payment is " +payments.get(0).getPayerName());
		log.info("Mobile in latest payment is " +payments.get(0).getMobileNumber());
		}
		if((!payments.isEmpty() && configurations.getValidationFlag().toUpperCase().equals("TRUE") && validateRequest(searchCriteria,payments.get(0)))
				|| (!payments.isEmpty() && configurations.getValidationFlag().toUpperCase().equals("FALSE"))){ 
			log.info("Payment object is not null and validations passed!!!");
			String o=null;
			String filestore=null;
			if(payments.get(0).getFileStoreId() != null) {
				filestore=payments.get(0).getFileStoreId();
				o=paymentService.getFilestore(payments.get(0).getFileStoreId()).toString();
			}
			else
			{
				List<Payment> latestPayment=new ArrayList<Payment>();
				latestPayment.add(payments.get(0));
				PaymentRequest paymentRequest=new PaymentRequest();
				paymentRequest.setPayments(latestPayment);
				//requestInfoWrapper.getRequestInfo().setMsgId("1674457280493|en_IN");
				paymentRequest.setRequestInfo(requestInfoWrapper.getRequestInfo());
				filestore=paymentService.createPDF(paymentRequest);
				o=paymentService.getFilestore(filestore).toString();
			
			}
				if(o!=null)
			
			 		{
				 	String path=o.split("url=")[1];
				 	String pdfPath=path.substring(0,path.length()-3);
				 	URL url1 =new URL(pdfPath);
				 	try {

				     // Read the PDF from the URL and save to a local file
				     InputStream is1 = url1.openStream();
				     ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				     int nRead;
				     byte[] data = new byte[1024];

				     while ((nRead = is1.read(data, 0, data.length)) != -1) {
				         buffer.write(data, 0, nRead);
				     }

				     buffer.flush();
				     byte[] targetArray = buffer.toByteArray();
				     String encodedString = Base64.getEncoder().encodeToString(targetArray); 
     				    
				     ResponseStatus responseStatus=new ResponseStatus();
				     responseStatus.setStatus("1");
				     DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
				     LocalDateTime now = LocalDateTime.now();  
				     responseStatus.setTs(dtf.format(now));
				     responseStatus.setTxn(searchCriteria.getTxn());
				     model.setResponseStatus(responseStatus);
				 
				     DocDetailsResponse docDetailsResponse=new DocDetailsResponse();
				     IssuedTo issuedTo=new IssuedTo();
				     List<Person> persons= new ArrayList<Person>();
				     issuedTo.setPersons(persons);
				     docDetailsResponse.setURI(DIGILOCKER_ISSUER_ID.concat("-").concat(DIGILOCKER_DOCTYPE).concat("-").
				    		 concat(filestore));
				     docDetailsResponse.setIssuedTo(issuedTo);

			    	 Certificate certificate=new Certificate();

				     if(searchCriteria.getDocType().equals("PRTAX"))
				     {
				    	 
				    	 certificate=populateCertificate(payments.get(0));
				 	     xstream.processAnnotations(Certificate.class);

				    					    	 
				     }
				     docDetailsResponse.setDataContent(Base64.getEncoder().encodeToString( xstream.toXML(certificate).getBytes()));

				     docDetailsResponse.setDocContent(encodedString);

				     model.setDocDetails(docDetailsResponse);
				       

			 }
			 catch (NullPointerException npe) {
			      log.error(npe.getMessage());
			      log.info("Error Occured",npe.getMessage());
			    }
			  }	
		} 
		
		else
		{
			ResponseStatus responseStatus=new ResponseStatus();
		     responseStatus.setStatus("0");
		     DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		     LocalDateTime now = LocalDateTime.now();  
		     responseStatus.setTs(dtf.format(now));
		     responseStatus.setTxn(searchCriteria.getTxn());
		     model.setResponseStatus(responseStatus);
		 
		     DocDetailsResponse docDetailsResponse=new DocDetailsResponse();
		     IssuedTo issuedTo=new IssuedTo();
		     List<Person> persons= new ArrayList<Person>();
		     issuedTo.setPersons(persons);
		     docDetailsResponse.setURI(null);
		     docDetailsResponse.setIssuedTo(issuedTo);
		     //docDetailsResponse.setDataContent("");
		     docDetailsResponse.setDocContent("");
		     log.info(EXCEPTION_TEXT_VALIDATION);
		     model.setDocDetails(docDetailsResponse);

		}
		
	    xstream.processAnnotations(PullURIResponse.class);
        xstream.toXML(model);
        
		return xstream.toXML(model);   

	}


	public String searchForDigiLockerDocRequest(SearchCriteria  searchCriteria) throws IOException
	{
			
		PullDocResponse model= new PullDocResponse();
			
		String[] urlArray=searchCriteria.getURI().split("-");
		int len=urlArray.length;
		String filestoreId="";
		for(int i=2;i<len;i++)
		{
			if(i==(len-1))
				filestoreId=filestoreId.concat(urlArray[i]);
			else
				filestoreId=filestoreId.concat(urlArray[i]).concat("-");

		}
		 String o=paymentService.getFilestore(filestoreId).toString();
		 
		 if(o!=null)
			 {
			 String path=o.split("url=")[1];
		 String pdfPath=path.substring(0,path.length()-3);
		 URL url1 =new URL(pdfPath);
		 try {

			          // Read the PDF from the URL and save to a local file
			     InputStream is1 = url1.openStream();
			     ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			     int nRead;
			     byte[] data = new byte[1024];

			     while ((nRead = is1.read(data, 0, data.length)) != -1) {
			         buffer.write(data, 0, nRead);
			     }

			     buffer.flush();
			     byte[] targetArray = buffer.toByteArray();
			     //byte[] sourceBytes = IOUtils.toByteArray(is1)

			     String encodedString = Base64.getEncoder().encodeToString(targetArray); 
			     
			     //model.setResponseStatus("1");
			     ResponseStatus responseStatus=new ResponseStatus();
			     responseStatus.setStatus("1");
			     DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
			     LocalDateTime now = LocalDateTime.now();  
			     responseStatus.setTs(dtf.format(now));
			     responseStatus.setTxn(searchCriteria.getTxn());
			     model.setResponseStatus(responseStatus);
			 
			     DocDetailsResponse docDetailsResponse=new DocDetailsResponse();
			     IssuedTo issuedTo=new IssuedTo();
			     List<Person> persons= new ArrayList<Person>();
			     issuedTo.setPersons(persons);
			     docDetailsResponse.setURI(null);
			     docDetailsResponse.setIssuedTo(issuedTo);
			     //docDetailsResponse.setDataContent(encodedString);
			     docDetailsResponse.setDocContent(encodedString);

			     model.setDocDetails(docDetailsResponse);
			       
			    
			     //return targetArray.toString();


			    } catch (NullPointerException npe) {
			    	log.error(npe.getMessage());
				      log.info("Error Occured",npe.getMessage());			    }
			  }	
		else
		{
			ResponseStatus responseStatus=new ResponseStatus();
		     responseStatus.setStatus("0");
		     DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		     LocalDateTime now = LocalDateTime.now();  
		     responseStatus.setTs(dtf.format(now));
		     responseStatus.setTxn(searchCriteria.getTxn());
		     model.setResponseStatus(responseStatus);
		     DocDetailsResponse docDetailsResponse=new DocDetailsResponse();
		     log.info(EXCEPTION_TEXT_VALIDATION);
		     IssuedTo issuedTo=new IssuedTo();
		     List<Person> persons= new ArrayList<Person>();
		     issuedTo.setPersons(persons);
		     docDetailsResponse.setURI(null);
		     docDetailsResponse.setIssuedTo(issuedTo);
		     //docDetailsResponse.setDataContent("");
		     docDetailsResponse.setDocContent("");

		     model.setDocDetails(docDetailsResponse);

		}
		
		
			
        XStream xstream = new XStream();
		xstream .addPermission(NoTypePermission.NONE); //forbid everything
		xstream .addPermission(NullPermission.NULL);   // allow "null"
		xstream .addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream .addPermission(AnyTypePermission.ANY);
        //xstream.processAnnotations(DocDetails.class);
        xstream.processAnnotations(PullDocResponse.class);
        xstream.toXML(model);
        
		return xstream.toXML(model);   

	}

		Boolean validateRequest(SearchCriteria searchCriteria, Payment payment)
		{
			if(!searchCriteria.getPayerName().equals(payment.getPayerName()))
					return false;
			else if(!searchCriteria.getMobile().equals(payment.getMobileNumber()))
					return false;
			else
				return true;
			
			
		}
		
		Certificate populateCertificate(Payment payment)
		{
			Certificate certificate=new Certificate();
			 certificate.setLanguage("99");
	    	 certificate.setName("Property Tax Receipt");
	    	 certificate.setType("PRTAX");
	    	 certificate.setNumber("");
	    	 certificate.setPrevNumber("");
	    	 certificate.setExpiryDate("");
	    	 certificate.setValidFromDate("");
	    	 certificate.setIssuedAt("");
	    	 certificate.setIssueDate("");
	    	 certificate.setStatus("A");
	    	 
	    	 IssuedBy issuedBy=new IssuedBy();
	    	 Organization organization=new Organization();
	    	 organization.setName("");
	    	 organization.setType("SG");
	    	 Address address=new Address();
	    	 address.setCountry("IN");
	    	 organization.setAddress(address);
	    	 issuedBy.setOrganisation(organization);
	    	 certificate.setIssuedBy(issuedBy);
	    	 
	    	 IssuedTo issuedTo=new IssuedTo();
	    	 Person person = new Person();
	    	 person.setAddress(address);
	    	 person.setPhoto("");
	    	 certificate.setIssuedTo(issuedTo);
	    	 
	    	 CertificateData certificateData=new CertificateData();
	    	 CertificateForData certificateForData=new CertificateForData();
	    	 certificateData.setCertificate(certificateForData);
	    	 
	    	 PropertyTaxReceipt propertyTaxReceipt=new PropertyTaxReceipt();
	    	 propertyTaxReceipt.setPaymentDate(payment.getPaymentDetails().get(0).getReceiptDate().toString());
	    	 propertyTaxReceipt.setServicetype(payment.getPaymentDetails().get(0).getBusinessService());
	    	 propertyTaxReceipt.setReceiptNo(payment.getPaymentDetails().get(0).getReceiptNumber());
	    	 propertyTaxReceipt.setPropertyID(payment.getPaymentDetails().get(0).getBill().getConsumerCode());
	    	 propertyTaxReceipt.setOwnerName(payment.getPayerName());
	    	 propertyTaxReceipt.setOwnerContact(payment.getMobileNumber());
	    	 propertyTaxReceipt.setPaymentstatus(payment.getPaymentStatus().toString());
	    	 PaymentForReceipt paymentForReceipt=new PaymentForReceipt();
	    	 paymentForReceipt.setPaymentMode(payment.getPaymentMode().toString());
	    	 String billingPeriod=
	    			 (payment.getPaymentDetails().get(0).getBill().getBillDetails().get(0).getFromPeriod().toString()).concat("-").
	    			 concat(payment.getPaymentDetails().get(0).getBill().getBillDetails().get(0).getToPeriod().toString());
	    	 paymentForReceipt.setBillingPeriod(billingPeriod);
	    	 paymentForReceipt.setPropertyTaxAmount(payment.getTotalDue().toString());
	    	 paymentForReceipt.setPaidAmount(payment.getTotalAmountPaid().toString());
	    	 paymentForReceipt.setPendingAmount((payment.getTotalDue().subtract(payment.getTotalAmountPaid())).toString());
	    	 paymentForReceipt.setExcessAmount("");
	    	 paymentForReceipt.setTransactionID(payment.getTransactionNumber());
	    	 paymentForReceipt.setG8ReceiptDate(payment.getPaymentDetails().get(0).getManualReceiptNumber());
	    	 paymentForReceipt.setG8ReceiptNo(payment.getPaymentDetails().get(0).getManualReceiptDate().toString());
	    	     	 
	    	 propertyTaxReceipt.setPaymentForReceipt(paymentForReceipt);
	    	 certificateData.setPropertyTaxReceipt(propertyTaxReceipt);
	    	 certificate.setCertificateData(certificateData);
	    	 return certificate;
		}
}
