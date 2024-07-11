package org.egov.search.controller;

import java.lang.reflect.Type;
import java.util.Map;

import org.egov.search.model.SearchRequest;
import org.egov.search.repository.SearchRepository;
import org.egov.search.service.SearchService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SearchController {
		
	@Autowired
	private SearchService searchService;
		
	@PostMapping("/{moduleName}/{searchName}/_get")
	@ResponseBody
	public ResponseEntity<?> getData(@PathVariable("moduleName") String moduleName,
			@PathVariable("searchName") String searchName,
			@RequestBody SearchRequest searchRequest, @RequestParam Map<String, Object> queryParams) {	
		if(null == searchRequest.getSearchCriteria()) {
			searchRequest.setSearchCriteria(queryParams);
		}
		Object searchResult = searchService.searchData(searchRequest,moduleName,searchName);
		
		log.info("Result of search query " + searchResult);
		try {
		    Type type = new TypeToken<Map<String, Object>>() {}.getType();
			Gson gson = new Gson();
			Map<String, Object> data = gson.fromJson(searchResult.toString(), type);
			return new ResponseEntity<>(data, HttpStatus.OK);
		}catch(Exception e) {
			if(null != searchResult)
				return new ResponseEntity<>(searchResult, HttpStatus.OK);
			else
				throw new CustomException("SEARCH_ERROR", "Error occurred while searching : " + e.getMessage());
		}
		
		//return new ResponseEntity<>(searchResult, HttpStatus.OK);

	}
@GetMapping("/unique-citizen-count")
	@ResponseBody
	public ResponseEntity<?> getUniqueCitizenCount(@RequestParam(value="date") String  date) {
		try {
			return new ResponseEntity<>(searchService.getUniqueCitezen(date), HttpStatus.OK);
		} catch (Exception e) {
			throw new CustomException("SEARCH_ERROR", "Error occurred while getting the citizen count : " + e.getMessage());
		}
	}
		
}