/**
 * 
 */
package org.egov.finance.inbox.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.inbox.entity.SubScheme;
import org.egov.finance.inbox.exception.ReportServiceException;
import org.egov.finance.inbox.model.SchemeModel;
import org.egov.finance.inbox.model.SubSchemeModel;
import org.egov.finance.inbox.model.request.SubSchemeRequest;
import org.egov.finance.inbox.repository.SubSchemeRepository;
import org.egov.finance.inbox.util.ApplicationThreadLocals;
import org.egov.finance.inbox.util.CommonUtils;
import org.egov.finance.inbox.util.ReportConstants;
import org.egov.finance.inbox.validation.SchemeValidation;
import org.egov.finance.inbox.validation.SubSchemeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
	private final SchemeService schemeService;
	private final SchemeValidation schemeValidation;
	private final CacheEvictionService cacheEvictionService;
	private final CommonUtils commonUtils;

	@Autowired
	public SubSchemeService(SubSchemeValidation subSchemeValidation, SubSchemeRepository subSchemeRepository,SchemeService schemeService,
			SchemeValidation schemeValidation,CacheEvictionService cacheEvictionService, CommonUtils commonUtils) {
		this.subSchemeValidation = subSchemeValidation;
		this.subSchemeRepository = subSchemeRepository;
		this.schemeService=schemeService;
		this.schemeValidation=schemeValidation;
		this.cacheEvictionService = cacheEvictionService;
		this.commonUtils = commonUtils;
	}

	public SubSchemeModel save(SubSchemeRequest subSchemeRequest) {
		SubSchemeModel subSchemeModel = subSchemeRequest.getSubSchemeRequest();
		SchemeModel searchcriteria=new SchemeModel();
		Map<String, String> errorMap = new HashMap<>();
		if (!ObjectUtils.isEmpty(subSchemeModel.getId())) {
			errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new ReportServiceException(errorMap);
		}
		SubScheme subScheme = subSchemeValidation.modeltoEntity(subSchemeModel);
		searchcriteria.setId(subSchemeModel.getScheme());
		List<SchemeModel> schemsearch=commonUtils.convertListIfNeeded(schemeService.search(searchcriteria), SchemeModel.class);
		if(!CollectionUtils.isEmpty(schemsearch))
		{
			subScheme.setScheme(schemeValidation.modelToEntity(schemsearch.get(0)));
		}
		else
			errorMap.put(ReportConstants.INVALID_SCHEME_ID, ReportConstants.INVALID_SCHEME_ID_MSG);
		
		if(!CollectionUtils.isEmpty(errorMap))
			throw new ReportServiceException(errorMap);
		
		subSchemeValidation.subSchemeCreateCodeAndSchemIDValidation(subSchemeModel);
		return subSchemeValidation.entitytoModel(subSchemeRepository.save(subScheme));
	}

	@Cacheable(value = ReportConstants.SUBSCHEME_SEARCH_REDIS_CACHE_NAME, keyGenerator = ReportConstants.SUBSCHEME_SEARCH_REDIS_KEY_GENERATOR)
	public List<SubSchemeModel> search(SubSchemeModel subSchemeModel) {
		Specification<SubScheme> specification = subSchemeValidation.build(subSchemeModel);
		
		return subSchemeRepository.findAll(specification).stream().map(subSchemeValidation::entitytoModel)
				.sorted(Comparator.comparingLong(SubSchemeModel::getId)).toList();
	}

	public SubSchemeModel update(SubSchemeRequest subSchemeRequest) {
		Map<String, String> errorMap = new HashMap<>();
		SchemeModel searchcriteria=new SchemeModel();
		SubScheme subSchemerequest = subSchemeValidation.modeltoEntity(subSchemeRequest.getSubSchemeRequest());
		if (ObjectUtils.isEmpty(subSchemerequest.getId())) {
			errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.INVALID_ID_PASSED_MESSAGE);
			throw new ReportServiceException(errorMap);
		}

		SubScheme subSchemeupdate = subSchemeRepository.findById(subSchemerequest.getId()).orElseThrow(() -> {
			errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.INVALID_ID_PASSED_MESSAGE);
			throw new ReportServiceException(errorMap);
		});
		
		searchcriteria.setId(subSchemeRequest.getSubSchemeRequest().getScheme());
		List<SchemeModel> schemesearch=commonUtils.convertListIfNeeded(schemeService.search(searchcriteria), SchemeModel.class);
		if(!CollectionUtils.isEmpty(schemesearch))
		{
			subSchemerequest.setScheme(schemeValidation.modelToEntity(schemesearch.get(0)));
		}
		else
			subSchemerequest.setScheme(null);
		
		List<String> updatedFields = commonUtils.applyNonNullFields(subSchemerequest, subSchemeupdate);
		SubSchemeModel subSchemeModel=new SubSchemeModel();
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());
		if(!ObjectUtils.isEmpty(updatedSet))
		{
			if(updatedSet.contains("code") || updatedSet.contains("scheme"))
			{
				subSchemeModel.setCode(subSchemeupdate.getCode());
				subSchemeModel.setScheme(subSchemeupdate.getScheme().getId());
				subSchemeValidation.subSchemeCreateCodeAndSchemIDValidation(subSchemeModel);
			}
		}
		
		
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				ReportConstants.SUBSCHEME_SEARCH_REDIS_CACHE_VERSION_KEY, ReportConstants.SUBSCHEME_SEARCH_REDIS_CACHE_NAME);
		return subSchemeValidation.entitytoModel(subSchemeRepository.save(subSchemeupdate));
		
	}

}
