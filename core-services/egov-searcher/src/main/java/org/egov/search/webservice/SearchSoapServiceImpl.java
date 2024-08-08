/**
 * 
 */
package org.egov.search.webservice;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.egov.search.model.SearchRequest;
import org.egov.search.service.SearchService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
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
		String results = "";
		Object searchResult = searchService.searchData(searchRequest, moduleName, searchName);
		if (null != searchResult) {

			results = (String) searchResult;
			try {
				results = convertJsonToXml(results);
				StringBuilder res =new StringBuilder().append("<![CDATA[").append(results).append("]]>");
				results = res.toString();
				log.info(results);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ResponseEntity<>(results, HttpStatus.OK);
		} else {
			throw new CustomException("SEARCH_ERROR", "Error occurred while searching : ");
		}
	}

	String convertToXml(Object obj) throws IOException {
		XmlMapper xmlMapper = new XmlMapper();
		String s = xmlMapper.writeValueAsString(obj);
		return removeIllegalXmlChars(s);
	}

	private String removeIllegalXmlChars(String s) {
		Pattern REMOVE_ILLEGAL_CHARS = Pattern.compile("(i?)([^\\s=\"'a-zA-Z0-9._-])|(xmlns=\"[^\"]*\")");
		Pattern XML_TAG = Pattern.compile("(?m)(?s)(?i)(?<first><(/)?)(?<nonXml>.+?)(?<last>(/)?>)");

		Matcher matcher = XML_TAG.matcher(s);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String elementName = REMOVE_ILLEGAL_CHARS.matcher(matcher.group("nonXml")).replaceAll("").trim();
			matcher.appendReplacement(sb, "${first}" + elementName + "${last}");
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	Map<String, Object> convertJson(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});
	}

	public String convertJsonToXml(String json) throws IOException {
		return convertToXml(convertJson(json));
	}
}
