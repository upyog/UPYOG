package org.egov.dx.service;
import static org.egov.dx.util.PTServiceDXConstants.DIGILOCKER_DOCTYPE;
import static org.egov.dx.util.PTServiceDXConstants.DIGILOCKER_ISSUER_ID;
import static org.egov.dx.util.PTServiceDXConstants.DIGILOCKER_ORIGIN_NOT_SUPPORTED;
import static org.egov.dx.util.PTServiceDXConstants.ORIGIN;
import static org.egov.dx.util.PTServiceDXConstants.EXCEPTION_TEXT_VALIDATION;


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
import org.egov.dx.web.models.DocDetailsResponse;
import org.egov.dx.web.models.IssuedTo;
import org.egov.dx.web.models.Payment;
import org.egov.dx.web.models.PaymentSearchCriteria;
import org.egov.dx.web.models.Person;
import org.egov.dx.web.models.PullDocResponse;
import org.egov.dx.web.models.PullURIResponse;
import org.egov.dx.web.models.RequestInfoWrapper;
import org.egov.dx.web.models.ResponseStatus;
import org.egov.dx.web.models.SearchCriteria;
import org.egov.dx.web.models.UserResponse;
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
        UserResponse userResponse =new UserResponse();
        try {
        	userResponse=userService.getUser();
        	}
        catch(Exception e)
        {
        	
        }
        request.setAuthToken(userResponse.getAuthToken());
        request.setUserInfo(userResponse.getUser());
        requestInfoWrapper.setRequestInfo(request);
		List<Payment> payments = paymentService.getPayments(criteria, requestInfoWrapper);
		System.out.println("payments are:" + (!payments.isEmpty()?payments.get(0):"No payments found"));
		PullURIResponse model= new PullURIResponse();
	       
	 
		if(!payments.isEmpty() && validateRequest(searchCriteria,payments.get(0))){ 
			
			
					String o=paymentService.getFilestore(requestInfoWrapper,payments.get(0).getFileStoreId()).toString();
			 		if(o!=null)
			 		{
				 	String path=o.split("url=")[1];
				 	System.out.println("opening connection");
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
				    		 concat(payments.get(0).getFileStoreId()));
				     docDetailsResponse.setIssuedTo(issuedTo);
				     docDetailsResponse.setDataContent(encodedString);
				     docDetailsResponse.setDocContent(encodedString);

				     model.setDocDetails(docDetailsResponse);
				       

			 }
			 catch (NullPointerException npe) {
			      System.out.println("FAILED.\n[" + npe.getMessage() + "]\n");
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
		     docDetailsResponse.setDataContent("");
		     docDetailsResponse.setDocContent("");

		     model.setDocDetails(docDetailsResponse);

		}
		
		
			
        XStream xstream = new XStream();
		xstream .addPermission(NoTypePermission.NONE); //forbid everything
		xstream .addPermission(NullPermission.NULL);   // allow "null"
		xstream .addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream .addPermission(AnyTypePermission.ANY);
        //xstream.processAnnotations(DocDetails.class);
        xstream.processAnnotations(PullURIResponse.class);
        xstream.toXML(model);
        
		return xstream.toXML(model);   

	}


	public String searchForDigiLockerDocRequest(SearchCriteria  searchCriteria) throws IOException
	{
		
        RequestInfo request=new RequestInfo();
        request.setApiId("Rainmaker");
        request.setMsgId("1670564653696|en_IN");
        RequestInfoWrapper requestInfoWrapper=new RequestInfoWrapper();
        UserResponse userResponse =new UserResponse();
        try {
        	userResponse=userService.getUser();
        	}
        catch(Exception e)
        {
        	
        }
        request.setAuthToken(userResponse.getAuthToken());
        request.setUserInfo(userResponse.getUser());
        requestInfoWrapper.setRequestInfo(request);
		
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
		 String o=paymentService.getFilestore(requestInfoWrapper,
				 filestoreId).toString();
		 
		 if(o!=null)
			 {
			 String path=o.split("url=")[1];
		 System.out.println("opening connection");
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
			     docDetailsResponse.setDataContent(encodedString);
			     docDetailsResponse.setDocContent(encodedString);

			     model.setDocDetails(docDetailsResponse);
			       
			    
			     //return targetArray.toString();


			    } catch (NullPointerException npe) {
			      System.out.println("FAILED.\n[" + npe.getMessage() + "]\n");
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
		     log.info(EXCEPTION_TEXT_VALIDATION);
		     IssuedTo issuedTo=new IssuedTo();
		     List<Person> persons= new ArrayList<Person>();
		     issuedTo.setPersons(persons);
		     docDetailsResponse.setURI(null);
		     docDetailsResponse.setIssuedTo(issuedTo);
		     docDetailsResponse.setDataContent("");
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
}
