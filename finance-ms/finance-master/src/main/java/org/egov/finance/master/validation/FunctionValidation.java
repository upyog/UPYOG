/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;

import org.egov.finance.master.entity.Function;
import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.FunctionModel;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.repository.FunctionRepository;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

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
		f.setCreatedBy(null != entity.getCreatedBy() ? entity.getCreatedBy() : null);
		f.setCreatedDate(null != entity.getCreatedDate() ? entity.getCreatedDate() : null);
		f.setLastModifiedBy(null != entity.getLastModifiedBy() ? entity.getLastModifiedBy() : null);
		f.setLastModifiedDate(null != entity.getLastModifiedDate() ? entity.getLastModifiedDate() : null);
		return f;
	}

	public void fundFieldValidation(FunctionModel funcM) {
		Map<String, String> errorMap = new HashMap<>();
		if (functionRespository.find(funcM.getCode()) != null)
			errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		if (functionRespository.findByName(funcM.getName()) != null)
			errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}

}

