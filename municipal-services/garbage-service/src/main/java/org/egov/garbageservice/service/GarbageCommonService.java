package org.egov.garbageservice.service;
import org.egov.garbageservice.model.GarbageCountResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.model.GarbageCommonRequest;
import org.egov.garbageservice.repository.GrbgChargeRepository;
import org.egov.garbageservice.repository.GrbgCollectionStaffRepository;
import org.egov.garbageservice.repository.GrbgCollectionUnitRepository;
import org.egov.garbageservice.repository.GrbgDeclarationRepository;
import org.egov.garbageservice.repository.GrbgOldDetailsRepository;
import org.egov.garbageservice.repository.GrbgScheduledRequestsRepository;
import org.egov.garbageservice.repository.GarbageCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


/**
 * Manages shared garbage master data such as collection units, charges, and related configuration.
 * Supports GarbageCommonController create and aggregate count APIs used across ULB deployments.
 */
@Service
public class GarbageCommonService {

	@Autowired
	private GrbgCollectionUnitRepository grbgCollectionUnitRepository;

	@Autowired
	private GarbageCountRepository grbgCountRepository;

	@Autowired
	private GrbgCollectionStaffRepository grbgCollectionStaffRepository;

	@Autowired
	private GrbgChargeRepository grbgChargeRepository;

	@Autowired
	private GrbgOldDetailsRepository grbgOldDetailsRepository;

	@Autowired
	private GrbgScheduledRequestsRepository grbgScheduledRequestsRepository;

	@Autowired
	private GrbgDeclarationRepository grbgDeclarationRepository;
	
	public GarbageCommonRequest create(GarbageCommonRequest garbageCommonRequest) {
		
		
		if(!CollectionUtils.isEmpty(garbageCommonRequest.getCreatingGarbageCollectionUnits())) {
			garbageCommonRequest.getCreatingGarbageCollectionUnits().stream().forEach(collUnit -> {
				if(StringUtils.isEmpty(collUnit.getUuid())) {
			// create garbage unit
					collUnit.setIsActive(true);
					collUnit.setUuid(UUID.randomUUID().toString());
					grbgCollectionUnitRepository.create(collUnit);
				}else {
			// update garbage unit
					grbgCollectionUnitRepository.update(collUnit);
				}
			});
		}

		if(!CollectionUtils.isEmpty(garbageCommonRequest.getCreatingGrbgCollectionStaff())) {
			garbageCommonRequest.getCreatingGrbgCollectionStaff().stream().forEach(collStaff -> {
				if(StringUtils.isEmpty(collStaff.getUuid())) {
			// create garbage staff
					collStaff.setIsActive(true);
					collStaff.setUuid(UUID.randomUUID().toString());
					grbgCollectionStaffRepository.create(collStaff);
				}else {
			// update garbage staff
					grbgCollectionStaffRepository.update(collStaff);
				}
			});
		}

		

		if(!CollectionUtils.isEmpty(garbageCommonRequest.getCreatingGrbgCharge())) {
			garbageCommonRequest.getCreatingGrbgCharge().stream().forEach(charge -> {
				if(StringUtils.isEmpty(charge.getUuid())) {
			// create garbage charge
					charge.setIsActive(true);
					charge.setUuid(UUID.randomUUID().toString());
					grbgChargeRepository.create(charge);
				}else {
			// update garbage charge
					grbgChargeRepository.update(charge);
				}
			});
		}
		
		
		if(!CollectionUtils.isEmpty(garbageCommonRequest.getCreatingGrbgOldDetails())) {
			garbageCommonRequest.getCreatingGrbgOldDetails().stream().forEach(oldDetails -> {
				if(StringUtils.isEmpty(oldDetails.getUuid())) {
			// create garbage old details
					oldDetails.setUuid(UUID.randomUUID().toString());
					grbgOldDetailsRepository.create(oldDetails);
				}else {
			// update garbage old details
					grbgOldDetailsRepository.update(oldDetails);
				}
			});
		}
		
		

		if(!CollectionUtils.isEmpty(garbageCommonRequest.getCreatingGrbgScheduledRequests())) {
			garbageCommonRequest.getCreatingGrbgScheduledRequests().stream().forEach(schedule -> {
				if(StringUtils.isEmpty(schedule.getUuid())) {
			// create garbage scheduled request
					schedule.setUuid(UUID.randomUUID().toString());
					grbgScheduledRequestsRepository.create(schedule);
				}else {
			// update garbage scheduled request
					grbgScheduledRequestsRepository.update(schedule);
				}
			});
		}
		

		if(!CollectionUtils.isEmpty(garbageCommonRequest.getCreatingGrbgDeclaration())) {
			garbageCommonRequest.getCreatingGrbgDeclaration().stream().forEach(declaration -> {
				if(StringUtils.isEmpty(declaration.getUuid())) {
			// create garbage declaration
					declaration.setIsActive(true);
					declaration.setUuid(UUID.randomUUID().toString());
					grbgDeclarationRepository.create(declaration);
				}else {
			// update garbage declaration
					grbgDeclarationRepository.update(declaration);
				}
			});
		}
		
		
		return garbageCommonRequest;
	}
	
	public GarbageCountResponse getAllcounts() {
        GarbageCountResponse response = new GarbageCountResponse();
        List<Map<String, Object>> statusList = null;
        statusList = grbgCountRepository.getAllCounts();
        
        if (!CollectionUtils.isEmpty(statusList)) {
        	response.setCountsData(
		                statusList.stream()
		                        .filter(Objects::nonNull) // Ensure no null entries
		                        .filter(status -> StringUtils.isNotEmpty(status.toString())) // Validate non-empty entries
		                        .collect(Collectors.toList())); // Collect the filtered list
			  
			  if (statusList.get(0).containsKey("total_applications")) {
		            Object totalApplicationsObj = statusList.get(0).get("total_applications");
		            if (totalApplicationsObj instanceof Number) { // Ensure the value is a number
		            	response.setApplicationTotalCount(((Number) totalApplicationsObj).longValue());
		            } else {
		                throw new IllegalArgumentException("total_applications is not a valid number");
		            }
		        }
		}
        return response;
	}	
	
}
