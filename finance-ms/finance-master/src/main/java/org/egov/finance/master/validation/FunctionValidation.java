/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Function;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.FunctionModel;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.repository.FunctionRepository;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Component
public class FunctionValidation {
	
	@Autowired
	CommonUtils commonUtils;
	@Autowired
	FunctionRepository functionRespository;

	public Function modelToEntity(FunctionModel model) {

		Function f = new Function();
		f.setId(model.getId() != null ? model.getId() : null);
		f.setIsActive(null != model.getIsActive() && model.getIsActive() ? true : false);
		f.setName(null != model.getName() ? model.getName() : null);
		f.setIsNotLeaf(model.getIsNotLeaf() ? true : false);
		f.setCode(null != model.getCode() ? model.getCode() : null);
		f.setLlevel(null != model.getLlevel() ? model.getLlevel() : null);
		f.setType(null!=model.getType()?model.getType():null);
		return f;
	}

	public FunctionModel entityTOModel(Function entity) {
		FunctionModel f = new FunctionModel();
		f.setId(entity.getId() != null ? entity.getId() : null);
		f.setIsActive(entity.getIsActive() ? true : false);
		f.setName(null != entity.getName() ? entity.getName() : null);
		f.setIsNotLeaf(entity.getIsNotLeaf() ? true : false);
		f.setCode(null != entity.getCode() ? entity.getCode() : null);
		f.setParentId(null != entity.getParentId() ? entity.getParentId().getId() : null);
		f.setLlevel(null != entity.getLlevel() ? entity.getLlevel() : null);
		f.setType(null != entity.getType() ? entity.getType() : null);
		f.setCreatedBy(null != entity.getCreatedBy() ? entity.getCreatedBy() : null);
		f.setCreatedDate(null != entity.getCreatedDate() ? entity.getCreatedDate() : null);
		f.setLastModifiedBy(null != entity.getLastModifiedBy() ? entity.getLastModifiedBy() : null);
		f.setLastModifiedDate(null != entity.getLastModifiedDate() ? entity.getLastModifiedDate() : null);
		
		return f;
	}

	public void functionCreateNameAndCodeValidation(FunctionModel funcM) {
		Map<String, String> errorMap = new HashMap<>();
		
			if(null==funcM.getName()||funcM.getName().isEmpty() || 
				null==funcM.getCode()|| funcM.getCode().isEmpty()) {
			errorMap.put(MasterConstants.INVALID_PARAMETERS, MasterConstants.INVALID_PARAMETERS_MSG);
		}
		if(errorMap.isEmpty()) {
			Specification<Function> spec = Specification.where(null);
			spec = spec.or((root, query, cb) -> cb.equal(root.get("code"), funcM.getCode()))
					.or( (root, query, cb) -> cb.equal(root.get("name"), funcM.getName()));    
			
			if (!functionRespository.findAll(spec).isEmpty())
				errorMap.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
			}
		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}
	
	public void validateCreateRequestModel(FunctionModel funcM) {
		Map<String, String> errorMap = new HashMap<>();
		
		if (!ObjectUtils.isEmpty(funcM.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
		}
		
		if(ObjectUtils.isEmpty(funcM.getCode())) {
			errorMap.put(MasterConstants.INVALID_CODE, MasterConstants.INVALID_CODE_MSG);
			
		}
		if(ObjectUtils.isEmpty(funcM.getName())) {
			errorMap.put(MasterConstants.INVALID_NAME, MasterConstants.INVALID_NAME_MSG);
			
		}
		if(!errorMap.isEmpty())
			throw new MasterServiceException(errorMap);
	}
	
	public void validateUpdateRequestModel(FunctionModel funcM) {
		Map<String, String> errorMap = new HashMap<>();
		
		if (ObjectUtils.isEmpty(funcM.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
		}
		
		if(ObjectUtils.isEmpty(funcM.getCode())) {
			errorMap.put(MasterConstants.INVALID_CODE, MasterConstants.INVALID_CODE_MSG);
			
		}
		if(ObjectUtils.isEmpty(funcM.getName())) {
			errorMap.put(MasterConstants.INVALID_NAME, MasterConstants.INVALID_NAME_MSG);
			
		}
		if(!errorMap.isEmpty())
			throw new MasterServiceException(errorMap);
	}
	
	
	public void functionCodeNameValidationForUpdate(FunctionModel funcM,Set<String> updatedSet) {
		Map<String, String> errorMap = new HashMap<>();
		
		if (updatedSet.contains("code") && !updatedSet.contains("name")){
			Specification<Function> spec = (root, query, cb) ->
            cb.equal(root.get("code"), funcM.getCode());
            
			functionRespository.findAll(spec).stream()
			.filter(x->!x.getId().equals(funcM.getId()))
			.findFirst()
			.ifPresent(x->{
				errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
			});	
		}	
		if (updatedSet.contains("name")&&!updatedSet.contains("code")) {
				Specification<Function> spec = (root, query, cb) -> 
				cb.equal(root.get("name"), funcM.getName());
			functionRespository.findAll(spec).stream()
			.filter(x->!x.getId().equals(funcM.getId()))
			.findFirst()
			.ifPresent(x->{
				errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
			});
			
		}
		else  if(updatedSet.contains("name")&&updatedSet.contains("code")){
			Specification<Function> spec = Specification.where(null);
			spec = spec
					.and((root, query, cb) -> cb.equal(root.get("code"), funcM.getCode()))
					.and( (root, query, cb) -> cb.equal(root.get("name"), funcM.getName()));    	
			functionRespository.findAll(spec).stream()
			.filter(x->!x.getId().equals(funcM.getId()))
			.findFirst()
			.ifPresent(x->{
				errorMap.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
			});
		}
			
		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}

}

