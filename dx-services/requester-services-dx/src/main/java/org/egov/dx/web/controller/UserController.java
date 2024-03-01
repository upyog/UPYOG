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

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.dx.service.UserService;
import org.egov.dx.web.models.AuthResponse;
import org.egov.dx.web.models.IssuedDocument;
import org.egov.dx.web.models.ResponseInfoFactory;
import org.egov.dx.web.models.TokenRequest;
import org.egov.dx.web.models.TokenRes;
import org.egov.dx.web.models.TokenResponse;
import org.egov.dx.web.models.UserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;



@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	ResponseInfoFactory responseInfoFactory;
		
	@RequestMapping(value = {"/authorization/url"}, method = RequestMethod.POST )
    public ResponseEntity<AuthResponse> search(@Valid @RequestBody RequestInfo requestInfo,@RequestParam("module") String module) throws NoSuchAlgorithmException
    { 
		AuthResponse authResponse=new AuthResponse();
	    URI redirectionURL=userService.getRedirectionURL(module,authResponse);
	    authResponse.setRedirectURL(redirectionURL.toString());
		log.info("Redirection URL"+redirectionURL.toString());
		return 	new ResponseEntity<>(authResponse,HttpStatus.OK);
     }	
	
	@RequestMapping(value = {"/authorization/url/citizen"}, method = RequestMethod.POST )
    public ResponseEntity<AuthResponse> searchForcitizen(@Valid @RequestBody RequestInfo requestInfo,@RequestParam("module") String module) throws NoSuchAlgorithmException
    { 
		AuthResponse authResponse=new AuthResponse();
	    URI redirectionURL=userService.getRedirectionURL(module,authResponse);
	    authResponse.setRedirectURL(redirectionURL.toString());
		log.info("Redirection URL"+redirectionURL.toString());
		return 	new ResponseEntity<>(authResponse,HttpStatus.OK);
     }	
	
	
	@RequestMapping(value = "/token", method = RequestMethod.POST)
    public ResponseEntity<TokenResponse>  getToken(@Valid @RequestBody TokenRequest tokenRequest)    { 
		
		TokenRes tokenRes=userService.getToken(tokenRequest.getTokenReq());
		ResponseInfo responseInfo=ResponseInfoFactory.createResponseInfoFromRequestInfo(tokenRequest.getRequestInfo(), null);
		TokenResponse tokenResponse=TokenResponse.builder().responseInfo(responseInfo).tokenRes(tokenRes).build();

		 return new ResponseEntity<>(tokenResponse,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/token/citizen", method = RequestMethod.POST)
    public ResponseEntity<TokenResponse>  getTokenCitizen(@Valid @RequestBody TokenRequest tokenRequest)    { 
		
		TokenRes tokenRes=userService.getToken(tokenRequest.getTokenReq());
		ResponseInfo responseInfo=ResponseInfoFactory.createResponseInfoFromRequestInfo(tokenRequest.getRequestInfo(), null);
		TokenResponse tokenResponse=TokenResponse.builder().responseInfo(responseInfo).tokenRes(tokenRes).build();

		 return new ResponseEntity<>(tokenResponse,HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/details", method = RequestMethod.POST)
    public ResponseEntity<TokenResponse>  getDetails(@Valid @RequestBody TokenRequest tokenRequest)    { 
		UserRes userRes=userService.getUser(tokenRequest.getTokenReq());
		ResponseInfo responseInfo=ResponseInfoFactory.createResponseInfoFromRequestInfo(tokenRequest.getRequestInfo(), null);
		TokenResponse tokenResponse=TokenResponse.builder().responseInfo(responseInfo).tokenRes(null).userRes(userRes).build();
		return new ResponseEntity<>(tokenResponse,HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/issuedfiles", method = RequestMethod.POST)
    public ResponseEntity<TokenResponse> getIssuedFiles(@Valid @RequestBody TokenRequest tokenRequest)    { 
		List<IssuedDocument> issuedDocument=userService.getIssuedDocument(tokenRequest.getTokenReq());
		ResponseInfo responseInfo=ResponseInfoFactory.createResponseInfoFromRequestInfo(tokenRequest.getRequestInfo(), null);
		TokenResponse tokenResponse=TokenResponse.builder().responseInfo(responseInfo).issuedDocument(issuedDocument).userRes(null).build();
		return new ResponseEntity<>(tokenResponse,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/file", method = RequestMethod.POST,produces = {"application/pdf"})
	@ResponseBody	
    public  byte[] getFile(@Valid @RequestBody TokenRequest tokenRequest)    { 
		byte[] doc=userService.getDoc(tokenRequest.getTokenReq(),tokenRequest.getTokenReq().getId());
		return doc;
	}
}
