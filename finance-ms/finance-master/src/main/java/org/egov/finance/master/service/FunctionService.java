/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.service;

import java.util.HashMap;
import java.util.Map;

import org.egov.finance.master.entity.Function;
import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.FunctionModel;
import org.egov.finance.master.model.request.FunctionRequest;
import org.egov.finance.master.repository.FunctionRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.FunctionValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class FunctionService {
	@Autowired
	FunctionRepository functionRespository;
	@Autowired
	private CacheEvictionService cacheEvictionService;
	@Autowired
	private CommonUtils commonUtils;
	
	@Autowired
	FunctionValidation validation;
	
	
	public FunctionModel save(FunctionRequest request) {
		FunctionModel model = request.getFunction();
		Map<String, String> errorMap = new HashMap<>();
		validation.validateCreateRequestModel(model);
		Function funcE = validation.modelToEntity(model);
		validation.functionCreateNameAndCodeValidation(model);
		Function parentFunction = functionRespository.findById(model.getParentId()).orElseThrow(()->{
			errorMap.put(MasterConstants.INVALID_PARENT_ID, MasterConstants.INVALID_PARENT_ID);
			throw new MasterServiceException(errorMap);
		});
		funcE.setParentId(parentFunction);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_NAME);
		return validation.entityTOModel(functionRespository.save(funcE));
		
	}
	
	
}

