/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.model.request.FundRequest;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.FundValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
		Specification<Fund> spec = Specification.where(null);
		if (fundCriteria.getCode() != null && !fundCriteria.getCode().isEmpty()) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("code"), fundCriteria.getCode()));
		}

		if (fundCriteria.getName() != null && !fundCriteria.getName().isEmpty()) {
			spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + fundCriteria.getName() + "%"));
		}
		if (fundCriteria.getIsactive() != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("isactive"), fundCriteria.getIsactive()));
		}
		if (fundCriteria.getIsnotleaf() != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("isnotleaf"), fundCriteria.getIsnotleaf()));
		}
		if (fundCriteria.getParentId() != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("parentId").get("id"), fundCriteria.getParentId()));
		}
		if (fundCriteria.getId() != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), fundCriteria.getId()));
		}
		if (fundCriteria.getIdentifier() != null && !fundCriteria.getIdentifier().equals("")) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("identifier"), fundCriteria.getIdentifier()));
		}

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.FUND_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUND_SEARCH_REDIS_CACHE_NAME);
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
		validation.fundFieldValidation(fundM, fundRepository);
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

		commonUtils.applyNonNullFields(fundRequest, fundUpdate);
		if (!ObjectUtils.isEmpty(request.getFund().getParentId())) {
			fundUpdate.setParentId(fundRepository.findById(request.getFund().getParentId()).orElseThrow(() -> {
				errorMap.put(MasterConstants.INVALID_PARENT_ID, MasterConstants.INVALID_PARENT_ID_MSG);
				throw new MasterServiceException(errorMap);
			}));
		} else {
			fundUpdate.setParentId(null);
		}
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.FUND_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.FUND_SEARCH_REDIS_CACHE_NAME);
		return validation.entityTOModel(fundRepository.save(fundUpdate));

	}

}
