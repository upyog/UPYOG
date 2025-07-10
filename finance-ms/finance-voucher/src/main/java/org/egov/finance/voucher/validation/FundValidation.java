/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.voucher.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.voucher.entity.Fund;
import org.egov.finance.voucher.exception.MasterServiceException;
import org.egov.finance.voucher.model.FundModel;
import org.egov.finance.voucher.repository.FundRepository;
import org.egov.finance.voucher.util.CommonUtils;
import org.egov.finance.voucher.util.MasterConstants;
import org.egov.finance.voucher.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

	public void fundCodeNameValidationForUpdate(FundModel fundM, Set<String> updatedSet) {
		Map<String, String> errorMap = new HashMap<>();

			if (updatedSet.contains("code") && !updatedSet.contains("name")) {
			    fundRepository.findOne(
			        SpecificationHelper.equal("code", fundM.getCode())
			    ).ifPresent(x->errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG));
			}
		    if (updatedSet.contains("name") && !updatedSet.contains("code")) {
			    fundRepository.findOne(
			        SpecificationHelper.equal("name", fundM.getName())
			    ).ifPresent(x->errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG));
		    }
		    else if (updatedSet.contains("name") && updatedSet.contains("code")) {
		    	if (updatedSet.contains("name") && !updatedSet.contains("code")) {
				    Specification <Fund> spec = Specification
				    		.where(SpecificationHelper.<Fund,String>equal("name",fundM.getName()))
				    		.or(SpecificationHelper.<Fund,String>equal("code",fundM.getCode()));
				    	fundRepository.findOne(spec).ifPresent(x->{
				    		errorMap.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
				    	});
				    }
				    		
		    }
		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}

	public void fundCreateNameAndCodeValidation(FundModel fundM) {
	    Map<String, String> errorMap = new HashMap<>();

	    if (!StringUtils.hasText(fundM.getName()) || !StringUtils.hasText(fundM.getCode())) {
	        errorMap.put(MasterConstants.INVALID_PARAMETERS, MasterConstants.INVALID_PARAMETERS_MSG);
	        throw new MasterServiceException(errorMap);
	    }
	    
	    fundRepository.findOne(Specification
	    		.where(SpecificationHelper.<Fund,String>equal("name", fundM.getName())
	    			.or(SpecificationHelper.<Fund,String>equal("code", fundM.getCode())
	    				))).ifPresent(x->{
	    					 errorMap.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
	    				        throw new MasterServiceException(errorMap);
	    				});
	}


}
