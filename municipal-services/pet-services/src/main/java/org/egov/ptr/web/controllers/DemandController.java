
package org.egov.ptr.web.controllers;

import io.swagger.annotations.ApiParam;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.ptr.models.*;
import org.egov.ptr.models.collection.GetBillCriteria;
import org.egov.ptr.service.DemandService;
import org.egov.ptr.service.PetRegistrationService;
import org.egov.ptr.util.ResponseInfoFactory;
import org.egov.ptr.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/pet-registration")
public class DemandController {

	@Autowired
	private DemandService demandService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_updatedemand")
	public ResponseEntity<DemandResponse> updateDemand(@RequestBody @Valid RequestInfoWrapper requestInfoWrapper,
													   @ModelAttribute @Valid GetBillCriteria getBillCriteria) {
		return new ResponseEntity<>(demandService.updateDemands(getBillCriteria, requestInfoWrapper), HttpStatus.OK);
	}
}
