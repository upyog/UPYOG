/**
 * Created on May 30, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.controller;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/fund")
@Slf4j
public class FundController {
	
	
	@Autowired
	private FundService fundService;
	
	@PostMapping("/_save")
	private ResponseEntity<?>saveFund(){
		return new ResponseEntity<>("hello", HttpStatus.OK);
	}
	
	@PostMapping(value = "/view/{id}")
	public ResponseEntity<?> view(@PathVariable("id") final Long id) {
		final Fund fund = fundService.findOne(id);
		
		return new ResponseEntity<>(fund,HttpStatus.OK);
	}
	

}
