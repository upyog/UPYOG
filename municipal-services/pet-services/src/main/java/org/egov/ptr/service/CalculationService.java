package org.egov.ptr.service;

import org.egov.ptr.models.CalculationType;
import org.egov.ptr.models.DemandDetail;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.util.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private PetUtil mdmsUtil;

	/**
	 * Calculates the demand based on the provided PetRegistrationRequest.
	 *
	 * @param petRegistrationRequest The request containing pet registration
	 *                               applications.
	 * @return A list of DemandDetail objects representing the calculated demand.
	 */
	public List<DemandDetail> calculateDemand(PetRegistrationRequest petRegistrationRequest) {
		String tenantId = petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId();

		List<CalculationType> calculationTypes = mdmsUtil.getcalculationType(petRegistrationRequest.getRequestInfo(),
				tenantId, PTRConstants.PET_MASTER_MODULE_NAME);

		log.info("Retrieved calculation types: {}", calculationTypes);

		return processCalculationForDemandGeneration(tenantId, calculationTypes, petRegistrationRequest);
	}

	private List<DemandDetail> processCalculationForDemandGeneration(String tenantId,
			List<CalculationType> calculationTypes, PetRegistrationRequest petRegistrationRequest) {

		String applicationType = petRegistrationRequest.getPetRegistrationApplications().get(0).getApplicationType();

		List<DemandDetail> demandDetails = new ArrayList<>();
		for (CalculationType type : calculationTypes) {
			if (type.equals(applicationType)) {
				DemandDetail demandDetail = DemandDetail.builder().taxAmount(type.getAmount())
						.taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
				demandDetails.add(demandDetail);
				demandDetails.add(demandDetail);
			}
		}
		return demandDetails;

	}
}
