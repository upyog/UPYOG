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
import java.util.Optional;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.model.request.FundRequest;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.FundValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@Service
public class FundService {

	@Autowired
	private FundRepository fundRepository;
	@Autowired
	private FundValidation validation;
	



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

		return fundRepository.findAll(spec).stream().map(validation::entityTOModel)
				.sorted(Comparator.comparingLong(FundModel::getId)).collect(Collectors.toList());
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
			parentFund = fundRepository.findById(fundM.getParentId()).orElse(null);
			errorMap.put(MasterConstants.INVALID_PARENT_ID, MasterConstants.INVALID_PARENT_ID_MSG);
			throw new MasterServiceException(errorMap);

		}
		fundE.setParentId(parentFund);
		return validation.entityTOModel(fundRepository.save(fundE));
	}

	public static void applyNonNullFields(Object source, Object target) {
		BeanWrapper srcWrapper = new BeanWrapperImpl(source);
		BeanWrapper trgWrapper = new BeanWrapperImpl(target);

		for (java.beans.PropertyDescriptor propertyDescriptor : srcWrapper.getPropertyDescriptors()) {
			String propertyName = propertyDescriptor.getName();
			Object value = srcWrapper.getPropertyValue(propertyName);

			if (value != null && trgWrapper.isWritableProperty(propertyName)) {
				trgWrapper.setPropertyValue(propertyName, value);
			}
		}
	}

	public FundModel update(FundRequest request) {
		FundModel fundRequest = request.getFund();
		Map<String, String> errorMap = new HashMap<>();
		if (ObjectUtils.isEmpty(fundRequest.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		}
		Optional<Fund> fundSearch = fundRepository.findById(fundRequest.getId());
		Fund fundUpdate=new Fund();
		if (fundSearch.isPresent()) {
			applyNonNullFields(fundRequest, fundSearch);
			fundUpdate = fundSearch.get();
		}
		if (!ObjectUtils.isEmpty(fundRequest.getParentId())) {
			fundRepository.findById(fundRequest.getParentId()).orElseThrow(() -> {
				errorMap.put(MasterConstants.INVALID_PARENT_ID, MasterConstants.INVALID_PARENT_ID_MSG);
				throw new MasterServiceException(errorMap);
			});
		}
		
		return validation.entityTOModel(fundRepository.save(fundUpdate));
		
	}

}
