/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.report.entity.Function;
import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.exception.MasterServiceException;
import org.egov.finance.report.model.FunctionModel;
import org.egov.finance.report.model.FundModel;
import org.egov.finance.report.model.request.FunctionRequest;
import org.egov.finance.report.repository.FunctionRepository;
import org.egov.finance.report.util.ApplicationThreadLocals;
import org.egov.finance.report.util.CommonUtils;
import org.egov.finance.report.util.MasterConstants;
import org.egov.finance.report.util.SpecificationHelper;
import org.egov.finance.report.validation.FunctionValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
			//	MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_NAME);
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
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_NAME);
		return validation.entityTOModel(functionRespository.save(funcUpdate));
		
	}
	
	
	
	@Cacheable(value = MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.FUNCTION_SEARCH_REDIS_KEY_GENERATOR)
	public List<FunctionModel> search(FunctionModel funcCriteria) {
		Specification<Function> spec = Specification.where(null);
		if (funcCriteria.getCode() != null && !funcCriteria.getCode().isEmpty()) {
			spec = SpecificationHelper.equal("code", funcCriteria.getCode());
		}
		if (funcCriteria.getName() != null && !funcCriteria.getName().isEmpty()) {
			spec = SpecificationHelper.likeIgnoreCase("name", funcCriteria.getName());
		}
		if (funcCriteria.getIsActive() != null) {
			spec = SpecificationHelper.equal("isActive", funcCriteria.getIsActive());
		}
		if (funcCriteria.getIsNotLeaf() != null) {
			spec = SpecificationHelper.equal("isNotLeaf", funcCriteria.getIsNotLeaf());
		}
		if (funcCriteria.getParentId() != null) {
			spec = SpecificationHelper.equal("parentId.id", funcCriteria.getParentId());
		}
		if (funcCriteria.getId() != null) {
			spec = SpecificationHelper.equal("id", funcCriteria.getId());
		}
		return functionRespository.findAll(spec).stream().map(validation::entityTOModel)
				.sorted(Comparator.comparingLong(FunctionModel::getId)).toList();
	}

	
}

