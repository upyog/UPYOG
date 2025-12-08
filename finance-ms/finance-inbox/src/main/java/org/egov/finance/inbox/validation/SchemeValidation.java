package org.egov.finance.inbox.validation;

/**
 * SchemeValidation.java
 * 
 * @author mmavuluri
 * @date 9 Jun 2025
 * @version 1.0
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.finance.inbox.entity.Fund;
import org.egov.finance.inbox.entity.Scheme;
import org.egov.finance.inbox.exception.InboxServiceException;
import org.egov.finance.inbox.model.SchemeModel;
import org.egov.finance.inbox.repository.FundRepository;
import org.egov.finance.inbox.repository.SchemeRepository;
import org.egov.finance.inbox.util.InboxConstants;
import org.egov.finance.inbox.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class SchemeValidation {

	@Autowired
	private FundRepository fundRepository;

	@Autowired
	SchemeRepository schemeRepository;

	public Scheme modelToEntity(SchemeModel model) {
		Scheme scheme = new Scheme();
		scheme.setId(model.getId());
		scheme.setCode(model.getCode());
		scheme.setName(model.getName());
		scheme.setValidfrom(model.getValidfrom());
		scheme.setValidto(model.getValidto());
		scheme.setIsactive(model.getIsactive());
		scheme.setDescription(model.getDescription());
		scheme.setSectorid(model.getSectorid());
		scheme.setAaes(model.getAaes());
		scheme.setFieldid(model.getFieldid());

		if (model.getFundId() != null) {
			Fund fund = validateAndGetFund(model.getFundId());
			scheme.setFund(fund);
		}
		return scheme;
	}

	public SchemeModel entityToModel(Scheme s) {
		SchemeModel model = new SchemeModel();
		model.setId(s.getId());
		model.setCode(s.getCode());
		model.setName(s.getName());
		model.setValidfrom(s.getValidfrom());
		model.setValidto(s.getValidto());
		model.setIsactive(s.getIsactive());
		model.setDescription(s.getDescription());
		model.setSectorid(s.getSectorid());
		model.setAaes(s.getAaes());
		model.setFieldid(s.getFieldid());
		model.setFundId(s.getFund() != null ? s.getFund().getId() : null);
		model.setCreatedBy(s.getCreatedBy());
		model.setCreatedDate(s.getCreatedDate());
		model.setLastModifiedBy(s.getLastModifiedBy());
		model.setLastModifiedDate(s.getLastModifiedDate());
		return model;
	}

	public void schemeCreateValidation(SchemeModel schemeM) {
		Map<String, String> errorMap = new HashMap<>();

		if (!StringUtils.hasText(schemeM.getCode()) || !StringUtils.hasText(schemeM.getName())) {
			errorMap.put(InboxConstants.INVALID_PARAMETERS, InboxConstants.INVALID_PARAMETERS_MSG);
			throw new InboxServiceException(errorMap);
		}

		boolean codeExists = schemeRepository
				.exists((root, query, cb) -> cb.equal(root.get("code"), schemeM.getCode()));
		boolean nameExists = schemeRepository
				.exists((root, query, cb) -> cb.equal(root.get("name"), schemeM.getName()));

		// Check (name, fundId) uniqueness
		boolean nameFundExists = isNameFundCombinationExists(schemeM.getName(), schemeM.getFundId());

		if (codeExists || nameExists || nameFundExists) {
			if (codeExists)
				errorMap.put(InboxConstants.CODE_NOT_UNIQUE, InboxConstants.CODE_IS_ALREADY_EXISTS_MSG);
			if (nameExists)
				errorMap.put(InboxConstants.NAME_NOT_UNIQUE, InboxConstants.NAME_IS_ALREADY_EXISTS_MSG);
			if (nameFundExists)
				errorMap.put(InboxConstants.DUPLICATE_SCHEME, InboxConstants.NAME_FUND_ALREADY_EXISTS_MSG);
			throw new InboxServiceException(errorMap);
		}
		// Fund existence check
		validateAndGetFund(schemeM.getFundId());
	}

	public void schemeUpdateValidation(SchemeModel schemeM, Set<String> updatedSet) {
		Map<String, String> errorMap = new HashMap<>();

		if (updatedSet.contains("code")) {
			boolean exists = schemeRepository.exists(
					(root, query, cb) -> cb.and(cb.equal(cb.lower(root.get("code")), schemeM.getCode().toLowerCase()),
							cb.notEqual(root.get("id"), schemeM.getId())));
			if (exists)
				errorMap.put(InboxConstants.CODE_NOT_UNIQUE, InboxConstants.CODE_IS_ALREADY_EXISTS_MSG);
		}

		if (updatedSet.contains("name")) {
			boolean exists = schemeRepository.exists(
					(root, query, cb) -> cb.and(cb.equal(cb.lower(root.get("name")), schemeM.getName().toLowerCase()),
							cb.notEqual(root.get("id"), schemeM.getId())));
			if (exists)
				errorMap.put(InboxConstants.NAME_NOT_UNIQUE, InboxConstants.NAME_IS_ALREADY_EXISTS_MSG);
		}

		if (updatedSet.contains("name") || updatedSet.contains("fund")) {
			if (isNameFundCombinationExistsForUpdate(schemeM.getName(), schemeM.getFundId(), schemeM.getId())) {
				errorMap.put(InboxConstants.DUPLICATE_SCHEME, InboxConstants.NAME_FUND_ALREADY_EXISTS_MSG);
			}
		}

		if (updatedSet.contains("fund")) {
			validateAndGetFund(schemeM.getFundId());// If our update logic supports changing fundId
		}

		if (!CollectionUtils.isEmpty(errorMap))
			throw new InboxServiceException(errorMap);

	}

	private Fund validateAndGetFund(Long fundId) {
		if (fundId == null)
			return null;

		return fundRepository.findById(fundId).orElseThrow(() -> {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(InboxConstants.INVALID_FUND, InboxConstants.INVALID_FUND_ASSOCIATED_MSG);
			return new InboxServiceException(errorMap);
		});
	}

	private boolean isNameFundCombinationExists(String name, Long fundId) {
		if (name == null || fundId == null)
			return false;

		Specification<Scheme> spec = SpecificationHelper.<Scheme, String>equal("name", name.trim())
				.and(SpecificationHelper.<Scheme, Long>equal("fund.id", fundId));

		return schemeRepository.count(spec) > 0;
	}

	public boolean isNameFundCombinationExistsForUpdate(String name, Long fundId, Long currentSchemeId) {
		if (name == null || fundId == null || currentSchemeId == null)
			return false;

		Specification<Scheme> spec = SpecificationHelper.<Scheme, String>equal("name", name.trim())
				.and(SpecificationHelper.<Scheme, Long>equal("fund.id", fundId))
				.and((root, query, cb) -> cb.notEqual(root.get("id"), currentSchemeId));

		return schemeRepository.count(spec) > 0;
	}

}
