/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Function;
import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.FunctionModel;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class FundValidation {

	@Autowired
	CommonUtils commonUtils;
	
	@Autowired
	FundRepository fundRepository;
	public Fund modelToEntity(FundModel model) {

		Fund f = new Fund();
		f.setId(model.getId() != null ? model.getId() : null);
		f.setIdentifier(null != model.getIdentifier() ? model.getIdentifier() : null);
		f.setIsactive(null != model.getIsactive() && model.getIsactive() ? true : false);
		f.setName(null != model.getName() ? model.getName() : null);
		f.setIsnotleaf(model.getIsnotleaf() ? true : false);
		f.setCode(null != model.getCode() ? model.getCode() : null);
		f.setLlevel(null != model.getLlevel() ? model.getLlevel() : null);
		return f;
	}

	public FundModel entityTOModel(Fund entity) {
		FundModel f = new FundModel();
		f.setId(entity.getId() != null ? entity.getId() : null);
		f.setIdentifier(null != entity.getIdentifier() ? entity.getIdentifier() : null);
		f.setIsactive(entity.getIsactive() ? true : false);
		f.setName(null != entity.getName() ? entity.getName() : null);
		f.setIsnotleaf(entity.getIsnotleaf() ? true : false);
		f.setCode(null != entity.getCode() ? entity.getCode() : null);
		f.setParentId(null != entity.getParentId() ? entity.getParentId().getId() : null);
		f.setLlevel(null != entity.getLlevel() ? entity.getLlevel() : null);
		f.setCreatedBy(null != entity.getCreatedBy() ? entity.getCreatedBy() : null);
		f.setCreatedDate(null != entity.getCreatedDate() ? entity.getCreatedDate() : null);
		f.setLastModifiedBy(null != entity.getLastModifiedBy() ? entity.getLastModifiedBy() : null);
		f.setLastModifiedDate(null != entity.getLastModifiedDate() ? entity.getLastModifiedDate() : null);
		return f;
	}

	public void fundCodeNameValidationForUpdate(FundModel fundM,Set<String> updatedSet) {
		Map<String, String> errorMap = new HashMap<>();
		
		if (updatedSet.contains("code")&&fundRepository.findByCode(fundM.getCode()) != null)
			errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		if (updatedSet.contains("name") &&fundRepository.findByName(fundM.getName()) != null)
			errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}
	
	
	public void fundCreateNameAndCodeValidation(FundModel fundM) {
		Map<String, String> errorMap = new HashMap<>();
		
			if(null==fundM.getName()||fundM.getName().isEmpty() || 
				null==fundM.getCode()|| fundM.getCode().isEmpty()) {
			errorMap.put(MasterConstants.INVALID_PARAMETERS, MasterConstants.INVALID_PARAMETERS_MSG);
		}
		if(errorMap.isEmpty()) {
			Specification<Fund> spec = Specification.where(null);
			spec = spec.and((root, query, cb) -> cb.equal(root.get("code"), fundM.getCode()))
					.and( (root, query, cb) -> cb.equal(root.get("name"), fundM.getName()));    
			
			if (!fundRepository.findAll(spec).isEmpty())
				errorMap.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
			}
		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}

}
