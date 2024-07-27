package com.example.hpgarbageservice.service;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.GarbageCommonRequest;
import com.example.hpgarbageservice.repository.GrbgChargeRepository;
import com.example.hpgarbageservice.repository.GrbgCollectionStaffRepository;
import com.example.hpgarbageservice.repository.GrbgCollectionUnitRepository;
import com.example.hpgarbageservice.repository.GrbgDeclarationRepository;
import com.example.hpgarbageservice.repository.GrbgOldDetailsRepository;
import com.example.hpgarbageservice.repository.GrbgScheduledRequestsRepository;

@Service
public class GarbageCommonService {

	@Autowired
	private GrbgCollectionUnitRepository grbgCollectionUnitRepository;

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
		
		
//		// update garbage unit
//		if(!CollectionUtils.isEmpty(garbageCommonRequest.getUpdatingGarbageCollectionUnits())) {
//			garbageCommonRequest.getUpdatingGarbageCollectionUnits().stream().forEach(collUnit -> {
//				grbgCollectionUnitRepository.update(collUnit);
//			});
//		}
		
		
//		// delete garbage unit
//		if(!CollectionUtils.isEmpty(garbageCommonRequest.getDeletingGarbageCollectionUnits())) {
//			garbageCommonRequest.getDeletingGarbageCollectionUnits().stream().forEach(collUnit -> {
//				grbgCollectionUnitRepository.deactivate(collUnit);
//			});
//		}
		
		
		
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
		
//		// update garbage staff
//		if(!CollectionUtils.isEmpty(garbageCommonRequest.getUpdatingGrbgCollectionStaff())) {
//			garbageCommonRequest.getUpdatingGrbgCollectionStaff().stream().forEach(collStaff -> {
//				grbgCollectionStaffRepository.update(collStaff);
//			});
//		}
		

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
	
	
}
