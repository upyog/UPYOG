/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Function;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.FunctionModel;
import org.egov.finance.master.model.request.FunctionRequest;
import org.egov.finance.master.repository.FunctionRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.FunctionValidation;
import org.springframework.beans.factory.annotation.Autowired;
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
		Function parentFunction =null;
		Map<String, String> errorMap = new HashMap<>();
		validation.validateCreateRequestModel(model);
		Function funcE = validation.modelToEntity(model);
		validation.functionCreateNameAndCodeValidation(model);
		
		if(!ObjectUtils.isEmpty(model.getParentId()))
		 parentFunction = functionRespository.findById(model.getParentId()).orElseThrow(()->{
			errorMap.put(MasterConstants.INVALID_PARENT_ID, MasterConstants.INVALID_PARENT_ID);
			throw new MasterServiceException(errorMap);
		});
		funcE.setParentId(parentFunction);
		//cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
		//		MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_NAME);
		return validation.entityTOModel(functionRespository.save(funcE));
		
	}
	
	public FunctionModel update(FunctionRequest request) {
		FunctionModel model = request.getFunction();
		Function parentFunction = null;
		Map<String, String> errorMap = new HashMap<>();
		validation.validateUpdateRequestModel(model);	
		Function funcRequest = validation.modelToEntity(model);
		Function funcUpdate = functionRespository.findById(model.getId()).orElseThrow(() -> {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		});
		
		if (ObjectUtils.isEmpty(model.getParentId())) {
		    funcRequest.setParentId(null);
		} else {
		    boolean parentChanged = funcUpdate.getParentId() == null 
		        || !funcUpdate.getParentId().getId().equals(model.getParentId());

		    if (parentChanged) {
		        funcRequest.setParentId(Function.builder().id(model.getParentId()).build());
		    }
		}
		
		List<String> updatedFields = commonUtils.applyNonNullFields(funcRequest, funcUpdate);
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());
		if (updatedSet.contains("name")) 
			model.setName(funcUpdate.getName());
		if (updatedSet.contains("code")) 
			model.setCode(funcUpdate.getCode());
		
		if(!ObjectUtils.isEmpty(updatedSet))
		validation.functionCodeNameValidationForUpdate(model,updatedSet);		
			if(updatedSet.contains("parentid") && !ObjectUtils.isEmpty(model.getParentId())) {
			 parentFunction = functionRespository.findById(model.getParentId()).orElseThrow(()->{
				errorMap.put(MasterConstants.INVALID_PARENT_ID, MasterConstants.INVALID_PARENT_ID);
				throw new MasterServiceException(errorMap);
			});
			 
		}
		funcUpdate.setParentId(parentFunction);
		//cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
		//		MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_NAME);
		return validation.entityTOModel(functionRespository.save(funcUpdate));
		
	}
	
	
}

