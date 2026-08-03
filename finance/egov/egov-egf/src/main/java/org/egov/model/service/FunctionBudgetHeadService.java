package org.egov.model.service;

import org.egov.commons.CFunction;
import org.egov.model.budget.BudgetHead;
import org.egov.model.budget.FunctionBudgetHead;
import org.egov.model.repository.FunctionBudgetHeadRepository;
import org.egov.utils.BudgetAccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * FunctionBudgetHeadService provides business logic for FunctionBudgetHead mapping operations.
 * Manages the association between functions (departments) and budget heads.
 *
 * Key Features:
 * - Retrieve budget head mappings for specific functions/departments
 * - Get all function-budget head mappings sorted by function code
 * - Search functions by name or code that have budget head mappings
 * - Support for authorized budget head access control per department
 *
 * @see FunctionBudgetHead
 * @see FunctionBudgetHeadRepository
 */
@Service
public class FunctionBudgetHeadService {

    @Autowired
    private FunctionBudgetHeadRepository functionBudgetHeadRepository;


    public List<FunctionBudgetHead> functionBudgetHeads(Long functionId) {
        return functionBudgetHeadRepository.findByFunctionId(functionId);
    }

    public List<FunctionBudgetHead> findAll() {
        return functionBudgetHeadRepository.findAll(new Sort(
                Sort.Direction.ASC, "function.code"));
    }

    public List<CFunction> getFunctionsByNameOrCode(String query) {
        return functionBudgetHeadRepository.findDistinctFunctionsHavingBudgetHead(query);
    }
    

}
