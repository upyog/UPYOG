package org.egov.garbageservice.controller;

import java.util.List;

import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.service.GarbageAccountService;
import org.egov.garbageservice.service.GarbageExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/garbage-excel")
public class GarbageExcelController {

	@Autowired
	private GarbageExcelService garbageExcelService;

	@Autowired
	private GarbageAccountService garbageAccountService;

	@PostMapping("/_create")
	public ResponseEntity<GarbageAccountResponse> createFromExcel(@RequestParam MultipartFile file) {

		List<GarbageAccount> garbageAccounts = garbageExcelService.createFromExcel(file);

		GarbageAccountResponse response = GarbageAccountResponse.builder().garbageAccounts(garbageAccounts).build();
		garbageAccountService.processResponse(response);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
