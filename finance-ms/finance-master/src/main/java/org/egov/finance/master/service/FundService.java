/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.model.request.FundRequest;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.validation.FundValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class FundService {

	@Autowired
	private  FundRepository fundRepository;
	@Autowired
	private FundValidation validation;
	
	public List<FundModel> search( FundModel fundCriteria) {
		 Specification<Fund> spec = Specification.where(null);
		 //root =  FROM FUND = sle
		 //QUERY CODEV + ""
		 //WHERE cb
		 
		    if (fundCriteria.getCode() != null && !fundCriteria.getCode().isEmpty()) {
		        spec = spec.and((root, query, cb) -> cb.equal(root.get("code"), fundCriteria.getCode()));
		    }

		    if (fundCriteria.getName() != null && !fundCriteria.getName().isEmpty()) {
		        spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%"+fundCriteria.getName()+"%"));
		    }
		    if (fundCriteria.getIsactive() != null) {
		        spec = spec.and((root, query, cb) -> cb.equal(root.get("isactive"),fundCriteria.getIsactive() ));
		    }
		    if (fundCriteria.getIsnotleaf() != null) {
		        spec = spec.and((root, query, cb) -> cb.equal(root.get("isnotleaf"),fundCriteria.getIsnotleaf() ));
		    }
		    if (fundCriteria.getParentId() != null) {
		        spec = spec.and((root, query, cb) -> cb.equal(root.get("parentId").get("id"),fundCriteria.getParentId() ));
		    }
		    if (fundCriteria.getId() != null) {
		        spec = spec.and((root, query, cb) -> cb.equal(root.get("id"),fundCriteria.getId() ));
		    }
		    if (fundCriteria.getIdentifier() != null && !fundCriteria.getIdentifier().equals("")) {
		        spec = spec.and((root, query, cb) -> cb.equal(root.get("identifier"),fundCriteria.getIdentifier() ));
		    }
		    
		 //   Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
		 //   Page<Fund> result = fundRepository.findAll(spec, pageable);

		    return   fundRepository.findAll(spec)
		    		.stream().map(validation::entityTOModel)
		    		.sorted(Comparator.comparingLong(FundModel::getId))
		    		.collect(Collectors.toList());
	}
	public FundModel save(FundRequest request) {
		FundModel  fundM = request.getFund();
		Fund fundE = validation.modelToEntity(fundM);
		fundE.setParentId(fundRepository.findById(fundM.getId()).orElse(null));
		//if()
		//validation 
		//Check uniuness of Name and code 
		//use above search
		return validation.entityTOModel(fundRepository.save(fundE));
	}
	
}

