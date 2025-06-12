/**
 * 
 */
package org.egov.finance.master.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.SubScheme;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.SubSchemeModel;
import org.egov.finance.master.model.request.SubSchemeRequest;
import org.egov.finance.master.repository.SubSchemeRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.SubSchemeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * SubSchemeService.java
 * 
 * @author bpattanayak
 * @date 11 Jun 2025
 * @version 1.0
 */

@Service
public class SubSchemeService {

	private final SubSchemeValidation subSchemeValidation;
	private final SubSchemeRepository subSchemeRepository;
	private final CacheEvictionService cacheEvictionService;
	private final CommonUtils commonUtils;

	@Autowired
	public SubSchemeService(SubSchemeValidation subSchemeValidation, SubSchemeRepository subSchemeRepository,
			CacheEvictionService cacheEvictionService, CommonUtils commonUtils) {
		this.subSchemeValidation = subSchemeValidation;
		this.subSchemeRepository = subSchemeRepository;
		this.cacheEvictionService = cacheEvictionService;
		this.commonUtils = commonUtils;
	}

	public SubSchemeModel save(SubSchemeRequest subSchemeRequest) {
		SubSchemeModel subSchemeModel = subSchemeRequest.getSubSchemeRequest();
		Map<String, String> errorMap = new HashMap<>();
		if (!ObjectUtils.isEmpty(subSchemeModel.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new MasterServiceException(errorMap);
		}
		SubScheme subScheme = subSchemeValidation.modeltoEntity(subSchemeModel);
		subScheme.setScheme(subSchemeModel.getScheme());
		subSchemeValidation.subSchemeCreateCodeAndSchemIDValidation(subSchemeModel);
		return subSchemeValidation.entitytoModel(subSchemeRepository.save(subScheme));
	}

	@Cacheable(value = MasterConstants.SUBSCHEME_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.SUBSCHEME_SEARCH_REDIS_KEY_GENERATOR)
	public List<SubSchemeModel> search(SubSchemeModel subSchemeModel) {
		Specification<SubScheme> specification = subSchemeValidation.build(subSchemeModel);
		return subSchemeRepository.findAll(specification).stream().map(subSchemeValidation::entitytoModel)
				.sorted(Comparator.comparingLong(SubSchemeModel::getId)).toList();
	}

	public SubSchemeModel update(SubSchemeRequest subSchemeRequest) {
		Map<String, String> errorMap = new HashMap<>();
		SubScheme subSchemerequest = subSchemeValidation.modeltoEntity(subSchemeRequest.getSubSchemeRequest());
		subSchemerequest.setScheme(subSchemeRequest.getSubSchemeRequest().getScheme());
		if (ObjectUtils.isEmpty(subSchemerequest.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		}

		SubScheme subSchemeupdate = subSchemeRepository.findById(subSchemerequest.getId()).orElseThrow(() -> {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		});
		
		List<String> updatedFields = commonUtils.applyNonNullFields(subSchemerequest, subSchemeupdate);
		SubSchemeModel subSchemeModel=new SubSchemeModel();
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());
		if(!ObjectUtils.isEmpty(updatedSet))
		{
			if(updatedSet.contains("code") && updatedSet.contains("scheme"))
			{
				subSchemeModel.setCode(subSchemeupdate.getCode());
				subSchemeModel.setScheme(subSchemeupdate.getScheme());
				subSchemeValidation.subSchemeCreateCodeAndSchemIDValidation(subSchemeModel);
			}
		}
		
		
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.SUBSCHEME_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.SUBSCHEME_SEARCH_REDIS_CACHE_NAME);
		return subSchemeValidation.entitytoModel(subSchemeRepository.save(subSchemeupdate));
		
	}

}
