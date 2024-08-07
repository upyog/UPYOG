package org.egov.search.webservice;

import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.egov.search.model.SearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@WebService(targetNamespace = "http://org.egov.search.webservice/", name = "SearchSoapService")

public interface SearchSoapService {

	
	@WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getData",
                    targetNamespace = "http://org.egov.search.webservice/",
                    className = "org.egov.search.webservice.GetData")
    @WebMethod(action = "urn:GetData")
	
    @ResponseWrapper(localName = "getDataResponse",
                     targetNamespace = "http://org.egov.search.webservice/",
                     className = "org.egov.search.webservice.GetDataResponse")
   
	//String sayHello(@WebParam(name = "myname", targetNamespace = "") String myname);
	
	public ResponseEntity<?> getData(@WebParam(name = "moduleName", targetNamespace = "") String moduleName,
			@PathVariable("searchName") String searchName,
			@RequestBody SearchRequest searchRequest, @RequestParam Map<String, Object> queryParams) ;	
	
}


