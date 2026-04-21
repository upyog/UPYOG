package org.egov.rl.services.validator;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.rl.services.config.RentLeaseConfiguration;
import org.egov.rl.services.models.AllotmentCriteria;
import org.egov.rl.services.models.AllotmentDetails;
import org.egov.rl.services.models.AllotmentRequest;
import org.egov.rl.services.models.OwnerInfo;
import org.egov.rl.services.models.enums.Status;
import org.egov.rl.services.repository.AllotmentRepository;
import org.egov.rl.services.service.BoundaryService;
import org.egov.rl.services.util.EncryptionDecryptionUtil;
import org.egov.rl.services.util.RLConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AllotmentValidator {

	@Autowired
	private RentLeaseConfiguration configs;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	EncryptionDecryptionUtil encryptionDecryptionUtil;

	@Autowired
	AllotmentRepository allotmentRepository;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	BoundaryService boundaryService;
	/**
	 * Validate the masterData and ctizenInfo of the given propertyRequest
	 * 
	 * @param request PropertyRequest for create
	 */
	public void validateAllotementRequest(AllotmentRequest allotementRequest) {

		Map<String, String> errorMap = new HashMap<>();
		if (allotementRequest.getAllotment() == null)
			throw new CustomException("ALLOTMENT INFO ERROR",
					"Allotment cannot be empty, please provide the Allotment information");
//		if (allotementRequest.getAllotment() != null) {
//			if (allotementRequest.getAllotment().get(0).getWitnessDetails() == null) {
//				throw new CustomException("WITNESS INFO ERROR",
//						"Witness cannot be empty, please provide at least two witness information");
//			}
//		}
		List<OwnerInfo> owners = Optional.ofNullable(allotementRequest.getAllotment().get(0).getOwnerInfo()).orElse(null);
		if (owners == null || CollectionUtils.isEmpty(owners))
			throw new CustomException("OWNER INFO ERROR",
					"Owners cannot be empty, please provide at least one owner information");

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		String tenantId = Optional.ofNullable(allotementRequest.getAllotment().get(0).getTenantId()).orElse(null);
		if ((tenantId == null) || (tenantId != null && tenantId.isEmpty())) {
			throw new CustomException("TENANT ID INFO ERROR",
					"TenantId cannot be empty, please provide tenantId information");
		}

		String propertyId = Optional.ofNullable(allotementRequest.getAllotment().get(0).getPropertyId()).orElse(null);
		if ((propertyId == null) || (propertyId != null && propertyId.isEmpty())) {
			throw new CustomException("PROPERTY ID INFO ERROR",
					"PropertyID cannot be empty, please provide tenantId information");
		}
		String previousApplicationNumber = allotementRequest.getAllotment().get(0).getPreviousApplicationNumber();
		String tradeLicenseNumber = allotementRequest.getAllotment().get(0).getTradeLicenseNumber();		
		if ((previousApplicationNumber != null) || (previousApplicationNumber != null && !previousApplicationNumber.isEmpty())) {
			if ((tradeLicenseNumber == null) || (tradeLicenseNumber != null && tradeLicenseNumber.isEmpty())) {
 			    throw new CustomException("TRADELICENSENUMBER INFO ERROR",
					"tradeLicenseNumber cannot be empty, please provide Trade License information");
			}
		}
		
		String id = allotementRequest.getAllotment().get(0).getId();
//		if (id == null) {
//			Set<String> propertyIds=new HashSet<>();
//			propertyIds.add(propertyId);
//			Set<Status> status=new HashSet<>();
//			status.add(Status.APPROVED);
//			status.add(Status.REQUEST_FOR_DISCONNECTION);
//			AllotmentCriteria allotmentCriteria=AllotmentCriteria
//					.builder()
//					.status(status)
//					.propertyId(propertyIds)
//					.isExpaireFlag(false)
//					.tenantId(tenantId)
//					.currentDate(RLConstants.CURRENT_DATE)
//					.build();
//			
//			AllotmentDetails allotmentDetails = allotmentRepository
//					.getAllotmentSearch(allotmentCriteria)
//					.stream().findFirst().orElse(null);
//			if ((allotmentDetails != null)) {
//				throw new CustomException("PROPERTY ID INFO ERROR",
//						"PropertyID already existing , please provide another property Id information");
//			}
//		}
		
//		long uniqueAadharNumberSet = owners.stream().map(owner -> owner.getAadharCardNumber().trim()).distinct().count();
//		long uniquePanNumberSet = owners.stream().map(owner -> owner.getPanCardNumber().trim()).distinct().count();
//	    Set<String> uniquePanNumberSet = owners.stream()
//				.map(owner -> owner.getPanNumber()).collect(Collectors.toSet());
		long uniqueEmailSet = owners.stream().map(owner -> owner.getEmailId().trim()).distinct().count();
//		if (uniqueAadharNumberSet != owners.size())
//			throw new CustomException("EG_RL_OWNER INFO ERROR", "Duplicate AadharCard Number in the request");
//		if (uniquePanNumberSet != owners.size())
//			throw new CustomException("EG_RL_OWNER INFO ERROR", "Duplicate PAN Card Number in the request");
		if (uniqueEmailSet != owners.size())
			throw new CustomException("EG_RL_OWNER INFO ERROR", "Duplicate Email ID in the request");

		long uniqueOwnerSet = owners.stream()
				.map(owner -> (owner.getName() + owner.getMobileNo())
						.trim())
				.distinct().count();
		if (uniqueOwnerSet != owners.size())
			throw new CustomException("EG_RL_OWNER INFO ERROR", "Duplicate Owner's name and mobile number in the request");
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		validateOwnersData(allotementRequest, errorMap);
		boundaryService.validateAndLoadPropertyData(allotementRequest, errorMap);

		try {
			long startDate1 = Optional.ofNullable(allotementRequest.getAllotment().get(0).getStartDate()).orElse(null);
			long endDate1 = Optional.ofNullable(allotementRequest.getAllotment().get(0).getEndDate()).orElse(null);
			if (startDate1 == 0l || String.valueOf(startDate1).isEmpty())
				throw new CustomException("STARTDATE INFO ERROR",
						"startDate cannot be empty, please provide the startDate information");
			if (endDate1 == 0l || String.valueOf(startDate1).isEmpty())
				throw new CustomException("ENDDATE INFO ERROR",
						"endDate cannot be empty, please provide the endDate information");

			// Date endDate2 = new Date(endDate1);
			Timestamp endDate = new Timestamp(endDate1);
			Timestamp startDate = new Timestamp(startDate1); // 1 second later//Timestamp

			if (startDate.after(endDate)) {
				throw new CustomException("STARTDATE AND ENDDATE INFO ERROR", "startDate should not be after endDate");
			} else if (startDate.equals(endDate)) {
				throw new CustomException("STARTDATE AND ENDDATE INFO ERROR",
						"startDate should not be equal to endDate");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("STARTDATE AND ENDDATE INFO ERROR", "startDate and endDate are wrong passing");
		}
	}

	public void validateUpdateAllotementRequest(AllotmentRequest allotementRequest) {

		Map<String, String> errorMap = new HashMap<>();
		if (allotementRequest.getAllotment() == null)
			throw new CustomException("ALLOTMENT INFO ERROR",
					"Allotment cannot be empty, please provide the Allotment information");
		List<OwnerInfo> owners = Optional.ofNullable(allotementRequest.getAllotment().get(0).getOwnerInfo()).orElse(null);
		if (owners == null || CollectionUtils.isEmpty(owners))
			throw new CustomException("OWNER INFO ERROR",
					"Owners cannot be empty, please provide at least one owner information");

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		String tenantId = Optional.ofNullable(allotementRequest.getAllotment().get(0).getTenantId()).orElse(null);
		if ((tenantId == null) || (tenantId != null && tenantId.isEmpty())) {
			throw new CustomException("TENANT ID INFO ERROR",
					"TenantId cannot be empty, please provide tenantId information");
		}

		String propertyId = Optional.ofNullable(allotementRequest.getAllotment().get(0).getPropertyId()).orElse(null);
		if ((propertyId == null) || (propertyId != null && propertyId.isEmpty())) {
			throw new CustomException("PROPERTY ID INFO ERROR",
					"PropertyID cannot be empty, please provide tenantId information");
		}

		String id = allotementRequest.getAllotment().get(0).getId();
		if ((id == null) || (id != null && id.isEmpty())) {
			throw new CustomException("ALLOTMENT ID INFO ERROR",
					"Allotment Id cannot be empty, please provide allotment id information");
		}
	
		AllotmentCriteria allotmentCriteria=new AllotmentCriteria();
		Set<String> ids=new HashSet<>();
		ids.add(id.trim());
		allotmentCriteria.setAllotmentIds(ids);
		allotmentCriteria.setTenantId(tenantId);
		allotmentCriteria.setIsExpaireFlag(false);
		
		AllotmentDetails allotmentDetails= allotmentRepository.getAllotmentSearch(allotmentCriteria).stream().findFirst().orElse(null);
		if ((allotmentDetails == null)) {
			throw new CustomException("ALLOTMENT ID INFO ERROR",
					"Wrong allotment id is passing , please provide another corroct allotment Id information");
		}


		// if(allotementRequest.getAllotment().get(0).getWorkflow().getAction().equals(RLConstants.APPLY_RL_APPLICATION)) {
		// 	if(allotementRequest.getAllotment().get(0).getDocuments()==null||allotementRequest.getAllotment().get(0).getDocuments().isEmpty()) {
		// 			throw new CustomException("EG_RL_DOCUMENT INFO ERROR", "Document can't be empty in the request");
		// 	}
		// }

//		long uniqueAadharNumberSet = owners.stream().map(owner -> owner.getAadharCardNumber().trim()).distinct().count();
//		long uniquePanNumberSet = owners.stream().map(owner -> owner.getPanCardNumber().trim()).distinct().count();
//	    Set<String> uniquePanNumberSet = owners.stream()
//				.map(owner -> owner.getPanNumber()).collect(Collectors.toSet());
		long uniqueEmailSet = owners.stream().map(owner -> owner.getEmailId().trim()).distinct().count();
//		if (uniqueAadharNumberSet != owners.size())
//			throw new CustomException("EG_RL_OWNER INFO ERROR", "Duplicate AadharCard Number in the request");
//		if (uniquePanNumberSet != owners.size())
//			throw new CustomException("EG_RL_OWNER INFO ERROR", "Duplicate PAN Card Number in the request");
		if (uniqueEmailSet != owners.size())
			throw new CustomException("EG_RL_OWNER INFO ERROR", "Duplicate Email ID in the request");

		long uniqueOwnerSet = owners.stream()
				.map(owner -> (owner.getName() + owner.getMobileNo())
						.trim())
				.distinct().count();
		if (uniqueOwnerSet != owners.size())
			throw new CustomException("EG_RL_OWNER INFO ERROR", "Duplicate Owner's name and mobile number in the request");
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		validateOwnersData(allotementRequest, errorMap);
		boundaryService.validateAndLoadPropertyData(allotementRequest, errorMap);
		try {
			long startDate1 = Optional.ofNullable(allotementRequest.getAllotment().get(0).getStartDate()).orElse(null);
			long endDate1 = Optional.ofNullable(allotementRequest.getAllotment().get(0).getEndDate()).orElse(null);
			if (startDate1 == 0l || String.valueOf(startDate1).isEmpty())
				throw new CustomException("STARTDATE INFO ERROR",
						"startDate cannot be empty, please provide the startDate information");
			if (endDate1 == 0l || String.valueOf(startDate1).isEmpty())
				throw new CustomException("ENDDATE INFO ERROR",
						"endDate cannot be empty, please provide the endDate information");

			// Date endDate2 = new Date(endDate1);
			Timestamp endDate = new Timestamp(endDate1);
			Timestamp startDate = new Timestamp(startDate1); // 1 second later//Timestamp

			if (startDate.after(endDate)) {
				throw new CustomException("STARTDATE AND ENDDATE INFO ERROR", "startDate should not be after endDate");
			} else if (startDate.equals(endDate)) {
				throw new CustomException("STARTDATE AND ENDDATE INFO ERROR",
						"startDate should not be equal to endDate");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("STARTDATE AND ENDDATE INFO ERROR", "startDate and endDate are wrong passing");
		}
	}

	public boolean isValidEmail(String email) {
		String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
		return email != null && email.matches(regex);
	}

	public boolean isValidAadhaar(String aadhaar) {
		String regex = "^[2-9]{1}[0-9]{11}$";
		return aadhaar != null && aadhaar.matches(regex);
	}

	public boolean isValidPAN(String pan) {
		String regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
		return pan != null && pan.matches(regex);
	}

	public boolean isValidMobileNo(String mobileno) {
		String regex = "^[0-9]{10,12}$";
		return mobileno != null && mobileno.matches(regex);
	}

	/**
	 * Validates if the fields in PropertyRequest are present in the MDMS master
	 * Data
	 *
	 * @param request PropertyRequest received for creating or update
	 *
	 */
	private void validateOwnersData(AllotmentRequest allotementRequest, Map<String, String> errorMap) {
//		String propertyId = Optional.ofNullable(allotementRequest.getAllotment().get(0).getPropertyId()).orElse(null);
//		String tenantId = Optional.ofNullable(allotementRequest.getAllotment().get(0).getTenantId()).orElse(null);
		List<OwnerInfo> owners = allotementRequest.getAllotment().get(0).getOwnerInfo();
		owners.stream().forEach(u -> {
//			if(!isValidAadhaar( u.getAadharCardNumber())){
//				errorMap.put("OWNER INFORMATION ERROR", "Please enter valid Aadhar Card number");
//			}
//			if(!isValidPAN( u.getPanCardNumber())){
//				errorMap.put("OWNER INFORMATION ERROR", "Please enter valid PAN Card number");
//			}
			if (!isValidEmail(u.getEmailId())) {
				errorMap.put("OWNER INFORMATION ERROR", "Please enter valid EMAIL ID");
			}
			if (!isValidMobileNo(u.getMobileNo())) {
				errorMap.put("OWNER INFORMATION ERROR", "Please enter valid Mobile number");
			}

		});
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

	}
}
