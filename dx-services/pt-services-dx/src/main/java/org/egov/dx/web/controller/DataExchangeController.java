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

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.egov.dx.service.DataExchangeService;
import org.egov.dx.web.models.PullDocRequest;
import org.egov.dx.web.models.PullURIRequest;
import org.egov.dx.web.models.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import lombok.extern.slf4j.Slf4j;



@RestController
@Slf4j
@RequestMapping("/dataexchange")
public class DataExchangeController {
	
	@Autowired
    private DataExchangeService dataExchangeService;
	
	@RequestMapping(path = {"/_searchReceipt"}, method = RequestMethod.POST ,consumes = {MediaType.APPLICATION_XML_VALUE},produces = {"application/xml","text/xml"})
    @ResponseBody()
    public String search(@Valid @RequestBody String requestBody, HttpServletRequest httpServletRequest) throws IOException, JAXBException
    { 
	    
		XStream xstream = new XStream();
		xstream .addPermission(NoTypePermission.NONE); 
		xstream .addPermission(NullPermission.NULL);   
		xstream .addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream .addPermission(AnyTypePermission.ANY);
        //xstream.processAnnotations(DocDetailsResponse.class);
        Object obj=new Object();
		ObjectMapper om=new ObjectMapper();
		PullURIRequest pojo=new PullURIRequest();
		PullDocRequest pojoDoc=new PullDocRequest();
		SearchCriteria searchCriteria=new SearchCriteria();
		String encodedString=null;
		if(requestBody.contains("PullURIRequest"))
		{
			log.info("In Pull URI Request");
	        xstream.processAnnotations(PullURIRequest.class);
	        obj=xstream.fromXML(requestBody);
			pojo=om.convertValue(obj, PullURIRequest.class);
			pojo.setTxn((requestBody.split("txn=\"")[1]).split("\"")[0]);
			searchCriteria.setPropertyId(pojo.getDocDetails().getPropertyId());
			searchCriteria.setCity(pojo.getDocDetails().getCity());
			//searchCriteria.setOrigin(httpServetRequest.getRequestURL().toString());
			searchCriteria.setOrigin("https://partners.digitallocker.gov.in");
			log.info("Request URL is "+ URI.create(httpServletRequest.getRequestURL().toString()).getHost());
			searchCriteria.setTxn(pojo.getTxn());
			searchCriteria.setDocType(pojo.getDocDetails().getDocType());
			searchCriteria.setPayerName(pojo.getDocDetails().getFullName());
			searchCriteria.setMobile(pojo.getDocDetails().getMobile());
			encodedString=dataExchangeService.searchPullURIRequest(searchCriteria);
		}
		else
		{
			log.info("In Pull Doc Request");
			xstream.processAnnotations(PullDocRequest.class);
	        obj=xstream.fromXML(requestBody);
			pojoDoc=om.convertValue(obj, PullDocRequest.class);
			pojoDoc.setTxn((requestBody.split("txn=\"")[1]).split("\"")[0]);
			//searchCriteria.setOrigin(httpServetRequest.getRequestURL().toString());
			searchCriteria.setOrigin("https://partners.digitallocker.gov.in");
			log.info("Request URL is "+URI.create(httpServletRequest.getRequestURL().toString()).getHost());
			searchCriteria.setURI(pojoDoc.getDocDetails().getURI());
			searchCriteria.setTxn(pojoDoc.getTxn());
		
			encodedString=dataExchangeService.searchPullDocRequest(searchCriteria);

		}
		
		return encodedString;	
     }	
}
