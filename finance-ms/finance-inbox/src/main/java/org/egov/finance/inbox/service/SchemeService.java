package org.egov.finance.inbox.service;

/**
 * SchemeService.java
 * 
 * @author mmavuluri
 * @date 9 Jun 2025
 * @version 1.0
 */
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.inbox.entity.Scheme;
import org.egov.finance.inbox.exception.InboxServiceException;
import org.egov.finance.inbox.model.SchemeModel;
import org.egov.finance.inbox.model.request.SchemeRequest;
import org.egov.finance.inbox.repository.SchemeRepository;
import org.egov.finance.inbox.util.ApplicationThreadLocals;
import org.egov.finance.inbox.util.CommonUtils;
import org.egov.finance.inbox.util.InboxConstants;
import org.egov.finance.inbox.validation.SchemeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchemeService {

	private SchemeRepository schemeRepository;
	private SchemeValidation validation;
	private CacheEvictionService cacheEvictionService;
	private CommonUtils commonUtils;

	@Autowired
	public SchemeService(SchemeRepository schemeRepository, SchemeValidation validation,
			CacheEvictionService cacheEvictionService, CommonUtils commonUtils) {
		this.schemeRepository = schemeRepository;
		this.validation = validation;
		this.cacheEvictionService = cacheEvictionService;
		this.commonUtils = commonUtils;
	}

	@Cacheable(value = InboxConstants.SCHEME_SEARCH_REDIS_CACHE_NAME, keyGenerator = InboxConstants.SCHEME_SEARCH_REDIS_KEY_GENERATOR)
	public List<SchemeModel> search(SchemeModel schemeCriteria) {
		Specification<Scheme> spec = Specification.where(null);

		if (!ObjectUtils.isEmpty(schemeCriteria.getCode()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("code"), schemeCriteria.getCode()));

		if (!ObjectUtils.isEmpty(schemeCriteria.getName()))
			spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + schemeCriteria.getName() + "%"));

		if (!ObjectUtils.isEmpty(schemeCriteria.getIsactive()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("isactive"), schemeCriteria.getIsactive()));

		if (!ObjectUtils.isEmpty(schemeCriteria.getId()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), schemeCriteria.getId()));

		if (!ObjectUtils.isEmpty(schemeCriteria.getFundId())) {
			log.info("Filtering by Fund ID: {}", schemeCriteria.getFundId());
			spec = spec.and((root, query, cb) -> cb.equal(root.join("fund").get("id"), schemeCriteria.getFundId()));
		}

		return schemeRepository.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(SchemeModel::getId)).toList();
	}

	public SchemeModel save(SchemeRequest request) {
		SchemeModel schemeM = request.getScheme();
		Map<String, String> errorMap = new HashMap<>();
		if (!ObjectUtils.isEmpty(schemeM.getId())) {
			errorMap.put(InboxConstants.INVALID_ID_PASSED, InboxConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new InboxServiceException(errorMap);
		}

		Scheme schemeE = validation.modelToEntity(schemeM);
		validation.schemeCreateValidation(schemeM);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				InboxConstants.SCHEME_SEARCH_REDIS_CACHE_VERSION_KEY, InboxConstants.SCHEME_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(schemeRepository.save(schemeE));
	}

	public SchemeModel update(SchemeRequest request) {
		Scheme schemeRequest = validation.modelToEntity(request.getScheme());
		Map<String, String> errorMap = new HashMap<>();

		if (ObjectUtils.isEmpty(schemeRequest.getId())) {
			errorMap.put(InboxConstants.INVALID_ID_PASSED, InboxConstants.INVALID_ID_PASSED_MESSAGE);
			throw new InboxServiceException(errorMap);
		}

		Scheme schemeUpdate = schemeRepository.findById(schemeRequest.getId()).orElseThrow(() -> {
			errorMap.put(InboxConstants.INVALID_ID_PASSED, InboxConstants.INVALID_ID_PASSED_MESSAGE);
			throw new InboxServiceException(errorMap);
		});

		List<String> updatedFields = commonUtils.applyNonNullFields(schemeRequest, schemeUpdate);
		SchemeModel schemeModel = new SchemeModel();
		schemeModel.setId(schemeUpdate.getId()); //Set ID
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

		if (updatedSet.contains("name"))
			schemeModel.setName(schemeUpdate.getName());

		if (updatedSet.contains("code"))
			schemeModel.setCode(schemeUpdate.getCode());

		if (updatedSet.contains("fund")) {
			schemeModel.setFundId(request.getScheme().getFundId() != null ? request.getScheme().getFundId()
					: (schemeUpdate.getFund() != null ? schemeUpdate.getFund().getId() : null));
		} else if (schemeUpdate.getFund() != null) {
			schemeModel.setFundId(schemeUpdate.getFund().getId());
		}

		if (!ObjectUtils.isEmpty(updatedSet))
			validation.schemeUpdateValidation(schemeModel, updatedSet);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				InboxConstants.SCHEME_SEARCH_REDIS_CACHE_VERSION_KEY, InboxConstants.SCHEME_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(schemeRepository.save(schemeUpdate));
	}

}
