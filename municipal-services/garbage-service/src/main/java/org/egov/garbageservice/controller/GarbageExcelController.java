package org.egov.garbageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Garbage Excel", description = "APIs for garbage account Excel operations")
@RequestMapping("/garbage-excel")
/**
 * REST controller for bulk garbage account creation from Excel uploads.
 *
 * Behavior:
 * - POST /_create — accepts multipart Excel file; parses rows via {@link org.egov.garbageservice.service.GarbageExcelService#createFromExcel}.
 * - Builds {@link org.egov.garbageservice.model.GarbageAccountResponse} and runs {@link GarbageAccountService#processResponse} for post-create processing.
 * - Returns HTTP 201 CREATED with the list of created accounts.
 *
 * Notes:
 * - Base path: /garbage-excel; Swagger tag "Garbage Excel".
 * - Request parameter name is {@code file} (MultipartFile); not JSON body.
 * - Validation and row-level errors are handled inside Excel/account services, not in this controller.
 */
public class GarbageExcelController {

	@Autowired
	private GarbageExcelService garbageExcelService;

	@Autowired
	private GarbageAccountService garbageAccountService;

	@Operation(summary = "Create garbage accounts from Excel")
	@PostMapping("/_create")
	public ResponseEntity<GarbageAccountResponse> createFromExcel(@RequestParam MultipartFile file) {

		List<GarbageAccount> garbageAccounts = garbageExcelService.createFromExcel(file);

		GarbageAccountResponse response = GarbageAccountResponse.builder().garbageAccounts(garbageAccounts).build();
		garbageAccountService.processResponse(response);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
