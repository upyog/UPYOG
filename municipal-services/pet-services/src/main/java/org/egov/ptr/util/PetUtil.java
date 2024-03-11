package org.egov.ptr.util;

import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PetUtil extends CommonUtils {

	@Autowired
	private PetConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Utility method to fetch bill for validation of payment
	 *
	 * @param propertyId
	 * @param tenantId
	 * @param request    //
	 */
//	public Boolean isBillUnpaid(String propertyId, String tenantId, RequestInfo request) {
//
//		Object res = null;
//
//		StringBuilder uri = new StringBuilder(configs.getEgbsHost())
//				.append(configs.getEgbsFetchBill())
//				.append("?tenantId=").append(tenantId)
//				.append("&consumerCode=").append(propertyId)
//				.append("&businessService=").append(ASMT_MODULENAME);
//
//		try {
//			res = restRepo.fetchResult(uri, new RequestInfoWrapper(request)).get();
//		} catch (ServiceCallException e) {
//
//			if(!(e.getError().contains(BILL_NO_DEMAND_ERROR_CODE) || e.getError().contains(BILL_NO_PAYABLE_DEMAND_ERROR_CODE)))
//				throw e;
//		}
//
//		if (res != null) {
//			JsonNode node = mapper.convertValue(res, JsonNode.class);
//			Double amount = node.at(BILL_AMOUNT_PATH).asDouble();
//			return amount > 0;
//		}
//		return false;
//	}
//
//
//	/**
//	 * Public method to infer whether the search is for open or authenticated user
//	 *
//	 * @param userInfo
//	 * @return
//	 */
//	public Boolean isPropertySearchOpen(User userInfo) {
//
//		return userInfo.getType().equalsIgnoreCase("SYSTEM")
//				&& userInfo.getRoles().stream().map(Role::getCode).collect(Collectors.toSet()).contains("ANONYMOUS");
//	}
//
//	public List<OwnerInfo> getCopyOfOwners(List<OwnerInfo> owners) {
//
//		List<OwnerInfo> copyOwners = new ArrayList<>();
//		owners.forEach(owner -> {
//
//			copyOwners.add(new OwnerInfo(owner));
//		});
//		return copyOwners;
//	}

}
