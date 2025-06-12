/**
 * 
 */
package org.egov.finance.master.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.egov.finance.master.entity.SubScheme;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.SubSchemeModel;
import org.egov.finance.master.repository.SubSchemeRepository;
import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * SubSchemeValidation.java
 * 
 * @author bpattanayak
 * @date 11 Jun 2025
 * @version 1.0
 */

@Component
public class SubSchemeValidation {

	private final SubSchemeRepository schemeRepository;

	@Autowired
	public SubSchemeValidation(SubSchemeRepository schemeRepository) {
		this.schemeRepository = schemeRepository;
	}

	public SubScheme modeltoEntity(SubSchemeModel subSchemeModel) {
		return SubScheme.builder().id(subSchemeModel.getId()).code(subSchemeModel.getCode())
				.name(subSchemeModel.getName()).validfrom(subSchemeModel.getValidfrom())
				.validto(subSchemeModel.getValidto()).isactive(subSchemeModel.getIsactive())
				.department(subSchemeModel.getDepartment())
				.initialEstimateAmount(subSchemeModel.getInitialEstimateAmount())
				.councilLoanProposalNumber(subSchemeModel.getCouncilLoanProposalNumber())
				.councilLoanProposalDate(subSchemeModel.getCouncilLoanProposalDate())
				.councilAdminSanctionNumber(subSchemeModel.getCouncilAdminSanctionNumber())
				.councilAdminSanctionDate(subSchemeModel.getCouncilAdminSanctionDate())
				.govtAdminSanctionDate(subSchemeModel.getGovtAdminSanctionDate())
				.govtAdminSanctionNumber(subSchemeModel.getGovtAdminSanctionNumber())
				.govtLoanProposalDate(subSchemeModel.getGovtLoanProposalDate())
				.govtLoanProposalNumber(subSchemeModel.getGovtLoanProposalNumber()).build();
	}

	public SubSchemeModel entitytoModel(SubScheme subScheme) {

		return SubSchemeModel.builder().id(subScheme.getId()).scheme(subScheme.getScheme()).code(subScheme.getCode())
				.name(subScheme.getName()).validfrom(subScheme.getValidfrom()).validto(subScheme.getValidto())
				.isactive(subScheme.getIsactive()).department(subScheme.getDepartment())
				.initialEstimateAmount(subScheme.getInitialEstimateAmount())
				.councilLoanProposalNumber(subScheme.getCouncilLoanProposalNumber())
				.councilLoanProposalDate(subScheme.getCouncilLoanProposalDate())
				.councilAdminSanctionNumber(subScheme.getCouncilAdminSanctionNumber())
				.councilAdminSanctionDate(subScheme.getCouncilAdminSanctionDate())
				.govtAdminSanctionDate(subScheme.getGovtAdminSanctionDate())
				.govtAdminSanctionNumber(subScheme.getGovtAdminSanctionNumber())
				.govtLoanProposalDate(subScheme.getGovtLoanProposalDate())
				.govtLoanProposalNumber(subScheme.getGovtLoanProposalNumber()).createdBy(subScheme.getCreatedBy())
				.createdDate(subScheme.getCreatedDate()).lastModifiedBy(subScheme.getLastModifiedBy())
				.lastModifiedDate(subScheme.getLastModifiedDate()).build();
	}

	public void subSchemeCodeNameValidationForUpdate(SubSchemeModel schemeModel, Set<String> updatedSet) {
		Map<String, String> errorMap = new HashMap<>();

		if (updatedSet.contains("code") && schemeRepository.findByCode(schemeModel.getCode()) != null)
			errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		if (updatedSet.contains("name") && schemeRepository.findByName(schemeModel.getName()) != null)
			errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}

	public void subSchemeCreateCodeAndSchemIDValidation(SubSchemeModel subSchemeModel) {
		Map<String, String> errorMap = new HashMap<>();

		if (!StringUtils.hasText(subSchemeModel.getCode())
				&& (subSchemeModel.getScheme() != null && subSchemeModel.getScheme() > 0)) {
			errorMap.put(MasterConstants.INVALID_PARAMETERS, MasterConstants.INVALID_PARAMETERS_MSG);
			throw new MasterServiceException(errorMap);
		}
		if (errorMap.isEmpty()
				&& schemeRepository.existsByCodeAndScheme(subSchemeModel.getCode(), subSchemeModel.getScheme()))
			errorMap.put(MasterConstants.CODE_SCHEMEID_NOT_UNIQUE, MasterConstants.CODE_SCHEMEID_NOT_UNIQUE_MESSAGE);

		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}

	public  Specification<SubScheme> build(SubSchemeModel model) {
		return Specification.where(hasID(model.getId())).and((hasCode(model.getCode()))).and(hasScheme(model.getScheme()))
				.and(hasName(model.getName())).and(validFrom(model.getValidfrom())).and(validTo(model.getValidto()));
	}

	private static Specification<SubScheme> hasID(Long id) {
		return (id == null) ? null : (root, query, cb) -> cb.equal(root.get("id"), id);
	}
	
	private static Specification<SubScheme> hasCode(String code) {
		return (code == null || code.isBlank()) ? null : (root, query, cb) -> cb.equal(root.get("code"), code);
	}

	private static Specification<SubScheme> hasScheme(Long schemeId) {
		return (schemeId == null) ? null : (root, query, cb) -> cb.equal(root.get("scheme"), schemeId);
	}

	private static Specification<SubScheme> hasName(String name) {
		return (name == null || name.isBlank()) ? null
				: (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}

	private static Specification<SubScheme> validFrom(Date fromDate) {
		return (fromDate == null) ? null
				: (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("validfrom"), parseFormattedDate(fromDate));
	}

	private static Specification<SubScheme> validTo(Date toDate) {
		return (toDate == null) ? null : (root, query, cb) -> cb.lessThanOrEqualTo(root.get("validto"), toDate);
	}
	
	public static Date parseFormattedDate(Date fromDate) {
	    try {
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST

	        String formatted = formatter.format(fromDate);
	        System.out.println("formatter.parse(formatted)"+formatter.parse(formatted));
	        return formatter.parse(formatted);
	    } catch (ParseException e) {
	        e.printStackTrace();
	        return null;
	    }
	}



}
