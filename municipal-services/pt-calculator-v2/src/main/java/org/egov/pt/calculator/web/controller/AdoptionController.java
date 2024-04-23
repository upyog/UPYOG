package org.egov.pt.calculator.web.controller;

import org.egov.pt.calculator.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdoptionController {

	@Autowired
	private AdoptionService adoptionService;

	@RequestMapping(value = "/_chatbotdailyreport", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<HttpStatus> chatbotdailyreport(@RequestParam(required = true) boolean isTotalReport, 
			@RequestParam(required = false, defaultValue = "1") int daysForIncrement) {

		adoptionService.chatbotdailyreport(isTotalReport, daysForIncrement);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
