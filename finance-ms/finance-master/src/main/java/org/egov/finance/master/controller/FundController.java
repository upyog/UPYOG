package org.egov.finance.master.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/fund")
@Slf4j
public class FundController {
	
	@PostMapping("/_save")
	private ResponseEntity<?>saveFund(){
		return new ResponseEntity<>("hello", HttpStatus.OK);
	}

}
