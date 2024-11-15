package org.egov.pqm.validator;

import static org.egov.pqm.util.Constants.PQM_SCHEMA_CODE_PLANT;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.repository.PlantUserRepository;
import org.egov.pqm.service.UserService;
import org.egov.pqm.util.ErrorConstants;
import org.egov.pqm.util.PlantUserConstants;
import org.egov.pqm.web.model.plant.user.PlantUser;
import org.egov.pqm.web.model.plant.user.PlantUserRequest;
import org.egov.pqm.web.model.plant.user.PlantUserResponse;
import org.egov.pqm.web.model.plant.user.PlantUserSearchCriteria;
import org.egov.pqm.web.model.plant.user.PlantUserSearchRequest;
import org.egov.pqm.web.model.plant.user.PlantUserType;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import digit.models.coremodels.UserDetailResponse;
import digit.models.coremodels.UserSearchRequest;

@Component
public class PlantUserValidator {

  @Autowired
  private MDMSValidator mdmsValidator;

  @Autowired
  private PlantUserRepository plantUserRepository;
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private ServiceConfiguration config;

  
  public void validatePlantUserMappingRequest(PlantUserRequest plantUserRequest) {
	  List<PlantUser> plantUsers=plantUserRequest.getPlantUsers();
	    if (plantUsers == null || plantUsers.isEmpty()) {
	        throw new IllegalArgumentException("PlantUsers list cannot be null or empty");
	    }

	    for (PlantUser plantUser : plantUsers) {
	        if (StringUtils.isEmpty(plantUser.getTenantId())) {
	            throw new CustomException("PlantMappingConstants.INVALID_TENANT", "TenantId is mandatory");
	        }

	        if (plantUser.getPlantUserUuid() == null || plantUser.getPlantUserUuid().isEmpty()) {
	            throw new CustomException(PlantUserConstants.INVALID_UUID, "At least one employee uuid is required");
	        }
		    
	        if (plantUser.getPlantUserType() == null ) {
	            throw new CustomException(PlantUserConstants.INVALID_UUID, "Plant user type is required");
	        }

	        if (plantUser.getPlantCode() == null || plantUser.getPlantCode().isEmpty()) {
	            throw new CustomException(PlantUserConstants.INVALID_PLANT_CODE, "Plant code is required");
	        }

	        mdmsValidator.validateIfMasterPresent(plantUserRequest.getRequestInfo(),
	              plantUser.getTenantId(), PQM_SCHEMA_CODE_PLANT,plantUser.getPlantCode());

	        UserDetailResponse userDetailResponse = userExists(plantUserRequest);

	        List<String> code = new ArrayList<>();
	        if (!userDetailResponse.getUser().isEmpty()) {
	            userDetailResponse.getUser().get(0).getRoles().forEach(role -> {
	                code.add("" + role.getCode());
	            });
	            if (!code.contains(PlantUserConstants.PQM_TP_OPERATOR) && !code.contains(PlantUserConstants.PQM_ADMIN)) {
	                throw new CustomException(ErrorConstants.INVALID_APPLICANT_ERROR,
	                        "Only PQM_TP_OPERATOR or PQM_ADMIN Employee Can do this creation.");
	            }
	        } else {
	            throw new CustomException(ErrorConstants.PQM_TP_OPERATOR_EMPLOYEE_INVALID_ERROR,
	                    "In PQM_TP_OPERATOR plant-to-employee mapping, employee doesn't exist");
	        }
	        PlantUserType plantUserType = plantUser.getPlantUserType();
	
	        if (PlantUserType.PLANT_OPERATOR.equals(plantUserType) && !code.contains(PlantUserConstants.PQM_TP_OPERATOR)) {
	            throw new CustomException(ErrorConstants.PLANT_USER_TYPE_INVALID_ERROR,
	                    "PlantUserType doesn't match with employee role");
	        }
	        if (PlantUserType.ULB.equals(plantUserType) && !code.contains(PlantUserConstants.PQM_ADMIN)) {
	            throw new CustomException(ErrorConstants.PLANT_USER_TYPE_INVALID_ERROR,
	                    "PlantUserType doesn't match with employee role");
	        }
	    }
	}


	public void validatePlantMappingExists(PlantUserRequest plantUserRequest) {
		List<PlantUser> plantUsers = plantUserRequest.getPlantUsers();
		if (plantUsers == null || plantUsers.isEmpty()) {
			throw new IllegalArgumentException("PlantUsers list cannot be null or empty");
		}

		List<String> plantOperatorUuids = new ArrayList<>();
		List<String> plantCodes = new ArrayList<>();
		String tenantId = null;

		// Assuming all PlantUser objects in the list have the same tenantId
		tenantId = plantUsers.get(0).getTenantId();

		for (PlantUser plantUser : plantUsers) {
			plantCodes.add(plantUser.getPlantCode());
			plantOperatorUuids.add(plantUser.getPlantUserUuid());

		}

		PlantUserSearchCriteria plantUserSearchCriteria = new PlantUserSearchCriteria();
		plantUserSearchCriteria.setPlantUserUuids(plantOperatorUuids);
		plantUserSearchCriteria.setPlantCodes(plantCodes);
		plantUserSearchCriteria.setTenantId(tenantId);

		PlantUserResponse plantUserResponse = plantUserRepository
				.search(PlantUserSearchRequest.builder().plantUserSearchCriteria(plantUserSearchCriteria).build());

		if (plantUserResponse != null && plantUserResponse.getPlantUsers() != null) {
		    for (PlantUser plantUser : plantUserResponse.getPlantUsers()) {
		        if (StringUtils.isNotBlank(plantUser.getId())) {
		            throw new CustomException(ErrorConstants.PLANT_EMPLOYEE_MAP_EXISTS_ERROR,
		                    "Plant and employee mapping already exist for Plant" + plantUser.getPlantCode()+ " with ID: " + plantUser.getId());
		        }
		    }
		}

	}
	
	public void validateUpdatePlantMappingExists(PlantUserRequest plantUserRequest) {
		List<PlantUser> plantUsers = plantUserRequest.getPlantUsers();
		if (plantUsers == null || plantUsers.isEmpty()) {
			throw new IllegalArgumentException("PlantUsers list cannot be null or empty");
		}

		List<String> plantOperatorUuids = new ArrayList<>();
		List<String> plantCodes = new ArrayList<>();
		String tenantId = null;

		// Assuming all PlantUser objects in the list have the same tenantId
		tenantId = plantUsers.get(0).getTenantId();

		for (PlantUser plantUser : plantUsers) {
			plantCodes.add(plantUser.getPlantCode());
			plantOperatorUuids.add(plantUser.getPlantUserUuid());

		}

		PlantUserSearchCriteria plantUserSearchCriteria = new PlantUserSearchCriteria();
		plantUserSearchCriteria.setPlantUserUuids(plantOperatorUuids);
		plantUserSearchCriteria.setPlantCodes(plantCodes);
		plantUserSearchCriteria.setTenantId(tenantId);

		PlantUserResponse plantUserResponse = plantUserRepository
				.search(PlantUserSearchRequest.builder().plantUserSearchCriteria(plantUserSearchCriteria).build());

		if (plantUserResponse != null && plantUserResponse.getPlantUsers() != null) {
		    for (PlantUser plantUser : plantUserResponse.getPlantUsers()) {
		        if (!StringUtils.isNotBlank(plantUser.getId())) {
		            throw new CustomException(ErrorConstants.PLANT_EMPLOYEE_MAP_EXISTS_ERROR,
		                    "Plant and employee mapping is not exist for Plant" + plantUser.getPlantCode()+ " with ID: " + plantUser.getId());
		        }
		    }
		}

	}
  
  public UserDetailResponse userExists(PlantUserRequest plantUserRequest) {
	  List<PlantUser> plantUsers=plantUserRequest.getPlantUsers();
	    if (plantUsers == null || plantUsers.isEmpty()) {
	        throw new IllegalArgumentException("PlantUsers list cannot be null or empty");
	    }

	    UserSearchRequest userSearchRequest = new UserSearchRequest();
	    List<String> uuids = new ArrayList<>();

	    for (PlantUser plantUser : plantUsers) {
	        if (plantUser.getPlantUserUuid() != null && !plantUser.getPlantUserUuid().isEmpty()) {
	            uuids.add(plantUser.getPlantUserUuid());
	        }
	    }

	    if (uuids.isEmpty()) {
	        // No valid UUIDs found in the list
	        throw new CustomException("INVALID_UUID", "At least one valid employee uuid is required");
	    }

	    userSearchRequest.setUuid(uuids);

	    StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
	    return userService.userCall(userSearchRequest, uri);
	}

}
