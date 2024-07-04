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

@Service
public class GarbageCommonService {

	@Autowired
	private GrbgCollectionUnitRepository grbgCollectionUnitRepository;

	@Autowired
	private GrbgCollectionStaffRepository grbgCollectionStaffRepository;

	@Autowired
	private GrbgChargeRepository grbgChargeRepository;
	
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
		
		create tbl grbg_scheduled_requests(uuid, garbage_id, type(grbgActivation/grbgDeactivation), start_date, end_date, is_active)
		create tbl grbg_old_details(uuid, garbage_id, old_garbage_id)
		create tbl grbg_declaration(uuid, statement, is_active)
		
		return garbageCommonRequest;
	}
	
	
}
