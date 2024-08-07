/**
 * 
 */
package org.egov.search.webservice;

import java.util.Map;

import javax.jws.WebService;

import org.egov.search.model.SearchRequest;
import org.egov.search.service.SearchService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 
 */

@WebService(serviceName = "SearchSoapService", portName = "GetData", targetNamespace = "http://org.egov.search.webservice/", endpointInterface = "org.egov.search.webservice.SearchSoapService")
public class SearchSoapServiceImpl implements SearchSoapService {
	@Autowired
	private SearchService searchService;

	public SearchSoapServiceImpl(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	public ResponseEntity<?> getData(String moduleName, String searchName, SearchRequest searchRequest,
			Map<String, String> queryParams) {

		if (null == searchRequest.getSearchCriteria()) {
			searchRequest.setSearchCriteria(queryParams);
		}
		Object searchResult = searchService.searchData(searchRequest, moduleName, searchName);
		if (null != searchResult)
			return new ResponseEntity<>(searchResult, HttpStatus.OK);
		else
			throw new CustomException("SEARCH_ERROR", "Error occurred while searching : ");
	}

}
