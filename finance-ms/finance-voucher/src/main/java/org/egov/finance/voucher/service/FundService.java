/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.voucher.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.voucher.entity.Function;
import org.egov.finance.voucher.entity.Fund;
import org.egov.finance.voucher.exception.MasterServiceException;
import org.egov.finance.voucher.model.FunctionModel;
import org.egov.finance.voucher.model.FundModel;
import org.egov.finance.voucher.model.request.FundRequest;
import org.egov.finance.voucher.repository.FundRepository;
import org.egov.finance.voucher.util.ApplicationThreadLocals;
import org.egov.finance.voucher.util.CommonUtils;
import org.egov.finance.voucher.util.MasterConstants;
import org.egov.finance.voucher.util.SpecificationHelper;
import org.egov.finance.voucher.validation.FundValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
public class FundService {

	private FundRepository fundRepository;

	private FundValidation validation;

	private CacheEvictionService cacheEvictionService;

	private CommonUtils commonUtils;

	@Autowired
	public FundService(FundRepository fundRepository, FundValidation validation,
			CacheEvictionService cacheEvictionService, CommonUtils commonUtils) {
		this.fundRepository = fundRepository;
		this.validation = validation;
		this.cacheEvictionService = cacheEvictionService;
		this.commonUtils = commonUtils;
	}

	@Cacheable(value = MasterConstants.FUND_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.FUND_SEARCH_REDIS_KEY_GENERATOR)
	public List<FundModel> search(FundModel fundCriteria) {
		Specification<Fund> spec = Specification.where((root, query, cb) -> cb.conjunction());
		if (fundCriteria.getCode() != null && !fundCriteria.getCode().isEmpty()) {	
			spec = spec.and(SpecificationHelper.equal("code", fundCriteria.getCode()));
		}
		if (!ObjectUtils.isEmpty(fundCriteria.getName())) {
			spec = spec.and(SpecificationHelper.likeIgnoreCase("name", fundCriteria.getName()));
		}
		if (!ObjectUtils.isEmpty(fundCriteria.getIsactive())) {
			spec = spec.and(SpecificationHelper.equal("isactive", fundCriteria.getIsactive()));
		}
		if (!ObjectUtils.isEmpty(fundCriteria.getIsnotleaf())) {
			spec = spec.and(SpecificationHelper.equal("isnotleaf", fundCriteria.getIsnotleaf()));
		}
		if (!ObjectUtils.isEmpty(fundCriteria.getParentId())) {
			spec = spec.and(SpecificationHelper.equal("parentId.id", fundCriteria.getParentId()));
		}
		if (!ObjectUtils.isEmpty(fundCriteria.getId())) {
			spec = spec.and(SpecificationHelper.equal("id", fundCriteria.getId()));
		}
		if (!ObjectUtils.isEmpty(fundCriteria.getIdentifier())) {
			spec = spec.and(SpecificationHelper.equal("identifier", fundCriteria.getIdentifier()));
		}

		return fundRepository.findAll(spec).stream().map(validation::entityTOModel)
				.sorted(Comparator.comparingLong(FundModel::getId)).toList();
	}

	public FundModel save(FundRequest request) {
		FundModel fundM = request.getFund();
		Map<String, String> errorMap = new HashMap<>();
		if (!ObjectUtils.isEmpty(fundM.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new MasterServiceException(errorMap);
		}
		Fund fundE = validation.modelToEntity(fundM);
		validation.fundCreateNameAndCodeValidation(fundM);
		Fund parentFund = null;

		if (!ObjectUtils.isEmpty(fundM.getParentId())) {
			parentFund = fundRepository.findById(fundM.getParentId()).orElseThrow(() -> {
				errorMap.put(MasterConstants.INVALID_PARENT_ID, MasterConstants.INVALID_PARENT_ID_MSG);
				throw new MasterServiceException(errorMap);
			});
		}
		fundE.setParentId(parentFund);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.FUND_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUND_SEARCH_REDIS_CACHE_NAME);
		return validation.entityTOModel(fundRepository.save(fundE));
	}

	public FundModel update(FundRequest request) {
		FundModel model = request.getFund();
		Fund parentFund = null;
		Fund fundRequest = validation.modelToEntity(request.getFund());
		Map<String, String> errorMap = new HashMap<>();
		if (ObjectUtils.isEmpty(fundRequest.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		}
		Fund fundUpdate = fundRepository.findById(fundRequest.getId()).orElseThrow(() -> {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		});
		
		if (ObjectUtils.isEmpty(model.getParentId())) {
			fundRequest.setParentId(null);
		} else {
		    boolean parentChanged = fundUpdate.getParentId() == null 
		        || !fundUpdate.getParentId().getId().equals(model.getParentId());

		    if (parentChanged) {
		    	Fund f = new Fund();
		    	f.setId(model.getParentId());
		    	//fundRequest.setParentId(parentFund);
		    	fundRequest.setParentId(f);
		    }
		}
		List<String> updatedFields = commonUtils.applyNonNullFields(fundRequest, fundUpdate);
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());
		if (updatedSet.contains("name")) 
			model.setName(fundUpdate.getName());
		if (updatedSet.contains("code")) 
			model.setCode(fundUpdate.getCode());
		
		if(!ObjectUtils.isEmpty(updatedSet))
			validation.fundCodeNameValidationForUpdate(model,updatedSet);
				if(updatedSet.contains("parentid") && !ObjectUtils.isEmpty(model.getParentId())) {
					parentFund = fundRepository.findById(model.getParentId()).orElseThrow(()->{
					errorMap.put(MasterConstants.INVALID_PARENT_ID, MasterConstants.INVALID_PARENT_ID);
					throw new MasterServiceException(errorMap);
				});
				}
				fundUpdate.setParentId(parentFund);
		
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.FUND_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUND_SEARCH_REDIS_CACHE_NAME);
		return validation.entityTOModel(fundRepository.save(fundUpdate));

	}

}
