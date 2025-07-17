package org.hpud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.hpud.repository.PropertyOwnerRepository;
import org.hpud.repository.GrbgAccountRepository;
import org.hpud.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GarbageService {

	@Autowired
	GrbgAccountRepository grbgAccRepo;
	
	@Autowired
	PropertyOwnerRepository pptOwnrRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Transactional
	public void updateUserUuidByids(String user_uuid,List<Long> grbgAcc) {
		  int updated = grbgAccRepo.updateUserUuidByids(user_uuid,grbgAcc);
	}
	
	@Transactional
	public void updateOwnerUuids(String user_uuid,List<String> pptownerids) {
		  int updated = pptOwnrRepo.updateUserUuidByids(user_uuid,pptownerids);
	}
	
	@Transactional
	public void makeUserInactive(Boolean active,List<String> inactiveusers) {
		  int updated = userRepo.toggleUserActivity(active,inactiveusers);
	}
	
	
	
}
