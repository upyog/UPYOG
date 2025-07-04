package org.egov.finance.voucher.service;

import java.util.List;
import java.util.Optional;

import org.egov.finance.voucher.entity.BudgetControlType;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.repository.BudgetControlTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class BudgetControlTypeService {

	private final BudgetControlTypeRepository budgetControlTypeRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public BudgetControlTypeService(final BudgetControlTypeRepository budgetCheckConfigRepository) {
		this.budgetControlTypeRepository = budgetCheckConfigRepository;
	}

	@Transactional
	public BudgetControlType create(final BudgetControlType budgetCheckConfig) {
		return budgetControlTypeRepository.save(budgetCheckConfig);
	}

	@Transactional
	public BudgetControlType update(final BudgetControlType budgetCheckConfig) {
		return budgetControlTypeRepository.saveAndFlush(budgetCheckConfig);
	}

	public List<BudgetControlType> findAll() {
		return budgetControlTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "value"));
	}

	public Optional<BudgetControlType> findOne(Long id) {
		return budgetControlTypeRepository.findById(id);
	}

	public List<BudgetControlType> search(BudgetControlType budgetCheckConfig) {
		return budgetControlTypeRepository.findAll();
	}

	public String getConfigValue() {
		List<BudgetControlType> configs = findAll();
		if (configs.size() == 1) {
			return configs.get(0).getValue();
		} else if (configs.size() == 0) {
			throw new ApplicationRuntimeException("Budget Check Configuration not defined");
		} else {
			throw new ApplicationRuntimeException("Multiple Budget Check Configurations  defined");
		}
	}

}
