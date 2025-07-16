package org.hpud.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.hpud.entity.GrbgAccount;
import org.hpud.model.UserSearchRequest;
import org.hpud.model.UserSearchResponse;
import org.hpud.repository.GrbgAccountRepository;
import org.hpud.repository.PropertyOwnerRepository;
import org.egov.common.contract.request.User;
import org.hpud.repository.UserRepository;
import org.hpud.repository.UserSsoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SupportService {

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	GrbgAccountRepository grbgAccRepo;
	
	@Autowired
	UserSsoRepository userSsoRepo;
	
	@Autowired
	UserService userService;
	
	@Autowired
	GarbageService grbgSrvice;
	
	@Autowired
	PropertyOwnerRepository propertyOwnerRepo;
	
	public String support(int limit) {
		
		List<String> duplicateMobileNumber = userRepo.getDuplicateMobiles(limit);
		log.info("duplicateMobile {}",duplicateMobileNumber);
		RequestInfo requestInfo = userService.createDefaultRequestInfo();
		UserSearchRequest request = UserSearchRequest.builder().tenantId("hp").requestInfo(requestInfo).build();
		int chunkSize = 100;
		for (int i = 0; i < duplicateMobileNumber.size(); i += chunkSize) {
		    List<String> chunk = duplicateMobileNumber.subList(
		        i, Math.min(i + chunkSize, duplicateMobileNumber.size())
		    );
		    chunk.stream().forEach(number -> {
		        if (number != null) {
		            List<String> usersUuids = userRepo.getUuidForMobileNumber(number);
		            List<String> ssoMappedAccount = userSsoRepo.findSssoMappedUser(usersUuids);
		            String activeUser;
		            if (!ssoMappedAccount.isEmpty()) {
		                activeUser = ssoMappedAccount.get(0);
		            } else {
		                activeUser = usersUuids.get(0);
		            }

		            if (activeUser != null) {
		                List<String> inactiveUserUuids = usersUuids.stream()
		                    .filter(uuid -> !uuid.equals(activeUser))
		                    .collect(Collectors.toList());

		                request.setUuid(Collections.singletonList(activeUser));
		                UserSearchResponse userRes = userService.getUser(request);
		                
		                if (userRes != null && !CollectionUtils.isEmpty(userRes.getUser())) {
		                    updateGrbgAccount(userRes.getUser().get(0));
		                    updatePropertyOwner(userRes.getUser().get(0));
		                    updateInactive(inactiveUserUuids);
		                }
		            }
		        }
		    });
		}
		return "Done";
	}
	
	public void updateGrbgAccount(User user) {
		List<Long> grbgAcc = grbgAccRepo.getAccountForMobileNumber(user.getMobileNumber(),user.getUuid());
		if (!CollectionUtils.isEmpty(grbgAcc))
			grbgSrvice.updateUserUuidByids(user.getUuid(), grbgAcc);
	}
	
	private void updatePropertyOwner(User user) {
		List<String>ptOwneruuids = propertyOwnerRepo.getownerForMobileNumber(user.getMobileNumber(), user.getUuid());
		if (!CollectionUtils.isEmpty(ptOwneruuids))
			grbgSrvice.updateOwnerUuids(user.getUuid(), ptOwneruuids);
//		System.out.println(ptOwneruuids);
	}
	
	private void updateInactive(List<String> inactiveusers) {
		if (!CollectionUtils.isEmpty(inactiveusers))
			grbgSrvice.makeUserInactive(false,inactiveusers);
	}
	
}
