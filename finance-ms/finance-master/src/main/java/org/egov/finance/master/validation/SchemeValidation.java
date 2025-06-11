package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.entity.Scheme;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.SchemeModel;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.repository.SchemeRepository;
import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class SchemeValidation {

	@Autowired
	private FundRepository fundRepository;

	public Scheme modelToEntity(SchemeModel model) {
		Scheme s = new Scheme();
		s.setId(model.getId());
		s.setCode(model.getCode());
		s.setName(model.getName());
		s.setValidfrom(model.getValidfrom());
		s.setValidto(model.getValidto());
		s.setIsactive(model.getIsactive());
		s.setDescription(model.getDescription());
		s.setSectorid(model.getSectorid());
		s.setAaes(model.getAaes());
		s.setFieldid(model.getFieldid());

		if (model.getFundId() != null) {
			Fund fund = fundRepository.findById(model.getFundId()).orElse(null);
			s.setFund(fund);
		}

		return s;
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

	public void schemeFieldValidation(SchemeModel schemeM, SchemeRepository schemeRepository) {
		Map<String, String> errorMap = new HashMap<>();
		if (schemeRepository.findByCode(schemeM.getCode()) != null)
			errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		if (schemeRepository.findByName(schemeM.getName()) != null)
			errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}

}
