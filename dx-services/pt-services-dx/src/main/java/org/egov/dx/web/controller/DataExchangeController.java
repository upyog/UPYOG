/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.dx.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.net.ConnectException;


import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.dx.service.PaymentService;
import org.egov.dx.service.UserService;
import org.egov.dx.web.models.Payment;
import org.egov.dx.web.models.PaymentSearchCriteria;
import org.egov.dx.web.models.RequestInfoWrapper;
import org.egov.dx.web.models.UserResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.io.ByteStreams;
import com.jayway.jsonpath.JsonPath;

import okhttp3.Response;

@RestController
@RequestMapping("/dataexchange")
public class DataExchangeController {
	
	@Autowired
    private PaymentService paymentService;
	
	@Autowired
    private UserService userService;
	
	@RequestMapping(path = {"/_searchReceipt"}, method = RequestMethod.POST)
    @ResponseBody
    public String search(@Valid @RequestBody String requestBody) throws IOException
     {
    	String encoded=null;
		PaymentSearchCriteria criteria = new PaymentSearchCriteria();
        criteria.setTenantId("pg."+JsonPath.read(requestBody, "$.City").toString());
        criteria.setConsumerCodes(Collections.singleton(JsonPath.read(requestBody, "$.PropertyID").toString()));
        RequestInfo request=new RequestInfo();
        request.setApiId("Rainmaker");
        request.setMsgId("1670564653696|en_IN");
        RequestInfoWrapper requestInfoWrapper=new RequestInfoWrapper();
        requestInfoWrapper.setRequestInfo(request);
        UserResponse userResponse=userService.getUser();
        request.setAuthToken(userResponse.getAuthToken());
        request.setUserInfo(userResponse.getUser());
		List<Payment> payments = paymentService.getPayments(criteria, requestInfoWrapper);
		System.out.println("payments are:" + (!payments.isEmpty()?payments.get(0):"No payments fould"));
		if(!payments.isEmpty()){ 
				
		 String o=paymentService.getFilestore(criteria, requestInfoWrapper,
				 payments.get(0).getPaymentDetails().get(0).getReceiptNumber(),
				 payments.get(0).getFileStoreId()).toString().split("url=")[1];
		 System.out.println("opening connection");
		 String pdfPath=o.substring(0,o.length()-3);
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
			     return targetArray.toString();


			    } catch (NullPointerException npe) {
			      System.out.println("FAILED.\n[" + npe.getMessage() + "]\n");
			    }
			  }	
		else
		{
			encoded="No payments found for this payment Id";
		}
			
		return encoded;   
    }
	
}