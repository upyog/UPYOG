/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.report.entity.Function;
import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.model.FunctionModel;
import org.egov.finance.report.model.FundModel;
import org.egov.finance.report.repository.FunctionRepository;
import org.egov.finance.report.util.CommonUtils;
import org.egov.finance.report.util.ReportConstants;
import org.egov.finance.report.util.SpecificationHelper;
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
			errorMap.put(ReportConstants.INVALID_PARAMETERS, ReportConstants.INVALID_PARAMETERS_MSG);
		}
		if(errorMap.isEmpty()) {
			Specification<Function> spec = Specification
					.where(SpecificationHelper.<Function,String>equal("name", funcM.getName()))
					.or(SpecificationHelper.<Function,String>equal("code", funcM.getCode()));
			if (!functionRespository.findAll(spec).isEmpty())
				errorMap.put(ReportConstants.CODE_NAME_NOT_UNIQUE, ReportConstants.CODE_NAME_NOT_UNIQUE_MSG);
			}
		if (!CollectionUtils.isEmpty(errorMap))
			throw new ReportServiceException(errorMap);
	}
	
	public void validateCreateRequestModel(FunctionModel funcM) {
		Map<String, String> errorMap = new HashMap<>();
		
		if (!ObjectUtils.isEmpty(funcM.getId())) {
			errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
		}
		
		if(ObjectUtils.isEmpty(funcM.getCode())) {
			errorMap.put(ReportConstants.INVALID_CODE, ReportConstants.INVALID_CODE_MSG);
			
		}
		if(ObjectUtils.isEmpty(funcM.getName())) {
			errorMap.put(ReportConstants.INVALID_NAME, ReportConstants.INVALID_NAME_MSG);
			
		}
		if(!errorMap.isEmpty())
			throw new ReportServiceException(errorMap);
	}
	
	public void validateUpdateRequestModel(FunctionModel funcM) {
		Map<String, String> errorMap = new HashMap<>();
		
		if (ObjectUtils.isEmpty(funcM.getId())) {
			errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.INVALID_ID_PASSED_MESSAGE);
		}
		
		if(ObjectUtils.isEmpty(funcM.getCode())) {
			errorMap.put(ReportConstants.INVALID_CODE, ReportConstants.INVALID_CODE_MSG);
			
		}
		if(ObjectUtils.isEmpty(funcM.getName())) {
			errorMap.put(ReportConstants.INVALID_NAME, ReportConstants.INVALID_NAME_MSG);
			
		}
		if(!errorMap.isEmpty())
			throw new ReportServiceException(errorMap);
	}
	
	
	public void functionCodeNameValidationForUpdate(FunctionModel funcM,Set<String> updatedSet) {
		Map<String, String> errorMap = new HashMap<>();
		
		if (updatedSet.contains("code") && !updatedSet.contains("name")){
            Specification<Function> spec = Specification
            		.where(SpecificationHelper.<Function,String>equal("code", funcM.getCode()));
			functionRespository.findAll(spec).stream()
			.filter(x->!x.getId().equals(funcM.getId()))
			.findFirst()
			.ifPresent(x->{
				errorMap.put(ReportConstants.CODE_NOT_UNIQUE, ReportConstants.CODE_IS_ALREADY_EXISTS_MSG);
			});	
		}	
		if (updatedSet.contains("name")&&!updatedSet.contains("code")) {
				Specification<Function> spec =Specification
						.where(SpecificationHelper.<Function, String>equal("name", funcM.getName()));
			functionRespository.findAll(spec).stream()
			.filter(x->!x.getId().equals(funcM.getId()))
			.findFirst()
			.ifPresent(x->{
				errorMap.put(ReportConstants.NAME_NOT_UNIQUE, ReportConstants.NAME_IS_ALREADY_EXISTS_MSG);
			});
			
		}
		else  if(updatedSet.contains("name")&&updatedSet.contains("code")){
			
			Specification<Function> spec = Specification.where(
				    SpecificationHelper.<Function, String>equal("name", funcM.getName())
				).or(
				    SpecificationHelper.<Function, String>equal("code", funcM.getCode())
				);
			functionRespository.findAll(spec).stream()
			.filter(x->!x.getId().equals(funcM.getId()))
			.findFirst()
			.ifPresent(x->{
				errorMap.put(ReportConstants.CODE_NAME_NOT_UNIQUE, ReportConstants.CODE_NAME_NOT_UNIQUE_MSG);
			});
		}
			
		if (!CollectionUtils.isEmpty(errorMap))
			throw new ReportServiceException(errorMap);
	}
}

